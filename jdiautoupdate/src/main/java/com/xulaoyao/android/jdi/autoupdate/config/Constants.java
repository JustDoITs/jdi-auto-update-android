package com.xulaoyao.android.jdi.autoupdate.config;

/**
 * Created by renwoxing on 2017/8/4.
 */
public class Constants {

    public static final String DOWNLOAD_SERVICE_APK_DOWNLOAD_URL = "auto.update.download.service.url";

    public static final String DOWNLOAD_SERVICE_ACTION_AUTO_DOWNLOAD_APK = "auto.update.download.service.ACTION_DOWNLOAD_APK";
    public static final String DOWNLOAD_SERVICE_APK_AUTO_UPDATE_BEAN = "auto.update.download.service.auto.update.bean";
    public static final String DOWNLOAD_SERVICE_APK_AUTO_DOWNLOAD = "auto.update.download.service.auto.download";

    // 下载完成广播
    public static final String DOWNLOAD_SERVICE_DOWNLOAD_COMPLETED_BROADCAST_ACTION = "auto.update.download.service.download.completed.BROADCAST";
    public static final String DOWNLOAD_SERVICE_DOWNLOAD_COMPLETED_DATA_APK_FILE_PATH = "auto.update.download.service.download.completed.data.apk_file_path";


    public static final int DOWNLOAD_SERVICE_APK_AUTO_UPDATE_BUFFER_SIZE = 10 * 1024; // 8k ~ 32K;
}
