/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package vn.mbm.phimp.me.gallery3d.media;

public class LocationMediaFilter extends MediaFilter {
    private double mRadius;
    private double mCenterLat;
    private double mCenterLon;
    public static final int EARTH_RADIUS_METERS = 6378137;
    public static final int LAT_MIN = -90;
    public static final int LAT_MAX = 90;
    public static final int LON_MIN = -180;
    public static final int LON_MAX = 180;

    LocationMediaFilter(double centerLatitude, double centerLongitude, double thresholdRadius) {
        mCenterLat = centerLatitude;
        mCenterLon = centerLongitude;
        mRadius = thresholdRadius;
    }

    LocationMediaFilter(double latitude1, double longitude1, double latitude2, double longitude2) {
        mCenterLat = centerLat(latitude1, latitude2);
        mCenterLon = centerLon(longitude1, longitude2);
        mRadius = distanceBetween(latitude1, longitude1, latitude2, longitude2);
    }

    public static final double centerLat(double lat1, double lat2) {
        return (centerOfAngles(lat1, lat2, LAT_MAX));
    }

    public static final double centerLon(double lon1, double lon2) {
        return (centerOfAngles(lon1, lon2, LON_MAX));
    }

    private static final double centerOfAngles(double ang1, double ang2, int wrapAroundThreshold) {
        boolean wrapAround = false;
        if (Math.abs(ang1 - ang2) > wrapAroundThreshold) {
            wrapAround = true;
        }
        double center = (ang1 + ang2) * 0.5;
        if (wrapAround) {
            center += wrapAroundThreshold;
            center %= wrapAroundThreshold;
        }
        return center;
    }

    public static double distanceBetween(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS_METERS * c;
    }

    public static final double toKm(double meter) {
        return meter / 1000;
    }

    public static final double toMile(double meter) {
        return meter / 1609;
    }

    @Override
    public boolean pass(MediaItem item) {
        double radius = distanceBetween(mCenterLat, mCenterLon, item.mLatitude, item.mLongitude);
        if (radius <= mRadius) {
            return true;
        }
        return false;
    }
}
