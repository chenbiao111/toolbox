package com.shixin.app;

import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.transition.TransitionManager;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.card.MaterialCardView;
import com.gyf.immersionbar.ImmersionBar;
import com.shixin.app.utils.FileUtil;
import com.shixin.app.utils.VideoLiveWallpaper;
import com.shixin.app.utils.VideoLiveWallpaper2;
import com.tapadoo.alerter.Alerter;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class VideoWallpaperActivity extends AppCompatActivity {

    @BindView(R.id.root)
    ViewGroup root;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.button1)
    MaterialButton button1;
    @BindView(R.id.button2)
    MaterialButton button2;
    @BindView(R.id.card)
    MaterialCardView card;
    @BindView(R.id.lj)
    TextView lj;
    @BindView(R.id.toggle)
    MaterialButtonToggleGroup toggle;

    public final int REQ_CD_IMAGE = 101;
    private Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
    private SharedPreferences video;
    private String path = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_wallpaper);
        ButterKnife.bind(this);

        ImmersionBar.with(this)
                .fitsSystemWindows(true)
                .statusBarColor(R.color.appbarColor)
                .navigationBarColor(R.color.backgroundColor)
                .autoDarkModeEnable(true)
                .init();

        toolbar.setTitle(getString(R.string.桌面视频壁纸));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        intent.setType("video/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);

        video = getSharedPreferences("video", Activity.MODE_PRIVATE);
        video.edit().putBoolean("voice", true).apply();



        toggle.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (checkedId == R.id.b1 && isChecked) {
                video.edit().putBoolean("voice", true).apply();
            }
            if (checkedId == R.id.b2 && isChecked) {
                video.edit().putBoolean("voice", false).apply();
            }
        });

        button1.setOnClickListener(v -> {
            startActivityForResult(intent, REQ_CD_IMAGE);
        });

        button2.setOnClickListener(v -> {
            if (path.length() != 0) {
                try {
                    video.edit().putString("path", path).apply();
                    if (video.getString("service", "Service1").equals("Service2")) {
                        VideoLiveWallpaper.setToWallPaper(this);
                        video.edit().putString("service", "Service1").apply();
                    } else {
                        VideoLiveWallpaper2.setToWallPaper(this);
                        video.edit().putString("service", "Service2").apply();
                    }
                } catch (Exception e) {
                }
            } else {
                Alerter.create((Activity) VideoWallpaperActivity.this)
                        .setTitle(R.string.温馨提示)
                        .setText(R.string.请先选择视频后再操作)
                        .setBackgroundColorInt(getResources().getColor(R.color.error))
                        .show();
            }
        });
    }

    @Override
    protected void onActivityResult(int _requestCode, int _resultCode, Intent _data) {
        super.onActivityResult(_requestCode, _resultCode, _data);

        if (_requestCode == REQ_CD_IMAGE && _resultCode == Activity.RESULT_OK) {
            ArrayList<String> _filePath = new ArrayList<>();
            if (_data != null) {
                if (_data.getClipData() != null) {
                    for (int _index = 0; _index < _data.getClipData().getItemCount(); _index++) {
                        ClipData.Item _item = _data.getClipData().getItemAt(_index);
                        _filePath.add(FileUtil.convertUriToFilePath(getApplicationContext(), _item.getUri()));
                    }
                } else {
                    _filePath.add(FileUtil.convertUriToFilePath(getApplicationContext(), _data.getData()));
                }
                path = _filePath.get(0);
                TransitionManager.beginDelayedTransition(root, new androidx.transition.AutoTransition());
                card.setVisibility(View.VISIBLE);
                lj.setText(_filePath.get(0));
            }
        }
    }
}