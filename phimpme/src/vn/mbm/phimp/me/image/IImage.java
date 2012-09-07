package vn.mbm.phimp.me.image;

import java.io.InputStream;

import android.graphics.Bitmap;
import android.net.Uri;

public interface IImage {
    static final int THUMBNAIL_TARGET_SIZE = 320;
    static final int MINI_THUMB_TARGET_SIZE = 96;
    static final int UNCONSTRAINED = -1;

    /** Get the image list which contains this image. */
    public abstract IImageList getContainer();

    /** Get the bitmap for the full size image. */
    public abstract Bitmap fullSizeBitmap(int minSideLength,
            int maxNumberOfPixels);
    public abstract Bitmap fullSizeBitmap(int minSideLength,
            int maxNumberOfPixels, boolean rotateAsNeeded);
    public abstract Bitmap fullSizeBitmap(int minSideLength,
            int maxNumberOfPixels, boolean rotateAsNeeded, boolean useNative);
    public abstract int getDegreesRotated();
    public static final boolean ROTATE_AS_NEEDED = true;
    public static final boolean NO_ROTATE = false;
    public static final boolean USE_NATIVE = true;
    public static final boolean NO_NATIVE = false;

    /** Get the input stream associated with a given full size image. */
    public abstract InputStream fullSizeImageData();
    public abstract long fullSizeImageId();
    public abstract Uri fullSizeImageUri();

    /** Get the path of the (full size) image data. */
    public abstract String getDataPath();

    // Get/Set the title of the image
    public abstract void setTitle(String name);
    public abstract String getTitle();

    // Get metadata of the image
    public abstract long getDateTaken();

    public abstract String getMimeType();

    public abstract int getWidth();

    public abstract int getHeight();

    public abstract String getDisplayName();

    // Get property of the image
    public abstract boolean isReadonly();
    public abstract boolean isDrm();

    // Get the bitmap/uri of the medium thumbnail
    public abstract Bitmap thumbBitmap(boolean rotateAsNeeded);
    public abstract Uri thumbUri();

    // Get the bitmap of the mini thumbnail.
    public abstract Bitmap miniThumbBitmap();

    // Rotate the image
    public abstract boolean rotateImageBy(int degrees);

}
