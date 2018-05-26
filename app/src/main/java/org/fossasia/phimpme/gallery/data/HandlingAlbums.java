package org.fossasia.phimpme.gallery.data;

import android.content.Context;
import android.media.MediaScannerConnection;

import org.fossasia.phimpme.R;
import org.fossasia.phimpme.gallery.data.providers.StorageProvider;
import org.fossasia.phimpme.gallery.data.base.AlbumsComparators;
import org.fossasia.phimpme.gallery.data.base.SortingMode;
import org.fossasia.phimpme.gallery.data.base.SortingOrder;
import org.fossasia.phimpme.gallery.data.providers.MediaStoreProvider;
import org.fossasia.phimpme.gallery.util.ContentHelper;
import org.fossasia.phimpme.gallery.util.PreferenceUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by dnld on 27/04/16.
 */
public class HandlingAlbums {

  public final static String TAG = "HandlingAlbums";
  private static String backupFile = "albums.dat";

  public ArrayList<Album> dispAlbums;
  private ArrayList<Album> selectedAlbums;

  private PreferenceUtil SP;

  private int current = 0;
  private boolean hidden;

  public HandlingAlbums(Context context) {
    SP = PreferenceUtil.getInstance(context);
    dispAlbums = new ArrayList<Album>();
    selectedAlbums = new ArrayList<Album>();
  }


  public void loadAlbums(Context context, boolean hidden) {
    this.hidden = hidden;

    ArrayList<Album> list = new ArrayList<Album>();
    if (SP.getBoolean(context.getString(R.string.preference_use_alternative_provider), false)) {
      StorageProvider p = new StorageProvider(context);
      list = p.getAlbums(context, hidden);
    } else {
      list.addAll(MediaStoreProvider.getAlbums(context, hidden));
    }
    dispAlbums = list;
    sortAlbums();
  }

  public void addAlbum(int position, Album album) {
    dispAlbums.add(position, album);
    setCurrentAlbum(album);

  }

  public void setCurrentAlbum(Album album) {
    current = dispAlbums.indexOf(album);
  }


  public Album getCurrentAlbum() {
    return dispAlbums.get(current);
  }

  public void saveBackup(final Context context) {
    if (!hidden) {
      new Thread(new Runnable() {
        public void run() {
          FileOutputStream outStream;
          try {
            File f = new File(context.getCacheDir(), backupFile);
            outStream = new FileOutputStream(f);
            ObjectOutputStream objectOutStream = new ObjectOutputStream(outStream);
            objectOutStream.writeObject(dispAlbums);
            objectOutStream.close();
          } catch (FileNotFoundException e1) {
            e1.printStackTrace();
          } catch (IOException e1) {
            e1.printStackTrace();
          }
        }
      }).start();
    }
  }

  @SuppressWarnings("unchecked")
  public static void addAlbumToBackup(final Context context, final Album album) {
    new Thread(new Runnable() {
      public void run() {
        try {
          boolean success = false;
          File f = new File(context.getCacheDir(), backupFile);
          ObjectInputStream reader = new ObjectInputStream(new FileInputStream(f));
          Object o = reader.readObject();
          ArrayList<Album> list = null;
          if (o != null) {
            list = (ArrayList<Album>) o;
            for(int i = 0; i < list.size(); i++) {
              if (list.get(i).equals(album)) {
                list.remove(i);
                list.add(i, album);
                success = true;
              }
            }
          }

          if (success) {
            ObjectOutputStream objectOutStream = new ObjectOutputStream(new FileOutputStream(f));
            objectOutStream.writeObject(list);
            objectOutStream.close();
          }

        } catch (FileNotFoundException e1) {
          e1.printStackTrace();
        } catch (IOException e1) {
          e1.printStackTrace();
        } catch (ClassNotFoundException e) {
          e.printStackTrace();
        }
      }
    }).start();
  }

  @SuppressWarnings("unchecked")
  public void restoreBackup(Context context) {
    FileInputStream inStream;
    try {
      File f = new File(context.getCacheDir(), backupFile);
      inStream = new FileInputStream(f);
      ObjectInputStream objectInStream = new ObjectInputStream(inStream);

      dispAlbums = (ArrayList<Album>) objectInStream.readObject();

      objectInStream.close();
    } catch (FileNotFoundException e1) {
      e1.printStackTrace();
    } catch (ClassNotFoundException e1) {
      e1.printStackTrace();
    } catch (OptionalDataException e1) {
      e1.printStackTrace();
    } catch (StreamCorruptedException e1) {
      e1.printStackTrace();
    } catch (IOException e1) {
      e1.printStackTrace();
    }
  }

  private int toggleSelectAlbum(int index) {
    if (dispAlbums.get(index) != null) {
      dispAlbums.get(index).setSelected(!dispAlbums.get(index).isSelected());
      if (dispAlbums.get(index).isSelected()) selectedAlbums.add(dispAlbums.get(index));
      else selectedAlbums.remove(dispAlbums.get(index));
    }
    return index;
  }

  public int toggleSelectAlbum(Album album) {
    return toggleSelectAlbum(dispAlbums.indexOf(album));
  }

  public Album getAlbum(int index){ return dispAlbums.get(index); }

  public void selectAllAlbums() {
    for (Album dispAlbum : dispAlbums)
      if (!dispAlbum.isSelected()) {
        dispAlbum.setSelected(true);
        selectedAlbums.add(dispAlbum);
      }
  }

  public void removeCurrentAlbum(){ dispAlbums.remove(current); }

  public int getSelectedCount() {
    return selectedAlbums.size();
  }

  public void clearSelectedAlbums() {
    for (Album dispAlbum : dispAlbums)
      dispAlbum.setSelected(false);

    selectedAlbums.clear();
  }

  private void scanFile(Context context, String[] path) {   MediaScannerConnection.scanFile(context, path, null, null); }

  public void hideAlbum(String path, Context context) {
    File dirName = new File(path);
    File file = new File(dirName, ".nomedia");
    if (!file.exists()) {
      try {
        FileOutputStream out = new FileOutputStream(file);
        out.flush();
        out.close();
        scanFile(context, new String[]{ file.getAbsolutePath() });
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }
  public void hideSelectedAlbums(Context context) {
    for (Album selectedAlbum : selectedAlbums)
      hideAlbum(selectedAlbum, context);
    clearSelectedAlbums();
  }

  private void hideAlbum(final Album a, Context context) {
    hideAlbum(a.getPath(), context);
    dispAlbums.remove(a);
  }

  public void unHideAlbum(String path, Context context) {
    File dirName = new File(path);
    File file = new File(dirName, ".nomedia");
    if (file.exists()) {
      if (file.delete())
        scanFile(context, new String[]{ file.getAbsolutePath() });
    }
  }
  public void unHideSelectedAlbums(Context context) {
    for (Album selectedAlbum : selectedAlbums)
      unHideAlbum(selectedAlbum, context);
    clearSelectedAlbums();
  }

  private void unHideAlbum(final Album a, Context context) {
    unHideAlbum(a.getPath(), context);
    dispAlbums.remove(a);
  }

  public boolean deleteSelectedAlbums(Context context) {
    boolean success = true;

    for (Album selectedAlbum : selectedAlbums) {
      int index = dispAlbums.indexOf(selectedAlbum);
      if(deleteAlbum(selectedAlbum, context))
        dispAlbums.remove(index);
      else success = false;
    }
    return success;
  }

  public boolean deleteAlbum(Album album, Context context) {
    return ContentHelper.deleteFilesInFolder(context, new File(album.getPath()));
  }

  public boolean moveSelectedAlbum(Context context, String targetDir) {
    Album current = selectedAlbums.get(0);
    current.updatePhotos(context);
    current.selectAllPhotos();
    int ans = current.moveSelectedMedia(context, targetDir);
    return ans != -1;
  }

  public void excludeSelectedAlbums(Context context) {
    for (Album selectedAlbum : selectedAlbums)
      excludeAlbum(context, selectedAlbum);

    clearSelectedAlbums();
  }

  private void excludeAlbum(Context context, Album a) {
    CustomAlbumsHelper h = CustomAlbumsHelper.getInstance(context);
    h.excludeAlbum(a.getPath());
    dispAlbums.remove(a);
  }

  public SortingMode getSortingMode() {
    return SortingMode.fromValue(SP.getInt("albums_sorting_mode", SortingMode.DATE.getValue()));
  }

  public SortingOrder getSortingOrder() {
    return SortingOrder.fromValue(SP.getInt("albums_sorting_order", SortingOrder.DESCENDING.getValue()));
  }

  public void setDefaultSortingMode(SortingMode sortingMode) {
    SP.putInt("albums_sorting_mode", sortingMode.getValue());
  }

  public void setDefaultSortingAscending(SortingOrder sortingOrder) {
    SP.putInt("albums_sorting_order", sortingOrder.getValue());
  }

  public void sortAlbums() {
    Collections.sort(dispAlbums, AlbumsComparators.getComparator(getSortingMode(), getSortingOrder()));
  }

  public Album getSelectedAlbum(int index) { return selectedAlbums.get(index); }

  public void loadAlbums(Context applicationContext) {
    loadAlbums(applicationContext, hidden);
  }
}
