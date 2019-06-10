package ke.co.scedar.db;

import com.fasterxml.jackson.core.type.TypeReference;
import ke.co.scedar.api.handlers.utils.CustomHandler;
import ke.co.scedar.db.fragment_schema.Site;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Databases {

    public static Database SQL_SERVER_DB;
    public static Database POSTGRES_DB;
    public static Database MYSQL_DB;
    public static List<Site> FRAGMENT_SCHEMA;

    /**
     * Initialize the databases
     */
    @SuppressWarnings("unchecked")
    public static void initialize() {

        //Initialize database connections and objects
        SQL_SERVER_DB = new Database(
                DbVendor.MicrosoftSQL, "TransyDB",
                "localhost", 1433, "elon", "dpm854419@UoN",
                "config.xml", true
        );

        POSTGRES_DB = new Database(
                DbVendor.PostgresSQL, "transy_db",
                "localhost", 5432, "elon", "dpm854419@UoN",
                "config.xml", true
        );

        MYSQL_DB = new Database(
                DbVendor.MySQL, "transy_db",
                "localhost", 3306, "elon_transy", "dpm854419@UoN",
                "config.xml", true
        );

        //Initialize fragment schema
        try {
            String strFragmentSchema = new String(Files.readAllBytes(Paths.get("fragment-schema.json")));
            FRAGMENT_SCHEMA = (List<Site>)
                    CustomHandler.getObject(strFragmentSchema, new TypeReference<List<Site>>() {});
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
