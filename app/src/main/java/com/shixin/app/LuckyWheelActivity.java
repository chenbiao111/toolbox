package com.shixin.app;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.gyf.immersionbar.ImmersionBar;
import com.shixin.app.utils.FileUtil;
import com.shixin.app.widget.LuckPanLayout;
import com.shixin.app.widget.RotatePan;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LuckyWheelActivity extends AppCompatActivity {

    @BindView(R.id.root)
    ViewGroup root;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.luckPanLayout)
    LuckPanLayout luckPanLayout;
    @BindView(R.id.rotatePan)
    RotatePan rotatePan;
    @BindView(R.id.go)
    ImageView go;
    @BindView(R.id.fab)
    ExtendedFloatingActionButton fab;

    private ArrayList<String> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lucky_wheel);
        ButterKnife.bind(this);

        ImmersionBar.with(this)
                .fitsSystemWindows(true)
                .statusBarColor(R.color.appbarColor)
                .navigationBarColor(R.color.backgroundColor)
                .autoDarkModeEnable(true)
                .init();

        toolbar.setTitle(getString(R.string.做决定));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        fab.setOnClickListener(v -> {
            final AlertDialog mDialog = new MaterialAlertDialogBuilder(LuckyWheelActivity.this)
                    .create();
            mDialog.setTitle(R.string.设置参数);
            mDialog.setMessage(getString(R.string.设置参数内容));
            View contentView = View.inflate(LuckyWheelActivity.this, R.layout.dialog_edit,null);
            mDialog.setView(contentView);
            MaterialButton button1 = contentView.findViewById(R.id.button1);
            MaterialButton button2 = contentView.findViewById(R.id.button2);
            TextInputLayout textInputLayout = contentView.findViewById(R.id.textInputLayout);
            TextInputEditText textInputEditText = contentView.findViewById(R.id.textInputEditText);
            textInputLayout.setHint(R.string.请输入参数内容);
            textInputEditText.setInputType(EditorInfo.TYPE_CLASS_TEXT);
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
            button1.setText(R.string.取消);
            button1.setBackgroundColor(getResources().getColor(R.color.itemBackColor));
            button2.setText(R.string.确定);
            button2.setBackgroundColor(getResources().getColor(R.color.zts));
            button1.setOnClickListener(v1 -> {
                mDialog.dismiss();
            });
            button2.setOnClickListener(v1 -> {
                if (TextUtils.isEmpty(textInputEditText.getText())) {
                    textInputLayout.setError(getString(R.string.请输入参数内容));
                    textInputLayout.setErrorEnabled(true);
                } else {
                    mDialog.dismiss();
                    list = new ArrayList<String>(Arrays.asList(textInputEditText.getText().toString().split(" ")));
                    rotatePan.setNames(list);
                    FileUtil.writeFile(FileUtil.getPackageDataDir(getApplicationContext()).concat("/.zjd"), new Gson().toJson(list));
                }
            });
            mDialog.show();
            WindowManager.LayoutParams layoutParams = mDialog.getWindow().getAttributes();
            layoutParams.width = getResources().getDisplayMetrics().widthPixels / 10 * 9;
            mDialog.getWindow().setAttributes(layoutParams);
        });

        luckPanLayout.setAnimationEndListener(position -> {
        });

        if (FileUtil.isExistFile(FileUtil.getPackageDataDir(getApplicationContext()).concat("/.zjd"))) {
            list = new Gson().fromJson(FileUtil.readFile(FileUtil.getPackageDataDir(getApplicationContext()).concat("/.zjd")), new TypeToken<ArrayList<String>>(){}.getType());
            rotatePan.setNames(list);
        }

        go.setOnClickListener(_view -> luckPanLayout.rotate(-1, 100));

    }
}