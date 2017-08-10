package com.xulaoyao.android.jdi.autoupdate.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.xulaoyao.android.jdi.autoupdate.bean.AutoUpdateBean;

/**
 * Created by renwoxing on 2017/8/4.
 */

public class AutoUpdateAppUtils {

    /**
     * 获取版本号 正整数如：20
     * 根据此值来判断是否要更新版本
     * @param mContext
     * @return
     */
    public static int getVersionCode(Context mContext) {
        if (mContext != null) {
            try {
                return mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0).versionCode;
            } catch (PackageManager.NameNotFoundException ignored) {
            }
        }
        return 0;
    }

    /**
     * 获取版本名 如"1.12.1"
     * @param mContext
     * @return
     */
    public static String getVersionName(Context mContext) {
        if (mContext != null) {
            try {
                return mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0).versionName;
            } catch (PackageManager.NameNotFoundException ignored) {
            }
        }

        return "";
    }


    /***
     * 是否有wifi
     * @param context
     * @return
     */
    public static boolean isWifi(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        return info != null && info.getType() == ConnectivityManager.TYPE_WIFI;
    }


    @NonNull
    public static String getApkName(AutoUpdateBean autoUpdateBean) {
        String apkUrl = autoUpdateBean.getUrl();
        String appName = apkUrl.substring(apkUrl.lastIndexOf("/") + 1, apkUrl.length());
        if (!appName.endsWith(".apk")) {
            appName = "temp.apk";
        }
        return appName;
    }

    @NonNull
    public static String getApkNameByUrl(String apkUrl) {
        if (TextUtils.isEmpty(apkUrl)){
            return "temp.apk";
        }
        //String apkUrl = autoUpdateBean.getUrl();
        String appName = apkUrl.substring(apkUrl.lastIndexOf("/") + 1, apkUrl.length());
        if (!appName.endsWith(".apk")) {
            appName = "temp.apk";
        }
        return appName;
    }







}
