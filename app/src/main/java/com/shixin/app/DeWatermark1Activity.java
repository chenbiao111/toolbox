package com.shixin.app;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.transition.AutoTransition;
import androidx.transition.TransitionManager;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.gyf.immersionbar.ImmersionBar;
import com.just.agentweb.AgentWeb;
import com.shixin.app.parse.Parser;
import com.shixin.app.parse.ParserFactory;
import com.shixin.app.parse.callback.ParseCallback;
import com.shixin.app.parse.error.ParseError;
import com.shixin.app.utils.UrlUtil;
import com.shixin.app.widget.KWebView;
import com.tapadoo.alerter.Alerter;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.FlowableOnSubscribe;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

import static com.shixin.app.utils.Utils.Download;
import static com.shixin.app.utils.Utils.LoadingDialog;
import static com.shixin.app.utils.Utils.loadDialog;

public class DeWatermark1Activity extends AppCompatActivity {

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
    @BindView(R.id.button1)
    MaterialButton button1;
    @BindView(R.id.button2)
    MaterialButton button2;

    @BindView(R.id.webView)
    KWebView webView;

    @BindView(R.id.web)
    MaterialCardView web;
    private AgentWeb mAgentWeb;

    private HashMap<String, Object> map = new HashMap<>();
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private final ParserFactory parserFactory = new ParserFactory();
    private Parser mParser;
    private String video_url = "";

    @SuppressLint("SimpleDateFormat")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_de_watermark_1);
        ButterKnife.bind(this);

        ImmersionBar.with(this)
                .fitsSystemWindows(true)
                .statusBarColor(R.color.appbarColor)
                .navigationBarColor(R.color.backgroundColor)
                .autoDarkModeEnable(true)
                .init();

        toolbar.setTitle(getString(R.string.抖音快手去水印));
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

        // 网页内容获取回调
        webView.setHtmlCallback((html) -> mParser.parseHtml(html, new ParseCallback() {
            @Override
            public void error(ParseError error) {
                runOnUiThread(() -> {
                    loadDialog.dismiss();
                    Alerter.create(DeWatermark1Activity.this)
                            .setTitle(R.string.解析失败)
                            .setText(error.getMsg())
                            .setBackgroundColorInt(getResources().getColor(R.color.success))
                            .show();
                });
            }

            @Override
            public void success(String url) {
                runOnUiThread(() -> {
                    loadDialog.dismiss();
                    video_url = url;
                    TransitionManager.beginDelayedTransition(root, new AutoTransition());
                    web.setVisibility(View.VISIBLE);
                    linear1.setVisibility(View.VISIBLE);
                    mAgentWeb.getWebCreator().getWebView().loadUrl(video_url);
                });
            }
        }));


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
                textInputLayout.setError(getString(R.string.请输入抖音快手作品链接));
                textInputLayout.setErrorEnabled(true);
            }else {
                LoadingDialog(DeWatermark1Activity.this);
                final String url = UrlUtil.getUrl(String.valueOf(textInputEditText.getText()));
                // 获取解析器
                mParser = parserFactory.getParser(url);
                // 获取重定向地址
                compositeDisposable.clear();
                compositeDisposable.add(Flowable.create((FlowableOnSubscribe<String>) emitter -> {
                    String redirectUrl = UrlUtil.getRedirectUrl(url);
                    emitter.onNext(redirectUrl);
                    emitter.onComplete();
                }, BackpressureStrategy.BUFFER)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe(redirectUrl -> {
                            // 加载网页
                            webView.loadUrl(redirectUrl);
                        }));
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
                ((ClipboardManager) v.getContext().getSystemService(CLIPBOARD_SERVICE)).setPrimaryClip(ClipData.newPlainText("clipboard", video_url));
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
                Download(DeWatermark1Activity.this, getString(R.string.保存视频), getString(R.string.视频保存路径), video_url, "/噬心工具箱/短视频去水印/视频/", "Video-" + new SimpleDateFormat("HH-mm-ss").format(new Date())+ ".mp4");
            } catch (Exception e) {
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }
}