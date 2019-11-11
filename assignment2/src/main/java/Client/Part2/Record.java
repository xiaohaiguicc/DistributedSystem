package Client.Part2;

public class Record {
  private long startTime;
  private long endTime;
  private int responseCode;
  private String requestType;

  public Record(long startTime, long endTime, int responseCode) {
    this.startTime = startTime;
    this.requestType = "POST";
    this.endTime = endTime;
    this.responseCode = responseCode;
  }

  public int getResponseCode() {
    return responseCode;
  }

  public long getLatency() {
    return endTime - startTime;
  }

  public long getStartTime() {
    return startTime;
  }

  public long getEndTime() {
    return endTime;
  }

  public String getRequestType() {
    return requestType;
  }
}
