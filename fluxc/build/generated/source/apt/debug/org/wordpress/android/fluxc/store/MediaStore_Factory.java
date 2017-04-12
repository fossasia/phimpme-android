package org.wordpress.android.fluxc.store;

import dagger.MembersInjector;
import dagger.internal.Factory;
import javax.annotation.Generated;
import javax.inject.Provider;
import org.wordpress.android.fluxc.Dispatcher;
import org.wordpress.android.fluxc.network.rest.wpcom.media.MediaRestClient;
import org.wordpress.android.fluxc.network.xmlrpc.media.MediaXMLRPCClient;

@Generated("dagger.internal.codegen.ComponentProcessor")
public final class MediaStore_Factory implements Factory<MediaStore> {
  private final MembersInjector<MediaStore> membersInjector;
  private final Provider<Dispatcher> dispatcherProvider;
  private final Provider<MediaRestClient> restClientProvider;
  private final Provider<MediaXMLRPCClient> xmlrpcClientProvider;

  public MediaStore_Factory(MembersInjector<MediaStore> membersInjector, Provider<Dispatcher> dispatcherProvider, Provider<MediaRestClient> restClientProvider, Provider<MediaXMLRPCClient> xmlrpcClientProvider) {  
    assert membersInjector != null;
    this.membersInjector = membersInjector;
    assert dispatcherProvider != null;
    this.dispatcherProvider = dispatcherProvider;
    assert restClientProvider != null;
    this.restClientProvider = restClientProvider;
    assert xmlrpcClientProvider != null;
    this.xmlrpcClientProvider = xmlrpcClientProvider;
  }

  @Override
  public MediaStore get() {  
    MediaStore instance = new MediaStore(dispatcherProvider.get(), restClientProvider.get(), xmlrpcClientProvider.get());
    membersInjector.injectMembers(instance);
    return instance;
  }

  public static Factory<MediaStore> create(MembersInjector<MediaStore> membersInjector, Provider<Dispatcher> dispatcherProvider, Provider<MediaRestClient> restClientProvider, Provider<MediaXMLRPCClient> xmlrpcClientProvider) {  
    return new MediaStore_Factory(membersInjector, dispatcherProvider, restClientProvider, xmlrpcClientProvider);
  }
}

