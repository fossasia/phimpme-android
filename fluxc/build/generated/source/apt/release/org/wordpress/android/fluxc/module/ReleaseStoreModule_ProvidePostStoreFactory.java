package org.wordpress.android.fluxc.module;

import dagger.internal.Factory;
import javax.annotation.Generated;
import javax.inject.Provider;
import org.wordpress.android.fluxc.Dispatcher;
import org.wordpress.android.fluxc.network.rest.wpcom.post.PostRestClient;
import org.wordpress.android.fluxc.network.xmlrpc.post.PostXMLRPCClient;
import org.wordpress.android.fluxc.store.PostStore;

@Generated("dagger.internal.codegen.ComponentProcessor")
public final class ReleaseStoreModule_ProvidePostStoreFactory implements Factory<PostStore> {
  private final ReleaseStoreModule module;
  private final Provider<Dispatcher> dispatcherProvider;
  private final Provider<PostRestClient> postRestClientProvider;
  private final Provider<PostXMLRPCClient> postXMLRPCClientProvider;

  public ReleaseStoreModule_ProvidePostStoreFactory(ReleaseStoreModule module, Provider<Dispatcher> dispatcherProvider, Provider<PostRestClient> postRestClientProvider, Provider<PostXMLRPCClient> postXMLRPCClientProvider) {  
    assert module != null;
    this.module = module;
    assert dispatcherProvider != null;
    this.dispatcherProvider = dispatcherProvider;
    assert postRestClientProvider != null;
    this.postRestClientProvider = postRestClientProvider;
    assert postXMLRPCClientProvider != null;
    this.postXMLRPCClientProvider = postXMLRPCClientProvider;
  }

  @Override
  public PostStore get() {  
    PostStore provided = module.providePostStore(dispatcherProvider.get(), postRestClientProvider.get(), postXMLRPCClientProvider.get());
    if (provided == null) {
      throw new NullPointerException("Cannot return null from a non-@Nullable @Provides method");
    }
    return provided;
  }

  public static Factory<PostStore> create(ReleaseStoreModule module, Provider<Dispatcher> dispatcherProvider, Provider<PostRestClient> postRestClientProvider, Provider<PostXMLRPCClient> postXMLRPCClientProvider) {  
    return new ReleaseStoreModule_ProvidePostStoreFactory(module, dispatcherProvider, postRestClientProvider, postXMLRPCClientProvider);
  }
}

