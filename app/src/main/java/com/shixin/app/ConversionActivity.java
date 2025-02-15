package com.shixin.app;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.gyf.immersionbar.ImmersionBar;
import com.shixin.app.utils.Conversion;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ConversionActivity extends AppCompatActivity implements View.OnFocusChangeListener, TextWatcher {

    @BindView(R.id.root)
    ViewGroup root;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.textInputLayout1)
    TextInputLayout textInputLayout1;
    @BindView(R.id.textInputEditText1)
    TextInputEditText textInputEditText1;
    @BindView(R.id.textInputLayout2)
    TextInputLayout textInputLayout2;
    @BindView(R.id.textInputEditText2)
    TextInputEditText textInputEditText2;
    @BindView(R.id.textInputLayout3)
    TextInputLayout textInputLayout3;
    @BindView(R.id.textInputEditText3)
    TextInputEditText textInputEditText3;
    @BindView(R.id.textInputLayout4)
    TextInputLayout textInputLayout4;
    @BindView(R.id.textInputEditText4)
    TextInputEditText textInputEditText4;

    private TextInputEditText[] edits;
    private TextInputEditText focus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversion);
        ButterKnife.bind(this);

        ImmersionBar.with(this)
                .fitsSystemWindows(true)
                .statusBarColor(R.color.appbarColor)
                .navigationBarColor(R.color.backgroundColor)
                .autoDarkModeEnable(true)
                .init();

        toolbar.setTitle(getString(R.string.进制转换));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        edits=new TextInputEditText[]{textInputEditText1,textInputEditText2,textInputEditText3,textInputEditText4};

        for(TextInputEditText i : edits) {
            i.setOnFocusChangeListener(this);
            i.addTextChangedListener(this);
        }

    }

    @Override	//文本改变前
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
    @Override	//文本改变中
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        String out1="",out2="",out3="",out4="";
        if(focus==textInputEditText1) {
            out1 = s.toString();
            out2 = Conversion.z10_2(out1);
            out3 = Conversion.z2_8(out2);
            out4 = Conversion.z2_16(out2);
        }
        if(focus==textInputEditText2) {
            out2 = s.toString();
            out1 = Conversion.z2_10(out2);
            out3 = Conversion.z2_8(out2);
            out4 = Conversion.z2_16(out2);
        }
        if(focus==textInputEditText3) {
            out3 = s.toString();
            out2 = Conversion.z8_2(out3);
            out1 = Conversion.z2_10(out2);
            out4 = Conversion.z2_16(out2);
        }
        if(focus==textInputEditText4) {
            out4 = s.toString();
            out2 = Conversion.z16_2(out4);
            out1 = Conversion.z2_10(out2);
            out3 = Conversion.z2_8(out2);
        }
        //必须先移除监听器，否则会导致栈溢出
        for(TextInputEditText i : edits) {
            i.removeTextChangedListener(this);
        }
        int index=out2.indexOf(".");	//限制二进制显示的小数为16位
        if (index>0 && out2.substring(index+1,out2.length()).length()>16) out2=out2.substring(0,index+1+16);
        textInputEditText1.setText(out1);
        textInputEditText2.setText(out2);
        textInputEditText3.setText(out3);
        textInputEditText4.setText(out4);
        for(TextInputEditText i : edits) {
            i.addTextChangedListener(this);
        }	//之后需要重新设置光标位置
        focus.setSelection(start+count);
    }
    @Override	//文本改变后
    public void afterTextChanged(Editable s) {}


    @Override	//焦点改变
    public void onFocusChange(View v, boolean hasFocus) {
        if(hasFocus) {
            this.focus = (TextInputEditText)findViewById(v.getId());
        }
    }
}