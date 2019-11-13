package Client.Part1;

import Client.Part2.Record;
import DAO.LiftRideDao;
import io.swagger.client.ApiClient;
import io.swagger.client.ApiException;
import io.swagger.client.ApiResponse;
import io.swagger.client.api.SkiersApi;
import io.swagger.client.model.LiftRide;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Logger;


public class Thread extends java.lang.Thread implements Runnable {
  private int startSkierId;
  private int endSkierId;
  private int startTime;
  private int endTime;
  private int liftNum;
  private int runTimes;
  private Integer dayID;
  private String ip;
  private String port;
  private Logger logger;
  private int req = 0;
  private int success = 0;
  private int failure = 0;
  private CountDownLatch firstCountDown;
  private CountDownLatch secondCountDown;
  private BlockingQueue<Record> records;
  private String url = "resortID/seasonID/dayID/skierID";
  private LiftRideDao liftRideDao;


  public Thread(int startSkierId, int endSkierId, int startTime, int endTime, int liftNum, int runTimes,
      CountDownLatch firstCountDown, CountDownLatch secondCountDown,
      BlockingQueue<Record> records, String ip, String port, Logger logger, LiftRideDao liftRideDao) {
    this.startSkierId = startSkierId;
    this.endSkierId = endSkierId;
    this.startTime = startTime;
    this.endTime = endTime;
    this.liftNum = liftNum;
    this.runTimes = runTimes;
    this.firstCountDown = firstCountDown;
    this.secondCountDown = secondCountDown;
    this.records = records;
    this.ip = ip;
    this.port = port;
    this.logger = logger;
    this.dayID = ThreadLocalRandom.current().nextInt(1, 30);
    this.liftRideDao = liftRideDao;
  }

  @Override
  public void run(){
    try {
      firstCountDown.await();
      for (int i = 0; i < runTimes; i++) {
        try {
          String basePath = ip + ":" + port + "/assignment2_war_exploded";
//          String basePath = ip + ":" + port + "/assignment2_war";
          SkiersApi apiInstance = new SkiersApi();
          ApiClient client = apiInstance.getApiClient();
          client.setBasePath(basePath);
          Integer time = ThreadLocalRandom.current().nextInt(startTime,endTime);
          Integer skierId = ThreadLocalRandom.current().nextInt(startSkierId, endSkierId);

          // build post request body
          LiftRide body = new LiftRide();
          body.setLiftID(ThreadLocalRandom.current().nextInt(1, liftNum));
          body.setTime(time);

          long start = System.currentTimeMillis();
          ApiResponse<Void> api = apiInstance.writeNewLiftRideWithHttpInfo(body, 1,
              "1", dayID.toString(), skierId);
          // phase 3
          if (secondCountDown == null) {
            ApiResponse<Integer> getApi = apiInstance.getSkierDayVerticalWithHttpInfo(1,
                "1", dayID.toString(), skierId);
            dealRequest("POST", start, getApi.getStatusCode());
          }
          dealRequest("GET", start, api.getStatusCode());

        } catch (ApiException e) {
          logger.info("ApiException: " + e.getMessage());
          System.out.println("ApiException: " + e.getMessage());
          countFailure();
        }
      }
    } catch(InterruptedException e) {
      logger.info("Exception for single thread");
      System.out.println(e.getMessage());
      countFailure();
    }
    finally {
      if (secondCountDown != null) {
        secondCountDown.countDown();
      }
    }
  }

  public void dealRequest(String requestType, long start, int statusCode) {
    countReq();
    Record record = new Record(start, System.currentTimeMillis(), requestType, statusCode, url);
    records.add(record);
//    liftRideDao.createRecord(record);
    if (statusCode / 100 == 2) {
      countSuccess();
    } else {
      countFailure();
      logger.info("Request Fail With Status Code" + statusCode);
    }
  }

  public void countReq() { req += 1; }

  public void countSuccess() {
    success += 1;
  }

  public int getReq() {
    return req;
  }

  public int getSuccess() {
    return success;
  }

  public void countFailure() {
    failure += 1;
  }

  public int getFailure() {
    return failure;
  }
}
