package org.fossasia.phimpme.gallery;

import android.content.Context;
import androidx.annotation.NonNull;
import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.Registry;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.bitmap_recycle.LruBitmapPool;
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory;
import com.bumptech.glide.load.engine.cache.LruResourceCache;
import com.bumptech.glide.load.engine.cache.MemorySizeCalculator;
import com.bumptech.glide.module.AppGlideModule;
import com.bumptech.glide.request.RequestOptions;

/** Created by dnld on 10/03/16. */
public class CustomGlideModule extends AppGlideModule {

  @Override
  public void registerComponents(
      @NonNull Context context, @NonNull Glide glide, @NonNull Registry registry) {}

  @Override
  public void applyOptions(Context context, GlideBuilder builder) {
    // Apply options to the builder here.
    builder.setDefaultRequestOptions(new RequestOptions().format(DecodeFormat.PREFER_ARGB_8888));

    MemorySizeCalculator calculator = new MemorySizeCalculator.Builder(context).build();
    int defaultMemoryCacheSize = calculator.getMemoryCacheSize();
    int defaultBitmapPoolSize = calculator.getBitmapPoolSize();

    int customMemoryCacheSize = (int) (1.2 * defaultMemoryCacheSize);
    int customBitmapPoolSize = (int) (1.2 * defaultBitmapPoolSize);

    builder.setMemoryCache(new LruResourceCache(customMemoryCacheSize));
    builder.setBitmapPool(new LruBitmapPool(customBitmapPoolSize));

    int cacheSize100MegaBytes = 104857600;

    builder.setDiskCache(new InternalCacheDiskCacheFactory(context, cacheSize100MegaBytes));
  }
}
