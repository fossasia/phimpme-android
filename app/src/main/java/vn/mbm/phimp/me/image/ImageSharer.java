package vn.mbm.phimp.me.image;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by harshit on 07-03-2017.
 */

public class ImageSharer {
    public static void shareSingle(Context context, String filePath) {
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("image/*");
        File imageFileToShare = new File(filePath);
        Uri imageURI = Uri.fromFile(imageFileToShare);
//                FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", imageFileToShare);
        share.putExtra(Intent.EXTRA_STREAM, imageURI);
        share.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        context.startActivity(Intent.createChooser(share, "Share Image"));
    }

    public static void shareMultiple(Context context, String imagelist) {
        String[] paths = imagelist.split("#");
        Intent share = new Intent(Intent.ACTION_SEND_MULTIPLE);
        share.setType("image/*");
        ArrayList<Uri> files = new ArrayList<Uri>();

        for (String path : paths) {
            File imageFileToShare = new File(path);
            Uri imageURI = Uri.fromFile(imageFileToShare); //FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", imageFileToShare);
            files.add(imageURI);
        }
        share.putParcelableArrayListExtra(Intent.EXTRA_STREAM, files);
        share.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        context.startActivity(Intent.createChooser(share, "Share " + paths.length + " images"));

    }
}
