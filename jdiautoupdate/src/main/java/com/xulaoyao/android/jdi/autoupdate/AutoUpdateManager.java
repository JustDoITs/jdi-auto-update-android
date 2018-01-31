package com.xulaoyao.android.jdi.autoupdate;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import com.xulaoyao.android.jdi.autoupdate.bean.AutoUpdateBean;
import com.xulaoyao.android.jdi.autoupdate.config.Constants;
import com.xulaoyao.android.jdi.autoupdate.http.IAutoUpdateCallback;
import com.xulaoyao.android.jdi.autoupdate.service.DownloadService;
import com.xulaoyao.android.jdi.autoupdate.utils.AutoUpdateAppUtils;
import com.xulaoyao.android.jdi.autoupdate.view.AutoUpdateDialogFragment;

import static com.xulaoyao.android.jdi.autoupdate.config.Constants.DOWNLOAD_SERVICE_ACTION_AUTO_DOWNLOAD_APK;
import static com.xulaoyao.android.jdi.autoupdate.config.Constants.DOWNLOAD_SERVICE_APK_AUTO_DOWNLOAD;
import static com.xulaoyao.android.jdi.autoupdate.config.Constants.DOWNLOAD_SERVICE_DOWNLOAD_COMPLETED_BROADCAST_ACTION;
import static com.xulaoyao.android.jdi.autoupdate.config.Constants.DOWNLOAD_SERVICE_DOWNLOAD_COMPLETED_DATA_APK_FILE_PATH;

/**
 * Created by renwoxing on 2017/8/4.
 */

public class AutoUpdateManager {

    private static final String TAG = AutoUpdateManager.class.getSimpleName();


    private Context mContext;
    private Context mActivity;
    private String mJsonUrl;


    private boolean mShowIgnoreVersion;
    private boolean mDismissNotificationProgress;
    private boolean mOnlyWifi;
    private boolean showLoading = false;
    private boolean mSilentDownload = false;

    private AutoUpdateBean mUpdateApp;

    //本地广播部分
    private LocalBroadcastManager localBroadcastManager;
    private AutoUpdateDownloadCompleteBroadcastReceiver broadcastReceiver;
    private IntentFilter intentFilter;


    //private IAutoUpdateCallback mCallback;


    private AutoUpdateManager(Builder builder) {
        mContext = builder.getContext().getApplicationContext();
        mActivity = builder.getContext();
        mJsonUrl = builder.getJsonUrl();
        mShowIgnoreVersion = builder.isShowIgnoreVersion();
        mDismissNotificationProgress = builder.isDismissNotificationProgress();
        mOnlyWifi = builder.isOnlyWifi();
        showLoading = builder.showLoadingUpdate;
        mSilentDownload = builder.mSilentDownload;
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
            new GetAutoUpdateJsonTask(mContext, showLoading, mDismissNotificationProgress, mJsonUrl, new IAutoUpdateCallback() {
                @Override
                public void onCompleted(AutoUpdateBean autoUpdateBean) {
                    //成功获取json
                    //Log.d(TAG, "------ 获取 update json: \n" + autoUpdateBean.toString());
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
        //大于本地版本更新
        if (autoUpdateBean.getVersionCode() > AutoUpdateAppUtils.getVersionCode(mContext)) {
            //Log.d(TAG, "有新本版本更新：" + autoUpdateBean.getVersionCode() + "|" + autoUpdateBean.getVersionName() + "\n" + autoUpdateBean.getMsg());
            // Done: 2017/8/9 new 此对象时 原对象没有释放，造成泄漏
            //AutoUpdateDialog.show(mContext, autoUpdateBean);
            //fix bug
            if (mSilentDownload) {
                //注册广播接收器
                localBroadcastManager = LocalBroadcastManager.getInstance(mContext);
                broadcastReceiver = new AutoUpdateDownloadCompleteBroadcastReceiver();
                intentFilter = new IntentFilter(DOWNLOAD_SERVICE_DOWNLOAD_COMPLETED_BROADCAST_ACTION);
                localBroadcastManager.registerReceiver(broadcastReceiver, intentFilter);
                //需要静默下载
                goToDownload(mContext, autoUpdateBean);

            } else {
                // 已在 LocalBroadcastManager 中调用
            }
        } else {
            //Toast.makeText(mContext, mContext.getString(R.string.android_auto_update_toast_no_new_update), Toast.LENGTH_SHORT).show();
            //Log.d(TAG, "没有新版本！");
        }
    }

    /**
     * 启动下载服务
     *
     * @param context
     * @param autoUpdateBean
     */
    private void goToDownload(Context context, AutoUpdateBean autoUpdateBean) {
        Intent intent = new Intent(context.getApplicationContext(), DownloadService.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.DOWNLOAD_SERVICE_APK_AUTO_UPDATE_BEAN, autoUpdateBean);
        bundle.putBoolean(DOWNLOAD_SERVICE_APK_AUTO_DOWNLOAD, mSilentDownload);
        intent.setAction(DOWNLOAD_SERVICE_ACTION_AUTO_DOWNLOAD_APK);
        intent.putExtras(bundle);
        context.startService(intent);
    }


    public static class Builder {
        //必须有
        private Context mContext;
        //必须有  更新json url
        private String mJsonUrl;
        //静默下载
        private boolean mSilentDownload = false;
        private boolean mShowIgnoreVersion;
        private boolean dismissNotificationProgress;
        private boolean mOnlyWifi;
        private boolean showLoadingUpdate;


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


        public Builder setSilentDownload(Boolean isSilentDownload) {
            this.mSilentDownload = isSilentDownload;
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

        /**
         * 显示 loading update
         *
         * @param show
         * @return
         */
        public Builder setShowLoadingUpdate(Boolean show) {
            showLoadingUpdate = show;
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


    private class AutoUpdateDownloadCompleteBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (DOWNLOAD_SERVICE_DOWNLOAD_COMPLETED_BROADCAST_ACTION.equals(action)) {
                String apkFilePath = intent.getStringExtra(DOWNLOAD_SERVICE_DOWNLOAD_COMPLETED_DATA_APK_FILE_PATH);
                Log.d("--", "service download completed. apk file path:" + apkFilePath);
                if (apkFilePath != null) {
                    //提示有更新
                    AutoUpdateDialogFragment mAutoUpdateDialogFragment = AutoUpdateDialogFragment.newInstance(mUpdateApp, mSilentDownload, apkFilePath);
                    mAutoUpdateDialogFragment.show(((FragmentActivity) mActivity).getSupportFragmentManager(), mJsonUrl);
                    //取消注册广播,防止内存泄漏
                    localBroadcastManager.unregisterReceiver(broadcastReceiver);
                }
            }
        }
    }
}
