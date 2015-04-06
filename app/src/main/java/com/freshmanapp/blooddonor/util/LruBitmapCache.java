package com.freshmanapp.blooddonor.util;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import com.android.volley.toolbox.ImageLoader.ImageCache;
/**
 * Created by Ramkumar on 06/04/15.
 * http://www.androidhive.info/2014/07/android-custom-listview-with-image-and-text-using-volley/
 */
public class LruBitmapCache extends LruCache<String, Bitmap> implements
        ImageCache {
    public static int getDefaultLruCacheSize() {
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 8;

        return cacheSize;
    }

    public LruBitmapCache() {
        this(getDefaultLruCacheSize());
    }

    public LruBitmapCache(int sizeInKiloBytes) {
        super(sizeInKiloBytes);
    }

    @Override
    protected int sizeOf(String key, Bitmap value) {
        return value.getRowBytes() * value.getHeight() / 1024;
    }

    @Override
    public Bitmap getBitmap(String url) {
        return get(url);
    }

    @Override
    public void putBitmap(String url, Bitmap bitmap) {
        put(url, bitmap);
    }
}
