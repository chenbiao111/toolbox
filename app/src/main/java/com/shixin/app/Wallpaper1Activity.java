package com.shixin.app;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

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
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.shixin.app.utils.Utils;
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
import static com.shixin.app.utils.Utils.loadDialog;

public class Wallpaper1Activity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.rv)
    RecyclerView rv;
    @BindView(R.id.srl)
    SmartRefreshLayout srl;
    private HashMap<String, Object> map = new HashMap<>();
    private HashMap<String, Object> map1 = new HashMap<>();
    private ArrayList<HashMap<String, Object>> listmap = new ArrayList<>();
    private ArrayList<HashMap<String, Object>> listmap1 = new ArrayList<>();
    private ArrayList<String> list = new ArrayList<>();
    private int ye = 1;
    private String edit;
    private Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallpaper1);
        ButterKnife.bind(this);

        ImmersionBar.with(this)
                .fitsSystemWindows(true)
                .statusBarColor(R.color.appbarColor)
                .navigationBarColor(R.color.backgroundColor)
                .autoDarkModeEnable(true)
                .init();

        toolbar.setTitle(getIntent().getStringExtra("bt"));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        rv.setItemViewCacheSize(9999);

        if (!Utils.isVPNConnected(this)) {
            Utils.LoadingDialog(this);
            HttpRequest.build(Wallpaper1Activity.this, "http://service.picasso.adesk.com/v1/lightwp/category/" + getIntent().getStringExtra("id") + "/vertical?limit=10&skip=" + String.valueOf(ye) + "&order=new")
                    .addHeaders("Charset", "UTF-8")
                    .setResponseListener(new ResponseListener() {
                        @Override
                        public void onResponse(String response, Exception error) {
                            Utils.loadDialog.dismiss();
                            try {
                                map = new Gson().fromJson(response, new TypeToken<HashMap<String, Object>>() {}.getType());
                                map1 = new Gson().fromJson(new Gson().toJson(map.get("res")), new TypeToken<HashMap<String, Object>>() {}.getType());
                                listmap = new Gson().fromJson(new Gson().toJson(map1.get("vertical")), new TypeToken<ArrayList<HashMap<String, Object>>>() {}.getType());
                                TransitionManager.beginDelayedTransition(srl, new androidx.transition.AutoTransition());
                                rv.setAdapter(new Recyclerview1Adapter(listmap));
                                rv.getAdapter().notifyDataSetChanged();
                            } catch (Exception e) {
                            }
                        }
                    }).doGet();
        }

        srl.setOnLoadMoreListener(refreshLayout -> {
            if (!Utils.isVPNConnected(Wallpaper1Activity.this)) {
                ye = ye + 10;
                HttpRequest.build(Wallpaper1Activity.this, "http://service.picasso.adesk.com/v1/lightwp/category/" + getIntent().getStringExtra("id") + "/vertical?limit=10&skip=" + String.valueOf(ye) + "&order=new")
                        .addHeaders("Charset", "UTF-8")
                        .setResponseListener(new ResponseListener() {
                            @Override
                            public void onResponse(String response, Exception error) {
                                srl.finishLoadMore(false);
                                try {
                                    map = new Gson().fromJson(response, new TypeToken<HashMap<String, Object>>() {}.getType());
                                    map1 = new Gson().fromJson(new Gson().toJson(map.get("res")), new TypeToken<HashMap<String, Object>>() {}.getType());
                                    listmap1 = new Gson().fromJson(new Gson().toJson(map1.get("vertical")), new TypeToken<ArrayList<HashMap<String, Object>>>() {}.getType());
                                    TransitionManager.beginDelayedTransition(srl, new androidx.transition.AutoTransition());
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

        @Override
        public @NotNull ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater _inflater = (LayoutInflater) parent.getContext().getSystemService(LAYOUT_INFLATER_SERVICE);
            View _v = _inflater.inflate(R.layout.item_bz1, null);
            RecyclerView.LayoutParams _lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            _v.setLayoutParams(_lp);
            return new ViewHolder(_v);
        }

        @Override
        public void onBindViewHolder(ViewHolder _holder, final int _position) {
            View _view = _holder.itemView;

            final MaterialCardView cardview1 = _view.findViewById(R.id.cardview1);
            final ImageView tp1 = _view.findViewById(R.id.tp1);

            Glide.with(Wallpaper1Activity.this).load(_data.get(_position).get("img")).thumbnail(0.1f).fitCenter().priority(Priority.IMMEDIATE).into(tp1);
            cardview1.setOnClickListener(_view1 -> {
                final AlertDialog mDialog = new MaterialAlertDialogBuilder(Wallpaper1Activity.this)
                        .create();
                View contentView = View.inflate(Wallpaper1Activity.this, R.layout.dialog_tp,null);
                mDialog.setView(contentView);
                mDialog.show();
                final ImageView imageView = contentView.findViewById(R.id.imageView);
                final MaterialButton button1 = contentView.findViewById(R.id.button1);
                final MaterialButton button2 = contentView.findViewById(R.id.button2);
                button1.setText(R.string.设置壁纸);
                button1.setBackgroundColor(getResources().getColor(R.color.zts));
                button1.setTextColor(getResources().getColor(R.color.white));
                button2.setText(R.string.保存图片);
                button2.setBackgroundColor(getResources().getColor(R.color.zts));
                button2.setTextColor(getResources().getColor(R.color.white));
                Glide.with(Wallpaper1Activity.this).load(_data.get(_position).get("img")).thumbnail(0.1f).fitCenter().priority(Priority.IMMEDIATE).into(imageView);
                button1.setOnClickListener(v1 -> {
                    mDialog.dismiss();
                    Utils.LoadingDialog(Wallpaper1Activity.this);
                    Glide.with(Wallpaper1Activity.this)
                            .asBitmap()
                            .load(_data.get((int) _position).get("wp"))
                            .into(new SimpleTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(@NonNull Bitmap bitmap, @Nullable Transition<? super Bitmap> transition) {
                                    Utils.loadDialog.dismiss();
                                    try {
                                        getContentResolver().delete(uri,null,null);
                                    } catch (Exception e) {
                                    }
                                    uri = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "wall", null));
                                    Intent intent = new Intent(Intent.ACTION_ATTACH_DATA);
                                    intent.addCategory(Intent.CATEGORY_DEFAULT);
                                    intent.setDataAndType(uri, "image/jpeg");
                                    intent.putExtra("mimeType", "image/jpeg");
                                    startActivity(Intent.createChooser(intent, "设置壁纸"));
                                }
                            });
                });
                button2.setOnClickListener(v1 -> {
                    mDialog.dismiss();
                    try {
                        LoadingDialog(Wallpaper1Activity.this);
                        Glide.with(Wallpaper1Activity.this)
                            .asBitmap()
                            .load(_data.get((int) _position).get("wp").toString())
                            .into(new SimpleTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(@NonNull Bitmap bitmap, @Nullable Transition<? super Bitmap> transition) {
                                    new Thread((Runnable) () -> {
                                        @SuppressLint("SimpleDateFormat")
                                        String savedFile = SaveImage(v1.getContext(), bitmap, "/噬心工具箱/壁纸大全/", "Image-" + new SimpleDateFormat("HH-mm-ss").format(new Date()) + ".png");
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            getContentResolver().delete(uri,null,null);
        } catch (Exception e) {
        }
    }
}