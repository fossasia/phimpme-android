package org.fossasia.phimpme.trashbin;

import static com.facebook.FacebookSdk.getApplicationContext;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.fossasia.phimpme.R;
import org.fossasia.phimpme.data.local.TrashBinRealmModel;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import android.net.Uri;
import android.os.Environment;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;

public class TrashBinAdapter extends RecyclerView.Adapter<TrashBinAdapter.ViewHolder> {

    private ArrayList<TrashBinRealmModel> trashItemsList = null;

    public TrashBinAdapter(ArrayList<TrashBinRealmModel> list) {
        trashItemsList = list;
    }

    @Override public TrashBinAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.trashbin_item_view, null, false);
        view.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT,
                RecyclerView.LayoutParams.WRAP_CONTENT));
        //view.setOnClickListener(onClickListener);
        return new TrashBinAdapter.ViewHolder(view);
    }

    @Override public void onBindViewHolder(final TrashBinAdapter.ViewHolder holder, final int position) {
        if(trashItemsList.size() != 0){
            final TrashBinRealmModel trashBinRealmModel = trashItemsList.get(position);
            String date = trashBinRealmModel.getDatetime();
            try {
                DateFormat format = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
                Date parsedDate = format.parse(date);
                DateFormat uploadDate = new SimpleDateFormat("dd/MM/yyyy");
                DateFormat uploadTime = new SimpleDateFormat("hh:mm:ss");
                holder.deleteDate.setText(uploadDate.format(parsedDate));
                holder.deleteTime.setText(uploadTime.format(parsedDate));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            holder.deleteDate.setTag(trashBinRealmModel);
            Uri uri = Uri.fromFile(new File(trashBinRealmModel.getTrashbinpath()));
            Glide.with(getApplicationContext()).load(uri)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.deletedImage);
            holder.popupMenuButton.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View view) {
                    PopupMenu menu = new PopupMenu(getApplicationContext(), holder.popupMenuButton);
                    menu.inflate(R.menu.menu_popup_trashbin);
                    menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override public boolean onMenuItemClick(MenuItem menuItem) {
                            switch (menuItem.getItemId()){

                                case R.id.restore_option:
                                    return true;

                                case R.id.delete_permanently:
                                    if(deletePermanent(trashBinRealmModel)){
                                        deleteFromRealm(trashItemsList.get(position).getTrashbinpath());
                                        trashItemsList.remove(position);
                                        notifyItemRemoved(position);
                                        notifyItemRangeChanged(position, trashItemsList.size());
                                    }
                                    return true;

                                default:
                                    return false;

                            }
                        }
                    });
                    menu.show();
                }
            });
        }
    }

    private void deleteFromRealm(final String path){
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override public void execute(Realm realm) {
                RealmResults<TrashBinRealmModel> trashBinRealmModels = realm.where(TrashBinRealmModel.class).equalTo
                        ("trashbinpath", path).findAll();
                trashBinRealmModels.deleteAllFromRealm();
            }
        });
    }

    private boolean deletePermanent(TrashBinRealmModel trashBinRealmModel){
        boolean succ = false;
        String path = trashBinRealmModel.getTrashbinpath();
        File file = new File(Environment.getExternalStorageDirectory() + "/" + ".nomedia");
        //File file = new File(Environment.getExternalStorageDirectory() + "/" + "TrashBin");
        if(file.exists()){
            File file1 = new File(path);
            if(file1.exists()){
                succ = file1.delete();
            }
        }
        return succ;
    }

    public void updateTrashListItems(List<TrashBinRealmModel> trashList) {
        final TrashDiffCallback diffCallback = new TrashDiffCallback(this.trashItemsList, trashList);
        final DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);
        this.trashItemsList.clear();
        this.trashItemsList.addAll(trashList);
        diffResult.dispatchUpdatesTo(this);
    }

    @Override public int getItemCount() {
        return trashItemsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.delete_time)
        public TextView deleteTime;

        @BindView(R.id.delete_date)
        public TextView deleteDate;

        @BindView(R.id.trashbin_image)
        public ImageView deletedImage;

        @BindView(R.id.textViewOptions)
        public TextView popupMenuButton;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}