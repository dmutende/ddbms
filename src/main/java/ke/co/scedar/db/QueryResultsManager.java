package ke.co.scedar.db;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class QueryResultsManager {
    
    private ResultSet rs;
    private long rowCount = 0L;
    private Statement statement;
    private List<String> resultColumns = new ArrayList<>();

    public QueryResultsManager(ResultSet rs, Statement statement) {
        this.rs = rs;
        this.statement = statement;
    }

    public long getRowCount() {
        return rowCount;
    }

    public ResultSet getRs() {
        return rs;
    }

    public Statement getStatement() {
        return statement;
    }

    public List<String> getResultColumns() {
        return resultColumns;
    }

    public List<HashMap<String, Object>> convertResultSetToObject(){
        return convertResultSetToObject(true);
    }

    public List<HashMap<String, Object>> convertResultSetToObject(boolean destroyResultSet){
        List<HashMap<String, Object>> records = new ArrayList<>();
        HashMap<String, Object> record;

        try {
            ResultSetMetaData rsMetadata = rs.getMetaData();
            int resultColumnCount = rsMetadata.getColumnCount();
            resultColumns = new ArrayList<>();

            //Get result columns
            for (int i = 1; i < resultColumnCount+1; i++) {
                resultColumns.add(rsMetadata.getColumnLabel(i));
            }

            //Get the data
            if (!rs.next()) {
                //then there are no rows.
                if(destroyResultSet) destroy();
                return records;
            }
            else {
                do {
                    record = new HashMap<>();
                    //Populate row
                    for (String columnLabel : resultColumns){
                        record.put(columnLabel, rs.getObject(columnLabel));
                    }
                    records.add(record);
                    rowCount += 1;
                } while (rs.next());

                if(destroyResultSet) destroy();
                return records;
            }

        } catch (Exception e){
            e.printStackTrace();
            System.err.println("ResultSetParser.toObject() Error: "+e.getMessage());
        }

        return records;
    }
    
    public void destroy(){
        try {

            if (statement != null) {
                try {
                    statement.close();
                } finally {
                    statement = null;
                }
            }

            if (rs != null) {
                try {
                    rs.close();
                } finally {
                    rs = null;
                }
            }

        } catch (Exception e) {
            System.err.println("QueryResultsManager.gc() Error: " + e.getMessage());
        } finally {
            statement = null;
            rs = null;
        }
    }
}
