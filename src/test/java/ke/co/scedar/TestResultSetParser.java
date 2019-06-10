package ke.co.scedar;

import ke.co.scedar.api.handlers.utils.CustomHandler;
import ke.co.scedar.db.Databases;
import ke.co.scedar.db.QueryResultsManager;
import org.junit.jupiter.api.Test;

public class TestResultSetParser {

    @Test public void parseResultSet(){

        Databases.initialize();
        QueryResultsManager resultsManager = Databases.SQL_SERVER_DB.selectQuery("select * from branches");
        System.out.println(CustomHandler.toJson(resultsManager.convertResultSetToObject(true)));
    }

}
