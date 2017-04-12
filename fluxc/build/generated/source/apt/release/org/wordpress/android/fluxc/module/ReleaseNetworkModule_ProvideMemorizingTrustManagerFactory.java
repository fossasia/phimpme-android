package org.wordpress.android.fluxc.module;

import android.content.Context;
import dagger.internal.Factory;
import javax.annotation.Generated;
import javax.inject.Provider;
import org.wordpress.android.fluxc.network.MemorizingTrustManager;

@Generated("dagger.internal.codegen.ComponentProcessor")
public final class ReleaseNetworkModule_ProvideMemorizingTrustManagerFactory implements Factory<MemorizingTrustManager> {
  private final ReleaseNetworkModule module;
  private final Provider<Context> appContextProvider;

  public ReleaseNetworkModule_ProvideMemorizingTrustManagerFactory(ReleaseNetworkModule module, Provider<Context> appContextProvider) {  
    assert module != null;
    this.module = module;
    assert appContextProvider != null;
    this.appContextProvider = appContextProvider;
  }

  @Override
  public MemorizingTrustManager get() {  
    MemorizingTrustManager provided = module.provideMemorizingTrustManager(appContextProvider.get());
    if (provided == null) {
      throw new NullPointerException("Cannot return null from a non-@Nullable @Provides method");
    }
    return provided;
  }

  public static Factory<MemorizingTrustManager> create(ReleaseNetworkModule module, Provider<Context> appContextProvider) {  
    return new ReleaseNetworkModule_ProvideMemorizingTrustManagerFactory(module, appContextProvider);
  }
}

