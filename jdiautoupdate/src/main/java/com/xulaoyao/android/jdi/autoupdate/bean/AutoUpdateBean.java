package com.xulaoyao.android.jdi.autoupdate.bean;

import java.io.Serializable;

/**
 * auto update json bean
 * Created by renwoxing on 2017/8/4.
 */

public class AutoUpdateBean implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
        {
          "versionCode": 1,
          "versionName": "0.1.0",
          "url": "http://git.jx-cloud.cc/release/smartclass-teacher-android/raw/0.1.1/smartclass-teacher-android-0.1.1.apk",
          "msg": "1，添加删除xxxx接口。\r\n2，添加xxxx认证。",
          "size": "5M",
          "md5":"A818AD325EACC199BC62C552A32C35F2"
        }
     */

    private int versionCode;
    private String versionName;
    private String url;
    private String msg;
    private Long size;
    private String md5;


    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }
    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }
    public String getVersionName() {
        return versionName;
    }

    public void setUrl(String url) {
        this.url = url;
    }
    public String getUrl() {
        return url;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
    public String getMsg() {
        return msg;
    }

    public void setSize(Long size) {
        this.size = size;
    }
    public Long getSize() {
        return size;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }
    public String getMd5() {
        return md5;
    }

    @Override
    public String toString() {
        return "AutoUpdateBean{" +
                "versionCode=" + versionCode +
                ", versionName='" + versionName + '\'' +
                ", url='" + url + '\'' +
                ", msg='" + msg + '\'' +
                ", size='" + size + '\'' +
                ", md5='" + md5 + '\'' +
                '}';
    }



    /**********以下是内部使用的数据**********/

    private boolean mHideDialog;
    private boolean mShowIgnoreVersion;
    private boolean mDismissNotificationProgress;
    private boolean mOnlyWifi;



    public boolean isShowIgnoreVersion() {
        return mShowIgnoreVersion;
    }

    public void showIgnoreVersion(boolean showIgnoreVersion) {
        mShowIgnoreVersion = showIgnoreVersion;
    }

    public void dismissNotificationProgress(boolean dismissNotificationProgress) {
        mDismissNotificationProgress = dismissNotificationProgress;
    }

    public boolean isDismissNotificationProgress() {
        return mDismissNotificationProgress;
    }

    public boolean isOnlyWifi() {
        return mOnlyWifi;
    }

    public void setOnlyWifi(boolean onlyWifi) {
        mOnlyWifi = onlyWifi;
    }

    public boolean isHideDialog() {
        return mHideDialog;
    }

    public void setHideDialog(boolean hideDialog) {
        mHideDialog = hideDialog;
    }


}
