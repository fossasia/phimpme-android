package org.wordpress.android.fluxc.generated;

import java.lang.String;
import org.wordpress.android.fluxc.action.AuthenticationAction;
import org.wordpress.android.fluxc.annotations.action.Action;
import org.wordpress.android.fluxc.annotations.action.ActionBuilder;
import org.wordpress.android.fluxc.network.discovery.SelfHostedEndpointFinder;
import org.wordpress.android.fluxc.network.rest.wpcom.auth.Authenticator;
import org.wordpress.android.fluxc.store.AccountStore;

public final class AuthenticationActionBuilder extends ActionBuilder {
  public static Action<AccountStore.AuthenticatePayload> newAuthenticateAction(AccountStore.AuthenticatePayload payload) {
    return new Action<>(AuthenticationAction.AUTHENTICATE, payload);
  }

  public static Action<String> newDiscoverEndpointAction(String payload) {
    return new Action<>(AuthenticationAction.DISCOVER_ENDPOINT, payload);
  }

  public static Action<String> newSendAuthEmailAction(String payload) {
    return new Action<>(AuthenticationAction.SEND_AUTH_EMAIL, payload);
  }

  public static Action<AccountStore.AuthenticateErrorPayload> newAuthenticateErrorAction(AccountStore.AuthenticateErrorPayload payload) {
    return new Action<>(AuthenticationAction.AUTHENTICATE_ERROR, payload);
  }

  public static Action<SelfHostedEndpointFinder.DiscoveryResultPayload> newDiscoveryResultAction(SelfHostedEndpointFinder.DiscoveryResultPayload payload) {
    return new Action<>(AuthenticationAction.DISCOVERY_RESULT, payload);
  }

  public static Action<Authenticator.AuthEmailResponsePayload> newSentAuthEmailAction(Authenticator.AuthEmailResponsePayload payload) {
    return new Action<>(AuthenticationAction.SENT_AUTH_EMAIL, payload);
  }
}
