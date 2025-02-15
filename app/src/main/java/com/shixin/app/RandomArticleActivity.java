package com.shixin.app;

import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.transition.TransitionManager;

import com.gyf.immersionbar.ImmersionBar;
import com.kongzue.baseokhttp.HttpRequest;
import com.kongzue.baseokhttp.listener.ResponseListener;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.shixin.app.utils.Utils.JieQu;
import static com.shixin.app.utils.Utils.LoadingDialog;
import static com.shixin.app.utils.Utils.isVPNConnected;
import static com.shixin.app.utils.Utils.loadDialog;

public class RandomArticleActivity extends AppCompatActivity {

    @BindView(R.id.root)
    ViewGroup root;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.subtitle)
    TextView subtitle;
    @BindView(R.id.content)
    TextView content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_random_article);
        ButterKnife.bind(this);

        ImmersionBar.with(this)
                .fitsSystemWindows(true)
                .statusBarColor(R.color.appbarColor)
                .navigationBarColor(R.color.backgroundColor)
                .autoDarkModeEnable(true)
                .init();

        toolbar.setTitle(getString(R.string.随机一文));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        if (!isVPNConnected(RandomArticleActivity.this)) {
            LoadingDialog(RandomArticleActivity.this);
            HttpRequest.build(RandomArticleActivity.this, "https://meiriyiwen.com/")
                    .addHeaders("Charset", "UTF-8")
                    .addHeaders("User-Agent", WebSettings.getDefaultUserAgent(RandomArticleActivity.this))
                    .setResponseListener(new ResponseListener() {
                        @Override
                        public void onResponse(String response, Exception error) {
                            loadDialog.dismiss();
                            try {
                                TransitionManager.beginDelayedTransition(root, new androidx.transition.AutoTransition());
                                title.setVisibility(View.VISIBLE);
                                subtitle.setVisibility(View.VISIBLE);
                                content.setVisibility(View.VISIBLE);
                                title.setText(JieQu(RandomArticleActivity.this,response,"<h2 class=\"articleTitle\">","</h2>").trim());
                                subtitle.setText(JieQu(RandomArticleActivity.this,response,"<div class=\"articleAuthorName\">","</div>").trim());
                                content.setText(Html.fromHtml(JieQu(RandomArticleActivity.this,response,"<div class=\"articleContent\">","</div>")));
                            } catch (Exception e) { }
                        }
                    }).doGet();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_random_article,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem){
        if (menuItem.getTitle().equals(getString(R.string.刷新))){
            if (!isVPNConnected(RandomArticleActivity.this)) {
                LoadingDialog(RandomArticleActivity.this);
                HttpRequest.build(RandomArticleActivity.this, "https://meiriyiwen.com/")
                        .addHeaders("Charset", "UTF-8")
                        .addHeaders("User-Agent", WebSettings.getDefaultUserAgent(RandomArticleActivity.this))
                        .setResponseListener(new ResponseListener() {
                            @Override
                            public void onResponse(String response, Exception error) {
                                loadDialog.dismiss();
                                try {
                                    TransitionManager.beginDelayedTransition(root, new androidx.transition.AutoTransition());
                                    title.setVisibility(View.VISIBLE);
                                    subtitle.setVisibility(View.VISIBLE);
                                    content.setVisibility(View.VISIBLE);
                                    title.setText(JieQu(RandomArticleActivity.this,response,"<h2 class=\"articleTitle\">","</h2>").trim());
                                    subtitle.setText(JieQu(RandomArticleActivity.this,response,"<div class=\"articleAuthorName\">","</div>").trim());
                                    content.setText(Html.fromHtml(JieQu(RandomArticleActivity.this,response,"<div class=\"articleContent\">","</div>")));
                                } catch (Exception e) { }
                            }
                        }).doGet();
            }
        }
        return super.onOptionsItemSelected(menuItem);
    }
}