package vn.mbm.phimp.me.accounts

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.twitter.sdk.android.core.identity.TwitterLoginButton
import vn.mbm.phimp.me.R


/**
 * A fragment for account picker in account manager.
 * Provides a list of available account to connect to Phimp.me
 */

class AccountPickerFragment : DialogFragment(){

    fun newInstance(): AccountPickerFragment {
        val f = AccountPickerFragment()
        return f
        var twitterLogin: TwitterLoginButton? = null

        fun newInstance(title: String?): AccountPickerFragment {
            val accountPickerFragment = AccountPickerFragment()
            val args = Bundle()
            args.putString("title", title);
            accountPickerFragment.setArguments(args);
            return accountPickerFragment;

        }


        /*override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val builder = AlertDialog.Builder(activity)

                .setView(R.layout.account_picker_dialog_layout)
                .setTitle(R.string.account_list)
                .setPositiveButton(R.string.ok_action, DialogInterface.OnClickListener {
                    dialog, which ->

                });



        builder.setTitle(R.string.account_list)
                .setItems(R.array.accounts_array, DialogInterface.OnClickListener { dialog, which ->
                    when (which){
                        0 -> {
                            Toast.makeText(activity, "Twitter", LENGTH_LONG).show()
                        }
                    }
                    // The 'which' argument contains the index position
                    // of the selected item
                })
        return builder.create()
    }*/


        override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            return inflater!!.inflate(R.layout.account_picker_dialog_layout, container)
        }

        override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)

            /*twitterLogin = view!!.findViewById(R.id.twitter_login_button) as TwitterLoginButton

        twitterLogin!!.callback = object : Callback<TwitterSession>() {
            override fun success(p0: Result<TwitterSession>?) {
                val session = TwitterCore.getInstance().sessionManager.activeSession
                val authToken = session.authToken
                val token = authToken.token
                val secret = authToken.secret

                Log.d("sfaf", session.toString())
            }

            override fun failure(p0: TwitterException?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        }*/
        }
    }
}// Required empty public constructor
