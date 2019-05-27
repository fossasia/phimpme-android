package org.fossasia.phimpme.gallery.util;

import com.bumptech.glide.signature.StringSignature;
import org.fossasia.phimpme.gallery.data.Media;

/** Created by dnld on 21/08/16. */
public class MediaSignature extends StringSignature {

  private MediaSignature(String path, long lastModified, int orientation) {
    super(path + lastModified + orientation);
  }

  public MediaSignature(Media media) {
    this(media.getPath(), media.getDateModified(), media.getOrientation());
  }
}
