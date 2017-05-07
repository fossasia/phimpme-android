package vn.mbm.phimp.me.wordpress;

/**
 * Created by rohanagarwal94 on 6/4/17.
 */

import org.wordpress.android.fluxc.network.rest.wpcom.auth.AppSecrets;

import dagger.Module;
import dagger.Provides;

@Module
public class AppSecretsModule {
    @Provides
    public AppSecrets provideAppSecrets() {
        return new AppSecrets(BuildConfig.OAUTH_APP_ID, BuildConfig.OAUTH_APP_SECRET);
    }
}
