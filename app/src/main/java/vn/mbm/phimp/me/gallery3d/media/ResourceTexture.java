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

import java.io.IOException;
import java.io.InputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public final class ResourceTexture extends Texture {
    private final int mResourceId;
    private final boolean mScaled;

    @Override
    public boolean isCached() {
        return true;
    }

    public ResourceTexture(int resourceId, boolean scaled) {
        mResourceId = resourceId;
        mScaled = scaled;
    }

    @Override
    protected Bitmap load(RenderView view) {
        // Load a bitmap from the resource.
        Bitmap bitmap = null;
        if (mScaled) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            bitmap = BitmapFactory.decodeResource(view.getResources(), mResourceId, options);
        } else {
            InputStream inputStream = view.getResources().openRawResource(mResourceId);
            if (inputStream != null) {
                try {
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                    bitmap = BitmapFactory.decodeStream(inputStream, null, options);
                } catch (Exception e) {
                } finally {
                    try {
                        inputStream.close();
                    } catch (IOException e) { /* ignore */
                    }
                }
            }
        }
        return bitmap;
    }
}
