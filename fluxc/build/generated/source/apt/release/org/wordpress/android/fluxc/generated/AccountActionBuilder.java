package org.wordpress.android.fluxc.generated;

import java.lang.String;
import java.lang.Void;
import org.wordpress.android.fluxc.action.AccountAction;
import org.wordpress.android.fluxc.annotations.action.Action;
import org.wordpress.android.fluxc.annotations.action.ActionBuilder;
import org.wordpress.android.fluxc.model.AccountModel;
import org.wordpress.android.fluxc.network.rest.wpcom.account.AccountRestClient;
import org.wordpress.android.fluxc.store.AccountStore;

public final class AccountActionBuilder extends ActionBuilder {
  public static Action<Void> newFetchAccountAction() {
    return generateNoPayloadAction(AccountAction.FETCH_ACCOUNT);
  }

  public static Action<Void> newFetchSettingsAction() {
    return generateNoPayloadAction(AccountAction.FETCH_SETTINGS);
  }

  public static Action<Void> newSendVerificationEmailAction() {
    return generateNoPayloadAction(AccountAction.SEND_VERIFICATION_EMAIL);
  }

  public static Action<AccountStore.PushAccountSettingsPayload> newPushSettingsAction(AccountStore.PushAccountSettingsPayload payload) {
    return new Action<>(AccountAction.PUSH_SETTINGS, payload);
  }

  public static Action<AccountStore.NewAccountPayload> newCreateNewAccountAction(AccountStore.NewAccountPayload payload) {
    return new Action<>(AccountAction.CREATE_NEW_ACCOUNT, payload);
  }

  public static Action<String> newIsAvailableBlogAction(String payload) {
    return new Action<>(AccountAction.IS_AVAILABLE_BLOG, payload);
  }

  public static Action<String> newIsAvailableDomainAction(String payload) {
    return new Action<>(AccountAction.IS_AVAILABLE_DOMAIN, payload);
  }

  public static Action<String> newIsAvailableEmailAction(String payload) {
    return new Action<>(AccountAction.IS_AVAILABLE_EMAIL, payload);
  }

  public static Action<String> newIsAvailableUsernameAction(String payload) {
    return new Action<>(AccountAction.IS_AVAILABLE_USERNAME, payload);
  }

  public static Action<AccountRestClient.AccountRestPayload> newFetchedAccountAction(AccountRestClient.AccountRestPayload payload) {
    return new Action<>(AccountAction.FETCHED_ACCOUNT, payload);
  }

  public static Action<AccountRestClient.AccountRestPayload> newFetchedSettingsAction(AccountRestClient.AccountRestPayload payload) {
    return new Action<>(AccountAction.FETCHED_SETTINGS, payload);
  }

  public static Action<AccountRestClient.NewAccountResponsePayload> newSentVerificationEmailAction(AccountRestClient.NewAccountResponsePayload payload) {
    return new Action<>(AccountAction.SENT_VERIFICATION_EMAIL, payload);
  }

  public static Action<AccountRestClient.AccountPushSettingsResponsePayload> newPushedSettingsAction(AccountRestClient.AccountPushSettingsResponsePayload payload) {
    return new Action<>(AccountAction.PUSHED_SETTINGS, payload);
  }

  public static Action<AccountRestClient.NewAccountResponsePayload> newCreatedNewAccountAction(AccountRestClient.NewAccountResponsePayload payload) {
    return new Action<>(AccountAction.CREATED_NEW_ACCOUNT, payload);
  }

  public static Action<AccountRestClient.IsAvailableResponsePayload> newCheckedIsAvailableAction(AccountRestClient.IsAvailableResponsePayload payload) {
    return new Action<>(AccountAction.CHECKED_IS_AVAILABLE, payload);
  }

  public static Action<AccountModel> newUpdateAccountAction(AccountModel payload) {
    return new Action<>(AccountAction.UPDATE_ACCOUNT, payload);
  }

  public static Action<AccountStore.UpdateTokenPayload> newUpdateAccessTokenAction(AccountStore.UpdateTokenPayload payload) {
    return new Action<>(AccountAction.UPDATE_ACCESS_TOKEN, payload);
  }

  public static Action<Void> newSignOutAction() {
    return generateNoPayloadAction(AccountAction.SIGN_OUT);
  }
}
