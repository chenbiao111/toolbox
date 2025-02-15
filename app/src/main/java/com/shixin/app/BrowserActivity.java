package com.shixin.app;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.gyf.immersionbar.ImmersionBar;
import com.just.agentweb.AgentWeb;
import com.just.agentweb.WebChromeClient;
import com.tapadoo.alerter.Alerter;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BrowserActivity extends AppCompatActivity {

    @BindView(R.id.root)
    LinearLayout root;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private AgentWeb mAgentWeb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browser);
        ButterKnife.bind(this);

        ImmersionBar.with(this)
                .fitsSystemWindows(true)
                .statusBarColor(R.color.appbarColor)
                .navigationBarColor(R.color.backgroundColor)
                .autoDarkModeEnable(true)
                .init();

        toolbar.setTitle(getString(R.string.app_name));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish());

        mAgentWeb = AgentWeb.with(this)
                .setAgentWebParent(root, new LinearLayout.LayoutParams(-1, -1))
                .useDefaultIndicator(Color.parseColor("#5187f4"))
                .setWebChromeClient(mWebChromeClient)
                .createAgentWeb()
                .ready()
                .go(getIntent().getStringExtra("网址"));
        mAgentWeb.getWebCreator().getWebView().setOverScrollMode(WebView.OVER_SCROLL_NEVER);
        mAgentWeb.getWebCreator().getWebView().getSettings().setUserAgentString(WebSettings.getDefaultUserAgent(BrowserActivity.this));

    }

    private WebChromeClient mWebChromeClient = new WebChromeClient() {
        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
            toolbar.setTitle(title);
        }
    };

    @Override
    public void onBackPressed(){
        if (!mAgentWeb.back()){
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        menu.add(0, 0, 0, R.string.刷新当前网页).setIcon(R.drawable.ic_twotone_refresh_24).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menu.add(0, 1, 0, R.string.复制网址);
        menu.add(0, 2, 0, R.string.在浏览器打开);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        final int _id = item.getItemId();
        final String _title = (String) item.getTitle();
        if (_id == 0) {
            mAgentWeb.getWebCreator().getWebView().reload();
        }
        if (_id == 1) {
            ((ClipboardManager) getSystemService(CLIPBOARD_SERVICE)).setPrimaryClip(ClipData.newPlainText("clipboard", mAgentWeb.getWebCreator().getWebView().getUrl()));
            Alerter.create((Activity) BrowserActivity.this)
                    .setTitle(R.string.复制成功)
                    .setText(R.string.链接已成功复制到剪切板)
                    .setBackgroundColorInt(getResources().getColor(R.color.success))
                    .show();
        }
        if (_id == 2) {
            Uri uri = Uri.parse(mAgentWeb.getWebCreator().getWebView().getUrl());
            Intent intent = new Intent();
            intent.setAction("android.intent.action.VIEW");
            intent.setData(uri);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}