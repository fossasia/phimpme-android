package org.fossasia.phimpme.uploadhistory;

import android.support.v7.util.DiffUtil;
import java.util.List;
import org.fossasia.phimpme.data.local.UploadHistoryRealmModel;

/** Created by saurav on 21/7/18. */
public class UploadHisDiffCallback extends DiffUtil.Callback {

  private final List<UploadHistoryRealmModel> oldUploadHisList;
  private final List<UploadHistoryRealmModel> newUploadHisList;

  public UploadHisDiffCallback(
      List<UploadHistoryRealmModel> oldUploadHisList,
      List<UploadHistoryRealmModel> newUploadHisList) {
    this.oldUploadHisList = oldUploadHisList;
    this.newUploadHisList = newUploadHisList;
  }

  @Override
  public int getOldListSize() {
    return oldUploadHisList.size();
  }

  @Override
  public int getNewListSize() {
    return newUploadHisList.size();
  }

  @Override
  public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
    return oldUploadHisList.get(oldItemPosition).getPathname()
        == newUploadHisList.get(newItemPosition).getPathname();
  }

  @Override
  public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {

    final UploadHistoryRealmModel oldItem = oldUploadHisList.get(oldItemPosition);
    final UploadHistoryRealmModel newItem = newUploadHisList.get(newItemPosition);

    return oldItem.getPathname().equals(newItem.getPathname());
  }
}
