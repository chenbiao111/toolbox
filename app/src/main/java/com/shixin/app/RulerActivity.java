package com.shixin.app;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.gyf.immersionbar.BarHide;
import com.gyf.immersionbar.ImmersionBar;
import com.shixin.app.widget.RulerView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RulerActivity extends AppCompatActivity {

    @BindView(R.id.rulerView)
    RulerView rulerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ruler);
        ButterKnife.bind(this);

        ImmersionBar.with(this).hideBar(BarHide.FLAG_HIDE_BAR).init();
        rulerView.setUnitType(RulerView.Unit.CM);

    }
}