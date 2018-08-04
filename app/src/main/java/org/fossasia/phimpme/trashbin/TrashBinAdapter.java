package org.fossasia.phimpme.trashbin;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.fossasia.phimpme.R;
import org.fossasia.phimpme.data.local.TrashBinRealmModel;
import static org.fossasia.phimpme.utilities.ActivitySwitchHelper.context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import android.net.Uri;
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

    @Override public void onBindViewHolder(final TrashBinAdapter.ViewHolder holder, int position) {
        if(trashItemsList.size() != 0){
            TrashBinRealmModel trashBinRealmModel = trashItemsList.get(position);
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
            Glide.with(context).load(uri)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.deletedImage);
            holder.popupMenuButton.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View view) {
                    PopupMenu menu = new PopupMenu(context, holder.popupMenuButton);
                    menu.inflate(R.menu.menu_popup_trashbin);
                    menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override public boolean onMenuItemClick(MenuItem menuItem) {
                            switch (menuItem.getItemId()){

                                case R.id.restore_option:
                                    return true;

                                case R.id.delete_permanently:
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