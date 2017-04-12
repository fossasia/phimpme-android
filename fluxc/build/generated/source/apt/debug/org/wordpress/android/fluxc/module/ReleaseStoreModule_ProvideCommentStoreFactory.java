package org.wordpress.android.fluxc.module;

import dagger.internal.Factory;
import javax.annotation.Generated;
import javax.inject.Provider;
import org.wordpress.android.fluxc.Dispatcher;
import org.wordpress.android.fluxc.network.rest.wpcom.comment.CommentRestClient;
import org.wordpress.android.fluxc.network.xmlrpc.comment.CommentXMLRPCClient;
import org.wordpress.android.fluxc.store.CommentStore;

@Generated("dagger.internal.codegen.ComponentProcessor")
public final class ReleaseStoreModule_ProvideCommentStoreFactory implements Factory<CommentStore> {
  private final ReleaseStoreModule module;
  private final Provider<Dispatcher> dispatcherProvider;
  private final Provider<CommentRestClient> restClientProvider;
  private final Provider<CommentXMLRPCClient> xmlrpcClientProvider;

  public ReleaseStoreModule_ProvideCommentStoreFactory(ReleaseStoreModule module, Provider<Dispatcher> dispatcherProvider, Provider<CommentRestClient> restClientProvider, Provider<CommentXMLRPCClient> xmlrpcClientProvider) {  
    assert module != null;
    this.module = module;
    assert dispatcherProvider != null;
    this.dispatcherProvider = dispatcherProvider;
    assert restClientProvider != null;
    this.restClientProvider = restClientProvider;
    assert xmlrpcClientProvider != null;
    this.xmlrpcClientProvider = xmlrpcClientProvider;
  }

  @Override
  public CommentStore get() {  
    CommentStore provided = module.provideCommentStore(dispatcherProvider.get(), restClientProvider.get(), xmlrpcClientProvider.get());
    if (provided == null) {
      throw new NullPointerException("Cannot return null from a non-@Nullable @Provides method");
    }
    return provided;
  }

  public static Factory<CommentStore> create(ReleaseStoreModule module, Provider<Dispatcher> dispatcherProvider, Provider<CommentRestClient> restClientProvider, Provider<CommentXMLRPCClient> xmlrpcClientProvider) {  
    return new ReleaseStoreModule_ProvideCommentStoreFactory(module, dispatcherProvider, restClientProvider, xmlrpcClientProvider);
  }
}

