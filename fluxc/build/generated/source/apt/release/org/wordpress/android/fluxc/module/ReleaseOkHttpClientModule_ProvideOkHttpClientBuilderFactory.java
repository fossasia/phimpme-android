package org.wordpress.android.fluxc.module;

import dagger.internal.Factory;
import javax.annotation.Generated;
import okhttp3.OkHttpClient.Builder;

@Generated("dagger.internal.codegen.ComponentProcessor")
public final class ReleaseOkHttpClientModule_ProvideOkHttpClientBuilderFactory implements Factory<Builder> {
  private final ReleaseOkHttpClientModule module;

  public ReleaseOkHttpClientModule_ProvideOkHttpClientBuilderFactory(ReleaseOkHttpClientModule module) {  
    assert module != null;
    this.module = module;
  }

  @Override
  public Builder get() {  
    Builder provided = module.provideOkHttpClientBuilder();
    if (provided == null) {
      throw new NullPointerException("Cannot return null from a non-@Nullable @Provides method");
    }
    return provided;
  }

  public static Factory<Builder> create(ReleaseOkHttpClientModule module) {  
    return new ReleaseOkHttpClientModule_ProvideOkHttpClientBuilderFactory(module);
  }
}

