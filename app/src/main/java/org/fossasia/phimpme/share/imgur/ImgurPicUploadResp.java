package org.fossasia.phimpme.share.imgur;

public class ImgurPicUploadResp {
  private boolean success;
  private Data data;

  public boolean isSuccess() {
    return success;
  }

  public void setSuccess(boolean success) {
    this.success = success;
  }

  public Data getData() {
    return data;
  }

  public void setData(Data data) {
    this.data = data;
  }

  public static class Data {
    private String id;
    private String title;
    private String link;

    public String getId() {
      return id;
    }

    public void setId(String id) {
      this.id = id;
    }

    public String getTitle() {
      return title;
    }

    public void setTitle(String title) {
      this.title = title;
    }

    public String getLink() {
      return link;
    }

    public void setLink(String link) {
      this.link = link;
    }
  }
}
