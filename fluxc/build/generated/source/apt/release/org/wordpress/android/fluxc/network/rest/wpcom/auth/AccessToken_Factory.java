package org.wordpress.android.fluxc.network.rest.wpcom.auth;

import android.content.Context;
import dagger.internal.Factory;
import javax.annotation.Generated;
import javax.inject.Provider;

@Generated("dagger.internal.codegen.ComponentProcessor")
public final class AccessToken_Factory implements Factory<AccessToken> {
  private final Provider<Context> appContextProvider;

  public AccessToken_Factory(Provider<Context> appContextProvider) {  
    assert appContextProvider != null;
    this.appContextProvider = appContextProvider;
  }

  @Override
  public AccessToken get() {  
    return new AccessToken(appContextProvider.get());
  }

  public static Factory<AccessToken> create(Provider<Context> appContextProvider) {  
    return new AccessToken_Factory(appContextProvider);
  }
}

