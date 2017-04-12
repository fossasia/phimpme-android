package vn.mbm.phimp.me.wordpress;

import org.wordpress.android.fluxc.action.AccountAction;
import org.wordpress.android.fluxc.annotations.action.Action;
import org.wordpress.android.fluxc.annotations.action.ActionBuilder;
import org.wordpress.android.fluxc.network.rest.wpcom.account.AccountRestClient.AccountRestPayload;
import org.wordpress.android.fluxc.store.AccountStore.NewAccountPayload;
import org.wordpress.android.fluxc.store.AccountStore.PushAccountSettingsPayload;
import org.wordpress.android.fluxc.store.AccountStore.UpdateTokenPayload;

/**
 * Created by rohanagarwal94 on 6/4/17.
 */

public final class AccountActionBuilder extends ActionBuilder {
    public AccountActionBuilder() {
    }

    public static Action<Void> newFetchAccountAction() {
        return generateNoPayloadAction(AccountAction.FETCH_ACCOUNT);
    }

    public static Action<Void> newFetchSettingsAction() {
        return generateNoPayloadAction(AccountAction.FETCH_SETTINGS);
    }

    public static Action<PushAccountSettingsPayload> newPushSettingsAction(PushAccountSettingsPayload payload) {
        return new Action(AccountAction.PUSH_SETTINGS, payload);
    }

    public static Action<NewAccountPayload> newCreateNewAccountAction(NewAccountPayload payload) {
        return new Action(AccountAction.CREATE_NEW_ACCOUNT, payload);
    }

    public static Action<String> newIsAvailableBlogAction(String payload) {
        return new Action(AccountAction.IS_AVAILABLE_BLOG, payload);
    }

    public static Action<String> newIsAvailableDomainAction(String payload) {
        return new Action(AccountAction.IS_AVAILABLE_DOMAIN, payload);
    }

    public static Action<String> newIsAvailableEmailAction(String payload) {
        return new Action(AccountAction.IS_AVAILABLE_EMAIL, payload);
    }

    public static Action<String> newIsAvailableUsernameAction(String payload) {
        return new Action(AccountAction.IS_AVAILABLE_USERNAME, payload);
    }

    public static Action<AccountRestPayload> newFetchedAccountAction(AccountRestPayload payload) {
        return new Action(AccountAction.FETCHED_ACCOUNT, payload);
    }

    public static Action<UpdateTokenPayload> newUpdateAccessTokenAction(UpdateTokenPayload payload) {
        return new Action(AccountAction.UPDATE_ACCESS_TOKEN, payload);
    }

    public static Action<Void> newSignOutAction() {
        return generateNoPayloadAction(AccountAction.SIGN_OUT);
    }
}
