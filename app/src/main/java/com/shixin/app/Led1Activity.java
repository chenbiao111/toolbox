package com.shixin.app;

import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.gyf.immersionbar.BarHide;
import com.gyf.immersionbar.ImmersionBar;
import com.shixin.app.widget.MarqueeView;

public class Led1Activity extends AppCompatActivity {

    private MarqueeView mv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_led1);

        ImmersionBar.with(this).hideBar(BarHide.FLAG_HIDE_BAR).fullScreen(true).init();
        mv = findViewById(R.id.mv);
        mv.setContent(getIntent().getStringExtra("nr"));
        mv.setTextColor(Color.parseColor(getIntent().getStringExtra("wzys")));
        mv.setTextSize(getIntent().getIntExtra("dx",120));
        mv.setTextSpeed(getIntent().getIntExtra("sd",12));
        mv.setBackgroundColor(Color.parseColor(getIntent().getStringExtra("bjys")));
    }
}