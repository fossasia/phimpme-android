package org.fossasia.phimpme.accounts;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.fossasia.phimpme.R;
import org.fossasia.phimpme.data.local.AccountDatabase;
import org.fossasia.phimpme.gallery.util.ThemeHelper;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmQuery;

import static org.fossasia.phimpme.data.local.AccountDatabase.HIDEINACCOUNTS;
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

    public AccountAdapter() {
        themeHelper = new ThemeHelper(getContext());
        updateTheme();
        this.switchAccentColor = themeHelper.getAccentColor();
        this.switchBackgroundColor = themeHelper.getPrimaryColor();
    }

    public void updateTheme() {
        themeHelper.updateTheme();
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
       // themeHelper.updateSwitchColor(holder.signInSignOutSwitch, switchBackgroundColor);
        String name = AccountDatabase.AccountName.values()[position].toString();
        if (realmResult.equalTo("name", name).count() > 0){
            holder.accountName.setText(realmResult
                    .equalTo("name", name).findAll()
                    .first().getUsername());
            holder.signInSignOutSwitch.setChecked(true);
            themeHelper.updateSwitchColor(holder.signInSignOutSwitch, switchAccentColor);
        } else {
            holder.accountName.setText(name);
        }

        Integer id = getContext().getResources().getIdentifier(context.getString(R.string.ic_) +
                        (name.toLowerCase()) + "_black"
                , context.getString(R.string.drawable)
                , getContext().getPackageName());

        holder.accountAvatar.setImageResource(id);

        id = getContext().getResources().getIdentifier((name.toLowerCase()) + "_color"
                , context.getString(R.string.color)
                , getContext().getPackageName());

        if (themeHelper.getBaseTheme() == ThemeHelper.LIGHT_THEME){

            holder.accountName.setTextColor(ContextCompat.getColor(getContext(), id));
            holder.accountAvatar.setColorFilter(ContextCompat.getColor(getContext(), id));

        } else {

            id = getContext().getResources().getIdentifier((name.toLowerCase()) + "_color_darktheme"
                    , context.getString(R.string.color)
                    , getContext().getPackageName());

            holder.accountName.setTextColor(ContextCompat.getColor(getContext(), id));
            holder.accountAvatar.setColorFilter(ContextCompat.getColor(getContext(), id));
        }

        holder.cardView.setCardBackgroundColor(themeHelper.getCardBackgroundColor());
    }

    @Override
    public int getItemCount() {
        /**
         * We are using the enum from the AccountDatabase model class, (-HIDEINACCOUNTS) from the length because
         * whatsapp, Instagram , googleplus and others option is only required in the Sharing activity.
         */
        //return AccountDatabase.AccountName.values().length - HIDEINACCOUNTS;
        return AccountDatabase.AccountName.values().length - HIDEINACCOUNTS;
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

        @BindView(R.id.card_view)
        CardView cardView;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
