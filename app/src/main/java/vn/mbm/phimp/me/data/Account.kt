package vn.mbm.phimp.me.data

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass

/**
 * Created by pa1pal on 08/06/17.
 */

@RealmClass
class Account(
        @PrimaryKey var name: String = "",
        var username: String = "",
        var token: String = ""
) : RealmObject()