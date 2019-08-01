package org.fossasia.phimpme.uploadhistory;

import static org.fossasia.phimpme.utilities.ActivitySwitchHelper.context;
import static org.fossasia.phimpme.utilities.ActivitySwitchHelper.getContext;

import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import org.fossasia.phimpme.R;
import org.fossasia.phimpme.data.local.UploadHistoryRealmModel;
import org.fossasia.phimpme.gallery.util.ThemeHelper;

/** Created by pa1pal on 17/08/17. */
public class UploadHistoryAdapter extends RecyclerView.Adapter<UploadHistoryAdapter.ViewHolder> {

  private ArrayList<UploadHistoryRealmModel> realmResult = new ArrayList<>();
  private int color;
  private View.OnClickListener onClickListener;
  public String imagePath;
  private ThemeHelper theme;

  public UploadHistoryAdapter(int color) {
    this.color = color;
    theme = new ThemeHelper(context);
  }

  @Override
  public UploadHistoryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view =
        LayoutInflater.from(parent.getContext())
            .inflate(R.layout.upload_history_item_view, null, false);
    view.setLayoutParams(
        new RecyclerView.LayoutParams(
            RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));
    view.setOnClickListener(onClickListener);
    return new ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(ViewHolder holder, int position) {
    Integer id;
    if (realmResult.size() != 0) {
      UploadHistoryRealmModel uploadHistoryRealmModel = realmResult.get(position);
      String date = uploadHistoryRealmModel.getDatetime();
      String name = uploadHistoryRealmModel.getName();
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

      holder.uploadData.setBackgroundColor(theme.getBackgroundColor());
      holder.date.setTextColor(theme.getTextColor());
      holder.time.setTextColor(theme.getTextColor());

      Uri uri = Uri.fromFile(new File(uploadHistoryRealmModel.getPathname()));
      imagePath = uploadHistoryRealmModel.getPathname();
      holder.uploadTime.setTag(uploadHistoryRealmModel);

      Glide.with(holder.uploadImage.getContext())
          .load(uri)
          .diskCacheStrategy(DiskCacheStrategy.ALL)
          .into(holder.uploadImage);

      id =
          getContext()
              .getResources()
              .getIdentifier(
                  context.getString(R.string.ic_) + (name.toLowerCase()) + "_black",
                  context.getString(R.string.drawable),
                  getContext().getPackageName());

      holder.accountImageShare.setImageResource(id);

      id =
          getContext()
              .getResources()
              .getIdentifier(
                  (name.toLowerCase()) + "_color",
                  context.getString(R.string.color),
                  getContext().getPackageName());

      holder.uploadDate.setTextColor(theme.getTextColor());
      holder.uploadTime.setTextColor(theme.getTextColor());

      holder.accountImageShare.setColorFilter(ContextCompat.getColor(getContext(), id));
    }
  }

  @Override
  public int getItemCount() {
    return realmResult.size();
  }

  public void setResults(ArrayList<UploadHistoryRealmModel> realmResult) {
    this.realmResult = realmResult;
    notifyDataSetChanged();
  }

  public void setOnClickListener(View.OnClickListener lis) {
    onClickListener = lis;
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

    @BindView(R.id.upload_data)
    LinearLayout uploadData;

    @BindView(R.id.time)
    TextView time;

    @BindView(R.id.date)
    TextView date;

    public ViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }
  }
}
