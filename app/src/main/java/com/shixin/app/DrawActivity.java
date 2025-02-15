package com.shixin.app;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.flask.colorpicker.ColorPickerView;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.gyf.immersionbar.ImmersionBar;
import com.shixin.app.utils.ColorPickerDialogBuilder;
import com.shixin.app.widget.PaletteView;
import com.tapadoo.alerter.Alerter;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.shixin.app.utils.Utils.LoadingDialog;
import static com.shixin.app.utils.Utils.SaveImage;
import static com.shixin.app.utils.Utils.loadDialog;

public class DrawActivity extends AppCompatActivity {

    @BindView(R.id.root)
    ViewGroup root;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.paletteView)
    PaletteView paletteView;
    @BindView(R.id.card1)
    MaterialCardView card1;
    @BindView(R.id.card2)
    MaterialCardView card2;
    @BindView(R.id.card3)
    MaterialCardView card3;
    @BindView(R.id.card4)
    MaterialCardView card4;
    @BindView(R.id.card5)
    MaterialCardView card5;

    private String hbcolor = "#FF000000";
    private int hbdx = 6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw);
        ButterKnife.bind(this);

        ImmersionBar.with(this)
                .fitsSystemWindows(true)
                .statusBarColor(R.color.appbarColor)
                .navigationBarColor(R.color.backgroundColor)
                .autoDarkModeEnable(true)
                .init();

        toolbar.setTitle(getString(R.string.简易画板));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        card3.setCardBackgroundColor(getResources().getColor(R.color.SelectedBackColor));
        card1.setOnClickListener(v -> paletteView.undo());
        card2.setOnClickListener(v -> paletteView.redo());
        card3.setOnClickListener(v -> {
            card3.setCardBackgroundColor(getResources().getColor(R.color.SelectedBackColor));
            card4.setCardBackgroundColor(getResources().getColor(R.color.appbarColor));
            paletteView.setMode(PaletteView.Mode.DRAW);
        });
        card4.setOnClickListener(v -> {
            card4.setCardBackgroundColor(getResources().getColor(R.color.SelectedBackColor));
            card3.setCardBackgroundColor(getResources().getColor(R.color.appbarColor));
            paletteView.setMode(PaletteView.Mode.ERASER);
        });
        card5.setOnClickListener(v -> paletteView.clear());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_draw,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem){
        final String title = (String) menuItem.getTitle();
        if (title.equals(getString(R.string.画笔颜色))){
            ColorPickerDialogBuilder
                    .with(DrawActivity.this)
                    .setTitle(getString(R.string.画笔颜色))
                    .initialColor(Color.parseColor(hbcolor))
                    .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                    .density(12)
                    .setOnColorSelectedListener(selectedColor -> {
                    })
                    .setPositiveButton(getString(R.string.确定), (dialog, selectedColor, allColors) -> {
                        hbcolor = "#"+Integer.toHexString(selectedColor);
                        paletteView.setPenColor(selectedColor);
                    })
                    .setNegativeButton(getString(R.string.取消), (dialog, which) -> {
                    })
                    .showColorEdit(true)
                    .showAlphaSlider(false)
                    .setColorEditTextColor(getResources().getColor(R.color.editTextColor))
                    .build()
                    .show();
        }
        if (title.equals(getString(R.string.画笔大小))){
            final AlertDialog mDialog = new MaterialAlertDialogBuilder(DrawActivity.this)
                    .setPositiveButton(R.string.确定,null)
                    .setNegativeButton(R.string.取消,null)
                    .create();
            mDialog.setTitle(getString(R.string.画笔大小));
            final View contentView = getLayoutInflater().inflate(R.layout.dialog_hbdx,null);
            mDialog.setView(contentView);
            final DiscreteSeekBar discreteSeekBar = contentView.findViewById(R.id.discreteSeekBar);
            discreteSeekBar.setProgress(hbdx);
            mDialog.setOnShowListener(dialog -> {
                Button positiveButton = mDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                Button negativeButton = mDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                positiveButton.setOnClickListener(v -> {
                    mDialog.dismiss();
                    hbdx = discreteSeekBar.getProgress();
                    paletteView.setPenRawSize(hbdx);
                });
                negativeButton.setOnClickListener(v -> mDialog.dismiss());
            });
            mDialog.show();
            WindowManager.LayoutParams layoutParams = mDialog.getWindow().getAttributes();
            layoutParams.width = getResources().getDisplayMetrics().widthPixels / 10 * 9;
            mDialog.getWindow().setAttributes(layoutParams);
        }
        if (title.equals(getString(R.string.保存为图片))){
            LoadingDialog(DrawActivity.this);
            new Thread((Runnable) () -> {
                @SuppressLint("SimpleDateFormat")
                String savedFile = SaveImage(DrawActivity.this, paletteView.buildBitmap(), "/噬心工具箱/简易画板/", "Image-" + new SimpleDateFormat("HH-mm-ss").format(new Date()) + ".png");
                if (savedFile != null){
                    MediaScannerConnection.scanFile((Activity) DrawActivity.this, new String[]{savedFile}, null, (MediaScannerConnection.OnScanCompletedListener) (str, uri) -> {
                        Intent intent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
                        intent.setData(uri);
                        ((Activity) DrawActivity.this).sendBroadcast(intent);
                        loadDialog.dismiss();
                        Alerter.create((Activity) DrawActivity.this)
                                .setTitle(R.string.保存成功)
                                .setText(getString(R.string.已保存到) + savedFile)
                                .setBackgroundColorInt(getResources().getColor(R.color.success))
                                .show();
                    });
                }else{
                    loadDialog.dismiss();
                }
            }).start();
        }
        return super.onOptionsItemSelected(menuItem);
    }
}