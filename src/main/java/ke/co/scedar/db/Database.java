package ke.co.scedar.db;

import com.mchange.v2.c3p0.DataSources;
import com.mchange.v2.c3p0.PooledDataSource;
import ke.co.scedar.utils.Logging;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

//TODO: Implement Formatted Logging Maybe log4j or slf4j
//      Implement JNDI Server instance
public class Database {

    private String c3p0ConfFilePath;
    private String JNDI;
    private DbVendor vendor;
    private Class databaseDriverClazz;
    private StandaloneJNDIServer standaloneJNDIServer;
    private DataSource dataSource;
    private String name;
    private String host;
    private int port;
    private String username;
    private String password;
    private String connectionUrl;
    private Connection connection = null;
    private Statement sqlQueryStatement;
    private ResultSet rs;
    private boolean lazyLoad = false;
    private boolean pooled;

    public Database(DbVendor vendor, String name, String host, int port,
                    String username, String password, String c3p0ConfFilePath, boolean pooled) {
        this.vendor = vendor;
        this.name = name;
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
        this.c3p0ConfFilePath = c3p0ConfFilePath;
        this.pooled = pooled;
        this.JNDI = "ke.co.scedar.Database.JNDI-" + vendor.value() + "-" + name + "-" + UUID.randomUUID();

        switch (vendor) {
            case PostgresSQL: {
                try {
                    databaseDriverClazz = Class.forName("org.postgresql.Driver");
                } catch (Exception e) {
                    System.err.println("Database.Database() Error: " + e.getMessage());
                }

                connectionUrl = "jdbc:postgresql://" + host + ":" + port + "/" + name;
                break;
            }

            case MicrosoftSQL: {
                try {
                    databaseDriverClazz = Class.forName("com.microsoft.sqlserver.jdbc.DriverJDBCVersion");
                } catch (Exception e) {
                    System.err.println("Database.Database() Error: " + e.getMessage());
                }

                connectionUrl = "jdbc:sqlserver://" + host + ":" + port + ";databaseName=" + name;
                break;
            }

            // Default -> MySQL
            default: {
                try {
                    databaseDriverClazz = Class.forName("com.mysql.cj.jdbc.Driver");
                } catch (Exception e) {
                    System.err.println("Database.Database() Error: " + e.getMessage());
                }

                connectionUrl = "jdbc:mysql://" + host + ":" + port + "/" +
                        name + "?useSSL=false&createDatabaseIfNotExist=true";
            }
        }

        connect();
    }

    private void connect() {
        try {

            System.out.println();
            System.out.println("Setting up Database Connection to - " + vendor.value() + " - " + name + " ...");

            dataSource = DataSources.unpooledDataSource(connectionUrl, username, password);

            if (pooled) dataSource = DataSources.pooledDataSource(dataSource, getOverrides());

//            standaloneJNDIServer = new StandaloneJNDIServer(JNDI, dataSource);
            System.out.println("DataSource bound to Java Naming and Directory Interface under the name '" + JNDI + "'");

            connection = dataSource.getConnection();

            System.out.println("Connected to: " + name);
            System.out.println("Using DB user: " + username);
            System.out.println("Connection URL: " + connectionUrl);

        } catch (Exception e) {
            System.err.println("Database.connect() Error: " + e.getMessage());
        }
    }

    public QueryResultsManager selectQuery(String query) {
        try {
            Logging.log();
            Logging.log("Query Execution Destination Details");
            Logging.log("Connected to - " + vendor.value() + " - " + name);
            Logging.log("Using DB user - " + username);
            Logging.log("Connection URL - " + connectionUrl);
            System.out.println("\nSELECT QUERY: " + query);

            sqlQueryStatement = connection.createStatement(
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);

            if (lazyLoad) {
                sqlQueryStatement.setFetchSize(Integer.MIN_VALUE);
                lazyLoad = false;
            }

            rs = sqlQueryStatement.executeQuery(query);

            if (rs == null)
                return null;

            return new QueryResultsManager (rs, sqlQueryStatement);

        } catch (Exception e) {
            System.err.println("Database.selectQuery() Error: " + e.getMessage());
        } finally {
            System.out.println("EXECUTED QUERY");
        }

        return null;
    }

    public QueryResultsManager selectQuery(String query, boolean lazyLoadResults) {
        lazyLoad = lazyLoadResults;
        return selectQuery(query);
    }

    public int updateQuery(String query) {
        try {

            Logging.log("\nUPDATE QUERY: " + query);

            sqlQueryStatement = connection.createStatement();

            return sqlQueryStatement.executeUpdate(query);

        } catch (Exception e) {
            System.err.println("Database.updateQuery() Error: " + e.getMessage());
        } finally {
            Logging.log("EXECUTED QUERY");
        }

        return -1;
    }

    public void disconnect() {
        try {
            DataSources.destroy(dataSource);
        } catch (Exception e) {
            System.err.println("Database.disconnect() Error: " + e.getMessage());
        } finally {
            connection = null;
        }
    }

    public void gc() {
        try {
            disconnect();
        } catch (Exception e) {
            System.err.println("Database.gc() Error: " + e.getMessage());
        } finally {
            dataSource = null;
            connection = null;
            sqlQueryStatement = null;
            rs = null;
        }
    }

    private Map getOverrides() {
        HashMap<String, String> overriders = new HashMap<>();
        overriders.put("idleConnectionTestPeriod", "30");
        overriders.put("initialPoolSize", "10");
        overriders.put("maxIdleTime", "30");
        overriders.put("maxPoolSize", "100");
        overriders.put("minPoolSize", "10");
        overriders.put("maxStatements", "200");
        overriders.put("maxStatementsPerConnection", "50");
        return overriders;
    }

    public DbVendor getVendor() {
        return vendor;
    }

    public String getName() {
        return name;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getUsername() {
        return username;
    }

    public String getConnectionUrl() {
        return connectionUrl;
    }

    public Connection getConnection() {
        return connection;
    }

    public String getC3p0ConfFilePath() {
        return c3p0ConfFilePath;
    }

    public String getJNDI() {
        return JNDI;
    }

    public boolean isPooled() {
        return pooled;
    }

    public void dumpCurrentPooledDatasourceStatus() {
        try {
            if (pooled) {
//                InitialContext ictx = new InitialContext();
//                DataSource ds = (DataSource) ictx.lookup(JNDI);

                if (dataSource instanceof PooledDataSource) {
                    PooledDataSource pds = (PooledDataSource) dataSource;
                    System.out.println("Pooled Datasource Status for - " + vendor.value() + " - " + name);
                    System.err.println("Datasource: " + pds.getDataSourceName());
                    System.err.println("Uptime: " + pds.getUpTimeMillisDefaultUser());
                    System.err.println("Identity Token: " + pds.getIdentityToken());
                    System.err.println("Connections: " + pds.getNumConnectionsAllUsers());
                    System.err.println("Busy Connections: " + pds.getNumBusyConnectionsAllUsers());
                    System.err.println("Idle Connections: " + pds.getNumIdleConnectionsAllUsers());
                    System.err.println("Closed Connections: " + pds.getNumUnclosedOrphanedConnectionsAllUsers());
                    System.err.println("Cached Statements: " + pds.getStatementCacheNumStatementsAllUsers());
                    System.err.println("Thread Pool Size: " + pds.getThreadPoolSize());
                    System.err.println("Thread Pool - Active Threads: " + pds.getThreadPoolNumActiveThreads());
                    System.err.println("Thread Pool - Idle Threads: " + pds.getThreadPoolNumIdleThreads());
                    System.err.println("Thread Pool - Task Pending Threads: " + pds.getThreadPoolNumTasksPending());
                    System.err.println("Thread Pool - Helper Threads: " + pds.getNumHelperThreads());
                    System.err.println();
                } else System.err.println("Not a c3p0 PooledDataSource!");
            } else {
                System.err.println("Not a c3p0 PooledDataSource!");
            }
        } catch (Exception e) {
            System.err.println("Database.dumpCurrentPooledDatabaseSourceStatus() Error: " + e.getMessage());
        }
    }

    public static void destroy(Object... databases) {
        for (Object database : databases) {
            System.err.println("\nDESTROYING " + ((Database) database).getVendor().value() +
                    " - " + ((Database) database).getName() + "...");
//            ((Database) database).clearResults();
            ((Database) database).disconnect();
            ((Database) database).gc();
            database = null;
        }
    }
}