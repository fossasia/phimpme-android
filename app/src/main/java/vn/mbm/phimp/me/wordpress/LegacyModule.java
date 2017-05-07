package vn.mbm.phimp.me.wordpress;

import com.android.volley.toolbox.ImageLoader.ImageCache;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import vn.mbm.phimp.me.MyApplication;

@Module
public class LegacyModule {
    @Singleton
    @Provides
    ImageCache provideImageCache() {
        return MyApplication.getBitmapCache();
    }

}
