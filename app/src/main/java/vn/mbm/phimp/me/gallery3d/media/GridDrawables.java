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

import javax.microedition.khronos.opengles.GL11;

import vn.mbm.phimp.me.R;
import vn.mbm.phimp.me.gallery3d.app.App;
import vn.mbm.phimp.me.gallery3d.app.Res;
import android.R.drawable;

public final class GridDrawables {
    // The display primitives.
    public static GridQuad sGrid;
    public static GridQuadFrame sFrame;
    public static GridQuad sTextGrid;
    public static GridQuad sSelectedGrid;
    public static GridQuad sVideoGrid;
    public static GridQuad sLocationGrid;
    public static GridQuad sSourceIconGrid;
    public static final GridQuad[] sFullscreenGrid = new GridQuad[3];

    // All the resource Textures.
    @SuppressWarnings("static-access")
	private static final int TEXTURE_FRAME = Res.drawable.stack_frame;
    @SuppressWarnings("static-access")
	private static final int TEXTURE_GRID_FRAME = Res.drawable.grid_frame;
    @SuppressWarnings("static-access")
	private static final int TEXTURE_FRAME_FOCUS = Res.drawable.stack_frame_focus;
    @SuppressWarnings("static-access")
	private static final int TEXTURE_FRAME_PRESSED = Res.drawable.stack_frame_gold;
    @SuppressWarnings("static-access")
	private static final int TEXTURE_LOCATION = Res.drawable.btn_location_filter_unscaled;
    @SuppressWarnings("static-access")
	private static final int TEXTURE_VIDEO = Res.drawable.videooverlay;
    @SuppressWarnings("static-access")
	private static final int TEXTURE_CHECKMARK_ON = Res.drawable.grid_check_on;
    @SuppressWarnings("static-access")
	private static final int TEXTURE_CHECKMARK_OFF = Res.drawable.grid_check_off;
    @SuppressWarnings("static-access")
	private static final int TEXTURE_CAMERA_SMALL = Res.drawable.icon_camera_small_unscaled;
    @SuppressWarnings("static-access")
	private static final int TEXTURE_PICASA_SMALL = Res.drawable.icon_picasa_small_unscaled;
    public static final int[] TEXTURE_SPINNER = new int[8];
    @SuppressWarnings("static-access")
	private static final int TEXTURE_TRANSPARENT = Res.drawable.transparent;
    @SuppressWarnings("static-access")
	private static final int TEXTURE_PLACEHOLDER = Res.drawable.grid_placeholder;

    public Texture mTextureFrame;
    public Texture mTextureGridFrame;
    public Texture mTextureFrameFocus;
    public Texture mTextureFramePressed;
    public Texture mTextureLocation;
    public Texture mTextureVideo;
    public Texture mTextureCheckmarkOn;
    public Texture mTextureCheckmarkOff;
    public Texture mTextureCameraSmall;
    public Texture mTexturePicasaSmall;
    public Texture[] mTextureSpinner = new Texture[8];
    public Texture mTextureTransparent;
    public Texture mTexturePlaceholder;

    // The textures generated from strings.
    public static final HashMap<String, StringTexture> sStringTextureTable = new HashMap<String, StringTexture>(128);

    static {
        // We first populate the spinner textures.
        final int[] textureSpinner = TEXTURE_SPINNER;
        textureSpinner[0] = R.drawable.ic_spinner1;
        textureSpinner[1] = R.drawable.ic_spinner2;
        textureSpinner[2] = R.drawable.ic_spinner3;
        textureSpinner[3] = R.drawable.ic_spinner4;
        textureSpinner[4] = R.drawable.ic_spinner5;
        textureSpinner[5] = R.drawable.ic_spinner6;
        textureSpinner[6] = R.drawable.ic_spinner7;
        textureSpinner[7] = R.drawable.ic_spinner8;
    }

    public GridDrawables(final int itemWidth, final int itemHeight) {
        if (sGrid == null) {
            final float height = 1.0f;
            final float width = (float) (height * itemWidth) / (float) itemHeight;
            final float aspectRatio = (float) itemWidth / (float) itemHeight;
            final float oneByAspect = 1.0f / aspectRatio;

            // We create the grid quad.
            sGrid = GridQuad.createGridQuad(width, height, 0, 0, 1.0f, oneByAspect, true);

            // We create the quads used in fullscreen.
            sFullscreenGrid[0] = GridQuad.createGridQuad(width, height, 0, 0, 1.0f, oneByAspect, false);
            sFullscreenGrid[0].setDynamic(true);
            sFullscreenGrid[1] = GridQuad.createGridQuad(width, height, 0, 0, 1.0f, oneByAspect, false);
            sFullscreenGrid[1].setDynamic(true);
            sFullscreenGrid[2] = GridQuad.createGridQuad(width, height, 0, 0, 1.0f, oneByAspect, false);
            sFullscreenGrid[2].setDynamic(true);

            // We create supplementary quads for the checkmarks, video overlay
            // and location button
            float sizeOfSelectedIcon = 32 * App.PIXEL_DENSITY; // In pixels.
            sizeOfSelectedIcon /= itemHeight;
            float sizeOfLocationIcon = 52 * App.PIXEL_DENSITY; // In pixels.
            sizeOfLocationIcon /= itemHeight;
            float sizeOfSourceIcon = 76 * App.PIXEL_DENSITY; // In pixels.
            sizeOfSourceIcon /= itemHeight;
            sSelectedGrid = GridQuad.createGridQuad(sizeOfSelectedIcon, sizeOfSelectedIcon, -0.5f, 0.25f, 1.0f, 1.0f, false);
            sVideoGrid = GridQuad.createGridQuad(sizeOfSelectedIcon, sizeOfSelectedIcon, -0.08f, -0.09f, 1.0f, 1.0f, false);
            sLocationGrid = GridQuad.createGridQuad(sizeOfLocationIcon, sizeOfLocationIcon, 0, 0, 1.0f, 1.0f, false);
            sSourceIconGrid = GridQuad.createGridQuad(sizeOfSourceIcon, sizeOfSourceIcon, 0, 0, 1.0f, 1.0f, false);

            // We create the quad for the text label.
            float seedTextWidth = (App.PIXEL_DENSITY < 1.5f) ? 128.0f : 256.0f;
            float textWidth = (seedTextWidth / (float) itemWidth) * width;
            float textHeightPow2 = (App.PIXEL_DENSITY < 1.5f) ? 32.0f : 64.0f;
            float textHeight = (textHeightPow2 / (float) itemHeight) * height;
            float textOffsetY = 0.0f;
            sTextGrid = GridQuad.createGridQuad(textWidth, textHeight, 0, textOffsetY, 1.0f, 1.0f, false);

            // We finally create the frame around every grid item
            sFrame = GridQuadFrame.createFrame(width, height, itemWidth, itemHeight);
        }
    }

    public void onSurfaceCreated(RenderView view, GL11 gl) {
        // The grid quad.
        sGrid.freeHardwareBuffers(gl);
        sGrid.generateHardwareBuffers(gl);

        // The fullscreen quads.
        sFullscreenGrid[0].freeHardwareBuffers(gl);
        sFullscreenGrid[1].freeHardwareBuffers(gl);
        sFullscreenGrid[2].freeHardwareBuffers(gl);
        sFullscreenGrid[0].generateHardwareBuffers(gl);
        sFullscreenGrid[1].generateHardwareBuffers(gl);
        sFullscreenGrid[2].generateHardwareBuffers(gl);

        // Supplementary quads.
        sSelectedGrid.freeHardwareBuffers(gl);
        sVideoGrid.freeHardwareBuffers(gl);
        sLocationGrid.freeHardwareBuffers(gl);
        sSourceIconGrid.freeHardwareBuffers(gl);
        sSelectedGrid.generateHardwareBuffers(gl);
        sVideoGrid.generateHardwareBuffers(gl);
        sLocationGrid.generateHardwareBuffers(gl);
        sSourceIconGrid.generateHardwareBuffers(gl);

        // Text quads.
        sTextGrid.freeHardwareBuffers(gl);
        sTextGrid.generateHardwareBuffers(gl);

        // Frame mesh.
        sFrame.freeHardwareBuffers(gl);
        sFrame.generateHardwareBuffers(gl);

        // Clear the string table.
        sStringTextureTable.clear();

        // Regenerate all the textures.
        mTextureFrame = view.getResource(TEXTURE_FRAME, false);
        mTextureGridFrame = view.getResource(TEXTURE_GRID_FRAME, false);
        mTextureFrameFocus = view.getResource(TEXTURE_FRAME_FOCUS, false);
        mTextureFramePressed = view.getResource(TEXTURE_FRAME_PRESSED, false);
        mTextureLocation = view.getResource(TEXTURE_LOCATION, false);
        mTextureVideo = view.getResource(TEXTURE_VIDEO, false);
        mTextureCheckmarkOn = view.getResource(TEXTURE_CHECKMARK_ON, false);
        mTextureCheckmarkOff = view.getResource(TEXTURE_CHECKMARK_OFF, false);
        mTextureCameraSmall = view.getResource(TEXTURE_CAMERA_SMALL, false);
        mTexturePicasaSmall = view.getResource(TEXTURE_PICASA_SMALL, false);
        mTextureTransparent = view.getResource(TEXTURE_TRANSPARENT, false);
        mTexturePlaceholder = view.getResource(TEXTURE_PLACEHOLDER, false);
        view.loadTexture(mTextureFrame);
        view.loadTexture(mTextureGridFrame);
        view.loadTexture(mTextureFrameFocus);
        view.loadTexture(mTextureFramePressed);

        mTextureSpinner[0] = view.getResource(R.drawable.ic_spinner1);
        mTextureSpinner[1] = view.getResource(R.drawable.ic_spinner2);
        mTextureSpinner[2] = view.getResource(R.drawable.ic_spinner3);
        mTextureSpinner[3] = view.getResource(R.drawable.ic_spinner4);
        mTextureSpinner[4] = view.getResource(R.drawable.ic_spinner5);
        mTextureSpinner[5] = view.getResource(R.drawable.ic_spinner6);
        mTextureSpinner[6] = view.getResource(R.drawable.ic_spinner7);
        mTextureSpinner[7] = view.getResource(R.drawable.ic_spinner8);
    }

    public int getIconForSet(MediaSet set, boolean scaled) {
        // We return the scaled version for HUD rendering and the unscaled
        // version for 3D rendering.
        if (scaled) {
            if (set == null) {
                return R.drawable.icon_folder_small;
            }
            if (set.mPicasaAlbumId != Shared.INVALID) {
                return R.drawable.icon_picasa_small;
            } else if (set.mId == LocalDataSource.CAMERA_BUCKET_ID) {
                return R.drawable.icon_camera_small;
            } else {
                return R.drawable.icon_folder_small;
            }
        } else {
            if (set == null) {
                return R.drawable.icon_folder_small_unscaled;
            }
            if (set.mPicasaAlbumId != Shared.INVALID) {
                return R.drawable.icon_picasa_small_unscaled;
            } else if (set.mId == LocalDataSource.CAMERA_BUCKET_ID) {
                return R.drawable.icon_camera_small_unscaled;
            } else {
                return R.drawable.icon_folder_small_unscaled;
            }
        }
    }
}
