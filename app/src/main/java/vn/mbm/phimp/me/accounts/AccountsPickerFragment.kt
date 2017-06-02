package vn.mbm.phimp.me.accounts

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.app.DialogFragment
import vn.mbm.phimp.me.R


/**
 * A fragment for account picker in account manager.
 * Provides a list of available account to connect to Phimp.me
 */

class AccountsPickerFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
        builder.setTitle(R.string.account_list)
                .setItems(R.array.accounts_array, DialogInterface.OnClickListener { dialog, which ->
                    // The 'which' argument contains the index position
                    // of the selected item
                })
        return builder.create()
    }


}// Required empty public constructor
