package com.shixin.app;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.transition.TransitionManager;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.gyf.immersionbar.ImmersionBar;
import com.tapadoo.alerter.Alerter;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SpecialTextActivity extends AppCompatActivity {

    @BindView(R.id.root)
    ViewGroup root;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.textInputLayout)
    TextInputLayout textInputLayout;
    @BindView(R.id.textInputEditText)
    TextInputEditText textInputEditText;
    @BindView(R.id.textInputLayout1)
    TextInputLayout textInputLayout1;
    @BindView(R.id.textInputEditText1)
    MaterialAutoCompleteTextView textInputEditText1;
    @BindView(R.id.button1)
    MaterialButton button1;
    @BindView(R.id.button2)
    MaterialButton button2;
    @BindView(R.id.textView)
    AutoCompleteTextView textView;
    @BindView(R.id.card)
    MaterialCardView card;
    @BindView(R.id.copy)
    MaterialCardView copy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_special_text);
        ButterKnife.bind(this);

        ImmersionBar.with(this)
                .fitsSystemWindows(true)
                .statusBarColor(R.color.appbarColor)
                .navigationBarColor(R.color.backgroundColor)
                .autoDarkModeEnable(true)
                .init();

        toolbar.setTitle(getString(R.string.特殊文本生成));
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

        textInputEditText1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                textInputLayout1.setErrorEnabled(false);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        String[] style = {"测⃠试⃠测⃠试⃠","⃢测⃢试⃢测⃢试⃢","a'ゞ测试测试","⃘⃘测⃘⃘试⃘⃘测⃘⃘试⃘⃘","⃟测⃟试⃟测⃟试⃟","꯭测꯭试꯭测꯭试꯭","̶̶̶̶测̶̶̶̶试̶̶̶̶测̶̶̶̶试̶̶̶̶","ۣۖิۣۖิۣۖิ测ۣۖิۣۖิۣۖิ试ۣۖิۣۖิۣۖิ测ۣۖิۣۖิۣۖิ试ۣۖิۣۖิۣۖิ","҉҉҉҉测҉҉҉҉试҉҉҉҉测҉҉҉҉试҉҉҉҉","ζั͡ ั͡测 ั͡试 ั͡测 ั͡试 ั͡✾"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, style);
        textInputEditText1.setAdapter(adapter);

        button1.setOnClickListener(v -> {
            TransitionManager.beginDelayedTransition(root, new androidx.transition.AutoTransition());
            textInputEditText.setText("");
            card.setVisibility(View.GONE);
        });

        button2.setOnClickListener(view -> {
            if (TextUtils.isEmpty(textInputEditText.getText().toString())){
                textInputLayout.setError(getString(R.string.请输入文本内容));
                textInputLayout.setErrorEnabled(true);
            }else {
                TransitionManager.beginDelayedTransition(root, new androidx.transition.AutoTransition());
                card.setVisibility(View.VISIBLE);
                try {
                    if (textInputEditText1.getText().toString().equals("测⃠试⃠测⃠试⃠")) {
                        textView.setText(textInputEditText.getText().toString().replace("", "⃠"));
                    }
                    if (textInputEditText1.getText().toString().equals("⃢测⃢试⃢测⃢试⃢")) {
                        textView.setText(textInputEditText.getText().toString().replace("", "⃢"));
                    }
                    if (textInputEditText1.getText().toString().equals("a'ゞ测试测试")) {
                        textView.setText("a'ゞ".concat(textInputEditText.getText().toString()));
                    }
                    if (textInputEditText1.getText().toString().equals("⃘⃘测⃘⃘试⃘⃘测⃘⃘试⃘⃘")) {
                        textView.setText(textInputEditText.getText().toString().replace("", "⃘⃘"));
                    }
                    if (textInputEditText1.getText().toString().equals("⃟测⃟试⃟测⃟试⃟")) {
                        textView.setText(textInputEditText.getText().toString().replace("", "⃟"));
                    }
                    if (textInputEditText1.getText().toString().equals("꯭测꯭试꯭测꯭试꯭")) {
                        textView.setText(textInputEditText.getText().toString().replace("", "꯭"));
                    }
                    if (textInputEditText1.getText().toString().equals("̶̶̶̶测̶̶̶̶试̶̶̶̶测̶̶̶̶试̶̶̶̶")) {
                        textView.setText(textInputEditText.getText().toString().replace("", "̶̶̶̶̶̶̶̶"));
                    }
                    if (textInputEditText1.getText().toString().equals("ۣۖิۣۖิۣۖิ测ۣۖิۣۖิۣۖิ试ۣۖิۣۖิۣۖิ测ۣۖิۣۖิۣۖิ试ۣۖิۣۖิۣۖิ")) {
                        textView.setText(textInputEditText.getText().toString().replace("", "ۣۖิ").replace(" ", ""));
                    }
                    if (textInputEditText1.getText().toString().equals("҉҉҉҉测҉҉҉҉试҉҉҉҉测҉҉҉҉试҉҉҉҉")) {
                        textView.setText(textInputEditText.getText().toString().replace("", "҉҉҉҉"));
                    }
                    if (textInputEditText1.getText().toString().equals("ζั͡ ั͡测 ั͡试 ั͡测 ั͡试 ั͡✾")) {
                        textView.setText(textInputEditText.getText().toString().replace("", " ั͡ζั͡").replace(" ", "").concat("✾"));
                    }
                } catch (Exception e) {
                }
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

    }
}