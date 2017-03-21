package com.bless.router.app;

import android.app.Activity;
import android.app.Application;

import com.bless.router.ModuleRouterInitializer;
import com.bless.router.Router;
import com.bless.router.RouterInitializer;

import java.util.Map;

/**
 * 作者:      ASLai(gdcpljh@126.com).
 * 日期:      2017/3/20
 * 版本:      V1.0
 * 描述:      description
 */

public class AndroidApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        initRouter();
    }

    private void initRouter() {
        Router.init("bless");
        Router.setHttpHost("www.bless.com");
        Router.register(new ModuleRouterInitializer());
        Router.register(new RouterInitializer() {
            @Override
            public void init(Map<String, Class<? extends Activity>> router) {
                router.put("base", BaseActivity.class);
            }
        });

    }

}
