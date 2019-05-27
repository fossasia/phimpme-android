package org.fossasia.phimpme.data.local;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/** Created by saurav on 10/10/17. */
public class FavouriteImagesModel extends RealmObject {

  @PrimaryKey private String path;
  private String description;

  public FavouriteImagesModel() {}

  public FavouriteImagesModel(String path, String description) {
    this.path = path;
    this.description = description;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public String getPath() {
    return path;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getDescription() {
    return description;
  }
}
