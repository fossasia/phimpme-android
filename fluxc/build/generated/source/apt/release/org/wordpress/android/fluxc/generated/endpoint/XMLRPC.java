package org.wordpress.android.fluxc.generated.endpoint;

import java.lang.Override;
import java.lang.String;
import org.wordpress.android.fluxc.annotations.Endpoint;

public enum XMLRPC {
    @Endpoint("wp.getOptions")
    GET_OPTIONS("wp.getOptions"),

    @Endpoint("wp.getPostFormats")
    GET_POST_FORMATS("wp.getPostFormats"),

    @Endpoint("wp.getUsersBlogs")
    GET_USERS_BLOGS("wp.getUsersBlogs"),

    @Endpoint("wp.getMediaLibrary")
    GET_MEDIA_LIBRARY("wp.getMediaLibrary"),

    @Endpoint("wp.getMediaItem")
    GET_MEDIA_ITEM("wp.getMediaItem"),

    @Endpoint("wp.getPost")
    GET_POST("wp.getPost"),

    @Endpoint("wp.getPosts")
    GET_POSTS("wp.getPosts"),

    @Endpoint("wp.newPost")
    NEW_POST("wp.newPost"),

    @Endpoint("wp.editPost")
    EDIT_POST("wp.editPost"),

    @Endpoint("wp.deletePost")
    DELETE_POST("wp.deletePost"),

    @Endpoint("wp.getTerm")
    GET_TERM("wp.getTerm"),

    @Endpoint("wp.getTerms")
    GET_TERMS("wp.getTerms"),

    @Endpoint("wp.newTerm")
    NEW_TERM("wp.newTerm"),

    @Endpoint("wp.uploadFile")
    UPLOAD_FILE("wp.uploadFile"),

    @Endpoint("wp.newComment")
    NEW_COMMENT("wp.newComment"),

    @Endpoint("wp.getComment")
    GET_COMMENT("wp.getComment"),

    @Endpoint("wp.getComments")
    GET_COMMENTS("wp.getComments"),

    @Endpoint("wp.deleteComment")
    DELETE_COMMENT("wp.deleteComment"),

    @Endpoint("wp.editComment")
    EDIT_COMMENT("wp.editComment"),

    @Endpoint("system.listMethods")
    LIST_METHODS("system.listMethods"),

    @Endpoint("wp.deletePost")
    DELETE_MEDIA("wp.deletePost"),

    @Endpoint("wp.editPost")
    EDIT_MEDIA("wp.editPost"),

    @Endpoint("wp.getUsersBlogs")
    GET_USERS_SITES("wp.getUsersBlogs");

    private final String mEndpoint;

    XMLRPC(String endpoint) {
        mEndpoint = endpoint;
    }

    @Override
    public String toString() {
        return mEndpoint;
    }
}
