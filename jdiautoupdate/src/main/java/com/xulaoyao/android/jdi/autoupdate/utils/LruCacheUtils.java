package com.xulaoyao.android.jdi.autoupdate.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.support.v4.util.LruCache;

/**
 * Created by renwoxing on 2017/8/10.
 */

public class LruCacheUtils {

    private static LruCacheUtils lruCacheUtils;

    //private DiskLruCache diskLruCache; //LRU磁盘缓存
    private LruCache<String, String> lruCache; //LRU内存缓存
    private Context mContext;

    private LruCacheUtils(Context context) {
        if (null != context){
            this.mContext = context;
        }
        //int MaxMemory = (int)(Runtime.getRuntime().maxMemory() / 1024);// kB
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        int memoryClass = am.getMemoryClass();
        final int cacheSize = memoryClass / 8 * 1024 * 1024;   //单位大小为字节  八分之一的内存作为缓存大小
        lruCache = new LruCache<>(cacheSize);
    }

    public static LruCacheUtils newInstance(Context context) {
        if (lruCacheUtils == null) {
            lruCacheUtils = new LruCacheUtils(context);
        }
        return lruCacheUtils;
    }


    //添加缓存
    public void addJsonToCache(String key, String value) {
        if (getJsonFromCache(key) == null) {
            lruCache.put(key, value);
        }
    }
    //读取缓存
    public String getJsonFromCache(String key) {
        return lruCache.get(key);
    }

}
