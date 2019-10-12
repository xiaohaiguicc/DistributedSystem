package Client.Part1;

import Client.Part2.Record;
import Client.Part2.Data;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class Client {
  private static final int NUMTHREADS = 32;
  private static final int NUMSKIERS = 20000;
  private static final int NUMLIFTS = 40;
  private static final int NUMRUNS = 20;
  private static final int DEFAULTTHREADMAXCAPACITY = 4000;
  private static final int DEFAULTPOSTMAXCAPACITY = 400000;
  private static final int DIVIDEND = 10;
  private static final int TIMEOUT = 30;
  private static final String ip = "http://172.31.83.150";
  private static final String port = "8080";
  private static BlockingQueue<Thread> phases = new ArrayBlockingQueue<>(DEFAULTTHREADMAXCAPACITY);
  private static int numReq = 0;
  private static int numRes = 0;
  private static int numFailure = 0;
  private static BlockingQueue<Record> records = new ArrayBlockingQueue<>(DEFAULTPOSTMAXCAPACITY);
  private static Logger logger = Logger.getLogger(Thread.class.getName());


  public static void main(String[] arg) throws Exception {

    CountDownLatch initializeFirstCountDown = new CountDownLatch(0);
    CountDownLatch firstCountDown = new CountDownLatch(NUMTHREADS/4/DIVIDEND);
    CountDownLatch secondCountDown  = new CountDownLatch(NUMTHREADS/DIVIDEND);

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
    int skierIdRange = NUMSKIERS/(NUMTHREADS/4);
    int endTime = 90;
    try {
      ExecutorService executorService = Executors.newFixedThreadPool(NUMTHREADS/4);
      int runTimes = NUMRUNS/DIVIDEND * skierIdRange;
      for (int i = 0; i < NUMTHREADS/4; i++) {
        Thread thread = new Thread(
            ThreadLocalRandom.current().nextInt(skierIdRange * i,
                skierIdRange * (i + 1)),
            ThreadLocalRandom.current().nextInt(endTime),
            ThreadLocalRandom.current().nextInt(NUMLIFTS),
            runTimes, firstCountDown, secondCountDown, records, ip, port, logger);
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
    int timeRange = ThreadLocalRandom.current().nextInt(endTime - startTime) + startTime;
    int skierIdRange = NUMSKIERS / NUMTHREADS;
    int runTimes = (int) (NUMRUNS * 0.8) * skierIdRange;
    try {
      ExecutorService executorService = Executors.newFixedThreadPool(NUMTHREADS);
      for (int i = 0; i < NUMTHREADS; i++) {
        Thread thread = new Thread(
            ThreadLocalRandom.current().nextInt(skierIdRange * i,
                skierIdRange * i + skierIdRange),
            timeRange, ThreadLocalRandom.current().nextInt(NUMLIFTS) + 1,
            runTimes, firstCountDown, secondCountDown, records, ip, port, logger);
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
    int skierIdRange = NUMSKIERS/(NUMTHREADS/4);
    int timeRange = ThreadLocalRandom.current().nextInt(endTime-startTime)+startTime;
    int runTimes = NUMRUNS/DIVIDEND;
    try {
      ExecutorService executorService = Executors.newFixedThreadPool(NUMTHREADS/4);
      for (int i = 0; i < NUMTHREADS/4; i++) {
        Thread thread = new Thread(
            ThreadLocalRandom.current().nextInt(skierIdRange*i,
                skierIdRange*i + skierIdRange),
            timeRange, ThreadLocalRandom.current().nextInt(NUMLIFTS),
            runTimes, firstCountDown, null, records, ip, port, logger);
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
