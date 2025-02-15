package com.shixin.app;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.transition.TransitionManager;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.gyf.immersionbar.ImmersionBar;
import com.kongzue.baseokhttp.HttpRequest;
import com.shixin.app.utils.BackgroundTask;
import com.shixin.app.utils.FileUtil;
import com.tapadoo.alerter.Alerter;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.shixin.app.utils.Utils.LoadingDialog;
import static com.shixin.app.utils.Utils.SaveImage;
import static com.shixin.app.utils.Utils.loadDialog;

public class PictureColoringActivity extends AppCompatActivity {

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
    @BindView(R.id.card1)
    MaterialCardView card1;
    @BindView(R.id.lj)
    TextView lj;

    @BindView(R.id.imageview1)
    ImageView imageview1;

    private String path = "";
    private String base64;

    public final int REQ_CD_IMAGE = 101;
    private Intent image = new Intent(Intent.ACTION_GET_CONTENT);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_coloring);
        ButterKnife.bind(this);

        ImmersionBar.with(this)
                .fitsSystemWindows(true)
                .statusBarColor(R.color.appbarColor)
                .navigationBarColor(R.color.backgroundColor)
                .autoDarkModeEnable(true)
                .init();

        toolbar.setTitle(getString(R.string.黑白图上色));
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
            if (TextUtils.isEmpty(lj.getText().toString())){
                Alerter.create(PictureColoringActivity.this)
                        .setTitle(R.string.温馨提示)
                        .setText(R.string.请先选择图片)
                        .setBackgroundColorInt(getResources().getColor(R.color.error))
                        .show();
            } else {
                LoadingDialog(PictureColoringActivity.this);
                @SuppressLint("SimpleDateFormat") final String time = new SimpleDateFormat("HH-mm-ss").format(new Date());
                new BackgroundTask(PictureColoringActivity.this) {
                    @Override
                    public void doInBackground() {
                        base64 = imageToBase64(path);
                    }

                    @Override
                    public void onPostExecute() {
                        //loadDialog.dismiss();
                        HttpRequest.build(PictureColoringActivity.this,"https://aip.baidubce.com/rest/2.0/image-process/v1/colourize?access_token=24.a4287646a4503bad7b510edf712aedd3.2592000.1636002162.282335-19823466")
                                .addHeaders("Charset", "UTF-8")
                                .addParameter("image", base64)
                                .addParameter("option", null)
                                .setResponseListener(new com.kongzue.baseokhttp.listener.ResponseListener() {
                                    @Override
                                    public void onResponse(String response, Exception error) {
                                        try {
                                            HashMap<String, Object> map = new Gson().fromJson(response, new TypeToken<HashMap<String, Object>>() {}.getType());
                                            TransitionManager.beginDelayedTransition(root, new androidx.transition.AutoTransition());
                                            card1.setVisibility(View.VISIBLE);
                                            //textView.setText(response);
                                            imageview1.setImageBitmap(base64ToFile(String.valueOf(map.get("image"))));
                                            loadDialog.dismiss();
                                        } catch (Exception exception) {

                                        }
                                    }
                                })
                                .doPost();
                    }
                }.execute();

            }

        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_picture_water,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem){
        final String title = (String) menuItem.getTitle();
        if (title.equals(getString(R.string.保存图片))){
            LoadingDialog(PictureColoringActivity.this);
            new Thread(() -> {
                try {
                    @SuppressLint("SimpleDateFormat")
                    String savedFile = SaveImage(PictureColoringActivity.this, ((BitmapDrawable) imageview1.getDrawable()).getBitmap(), "/噬心工具箱/黑白图上色/", "Image-" + new SimpleDateFormat("HH-mm-ss").format(new Date()) + ".png");
                    if (savedFile != null) {
                        MediaScannerConnection.scanFile(PictureColoringActivity.this, new String[]{savedFile}, null, (MediaScannerConnection.OnScanCompletedListener) (str, uri) -> {
                            Intent intent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
                            intent.setData(uri);
                            PictureColoringActivity.this.sendBroadcast(intent);
                            loadDialog.dismiss();
                            Alerter.create(PictureColoringActivity.this)
                                    .setTitle(R.string.保存成功)
                                    .setText(getString(R.string.已保存到) + savedFile)
                                    .setBackgroundColorInt(getResources().getColor(R.color.success))
                                    .show();
                        });
                    } else {
                        loadDialog.dismiss();
                    }
                } catch (Exception e) {
                    loadDialog.dismiss();
                }
            }).start();
        }
        return super.onOptionsItemSelected(menuItem);
    }

    /**
     * 将Base64编码转换为图片Bitmap
     */
    public static Bitmap base64ToFile(String base64Str) {


        byte[] decodedString = Base64.decode(base64Str, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

        return decodedByte;
    }


    /**
     * 将图片转换成Base64编码的字符串
     */
    public static String imageToBase64(String path){
        if(TextUtils.isEmpty(path)){
            return null;
        }
        InputStream is = null;
        byte[] data = null;
        String result = null;
        try{
            is = new FileInputStream(path);
            //创建一个字符流大小的数组。
            data = new byte[is.available()];
            //写入数组
            is.read(data);
            //用默认的编码格式进行编码
            result = Base64.encodeToString(data,Base64.NO_WRAP);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(null !=is){
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        return result;
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