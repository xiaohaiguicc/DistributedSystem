package DAO;

public class LiftRide {
  private Integer skierId;
  private Integer resortId;
  private Integer seasonId;
  private Integer dayId;
  private Integer time;
  private Integer liftId;
  private Integer vertical;

  public LiftRide(Integer resortId, Integer seasonId, Integer dayId,  Integer skierId, Integer time, Integer liftId) {
    this.resortId = resortId;
    this.seasonId = seasonId;
    this.dayId = dayId;
    this.skierId = skierId;
    this.time = time;
    this.liftId = liftId;
    this.vertical = 10 * liftId;
  }

  public Integer getSkierId() {
    return skierId;
  }

  public Integer getResortId() {
    return resortId;
  }

  public Integer getSeasonId() {
    return seasonId;
  }

  public Integer getDayId() {
    return dayId;
  }

  public Integer getTime() {
    return time;
  }

  public Integer getLiftId() {
    return liftId;
  }

  public Integer getVertical() {
    return vertical;
  }

  @Override
  public String toString() {
    return "LiftRide{" +
        "skierId=" + skierId +
        ", resortId=" + resortId +
        ", seasonId=" + seasonId +
        ", dayId=" + dayId +
        ", time=" + time +
        ", liftId=" + liftId +
        ", vertical=" + vertical +
        '}';
  }
}
