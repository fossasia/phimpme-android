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

import javax.microedition.khronos.opengles.GL11;

import android.hardware.SensorEvent;
import android.view.KeyEvent;

public abstract class RootLayer extends Layer {
    public void onOrientationChanged(int orientation) {
    }

    public void onSurfaceCreated(RenderView renderView, GL11 gl) {
    }

    public void onSurfaceChanged(RenderView view, int width, int height) {
    }

    public void onSensorChanged(RenderView view, SensorEvent e) {
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return false;
    }

    public boolean onKeyUp(int keyCode, KeyEvent event) {
        return false;
    }

    public void handleLowMemory() {

    }
    
    public void onResume() {
        
    }
    
    public void onPause() {
        
    }
}
