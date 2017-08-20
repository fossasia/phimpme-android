package org.fossasia.phimpme.uploadhistory;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.fossasia.phimpme.R;
import org.fossasia.phimpme.data.local.UploadHistoryRealmModel;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmQuery;

/**
 * Created by pa1pal on 17/08/17.
 */

public class UploadHistoryAdapter extends RecyclerView.Adapter<UploadHistoryAdapter.ViewHolder> {

    private Realm realm = Realm.getDefaultInstance();
    private RealmQuery<UploadHistoryRealmModel> realmResult = realm.where(UploadHistoryRealmModel.class);

    @Override
    public UploadHistoryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.upload_history_item_view, null, false);
        view.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT,
                RecyclerView.LayoutParams.WRAP_CONTENT));
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        realmResult = realm.where(UploadHistoryRealmModel.class);
        // themeHelper.updateSwitchColor(holder.signInSignOutSwitch, switchBackgroundColor);

        if (realmResult.findAll().size() != 0) {
            holder.uploadAccountName.setText(realmResult.findAll().get(position).getName());
            holder.uploadTime.setText(realmResult.findAll().get(position).getDatetime());
        }
    }

    @Override
    public int getItemCount() {
        return (int) realmResult.count();
    }

    public void setResults(RealmQuery<UploadHistoryRealmModel> results) {
        realmResult = results;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.upload_account_name)
        TextView uploadAccountName;

        @BindView(R.id.upload_time)
        TextView uploadTime;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
