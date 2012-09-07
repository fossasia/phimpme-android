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

public final class Vector3f {
    public float x;
    public float y;
    public float z;

    public Vector3f() {

    }

    public Vector3f(float x, float y, float z) {
        set(x, y, z);
    }

    public Vector3f(Vector3f vector) {
        x = vector.x;
        y = vector.y;
        z = vector.z;
    }

    public void set(Vector3f vector) {
        x = vector.x;
        y = vector.y;
        z = vector.z;
    }

    public void set(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void add(Vector3f vector) {
        x += vector.x;
        y += vector.y;
        z += vector.z;
    }

    public void subtract(Vector3f vector) {
        x -= vector.x;
        y -= vector.y;
        z -= vector.z;
    }

    public boolean equals(Vector3f vector) {
        if (x == vector.x && y == vector.y && z == vector.z)
            return true;
        return false;
    }

    @Override
    public String toString() {
        return (new String("(" + x + ", " + y + ", " + z + ")"));
    }

    public void add(float x, float y, float z) {
        this.x += x;
        this.y += y;
        this.z += z;
    }

    public void scale(float spreadValueX, float spreadValueY, float spreadValueZ) {
        x *= spreadValueX;
        y *= spreadValueY;
        z *= spreadValueZ;
    }
}
