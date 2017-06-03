package vn.mbm.phimp.me.accounts

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import vn.mbm.phimp.me.R


/**
 * A fragment for account picker in account manager.
 * Provides a list of available account to connect to Phimp.me
 */

class AccountPickerFragment : DialogFragment() {

    fun newInstance(): AccountPickerFragment {
        val f = AccountPickerFragment()
        return f
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val builder = AlertDialog.Builder(activity)
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
    }


}// Required empty public constructor
