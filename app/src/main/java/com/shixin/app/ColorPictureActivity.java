package com.shixin.app;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.flask.colorpicker.ColorPickerView;
import com.google.android.material.card.MaterialCardView;
import com.gyf.immersionbar.ImmersionBar;
import com.shixin.app.utils.ColorPickerDialogBuilder;
import com.tapadoo.alerter.Alerter;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.shixin.app.utils.Utils.LoadingDialog;
import static com.shixin.app.utils.Utils.SaveImage;
import static com.shixin.app.utils.Utils.loadDialog;

public class ColorPictureActivity extends AppCompatActivity {

    @BindView(R.id.root)
    ViewGroup root;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.ys)
    MaterialCardView ys;
    @BindView(R.id.ys1)
    MaterialCardView ys1;
    @BindView(R.id.img)
    ImageView img;
    @BindView(R.id.seekbar1)
    DiscreteSeekBar seekbar1;
    @BindView(R.id.seekbar2)
    DiscreteSeekBar seekbar2;

    private Bitmap bitmap = null;
    private int color = 0xFF000000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_color_picture);
        ButterKnife.bind(this);

        ImmersionBar.with(this)
                .fitsSystemWindows(true)
                .statusBarColor(R.color.appbarColor)
                .navigationBarColor(R.color.backgroundColor)
                .autoDarkModeEnable(true)
                .init();

        toolbar.setTitle(getString(R.string.纯色图制作));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        bitmap = Bitmap.createBitmap(seekbar1.getProgress(), seekbar2.getProgress(), Bitmap.Config.ARGB_8888);
        bitmap.eraseColor(color);
        img.setImageBitmap(bitmap);

        ys.setOnClickListener(v -> {
            ColorPickerDialogBuilder
                    .with(v.getContext())
                    .setTitle(getString(R.string.图片颜色))
                    .initialColor(color)
                    .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                    .density(12)
                    .setOnColorSelectedListener(selectedColor -> {
                    })
                    .setPositiveButton(getString(R.string.确定), (dialog, selectedColor, allColors) -> {
                        color = selectedColor;
                        ys1.setCardBackgroundColor(selectedColor);
                        bitmap = Bitmap.createBitmap(seekbar1.getProgress(), seekbar2.getProgress(), Bitmap.Config.ARGB_8888);
                        bitmap.eraseColor(color);
                        img.setImageBitmap(bitmap);
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
                bitmap = Bitmap.createBitmap(seekbar1.getProgress(), seekbar2.getProgress(), Bitmap.Config.ARGB_8888);
                bitmap.eraseColor(color);
                img.setImageBitmap(bitmap);
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
                bitmap = Bitmap.createBitmap(seekbar1.getProgress(), seekbar2.getProgress(), Bitmap.Config.ARGB_8888);
                bitmap.eraseColor(color);
                img.setImageBitmap(bitmap);
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
            LoadingDialog(ColorPictureActivity.this);
            try {
                new Thread(() -> {
                    String savedFile = SaveImage(ColorPictureActivity.this, ((BitmapDrawable) img.getDrawable()).getBitmap(), "/噬心工具箱/纯色图制作/", "Image-" + new SimpleDateFormat("HH-mm-ss").format(new Date()) + ".png");
                    if (savedFile != null) {
                        MediaScannerConnection.scanFile(ColorPictureActivity.this, new String[]{savedFile}, null, (MediaScannerConnection.OnScanCompletedListener) (str, uri) -> {
                            Intent intent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
                            intent.setData(uri);
                            ColorPictureActivity.this.sendBroadcast(intent);
                            loadDialog.dismiss();
                            Alerter.create(ColorPictureActivity.this)
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

}