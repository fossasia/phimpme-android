package vn.mbm.phimp.me.accounts

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import vn.mbm.phimp.me.R

/**
 * Created by pa1pal on 5/6/17.
 */
class AccountAdapter : RecyclerView.Adapter<AccountAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent!!.getContext())
                .inflate(R.layout.accounts_item_view, null, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        holder!!.userName!!.setText("temp")
        holder!!.userFullName!!.setText("temp_full_name")
    }

    override fun getItemCount(): Int {
        //TODO: added a temporary value. Exact value will be depends how many accounts are signed in. Length will be taken from databse entries.
        return 10
    }

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        internal var accountAvatar : ImageView? = null
        internal var userName: TextView? = null
        internal var userFullName: TextView? = null

        init {
            accountAvatar!!.findViewById(R.id.account_avatar) as ImageView
            userName!!.findViewById(R.id.account_username) as TextView
            userFullName!!.findViewById(R.id.account_name) as TextView
        }
    }
}