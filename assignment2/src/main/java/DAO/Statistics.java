package DAO;

public class Statistics {
  private String url;
  private String requestType;
  private Integer mean;
  private Integer max;

  public Statistics(String url, String requestType, Integer mean, Integer max) {
    this.url = url;
    this.requestType = requestType;
    this.mean = mean;
    this.max = max;
  }

  public String getUrl() {
    return url;
  }

  public String getRequestType() {
    return requestType;
  }

  public Integer getMean() {
    return mean;
  }

  public Integer getMax() {
    return max;
  }

  @Override
  public String toString() {
    return "Statistics{" +
        "url='" + url + '\'' +
        ", requestType='" + requestType + '\'' +
        ", mean=" + mean +
        ", max=" + max +
        '}';
  }
}
