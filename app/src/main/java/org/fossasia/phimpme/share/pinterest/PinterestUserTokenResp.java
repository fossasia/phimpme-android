package org.fossasia.phimpme.share.pinterest;

import com.google.gson.annotations.SerializedName;

/** Created by @codedsun on 23/Oct/2019 */
public class PinterestUserTokenResp {
  String status;

  @SerializedName("access_token")
  String accessToken;

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getAccessToken() {
    return accessToken;
  }

  public void setAccessToken(String accessToken) {
    this.accessToken = accessToken;
  }
}
