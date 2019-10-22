package org.fossasia.phimpme.share.pinterest;

import java.util.ArrayList;

/** Created by @codedsun on 05/Nov/2019 */
public class PinterestUploadImgResp {
  private ArrayList<Data> data;

  private String message;

  public ArrayList<Data> getData() {
    return data;
  }

  public void setData(ArrayList<Data> data) {
    this.data = data;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public static class Data {
    private String link;
    private String note;
    private String id;
    private String url;

    public String getLink() {
      return link;
    }

    public void setLink(String link) {
      this.link = link;
    }

    public String getNote() {
      return note;
    }

    public void setNote(String note) {
      this.note = note;
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
