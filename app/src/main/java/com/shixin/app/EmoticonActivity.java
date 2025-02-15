package com.shixin.app;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.widget.ImageView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.transition.TransitionManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.FutureTarget;
import com.bumptech.glide.request.target.Target;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.gyf.immersionbar.ImmersionBar;
import com.kongzue.baseokhttp.HttpRequest;
import com.kongzue.baseokhttp.listener.ResponseListener;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.shixin.app.utils.FileUtil;
import com.tapadoo.alerter.Alerter;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.shixin.app.utils.Utils.LoadingDialog;
import static com.shixin.app.utils.Utils.isVPNConnected;
import static com.shixin.app.utils.Utils.loadDialog;

public class EmoticonActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.textInputLayout)
    TextInputLayout textInputLayout;
    @BindView(R.id.textInputEditText)
    TextInputEditText textInputEditText;
    @BindView(R.id.fab)
    ExtendedFloatingActionButton fab;
    @BindView(R.id.rv)
    RecyclerView rv;
    @BindView(R.id.srl)
    SmartRefreshLayout srl;

    private int ye = 0;
    private String imagePath = null;
    private String imagePath1 = null;
    private ArrayList<HashMap<String, Object>> listmap = new ArrayList<>();
    private ArrayList<HashMap<String, Object>> listmapa = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emoticon);
        ButterKnife.bind(this);

        ImmersionBar.with(this)
                .fitsSystemWindows(true)
                .statusBarColor(R.color.appbarColor)
                .navigationBarColor(R.color.backgroundColor)
                .autoDarkModeEnable(true)
                .init();

        toolbar.setTitle(getString(R.string.表情包搜索));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        rv.setItemViewCacheSize(9999);

        textInputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                textInputLayout.setErrorEnabled(false);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });


        fab.setOnClickListener(view -> {
            if (TextUtils.isEmpty(textInputEditText.getText())){
                textInputLayout.setError(getString(R.string.请输入关键字));
                textInputLayout.setErrorEnabled(true);
            }else {
                if (!isVPNConnected(EmoticonActivity.this)) {
                    LoadingDialog(EmoticonActivity.this);
                    ye = 0;
                    HttpRequest.build(EmoticonActivity.this, "https://www.dbbqb.com/api/search/json?start=" + ye + "&w=" + textInputEditText.getText())
                            .addHeaders("Charset", "UTF-8")
                            .addHeaders("User-Agent", WebSettings.getDefaultUserAgent(EmoticonActivity.this))
                            .setResponseListener(new ResponseListener() {
                                @Override
                                public void onResponse(String response, Exception error) {
                                    loadDialog.dismiss();
                                    try {
                                        listmap = new Gson().fromJson(response, new TypeToken<ArrayList<HashMap<String, Object>>>() {}.getType());
                                        TransitionManager.beginDelayedTransition(srl, new androidx.transition.AutoTransition());
                                        rv.setLayoutManager(new StaggeredGridLayoutManager(3,StaggeredGridLayoutManager.VERTICAL));
                                        rv.setAdapter(new Recyclerview1Adapter(listmap));
                                        rv.getAdapter().notifyDataSetChanged();
                                    } catch (Exception e) {
                                    }
                                }
                            }).doGet();
                }
            }
        });

        srl.setOnLoadMoreListener(refreshLayout -> {
            if (!isVPNConnected(EmoticonActivity.this)) {
                ye = ye + 100;
                HttpRequest.build(EmoticonActivity.this, "https://www.dbbqb.com/api/search/json?start=" + ye + "&w=" + textInputEditText.getText())
                        .addHeaders("Charset", "UTF-8")
                        .addHeaders("User-Agent", WebSettings.getDefaultUserAgent(EmoticonActivity.this))
                        .setResponseListener(new ResponseListener() {
                            @Override
                            public void onResponse(String response, Exception error) {
                                try {
                                    srl.finishLoadMore(false);
                                    listmapa = new Gson().fromJson(response, new TypeToken<ArrayList<HashMap<String, Object>>>() {}.getType());
                                    TransitionManager.beginDelayedTransition(srl, new androidx.transition.AutoTransition());
                                    listmap.addAll(listmapa);
                                    rv.getAdapter().notifyItemRangeChanged(listmap.size() - listmapa.size(), listmapa.size());
                                } catch (Exception e) { }
                            }
                        }).doGet();
            }
        });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            FileUtil.deleteFile(imagePath);
        } catch (Exception e) {
        }
    }

    public class Recyclerview1Adapter extends RecyclerView.Adapter<Recyclerview1Adapter.ViewHolder> {
        ArrayList<HashMap<String, Object>> _data;
        public Recyclerview1Adapter(ArrayList<HashMap<String, Object>> _arr) {
            _data = _arr;
        }

        @Override
        public @NotNull ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater _inflater = (LayoutInflater) parent.getContext().getSystemService(LAYOUT_INFLATER_SERVICE);
            View _v = _inflater.inflate(R.layout.item_bz1, null);
            RecyclerView.LayoutParams _lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            _v.setLayoutParams(_lp);
            return new ViewHolder(_v);
        }

        @Override
        public void onBindViewHolder(ViewHolder _holder, final int _position) {
            View _view = _holder.itemView;

            final MaterialCardView cardview1 = (MaterialCardView) _view.findViewById(R.id.cardview1);
            final ImageView tp1 = (ImageView) _view.findViewById(R.id.tp1);

            Glide.with(EmoticonActivity.this).load("https://image.dbbqb.com/" + _data.get(_position).get("path")).thumbnail(0.1f).fitCenter().priority(Priority.IMMEDIATE).into(tp1);
            cardview1.setOnClickListener(_view1 -> {
                final AlertDialog mDialog = new MaterialAlertDialogBuilder(EmoticonActivity.this)
                        .create();
                View contentView = View.inflate(EmoticonActivity.this, R.layout.dialog_tp,null);
                mDialog.setView(contentView);
                mDialog.show();
                final ImageView imageView = contentView.findViewById(R.id.imageView);
                final MaterialButton button1 = contentView.findViewById(R.id.button1);
                final MaterialButton button2 = contentView.findViewById(R.id.button2);
                button1.setText(R.string.分享);
                button1.setBackgroundColor(getResources().getColor(R.color.zts));
                button1.setTextColor(getResources().getColor(R.color.white));
                button2.setText(R.string.保存);
                button2.setBackgroundColor(getResources().getColor(R.color.zts));
                button2.setTextColor(getResources().getColor(R.color.white));
                Glide.with(EmoticonActivity.this).load("https://image.dbbqb.com/" + _data.get(_position).get("path")).thumbnail(0.1f).fitCenter().priority(Priority.IMMEDIATE).into(imageView);
                button1.setOnClickListener(v1 -> {
                    mDialog.dismiss();
                    LoadingDialog(EmoticonActivity.this);
                    try {
                        FileUtil.deleteFile(imagePath);
                    } catch (Exception e) {
                    }
                    new Thread(() -> {
                        //java.lang.IllegalArgumentException: YOu must call this method on a background thread
                        //必须在子线程中进行
                        imagePath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+System.currentTimeMillis();
                        String path = getImagePath("https://image.dbbqb.com/" + _data.get(_position).get("path"));
                        copyFile(path, imagePath);
                        Intent intentBroadcast = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                        File file = new File(imagePath);
                        shareImage(EmoticonActivity.this, file, null, "com.tencent.mobileqq", "com.tencent.mobileqq.activity.JumpActivity");
                        runOnUiThread(new Thread(() -> {
                            loadDialog.dismiss();
                        }));
                    }).start();
                });
                button2.setOnClickListener(v1 -> {
                    mDialog.dismiss();
                    try {
                        LoadingDialog(EmoticonActivity.this);
                        new Thread(() -> {
                            //java.lang.IllegalArgumentException: YOu must call this method on a background thread
                            //必须在子线程中进行
                            if (!FileUtil.isExistFile(FileUtil.getExternalStorageDir().concat("/噬心工具箱/表情包搜索/"))) {
                                FileUtil.makeDir(FileUtil.getExternalStorageDir().concat("/噬心工具箱/表情包搜索/"));
                            }
                            if (imageView.getDrawable() instanceof GifDrawable){
                                imagePath1 = Environment.getExternalStorageDirectory().getAbsolutePath()+"/噬心工具箱/表情包搜索/"+System.currentTimeMillis()+".gif";
                            }else {
                                imagePath1 = Environment.getExternalStorageDirectory().getAbsolutePath()+"/噬心工具箱/表情包搜索/"+System.currentTimeMillis()+".png";
                            }
                            String path = getImagePath("https://image.dbbqb.com/" + _data.get(_position).get("path"));
                            copyFile(path, imagePath1);
                            runOnUiThread(new Thread(() -> {
                                MediaScannerConnection.scanFile((Activity) EmoticonActivity.this, new String[]{imagePath1}, null, (str, uri) -> {
                                    Intent intent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
                                    intent.setData(uri);
                                    ((Activity) EmoticonActivity.this).sendBroadcast(intent);
                                    loadDialog.dismiss();
                                    Alerter.create((Activity) EmoticonActivity.this)
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
            });
        }

        @Override
        public int getItemCount() {
            return _data.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder{
            public ViewHolder(View v){
                super(v);
            }
        }

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

    /**
     * 分享前必须执行本代码，主要用于兼容SDK18以上的系统
     */
    private static void checkFileUriExposure() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
            builder.detectFileUriExposure();
        }
    }

    /**
     * @param context  上下文
     * @param path     不为空的时候，表示分享单张图片，会检验图片文件是否存在
     * @param pathList 不为空的时候表示分享多张图片，会检验每一张图片是否存在
     * @param pkg      分享到的指定app的包名
     * @param cls      分享到的页面（微博不需要指定页面）
     */
    private void shareImage(Context context, File path, List<String> pathList, String pkg, String cls) {
        if (path == null && pathList == null) {

            return;
        }

        checkFileUriExposure();

        try {
            if (path != null) {

                Intent intent = new Intent();
                if (pkg != null && cls != null) {
                    //指定分享到的app
                    if (pkg.equals("com.sina.weibo")) {
                        //微博分享的需要特殊处理
                        intent.setPackage(pkg);
                    } else {
                        ComponentName comp = new ComponentName(pkg, cls);
                        intent.setComponent(comp);
                    }
                }
                intent.setAction(Intent.ACTION_SEND);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(path));
                intent.setType("image/*");   //分享文件
                context.startActivity(Intent.createChooser(intent, "分享"));
            }

        } catch (Exception e) {

        }
    }
}