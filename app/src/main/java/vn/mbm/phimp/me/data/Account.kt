package vn.mbm.phimp.me.data

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

/**
 * Created by pa1pal on 08/06/17.
 */

class Account(@PrimaryKey val name: String, val username: String, val token: String) : RealmObject()