package com.xulaoyao.android.jdi.autoupdate.view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.FileProvider;
import android.util.Log;

import com.xulaoyao.android.jdi.autoupdate.R;
import com.xulaoyao.android.jdi.autoupdate.bean.AutoUpdateBean;
import com.xulaoyao.android.jdi.autoupdate.config.Constants;
import com.xulaoyao.android.jdi.autoupdate.service.DownloadService;

import java.io.File;
import java.io.IOException;

import static com.xulaoyao.android.jdi.autoupdate.config.Constants.DOWNLOAD_SERVICE_ACTION_AUTO_DOWNLOAD_APK;
import static com.xulaoyao.android.jdi.autoupdate.config.Constants.DOWNLOAD_SERVICE_APK_AUTO_DOWNLOAD;
import static com.xulaoyao.android.jdi.autoupdate.config.Constants.DOWNLOAD_SERVICE_DOWNLOAD_COMPLETED_DATA_APK_FILE_PATH;

/**
 * Created by renwoxing on 2017/8/9.
 */

public class AutoUpdateDialogFragment extends DialogFragment implements DialogInterface.OnClickListener {


    public static AutoUpdateDialogFragment newInstance(AutoUpdateBean autoUpdateBean, boolean autoDownloadCompleted, String apkPath) {
        AutoUpdateDialogFragment instance = new AutoUpdateDialogFragment();
        Bundle b = new Bundle();
        b.putParcelable(Constants.DOWNLOAD_SERVICE_APK_AUTO_UPDATE_BEAN, autoUpdateBean);
        b.putBoolean(DOWNLOAD_SERVICE_APK_AUTO_DOWNLOAD, autoDownloadCompleted);
        b.putString(DOWNLOAD_SERVICE_DOWNLOAD_COMPLETED_DATA_APK_FILE_PATH, apkPath);
        instance.setArguments(b);
        return instance;
    }

    private AutoUpdateBean getAutoUpdateBean() {
        if (getArguments() != null) {
            return (AutoUpdateBean) getArguments().getParcelable(Constants.DOWNLOAD_SERVICE_APK_AUTO_UPDATE_BEAN);
        }
        return null;
    }

    /**
     * 获取是否已静默下载完成
     *
     * @return
     */
    private Boolean getAutoDownloadCompleted() {
        if (getArguments() != null) {
            return (Boolean) getArguments().getBoolean(Constants.DOWNLOAD_SERVICE_APK_AUTO_DOWNLOAD);
        }
        return false;
    }

    private String getApkFilePath() {
        if (getArguments() != null) {
            return (String) getArguments().getString(Constants.DOWNLOAD_SERVICE_DOWNLOAD_COMPLETED_DATA_APK_FILE_PATH);
        }
        return null;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String msg = getString(R.string.android_auto_update_dialog_title);
        String string_BUTTON_POSITIVE = getString(R.string.android_auto_update_dialog_btn_download);
        if (getAutoUpdateBean() != null) {
            msg = getAutoUpdateBean().getMsg();
        }
        if (getAutoDownloadCompleted()) {
            string_BUTTON_POSITIVE = getString(R.string.android_auto_update_dialog_btn_install);
        }
        AlertDialog.Builder b = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.android_auto_update_dialog_title)
                .setMessage(msg)
                .setPositiveButton(string_BUTTON_POSITIVE, this)  //设置回调函数
                .setNegativeButton(R.string.android_auto_update_dialog_btn_cancel, this); //设置回调函数
        return b.create();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (which == AlertDialog.BUTTON_POSITIVE) { //下载
            Log.d("--", "onClick: ___");
            if (getAutoDownloadCompleted()) {
                Log.d("--", "onClick: 开始安装" + getApkFilePath());
                //开启静默下载 直接安装
                if (getApkFilePath() != null) {
                    File file = new File(getApkFilePath());
                    if (file.exists()) {
                        installApk(file);
                    }
                }
            } else {
                //没有开启静默下载
                goToDownload(getActivity(), getAutoUpdateBean());
            }
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
        bundle.putBoolean(DOWNLOAD_SERVICE_APK_AUTO_DOWNLOAD, getAutoDownloadCompleted());
        intent.setAction(DOWNLOAD_SERVICE_ACTION_AUTO_DOWNLOAD_APK);
        intent.putExtras(bundle);
        context.startService(intent);
    }

    private void installApk(File apkFile) {
        Log.d("--s", "installApk: "+apkFile.getAbsolutePath());
        Intent intent = new Intent(Intent.ACTION_VIEW);
        //如果没有设置SDCard写权限，或者没有sdcard,apk文件保存在内存中，需要授予权限才能安装
        try {
            String[] command = {"chmod", "777", apkFile.toString()};
            ProcessBuilder builder = new ProcessBuilder(command);
            builder.start();
        } catch (IOException ignored) {
        }
        if (Build.VERSION.SDK_INT >= 24) { //7.0以上 适配
            Uri apkUri = FileProvider.getUriForFile(getActivity().getApplicationContext(), getActivity().getApplication().getPackageName() + ".fileprovider", apkFile);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        } else {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
        }
        startActivity(intent);
    }


}
