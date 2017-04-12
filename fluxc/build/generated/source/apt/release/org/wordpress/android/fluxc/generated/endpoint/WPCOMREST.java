package org.wordpress.android.fluxc.generated.endpoint;

import java.lang.String;
import org.wordpress.android.fluxc.annotations.Endpoint;
import org.wordpress.android.fluxc.annotations.endpoint.WPComEndpoint;

public class WPCOMREST {
    @Endpoint("/me/")
    public static MeEndpoint me = new MeEndpoint("/");

    @Endpoint("/sites/")
    public static SitesEndpoint sites = new SitesEndpoint("/");

    @Endpoint("/users/")
    public static UsersEndpoint users = new UsersEndpoint("/");

    @Endpoint("/is-available/")
    public static Is_availableEndpoint is_available = new Is_availableEndpoint("/");

    @Endpoint("/auth/")
    public static AuthEndpoint auth = new AuthEndpoint("/");

    public static class MeEndpoint extends WPComEndpoint {
        private static final String ME_ENDPOINT = "me/";

        @Endpoint("/me/settings/")
        public WPComEndpoint settings = new WPComEndpoint(getEndpoint() + "settings/");

        @Endpoint("/me/sites/")
        public WPComEndpoint sites = new WPComEndpoint(getEndpoint() + "sites/");

        @Endpoint("/me/send-verification-email/")
        public WPComEndpoint send_verification_email = new WPComEndpoint(getEndpoint() + "send-verification-email/");

        private MeEndpoint(String previousEndpoint) {
            super(previousEndpoint + ME_ENDPOINT);
        }
    }

    public static class SitesEndpoint extends WPComEndpoint {
        private static final String SITES_ENDPOINT = "sites/";

        @Endpoint("/sites/new/")
        public WPComEndpoint new_ = new WPComEndpoint(getEndpoint() + "new/");

        private SitesEndpoint(String previousEndpoint) {
            super(previousEndpoint + SITES_ENDPOINT);
        }

        @Endpoint("/sites/$site/")
        public SiteEndpoint site(long siteId) {
            return new SiteEndpoint(getEndpoint(), siteId);
        }

        public static class SiteEndpoint extends WPComEndpoint {
            @Endpoint("/sites/$site/post-formats/")
            public WPComEndpoint post_formats = new WPComEndpoint(getEndpoint() + "post-formats/");

            @Endpoint("/sites/$site/delete/")
            public WPComEndpoint delete = new WPComEndpoint(getEndpoint() + "delete/");

            @Endpoint("/sites/$site/exports/")
            public ExportsEndpoint exports = new ExportsEndpoint(getEndpoint());

            @Endpoint("/sites/$site/posts/")
            public PostsEndpoint posts = new PostsEndpoint(getEndpoint());

            @Endpoint("/sites/$site/media/")
            public MediaEndpoint media = new MediaEndpoint(getEndpoint());

            @Endpoint("/sites/$site/comments/")
            public CommentsEndpoint comments = new CommentsEndpoint(getEndpoint());

            @Endpoint("/sites/$site/taxonomies/")
            public TaxonomiesEndpoint taxonomies = new TaxonomiesEndpoint(getEndpoint());

            private SiteEndpoint(String previousEndpoint, long siteId) {
                super(previousEndpoint, siteId);
            }

            public static class ExportsEndpoint extends WPComEndpoint {
                private static final String EXPORTS_ENDPOINT = "exports/";

                @Endpoint("/sites/$site/exports/start/")
                public WPComEndpoint start = new WPComEndpoint(getEndpoint() + "start/");

                private ExportsEndpoint(String previousEndpoint) {
                    super(previousEndpoint + EXPORTS_ENDPOINT);
                }
            }

            public static class PostsEndpoint extends WPComEndpoint {
                private static final String POSTS_ENDPOINT = "posts/";

                @Endpoint("/sites/$site/posts/new/")
                public WPComEndpoint new_ = new WPComEndpoint(getEndpoint() + "new/");

                private PostsEndpoint(String previousEndpoint) {
                    super(previousEndpoint + POSTS_ENDPOINT);
                }

                @Endpoint("/sites/$site/posts/$post_ID/")
                public PostEndpoint post(long postId) {
                    return new PostEndpoint(getEndpoint(), postId);
                }

                @Endpoint("/sites/$site/posts/slug:$post_slug/")
                public WPComEndpoint slug(String slug) {
                    return new WPComEndpoint(getEndpoint(), "slug:" + slug);
                }

                public static class PostEndpoint extends WPComEndpoint {
                    @Endpoint("/sites/$site/posts/$post_ID/delete/")
                    public WPComEndpoint delete = new WPComEndpoint(getEndpoint() + "delete/");

                    @Endpoint("/sites/$site/posts/$post_ID/replies/")
                    public RepliesEndpoint replies = new RepliesEndpoint(getEndpoint());

                    private PostEndpoint(String previousEndpoint, long postId) {
                        super(previousEndpoint, postId);
                    }

                    public static class RepliesEndpoint extends WPComEndpoint {
                        private static final String REPLIES_ENDPOINT = "replies/";

                        @Endpoint("/sites/$site/posts/$post_ID/replies/new/")
                        public WPComEndpoint new_ = new WPComEndpoint(getEndpoint() + "new/");

                        private RepliesEndpoint(String previousEndpoint) {
                            super(previousEndpoint + REPLIES_ENDPOINT);
                        }
                    }
                }
            }

            public static class MediaEndpoint extends WPComEndpoint {
                private static final String MEDIA_ENDPOINT = "media/";

                @Endpoint("/sites/$site/media/new/")
                public WPComEndpoint new_ = new WPComEndpoint(getEndpoint() + "new/");

                private MediaEndpoint(String previousEndpoint) {
                    super(previousEndpoint + MEDIA_ENDPOINT);
                }

                @Endpoint("/sites/$site/media/$media_ID/")
                public MediaItemEndpoint item(long mediaId) {
                    return new MediaItemEndpoint(getEndpoint(), mediaId);
                }

                public static class MediaItemEndpoint extends WPComEndpoint {
                    @Endpoint("/sites/$site/media/$media_ID/delete/")
                    public WPComEndpoint delete = new WPComEndpoint(getEndpoint() + "delete/");

                    private MediaItemEndpoint(String previousEndpoint, long mediaId) {
                        super(previousEndpoint, mediaId);
                    }
                }
            }

            public static class CommentsEndpoint extends WPComEndpoint {
                private static final String COMMENTS_ENDPOINT = "comments/";

                private CommentsEndpoint(String previousEndpoint) {
                    super(previousEndpoint + COMMENTS_ENDPOINT);
                }

                @Endpoint("/sites/$site/comments/$comment_ID/")
                public CommentEndpoint comment(long commentId) {
                    return new CommentEndpoint(getEndpoint(), commentId);
                }

                public static class CommentEndpoint extends WPComEndpoint {
                    @Endpoint("/sites/$site/comments/$comment_ID/delete/")
                    public WPComEndpoint delete = new WPComEndpoint(getEndpoint() + "delete/");

                    @Endpoint("/sites/$site/comments/$comment_ID/replies/")
                    public RepliesEndpoint replies = new RepliesEndpoint(getEndpoint());

                    @Endpoint("/sites/$site/comments/$comment_ID/likes/")
                    public LikesEndpoint likes = new LikesEndpoint(getEndpoint());

                    private CommentEndpoint(String previousEndpoint, long commentId) {
                        super(previousEndpoint, commentId);
                    }

                    public static class RepliesEndpoint extends WPComEndpoint {
                        private static final String REPLIES_ENDPOINT = "replies/";

                        @Endpoint("/sites/$site/comments/$comment_ID/replies/new/")
                        public WPComEndpoint new_ = new WPComEndpoint(getEndpoint() + "new/");

                        private RepliesEndpoint(String previousEndpoint) {
                            super(previousEndpoint + REPLIES_ENDPOINT);
                        }
                    }

                    public static class LikesEndpoint extends WPComEndpoint {
                        private static final String LIKES_ENDPOINT = "likes/";

                        @Endpoint("/sites/$site/comments/$comment_ID/likes/new/")
                        public WPComEndpoint new_ = new WPComEndpoint(getEndpoint() + "new/");

                        @Endpoint("/sites/$site/comments/$comment_ID/likes/mine/")
                        public MineEndpoint mine = new MineEndpoint(getEndpoint());

                        private LikesEndpoint(String previousEndpoint) {
                            super(previousEndpoint + LIKES_ENDPOINT);
                        }

                        public static class MineEndpoint extends WPComEndpoint {
                            private static final String MINE_ENDPOINT = "mine/";

                            @Endpoint("/sites/$site/comments/$comment_ID/likes/mine/delete/")
                            public WPComEndpoint delete = new WPComEndpoint(getEndpoint() + "delete/");

                            private MineEndpoint(String previousEndpoint) {
                                super(previousEndpoint + MINE_ENDPOINT);
                            }
                        }
                    }
                }
            }

            public static class TaxonomiesEndpoint extends WPComEndpoint {
                private static final String TAXONOMIES_ENDPOINT = "taxonomies/";

                private TaxonomiesEndpoint(String previousEndpoint) {
                    super(previousEndpoint + TAXONOMIES_ENDPOINT);
                }

                @Endpoint("/sites/$site/taxonomies/$taxonomy/")
                public TaxonomyEndpoint taxonomy(String taxonomy) {
                    return new TaxonomyEndpoint(getEndpoint(), taxonomy);
                }

                public static class TaxonomyEndpoint extends WPComEndpoint {
                    @Endpoint("/sites/$site/taxonomies/$taxonomy/terms/")
                    public TermsEndpoint terms = new TermsEndpoint(getEndpoint());

                    private TaxonomyEndpoint(String previousEndpoint, String taxonomy) {
                        super(previousEndpoint, taxonomy);
                    }

                    public static class TermsEndpoint extends WPComEndpoint {
                        private static final String TERMS_ENDPOINT = "terms/";

                        @Endpoint("/sites/$site/taxonomies/$taxonomy/terms/new/")
                        public WPComEndpoint new_ = new WPComEndpoint(getEndpoint() + "new/");

                        private TermsEndpoint(String previousEndpoint) {
                            super(previousEndpoint + TERMS_ENDPOINT);
                        }

                        @Endpoint("/sites/$site/taxonomies/$taxonomy/terms/slug:$slug/")
                        public WPComEndpoint slug(String slug) {
                            return new WPComEndpoint(getEndpoint(), "slug:" + slug);
                        }
                    }
                }
            }
        }
    }

    public static class UsersEndpoint extends WPComEndpoint {
        private static final String USERS_ENDPOINT = "users/";

        @Endpoint("/users/new/")
        public WPComEndpoint new_ = new WPComEndpoint(getEndpoint() + "new/");

        private UsersEndpoint(String previousEndpoint) {
            super(previousEndpoint + USERS_ENDPOINT);
        }
    }

    public static class Is_availableEndpoint extends WPComEndpoint {
        private static final String IS_AVAILABLE_ENDPOINT = "is-available/";

        @Endpoint("/is-available/email/")
        public WPComEndpoint email = new WPComEndpoint(getEndpoint() + "email/");

        @Endpoint("/is-available/username/")
        public WPComEndpoint username = new WPComEndpoint(getEndpoint() + "username/");

        @Endpoint("/is-available/blog/")
        public WPComEndpoint blog = new WPComEndpoint(getEndpoint() + "blog/");

        @Endpoint("/is-available/domain/")
        public WPComEndpoint domain = new WPComEndpoint(getEndpoint() + "domain/");

        private Is_availableEndpoint(String previousEndpoint) {
            super(previousEndpoint + IS_AVAILABLE_ENDPOINT);
        }
    }

    public static class AuthEndpoint extends WPComEndpoint {
        private static final String AUTH_ENDPOINT = "auth/";

        @Endpoint("/auth/send-login-email/")
        public WPComEndpoint send_login_email = new WPComEndpoint(getEndpoint() + "send-login-email/");

        private AuthEndpoint(String previousEndpoint) {
            super(previousEndpoint + AUTH_ENDPOINT);
        }
    }
}
