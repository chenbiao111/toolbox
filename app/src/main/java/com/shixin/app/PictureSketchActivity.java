package com.shixin.app;

import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.transition.TransitionManager;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.gyf.immersionbar.ImmersionBar;
import com.shixin.app.utils.FileUtil;
import com.shixin.app.utils.Utils;
import com.tapadoo.alerter.Alerter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.glide.transformations.gpu.SketchFilterTransformation;

public class PictureSketchActivity extends AppCompatActivity {

    @BindView(R.id.root)
    ViewGroup root;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.img)
    ImageView img;
    @BindView(R.id.button1)
    MaterialButton button1;
    @BindView(R.id.button2)
    MaterialButton button2;

    public final int REQ_CD_IMAGE = 101;
    private Intent image = new Intent(Intent.ACTION_GET_CONTENT);
    private Bitmap bitmap = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_sketch);
        ButterKnife.bind(this);

        ImmersionBar.with(this)
                .fitsSystemWindows(true)
                .statusBarColor(R.color.appbarColor)
                .navigationBarColor(R.color.backgroundColor)
                .autoDarkModeEnable(true)
                .init();

        toolbar.setTitle(getString(R.string.图片转素描图));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        image.setType("image/*");
        image.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
        button1.setOnClickListener(v -> {
            startActivityForResult(image, REQ_CD_IMAGE);
        });

        button2.setOnClickListener(v -> {
            if (bitmap == null) {
                Alerter.create(PictureSketchActivity.this)
                        .setTitle(R.string.温馨提示)
                        .setText(R.string.请先选择图片)
                        .setBackgroundColorInt(getResources().getColor(R.color.error))
                        .show();
            }
            else {
                Utils.LoadingDialog(PictureSketchActivity.this);
                new Thread((Runnable) () -> {
                    String savedFile = Utils.SaveImage(PictureSketchActivity.this, ((BitmapDrawable) img.getDrawable()).getBitmap(), "/噬心工具箱/图片转素描图/", "Image-" + new SimpleDateFormat("HH-mm-ss").format(new Date()) + ".png");
                    if (savedFile != null) {
                        MediaScannerConnection.scanFile((Activity) PictureSketchActivity.this, new String[]{savedFile}, null, (MediaScannerConnection.OnScanCompletedListener) (str, uri) -> {
                            Intent intent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
                            intent.setData(uri);
                            ((Activity) PictureSketchActivity.this).sendBroadcast(intent);
                            Utils.loadDialog.dismiss();
                            Alerter.create((Activity) PictureSketchActivity.this)
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
                Glide.with(this)
                        .asBitmap()
                        .load(bitmap)
                        .apply(com.bumptech.glide.request.RequestOptions.bitmapTransform(new SketchFilterTransformation()))
                        .into(img);

            }
        }
    }
}