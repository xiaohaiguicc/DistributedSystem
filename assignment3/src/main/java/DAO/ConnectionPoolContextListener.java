package DAO;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import java.sql.DriverManager;

public class ConnectionPoolContextListener {

  // Saving credentials in environment variables is convenient, but not secure - consider a more
  // secure solution such as https://cloud.google.com/kms/ to help keep secrets safe.
  private static final String CLOUD_SQL_CONNECTION_NAME = "cs6650a3:us-east1:cs6650";
  private static final String DB_USER = "root";
  private static final String DB_PASS = "1";
  private static final String DB_NAME = "LiftRides";
  private static DataSource pool = null;

  public Connection getConnection() throws SQLException {
      HikariConfig config = new HikariConfig();

      // Configure which instance and what database user to connect with.
      config.setJdbcUrl(String.format("jdbc:mysql:///%s", DB_NAME));
      config.setUsername(DB_USER); // e.g. "root", "postgres"
      config.setPassword(DB_PASS); // e.g. "my-password"

      // For Java users, the Cloud SQL JDBC Socket Factory can provide authenticated connections.
      // See https://github.com/GoogleCloudPlatform/cloud-sql-jdbc-socket-factory for details.
      config.addDataSourceProperty("socketFactory", "com.google.cloud.sql.mysql.SocketFactory");
      config.addDataSourceProperty("cloudSqlInstance", CLOUD_SQL_CONNECTION_NAME);
      config.addDataSourceProperty("useSSL", "false");
      config.setMaxLifetime(28740000);
      config.setIdleTimeout(28740000);
      pool = new HikariDataSource(config);
//      Class.forName("com.mysql.cj.jdbc.Driver");
      return pool.getConnection();
  }

//  @Override
//  public void contextDestroyed(ServletContextEvent event) {
//    // This function is called when the Servlet is destroyed.
//    HikariDataSource pool = (HikariDataSource) event.getServletContext().getAttribute("my-pool");
//    if (pool != null) {
//      pool.close();
//    }
//  }
//
//  @Override
//  public void contextInitialized(ServletContextEvent event) {
//    // This function is called when the application starts and will safely create a connection pool
//    // that can be used to connect to.
//    DataSource pool = (DataSource) event.getServletContext().getAttribute("my-pool");
//    if (pool == null) {
//      pool = createConnectionPool();
//      event.getServletContext().setAttribute("my-pool", pool);
//    }
//  }
  public static void closeConnection(Connection connection) throws SQLException {
    try {
      connection.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
}
