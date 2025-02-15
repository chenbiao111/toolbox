package com.shixin.app;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.transition.TransitionManager;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.gyf.immersionbar.ImmersionBar;
import com.shixin.app.utils.FileUtil;
import com.shixin.app.utils.Utils;
import com.shixin.app.utils.VideoProcessorUtils;
import com.tapadoo.alerter.Alerter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ExtractAudioActivity extends AppCompatActivity {

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

    private String path = "";

    public final int REQ_CD_IMAGE = 101;
    private Intent image = new Intent(Intent.ACTION_GET_CONTENT);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_extract_audio);
        ButterKnife.bind(this);

        ImmersionBar.with(this)
                .fitsSystemWindows(true)
                .statusBarColor(R.color.appbarColor)
                .navigationBarColor(R.color.backgroundColor)
                .autoDarkModeEnable(true)
                .init();

        toolbar.setTitle(getString(R.string.视频提取音频));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        image.setType("video/*");
        image.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
        button1.setOnClickListener(v -> {
            startActivityForResult(image, REQ_CD_IMAGE);
        });

        button2.setOnClickListener(v -> {
            if (TextUtils.isEmpty(lj.getText().toString())){
                Alerter.create(ExtractAudioActivity.this)
                        .setTitle(R.string.温馨提示)
                        .setText(R.string.请先选择视频)
                        .setBackgroundColorInt(getResources().getColor(R.color.error))
                        .show();
            } else {
                Utils.LoadingDialog(ExtractAudioActivity.this);
                @SuppressLint("SimpleDateFormat")
                final String time = new SimpleDateFormat("HH-mm-ss").format(new Date());
                if (FileUtil.isExistFile(FileUtil.getExternalStorageDir().concat("/噬心工具箱/视频提取音频/"))) {
                } else {
                    FileUtil.makeDir(FileUtil.getExternalStorageDir().concat("/噬心工具箱/视频提取音频/"));
                }
                final String fileName = "Audio-"+time+".mp3";
                new Thread(() -> {
                    try {
                        boolean is = VideoProcessorUtils.splitAudioFile(lj.getText().toString(), FileUtil.getExternalStorageDir().concat("/噬心工具箱/视频提取音频/").concat(fileName));
                        if(is) {
                            MediaScannerConnection.scanFile((Activity) ExtractAudioActivity.this, new String[]{FileUtil.getExternalStorageDir().concat("/噬心工具箱/视频提取音频/").concat(fileName)}, null, new MediaScannerConnection.OnScanCompletedListener() {
                                public void onScanCompleted(String str, Uri uri) {

                                    Intent intent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
                                    intent.setData(uri);
                                    ((Activity) ExtractAudioActivity.this).sendBroadcast(intent);
                                    Utils.loadDialog.dismiss();
                                    Alerter.create((Activity) ExtractAudioActivity.this)
                                            .setTitle(R.string.提取成功)
                                            .setText(getString(R.string.已保存到)+FileUtil.getExternalStorageDir().concat("/噬心工具箱/视频提取音频/").concat(fileName))
                                            .setBackgroundColorInt(getResources().getColor(R.color.success))
                                            .show();
                                }});
                        }
                    } catch (Exception e) {
                    }
                }).start();
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

                TransitionManager.beginDelayedTransition(root, new androidx.transition.AutoTransition());
                card.setVisibility(View.VISIBLE);
                lj.setText(_filePath.get(0));
            }
        }
    }
}