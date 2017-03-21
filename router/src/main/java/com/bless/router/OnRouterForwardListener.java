package com.bless.router;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;

/**
 * 作者:      ASLai(gdcpljh@126.com).
 * 日期:      2017/3/21
 * 版本:      V1.0
 * 描述:      在路由器转发时的监听，用于自定义跳转
 */

public interface OnRouterForwardListener {

    void startActivity(Context context, Intent intent);

    void startActivityForResult(Activity activity, Intent intent, int requestCode);

    void startActivityForResult(Fragment fragment, Intent intent, int requestCode);

    void startActivityForResult(android.support.v4.app.Fragment fragment, Intent intent, int requestCode);
}
