package com.shixin.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.AutoTransition;
import androidx.transition.TransitionManager;

import com.google.android.material.button.MaterialButton;
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

import static com.shixin.app.utils.Utils.LoadingDialog;
import static com.shixin.app.utils.Utils.isVPNConnected;
import static com.shixin.app.utils.Utils.loadDialog;

public class LiveChinaActivity extends AppCompatActivity {

    @BindView(R.id.root)
    ViewGroup root;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.rv)
    RecyclerView rv;

    private HashMap<String, Object> map = new HashMap<>();
    private ArrayList<String> list = new ArrayList<>();
    private ArrayList<HashMap<String, Object>> listmap = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_china);
        ButterKnife.bind(this);

        ImmersionBar.with(this)
                .fitsSystemWindows(true)
                .statusBarColor(R.color.appbarColor)
                .navigationBarColor(R.color.backgroundColor)
                .autoDarkModeEnable(true)
                .init();

        toolbar.setTitle(getString(R.string.直播中国));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        rv.setItemViewCacheSize(9999);

        if (!isVPNConnected(LiveChinaActivity.this)) {
            LoadingDialog(LiveChinaActivity.this);
            HttpRequest.build(LiveChinaActivity.this,"https://gitee.com/x1602965165/DaiMeng/raw/master/livechina.txt")
                    .addHeaders("Charset", "UTF-8")
                    .addHeaders("User-Agent", WebSettings.getDefaultUserAgent(LiveChinaActivity.this))
                    .skipSSLCheck()
                    .setResponseListener(new ResponseListener() {
                        @Override
                        public void onResponse(String response, Exception error) {
                            if (error == null) {
                                loadDialog.dismiss();
                                try {
                                    map.clear();
                                    list.clear();
                                    listmap.clear();
                                    list = new ArrayList<String>(Arrays.asList(response.split("\n")));
                                    for (int i1 = 0; i1 < (int) (list.size()); i1++) {
                                        map = new HashMap<>();
                                        map.put("name", list.get(i1).split(",")[0]);
                                        map.put("url", list.get(i1).split(",")[1]);
                                        listmap.add(map);
                                    }
                                    TransitionManager.beginDelayedTransition(root, new AutoTransition());
                                    rv.setAdapter(new Recyclerview1Adapter(listmap));
                                    rv.getAdapter().notifyDataSetChanged();
                                } catch (Exception e){
                                }
                            } else {
                                loadDialog.dismiss();
                                Alerter.create((Activity) LiveChinaActivity.this)
                                        .setTitle(R.string.温馨提示)
                                        .setText(R.string.加载失败)
                                        .setBackgroundColorInt(getResources().getColor(R.color.error))
                                        .show();
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

        @Override
        public @NotNull Recyclerview1Adapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater _inflater = (LayoutInflater) parent.getContext().getSystemService(LAYOUT_INFLATER_SERVICE);
            View _v = _inflater.inflate(R.layout.item_button, null);
            RecyclerView.LayoutParams _lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            _v.setLayoutParams(_lp);
            return new Recyclerview1Adapter.ViewHolder(_v);
        }

        @Override
        public void onBindViewHolder(Recyclerview1Adapter.ViewHolder _holder, final int _position) {
            View _view = _holder.itemView;
            final MaterialButton button1 = _view.findViewById(R.id.button1);
            button1.setText((CharSequence) _data.get((int)_position).get("name"));
            button1.setOnClickListener(v -> {
                Intent intent = new Intent();
                intent.putExtra("url", String.valueOf(_data.get((int)_position).get("url")));
                intent.putExtra("title", String.valueOf(_data.get((int)_position).get("name")));
                intent.putExtra("islive", true);
                intent.setClass(LiveChinaActivity.this, PlayerActivity.class);
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
}