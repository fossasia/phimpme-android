package org.fossasia.phimpme;

import org.fossasia.phimpme.data.local.AccountDatabase;
import org.powermock.api.mockito.PowerMockito;

import io.realm.Realm;

import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;
/**
 * Created by pa1pal on 02/08/17.
 */

public class MockSupport {

    public static Realm mockRealm() {
        mockStatic(Realm.class);

        Realm mockRealm = PowerMockito.mock(Realm.class);

        when(mockRealm.createObject(AccountDatabase.class)).thenReturn(new AccountDatabase());

        when(Realm.getDefaultInstance()).thenReturn(mockRealm);

        return mockRealm;
    }
}
