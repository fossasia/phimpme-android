package org.fossasia.phimpme.me;

import android.support.test.runner.AndroidJUnit4;

import org.fossasia.phimpme.accounts.AccountPresenter;
import org.fossasia.phimpme.data.local.DatabaseHelper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import io.realm.Realm;

import static org.mockito.Mockito.verify;

/**
 * Created by pa1pal on 31/07/17.
 */

@RunWith(AndroidJUnit4.class)
public class AccountPresenterTest {
    private Realm realm;

    @Mock
    private DatabaseHelper databaseHelper;
    private AccountPresenter accountPresenter;

    @Before
    public void setUp() throws Exception {
//        RealmConfiguration config =
//                new RealmConfiguration.Builder().inMemory().name("test-realm").build();
//        realm = Realm.getInstance(config);'
        realm = Realm.getDefaultInstance();
        //realm = MockSupport.mockRealm();
        MockitoAnnotations.initMocks(this);
//        databaseHelper = Mockito.mock(DatabaseHelper.class);
        accountPresenter = new AccountPresenter(databaseHelper);
    }

    @Test
    public void loadAccountsFromDatabase() {
        accountPresenter.loadFromDatabase();
        verify(databaseHelper).fetchAccountDetails();
       // Mockito.doReturn(notNull(RealmQuery.class)).when(accountPresenter).loadFromDatabase();
    }
}
