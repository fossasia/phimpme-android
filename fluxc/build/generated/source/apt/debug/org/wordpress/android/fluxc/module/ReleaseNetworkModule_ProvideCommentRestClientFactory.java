package org.wordpress.android.fluxc.module;

import android.content.Context;
import com.android.volley.RequestQueue;
import dagger.internal.Factory;
import javax.annotation.Generated;
import javax.inject.Provider;
import org.wordpress.android.fluxc.Dispatcher;
import org.wordpress.android.fluxc.network.UserAgent;
import org.wordpress.android.fluxc.network.rest.wpcom.auth.AccessToken;
import org.wordpress.android.fluxc.network.rest.wpcom.comment.CommentRestClient;

@Generated("dagger.internal.codegen.ComponentProcessor")
public final class ReleaseNetworkModule_ProvideCommentRestClientFactory implements Factory<CommentRestClient> {
  private final ReleaseNetworkModule module;
  private final Provider<Context> appContextProvider;
  private final Provider<Dispatcher> dispatcherProvider;
  private final Provider<RequestQueue> requestQueueProvider;
  private final Provider<AccessToken> tokenProvider;
  private final Provider<UserAgent> userAgentProvider;

  public ReleaseNetworkModule_ProvideCommentRestClientFactory(ReleaseNetworkModule module, Provider<Context> appContextProvider, Provider<Dispatcher> dispatcherProvider, Provider<RequestQueue> requestQueueProvider, Provider<AccessToken> tokenProvider, Provider<UserAgent> userAgentProvider) {  
    assert module != null;
    this.module = module;
    assert appContextProvider != null;
    this.appContextProvider = appContextProvider;
    assert dispatcherProvider != null;
    this.dispatcherProvider = dispatcherProvider;
    assert requestQueueProvider != null;
    this.requestQueueProvider = requestQueueProvider;
    assert tokenProvider != null;
    this.tokenProvider = tokenProvider;
    assert userAgentProvider != null;
    this.userAgentProvider = userAgentProvider;
  }

  @Override
  public CommentRestClient get() {  
    CommentRestClient provided = module.provideCommentRestClient(appContextProvider.get(), dispatcherProvider.get(), requestQueueProvider.get(), tokenProvider.get(), userAgentProvider.get());
    if (provided == null) {
      throw new NullPointerException("Cannot return null from a non-@Nullable @Provides method");
    }
    return provided;
  }

  public static Factory<CommentRestClient> create(ReleaseNetworkModule module, Provider<Context> appContextProvider, Provider<Dispatcher> dispatcherProvider, Provider<RequestQueue> requestQueueProvider, Provider<AccessToken> tokenProvider, Provider<UserAgent> userAgentProvider) {  
    return new ReleaseNetworkModule_ProvideCommentRestClientFactory(module, appContextProvider, dispatcherProvider, requestQueueProvider, tokenProvider, userAgentProvider);
  }
}

