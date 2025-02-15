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
import android.widget.AutoCompleteTextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.transition.TransitionManager;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.gyf.immersionbar.ImmersionBar;
import com.tapadoo.alerter.Alerter;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MorseCodeActivity extends AppCompatActivity {

    @BindView(R.id.root)
    ViewGroup root;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.textInputLayout)
    TextInputLayout textInputLayout;
    @BindView(R.id.textInputEditText)
    TextInputEditText textInputEditText;
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
        setContentView(R.layout.activity_morse_code);
        ButterKnife.bind(this);

        ImmersionBar.with(this)
                .fitsSystemWindows(true)
                .statusBarColor(R.color.appbarColor)
                .navigationBarColor(R.color.backgroundColor)
                .autoDarkModeEnable(true)
                .init();

        toolbar.setTitle(getString(R.string.摩斯电码));
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

        button1.setOnClickListener(v -> {
            if (TextUtils.isEmpty(textInputEditText.getText().toString())){
                textInputLayout.setError(getString(R.string.请输入文本内容));
                textInputLayout.setErrorEnabled(true);
            }else {
                TransitionManager.beginDelayedTransition(root, new androidx.transition.AutoTransition());
                card.setVisibility(View.VISIBLE);
                try {
                    MorseCoder morseCoder = new MorseCoder();
                    textView.setText(morseCoder.decode(textInputEditText.getText().toString()));
                } catch (Exception e) {
                }
            }
        });

        button2.setOnClickListener(view -> {
            if (TextUtils.isEmpty(textInputEditText.getText().toString())){
                textInputLayout.setError(getString(R.string.请输入文本内容));
                textInputLayout.setErrorEnabled(true);
            }else {
                TransitionManager.beginDelayedTransition(root, new androidx.transition.AutoTransition());
                card.setVisibility(View.VISIBLE);
                try {
                    MorseCoder morseCoder = new MorseCoder();
                    textView.setText(morseCoder.encode(textInputEditText.getText().toString()));
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

    public final static class MorseCoder {

        private static final Map<Integer, String> alphabets = new HashMap<>();    // code point -> morse
        private static final Map<String, Integer> dictionaries = new HashMap<>(); // morse -> code point

        private static void registerMorse(Character abc, String dict) {
            alphabets.put(Integer.valueOf(abc), dict);
            dictionaries.put(dict, Integer.valueOf(abc));
        }

        static {
            // Letters
            registerMorse('A', "01");
            registerMorse('B', "1000");
            registerMorse('C', "1010");
            registerMorse('D', "100");
            registerMorse('E', "0");
            registerMorse('F', "0010");
            registerMorse('G', "110");
            registerMorse('H', "0000");
            registerMorse('I', "00");
            registerMorse('J', "0111");
            registerMorse('K', "101");
            registerMorse('L', "0100");
            registerMorse('M', "11");
            registerMorse('N', "10");
            registerMorse('O', "111");
            registerMorse('P', "0110");
            registerMorse('Q', "1101");
            registerMorse('R', "010");
            registerMorse('S', "000");
            registerMorse('T', "1");
            registerMorse('U', "001");
            registerMorse('V', "0001");
            registerMorse('W', "011");
            registerMorse('X', "1001");
            registerMorse('Y', "1011");
            registerMorse('Z', "1100");
            // Numbers
            registerMorse('0', "11111");
            registerMorse('1', "01111");
            registerMorse('2', "00111");
            registerMorse('3', "00011");
            registerMorse('4', "00001");
            registerMorse('5', "00000");
            registerMorse('6', "10000");
            registerMorse('7', "11000");
            registerMorse('8', "11100");
            registerMorse('9', "11110");
            // Punctuation
            registerMorse('.', "010101");
            registerMorse(',', "110011");
            registerMorse('?', "001100");
            registerMorse('\'', "011110");
            registerMorse('!', "101011");
            registerMorse('/', "10010");
            registerMorse('(', "10110");
            registerMorse(')', "101101");
            registerMorse('&', "01000");
            registerMorse(':', "111000");
            registerMorse(';', "101010");
            registerMorse('=', "10001");
            registerMorse('+', "01010");
            registerMorse('-', "100001");
            registerMorse('_', "001101");
            registerMorse('"', "010010");
            registerMorse('$', "0001001");
            registerMorse('@', "011010");
        }

        private final char dit; // short mark or dot
        private final char dah; // longer mark or dash
        private final char split;

        public MorseCoder() {
            this('.', '-', '/');
        }

        public MorseCoder(char dit, char dah, char split) {
            this.dit = dit;
            this.dah = dah;
            this.split = split;
        }

        public String encode(String text) {
            if (text == null) {
                throw new IllegalArgumentException("Text should not be null.");
            }
            StringBuilder morseBuilder = new StringBuilder();
            text = text.toUpperCase();
            for (int i = 0; i < text.codePointCount(0, text.length()); i++) {
                int codePoint = text.codePointAt(text.offsetByCodePoints(0, i));
                String word = alphabets.get(codePoint);
                if (word == null) {
                    word = Integer.toBinaryString(codePoint);
                }
                morseBuilder.append(word.replace('0', dit).replace('1', dah)).append(split);
            }
            return morseBuilder.toString();
        }

        public String decode(String morse) {
            if (morse == null) {
                throw new IllegalArgumentException("Morse should not be null.");
            }
            StringBuilder textBuilder = new StringBuilder();
            StringTokenizer tokenizer = new StringTokenizer(morse, String.valueOf(split));
            while (tokenizer.hasMoreTokens()) {
                String word = tokenizer.nextToken().replace(dit, '0').replace(dah, '1');
                Integer codePoint = dictionaries.get(word);
                if (codePoint == null) {
                    codePoint = Integer.valueOf(word, 2);
                }
                textBuilder.appendCodePoint(codePoint);
            }
            return textBuilder.toString();
        }

    }
}