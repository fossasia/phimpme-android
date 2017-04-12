package org.wordpress.android.fluxc.module;

import dagger.internal.Factory;
import javax.annotation.Generated;
import javax.inject.Provider;
import org.wordpress.android.fluxc.Dispatcher;
import org.wordpress.android.fluxc.network.rest.wpcom.media.MediaRestClient;
import org.wordpress.android.fluxc.network.xmlrpc.media.MediaXMLRPCClient;
import org.wordpress.android.fluxc.store.MediaStore;

@Generated("dagger.internal.codegen.ComponentProcessor")
public final class ReleaseStoreModule_ProvideMediaStoreFactory implements Factory<MediaStore> {
  private final ReleaseStoreModule module;
  private final Provider<Dispatcher> dispatcherProvider;
  private final Provider<MediaRestClient> mediaRestClientProvider;
  private final Provider<MediaXMLRPCClient> mediaXMLRPCClientProvider;

  public ReleaseStoreModule_ProvideMediaStoreFactory(ReleaseStoreModule module, Provider<Dispatcher> dispatcherProvider, Provider<MediaRestClient> mediaRestClientProvider, Provider<MediaXMLRPCClient> mediaXMLRPCClientProvider) {  
    assert module != null;
    this.module = module;
    assert dispatcherProvider != null;
    this.dispatcherProvider = dispatcherProvider;
    assert mediaRestClientProvider != null;
    this.mediaRestClientProvider = mediaRestClientProvider;
    assert mediaXMLRPCClientProvider != null;
    this.mediaXMLRPCClientProvider = mediaXMLRPCClientProvider;
  }

  @Override
  public MediaStore get() {  
    MediaStore provided = module.provideMediaStore(dispatcherProvider.get(), mediaRestClientProvider.get(), mediaXMLRPCClientProvider.get());
    if (provided == null) {
      throw new NullPointerException("Cannot return null from a non-@Nullable @Provides method");
    }
    return provided;
  }

  public static Factory<MediaStore> create(ReleaseStoreModule module, Provider<Dispatcher> dispatcherProvider, Provider<MediaRestClient> mediaRestClientProvider, Provider<MediaXMLRPCClient> mediaXMLRPCClientProvider) {  
    return new ReleaseStoreModule_ProvideMediaStoreFactory(module, dispatcherProvider, mediaRestClientProvider, mediaXMLRPCClientProvider);
  }
}

