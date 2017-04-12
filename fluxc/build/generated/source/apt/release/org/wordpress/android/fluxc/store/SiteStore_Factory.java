package org.wordpress.android.fluxc.store;

import dagger.MembersInjector;
import dagger.internal.Factory;
import javax.annotation.Generated;
import javax.inject.Provider;
import org.wordpress.android.fluxc.Dispatcher;
import org.wordpress.android.fluxc.network.rest.wpcom.site.SiteRestClient;
import org.wordpress.android.fluxc.network.xmlrpc.site.SiteXMLRPCClient;

@Generated("dagger.internal.codegen.ComponentProcessor")
public final class SiteStore_Factory implements Factory<SiteStore> {
  private final MembersInjector<SiteStore> membersInjector;
  private final Provider<Dispatcher> dispatcherProvider;
  private final Provider<SiteRestClient> siteRestClientProvider;
  private final Provider<SiteXMLRPCClient> siteXMLRPCClientProvider;

  public SiteStore_Factory(MembersInjector<SiteStore> membersInjector, Provider<Dispatcher> dispatcherProvider, Provider<SiteRestClient> siteRestClientProvider, Provider<SiteXMLRPCClient> siteXMLRPCClientProvider) {  
    assert membersInjector != null;
    this.membersInjector = membersInjector;
    assert dispatcherProvider != null;
    this.dispatcherProvider = dispatcherProvider;
    assert siteRestClientProvider != null;
    this.siteRestClientProvider = siteRestClientProvider;
    assert siteXMLRPCClientProvider != null;
    this.siteXMLRPCClientProvider = siteXMLRPCClientProvider;
  }

  @Override
  public SiteStore get() {  
    SiteStore instance = new SiteStore(dispatcherProvider.get(), siteRestClientProvider.get(), siteXMLRPCClientProvider.get());
    membersInjector.injectMembers(instance);
    return instance;
  }

  public static Factory<SiteStore> create(MembersInjector<SiteStore> membersInjector, Provider<Dispatcher> dispatcherProvider, Provider<SiteRestClient> siteRestClientProvider, Provider<SiteXMLRPCClient> siteXMLRPCClientProvider) {  
    return new SiteStore_Factory(membersInjector, dispatcherProvider, siteRestClientProvider, siteXMLRPCClientProvider);
  }
}

