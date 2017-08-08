package com.xulaoyao.android.jdi.autoupdate.http;

import com.xulaoyao.android.jdi.autoupdate.bean.AutoUpdateBean;

/**
 * Created by renwoxing on 2017/8/7.
 */

public interface IAutoUpdateCallback {

    public void onCompleted(AutoUpdateBean autoUpdateBean);

    public void onFailed(String msg);
}
