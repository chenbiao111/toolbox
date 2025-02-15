package com.shixin.app;

import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.gyf.immersionbar.ImmersionBar;
import com.white.progressview.CircleProgressView;

import java.math.BigDecimal;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MetalDetectionActivity extends AppCompatActivity implements SensorEventListener {

    @BindView(R.id.root)
    ViewGroup root;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    final String TAG = "MetalDetector";
    private TextView mTextX;
    private TextView mTextY;
    private TextView mTextZ;
    private TextView mTotal;
    private SensorManager sensorManager;
    private CircleProgressView progressView;
    private TextView metalState;
    double alarmLim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_metal_detection);
        ButterKnife.bind(this);

        ImmersionBar.with(this)
                .fitsSystemWindows(true)
                .statusBarColor(R.color.appbarColor)
                .navigationBarColor(R.color.backgroundColor)
                .autoDarkModeEnable(true)
                .init();

        toolbar.setTitle(getString(R.string.金属探测器));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        initFunc();//初始化所有控件

    }

    @Override
    protected void onResume() {
        super.onResume();
        // 实例化传感器管理
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        //注册磁场传感器监听器
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), SensorManager.SENSOR_DELAY_NORMAL);
    }


    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Double rawTotal;//未处理的数据
        if(sensorEvent.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD){
            //分别计算三轴磁感应强度
            float X_lateral = sensorEvent.values[0];
            float Y_lateral = sensorEvent.values[1];
            float Z_lateral = sensorEvent.values[2];
            //Log.d(TAG,X_lateral + "");
            //计算出总磁感应强度
            rawTotal = Math.sqrt(X_lateral * X_lateral + Y_lateral * Y_lateral + Z_lateral * Z_lateral);
            //初始化BigDecimal类
            BigDecimal total = new BigDecimal(rawTotal);
            double res = total.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
            mTextX.setText(X_lateral + " μT");
            mTextY.setText(Y_lateral + " μT");
            mTextZ.setText(Z_lateral + " μT");
            mTotal.setText( res + " μT");
            alarmLim = 80;

            if (res < alarmLim){
                metalState.setTextColor(getResources().getColor(R.color.editTextColor));//设置文字颜色为黑色
                metalState.setText(R.string.未探测到金属);
                int progress = (int)((res / alarmLim )* 100);//计算进度
                progressView.setReachBarColor(getResources().getColor(R.color.zts));
                progressView.setProgress(progress);//进度条
            }else{
                metalState.setTextColor(Color.parseColor("#4CAF50"));//红色
                metalState.setText(R.string.探测到金属);
                progressView.setReachBarColor(Color.parseColor("#4CAF50"));
                progressView.setProgress(100);//进度条满
                Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                getSystemService(VIBRATOR_SERVICE);//获得 一个震动的服务
                vibrator.vibrate(100);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    @Override
    protected void onPause() {
        sensorManager.unregisterListener(this);
        super.onPause();
    }

    private void initFunc(){
        mTextX = (TextView)findViewById(R.id.xz);
        mTextY = (TextView)findViewById(R.id.yz);
        mTextZ = (TextView)findViewById(R.id.zz);
        mTotal = (TextView)findViewById(R.id.qd);
        progressView = findViewById(R.id.totalMetalProgress);
        metalState = (TextView) findViewById(R.id.metalDetect);
    }
}