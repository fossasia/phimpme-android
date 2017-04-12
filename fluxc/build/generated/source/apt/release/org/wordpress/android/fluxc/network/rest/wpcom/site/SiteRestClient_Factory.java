package org.wordpress.android.fluxc.network.rest.wpcom.site;

import android.content.Context;
import com.android.volley.RequestQueue;
import dagger.MembersInjector;
import dagger.internal.Factory;
import javax.annotation.Generated;
import javax.inject.Provider;
import org.wordpress.android.fluxc.Dispatcher;
import org.wordpress.android.fluxc.network.UserAgent;
import org.wordpress.android.fluxc.network.rest.wpcom.auth.AccessToken;
import org.wordpress.android.fluxc.network.rest.wpcom.auth.AppSecrets;

@Generated("dagger.internal.codegen.ComponentProcessor")
public final class SiteRestClient_Factory implements Factory<SiteRestClient> {
  private final MembersInjector<SiteRestClient> membersInjector;
  private final Provider<Context> appContextProvider;
  private final Provider<Dispatcher> dispatcherProvider;
  private final Provider<RequestQueue> requestQueueProvider;
  private final Provider<AppSecrets> appSecretsProvider;
  private final Provider<AccessToken> accessTokenProvider;
  private final Provider<UserAgent> userAgentProvider;

  public SiteRestClient_Factory(MembersInjector<SiteRestClient> membersInjector, Provider<Context> appContextProvider, Provider<Dispatcher> dispatcherProvider, Provider<RequestQueue> requestQueueProvider, Provider<AppSecrets> appSecretsProvider, Provider<AccessToken> accessTokenProvider, Provider<UserAgent> userAgentProvider) {  
    assert membersInjector != null;
    this.membersInjector = membersInjector;
    assert appContextProvider != null;
    this.appContextProvider = appContextProvider;
    assert dispatcherProvider != null;
    this.dispatcherProvider = dispatcherProvider;
    assert requestQueueProvider != null;
    this.requestQueueProvider = requestQueueProvider;
    assert appSecretsProvider != null;
    this.appSecretsProvider = appSecretsProvider;
    assert accessTokenProvider != null;
    this.accessTokenProvider = accessTokenProvider;
    assert userAgentProvider != null;
    this.userAgentProvider = userAgentProvider;
  }

  @Override
  public SiteRestClient get() {  
    SiteRestClient instance = new SiteRestClient(appContextProvider.get(), dispatcherProvider.get(), requestQueueProvider.get(), appSecretsProvider.get(), accessTokenProvider.get(), userAgentProvider.get());
    membersInjector.injectMembers(instance);
    return instance;
  }

  public static Factory<SiteRestClient> create(MembersInjector<SiteRestClient> membersInjector, Provider<Context> appContextProvider, Provider<Dispatcher> dispatcherProvider, Provider<RequestQueue> requestQueueProvider, Provider<AppSecrets> appSecretsProvider, Provider<AccessToken> accessTokenProvider, Provider<UserAgent> userAgentProvider) {  
    return new SiteRestClient_Factory(membersInjector, appContextProvider, dispatcherProvider, requestQueueProvider, appSecretsProvider, accessTokenProvider, userAgentProvider);
  }
}

