package org.fossasia.phimpme.utilities;

import okhttp3.MultipartBody;
import org.fossasia.phimpme.share.pinterest.PinterestBoardsResp;
import org.fossasia.phimpme.share.pinterest.PinterestUploadImgResp;
import org.fossasia.phimpme.share.pinterest.PinterestUserResp;
import org.fossasia.phimpme.share.pinterest.PinterestUserTokenResp;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

/** Created by @codedsun on 23/Oct/2019 */
public interface PinterestApi {

  @GET("me")
  Call<PinterestUserResp> getUserDetails(@Query("access_token") String accessToken);

  @POST("oauth/token")
  @FormUrlEncoded
  Call<PinterestUserTokenResp> getUserToken(
      @Field("grant_type") String grantType,
      @Field("client_id") String clientId,
      @Field("client_secret") String clientSecret,
      @Field("code") String code);

  @GET("me/boards")
  Call<PinterestBoardsResp> getUserBoards(@Query("access_token") String accessToken);

  @Multipart
  @POST("pins/")
  Call<PinterestUploadImgResp> uploadImageToPinterest(
      @Query("access_token") String accessToken,
      @Query("note") String note,
      @Query("board") String boards,
      @Part MultipartBody.Part image);
}
