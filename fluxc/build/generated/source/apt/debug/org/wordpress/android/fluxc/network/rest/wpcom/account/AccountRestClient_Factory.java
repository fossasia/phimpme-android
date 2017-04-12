package org.wordpress.android.fluxc.network.rest.wpcom.account;

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
public final class AccountRestClient_Factory implements Factory<AccountRestClient> {
  private final MembersInjector<AccountRestClient> membersInjector;
  private final Provider<Context> appContextProvider;
  private final Provider<Dispatcher> dispatcherProvider;
  private final Provider<RequestQueue> requestQueueProvider;
  private final Provider<AppSecrets> appSecretsProvider;
  private final Provider<AccessToken> accessTokenProvider;
  private final Provider<UserAgent> userAgentProvider;

  public AccountRestClient_Factory(MembersInjector<AccountRestClient> membersInjector, Provider<Context> appContextProvider, Provider<Dispatcher> dispatcherProvider, Provider<RequestQueue> requestQueueProvider, Provider<AppSecrets> appSecretsProvider, Provider<AccessToken> accessTokenProvider, Provider<UserAgent> userAgentProvider) {  
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
  public AccountRestClient get() {  
    AccountRestClient instance = new AccountRestClient(appContextProvider.get(), dispatcherProvider.get(), requestQueueProvider.get(), appSecretsProvider.get(), accessTokenProvider.get(), userAgentProvider.get());
    membersInjector.injectMembers(instance);
    return instance;
  }

  public static Factory<AccountRestClient> create(MembersInjector<AccountRestClient> membersInjector, Provider<Context> appContextProvider, Provider<Dispatcher> dispatcherProvider, Provider<RequestQueue> requestQueueProvider, Provider<AppSecrets> appSecretsProvider, Provider<AccessToken> accessTokenProvider, Provider<UserAgent> userAgentProvider) {  
    return new AccountRestClient_Factory(membersInjector, appContextProvider, dispatcherProvider, requestQueueProvider, appSecretsProvider, accessTokenProvider, userAgentProvider);
  }
}

