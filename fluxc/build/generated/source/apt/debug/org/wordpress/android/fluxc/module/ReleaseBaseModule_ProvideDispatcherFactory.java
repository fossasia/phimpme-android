package org.wordpress.android.fluxc.module;

import dagger.internal.Factory;
import javax.annotation.Generated;
import org.wordpress.android.fluxc.Dispatcher;

@Generated("dagger.internal.codegen.ComponentProcessor")
public final class ReleaseBaseModule_ProvideDispatcherFactory implements Factory<Dispatcher> {
  private final ReleaseBaseModule module;

  public ReleaseBaseModule_ProvideDispatcherFactory(ReleaseBaseModule module) {  
    assert module != null;
    this.module = module;
  }

  @Override
  public Dispatcher get() {  
    Dispatcher provided = module.provideDispatcher();
    if (provided == null) {
      throw new NullPointerException("Cannot return null from a non-@Nullable @Provides method");
    }
    return provided;
  }

  public static Factory<Dispatcher> create(ReleaseBaseModule module) {  
    return new ReleaseBaseModule_ProvideDispatcherFactory(module);
  }
}

