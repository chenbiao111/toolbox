package com.shixin.app;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.transition.TransitionManager;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.gyf.immersionbar.ImmersionBar;
import com.kongzue.baseokhttp.HttpRequest;
import com.kongzue.baseokhttp.listener.ResponseListener;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.shixin.app.utils.Utils.JieQu;
import static com.shixin.app.utils.Utils.LoadingDialog;
import static com.shixin.app.utils.Utils.isVPNConnected;
import static com.shixin.app.utils.Utils.loadDialog;

public class IdiomActivity extends AppCompatActivity {

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
    @BindView(R.id.linear)
    LinearLayout linear;
    @BindView(R.id.textview1)
    TextView textview1;
    @BindView(R.id.textview2)
    TextView textview2;
    @BindView(R.id.textview3)
    TextView textview3;
    @BindView(R.id.textview4)
    TextView textview4;
    @BindView(R.id.textview5)
    TextView textview5;
    @BindView(R.id.textview6)
    TextView textview6;
    @BindView(R.id.textview7)
    TextView textview7;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_idiom);
        ButterKnife.bind(this);

        ImmersionBar.with(this)
                .fitsSystemWindows(true)
                .statusBarColor(R.color.appbarColor)
                .navigationBarColor(R.color.backgroundColor)
                .autoDarkModeEnable(true)
                .init();

        toolbar.setTitle(getString(R.string.成语词典));
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
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        fab.setOnClickListener(view -> {
            if (TextUtils.isEmpty(textInputEditText.getText())){
                textInputLayout.setError(getString(R.string.请输入成语));
                textInputLayout.setErrorEnabled(true);
            }else {
                try {
                    if (!isVPNConnected(IdiomActivity.this)) {
                        LoadingDialog(IdiomActivity.this);
                        HttpRequest.build(IdiomActivity.this,"https://v.juhe.cn/chengyu/query?key=7c39fd238f0bfb4bb46ded3bd98179c6&word=" + textInputEditText.getText().toString())
                                .addHeaders("Charset","UTF-8")
                                .setResponseListener(new ResponseListener() {
                                    @Override
                                    public void onResponse(String response, Exception error) {
                                        loadDialog.dismiss();
                                        try {
                                            TransitionManager.beginDelayedTransition(root, new androidx.transition.AutoTransition());
                                            linear.setVisibility(View.VISIBLE);
                                            HashMap<String, Object> map = new Gson().fromJson(JieQu(IdiomActivity.this, response, "\"result\":", "},").concat("}"), new TypeToken<HashMap<String, Object>>(){}.getType());
                                            textview1.setText(String.valueOf(map.get("pinyin")).trim());
                                            textview2.setText(String.valueOf(map.get("chengyujs")).trim());
                                            textview3.setText(String.valueOf(map.get("from_")).trim());
                                            textview4.setText(String.valueOf(map.get("example")).trim());
                                            textview5.setText(String.valueOf(map.get("yufa")).trim());
                                            textview6.setText(String.valueOf(map.get("ciyujs")).trim());
                                            textview7.setText(String.valueOf(map.get("yinzhengjs")).trim());
                                        } catch(Exception e) {

                                        }
                                    }
                                }).doGet();
                    }
                } catch (Exception e) {
                }
            }
        });

    }
}