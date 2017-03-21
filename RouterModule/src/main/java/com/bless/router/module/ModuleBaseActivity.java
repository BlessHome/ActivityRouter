package com.bless.router.module;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.bless.router.annotation.RouterName;
import com.bless.router.annotation.RouterParam;

@RouterName("base")
public class ModuleBaseActivity extends AppCompatActivity {

    @RouterParam("name")
    protected String name;

    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_module_base);

        textView = (TextView) findViewById(R.id.text_view);
        textView.setText("ModuleBaseActivity");
    }

    public TextView getTextView() {
        return textView;
    }
}
