package com.xulaoyao.android.jdi.autoupdate.view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;

import com.xulaoyao.android.jdi.autoupdate.R;
import com.xulaoyao.android.jdi.autoupdate.bean.AutoUpdateBean;
import com.xulaoyao.android.jdi.autoupdate.config.Constants;
import com.xulaoyao.android.jdi.autoupdate.service.DownloadService;

/**
 * Created by renwoxing on 2017/8/9.
 */

public class AutoUpdateDialogFragment extends DialogFragment implements DialogInterface.OnClickListener {


    public static AutoUpdateDialogFragment newInstance(AutoUpdateBean autoUpdateBean) {
        AutoUpdateDialogFragment instance = new AutoUpdateDialogFragment();
        Bundle b = new Bundle();
        b.putSerializable(Constants.DOWNLOAD_SERVICE_APK_AUTO_UPDATE_BEAN, autoUpdateBean);
        instance.setArguments(b);
        return instance;
    }

    private AutoUpdateBean getAutoUpdateBean() {
        return (AutoUpdateBean) getArguments().getSerializable(Constants.DOWNLOAD_SERVICE_APK_AUTO_UPDATE_BEAN);
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder b = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.android_auto_update_dialog_title)
                .setMessage(getAutoUpdateBean().getMsg())
                .setPositiveButton(R.string.android_auto_update_dialog_btn_download, this)  //设置回调函数
                .setNegativeButton(R.string.android_auto_update_dialog_btn_cancel, this); //设置回调函数
        return b.create();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (which == AlertDialog.BUTTON_POSITIVE) { //下载
            goToDownload(getActivity(), getAutoUpdateBean());
        }
    }


    /**
     * 启动下载服务
     * @param context
     * @param autoUpdateBean
     */
    private void goToDownload(Context context, AutoUpdateBean autoUpdateBean) {
        Intent intent = new Intent(context.getApplicationContext(), DownloadService.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.DOWNLOAD_SERVICE_APK_AUTO_UPDATE_BEAN, autoUpdateBean);
        intent.putExtras(bundle);
        context.startService(intent);
    }

}
