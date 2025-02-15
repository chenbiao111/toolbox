package com.shixin.app;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.transition.TransitionManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.gyf.immersionbar.ImmersionBar;
import com.kongzue.baseokhttp.HttpRequest;
import com.kongzue.baseokhttp.listener.ResponseListener;
import com.shixin.app.utils.Utils;
import com.tapadoo.alerter.Alerter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.shixin.app.utils.Utils.JieQu;
import static com.shixin.app.utils.Utils.LoadingDialog;
import static com.shixin.app.utils.Utils.loadDialog;

public class BiliBiliActivity extends AppCompatActivity {

    @BindView(R.id.root)
    ViewGroup root;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.card)
    MaterialCardView card;
    @BindView(R.id.imageView)
    ImageView imageView;
    @BindView(R.id.textInputLayout)
    TextInputLayout textInputLayout;
    @BindView(R.id.textInputEditText)
    TextInputEditText textInputEditText;
    @BindView(R.id.fab)
    ExtendedFloatingActionButton fab;
    @BindView(R.id.button3)
    MaterialButton button3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bilibili);
        ButterKnife.bind(this);

        ImmersionBar.with(this)
                .fitsSystemWindows(true)
                .statusBarColor(R.color.appbarColor)
                .navigationBarColor(R.color.backgroundColor)
                .autoDarkModeEnable(true)
                .init();

        toolbar.setTitle(getString(R.string.B站封面提取));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        textInputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                textInputLayout.setErrorEnabled(false);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(textInputEditText.getText())){
                    TransitionManager.beginDelayedTransition(root, new androidx.transition.AutoTransition());
                    card.setVisibility(View.GONE);
                    button3.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });


        fab.setOnClickListener(view -> {
            if (TextUtils.isEmpty(textInputEditText.getText())){
                textInputLayout.setError(getString(R.string.请输入BV号));
                textInputLayout.setErrorEnabled(true);
            }else {
                if (!Utils.isVPNConnected(this)) {
                    LoadingDialog(BiliBiliActivity.this);
                    HttpRequest.build(BiliBiliActivity.this, "https://api.bilibili.com/x/web-interface/view?bvid=" + textInputEditText.getText().toString() + "&callback=var%20bpic=&jsonp=jsonp")
                            .addHeaders("Charset", "UTF-8")
                            .setResponseListener(new ResponseListener() {
                                @Override
                                public void onResponse(String response, Exception error) {
                                    loadDialog.dismiss();
                                    try {
                                        HashMap<String, Object> map = new Gson().fromJson(JieQu(BiliBiliActivity.this,response,"var bpic=(","}}})") + "}}}",new TypeToken<HashMap<String, Object>>() {}.getType());
                                        map = new Gson().fromJson(new Gson().toJson(map.get("data")), new TypeToken<HashMap<String, Object>>() {}.getType());
                                        TransitionManager.beginDelayedTransition(root, new androidx.transition.AutoTransition());
                                        card.setVisibility(View.VISIBLE);
                                        button3.setVisibility(View.VISIBLE);
                                        Glide.with(BiliBiliActivity.this).load(map.get("pic").toString()).thumbnail(0.1f).fitCenter().priority(Priority.IMMEDIATE).into(imageView);
                                    } catch (Exception e){
                                    }
                                }
                            }).doGet();
                }
            }
        });

        button3.setOnClickListener(v -> {
            Utils.LoadingDialog(BiliBiliActivity.this);
            new Thread((Runnable) () -> {
                String savedFile = Utils.SaveImage(BiliBiliActivity.this, ((BitmapDrawable) imageView.getDrawable()).getBitmap(), "/噬心工具箱/BiliBili封面/", "Image-" + new SimpleDateFormat("HH-mm-ss").format(new Date()) + ".png");
                if (savedFile != null) {
                    MediaScannerConnection.scanFile((Activity) BiliBiliActivity.this, new String[]{savedFile}, null, (MediaScannerConnection.OnScanCompletedListener) (str, uri) -> {
                        Intent intent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
                        intent.setData(uri);
                        ((Activity) BiliBiliActivity.this).sendBroadcast(intent);
                        loadDialog.dismiss();
                        Alerter.create((Activity) BiliBiliActivity.this)
                                .setTitle(R.string.保存成功)
                                .setText(getString(R.string.已保存到) + savedFile)
                                .setBackgroundColorInt(getResources().getColor(R.color.success))
                                .show();
                    });
                } else {
                    loadDialog.dismiss();
                }
            }).start();
        });

    }
}