package com.shixin.app;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.transition.TransitionManager;

import com.google.android.material.button.MaterialButton;
import com.gyf.immersionbar.ImmersionBar;
import com.shixin.app.utils.FileUtil;
import com.shixin.app.utils.ImageUtils;
import com.shixin.app.utils.MediaScanner;
import com.shixin.app.utils.Utils;
import com.tapadoo.alerter.Alerter;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PicturePixelActivity extends AppCompatActivity {

    @BindView(R.id.root)
    ViewGroup root;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.img)
    ImageView img;
    @BindView(R.id.seekbar1)
    DiscreteSeekBar seekbar1;
    @BindView(R.id.button1)
    MaterialButton button1;
    @BindView(R.id.button2)
    MaterialButton button2;

    private int color = 0xFF000000;
    public final int REQ_CD_IMAGE = 101;
    private Intent image = new Intent(Intent.ACTION_GET_CONTENT);
    private Bitmap bitmap = null;

    private File imgPath;
    private String SDCARD= Environment.getExternalStorageDirectory().getAbsolutePath();
    private ConvertThread convertThread;
    private boolean isFinish=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_pixel);
        ButterKnife.bind(this);

        ImmersionBar.with(this)
                .fitsSystemWindows(true)
                .statusBarColor(R.color.appbarColor)
                .navigationBarColor(R.color.backgroundColor)
                .autoDarkModeEnable(true)
                .init();

        toolbar.setTitle(getString(R.string.图片像素化));
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
                Alerter.create(PicturePixelActivity.this)
                        .setTitle(R.string.温馨提示)
                        .setText(R.string.请先选择图片)
                        .setBackgroundColorInt(getResources().getColor(R.color.error))
                        .show();
            }
            else {
                Utils.LoadingDialog(PicturePixelActivity.this);
                File output=getOutputFile();
                convertThread = new ConvertThread(handler, imgPath, output, seekbar1.getProgress());
                convertThread.start();
            }
        });

    }

    private File getOutputFile() {
        File dir = new File(SDCARD+File.separator+"噬心工具箱" + File.separator + "图片像素化");
        if (!dir.exists() ) {
            if(!dir.mkdirs())
            {
                return null;
            }
        }
        String name = imgPath.getName();
        File file = new File(dir, name);
        return  file;
    }
    private MediaScanner mediaScanner;
    @SuppressLint("HandlerLeak")
    private Handler handler=new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if(msg.what==1) {
                byte[] data=(byte[])msg.obj;
                if(data==null)
                {
                    Alerter.create(PicturePixelActivity.this)
                            .setTitle(R.string.温馨提示)
                            .setText(R.string.转换失败)
                            .setBackgroundColorInt(getResources().getColor(R.color.error))
                            .show();
                }
                else {
                    isFinish=true;
                    img.setImageBitmap(BitmapFactory.decodeByteArray(data, 0, data.length));
                    Alerter.create(PicturePixelActivity.this)
                            .setTitle(R.string.转换成功)
                            .setText(getString(R.string.已保存到)+getOutputFile().getAbsolutePath())
                            .setBackgroundColorInt(getResources().getColor(R.color.success))
                            .show();
                    Utils.loadDialog.dismiss();
                    if (mediaScanner == null)
                        mediaScanner = new MediaScanner(PicturePixelActivity.this);
                    mediaScanner.scanFile(getOutputFile().getAbsolutePath(), "image/*");
                }
            }
        }
    };

    private class ConvertThread extends Thread {
        private Handler handler;
        private  File in;
        private File out;
        private String text;
        private int fontSize;
        private  int style=0;
        private  int backColor;
        public ConvertThread(Handler handler,File in,File out,int backColor,String text,int fontSize)
        {
            this.handler=handler;
            this.in=in;
            this.out=out;
            this.text=text;
            this.fontSize=fontSize;
            this.style=0;
            this.backColor=backColor;
        }
        public ConvertThread(Handler handler,File in,File out,int fontSize)
        {
            this.handler=handler;
            this.in=in;
            this.out=out;
            this.fontSize=fontSize;
            this.style=1;
        }
        @Override
        public void run() {
            byte[] data=convert(in,out,text,fontSize);
            handler.sendMessage(handler.obtainMessage(1,data));
        }

        /**
         * 转换
         * @param input
         * @param output
         * @param text
         * @param fontSize
         */
        private  byte[] convert(File input,File output,String text,int fontSize) {
            Bitmap bitmap= BitmapFactory.decodeFile(input.getAbsolutePath());
            Bitmap target=null;
            if(style==0) {
                target = ImageUtils.getTextBitmap(bitmap,backColor, text, fontSize);
            }else
            {
                target = ImageUtils.getBlockBitmap(bitmap, fontSize);
            }
            FileOutputStream fileOutputStream=null;
            ByteArrayOutputStream byteArrayOutputStream=null;
            try {
                fileOutputStream=new FileOutputStream(output);
                byteArrayOutputStream=new ByteArrayOutputStream();

                target.compress(Bitmap.CompressFormat.PNG,100,byteArrayOutputStream);
                byte[] data=byteArrayOutputStream.toByteArray();
                fileOutputStream.write(data,0,data.length);
                fileOutputStream.flush();
                return data;

            } catch (Exception e) {
                e.printStackTrace();
            }
            finally {
                if(fileOutputStream!=null)
                {
                    try {
                        fileOutputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                if(byteArrayOutputStream!=null)
                {
                    try {
                        byteArrayOutputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            return null;
        }
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
                imgPath = new File(_filePath.get(0));
                bitmap = FileUtil.decodeSampleBitmapFromPath(_filePath.get(0), 1024, 1024);
                img.setImageBitmap(bitmap);

            }
        }
    }
}