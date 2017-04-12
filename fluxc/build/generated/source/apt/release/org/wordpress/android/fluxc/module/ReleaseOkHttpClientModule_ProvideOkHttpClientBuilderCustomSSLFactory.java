package org.wordpress.android.fluxc.module;

import dagger.internal.Factory;
import javax.annotation.Generated;
import javax.inject.Provider;
import okhttp3.OkHttpClient.Builder;
import org.wordpress.android.fluxc.network.MemorizingTrustManager;

@Generated("dagger.internal.codegen.ComponentProcessor")
public final class ReleaseOkHttpClientModule_ProvideOkHttpClientBuilderCustomSSLFactory implements Factory<Builder> {
  private final ReleaseOkHttpClientModule module;
  private final Provider<MemorizingTrustManager> memorizingTrustManagerProvider;

  public ReleaseOkHttpClientModule_ProvideOkHttpClientBuilderCustomSSLFactory(ReleaseOkHttpClientModule module, Provider<MemorizingTrustManager> memorizingTrustManagerProvider) {  
    assert module != null;
    this.module = module;
    assert memorizingTrustManagerProvider != null;
    this.memorizingTrustManagerProvider = memorizingTrustManagerProvider;
  }

  @Override
  public Builder get() {  
    Builder provided = module.provideOkHttpClientBuilderCustomSSL(memorizingTrustManagerProvider.get());
    if (provided == null) {
      throw new NullPointerException("Cannot return null from a non-@Nullable @Provides method");
    }
    return provided;
  }

  public static Factory<Builder> create(ReleaseOkHttpClientModule module, Provider<MemorizingTrustManager> memorizingTrustManagerProvider) {  
    return new ReleaseOkHttpClientModule_ProvideOkHttpClientBuilderCustomSSLFactory(module, memorizingTrustManagerProvider);
  }
}

