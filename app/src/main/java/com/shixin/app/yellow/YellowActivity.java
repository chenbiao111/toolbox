package com.shixin.app.yellow;

import static com.shixin.app.utils.Utils.CopyDialog;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.TransitionManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.gyf.immersionbar.ImmersionBar;
import com.kongzue.baseokhttp.HttpRequest;
import com.kongzue.baseokhttp.listener.ResponseListener;
import com.shixin.app.R;
import com.shixin.app.RC4Activity;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;

public class YellowActivity extends AppCompatActivity {

    @BindView(R.id.root)
    ViewGroup root;
    @BindView(R.id.rv2)
    RecyclerView rv2;

    public static String pbbt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yellow);
        ButterKnife.bind(this);

        ImmersionBar.with(this)
                .fitsSystemWindows(true)
                .statusBarColor(R.color.appbarColor)
                .navigationBarColor(R.color.backgroundColor)
                .autoDarkModeEnable(true)
                .navigationBarDarkIcon(true)
                .init();

        rv2.setItemViewCacheSize(9999);

        HttpRequest.build(YellowActivity.this, "https://gitee.com/x1602965165/DaiMeng/raw/master/yellow")
                .addHeaders("Charset", "UTF-8")
                .setResponseListener(new ResponseListener() {
                    @Override
                    public void onResponse(String response, Exception error) {
                        try {
                            HashMap<String, Object> map = new Gson().fromJson(RC4Activity.RC4Util.decryRC4(response, "xiao","GBK"), new TypeToken<HashMap<String, Object>>() {}.getType());
                            ArrayList<HashMap<String, Object>> listmap1 = new Gson().fromJson(new Gson().toJson(map.get("视频直播APP")), new TypeToken<ArrayList<HashMap<String, Object>>>() {}.getType());

                            map = new Gson().fromJson(new Gson().toJson(map.get("data")), new TypeToken<HashMap<String, Object>>() {}.getType());
                            pbbt = String.valueOf(map.get("直播广告屏蔽"));

                            if (!String.valueOf(map.get("程序公告")).equals("")) {
                                final AlertDialog mDialog = new MaterialAlertDialogBuilder(YellowActivity.this)
                                        .create();
                                mDialog.setTitle("温馨提示");
                                mDialog.setMessage(String.valueOf(map.get("程序公告")));
                                mDialog.setCancelable(false);
                                View contentView = View.inflate(YellowActivity.this, R.layout.dialog_text,null);
                                mDialog.setView(contentView);
                                MaterialButton b1 = contentView.findViewById(R.id.button1);
                                MaterialButton b2 = contentView.findViewById(R.id.button2);

                                b1.setText("取消");
                                b1.setBackgroundColor(getResources().getColor(R.color.itemBackColor));
                                b2.setText("确定");
                                b2.setBackgroundColor(getResources().getColor(R.color.zts));
                                b1.setOnClickListener(v1 -> {
                                    mDialog.dismiss();
                                });
                                b2.setOnClickListener(v1 -> {
                                    mDialog.dismiss();
                                });
                                mDialog.show();
                                WindowManager.LayoutParams layoutParams = mDialog.getWindow().getAttributes();
                                layoutParams.width = getResources().getDisplayMetrics().widthPixels / 10 * 9;
                                mDialog.getWindow().setAttributes(layoutParams);
                            }

                            TransitionManager.beginDelayedTransition(root, new androidx.transition.AutoTransition());
                            rv2.setAdapter(new Recyclerview2Adapter(listmap1));
                            rv2.getAdapter().notifyDataSetChanged();
                        } catch (Exception e) {
                            CopyDialog(YellowActivity.this,"",e.toString());
                        }
                    }
                }).doGet();


    }

    public class Recyclerview2Adapter extends RecyclerView.Adapter<Recyclerview2Adapter.ViewHolder> {
        ArrayList<HashMap<String, Object>> _data;
        public Recyclerview2Adapter(ArrayList<HashMap<String, Object>> _arr) {
            _data = _arr;
        }

        @Override
        public @NotNull
        ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater _inflater = (LayoutInflater) parent.getContext().getSystemService(LAYOUT_INFLATER_SERVICE);
            View _v = _inflater.inflate(R.layout.item_y1, null);
            RecyclerView.LayoutParams _lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            _v.setLayoutParams(_lp);
            return new ViewHolder(_v);
        }

        @SuppressLint("SimpleDateFormat")
        @Override
        public void onBindViewHolder(ViewHolder _holder, final int _position) {
            View _view = _holder.itemView;

            //final MaterialCardView cardview1 = _view.findViewById(R.id.cardview1);
            final TextView txt1 = _view.findViewById(R.id.textview1);
            final TextView txt2 = _view.findViewById(R.id.textview2);
            final TextView txt3 = _view.findViewById(R.id.textview3);
            final ImageView tp1 = _view.findViewById(R.id.imageview1);
            final Button button1 = _view.findViewById(R.id.button1);

            Random random = new Random();
            int s = random.nextInt(10000)%(10000 - 1000 + 1) + 1000;
            txt3.setText("在线：" + s);
            txt1.setText((CharSequence) _data.get(_position).get("标题"));
            txt2.setText((CharSequence) _data.get(_position).get("信息"));
            Glide.with(YellowActivity.this).load(_data.get(_position).get("图片")).thumbnail(0.1f).fitCenter().priority(Priority.IMMEDIATE).into(tp1);

            button1.setOnClickListener(v -> {
                try {
                    if (String.valueOf(_data.get(_position).get("功能")).equals("0")){
                        Intent intent = new Intent();
                        intent.putExtra("url", (String) _data.get((int)_position).get("接口"));
                        intent.putExtra("title", (String) _data.get((int)_position).get("标题"));
                        intent.setClass(YellowActivity.this, VideoActivity.class);
                        startActivity(intent);
                    }
                    if (String.valueOf(_data.get(_position).get("功能")).equals("1")){
                        Intent intent = new Intent();
                        intent.putExtra("url", (String) _data.get((int)_position).get("接口"));
                        intent.putExtra("title", (String) _data.get((int)_position).get("标题"));
                        intent.setClass(YellowActivity.this, ZhiboActivity.class);
                        startActivity(intent);
                    }
                    if (String.valueOf(_data.get(_position).get("功能")).equals("2")){
                        Uri uri = Uri.parse((String) _data.get((int)_position).get("接口"));
                        Intent intent = new Intent();
                        intent.setAction("android.intent.action.VIEW");
                        intent.setData(uri);
                        startActivity(intent);
                    }
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

}