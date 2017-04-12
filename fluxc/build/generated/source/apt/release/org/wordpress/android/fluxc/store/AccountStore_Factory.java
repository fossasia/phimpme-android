package org.wordpress.android.fluxc.store;

import dagger.MembersInjector;
import dagger.internal.Factory;
import javax.annotation.Generated;
import javax.inject.Provider;
import org.wordpress.android.fluxc.Dispatcher;
import org.wordpress.android.fluxc.network.discovery.SelfHostedEndpointFinder;
import org.wordpress.android.fluxc.network.rest.wpcom.account.AccountRestClient;
import org.wordpress.android.fluxc.network.rest.wpcom.auth.AccessToken;
import org.wordpress.android.fluxc.network.rest.wpcom.auth.Authenticator;

@Generated("dagger.internal.codegen.ComponentProcessor")
public final class AccountStore_Factory implements Factory<AccountStore> {
  private final MembersInjector<AccountStore> membersInjector;
  private final Provider<Dispatcher> dispatcherProvider;
  private final Provider<AccountRestClient> accountRestClientProvider;
  private final Provider<SelfHostedEndpointFinder> selfHostedEndpointFinderProvider;
  private final Provider<Authenticator> authenticatorProvider;
  private final Provider<AccessToken> accessTokenProvider;

  public AccountStore_Factory(MembersInjector<AccountStore> membersInjector, Provider<Dispatcher> dispatcherProvider, Provider<AccountRestClient> accountRestClientProvider, Provider<SelfHostedEndpointFinder> selfHostedEndpointFinderProvider, Provider<Authenticator> authenticatorProvider, Provider<AccessToken> accessTokenProvider) {  
    assert membersInjector != null;
    this.membersInjector = membersInjector;
    assert dispatcherProvider != null;
    this.dispatcherProvider = dispatcherProvider;
    assert accountRestClientProvider != null;
    this.accountRestClientProvider = accountRestClientProvider;
    assert selfHostedEndpointFinderProvider != null;
    this.selfHostedEndpointFinderProvider = selfHostedEndpointFinderProvider;
    assert authenticatorProvider != null;
    this.authenticatorProvider = authenticatorProvider;
    assert accessTokenProvider != null;
    this.accessTokenProvider = accessTokenProvider;
  }

  @Override
  public AccountStore get() {  
    AccountStore instance = new AccountStore(dispatcherProvider.get(), accountRestClientProvider.get(), selfHostedEndpointFinderProvider.get(), authenticatorProvider.get(), accessTokenProvider.get());
    membersInjector.injectMembers(instance);
    return instance;
  }

  public static Factory<AccountStore> create(MembersInjector<AccountStore> membersInjector, Provider<Dispatcher> dispatcherProvider, Provider<AccountRestClient> accountRestClientProvider, Provider<SelfHostedEndpointFinder> selfHostedEndpointFinderProvider, Provider<Authenticator> authenticatorProvider, Provider<AccessToken> accessTokenProvider) {  
    return new AccountStore_Factory(membersInjector, dispatcherProvider, accountRestClientProvider, selfHostedEndpointFinderProvider, authenticatorProvider, accessTokenProvider);
  }
}

