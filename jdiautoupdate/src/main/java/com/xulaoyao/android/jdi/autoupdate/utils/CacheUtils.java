package com.xulaoyao.android.jdi.autoupdate.utils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.xulaoyao.android.jdi.autoupdate.DiskLruCache.DiskLruCache;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

/**
 * Created by renwoxing on 2017/8/9.
 */

public class CacheUtils {

    private static final String TAG = CacheUtils.class.getSimpleName();

    private static CacheUtils instance;

    private static DiskLruCache mDiskLruCache;
    //指定磁盘缓存大小
    private static final long DISK_CACHE_SIZE = 1024 * 1024 * 50;//50MB

    private static Context mContext;

    public static CacheUtils newInstance(Context context) {
        if (null == instance) {
            Log.d(TAG, " instance is init");
            instance = new CacheUtils();
            if (null == mDiskLruCache) {
                mContext = context;
                //得到缓存文件
                File diskCacheDir = getDiskCacheDir(context, "updatecache");
                //如果文件不存在 直接创建
                if (!diskCacheDir.exists()) {
                    diskCacheDir.mkdirs();
                }
                try {
                    mDiskLruCache = DiskLruCache.open(diskCacheDir, 1, 1, DISK_CACHE_SIZE);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return instance;
    }


    /**
     * 创建缓存文件
     *
     * @param context  上下文对象
     * @param filePath 文件路径
     * @return 返回一个文件
     */
    public static File getDiskCacheDir(Context context, String filePath) {
        boolean externalStorageAvailable = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        final String cachePath;
        if (externalStorageAvailable) {
            cachePath = context.getExternalCacheDir().getPath();
        } else {
            cachePath = context.getCacheDir().getPath();
        }
        return new File(cachePath + File.separator + filePath);
    }


    public static void saveJsonToCache(final String key, final String json) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (null != mDiskLruCache) {
                        DiskLruCache.Editor editor = mDiskLruCache.edit(key);
                        BufferedWriter bw = null;
                        try {
                            if (editor == null) return;
                            OutputStream os = editor.newOutputStream(0);
                            bw = new BufferedWriter(new OutputStreamWriter(os));
                            bw.write(json);
                            editor.commit();//write CLEAN
                        } catch (IOException e) {
                            e.printStackTrace();
                            try {
                                editor.abort();//write REMOVE
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }
                        } finally {
                            try {
                                if (bw != null)
                                    bw.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    public static String getJson(String key) {
        InputStream inputStream = null;
        try {
            //write READ
            inputStream = get(key);
            if (inputStream == null) return null;
            StringBuilder sb = new StringBuilder();
            int len = 0;
            byte[] buf = new byte[128];
            while ((len = inputStream.read(buf)) != -1) {
                sb.append(new String(buf, 0, len));
            }
            return sb.toString();


        } catch (IOException e) {
            e.printStackTrace();
            if (inputStream != null)
                try {
                    inputStream.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
        }
        return null;
    }


    public static InputStream get(String key) {
        try {
            if (null == mDiskLruCache) {
                return null;
            }
            DiskLruCache.Snapshot snapshot = mDiskLruCache.get(key);
            if (snapshot == null) //not find entry , or entry.readable = false
            {
                Log.e(TAG, "not find entry , or entry.readable = false. key:" + key);
                return null;
            }
            //write READ
            return snapshot.getInputStream(0);

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }catch (Exception e){
            Log.e(TAG, "err : \n" + e.getMessage());
            return null;
        }

    }
}
