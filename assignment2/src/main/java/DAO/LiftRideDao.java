package DAO;

import Client.Part2.Record;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class LiftRideDao {
  public static final int MAX_TRY = 10;
  private static final int MAX_QUEUE_SIZE = 500;
  private static final int QUEUE_WAIT_TIME_MS = 1000;
  private BlockingQueue<String> queueLiftRide = new ArrayBlockingQueue<String>(MAX_QUEUE_SIZE);
  private BlockingQueue<String> queueRecord = new ArrayBlockingQueue<String>(MAX_QUEUE_SIZE);
  private Timer timer;

  public LiftRideDao() {
    timer = new Timer();
    timer.schedule(new Task(), QUEUE_WAIT_TIME_MS);
  }

  public boolean createLiftRide(LiftRide newLiftRide) {
//    String insertQueryStatement = "INSERT INTO LiftRides (skierId, resortId, seasonId, dayId, time, liftId, vertical) " +
//        "VALUES (?,?,?,?,?,?,?)";
//    Connection conn = null;
//    PreparedStatement preparedStatement = null;
//    try {
//      conn = DBCPDataSource.getConnection();
//      preparedStatement = conn.prepareStatement(insertQueryStatement);
//      preparedStatement.setInt(1, newLiftRide.getSkierId());
//      preparedStatement.setInt(2, newLiftRide.getResortId());
//      preparedStatement.setInt(3, newLiftRide.getSeasonId());
//      preparedStatement.setInt(4, newLiftRide.getDayId());
//      preparedStatement.setInt(5, newLiftRide.getTime());
//      preparedStatement.setInt(6, newLiftRide.getLiftId());
//      preparedStatement.setInt(7, newLiftRide.getVertical());
//
//      // execute insert SQL statement
//      preparedStatement.executeUpdate();
//      return "success";
//    } catch (SQLException e) {
//      StringWriter sw = new StringWriter();
//      PrintWriter pw = new PrintWriter(sw);
//      e.printStackTrace(pw);
//
//      return sw.toString();
//    } finally {
//      try {
//        if (conn != null) {
//          DBCPDataSource.closeConnection(conn);
//        }
//        if (preparedStatement != null) {
//          preparedStatement.close();
//        }
//      } catch (SQLException se) {
//        se.printStackTrace();
//      }
//    }
    String insertQueryStatement = "INSERT INTO LiftRides (skierId, resortId, seasonId, dayId, time, liftId, vertical) " +
        "VALUES ("
        + newLiftRide.getSkierId() + ","
        + newLiftRide.getResortId() + ","
        + newLiftRide.getSeasonId() + ","
        + newLiftRide.getDayId() + ","
        + newLiftRide.getTime() + ","
        + newLiftRide.getLiftId() + ","
        + newLiftRide.getVertical() + ")";
    synchronized (queueLiftRide) {
      queueLiftRide.add(insertQueryStatement);
      if (queueLiftRide.size() >= MAX_QUEUE_SIZE) {
        return batchInsert(queueLiftRide);
      }
    }
    return true;
  }

  public boolean batchInsert(BlockingQueue<String> queue) {
    synchronized (queue) {
      boolean finished = false;
      int num_try = 0;
      while (!finished && num_try < MAX_TRY) {
        Connection conn = null;
        try {
          conn = DBCPDataSource.getConnection();
          Statement updateStatement = conn.createStatement();
          for (String query : queue) {
            updateStatement.addBatch(query);
          }

          updateStatement.executeBatch();
          finished = true;
        } catch (SQLException e) {
          e.printStackTrace();
          num_try++;
        } finally {
          try {
            if (conn != null) {
              DBCPDataSource.closeConnection(conn);
            }
          } catch (SQLException se) {
            se.printStackTrace();
          }
        }
      }
        queue.clear();
        return finished;
      }
  }

  public String getVerticalForSpecificDay(int skierId, int seasonId, int resortId, int dayId) {
    String getQueryStatement
        = "SELECT SUM(vertical) as vertical FROM LiftRides"
        + " WHERE skierId = " + skierId
        + " AND seasonId = " + seasonId
        + " AND resortId = " + resortId
        + " AND dayId = " + dayId
        + ";";

    Integer totalVertical = 0;
    Connection conn = null;
    PreparedStatement preparedStatement = null;
    try {
      conn = DBCPDataSource.getConnection();
      preparedStatement = conn.prepareStatement(getQueryStatement);
      ResultSet resultSet = preparedStatement.executeQuery();
      if (resultSet.next()) {
        totalVertical = resultSet.getInt(1);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      try {
        if (conn != null) {
          DBCPDataSource.closeConnection(conn);
        }
        if (preparedStatement != null) {
          preparedStatement.close();
        }
      } catch (SQLException se) {
        se.printStackTrace();
      }
    }
    return totalVertical.toString();
  }

  public String getVerticalForSpecificResort(int skierId) {
    String getQueryStatement
        = "SELECT seasonId, SUM(vertical) as vertical FROM LiftRides"
        + " WHERE skierId = " + skierId
        + " GROUP BY seasonId"
        +";";

    Connection conn = null;
    PreparedStatement preparedStatement = null;
    StringBuilder sb = new StringBuilder();
    try {
      conn = DBCPDataSource.getConnection();
      preparedStatement = conn.prepareStatement(getQueryStatement);
      ResultSet resultSet = preparedStatement.executeQuery();
      sb = new StringBuilder();
      while (resultSet.next()) {
        sb.append("seasonID: " + resultSet.getInt(1) + ",");
        sb.append("totalVertical: " + resultSet.getInt(2) + "\n");
      }
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      try {
        if (conn != null) {
          DBCPDataSource.closeConnection(conn);
        }
        if (preparedStatement != null) {
          preparedStatement.close();
        }
      } catch (SQLException se) {
        se.printStackTrace();
      }
    }
    return sb == null ? null : sb.toString();
  }

  public boolean createRecord(Record record) {
    String insertQueryStatement = "INSERT INTO statistics(url, requestType, mean, max) " +
        "VALUES ("
        + "\"" + record.getUrl() + "\","
        + "\"" + record.getRequestType() + "\","
        + record.getLatency() + ","
        + record.getLatency() + ")";
    synchronized (queueRecord) {
      queueRecord.add(insertQueryStatement);
      if (queueRecord.size() >= MAX_QUEUE_SIZE) {
        return batchInsert(queueRecord);
      }
    }
    return true;
  }

  public List<Statistics> getStatistics() {
    String getQueryStatement = "SELECT url, requestType, AVG(mean), MAX(max) FROM statistics GROUP BY url, requestType;";

    Connection conn = null;
    PreparedStatement preparedStatement = null;
    List<Statistics> statistics = new ArrayList<Statistics>();
    try {
      conn = DBCPDataSource.getConnection();
      preparedStatement = conn.prepareStatement(getQueryStatement);
      ResultSet resultSet = preparedStatement.executeQuery();
      while (resultSet.next()) {
        statistics.add(new Statistics(resultSet.getString(1), resultSet.getString(2), resultSet.getInt(3), resultSet.getInt(4)));
      }
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      try {
        if (conn != null) {
          DBCPDataSource.closeConnection(conn);
        }
        if (preparedStatement != null) {
          preparedStatement.close();
        }
      } catch (SQLException se) {
        se.printStackTrace();
      }
    }
    return statistics;
  }

  public static void main(String[] args) {
//    LiftRideDao liftRideDao = new LiftRideDao();
//    liftRideDao.createLiftRide(new LiftRide(11, 2, 3, 5, 500, 20));
//    liftRideDao.createRecord(new Record(1,5, "GET", 200, "resortID/seasonID/days/skierID"));
//    List<Statistics> statistics = liftRideDao.getStatistics();
//    for (Statistics statistics1: statistics) {
//      System.out.print(statistics1.toString());
//    }
//    String result = liftRideDao.getVerticalForSpecificResort(-1);
//    String result = liftRideDao.getVerticalForSpecificDay(1, 1, 31, 31);
//    System.out.print(result);
  }

  private void restartTimer() {
    if (timer != null) {
      timer.cancel(); //Terminate the timer thread
    }
    timer = new Timer();
    timer.schedule(new Task(), QUEUE_WAIT_TIME_MS);
  }

  class Task extends TimerTask {
    public void run() {
      if (!queueLiftRide.isEmpty()) {
        batchInsert(queueLiftRide);
      }
      if (!queueRecord.isEmpty()) {
        batchInsert(queueRecord);
      }
      restartTimer();
    }
  }
}
