package com.xulaoyao.android.jdi.autoupdate;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.xulaoyao.android.jdi.autoupdate.http.GetAutoUpdateJsonManager;
import com.xulaoyao.android.jdi.autoupdate.http.IAutoUpdateCallback;

/**
 * Created by renwoxing on 2017/8/7.
 */

public class GetAutoUpdateJsonTask extends AsyncTask<Void,Void,String> {

    private ProgressDialog dialog;
    private Context mContext;
    private boolean mShowProgressDialog;
    //private static final String url = Constants.UPDATE_URL;

    private IAutoUpdateCallback mCallback;

    private String mJsonUrl;

    GetAutoUpdateJsonTask(Context context, boolean showProgressDialog, String jsonUrl, IAutoUpdateCallback callback) {

        this.mContext = context;
        this.mShowProgressDialog = showProgressDialog;
        this.mJsonUrl = jsonUrl;
        this.mCallback = callback;

    }


    protected void onPreExecute() {
        if (mShowProgressDialog) {
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
            this.mCallback.onCompleted(GetAutoUpdateJsonManager.parseJson(result));
        }else {
            this.mCallback.onFailed("没有获取到 Auto update json.");
        }
    }

    @Override
    protected void onCancelled(){
        this.mCallback.onFailed("放弃获取 Auto update json.");
    }







    @Override
    protected String doInBackground(Void... voids) {
        return GetAutoUpdateJsonManager.get(mJsonUrl);
    }
}
