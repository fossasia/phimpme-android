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

package vn.mbm.phimp.me.gallery3d.picasa;

import org.xml.sax.Attributes;

/**
 * This class models the album entry kind in the Picasa GData API.
 */
@Entry.Table("albums")
public final class AlbumEntry extends Entry {
    public static final EntrySchema SCHEMA = new EntrySchema(AlbumEntry.class);

    /**
     * The user account that is the sync source for this entry. Must be set
     * before insert/update.
     */
    @Column(Columns.SYNC_ACCOUNT)
    public String syncAccount;

    /**
     * The ETag for the album/photos GData feed.
     */
    @Column(Columns.PHOTOS_ETAG)
    public String photosEtag = null;

    /**
     * True if the contents of the album need to be synchronized. Must be set
     * before insert/update.
     */
    @Column(Columns.PHOTOS_DIRTY)
    public boolean photosDirty;

    /**
     * The "edit" URI of the album.
     */
    @Column(Columns.EDIT_URI)
    public String editUri;

    /**
     * The album owner.
     */
    @Column(Columns.USER)
    public String user;

    /**
     * The title of the album.
     */
    @Column(value = Columns.TITLE)
    public String title;

    /**
     * A short summary of the contents of the album.
     */
    @Column(value = Columns.SUMMARY)
    public String summary;

    /**
     * The date the album was created.
     */
    @Column(Columns.DATE_PUBLISHED)
    public long datePublished;

    /**
     * The date the album was last updated.
     */
    @Column(Columns.DATE_UPDATED)
    public long dateUpdated;

    /**
     * The date the album entry was last edited. May be more recent than
     * dateUpdated.
     */
    @Column(Columns.DATE_EDITED)
    public long dateEdited;

    /**
     * The number of photos in the album.
     */
    @Column(Columns.NUM_PHOTOS)
    public int numPhotos;

    /**
     * The number of bytes of storage that this album uses.
     */
    @Column(Columns.BYTES_USED)
    public long bytesUsed;

    /**
     * The user-specified location associated with the album.
     */
    @Column(Columns.LOCATION_STRING)
    public String locationString;

    /**
     * The thumbnail URL associated with the album.
     */
    @Column(Columns.THUMBNAIL_URL)
    public String thumbnailUrl;

    /**
     * A link to the HTML page associated with the album.
     */
    @Column(Columns.HTML_PAGE_URL)
    public String htmlPageUrl;

    /**
     * Column names specific to album entries.
     */
    public static final class Columns extends PicasaApi.Columns {
        public static final String PHOTOS_ETAG = "photos_etag";
        public static final String USER = "user";
        public static final String BYTES_USED = "bytes_used";
        public static final String NUM_PHOTOS = "num_photos";
        public static final String LOCATION_STRING = "location_string";
        public static final String PHOTOS_DIRTY = "photos_dirty";
    }

    /**
     * Resets values to defaults for object reuse.
     */
    @Override
    public void clear() {
        super.clear();
        syncAccount = null;
        photosDirty = false;
        editUri = null;
        user = null;
        title = null;
        summary = null;
        datePublished = 0;
        dateUpdated = 0;
        dateEdited = 0;
        numPhotos = 0;
        bytesUsed = 0;
        locationString = null;
        thumbnailUrl = null;
        htmlPageUrl = null;
    }

    /**
     * Sets the property value corresponding to the given XML element, if
     * applicable.
     */
    @Override
    public void setPropertyFromXml(String uri, String localName, Attributes attrs, String content) {
        char localNameChar = localName.charAt(0);
        if (uri.equals(GDataParser.GPHOTO_NAMESPACE)) {
            switch (localNameChar) {
            case 'i':
                if (localName.equals("id")) {
                    id = Long.parseLong(content);
                }
                break;
            case 'u':
                if (localName.equals("user")) {
                    user = content;
                }
                break;
            case 'n':
                if (localName.equals("numphotos")) {
                    numPhotos = Integer.parseInt(content);
                }
                break;
            case 'b':
                if (localName.equals("bytesUsed")) {
                    bytesUsed = Long.parseLong(content);
                }
                break;
            }
        } else if (uri.equals(GDataParser.ATOM_NAMESPACE)) {
            switch (localNameChar) {
            case 't':
                if (localName.equals("title")) {
                    title = content;
                }
                break;
            case 's':
                if (localName.equals("summary")) {
                    summary = content;
                }
                break;
            case 'p':
                if (localName.equals("published")) {
                    datePublished = GDataParser.parseAtomTimestamp(content);
                }
                break;
            case 'u':
                if (localName.equals("updated")) {
                    dateUpdated = GDataParser.parseAtomTimestamp(content);
                }
                break;
            case 'l':
                if (localName.equals("link")) {
                    String rel = attrs.getValue("", "rel");
                    String href = attrs.getValue("", "href");
                    if (rel.equals("alternate") && attrs.getValue("", "type").equals("text/html")) {
                        htmlPageUrl = href;
                    } else if (rel.equals("edit")) {
                        editUri = href;
                    }
                }
                break;
            }
        } else if (uri.equals(GDataParser.APP_NAMESPACE)) {
            if (localName.equals("edited")) {
                dateEdited = GDataParser.parseAtomTimestamp(content);
            }
        } else if (uri.equals(GDataParser.MEDIA_RSS_NAMESPACE)) {
            if (localName == "thumbnail") {
                thumbnailUrl = attrs.getValue("", "url");
            }
        }
    }
}
