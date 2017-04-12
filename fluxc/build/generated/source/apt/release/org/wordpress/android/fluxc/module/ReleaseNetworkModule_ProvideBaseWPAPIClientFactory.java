package org.wordpress.android.fluxc.module;

import com.android.volley.RequestQueue;
import dagger.internal.Factory;
import javax.annotation.Generated;
import javax.inject.Provider;
import org.wordpress.android.fluxc.Dispatcher;
import org.wordpress.android.fluxc.network.UserAgent;
import org.wordpress.android.fluxc.network.rest.wpapi.BaseWPAPIRestClient;

@Generated("dagger.internal.codegen.ComponentProcessor")
public final class ReleaseNetworkModule_ProvideBaseWPAPIClientFactory implements Factory<BaseWPAPIRestClient> {
  private final ReleaseNetworkModule module;
  private final Provider<Dispatcher> dispatcherProvider;
  private final Provider<RequestQueue> requestQueueProvider;
  private final Provider<UserAgent> userAgentProvider;

  public ReleaseNetworkModule_ProvideBaseWPAPIClientFactory(ReleaseNetworkModule module, Provider<Dispatcher> dispatcherProvider, Provider<RequestQueue> requestQueueProvider, Provider<UserAgent> userAgentProvider) {  
    assert module != null;
    this.module = module;
    assert dispatcherProvider != null;
    this.dispatcherProvider = dispatcherProvider;
    assert requestQueueProvider != null;
    this.requestQueueProvider = requestQueueProvider;
    assert userAgentProvider != null;
    this.userAgentProvider = userAgentProvider;
  }

  @Override
  public BaseWPAPIRestClient get() {  
    BaseWPAPIRestClient provided = module.provideBaseWPAPIClient(dispatcherProvider.get(), requestQueueProvider.get(), userAgentProvider.get());
    if (provided == null) {
      throw new NullPointerException("Cannot return null from a non-@Nullable @Provides method");
    }
    return provided;
  }

  public static Factory<BaseWPAPIRestClient> create(ReleaseNetworkModule module, Provider<Dispatcher> dispatcherProvider, Provider<RequestQueue> requestQueueProvider, Provider<UserAgent> userAgentProvider) {  
    return new ReleaseNetworkModule_ProvideBaseWPAPIClientFactory(module, dispatcherProvider, requestQueueProvider, userAgentProvider);
  }
}

