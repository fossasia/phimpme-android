package org.wordpress.android.fluxc.module;

import dagger.internal.Factory;
import javax.annotation.Generated;
import javax.inject.Provider;
import org.wordpress.android.fluxc.Dispatcher;
import org.wordpress.android.fluxc.network.rest.wpcom.taxonomy.TaxonomyRestClient;
import org.wordpress.android.fluxc.network.xmlrpc.taxonomy.TaxonomyXMLRPCClient;
import org.wordpress.android.fluxc.store.TaxonomyStore;

@Generated("dagger.internal.codegen.ComponentProcessor")
public final class ReleaseStoreModule_ProvideTaxonomyStoreFactory implements Factory<TaxonomyStore> {
  private final ReleaseStoreModule module;
  private final Provider<Dispatcher> dispatcherProvider;
  private final Provider<TaxonomyRestClient> taxonomyRestClientProvider;
  private final Provider<TaxonomyXMLRPCClient> taxonomyXMLRPCClientProvider;

  public ReleaseStoreModule_ProvideTaxonomyStoreFactory(ReleaseStoreModule module, Provider<Dispatcher> dispatcherProvider, Provider<TaxonomyRestClient> taxonomyRestClientProvider, Provider<TaxonomyXMLRPCClient> taxonomyXMLRPCClientProvider) {  
    assert module != null;
    this.module = module;
    assert dispatcherProvider != null;
    this.dispatcherProvider = dispatcherProvider;
    assert taxonomyRestClientProvider != null;
    this.taxonomyRestClientProvider = taxonomyRestClientProvider;
    assert taxonomyXMLRPCClientProvider != null;
    this.taxonomyXMLRPCClientProvider = taxonomyXMLRPCClientProvider;
  }

  @Override
  public TaxonomyStore get() {  
    TaxonomyStore provided = module.provideTaxonomyStore(dispatcherProvider.get(), taxonomyRestClientProvider.get(), taxonomyXMLRPCClientProvider.get());
    if (provided == null) {
      throw new NullPointerException("Cannot return null from a non-@Nullable @Provides method");
    }
    return provided;
  }

  public static Factory<TaxonomyStore> create(ReleaseStoreModule module, Provider<Dispatcher> dispatcherProvider, Provider<TaxonomyRestClient> taxonomyRestClientProvider, Provider<TaxonomyXMLRPCClient> taxonomyXMLRPCClientProvider) {  
    return new ReleaseStoreModule_ProvideTaxonomyStoreFactory(module, dispatcherProvider, taxonomyRestClientProvider, taxonomyXMLRPCClientProvider);
  }
}

