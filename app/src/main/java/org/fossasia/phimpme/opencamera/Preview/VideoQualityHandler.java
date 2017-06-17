package org.fossasia.phimpme.opencamera.Preview;

import android.media.CamcorderProfile;
import android.util.Log;

import org.fossasia.phimpme.opencamera.CameraController.CameraController;
import org.fossasia.phimpme.opencamera.Camera.MyDebug;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/** Handles video quality options.
 *  Note that this class should avoid calls to the Android API, so we can perform local unit testing
 *  on it.
 */
public class VideoQualityHandler {
    private static final String TAG = "VideoQualityHandler";

    public static class Dimension2D {
        final int width;
        final int height;

        public Dimension2D(int width, int height) {
            this.width = width;
            this.height = height;
        }
    }

    // video_quality can either be:
    // - an int, in which case it refers to a CamcorderProfile
    // - of the form [CamcorderProfile]_r[width]x[height] - we use the CamcorderProfile as a base, and override the video resolution - this is needed to support resolutions which don't have corresponding camcorder profiles
    private List<String> video_quality;
    private int current_video_quality = -1; // this is an index into the video_quality array, or -1 if not found (though this shouldn't happen?)
    private List<CameraController.Size> video_sizes;

    void resetCurrentQuality() {
        video_quality = null;
        current_video_quality = -1;
    }

    /** Initialises the class with the available video profiles and resolutions. The user should first
     *  set the video sizes via setVideoSizes().
     * @param profiles   A list of qualities (see CamcorderProfile.QUALITY_*). Should be supplied in
     *                   order from highest to lowest quality.
     * @param dimensions A corresponding list of the width/height for that quality (as given by
     *                   videoFrameWidth, videoFrameHeight in the profile returned by CamcorderProfile.get()).
     */
    public void initialiseVideoQualityFromProfiles(List<Integer> profiles, List<Dimension2D> dimensions) {
        if( MyDebug.LOG )
            Log.d(TAG, "initialiseVideoQualityFromProfiles()");
        video_quality = new ArrayList<>();
        boolean done_video_size[] = null;
        if( video_sizes != null ) {
            done_video_size = new boolean[video_sizes.size()];
            for(int i=0;i<video_sizes.size();i++)
                done_video_size[i] = false;
        }
        if( profiles.size() != dimensions.size() ) {
            Log.e(TAG, "profiles and dimensions have unequal sizes");
            throw new RuntimeException(); // this is a programming error
        }
        for(int i=0;i<profiles.size();i++) {
            Dimension2D dim = dimensions.get(i);
            addVideoResolutions(done_video_size, profiles.get(i), dim.width, dim.height);
        }
        if( MyDebug.LOG ) {
            for(int i=0;i<video_quality.size();i++) {
                Log.d(TAG, "supported video quality: " + video_quality.get(i));
            }
        }
    }

    // Android docs and FindBugs recommend that Comparators also be Serializable
    private static class SortVideoSizesComparator implements Comparator<CameraController.Size>, Serializable {
        private static final long serialVersionUID = 5802214721033718212L;

        @Override
        public int compare(final CameraController.Size a, final CameraController.Size b) {
            return b.width * b.height - a.width * a.height;
        }
    }

    public void sortVideoSizes() {
        if( MyDebug.LOG )
            Log.d(TAG, "sortVideoSizes()");
        Collections.sort(this.video_sizes, new SortVideoSizesComparator());
        if( MyDebug.LOG ) {
            for(CameraController.Size size : video_sizes) {
                Log.d(TAG, "    supported video size: " + size.width + ", " + size.height);
            }
        }
    }

    private void addVideoResolutions(boolean done_video_size[], int base_profile, int min_resolution_w, int min_resolution_h) {
        if( video_sizes == null ) {
            return;
        }
        if( MyDebug.LOG )
            Log.d(TAG, "profile " + base_profile + " is resolution " + min_resolution_w + " x " + min_resolution_h);
        for(int i=0;i<video_sizes.size();i++) {
            if( done_video_size[i] )
                continue;
            CameraController.Size size = video_sizes.get(i);
            if( size.width == min_resolution_w && size.height == min_resolution_h ) {
                String str = "" + base_profile;
                video_quality.add(str);
                done_video_size[i] = true;
                if( MyDebug.LOG )
                    Log.d(TAG, "added: " + str);
            }
            else if( base_profile == CamcorderProfile.QUALITY_LOW || size.width * size.height >= min_resolution_w*min_resolution_h ) {
                String str = "" + base_profile + "_r" + size.width + "x" + size.height;
                video_quality.add(str);
                done_video_size[i] = true;
                if( MyDebug.LOG )
                    Log.d(TAG, "added: " + str);
            }
        }
    }

    public List<String> getSupportedVideoQuality() {
        if( MyDebug.LOG )
            Log.d(TAG, "getSupportedVideoQuality");
        return this.video_quality;
    }

    public int getCurrentVideoQualityIndex() {
        if( MyDebug.LOG )
            Log.d(TAG, "getCurrentVideoQualityIndex");
        return this.current_video_quality;
    }

    public void setCurrentVideoQualityIndex(int current_video_quality) {
        if( MyDebug.LOG )
            Log.d(TAG, "setCurrentVideoQualityIndex: " + current_video_quality);
        this.current_video_quality = current_video_quality;
    }

    public String getCurrentVideoQuality() {
        if( current_video_quality == -1 )
            return null;
        return video_quality.get(current_video_quality);
    }

    public List<CameraController.Size> getSupportedVideoSizes() {
        if( MyDebug.LOG )
            Log.d(TAG, "getSupportedVideoSizes");
        return this.video_sizes;
    }

    public void setVideoSizes(List<CameraController.Size> video_sizes) {
        this.video_sizes = video_sizes;
        this.sortVideoSizes();
    }

}
