package com.shixin.app.yellow;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.AutoTransition;
import androidx.transition.TransitionManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.tabs.TabLayout;
import com.gyf.immersionbar.ImmersionBar;
import com.kongzue.baseokhttp.HttpRequest;
import com.kongzue.baseokhttp.listener.ResponseListener;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.shixin.app.BrowserActivity;
import com.shixin.app.PlayerActivity;
import com.shixin.app.R;
import com.shixin.app.utils.Utils;
import com.tapadoo.alerter.Alerter;

import org.jetbrains.annotations.NotNull;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class VideoActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private RecyclerView rv;
    private SmartRefreshLayout srl;
    private ProgressBar progressBar;
    private TimerTask timer;
    private Timer _timer = new Timer();
    private ArrayList<String> list_fl = new ArrayList<>();
    private ArrayList<String> list = new ArrayList<>();
    private HashMap<String, Object> map = new HashMap<>();
    private ArrayList<HashMap<String, Object>> listmap = new ArrayList<>();
    private String name;
    private String jxurl;
    private ImageView imageView;
    private int ye = 1;
    private int id = 0;

    private ArrayList<HashMap<String, Object>> listmap1 = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ImmersionBar.with(this)
                .fitsSystemWindows(true)
                .statusBarColor(R.color.appbarColor)
                .navigationBarColor(R.color.backgroundColor)
                .autoDarkModeEnable(true)
                .navigationBarDarkIcon(true)
                .init();



        toolbar.setTitle(getIntent().getStringExtra("title"));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        //toolbar.getOverflowIcon().setColorFilter(Color.parseColor("#000000"), PorterDuff.Mode.SRC_IN);
        //toolbar.getNavigationIcon().setColorFilter(Color.parseColor("#000000"), PorterDuff.Mode.SRC_IN);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        rv = findViewById(R.id.rv);
        rv.setItemViewCacheSize(9999);
        srl = findViewById(R.id.srl);
        progressBar = findViewById(R.id.progressbar);

        tabLayout = findViewById(R.id.tabs);

        if (!Utils.isVPNConnected(VideoActivity.this)) {
            HttpRequest.build(VideoActivity.this, getIntent().getStringExtra("url"))
                    .addHeaders("Charset","UTF-8")
                    .setResponseListener(new ResponseListener() {
                        @Override
                        public void onResponse(String response, Exception error) {
                            try {
                                list_fl = new ArrayList<String>(Arrays.asList(Utils.JieQu(VideoActivity.this, response, "<class><ty", "</class>").split("<ty")));
                                for(int i = 0; i < (int)(list_fl.size()); i++) {
                                    tabLayout.addTab(tabLayout.newTab().setText(Utils.JieQu(VideoActivity.this,list_fl.get(i),"\">","</ty>").trim()));
                                }
                            } catch (Exception e){
                            }
                        }
                    }).doGet();
        }

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                try {
                    Utils.LoadingDialog(VideoActivity.this);
                    //Toast.makeText(VideoActivity.this, "http://wmcj8.com/inc/sapi.php?ac=videolist&t=" + Utils.JieQu(VideoActivity.this, list_fl.get(tab.getPosition()), "id=\"", "\"") + "&pg=0",Toast.LENGTH_SHORT).show();
                    if (!Utils.isVPNConnected(VideoActivity.this)) {
                        id = tab.getPosition();
                        ye = 1;
                        HttpRequest.build(VideoActivity.this, getIntent().getStringExtra("url") + Utils.JieQu(VideoActivity.this, list_fl.get(tab.getPosition()), "id=\"", "\"") + "&pg=" + ye)
                                .addHeaders("Charset","UTF-8")
                                .setResponseListener(new ResponseListener() {
                                    @Override
                                    public void onResponse(String response, Exception error) {
                                        Utils.loadDialog.dismiss();
                                        try {
                                            list.clear();
                                            listmap.clear();
                                            list = new ArrayList<String>(Arrays.asList(Utils.JieQu(VideoActivity.this, response, "<video>", "</list>").split("<video>")));
                                            for(int i1 = 0; i1 < (int)(list.size()); i1++) {
                                                map = new HashMap<>();
                                                map.put("name", Utils.JieQu(VideoActivity.this,list.get(i1), "<name><![CDATA[", "]]></name>"));
                                                map.put("img", Utils.JieQu(VideoActivity.this,list.get(i1), "<pic>", "</pic>"));
                                                map.put("url", "http" + Utils.JieQu(VideoActivity.this,Utils.JieQu(VideoActivity.this,list.get(i1), "<dl>", "</dl>"), "http", "m3u8") + "m3u8");
                                                listmap.add(map);
                                            }
                                            TransitionManager.beginDelayedTransition(srl, new AutoTransition());
                                            rv.setAdapter(new Recyclerview1Adapter(listmap));
                                            rv.getAdapter().notifyDataSetChanged();
                                            progressBar.setVisibility(View.GONE);
                                            rv.setVisibility(View.VISIBLE);
                                        } catch (Exception e){
                                        }
                                    }
                                }).doGet();
                    }
                } catch (Exception e) {
                }
            }


            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        srl.setOnLoadMoreListener(refreshLayout -> {
            if (!Utils.isVPNConnected(VideoActivity.this)) {
                ye++;
                //Toast.makeText(VideoActivity.this, "http://wmcj8.com/inc/sapi.php?ac=videolist&t=" + Utils.JieQu(VideoActivity.this, list_fl.get(id), "id=\"", "\"") + "&pg=" + ye,Toast.LENGTH_SHORT).show();
                HttpRequest.build(VideoActivity.this, getIntent().getStringExtra("url") + Utils.JieQu(VideoActivity.this, list_fl.get(id), "id=\"", "\"") + "&pg=" + ye)
                        .addHeaders("Charset", "UTF-8")
                        .setResponseListener(new ResponseListener() {
                            @Override
                            public void onResponse(String response, Exception error) {
                                srl.finishLoadMore(false);
                                try {
                                    list.clear();
                                    list = new ArrayList<String>(Arrays.asList(Utils.JieQu(VideoActivity.this, response, "<video>", "</list>").split("<video>")));
                                    for(int i1 = 0; i1 < (int)(list.size()); i1++) {
                                        map = new HashMap<>();
                                        map.put("name", Utils.JieQu(VideoActivity.this,list.get(i1), "<name><![CDATA[", "]]></name>"));
                                        map.put("img", Utils.JieQu(VideoActivity.this,list.get(i1), "<pic>", "</pic>"));
                                        map.put("url", "http" + Utils.JieQu(VideoActivity.this,Utils.JieQu(VideoActivity.this,list.get(i1), "<dd flag=\"ckplayer\">", "</dd>"), "http", "m3u8") + "m3u8");
                                        listmap1.add(map);
                                    }
                                    TransitionManager.beginDelayedTransition(srl, new AutoTransition());
                                    listmap.addAll(listmap1);
                                    rv.getAdapter().notifyItemRangeChanged(listmap.size() - listmap1.size(), listmap1.size());
                                } catch (Exception e) {
                                }
                            }
                        }).doGet();
            }
        });
    }

    public class Recyclerview1Adapter extends RecyclerView.Adapter<Recyclerview1Adapter.ViewHolder> {
        ArrayList<HashMap<String, Object>> _data;
        public Recyclerview1Adapter(ArrayList<HashMap<String, Object>> _arr) {
            _data = _arr;
        }

        @NotNull
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater _inflater = (LayoutInflater) parent.getContext().getSystemService(LAYOUT_INFLATER_SERVICE);
            View _v = _inflater.inflate(R.layout.item_zhibo, null);
            RecyclerView.LayoutParams _lp = new RecyclerView.LayoutParams((getResources().getDisplayMetrics().widthPixels - Utils.dp2px(VideoActivity.this,20)) / 2, (getResources().getDisplayMetrics().widthPixels - Utils.dp2px(VideoActivity.this,20)) / 3);
            _v.setLayoutParams(_lp);
            return new ViewHolder(_v);
        }

        @Override
        public void onBindViewHolder(ViewHolder _holder, final int _position) {
            View _view = _holder.itemView;

            final TextView txt1 = _view.findViewById(R.id.textview1);
            final TextView txt2 = _view.findViewById(R.id.textview2);
            final ImageView tp1 = _view.findViewById(R.id.imageview1);
            final MaterialCardView cardView = _view.findViewById(R.id.cardview1);
            txt2.setVisibility(View.GONE);

            txt1.setText((CharSequence) _data.get(_position).get("name"));

            Glide.with(VideoActivity.this)
                    .load(String.valueOf(_data.get(_position).get("img")))
                    .placeholder(R.mipmap.ic_launcher)
                    .error(R.mipmap.ic_launcher)
                    .thumbnail(0.1f).fitCenter()
                    .priority(Priority.IMMEDIATE)
                    .into(tp1);


            cardView.setOnClickListener(v -> {
                final AlertDialog mDialog = new MaterialAlertDialogBuilder(VideoActivity.this)
                        .setPositiveButton(R.string.内置播放, (dialog, which) -> {
                            Intent intent = new Intent();
                            intent.putExtra("url", String.valueOf(_data.get((int)_position).get("url")));
                            intent.putExtra("title", String.valueOf(_data.get((int)_position).get("name")));
                            intent.putExtra("islive", false);
                            intent.setClass(VideoActivity.this, PlayerActivity.class);
                            startActivity(intent);
                        })
                        .setNegativeButton(R.string.浏览器, (dialog, which) -> {
                            Intent intent = new Intent();
                            intent.putExtra("网址", String.valueOf(_data.get((int)_position).get("url")));
                            intent.setClass(VideoActivity.this, BrowserActivity.class);
                            startActivity(intent);
                        })
                        .setNeutralButton(R.string.复制, (dialog, which) -> {
                            ((ClipboardManager) getSystemService(CLIPBOARD_SERVICE)).setPrimaryClip(ClipData.newPlainText("clipboard", String.valueOf(_data.get((int)_position).get("url"))));
                            Alerter.create((Activity) VideoActivity.this)
                                    .setTitle(R.string.复制成功)
                                    .setText(R.string.链接已成功复制到剪切板)
                                    .setBackgroundColorInt(getResources().getColor(R.color.success))
                                    .show();
                        })
                        .create();
                mDialog.setTitle(getString(R.string.播放直链));
                mDialog.setMessage(String.valueOf(_data.get((int)_position).get("url")));
                mDialog.show();
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
     * Base64解密字符串
     * @param content -- 待解密字符串
     * @param charsetName -- 字符串编码方式
     * @return
     */
    private String base64Decode(String content, String charsetName) {
        if (TextUtils.isEmpty(charsetName)) {
            charsetName = "UTF-8";
        }
        byte[] contentByte = Base64.decode(content, Base64.DEFAULT);
        try {
            return new String(contentByte, charsetName);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }

}