package com.shixin.app.yellow;


import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.gyf.immersionbar.BarHide;
import com.gyf.immersionbar.ImmersionBar;
import com.shixin.app.R;
import com.shixin.app.utils.Utils;

import butterknife.ButterKnife;
import xyz.doikki.videoplayer.controller.GestureVideoController;
import xyz.doikki.videoplayer.exo.ExoMediaPlayer;
import xyz.doikki.videoplayer.exo.ExoMediaPlayerFactory;
import xyz.doikki.videoplayer.player.VideoView;

public class PlayerActivity extends AppCompatActivity {

    private VideoView<ExoMediaPlayer> videoView;
    private TextView textView;
    private ImageView imageView;
    private ImageView tui;
    private String speed = "1.0";
    private String proportion = "默认";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_y_player);
        ButterKnife.bind(this);

        ImmersionBar.with(this).hideBar(BarHide.FLAG_HIDE_BAR).fullScreen(true).init();
        videoView = findViewById(R.id.videoView);
        imageView = findViewById(R.id.imageView);
        textView = findViewById(R.id.textView);

        //videoView.startFullScreen();
        videoView.setPlayerFactory(ExoMediaPlayerFactory.create());
        videoView.setUrl(getIntent().getStringExtra("url"));
        //Toast.makeText(PlayerActivity.this,getIntent().getStringExtra("url"),Toast.LENGTH_SHORT).show();
        textView.setText(getIntent().getStringExtra("title"));
        Glide.with(this).load(getIntent().getStringExtra("img")).thumbnail(0.1f).fitCenter().priority(Priority.IMMEDIATE).into(imageView);


        //Toast.makeText(this,getIntent().getStringExtra("url"),Toast.LENGTH_LONG).show();

        //Utils.LoadingDialog(this);

        videoView.addOnStateChangeListener(new VideoView.OnStateChangeListener() {
            @Override
            public void onPlayerStateChanged(int playerState) {

            }

            @Override
            public void onPlayStateChanged(int playState) {
                switch (playState) {
                    //调用release方法会回到此状态
                    case VideoView.STATE_IDLE:
                        Utils.loadDialog.dismiss();
                        break;
                    case VideoView.STATE_PLAYING:
                    case VideoView.STATE_PAUSED:
                    case VideoView.STATE_PREPARED:
                    case VideoView.STATE_ERROR:
                    case VideoView.STATE_BUFFERED:
                        Utils.loadDialog.dismiss();
                        break;
                    case VideoView.STATE_PREPARING:
                    case VideoView.STATE_BUFFERING:
                        Utils.LoadingDialog(PlayerActivity.this);
                        break;
                    case VideoView.STATE_PLAYBACK_COMPLETED:
                        Utils.loadDialog.dismiss();
                        break;
                }
            }
        });

        videoView.setVideoController(new GestureVideoController(this) {
            @Override
            protected int getLayoutId() {
                return 0;
            }
        });
        videoView.start();

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