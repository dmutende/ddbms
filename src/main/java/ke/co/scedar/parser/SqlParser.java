package ke.co.scedar.parser;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TBaseType;
import gudusoft.gsqlparser.TCustomSqlStatement;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.*;
import gudusoft.gsqlparser.pp.para.GFmtOpt;
import gudusoft.gsqlparser.pp.para.GFmtOptFactory;
import gudusoft.gsqlparser.pp.stmtformatter.FormatterFactory;
import gudusoft.gsqlparser.stmt.TInsertSqlStatement;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import gudusoft.gsqlparser.stmt.TUpdateSqlStatement;
import ke.co.scedar.db.fragment_schema.ComponentMetaData;
import ke.co.scedar.db.fragment_schema.ComponentType;
import ke.co.scedar.db.fragment_schema.FragmentSchema;
import ke.co.scedar.utils.ExpressionEvaluator;
import ke.co.scedar.utils.JoinInfo;
import ke.co.scedar.utils.Logging;
import ke.co.scedar.utils.SqlStatementType;

import java.util.HashMap;
import java.util.List;

public class SqlParser {

    private SqlStatementComponents sqlStatementComponents;

    public SqlStatementComponents parse(String sqlStatement, EDbVendor dbVendor) {
        sqlStatementComponents = new SqlStatementComponents();
        sqlStatementComponents.setOriginalQuery(sqlStatement);

        TGSqlParser sqlParser = new TGSqlParser(dbVendor);
        sqlParser.sqltext = sqlStatement;

        if (sqlParser.parse() > 0) {
            System.err.println("Error parsing sql statement. Error: " + sqlParser.getErrormessage());
            Logging.log("Syntax Errors: " + sqlParser.getSyntaxErrors(), 0);
            Logging.log("SQL Statement: " + sqlParser.sqltext, 0);
            Logging.log("Error count: " + sqlParser.getErrorCount(), 0);

            sqlStatementComponents.setHasErrors(true);
            sqlStatementComponents.setSyntaxErrors(sqlParser.getSyntaxErrors());
            sqlStatementComponents.setErrorMessage(sqlParser.getErrormessage() + " <NB: Default SQL Dialect -> MySQL>");
            sqlStatementComponents.setErrorCount(sqlParser.getErrorCount());
            return sqlStatementComponents;
        }

        Logging.log("Parsing SQL Statement: ", 0);
        printSql(sqlParser);

        Logging.log("Details:", 0);
        TCustomSqlStatement customSqlStatement = sqlParser.sqlstatements.get(0);
        analyzeSqlStatement(customSqlStatement);

        Logging.log("***********************************************************************\n", 0);

        verifySqlStatementComponents(sqlStatementComponents);

        Logging.log("******** SQL Statement Components *********", 0);
        Logging.log(sqlStatementComponents);
        Logging.log("******** END of SQL Statement Components *********", 0);

        return sqlStatementComponents;
    }

    private void verifySqlStatementComponents(SqlStatementComponents sqlStatementComponents) {

        ComponentMetaData componentMetaData =
                FragmentSchema.doesTableExist(sqlStatementComponents.getTableName());

        if (componentMetaData.hasError()) {
            sqlStatementComponents.setHasErrors(true);
            sqlStatementComponents.setErrorMessage("Object '" + sqlStatementComponents.getTableName()
                    + "' <type:" + componentMetaData.getComponentType().value()
                    + "> in cartesian product clause does not exist in current application schema");
            sqlStatementComponents.setErrorCount(1);
            return;
        }

        //Check projection columns
        for (int i = 0; i < sqlStatementComponents.getColumnsToProject().size(); i++) {
            if(!componentMetaData.hasField(sqlStatementComponents.getColumnsToProject().get(i))){
                //Try check for aliases in case of function column projections i.e sum() or count()
                try{
                    String alias = sqlStatementComponents.getColumnAliases().get(i);
                    if(alias.equals("NO_ALIAS")){
                        sqlStatementComponents.setHasErrors(true);
                        sqlStatementComponents.setErrorMessage("Object '"
                                + sqlStatementComponents.getColumnsToProject().get(i)
                                + "' <type:" + ComponentType.Field.value()
                                + "> in projection clause does not exist in current application schema");
                        sqlStatementComponents.setErrorCount(1);
                        return;
                    }
                } catch (IndexOutOfBoundsException ignore){

                } catch (Exception e){
                    e.printStackTrace();
                    System.err.println("SqlParser.verifySqlStatementComponents() Error: " + e.getMessage());
                }
            }
        }

        //Check where clause columns for non joins
        for (String field : sqlStatementComponents.getWhereClauseColumns()) {

            if(sqlStatementComponents.getJoinsInfo().size() > 0){
                if(!field.contains(sqlStatementComponents.getTableAlias()+".")) continue;
                field = field.replaceAll(sqlStatementComponents.getTableAlias() + "\\.", "");
            }

            if (!componentMetaData.hasField(field)) {
                sqlStatementComponents.setHasErrors(true);
                sqlStatementComponents.setErrorMessage("Object '" + field
                        + "' <type:" + ComponentType.Field.value()
                        + "> in where clause does not exist in current application schema");
                sqlStatementComponents.setErrorCount(1);
                return;
            }
        }

        //Verify joins
        if (sqlStatementComponents.getJoinsInfo().size() > 0) {
            for (JoinInfo joinInfo : sqlStatementComponents.getJoinsInfo()) {

                //Verify Join Table
                ComponentMetaData joinTableMetaData = FragmentSchema.doesTableExist(joinInfo.getJoinTableName());
                if (joinTableMetaData.hasError()) {
                    sqlStatementComponents.setHasErrors(true);
                    sqlStatementComponents.setErrorMessage("Object '" + joinInfo.getJoinTableName()
                            + "' <type:" + joinTableMetaData.getComponentType().value()
                            + "> in join clause does not exist in current application schema");
                    sqlStatementComponents.setErrorCount(1);
                    return;
                }

                //Verify Join columns
                for (String column : joinInfo.getJoinColumns()) {
                    String joinColumn = column.replaceAll(joinInfo.getJoinTableAlias() + "\\.", "");
                    if (!joinTableMetaData.hasField(joinColumn)) {
                        sqlStatementComponents.setHasErrors(true);
                        sqlStatementComponents.setErrorMessage("Object '" + joinColumn
                                + "' <type:" + ComponentType.Field.value()
                                + "> in inner join clause for relation '" + joinInfo.getJoinTableName()
                                + "' does not exist in current application schema");
                        sqlStatementComponents.setErrorCount(1);
                        return;
                    }
                }
            }
        }
        sqlStatementComponents.setComponentMetaData(componentMetaData);
    }

    private void analyzeSqlStatement(TCustomSqlStatement sqlStatement) {
        switch (sqlStatement.sqlstatementtype) {
            case sstselect:
                sqlStatementComponents.setSqlStatementType(SqlStatementType.SelectStatement);
                analyzeSelectQuery((TSelectSqlStatement) sqlStatement);
                break;

            case sstupdate:
                sqlStatementComponents.setSqlStatementType(SqlStatementType.UpdateStatement);
                analyzeUpdateStatement((TUpdateSqlStatement) sqlStatement);
                break;

            case sstinsert:
                sqlStatementComponents.setSqlStatementType(SqlStatementType.InsertStatement);
                analyzeInsertStatement((TInsertSqlStatement) sqlStatement);
                break;

            default:
                sqlStatementComponents.setSqlStatementType(SqlStatementType.UnknownStatement);
                Logging.log(sqlStatement.sqlstatementtype.toString(), 0);
                Logging.log(sqlStatement.toString(), 0);
                try {
                    throw new Exception("Unknown SQL Statement");
                } catch (Exception e) {
                    System.err.println("parser.SqlParser.analyzeSqlStatement() Error: " + e.getMessage());
                }
        }
    }

    private void analyzeSelectQuery(TSelectSqlStatement selectSqlStatement) {

        for (int i = 0; i < selectSqlStatement.getResultColumnList().size(); i++) {
            TResultColumn resultColumn = selectSqlStatement.getResultColumnList().getResultColumn(i);
            if (resultColumn.getAliasClause() != null) {
                Logging.log("Project Column: " + resultColumn.getColumnNameOnly() + " (Alias: " + resultColumn.getColumnAlias() + ")", 0);
                sqlStatementComponents.addProjectionColumn(resultColumn.getColumnNameOnly(), resultColumn.getColumnAlias());
            } else {
                Logging.log("Project Column: " + resultColumn.getColumnNameOnly(), 0);
                sqlStatementComponents.addProjectionColumn(resultColumn.getColumnNameOnly(), null);
            }
        }

        for (int i = 0; i < selectSqlStatement.joins.size(); i++) {
            TJoin join = selectSqlStatement.joins.getJoin(i);

            sqlStatementComponents.setTableName(join.getTable().getName());
            sqlStatementComponents.setTableFullName(join.getTable().getFullName());
            sqlStatementComponents.setTableAlias(join.getTable().getAliasName());

            Logging.log("Table Name: " + join.getTable().getName(), 0);
            Logging.log("Table Full Name: " + join.getTable().getFullName(), 0);
            Logging.log("Table Alias: " + join.getTable().getAliasName(), 0);

            if (join.getKind() == TBaseType.join_source_table) {
                for (int j = 0; j < join.getJoinItems().size(); j++) {
                    TJoinItem joinItem = join.getJoinItems().getJoinItem(j);

                    JoinInfo joinInfo = new JoinInfo(joinItem.getJoinType().name(), joinItem.getTable().getName(),
                            joinItem.getTable().getAliasName(), joinItem.getTable().getFullName());

                    Logging.log("Join Type: " + joinItem.getJoinType().name(), 0);
                    Logging.log("Join Table Name: " + joinItem.getTable().getName(), 0);
                    Logging.log("Join Table Full Name: " + joinItem.getTable().getFullName(), 0);
                    Logging.log("Join Table Alias: " + joinItem.getTable().getAliasName(), 0);

                    if (joinItem.getOnCondition() != null) {
                        Logging.log("Join Condition (On) : " + joinItem.getOnCondition(), 0);

                        HashMap<String, List<String>> explodedExpr =
                                evaluateExpression(joinItem.getOnCondition(), "join");
                        joinInfo.setJoinColumns(explodedExpr.get("joinColumns"));
                        joinInfo.setJoinOperators(explodedExpr.get("joinOperators"));
                        joinInfo.setJoinValues(explodedExpr.get("joinValues"));
                        joinInfo.setJoinTokenBeforeExpression(explodedExpr.get("joinTokenBeforeExpression"));
                        joinInfo.setJoinConditionString(joinItem.getOnCondition().toString());
                    } else if (joinItem.getUsingColumns() != null) {
                        Logging.log("Join Columns: " + joinItem.getUsingColumns().toString(), 0);
                        joinInfo.setJoinConditionString(joinItem.getUsingColumns().toString());
                    }

                    sqlStatementComponents.addJoinInfo(joinInfo);
                }
            }
        }

        setWhereClauseMetadata(selectSqlStatement.getWhereClause());
    }

    private void analyzeUpdateStatement(TUpdateSqlStatement updateSqlStatement) {
        Logging.log("Table Name: " + updateSqlStatement.getTargetTable(), 0);
        Logging.log("Set Clause:", 0);
        for (int i = 0; i < updateSqlStatement.getResultColumnList().size(); i++) {
            TResultColumn resultColumn = updateSqlStatement.getResultColumnList().getResultColumn(i);
            TExpression expression = resultColumn.getExpr();
            Logging.log("\tColumn: " + expression.getLeftOperand() +
                    " Value: " + expression.getRightOperand(), 0);
        }
        setWhereClauseMetadata(updateSqlStatement.getWhereClause());
    }

    private void analyzeInsertStatement(TInsertSqlStatement insertSqlStatement) {
        if (insertSqlStatement.getTargetTable() != null) {
            Logging.log("Table Name: " + insertSqlStatement.getTargetTable(), 0);
        }

        if (insertSqlStatement.getColumnList() != null) {
            System.out.print("Columns: ");
            for (int i = 0; i < insertSqlStatement.getColumnList().size(); i++) {
                System.out.print(insertSqlStatement.getColumnList().getObjectName(i));
                if (!(i == insertSqlStatement.getColumnList().size() - 1))
                    System.out.print(", ");
            }
        }

        Logging.log("", 0);

        if (insertSqlStatement.getValues() != null) {
            System.out.print("Values: ");
            for (int i = 0; i < insertSqlStatement.getValues().size(); i++) {
                TMultiTarget mt = insertSqlStatement.getValues().getMultiTarget(i);
                for (int j = 0; j < mt.getColumnList().size(); j++) {
                    System.out.print(mt.getColumnList().getResultColumn(j));
                    if (!(j == mt.getColumnList().size() - 1))
                        System.out.print(", ");
                }
            }
        }
        Logging.log("", 0);
    }

    private HashMap<String, List<String>> evaluateExpression(TExpression expression, String expressionType) {
        ExpressionEvaluator expressionEvaluator = new ExpressionEvaluator(expression);
        HashMap<String, List<String>> explodedExpressions = new HashMap<>();

        explodedExpressions.put(expressionType + "Columns", expressionEvaluator.getColumns());
        explodedExpressions.put(expressionType + "Operators", expressionEvaluator.getOperators());
        explodedExpressions.put(expressionType + "Values", expressionEvaluator.getValues());
        explodedExpressions.put(expressionType + "TokenBeforeExpression",
                expressionEvaluator.getTokenBeforeExpression());

        Logging.log(expressionType + " Condition: " + expression, 0);
        Logging.log("\tColumn: " + expressionEvaluator.getColumns(), 0);
        Logging.log("\tOperator: " + expressionEvaluator.getOperators(), 0);
        Logging.log("\tValue: " + expressionEvaluator.getValues(), 0);
        Logging.log("\tToken Before Expr: " + expressionEvaluator.getTokenBeforeExpression(), 0);

        return explodedExpressions;
    }

    private void setWhereClauseMetadata(TWhereClause whereClause) {
        if (whereClause != null) {
            HashMap<String, List<String>> explodedExpr =
                    evaluateExpression(whereClause.getCondition(), "where");
            sqlStatementComponents.setWhereClauseColumns(explodedExpr.get("whereColumns"));
            sqlStatementComponents.setWhereClauseOperators(explodedExpr.get("whereOperators"));
            sqlStatementComponents.setWhereClauseValues(explodedExpr.get("whereValues"));
            sqlStatementComponents.setWhereClauseTokenBeforeExpression(explodedExpr.get("whereTokenBeforeExpression"));
            sqlStatementComponents.setWhereConditionString(
                    whereClause.getCondition().toString());
        }
    }

    private void printSql(TGSqlParser sqlParser) {
        GFmtOpt option = GFmtOptFactory.newInstance();
        String result = FormatterFactory.pp(sqlParser, option);
        Logging.log(result, 0);
        Logging.log("", 0);
    }

}
