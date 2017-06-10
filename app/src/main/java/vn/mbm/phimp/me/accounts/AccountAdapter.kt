package vn.mbm.phimp.me.accounts

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import io.realm.RealmResults
import vn.mbm.phimp.me.R
import vn.mbm.phimp.me.data.AccountDatabase

/**
 * Created by pa1pal on 5/6/17.
 */
class AccountAdapter : RecyclerView.Adapter<AccountAdapter.ViewHolder>() {

    private var accountDetails: RealmResults<AccountDatabase>? = null

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent!!.getContext())
                .inflate(R.layout.accounts_item_view, null, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        holder!!.userName!!.setText("@test_username")
        holder!!.userFullName!!.setText("Test Full Name")
    }

    override fun getItemCount(): Int {
        //TODO: added a temporary value. Exact value will be depends how many accounts are signed in.
        return accountDetails!!.size
    }

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        internal var accountAvatar: ImageView? = null
        internal var userName: TextView? = null
        internal var userFullName: TextView? = null
        internal var accountLogoIndicator: ImageView? = null

        init {
            accountAvatar = v.findViewById(R.id.account_avatar) as ImageView
            accountLogoIndicator = v.findViewById(R.id.account_logo_indicator) as ImageView
            userName = v.findViewById(R.id.account_username) as TextView
            userFullName = v.findViewById(R.id.account_name) as TextView
        }
    }

    fun setResults(accountDetails: RealmResults<AccountDatabase>) {
        this.accountDetails = accountDetails
        notifyDataSetChanged()
    }

}