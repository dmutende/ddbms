package ke.co.scedar.db;

import com.mchange.v2.c3p0.AbstractConnectionCustomizer;

import java.sql.Connection;
import java.sql.Statement;

public class InitSqlConnectionCustomizer extends AbstractConnectionCustomizer {

    private String getInitSql(String parentDataSourceIdentityToken) {

        return (String) extensionsForToken(parentDataSourceIdentityToken).get("initSql");
    }

    public void onCheckOut(Connection c, String parentDataSourceIdentityToken) {

        String initSql = getInitSql(parentDataSourceIdentityToken);

        if (initSql != null) {
            try (Statement stmt = c.createStatement()) {
                stmt.executeUpdate(initSql);
            } catch (Exception e) {
                System.err.println("InitSqlConnectionCustomizer.onCheckOut() Error: " + e.getMessage());
            }
        }
    }
}