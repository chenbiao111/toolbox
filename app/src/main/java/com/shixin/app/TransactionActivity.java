package com.shixin.app;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ListPopupWindow;
import androidx.appcompat.widget.Toolbar;
import androidx.transition.TransitionManager;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.gyf.immersionbar.ImmersionBar;
import com.shixin.app.utils.TranslateUtil;
import com.tapadoo.alerter.Alerter;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.shixin.app.utils.Utils.LoadingDialog;
import static com.shixin.app.utils.Utils.loadDialog;

public class TransactionActivity extends AppCompatActivity {

    @BindView(R.id.root)
    ViewGroup root;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.fab)
    ExtendedFloatingActionButton fab;
    @BindView(R.id.button1)
    MaterialButton button1;
    @BindView(R.id.button2)
    MaterialButton button2;
    @BindView(R.id.textInputLayout)
    TextInputLayout textInputLayout;
    @BindView(R.id.textInputEditText)
    TextInputEditText textInputEditText;
    @BindView(R.id.textView)
    AutoCompleteTextView textView;
    @BindView(R.id.copy)
    MaterialCardView copy;
    @BindView(R.id.volume)
    MaterialCardView volume;
    @BindView(R.id.card)
    MaterialCardView card;

    private TextToSpeech tts;
    private TranslateUtil.TranslateCallback translateCallback;

    private String left = "auto";
    private String right = "zh-cn";
    private ListPopupWindow listPopupWindow;
    private ListPopupWindow listPopupWindow1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translation);
        ButterKnife.bind(this);

        ImmersionBar.with(this)
                .fitsSystemWindows(true)
                .statusBarColor(R.color.appbarColor)
                .navigationBarColor(R.color.backgroundColor)
                .autoDarkModeEnable(true)
                .keyboardEnable(true)
                .keyboardMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
                .init();

        toolbar.setTitle(getString(R.string.Google翻译));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        tts = new TextToSpeech(getApplicationContext(),null);

        listPopupWindow = new ListPopupWindow(TransactionActivity.this);
        final String[] style = {getString(R.string.自动检测),getString(R.string.简体中文),getString(R.string.英语),getString(R.string.日语),getString(R.string.韩语),getString(R.string.法语),getString(R.string.俄语),getString(R.string.繁体中文)};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(TransactionActivity.this, R.layout.support_simple_spinner_dropdown_item, style);
        listPopupWindow.setAdapter(adapter);
        listPopupWindow.setAnchorView(button1);
        listPopupWindow.setOnItemClickListener((AdapterView.OnItemClickListener) (parent, view, position, id) -> {
            listPopupWindow.dismiss();
            String name = style[position];
            if (name.equals(getString(R.string.自动检测))) {
                left = "auto";
                button1.setText(getString(R.string.自动检测));
            }
            if (name.equals(getString(R.string.简体中文))) {
                left = "zh-cn";
                button1.setText(getString(R.string.简体中文));
            }
            if (name.equals(getString(R.string.英语))) {
                left = "en";
                button1.setText(getString(R.string.英语));
            }
            if (name.equals(getString(R.string.日语))) {
                left = "ja";
                button1.setText(getString(R.string.日语));
            }
            if (name.equals(getString(R.string.韩语))) {
                left = "ko";
                button1.setText(getString(R.string.韩语));
            }
            if (name.equals(getString(R.string.法语))) {
                left = "fr";
                button1.setText(getString(R.string.法语));
            }
            if (name.equals(getString(R.string.俄语))) {
                left = "ru";
                button1.setText(getString(R.string.俄语));
            }
            if (name.equals(getString(R.string.繁体中文))) {
                left = "zh-tw";
                button1.setText(getString(R.string.繁体中文));
            }
        });

        button1.setOnClickListener(v -> listPopupWindow.show());

        listPopupWindow1 = new ListPopupWindow(TransactionActivity.this);
        final String[] style1 = {getString(R.string.简体中文),getString(R.string.英语),getString(R.string.日语),getString(R.string.韩语),getString(R.string.法语),getString(R.string.俄语),getString(R.string.繁体中文)};
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(TransactionActivity.this, R.layout.support_simple_spinner_dropdown_item, style1);
        listPopupWindow1.setAdapter(adapter1);
        listPopupWindow1.setAnchorView(button2);
        listPopupWindow1.setOnItemClickListener((parent, view, position, id) -> {
            listPopupWindow1.dismiss();
            String name = style1[position];
            if (name.equals(getString(R.string.简体中文))) {
                right = "zh-cn";
                button2.setText(getString(R.string.简体中文));
            }
            if (name.equals(getString(R.string.英语))) {
                right = "en";
                button2.setText(getString(R.string.英语));
            }
            if (name.equals(getString(R.string.日语))) {
                right = "ja";
                button2.setText(getString(R.string.日语));
            }
            if (name.equals(getString(R.string.韩语))) {
                right = "ko";
                button2.setText(getString(R.string.韩语));
            }
            if (name.equals(getString(R.string.法语))) {
                right = "fr";
                button2.setText(getString(R.string.法语));
            }
            if (name.equals(getString(R.string.俄语))) {
                right = "ru";
                button2.setText(getString(R.string.俄语));
            }
            if (name.equals(getString(R.string.繁体中文))) {
                right = "zh-tw";
                button2.setText(getString(R.string.繁体中文));
            }
        });

        button2.setOnClickListener(v -> listPopupWindow1.show());

        fab.setOnClickListener(v -> {
            if (TextUtils.isEmpty(textInputEditText.getText().toString())){
                textInputLayout.setError(getString(R.string.请输入文本内容));
                textInputLayout.setErrorEnabled(true);
            } else {
                LoadingDialog(TransactionActivity.this);
                new TranslateUtil().translate(TransactionActivity.this, left, right, textInputEditText.getText().toString(), translateCallback);
            }
        });

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

        translateCallback = result -> {
            loadDialog.dismiss();
            TransitionManager.beginDelayedTransition(root, new androidx.transition.AutoTransition());
            textView.setText(result);
            textView.setFocusable(true);
            card.setVisibility(View.VISIBLE);
        };

        volume.setOnClickListener(v -> tts.speak(textView.getText().toString(), TextToSpeech.QUEUE_ADD,null));

        copy.setOnClickListener(v -> {
            ((ClipboardManager) v.getContext().getSystemService(CLIPBOARD_SERVICE)).setPrimaryClip(ClipData.newPlainText("clipboard", textView.getText().toString()));
            Alerter.create((Activity) v.getContext())
                    .setTitle(R.string.复制成功)
                    .setText(R.string.已成功将内容复制到剪切板)
                    .setBackgroundColorInt(getResources().getColor(R.color.success))
                    .show();
        });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        tts.stop();
    }
}