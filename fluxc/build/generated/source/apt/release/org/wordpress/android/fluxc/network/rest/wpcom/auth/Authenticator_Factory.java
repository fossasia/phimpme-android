package org.wordpress.android.fluxc.network.rest.wpcom.auth;

import com.android.volley.RequestQueue;
import dagger.internal.Factory;
import javax.annotation.Generated;
import javax.inject.Provider;
import org.wordpress.android.fluxc.Dispatcher;

@Generated("dagger.internal.codegen.ComponentProcessor")
public final class Authenticator_Factory implements Factory<Authenticator> {
  private final Provider<Dispatcher> dispatcherProvider;
  private final Provider<RequestQueue> requestQueueProvider;
  private final Provider<AppSecrets> secretsProvider;

  public Authenticator_Factory(Provider<Dispatcher> dispatcherProvider, Provider<RequestQueue> requestQueueProvider, Provider<AppSecrets> secretsProvider) {  
    assert dispatcherProvider != null;
    this.dispatcherProvider = dispatcherProvider;
    assert requestQueueProvider != null;
    this.requestQueueProvider = requestQueueProvider;
    assert secretsProvider != null;
    this.secretsProvider = secretsProvider;
  }

  @Override
  public Authenticator get() {  
    return new Authenticator(dispatcherProvider.get(), requestQueueProvider.get(), secretsProvider.get());
  }

  public static Factory<Authenticator> create(Provider<Dispatcher> dispatcherProvider, Provider<RequestQueue> requestQueueProvider, Provider<AppSecrets> secretsProvider) {  
    return new Authenticator_Factory(dispatcherProvider, requestQueueProvider, secretsProvider);
  }
}

