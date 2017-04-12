package org.wordpress.android.fluxc.network.rest.wpcom.post;

import android.content.Context;
import com.android.volley.RequestQueue;
import dagger.MembersInjector;
import dagger.internal.Factory;
import javax.annotation.Generated;
import javax.inject.Provider;
import org.wordpress.android.fluxc.Dispatcher;
import org.wordpress.android.fluxc.network.UserAgent;
import org.wordpress.android.fluxc.network.rest.wpcom.auth.AccessToken;

@Generated("dagger.internal.codegen.ComponentProcessor")
public final class PostRestClient_Factory implements Factory<PostRestClient> {
  private final MembersInjector<PostRestClient> membersInjector;
  private final Provider<Context> appContextProvider;
  private final Provider<Dispatcher> dispatcherProvider;
  private final Provider<RequestQueue> requestQueueProvider;
  private final Provider<AccessToken> accessTokenProvider;
  private final Provider<UserAgent> userAgentProvider;

  public PostRestClient_Factory(MembersInjector<PostRestClient> membersInjector, Provider<Context> appContextProvider, Provider<Dispatcher> dispatcherProvider, Provider<RequestQueue> requestQueueProvider, Provider<AccessToken> accessTokenProvider, Provider<UserAgent> userAgentProvider) {  
    assert membersInjector != null;
    this.membersInjector = membersInjector;
    assert appContextProvider != null;
    this.appContextProvider = appContextProvider;
    assert dispatcherProvider != null;
    this.dispatcherProvider = dispatcherProvider;
    assert requestQueueProvider != null;
    this.requestQueueProvider = requestQueueProvider;
    assert accessTokenProvider != null;
    this.accessTokenProvider = accessTokenProvider;
    assert userAgentProvider != null;
    this.userAgentProvider = userAgentProvider;
  }

  @Override
  public PostRestClient get() {  
    PostRestClient instance = new PostRestClient(appContextProvider.get(), dispatcherProvider.get(), requestQueueProvider.get(), accessTokenProvider.get(), userAgentProvider.get());
    membersInjector.injectMembers(instance);
    return instance;
  }

  public static Factory<PostRestClient> create(MembersInjector<PostRestClient> membersInjector, Provider<Context> appContextProvider, Provider<Dispatcher> dispatcherProvider, Provider<RequestQueue> requestQueueProvider, Provider<AccessToken> accessTokenProvider, Provider<UserAgent> userAgentProvider) {  
    return new PostRestClient_Factory(membersInjector, appContextProvider, dispatcherProvider, requestQueueProvider, accessTokenProvider, userAgentProvider);
  }
}

