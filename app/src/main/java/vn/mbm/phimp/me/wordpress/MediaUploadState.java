package vn.mbm.phimp.me.wordpress;

import android.content.Context;

import vn.mbm.phimp.me.R;

/**
 * Created by rohanagarwal94 on 6/4/17.
 */

public enum MediaUploadState {
    QUEUED,
    UPLOADING,
    DELETING,
    DELETED,
    FAILED,
    UPLOADED;

    public static MediaUploadState fromString(String strState) {
        if (strState != null) {
            for (MediaUploadState state: MediaUploadState.values()) {
                if (strState.equalsIgnoreCase(state.name())) {
                    return state;
                }
            }
        }
        return UPLOADED;
    }

    public static String getLabel(Context context, MediaUploadState state) {
        switch (state) {
            case QUEUED:
                return context.getString(R.string.media_upload_state_queued);
            case UPLOADING:
                return context.getString(R.string.media_upload_state_uploading);
            case DELETING:
                return context.getString(R.string.media_upload_state_deleting);
            case DELETED:
                return context.getString(R.string.media_upload_state_deleted);
            case FAILED:
                return context.getString(R.string.media_upload_state_failed);
            case UPLOADED:
                return context.getString(R.string.media_upload_state_uploaded);
        }
        return "";
    }
}
