package com.shixin.app;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.transition.TransitionManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.gyf.immersionbar.ImmersionBar;
import com.kongzue.baseokhttp.HttpRequest;
import com.kongzue.baseokhttp.listener.ResponseListener;
import com.tapadoo.alerter.Alerter;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.shixin.app.utils.Utils.LoadingDialog;
import static com.shixin.app.utils.Utils.SaveImage;
import static com.shixin.app.utils.Utils.isVPNConnected;
import static com.shixin.app.utils.Utils.loadDialog;

public class GifmakerPictureActivity extends AppCompatActivity {

    @BindView(R.id.root)
    ViewGroup root;
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


    private HashMap<String, Object> map = new HashMap<>();
    private ArrayList<HashMap<String, Object>> listmap = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gifmaker_picture);
        ButterKnife.bind(this);

        ImmersionBar.with(this)
                .fitsSystemWindows(true)
                .statusBarColor(R.color.appbarColor)
                .navigationBarColor(R.color.backgroundColor)
                .autoDarkModeEnable(true)
                .init();

        toolbar.setTitle(getString(R.string.快手图集下载));
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
                textInputLayout.setError(getString(R.string.请输入图集链接));
                textInputLayout.setErrorEnabled(true);
            }else {
                if (!isVPNConnected(GifmakerPictureActivity.this)) {
                    LoadingDialog(GifmakerPictureActivity.this);
                    HttpRequest.build(GifmakerPictureActivity.this, getUrl(String.valueOf(textInputEditText.getText())))
                            .addHeaders("Charset", "UTF-8")
                            .addHeaders("User-Agent", WebSettings.getDefaultUserAgent(GifmakerPictureActivity.this))
                            .setResponseListener(new ResponseListener() {
                                @Override
                                public void onResponse(String response, Exception error) {
                                    loadDialog.dismiss();
                                    try {
                                        map.clear();
                                        listmap.clear();
                                        ArrayList<String> list = (ArrayList<String>) getImagePath(response);
                                        for(int i1 = 0; i1 < (int)(list.size()); i1++) {
                                            map = new HashMap<>();
                                            map.put("img", "http:" + list.get(i1));
                                            listmap.add(map);
                                        }
                                        TransitionManager.beginDelayedTransition(root, new androidx.transition.AutoTransition());
                                        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
                                        layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
                                        rv.setLayoutManager(layoutManager);
                                        rv.setAdapter(new Recyclerview1Adapter(listmap));
                                        rv.getAdapter().notifyDataSetChanged();
                                    } catch (Exception e) { }
                                }
                            }).doGet();
                }
            }
        });


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
            return new Recyclerview1Adapter.ViewHolder(_v);
        }

        @Override
        public void onBindViewHolder(ViewHolder _holder, final int _position) {
            View _view = _holder.itemView;

            final MaterialCardView cardview1 = (MaterialCardView) _view.findViewById(R.id.cardview1);
            final ImageView tp1 = (ImageView) _view.findViewById(R.id.tp1);

            Glide.with(GifmakerPictureActivity.this).load(_data.get(_position).get("img")).thumbnail(0.1f).fitCenter().priority(Priority.IMMEDIATE).into(tp1);
            cardview1.setOnClickListener(_view1 -> {
                final AlertDialog mDialog = new MaterialAlertDialogBuilder(GifmakerPictureActivity.this)
                        .create();
                View contentView = View.inflate(GifmakerPictureActivity.this, R.layout.dialog_tp,null);
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
                Glide.with(GifmakerPictureActivity.this).load(_data.get(_position).get("img")).thumbnail(0.1f).fitCenter().priority(Priority.IMMEDIATE).into(imageView);
                button1.setOnClickListener(v1 -> {
                    mDialog.dismiss();
                });
                button2.setOnClickListener(v1 -> {
                    mDialog.dismiss();
                    try {
                        LoadingDialog(GifmakerPictureActivity.this);
                        Glide.with(GifmakerPictureActivity.this)
                                .asBitmap()
                                .load(_data.get((int) _position).get("img"))
                                .into(new SimpleTarget<Bitmap>() {
                                    @Override
                                    public void onResourceReady(@NonNull Bitmap bitmap, @Nullable Transition<? super Bitmap> transition) {
                                        new Thread((Runnable) () -> {
                                            @SuppressLint("SimpleDateFormat")
                                            String savedFile = SaveImage(v1.getContext(), bitmap, "/噬心工具箱/快手图集下载/", "Image-" + new SimpleDateFormat("HH-mm-ss").format(new Date()) + ".png");
                                            if (savedFile != null) {
                                                MediaScannerConnection.scanFile((Activity) v1.getContext(), new String[]{savedFile}, null, (MediaScannerConnection.OnScanCompletedListener) (str, uri) -> {
                                                    Intent intent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
                                                    intent.setData(uri);
                                                    ((Activity) v1.getContext()).sendBroadcast(intent);
                                                    loadDialog.dismiss();
                                                    Alerter.create((Activity) v1.getContext())
                                                            .setTitle(R.string.保存成功)
                                                            .setText(getString(R.string.已保存到) + savedFile)
                                                            .setBackgroundColorInt(getResources().getColor(R.color.success))
                                                            .show();
                                                });
                                            } else {
                                                loadDialog.dismiss();
                                            }
                                        }).start();
                                    }
                                });
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

    public static List<String> getImagePath(String htmlText) {
        List<String> imagePaht = new ArrayList<>();
        Pattern p = Pattern.compile(
                "<img\\b[^>]*\\bsrc\\b\\s*=\\s*('|\")?([^'\"\n\r\f>]+(\\.jpg|\\.bmp|\\.gif|\\.png|\\.jpe|\\.jpeg|\\.pic)\\b)[^>]*>",
                Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(htmlText);
        String quote = null;
        String src = null;
        while (m.find()) {
            quote = m.group(1);
            src = (quote == null || quote.trim().length() == 0) ? m.group(2).split("\\s+")[0] : m.group(2);
            imagePaht.add(src);

        }
        return imagePaht;
    }

    public static String getUrl(String text) {
        Pattern pattern = Pattern.compile("((http[s]{0,1}|ftp)://[a-zA-Z0-9\\.\\-]+\\.([a-zA-Z]{2,4})(:\\d+)?(/[a-zA-Z0-9\\.\\-~!@#$%^&*+?:_/=<>]*)?)|((www.)|[a-zA-Z0-9\\.\\-]+\\.([a-zA-Z]{2,4})(:\\d+)?(/[a-zA-Z0-9\\.\\-~!@#$%^&*+?:_/=<>]*)?)");
        Matcher matcher = pattern.matcher(text);
        StringBuffer buffer = new StringBuffer();
        while (matcher.find()) {
            buffer.append(matcher.group());
        }
        return buffer.toString();
    }
}