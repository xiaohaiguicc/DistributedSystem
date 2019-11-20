package Client.Part1;

import Client.Part2.Data;
import Client.Part2.Record;
import DAO.LiftRideDao;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class SkierClient {
  private static final int NUM_THREADS = 128;
  private static final int NUM_SKIERS = 20000;
  private static final int NUM_LIFTS = 40;
  private static final int NUM_RUNS = 20;
  private static final int DEFAULTTHREADMAXCAPACITY = 4000;
  private static final int DEFAULTPOSTMAXCAPACITY = 400000;
  private static final int DIVIDEND = 10;
  private static final int TIMEOUT = 30;
//  private static final String ip = "http://localhost";
  private static final String ip = "https://cs6650a3.appspot.com";
//  private static final String ip = "http://ec2-user@ec2-18-204-205-169.compute-1.amazonaws.com";
  private static final String port = "8080";
  private static BlockingQueue<Thread> phases = new ArrayBlockingQueue<Thread>(DEFAULTTHREADMAXCAPACITY);
  private static int numReq = 0;
  private static int numRes = 0;
  private static int numFailure = 0;
  private static BlockingQueue<Record> records = new ArrayBlockingQueue<Record>(DEFAULTPOSTMAXCAPACITY);
  private static Logger logger = Logger.getLogger(Thread.class.getName());
  private static final LiftRideDao liftRideDao = new LiftRideDao();

  public static void main(String[] arg) throws Exception {

    CountDownLatch initializeFirstCountDown = new CountDownLatch(0);
    CountDownLatch firstCountDown = new CountDownLatch(NUM_THREADS /4/DIVIDEND);
    CountDownLatch secondCountDown  = new CountDownLatch(NUM_THREADS /DIVIDEND);

    long wallStart = System.currentTimeMillis();
    phase1(initializeFirstCountDown, firstCountDown);
    phase2(firstCountDown, secondCountDown);
    phase3(secondCountDown);
    long wallEnd = System.currentTimeMillis();

    counter();

    Data data = new Data(wallEnd - wallStart, numReq,
        numRes, numFailure, records, logger);
    data.performanceStats();
  }

  private static void counter() {
    for (Thread thread : phases) {
      numFailure += thread.getFailure();
      numReq += thread.getReq();
      numRes += thread.getSuccess();
    }
  }
  private static void phase1(CountDownLatch firstCountDown, CountDownLatch secondCountDown) {
    int skierIdRange = NUM_SKIERS /(NUM_THREADS /4);
    int endTime = 90;
    try {
      ExecutorService executorService = Executors.newFixedThreadPool(NUM_THREADS /4);
      int runTimes = NUM_RUNS /DIVIDEND * skierIdRange;
      for (int i = 0; i < NUM_THREADS /4; i++) {
        Thread thread = new Thread(
            skierIdRange * i + 1,
                skierIdRange * (i + 1),
            0, endTime,
            NUM_LIFTS,
            runTimes, firstCountDown, secondCountDown, records, ip, port, logger, liftRideDao);
        executorService.execute(thread);
        phases.add(thread);
      }
      executorService.shutdown();
      executorService.awaitTermination(TIMEOUT, TimeUnit.MINUTES);
    } catch (InterruptedException e) {
      logger.info("Thread running was interrupted with message: " + e.getMessage());
    }
  }

  private static void phase2(CountDownLatch firstCountDown, CountDownLatch secondCountDown) {
    int startTime = 91;
    int endTime = 360;
    int skierIdRange = NUM_SKIERS / NUM_THREADS;
    int runTimes = (int) (NUM_RUNS * 0.8) * skierIdRange;
    try {
      ExecutorService executorService = Executors.newFixedThreadPool(NUM_THREADS);
      for (int i = 0; i < NUM_THREADS; i++) {
        Thread thread = new Thread(
            skierIdRange * i + 1,
                skierIdRange * (i + 1),
            startTime, endTime, NUM_LIFTS,
            runTimes, firstCountDown, secondCountDown, records, ip, port, logger, liftRideDao);
        executorService.execute(thread);
        phases.add(thread);
      }
      executorService.shutdown();
      executorService.awaitTermination(TIMEOUT, TimeUnit.MINUTES);
    } catch (InterruptedException e) {
      logger.info("Thread running was interrupted with message: " + e.getMessage());
    }
  }

  private static void phase3(CountDownLatch firstCountDown) {
    int startTime = 361;
    int endTime = 420;
    int skierIdRange = NUM_SKIERS /(NUM_THREADS /4);
    int runTimes = NUM_RUNS /DIVIDEND;
    try {
      ExecutorService executorService = Executors.newFixedThreadPool(NUM_THREADS /4);
      for (int i = 0; i < NUM_THREADS /4; i++) {
        Thread thread = new Thread(
            skierIdRange * i + 1,
                skierIdRange * (i + 1),
            startTime, endTime, NUM_LIFTS,
            runTimes, firstCountDown, null, records, ip, port, logger, liftRideDao);
        executorService.execute(thread);
        phases.add(thread);
      }
      executorService.shutdown();
      executorService.awaitTermination(TIMEOUT, TimeUnit.MINUTES);
    } catch (InterruptedException e) {
      logger.info("Thread running was interrupted with message: " + e.getMessage());
    }
  }
}
