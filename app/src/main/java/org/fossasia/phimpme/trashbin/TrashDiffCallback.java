package org.fossasia.phimpme.trashbin;

import java.util.List;

import org.fossasia.phimpme.data.local.TrashBinRealmModel;

import android.support.v7.util.DiffUtil;

public class TrashDiffCallback extends DiffUtil.Callback {

    private final List<TrashBinRealmModel> oldTrashList;
    private final List<TrashBinRealmModel> newTrashList;

    public TrashDiffCallback(List<TrashBinRealmModel> oldTrashList,
                             List<TrashBinRealmModel> newTrashList) {
        this.oldTrashList = oldTrashList;
        this.newTrashList = newTrashList;
    }

    @Override public int getOldListSize() {
        return oldTrashList.size();
    }

    @Override public int getNewListSize() {
        return newTrashList.size();
    }

    @Override public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return oldTrashList.get(oldItemPosition).getTrashbinpath() == newTrashList.get(
                newItemPosition).getTrashbinpath();
    }

    @Override public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        final TrashBinRealmModel oldEmployee = oldTrashList.get(oldItemPosition);
        final TrashBinRealmModel newEmployee = newTrashList.get(newItemPosition);

        return oldEmployee.getTrashbinpath().equals(newEmployee.getTrashbinpath());
    }
}
