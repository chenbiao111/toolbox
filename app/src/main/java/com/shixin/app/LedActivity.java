package com.shixin.app;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.transition.TransitionManager;

import com.flask.colorpicker.ColorPickerView;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.gyf.immersionbar.ImmersionBar;
import com.shixin.app.utils.ColorPickerDialogBuilder;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

import butterknife.BindView;
import butterknife.ButterKnife;


public class LedActivity extends AppCompatActivity {

    private String bjcolor = "#FF000000";
    private String wzcolor = "#FFFFFFFF";

    @BindView(R.id.root)
    ViewGroup root;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.sd)
    MaterialCardView sd;
    @BindView(R.id.bj)
    MaterialCardView bj;
    @BindView(R.id.wz)
    MaterialCardView wz;
    @BindView(R.id.bj1)
    MaterialCardView bj1;
    @BindView(R.id.wz1)
    MaterialCardView wz1;
    @BindView(R.id.toggle)
    MaterialButtonToggleGroup toggle;
    @BindView(R.id.fab)
    ExtendedFloatingActionButton fab;
    @BindView(R.id.textInputLayout)
    TextInputLayout textInputLayout;
    @BindView(R.id.textInputEditText)
    TextInputEditText textInputEditText;
    @BindView(R.id.seekbar1)
    DiscreteSeekBar seekbar1;
    @BindView(R.id.seekbar2)
    DiscreteSeekBar seekbar2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_led);
        ButterKnife.bind(this);

        ImmersionBar.with(this)
                .fitsSystemWindows(true)
                .statusBarColor(R.color.appbarColor)
                .navigationBarColor(R.color.backgroundColor)
                .autoDarkModeEnable(true)
                .keyboardEnable(true)
                .keyboardMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
                .init();

        toolbar.setTitle(getString(R.string.LED手机字幕));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        toggle.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (checkedId == R.id.b1 && isChecked) {
                TransitionManager.beginDelayedTransition(root, new androidx.transition.Slide(Gravity.END));
                sd.setVisibility(View.GONE);
            }
            if (checkedId == R.id.b2 && isChecked) {
                TransitionManager.beginDelayedTransition(root, new androidx.transition.Slide(Gravity.START));
                sd.setVisibility(View.VISIBLE);
            }
        });

        bj.setOnClickListener(v -> {
            ColorPickerDialogBuilder
                    .with(v.getContext())
                    .setTitle(getString(R.string.背景颜色))
                    .initialColor(Color.parseColor(bjcolor))
                    .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                    .density(12)
                    .setOnColorSelectedListener(selectedColor -> {
                    })
                    .setPositiveButton(getString(R.string.确定), (dialog, selectedColor, allColors) -> {
                        bjcolor = "#"+Integer.toHexString(selectedColor);
                        try {
                            bj1.setCardBackgroundColor(selectedColor);
                        } catch (Exception e) {

                        }
                    })
                    .setNegativeButton(getString(R.string.取消), (dialog, which) -> {
                    })
                    .showColorEdit(true)
                    .showAlphaSlider(true)
                    .setColorEditTextColor(getResources().getColor(R.color.editTextColor))
                    .build()
                    .show();
        });

        wz.setOnClickListener(v -> {
            ColorPickerDialogBuilder
                    .with(v.getContext())
                    .setTitle(getString(R.string.文字颜色))
                    .initialColor(Color.parseColor(wzcolor))
                    .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                    .density(12)
                    .setOnColorSelectedListener(selectedColor -> {
                    })
                    .setPositiveButton(getString(R.string.确定), (dialog, selectedColor, allColors) -> {
                        wzcolor = "#"+Integer.toHexString(selectedColor);
                        try {
                            wz1.setCardBackgroundColor(selectedColor);
                        } catch (Exception e) {

                        }
                    })
                    .setNegativeButton(getString(R.string.取消), (dialog, which) -> {
                    })
                    .showColorEdit(true)
                    .showAlphaSlider(false)
                    .setColorEditTextColor(getResources().getColor(R.color.editTextColor))
                    .build()
                    .show();
        });

        fab.setOnClickListener(v -> {
            if (TextUtils.isEmpty(textInputEditText.getText().toString())){
                textInputLayout.setError("请输入文本内容");
                textInputLayout.setErrorEnabled(true);
            } else if (sd.getVisibility() == View.VISIBLE){
                Intent intent = new Intent(LedActivity.this, Led1Activity.class);
                intent.putExtra("nr",textInputEditText.getText().toString());
                intent.putExtra("bjys",bjcolor);
                intent.putExtra("wzys",wzcolor);
                intent.putExtra("sd",seekbar2.getProgress());
                intent.putExtra("dx",seekbar1.getProgress());
                startActivity(intent);
            } else {
                Intent intent = new Intent(LedActivity.this, Led2Activity.class);
                intent.putExtra("nr",textInputEditText.getText().toString());
                intent.putExtra("bjys",bjcolor);
                intent.putExtra("wzys",wzcolor);
                intent.putExtra("dx",seekbar1.getProgress());
                startActivity(intent);
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

    }

}