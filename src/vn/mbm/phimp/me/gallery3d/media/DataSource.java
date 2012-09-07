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

import java.util.ArrayList;

public interface DataSource {
    // Load the sets to be displayed.
    void loadMediaSets(final MediaFeed feed);

    // rangeStart->rangeEnd is inclusive
    // Pass in Shared.INFINITY for the rangeEnd to load all items.
    void loadItemsForSet(final MediaFeed feed, final MediaSet parentSet, int rangeStart, int rangeEnd);

    // Called when the data source will no longer be used.
    void shutdown();

    boolean performOperation(int operation, ArrayList<MediaBucket> mediaBuckets, Object data);

    DiskCache getThumbnailCache();
    
    // This method is called so that we can setup listeners for any databases that the datasource uses
    String[] getDatabaseUris();

    // Called when the user explicitly requests a refresh, or when the application is brought to the foreground.
    // Alternatively, when one or more of the database's data changes, this method will be called.
    void refresh(final MediaFeed feed, final String[] databaseUris);
    
}
