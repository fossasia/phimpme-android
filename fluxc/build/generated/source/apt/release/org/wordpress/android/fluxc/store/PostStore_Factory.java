package org.wordpress.android.fluxc.store;

import dagger.MembersInjector;
import dagger.internal.Factory;
import javax.annotation.Generated;
import javax.inject.Provider;
import org.wordpress.android.fluxc.Dispatcher;
import org.wordpress.android.fluxc.network.rest.wpcom.post.PostRestClient;
import org.wordpress.android.fluxc.network.xmlrpc.post.PostXMLRPCClient;

@Generated("dagger.internal.codegen.ComponentProcessor")
public final class PostStore_Factory implements Factory<PostStore> {
  private final MembersInjector<PostStore> membersInjector;
  private final Provider<Dispatcher> dispatcherProvider;
  private final Provider<PostRestClient> postRestClientProvider;
  private final Provider<PostXMLRPCClient> postXMLRPCClientProvider;

  public PostStore_Factory(MembersInjector<PostStore> membersInjector, Provider<Dispatcher> dispatcherProvider, Provider<PostRestClient> postRestClientProvider, Provider<PostXMLRPCClient> postXMLRPCClientProvider) {  
    assert membersInjector != null;
    this.membersInjector = membersInjector;
    assert dispatcherProvider != null;
    this.dispatcherProvider = dispatcherProvider;
    assert postRestClientProvider != null;
    this.postRestClientProvider = postRestClientProvider;
    assert postXMLRPCClientProvider != null;
    this.postXMLRPCClientProvider = postXMLRPCClientProvider;
  }

  @Override
  public PostStore get() {  
    PostStore instance = new PostStore(dispatcherProvider.get(), postRestClientProvider.get(), postXMLRPCClientProvider.get());
    membersInjector.injectMembers(instance);
    return instance;
  }

  public static Factory<PostStore> create(MembersInjector<PostStore> membersInjector, Provider<Dispatcher> dispatcherProvider, Provider<PostRestClient> postRestClientProvider, Provider<PostXMLRPCClient> postXMLRPCClientProvider) {  
    return new PostStore_Factory(membersInjector, dispatcherProvider, postRestClientProvider, postXMLRPCClientProvider);
  }
}

