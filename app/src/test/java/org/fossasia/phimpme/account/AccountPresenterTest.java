package org.fossasia.phimpme.account;

import android.content.Context;

import org.fossasia.phimpme.MyApplication;
import org.fossasia.phimpme.accounts.AccountContract;
import org.fossasia.phimpme.accounts.AccountPresenter;
import org.fossasia.phimpme.data.local.AccountDatabase;
import org.fossasia.phimpme.data.local.DatabaseHelper;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RuntimeEnvironment;

import java.util.Arrays;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmObject;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.internal.RealmCore;
import io.realm.log.RealmLog;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.doNothing;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

/**
 * Created by pa1pal on 31/07/17.
 */

public class AccountPresenterTest {

    //private static RealmQuery<AccountDatabase> ACCOUNTS;

    @Mock
    private AccountContract.View accountView;

    private AccountPresenter accountPresenter;

    private DatabaseHelper databaseHelper;

    private Realm mockRealm;

    private RealmResults<AccountDatabase> accountDatabase;

    private Realm realm;

    /**
     * {@link org.mockito.ArgumentCaptor} is a powerful Mockito API to capture argument values and use them to
     * perform further actions or assertions on them.
     */
    @Captor
    private ArgumentCaptor<DatabaseHelper> databaseHelperCaptor;

    @Captor
    private ArgumentCaptor<RealmQuery<AccountDatabase>> realmQueryArgumentCaptor;

    @Before
    public void setupAccountPresenter() throws Exception {
        // Mockito has a very convenient way to inject mocks by using the @Mock annotation. To
        // inject the mocks in the test the initMocks method needs to be called.
        MockitoAnnotations.initMocks(this);

        Realm.init(MyApplication.applicationContext);
        realm = Realm.getDefaultInstance();
        accountPresenter = new AccountPresenter(realm);

        // Setup Realm to be mocked. The order of these matters
        mockStatic(RealmCore.class);
        mockStatic(RealmLog.class);
        mockStatic(Realm.class);
        mockStatic(RealmConfiguration.class);
        Realm.init(RuntimeEnvironment.application);

        // Create the mock
        final Realm mockRealm = mock(Realm.class);
        final RealmConfiguration mockRealmConfig = mock(RealmConfiguration.class);

        doNothing().when(RealmCore.class);
        RealmCore.loadLibrary(any(Context.class));

        whenNew(RealmConfiguration.class).withAnyArguments().thenReturn(mockRealmConfig);

        // Anytime getInstance is called with any configuration, then return the mockRealm
        when(Realm.getDefaultInstance()).thenReturn(mockRealm);

        // Anytime we ask Realm to create a Person, return a new instance.
        when(mockRealm.createObject(AccountDatabase.class)).thenReturn(new AccountDatabase());

        // Set up some naive stubs
        AccountDatabase a1 = new AccountDatabase();
        a1.setAccountname(AccountDatabase.AccountName.FACEBOOK);

        AccountDatabase a2 = new AccountDatabase();
        a2.setAccountname(AccountDatabase.AccountName.TWITTER);

        AccountDatabase a3 = new AccountDatabase();
        a3.setAccountname(AccountDatabase.AccountName.NEXTCLOUD);

        List<AccountDatabase> accountList = Arrays.asList(a1, a2, a3);

        // Create a mock RealmQuery
        RealmQuery<AccountDatabase> accountQuery = mockRealmQuery();

        when(mockRealm.where(AccountDatabase.class)).thenReturn(accountQuery);

    }

    @Test
    public void loadAccountsFromDatasbase() {
        accountPresenter.loadFromDatabase();
        verify(accountPresenter).handleResults(mockRealm.where(AccountDatabase.class));
    }

    @SuppressWarnings("unchecked")
    private <T extends RealmObject> RealmQuery<T> mockRealmQuery() {
        return mock(RealmQuery.class);
    }
}
