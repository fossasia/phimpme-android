package org.fossasia.phimpme.editor.view.imagezoom.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import org.fossasia.phimpme.editor.utils.BitmapUtils;

public class DecodeUtils {

  /**
   * Try to load a {@link Bitmap} from the passed {@link Uri} ( a file, a content or an url )
   *
   * @param context the current app context
   * @param uri the image source
   * @param maxW the final image maximum width
   * @param maxH the final image maximum height
   * @return the loaded and resized bitmap, if success, or null if load was unsuccesful
   */
  public static Bitmap decode(Context context, Uri uri, int maxW, int maxH) {
    InputStream stream = openInputStream(context, uri);
    if (null == stream) {
      return null;
    }

    int orientation = ExifUtils.getExifOrientation(context, uri);

    Bitmap bitmap = null;
    int[] imageSize = new int[2];
    final boolean decoded = decodeImageBounds(stream, imageSize);
    IOUtils.closeSilently(stream);

    if (decoded) {
      int sampleSize;
      if (maxW < 0 || maxH < 0) {
        sampleSize = 1;
      } else {
        sampleSize =
            computeSampleSize(
                imageSize[0], imageSize[1], (int) (maxW * 1.2), (int) (maxH * 1.2), orientation);
      }

      BitmapFactory.Options options = getDefaultOptions();
      options.inSampleSize = sampleSize;

      bitmap = decodeBitmap(context, uri, options, maxW, maxH, orientation, 0);
    }

    return bitmap;
  }

  static Bitmap decodeBitmap(
      Context context,
      Uri uri,
      BitmapFactory.Options options,
      int maxW,
      int maxH,
      int orientation,
      int pass) {

    Bitmap bitmap = null;
    Bitmap newBitmap = null;

    if (pass > 20) {
      return null;
    }

    InputStream stream = openInputStream(context, uri);
    if (null == stream) return null;

    try {
      // decode the bitmap via android BitmapFactory
      bitmap = BitmapFactory.decodeStream(stream, null, options);
      IOUtils.closeSilently(stream);

      if (bitmap != null) {
        if (maxW > 0 && maxH > 0) {
          newBitmap = BitmapUtils.resizeBitmap(bitmap, maxW, maxH, orientation);
          if (bitmap != newBitmap) {
            bitmap.recycle();
          }
          bitmap = newBitmap;
        }
      }

    } catch (OutOfMemoryError error) {
      IOUtils.closeSilently(stream);
      if (null != bitmap) {
        bitmap.recycle();
      }
      options.inSampleSize += 1;
      bitmap = decodeBitmap(context, uri, options, maxW, maxH, orientation, pass + 1);
    }
    return bitmap;
  }

  /**
   * Return an {@link InputStream} from the given uri. ( can be a local content, a file path or an
   * http url )
   *
   * @param context
   * @param uri
   * @return the {@link InputStream} from the given uri, null if uri cannot be opened
   */
  public static InputStream openInputStream(Context context, Uri uri) {
    if (null == uri) return null;
    final String scheme = uri.getScheme();
    InputStream stream = null;
    if (scheme == null || ContentResolver.SCHEME_FILE.equals(scheme)) {
      // from file
      stream = openFileInputStream(uri.getPath());
    } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
      // from content
      stream = openContentInputStream(context, uri);
    } else if ("http".equals(scheme) || "https".equals(scheme)) {
      // from remote uri
      stream = openRemoteInputStream(uri);
    }
    return stream;
  }

  public static boolean decodeImageBounds(final InputStream stream, int[] outSize) {
    BitmapFactory.Options options = new BitmapFactory.Options();
    options.inJustDecodeBounds = true;
    BitmapFactory.decodeStream(stream, null, options);
    if (options.outHeight > 0 && options.outWidth > 0) {
      outSize[0] = options.outWidth;
      outSize[1] = options.outHeight;
      return true;
    }
    return false;
  }

  private static int computeSampleSize(
      final int bitmapW, final int bitmapH, final int maxW, final int maxH, final int orientation) {
    double w, h;

    if (orientation == 0 || orientation == 180) {
      w = bitmapW;
      h = bitmapH;
    } else {
      w = bitmapH;
      h = bitmapW;
    }

    final int sampleSize = (int) Math.ceil(Math.max(w / maxW, h / maxH));
    return sampleSize;
  }

  /**
   * Return a {@link FileInputStream} from the given path or null if file not found
   *
   * @param path the file path
   * @return the {@link FileInputStream} of the given path, null if {@link FileNotFoundException} is
   *     thrown
   */
  static InputStream openFileInputStream(String path) {
    try {
      return new FileInputStream(path);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * Return a {@link BufferedInputStream} from the given uri or null if an exception is thrown
   *
   * @param context
   * @param uri
   * @return the {@link InputStream} of the given path. null if file is not found
   */
  static InputStream openContentInputStream(Context context, Uri uri) {
    try {
      return context.getContentResolver().openInputStream(uri);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * Return an {@link InputStream} from the given url or null if failed to retrieve the content
   *
   * @param uri
   * @return
   */
  static InputStream openRemoteInputStream(Uri uri) {
    java.net.URL finalUrl;
    try {
      finalUrl = new java.net.URL(uri.toString());
    } catch (MalformedURLException e) {
      e.printStackTrace();
      return null;
    }

    HttpURLConnection connection;
    try {
      connection = (HttpURLConnection) finalUrl.openConnection();
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }

    connection.setInstanceFollowRedirects(false);
    int code;
    try {
      code = connection.getResponseCode();
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }

    // permanent redirection
    if (code == HttpURLConnection.HTTP_MOVED_PERM
        || code == HttpURLConnection.HTTP_MOVED_TEMP
        || code == HttpURLConnection.HTTP_SEE_OTHER) {
      String newLocation = connection.getHeaderField("Location");
      return openRemoteInputStream(Uri.parse(newLocation));
    }

    try {
      return (InputStream) finalUrl.getContent();
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
  }

  static BitmapFactory.Options getDefaultOptions() {
    BitmapFactory.Options options = new BitmapFactory.Options();
    options.inScaled = false;
    options.inPreferredConfig = Bitmap.Config.ARGB_8888;
    options.inDither = false;
    options.inJustDecodeBounds = false;
    options.inPurgeable = true;
    options.inInputShareable = true;
    options.inTempStorage = new byte[16 * 1024];
    return options;
  }
}
