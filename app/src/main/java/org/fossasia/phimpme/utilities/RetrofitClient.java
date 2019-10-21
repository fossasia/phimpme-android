package org.fossasia.phimpme.utilities;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import org.fossasia.phimpme.BuildConfig;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

// Retrofit client used for API Calls
public class RetrofitClient {

  private static OkHttpClient.Builder httpClientBuilder = null;

  private static OkHttpClient.Builder httpClient() {
    if (httpClientBuilder == null) {
      httpClientBuilder = new OkHttpClient.Builder();
      HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
      if (BuildConfig.DEBUG) {
        interceptor.level(HttpLoggingInterceptor.Level.BASIC);
      } else {
        interceptor.level(HttpLoggingInterceptor.Level.NONE);
      }
      httpClientBuilder.interceptors().add(interceptor);
    }
    return httpClientBuilder;
  }

  public static Retrofit getRetrofitClient(String baseUrl) {
    return new Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(httpClient().build())
        .addConverterFactory(GsonConverterFactory.create())
        .build();
  }
}
