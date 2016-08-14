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
 * This class models the photo entry kind in the Picasa GData API.
 */
@Entry.Table("photos")
public final class PhotoEntry extends Entry {
    public static final EntrySchema SCHEMA = new EntrySchema(PhotoEntry.class);

    /**
     * The user account that is the sync source for this entry. Must be set
     * before insert/update.
     */
    @Column("sync_account")
    public String syncAccount;

    /**
     * The "edit" URI of the photo.
     */
    @Column("edit_uri")
    public String editUri;

    /**
     * The containing album ID.
     */
    @Column(value = "album_id", indexed = true)
    public long albumId;

    /**
     * The display index of the photo within the album. Must be set before
     * insert/update.
     */
    @Column(value = "display_index", indexed = true)
    public int displayIndex;

    /**
     * The title of the photo.
     */
    @Column("title")
    public String title;

    /**
     * A short summary of the photo.
     */
    @Column("summary")
    public String summary;

    /**
     * The date the photo was added.
     */
    @Column("date_published")
    public long datePublished;

    /**
     * The date the photo was last updated.
     */
    @Column("date_updated")
    public long dateUpdated;

    /**
     * The date the photo entry was last edited. May be more recent than
     * dateUpdated.
     */
    @Column("date_edited")
    public long dateEdited;

    /**
     * The date the photo was captured as specified in the EXIF data.
     */
    @Column("date_taken")
    public long dateTaken;

    /**
     * The number of comments associated with the photo.
     */
    @Column("comment_count")
    public int commentCount;

    /**
     * The width of the photo in pixels.
     */
    @Column("width")
    public int width;

    /**
     * The height of the photo in pixels.
     */
    @Column("height")
    public int height;

    /**
     * The rotation of the photo in degrees, if rotation has not already been
     * applied.
     */
    @Column("rotation")
    public int rotation;

    /**
     * The size of the photo is bytes.
     */
    @Column("size")
    public int size;

    /**
     * The latitude associated with the photo.
     */
    @Column("latitude")
    public double latitude;

    /**
     * The longitude associated with the photo.
     */
    @Column("longitude")
    public double longitude;

    /**
     * The "mini-thumbnail" URL for the photo (currently 144px-cropped).
     */
    @Column("thumbnail_url")
    public String thumbnailUrl;

    /**
     * The "screennail" URL for the photo (currently 800px).
     */
    @Column("screennail_url")
    public String screennailUrl;

    /**
     * The "content" URL for the photo (currently 1280px, or a video). The
     * original image URL is not fetched since "imgmax" accepts one size, used
     * to get this resource.
     */
    @Column("content_url")
    public String contentUrl;

    /**
     * The MIME type of the content URL.
     */
    @Column("content_type")
    public String contentType;

    /**
     * A link to the HTML page associated with the album.
     */
    @Column("html_page_url")
    public String htmlPageUrl;

    /**
     * Resets values to defaults for object reuse.
     */
    @Override
    public void clear() {
        super.clear();
        syncAccount = null;
        editUri = null;
        albumId = 0;
        displayIndex = 0;
        title = null;
        summary = null;
        datePublished = 0;
        dateUpdated = 0;
        dateEdited = 0;
        dateTaken = 0;
        commentCount = 0;
        width = 0;
        height = 0;
        rotation = 0;
        size = 0;
        latitude = 0;
        longitude = 0;
        thumbnailUrl = null;
        screennailUrl = null;
        contentUrl = null;
        contentType = null;
        htmlPageUrl = null;
    }

    /**
     * Sets the property value corresponding to the given XML element, if
     * applicable.
     */
    @Override
    public void setPropertyFromXml(String uri, String localName, Attributes attrs, String content) {
        try {
            char localNameChar = localName.charAt(0);
            if (uri.equals(GDataParser.GPHOTO_NAMESPACE)) {
                switch (localNameChar) {
                case 'i':
                    if (localName.equals("id")) {
                        id = Long.parseLong(content);
                    }
                    break;
                case 'a':
                    if (localName.equals("albumid")) {
                        albumId = Long.parseLong(content);
                    }
                    break;
                case 't':
                    if (localName.equals("timestamp")) {
                        dateTaken = Long.parseLong(content);
                    }
                    break;
                case 'c':
                    if (localName.equals("commentCount")) {
                        commentCount = Integer.parseInt(content);
                    }
                    break;
                case 'w':
                    if (localName.equals("width")) {
                        width = Integer.parseInt(content);
                    }
                    break;
                case 'h':
                    if (localName.equals("height")) {
                        height = Integer.parseInt(content);
                    }
                    break;
                case 'r':
                    if (localName.equals("rotation")) {
                        rotation = Integer.parseInt(content);
                    }
                    break;
                case 's':
                    if (localName.equals("size")) {
                        size = Integer.parseInt(content);
                    }
                    break;
                case 'l':
                    if (localName.equals("latitude")) {
                        latitude = Double.parseDouble(content);
                    } else if (localName.equals("longitude")) {
                        longitude = Double.parseDouble(content);
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
                if (localName.equals("thumbnail")) {
                    int width = Integer.parseInt(attrs.getValue("", "width"));
                    int height = Integer.parseInt(attrs.getValue("", "height"));
                    int dimension = Math.max(width, height);
                    String url = attrs.getValue("", "url");
                    if (dimension <= 300) {
                        thumbnailUrl = url;
                    } else {
                        screennailUrl = url;
                    }
                } else if (localName.equals("content")) {
                    // Only replace an existing URL if the MIME type is video.
                    String type = attrs.getValue("", "type");
                    if (contentUrl == null || type.startsWith("video/")) {
                        contentUrl = attrs.getValue("", "url");
                        contentType = type;
                    }
                }
            } else if (uri.equals(GDataParser.GML_NAMESPACE)) {
                if (localName.equals("pos")) {
                    int spaceIndex = content.indexOf(' ');
                    if (spaceIndex != -1) {
                        latitude = Double.parseDouble(content.substring(0, spaceIndex));
                        longitude = Double.parseDouble(content.substring(spaceIndex + 1));
                    }
                }
            }
        } catch (Exception e) {
            return;
        }
    }

}
