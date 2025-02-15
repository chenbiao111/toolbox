package com.shixin.app;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.transition.TransitionManager;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.gyf.immersionbar.ImmersionBar;
import com.kongzue.baseokhttp.HttpRequest;
import com.kongzue.baseokhttp.listener.ResponseListener;
import com.tapadoo.alerter.Alerter;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.shixin.app.utils.Utils.Download;
import static com.shixin.app.utils.Utils.LoadingDialog;
import static com.shixin.app.utils.Utils.loadDialog;

public class NeteaseSongActivity extends AppCompatActivity {

    @BindView(R.id.root)
    ViewGroup root;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.textInputLayout)
    TextInputLayout textInputLayout;
    @BindView(R.id.textInputEditText)
    TextInputEditText textInputEditText;
    @BindView(R.id.fab)
    ExtendedFloatingActionButton fab;
    @BindView(R.id.textView)
    AutoCompleteTextView textView;
    @BindView(R.id.card)
    MaterialCardView card;
    @BindView(R.id.copy)
    MaterialCardView copy;
    @BindView(R.id.down)
    MaterialCardView down;

    private HashMap<String, Object> map = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_netease_song);
        ButterKnife.bind(this);

        ImmersionBar.with(this)
                .fitsSystemWindows(true)
                .statusBarColor(R.color.appbarColor)
                .navigationBarColor(R.color.backgroundColor)
                .autoDarkModeEnable(true)
                .init();

        toolbar.setTitle(getString(R.string.网易云音乐解析));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        textInputEditText = findViewById(R.id.textInputEditText);
        textInputLayout = findViewById(R.id.textInputLayout);
        textView = findViewById(R.id.textView);


        textInputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                textInputLayout.setErrorEnabled(false);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        fab.setOnClickListener(view -> {
            if (TextUtils.isEmpty(textInputEditText.getText().toString())){
                textInputLayout.setError(getString(R.string.请输入网易云歌曲ID));
                textInputLayout.setErrorEnabled(true);
            }else {
                LoadingDialog(NeteaseSongActivity.this);
                HttpRequest.build(NeteaseSongActivity.this, "https://tenapi.cn/music/?id=" + textInputEditText.getText().toString() + "&type=song&media=netease")
                        .addHeaders("Charset", "UTF-8")
                        .setResponseListener(new ResponseListener() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void onResponse(String response, Exception error) {
                                loadDialog.dismiss();
                                try {
                                    map = new Gson().fromJson(response, new TypeToken<HashMap<String, Object>>() {}.getType());
                                    TransitionManager.beginDelayedTransition(root, new androidx.transition.AutoTransition());
                                    card.setVisibility(View.VISIBLE);
                                    textView.setText((CharSequence) map.get("mp3url"));
                                } catch (Exception e) {
                                }
                            }
                        }).doGet();
            }
        });

        copy.setOnClickListener(v -> {
            ((ClipboardManager) v.getContext().getSystemService(CLIPBOARD_SERVICE)).setPrimaryClip(ClipData.newPlainText("clipboard", textView.getText().toString()));
            Alerter.create((Activity) v.getContext())
                    .setTitle(R.string.复制成功)
                    .setText(R.string.已成功将内容复制到剪切板)
                    .setBackgroundColorInt(getResources().getColor(R.color.success))
                    .show();
        });

        down.setOnClickListener(v -> {
            Download(NeteaseSongActivity.this, getString(R.string.保存歌曲), getString(R.string.网易云音乐解析路径), String.valueOf(map.get("mp3url")), "/噬心工具箱/网易云音乐解析/", map.get("name") + "-" + map.get("author") + ".mp3");
        });

    }
}