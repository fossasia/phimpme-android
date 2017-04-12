package org.wordpress.android.fluxc.module;

import android.content.Context;
import com.android.volley.RequestQueue;
import dagger.internal.Factory;
import javax.annotation.Generated;
import javax.inject.Provider;
import okhttp3.OkHttpClient.Builder;

@Generated("dagger.internal.codegen.ComponentProcessor")
public final class ReleaseNetworkModule_ProvideRequestQueueCustomSSLFactory implements Factory<RequestQueue> {
  private final ReleaseNetworkModule module;
  private final Provider<Builder> okHttpClientBuilderProvider;
  private final Provider<Context> appContextProvider;

  public ReleaseNetworkModule_ProvideRequestQueueCustomSSLFactory(ReleaseNetworkModule module, Provider<Builder> okHttpClientBuilderProvider, Provider<Context> appContextProvider) {  
    assert module != null;
    this.module = module;
    assert okHttpClientBuilderProvider != null;
    this.okHttpClientBuilderProvider = okHttpClientBuilderProvider;
    assert appContextProvider != null;
    this.appContextProvider = appContextProvider;
  }

  @Override
  public RequestQueue get() {  
    RequestQueue provided = module.provideRequestQueueCustomSSL(okHttpClientBuilderProvider.get(), appContextProvider.get());
    if (provided == null) {
      throw new NullPointerException("Cannot return null from a non-@Nullable @Provides method");
    }
    return provided;
  }

  public static Factory<RequestQueue> create(ReleaseNetworkModule module, Provider<Builder> okHttpClientBuilderProvider, Provider<Context> appContextProvider) {  
    return new ReleaseNetworkModule_ProvideRequestQueueCustomSSLFactory(module, okHttpClientBuilderProvider, appContextProvider);
  }
}

