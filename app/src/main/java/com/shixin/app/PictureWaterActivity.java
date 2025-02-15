package com.shixin.app;

import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.transition.TransitionManager;

import com.flask.colorpicker.ColorPickerView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.gyf.immersionbar.ImmersionBar;
import com.shixin.app.utils.ColorPickerDialogBuilder;
import com.shixin.app.utils.FileUtil;
import com.tapadoo.alerter.Alerter;
import com.watermark.androidwm_light.WatermarkBuilder;
import com.watermark.androidwm_light.bean.WatermarkText;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.shixin.app.utils.Utils.LoadingDialog;
import static com.shixin.app.utils.Utils.SaveImage;
import static com.shixin.app.utils.Utils.loadDialog;

public class PictureWaterActivity extends AppCompatActivity {

    @BindView(R.id.root)
    ViewGroup root;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.xztp)
    MaterialButton xztp;
    @BindView(R.id.ys)
    MaterialCardView ys;
    @BindView(R.id.ys1)
    MaterialCardView ys1;
    @BindView(R.id.img)
    ImageView img;
    @BindView(R.id.toggle)
    MaterialButtonToggleGroup toggle;
    @BindView(R.id.textInputLayout)
    TextInputLayout textInputLayout;
    @BindView(R.id.textInputEditText)
    TextInputEditText textInputEditText;
    @BindView(R.id.seekbar1)
    DiscreteSeekBar seekbar1;
    @BindView(R.id.seekbar2)
    DiscreteSeekBar seekbar2;
    @BindView(R.id.seekbar3)
    DiscreteSeekBar seekbar3;

    public final int REQ_CD_IMAGE = 101;
    private Intent image = new Intent(Intent.ACTION_GET_CONTENT);
    private Bitmap bitmap = null;
    private WatermarkText watermarkText;
    private String string = "";
    private int syys = 0xFFFFFFFF;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_water);
        ButterKnife.bind(this);

        ImmersionBar.with(this)
                .fitsSystemWindows(true)
                .statusBarColor(R.color.appbarColor)
                .navigationBarColor(R.color.backgroundColor)
                .autoDarkModeEnable(true)
                .init();

        toolbar.setTitle(getString(R.string.图片水印));
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
            if (TextUtils.isEmpty(textInputEditText.getText().toString())) {
                textInputLayout.setError(getString(R.string.请输入水印内容));
                textInputLayout.setErrorEnabled(true);
                img.setImageBitmap(bitmap);
            } else {
                if (toggle.getCheckedButtonId() == R.id.b1) {
                    string = textInputEditText.getText().toString();
                } else if (toggle.getCheckedButtonId() == R.id.b2) {
                    string = textInputEditText.getText().toString() + "\n";
                } else if (toggle.getCheckedButtonId() == R.id.b3) {
                    string = textInputEditText.getText().toString() + "\n\n";
                }
                watermarkText = new WatermarkText(string)
                        .setTextColor(syys)
                        .setTextAlpha(seekbar3.getProgress())
                        .setRotation(seekbar2.getProgress())
                        .setTextSize(seekbar1.getProgress());
                WatermarkBuilder.create(PictureWaterActivity.this, bitmap)
                        .setTileMode(true)
                        .loadWatermarkText(watermarkText)
                        .getWatermark()
                        .setToImageView(img);
            }
        });

        textInputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                textInputLayout.setErrorEnabled(false);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(textInputEditText.getText().toString())) {
                    textInputLayout.setError(getString(R.string.请输入水印内容));
                    textInputLayout.setErrorEnabled(true);
                    img.setImageBitmap(bitmap);
                } else {
                    if (toggle.getCheckedButtonId() == R.id.b1) {
                        string = textInputEditText.getText().toString();
                    } else if (toggle.getCheckedButtonId() == R.id.b2) {
                        string = textInputEditText.getText().toString() + "\n";
                    } else if (toggle.getCheckedButtonId() == R.id.b3) {
                        string = textInputEditText.getText().toString() + "\n\n";
                    }
                    watermarkText = new WatermarkText(string)
                            .setTextColor(syys)
                            .setTextAlpha(seekbar3.getProgress())
                            .setRotation(seekbar2.getProgress())
                            .setTextSize(seekbar1.getProgress());
                    WatermarkBuilder.create(PictureWaterActivity.this, bitmap)
                            .setTileMode(true)
                            .loadWatermarkText(watermarkText)
                            .getWatermark()
                            .setToImageView(img);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        ys.setOnClickListener(v -> {
            ColorPickerDialogBuilder
                    .with(v.getContext())
                    .setTitle(getString(R.string.水印颜色))
                    .initialColor(syys)
                    .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                    .density(12)
                    .setOnColorSelectedListener(selectedColor -> {
                    })
                    .setPositiveButton(getString(R.string.确定), (dialog, selectedColor, allColors) -> {
                        syys = selectedColor;
                        ys1.setCardBackgroundColor(selectedColor);
                        if (TextUtils.isEmpty(textInputEditText.getText().toString())) {
                            textInputLayout.setError(getString(R.string.请输入水印内容));
                            textInputLayout.setErrorEnabled(true);
                            img.setImageBitmap(bitmap);
                        } else {
                            if (toggle.getCheckedButtonId() == R.id.b1) {
                                string = textInputEditText.getText().toString();
                            } else if (toggle.getCheckedButtonId() == R.id.b2) {
                                string = textInputEditText.getText().toString() + "\n";
                            } else if (toggle.getCheckedButtonId() == R.id.b3) {
                                string = textInputEditText.getText().toString() + "\n\n";
                            }
                            watermarkText = new WatermarkText(string)
                                    .setTextColor(syys)
                                    .setTextAlpha(seekbar3.getProgress())
                                    .setRotation(seekbar2.getProgress())
                                    .setTextSize(seekbar1.getProgress());
                            WatermarkBuilder.create(PictureWaterActivity.this, bitmap)
                                    .setTileMode(true)
                                    .loadWatermarkText(watermarkText)
                                    .getWatermark()
                                    .setToImageView(img);
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

        seekbar1.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
                if (TextUtils.isEmpty(textInputEditText.getText().toString())) {
                    textInputLayout.setError(getString(R.string.请输入水印内容));
                    textInputLayout.setErrorEnabled(true);
                    img.setImageBitmap(bitmap);
                } else {
                    if (toggle.getCheckedButtonId() == R.id.b1) {
                        string = textInputEditText.getText().toString();
                    } else if (toggle.getCheckedButtonId() == R.id.b2) {
                        string = textInputEditText.getText().toString() + "\n";
                    } else if (toggle.getCheckedButtonId() == R.id.b3) {
                        string = textInputEditText.getText().toString() + "\n\n";
                    }
                    watermarkText = new WatermarkText(string)
                            .setTextColor(syys)
                            .setTextAlpha(seekbar3.getProgress())
                            .setRotation(seekbar2.getProgress())
                            .setTextSize(seekbar1.getProgress());
                    WatermarkBuilder.create(PictureWaterActivity.this, bitmap)
                            .setTileMode(true)
                            .loadWatermarkText(watermarkText)
                            .getWatermark()
                            .setToImageView(img);
                }
            }

            @Override
            public void onStartTrackingTouch(DiscreteSeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(DiscreteSeekBar seekBar) {

            }
        });

        seekbar2.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
                if (TextUtils.isEmpty(textInputEditText.getText().toString())) {
                    textInputLayout.setError(getString(R.string.请输入水印内容));
                    textInputLayout.setErrorEnabled(true);
                    img.setImageBitmap(bitmap);
                } else {
                    if (toggle.getCheckedButtonId() == R.id.b1) {
                        string = textInputEditText.getText().toString();
                    } else if (toggle.getCheckedButtonId() == R.id.b2) {
                        string = textInputEditText.getText().toString() + "\n";
                    } else if (toggle.getCheckedButtonId() == R.id.b3) {
                        string = textInputEditText.getText().toString() + "\n\n";
                    }
                    watermarkText = new WatermarkText(string)
                            .setTextColor(syys)
                            .setTextAlpha(seekbar3.getProgress())
                            .setRotation(seekbar2.getProgress())
                            .setTextSize(seekbar1.getProgress());
                    WatermarkBuilder.create(PictureWaterActivity.this, bitmap)
                            .setTileMode(true)
                            .loadWatermarkText(watermarkText)
                            .getWatermark()
                            .setToImageView(img);
                }
            }

            @Override
            public void onStartTrackingTouch(DiscreteSeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(DiscreteSeekBar seekBar) {

            }
        });

        seekbar3.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
                if (TextUtils.isEmpty(textInputEditText.getText().toString())) {
                    textInputLayout.setError(getString(R.string.请输入水印内容));
                    textInputLayout.setErrorEnabled(true);
                    img.setImageBitmap(bitmap);
                } else {
                    if (toggle.getCheckedButtonId() == R.id.b1) {
                        string = textInputEditText.getText().toString();
                    } else if (toggle.getCheckedButtonId() == R.id.b2) {
                        string = textInputEditText.getText().toString() + "\n";
                    } else if (toggle.getCheckedButtonId() == R.id.b3) {
                        string = textInputEditText.getText().toString() + "\n\n";
                    }
                    watermarkText = new WatermarkText(string)
                            .setTextColor(syys)
                            .setTextAlpha(seekbar3.getProgress())
                            .setRotation(seekbar2.getProgress())
                            .setTextSize(seekbar1.getProgress());
                    WatermarkBuilder.create(PictureWaterActivity.this, bitmap)
                            .setTileMode(true)
                            .loadWatermarkText(watermarkText)
                            .getWatermark()
                            .setToImageView(img);
                }
            }

            @Override
            public void onStartTrackingTouch(DiscreteSeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(DiscreteSeekBar seekBar) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_picture_water,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem){
        final String title = (String) menuItem.getTitle();
        if (title.equals(getString(R.string.保存图片))){
            LoadingDialog(PictureWaterActivity.this);
            try {
                new Thread((Runnable) () -> {
                    String savedFile = SaveImage(PictureWaterActivity.this, ((BitmapDrawable) img.getDrawable()).getBitmap(), "/噬心工具箱/图片水印/", "Image-" + new SimpleDateFormat("HH-mm-ss").format(new Date()) + ".png");
                    if (savedFile != null) {
                        MediaScannerConnection.scanFile(PictureWaterActivity.this, new String[]{savedFile}, null, (MediaScannerConnection.OnScanCompletedListener) (str, uri) -> {
                            Intent intent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
                            intent.setData(uri);
                            PictureWaterActivity.this.sendBroadcast(intent);
                            loadDialog.dismiss();
                            Alerter.create(PictureWaterActivity.this)
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
        }
        return super.onOptionsItemSelected(menuItem);
    }

    @Override
    protected void onActivityResult(int _requestCode, int _resultCode, Intent _data) {
        super.onActivityResult(_requestCode, _resultCode, _data);

        if (_requestCode == REQ_CD_IMAGE && _resultCode == Activity.RESULT_OK) {
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

                TransitionManager.beginDelayedTransition(root, new androidx.transition.AutoTransition());
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) xztp.getLayoutParams();
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_END, RelativeLayout.TRUE);
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
                xztp.setLayoutParams(layoutParams);
                xztp.setText(R.string.重选图片);

                img.setVisibility(View.VISIBLE);
                bitmap = FileUtil.decodeSampleBitmapFromPath(_filePath.get(0), 1024, 1024);
                //img.setImageBitmap(bitmap);

                if (TextUtils.isEmpty(textInputEditText.getText().toString())) {
                    textInputLayout.setError(getString(R.string.请输入水印内容));
                    textInputLayout.setErrorEnabled(true);
                    img.setImageBitmap(bitmap);
                } else {
                    if (toggle.getCheckedButtonId() == R.id.b1) {
                        string = textInputEditText.getText().toString();
                    } else if (toggle.getCheckedButtonId() == R.id.b2) {
                        string = textInputEditText.getText().toString() + "\n";
                    } else if (toggle.getCheckedButtonId() == R.id.b3) {
                        string = textInputEditText.getText().toString() + "\n\n";
                    }
                    watermarkText = new WatermarkText(string)
                            .setTextColor(syys)
                            .setTextAlpha(seekbar3.getProgress())
                            .setRotation(seekbar2.getProgress())
                            .setTextSize(seekbar1.getProgress());
                    WatermarkBuilder.create(PictureWaterActivity.this, bitmap)
                            .setTileMode(true)
                            .loadWatermarkText(watermarkText)
                            .getWatermark()
                            .setToImageView(img);
                }
            }
        }
    }

}