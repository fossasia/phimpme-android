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

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

import javax.microedition.khronos.opengles.GL11;

import vn.mbm.phimp.me.gallery3d.app.Res;
import android.os.SystemClock;

public final class LoadingLayer extends Layer {
    private static final float FADE_INTERVAL = 0.5f;
    private static final float GRAY_VALUE = 0.1f;
    @SuppressWarnings("static-access")
	private static final int[] PRELOAD_RESOURCES_ASYNC_UNSCALED = { Res.drawable.stack_frame, Res.drawable.grid_frame,
            Res.drawable.stack_frame_focus, Res.drawable.stack_frame_gold, Res.drawable.btn_location_filter_unscaled,
            Res.drawable.videooverlay, Res.drawable.grid_check_on, Res.drawable.grid_check_off, Res.drawable.icon_camera_small_unscaled,
            Res.drawable.icon_picasa_small_unscaled };

    private static final int[] PRELOAD_RESOURCES_ASYNC_SCALED = {/*
                                                                  * Res.drawable.btn_camera_pressed
                                                                  * ,
                                                                  * Res.drawable.
                                                                  * btn_camera_focus
                                                                  * ,
                                                                  * Res.drawable.
                                                                  * fullscreen_hud_bg
                                                                  * ,
                                                                  * Res.drawable.
                                                                  * icon_delete,
                                                                  * Res.drawable.
                                                                  * icon_edit,
                                                                  * Res.drawable.
                                                                  * icon_more,
                                                                  * Res.drawable.
                                                                  * icon_share,
                                                                  * Res.drawable.
                                                                  * selection_bg_upper
                                                                  * ,
                                                                  * Res.drawable.
                                                                  * selection_menu_bg
                                                                  * ,
                                                                  * Res.drawable.
                                                                  * selection_menu_bg_pressed
                                                                  * ,
                                                                  * Res.drawable.
                                                                  * selection_menu_bg_pressed_left
                                                                  * ,
                                                                  * Res.drawable.
                                                                  * selection_menu_bg_pressed_right
                                                                  * ,
                                                                  * Res.drawable.
                                                                  * selection_menu_divider
                                                                  * ,
                                                                  * Res.drawable.
                                                                  * timebar_bg,
                                                                  * Res.drawable.
                                                                  * timebar_knob
                                                                  * ,
                                                                  * Res.drawable.
                                                                  * timebar_knob_pressed
                                                                  * ,
                                                                  * Res.drawable.
                                                                  * timebar_prev
                                                                  * ,
                                                                  * Res.drawable.
                                                                  * timebar_next
                                                                  * ,
                                                                  * Res.drawable.
                                                                  * mode_grid,
                                                                  * Res.drawable.
                                                                  * mode_stack,
                                                                  * Res.drawable.
                                                                  * icon_camera_small
                                                                  * ,
                                                                  * Res.drawable.
                                                                  * icon_location_small
                                                                  * ,
                                                                  * Res.drawable.
                                                                  * icon_picasa_small
                                                                  * ,
                                                                  * Res.drawable.
                                                                  * icon_folder_small
                                                                  * ,
                                                                  * Res.drawable.
                                                                  * scroller_new
                                                                  * ,
                                                                  * Res.drawable.
                                                                  * scroller_pressed_new
                                                                  * ,
                                                                  * Res.drawable.
                                                                  * btn_camera,
                                                                  * Res.drawable.
                                                                  * btn_play,
                                                                  * Res.drawable
                                                                  * .pathbar_bg,
                                                                  * Res.drawable.
                                                                  * pathbar_cap,
                                                                  * Res.drawable.
                                                                  * pathbar_join
                                                                  * ,
                                                                  * Res.drawable.
                                                                  * transparent,
                                                                  * Res.drawable.
                                                                  * icon_home_small
                                                                  * ,
                                                                  * Res.drawable.
                                                                  * ic_fs_details
                                                                  * ,
                                                                  * Res.drawable.
                                                                  * ic_spinner1,
                                                                  * Res.drawable.
                                                                  * ic_spinner2,
                                                                  * Res.drawable.
                                                                  * ic_spinner3,
                                                                  * Res.drawable.
                                                                  * ic_spinner4,
                                                                  * Res.drawable.
                                                                  * ic_spinner5,
                                                                  * Res.drawable.
                                                                  * ic_spinner6,
                                                                  * Res.drawable.
                                                                  * ic_spinner7,
                                                                  * Res.drawable.
                                                                  * ic_spinner8
                                                                  */};

    private boolean mLoaded = false;
    private final FloatAnim mOpacity = new FloatAnim(1f);
    private IntBuffer mVertexBuffer;

    public LoadingLayer() {
        // Create vertex buffer for a screen-spanning quad.
        int dimension = 10000 * 0x10000;
        int[] vertices = { -dimension, -dimension, 0, dimension, -dimension, 0, -dimension, dimension, 0, dimension, dimension, 0 };
        ByteBuffer vertexByteBuffer = ByteBuffer.allocateDirect(vertices.length * 4);
        vertexByteBuffer.order(ByteOrder.nativeOrder());
        mVertexBuffer = vertexByteBuffer.asIntBuffer();
        mVertexBuffer.put(vertices);
        mVertexBuffer.position(0);
    }

    public boolean isLoaded() {
        return true;
    }

    @Override
    public void generate(RenderView view, RenderView.Lists lists) {
        // Add to drawing list.
        lists.blendedList.add(this);

        // Start loading textures.
        int[] textures = PRELOAD_RESOURCES_ASYNC_UNSCALED;
        for (int i = 0; i != textures.length; ++i) {
            view.loadTexture(view.getResource(textures[i], false));
        }
        textures = PRELOAD_RESOURCES_ASYNC_SCALED;
        for (int i = 0; i != textures.length; ++i) {
            view.loadTexture(view.getResource(textures[i]));
        }
    }

    @Override
    public void renderBlended(RenderView view, GL11 gl) {
        // Wait for textures to finish loading before fading out.
        if (!mLoaded) {
            // Request that the view upload all loaded textures.
            view.processAllTextures();

            // Determine if all textures have loaded.
            int[] textures = PRELOAD_RESOURCES_ASYNC_SCALED;
            boolean complete = true;
            for (int i = 0; i != textures.length; ++i) {
                if (view.getResource(textures[i]).mState != Texture.STATE_LOADED) {
                    complete = false;
                    break;
                }
            }
            textures = PRELOAD_RESOURCES_ASYNC_UNSCALED;
            for (int i = 0; i != textures.length; ++i) {
                if (view.getResource(textures[i], false).mState != Texture.STATE_LOADED) {
                    complete = false;
                    break;
                }
            }
            if (complete) {
                mLoaded = true;
                mOpacity.animateValue(0f, FADE_INTERVAL, SystemClock.uptimeMillis());
            }
        }

        // Draw the loading screen.
        float alpha = mOpacity.getValue(view.getFrameTime());
        if (alpha > 0.004f) {
            float gray = GRAY_VALUE * alpha;
            gl.glTexEnvf(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, GL11.GL_MODULATE);
            gl.glColor4f(gray, gray, gray, alpha);
            gl.glVertexPointer(3, GL11.GL_FIXED, 0, mVertexBuffer);
            gl.glDisable(GL11.GL_TEXTURE_2D);
            gl.glDisable(GL11.GL_DEPTH_TEST);
            gl.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, 4);
            gl.glEnable(GL11.GL_DEPTH_TEST);
            gl.glEnable(GL11.GL_TEXTURE_2D);
            view.resetColor();
        } else {
            // Hide the layer once completely faded out.
            setHidden(true);
        }
    }

    void reset() {
        mLoaded = false;
        mOpacity.setValue(1.0f);
        setHidden(false);
    }
}
