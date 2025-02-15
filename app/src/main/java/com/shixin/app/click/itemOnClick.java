package com.shixin.app.click;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Vibrator;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AlertDialog;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.kongzue.baseokhttp.HttpRequest;
import com.kongzue.baseokhttp.listener.ResponseListener;
import com.shixin.app.AbbreviatedActivity;
import com.shixin.app.AppActivity;
import com.shixin.app.AvatarActivity;
import com.shixin.app.AvatarMakeActivity;
import com.shixin.app.Base64Activity;
import com.shixin.app.BiliBiliActivity;
import com.shixin.app.ClockActivity;
import com.shixin.app.ColorPictureActivity;
import com.shixin.app.CompassActivity;
import com.shixin.app.ConversionActivity;
import com.shixin.app.DateCalculatorActivity;
import com.shixin.app.DayNewsActivity;
import com.shixin.app.DeWatermark1Activity;
import com.shixin.app.DeWatermarkActivity;
import com.shixin.app.DictionaryActivity;
import com.shixin.app.DonationListActivity;
import com.shixin.app.DrawActivity;
import com.shixin.app.EmoticonActivity;
import com.shixin.app.ExtractAudioActivity;
import com.shixin.app.FontSizeActivity;
import com.shixin.app.GifActivity;
import com.shixin.app.GifmakerPictureActivity;
import com.shixin.app.HistoryActivity;
import com.shixin.app.IconSearchActivity;
import com.shixin.app.IdiomActivity;
import com.shixin.app.IpSiteActivity;
import com.shixin.app.IptvActivity;
import com.shixin.app.KSongActivity;
import com.shixin.app.LanzouActivity;
import com.shixin.app.LedActivity;
import com.shixin.app.LevelActivity;
import com.shixin.app.LiveChinaActivity;
import com.shixin.app.LoveTalkActivity;
import com.shixin.app.LuckyWheelActivity;
import com.shixin.app.MetalDetectionActivity;
import com.shixin.app.MiniEnglishActivity;
import com.shixin.app.MorseCodeActivity;
import com.shixin.app.MusicActivity;
import com.shixin.app.NeteaseSongActivity;
import com.shixin.app.PhoneAttributionActivity;
import com.shixin.app.PictureBlurActivity;
import com.shixin.app.PictureClarityActivity;
import com.shixin.app.PictureColorActivity;
import com.shixin.app.PictureColoringActivity;
import com.shixin.app.PictureCompressActivity;
import com.shixin.app.PictureGreyActivity;
import com.shixin.app.PictureHideActivity;
import com.shixin.app.PictureLowPolyActivity;
import com.shixin.app.PictureNineActivity;
import com.shixin.app.PicturePixelActivity;
import com.shixin.app.PictureSketchActivity;
import com.shixin.app.PictureTextActivity;
import com.shixin.app.PictureUrlActivity;
import com.shixin.app.PictureWaterActivity;
import com.shixin.app.PowerActivity;
import com.shixin.app.ProtractorActivity;
import com.shixin.app.QRCodeActivity;
import com.shixin.app.QqVoiceActivity;
import com.shixin.app.R;
import com.shixin.app.RC4Activity;
import com.shixin.app.RandomActivity;
import com.shixin.app.RandomArticleActivity;
import com.shixin.app.RelativeActivity;
import com.shixin.app.RubbishActivity;
import com.shixin.app.RulerActivity;
import com.shixin.app.ScreenActivity;
import com.shixin.app.SearchImageActivity;
import com.shixin.app.SgameActivity;
import com.shixin.app.ShortUrlActivity;
import com.shixin.app.SmallDigitActivity;
import com.shixin.app.SpecialTextActivity;
import com.shixin.app.SplitWordActivity;
import com.shixin.app.TransactionActivity;
import com.shixin.app.Utf8Activity;
import com.shixin.app.VideoSearchActivity;
import com.shixin.app.VideoWallpaperActivity;
import com.shixin.app.VipVideoActivity;
import com.shixin.app.WallpaperActivity;
import com.shixin.app.WebCodeActivity;
import com.shixin.app.WebPictureActivity;
import com.shixin.app.WifiActivity;
import com.shixin.app.utils.ShellUtils;
import com.shixin.app.utils.Utils;
import com.shixin.app.yellow.YellowActivity;
import com.tapadoo.alerter.Alerter;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static com.shixin.app.utils.Utils.LoadingDialog;
import static com.shixin.app.utils.Utils.SaveImage;
import static com.shixin.app.utils.Utils.loadDialog;


public class itemOnClick {

    public static MediaPlayer mediaPlayer = new MediaPlayer();

    public static void item_1(Context context, String name) {
        if (name.equals(context.getString(R.string.恋爱话术))) {
            context.startActivity(new Intent(context, LoveTalkActivity.class));
        }
        if (name.equals(context.getString(R.string.QQ变音))) {
            context.startActivity(new Intent(context, QqVoiceActivity.class));
        }
        if (name.equals(context.getString(R.string.刻度尺))) {
            context.startActivity(new Intent(context, RulerActivity.class));
        }
        if (name.equals(context.getString(R.string.IPTV电视直播))) {
            context.startActivity(new Intent(context, IptvActivity.class));
        }
        if (name.equals(context.getString(R.string.直播中国))) {
            context.startActivity(new Intent(context, LiveChinaActivity.class));
        }
        if (name.equals(context.getString(R.string.VIP影视解析))) {
            context.startActivity(new Intent(context, VipVideoActivity.class));
        }
        if (name.equals(context.getString(R.string.影视搜索))) {
            context.startActivity(new Intent(context, VideoSearchActivity.class));
        }
        if (name.equals(context.getString(R.string.指南针))) {
            context.startActivity(new Intent(context, CompassActivity.class));
        }
        if (name.equals(context.getString(R.string.水平仪))) {
            context.startActivity(new Intent(context, LevelActivity.class));
        }
        if (name.equals(context.getString(R.string.量角器))) {
            if (!XXPermissions.isGrantedPermission(context, Permission.CAMERA)){
                final AlertDialog mDialog = new MaterialAlertDialogBuilder(context)
                        .setPositiveButton(R.string.申请,null)
                        .setNegativeButton(R.string.拒绝,null)
                        .create();
                mDialog.setTitle(context.getString(R.string.申请权限));
                mDialog.setMessage(Html.fromHtml(context.getString(R.string.相机权限)));
                mDialog.setOnShowListener(dialog -> {
                    Button positiveButton = mDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                    Button negativeButton = mDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                    positiveButton.setOnClickListener(v -> {
                        mDialog.dismiss();
                        XXPermissions.with(context)
                                .permission(Permission.CAMERA)
                                .request(new OnPermissionCallback() {
                                    @Override
                                    public void onGranted(List<String> permissions, boolean all) {
                                        if (all) {
                                            //toast("获取权限成功");
                                            context.startActivity(new Intent(context, ProtractorActivity.class));
                                        }
                                    }

                                    @Override
                                    public void onDenied(List<String> permissions, boolean never) {
                                        if (never) {
                                            //toast("被永久拒绝授权，请手动授予权限");
                                            // 如果是被永久拒绝就跳转到应用权限系统设置页面
                                            XXPermissions.startPermissionActivity(context, permissions);
                                        } else {
                                            //toast("获取权限失败");
                                        }
                                    }
                                });
                    });
                    negativeButton.setOnClickListener(v -> {
                        mDialog.dismiss();
                    });
                });
                mDialog.show();
                WindowManager.LayoutParams layoutParams = mDialog.getWindow().getAttributes();
                layoutParams.width = context.getResources().getDisplayMetrics().widthPixels / 10 * 9;
                mDialog.getWindow().setAttributes(layoutParams);
            } else {
                context.startActivity(new Intent(context, ProtractorActivity.class));
            }
        }
        if (name.equals(context.getString(R.string.时间屏幕))) {
            context.startActivity(new Intent(context, ClockActivity.class));
        }
        if (name.equals(context.getString(R.string.LED手机字幕))) {
            context.startActivity(new Intent(context, LedActivity.class));
        }
        if (name.equals(context.getString(R.string.网页获源))) {
            context.startActivity(new Intent(context, WebCodeActivity.class));
        }
        if (name.equals(context.getString(R.string.短网址生成))) {
            context.startActivity(new Intent(context, ShortUrlActivity.class));
        }
        if (name.equals(context.getString(R.string.简易画板))) {
            context.startActivity(new Intent(context, DrawActivity.class));
        }
        if (name.equals(context.getString(R.string.每日60秒早报))) {
            context.startActivity(new Intent(context, DayNewsActivity.class));
        }
        if (name.equals(context.getString(R.string.历史上的今天))) {
            context.startActivity(new Intent(context, HistoryActivity.class));
        }
        if (name.equals(context.getString(R.string.Google翻译))) {
            context.startActivity(new Intent(context, TransactionActivity.class));
        }
    }

    public static void item_2(Context context, String name) {
        if (name.equals(context.getString(R.string.高级重启))) {
            CharSequence[] choices = {context.getString(R.string.设备重启), context.getString(R.string.快速重启), context.getString(R.string.重启用户界面), context.getString(R.string.设备关机), context.getString(R.string.重启至REC模式), context.getString(R.string.重启至FAST模式), context.getString(R.string.重启至安全模式)};
            final AlertDialog mDialog = new MaterialAlertDialogBuilder(context)
                    .setPositiveButton(R.string.确定, (dialog, which) -> {
                        int position = ((AlertDialog)dialog).getListView().getCheckedItemPosition();
                        if (position != AdapterView.INVALID_POSITION) {
                            if (ShellUtils.checkRootPermission()) {
                                if (position == 0) {
                                    ShellUtils.execCommand("reboot", true);
                                }
                                if (position == 1) {
                                    ShellUtils.execCommand("setprop ctl.restart zygote", true);
                                }
                                if (position == 2) {
                                    ShellUtils.execCommand("killall com.android.systemui", true);
                                }
                                if (position == 3) {
                                    ShellUtils.execCommand("reboot -p", true);
                                }
                                if (position == 4) {
                                    ShellUtils.execCommand("reboot recovery", true);
                                }
                                if (position == 5) {
                                    ShellUtils.execCommand("reboot bootloader", true);
                                }
                                if (position == 6) {
                                    ShellUtils.execCommand("setprop persist.sys.safemode 1", true);
                                }
                            } else {
                                Alerter.create((Activity) context)
                                        .setTitle(R.string.无Root权限)
                                        .setText(R.string.没有Root权限)
                                        .setBackgroundColorInt(context.getResources().getColor(R.color.error))
                                        .show();
                            }
                        }
                    })
                    .setNegativeButton(R.string.取消,null)
                    .setSingleChoiceItems(choices, 1, null)
                    .create();
            mDialog.setTitle(context.getString(R.string.高级重启));
            mDialog.show();
            WindowManager.LayoutParams layoutParams = mDialog.getWindow().getAttributes();
            layoutParams.width = context.getResources().getDisplayMetrics().widthPixels / 10 * 9;
            mDialog.getWindow().setAttributes(layoutParams);
        }
        if (name.equals(context.getString(R.string.DPI修改))) {
            CharSequence[] choices = {"320","360","400","440","480","520"};
            final AlertDialog mDialog = new MaterialAlertDialogBuilder(context)
                    .setPositiveButton(R.string.确定, (dialog, which) -> {
                        int position = ((AlertDialog)dialog).getListView().getCheckedItemPosition();
                        if (position != AdapterView.INVALID_POSITION) {
                            if (ShellUtils.checkRootPermission()) {
                                if (position == 0) {
                                    ShellUtils.execCommand("wm density 320", true);
                                }
                                if (position == 1) {
                                    ShellUtils.execCommand("wm density 360", true);
                                }
                                if (position == 2) {
                                    ShellUtils.execCommand("wm density 400", true);
                                }
                                if (position == 3) {
                                    ShellUtils.execCommand("wm density 440", true);
                                }
                                if (position == 4) {
                                    ShellUtils.execCommand("wm density 480", true);
                                }
                                if (position == 5) {
                                    ShellUtils.execCommand("wm density 520", true);
                                }
                            } else {
                                Alerter.create((Activity) context)
                                        .setTitle(R.string.无Root权限)
                                        .setText(R.string.没有Root权限)
                                        .setBackgroundColorInt(context.getResources().getColor(R.color.error))
                                        .show();
                            }
                        }
                    })
                    .setNegativeButton(R.string.恢复, (dialog, which) -> {
                        if (ShellUtils.checkRootPermission()) {
                            ShellUtils.execCommand("wm density reset", true);
                        } else {
                            Alerter.create((Activity) context)
                                    .setTitle(R.string.无Root权限)
                                    .setText(R.string.没有Root权限)
                                    .setBackgroundColorInt(context.getResources().getColor(R.color.error))
                                    .show();
                        }
                    })
                    .setNeutralButton(R.string.自定义, (dialog, which) -> {
                        final AlertDialog mDialog1 = new MaterialAlertDialogBuilder(context)
                                .create();
                        mDialog1.setTitle(R.string.自定义);
                        mDialog1.setMessage(context.getString(R.string.DPI提示));
                        View contentView = View.inflate(context, R.layout.dialog_edit,null);
                        mDialog1.setView(contentView);

                        MaterialButton button1 = contentView.findViewById(R.id.button1);
                        MaterialButton button2 = contentView.findViewById(R.id.button2);
                        TextInputLayout textInputLayout = contentView.findViewById(R.id.textInputLayout);
                        TextInputEditText textInputEditText = contentView.findViewById(R.id.textInputEditText);
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
                        button1.setBackgroundColor(context.getResources().getColor(R.color.itemBackColor));
                        button2.setText(R.string.修改);
                        button2.setBackgroundColor(context.getResources().getColor(R.color.zts));
                        button1.setOnClickListener(v -> {
                            mDialog1.dismiss();
                        });
                        button2.setOnClickListener(v -> {
                            if (TextUtils.isEmpty(textInputEditText.getText().toString())){
                                textInputLayout.setError(context.getString(R.string.请输入DPI值));
                                textInputLayout.setErrorEnabled(true);
                            } else {
                                mDialog1.dismiss();
                                if (ShellUtils.checkRootPermission()) {
                                    String shellcode = "wm density " + textInputEditText.getText();
                                    ShellUtils.execCommand(shellcode, true);
                                } else {
                                    Alerter.create((Activity) context)
                                            .setTitle(R.string.无Root权限)
                                            .setText(R.string.没有Root权限)
                                            .setBackgroundColorInt(context.getResources().getColor(R.color.error))
                                            .show();
                                }
                            }
                        });
                        mDialog1.show();
                        WindowManager.LayoutParams layoutParams = mDialog1.getWindow().getAttributes();
                        layoutParams.width = context.getResources().getDisplayMetrics().widthPixels / 10 * 9;
                        mDialog1.getWindow().setAttributes(layoutParams);
                    })
                    .setSingleChoiceItems(choices, 3, null)
                    .create();
            mDialog.setTitle(context.getString(R.string.高级重启));
            mDialog.show();
            WindowManager.LayoutParams layoutParams = mDialog.getWindow().getAttributes();
            layoutParams.width = context.getResources().getDisplayMetrics().widthPixels / 10 * 9;
            mDialog.getWindow().setAttributes(layoutParams);
        }
        if (name.equals(context.getString(R.string.电量伪装))) {
            final AlertDialog mDialog = new MaterialAlertDialogBuilder(context)
                    .create();
            mDialog.setTitle(R.string.电量伪装);
            mDialog.setMessage(context.getString(R.string.电量伪装内容));
            View contentView = View.inflate(context, R.layout.dialog_edit,null);
            mDialog.setView(contentView);
            MaterialButton button1 = contentView.findViewById(R.id.button1);
            MaterialButton button2 = contentView.findViewById(R.id.button2);
            TextInputLayout textInputLayout = contentView.findViewById(R.id.textInputLayout);
            TextInputEditText textInputEditText = contentView.findViewById(R.id.textInputEditText);
            textInputLayout.setHint(context.getString(R.string.请输入电量值));
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
            button1.setText(R.string.恢复);
            button1.setBackgroundColor(context.getResources().getColor(R.color.itemBackColor));
            button2.setText(R.string.修改);
            button2.setBackgroundColor(context.getResources().getColor(R.color.zts));
            button1.setOnClickListener(v -> {
                mDialog.dismiss();
                if (ShellUtils.checkRootPermission()) {
                    ShellUtils.execCommand("dumpsys battery reset", true);
                } else {
                    Alerter.create((Activity) context)
                            .setTitle(R.string.无Root权限)
                            .setText(R.string.没有Root权限)
                            .setBackgroundColorInt(context.getResources().getColor(R.color.error))
                            .show();
                }
            });
            button2.setOnClickListener(v -> {
                if (TextUtils.isEmpty(textInputEditText.getText().toString())){
                    textInputLayout.setError(context.getString(R.string.请输入电量值));
                    textInputLayout.setErrorEnabled(true);
                } else if (Integer.parseInt(String.valueOf(textInputEditText.getText())) == 0) {
                    textInputLayout.setError(context.getString(R.string.电量伪装提示));
                    textInputLayout.setErrorEnabled(true);
                } else {
                    mDialog.dismiss();
                    if (ShellUtils.checkRootPermission()) {
                        String shellcode = "dumpsys battery set level " + textInputEditText.getText();
                        ShellUtils.execCommand(shellcode, true);
                    } else {
                        Alerter.create((Activity) context)
                                .setTitle(R.string.无Root权限)
                                .setText(R.string.没有Root权限)
                                .setBackgroundColorInt(context.getResources().getColor(R.color.error))
                                .show();
                    }
                }
            });
            mDialog.show();
            WindowManager.LayoutParams layoutParams = mDialog.getWindow().getAttributes();
            layoutParams.width = context.getResources().getDisplayMetrics().widthPixels / 10 * 9;
            mDialog.getWindow().setAttributes(layoutParams);
        }
        if (name.equals(context.getString(R.string.隐藏状态栏导航栏))) {
            CharSequence[] choices = {context.getString(R.string.隐藏状态栏), context.getString(R.string.隐藏导航栏), context.getString(R.string.全部隐藏), context.getString(R.string.全部恢复)};
            final AlertDialog mDialog = new MaterialAlertDialogBuilder(context)
                    .setPositiveButton(R.string.确定, (dialog, which) -> {
                        int position = ((AlertDialog)dialog).getListView().getCheckedItemPosition();
                        if (position != AdapterView.INVALID_POSITION) {
                            if (ShellUtils.checkRootPermission()) {
                                if (position == 0) {
                                    ShellUtils.execCommand("settings put global policy_control immersive.status=*", true);
                                }
                                if (position == 1) {
                                    ShellUtils.execCommand("settings put global policy_control immersive.navigation=*", true);
                                }
                                if (position == 2) {
                                    ShellUtils.execCommand("settings put global policy_control immersive.full=*", true);
                                }
                                if (position == 3) {
                                    ShellUtils.execCommand("settings put global policy_control null", true);
                                }
                            } else {
                                Alerter.create((Activity) context)
                                        .setTitle(R.string.无Root权限)
                                        .setText(R.string.没有Root权限)
                                        .setBackgroundColorInt(context.getResources().getColor(R.color.error))
                                        .show();
                            }
                        }
                    })
                    .setNegativeButton(R.string.取消,null)
                    .setSingleChoiceItems(choices, 0, null)
                    .create();
            mDialog.setTitle(context.getString(R.string.状态栏导航栏));
            mDialog.show();
            WindowManager.LayoutParams layoutParams = mDialog.getWindow().getAttributes();
            layoutParams.width = context.getResources().getDisplayMetrics().widthPixels / 10 * 9;
            mDialog.getWindow().setAttributes(layoutParams);
        }
        if (name.equals(context.getString(R.string.提取手机壁纸))) {
            CharSequence[] choices = {context.getString(R.string.提取桌面壁纸),context.getString(R.string.提取锁屏壁纸)};
            final AlertDialog mDialog = new MaterialAlertDialogBuilder(context)
                    .setPositiveButton(R.string.确定, (dialog, which) -> {
                        int position = ((AlertDialog)dialog).getListView().getCheckedItemPosition();
                        if (position != AdapterView.INVALID_POSITION) {
                            LoadingDialog(context);
                            if (position == 0) {
                                try {
                                    new Thread((Runnable) () -> {
                                        @SuppressLint("SimpleDateFormat")
                                        String savedFile = SaveImage(context, Utils.getWallpaper_1(context), "/噬心工具箱/桌面壁纸/", "Image-" + new SimpleDateFormat("HH-mm-ss").format(new Date()) + ".png");
                                        if (savedFile != null) {
                                            MediaScannerConnection.scanFile((Activity) context, new String[]{savedFile}, null, (MediaScannerConnection.OnScanCompletedListener) (str, uri) -> {
                                                Intent intent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
                                                intent.setData(uri);
                                                ((Activity) context).sendBroadcast(intent);
                                                loadDialog.dismiss();
                                                Alerter.create((Activity) context)
                                                        .setTitle(R.string.保存成功)
                                                        .setText(R.string.已保存到 + savedFile)
                                                        .setBackgroundColorInt(context.getResources().getColor(R.color.success))
                                                        .show();
                                            });
                                        } else {
                                            loadDialog.dismiss();
                                        }
                                    }).start();
                                } catch (Exception e) {
                                    loadDialog.dismiss();
                                }
                            }
                            if (position == 1) {
                                try {
                                    new Thread((Runnable) () -> {
                                        @SuppressLint("SimpleDateFormat")
                                        String savedFile = SaveImage(context, Utils.getWallpaper_2(context), "/噬心工具箱/锁屏壁纸/", "Image-" + new SimpleDateFormat("HH-mm-ss").format(new Date()) + ".png");
                                        if (savedFile != null) {
                                            MediaScannerConnection.scanFile((Activity) context, new String[]{savedFile}, null, (MediaScannerConnection.OnScanCompletedListener) (str, uri) -> {
                                                Intent intent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
                                                intent.setData(uri);
                                                ((Activity) context).sendBroadcast(intent);
                                                loadDialog.dismiss();
                                                Alerter.create((Activity) context)
                                                        .setTitle(R.string.保存成功)
                                                        .setText(R.string.已保存到 + savedFile)
                                                        .setBackgroundColorInt(context.getResources().getColor(R.color.success))
                                                        .show();
                                            });
                                        } else {
                                            loadDialog.dismiss();
                                        }
                                    }).start();
                                } catch (Exception e) {
                                    loadDialog.dismiss();
                                }
                            }
                        }
                    })
                    .setNegativeButton(R.string.取消,null)
                    .setSingleChoiceItems(choices, 0, null)
                    .create();
            mDialog.setTitle(context.getString(R.string.提取手机壁纸));
            mDialog.show();
            WindowManager.LayoutParams layoutParams = mDialog.getWindow().getAttributes();
            layoutParams.width = context.getResources().getDisplayMetrics().widthPixels / 10 * 9;
            mDialog.getWindow().setAttributes(layoutParams);
        }
        if (name.equals(context.getString(R.string.查看设备信息))) {
            Utils.CopyDialog(context,context.getString(R.string.设备信息),Utils.getPhoneInfo(context));
        }
        if (name.equals(context.getString(R.string.WIFI密码查看))) {
            if (ShellUtils.checkRootPermission()) {
                context.startActivity(new Intent(context, WifiActivity.class));
            } else {
                Alerter.create((Activity) context)
                        .setTitle(R.string.无Root权限)
                        .setText(R.string.没有Root权限)
                        .setBackgroundColorInt(context.getResources().getColor(R.color.error))
                        .show();
            }
        }
        if (name.equals(context.getString(R.string.应用管理Apk提取))) {
            context.startActivity(new Intent(context, AppActivity.class));
        }
        if (name.equals(context.getString(R.string.屏幕坏点检测))) {
            context.startActivity(new Intent(context, ScreenActivity.class));
        }
        if (name.equals(context.getString(R.string.系统界面调节工具))) {
            try {
                Intent intent = new Intent();
                intent.setComponent(new ComponentName("com.android.systemui","com.android.systemui.DemoMode"));
                intent.setAction(Intent.ACTION_VIEW);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            } catch (Exception e) {
            }
        }
        if (name.equals(context.getString(R.string.桌面视频壁纸))) {
            context.startActivity(new Intent(context, VideoWallpaperActivity.class));
        }
        if (name.equals(context.getString(R.string.系统字体大小调节))) {
            context.startActivity(new Intent(context, FontSizeActivity.class));
        }
        if (name.equals(context.getString(R.string.振动器))) {
            final Vibrator vibrator = (Vibrator) context.getSystemService(Service.VIBRATOR_SERVICE);
            final AlertDialog mDialog = new MaterialAlertDialogBuilder(context)
                    .create();
            final View contentView = View.inflate(context, R.layout.dialog_vibration,null);
            mDialog.setView(contentView);
            final MaterialButton b1 = contentView.findViewById(R.id.button1);
            final MaterialButton b2 = contentView.findViewById(R.id.button2);

            b1.setOnClickListener(v -> vibrator.cancel());
            b2.setOnClickListener(v -> {
                long[] pattern = {100, 100, 100, 1000};
                vibrator.vibrate(pattern,0);
            });
            mDialog.setOnDismissListener(dialog -> vibrator.cancel());
            mDialog.show();
            WindowManager.LayoutParams layoutParams = mDialog.getWindow().getAttributes();
            layoutParams.width = context.getResources().getDisplayMetrics().widthPixels / 10 * 9;
            mDialog.getWindow().setAttributes(layoutParams);
        }
    }

    public static void item_3(Context context, String name) {
        if (name.equals(context.getString(R.string.图片取色))) {
            context.startActivity(new Intent(context, PictureColorActivity.class));
        }
        if (name.equals(context.getString(R.string.头像制作))) {
            context.startActivity(new Intent(context, AvatarMakeActivity.class));
        }
        if (name.equals(context.getString(R.string.图片清晰度提升))) {
            context.startActivity(new Intent(context, PictureClarityActivity.class));
        }
        if (name.equals(context.getString(R.string.黑白图上色))) {
            context.startActivity(new Intent(context, PictureColoringActivity.class));
        }
        if (name.equals(context.getString(R.string.以图搜图))) {
            context.startActivity(new Intent(context, SearchImageActivity.class));
        }
        if (name.equals(context.getString(R.string.GIF图片分解))) {
            context.startActivity(new Intent(context, GifActivity.class));
        }
        if (name.equals(context.getString(R.string.二维码生成))) {
            context.startActivity(new Intent(context, QRCodeActivity.class));
        }
        if (name.equals(context.getString(R.string.图片水印))) {
            context.startActivity(new Intent(context, PictureWaterActivity.class));
        }
        if (name.equals(context.getString(R.string.九宫格切图))) {
            context.startActivity(new Intent(context, PictureNineActivity.class));
        }
        if (name.equals(context.getString(R.string.纯色图制作))) {
            context.startActivity(new Intent(context, ColorPictureActivity.class));
        }
        if (name.equals(context.getString(R.string.隐藏图制作))) {
            context.startActivity(new Intent(context, PictureHideActivity.class));
        }
        if (name.equals(context.getString(R.string.图片文字化))) {
            context.startActivity(new Intent(context, PictureTextActivity.class));
        }
        if (name.equals(context.getString(R.string.图片像素化))) {
            context.startActivity(new Intent(context, PicturePixelActivity.class));
        }
        if (name.equals(context.getString(R.string.图片压缩))) {
            context.startActivity(new Intent(context, PictureCompressActivity.class));
        }
        if (name.equals(context.getString(R.string.图片转黑白))) {
            context.startActivity(new Intent(context, PictureGreyActivity.class));
        }
        if (name.equals(context.getString(R.string.毛玻璃图片生成))) {
            context.startActivity(new Intent(context, PictureBlurActivity.class));
        }
        if (name.equals(context.getString(R.string.LowPoly图片生成))) {
            context.startActivity(new Intent(context, PictureLowPolyActivity.class));
        }
        if (name.equals(context.getString(R.string.图片转素描图))) {
            context.startActivity(new Intent(context, PictureSketchActivity.class));
        }
        if (name.equals(context.getString(R.string.壁纸大全))) {
            context.startActivity(new Intent(context, WallpaperActivity.class));
        }
        if (name.equals(context.getString(R.string.王者荣耀图集))) {
            context.startActivity(new Intent(context, SgameActivity.class));
        }
        if (name.equals(context.getString(R.string.头像大全))) {
            context.startActivity(new Intent(context, AvatarActivity.class));
        }
    }

    public static void item_4(Context context, String name) {
        if (name.equals(context.getString(R.string.王者荣耀最低战力地区查询))) {
            context.startActivity(new Intent(context, PowerActivity.class));
        }
        if (name.equals(context.getString(R.string.IP查询地理位置))) {
            context.startActivity(new Intent(context, IpSiteActivity.class));
        }
        if (name.equals(context.getString(R.string.字典查询))) {
            context.startActivity(new Intent(context, DictionaryActivity.class));
        }
        if (name.equals(context.getString(R.string.手机号归属地查询))) {
            context.startActivity(new Intent(context, PhoneAttributionActivity.class));
        }
        if (name.equals(context.getString(R.string.成语词典))) {
            context.startActivity(new Intent(context, IdiomActivity.class));
        }
        if (name.equals(context.getString(R.string.音乐搜索器))) {
            context.startActivity(new Intent(context, MusicActivity.class));
        }
        if (name.equals(context.getString(R.string.缩写查询))) {
            context.startActivity(new Intent(context, AbbreviatedActivity.class));
        }
        if (name.equals(context.getString(R.string.表情包搜索))) {
            context.startActivity(new Intent(context, EmoticonActivity.class));
        }
        if (name.equals(context.getString(R.string.垃圾分类查询))) {
            context.startActivity(new Intent(context, RubbishActivity.class));
        }
        if (name.equals(context.getString(R.string.阿里图标库搜索))) {
            context.startActivity(new Intent(context, IconSearchActivity.class));
        }
    }

    public static void item_5(Context context, String name) {
        if (name.equals(context.getString(R.string.全名K歌音乐解析))) {
            context.startActivity(new Intent(context, KSongActivity.class));
        }
        if (name.equals(context.getString(R.string.网易云音乐解析))) {
            context.startActivity(new Intent(context, NeteaseSongActivity.class));
        }
        if (name.equals(context.getString(R.string.图片取直链))) {
            context.startActivity(new Intent(context, PictureUrlActivity.class));
        }
        if (name.equals(context.getString(R.string.视频提取音频))) {
            context.startActivity(new Intent(context, ExtractAudioActivity.class));
        }
        if (name.equals(context.getString(R.string.B站封面提取))) {
            context.startActivity(new Intent(context, BiliBiliActivity.class));
        }
        if (name.equals(context.getString(R.string.快手图集下载))) {
            context.startActivity(new Intent(context, GifmakerPictureActivity.class));
        }
        if (name.equals(context.getString(R.string.网页图片提取))) {
            context.startActivity(new Intent(context, WebPictureActivity.class));
        }
        if (name.equals(context.getString(R.string.蓝奏云直链提取))) {
            context.startActivity(new Intent(context, LanzouActivity.class));
        }
        if (name.equals(context.getString(R.string.短视频解析))) {
            context.startActivity(new Intent(context, DeWatermarkActivity.class));
        }
        if (name.equals(context.getString(R.string.抖音快手去水印))) {
            context.startActivity(new Intent(context, DeWatermark1Activity.class));
        }
    }

    public static void item_6(Context context, String name) {
        if (name.equals(context.getString(R.string.亲戚称呼计算))) {
            context.startActivity(new Intent(context, RelativeActivity.class));
        }
        if (name.equals(context.getString(R.string.进制转换))) {
            context.startActivity(new Intent(context, ConversionActivity.class));
        }
        if (name.equals(context.getString(R.string.日期计算器))) {
            context.startActivity(new Intent(context, DateCalculatorActivity.class));
        }
    }

    public static void item_7(Context context, String name) {
        if (name.equals(context.getString(R.string.拆字))) {
            context.startActivity(new Intent(context, SplitWordActivity.class));
        }
        if (name.equals(context.getString(R.string.摩斯电码))) {
            context.startActivity(new Intent(context, MorseCodeActivity.class));
        }
        if (name.equals(context.getString(R.string.Base64加解密))) {
            context.startActivity(new Intent(context, Base64Activity.class));
        }
        if (name.equals(context.getString(R.string.RC4加解密))) {
            context.startActivity(new Intent(context, RC4Activity.class));
        }
        if (name.equals(context.getString(R.string.数字转上下标))) {
            context.startActivity(new Intent(context, SmallDigitActivity.class));
        }
        if (name.equals(context.getString(R.string.特殊文本生成))) {
            context.startActivity(new Intent(context, SpecialTextActivity.class));
        }
        if (name.equals(context.getString(R.string.迷你英文生成))) {
            context.startActivity(new Intent(context, MiniEnglishActivity.class));
        }
        if (name.equals(context.getString(R.string.UTF8转码))) {
            context.startActivity(new Intent(context, Utf8Activity.class));
        }
    }

    public static void item_8(Context context, String name) {
        if (name.equals(context.getString(R.string.金属探测器))) {
            context.startActivity(new Intent(context, MetalDetectionActivity.class));
        }
        if (name.equals(context.getString(R.string.随机数生成))) {
            context.startActivity(new Intent(context, RandomActivity.class));
        }
        if (name.equals(context.getString(R.string.随机一文))) {
            context.startActivity(new Intent(context, RandomArticleActivity.class));
        }
        if (name.equals(context.getString(R.string.做决定))) {
            context.startActivity(new Intent(context, LuckyWheelActivity.class));
        }
        if (name.equals(context.getString(R.string.QQ临时会话))) {
            final AlertDialog mDialog = new MaterialAlertDialogBuilder(context)
                    .create();
            mDialog.setTitle(R.string.QQ临时会话);
            mDialog.setMessage(context.getString(R.string.QQ临时会话内容));
            View contentView = View.inflate(context, R.layout.dialog_edit,null);
            mDialog.setView(contentView);
            MaterialButton button1 = contentView.findViewById(R.id.button1);
            MaterialButton button2 = contentView.findViewById(R.id.button2);
            TextInputLayout textInputLayout = contentView.findViewById(R.id.textInputLayout);
            TextInputEditText textInputEditText = contentView.findViewById(R.id.textInputEditText);
            textInputLayout.setHint(R.string.请输入QQ帐号);
            textInputEditText.setInputType(EditorInfo.TYPE_CLASS_NUMBER);
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
            button1.setBackgroundColor(context.getResources().getColor(R.color.itemBackColor));
            button2.setText(R.string.确定);
            button2.setBackgroundColor(context.getResources().getColor(R.color.zts));
            button1.setOnClickListener(v -> {
                mDialog.dismiss();
            });
            button2.setOnClickListener(v -> {
                if (TextUtils.isEmpty(textInputEditText.getText())) {
                    textInputLayout.setError(context.getString(R.string.请输入QQ帐号));
                    textInputLayout.setErrorEnabled(true);
                } else {
                    mDialog.dismiss();
                    context.startActivity(new Intent("android.intent.action.VIEW", Uri.parse("mqqwpa://im/chat?chat_type=wpa&uin=" + textInputEditText.getText() + "&version=1")));
                }
            });
            mDialog.show();
            WindowManager.LayoutParams layoutParams = mDialog.getWindow().getAttributes();
            layoutParams.width = context.getResources().getDisplayMetrics().widthPixels / 10 * 9;
            mDialog.getWindow().setAttributes(layoutParams);
        }
        if (name.equals(context.getString(R.string.搜索隐藏QQ))) {
            final AlertDialog mDialog = new MaterialAlertDialogBuilder(context)
                    .create();
            mDialog.setTitle(R.string.搜索隐藏QQ);
            mDialog.setMessage(context.getString(R.string.搜索隐藏QQ内容));
            View contentView = View.inflate(context, R.layout.dialog_edit,null);
            mDialog.setView(contentView);
            MaterialButton button1 = contentView.findViewById(R.id.button1);
            MaterialButton button2 = contentView.findViewById(R.id.button2);
            TextInputLayout textInputLayout = contentView.findViewById(R.id.textInputLayout);
            TextInputEditText textInputEditText = contentView.findViewById(R.id.textInputEditText);
            textInputLayout.setHint(R.string.请输入QQ帐号);
            textInputEditText.setInputType(EditorInfo.TYPE_CLASS_NUMBER);
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
            button1.setBackgroundColor(context.getResources().getColor(R.color.itemBackColor));
            button2.setText(R.string.确定);
            button2.setBackgroundColor(context.getResources().getColor(R.color.zts));
            button1.setOnClickListener(v -> {
                mDialog.dismiss();
            });
            button2.setOnClickListener(v -> {
                if (TextUtils.isEmpty(textInputEditText.getText())) {
                    textInputLayout.setError(context.getString(R.string.请输入QQ帐号));
                    textInputLayout.setErrorEnabled(true);
                } else {
                    mDialog.dismiss();
                    context.startActivity(new Intent("android.intent.action.VIEW", Uri.parse("mqqapi://card/show_pslcard?src_type=internal&source=sharecard&version=1&uin=" + textInputEditText.getText())));
                }
            });
            mDialog.show();
            WindowManager.LayoutParams layoutParams = mDialog.getWindow().getAttributes();
            layoutParams.width = context.getResources().getDisplayMetrics().widthPixels / 10 * 9;
            mDialog.getWindow().setAttributes(layoutParams);
        }
        if (name.equals(context.getString(R.string.QQ单项好友管理))) {
            context.startActivity(new Intent("android.intent.action.VIEW", Uri.parse("mqqapi://forward/url?url_prefix=aHR0cHM6Ly90aS5xcS5jb20vZnJpZW5kcy91bmlkaXJlY3Rpb24/X3d2PTImX3d3dj0xMjgmdHVpbj0=")));
        }
        if (name.equals(context.getString(R.string.舔狗日记))) {
            if (!Utils.isVPNConnected(context)) {
                LoadingDialog(context);
                HttpRequest.build(context, "https://cloud.qqshabi.cn/api/tiangou/api.php")
                        .addHeaders("Charset", "UTF-8")
                        .setResponseListener(new ResponseListener() {
                            @Override
                            public void onResponse(String response, Exception error) {
                                try {
                                    loadDialog.dismiss();
                                    //map = new Gson().fromJson(response, new TypeToken<HashMap<String, Object>>() {}.getType());
                                    Utils.CopyDialog(context, context.getString(R.string.舔狗日记), response);
                                } catch (Exception e) {
                                }
                            }
                        }).doGet();
            }
        }
        if (name.equals(context.getString(R.string.随机笑话))) {
            if (!Utils.isVPNConnected(context)) {
                LoadingDialog(context);
                HttpRequest.build(context, "https://api.vvhan.com/api/xh")
                        .addHeaders("Charset", "UTF-8")
                        .setResponseListener(new ResponseListener() {
                            @Override
                            public void onResponse(String response, Exception error) {
                                try {
                                    loadDialog.dismiss();
                                    //map = new Gson().fromJson(response, new TypeToken<HashMap<String, Object>>() {}.getType());
                                    Utils.CopyDialog(context, context.getString(R.string.随机笑话), response);
                                } catch (Exception e) {
                                }
                            }
                        }).doGet();
            }
        }
        if (name.equals(context.getString(R.string.支付宝到账音效))) {
            final AlertDialog mDialog = new MaterialAlertDialogBuilder(context)
                    .create();
            mDialog.setTitle(R.string.支付宝到账音效);
            mDialog.setMessage(context.getString(R.string.支付宝到账内容));
            View contentView = View.inflate(context, R.layout.dialog_edit,null);
            mDialog.setView(contentView);
            MaterialButton button1 = contentView.findViewById(R.id.button1);
            MaterialButton button2 = contentView.findViewById(R.id.button2);
            TextInputLayout textInputLayout = contentView.findViewById(R.id.textInputLayout);
            TextInputEditText textInputEditText = contentView.findViewById(R.id.textInputEditText);
            textInputLayout.setHint(R.string.请输入金额数量);
            textInputEditText.setInputType(EditorInfo.TYPE_CLASS_NUMBER);
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
            button1.setBackgroundColor(context.getResources().getColor(R.color.itemBackColor));
            button2.setText(R.string.确定);
            button2.setBackgroundColor(context.getResources().getColor(R.color.zts));
            button1.setOnClickListener(v -> {
                mDialog.dismiss();
            });
            button2.setOnClickListener(v -> {
                if (TextUtils.isEmpty(textInputEditText.getText())) {
                    textInputLayout.setError(context.getString(R.string.请输入金额数量));
                    textInputLayout.setErrorEnabled(true);
                } else {
                    mDialog.dismiss();
                    String Url = "https://mm.cqu.cc/share/zhifubaodaozhang/mp3/" + textInputEditText.getText() + ".mp3";
                    LoadingDialog(context);
                    try {
                        mediaPlayer.reset();
                        mediaPlayer.setDataSource(Url);
                        mediaPlayer.prepareAsync();
                        mediaPlayer.setOnPreparedListener(media_player -> {
                            loadDialog.dismiss();
                            mediaPlayer.start();
                        });
                        mediaPlayer.setOnCompletionListener(media_player -> {
                        });
                    } catch(Exception e) {
                        loadDialog.dismiss();
                    }
                }
            });
            mDialog.show();
            WindowManager.LayoutParams layoutParams = mDialog.getWindow().getAttributes();
            layoutParams.width = context.getResources().getDisplayMetrics().widthPixels / 10 * 9;
            mDialog.getWindow().setAttributes(layoutParams);
        }
    }

    public static void item_jz(Context context, String name) {
        if (name.equals(context.getString(R.string.捐赠榜单))) {
            context.startActivity(new Intent(context, DonationListActivity.class));
        }
        if (name.equals(context.getString(R.string.支付宝))) {
            final AlertDialog Dialog = new MaterialAlertDialogBuilder(context)
                    .create();
            final View view = View.inflate(context, R.layout.dialog_jzt,null);
            Dialog.setView(view);
            Dialog.show();
            final ImageView img = view.findViewById(R.id.tp1);
            img.setImageResource(R.drawable.zfb);
            WindowManager.LayoutParams layoutParams = Dialog.getWindow().getAttributes();
            layoutParams.width = context.getResources().getDisplayMetrics().widthPixels / 10 * 8;
            Dialog.getWindow().setAttributes(layoutParams);
        }
        if (name.equals(context.getString(R.string.QQ))) {
            final AlertDialog Dialog = new MaterialAlertDialogBuilder(context)
                    .create();
            final View view = View.inflate(context, R.layout.dialog_jzt,null);
            Dialog.setView(view);
            Dialog.show();
            final ImageView img = view.findViewById(R.id.tp1);
            img.setImageResource(R.drawable.qqq);
            WindowManager.LayoutParams layoutParams = Dialog.getWindow().getAttributes();
            layoutParams.width = context.getResources().getDisplayMetrics().widthPixels / 10 * 8;
            Dialog.getWindow().setAttributes(layoutParams);
        }
        if (name.equals(context.getString(R.string.微信))) {
            final AlertDialog Dialog = new MaterialAlertDialogBuilder(context)
                    .create();
            final View view = View.inflate(context, R.layout.dialog_jzt,null);
            Dialog.setView(view);
            Dialog.show();
            final ImageView img = view.findViewById(R.id.tp1);
            img.setImageResource(R.drawable.weixin);
            WindowManager.LayoutParams layoutParams = Dialog.getWindow().getAttributes();
            layoutParams.width = context.getResources().getDisplayMetrics().widthPixels / 10 * 8;
            Dialog.getWindow().setAttributes(layoutParams);
        }
    }

    public static void item_yellow(Context context, String name) {
        if (name.contains("十八") || name.contains("18")) {
            LoadingDialog(context);
            HttpRequest.build(context, "https://gitee.com/alex12075/ToolsBox/raw/master/config.json")
                    .addHeaders("Charset","UTF-8")
                    .setResponseListener(new ResponseListener() {
                        @Override
                        public void onResponse(String response, Exception error) {
                            try {
                                loadDialog.dismiss();
                                HashMap<String, Object> map = new Gson().fromJson(response, new TypeToken<HashMap<String, Object>>(){}.getType());
                                if (!String.valueOf(map.get("功能密码")).equals("关闭")) {

                                }
                                final AlertDialog mDialog = new MaterialAlertDialogBuilder(context)
                                        .create();
                                mDialog.setTitle("功能被锁定");
                                mDialog.setMessage("您需要输入密码后才能使用该功能");
                                View contentView = View.inflate(context, R.layout.dialog_edit,null);
                                mDialog.setView(contentView);
                                MaterialButton button1 = contentView.findViewById(R.id.button1);
                                MaterialButton button2 = contentView.findViewById(R.id.button2);
                                TextInputLayout textInputLayout = contentView.findViewById(R.id.textInputLayout);
                                TextInputEditText textInputEditText = contentView.findViewById(R.id.textInputEditText);
                                textInputLayout.setHint("请输入密码");
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
                                button1.setText(R.string.获取);
                                button1.setBackgroundColor(context.getResources().getColor(R.color.itemBackColor));
                                button2.setText(R.string.确定);
                                button2.setBackgroundColor(context.getResources().getColor(R.color.zts));
                                button1.setOnClickListener(v -> {
                                    Utils.joinQQGroup(context, (String) map.get("群KEY"));
                                });
                                button2.setOnClickListener(v -> {
                                    if (TextUtils.isEmpty(textInputEditText.getText())){
                                        textInputLayout.setError("请输入密码");
                                        textInputLayout.setErrorEnabled(true);
                                    } else {
                                        try {
                                            if (String.valueOf(textInputEditText.getText()).equals(RC4Activity.RC4Util.decryRC4(String.valueOf(map.get("功能密码")), "xiao","GBK"))){
                                                mDialog.dismiss();
                                                context.startActivity(new Intent(context, YellowActivity.class));
                                            } else {
                                                textInputLayout.setError("密码错误");
                                                textInputLayout.setErrorEnabled(true);
                                            }
                                        } catch (UnsupportedEncodingException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                                mDialog.show();
                                WindowManager.LayoutParams layoutParams = mDialog.getWindow().getAttributes();
                                layoutParams.width = context.getResources().getDisplayMetrics().widthPixels / 10 * 9;
                                mDialog.getWindow().setAttributes(layoutParams);
                            } catch (Exception e){
                            }
                        }
                    }).doGet();

        }
    }

    public static void item_vip(Context context, String name) {
        LoadingDialog(context);
        HttpRequest.build(context, "https://gitee.com/alex12075/ToolsBox/raw/master/config.json")
                .addHeaders("Charset","UTF-8")
                .setResponseListener(new ResponseListener() {
                    @Override
                    public void onResponse(String response, Exception error) {
                        try {
                            loadDialog.dismiss();
                            if (RC4Activity.RC4Util.decryRC4(response, "xiao","GBK").contains(String.valueOf(Build.TIME))) {
                                if (name.contains("功能名称")) {
                                    context.startActivity(new Intent(context, DonationListActivity.class));
                                }
                            } else {
                                final AlertDialog mDialog = new MaterialAlertDialogBuilder(context)
                                        .create();
                                mDialog.setTitle("温馨提示");
                                mDialog.setMessage("抱歉，此功能需要开通VIP后才能够正常使用");
                                View contentView = View.inflate(context, R.layout.dialog_edit,null);
                                mDialog.setView(contentView);
                                MaterialButton button1 = contentView.findViewById(R.id.button1);
                                MaterialButton button2 = contentView.findViewById(R.id.button2);
                                TextInputLayout textInputLayout = contentView.findViewById(R.id.textInputLayout);
                                textInputLayout.setVisibility(View.GONE);
                                button1.setText(R.string.取消);
                                button1.setBackgroundColor(context.getResources().getColor(R.color.itemBackColor));
                                button2.setText(R.string.确定);
                                button2.setBackgroundColor(context.getResources().getColor(R.color.zts));
                                button1.setOnClickListener(v -> {
                                    mDialog.dismiss();
                                });
                                button2.setOnClickListener(v -> {

                                });
                                mDialog.show();
                                WindowManager.LayoutParams layoutParams = mDialog.getWindow().getAttributes();
                                layoutParams.width = context.getResources().getDisplayMetrics().widthPixels / 10 * 9;
                                mDialog.getWindow().setAttributes(layoutParams);
                            }
                        } catch (Exception e){
                        }
                    }
                }).doGet();
    }

    /*
    public static void item_other(Context context, String name) {
        if (name.equals("更新日志")) {
            LoadingDialog(context);
            HttpRequest.build(context, "https://gitee.com/x1602965165/DaiMeng/raw/master/update_log")
                    .addHeaders("Charset", "UTF-8")
                    .setResponseListener(new ResponseListener() {
                        @Override
                        public void onResponse(String response, Exception error) {
                            loadDialog.dismiss();
                            try {
                                TextDialog(context, "更新日志", response);
                            } catch (Exception e) {
                            }
                        }
                    }).doGet();
        }
        if (name.equals("官方交流群")) {
            LoadingDialog(context);
            HttpRequest.build(context, "https://gitee.com/alex12075/ToolsBox/raw/master/config.json")
                    .addHeaders("Charset", "UTF-8")
                    .setResponseListener(new ResponseListener() {
                        @Override
                        public void onResponse(String response, Exception error) {
                            loadDialog.dismiss();
                            try {
                                try {
                                    HashMap<String, Object> key = new Gson().fromJson(response, new TypeToken<HashMap<String, Object>>(){}.getType());
                                    Utils.joinQQGroup(context, (String) key.get("群KEY"));
                                } catch (Exception e){
                                }
                            } catch (Exception e) {
                            }
                        }
                    }).doGet();
        }
        if (name.equals("检查更新")) {
            upData(context);
        }
        if (name.equals("支付宝捐赠")) {
            final AlertDialog Dialog = new AlertDialog.Builder(context)
                    .create();
            final View view = View.inflate(context, R.layout.dialog_jzt,null);
            Dialog.setView(view);
            Dialog.show();
            final ImageView img = view.findViewById(R.id.tp1);
            img.setImageResource(R.drawable.zfb);
            WindowManager.LayoutParams layoutParams = Dialog.getWindow().getAttributes();
            layoutParams.width = context.getResources().getDisplayMetrics().widthPixels/5*4;
            Dialog.getWindow().setAttributes(layoutParams);
        }
        if (name.equals("微信捐赠")) {
            final AlertDialog Dialog = new AlertDialog.Builder(context)
                    .create();
            final View view = View.inflate(context, R.layout.dialog_jzt,null);
            Dialog.setView(view);
            Dialog.show();
            final ImageView img = view.findViewById(R.id.tp1);
            img.setImageResource(R.drawable.weixin);
            WindowManager.LayoutParams layoutParams = Dialog.getWindow().getAttributes();
            layoutParams.width = context.getResources().getDisplayMetrics().widthPixels/5*4;
            Dialog.getWindow().setAttributes(layoutParams);
        }
        if (name.equals("QQ捐赠")) {
            final AlertDialog Dialog = new AlertDialog.Builder(context)
                    .create();
            final View view = View.inflate(context, R.layout.dialog_jzt,null);
            Dialog.setView(view);
            Dialog.show();
            final ImageView img = view.findViewById(R.id.tp1);
            img.setImageResource(R.drawable.qqq);
            WindowManager.LayoutParams layoutParams = Dialog.getWindow().getAttributes();
            layoutParams.width = context.getResources().getDisplayMetrics().widthPixels/5*4;
            Dialog.getWindow().setAttributes(layoutParams);
        }
        if (name.equals("捐赠榜单")) {
            Toast.makeText(context,"敬请期待", Toast.LENGTH_SHORT).show();
        }
    }
     */
}
