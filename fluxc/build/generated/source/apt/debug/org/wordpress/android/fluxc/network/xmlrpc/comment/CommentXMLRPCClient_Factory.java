package org.wordpress.android.fluxc.network.xmlrpc.comment;

import com.android.volley.RequestQueue;
import dagger.MembersInjector;
import dagger.internal.Factory;
import javax.annotation.Generated;
import javax.inject.Provider;
import org.wordpress.android.fluxc.Dispatcher;
import org.wordpress.android.fluxc.network.HTTPAuthManager;
import org.wordpress.android.fluxc.network.UserAgent;
import org.wordpress.android.fluxc.network.rest.wpcom.auth.AccessToken;

@Generated("dagger.internal.codegen.ComponentProcessor")
public final class CommentXMLRPCClient_Factory implements Factory<CommentXMLRPCClient> {
  private final MembersInjector<CommentXMLRPCClient> membersInjector;
  private final Provider<Dispatcher> dispatcherProvider;
  private final Provider<RequestQueue> requestQueueProvider;
  private final Provider<AccessToken> accessTokenProvider;
  private final Provider<UserAgent> userAgentProvider;
  private final Provider<HTTPAuthManager> httpAuthManagerProvider;

  public CommentXMLRPCClient_Factory(MembersInjector<CommentXMLRPCClient> membersInjector, Provider<Dispatcher> dispatcherProvider, Provider<RequestQueue> requestQueueProvider, Provider<AccessToken> accessTokenProvider, Provider<UserAgent> userAgentProvider, Provider<HTTPAuthManager> httpAuthManagerProvider) {  
    assert membersInjector != null;
    this.membersInjector = membersInjector;
    assert dispatcherProvider != null;
    this.dispatcherProvider = dispatcherProvider;
    assert requestQueueProvider != null;
    this.requestQueueProvider = requestQueueProvider;
    assert accessTokenProvider != null;
    this.accessTokenProvider = accessTokenProvider;
    assert userAgentProvider != null;
    this.userAgentProvider = userAgentProvider;
    assert httpAuthManagerProvider != null;
    this.httpAuthManagerProvider = httpAuthManagerProvider;
  }

  @Override
  public CommentXMLRPCClient get() {  
    CommentXMLRPCClient instance = new CommentXMLRPCClient(dispatcherProvider.get(), requestQueueProvider.get(), accessTokenProvider.get(), userAgentProvider.get(), httpAuthManagerProvider.get());
    membersInjector.injectMembers(instance);
    return instance;
  }

  public static Factory<CommentXMLRPCClient> create(MembersInjector<CommentXMLRPCClient> membersInjector, Provider<Dispatcher> dispatcherProvider, Provider<RequestQueue> requestQueueProvider, Provider<AccessToken> accessTokenProvider, Provider<UserAgent> userAgentProvider, Provider<HTTPAuthManager> httpAuthManagerProvider) {  
    return new CommentXMLRPCClient_Factory(membersInjector, dispatcherProvider, requestQueueProvider, accessTokenProvider, userAgentProvider, httpAuthManagerProvider);
  }
}

