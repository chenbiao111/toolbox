package com.shixin.app;

import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.transition.TransitionManager;

import com.google.android.material.button.MaterialButton;
import com.gyf.immersionbar.ImmersionBar;
import com.shixin.app.lowpoly.PolyfunKey;
import com.shixin.app.lowpoly.StartPolyFun;
import com.shixin.app.utils.FileUtil;
import com.shixin.app.utils.Utils;
import com.tapadoo.alerter.Alerter;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PictureLowPolyActivity extends AppCompatActivity {

    @BindView(R.id.root)
    ViewGroup root;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.img)
    public ImageView img;
    @BindView(R.id.button1)
    MaterialButton button1;
    @BindView(R.id.button2)
    MaterialButton button2;
    @BindView(R.id.button3)
    MaterialButton button3;
    @BindView(R.id.seekbar1)
    DiscreteSeekBar seekbar1;

    public final int REQ_CD_IMAGE = 101;
    private Intent image = new Intent(Intent.ACTION_GET_CONTENT);
    private Bitmap bitmap = null;
    private boolean isDone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_lowpoly);
        ButterKnife.bind(this);

        ImmersionBar.with(this)
                .fitsSystemWindows(true)
                .statusBarColor(R.color.appbarColor)
                .navigationBarColor(R.color.backgroundColor)
                .autoDarkModeEnable(true)
                .init();

        toolbar.setTitle(getString(R.string.LowPoly图片生成));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        PolyfunKey.pc = 1200;
        image.setType("image/*");
        image.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
        button1.setOnClickListener(v -> {
            startActivityForResult(image, REQ_CD_IMAGE);
        });

        button2.setOnClickListener(v -> {
            if (bitmap == null) {
                Alerter.create(PictureLowPolyActivity.this)
                        .setTitle(R.string.温馨提示)
                        .setText(R.string.请先选择图片)
                        .setBackgroundColorInt(Color.parseColor("#F44336"))
                        .show();
            }
            else {
                if (!isDone){
                    Utils.LoadingDialog(PictureLowPolyActivity.this);
                    new StartPolyFun(PictureLowPolyActivity.this).start();
                } else {
                    Alerter.create(PictureLowPolyActivity.this)
                            .setTitle(R.string.温馨提示)
                            .setText(R.string.请重新选择图片)
                            .setBackgroundColorInt(Color.parseColor("#4caf50"))
                            .show();
                }
                isDone = true;
            }
        });

        button3.setOnClickListener(v -> {
            if (bitmap == null) {
                Alerter.create(PictureLowPolyActivity.this)
                        .setTitle(R.string.温馨提示)
                        .setText(R.string.请先选择或者生成图片)
                        .setBackgroundColorInt(getResources().getColor(R.color.error))
                        .show();
            }
            else {
                Utils.LoadingDialog(PictureLowPolyActivity.this);
                new Thread((Runnable) () -> {
                    String savedFile = Utils.SaveImage(PictureLowPolyActivity.this, ((BitmapDrawable) img.getDrawable()).getBitmap(), "/噬心工具箱/LowPoly图片/", "Image-" + new SimpleDateFormat("HH-mm-ss").format(new Date()) + ".png");
                    if (savedFile != null) {
                        MediaScannerConnection.scanFile((Activity) PictureLowPolyActivity.this, new String[]{savedFile}, null, (MediaScannerConnection.OnScanCompletedListener) (str, uri) -> {
                            Intent intent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
                            intent.setData(uri);
                            ((Activity) PictureLowPolyActivity.this).sendBroadcast(intent);
                            Utils.loadDialog.dismiss();
                            Alerter.create((Activity) PictureLowPolyActivity.this)
                                    .setTitle(R.string.保存成功)
                                    .setText(getString(R.string.已保存到) + savedFile)
                                    .setBackgroundColorInt(getResources().getColor(R.color.success))
                                    .show();
                        });
                    } else {
                        Utils.loadDialog.dismiss();
                    }
                }).start();
            }
        });

        seekbar1.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
                PolyfunKey.pc = value;
            }

            @Override
            public void onStartTrackingTouch(DiscreteSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(DiscreteSeekBar seekBar) {

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
                img.setVisibility(View.VISIBLE);
                bitmap = FileUtil.decodeSampleBitmapFromPath(_filePath.get(0), 1024, 1024);
                img.setImageBitmap(bitmap);
                isDone = false;
            }
        }
    }
}