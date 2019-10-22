package org.fossasia.phimpme.share.pinterest;

import java.util.ArrayList;

/** Created by @codedsun on 23/Oct/2019 */
public class PinterestBoardsResp {
  private ArrayList<Data> data;

  public ArrayList<Data> getData() {
    return data;
  }

  public void setData(ArrayList<Data> data) {
    this.data = data;
  }

  public static class Data {
    private String name;
    private String url;
    private String id;

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public String getUrl() {
      return url;
    }

    public void setUrl(String url) {
      this.url = url;
    }

    public String getId() {
      return id;
    }

    public void setId(String id) {
      this.id = id;
    }
  }
}
