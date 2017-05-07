package vn.mbm.phimp.me.wordpress;

import org.wordpress.android.fluxc.module.AppContextModule;
import org.wordpress.android.fluxc.module.ReleaseBaseModule;
import org.wordpress.android.fluxc.module.ReleaseNetworkModule;
import org.wordpress.android.fluxc.module.ReleaseOkHttpClientModule;
import org.wordpress.android.fluxc.module.ReleaseStoreModule;
import org.wordpress.android.fluxc.module.ReleaseToolsModule;

import javax.inject.Singleton;

import dagger.Component;
import vn.mbm.phimp.me.MyApplication;
import vn.mbm.phimp.me.Upload;

/**
 * Created by rohanagarwal94 on 6/4/17.
 */

@Singleton
@Component(modules = {
        AppContextModule.class,
        AppSecretsModule.class,
        ReleaseBaseModule.class,
        ReleaseOkHttpClientModule.class,
        ReleaseNetworkModule.class,
        LegacyModule.class,
        ReleaseStoreModule.class,
        ReleaseToolsModule.class
})
public interface AppComponent {
    void inject(MyApplication application);
    void inject(SignInActivity object);
    void inject(SignInFragment object);
    void inject(SignInDialogFragment object);

    void inject(SitePickerActivity object);
    void inject(Upload object);
    void inject(SitePickerAdapter object);
    void inject(MediaBrowserActivity object);
    void inject(MediaGridFragment object);
    void inject(MediaItemFragment object);
    void inject(MediaEditFragment object);

    void inject(MediaUploadService object);
    void inject(MediaDeleteService object);
}
