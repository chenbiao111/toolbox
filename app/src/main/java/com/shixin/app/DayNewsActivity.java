package com.shixin.app;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.transition.TransitionManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.gyf.immersionbar.ImmersionBar;
import com.kongzue.baseokhttp.HttpRequest;
import com.kongzue.baseokhttp.listener.ResponseListener;
import com.shixin.app.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.shixin.app.utils.Utils.LoadingDialog;
import static com.shixin.app.utils.Utils.dp2px;
import static com.shixin.app.utils.Utils.loadDialog;

public class DayNewsActivity extends AppCompatActivity {

    @BindView(R.id.root)
    ViewGroup root;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.img)
    ImageView img;
    @BindView(R.id.date)
    TextView date;
    @BindView(R.id.weiyu)
    TextView weiyu;
    @BindView(R.id.news)
    TextView news;
    @BindView(R.id.title)
    TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day_news);
        ButterKnife.bind(this);

        ImmersionBar.with(this)
                .fitsSystemWindows(true)
                .statusBarColor(R.color.appbarColor)
                .navigationBarColor(R.color.backgroundColor)
                .autoDarkModeEnable(true)
                .init();

        toolbar.setTitle(getString(R.string.每日60秒早报));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        if (!Utils.isVPNConnected(this)) {
            LoadingDialog(DayNewsActivity.this);
            HttpRequest.build(this,"http://excerpt.rubaoo.com/toolman/getMiniNews")
                    .addHeaders("Charset","UTF-8")
                    .setResponseListener(new ResponseListener() {
                        @Override
                        public void onResponse(String response, Exception error) {
                            loadDialog.dismiss();
                                try {
                                    HashMap<String, Object> map = new Gson().fromJson(response, new TypeToken<HashMap<String, Object>>(){}.getType());
                                    map = new Gson().fromJson(new Gson().toJson(map.get("data")), new TypeToken<HashMap<String, Object>>() {}.getType());
                                    TransitionManager.beginDelayedTransition(root, new androidx.transition.AutoTransition());
                                    Glide.with(DayNewsActivity.this)
                                            .asBitmap()
                                            .load(map.get("head_image"))
                                            .into(new SimpleTarget<Bitmap>() {
                                                @Override
                                                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                                    TransitionManager.beginDelayedTransition(root, new androidx.transition.AutoTransition());
                                                    img.setImageBitmap(resource);
                                                    img.getLayoutParams().height = dp2px(DayNewsActivity.this,100);
                                                }
                                            });
                                    title.setText(getString(R.string.每天60秒读懂世界));
                                    date.setText((CharSequence) map.get("date"));
                                    weiyu.setText((CharSequence) map.get("weiyu"));
                                    ArrayList<String> list = new Gson().fromJson(new Gson().toJson(map.get("news")), new TypeToken<ArrayList<String>>() {}.getType());
                                    String[] data = list.toArray(new String[list.size()]);
                                    StringBuffer sb = new StringBuffer();
                                    for (String a: data){
                                        sb.append(a).append("\n\n");
                                    }
                                    news.setText(sb.toString());
                                } catch (Exception e){
                                }
                        }
                    }).doGet();
        }

    }
}