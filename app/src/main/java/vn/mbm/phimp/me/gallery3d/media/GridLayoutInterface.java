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

import vn.mbm.phimp.me.gallery3d.app.App;

public final class GridLayoutInterface extends LayoutInterface {
    public GridLayoutInterface(int numRows) {
        mNumRows = numRows;
        mSpacingX = (int) (20 * App.PIXEL_DENSITY);
        mSpacingY = (int) (40 * App.PIXEL_DENSITY);
    }
    
    public float getSpacingForBreak() {
        return mSpacingX / 2;
    }

    public int getNextSlotIndexForBreak(int breakSlotIndex) {
        int numRows = mNumRows;
        int mod = breakSlotIndex % numRows;
        int add = (numRows - mod);
        if (add >= numRows)
            add -= numRows;
        return breakSlotIndex + add;
    }

    public void getPositionForSlotIndex(int slotIndex, int itemWidth, int itemHeight, Vector3f outPosition) {
        int numRows = mNumRows;
        int resultSlotIndex = slotIndex;
        outPosition.x = (resultSlotIndex / numRows) * (itemWidth + mSpacingX);
        outPosition.y = (resultSlotIndex % numRows) * (itemHeight + mSpacingY);
        int maxY = (numRows - 1) * (itemHeight + mSpacingY);
        outPosition.y -= (maxY >> 1);
        outPosition.z = 0;
    }

    public int mNumRows;
    public int mSpacingX;
    public int mSpacingY;
}
