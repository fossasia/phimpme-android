package org.fossasia.phimpme.accounts;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.fossasia.phimpme.R;
import org.fossasia.phimpme.data.local.AccountDatabase;
import org.fossasia.phimpme.leafpic.util.ThemeHelper;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmQuery;

import static org.fossasia.phimpme.accounts.AccountActivity.accountName;
import static org.fossasia.phimpme.utilities.ActivitySwitchHelper.context;
import static org.fossasia.phimpme.utilities.ActivitySwitchHelper.getContext;

/**
 * Created by pa1pal on 7/7/17.
 */

public class AccountAdapter extends RecyclerView.Adapter<AccountAdapter.ViewHolder> {
    private Realm realm = Realm.getDefaultInstance();
    private RealmQuery<AccountDatabase> realmResult = realm.where(AccountDatabase.class);
    public int switchAccentColor;
    public int switchBackgroundColor;
    private ThemeHelper themeHelper;

    public AccountAdapter(int switchColor, int switchBackgroundColor) {
        this.switchAccentColor = switchColor;
        this.switchBackgroundColor = switchBackgroundColor;
        themeHelper = new ThemeHelper(getContext());
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.accounts_item_view, null, false);
        view.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT,
                RecyclerView.LayoutParams.WRAP_CONTENT));
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        realmResult = realm.where(AccountDatabase.class);
        themeHelper.updateSwitchColor(holder.signInSignOutSwitch, switchBackgroundColor);
        if (realmResult.equalTo("name", accountName[position]).count() > 0){
            holder.accountName.setText(realmResult
                    .equalTo("name", accountName[position]).findAll()
                    .first().getUsername());
            holder.signInSignOutSwitch.setChecked(true);
            themeHelper.updateSwitchColor(holder.signInSignOutSwitch, switchAccentColor);
        } else {
            holder.accountName.setText(accountName[position]);
        }

        Integer id = getContext().getResources().getIdentifier(context.getString(R.string.ic_) +
                        (accountName[position].toLowerCase()) + "_black"
                , context.getString(R.string.drawable)
                , getContext().getPackageName());

        holder.accountAvatar.setImageResource(id);
    }

    @Override
    public int getItemCount() {
        return accountName.length;
    }

    public void setResults(RealmQuery<AccountDatabase> results) {
        realmResult = results;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.account_avatar)
        ImageView accountAvatar;

        @BindView(R.id.account_logo_indicator)
        ImageView accountLogoIndicator;

        @BindView(R.id.account_username)
        TextView accountName;

        @BindView(R.id.sign_in_sign_out_switch)
        SwitchCompat signInSignOutSwitch;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
