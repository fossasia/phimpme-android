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

import java.util.HashMap;

import vn.mbm.phimp.me.gallery3d.app.App;

// CR: this stuff needs comments really badly.
public final class DisplaySlot {
    private MediaSet mSetRef;
    private String mTitle;
    private StringTexture mTitleImage;
    private String mLocation;
    private StringTexture mLocationImage;

    private static final StringTexture.Config CAPTION_STYLE = new StringTexture.Config();
    private static final StringTexture.Config CLUSTER_STYLE = new StringTexture.Config();
    private static final StringTexture.Config LOCATION_STYLE = new StringTexture.Config();

    static {
        CAPTION_STYLE.sizeMode = StringTexture.Config.SIZE_TEXT_TO_BOUNDS;
        CAPTION_STYLE.fontSize = 16 * App.PIXEL_DENSITY;
        CAPTION_STYLE.bold = true;
        CAPTION_STYLE.width = (App.PIXEL_DENSITY < 1.5f) ? 128 : 256;
        CAPTION_STYLE.height = (App.PIXEL_DENSITY < 1.5f) ? 32 : 64;
        CAPTION_STYLE.yalignment = StringTexture.Config.ALIGN_TOP;
        CAPTION_STYLE.xalignment = StringTexture.Config.ALIGN_HCENTER;

        CLUSTER_STYLE.sizeMode = StringTexture.Config.SIZE_TEXT_TO_BOUNDS;
        CLUSTER_STYLE.width = (App.PIXEL_DENSITY < 1.5f) ? 128 : 256;
        CLUSTER_STYLE.height = (App.PIXEL_DENSITY < 1.5f) ? 32 : 64;
        CLUSTER_STYLE.yalignment = StringTexture.Config.ALIGN_TOP;
        CLUSTER_STYLE.fontSize = 16 * App.PIXEL_DENSITY;
        CLUSTER_STYLE.bold = true;
        CLUSTER_STYLE.xalignment = StringTexture.Config.ALIGN_HCENTER;

        LOCATION_STYLE.sizeMode = StringTexture.Config.SIZE_TEXT_TO_BOUNDS;
        LOCATION_STYLE.fontSize = 12 * App.PIXEL_DENSITY;
        LOCATION_STYLE.width = (App.PIXEL_DENSITY < 1.5f) ? 128 : 256;
        LOCATION_STYLE.height = (App.PIXEL_DENSITY < 1.5f) ? 32 : 64;
        LOCATION_STYLE.fontSize = 12 * App.PIXEL_DENSITY;
        LOCATION_STYLE.xalignment = StringTexture.Config.ALIGN_HCENTER;
    }

    public void setMediaSet(MediaSet set) {
        mSetRef = set;
        mTitle = null;
        mTitleImage = null;
        mLocationImage = null;
        if (set.mReverseGeocodedLocation == null) {
            set.mReverseGeocodedLocationRequestMade = false;
            set.mReverseGeocodedLocationComputed = false;
        }
    }

    public MediaSet getMediaSet() {
        return mSetRef;
    }

    public boolean hasValidLocation() {
        if (mSetRef != null) {
            return (mSetRef.mReverseGeocodedLocation != null);
        } else {
            return false;
        }
    }

    private StringTexture getTextureForString(String string, HashMap<String, StringTexture> textureTable,
            StringTexture.Config config) {
        StringTexture texture = null;
        if (textureTable != null && textureTable.containsKey(string)) {
            texture = textureTable.get(string);
        }
        if (texture == null) {
            texture = new StringTexture(string, config);
            if (textureTable != null) {
                textureTable.put(string, texture);
            }
        }
        return texture;
    }

    public StringTexture getTitleImage(HashMap<String, StringTexture> textureTable) {
        if (mSetRef == null) {
            return null;
        }
        StringTexture texture = mTitleImage;
        String title = mSetRef.mTruncTitleString;
        if (texture == null && title != null && !(title.equals(mTitle))) {
            texture = getTextureForString(title, textureTable, ((mSetRef.mId != Shared.INVALID && mSetRef.mId != 0) ? CAPTION_STYLE
                    : CLUSTER_STYLE));
            mTitleImage = texture;
            mTitle = title;
        }
        return texture;
    }

    public StringTexture getLocationImage(ReverseGeocoder reverseGeocoder, HashMap<String, StringTexture> textureTable) {
        if (mSetRef == null || mSetRef.mTitleString == null) {
            return null;
        }
        if (mLocationImage == null) {
            if (!mSetRef.mReverseGeocodedLocationRequestMade && reverseGeocoder != null) {
                reverseGeocoder.enqueue(mSetRef);
                mSetRef.mReverseGeocodedLocationRequestMade = true;
            }
            if (mSetRef.mReverseGeocodedLocationComputed) {
                String geocodedLocation = mSetRef.mReverseGeocodedLocation;
                if (geocodedLocation != null) {
                    mLocation = geocodedLocation;
                    mLocationImage = getTextureForString(mLocation, textureTable, LOCATION_STYLE);
                }
            }
        }
        return mLocationImage;
    }
}
