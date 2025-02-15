package com.shixin.app;

import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.transition.TransitionManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.FutureTarget;
import com.bumptech.glide.request.target.Target;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.gyf.immersionbar.ImmersionBar;
import com.just.agentweb.AgentWeb;
import com.kongzue.baseokhttp.HttpRequest;
import com.shixin.app.utils.FileUtil;
import com.shixin.app.utils.Utils;
import com.tapadoo.alerter.Alerter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.shixin.app.utils.Utils.LoadingDialog;
import static com.shixin.app.utils.Utils.loadDialog;

public class SearchImageActivity extends AppCompatActivity {

    @BindView(R.id.root)
    ViewGroup root;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.fab)
    ExtendedFloatingActionButton fab;

    public final int REQ_CD_IMAGE = 101;
    private Intent image = new Intent(Intent.ACTION_GET_CONTENT);

    private String imagePath1 = null;
    private AgentWeb mAgentWeb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_image);
        ButterKnife.bind(this);

        ImmersionBar.with(this)
                .fitsSystemWindows(true)
                .statusBarColor(R.color.appbarColor)
                .navigationBarColor(R.color.backgroundColor)
                .autoDarkModeEnable(true)
                .init();

        toolbar.setTitle(getString(R.string.以图搜图));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish());

        image.setType("image/*");
        image.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
        fab.setOnClickListener(v -> {
            startActivityForResult(image, REQ_CD_IMAGE);
        });

        mAgentWeb = AgentWeb.with(this)
                .setAgentWebParent(root, new LinearLayout.LayoutParams(-1, -1))
                .useDefaultIndicator(Color.parseColor("#5187f4"))
                //.setWebChromeClient(mWebChromeClient)
                .createAgentWeb()
                .ready()
                .go(null);
        mAgentWeb.getWebCreator().getWebView().setOverScrollMode(WebView.OVER_SCROLL_NEVER);

        //WebView长按的监听
        mAgentWeb.getWebCreator().getWebView().setOnLongClickListener(v -> {
            final WebView.HitTestResult hitTestResult = mAgentWeb.getWebCreator().getWebView().getHitTestResult();
            // 如果是图片类型或者是带有图片链接的类型
            if (hitTestResult.getType() == WebView.HitTestResult.IMAGE_TYPE || hitTestResult.getType() == WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE) {
                // 弹出保存图片的对话框
                String pic = hitTestResult.getExtra();
                final AlertDialog mDialog = new MaterialAlertDialogBuilder(SearchImageActivity.this)
                        .create();
                View contentView = View.inflate(SearchImageActivity.this, R.layout.dialog_tp,null);
                mDialog.setView(contentView);
                mDialog.show();
                final ImageView imageView = contentView.findViewById(R.id.imageView);
                final MaterialButton button1 = contentView.findViewById(R.id.button1);
                final MaterialButton button2 = contentView.findViewById(R.id.button2);
                button1.setText(R.string.取消);
                button1.setBackgroundColor(getResources().getColor(R.color.itemBackColor));
                button1.setTextColor(getResources().getColor(R.color.editTextColor));
                button2.setText(R.string.保存);
                button2.setBackgroundColor(getResources().getColor(R.color.zts));
                button2.setTextColor(getResources().getColor(R.color.white));
                Glide.with(SearchImageActivity.this).load(pic).thumbnail(0.1f).fitCenter().priority(Priority.IMMEDIATE).into(imageView);
                button1.setOnClickListener(v1 -> {
                    mDialog.dismiss();
                });
                button2.setOnClickListener(v1 -> {
                    mDialog.dismiss();
                    try {
                        LoadingDialog(SearchImageActivity.this);
                        new Thread(() -> {
                            //java.lang.IllegalArgumentException: YOu must call this method on a background thread
                            //必须在子线程中进行
                            if (!FileUtil.isExistFile(FileUtil.getExternalStorageDir().concat("/噬心工具箱/以图搜图/"))) {
                                FileUtil.makeDir(FileUtil.getExternalStorageDir().concat("/噬心工具箱/以图搜图/"));
                            }
                            if (imageView.getDrawable() instanceof GifDrawable){
                                imagePath1 = Environment.getExternalStorageDirectory().getAbsolutePath()+"/噬心工具箱/以图搜图/"+System.currentTimeMillis()+".gif";
                            }else {
                                imagePath1 = Environment.getExternalStorageDirectory().getAbsolutePath()+"/噬心工具箱/以图搜图/"+System.currentTimeMillis()+".png";
                            }
                            String path = getImagePath(pic);
                            copyFile(path, imagePath1);
                            runOnUiThread(new Thread(() -> {
                                MediaScannerConnection.scanFile((Activity) SearchImageActivity.this, new String[]{imagePath1}, null, (str, uri) -> {
                                    Intent intent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
                                    intent.setData(uri);
                                    ((Activity) SearchImageActivity.this).sendBroadcast(intent);
                                    loadDialog.dismiss();
                                    Alerter.create((Activity) SearchImageActivity.this)
                                            .setTitle(R.string.保存成功)
                                            .setText(getString(R.string.已保存到) + imagePath1)
                                            .setBackgroundColorInt(getResources().getColor(R.color.success))
                                            .show();
                                });
                            }));
                        }).start();
                    } catch (Exception e) {
                    }
                });
                WindowManager.LayoutParams layoutParams = mDialog.getWindow().getAttributes();
                layoutParams.width = getResources().getDisplayMetrics().widthPixels / 10 * 9;
                mDialog.getWindow().setAttributes(layoutParams);
                return true;
            }
            return false;
        });


    }

    /**
     * Glide 获得图片缓存路径
     */
    private String getImagePath(String imgUrl) {
        String path = null;
        FutureTarget<File> future = Glide.with(this)
                .load(imgUrl)
                .downloadOnly(Target.SIZE_ORIGINAL,Target.SIZE_ORIGINAL);
        try {
            File cacheFile = future.get();
            path = cacheFile.getAbsolutePath();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return path;
    }

    public void copyFile(String oldPath, final String newPath) {
        try {
            int bytesum = 0;
            int byteread = 0;
            File oldfile = new File(oldPath);
            if (oldfile.exists()) { //文件存在时
                InputStream inStream = new FileInputStream(oldPath); //读入原文件
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1444];
                int length;
                while ( (byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread; //字节数 文件大小
                    System.out.println(bytesum);
                    fs.write(buffer, 0, byteread);
                }


                inStream.close();
            }
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {

                }
            });
        }
        catch (Exception e) {
            e.printStackTrace();

        }

    }

    @Override
    public void onBackPressed(){
        if (!mAgentWeb.back()){
            finish();
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

                Utils.LoadingDialog(this);
                HttpRequest.build((Activity) this,"http://pic.sogou.com/pic/upload_pic.jsp")
                        .addHeaders("Charset", "UTF-8")
                        .addParameter("File", new File(_filePath.get(0)))
                        .setResponseListener(new com.kongzue.baseokhttp.listener.ResponseListener() {
                            @Override
                            public void onResponse(String response, Exception error) {
                                Utils.loadDialog.dismiss();
                                TransitionManager.beginDelayedTransition(root, new androidx.transition.AutoTransition());
                                root.setVisibility(View.VISIBLE);
                                mAgentWeb.getWebCreator().getWebView().loadUrl("https://pic.sogou.com/pic/ris_searchList.jsp?statref=pic_index_common&keyword=" + response);
                            }
                        })
                        .doPost();
            }
        }
    }
}