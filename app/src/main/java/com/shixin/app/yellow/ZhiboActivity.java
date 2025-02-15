package com.shixin.app.yellow;


import static com.shixin.app.yellow.YellowActivity.pbbt;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.AutoTransition;
import androidx.transition.TransitionManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.google.android.material.card.MaterialCardView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.gyf.immersionbar.ImmersionBar;
import com.kongzue.baseokhttp.HttpRequest;
import com.kongzue.baseokhttp.listener.ResponseListener;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.shixin.app.R;
import com.shixin.app.utils.BackgroundTask;
import com.shixin.app.utils.Utils;

import org.jetbrains.annotations.NotNull;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class ZhiboActivity extends AppCompatActivity {

    private RecyclerView rv;
    private SmartRefreshLayout srl;
    private TimerTask timer;
    private Timer _timer = new Timer();
    private ArrayList<HashMap<String, Object>> listmap = new ArrayList<>();

    private HashMap<String, Object> map = new HashMap<>();
    private ArrayList<HashMap<String, Object>> listmap1 = new ArrayList<>();

    private String json = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zhibo);

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

        Utils.LoadingDialog(ZhiboActivity.this);
        if (!Utils.isVPNConnected(ZhiboActivity.this)) {
            HttpRequest.build(ZhiboActivity.this, getIntent().getStringExtra("url"))
                    .addHeaders("Charset","UTF-8")
                    .setResponseListener(new ResponseListener() {
                        @Override
                        public void onResponse(String response, Exception error) {
                            try {

                                new BackgroundTask(ZhiboActivity.this) {
                                    @Override
                                    public void doInBackground() {
                                        HashMap<String, Object> data = new Gson().fromJson(response, new TypeToken<HashMap<String, Object>>(){}.getType());
                                        json = new Gson().toJson(data);
                                        for (String str : pbbt.split("——")){
                                            json = json.replaceAll(str, "屏蔽");
                                        }

                                        HashMap<String, Object> data1 = new Gson().fromJson(json, new TypeToken<HashMap<String, Object>>(){}.getType());
                                        listmap = new Gson().fromJson(new Gson().toJson(data1.get("zhubo")), new TypeToken<ArrayList<HashMap<String, Object>>>() {}.getType());
                                        for(int i = 0; i < (int)(listmap.size()); i++) {
                                            if (String.valueOf(listmap.get(i).get("title")).contains("屏蔽")) {
                                                //listmap.remove(i);
                                            } else if (String.valueOf(listmap.get(i).get("address")).contains("屏蔽")) {
                                                //listmap.remove(i);
                                            } else {
                                                map = new HashMap<>();
                                                map.put("title", listmap.get(i).get("title"));
                                                map.put("img", listmap.get(i).get("img"));
                                                map.put("address", listmap.get(i).get("address"));
                                                listmap1.add(map);
                                            }
                                        }
                                    }

                                    @Override
                                    public void onPostExecute() {
                                        TransitionManager.beginDelayedTransition(srl, new AutoTransition());
                                        rv.setAdapter(new Recyclerview1Adapter(listmap1));
                                        rv.getAdapter().notifyDataSetChanged();
                                        Utils.loadDialog.dismiss();
                                    }
                                }.execute();
                            } catch (Exception e){
                            }
                        }
                    }).doGet();
        }

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
            RecyclerView.LayoutParams _lp = new RecyclerView.LayoutParams((getResources().getDisplayMetrics().widthPixels - Utils.dp2px(ZhiboActivity.this,20)) / 2 , (getResources().getDisplayMetrics().widthPixels - Utils.dp2px(ZhiboActivity.this,20)) / 2 );
            _v.setLayoutParams(_lp);
            return new ViewHolder(_v);
        }

        @Override
        public void onBindViewHolder(ViewHolder _holder, final int _position) {
            View _view = _holder.itemView;

            final TextView txt1 = _view.findViewById(R.id.textview1);
            final ImageView tp1 = _view.findViewById(R.id.imageview1);
            final TextView txt2 = _view.findViewById(R.id.textview2);
            final MaterialCardView cardView = _view.findViewById(R.id.cardview1);



            Random random = new Random();
            int s = random.nextInt(10000)%(10000 - 1000 + 1) + 1000;
            txt2.setText("人气：" + s);

            txt1.setText((CharSequence) _data.get(_position).get("title"));
            Glide.with(ZhiboActivity.this).load(_data.get(_position).get("img")).placeholder(R.mipmap.ic_launcher).error(R.mipmap.ic_launcher).thumbnail(0.1f).fitCenter().priority(Priority.IMMEDIATE).into(tp1);

            cardView.setOnClickListener(v -> {
                Intent intent = new Intent();
                intent.putExtra("url", String.valueOf(_data.get(_position).get("address")));
                intent.putExtra("img", String.valueOf(_data.get(_position).get("img")));
                intent.putExtra("title", String.valueOf(_data.get(_position).get("title")));
                //intent.putExtra("title", getIntent().getStringExtra("name") + "-" + (String) _data.get((int)_position).get("name"));
                intent.setClass(ZhiboActivity.this, PlayerActivity.class);
                startActivity(intent);
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