package com.xulaoyao.android.jdi.autoupdate.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * auto update json bean
 * Created by renwoxing on 2017/8/4.
 */

public class AutoUpdateBean implements Parcelable {

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

    public AutoUpdateBean() {
    }

    protected AutoUpdateBean(Parcel in) {
        versionCode = in.readInt();
        versionName = in.readString();
        url = in.readString();
        msg = in.readString();
        if (in.readByte() == 0) {
            size = null;
        } else {
            size = in.readLong();
        }
        md5 = in.readString();
        mShowIgnoreVersion = in.readByte() != 0;
        mDismissNotificationProgress = in.readByte() != 0;
        mOnlyWifi = in.readByte() != 0;
    }

    public static final Creator<AutoUpdateBean> CREATOR = new Creator<AutoUpdateBean>() {
        @Override
        public AutoUpdateBean createFromParcel(Parcel in) {
            return new AutoUpdateBean(in);
        }

        @Override
        public AutoUpdateBean[] newArray(int size) {
            return new AutoUpdateBean[size];
        }
    };

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


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(versionCode);
        parcel.writeString(versionName);
        parcel.writeString(url);
        parcel.writeString(msg);
        if (size == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeLong(size);
        }
        parcel.writeString(md5);
        parcel.writeByte((byte) (mShowIgnoreVersion ? 1 : 0));
        parcel.writeByte((byte) (mDismissNotificationProgress ? 1 : 0));
        parcel.writeByte((byte) (mOnlyWifi ? 1 : 0));
    }
}
