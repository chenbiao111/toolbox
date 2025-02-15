package com.shixin.app;

import static com.shixin.app.QqVoiceActivity.listFileSortByModifyTime;
import static com.shixin.app.utils.Utils.LoadingDialog;
import static com.shixin.app.utils.Utils.SaveImage;
import static com.shixin.app.utils.Utils.loadDialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.AutoTransition;
import androidx.transition.TransitionManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.downloader.Error;
import com.downloader.OnDownloadListener;
import com.downloader.PRDownloader;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.gyf.immersionbar.ImmersionBar;
import com.kongzue.baseokhttp.HttpRequest;
import com.shixin.app.utils.BackgroundTask;
import com.shixin.app.utils.FileUtil;
import com.shixin.app.utils.ZipUtils;
import com.tapadoo.alerter.Alerter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AvatarMakeActivity extends AppCompatActivity {

    @BindView(R.id.root)
    ViewGroup root;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.layout)
    ConstraintLayout layout;
    @BindView(R.id.image)
    RelativeLayout relativeLayout;
    @BindView(R.id.button1)
    MaterialButton button1;
    @BindView(R.id.button2)
    MaterialButton button2;

    @BindView(R.id.imageview1)
    ImageView imageview1;
    @BindView(R.id.imageview2)
    ImageView imageview2;
    @BindView(R.id.rv)
    RecyclerView rv;
    private HashMap<String, Object> map = new HashMap<>();
    private ArrayList<HashMap<String, Object>> listmap = new ArrayList<>();

    private Bitmap bitmap = null;

    public final int REQ_CD_IMAGE = 101;
    public final int REQ_CD_IMAGE1 = 102;
    private Intent image = new Intent(Intent.ACTION_GET_CONTENT);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_avatar_make);
        ButterKnife.bind(this);

        ImmersionBar.with(this)
                .fitsSystemWindows(true)
                .statusBarColor(R.color.appbarColor)
                .navigationBarColor(R.color.backgroundColor)
                .autoDarkModeEnable(true)
                .init();

        toolbar.setTitle(getString(R.string.头像制作));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        image.setType("image/*");
        image.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);

        List<File> length = listFileSortByModifyTime(FileUtil.getExternalStorageDir().concat("/噬心工具箱/头像素材/"));
        if (length.size() == 0){
            Download(AvatarMakeActivity.this,"下载素材","未检测到素材文件，请点击下载后才能够正常使用","https://gitee.com/x1602965165/DaiMeng/raw/master/res/%E5%A4%B4%E5%83%8F%E5%88%B6%E4%BD%9C.zip","/噬心工具箱/","头像素材.zip");
        } else {
            for (File file : length) {
                map = new HashMap<>();
                map.put("name", file.toString());
                listmap.add(map);
                rv.setAdapter(new Recyclerview1Adapter(listmap));
                rv.getAdapter().notifyDataSetChanged();
            }
        }

        button1.setOnClickListener(v -> {
            startActivityForResult(image, REQ_CD_IMAGE);
        });

        button2.setOnClickListener(v -> {
            startActivityForResult(image, REQ_CD_IMAGE1);
        });

    }

    public class Recyclerview1Adapter extends RecyclerView.Adapter<Recyclerview1Adapter.ViewHolder> {
        ArrayList<HashMap<String, Object>> _data;
        public Recyclerview1Adapter(ArrayList<HashMap<String, Object>> _arr) {
            _data = _arr;
        }

        @Override
        public Recyclerview1Adapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater _inflater = (LayoutInflater)parent.getContext().getSystemService(parent.getContext().LAYOUT_INFLATER_SERVICE);
            View _v = _inflater.inflate(R.layout.item_bz1, null);
            RecyclerView.LayoutParams _lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            _v.setLayoutParams(_lp);
            return new Recyclerview1Adapter.ViewHolder(_v);
        }

        @Override
        public void onBindViewHolder(Recyclerview1Adapter.ViewHolder _holder, final int _position) {
            View _view = _holder.itemView;

            final MaterialCardView cardview1 = _view.findViewById(R.id.cardview1);
            final ImageView tp1 = _view.findViewById(R.id.tp1);

            Glide.with(AvatarMakeActivity.this).load(String.valueOf(_data.get(_position).get("name"))).thumbnail(0.1f).fitCenter().priority(Priority.IMMEDIATE).into(tp1);
            cardview1.setOnClickListener(v -> {
                imageview2.setImageBitmap(FileUtil.decodeSampleBitmapFromPath(String.valueOf(_data.get(_position).get("name")), 1024, 1024));
                TransitionManager.beginDelayedTransition(root, new androidx.transition.AutoTransition());
                layout.setVisibility(View.VISIBLE);
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

    //下载文件
    public void Download(Context context, String title, String content, String url, String path, String name) {
        final AlertDialog mDialog = new MaterialAlertDialogBuilder(context)
                .create();
        View contentView = View.inflate(context, R.layout.dialog_download,null);
        mDialog.setTitle(title);
        mDialog.setMessage(content);
        mDialog.setView(contentView);
        mDialog.show();
        final MaterialButton button1 = contentView.findViewById(R.id.button1);
        final MaterialButton button2 = contentView.findViewById(R.id.button2);
        final ProgressBar progressBar = contentView.findViewById(R.id.jdt);
        final TextInputEditText textInputEditText = contentView.findViewById(R.id.textInputEditText);
        final TextInputLayout textInputLayout = contentView.findViewById(R.id.textInputLayout);
        textInputEditText.setText(name);
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
        button1.setText(R.string.取消);
        button1.setBackgroundColor(context.getResources().getColor(R.color.itemBackColor));
        button2.setText(R.string.保存);
        button2.setBackgroundColor(context.getResources().getColor(R.color.zts));
        button1.setOnClickListener(v11 -> {
            mDialog.dismiss();
        });
        button2.setOnClickListener(v11 -> {
            if (TextUtils.isEmpty(textInputEditText.getText())) {
                textInputLayout.setError(context.getString(R.string.请输入文件名称));
                textInputLayout.setErrorEnabled(true);
            } else {
                button2.setText(R.string.请稍等);
                progressBar.setVisibility(View.VISIBLE);
                if (!FileUtil.isExistFile(FileUtil.getExternalStorageDir().concat(path))) {
                    FileUtil.makeDir(FileUtil.getExternalStorageDir().concat(path));
                }
                int downloadIdOne = PRDownloader.download(url, FileUtil.getExternalStorageDir().concat(path), String.valueOf(textInputEditText.getText()))
                        .build()
                        .setOnStartOrResumeListener(() -> { })
                        .setOnPauseListener(() -> { })
                        .setOnCancelListener(() -> { })
                        .setOnProgressListener(progress -> {
                            long progressPercent = progress.currentBytes * 100 / progress.totalBytes;
                            button2.setText(R.string.下载中); // + progressPercent + "％");
                            progressBar.setIndeterminate(false);
                            progressBar.setProgress((int) progressPercent);
                        })
                        .start(new OnDownloadListener() {
                            @Override
                            public void onDownloadComplete() {
                                progressBar.setVisibility(View.GONE);
                                mDialog.dismiss();
                                try {
                                    ZipUtils.UnZipFolder(FileUtil.getExternalStorageDir().concat(path) + textInputEditText.getText(), FileUtil.getExternalStorageDir().concat("/噬心工具箱/头像素材/"));
                                    List<File> length = listFileSortByModifyTime(FileUtil.getExternalStorageDir().concat("/噬心工具箱/头像素材/"));
                                    if (length.size() == 0){
                                        Download(AvatarMakeActivity.this,"下载素材","未检测到素材文件，请点击下载后才能够正常使用","https://gitee.com/x1602965165/DaiMeng/raw/master/res/%E5%A4%B4%E5%83%8F%E5%88%B6%E4%BD%9C.zip","/噬心工具箱/","头像素材.zip");
                                    } else {
                                        for (File file : length) {
                                            map = new HashMap<>();
                                            map.put("name", file.toString());
                                            listmap.add(map);
                                            TransitionManager.beginDelayedTransition(root, new AutoTransition());
                                            rv.setAdapter(new Recyclerview1Adapter(listmap));
                                            rv.getAdapter().notifyDataSetChanged();
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onError(Error error) {
                            }

                        });
            }
        });
        WindowManager.LayoutParams layoutParams = mDialog.getWindow().getAttributes();
        layoutParams.width = context.getResources().getDisplayMetrics().widthPixels / 10 * 9;
        mDialog.getWindow().setAttributes(layoutParams);
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
            LoadingDialog(AvatarMakeActivity.this);
            new Thread(() -> {
                try {
                    relativeLayout.setDrawingCacheEnabled(true);
                    relativeLayout.buildDrawingCache(true);
                    @SuppressLint("SimpleDateFormat")
                    String savedFile = SaveImage(AvatarMakeActivity.this, relativeLayout.getDrawingCache(), "/噬心工具箱/头像制作/", "Image-" + new SimpleDateFormat("HH-mm-ss").format(new Date()) + ".png");
                    if (savedFile != null) {
                        MediaScannerConnection.scanFile(AvatarMakeActivity.this, new String[]{savedFile}, null, (MediaScannerConnection.OnScanCompletedListener) (str, uri) -> {
                            Intent intent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
                            intent.setData(uri);
                            AvatarMakeActivity.this.sendBroadcast(intent);
                            loadDialog.dismiss();
                            Alerter.create(AvatarMakeActivity.this)
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
                layout.setVisibility(View.VISIBLE);
                bitmap = FileUtil.decodeSampleBitmapFromPath(_filePath.get(0), 1024, 1024);
                imageview1.setImageBitmap(bitmap);
            }
        }
        if (_requestCode == REQ_CD_IMAGE1 && _resultCode == Activity.RESULT_OK) {
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
                layout.setVisibility(View.VISIBLE);
                imageview2.setImageBitmap(FileUtil.decodeSampleBitmapFromPath(_filePath.get(0), 1024, 1024));
            }
        }
    }
}