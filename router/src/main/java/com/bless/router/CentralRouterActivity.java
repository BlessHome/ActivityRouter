package com.bless.router;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;

/**
 * 作者:      ASLai(gdcpljh@126.com).
 * 日期:      2017/3/20
 * 版本:      V1.0
 * 描述:      中心路由
 */

public class CentralRouterActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Uri data = getIntent().getData();
        if (data != null) {
            String url = getIntent().getDataString();
            if (data.getScheme().equals("http") && !TextUtils.isEmpty(Router.getHttpHost()) && Router.getHttpHost().equals(data.getHost())) {
                url = url.replaceFirst("http", Router.getScheme()).replace(Router.getHttpHost() + "/", "");
            }
            Router.startActivity(this, url);
        }
        this.finish();
    }
}
