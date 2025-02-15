package com.shixin.app;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.TransitionManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.gyf.immersionbar.ImmersionBar;
import com.kongzue.baseokhttp.HttpRequest;
import com.kongzue.baseokhttp.listener.ResponseListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.shixin.app.utils.Utils.CopyDialog;
import static com.shixin.app.utils.Utils.LoadingDialog;
import static com.shixin.app.utils.Utils.isVPNConnected;
import static com.shixin.app.utils.Utils.loadDialog;

public class PowerActivity extends AppCompatActivity {

    @BindView(R.id.root)
    ViewGroup root;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.rv)
    RecyclerView rv;
    private HashMap<String, Object> map = new HashMap<>();
    private HashMap<String, Object> map1 = new HashMap<>();
    private ArrayList<HashMap<String, Object>> listmap = new ArrayList<>();
    private ArrayList<String> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_power);
        ButterKnife.bind(this);

        ImmersionBar.with(this)
                .fitsSystemWindows(true)
                .statusBarColor(R.color.appbarColor)
                .navigationBarColor(R.color.backgroundColor)
                .autoDarkModeEnable(true)
                .init();

        toolbar.setTitle(getString(R.string.王者荣耀最低战力地区查询));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        rv.setItemViewCacheSize(9999);

        if (!isVPNConnected(this)) {
            LoadingDialog(this);
            HttpRequest.build(this, "https://pvp.qq.com/web201605/js/herolist.json")
                    .addHeaders("Charset", "UTF-8")
                    .setResponseListener(new ResponseListener() {
                        @Override
                        public void onResponse(String response, Exception error) {
                            loadDialog.dismiss();
                            try {
                                ArrayList<HashMap<String, Object>> list = new Gson().fromJson(response, new TypeToken<ArrayList<HashMap<String, Object>>>() {}.getType());
                                TransitionManager.beginDelayedTransition(rv, new androidx.transition.AutoTransition());
                                rv.setAdapter(new Recyclerview1Adapter(list));
                                rv.getAdapter().notifyDataSetChanged();
                            } catch (Exception e) {
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
        public @NotNull ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater _inflater = (LayoutInflater) parent.getContext().getSystemService(LAYOUT_INFLATER_SERVICE);
            View _v = _inflater.inflate(R.layout.item_wzzl, null);
            RecyclerView.LayoutParams _lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            _v.setLayoutParams(_lp);
            return new ViewHolder(_v);
        }

        @Override
        public void onBindViewHolder(ViewHolder _holder, final int _position) {
            View _view = _holder.itemView;

            final MaterialCardView cardview1 = _view.findViewById(R.id.cardview1);
            final ImageView tp1 = _view.findViewById(R.id.tp1);
            final TextView name = _view.findViewById(R.id.name);
            final TextView title = _view.findViewById(R.id.title);

            Glide.with(PowerActivity.this).load("https://game.gtimg.cn/images/yxzj/img201606/heroimg/" + String.valueOf(_data.get(_position).get("ename")).replace(".0","") + "/" + String.valueOf(_data.get(_position).get("ename")).replace(".0","") +".jpg").thumbnail(0.1f).fitCenter().priority(Priority.IMMEDIATE).into(tp1);
            name.setText(String.valueOf(_data.get(_position).get("cname")));
            title.setText(String.valueOf(_data.get(_position).get("title")));

            cardview1.setOnClickListener(v -> {
                CharSequence[] choices = {"安卓QQ区","安卓微信区"};
                final AlertDialog mDialog = new MaterialAlertDialogBuilder(PowerActivity.this)
                        .setPositiveButton(R.string.确定, (dialog, which) -> {
                            int position = ((AlertDialog)dialog).getListView().getCheckedItemPosition();
                            if (position != AdapterView.INVALID_POSITION) {
                                if (position == 0) {
                                    if (!isVPNConnected(PowerActivity.this)) {
                                        LoadingDialog(PowerActivity.this);
                                        HttpRequest.build(PowerActivity.this, "https://www.sapi.run/hero/select.php?hero=" + _data.get(_position).get("cname") + "&type=qq")
                                                .addHeaders("Charset", "UTF-8")
                                                .setResponseListener(new ResponseListener() {
                                                    @Override
                                                    public void onResponse(String response, Exception error) {
                                                        loadDialog.dismiss();
                                                        try {
                                                            HashMap<String, Object> data = new Gson().fromJson(response, new TypeToken<HashMap<String, Object>>() {}.getType());
                                                            data = new Gson().fromJson(new Gson().toJson(data.get("data")), new TypeToken<HashMap<String, Object>>() {}.getType());
                                                            CopyDialog(PowerActivity.this, String.valueOf(data.get("alias")), "更新时间：" + data.get("updatetime") + "\n\n区标：" + data.get("area") + " (" + data.get("areaPower") + ")" + "\n\n市标：" + data.get("city") + " (" + data.get("cityPower") + ")" + "\n\n省标：" + data.get("province") + " (" + data.get("provincePower") + ")");
                                                        } catch (Exception e) {
                                                        }
                                                    }
                                                }).doGet();
                                    }
                                }
                                if (position == 1) {
                                    if (!isVPNConnected(PowerActivity.this)) {
                                        LoadingDialog(PowerActivity.this);
                                        HttpRequest.build(PowerActivity.this, "https://www.sapi.run/hero/select.php?hero=" + _data.get(_position).get("cname") + "&type=wx")
                                                .addHeaders("Charset", "UTF-8")
                                                .setResponseListener(new ResponseListener() {
                                                    @Override
                                                    public void onResponse(String response, Exception error) {
                                                        loadDialog.dismiss();
                                                        try {
                                                            HashMap<String, Object> data = new Gson().fromJson(response, new TypeToken<HashMap<String, Object>>() {}.getType());
                                                            data = new Gson().fromJson(new Gson().toJson(data.get("data")), new TypeToken<HashMap<String, Object>>() {}.getType());
                                                            CopyDialog(PowerActivity.this, String.valueOf(data.get("alias")), "更新时间：" + data.get("updatetime") + "\n\n区标：" + data.get("area") + " (" + data.get("areaPower") + ")" + "\n\n市标：" + data.get("city") + " (" + data.get("cityPower") + ")" + "\n\n省标：" + data.get("province") + " (" + data.get("provincePower") + ")");
                                                        } catch (Exception e) {
                                                        }
                                                    }
                                                }).doGet();
                                    }
                                }
                            }
                        })
                        .setNegativeButton(R.string.取消,null)
                        .setSingleChoiceItems(choices, 0, null)
                        .create();
                mDialog.setTitle(getString(R.string.选择分区));
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
}