package org.wordpress.android.fluxc.module;

import android.content.Context;
import dagger.internal.Factory;
import javax.annotation.Generated;
import javax.inject.Provider;
import org.wordpress.android.fluxc.network.rest.wpcom.auth.AccessToken;

@Generated("dagger.internal.codegen.ComponentProcessor")
public final class ReleaseNetworkModule_ProvideAccountTokenFactory implements Factory<AccessToken> {
  private final ReleaseNetworkModule module;
  private final Provider<Context> appContextProvider;

  public ReleaseNetworkModule_ProvideAccountTokenFactory(ReleaseNetworkModule module, Provider<Context> appContextProvider) {  
    assert module != null;
    this.module = module;
    assert appContextProvider != null;
    this.appContextProvider = appContextProvider;
  }

  @Override
  public AccessToken get() {  
    AccessToken provided = module.provideAccountToken(appContextProvider.get());
    if (provided == null) {
      throw new NullPointerException("Cannot return null from a non-@Nullable @Provides method");
    }
    return provided;
  }

  public static Factory<AccessToken> create(ReleaseNetworkModule module, Provider<Context> appContextProvider) {  
    return new ReleaseNetworkModule_ProvideAccountTokenFactory(module, appContextProvider);
  }
}

