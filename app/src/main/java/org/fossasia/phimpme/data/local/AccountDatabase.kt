package org.fossasia.phimpme.data.local
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

/**
 * Created by pa1pal on 08/06/17.
 *
 * Model class for accounts. Where we store relevant details related to accounts, which we required
 * to access their SDKs and APIs
 */

open class AccountDatabase(
        @PrimaryKey var name: String = "",
        var username: String = "",
        var token: String = ""
) : RealmObject(){
    /**
     * No need to create getter and setter property in Kotlin Model
     */
}