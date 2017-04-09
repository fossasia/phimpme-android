package vn.mbm.phimp.me.folderchooser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rohanagarwal94 on 31/3/17.
 */

public class MediaExtension {
    private static final List<MediaExtension> MEDIA_EXTENSIONs = new ArrayList<MediaExtension>() {{
        add(new MediaExtension("jpg", null));
        add(new MediaExtension("png", null));
        add(new MediaExtension("jpeg", null));
        add(new MediaExtension("gif", null));
    }};

    public String extension;
    public String mediaType;

    private MediaExtension(String extension, String mediaType) {
        this.extension = extension;
        this.mediaType = mediaType;
    }

    public static List<MediaExtension> getList() {
        return MEDIA_EXTENSIONs;
    }
}
