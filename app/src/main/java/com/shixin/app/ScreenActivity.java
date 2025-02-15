package com.shixin.app;

import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.gyf.immersionbar.BarHide;
import com.gyf.immersionbar.ImmersionBar;

public class ScreenActivity extends AppCompatActivity {

    private LinearLayout linear1;
    private int position = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen);
        ImmersionBar.with(this).hideBar(BarHide.FLAG_HIDE_BAR).fullScreen(true).init();

        linear1 = findViewById(R.id.linear1);
        linear1.setOnClickListener(v -> {
            if (position == 3) {
                finish();
            }
            if (position == 2) {
                linear1.setBackgroundColor(0xFFFFFFFF);
                position++;
            }
            if (position == 1) {
                linear1.setBackgroundColor(0xFF0000FE);
                position++;
            }
            if (position == 0) {
                linear1.setBackgroundColor(0xFF00FF01);
                position++;
            }

        });
    }
}