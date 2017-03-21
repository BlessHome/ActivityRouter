package com.bless.router.app;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;

import com.bless.router.Router;
import com.bless.router.annotation.RouterName;
import com.bless.router.annotation.RouterParam;
import com.bless.router.module.ModuleBaseActivity;

/**
 * 作者:      ASLai(gdcpljh@126.com).
 * 日期:      2017/3/20
 * 版本:      V1.0
 * 描述:      description
 */

@RouterName("base")
public class BaseActivity extends ModuleBaseActivity {

    @RouterParam("formActivity")
    private String formActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Router.inject(this);
        getTextView().setText(name + "-" + formActivity);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void startActivityForResult(Intent intent, int requestCode, Bundle options) {
        intent.putExtra("formActivity", getClass().getName());
        super.startActivityForResult(intent, requestCode, options);
    }
}
