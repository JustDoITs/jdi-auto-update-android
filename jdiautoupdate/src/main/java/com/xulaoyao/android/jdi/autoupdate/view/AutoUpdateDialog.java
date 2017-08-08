package com.xulaoyao.android.jdi.autoupdate.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;

import com.xulaoyao.android.jdi.autoupdate.R;
import com.xulaoyao.android.jdi.autoupdate.bean.AutoUpdateBean;
import com.xulaoyao.android.jdi.autoupdate.config.Constants;
import com.xulaoyao.android.jdi.autoupdate.service.DownloadService;

/**
 * Created by renwoxing on 2017/8/7.
 */

public class AutoUpdateDialog {

    public static void show(final Context context, final AutoUpdateBean autoUpdateBean) {
        if (isContextValid(context)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(R.string.android_auto_update_dialog_title);
            builder.setMessage(Html.fromHtml(autoUpdateBean.getMsg()))
                    .setPositiveButton(R.string.android_auto_update_dialog_btn_download, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            goToDownload(context, autoUpdateBean);
                        }
                    })
                    .setNegativeButton(R.string.android_auto_update_dialog_btn_cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    });

            AlertDialog dialog = builder.create();
            //点击对话框外面,对话框不消失
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }
    }

    private static boolean isContextValid(Context context) {
        return context instanceof Activity && !((Activity) context).isFinishing();
    }


    private static void goToDownload(Context context, AutoUpdateBean autoUpdateBean) {
        Intent intent = new Intent(context.getApplicationContext(), DownloadService.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.DOWNLOAD_SERVICE_APK_AUTO_UPDATE_BEAN, autoUpdateBean);
        intent.putExtras(bundle);
        //intent.putExtra(Constants.DOWNLOAD_SERVICE_APK_AUTO_UPDATE_BEAN, autoUpdateBean);
        context.startService(intent);
    }


//    /**
//     * Show dialog
//     */
//    private void showDialog(Context context, AutoUpdateBean autoUpdateBean) {
//        AutoUpdateDialog.show(context, autoUpdateBean);
//    }
//
//    /**
//     * Show Notification
//     */
//    private void showNotification(Context context, AutoUpdateBean autoUpdateBean) {
//        Intent myIntent = new Intent(context, DownloadService.class);
//        myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        myIntent.putExtra(Constants.DOWNLOAD_SERVICE_APK_DOWNLOAD_URL, autoUpdateBean.getUrl());
//        PendingIntent pendingIntent = PendingIntent.getService(context, 0, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//
//        int smallIcon = context.getApplicationInfo().icon;
//        Notification notify = new NotificationCompat.Builder(context)
//                .setTicker(context.getString(R.string.android_auto_update_notify_ticker))
//                .setContentTitle(context.getString(R.string.android_auto_update_notify_content))
//                .setContentText(autoUpdateBean.getMsg())
//                .setSmallIcon(smallIcon)
//                .setContentIntent(pendingIntent).build();
//
//        notify.flags = android.app.Notification.FLAG_AUTO_CANCEL;
//        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//        notificationManager.notify(0, notify);
//    }
}
