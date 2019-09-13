package org.fossasia.phimpme.trashbin;

import static org.fossasia.phimpme.utilities.ActivitySwitchHelper.context;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import io.realm.Realm;
import io.realm.RealmResults;
import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import org.fossasia.phimpme.R;
import org.fossasia.phimpme.data.local.TrashBinRealmModel;
import org.fossasia.phimpme.gallery.util.ContentHelper;
import org.fossasia.phimpme.gallery.util.StringUtils;
import org.fossasia.phimpme.gallery.util.ThemeHelper;
import org.fossasia.phimpme.utilities.BasicCallBack;

public class TrashBinAdapter extends RecyclerView.Adapter<TrashBinAdapter.ViewHolder> {

  private ArrayList<TrashBinRealmModel> trashItemsList = null;
  private View.OnClickListener onClickListener;
  private BasicCallBack basicCallBack;
  private OnDeleteClickListener onDeleteClickListener;
  private ThemeHelper theme;

  public TrashBinAdapter(ArrayList<TrashBinRealmModel> list, BasicCallBack basicCallBack) {
    trashItemsList = list;
    this.basicCallBack = basicCallBack;
    theme = new ThemeHelper(context);
  }

  public interface OnDeleteClickListener {
    void onDelete(int position);
  }

  @Override
  public TrashBinAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view =
        LayoutInflater.from(parent.getContext()).inflate(R.layout.trashbin_item_view, null, false);
    view.setLayoutParams(
        new RecyclerView.LayoutParams(
            RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));
    view.setOnClickListener(onClickListener);
    return new TrashBinAdapter.ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(final TrashBinAdapter.ViewHolder holder, final int position) {
    if (trashItemsList.size() != 0) {
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
      Glide.with(holder.deletedImage.getContext())
          .load(uri)
          .diskCacheStrategy(DiskCacheStrategy.ALL)
          .into(holder.deletedImage);
      holder.popupMenuButton.setOnClickListener(
          new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              PopupMenu menu = new PopupMenu(context, holder.popupMenuButton);
              menu.inflate(R.menu.menu_popup_trashbin);
              menu.setOnMenuItemClickListener(
                  new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                      switch (menuItem.getItemId()) {
                        case R.id.restore_option:
                          restoreImage(trashBinRealmModel, position);
                          if (trashItemsList.size() == 0) {
                            basicCallBack.callBack(2, null);
                          }
                          return true;

                        case R.id.delete_permanently:
                          if (onDeleteClickListener != null)
                            onDeleteClickListener.onDelete(position);
                          return true;

                        default:
                          return false;
                      }
                    }
                  });
              menu.show();
            }
          });
      holder.popupMenuButton.setTextColor(theme.getTextColor());
      holder.date.setTextColor(theme.getTextColor());
      holder.deleteDate.setTextColor(theme.getTextColor());
      holder.deleteTime.setTextColor(theme.getTextColor());
      holder.time.setTextColor(theme.getTextColor());
      holder.deleteDetails.setBackgroundColor(theme.getBackgroundColor());
    }
  }

  public void updateDeleteContent(int position) {
    final TrashBinRealmModel trashBinRealmModel = trashItemsList.get(position);
    if (deletePermanent(trashBinRealmModel)) {
      deleteFromRealm(trashItemsList.get(position).getTrashbinpath());
      trashItemsList.remove(position);
      notifyItemRemoved(position);
      notifyItemRangeChanged(position, trashItemsList.size());
    }
    if (trashItemsList.size() == 0) {
      basicCallBack.callBack(2, null);
    }
  }

  private void restoreImage(TrashBinRealmModel trashBinRealmModel, int pos) {
    String oldpath = trashBinRealmModel.getOldpath();
    String oldFolder = oldpath.substring(0, oldpath.lastIndexOf("/"));
    if (restoreMove(context, trashBinRealmModel.getTrashbinpath(), oldFolder)) {
      scanFile(
          context,
          new String[] {
            trashBinRealmModel.getTrashbinpath(),
            StringUtils.getPhotoPathMoved(trashBinRealmModel.getTrashbinpath(), oldFolder)
          });
      if (removeFromRealm(trashBinRealmModel.getTrashbinpath())) {
        trashItemsList.remove(pos);
        notifyItemRemoved(pos);
        notifyItemRangeChanged(pos, trashItemsList.size());
      }
    }
  }

  private boolean restoreMove(Context context, String source, String targetDir) {
    File from = new File(source);
    File to = new File(targetDir);
    return ContentHelper.moveFile(context, from, to);
  }

  private boolean removeFromRealm(final String path) {
    final boolean[] delete = {false};
    Realm realm = Realm.getDefaultInstance();
    realm.executeTransaction(
        new Realm.Transaction() {
          @Override
          public void execute(Realm realm) {
            RealmResults<TrashBinRealmModel> result =
                realm.where(TrashBinRealmModel.class).equalTo("trashbinpath", path).findAll();
            delete[0] = result.deleteAllFromRealm();
          }
        });
    return delete[0];
  }

  public void scanFile(Context context, String[] path) {
    MediaScannerConnection.scanFile(context, path, null, null);
  }

  private void deleteFromRealm(final String path) {
    Realm realm = Realm.getDefaultInstance();
    realm.executeTransaction(
        new Realm.Transaction() {
          @Override
          public void execute(Realm realm) {
            RealmResults<TrashBinRealmModel> trashBinRealmModels =
                realm.where(TrashBinRealmModel.class).equalTo("trashbinpath", path).findAll();
            trashBinRealmModels.deleteAllFromRealm();
          }
        });
  }

  public void setOnDeleteClickListener(OnDeleteClickListener onDeleteClickListener) {
    this.onDeleteClickListener = onDeleteClickListener;
  }

  private boolean deletePermanent(TrashBinRealmModel trashBinRealmModel) {
    boolean succ = false;
    String path = trashBinRealmModel.getTrashbinpath();
    File file = new File(Environment.getExternalStorageDirectory() + "/" + ".nomedia");
    // File file = new File(Environment.getExternalStorageDirectory() + "/" + "TrashBin");
    if (file.exists()) {
      File file1 = new File(path);
      if (file1.exists()) {
        succ = file1.delete();
      }
    }
    return succ;
  }

  public void setResults(ArrayList<TrashBinRealmModel> trashItemsList) {
    this.trashItemsList = trashItemsList;
    notifyDataSetChanged();
  }

  public void setOnClickListener(View.OnClickListener lis) {
    onClickListener = lis;
  }

  @Override
  public int getItemCount() {
    return trashItemsList.size();
  }

  public static class ViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.delete_time)
    public TextView deleteTime;

    @BindView(R.id.delete_date)
    public TextView deleteDate;

    @BindView(R.id.trashbin_image)
    public ImageView deletedImage;

    @BindView(R.id.textViewOptions)
    public TextView popupMenuButton;

    @BindView(R.id.date)
    public TextView date;

    @BindView(R.id.time)
    public TextView time;

    @BindView(R.id.delete_details)
    public LinearLayout deleteDetails;

    public ViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }
  }
}
