package org.fossasia.phimpme.account;

import android.content.Context;

import org.fossasia.phimpme.BuildConfig;
import org.fossasia.phimpme.accounts.AccountContract;
import org.fossasia.phimpme.accounts.AccountPresenter;
import org.fossasia.phimpme.data.local.AccountDatabase;
import org.fossasia.phimpme.data.local.DatabaseHelper;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmQuery;
import io.realm.RealmResults;

import static org.mockito.Matchers.notNull;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mock;

/**
 * Created by pa1pal on 31/07/17.
 */

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
@PowerMockIgnore({"org.mockito.*", "org.robolectric.*", "android.*"})
@PrepareForTest({Realm.class})
public class AccountPTest {

    //private static RealmQuery<AccountDatabase> ACCOUNTS;

    @Mock
    private AccountContract.View accountView;

    @Mock
    Context context;

    private AccountPresenter accountPresenter;

    private DatabaseHelper databaseHelper;

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

    Realm mockRealm;

    @Rule
    public PowerMockRule rule = new PowerMockRule();


    @Before
    public void setupAccountPresenter() throws Exception {
        // Mockito has a very convenient way to inject mocks by using the @Mock annotation. To
        // inject the mocks in the test the initMocks method needs to be called.
        mockRealm = MockSupport.mockRealm();

        MockitoAnnotations.initMocks(this);

       // Realm.init(context);
        realm = PowerMockito.mock(Realm.class);

        //realm = Realm.getDefaultInstance();
        accountPresenter = Mockito.spy(new AccountPresenter(mockRealm));

//        // Setup Realm to be mocked. The order of these matters
//        mockStatic(RealmCore.class);
//        mockStatic(RealmLog.class);
//        mockStatic(Realm.class);
//        mockStatic(RealmConfiguration.class);
//        Realm.init(RuntimeEnvironment.application);
//
//        // Create the mock
//        final Realm mockRealm = mock(Realm.class);
//        final RealmConfiguration mockRealmConfig = mock(RealmConfiguration.class);
//
//        doNothing().when(RealmCore.class);
//        RealmCore.loadLibrary(any(Context.class));
//
//        whenNew(RealmConfiguration.class).withAnyArguments().thenReturn(mockRealmConfig);
//
//        // Anytime getInstance is called with any configuration, then return the mockRealm
//        when(Realm.getDefaultInstance()).thenReturn(mockRealm);
//
//        // Anytime we ask Realm to create a Person, return a new instance.
//        when(mockRealm.createObject(AccountDatabase.class)).thenReturn(new AccountDatabase());
//
//        // Set up some naive stubs
//        AccountDatabase a1 = new AccountDatabase();
//        a1.setAccountname(AccountDatabase.AccountName.FACEBOOK);
//
//        AccountDatabase a2 = new AccountDatabase();
//        a2.setAccountname(AccountDatabase.AccountName.TWITTER);
//
//        AccountDatabase a3 = new AccountDatabase();
//        a3.setAccountname(AccountDatabase.AccountName.NEXTCLOUD);
//
//        List<AccountDatabase> accountList = Arrays.asList(a1, a2, a3);
//
//        // Create a mock RealmQuery
//        RealmQuery<AccountDatabase> accountQuery = mockRealmQuery();
//
//        when(mockRealm.where(AccountDatabase.class)).thenReturn(accountQuery);

    }

    @Test
    public void loadAccountsFromDatabase() {
        accountPresenter.loadFromDatabase();
        verify(accountPresenter).handleResults(mockRealm.where(AccountDatabase.class));
        Mockito.doReturn(notNull(RealmQuery.class)).when(accountPresenter).loadFromDatabase();
    }

    @SuppressWarnings("unchecked")
    private <T extends RealmObject> RealmQuery<T> mockRealmQuery() {
        return mock(RealmQuery.class);
    }
}
