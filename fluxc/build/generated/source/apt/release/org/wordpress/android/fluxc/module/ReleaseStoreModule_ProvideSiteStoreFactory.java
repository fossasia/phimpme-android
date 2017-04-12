package org.wordpress.android.fluxc.module;

import dagger.internal.Factory;
import javax.annotation.Generated;
import javax.inject.Provider;
import org.wordpress.android.fluxc.Dispatcher;
import org.wordpress.android.fluxc.network.rest.wpcom.site.SiteRestClient;
import org.wordpress.android.fluxc.network.xmlrpc.site.SiteXMLRPCClient;
import org.wordpress.android.fluxc.store.SiteStore;

@Generated("dagger.internal.codegen.ComponentProcessor")
public final class ReleaseStoreModule_ProvideSiteStoreFactory implements Factory<SiteStore> {
  private final ReleaseStoreModule module;
  private final Provider<Dispatcher> dispatcherProvider;
  private final Provider<SiteRestClient> siteRestClientProvider;
  private final Provider<SiteXMLRPCClient> siteXMLRPCClientProvider;

  public ReleaseStoreModule_ProvideSiteStoreFactory(ReleaseStoreModule module, Provider<Dispatcher> dispatcherProvider, Provider<SiteRestClient> siteRestClientProvider, Provider<SiteXMLRPCClient> siteXMLRPCClientProvider) {  
    assert module != null;
    this.module = module;
    assert dispatcherProvider != null;
    this.dispatcherProvider = dispatcherProvider;
    assert siteRestClientProvider != null;
    this.siteRestClientProvider = siteRestClientProvider;
    assert siteXMLRPCClientProvider != null;
    this.siteXMLRPCClientProvider = siteXMLRPCClientProvider;
  }

  @Override
  public SiteStore get() {  
    SiteStore provided = module.provideSiteStore(dispatcherProvider.get(), siteRestClientProvider.get(), siteXMLRPCClientProvider.get());
    if (provided == null) {
      throw new NullPointerException("Cannot return null from a non-@Nullable @Provides method");
    }
    return provided;
  }

  public static Factory<SiteStore> create(ReleaseStoreModule module, Provider<Dispatcher> dispatcherProvider, Provider<SiteRestClient> siteRestClientProvider, Provider<SiteXMLRPCClient> siteXMLRPCClientProvider) {  
    return new ReleaseStoreModule_ProvideSiteStoreFactory(module, dispatcherProvider, siteRestClientProvider, siteXMLRPCClientProvider);
  }
}

