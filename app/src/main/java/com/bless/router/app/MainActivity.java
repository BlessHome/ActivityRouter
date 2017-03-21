package com.bless.router.app;

import android.os.Bundle;
import android.app.Activity;
import android.view.View;

import com.bless.router.Router;
import com.bless.router.RouterHelper;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onBaseActivity(View view) {
        RouterHelper.getBaseActivityHelper().withName("Main").withFormActivity(this.getClass().getCanonicalName()).start(this);

//        Router.startActivity(this, "bless://base?name=Main");
    }
}
