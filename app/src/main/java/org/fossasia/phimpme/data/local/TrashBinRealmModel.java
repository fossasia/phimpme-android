package org.fossasia.phimpme.data.local;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/** Created by saurav on 21/6/18. */
public class TrashBinRealmModel extends RealmObject {

  @PrimaryKey private String trashbinpath;
  private String oldpath;
  private String datetime;
  private String timeperiod;

  public TrashBinRealmModel() {}

  public TrashBinRealmModel(String oldpath, String newpath, String datetime, String timeperiod) {
    this.oldpath = oldpath;
    this.trashbinpath = newpath;
    this.datetime = datetime;
    this.timeperiod = timeperiod;
  }

  public void setTrashbinpath(String trashbinpath) {
    this.trashbinpath = trashbinpath;
  }

  public String getTrashbinpath() {
    return trashbinpath;
  }

  public void setDatetime(String datetime) {
    this.datetime = datetime;
  }

  public String getDatetime() {
    return datetime;
  }

  public void setOldpath(String oldpath) {
    this.oldpath = oldpath;
  }

  public String getOldpath() {
    return oldpath;
  }

  public void setTimeperiod(String timeperiod) {
    this.timeperiod = timeperiod;
  }

  public String getTimeperiod() {
    return timeperiod;
  }
}
