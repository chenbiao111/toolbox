package com.shixin.app;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.gyf.immersionbar.ImmersionBar;
import com.just.agentweb.AgentWeb;
import com.kongzue.baseokhttp.HttpRequest;
import com.kongzue.baseokhttp.listener.ResponseListener;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.shixin.app.utils.Utils.LoadingDialog;
import static com.shixin.app.utils.Utils.loadDialog;

public class VipVideoActivity extends AppCompatActivity {

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
        setContentView(R.layout.activity_vip_video);
        ButterKnife.bind(this);

        ImmersionBar.with(this)
                .fitsSystemWindows(true)
                .statusBarColor(R.color.appbarColor)
                .navigationBarColor(R.color.backgroundColor)
                .autoDarkModeEnable(true)
                .init();

        toolbar.setTitle(getString(R.string.VIP影视解析));
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

        button1.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.putExtra("网址", "https://www.iqiyi.com/");
            intent.setClass(VipVideoActivity.this, VipVideoBrowserActivity.class);
            startActivity(intent);
        });

        button2.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.putExtra("网址", "https://v.qq.com/");
            intent.setClass(VipVideoActivity.this, VipVideoBrowserActivity.class);
            startActivity(intent);
        });

        button3.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.putExtra("网址", "https://www.youku.com/");
            intent.setClass(VipVideoActivity.this, VipVideoBrowserActivity.class);
            startActivity(intent);
        });

        button4.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.putExtra("网址", "https://www.mgtv.com/");
            intent.setClass(VipVideoActivity.this, VipVideoBrowserActivity.class);
            startActivity(intent);
        });

        fab.setOnClickListener(view -> {
            if (TextUtils.isEmpty(textInputEditText.getText())){
                textInputLayout.setError(getString(R.string.请输入影视链接));
                textInputLayout.setErrorEnabled(true);
            }else {
                LoadingDialog(VipVideoActivity.this);
                HttpRequest.build(VipVideoActivity.this,"https://gitee.com/alex12075/ToolsBox/raw/master/config.json")
                        .addHeaders("Charset","UTF-8")
                        .addHeaders("User-Agent", WebSettings.getDefaultUserAgent(VipVideoActivity.this))
                        .setResponseListener(new ResponseListener() {
                            @Override
                            public void onResponse(String response, Exception error) {
                                try {
                                    loadDialog.dismiss();
                                    HashMap<String, Object> map = new Gson().fromJson(response, new TypeToken<HashMap<String, Object>>(){}.getType());
                                    TransitionManager.beginDelayedTransition(root, new AutoTransition());
                                    web.setVisibility(View.VISIBLE);
                                    mAgentWeb.getWebCreator().getWebView().loadUrl(map.get("解析地址") + String.valueOf(textInputEditText.getText()));
                                } catch (Exception e){
                                }
                            }
                        }).doGet();

            }
        });


    }
}