package org.wordpress.android.fluxc.module;

import android.content.Context;
import dagger.internal.Factory;
import javax.annotation.Generated;

@Generated("dagger.internal.codegen.ComponentProcessor")
public final class AppContextModule_ProvidesContextFactory implements Factory<Context> {
  private final AppContextModule module;

  public AppContextModule_ProvidesContextFactory(AppContextModule module) {  
    assert module != null;
    this.module = module;
  }

  @Override
  public Context get() {  
    Context provided = module.providesContext();
    if (provided == null) {
      throw new NullPointerException("Cannot return null from a non-@Nullable @Provides method");
    }
    return provided;
  }

  public static Factory<Context> create(AppContextModule module) {  
    return new AppContextModule_ProvidesContextFactory(module);
  }
}

