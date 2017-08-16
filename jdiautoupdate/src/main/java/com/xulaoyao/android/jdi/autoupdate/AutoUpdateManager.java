package com.xulaoyao.android.jdi.autoupdate;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;

import com.xulaoyao.android.jdi.autoupdate.bean.AutoUpdateBean;
import com.xulaoyao.android.jdi.autoupdate.http.IAutoUpdateCallback;
import com.xulaoyao.android.jdi.autoupdate.utils.AutoUpdateAppUtils;
import com.xulaoyao.android.jdi.autoupdate.view.AutoUpdateDialogFragment;

/**
 * Created by renwoxing on 2017/8/4.
 */

public class AutoUpdateManager {

    private static final String TAG = AutoUpdateManager.class.getSimpleName();


    private Context mContext;
    private String mJsonUrl;



    private boolean mShowIgnoreVersion;
    private boolean mDismissNotificationProgress;
    private boolean mOnlyWifi;

    private AutoUpdateBean mUpdateApp;


    //private IAutoUpdateCallback mCallback;


    private AutoUpdateManager(Builder builder) {
        mContext = builder.getContext();
        mJsonUrl = builder.getJsonUrl();
        mShowIgnoreVersion = builder.isShowIgnoreVersion();
        mDismissNotificationProgress = builder.isDismissNotificationProgress();
        mOnlyWifi = builder.isOnlyWifi();
    }


    /**
     * @return 新版本信息
     */
    public AutoUpdateBean fillUpdateAppData() {
        if (mUpdateApp != null) {
            mUpdateApp.showIgnoreVersion(mShowIgnoreVersion);
            mUpdateApp.dismissNotificationProgress(mDismissNotificationProgress);
            mUpdateApp.setOnlyWifi(mOnlyWifi);
            return mUpdateApp;
        }
        return null;
    }


    /**
     * auto update
     * silence
     */
    public void execute() {
        if (mContext != null) {
            new GetAutoUpdateJsonTask(mContext, !mDismissNotificationProgress, mJsonUrl, new IAutoUpdateCallback() {
                @Override
                public void onCompleted(AutoUpdateBean autoUpdateBean) {
                    //成功获取json
                    Log.d(TAG, "------ 获取 update json: \n" + autoUpdateBean.toString());
                    mUpdateApp = autoUpdateBean;
                    fillUpdateAppData();
                    checkNewApk(autoUpdateBean);
                }

                @Override
                public void onFailed(String msg) {
                    Log.e(TAG, "--- get update json fail: " + msg);
                }
            }).execute();

        } else {
            Log.e(TAG, "The arg context is null");
        }

    }


    private void checkNewApk(AutoUpdateBean autoUpdateBean) {
        if (null == autoUpdateBean) {
            return;
        }
        if (autoUpdateBean.getVersionCode() > AutoUpdateAppUtils.getVersionCode(mContext)) {
            Log.d(TAG,"有新本版本更新："+autoUpdateBean.getVersionCode()+"|"+autoUpdateBean.getVersionName()+"\n"+autoUpdateBean.getMsg());
            // Done: 2017/8/9 new 此对象时 原对象没有释放，造成泄漏
            //AutoUpdateDialog.show(mContext, autoUpdateBean);
            //fix bug
            AutoUpdateDialogFragment mAutoUpdateDialogFragment = AutoUpdateDialogFragment.newInstance(autoUpdateBean);
            mAutoUpdateDialogFragment.show(((FragmentActivity)mContext).getSupportFragmentManager(),mJsonUrl);
        }
        else {
            //Toast.makeText(mContext, mContext.getString(R.string.android_auto_update_toast_no_new_update), Toast.LENGTH_SHORT).show();
            Log.d(TAG,"没有新版本！");
        }
    }


    public static class Builder {
        //必须有
        private Context mContext;
        //必须有  更新json url
        private String mJsonUrl;

        private boolean mShowIgnoreVersion;
        private boolean dismissNotificationProgress;
        private boolean mOnlyWifi;


        public Context getContext() {
            return mContext;
        }

        /**
         * 是否是post请求，默认是get
         *
         * @param context 当前提示的 Activity
         * @return Builder builder
         */
        public Builder setContext(Context context) {
            mContext = context;
            return this;
        }


        public String getJsonUrl() {
            return mJsonUrl;
        }

        /**
         * 更新地址
         *
         * @param updateUrl 更新地址
         * @return Builder builder
         */
        public Builder setJsonUrl(String updateUrl) {
            mJsonUrl = updateUrl;
            return this;
        }


        /**
         * @return 生成app管理器
         */
        public AutoUpdateManager build() {
            //校验
            if (getContext() == null || TextUtils.isEmpty(getJsonUrl())) {
                throw new NullPointerException("必要参数不能为空");
            }
            return new AutoUpdateManager(this);
        }

        /**
         * 显示忽略版本
         *
         * @return 是否忽略版本
         */
        public Builder showIgnoreVersion() {
            mShowIgnoreVersion = true;
            return this;
        }

        public boolean isShowIgnoreVersion() {
            return mShowIgnoreVersion;
        }

        /**
         * 不显示通知栏进度条
         *
         * @return builder
         */
        public Builder dismissNotificationProgress() {
            dismissNotificationProgress = true;
            return this;
        }

        public boolean isDismissNotificationProgress() {
            return dismissNotificationProgress;
        }

        public Builder setOnlyWifi() {
            mOnlyWifi = true;
            return this;
        }

        public boolean isOnlyWifi() {
            return mOnlyWifi;
        }
    }


}
