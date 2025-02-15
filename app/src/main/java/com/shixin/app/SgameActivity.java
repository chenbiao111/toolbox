package com.shixin.app;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.TransitionManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.gyf.immersionbar.ImmersionBar;
import com.kongzue.baseokhttp.HttpRequest;
import com.kongzue.baseokhttp.listener.ResponseListener;
import com.tapadoo.alerter.Alerter;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.shixin.app.utils.Utils.LoadingDialog;
import static com.shixin.app.utils.Utils.SaveImage;
import static com.shixin.app.utils.Utils.isVPNConnected;
import static com.shixin.app.utils.Utils.loadDialog;

public class SgameActivity extends AppCompatActivity {

    @BindView(R.id.root)
    ViewGroup root;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private HashMap<String, Object> map = new HashMap<>();
    private ArrayList<HashMap<String, Object>> listmap = new ArrayList<>();
    private String[] skin_name;

    @BindView(R.id.rv)
    RecyclerView rv;
    @BindView(R.id.rv1)
    RecyclerView rv1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sgame);
        ButterKnife.bind(this);

        ImmersionBar.with(this)
                .fitsSystemWindows(true)
                .statusBarColor(R.color.appbarColor)
                .navigationBarColor(R.color.backgroundColor)
                .autoDarkModeEnable(true)
                .init();

        toolbar.setTitle(getString(R.string.王者荣耀图集));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        rv.setItemViewCacheSize(9999);
        rv1.setItemViewCacheSize(9999);

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
                                skin_name = list.get(0).get("skin_name").toString().split("\\|");
                                for (int i = 0; i < skin_name.length; i++) {
                                    int position = i+1;
                                    map = new HashMap<>();
                                    map.put("img", "https://game.gtimg.cn/images/yxzj/img201606/skin/hero-info/" + String.valueOf(list.get(0).get("ename")).replace(".0","") + "/" + String.valueOf(list.get(0).get("ename")).replace(".0","") + "-bigskin-" + position + ".jpg");
                                    map.put("name", skin_name[i]);
                                    listmap.add(map);
                                }
                                TransitionManager.beginDelayedTransition(rv1, new androidx.transition.AutoTransition());
                                rv1.setAdapter(new Recyclerview2Adapter(listmap));
                                rv1.getAdapter().notifyDataSetChanged();
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
            View _v = _inflater.inflate(R.layout.item_wz2, null);
            RecyclerView.LayoutParams _lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            _v.setLayoutParams(_lp);
            return new Recyclerview1Adapter.ViewHolder(_v);
        }

        @Override
        public void onBindViewHolder(ViewHolder _holder, final int _position) {
            View _view = _holder.itemView;

            //final MaterialButton button = _view.findViewById(R.id.button1);
            final MaterialCardView cardview1 = (MaterialCardView) _view.findViewById(R.id.cardview1);
            final ImageView tp1 = (ImageView) _view.findViewById(R.id.tp1);

            Glide.with(SgameActivity.this).load("https://game.gtimg.cn/images/yxzj/img201606/heroimg/" + String.valueOf(_data.get(_position).get("ename")).replace(".0","") + "/" + String.valueOf(_data.get(_position).get("ename")).replace(".0","") +".jpg").thumbnail(0.1f).fitCenter().priority(Priority.IMMEDIATE).into(tp1);
            //button.setText((CharSequence) _data.get(_position).get("cname"));
            cardview1.setOnClickListener(v -> {
                try {
                    skin_name = _data.get(_position).get("skin_name").toString().split("\\|");
                    map.clear();
                    listmap.clear();
                    for (int i = 0; i < skin_name.length; i++) {
                        int position = i+1;
                        map = new HashMap<>();
                        map.put("img", "https://game.gtimg.cn/images/yxzj/img201606/skin/hero-info/" + String.valueOf(_data.get(_position).get("ename")).replace(".0","") + "/" + String.valueOf(_data.get(_position).get("ename")).replace(".0","") + "-bigskin-" + position + ".jpg");
                        map.put("name", skin_name[i]);
                        listmap.add(map);
                    }
                    TransitionManager.beginDelayedTransition(rv1, new androidx.transition.AutoTransition());
                    rv1.setAdapter(new Recyclerview2Adapter(listmap));
                    rv1.getAdapter().notifyDataSetChanged();
                } catch (Exception e) {
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

    public class Recyclerview2Adapter extends RecyclerView.Adapter<Recyclerview2Adapter.ViewHolder> {
        ArrayList<HashMap<String, Object>> _data;
        public Recyclerview2Adapter(ArrayList<HashMap<String, Object>> _arr) {
            _data = _arr;
        }

        @Override
        public @NotNull ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater _inflater = (LayoutInflater) parent.getContext().getSystemService(LAYOUT_INFLATER_SERVICE);
            View _v = _inflater.inflate(R.layout.item_wz1, null);
            RecyclerView.LayoutParams _lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            _v.setLayoutParams(_lp);
            return new Recyclerview2Adapter.ViewHolder(_v);
        }

        @Override
        public void onBindViewHolder(ViewHolder _holder, final int _position) {
            View _view = _holder.itemView;

            final MaterialCardView cardview1 = (MaterialCardView) _view.findViewById(R.id.cardview1);
            final TextView txt1 = (TextView) _view.findViewById(R.id.txt1);
            final ImageView tp1 = (ImageView) _view.findViewById(R.id.tp1);

            txt1.setText((CharSequence) _data.get(_position).get("name"));
            Glide.with(SgameActivity.this).load(_data.get(_position).get("img")).thumbnail(0.1f).fitCenter().priority(Priority.IMMEDIATE).into(tp1);
            cardview1.setOnClickListener(_view1 -> {
                final AlertDialog mDialog = new MaterialAlertDialogBuilder(SgameActivity.this)
                        .create();
                View contentView = View.inflate(SgameActivity.this, R.layout.dialog_tp,null);
                mDialog.setView(contentView);
                mDialog.show();
                final ImageView imageView = contentView.findViewById(R.id.imageView);
                final MaterialButton button1 = contentView.findViewById(R.id.button1);
                final MaterialButton button2 = contentView.findViewById(R.id.button2);
                button1.setText(R.string.取消);
                button1.setBackgroundColor(getResources().getColor(R.color.itemBackColor));
                button1.setTextColor(getResources().getColor(R.color.editTextColor));
                button2.setText(R.string.保存);
                button2.setBackgroundColor(getResources().getColor(R.color.zts));
                button2.setTextColor(getResources().getColor(R.color.white));
                Glide.with(SgameActivity.this).load(_data.get(_position).get("img")).thumbnail(0.1f).fitCenter().priority(Priority.IMMEDIATE).into(imageView);
                button1.setOnClickListener(v1 -> {
                    mDialog.dismiss();
                });
                button2.setOnClickListener(v1 -> {
                    mDialog.dismiss();
                    try {
                        LoadingDialog(SgameActivity.this);
                        Glide.with(SgameActivity.this)
                                .asBitmap()
                                .load(_data.get((int) _position).get("img"))
                                .into(new SimpleTarget<Bitmap>() {
                                    @Override
                                    public void onResourceReady(@NonNull Bitmap bitmap, @Nullable Transition<? super Bitmap> transition) {
                                        new Thread((Runnable) () -> {
                                            @SuppressLint("SimpleDateFormat")
                                            String savedFile = SaveImage(v1.getContext(), bitmap, "/噬心工具箱/王者荣耀图集/", "Image-" + new SimpleDateFormat("HH-mm-ss").format(new Date()) + ".png");
                                            if (savedFile != null) {
                                                MediaScannerConnection.scanFile((Activity) v1.getContext(), new String[]{savedFile}, null, (MediaScannerConnection.OnScanCompletedListener) (str, uri) -> {
                                                    Intent intent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
                                                    intent.setData(uri);
                                                    ((Activity) v1.getContext()).sendBroadcast(intent);
                                                    loadDialog.dismiss();
                                                    Alerter.create((Activity) v1.getContext())
                                                            .setTitle(R.string.保存成功)
                                                            .setText(getString(R.string.已保存到) + savedFile)
                                                            .setBackgroundColorInt(getResources().getColor(R.color.success))
                                                            .show();
                                                });
                                            } else {
                                                loadDialog.dismiss();
                                            }
                                        }).start();
                                    }
                                });
                    } catch (Exception e) {
                    }
                });
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