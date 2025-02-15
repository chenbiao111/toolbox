package com.shixin.app;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.gyf.immersionbar.BarHide;
import com.gyf.immersionbar.ImmersionBar;
import com.shixin.app.widget.CompassView;

public class CompassActivity extends AppCompatActivity {

    private CompassView compass;
    private TextView direction;
    private String directiona = "UNKNOWN";
    private SensorManager a;
    private SensorManager mSensorManager;
    private Vibrator b;
    private SensorEventListener _a_sensor_listener;
    private final boolean isVibrate = true;
    private float lastDirAngel = 0;
    private final String[] mDirectionText = new String[]{"北", "东北", "东", "东南", "南", "西南", "西", "西北"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compass);

        ImmersionBar.with(this).hideBar(BarHide.FLAG_HIDE_BAR).init();
        compass = findViewById(R.id.compass);
        direction = findViewById(R.id.direction);
        compass.setLayoutParams(new LinearLayout.LayoutParams(this.getResources().getDisplayMetrics().widthPixels,this.getResources().getDisplayMetrics().widthPixels));

        a = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);
        if (a.getDefaultSensor(Sensor.TYPE_GYROSCOPE) == null){
            //Toast.makeText(this,"你的设备不支持陀螺仪",Toast.LENGTH_SHORT).show();
        }
        b = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);

        _a_sensor_listener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                float[] _rotationMatrix = new float[16];
                SensorManager.getRotationMatrixFromVector(_rotationMatrix, event.values);
                float[] _remappedRotationMatrix = new float[16];
                SensorManager.remapCoordinateSystem(_rotationMatrix, SensorManager.AXIS_X, SensorManager.AXIS_Z, _remappedRotationMatrix);
                float[] _orientations = new float[3];
                SensorManager.getOrientation(_remappedRotationMatrix, _orientations);
                for(int _i = 0; _i < 3; _i++) {
                    _orientations[_i] = (float)(Math.toDegrees(_orientations[_i]));
                }
                final double _x = _orientations[0];
                final double _y = _orientations[1];
                final double _z = _orientations[2];
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };
        a.registerListener(_a_sensor_listener, a.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR), SensorManager.SENSOR_DELAY_NORMAL);
    }

    @SuppressWarnings("deprecation")
    private void initSensor() {
        mSensorManager = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);
        if (mSensorManager != null) {
            Sensor sensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
            //注册监听
            mSensorManager.registerListener(sensorEventListener, sensor, SensorManager.SENSOR_DELAY_FASTEST);
        }
    }

    private final SensorEventListener sensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            float dirAngel = event.values[0];

            compass.setDirectionAngle(dirAngel);
            directiona = mDirectionText[((int) (dirAngel + 22.5f) % 360) / 45];
            direction.setText(directiona);
			            /*if (isVibrate && (int) dirAngel % 30 == 0 && (int) dirAngel != (int) lastDirAngel) {
                Vibrator vibrator = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
                if (vibrator != null) {
                    vibrator.vibrate(20);
                }
            }*/
            lastDirAngel = dirAngel;
        }
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            if (accuracy != SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM
                    && accuracy != SensorManager.SENSOR_STATUS_ACCURACY_HIGH) {
            }
        }
    };

    @Override
    public void onResume(){
        super.onResume();
        initSensor();
    }

    @Override
    public void onPause(){
        super.onPause();
        mSensorManager.unregisterListener(sensorEventListener);
    }
}