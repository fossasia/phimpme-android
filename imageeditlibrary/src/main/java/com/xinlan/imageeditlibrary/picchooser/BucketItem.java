/*
 * Copyright 2013 Thomas Hoffmann
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.xinlan.imageeditlibrary.picchooser;

class BucketItem extends GridItem {

    final int id;
    int images = 1;

    /**
     * Creates a new BucketItem
     *
     * @param n the name of the bucket
     * @param p the absolute path to the bucket
     * @param i the bucket ID
     */
    public BucketItem(final String n, final String p,final String taken, int i) {
        super(n, p,taken,0);
        id = i;
    }

}
