package org.wordpress.android.fluxc.module;

import android.content.Context;
import dagger.internal.Factory;
import javax.annotation.Generated;
import javax.inject.Provider;
import org.wordpress.android.fluxc.network.UserAgent;

@Generated("dagger.internal.codegen.ComponentProcessor")
public final class ReleaseNetworkModule_ProvideUserAgentFactory implements Factory<UserAgent> {
  private final ReleaseNetworkModule module;
  private final Provider<Context> appContextProvider;

  public ReleaseNetworkModule_ProvideUserAgentFactory(ReleaseNetworkModule module, Provider<Context> appContextProvider) {  
    assert module != null;
    this.module = module;
    assert appContextProvider != null;
    this.appContextProvider = appContextProvider;
  }

  @Override
  public UserAgent get() {  
    UserAgent provided = module.provideUserAgent(appContextProvider.get());
    if (provided == null) {
      throw new NullPointerException("Cannot return null from a non-@Nullable @Provides method");
    }
    return provided;
  }

  public static Factory<UserAgent> create(ReleaseNetworkModule module, Provider<Context> appContextProvider) {  
    return new ReleaseNetworkModule_ProvideUserAgentFactory(module, appContextProvider);
  }
}

