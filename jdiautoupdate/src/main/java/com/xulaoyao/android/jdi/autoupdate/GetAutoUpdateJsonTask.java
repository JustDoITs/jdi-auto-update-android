package com.xulaoyao.android.jdi.autoupdate;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.xulaoyao.android.jdi.autoupdate.http.GetAutoUpdateJsonManager;
import com.xulaoyao.android.jdi.autoupdate.http.IAutoUpdateCallback;
import com.xulaoyao.android.jdi.autoupdate.utils.LruCacheUtils;
import com.xulaoyao.android.jdi.autoupdate.utils.Md5Utils;

/**
 * Created by renwoxing on 2017/8/7.
 */

public class GetAutoUpdateJsonTask extends AsyncTask<Void, Void, String> {

    private static final String TAG = GetAutoUpdateJsonTask.class.getSimpleName();

    private ProgressDialog dialog;
    private Context mContext;
    private boolean mShowProgressDialog;
    private boolean mShowLoading = false;


    private IAutoUpdateCallback mCallback;

    private String mJsonUrl;

    GetAutoUpdateJsonTask(Context context, boolean showProgressDialog, String jsonUrl, IAutoUpdateCallback callback) {
        this.mContext = context;
        this.mShowProgressDialog = showProgressDialog;
        this.mJsonUrl = jsonUrl;
        this.mCallback = callback;
    }

    GetAutoUpdateJsonTask(Context context,boolean showLoading, boolean showProgressDialog, String jsonUrl, IAutoUpdateCallback callback) {
        this.mContext = context;
        this.mShowProgressDialog = showProgressDialog;
        this.mJsonUrl = jsonUrl;
        this.mCallback = callback;
        this.mShowLoading = showLoading;
    }

    public void setShowLoading(boolean showLoading) {
        this.mShowLoading = showLoading;
    }

    protected void onPreExecute() {
        if (mShowLoading) {
            dialog = new ProgressDialog(mContext);
            dialog.setMessage(mContext.getString(R.string.android_auto_update_dialog_checking));
            dialog.show();
        }
    }


    @Override
    protected void onPostExecute(String result) {

        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }

        if (!TextUtils.isEmpty(result)) {
            //本地写入缓存
            //CacheUtils.newInstance(mContext).saveJsonToCache(Md5Utils.hashKeyFormUrl(mJsonUrl), result);
            //内存缓存写入
            LruCacheUtils.newInstance(mContext).addJsonToCache(Md5Utils.hashKeyFormUrl(mJsonUrl), result);
            this.mCallback.onCompleted(GetAutoUpdateJsonManager.parseJson(result));
        } else {
            this.mCallback.onFailed("没有获取到 Auto update json.");
        }
    }

    @Override
    protected void onCancelled() {
        this.mCallback.onFailed("放弃获取 Auto update json.");
    }


    @Override
    protected String doInBackground(Void... voids) {
        // TODO: 2017/8/9 需要定时清除缓存
        //本地缓存获取
        //String cacheJson = CacheUtils.newInstance(mContext).getJson(Md5Utils.hashKeyFormUrl(mJsonUrl));
        //内存缓存
        String cacheJson = LruCacheUtils.newInstance(mContext).getJsonFromCache(Md5Utils.hashKeyFormUrl(mJsonUrl));
        if (!TextUtils.isEmpty(cacheJson)) {
            Log.d(TAG, "从 cache 中 获取json 内容: \n" + cacheJson);
            return cacheJson;
        } else {
            Log.d(TAG, "从 服务器获取json 内容");
            return GetAutoUpdateJsonManager.get(mJsonUrl);
        }
    }
}
