package com.shixin.app;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
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
import androidx.transition.AutoTransition;
import androidx.transition.TransitionManager;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.gyf.immersionbar.ImmersionBar;
import com.kongzue.baseokhttp.HttpRequest;
import com.kongzue.baseokhttp.listener.ResponseListener;
import com.tapadoo.alerter.Alerter;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.glide.transformations.BlurTransformation;

import static com.shixin.app.utils.Utils.JieQu;
import static com.shixin.app.utils.Utils.LoadingDialog;
import static com.shixin.app.utils.Utils.isVPNConnected;
import static com.shixin.app.utils.Utils.loadDialog;

public class VideoDetailsActivity extends AppCompatActivity {

    @BindView(R.id.root)
    ViewGroup root;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.imageview)
    ImageView imageView;
    @BindView(R.id.rv)
    RecyclerView rv;
    @BindView(R.id.toolbar_layout)
    CollapsingToolbarLayout collapsingToolbarLayout;
    @BindView(R.id.tabs)
    TabLayout tabs;

    private ArrayList<String> list = new ArrayList<>();
    private HashMap<String, Object> map = new HashMap<>();
    private ArrayList<HashMap<String, Object>> listmap = new ArrayList<>();
    private ArrayList<String> list1 = new ArrayList<>();
    private HashMap<String, Object> map1 = new HashMap<>();
    private ArrayList<HashMap<String, Object>> listmap1 = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_details);
        ButterKnife.bind(this);

        ImmersionBar.with(this)
                .titleBar(toolbar)
                //.statusBarColor(R.color.appbarColor)
                .navigationBarColor(R.color.backgroundColor)
                .autoDarkModeEnable(true)
                .statusBarDarkFont(false)
                .init();

        toolbar.setTitle(getIntent().getStringExtra("name"));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        toolbar.getOverflowIcon().setColorFilter(Color.parseColor("#ffffff"), PorterDuff.Mode.SRC_IN);
        toolbar.getNavigationIcon().setColorFilter(Color.parseColor("#ffffff"), PorterDuff.Mode.SRC_IN);

        rv.setItemViewCacheSize(9999);
        collapsingToolbarLayout.setExpandedTitleColor(Color.parseColor("#FFFFFF"));
        collapsingToolbarLayout.setCollapsedTitleTextColor(Color.parseColor("#FFFFFF"));

        tabs.addTab(tabs.newTab().setText("超级网盘"));
        tabs.addTab(tabs.newTab().setText("秒播"));

        TransitionManager.beginDelayedTransition(root, new androidx.transition.AutoTransition());
        Glide.with(this)
                .asBitmap()
                .load(getIntent().getStringExtra("img"))
                .apply(com.bumptech.glide.request.RequestOptions.bitmapTransform(new BlurTransformation(25)))
                .into(imageView);

        LoadingDialog(VideoDetailsActivity.this);
        if (!isVPNConnected(VideoDetailsActivity.this)) {
            HttpRequest.build(VideoDetailsActivity.this,getIntent().getStringExtra("url"))
                    .addHeaders("Charset", "UTF-8")
                    .addHeaders("User-Agent", WebSettings.getDefaultUserAgent(VideoDetailsActivity.this))
                    .skipSSLCheck()
                    .setResponseListener(new ResponseListener() {
                        @Override
                        public void onResponse(String response, Exception error) {
                            if(error == null) {
                                loadDialog.dismiss();
                                try {
                                    map.clear();
                                    list.clear();
                                    listmap.clear();
                                    list = new ArrayList<>(Arrays.asList(JieQu(VideoDetailsActivity.this, JieQu(VideoDetailsActivity.this, response, "超级网盘 ", "猜你喜欢"), "<li>", "</ul>").split("<li>")));
                                    for(int i = 0; i < (int)(list.size()); i++) {
                                        map = new HashMap<>();
                                        map.put("name", JieQu(VideoDetailsActivity.this, list.get(i), "html\">", "</a>"));
                                        map.put("url", "https://www.chok8.com" + JieQu(VideoDetailsActivity.this, list.get(i), "<a href=\"", "\""));
                                        listmap.add(map);
                                    }
                                    map1.clear();
                                    list1.clear();
                                    listmap1.clear();
                                    list1 = new ArrayList<>(Arrays.asList(JieQu(VideoDetailsActivity.this, JieQu(VideoDetailsActivity.this, response, "秒播 </h3>", "</ul>") + "</ul>", "<li>", "</ul>").split("<li>")));
                                    for(int i1 = 0; i1 < (int)(list1.size()); i1++) {
                                        map1 = new HashMap<>();
                                        map1.put("name", JieQu(VideoDetailsActivity.this, list1.get(i1), "html\">", "</a>"));
                                        map1.put("url", "https://www.chok8.com" + JieQu(VideoDetailsActivity.this, list1.get(i1), "<a href=\"", "\""));
                                        listmap1.add(map1);
                                    }
                                    TransitionManager.beginDelayedTransition(rv, new AutoTransition());
                                    rv.setAdapter(new Recyclerview1Adapter(listmap));
                                    rv.getAdapter().notifyDataSetChanged();
                                } catch (Exception e){
                                }
                            } else {
                                loadDialog.dismiss();
                                Alerter.create((Activity) VideoDetailsActivity.this)
                                        .setTitle(R.string.温馨提示)
                                        .setText(R.string.加载失败)
                                        .setBackgroundColorInt(getResources().getColor(R.color.error))
                                        .show();
                            }

                        }
                    }).doGet();
        }

        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    TransitionManager.beginDelayedTransition(rv, new androidx.transition.AutoTransition());
                    rv.setAdapter(new Recyclerview1Adapter(listmap));
                    rv.getAdapter().notifyDataSetChanged();
                }
                if (tab.getPosition() == 1) {
                    TransitionManager.beginDelayedTransition(rv, new androidx.transition.AutoTransition());
                    rv.setAdapter(new Recyclerview1Adapter(listmap1));
                    rv.getAdapter().notifyDataSetChanged();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

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
            View _v = _inflater.inflate(R.layout.item_button, null);
            RecyclerView.LayoutParams _lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            _v.setLayoutParams(_lp);
            return new ViewHolder(_v);
        }

        @Override
        public void onBindViewHolder(ViewHolder _holder, @SuppressLint("RecyclerView") final int _position) {
            View _view = _holder.itemView;

            final MaterialButton button = _view.findViewById(R.id.button1);

            button.setText((CharSequence) _data.get(_position).get("name"));
            button.setOnClickListener(v -> {
                if (!isVPNConnected(VideoDetailsActivity.this)) {
                    LoadingDialog(VideoDetailsActivity.this);
                    HttpRequest.build(VideoDetailsActivity.this, (String) _data.get(_position).get("url"))
                            .addHeaders("Charset", "UTF-8")
                            .addHeaders("User-Agent", WebSettings.getDefaultUserAgent(VideoDetailsActivity.this))
                            .setResponseListener(new ResponseListener() {
                                @Override
                                public void onResponse(String response, Exception error) {
                                    if(error == null) {
                                        loadDialog.dismiss();
                                        try {
                                            HashMap<String, Object> url = new Gson().fromJson(JieQu(VideoDetailsActivity.this, response, "<script type=\"text/javascript\">var player_aaaa=", "</script>"), new TypeToken<HashMap<String, Object>>() {
                                            }.getType());
                                            //CopyDialog(VideoDetailsActivity.this, "播放地址", (String) url.get("url"));
                                            if (String.valueOf(url.get("url")).endsWith(".m3u8") || String.valueOf(url.get("url")).endsWith(".mp4")) {
                                                final AlertDialog mDialog = new MaterialAlertDialogBuilder(VideoDetailsActivity.this)
                                                        .setPositiveButton(R.string.内置播放, (dialog, which) -> {
                                                            Intent intent = new Intent();
                                                            intent.putExtra("url", String.valueOf(url.get("url")));
                                                            intent.putExtra("title", getIntent().getStringExtra("name") + "-" + (String) _data.get((int) _position).get("name"));
                                                            intent.putExtra("islive", false);
                                                            intent.setClass(VideoDetailsActivity.this, PlayerActivity.class);
                                                            startActivity(intent);
                                                        })
                                                        .setNegativeButton(R.string.浏览器, (dialog, which) -> {
                                                            Intent intent = new Intent();
                                                            intent.putExtra("网址", String.valueOf(url.get("url")));
                                                            intent.setClass(VideoDetailsActivity.this, BrowserActivity.class);
                                                            startActivity(intent);
                                                        })
                                                        .setNeutralButton(R.string.复制, (dialog, which) -> {
                                                            ((ClipboardManager) getSystemService(CLIPBOARD_SERVICE)).setPrimaryClip(ClipData.newPlainText("clipboard", String.valueOf(url.get("url"))));
                                                            Alerter.create((Activity) VideoDetailsActivity.this)
                                                                    .setTitle(R.string.复制成功)
                                                                    .setText(R.string.链接已成功复制到剪切板)
                                                                    .setBackgroundColorInt(getResources().getColor(R.color.success))
                                                                    .show();
                                                        })
                                                        .create();
                                                mDialog.setTitle(getString(R.string.播放直链));
                                                mDialog.setMessage(String.valueOf(url.get("url")));
                                                mDialog.show();
                                                WindowManager.LayoutParams layoutParams = mDialog.getWindow().getAttributes();
                                                layoutParams.width = getResources().getDisplayMetrics().widthPixels / 10 * 9;
                                                mDialog.getWindow().setAttributes(layoutParams);
                                            } else {
                                                Intent intent = new Intent();
                                                intent.putExtra("网址", String.valueOf(url.get("url")));
                                                intent.setClass(VideoDetailsActivity.this, VipVideoBrowserActivity.class);
                                                startActivity(intent);
                                            }

                                        } catch (Exception e) {
                                        }
                                    } else {
                                        loadDialog.dismiss();
                                    }

                                }
                            }).doGet();
                }
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
}