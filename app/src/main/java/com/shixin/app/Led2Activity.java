package com.shixin.app;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.gyf.immersionbar.BarHide;
import com.gyf.immersionbar.ImmersionBar;

public class Led2Activity extends AppCompatActivity {

    private TextView mv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_led2);

        ImmersionBar.with(this).hideBar(BarHide.FLAG_HIDE_BAR).init();
        mv = findViewById(R.id.textView);
        mv.setText(getIntent().getStringExtra("nr"));
        mv.setTextColor(Color.parseColor(getIntent().getStringExtra("wzys")));
        mv.setTextSize(getIntent().getIntExtra("dx",120));
        mv.setBackgroundColor(Color.parseColor(getIntent().getStringExtra("bjys")));
    }
}