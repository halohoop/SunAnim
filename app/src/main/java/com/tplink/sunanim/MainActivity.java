package com.tplink.sunanim;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.tplink.sunanim_lib.widgets.SunAnimView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private SunAnimView sav;
    private TextView tv;
    private TextView tv1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv = (TextView) findViewById(R.id.tv);
        tv1 = (TextView) findViewById(R.id.tv1);
        tv.setOnClickListener(this);
        tv1.setOnClickListener(this);
        sav = (SunAnimView) findViewById(R.id.sav);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sav.startHaloSpread();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv:
                sav.startHaloSpread();
                break;
            case R.id.tv1:
                sav.pauseHaloSpread();
                break;
        }
    }
}
