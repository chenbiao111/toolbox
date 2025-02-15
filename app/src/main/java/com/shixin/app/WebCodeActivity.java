package com.shixin.app;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.transition.TransitionManager;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.gyf.immersionbar.ImmersionBar;
import com.kongzue.baseokhttp.HttpRequest;
import com.kongzue.baseokhttp.listener.ResponseListener;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.shixin.app.utils.Utils.LoadingDialog;
import static com.shixin.app.utils.Utils.loadDialog;

public class WebCodeActivity extends AppCompatActivity {

    @BindView(R.id.root)
    ViewGroup root;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.textInputLayout)
    TextInputLayout textInputLayout;
    @BindView(R.id.textInputEditText)
    TextInputEditText textInputEditText;
    @BindView(R.id.textView)
    TextView textView;
    @BindView(R.id.toolbar_layout)
    CollapsingToolbarLayout toolBarLayout;
    @BindView(R.id.fab)
    ExtendedFloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_code);
        ButterKnife.bind(this);

        ImmersionBar.with(this)
                .fitsSystemWindows(true)
                .statusBarColor(R.color.appbarColor)
                .navigationBarColor(R.color.backgroundColor)
                .autoDarkModeEnable(true)
                .init();

        toolbar.setTitle(getString(R.string.网页获源));
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
                textInputLayout.setError(getString(R.string.请输入网址));
                textInputLayout.setErrorEnabled(true);
            }else {
                LoadingDialog(WebCodeActivity.this);
                HttpRequest.build(WebCodeActivity.this, textInputEditText.getText().toString())
                        .addHeaders("Charset", "UTF-8")
                        .addHeaders("User-Agent", WebSettings.getDefaultUserAgent(WebCodeActivity.this))
                        .setResponseListener(new ResponseListener() {
                            @Override
                            public void onResponse(String response, Exception error) {
                                loadDialog.dismiss();
                                try {
                                    TransitionManager.beginDelayedTransition(root, new androidx.transition.AutoTransition());
                                    textView.setText(response);
                                } catch (Exception e) {
                                }
                            }
                        }).doGet();
            }
        });

    }
}