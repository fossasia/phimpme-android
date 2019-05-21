package org.fossasia.phimpme.share;

import static org.fossasia.phimpme.utilities.ActivitySwitchHelper.context;
import static org.fossasia.phimpme.utilities.ActivitySwitchHelper.getContext;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import java.util.ArrayList;
import org.fossasia.phimpme.R;
import org.fossasia.phimpme.data.local.AccountDatabase;
import org.fossasia.phimpme.gallery.util.ThemeHelper;
import org.fossasia.phimpme.utilities.Utils;

/** Created by pa1pal on 24/07/17. */
public class ShareAdapter extends RecyclerView.Adapter<ShareAdapter.ViewHolder> {

  private ThemeHelper themeHelper = new ThemeHelper(getContext());
  private ArrayList<AccountDatabase.AccountName> sharableAccountsList = new ArrayList<>();

  @Override
  public ShareAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view =
        LayoutInflater.from(parent.getContext()).inflate(R.layout.share_item_view, null, false);
    view.setLayoutParams(
        new RecyclerView.LayoutParams(
            RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));
    sharableAccountsList = Utils.getSharableAccountsList();
    return new ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(ViewHolder holder, int position) {
    Integer id;
    String name = sharableAccountsList.get(position).toString();
    holder.accountName.setText(name);

    id =
        getContext()
            .getResources()
            .getIdentifier(
                context.getString(R.string.ic_) + (name.toLowerCase()) + "_black",
                context.getString(R.string.drawable),
                getContext().getPackageName());

    holder.accountImage.setImageResource(id);

    id =
        getContext()
            .getResources()
            .getIdentifier(
                (name.toLowerCase()) + "_color",
                context.getString(R.string.color),
                getContext().getPackageName());

    if (themeHelper.getBaseTheme() == ThemeHelper.LIGHT_THEME) {

      holder.accountName.setTextColor(ContextCompat.getColor(getContext(), id));
      holder.accountImage.setColorFilter(ContextCompat.getColor(getContext(), id));

    } else {

      id =
          getContext()
              .getResources()
              .getIdentifier(
                  (name.toLowerCase()) + "_color_darktheme",
                  context.getString(R.string.color),
                  getContext().getPackageName());

      holder.accountName.setTextColor(ContextCompat.getColor(getContext(), id));
      holder.accountImage.setColorFilter(ContextCompat.getColor(getContext(), id));
    }
  }

  @Override
  public int getItemCount() {
    return Utils.getSharableAccountsList().size();
  }

  public static class ViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.account_image)
    ImageView accountImage;

    @BindView(R.id.account_name)
    TextView accountName;

    public ViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }
  }
}
