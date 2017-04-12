package org.wordpress.android.fluxc.network.discovery;

import dagger.internal.Factory;
import javax.annotation.Generated;
import javax.inject.Provider;
import org.wordpress.android.fluxc.Dispatcher;
import org.wordpress.android.fluxc.network.rest.wpapi.BaseWPAPIRestClient;
import org.wordpress.android.fluxc.network.xmlrpc.BaseXMLRPCClient;

@Generated("dagger.internal.codegen.ComponentProcessor")
public final class SelfHostedEndpointFinder_Factory implements Factory<SelfHostedEndpointFinder> {
  private final Provider<Dispatcher> dispatcherProvider;
  private final Provider<BaseXMLRPCClient> baseXMLRPCClientProvider;
  private final Provider<BaseWPAPIRestClient> baseWPAPIRestClientProvider;

  public SelfHostedEndpointFinder_Factory(Provider<Dispatcher> dispatcherProvider, Provider<BaseXMLRPCClient> baseXMLRPCClientProvider, Provider<BaseWPAPIRestClient> baseWPAPIRestClientProvider) {  
    assert dispatcherProvider != null;
    this.dispatcherProvider = dispatcherProvider;
    assert baseXMLRPCClientProvider != null;
    this.baseXMLRPCClientProvider = baseXMLRPCClientProvider;
    assert baseWPAPIRestClientProvider != null;
    this.baseWPAPIRestClientProvider = baseWPAPIRestClientProvider;
  }

  @Override
  public SelfHostedEndpointFinder get() {  
    return new SelfHostedEndpointFinder(dispatcherProvider.get(), baseXMLRPCClientProvider.get(), baseWPAPIRestClientProvider.get());
  }

  public static Factory<SelfHostedEndpointFinder> create(Provider<Dispatcher> dispatcherProvider, Provider<BaseXMLRPCClient> baseXMLRPCClientProvider, Provider<BaseWPAPIRestClient> baseWPAPIRestClientProvider) {  
    return new SelfHostedEndpointFinder_Factory(dispatcherProvider, baseXMLRPCClientProvider, baseWPAPIRestClientProvider);
  }
}

