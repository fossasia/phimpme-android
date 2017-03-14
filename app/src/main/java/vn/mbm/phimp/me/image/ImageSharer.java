package vn.mbm.phimp.me.image;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import java.io.File;
import java.util.ArrayList;


public class ImageSharer {
    /**This function can be called to share single or multiple images via supported apps.
     * @param context
     * @param imagelist list of images to share separated by #.
     */
    public static void share(Context context, String imagelist) {
        String[] paths = imagelist.split("#");
        Intent share = new Intent(paths.length > 1 ? Intent.ACTION_SEND_MULTIPLE : Intent.ACTION_SEND);
        share.setType("image/*");
        ArrayList<Uri> files = new ArrayList<Uri>();

        for (String path : paths) {
            File imageFileToShare = new File(path);
            Uri imageURI = Uri.fromFile(imageFileToShare);
            files.add(imageURI);
        }
        share.putParcelableArrayListExtra(Intent.EXTRA_STREAM, files);
        share.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        context.startActivity(Intent.createChooser(share, "Share " + paths.length + " image(s)"));
    }
}
