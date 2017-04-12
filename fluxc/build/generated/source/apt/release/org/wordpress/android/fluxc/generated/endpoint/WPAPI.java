package org.wordpress.android.fluxc.generated.endpoint;

import java.lang.String;
import org.wordpress.android.fluxc.annotations.Endpoint;
import org.wordpress.android.fluxc.annotations.endpoint.WPAPIEndpoint;

public class WPAPI {
    @Endpoint("/posts/")
    public static PostsEndpoint posts = new PostsEndpoint("/");

    @Endpoint("/pages/")
    public static PagesEndpoint pages = new PagesEndpoint("/");

    @Endpoint("/media/")
    public static MediaEndpoint media = new MediaEndpoint("/");

    @Endpoint("/comments/")
    public static CommentsEndpoint comments = new CommentsEndpoint("/");

    @Endpoint("/settings/")
    public static WPAPIEndpoint settings = new WPAPIEndpoint("/settings/");

    public static class PostsEndpoint extends WPAPIEndpoint {
        private static final String POSTS_ENDPOINT = "posts/";

        private PostsEndpoint(String previousEndpoint) {
            super(previousEndpoint + POSTS_ENDPOINT);
        }

        @Endpoint("/posts/<id>/")
        public WPAPIEndpoint id(long id) {
            return new WPAPIEndpoint(getEndpoint(), id);
        }
    }

    public static class PagesEndpoint extends WPAPIEndpoint {
        private static final String PAGES_ENDPOINT = "pages/";

        private PagesEndpoint(String previousEndpoint) {
            super(previousEndpoint + PAGES_ENDPOINT);
        }

        @Endpoint("/pages/<id>/")
        public WPAPIEndpoint id(long id) {
            return new WPAPIEndpoint(getEndpoint(), id);
        }
    }

    public static class MediaEndpoint extends WPAPIEndpoint {
        private static final String MEDIA_ENDPOINT = "media/";

        private MediaEndpoint(String previousEndpoint) {
            super(previousEndpoint + MEDIA_ENDPOINT);
        }

        @Endpoint("/media/<id>/")
        public WPAPIEndpoint id(long id) {
            return new WPAPIEndpoint(getEndpoint(), id);
        }
    }

    public static class CommentsEndpoint extends WPAPIEndpoint {
        private static final String COMMENTS_ENDPOINT = "comments/";

        private CommentsEndpoint(String previousEndpoint) {
            super(previousEndpoint + COMMENTS_ENDPOINT);
        }

        @Endpoint("/comments/<id>/")
        public WPAPIEndpoint id(long id) {
            return new WPAPIEndpoint(getEndpoint(), id);
        }
    }
}
