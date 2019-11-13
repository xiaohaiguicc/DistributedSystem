package DAO;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;
import javax.swing.plaf.synth.SynthTextAreaUI;
import org.apache.commons.dbcp2.*;
import java.sql.*;

public class DBCPDataSource {
  private static BasicDataSource dataSource;

  // NEVER store sensitive information below in plain text!
  private static final String HOST_NAME = "localhost";
//  private static final String HOST_NAME = "database-1";
  private static final String PORT = "3306";
  private static final String DATABASE = "LiftRides";
  private static final String USERNAME = "root";
  private static final String PASSWORD = "1";

  static {
    // https://dev.mysql.com/doc/connector-j/8.0/en/connector-j-reference-jdbc-url-format.html
    dataSource = new BasicDataSource();
    String url = String.format("jdbc:mysql://%s:%s/%s?serverTimezone=UTC", HOST_NAME, PORT, DATABASE);
    dataSource.setUrl(url);
    dataSource.setUsername(USERNAME);
    dataSource.setPassword(PASSWORD);
    dataSource.setInitialSize(10);
    dataSource.setMinIdle(0);
    dataSource.setMaxIdle(-1);
    dataSource.setMaxTotal(-1);
  }

  public static Connection getConnection() throws SQLException {
    try {
      Class.forName("com.mysql.cj.jdbc.Driver");
      return dataSource.getConnection();
    } catch (ClassNotFoundException e) {
      StringWriter sw = new StringWriter();
      PrintWriter pw = new PrintWriter(sw);
      e.printStackTrace(pw);
    }
    return null;
  }

  public static void closeConnection(Connection connection) throws SQLException {
    try {
      connection.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
}