package org.fossasia.phimpme.uploadhistory;

import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.fossasia.phimpme.R;
import org.fossasia.phimpme.data.local.UploadHistoryRealmModel;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmQuery;

import static com.facebook.FacebookSdk.getApplicationContext;
import static org.fossasia.phimpme.utilities.ActivitySwitchHelper.context;
import static org.fossasia.phimpme.utilities.ActivitySwitchHelper.getContext;

/**
 * Created by pa1pal on 17/08/17.
 */

public class UploadHistoryAdapter extends RecyclerView.Adapter<UploadHistoryAdapter.ViewHolder> {

    private Realm realm = Realm.getDefaultInstance();
    private RealmQuery<UploadHistoryRealmModel> realmResult = realm.where(UploadHistoryRealmModel.class);
    private int color;

    public UploadHistoryAdapter(int color) {
        this.color=color;
    }

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
        Integer id;
        realmResult = realm.where(UploadHistoryRealmModel.class);

        if (realmResult.findAll().size() != 0) {

            String date = realmResult.findAll().get(position).getDatetime();
            String name=realmResult.findAll().get(position).getName();
            try {
                DateFormat format = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
                Date parsedDate = format.parse(date);
                DateFormat uploadDate = new SimpleDateFormat("dd/MM/yyyy");
                DateFormat uploadTime = new SimpleDateFormat("hh:mm:ss");
                holder.uploadDate.setText(uploadDate.format(parsedDate));
                holder.uploadTime.setText(uploadTime.format(parsedDate));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            Uri uri = Uri.fromFile(new File(realmResult.findAll().get(position).getPathname()));

            Glide.with(getApplicationContext()).load(uri)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.uploadImage);

            id = getContext().getResources().getIdentifier(context.getString(R.string.ic_) +
                            (name.toLowerCase()) + "_black", context.getString(R.string.drawable)
                    , getContext().getPackageName());

            holder.accountImageShare.setImageResource(id);

            id = getContext().getResources().getIdentifier((name.toLowerCase()) + "_color"
                    , context.getString(R.string.color)
                    , getContext().getPackageName());

            holder.accountImageShare.setColorFilter(ContextCompat.getColor(getContext(), id));
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

        @BindView(R.id.upload_time)
        TextView uploadTime;

        @BindView(R.id.upload_date)
        TextView uploadDate;

        @BindView(R.id.upload_image)
        ImageView uploadImage;

        @BindView(R.id.account_image_share)
        ImageView accountImageShare;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
