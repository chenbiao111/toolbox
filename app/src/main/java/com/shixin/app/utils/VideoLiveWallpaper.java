package com.shixin.app.utils;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.service.wallpaper.WallpaperService;
import android.util.Log;
import android.view.SurfaceHolder;

import java.io.IOException;

/**
 * thx for https://github.com/songixan/Wallpaper
 */
public class VideoLiveWallpaper extends WallpaperService {

    public Engine onCreateEngine() {
        return new VideoEngine();
    }


    public static final String VIDEO_PARAMS_CONTROL_ACTION = "com.shixin.gjx";
    public static final String KEY_ACTION = "action";
    public static final int ACTION_VOICE_SILENCE = 110;
    public static final int ACTION_VOICE_NORMAL = 111;

    public static void voiceSilence(Context context) {
        Intent intent = new Intent(VideoLiveWallpaper.VIDEO_PARAMS_CONTROL_ACTION);
        intent.putExtra(VideoLiveWallpaper.KEY_ACTION, VideoLiveWallpaper.ACTION_VOICE_SILENCE);
        context.sendBroadcast(intent);
    }


    public static void voiceNormal(Context context) {
        Intent intent = new Intent(VideoLiveWallpaper.VIDEO_PARAMS_CONTROL_ACTION);
        intent.putExtra(VideoLiveWallpaper.KEY_ACTION, VideoLiveWallpaper.ACTION_VOICE_NORMAL);
        context.sendBroadcast(intent);
    }


    public static void setToWallPaper(Activity context) {
        final Intent intent = new Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER);
        intent.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                new ComponentName(context, VideoLiveWallpaper.class));
        context.startActivity(intent);
    }

    class VideoEngine extends Engine {

        private MediaPlayer mMediaPlayer;

        private BroadcastReceiver mVideoParamsControlReceiver;
        
        private SharedPreferences video;

        @Override
        public void onCreate(final SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);
            Log.e("LOG", "onCreate");
            video = getApplicationContext().getSharedPreferences("video", Activity.MODE_MULTI_PROCESS);
            IntentFilter intentFilter = new IntentFilter(VIDEO_PARAMS_CONTROL_ACTION);
            registerReceiver(mVideoParamsControlReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    int action = intent.getIntExtra(KEY_ACTION, -1);
                    switch (action) {
                        case ACTION_VOICE_NORMAL:
                            mMediaPlayer.setVolume(1.0f, 1.0f);
                            break;
                        case ACTION_VOICE_SILENCE:
                            mMediaPlayer.setVolume(0, 0);
                            break;
                    }
                }
            }, intentFilter);


        }

        @Override
        public void onDestroy() {
            unregisterReceiver(mVideoParamsControlReceiver);
            super.onDestroy();

        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            Log.e("LOG", "onVisibilityChanged");
            if (visible) {
                mMediaPlayer.start();
            } else {
                mMediaPlayer.pause();
            }
        }


        @Override
        public void onSurfaceCreated(SurfaceHolder holder) {
            super.onSurfaceCreated(holder);
            Log.e("LOG", "onSurfaceCreated");
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setSurface(holder.getSurface());
            play();
        }

        @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            super.onSurfaceChanged(holder, format, width, height);
            Log.e("LOG", "onSurfaceChanged");
        }

        @Override
        public void onSurfaceDestroyed(SurfaceHolder holder) {
            super.onSurfaceDestroyed(holder);
            Log.e("LOG", "onSurfaceDestroyed");
            mMediaPlayer.release();
            mMediaPlayer = null;

        }

        private void play() {
            try {
                String path = video.getString("path", "");
                boolean voice = video.getBoolean("voice", false);
                mMediaPlayer.setDataSource(path);
                mMediaPlayer.setLooping(true);
                if (voice) mMediaPlayer.setVolume(1.0f, 1.0f);
                else mMediaPlayer.setVolume(0, 0);
                mMediaPlayer.setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);
                mMediaPlayer.prepare();
                mMediaPlayer.start();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}  