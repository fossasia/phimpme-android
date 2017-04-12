package org.wordpress.android.fluxc.module;

import dagger.internal.Factory;
import javax.annotation.Generated;
import org.wordpress.android.fluxc.network.HTTPAuthManager;

@Generated("dagger.internal.codegen.ComponentProcessor")
public final class ReleaseNetworkModule_ProvideHTTPAuthManagerFactory implements Factory<HTTPAuthManager> {
  private final ReleaseNetworkModule module;

  public ReleaseNetworkModule_ProvideHTTPAuthManagerFactory(ReleaseNetworkModule module) {  
    assert module != null;
    this.module = module;
  }

  @Override
  public HTTPAuthManager get() {  
    HTTPAuthManager provided = module.provideHTTPAuthManager();
    if (provided == null) {
      throw new NullPointerException("Cannot return null from a non-@Nullable @Provides method");
    }
    return provided;
  }

  public static Factory<HTTPAuthManager> create(ReleaseNetworkModule module) {  
    return new ReleaseNetworkModule_ProvideHTTPAuthManagerFactory(module);
  }
}

