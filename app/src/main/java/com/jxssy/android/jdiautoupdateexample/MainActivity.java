package com.jxssy.android.jdiautoupdateexample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.xulaoyao.android.jdi.autoupdate.AutoUpdateManager;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        new AutoUpdateManager.Builder()
                .setContext(this)
                .setSilentDownload(true)  //静默下载
                .setJsonUrl("http://jxc-app.oss-cn-shenzhen.aliyuncs.com/download/jxonline_app/android/update.json")
                .build()
                .execute();


    }
}
