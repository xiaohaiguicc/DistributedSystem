package Client.Part2;

public class Record {
  private long startTime;
  private long endTime;
  private int responseCode;
  private String requestType;
  private String url;

  public Record(long startTime, long endTime, String requestType, int responseCode, String url) {
    this.startTime = startTime;
    this.requestType = requestType;
    this.endTime = endTime;
    this.responseCode = responseCode;
    this.url = url;
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

  public String getUrl() {
    return url;
  }
}
