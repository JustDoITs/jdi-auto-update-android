package com.xulaoyao.android.jdi.autoupdate.service;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;
import android.support.v4.content.FileProvider;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.xulaoyao.android.jdi.autoupdate.bean.AutoUpdateBean;
import com.xulaoyao.android.jdi.autoupdate.config.Constants;
import com.xulaoyao.android.jdi.autoupdate.utils.AutoUpdateAppUtils;
import com.xulaoyao.android.jdi.autoupdate.utils.Md5Utils;
import com.xulaoyao.android.jdi.autoupdate.utils.StorageUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.xulaoyao.android.jdi.autoupdate.config.Constants.DOWNLOAD_SERVICE_ACTION_AUTO_DOWNLOAD_APK;
import static com.xulaoyao.android.jdi.autoupdate.config.Constants.DOWNLOAD_SERVICE_APK_AUTO_UPDATE_BUFFER_SIZE;


/**
 * Created by renwoxing on 2017/8/4.
 */

public class DownloadService extends IntentService {

    private static final String TAG = "DownloadService";

    private static final int NOTIFICATION_ID = 0;

    private NotificationManager mNotifyManager;
    private Builder mBuilder;

    private AutoUpdateBean mAutoUpdateBean;
    private boolean mSilentDownload = false;


    public DownloadService() {
        super("DownloadService");
    }


    /**
     * IntentService 服务重载 方法
     *
     * @param intent
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (DOWNLOAD_SERVICE_ACTION_AUTO_DOWNLOAD_APK.equals(action)) {
                try {
                    mNotifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    mBuilder = new NotificationCompat.Builder(this);

                    String appName = getString(getApplicationInfo().labelRes);
                    int icon = getApplicationInfo().icon;
                    mBuilder.setContentTitle(appName).setSmallIcon(icon);
                    //mBuilder.setContentTitle("自动更新").setSmallIcon(R.mipmap.ic_launcher);
                } catch (Exception e) {
                }
                mAutoUpdateBean = (AutoUpdateBean) intent.getParcelableExtra(Constants.DOWNLOAD_SERVICE_APK_AUTO_UPDATE_BEAN);
                mSilentDownload = (Boolean) intent.getBooleanExtra(Constants.DOWNLOAD_SERVICE_APK_AUTO_DOWNLOAD, false);
                if (mAutoUpdateBean != null) {
                    downloadApk();
                }
            }
        }
    }

    private boolean retry = true;

    private void downloadApk() {
        InputStream in = null;
        FileOutputStream out = null;
        try {

            URL url = new URL(mAutoUpdateBean.getUrl());
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestProperty("User-Agent", " Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/37.0.2062.120 Safari/537.36");
            // 设置是否使用缓存  默认是true
            urlConnection.setUseCaches(true);
            urlConnection.setRequestMethod("GET");
            urlConnection.setDoOutput(false);
            urlConnection.setConnectTimeout(10 * 1000);
            urlConnection.setReadTimeout(10 * 1000);
            urlConnection.setRequestProperty("Connection", "Keep-Alive");
            urlConnection.setRequestProperty("Charset", "UTF-8");
            urlConnection.setRequestProperty("Accept-Encoding", "gzip, deflate"); //bug getContentLength = -1
            //urlConnection.setRequestProperty("Accept-Encoding", "identity");

            urlConnection.connect();
            long _byte_total = urlConnection.getContentLength();  //进度计算
            _byte_total = _byte_total == -1 ? mAutoUpdateBean.getSize() : _byte_total;
            //long _byte_total = Long.parseLong(urlConnection.getHeaderField("Content-Length"));  //进度计算
            long _byte_sum_current = 0;                                   //进度
            int len = 0;
            in = urlConnection.getInputStream();
            File dir = StorageUtils.getCacheDirectory(this);
            Log.d(TAG, "  download service start! dir:" + dir);
            String apkName = AutoUpdateAppUtils.getApkNameByUrl(mAutoUpdateBean.getUrl()); // Done: 2017/8/4 需要传url
            File apkFile = new File(dir, apkName);
            out = new FileOutputStream(apkFile);
            byte[] buffer = new byte[DOWNLOAD_SERVICE_APK_AUTO_UPDATE_BUFFER_SIZE];
            int oldProgress = 0;
            while ((len = in.read(buffer)) != -1) {
                _byte_sum_current += len;
                out.write(buffer, 0, len);
                //Log.d(TAG, String.format("current:%s/total:%s", _byte_sum_current, _byte_total));
                int progress = (int) (_byte_sum_current * 100L / _byte_total);
                // 如果进度与之前进度相等，则不更新，如果更新太频繁，否则会造成界面卡顿
                if (progress != oldProgress) {
                    updateProgress(progress);
                }
                oldProgress = progress;
            }

            // 下载完成
            // done: 2017/8/8 需要判断与检测是否完整
            if (checkApkCompleted(apkFile)) {
                Log.d(TAG, "  download service completed! apk file path :" + apkFile.getAbsolutePath());
                if (mSilentDownload) {
                    //通过本地广播 回传
                    Intent localIntent = new Intent(Constants.DOWNLOAD_SERVICE_DOWNLOAD_COMPLETED_BROADCAST_ACTION)
                            .putExtra(Constants.DOWNLOAD_SERVICE_DOWNLOAD_COMPLETED_DATA_APK_FILE_PATH, apkFile.getAbsolutePath());
                    // Broadcasts the Intent to receivers in this app.
                    LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
                } else {
                    installApk(apkFile);
                }
            } else {
                // TODO: 2017/8/8 需要重试机制
                if (retry) {
                    retry = false;
                    downloadApk();
                }
            }
            mNotifyManager.cancel(NOTIFICATION_ID);

        } catch (Exception e) {
            Log.e(TAG, "download apk file error:\n" + e.getMessage());
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException ignored) {
                    Log.e(TAG, ignored.getMessage());
                }
                out = null;
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ignored) {
                    Log.e(TAG, ignored.getMessage());
                }
                in = null;
            }
        }
    }

    /******************* 私有方法 *********************/

    private void updateProgress(int progress) {
        //Log.d(TAG,"正在下载:" + progress + "%");
//        mBuilder.setContentText(this.getString(R.string.android_auto_update_download_progress, progress)).setProgress(100, progress, false);
//        //setContentInent如果不设置在4.0+上没有问题，在4.0以下会报异常
//        PendingIntent pendingintent = PendingIntent.getActivity(this, 0, new Intent(), PendingIntent.FLAG_CANCEL_CURRENT);
//        mBuilder.setContentIntent(pendingintent);
//        mNotifyManager.notify(NOTIFICATION_ID, mBuilder.build());
    }


    private void installApk(File apkFile) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        //如果没有设置SDCard写权限，或者没有sdcard,apk文件保存在内存中，需要授予权限才能安装
        try {
            String[] command = {"chmod", "777", apkFile.toString()};
            ProcessBuilder builder = new ProcessBuilder(command);
            builder.start();
        } catch (IOException ignored) {
        }
        if (Build.VERSION.SDK_INT >= 24) { //7.0以上 适配
            Uri apkUri = FileProvider.getUriForFile(getApplicationContext(), getApplication().getPackageName() + ".fileprovider", apkFile);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        } else {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
        }
        startActivity(intent);
    }


    /**
     * 根据文件的 md5 值来判断文件是否完成
     *
     * @param file
     * @return
     */
    private boolean checkApkCompleted(File file) {
        if (null == mAutoUpdateBean) {
            return false;
        }
        if (mAutoUpdateBean.getMd5().equalsIgnoreCase(Md5Utils.getFileMD5(file))) {
            return true;
        } else {
            if (file.length() == mAutoUpdateBean.getSize()) {
                return true;
            }
            return false;
        }
    }


}
