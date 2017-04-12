package org.wordpress.android.fluxc.module;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader.ImageCache;
import dagger.internal.Factory;
import javax.annotation.Generated;
import javax.inject.Provider;
import org.wordpress.android.fluxc.network.HTTPAuthManager;
import org.wordpress.android.fluxc.network.UserAgent;
import org.wordpress.android.fluxc.network.rest.wpcom.auth.AccessToken;
import org.wordpress.android.fluxc.tools.FluxCImageLoader;

@Generated("dagger.internal.codegen.ComponentProcessor")
public final class ReleaseToolsModule_ProvideFluxCImageLoaderFactory implements Factory<FluxCImageLoader> {
  private final ReleaseToolsModule module;
  private final Provider<RequestQueue> queueProvider;
  private final Provider<ImageCache> imageCacheProvider;
  private final Provider<AccessToken> accessTokenProvider;
  private final Provider<HTTPAuthManager> httpAuthManagerProvider;
  private final Provider<UserAgent> userAgentProvider;

  public ReleaseToolsModule_ProvideFluxCImageLoaderFactory(ReleaseToolsModule module, Provider<RequestQueue> queueProvider, Provider<ImageCache> imageCacheProvider, Provider<AccessToken> accessTokenProvider, Provider<HTTPAuthManager> httpAuthManagerProvider, Provider<UserAgent> userAgentProvider) {  
    assert module != null;
    this.module = module;
    assert queueProvider != null;
    this.queueProvider = queueProvider;
    assert imageCacheProvider != null;
    this.imageCacheProvider = imageCacheProvider;
    assert accessTokenProvider != null;
    this.accessTokenProvider = accessTokenProvider;
    assert httpAuthManagerProvider != null;
    this.httpAuthManagerProvider = httpAuthManagerProvider;
    assert userAgentProvider != null;
    this.userAgentProvider = userAgentProvider;
  }

  @Override
  public FluxCImageLoader get() {  
    FluxCImageLoader provided = module.provideFluxCImageLoader(queueProvider.get(), imageCacheProvider.get(), accessTokenProvider.get(), httpAuthManagerProvider.get(), userAgentProvider.get());
    if (provided == null) {
      throw new NullPointerException("Cannot return null from a non-@Nullable @Provides method");
    }
    return provided;
  }

  public static Factory<FluxCImageLoader> create(ReleaseToolsModule module, Provider<RequestQueue> queueProvider, Provider<ImageCache> imageCacheProvider, Provider<AccessToken> accessTokenProvider, Provider<HTTPAuthManager> httpAuthManagerProvider, Provider<UserAgent> userAgentProvider) {  
    return new ReleaseToolsModule_ProvideFluxCImageLoaderFactory(module, queueProvider, imageCacheProvider, accessTokenProvider, httpAuthManagerProvider, userAgentProvider);
  }
}

