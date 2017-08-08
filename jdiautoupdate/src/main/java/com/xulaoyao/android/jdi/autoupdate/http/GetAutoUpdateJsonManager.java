package com.xulaoyao.android.jdi.autoupdate.http;

import android.util.Log;

import com.xulaoyao.android.jdi.autoupdate.bean.AutoUpdateBean;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * http get 方式 获取 auto update json 文件信息并返回
 * Created by renwoxing on 2017/8/4.
 */

public class GetAutoUpdateJsonManager {

    private static final String TAG = GetAutoUpdateJsonManager.class.getSimpleName();
    /**
     * 获取 json string
     * @param jsonUrl
     * @return
     */
    public static String get(String jsonUrl) {
        return getJsonString(jsonUrl);
    }

    /**
     * 获取对象
     * @param jsonUrl
     * @return
     */
    public static AutoUpdateBean getBean(String jsonUrl) {
        return parseJson(getJsonString(jsonUrl));
    }


    /**
     * 解析json,自定义协议
     *
     * @param json 服务器返回的json
     * @return UpdateAppBean
     */
    public static AutoUpdateBean parseJson(String json) {
        AutoUpdateBean autoUpdateBean = new AutoUpdateBean();
        try {
            JSONObject jsonObject = new JSONObject(json);
            autoUpdateBean.setUrl(jsonObject.optString("url"));
            autoUpdateBean.setMsg(jsonObject.optString("msg"));
            autoUpdateBean.setMd5(jsonObject.optString("md5"));
            autoUpdateBean.setSize(jsonObject.optLong("size"));
            autoUpdateBean.setVersionCode(jsonObject.optInt("versionCode"));
            autoUpdateBean.setVersionName(jsonObject.optString("versionName"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return autoUpdateBean;
    }


    /**
     * http 请求
     * @param jsonUrl
     * @return
     */
    private static String getJsonString(String jsonUrl) {
        HttpURLConnection uRLConnection = null;
        InputStream is = null;
        BufferedReader buffer = null;
        String result = null;
        try {
            URL url = new URL(jsonUrl);
            uRLConnection = (HttpURLConnection) url.openConnection();
            uRLConnection.setRequestMethod("GET");

            is = uRLConnection.getInputStream();
            buffer = new BufferedReader(new InputStreamReader(is));
            StringBuilder strBuilder = new StringBuilder();
            String line;
            while ((line = buffer.readLine()) != null) {
                strBuilder.append(line);
            }
            result = strBuilder.toString();
        } catch (Exception e) {
            Log.e(TAG, "http get error:\n"+e.getMessage());
            //e.printStackTrace();
        } finally {
            if (buffer != null) {
                try {
                    buffer.close();
                } catch (IOException ignored) {
                    Log.e(TAG,ignored.getMessage());
                }
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException ignored) {
                    Log.e(TAG,ignored.getMessage());
                }
            }
            if (uRLConnection != null) {
                uRLConnection.disconnect();
            }
        }
        return result;
    }


}
