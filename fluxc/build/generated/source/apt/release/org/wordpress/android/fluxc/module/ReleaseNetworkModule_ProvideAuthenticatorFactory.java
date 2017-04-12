package org.wordpress.android.fluxc.module;

import com.android.volley.RequestQueue;
import dagger.internal.Factory;
import javax.annotation.Generated;
import javax.inject.Provider;
import org.wordpress.android.fluxc.Dispatcher;
import org.wordpress.android.fluxc.network.rest.wpcom.auth.AppSecrets;
import org.wordpress.android.fluxc.network.rest.wpcom.auth.Authenticator;

@Generated("dagger.internal.codegen.ComponentProcessor")
public final class ReleaseNetworkModule_ProvideAuthenticatorFactory implements Factory<Authenticator> {
  private final ReleaseNetworkModule module;
  private final Provider<Dispatcher> dispatcherProvider;
  private final Provider<AppSecrets> appSecretsProvider;
  private final Provider<RequestQueue> requestQueueProvider;

  public ReleaseNetworkModule_ProvideAuthenticatorFactory(ReleaseNetworkModule module, Provider<Dispatcher> dispatcherProvider, Provider<AppSecrets> appSecretsProvider, Provider<RequestQueue> requestQueueProvider) {  
    assert module != null;
    this.module = module;
    assert dispatcherProvider != null;
    this.dispatcherProvider = dispatcherProvider;
    assert appSecretsProvider != null;
    this.appSecretsProvider = appSecretsProvider;
    assert requestQueueProvider != null;
    this.requestQueueProvider = requestQueueProvider;
  }

  @Override
  public Authenticator get() {  
    Authenticator provided = module.provideAuthenticator(dispatcherProvider.get(), appSecretsProvider.get(), requestQueueProvider.get());
    if (provided == null) {
      throw new NullPointerException("Cannot return null from a non-@Nullable @Provides method");
    }
    return provided;
  }

  public static Factory<Authenticator> create(ReleaseNetworkModule module, Provider<Dispatcher> dispatcherProvider, Provider<AppSecrets> appSecretsProvider, Provider<RequestQueue> requestQueueProvider) {  
    return new ReleaseNetworkModule_ProvideAuthenticatorFactory(module, dispatcherProvider, appSecretsProvider, requestQueueProvider);
  }
}

