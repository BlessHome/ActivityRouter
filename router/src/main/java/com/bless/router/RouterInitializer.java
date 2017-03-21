package com.bless.router;

import android.app.Activity;

import java.util.Map;

/**
 * 作者:      ASLai(gdcpljh@126.com).
 * 日期:      2017/3/20
 * 版本:      V1.0
 * 描述:      路由初始化
 */

public interface RouterInitializer {
    void init(Map<String, Class<? extends Activity>> router);
}
