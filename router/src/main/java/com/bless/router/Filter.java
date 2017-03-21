package com.bless.router;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;

/**
 * 作者:      ASLai(gdcpljh@126.com).
 * 日期:      2017/3/20
 * 版本:      V1.0
 * 描述:      description
 */

public interface Filter {

    String doFilter(String url);

    boolean startActivityForResult(Activity activity, String url, int requestCode);

    boolean start(Context context, String url);

    boolean startActivityForResult(Fragment fragment, String url, int requestCode);

    boolean startActivityForResult(android.support.v4.app.Fragment fragment, String url, int requestCode);
}
