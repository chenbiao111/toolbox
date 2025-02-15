package com.shixin.app;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.gyf.immersionbar.BarHide;
import com.gyf.immersionbar.ImmersionBar;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.core.DrawerPopupView;
import com.lxj.xpopup.enums.PopupPosition;
import com.shixin.app.videocontroller.StandardVideoController;
import com.shixin.app.videocontroller.component.CompleteView;
import com.shixin.app.videocontroller.component.ErrorView;
import com.shixin.app.videocontroller.component.GestureView;
import com.shixin.app.videocontroller.component.LiveControlView;
import com.shixin.app.videocontroller.component.PrepareView;
import com.shixin.app.videocontroller.component.TitleView;
import com.shixin.app.videocontroller.component.VodControlView;

import xyz.doikki.videoplayer.exo.ExoMediaPlayer;
import xyz.doikki.videoplayer.exo.ExoMediaPlayerFactory;
import xyz.doikki.videoplayer.player.VideoView;


public class PlayerActivity extends AppCompatActivity {

    private VideoView<ExoMediaPlayer> videoView;
    private StandardVideoController controller;
    private String speed = "1.0";
    private String proportion = "默认";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        ImmersionBar.with(this).hideBar(BarHide.FLAG_HIDE_BAR).init();
        videoView = findViewById(R.id.videoView);

        videoView.setPlayerFactory(ExoMediaPlayerFactory.create());
        videoView.startFullScreen();
        videoView.setUrl(getIntent().getStringExtra("url"));

        //Toast.makeText(this,getIntent().getStringExtra("url"), Toast.LENGTH_LONG).show();

        controller = new StandardVideoController(this);
        controller.addControlComponent(new CompleteView(this));
        controller.addControlComponent(new ErrorView(this));
        controller.addControlComponent(new PrepareView(this));

        if (getIntent().getBooleanExtra("islive",false)) {
            videoView.setScreenScaleType(VideoView.SCREEN_SCALE_16_9);
            controller.addControlComponent(new LiveControlView(this));
        } else {
            VodControlView vodControlView = new VodControlView(this);
            vodControlView.findViewById(R.id.speed).setOnClickListener(v -> new XPopup.Builder(PlayerActivity.this)
                    .popupPosition(PopupPosition.Right)//右边
                    //.hasStatusBarShadow(true) //启用状态栏阴影
                    .asCustom(new CustomDrawerPopupView(PlayerActivity.this))
                    .show());
            vodControlView.findViewById(R.id.proportion).setOnClickListener(v -> new XPopup.Builder(PlayerActivity.this)
                    .popupPosition(PopupPosition.Right)//右边
                    //.hasStatusBarShadow(true) //启用状态栏阴影
                    .asCustom(new CustomDrawerPopupView1(PlayerActivity.this))
                    .show());

            controller.addControlComponent(vodControlView);
        }

        controller.addControlComponent(new GestureView(this));

        if (getIntent().getBooleanExtra("islive",false)) {
            controller.setCanChangePosition(false);
        } else {
            controller.setCanChangePosition(true);
        }


        TitleView titleView = new TitleView(this);
        titleView.findViewById(R.id.back).setOnClickListener(v -> finish());

        titleView.setTitle(getIntent().getStringExtra("title"));
        controller.addControlComponent(titleView);

        videoView.setVideoController(controller);
        videoView.start();

    }

    public class CustomDrawerPopupView extends DrawerPopupView {
        public CustomDrawerPopupView(@androidx.annotation.NonNull Context context) {
            super(context);
        }
        @Override
        protected int getImplLayoutId() {
            return R.layout.speed;
        }
        @Override
        protected void onCreate() {
            super.onCreate();

            final TextView txt1 = findViewById(R.id.textview1);
            final TextView txt2 = findViewById(R.id.textview2);
            final TextView txt3 = findViewById(R.id.textview3);
            final TextView txt4 = findViewById(R.id.textview4);
            final TextView txt5 = findViewById(R.id.textview5);

            if (speed.equals("0.75")) {
                txt1.setTextColor(Color.parseColor("#5187f4"));
            }
            if (speed.equals("1.0")) {
                txt2.setTextColor(Color.parseColor("#5187f4"));
            }
            if (speed.equals("1.25")) {
                txt3.setTextColor(Color.parseColor("#5187f4"));
            }
            if (speed.equals("1.5")) {
                txt4.setTextColor(Color.parseColor("#5187f4"));
            }
            if (speed.equals("2.0")) {
                txt5.setTextColor(Color.parseColor("#5187f4"));
            }

            findViewById(R.id.cardview1).setOnClickListener(v -> {
                videoView.setSpeed(0.75f);
                speed = "0.75";
                txt1.setTextColor(Color.parseColor("#5187f4"));
                txt2.setTextColor(Color.parseColor("#ffffff"));
                txt3.setTextColor(Color.parseColor("#ffffff"));
                txt4.setTextColor(Color.parseColor("#ffffff"));
                txt5.setTextColor(Color.parseColor("#ffffff"));
            });

            findViewById(R.id.cardview2).setOnClickListener(v -> {
                videoView.setSpeed(1.0f);
                speed = "1.0";
                txt2.setTextColor(Color.parseColor("#5187f4"));
                txt1.setTextColor(Color.parseColor("#ffffff"));
                txt3.setTextColor(Color.parseColor("#ffffff"));
                txt4.setTextColor(Color.parseColor("#ffffff"));
                txt5.setTextColor(Color.parseColor("#ffffff"));
            });

            findViewById(R.id.cardview3).setOnClickListener(v -> {
                videoView.setSpeed(1.25f);
                speed = "1.25";
                txt3.setTextColor(Color.parseColor("#5187f4"));
                txt2.setTextColor(Color.parseColor("#ffffff"));
                txt1.setTextColor(Color.parseColor("#ffffff"));
                txt4.setTextColor(Color.parseColor("#ffffff"));
                txt5.setTextColor(Color.parseColor("#ffffff"));
            });

            findViewById(R.id.cardview4).setOnClickListener(v -> {
                videoView.setSpeed(1.5f);
                speed = "1.5";
                txt4.setTextColor(Color.parseColor("#5187f4"));
                txt2.setTextColor(Color.parseColor("#ffffff"));
                txt3.setTextColor(Color.parseColor("#ffffff"));
                txt1.setTextColor(Color.parseColor("#ffffff"));
                txt5.setTextColor(Color.parseColor("#ffffff"));
            });

            findViewById(R.id.cardview5).setOnClickListener(v -> {
                videoView.setSpeed(2.0f);
                speed = "2.0";
                txt5.setTextColor(Color.parseColor("#5187f4"));
                txt2.setTextColor(Color.parseColor("#ffffff"));
                txt3.setTextColor(Color.parseColor("#ffffff"));
                txt4.setTextColor(Color.parseColor("#ffffff"));
                txt1.setTextColor(Color.parseColor("#ffffff"));
            });
        }
    }

    public class CustomDrawerPopupView1 extends DrawerPopupView {
        public CustomDrawerPopupView1(@androidx.annotation.NonNull Context context) {
            super(context);
        }
        @Override
        protected int getImplLayoutId() {
            return R.layout.proportion;
        }
        @Override
        protected void onCreate() {
            super.onCreate();

            final TextView txt1 = findViewById(R.id.textview1);
            final TextView txt2 = findViewById(R.id.textview2);
            final TextView txt3 = findViewById(R.id.textview3);
            final TextView txt4 = findViewById(R.id.textview4);
            final TextView txt5 = findViewById(R.id.textview5);

            if (proportion.equals("默认")) {
                txt1.setTextColor(Color.parseColor("#5187f4"));
            }
            if (proportion.equals("16:9")) {
                txt2.setTextColor(Color.parseColor("#5187f4"));
            }
            if (proportion.equals("原始大小")) {
                txt3.setTextColor(Color.parseColor("#5187f4"));
            }
            if (proportion.equals("填充")) {
                txt4.setTextColor(Color.parseColor("#5187f4"));
            }
            if (proportion.equals("居中裁剪")) {
                txt5.setTextColor(Color.parseColor("#5187f4"));
            }

            findViewById(R.id.cardview1).setOnClickListener(v -> {
                videoView.setScreenScaleType(VideoView.SCREEN_SCALE_DEFAULT);
                proportion = "默认";
                txt1.setTextColor(Color.parseColor("#5187f4"));
                txt2.setTextColor(Color.parseColor("#ffffff"));
                txt3.setTextColor(Color.parseColor("#ffffff"));
                txt4.setTextColor(Color.parseColor("#ffffff"));
                txt5.setTextColor(Color.parseColor("#ffffff"));
            });

            findViewById(R.id.cardview2).setOnClickListener(v -> {
                videoView.setScreenScaleType(VideoView.SCREEN_SCALE_16_9);
                proportion = "16:9";
                txt2.setTextColor(Color.parseColor("#5187f4"));
                txt1.setTextColor(Color.parseColor("#ffffff"));
                txt3.setTextColor(Color.parseColor("#ffffff"));
                txt4.setTextColor(Color.parseColor("#ffffff"));
                txt5.setTextColor(Color.parseColor("#ffffff"));
            });

            findViewById(R.id.cardview3).setOnClickListener(v -> {
                videoView.setScreenScaleType(VideoView.SCREEN_SCALE_ORIGINAL);
                proportion = "原始大小";
                txt3.setTextColor(Color.parseColor("#5187f4"));
                txt2.setTextColor(Color.parseColor("#ffffff"));
                txt1.setTextColor(Color.parseColor("#ffffff"));
                txt4.setTextColor(Color.parseColor("#ffffff"));
                txt5.setTextColor(Color.parseColor("#ffffff"));
            });

            findViewById(R.id.cardview4).setOnClickListener(v -> {
                videoView.setScreenScaleType(VideoView.SCREEN_SCALE_MATCH_PARENT);
                proportion = "填充";
                txt4.setTextColor(Color.parseColor("#5187f4"));
                txt2.setTextColor(Color.parseColor("#ffffff"));
                txt3.setTextColor(Color.parseColor("#ffffff"));
                txt1.setTextColor(Color.parseColor("#ffffff"));
                txt5.setTextColor(Color.parseColor("#ffffff"));
            });

            findViewById(R.id.cardview5).setOnClickListener(v -> {
                videoView.setScreenScaleType(VideoView.SCREEN_SCALE_CENTER_CROP);
                proportion = "居中裁剪";
                txt5.setTextColor(Color.parseColor("#5187f4"));
                txt2.setTextColor(Color.parseColor("#ffffff"));
                txt3.setTextColor(Color.parseColor("#ffffff"));
                txt4.setTextColor(Color.parseColor("#ffffff"));
                txt1.setTextColor(Color.parseColor("#ffffff"));
            });
        }
    }

    @Override protected void onPause() {
        super.onPause();
        videoView.pause();
    }

    @Override protected void onResume() {
        super.onResume();
        videoView.resume();
    }

    @Override protected void onDestroy() {
        super.onDestroy();
        videoView.release();
    }

    @Override public void onBackPressed() {
            super.onBackPressed();
    }
}