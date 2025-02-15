package com.shixin.app;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.transition.TransitionManager;

import com.flask.colorpicker.ColorPickerView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.gyf.immersionbar.ImmersionBar;
import com.shixin.app.utils.CodeCreator;
import com.shixin.app.utils.ColorPickerDialogBuilder;
import com.shixin.app.utils.FileUtil;
import com.tapadoo.alerter.Alerter;
import com.yalantis.ucrop.UCrop;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.shixin.app.utils.Utils.LoadingDialog;
import static com.shixin.app.utils.Utils.SaveImage;
import static com.shixin.app.utils.Utils.loadDialog;
import static com.shixin.app.utils.Utils.startUCrop;

public class QRCodeActivity extends AppCompatActivity {

    @BindView(R.id.root)
    ViewGroup root;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.logo_card)
    MaterialCardView logo_card;
    @BindView(R.id.bj)
    MaterialCardView bj;
    @BindView(R.id.qj)
    MaterialCardView qj;
    @BindView(R.id.bj1)
    MaterialCardView bj1;
    @BindView(R.id.qj1)
    MaterialCardView qj1;
    @BindView(R.id.toggle)
    MaterialButtonToggleGroup toggle;
    @BindView(R.id.textInputLayout)
    TextInputLayout textInputLayout;
    @BindView(R.id.textInputEditText)
    TextInputEditText textInputEditText;
    @BindView(R.id.seekbar1)
    DiscreteSeekBar seekbar1;
    @BindView(R.id.fab)
    ExtendedFloatingActionButton fab;
    @BindView(R.id.xztp)
    MaterialButton xztp;
    @BindView(R.id.tplj)
    TextView tplj;

    private String qjcolor = "#FF000000";
    private String bjcolor = "#FFFFFFFF";
    private Bitmap logo = null;
    private Bitmap bitmap = null;
    public final int REQ_CD_IMAGE = 101;
    private Intent image = new Intent(Intent.ACTION_GET_CONTENT);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_code);
        ButterKnife.bind(this);

        ImmersionBar.with(this)
                .fitsSystemWindows(true)
                .statusBarColor(R.color.appbarColor)
                .navigationBarColor(R.color.backgroundColor)
                .autoDarkModeEnable(true)
                .keyboardEnable(true)
                .keyboardMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
                .init();

        toolbar.setTitle(getString(R.string.二维码生成));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        image.setType("image/*");
        image.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
        xztp.setOnClickListener(v -> {
            startActivityForResult(image, REQ_CD_IMAGE);
        });

        toggle.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (checkedId == R.id.b1 && isChecked) {
                TransitionManager.beginDelayedTransition(root, new androidx.transition.Slide(Gravity.END));
                logo_card.setVisibility(View.GONE);
                logo = null;
            }
            if (checkedId == R.id.b2 && isChecked) {
                TransitionManager.beginDelayedTransition(root, new androidx.transition.Slide(Gravity.START));
                logo_card.setVisibility(View.VISIBLE);
                logo = bitmap;
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
                    .showAlphaSlider(false)
                    .setColorEditTextColor(getResources().getColor(R.color.editTextColor))
                    .build()
                    .show();
        });

        qj.setOnClickListener(v -> {
            ColorPickerDialogBuilder
                    .with(v.getContext())
                    .setTitle(getString(R.string.前景颜色))
                    .initialColor(Color.parseColor(qjcolor))
                    .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                    .density(12)
                    .setOnColorSelectedListener(selectedColor -> {
                    })
                    .setPositiveButton(getString(R.string.确定), (dialog, selectedColor, allColors) -> {
                        qjcolor = "#"+Integer.toHexString(selectedColor);
                        try {
                            qj1.setCardBackgroundColor(selectedColor);
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

        fab.setOnClickListener(v -> {
            if (TextUtils.isEmpty(textInputEditText.getText().toString())) {
                textInputLayout.setError(getString(R.string.请输入二维码内容));
                textInputLayout.setErrorEnabled(true);
            } else {
                final AlertDialog mDialog = new MaterialAlertDialogBuilder(QRCodeActivity.this)
                        .create();
                View contentView = View.inflate(QRCodeActivity.this, R.layout.dialog_tp,null);
                mDialog.setView(contentView);
                mDialog.show();
                final ImageView imageView = contentView.findViewById(R.id.imageView);
                final MaterialButton button1 = contentView.findViewById(R.id.button1);
                final MaterialButton button2 = contentView.findViewById(R.id.button2);
                button1.setText(R.string.取消);
                button1.setBackgroundColor(getResources().getColor(R.color.itemBackColor));
                button2.setText(R.string.保存);
                button2.setBackgroundColor(getResources().getColor(R.color.zts));
                if (logo == null) {
                    imageView.setImageBitmap(CodeCreator.createQRCode(String.valueOf(textInputEditText.getText()), seekbar1.getProgress(), seekbar1.getProgress(), qjcolor, bjcolor, null));
                } else {
                    imageView.setImageBitmap(CodeCreator.createQRCode(String.valueOf(textInputEditText.getText()), seekbar1.getProgress(), seekbar1.getProgress(), qjcolor, bjcolor, logo));
                }
                button1.setOnClickListener(v1 -> {
                    mDialog.dismiss();
                });
                button2.setOnClickListener(v1 -> {
                    mDialog.dismiss();
                    try {
                        LoadingDialog(QRCodeActivity.this);
                        new Thread((Runnable) () -> {
                            @SuppressLint("SimpleDateFormat")
                            String savedFile = SaveImage(v1.getContext(), ((BitmapDrawable) imageView.getDrawable()).getBitmap(), "/噬心工具箱/二维码生成/", "Image-" + new SimpleDateFormat("HH-mm-ss").format(new Date()) + ".png");
                            if (savedFile != null) {
                                MediaScannerConnection.scanFile((Activity) v1.getContext(), new String[]{savedFile}, null, (MediaScannerConnection.OnScanCompletedListener) (str, uri) -> {
                                    Intent intent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
                                    intent.setData(uri);
                                    ((Activity) v1.getContext()).sendBroadcast(intent);
                                    loadDialog.dismiss();
                                    Alerter.create((Activity) v1.getContext())
                                            .setTitle(R.string.保存成功)
                                            .setText(getString(R.string.已保存到) + savedFile)
                                            .setBackgroundColorInt(getResources().getColor(R.color.success))
                                            .show();
                                });
                            } else {
                                loadDialog.dismiss();
                            }
                        }).start();
                    } catch (Exception e) {
                    }
                });
                WindowManager.LayoutParams layoutParams = mDialog.getWindow().getAttributes();
                layoutParams.width = getResources().getDisplayMetrics().widthPixels / 10 * 9;
                mDialog.getWindow().setAttributes(layoutParams);
            }
        });

    }

    @Override
    protected void onActivityResult(int _requestCode, int _resultCode, Intent _data) {

        super.onActivityResult(_requestCode, _resultCode, _data);

        if (_resultCode == RESULT_OK && _requestCode == UCrop.REQUEST_CROP) {
            final Uri resultUri = UCrop.getOutput(_data);
            bitmap = FileUtil.decodeSampleBitmapFromPath(resultUri.getPath(), 1024, 1024);
            logo = bitmap;
        } else if (_resultCode == UCrop.RESULT_ERROR) {
            final Throwable cropError = UCrop.getError(_data);
        }

        if (_requestCode == REQ_CD_IMAGE) {
            if (_resultCode == Activity.RESULT_OK) {
                ArrayList<String> _filePath = new ArrayList<>();
                if (_data != null) {
                    if (_data.getClipData() != null) {
                        for (int _index = 0; _index < _data.getClipData().getItemCount(); _index++) {
                            ClipData.Item _item = _data.getClipData().getItemAt(_index);
                            _filePath.add(FileUtil.convertUriToFilePath(getApplicationContext(), _item.getUri()));
                        }
                    } else {
                        _filePath.add(FileUtil.convertUriToFilePath(getApplicationContext(), _data.getData()));
                    }
                }
                startUCrop(QRCodeActivity.this, _filePath.get(0), 1, 1);
                tplj.setText(new File(_filePath.get(0)).getName());
            }
        }
    }

}