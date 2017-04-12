package org.wordpress.android.fluxc.store;

import dagger.MembersInjector;
import dagger.internal.Factory;
import javax.annotation.Generated;
import javax.inject.Provider;
import org.wordpress.android.fluxc.Dispatcher;
import org.wordpress.android.fluxc.network.rest.wpcom.comment.CommentRestClient;
import org.wordpress.android.fluxc.network.xmlrpc.comment.CommentXMLRPCClient;

@Generated("dagger.internal.codegen.ComponentProcessor")
public final class CommentStore_Factory implements Factory<CommentStore> {
  private final MembersInjector<CommentStore> membersInjector;
  private final Provider<Dispatcher> dispatcherProvider;
  private final Provider<CommentRestClient> commentRestClientProvider;
  private final Provider<CommentXMLRPCClient> commentXMLRPCClientProvider;

  public CommentStore_Factory(MembersInjector<CommentStore> membersInjector, Provider<Dispatcher> dispatcherProvider, Provider<CommentRestClient> commentRestClientProvider, Provider<CommentXMLRPCClient> commentXMLRPCClientProvider) {  
    assert membersInjector != null;
    this.membersInjector = membersInjector;
    assert dispatcherProvider != null;
    this.dispatcherProvider = dispatcherProvider;
    assert commentRestClientProvider != null;
    this.commentRestClientProvider = commentRestClientProvider;
    assert commentXMLRPCClientProvider != null;
    this.commentXMLRPCClientProvider = commentXMLRPCClientProvider;
  }

  @Override
  public CommentStore get() {  
    CommentStore instance = new CommentStore(dispatcherProvider.get(), commentRestClientProvider.get(), commentXMLRPCClientProvider.get());
    membersInjector.injectMembers(instance);
    return instance;
  }

  public static Factory<CommentStore> create(MembersInjector<CommentStore> membersInjector, Provider<Dispatcher> dispatcherProvider, Provider<CommentRestClient> commentRestClientProvider, Provider<CommentXMLRPCClient> commentXMLRPCClientProvider) {  
    return new CommentStore_Factory(membersInjector, dispatcherProvider, commentRestClientProvider, commentXMLRPCClientProvider);
  }
}

