package org.wordpress.android.fluxc.store;

import dagger.MembersInjector;
import dagger.internal.Factory;
import javax.annotation.Generated;
import javax.inject.Provider;
import org.wordpress.android.fluxc.Dispatcher;
import org.wordpress.android.fluxc.network.rest.wpcom.taxonomy.TaxonomyRestClient;
import org.wordpress.android.fluxc.network.xmlrpc.taxonomy.TaxonomyXMLRPCClient;

@Generated("dagger.internal.codegen.ComponentProcessor")
public final class TaxonomyStore_Factory implements Factory<TaxonomyStore> {
  private final MembersInjector<TaxonomyStore> membersInjector;
  private final Provider<Dispatcher> dispatcherProvider;
  private final Provider<TaxonomyRestClient> taxonomyRestClientProvider;
  private final Provider<TaxonomyXMLRPCClient> taxonomyXMLRPCClientProvider;

  public TaxonomyStore_Factory(MembersInjector<TaxonomyStore> membersInjector, Provider<Dispatcher> dispatcherProvider, Provider<TaxonomyRestClient> taxonomyRestClientProvider, Provider<TaxonomyXMLRPCClient> taxonomyXMLRPCClientProvider) {  
    assert membersInjector != null;
    this.membersInjector = membersInjector;
    assert dispatcherProvider != null;
    this.dispatcherProvider = dispatcherProvider;
    assert taxonomyRestClientProvider != null;
    this.taxonomyRestClientProvider = taxonomyRestClientProvider;
    assert taxonomyXMLRPCClientProvider != null;
    this.taxonomyXMLRPCClientProvider = taxonomyXMLRPCClientProvider;
  }

  @Override
  public TaxonomyStore get() {  
    TaxonomyStore instance = new TaxonomyStore(dispatcherProvider.get(), taxonomyRestClientProvider.get(), taxonomyXMLRPCClientProvider.get());
    membersInjector.injectMembers(instance);
    return instance;
  }

  public static Factory<TaxonomyStore> create(MembersInjector<TaxonomyStore> membersInjector, Provider<Dispatcher> dispatcherProvider, Provider<TaxonomyRestClient> taxonomyRestClientProvider, Provider<TaxonomyXMLRPCClient> taxonomyXMLRPCClientProvider) {  
    return new TaxonomyStore_Factory(membersInjector, dispatcherProvider, taxonomyRestClientProvider, taxonomyXMLRPCClientProvider);
  }
}

