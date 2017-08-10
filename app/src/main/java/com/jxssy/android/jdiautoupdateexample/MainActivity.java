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
                .setJsonUrl("http://git.jx-cloud.cc/release/smartclass-teacher-android/raw/master/update.json")
                .build()
                .execute();


    }
}
