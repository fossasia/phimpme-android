package org.fossasia.phimpme.gallery.data.providers;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.Nullable;

import org.fossasia.phimpme.gallery.data.Album;
import org.fossasia.phimpme.gallery.data.CustomAlbumsHelper;
import org.fossasia.phimpme.gallery.data.Media;
import org.fossasia.phimpme.gallery.data.base.ImageFileFilter;
import org.fossasia.phimpme.gallery.util.ContentHelper;
import org.fossasia.phimpme.gallery.util.StringUtils;
import org.jetbrains.annotations.TestOnly;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by dnld on 24/07/16.
 */

public class  MediaStoreProvider {


	private static ArrayList<String> excludedAlbums;

	public static ArrayList<Album> getAlbums(Context context, boolean hidden) {
		excludedAlbums = getExcludedFolders(context);
		return hidden ? getHiddenAlbums(context) : getAlbums(context);
	}

	private static   ArrayList<Album> getHiddenAlbums(Context context) {
		ArrayList<Album> list = new ArrayList<Album>();
		String[] projection = new String[]{ MediaStore.Files.FileColumns.DATA, MediaStore.Files.FileColumns.PARENT };
		String selection = MediaStore.Files.FileColumns.MEDIA_TYPE+"="+MediaStore.Files.FileColumns.MEDIA_TYPE_NONE+" and "+
										   MediaStore.Files.FileColumns.DATA +" LIKE '%.nomedia'";
		Cursor cur = context.getContentResolver().query(MediaStore.Files.getContentUri("external"), projection, selection, null, null);
		if(cur != null && cur.moveToFirst()) {
			do {
				File folder = new File(cur.getString(0)).getParentFile();
				File[] files = folder.listFiles(new ImageFileFilter(true));
				if (files != null && files.length > 0) {
					Album album = new Album(context, folder.getAbsolutePath(), -1, folder.getName(), files.length);

					// TODO: 21/08/16 sort and find?
					long lastMod = Long.MIN_VALUE;
					File f = null;
					for(File file : files) {
						if (file.lastModified() > lastMod) {
							f = file;
							lastMod = file.lastModified();
						}
					}
					if (f != null && !isExcluded(f.getPath(), context)) {
						album.addMedia(new Media(f.getPath(), f.lastModified()));
						list.add(album);
					}
				}
			} while(cur.moveToNext());
			cur.close();
		}
		return list;
	}

	private static boolean isExcluded(String path, Context context) {
		if(excludedAlbums == null){
			excludedAlbums = getExcludedFolders(context);
		}
		for(String s : excludedAlbums) if (path.startsWith(s)) return true;
		return false;
	}

	public static ArrayList<Album> getAlbums(Context context) {
		ArrayList<Album> list = new ArrayList<Album>();

		String[] projection = new String[]{
						MediaStore.Files.FileColumns.PARENT,
						MediaStore.Images.Media.BUCKET_DISPLAY_NAME
		};

		String selection, selectionArgs[];

			selection = MediaStore.Files.FileColumns.MEDIA_TYPE + "=? ) GROUP BY ( " + MediaStore.Files.FileColumns.PARENT + " ";
			selectionArgs = new String[] { String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE) };

		Cursor cur = context.getContentResolver().query(
						MediaStore.Files.getContentUri("external"), projection, selection, selectionArgs, null);

		if (cur != null) {
			if (cur.moveToFirst()) {
				int idColumn = cur.getColumnIndex(MediaStore.Files.FileColumns.PARENT);
				int nameColumn = cur.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
				do {
					Media media = getLastMedia(context, cur.getLong(idColumn));
					if (media != null && media.getPath() != null) {
						String path = StringUtils.getBucketPathByImagePath(media.getPath());
						boolean excluded = isExcluded( path, context);
						if (!excluded) {
							Album album = new Album(context, path, cur.getLong(idColumn), cur.getString(nameColumn), getAlbumCount(context, cur.getLong(idColumn)));
							if (album.addMedia(getLastMedia(context, album.getId()))) list.add(album);
						}
					}
				}
				while (cur.moveToNext());
			}
			cur.close();
		}
		return list;
	}

	private  static ArrayList<String> getExcludedFolders(Context context) {
		ArrayList<String>  list = new ArrayList<String>();
		//forced excluded folder
		HashSet<File> storageRoots = ContentHelper.getStorageRoots(context);
		for(File file : storageRoots) {
			list.add(new File(file.getPath(), "Android").getPath());
		}

		CustomAlbumsHelper handler = CustomAlbumsHelper.getInstance(context);
		list.addAll(handler.getExcludedFoldersPaths());
		return list;
	}

	private static int getAlbumCount(Context context, long id) {
		int c = 0;
		String selection = "( " + MediaStore.Files.FileColumns.MEDIA_TYPE + "=? ) and " + MediaStore.Files.FileColumns.PARENT + "=?";

		String[] selectionArgs = new String[] {
						String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE),
						String.valueOf(id)
		};

		Cursor cur = context.getContentResolver().query(MediaStore.Files.getContentUri("external"),
						new String[]{ MediaStore.Files.FileColumns.PARENT }, selection, selectionArgs, null);

		if (cur != null) {
			c = cur.getCount();
			cur.close();
		}
		return c;
	}

	@Nullable private static Media getLastMedia(Context context, long albumId) {
		ArrayList<Media> list = getMedia(context, albumId, 1);
		return list.size() > 0 ? list.get(0) : null;
	}

	public static ArrayList<Media> getMedia(Context context, long albumId) {
		return getMedia(context, albumId, -1);
	}

	private static ArrayList<Media> getMedia(Context context, long albumId, int n) {

		String limit = n == -1 ? "" : "LIMIT " + n;
		ArrayList<Media> list = new ArrayList<Media>();


		String[] projection = new String[]{
						// NOTE: don't change the order!
						MediaStore.Images.Media.DATA,
						MediaStore.Images.Media.DATE_TAKEN,
						MediaStore.Images.Media.MIME_TYPE,
						MediaStore.Images.Media.SIZE,
						MediaStore.Images.Media.ORIENTATION
		};

		Uri images = MediaStore.Files.getContentUri("external");
		String selection, selectionArgs[];

			selection = MediaStore.Files.FileColumns.MEDIA_TYPE + "=?  and " + MediaStore.Files.FileColumns.PARENT + "=?";
			selectionArgs = new String[] { String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE), String.valueOf(albumId) };

		Cursor cur = context.getContentResolver().query(
						images, projection, selection, selectionArgs,
						" " + MediaStore.Images.Media.DATE_TAKEN + " DESC " + limit);

		if (cur != null) {
			if (cur.moveToFirst()) do list.add(new Media(cur)); while (cur.moveToNext());
			cur.close();
		}
		return list;
	}

	public static long getAlbumId(Context context, String mediaPath) {
		long id = -1;
		Cursor cur = context.getContentResolver().query(MediaStore.Files.getContentUri("external"),
						new String[]{ MediaStore.Files.FileColumns.PARENT },
						MediaStore.Files.FileColumns.DATA+"=?", new String[]{ mediaPath }, null);
		if(cur != null && cur.moveToNext()){
			id = cur.getLong(0);
			cur.close();
		}
		return id;
	}

	@TestOnly
	private String getThumbnailPath(Context context, long id) {
		Cursor cursor = MediaStore.Images.Thumbnails.queryMiniThumbnail(
						context.getContentResolver(), id, MediaStore.Images.Thumbnails.MINI_KIND,
						new String[]{ MediaStore.Images.Thumbnails.DATA });
		if(cursor.moveToFirst())
			return cursor.getString(cursor.getColumnIndex(MediaStore.Images.Thumbnails.DATA));
		return null;
	}
}


