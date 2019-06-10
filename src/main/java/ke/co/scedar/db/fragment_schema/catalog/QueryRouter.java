package ke.co.scedar.db.fragment_schema.catalog;

import ke.co.scedar.db.Databases;
import ke.co.scedar.db.QueryResultsManager;
import ke.co.scedar.db.fragment_schema.FragmentSchema;
import ke.co.scedar.parser.SqlStatementComponents;
import ke.co.scedar.utils.DateTime;
import ke.co.scedar.utils.JoinInfo;
import ke.co.scedar.utils.Logging;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class QueryRouter {

    public static void route(SqlStatementComponents sqlStatementComponents) {

        Logging.log("Checking for fragmentation...");
        //Execute in main site
        if (!sqlStatementComponents.getComponentMetaData().isFragmented()) {
            Logging.log("Table '" +
                    sqlStatementComponents.getComponentMetaData().getTableName() + "' is not fragmented");
            routeToMainSite(sqlStatementComponents);
            return;
        }

        Logging.log("Table '" +
                sqlStatementComponents.getComponentMetaData().getTableName() + "' is fragmented." +
                " Confirming fragmentation mode is supported. i.e PHF");

        //Make sure fragmentation is PHF
        if (!sqlStatementComponents.getComponentMetaData().getFragmentationMode().equalsIgnoreCase("PHF")) {
            sqlStatementComponents.setHasErrors(true);
            sqlStatementComponents.setErrorCount(sqlStatementComponents.getErrorCount() + 1);
            sqlStatementComponents.setErrorMessage("Only PHF fragmentation mode is supported in this version of ddbms");
            Logging.log("Only PHF fragmentation mode is supported in this version of ddbms");
            return;
        }

        //int deductedSiteId = sqlStatementComponents.getComponentMetaData().getSiteId();
        //String fragmentedTableName = sqlStatementComponents.getComponentMetaData().getTableName();

        //FIXME: 6/9/2019 Ha ha. This is still haunting. Ensure support for more than 1 predicate
        String fragmentationField = sqlStatementComponents.getComponentMetaData().getFragmentationField();
        Logging.log("Identified fragmentation field as '" + fragmentationField + "'");
        //String fragmentationValue = sqlStatementComponents.getComponentMetaData().getFragmentationValue();

        //Get query's where condition value
        Logging.log("Inspecting where condition to determine where to route the query...");
        String queryWhereValue = "";
        for (int i = 0; i < sqlStatementComponents.getWhereClauseColumns().size(); i++) {
            Logging.log("Checking where column '" + sqlStatementComponents.getWhereClauseColumns().get(i) + "'...");
            if (sqlStatementComponents.getWhereClauseColumns().get(i).equals(fragmentationField)) {
                queryWhereValue = sqlStatementComponents.getWhereClauseValues().get(i);
                Logging.log("\tFragmentation column found. Current where value is '" + queryWhereValue + "'");
                break;
            }
        }

        //In case we're lucky and we have no joins, lets just do this and end :)
        if (sqlStatementComponents.getJoinsInfo().size() < 1) {
            Logging.log("No joins detected. So this should be easy :)");
            if (fragmentationField.contains("branch_id")) {
                switch (queryWhereValue) {
                    case "1":
                        //Main site stuff
                        routeToMainSite(sqlStatementComponents);
                        return;

                    case "2":
                        //Site 2 stuff
                        routeToSiteDos(sqlStatementComponents);
                        return;

                    case "3":
                        //Site 3 stuff
                        routeToSiteTres(sqlStatementComponents);
                        return;

                    default:
                        //Not known
                        sqlStatementComponents.setHasErrors(true);
                        sqlStatementComponents.setErrorCount(sqlStatementComponents.getErrorCount() + 1);
                        sqlStatementComponents.setErrorMessage("DDBMS API is having trouble routing your query to the " +
                                "right site and/or fragment. (HINT: include where clause containing fragmented field)");
                        return;
                }
            }
        }

        //Now that we have joins, let's do this sasa
        //We know routes and drivers are fragmented, so let's check if they're anywhere in the query
        Logging.log("Checking if table 'routes' or 'drivers' feature anywhere in the query...");
        boolean doesRoutesFeatureInQuery =
                checkIfTableNameExistsAnywhereInQuery(sqlStatementComponents, "routes");
        boolean doesDriversFeatureInQuery =
                checkIfTableNameExistsAnywhereInQuery(sqlStatementComponents, "drivers");

        // Ok, so they  don't feature anywhere. Send query to main site
        if ((!doesRoutesFeatureInQuery) && (!doesDriversFeatureInQuery)) {
            Logging.log("Table 'routes' or 'drivers' do not feature anywhere");
            routeToMainSite(sqlStatementComponents);
            return;
        }

        //Let's now create temporary tables

        //Create one for routes if it features and copy data on routes main site to the temporary table
        // NB: using ## as temp table prefix to make it globaly accessible by any connection since we're using c3p0 and
        // we have no control of which explicit connection will be retrieving the data

        //Use timestamp to create unique temp tables and avoid mixup during concurrent events
        String tempRoutesTableName = "##temp_" + DateTime.getCurrentUnixTimestamp() + "_routes";
        String routesSelection = "";
        if (doesRoutesFeatureInQuery) {
            //Also, lets get the relevant query filter / selection part
            routesSelection = getSelectionClause(sqlStatementComponents, "routes");

            Logging.log("Fragmented table 'routes' features in subject query.");
            Logging.log("Creating temporary table '"+tempRoutesTableName+"'");
            createTemporaryTable("routes", tempRoutesTableName, routesSelection);
        }

        //Create one for drivers if it features and copy data on routes main site to the temporary table
        String tempDriversTableName = "##temp_" + DateTime.getCurrentUnixTimestamp() + "_drivers";
        String driversSelection = "";
        if (doesDriversFeatureInQuery) {
            //Also, lets get the relevant query filter / selection part
            driversSelection = getSelectionClause(sqlStatementComponents, "drivers");

            Logging.log("Fragmented table 'drivers' features in subject query.");
            Logging.log("Creating temporary table '"+tempDriversTableName+"'");
            createTemporaryTable("drivers", tempDriversTableName, driversSelection);
        }

        //Now that we have the temporary tables set up, let's ship in the data from the other sites

        //Ship routes from site 2 & 3
        if (doesRoutesFeatureInQuery) {
            Logging.log("Preparing to ship 'routes' table from site 2 and 3...");
            shipDataFromSiteIntoMainSite("2", "routes", routesSelection, tempRoutesTableName);
            shipDataFromSiteIntoMainSite("3", "routes", routesSelection, tempRoutesTableName);
        }

        //Ship drivers from site 2 & 3
        if (doesDriversFeatureInQuery) {
            Logging.log("Preparing to ship 'drivers' table from site 2 and 3...");
            shipDataFromSiteIntoMainSite("2", "drivers", driversSelection, tempDriversTableName);
            shipDataFromSiteIntoMainSite("3", "drivers", driversSelection, tempDriversTableName);
        }

        //Next, we make sure the original query is set.
        //We do this by  replacing the routes and drivers table names with the new temp table names
        String updatedSqlStatement = sqlStatementComponents.getOriginalQuery()
                .replaceAll("routes", tempRoutesTableName);
        updatedSqlStatement = updatedSqlStatement.replaceAll("drivers", tempDriversTableName);
        Logging.log("Updated original sql statement to leverage temporary " +
                "tables with combined data from all the sites ");

        //Finally, we select the data
        Logging.log("Finally.. Selecting data from Main Site");
        QueryResultsManager queryResultsManager = Databases.SQL_SERVER_DB.selectQuery(updatedSqlStatement);
        sqlStatementComponents.setQueryResultsManager(queryResultsManager);

    }

    private static void shipDataFromSiteIntoMainSite(String sourceSite, String sourceTable,
                                                     String selection, String destinationTable) {
        QueryResultsManager queryResultsManager;
        Logging.log("Generating selection for table '"+sourceTable+"' in site - "+sourceSite+"....");
        String targetQuery = "select * from " + sourceTable + selection;
        Logging.log("Target Query (with selection) generated: '"+ targetQuery+"'");

        List<HashMap<String, Object>> records;

        switch (sourceSite) {
            case "2":
                //Get data from site 2
                Logging.log("Shipping data from Site 2...");
                queryResultsManager = Databases.POSTGRES_DB.selectQuery(targetQuery);
                records = queryResultsManager.convertResultSetToObject(true);
                Logging.log("Data reached ddms API. Records fetched: " + queryResultsManager.getRowCount());

                //Insert the data into the temp table in main site
                Logging.log("Setting up shipped data in Main Site...");
                Databases.SQL_SERVER_DB.updateQuery(buildInsertQueries(records, destinationTable,
                        queryResultsManager.getResultColumns()));
                Logging.log("Set up complete.");

                break;

            case "3":
                //Get data from site 3
                Logging.log("Shipping data from Site 3...");
                queryResultsManager = Databases.MYSQL_DB.selectQuery(targetQuery);
                records = queryResultsManager.convertResultSetToObject(true);
                Logging.log("Data reached ddms API. Records fetched: " + queryResultsManager.getRowCount());

                //Insert the data into the temp table in main site
                Logging.log("Setting up shipped data in Main Site...");
                Databases.SQL_SERVER_DB.updateQuery(buildInsertQueries(records, destinationTable,
                        queryResultsManager.getResultColumns()));
                Logging.log("Set up complete.");

                break;
        }
    }

    private static String buildInsertQueries(List<HashMap<String, Object>> records, String targetTable, List<String> resultColumns){
        StringBuilder batchInsertQueryString = new StringBuilder();

        String insertQuery = "insert into "+targetTable+" (";
        String fields = String.join(",", resultColumns);

        insertQuery += fields + ") values (";

        for (int i = 0; i < records.size() ; i++) {
            StringBuilder values = new StringBuilder();
            String insertQ = insertQuery;
            HashMap<String, Object> record =  records.get(i);

            for (int j = 0; j <resultColumns.size(); j++) {
                values.append("'").append(record.get(resultColumns.get(j))).append("'");
                if(j != resultColumns.size()-1) values.append(",");
            }

            insertQ = insertQ+values.toString()+")";
            batchInsertQueryString
                    .append("set identity_insert ")
                    .append(targetTable).append(" on;")
                    .append(insertQ).append(";")
                    .append("set identity_insert ")
                    .append(targetTable).append(" off;");
        }
        return batchInsertQueryString.toString();
    }

    private static String getSelectionClause(SqlStatementComponents sqlStatementComponents, String tableName) {

        StringBuilder selectionClause = new StringBuilder(" where ");
        List<String> fields = FragmentSchema.getFieldsInTable(tableName);
        boolean atLeastOneFieldFeatures = false;

        for (int i = 0; i < sqlStatementComponents.getWhereClauseColumns().size(); i++) {
            for (String field : fields) {
                if (sqlStatementComponents.getWhereClauseColumns().get(i).contains(field)) {
                    atLeastOneFieldFeatures = true;
                    selectionClause.append(field).append(" ")
                            .append(sqlStatementComponents.getWhereClauseOperators().get(i))
                            .append(" ").append(sqlStatementComponents.getWhereClauseValues().get(i));
                    break;
                }
            }
            if(atLeastOneFieldFeatures)
                if (i != sqlStatementComponents.getWhereClauseColumns().size() - 1) selectionClause.append(" and ");
        }
        return (atLeastOneFieldFeatures) ? selectionClause.toString() : "";
    }

    private static void createTemporaryTable(String tableName, String tempTableName, String selection) {
        //Create temp table in main site and copy existing main site data into it
        Logging.log("Applying selection: \""+selection+"\" before selecting the data to copy.");
        Logging.log("Copying data in source table '"+tableName+"', in main site, " +
                "to target table '"+tempTableName+"' (in main site also)");
        Databases.SQL_SERVER_DB.updateQuery("" +
                "SELECT * INTO " + tempTableName +
                " FROM " + tableName + selection);
    }

    private static boolean
    checkIfTableNameExistsAnywhereInQuery(SqlStatementComponents sqlStatementComponents, String tableName) {
        if (sqlStatementComponents.getComponentMetaData().getTableName().equalsIgnoreCase(tableName)) return true;

        for (JoinInfo joinInfo : sqlStatementComponents.getJoinsInfo()) {
            if (joinInfo.getJoinTableName().equalsIgnoreCase(tableName)) return true;
        }

        return false;
    }

    private static void routeToMainSite(SqlStatementComponents sqlStatementComponents) {
        String query = sqlStatementComponents.getOriginalQuery();

        switch (sqlStatementComponents.getSqlStatementType()) {
            case InsertStatement:
            case UpdateStatement:
                Logging.log("Updating table: '" +
                        sqlStatementComponents.getComponentMetaData().getTableName() + "' on Main Site");
                Databases.SQL_SERVER_DB.updateQuery(query);
                break;

            case SelectStatement:
                Logging.log("Selecting data from table: '" +
                        sqlStatementComponents.getComponentMetaData().getTableName() + "' on Main Site");
                QueryResultsManager queryResultsManager = Databases.SQL_SERVER_DB.selectQuery(query);
                sqlStatementComponents.setQueryResultsManager(queryResultsManager);
                break;
        }
    }

    private static void routeToSiteDos(SqlStatementComponents sqlStatementComponents) {
        String query = sqlStatementComponents.getOriginalQuery();

        switch (sqlStatementComponents.getSqlStatementType()) {
            case InsertStatement:
            case UpdateStatement:
                Logging.log("Updating table: '" +
                        sqlStatementComponents.getComponentMetaData().getTableName() + "' on Site 2");
                Databases.POSTGRES_DB.updateQuery(query);
                break;

            case SelectStatement:
                Logging.log("Selecting data from table: '" +
                        sqlStatementComponents.getComponentMetaData().getTableName() + "' on Site 2");
                QueryResultsManager queryResultsManager = Databases.POSTGRES_DB.selectQuery(query);
                sqlStatementComponents.setQueryResultsManager(queryResultsManager);
                break;
        }
    }

    private static void routeToSiteTres(SqlStatementComponents sqlStatementComponents) {
        String query = sqlStatementComponents.getOriginalQuery();

        switch (sqlStatementComponents.getSqlStatementType()) {
            case InsertStatement:
            case UpdateStatement:
                Logging.log("Updating table: '" +
                        sqlStatementComponents.getComponentMetaData().getTableName() + "' on Site 3");
                Databases.MYSQL_DB.updateQuery(query);
                break;

            case SelectStatement:
                Logging.log("Selecting data from table: '" +
                        sqlStatementComponents.getComponentMetaData().getTableName() + "' on Site 3");
                QueryResultsManager queryResultsManager = Databases.MYSQL_DB.selectQuery(query);
                sqlStatementComponents.setQueryResultsManager(queryResultsManager);
                break;
        }
    }

}
