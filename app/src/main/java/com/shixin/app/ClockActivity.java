package com.shixin.app;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.animation.OvershootInterpolator;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.gyf.immersionbar.BarHide;
import com.gyf.immersionbar.ImmersionBar;
import com.robinhood.ticker.TickerUtils;
import com.robinhood.ticker.TickerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class ClockActivity extends AppCompatActivity {

    private TickerView tickerView;
    private final Timer _timer = new Timer();
    private TextView textView;
    private CoordinatorLayout root;
    private boolean is = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clock);
        ImmersionBar.with(this).hideBar(BarHide.FLAG_HIDE_BAR).fullScreen(true).init();
        getWindow().addFlags(128);
        root = findViewById(R.id.root);
        tickerView = findViewById(R.id.tickerView);
        textView = findViewById(R.id.textView);
        tickerView.setAnimationInterpolator(new OvershootInterpolator());
        tickerView.setCharacterLists(TickerUtils.provideNumberList());

        TimerTask time = new TimerTask() {
            @SuppressLint("SimpleDateFormat")
            @Override
            public void run() {
                runOnUiThread(() -> {
                    tickerView.setText(new SimpleDateFormat("HH:mm:ss").format(new Date()));
                    textView.setText(new SimpleDateFormat("yyyy年MM月dd日").format(new Date()));
                });
            }
        };
        _timer.scheduleAtFixedRate(time, 0, 1000);

        root.setOnClickListener(v -> {
            if (is) {
                is = false;
                ObjectAnimator of_int = ObjectAnimator.ofInt(root, "BackgroundColor", 0xFFFFFFFF, 0xFF000000);
                of_int.setDuration(800L);
                of_int.setEvaluator(new ArgbEvaluator());
                of_int.setRepeatMode(ValueAnimator.RESTART);
                of_int.start();

                of_int = ObjectAnimator.ofInt(tickerView, "TextColor", 0xFF000000, 0xFFFFFFFF);
                of_int.setDuration(800L);
                of_int.setEvaluator(new ArgbEvaluator());
                of_int.setRepeatMode(ValueAnimator.RESTART);
                of_int.start();

                of_int = ObjectAnimator.ofInt(textView, "TextColor", 0xFF000000, 0xFFFFFFFF);
                of_int.setDuration(800L);
                of_int.setEvaluator(new ArgbEvaluator());
                of_int.setRepeatMode(ValueAnimator.RESTART);
                of_int.start();
            }
            else {
                is = true;
                ObjectAnimator of_int = ObjectAnimator.ofInt(root, "BackgroundColor", 0xFF000000, 0xFFFFFFFF);
                of_int.setDuration(800L);
                of_int.setEvaluator(new ArgbEvaluator());
                of_int.setRepeatMode(ValueAnimator.RESTART);
                of_int.start();

                of_int = ObjectAnimator.ofInt(tickerView, "TextColor", 0xFFFFFFFF, 0xFF000000);
                of_int.setDuration(800L);
                of_int.setEvaluator(new ArgbEvaluator());
                of_int.setRepeatMode(ValueAnimator.RESTART);
                of_int.start();

                of_int = ObjectAnimator.ofInt(textView, "TextColor", 0xFFFFFFFF, 0xFF000000);
                of_int.setDuration(800L);
                of_int.setEvaluator(new ArgbEvaluator());
                of_int.setRepeatMode(ValueAnimator.RESTART);
                of_int.start();
            }

        });
    }
}