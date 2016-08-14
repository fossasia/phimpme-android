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
import java.nio.CharBuffer;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

/**
 * A 2D rectangular mesh. Can be drawn textured or untextured. This version is
 * modified from the original Grid.java (found in the SpriteText package in the
 * APIDemos Android sample) to support hardware vertex buffers.
 */

final class GridQuad {
    private FloatBuffer mVertexBuffer;
    private FloatBuffer mOverlayTexCoordBuffer;
    private CharBuffer mIndexBuffer;

    private int mW;
    private int mH;
    private static final int INDEX_COUNT = 4;
    private static final int ORIENTATION_COUNT = 360;
    private int mVertBufferIndex;
    private int mIndexBufferIndex;
    private int mOverlayTextureCoordBufferIndex;
    private boolean mDynamicVBO;
    private float mU;
    private float mV;
    private float mAnimU;
    private float mAnimV;
    private float mWidth;
    private float mHeight;
    private float mAnimWidth;
    private float mAnimHeight;
    private boolean mQuadChanged;
    private float mDefaultAspectRatio;
    private int mBaseTextureCoordBufferIndex;
    private FloatBuffer mBaseTexCoordBuffer;
    private final boolean mOrientedQuad;
    private MatrixStack mMatrix;
    private float[] mCoordsIn = new float[4];
    private float[] mCoordsOut = new float[4];

    public static GridQuad createGridQuad(float width, float height, float xOffset, float yOffset, float uExtents, float vExtents,
            boolean generateOrientedQuads) {
        // generateOrientedQuads = false;
        GridQuad grid = new GridQuad(generateOrientedQuads);
        grid.mWidth = width;
        grid.mHeight = height;
        grid.mAnimWidth = width;
        grid.mAnimHeight = height;
        grid.mDefaultAspectRatio = width / height;
        float widthBy2 = width * 0.5f;
        float heightBy2 = height * 0.5f;
        final float v = vExtents;
        final float u = uExtents;
        if (!generateOrientedQuads) {
            grid.set(0, 0, -widthBy2 + xOffset, -heightBy2 + yOffset, 0.0f, u, v);
            grid.set(1, 0, widthBy2 + xOffset, -heightBy2 + yOffset, 0.0f, 0.0f, v);
            grid.set(0, 1, -widthBy2 + xOffset, heightBy2 + yOffset, 0.0f, u, 0.0f);
            grid.set(1, 1, widthBy2 + xOffset, heightBy2 + yOffset, 0.0f, 0.0f, 0.0f);
        } else {
            for (int i = 0; i < ORIENTATION_COUNT; ++i) {
                grid.set(0, 0, -widthBy2 + xOffset, -heightBy2 + yOffset, 0.0f, u, v, true, i);
                grid.set(1, 0, widthBy2 + xOffset, -heightBy2 + yOffset, 0.0f, 0.0f, v, true, i);
                grid.set(0, 1, -widthBy2 + xOffset, heightBy2 + yOffset, 0.0f, u, 0.0f, true, i);
                grid.set(1, 1, widthBy2 + xOffset, heightBy2 + yOffset, 0.0f, 0.0f, 0.0f, true, i);
            }
        }
        grid.mU = uExtents;
        grid.mV = uExtents;
        return grid;
    }

    public GridQuad(boolean generateOrientedQuads) {
        mOrientedQuad = generateOrientedQuads;
        if (mOrientedQuad) {
            mMatrix = new MatrixStack();
            mMatrix.glLoadIdentity();
        }
        int vertsAcross = 2;
        int vertsDown = 2;
        mW = vertsAcross;
        mH = vertsDown;
        int size = vertsAcross * vertsDown;
        final int FLOAT_SIZE = 4;
        final int CHAR_SIZE = 2;
        final int orientationCount = (!generateOrientedQuads) ? 1 : ORIENTATION_COUNT;
        mVertexBuffer = ByteBuffer.allocateDirect(FLOAT_SIZE * size * 3 * orientationCount).order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        mOverlayTexCoordBuffer = ByteBuffer.allocateDirect(FLOAT_SIZE * size * 2 * orientationCount).order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        mBaseTexCoordBuffer = ByteBuffer.allocateDirect(FLOAT_SIZE * size * 2 * orientationCount).order(ByteOrder.nativeOrder())
                .asFloatBuffer();

        int indexCount = INDEX_COUNT; // using tristrips
        mIndexBuffer = ByteBuffer.allocateDirect(CHAR_SIZE * indexCount * orientationCount).order(ByteOrder.nativeOrder())
                .asCharBuffer();

        /*
         * Initialize triangle list mesh.
         * 
         * [0]-----[ 1] ... | / | | / | | / | [w]-----[w+1] ... | |
         */
        CharBuffer buffer = mIndexBuffer;
        for (int i = 0; i < INDEX_COUNT * orientationCount; ++i) {
            buffer.put(i, (char) i);
        }
        mVertBufferIndex = 0;
    }

    public void setDynamic(boolean dynamic) {
        mDynamicVBO = dynamic;
        if (mOrientedQuad) {
            throw new UnsupportedOperationException("Dynamic Quads can't have orientations");
        }
    }

    public float getWidth() {
        return mWidth;
    }

    public float getHeight() {
        return mHeight;
    }

    public void update(float timeElapsed) {
        mAnimWidth = FloatUtils.animate(mAnimWidth, mWidth, timeElapsed);
        mAnimHeight = FloatUtils.animate(mAnimHeight, mHeight, timeElapsed);
        mAnimU = FloatUtils.animate(mAnimU, mU, timeElapsed);
        mAnimV = FloatUtils.animate(mAnimV, mV, timeElapsed);
        recomputeQuad();
    }

    public void commit() {
        mAnimWidth = mWidth;
        mAnimHeight = mHeight;
        mAnimU = mU;
        mAnimV = mV;
    }

    public void recomputeQuad() {
        mVertexBuffer.clear();
        mBaseTexCoordBuffer.clear();
        float widthBy2 = mAnimWidth * 0.5f;
        float heightBy2 = mAnimHeight * 0.5f;
        float xOffset = 0.0f;
        float yOffset = 0.0f;
        float u = mU;
        float v = mV;
        set(0, 0, -widthBy2 + xOffset, -heightBy2 + yOffset, 0.0f, u, v, false, 0);
        set(1, 0, widthBy2 + xOffset, -heightBy2 + yOffset, 0.0f, 0.0f, v, false, 0);
        set(0, 1, -widthBy2 + xOffset, heightBy2 + yOffset, 0.0f, u, 0.0f, false, 0);
        set(1, 1, widthBy2 + xOffset, heightBy2 + yOffset, 0.0f, 0.0f, 0.0f, false, 0);
        mQuadChanged = true;
    }

    public void resizeQuad(float viewAspect, float u, float v, float imageWidth, float imageHeight) {
        // given the u,v; we know the aspect ratio of the image
        // we have to change one of the co-ords depending upon the image and
        // viewport aspect ratio
        mU = u;
        mV = v;
        float imageAspect = imageWidth / imageHeight;
        float width = mDefaultAspectRatio;
        float height = 1.0f;
        if (viewAspect < 1.0f) {
            height = height * (mDefaultAspectRatio / imageAspect);
            float maxHeight = width / viewAspect;
            if (height > maxHeight) {
                // we need to reduce the width and height proportionately
                float ratio = height / maxHeight;
                height /= ratio;
                width /= ratio;
            }
        } else {
            width = width * (imageAspect / mDefaultAspectRatio);
            float maxWidth = height * viewAspect;
            if (width > maxWidth) {
                float ratio = width / maxWidth;
                width /= ratio;
                height /= ratio;
            }
        }
        mWidth = width;
        mHeight = height;
        commit();
        recomputeQuad();
    }

    public void set(int i, int j, float x, float y, float z, float u, float v) {
        set(i, j, x, y, z, u, v, true, 0);
    }

    private void set(int i, int j, float x, float y, float z, float u, float v, boolean modifyOverlay, int orientationId) {
        if (i < 0 || i >= mW) {
            throw new IllegalArgumentException("i");
        }
        if (j < 0 || j >= mH) {
            throw new IllegalArgumentException("j");
        }
        int index = orientationId * INDEX_COUNT + mW * j + i;
        int posIndex = index * 3;
        mVertexBuffer.put(posIndex, x);
        mVertexBuffer.put(posIndex + 1, y);
        mVertexBuffer.put(posIndex + 2, z);
        int baseTexIndex = index * 2;
        // we can calculate the u,v for the orientation here
        MatrixStack matrix = mMatrix;
        if (matrix != null) {
            orientationId *= 2;
            matrix.glLoadIdentity();
            matrix.glTranslatef(0.5f, 0.5f, 0.0f);
            float itheta = (float) Math.toRadians(orientationId);
            float sini = (float) Math.sin(itheta);
            float scale = 1.0f + (sini * sini) * 0.33333333f;
            scale = 1.0f / scale;
            matrix.glRotatef(-orientationId, 0.0f, 0.0f, 1.0f);
            matrix.glScalef(scale, scale, 1.0f);
            matrix.glTranslatef(-0.5f + (float) (sini * 0.125f / scale), -0.5f
                    + (float) (Math.abs(Math.sin(itheta * 0.5f) * 0.25f)), 0.0f);
            // now we have the desired matrix
            // populate s,t,r,q
            // http://glprogramming.com/red/chapter09.html
            mCoordsIn[0] = u;
            mCoordsIn[1] = v;
            mCoordsIn[2] = 0.0f;
            mCoordsIn[3] = 1.0f;
            matrix.apply(mCoordsIn, mCoordsOut);
            u = mCoordsOut[0] / mCoordsOut[3];
            v = mCoordsOut[1] / mCoordsOut[3];
        }
        mBaseTexCoordBuffer.put(baseTexIndex, u);
        mBaseTexCoordBuffer.put(baseTexIndex + 1, v);
        if (modifyOverlay) {
            int texIndex = index * 2;
            mOverlayTexCoordBuffer.put(texIndex, u);
            mOverlayTexCoordBuffer.put(texIndex + 1, v);
        }
    }

    public void bindArrays(GL10 gl) {
        GL11 gl11 = (GL11) gl;
        // draw using hardware buffers
        gl11.glBindBuffer(GL11.GL_ARRAY_BUFFER, mVertBufferIndex);
        gl11.glVertexPointer(3, GL11.GL_FLOAT, 0, 0);

        gl11.glBindBuffer(GL11.GL_ARRAY_BUFFER, mOverlayTextureCoordBufferIndex);
        if (mDynamicVBO && mQuadChanged) {
            final int texCoordSize = mOverlayTexCoordBuffer.capacity() * 4;
            mOverlayTexCoordBuffer.position(0);
            gl11.glBufferData(GL11.GL_ARRAY_BUFFER, texCoordSize, mOverlayTexCoordBuffer, GL11.GL_DYNAMIC_DRAW);
        }
        gl11.glTexCoordPointer(2, GL11.GL_FLOAT, 0, 0);
        gl11.glClientActiveTexture(GL11.GL_TEXTURE1);
        if (mDynamicVBO && mQuadChanged) {
            final int texCoordSize = mBaseTexCoordBuffer.capacity() * 4;
            mBaseTexCoordBuffer.position(0);
            gl11.glBufferData(GL11.GL_ARRAY_BUFFER, texCoordSize, mBaseTexCoordBuffer, GL11.GL_DYNAMIC_DRAW);
        }
        gl11.glTexCoordPointer(2, GL11.GL_FLOAT, 0, 0);
        gl11.glClientActiveTexture(GL11.GL_TEXTURE0);
        if (mDynamicVBO && mQuadChanged) {
            mQuadChanged = false;
            gl11.glBindBuffer(GL11.GL_ARRAY_BUFFER, mVertBufferIndex);
            final int vertexSize = mVertexBuffer.capacity() * 4;
            mVertexBuffer.position(0);
            gl11.glBufferData(GL11.GL_ARRAY_BUFFER, vertexSize, mVertexBuffer, GL11.GL_DYNAMIC_DRAW);
        }
        gl11.glBindBuffer(GL11.GL_ELEMENT_ARRAY_BUFFER, mIndexBufferIndex);
    }

    public static final void draw(GL11 gl11, float orientationDegrees) {
        // don't call this method unless bindArrays was called
        int orientation = (int) Shared.normalizePositive(orientationDegrees);
        gl11.glDrawElements(GL11.GL_TRIANGLE_STRIP, INDEX_COUNT, GL11.GL_UNSIGNED_SHORT, orientation * INDEX_COUNT);
    }

    public void unbindArrays(GL10 gl) {
        GL11 gl11 = (GL11) gl;
        gl11.glBindBuffer(GL11.GL_ARRAY_BUFFER, 0);
        gl11.glBindBuffer(GL11.GL_ELEMENT_ARRAY_BUFFER, 0);
    }

    public boolean usingHardwareBuffers() {
        return mVertBufferIndex != 0;
    }

    /**
     * When the OpenGL ES device is lost, GL handles become invalidated. In that
     * case, we just want to "forget" the old handles (without explicitly
     * deleting them) and make new ones.
     */
    public void forgetHardwareBuffers() {
        mVertBufferIndex = 0;
        mIndexBufferIndex = 0;
        mOverlayTextureCoordBufferIndex = 0;
    }

    /**
     * Deletes the hardware buffers allocated by this object (if any).
     */
    public void freeHardwareBuffers(GL10 gl) {
        if (mVertBufferIndex != 0) {
            if (gl instanceof GL11) {
                GL11 gl11 = (GL11) gl;
                int[] buffer = new int[1];
                buffer[0] = mVertBufferIndex;
                gl11.glDeleteBuffers(1, buffer, 0);

                buffer[0] = mOverlayTextureCoordBufferIndex;
                gl11.glDeleteBuffers(1, buffer, 0);

                buffer[0] = mIndexBufferIndex;
                gl11.glDeleteBuffers(1, buffer, 0);
            }

            forgetHardwareBuffers();
        }
    }

    /**
     * Allocates hardware buffers on the graphics card and fills them with data
     * if a buffer has not already been previously allocated. Note that this
     * function uses the GL_OES_vertex_buffer_object extension, which is not
     * guaranteed to be supported on every device.
     * 
     * @param gl
     *            A pointer to the OpenGL ES context.
     */

    public void generateHardwareBuffers(GL10 gl) {
        if (mVertBufferIndex == 0) {
            if (gl instanceof GL11) {
                GL11 gl11 = (GL11) gl;
                int[] buffer = new int[1];

                // Allocate and fill the vertex buffer.
                gl11.glGenBuffers(1, buffer, 0);
                mVertBufferIndex = buffer[0];
                gl11.glBindBuffer(GL11.GL_ARRAY_BUFFER, mVertBufferIndex);
                final int vertexSize = mVertexBuffer.capacity() * 4;
                int bufferType = (mDynamicVBO) ? GL11.GL_DYNAMIC_DRAW : GL11.GL_STATIC_DRAW;
                mVertexBuffer.position(0);
                gl11.glBufferData(GL11.GL_ARRAY_BUFFER, vertexSize, mVertexBuffer, bufferType);

                // Allocate and fill the texture coordinate buffer.
                gl11.glGenBuffers(1, buffer, 0);
                mOverlayTextureCoordBufferIndex = buffer[0];
                gl11.glBindBuffer(GL11.GL_ARRAY_BUFFER, mOverlayTextureCoordBufferIndex);
                final int texCoordSize = mOverlayTexCoordBuffer.capacity() * 4;
                mOverlayTexCoordBuffer.position(0);
                gl11.glBufferData(GL11.GL_ARRAY_BUFFER, texCoordSize, mOverlayTexCoordBuffer, bufferType);

                gl11.glGenBuffers(1, buffer, 0);
                mBaseTextureCoordBufferIndex = buffer[0];
                gl11.glBindBuffer(GL11.GL_ARRAY_BUFFER, mBaseTextureCoordBufferIndex);
                mBaseTexCoordBuffer.position(0);
                gl11.glBufferData(GL11.GL_ARRAY_BUFFER, texCoordSize, mBaseTexCoordBuffer, bufferType);

                // Unbind the array buffer.
                gl11.glBindBuffer(GL11.GL_ARRAY_BUFFER, 0);

                // Allocate and fill the index buffer.
                gl11.glGenBuffers(1, buffer, 0);
                mIndexBufferIndex = buffer[0];
                gl11.glBindBuffer(GL11.GL_ELEMENT_ARRAY_BUFFER, mIndexBufferIndex);
                // A char is 2 bytes.
                final int indexSize = mIndexBuffer.capacity() * 2;
                mIndexBuffer.position(0);
                gl11.glBufferData(GL11.GL_ELEMENT_ARRAY_BUFFER, indexSize, mIndexBuffer, GL11.GL_STATIC_DRAW);

                // Unbind the element array buffer.
                gl11.glBindBuffer(GL11.GL_ELEMENT_ARRAY_BUFFER, 0);
            }
        }
    }

}
