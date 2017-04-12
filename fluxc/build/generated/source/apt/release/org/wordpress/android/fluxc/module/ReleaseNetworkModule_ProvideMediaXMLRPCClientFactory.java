package org.wordpress.android.fluxc.module;

import com.android.volley.RequestQueue;
import dagger.internal.Factory;
import javax.annotation.Generated;
import javax.inject.Provider;
import okhttp3.OkHttpClient.Builder;
import org.wordpress.android.fluxc.Dispatcher;
import org.wordpress.android.fluxc.network.HTTPAuthManager;
import org.wordpress.android.fluxc.network.UserAgent;
import org.wordpress.android.fluxc.network.rest.wpcom.auth.AccessToken;
import org.wordpress.android.fluxc.network.xmlrpc.media.MediaXMLRPCClient;

@Generated("dagger.internal.codegen.ComponentProcessor")
public final class ReleaseNetworkModule_ProvideMediaXMLRPCClientFactory implements Factory<MediaXMLRPCClient> {
  private final ReleaseNetworkModule module;
  private final Provider<Dispatcher> dispatcherProvider;
  private final Provider<RequestQueue> requestQueueProvider;
  private final Provider<Builder> okHttpClientBuilderProvider;
  private final Provider<AccessToken> tokenProvider;
  private final Provider<UserAgent> userAgentProvider;
  private final Provider<HTTPAuthManager> httpAuthManagerProvider;

  public ReleaseNetworkModule_ProvideMediaXMLRPCClientFactory(ReleaseNetworkModule module, Provider<Dispatcher> dispatcherProvider, Provider<RequestQueue> requestQueueProvider, Provider<Builder> okHttpClientBuilderProvider, Provider<AccessToken> tokenProvider, Provider<UserAgent> userAgentProvider, Provider<HTTPAuthManager> httpAuthManagerProvider) {  
    assert module != null;
    this.module = module;
    assert dispatcherProvider != null;
    this.dispatcherProvider = dispatcherProvider;
    assert requestQueueProvider != null;
    this.requestQueueProvider = requestQueueProvider;
    assert okHttpClientBuilderProvider != null;
    this.okHttpClientBuilderProvider = okHttpClientBuilderProvider;
    assert tokenProvider != null;
    this.tokenProvider = tokenProvider;
    assert userAgentProvider != null;
    this.userAgentProvider = userAgentProvider;
    assert httpAuthManagerProvider != null;
    this.httpAuthManagerProvider = httpAuthManagerProvider;
  }

  @Override
  public MediaXMLRPCClient get() {  
    MediaXMLRPCClient provided = module.provideMediaXMLRPCClient(dispatcherProvider.get(), requestQueueProvider.get(), okHttpClientBuilderProvider.get(), tokenProvider.get(), userAgentProvider.get(), httpAuthManagerProvider.get());
    if (provided == null) {
      throw new NullPointerException("Cannot return null from a non-@Nullable @Provides method");
    }
    return provided;
  }

  public static Factory<MediaXMLRPCClient> create(ReleaseNetworkModule module, Provider<Dispatcher> dispatcherProvider, Provider<RequestQueue> requestQueueProvider, Provider<Builder> okHttpClientBuilderProvider, Provider<AccessToken> tokenProvider, Provider<UserAgent> userAgentProvider, Provider<HTTPAuthManager> httpAuthManagerProvider) {  
    return new ReleaseNetworkModule_ProvideMediaXMLRPCClientFactory(module, dispatcherProvider, requestQueueProvider, okHttpClientBuilderProvider, tokenProvider, userAgentProvider, httpAuthManagerProvider);
  }
}

