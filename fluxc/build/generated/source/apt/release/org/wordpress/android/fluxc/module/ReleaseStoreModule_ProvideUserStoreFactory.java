package org.wordpress.android.fluxc.module;

import dagger.internal.Factory;
import javax.annotation.Generated;
import javax.inject.Provider;
import org.wordpress.android.fluxc.Dispatcher;
import org.wordpress.android.fluxc.network.discovery.SelfHostedEndpointFinder;
import org.wordpress.android.fluxc.network.rest.wpcom.account.AccountRestClient;
import org.wordpress.android.fluxc.network.rest.wpcom.auth.AccessToken;
import org.wordpress.android.fluxc.network.rest.wpcom.auth.Authenticator;
import org.wordpress.android.fluxc.store.AccountStore;

@Generated("dagger.internal.codegen.ComponentProcessor")
public final class ReleaseStoreModule_ProvideUserStoreFactory implements Factory<AccountStore> {
  private final ReleaseStoreModule module;
  private final Provider<Dispatcher> dispatcherProvider;
  private final Provider<AccountRestClient> accountRestClientProvider;
  private final Provider<SelfHostedEndpointFinder> selfHostedEndpointFinderProvider;
  private final Provider<Authenticator> authenticatorProvider;
  private final Provider<AccessToken> accessTokenProvider;

  public ReleaseStoreModule_ProvideUserStoreFactory(ReleaseStoreModule module, Provider<Dispatcher> dispatcherProvider, Provider<AccountRestClient> accountRestClientProvider, Provider<SelfHostedEndpointFinder> selfHostedEndpointFinderProvider, Provider<Authenticator> authenticatorProvider, Provider<AccessToken> accessTokenProvider) {  
    assert module != null;
    this.module = module;
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
    AccountStore provided = module.provideUserStore(dispatcherProvider.get(), accountRestClientProvider.get(), selfHostedEndpointFinderProvider.get(), authenticatorProvider.get(), accessTokenProvider.get());
    if (provided == null) {
      throw new NullPointerException("Cannot return null from a non-@Nullable @Provides method");
    }
    return provided;
  }

  public static Factory<AccountStore> create(ReleaseStoreModule module, Provider<Dispatcher> dispatcherProvider, Provider<AccountRestClient> accountRestClientProvider, Provider<SelfHostedEndpointFinder> selfHostedEndpointFinderProvider, Provider<Authenticator> authenticatorProvider, Provider<AccessToken> accessTokenProvider) {  
    return new ReleaseStoreModule_ProvideUserStoreFactory(module, dispatcherProvider, accountRestClientProvider, selfHostedEndpointFinderProvider, authenticatorProvider, accessTokenProvider);
  }
}

