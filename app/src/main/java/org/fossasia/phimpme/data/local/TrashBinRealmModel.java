package org.fossasia.phimpme.data.local;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/** Created by saurav on 21/6/18. Last Modified by SatyaJiit */

/* Used ID as PrimaryKey instead of filePath
 * Since there might be cases that user deletes a file
 * which has same name then that would cause a Duplicate PrimaryKey Exception
 */

public class TrashBinRealmModel extends RealmObject {

  @PrimaryKey private int id;
  private String trashbinpath;
  private String oldpath;
  private String datetime;
  private String timeperiod;

  public TrashBinRealmModel() {}

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
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
}
