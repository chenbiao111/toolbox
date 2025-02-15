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

import java.io.UnsupportedEncodingException;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RC4Activity extends AppCompatActivity {

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
    TextInputEditText textInputEditText1;
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
        setContentView(R.layout.activity_rc4);
        ButterKnife.bind(this);

        ImmersionBar.with(this)
                .fitsSystemWindows(true)
                .statusBarColor(R.color.appbarColor)
                .navigationBarColor(R.color.backgroundColor)
                .autoDarkModeEnable(true)
                .init();

        toolbar.setTitle(getString(R.string.RC4加解密));
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

        button1.setOnClickListener(v -> {
            if (TextUtils.isEmpty(textInputEditText.getText().toString())){
                textInputLayout.setError(getString(R.string.请输入文本内容));
                textInputLayout.setErrorEnabled(true);
            } else if (TextUtils.isEmpty(textInputEditText1.getText().toString())){
                textInputLayout1.setError(getString(R.string.请输入密钥));
                textInputLayout1.setErrorEnabled(true);
            } else {
                TransitionManager.beginDelayedTransition(root, new androidx.transition.AutoTransition());
                card.setVisibility(View.VISIBLE);
                try {
                    textView.setText(RC4Util.decryRC4(textInputEditText.getText().toString(), textInputEditText1.getText().toString(),"GBK"));
                } catch (Exception e) {
                }
            }
        });

        button2.setOnClickListener(view -> {
            if (TextUtils.isEmpty(textInputEditText.getText().toString())){
                textInputLayout.setError(getString(R.string.请输入文本内容));
                textInputLayout.setErrorEnabled(true);
            } else if (TextUtils.isEmpty(textInputEditText1.getText().toString())){
                textInputLayout1.setError(getString(R.string.请输入密钥));
                textInputLayout1.setErrorEnabled(true);
            } else {
                TransitionManager.beginDelayedTransition(root, new androidx.transition.AutoTransition());
                card.setVisibility(View.VISIBLE);
                try {
                    textView.setText(RC4Util.encryRC4String(textInputEditText.getText().toString(), textInputEditText1.getText().toString(),"GBK"));
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

    public static class RC4Util {

        /**
         * RC4加密，将加密后的数据进行哈希
         * @param data 需要加密的数据
         * @param key 加密密钥
         * @param chartSet 编码方式
         * @return 返回加密后的数据
         * @throws UnsupportedEncodingException
         */
        public static String encryRC4String(String data, String key, String chartSet) throws UnsupportedEncodingException {
            if (data == null || key == null) {
                return null;
            }
            return bytesToHex(encryRC4Byte(data, key, chartSet));
        }

        /**
         * RC4加密，将加密后的字节数据
         * @param data 需要加密的数据
         * @param key 加密密钥
         * @param chartSet 编码方式
         * @return 返回加密后的数据
         * @throws UnsupportedEncodingException
         */
        public static byte[] encryRC4Byte(String data, String key, String chartSet) throws UnsupportedEncodingException {
            if (data == null || key == null) {
                return null;
            }
            if (chartSet == null || chartSet.isEmpty()) {
                byte bData[] = data.getBytes();
                return RC4Base(bData, key);
            } else {
                byte bData[] = data.getBytes(chartSet);
                return RC4Base(bData, key);
            }
        }

        /**
         * RC4解密
         * @param data 需要解密的数据
         * @param key 加密密钥
         * @param chartSet 编码方式
         * @return 返回解密后的数据
         * @throws UnsupportedEncodingException
         */
        public static String decryRC4(String data, String key,String chartSet) throws UnsupportedEncodingException {
            if (data == null || key == null) {
                return null;
            }
            return new String(RC4Base(hexToByte(data), key),chartSet);
        }

        /**
         * RC4加密初始化密钥
         * @param aKey
         * @return
         */
        private static byte[] initKey(String aKey) {
            byte[] bkey = aKey.getBytes();
            byte state[] = new byte[256];

            for (int i = 0; i < 256; i++) {
                state[i] = (byte) i;
            }
            int index1 = 0;
            int index2 = 0;
            if (bkey.length == 0) {
                return null;
            }
            for (int i = 0; i < 256; i++) {
                index2 = ((bkey[index1] & 0xff) + (state[i] & 0xff) + index2) & 0xff;
                byte tmp = state[i];
                state[i] = state[index2];
                state[index2] = tmp;
                index1 = (index1 + 1) % bkey.length;
            }
            return state;
        }


        /**
         * 字节数组转十六进制
         * @param bytes
         * @return
         */
        public static String bytesToHex(byte[] bytes) {
            StringBuffer sb = new StringBuffer();
            for(int i = 0; i < bytes.length; i++) {
                String hex = Integer.toHexString(bytes[i] & 0xFF);
                if(hex.length() < 2){
                    sb.append(0);
                }
                sb.append(hex);
            }
            return sb.toString();
        }

        /**
         * 十六进制转字节数组
         * @return
         */
        public static byte[] hexToByte(String inHex){
            int hexlen = inHex.length();
            byte[] result;
            if (hexlen % 2 == 1){
                hexlen++;
                result = new byte[(hexlen/2)];
                inHex="0"+inHex;
            }else {
                result = new byte[(hexlen/2)];
            }
            int j=0;
            for (int i = 0; i < hexlen; i+=2){
                result[j]=(byte)Integer.parseInt(inHex.substring(i,i+2),16);
                j++;
            }
            return result;
        }

        /**
         * RC4解密
         * @param input
         * @param mKkey
         * @return
         */
        private static byte[] RC4Base(byte[] input, String mKkey) {
            int x = 0;
            int y = 0;
            byte key[] = initKey(mKkey);
            int xorIndex;
            byte[] result = new byte[input.length];
            for (int i = 0; i < input.length; i++) {
                x = (x + 1) & 0xff;
                y = ((key[x] & 0xff) + y) & 0xff;
                byte tmp = key[x];
                key[x] = key[y];
                key[y] = tmp;
                xorIndex = ((key[x] & 0xff) + (key[y] & 0xff)) & 0xff;
                result[i] = (byte) (input[i] ^ key[xorIndex]);
            }
            return result;
        }
    }
}