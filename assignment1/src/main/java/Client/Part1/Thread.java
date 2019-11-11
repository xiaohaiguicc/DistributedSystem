package Client.Part1;

import Client.Part2.Record;
import io.swagger.client.ApiClient;
import io.swagger.client.ApiResponse;
import io.swagger.client.api.SkiersApi;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;

public class Thread extends java.lang.Thread implements Runnable {
  private int randomSkierId;
  private int randomTime;
  private int randomLiftNum;
  private int runTimes;
  private String ip;
  private String port;
  private Logger logger;
  private int req = 0;
  private int success = 0;
  private int failure = 0;
  private CountDownLatch firstCountDown;
  private CountDownLatch secondCountDown;
  private BlockingQueue<Record> records;


  public Thread(int randomSkierId, int randomTime, int randomLiftNum, int runTimes,
      CountDownLatch firstCountDown, CountDownLatch secondCountDown,
      BlockingQueue<Record> records, String ip, String port, Logger logger) {
    this.randomLiftNum = randomLiftNum;
    this.randomSkierId = randomSkierId;
    this.randomTime = randomTime;
    this.runTimes = runTimes;
    this.firstCountDown = firstCountDown;
    this.secondCountDown = secondCountDown;
    this.records = records;
    this.ip = ip;
    this.port = port;
    this.logger = logger;
  }

  @Override
  public void run(){
    try {
      firstCountDown.await();
      for (int i = 0; i < runTimes; i++) {
        try {
          String basePath = ip + ":" + port + "/assignment1_war_exploded";
          SkiersApi apiInstance = new SkiersApi();
          ApiClient client = apiInstance.getApiClient();
          client.setBasePath(basePath);

          long start = System.currentTimeMillis();
          ApiResponse<Integer> api = apiInstance.getSkierDayVerticalWithHttpInfo(1,
              "1", Integer.toString(randomTime), randomSkierId);
          countReq();
          long latency = System.currentTimeMillis() - start;
          records.add(new Record(start, latency, api.getStatusCode()));
          int statusCode = api.getStatusCode();
          if (statusCode / 100 == 2) {
            countSuccess();
          } else {
            countFailure();
            logger.info("Request Fail With Status Code" + statusCode);
          }
        } catch (Exception e) {
          logger.info("Exception when get response");
          System.out.println(e.getMessage());
        }
      }
    } catch(InterruptedException e) {
      logger.info("Exception for single thread");
      System.out.println(e.getMessage());
    }
    finally {
      if (secondCountDown != null) {
        secondCountDown.countDown();
      }
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
