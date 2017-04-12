package vn.mbm.phimp.me.wordpress;

import android.net.Uri;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import org.wordpress.android.fluxc.model.MediaModel;
import org.wordpress.android.util.MediaUtils;

import vn.mbm.phimp.me.R;

public class WordPressMediaUtils {

    public static int getPlaceholder(String url) {
        if (MediaUtils.isValidImage(url)) {
            return R.drawable.media_image_placeholder;
        } else {
            return 0;
        }
    }

    public static boolean canDeleteMedia(MediaModel mediaModel) {
        String state = mediaModel.getUploadState();
        return state == null || (!state.equalsIgnoreCase("uploading") && !state.equalsIgnoreCase("deleted"));
    }

    /**
     * Loads the given network image URL into the {@link NetworkImageView}.
     */
    public static void loadNetworkImage(String imageUrl, NetworkImageView imageView, ImageLoader imageLoader) {
        if (imageUrl != null) {
            Uri uri = Uri.parse(imageUrl);
            String filepath = uri.getLastPathSegment();

            int placeholderResId = WordPressMediaUtils.getPlaceholder(filepath);
            imageView.setErrorImageResId(placeholderResId);

            // default image while downloading
            imageView.setDefaultImageResId(R.drawable.media_item_background);

            if (MediaUtils.isValidImage(filepath)) {
                imageView.setTag(imageUrl);
                imageView.setImageUrl(imageUrl, imageLoader);
            } else {
                imageView.setImageResource(placeholderResId);
            }
        } else {
            imageView.setImageResource(0);
        }
    }
}
