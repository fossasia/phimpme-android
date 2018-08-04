package org.fossasia.phimpme.gallery.data.providers;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import org.fossasia.phimpme.gallery.data.CustomAlbumsHelper;
import org.fossasia.phimpme.gallery.data.Album;
import org.fossasia.phimpme.gallery.data.Media;
import org.fossasia.phimpme.gallery.data.base.FoldersFileFilter;
import org.fossasia.phimpme.gallery.data.base.ImageFileFilter;
import org.fossasia.phimpme.gallery.util.ContentHelper;
import org.fossasia.phimpme.gallery.util.PreferenceUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by weconstudio on 07/07/16.
 */
public class StorageProvider {

    private ArrayList<File> excludedFolders;
    private boolean includeVideo = false;
    private PreferenceUtil SP;

    public StorageProvider(Context context) {
        SP = PreferenceUtil.getInstance(context);
        excludedFolders = getExcludedFolders(context);
    }

    public ArrayList<Album> getAlbums(Context context, boolean hidden) {
        ArrayList<Album> list = new ArrayList<Album>();
        includeVideo = SP.getBoolean("set_include_video", false);
        if (hidden)
            for (File storage : ContentHelper.getStorageRoots(context))
                fetchRecursivelyHiddenFolder(context, storage, list);
        else
            for (File storage : ContentHelper.getStorageRoots(context))
                fetchRecursivelyFolder(context, storage, list);
        return list;
    }

    private ArrayList<File> getExcludedFolders(Context context) {
        ArrayList<File>  list = new ArrayList<File>();
        //forced excluded folder
        HashSet<File> storageRoots = ContentHelper.getStorageRoots(context);
        for(File file : storageRoots) {
            list.add(new File(file.getPath(), "Android"));
        }

        CustomAlbumsHelper handler = CustomAlbumsHelper.getInstance(context);
        list.addAll(handler.getExcludedFolders());
        return list;
    }

    private void fetchRecursivelyHiddenFolder(Context context, File dir, ArrayList<Album> albumArrayList) {
        if (!excludedFolders.contains(dir)) {
            File[] folders = dir.listFiles(new FoldersFileFilter());
            if (folders != null) {
                for (File temp : folders) {
                    File nomedia = new File(temp, ".nomedia");
                    if (!excludedFolders.contains(temp) && (nomedia.exists() || temp.isHidden()))
                        checkAndAddFolder(context, temp, albumArrayList);

                    fetchRecursivelyHiddenFolder(context, temp, albumArrayList);
                }
            }
        }
    }

    private void fetchRecursivelyFolder(Context context, File dir, ArrayList<Album> albumArrayList) {
        if (!excludedFolders.contains(dir)) {
            checkAndAddFolder(context, dir, albumArrayList);
            File[] children = dir.listFiles(new FoldersFileFilter());
            if (children != null) {
                for (File temp : children) {
                    File nomedia = new File(temp, ".nomedia");
                    if (!excludedFolders.contains(temp) && !temp.isHidden() && !nomedia.exists()) {
                        //not excluded/hidden folder
                        fetchRecursivelyFolder(context, temp, albumArrayList);
                    }
                }
            }
        }
    }

    private void checkAndAddFolder(Context context, File dir, ArrayList<Album> albumArrayList) {
        File[] files = dir.listFiles(new ImageFileFilter(includeVideo));
        if (files != null && files.length > 0) {
            //valid folder
            Album asd = new Album(context, dir.getAbsolutePath(), -1, dir.getName(), files.length);

            long lastMod = Long.MIN_VALUE;
            File choice = null;
            for (File file : files) {
                if (file.lastModified() > lastMod) {
                    choice = file;
                    lastMod = file.lastModified();
                }
            }
            if (choice != null)
                asd.addMedia( new Media(choice.getAbsolutePath(), choice.lastModified()));

            albumArrayList.add(asd);
        }
    }

    public static ArrayList<Media> getMedia(String path, boolean includeVideo) {
        ArrayList<Media> list = new ArrayList<Media>();
        File[] images = null;
        if (path != null)
            images = new File(path).listFiles(new ImageFileFilter(includeVideo));
        if (images != null)
            for (File image : images)
                list.add(new Media(image));
        return list;
    }

    public static ArrayList<Media> getAllShownImages(Activity activity) {
        Uri uri;
        Cursor cursor;
        int column_index;
        ArrayList<String> listOfAllImages = new ArrayList<String>();
        ArrayList<Media> list = new ArrayList<Media>();
        String absolutePathOfImage;
        uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {MediaStore.MediaColumns.DATA};

        cursor = activity.getContentResolver().query(uri, projection, null,
                null, null);

        // column_index = cursor.getColumnIndexOrThrow(MediaColumns.DATA);
        column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        while (cursor.moveToNext()) {
            absolutePathOfImage = cursor.getString(column_index);
            listOfAllImages.add(absolutePathOfImage);
        }
        for (String path : listOfAllImages) {
            list.add(new Media(new File(path)));
        }
        return list;
    }

}
