package org.fossasia.phimpme.account;

import org.fossasia.phimpme.BuildConfig;
import org.fossasia.phimpme.accounts.AccountContract;
import org.fossasia.phimpme.accounts.AccountPresenter;
import org.fossasia.phimpme.data.local.DatabaseHelper;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
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

import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mock;

/**
 * Created by pa1pal on 31/07/17.
 */

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 18, manifest = "src/main/AndroidManifest.xml"
        , packageName = "org.fossasia.phimpme")
@PowerMockIgnore({"org.mockito.*", "org.robolectric.*", "android.*"})
@PrepareForTest({Realm.class})
public class AccountPTest {

    @Mock
    private AccountContract.View accountView;

    private AccountPresenter accountPresenter;

    @Mock
    private DatabaseHelper databaseHelper;

    private Realm realm;

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

        MockitoAnnotations.initMocks(this);

        accountPresenter = new AccountPresenter(databaseHelper);


    }

    @Test
    public void loadAccountsFromDatabase() {
        accountPresenter.loadFromDatabase();
//        verify(accountPresenter).handleResults(mockRealm.where(AccountDatabase.class));
//        Mockito.doReturn(notNull(RealmQuery.class)).when(accountPresenter).loadFromDatabase();
        verify(databaseHelper).fetchAccountDetails();
    }

    @SuppressWarnings("unchecked")
    private <T extends RealmObject> RealmQuery<T> mockRealmQuery() {
        return mock(RealmQuery.class);
    }
}
