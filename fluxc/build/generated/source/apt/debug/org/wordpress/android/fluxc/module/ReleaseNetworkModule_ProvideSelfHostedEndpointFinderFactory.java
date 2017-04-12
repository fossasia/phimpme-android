package org.wordpress.android.fluxc.module;

import dagger.internal.Factory;
import javax.annotation.Generated;
import javax.inject.Provider;
import org.wordpress.android.fluxc.Dispatcher;
import org.wordpress.android.fluxc.network.discovery.SelfHostedEndpointFinder;
import org.wordpress.android.fluxc.network.rest.wpapi.BaseWPAPIRestClient;
import org.wordpress.android.fluxc.network.xmlrpc.BaseXMLRPCClient;

@Generated("dagger.internal.codegen.ComponentProcessor")
public final class ReleaseNetworkModule_ProvideSelfHostedEndpointFinderFactory implements Factory<SelfHostedEndpointFinder> {
  private final ReleaseNetworkModule module;
  private final Provider<Dispatcher> dispatcherProvider;
  private final Provider<BaseXMLRPCClient> baseXMLRPCClientProvider;
  private final Provider<BaseWPAPIRestClient> baseWPAPIRestClientProvider;

  public ReleaseNetworkModule_ProvideSelfHostedEndpointFinderFactory(ReleaseNetworkModule module, Provider<Dispatcher> dispatcherProvider, Provider<BaseXMLRPCClient> baseXMLRPCClientProvider, Provider<BaseWPAPIRestClient> baseWPAPIRestClientProvider) {  
    assert module != null;
    this.module = module;
    assert dispatcherProvider != null;
    this.dispatcherProvider = dispatcherProvider;
    assert baseXMLRPCClientProvider != null;
    this.baseXMLRPCClientProvider = baseXMLRPCClientProvider;
    assert baseWPAPIRestClientProvider != null;
    this.baseWPAPIRestClientProvider = baseWPAPIRestClientProvider;
  }

  @Override
  public SelfHostedEndpointFinder get() {  
    SelfHostedEndpointFinder provided = module.provideSelfHostedEndpointFinder(dispatcherProvider.get(), baseXMLRPCClientProvider.get(), baseWPAPIRestClientProvider.get());
    if (provided == null) {
      throw new NullPointerException("Cannot return null from a non-@Nullable @Provides method");
    }
    return provided;
  }

  public static Factory<SelfHostedEndpointFinder> create(ReleaseNetworkModule module, Provider<Dispatcher> dispatcherProvider, Provider<BaseXMLRPCClient> baseXMLRPCClientProvider, Provider<BaseWPAPIRestClient> baseWPAPIRestClientProvider) {  
    return new ReleaseNetworkModule_ProvideSelfHostedEndpointFinderFactory(module, dispatcherProvider, baseXMLRPCClientProvider, baseWPAPIRestClientProvider);
  }
}

