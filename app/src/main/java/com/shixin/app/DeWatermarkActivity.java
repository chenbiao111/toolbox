package com.shixin.app;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.transition.AutoTransition;
import androidx.transition.TransitionManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.gyf.immersionbar.ImmersionBar;
import com.just.agentweb.AgentWeb;
import com.kongzue.baseokhttp.HttpRequest;
import com.kongzue.baseokhttp.listener.ResponseListener;
import com.tapadoo.alerter.Alerter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.shixin.app.utils.UrlUtil.getUrl;
import static com.shixin.app.utils.Utils.Download;
import static com.shixin.app.utils.Utils.LoadingDialog;
import static com.shixin.app.utils.Utils.SaveImage;
import static com.shixin.app.utils.Utils.loadDialog;

public class DeWatermarkActivity extends AppCompatActivity {

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
    @BindView(R.id.linear1)
    LinearLayout linear1;
    @BindView(R.id.linear2)
    LinearLayout linear2;
    @BindView(R.id.button1)
    MaterialButton button1;
    @BindView(R.id.button2)
    MaterialButton button2;

    @BindView(R.id.button3)
    MaterialButton button3;
    @BindView(R.id.button4)
    MaterialButton button4;

    @BindView(R.id.web)
    MaterialCardView web;
    private AgentWeb mAgentWeb;

    private HashMap<String, Object> map = new HashMap<>();

    @SuppressLint("SimpleDateFormat")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_de_watermark);
        ButterKnife.bind(this);

        ImmersionBar.with(this)
                .fitsSystemWindows(true)
                .statusBarColor(R.color.appbarColor)
                .navigationBarColor(R.color.backgroundColor)
                .autoDarkModeEnable(true)
                .init();

        toolbar.setTitle(getString(R.string.短视频解析));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        mAgentWeb = AgentWeb.with(this)
                .setAgentWebParent(web, new LinearLayout.LayoutParams(-1, -1))
                .useDefaultIndicator(Color.parseColor("#5187f4"))
                //.setWebChromeClient(mWebChromeClient)
                .createAgentWeb()
                .ready()
                .go(null);
        mAgentWeb.getWebCreator().getWebView().setOverScrollMode(WebView.OVER_SCROLL_NEVER);


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
                textInputLayout.setError(getString(R.string.请输入短视频链接));
                textInputLayout.setErrorEnabled(true);
            }else {
                LoadingDialog(DeWatermarkActivity.this);
                HttpRequest.build(DeWatermarkActivity.this, "https://analyse.layzz.cn//lyz/miniAnalyse")
                        .addHeaders("Accept-Charset", "utf8")
                        .addHeaders("Content-Type", "application/json")
                        .addHeaders("User-Agent", WebSettings.getDefaultUserAgent(DeWatermarkActivity.this))
                        .addHeaders("Host", "analyse.layzz.cn")
                        .setJsonParameter("{\"code\":\"3c7658e1376b02f08ddeb30068ece76e\",\"programType\":94,\"link\":\"" + getUrl(String.valueOf(textInputEditText.getText())) + "\",\"nickName\":\"用户\",\"avatarUrl\":\"https://ss3.bdstatic.com/70cFv8Sh_Q1YnxGkpoWK1HF6hhy/it/u=2401491928,907118315&fm=27&gp=0.jpg\",\"version\":1}")
                        .setResponseListener(new ResponseListener() {
                            @Override
                            public void onResponse(String response, Exception error) {
                                loadDialog.dismiss();
                                try {
                                    //CopyDialog(DeWatermarkActivity.this,"",response);
                                    map = new Gson().fromJson(response, new TypeToken<HashMap<String, Object>>(){}.getType());
                                    map = new Gson().fromJson(new Gson().toJson(map.get("data")), new TypeToken<HashMap<String, Object>>(){}.getType());
                                    TransitionManager.beginDelayedTransition(root, new AutoTransition());
                                    web.setVisibility(View.VISIBLE);
                                    linear1.setVisibility(View.VISIBLE);
                                    linear2.setVisibility(View.VISIBLE);
                                    mAgentWeb.getWebCreator().getWebView().loadUrl(String.valueOf(map.get("playAddr")).trim());
                                } catch (Exception e) {
                                }
                            }
                        }).doPost();
            }
        });

        /*
        copy.setOnClickListener(v -> {
            ((ClipboardManager) v.getContext().getSystemService(CLIPBOARD_SERVICE)).setPrimaryClip(ClipData.newPlainText("clipboard", textView.getText().toString()));
            Alerter.create((Activity) v.getContext())
                    .setTitle(R.string.复制成功)
                    .setText(R.string.已成功将内容复制到剪切板)
                    .setBackgroundColorInt(getResources().getColor(R.color.success))
                    .show();
        });
         */

        button1.setOnClickListener(v -> {
            try {
                ((ClipboardManager) v.getContext().getSystemService(CLIPBOARD_SERVICE)).setPrimaryClip(ClipData.newPlainText("clipboard", String.valueOf(map.get("cover"))));
                Alerter.create((Activity) v.getContext())
                        .setTitle(R.string.复制成功)
                        .setText(R.string.已成功将内容复制到剪切板)
                        .setBackgroundColorInt(getResources().getColor(R.color.success))
                        .show();
            } catch (Exception e) {
            }

        });
        button2.setOnClickListener(v -> {
            try {
                ((ClipboardManager) v.getContext().getSystemService(CLIPBOARD_SERVICE)).setPrimaryClip(ClipData.newPlainText("clipboard", String.valueOf(map.get("playAddr"))));
                Alerter.create((Activity) v.getContext())
                        .setTitle(R.string.复制成功)
                        .setText(R.string.已成功将内容复制到剪切板)
                        .setBackgroundColorInt(getResources().getColor(R.color.success))
                        .show();
            } catch (Exception e) {
            }
        });
        button3.setOnClickListener(v -> {
            try {
                LoadingDialog(DeWatermarkActivity.this);
                Glide.with(DeWatermarkActivity.this)
                        .asBitmap()
                        .load(String.valueOf(map.get("cover")))
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap bitmap, @Nullable Transition<? super Bitmap> transition) {
                                new Thread((Runnable) () -> {
                                    @SuppressLint("SimpleDateFormat")
                                    String savedFile = SaveImage(v.getContext(), bitmap, "/噬心工具箱/短视频去水印/封面/", "Image-" + new SimpleDateFormat("HH-mm-ss").format(new Date()) + ".png");
                                    if (savedFile != null) {
                                        MediaScannerConnection.scanFile((Activity) v.getContext(), new String[]{savedFile}, null, (MediaScannerConnection.OnScanCompletedListener) (str, uri) -> {
                                            Intent intent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
                                            intent.setData(uri);
                                            ((Activity) v.getContext()).sendBroadcast(intent);
                                            loadDialog.dismiss();
                                            Alerter.create((Activity) v.getContext())
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
        button4.setOnClickListener(v -> {
            try {
                Download(DeWatermarkActivity.this, getString(R.string.保存视频), getString(R.string.视频保存路径), String.valueOf(map.get("playAddr")), "/噬心工具箱/短视频去水印/视频/", "Video-" + new SimpleDateFormat("HH-mm-ss").format(new Date())+ ".mp4");
            } catch (Exception e) {
            }
        });

    }
}