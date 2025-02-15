package com.shixin.app;

import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.transition.TransitionManager;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.gyf.immersionbar.ImmersionBar;
import com.shixin.app.utils.BitmapPixelUtil;
import com.shixin.app.utils.FileUtil;
import com.shixin.app.utils.Utils;
import com.tapadoo.alerter.Alerter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.shixin.app.utils.Utils.LoadingDialog;

public class PictureHideActivity extends AppCompatActivity {

    @BindView(R.id.root)
    ViewGroup root;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.button1)
    MaterialButton button1;
    @BindView(R.id.button2)
    MaterialButton button2;
    @BindView(R.id.fab)
    ExtendedFloatingActionButton fab;
    @BindView(R.id.tp1)
    ImageView tp1;
    @BindView(R.id.tp2)
    ImageView tp2;

    public final int REQ_CD_IMAGE = 101;
    public final int REQ_CD_IMAGE1 = 102;
    private Intent image = new Intent(Intent.ACTION_GET_CONTENT);
    private boolean isInputImage1Ready, isInputImage2Ready, isHanding;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_hide);
        ButterKnife.bind(this);

        ImmersionBar.with(this)
                .fitsSystemWindows(true)
                .statusBarColor(R.color.appbarColor)
                .navigationBarColor(R.color.backgroundColor)
                .autoDarkModeEnable(true)
                .init();

        toolbar.setTitle(getString(R.string.隐藏图制作));
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
            startActivityForResult(image, REQ_CD_IMAGE1);
        });

        fab.setOnClickListener(v -> {
            handleStartButtonEvent();
        });

    }

    private void handleStartButtonEvent() {
        if (isInputImage1Ready && isInputImage2Ready) {
            isInputImage1Ready = isInputImage2Ready = false;
            LoadingDialog(PictureHideActivity.this);
            new Thread() {
                @Override
                public void run() {
                    isHanding = true;
                    Bitmap bitmap1 = ((BitmapDrawable) tp1.getDrawable()).getBitmap();
                    Bitmap bitmap2 = ((BitmapDrawable) tp2.getDrawable()).getBitmap();
                    if (bitmap1.getByteCount() > bitmap2.getByteCount()) {
                        bitmap1 = BitmapPixelUtil.scaleBitmap(bitmap1, bitmap2.getWidth(), bitmap2.getHeight());
                    } else if (bitmap1.getByteCount() < bitmap2.getByteCount()) {
                        bitmap2 = BitmapPixelUtil.scaleBitmap(bitmap2, bitmap1.getWidth(), bitmap1.getHeight());
                    }
                    Bitmap resultBitmap = BitmapPixelUtil.makeHideImage(bitmap1, bitmap2, progress -> runOnUiThread(() ->
                            ((ContentLoadingProgressBar) findViewById(R.id.progress_bar)).setProgress((int) (progress * 100))));
                    runOnUiThread(() -> {
                        //tp3.setImageBitmap(resultBitmap);
                        new Thread(() -> {
                            String savedFile = Utils.SaveImage(PictureHideActivity.this, resultBitmap, "/噬心工具箱/隐藏图制作/", "Image-" + new SimpleDateFormat("HH-mm-ss").format(new Date()) + ".png");
                            if (savedFile != null){
                                MediaScannerConnection.scanFile(PictureHideActivity.this, new String[]{savedFile}, null, new MediaScannerConnection.OnScanCompletedListener() {
                                    public void onScanCompleted(String str, Uri uri) {
                                        Intent intent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
                                        intent.setData(uri);
                                        PictureHideActivity.this.sendBroadcast(intent);
                                        Utils.loadDialog.dismiss();
                                        Alerter.create(PictureHideActivity.this)
                                                .setTitle(R.string.保存成功)
                                                .setText(getString(R.string.已保存到) + savedFile)
                                                .setBackgroundColorInt(getResources().getColor(R.color.success))
                                                .show();
                                    }
                                });
                            }else{
                                Utils.loadDialog.dismiss();
                            }
                        }).start();
                        isInputImage1Ready = isInputImage2Ready = true;
                        isHanding = false;
                    });
                }
            }.start();
        }
    }

    @Override
    protected void onActivityResult(int _requestCode, int _resultCode, Intent _data) {
        super.onActivityResult(_requestCode, _resultCode, _data);
        ImageView imageView = null;
        switch (_requestCode) {
            case REQ_CD_IMAGE:
                if (_resultCode == Activity.RESULT_OK) {
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
                    }
                    imageView = (ImageView) findViewById(R.id.tp1);
                    isInputImage1Ready = true;

                    if (imageView != null) {
                        Bitmap bitmap = BitmapFactory.decodeFile(_filePath.get(0)).copy(Bitmap.Config.ARGB_8888, true);
                        imageView.setImageBitmap(bitmap);
                    }
                }
                break;
            case REQ_CD_IMAGE1:
                if (_resultCode == Activity.RESULT_OK) {
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
                    }
                    imageView = (ImageView) findViewById(R.id.tp2);
                    isInputImage2Ready = true;

                    if (imageView != null) {
                        Bitmap bitmap = BitmapFactory.decodeFile(_filePath.get(0)).copy(Bitmap.Config.ARGB_8888, true);
                        imageView.setImageBitmap(bitmap);
                    }
                }
                break;
            default:
                break;
        }

        if (isInputImage1Ready && isInputImage2Ready) {
            TransitionManager.beginDelayedTransition(root, new androidx.transition.AutoTransition());
            fab.setVisibility(View.VISIBLE);
        }
    }
}