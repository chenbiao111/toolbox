package com.shixin.app;

import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.transition.AutoTransition;
import androidx.transition.TransitionManager;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.card.MaterialCardView;
import com.gyf.immersionbar.ImmersionBar;
import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.tapadoo.alerter.Alerter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FontSizeActivity extends AppCompatActivity {

    @BindView(R.id.root)
    ViewGroup root;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.toggle)
    MaterialButtonToggleGroup toggle;
    @BindView(R.id.qx_card)
    MaterialCardView qx_card;
    @BindView(R.id.qx)
    MaterialButton qx;
    @BindView(R.id.button1)
    MaterialButton button1;
    @BindView(R.id.button2)
    MaterialButton button2;

    private Float[] dx = {1.0f};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_font_size);
        ButterKnife.bind(this);

        ImmersionBar.with(this)
                .fitsSystemWindows(true)
                .statusBarColor(R.color.appbarColor)
                .navigationBarColor(R.color.backgroundColor)
                .autoDarkModeEnable(true)
                .init();

        toolbar.setTitle(getString(R.string.系统字体大小调节));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        if (XXPermissions.isGrantedPermission(this, Permission.WRITE_SETTINGS)){
            qx_card.setVisibility(View.GONE);
        }

        try {
            if (Settings.System.getFloat(getContentResolver(),"font_scale") == 0.7f) {
                toggle.check(R.id.b1);
            }
            if (Settings.System.getFloat(getContentResolver(),"font_scale") == 0.85f) {
                toggle.check(R.id.b2);
            }
            if (Settings.System.getFloat(getContentResolver(),"font_scale") == 1.0f) {
                toggle.check(R.id.b3);
            }
            if (Settings.System.getFloat(getContentResolver(),"font_scale") == 1.15f) {
                toggle.check(R.id.b4);
            }
            if (Settings.System.getFloat(getContentResolver(),"font_scale") == 1.3f) {
                toggle.check(R.id.b5);
            }
        } catch (Exception e) {

        }


        qx.setOnClickListener(v -> {
            XXPermissions.with(this)
                    .permission(Permission.WRITE_SETTINGS)
                    .request(new OnPermissionCallback() {
                        @Override
                        public void onGranted(List<String> permissions, boolean all) {
                            if (all) {
                                TransitionManager.beginDelayedTransition(root, new AutoTransition());
                                qx_card.setVisibility(View.GONE);
                            }
                        }
                        @Override
                        public void onDenied(List<String> permissions, boolean never) {
                            if (never) {
                                //toast("被永久拒绝授权，请手动授予存储权限");
                                // 如果是被永久拒绝就跳转到应用权限系统设置页面
                                XXPermissions.startPermissionActivity(FontSizeActivity.this, permissions);
                            } else {
                            }
                        }
                    });
        });

        toggle.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (checkedId == R.id.b1 && isChecked) {
                dx[0] = 0.70f;
            }
            if (checkedId == R.id.b2 && isChecked) {
                dx[0] = 0.85f;
            }
            if (checkedId == R.id.b3 && isChecked) {
                dx[0] = 1.00f;
            }
            if (checkedId == R.id.b4 && isChecked) {
                dx[0] = 1.15f;
            }
            if (checkedId == R.id.b5 && isChecked) {
                dx[0] = 1.30f;
            }
        });

        button1.setOnClickListener(v -> {
            if (XXPermissions.isGrantedPermission(this, Permission.WRITE_SETTINGS)){
                try {
                    Settings.System.putFloat(getContentResolver(),"font_scale", 1.00f);
                } catch (Exception e) {
                }
            } else {
                Alerter.create(FontSizeActivity.this)
                        .setTitle(R.string.温馨提示)
                        .setText(R.string.请授予相关权限后才能够使用)
                        .setBackgroundColorInt(getResources().getColor(R.color.error))
                        .show();
            }
        });

        button2.setOnClickListener(v -> {
            if (XXPermissions.isGrantedPermission(this, Permission.WRITE_SETTINGS)){
                try {
                    Settings.System.putFloat(getContentResolver(),"font_scale", dx[0]);
                } catch (Exception e) {
                }
            } else {
                Alerter.create(FontSizeActivity.this)
                        .setTitle(R.string.温馨提示)
                        .setText(R.string.请授予相关权限后才能够使用)
                        .setBackgroundColorInt(getResources().getColor(R.color.error))
                        .show();
            }
        });

    }
}