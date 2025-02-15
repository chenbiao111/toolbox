package com.shixin.app.fragment;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.transition.ChangeBounds;
import androidx.transition.TransitionManager;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.shixin.app.R;
import com.shixin.app.click.itemOnClick;
import com.shixin.app.widget.AutoFlowLayout;
import com.shixin.app.widget.FlowAdapter;


import java.util.Arrays;

public class Home2Fragment extends Fragment {

    private ViewGroup root;

    private SharedPreferences sp;
    private ImageView img1;
    private ImageView img2;
    private ImageView img3;
    private ImageView img4;
    private ImageView img5;
    private ImageView img6;
    private ImageView img7;
    private ImageView img8;
    private AutoFlowLayout flow1;
    private AutoFlowLayout flow2;
    private AutoFlowLayout flow3;
    private AutoFlowLayout flow4;
    private AutoFlowLayout flow5;
    private AutoFlowLayout flow6;
    private AutoFlowLayout flow7;
    private AutoFlowLayout flow8;
    private MaterialCardView card1;
    private MaterialCardView card2;
    private MaterialCardView card3;
    private MaterialCardView card4;
    private MaterialCardView card5;
    private MaterialCardView card6;
    private MaterialCardView card7;
    private MaterialCardView card8;

    public final static String mData1 = "刻度尺,指南针,水平仪,量角器,Google翻译,网页获源,短网址生成,简易画板,LED手机字幕,时间屏幕,历史上的今天,每日60秒早报,VIP影视解析,IPTV电视直播,直播中国,恋爱话术-撩妹,QQ变音";
    public final static String mData2 = "应用管理(Apk提取),电量伪装,DPI修改,高级重启,隐藏状态栏/导航栏,WIFI密码查看,振动器,提取手机壁纸,屏幕坏点检测,查看设备信息,桌面视频壁纸,系统字体大小调节,系统界面调节工具";
    public final static String mData3 = "二维码生成,图片水印,图片取色,九宫格切图,纯色图制作,隐藏图制作,图片文字化,图片像素化,图片压缩,图片转黑白,毛玻璃图片生成,LowPoly图片生成,图片转素描图,GIF图片分解,壁纸大全,头像大全,王者荣耀图集,以图搜图,黑白图上色,图片清晰度提升,头像制作";
    public final static String mData4 = "王者荣耀最低战力地区查询,垃圾分类查询,成语词典,字典查询,音乐搜索器,缩写查询,表情包搜索,阿里图标库搜索,IP查询地理位置,手机号归属地查询";
    public final static String mData5 = "图片取直链,视频提取音频,B站封面提取,短视频解析/去水印,抖音/快手去水印,快手图集下载,网页图片提取,蓝奏云直链提取,全名K歌音乐解析,网易云音乐解析";
    public final static String mData6 = "亲戚称呼计算,进制转换,日期计算器";
    public final static String mData7 = "拆字,摩斯电码,Base64加解密,RC4加解密,数字转上下标,特殊文本生成,迷你英文生成,UTF-8转码";
    public final static String mData8 = "QQ临时会话,搜索隐藏QQ,QQ单项好友管理,舔狗日记,随机笑话,支付宝到账音效,金属探测器,随机一文,随机数生成,做决定/转盘";

    public static Home2Fragment newInstance() {
        return new Home2Fragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home2, container, false);

        root = view.findViewById(R.id.root);
        sp = getContext().getSharedPreferences("sp", Activity.MODE_PRIVATE);


        flow1 = view.findViewById(R.id.flow1);
        flow2 = view.findViewById(R.id.flow2);
        flow3 = view.findViewById(R.id.flow3);
        flow4 = view.findViewById(R.id.flow4);
        flow5 = view.findViewById(R.id.flow5);
        flow6 = view.findViewById(R.id.flow6);
        flow7 = view.findViewById(R.id.flow7);
        flow8 = view.findViewById(R.id.flow8);
        AutoFlowLayout_1();
        AutoFlowLayout_2();
        AutoFlowLayout_3();
        AutoFlowLayout_4();
        AutoFlowLayout_5();
        AutoFlowLayout_6();
        AutoFlowLayout_7();
        AutoFlowLayout_8();

        img1 = view.findViewById(R.id.img1);
        img2 = view.findViewById(R.id.img2);
        img3 = view.findViewById(R.id.img3);
        img4 = view.findViewById(R.id.img4);
        img5 = view.findViewById(R.id.img5);
        img6 = view.findViewById(R.id.img6);
        img7 = view.findViewById(R.id.img7);
        img8 = view.findViewById(R.id.img8);
        Visible();

        card1 = view.findViewById(R.id.card1);
        card2 = view.findViewById(R.id.card2);
        card3 = view.findViewById(R.id.card3);
        card4 = view.findViewById(R.id.card4);
        card5 = view.findViewById(R.id.card5);
        card6 = view.findViewById(R.id.card6);
        card7 = view.findViewById(R.id.card7);
        card8 = view.findViewById(R.id.card8);
        Visible_bt();

        return view;
    }

    public void AutoFlowLayout_1(){
        flow1.setAdapter(new FlowAdapter(Arrays.asList(mData1.split(","))) {
            @Override
            public View getView(final int position) {
                View item = getLayoutInflater().inflate(R.layout.item_gn, null);
                Chip chip = item.findViewById(R.id.chip);
                chip.setText(mData1.split(",")[position]);
                chip.setOnClickListener(v -> itemOnClick.item_1(v.getContext(), mData1.split(",")[position]));
                chip.setOnLongClickListener(v -> {

                    return false;
                });
                return item;
            }
        });
    }

    public void AutoFlowLayout_2(){
        flow2.setAdapter(new FlowAdapter(Arrays.asList(mData2.split(","))) {
            @Override
            public View getView(final int position) {
                View item = getLayoutInflater().inflate(R.layout.item_gn, null);
                Chip chip = item.findViewById(R.id.chip);
                chip.setText(mData2.split(",")[position]);
                chip.setOnClickListener(v -> itemOnClick.item_2(v.getContext(),mData2.split(",")[position]));
                chip.setOnLongClickListener(v -> {

                    return false;
                });
                return item;
            }
        });
    }

    public void AutoFlowLayout_3(){
        flow3.setAdapter(new FlowAdapter(Arrays.asList(mData3.split(","))) {
            @Override
            public View getView(final int position) {
                View item = getLayoutInflater().inflate(R.layout.item_gn, null);
                Chip chip = item.findViewById(R.id.chip);
                chip.setText(mData3.split(",")[position]);
                chip.setOnClickListener(v -> itemOnClick.item_3(v.getContext(),mData3.split(",")[position]));
                chip.setOnLongClickListener(v -> {

                    return false;
                });
                return item;
            }
        });
    }

    public void AutoFlowLayout_4(){
        flow4.setAdapter(new FlowAdapter(Arrays.asList(mData4.split(","))) {
            @Override
            public View getView(final int position) {
                View item = getLayoutInflater().inflate(R.layout.item_gn, null);
                Chip chip = item.findViewById(R.id.chip);
                chip.setText(mData4.split(",")[position]);
                chip.setOnClickListener(v -> itemOnClick.item_4(v.getContext(),mData4.split(",")[position]));
                chip.setOnLongClickListener(v -> {

                    return false;
                });
                return item;
            }
        });
    }

    public void AutoFlowLayout_5(){
        flow5.setAdapter(new FlowAdapter(Arrays.asList(mData5.split(","))) {
            @Override
            public View getView(final int position) {
                View item = getLayoutInflater().inflate(R.layout.item_gn, null);
                Chip chip = item.findViewById(R.id.chip);
                chip.setText(mData5.split(",")[position]);
                chip.setOnClickListener(v -> itemOnClick.item_5(v.getContext(),mData5.split(",")[position]));
                chip.setOnLongClickListener(v -> {

                    return false;
                });
                return item;
            }
        });
    }

    public void AutoFlowLayout_6(){
        flow6.setAdapter(new FlowAdapter(Arrays.asList(mData6.split(","))) {
            @Override
            public View getView(final int position) {
                View item = getLayoutInflater().inflate(R.layout.item_gn, null);
                Chip chip = item.findViewById(R.id.chip);
                chip.setText(mData6.split(",")[position]);
                chip.setOnClickListener(v -> itemOnClick.item_6(v.getContext(),mData6.split(",")[position]));
                chip.setOnLongClickListener(v -> {

                    return false;
                });
                return item;
            }
        });
    }

    public void AutoFlowLayout_7(){
        flow7.setAdapter(new FlowAdapter(Arrays.asList(mData7.split(","))) {
            @Override
            public View getView(final int position) {
                View item = getLayoutInflater().inflate(R.layout.item_gn, null);
                Chip chip = item.findViewById(R.id.chip);
                chip.setText(mData7.split(",")[position]);
                chip.setOnClickListener(v -> itemOnClick.item_7(v.getContext(),mData7.split(",")[position]));
                chip.setOnLongClickListener(v -> {

                    return false;
                });
                return item;
            }
        });
    }

    public void AutoFlowLayout_8(){
        flow8.setAdapter(new FlowAdapter(Arrays.asList(mData8.split(","))) {
            @Override
            public View getView(final int position) {
                View item = getLayoutInflater().inflate(R.layout.item_gn, null);
                Chip chip = item.findViewById(R.id.chip);
                chip.setText(mData8.split(",")[position]);
                chip.setOnClickListener(v -> itemOnClick.item_8(v.getContext(),mData8.split(",")[position]));
                chip.setOnLongClickListener(v -> {

                    return false;
                });
                return item;
            }
        });
    }

    public void Visible() {
        if (sp.getBoolean("1", true)) {
            flow1.setVisibility(View.VISIBLE);
            ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(img1, "rotation", 0f, 90f);
            objectAnimator.setDuration(0);
            objectAnimator.start();
            sp.edit().putBoolean("1", true).apply();
        } else {
            flow1.setVisibility(View.GONE);
            ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(img1, "rotation", 90f, 0f);
            objectAnimator.setDuration(0);
            objectAnimator.start();
            sp.edit().putBoolean("1", false).apply();
        }
        if (sp.getBoolean("2", true)) {
            flow2.setVisibility(View.VISIBLE);
            ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(img2, "rotation", 0f, 90f);
            objectAnimator.setDuration(0);
            objectAnimator.start();
            sp.edit().putBoolean("2", true).apply();
        } else {
            flow2.setVisibility(View.GONE);
            ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(img2, "rotation", 90f, 0f);
            objectAnimator.setDuration(0);
            objectAnimator.start();
            sp.edit().putBoolean("2", false).apply();
        }
        if (sp.getBoolean("3", true)) {
            flow3.setVisibility(View.VISIBLE);
            ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(img3, "rotation", 0f, 90f);
            objectAnimator.setDuration(0);
            objectAnimator.start();
            sp.edit().putBoolean("3", true).apply();
        } else {
            flow3.setVisibility(View.GONE);
            ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(img3, "rotation", 90f, 0f);
            objectAnimator.setDuration(0);
            objectAnimator.start();
            sp.edit().putBoolean("3", false).apply();
        }
        if (sp.getBoolean("4", true)) {
            flow4.setVisibility(View.VISIBLE);
            ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(img4, "rotation", 0f, 90f);
            objectAnimator.setDuration(0);
            objectAnimator.start();
            sp.edit().putBoolean("4", true).apply();
        } else {
            flow4.setVisibility(View.GONE);
            ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(img4, "rotation", 90f, 0f);
            objectAnimator.setDuration(0);
            objectAnimator.start();
            sp.edit().putBoolean("4", false).apply();
        }
        if (sp.getBoolean("5", true)) {
            flow5.setVisibility(View.VISIBLE);
            ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(img5, "rotation", 0f, 90f);
            objectAnimator.setDuration(0);
            objectAnimator.start();
            sp.edit().putBoolean("5", true).apply();
        } else {
            flow5.setVisibility(View.GONE);
            ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(img5, "rotation", 90f, 0f);
            objectAnimator.setDuration(0);
            objectAnimator.start();
            sp.edit().putBoolean("5", false).apply();
        }
        if (sp.getBoolean("6", true)) {
            flow6.setVisibility(View.VISIBLE);
            ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(img6, "rotation", 0f, 90f);
            objectAnimator.setDuration(0);
            objectAnimator.start();
            sp.edit().putBoolean("6", true).apply();
        } else {
            flow6.setVisibility(View.GONE);
            ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(img6, "rotation", 90f, 0f);
            objectAnimator.setDuration(0);
            objectAnimator.start();
            sp.edit().putBoolean("6", false).apply();
        }
        if (sp.getBoolean("7", true)) {
            flow7.setVisibility(View.VISIBLE);
            ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(img7, "rotation", 0f, 90f);
            objectAnimator.setDuration(0);
            objectAnimator.start();
            sp.edit().putBoolean("7", true).apply();
        } else {
            flow7.setVisibility(View.GONE);
            ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(img7, "rotation", 90f, 0f);
            objectAnimator.setDuration(0);
            objectAnimator.start();
            sp.edit().putBoolean("7", false).apply();
        }
        if (sp.getBoolean("8", true)) {
            flow8.setVisibility(View.VISIBLE);
            ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(img8, "rotation", 0f, 90f);
            objectAnimator.setDuration(0);
            objectAnimator.start();
            sp.edit().putBoolean("8", true).apply();
        } else {
            flow8.setVisibility(View.GONE);
            ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(img8, "rotation", 90f, 0f);
            objectAnimator.setDuration(0);
            objectAnimator.start();
            sp.edit().putBoolean("8", false).apply();
        }
    }

    public void Visible_bt() {
        card1.setOnClickListener(v -> {
            if (sp.getBoolean("1", true)) {
                TransitionManager.beginDelayedTransition(root, new ChangeBounds());
                flow1.setVisibility(View.GONE);
                ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(img1, "rotation", 90f, 0f);
                objectAnimator.setDuration(400);
                objectAnimator.start();
                sp.edit().putBoolean("1", false).apply();
            } else {
                TransitionManager.beginDelayedTransition(root, new ChangeBounds());
                flow1.setVisibility(View.VISIBLE);
                ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(img1, "rotation", 0f, 90f);
                objectAnimator.setDuration(400);
                objectAnimator.start();
                sp.edit().putBoolean("1", true).apply();
            }
        });
        card2.setOnClickListener(v -> {
            if (sp.getBoolean("2", true)) {
                TransitionManager.beginDelayedTransition(root, new ChangeBounds());
                flow2.setVisibility(View.GONE);
                ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(img2, "rotation", 90f, 0f);
                objectAnimator.setDuration(400);
                objectAnimator.start();
                sp.edit().putBoolean("2", false).apply();
            } else {
                TransitionManager.beginDelayedTransition(root, new ChangeBounds());
                flow2.setVisibility(View.VISIBLE);
                ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(img2, "rotation", 0f, 90f);
                objectAnimator.setDuration(400);
                objectAnimator.start();
                sp.edit().putBoolean("2", true).apply();
            }
        });
        card3.setOnClickListener(v -> {
            if (sp.getBoolean("3", true)) {
                TransitionManager.beginDelayedTransition(root, new ChangeBounds());
                flow3.setVisibility(View.GONE);
                ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(img3, "rotation", 90f, 0f);
                objectAnimator.setDuration(400);
                objectAnimator.start();
                sp.edit().putBoolean("3", false).apply();
            } else {
                TransitionManager.beginDelayedTransition(root, new ChangeBounds());
                flow3.setVisibility(View.VISIBLE);
                ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(img3, "rotation", 0f, 90f);
                objectAnimator.setDuration(400);
                objectAnimator.start();
                sp.edit().putBoolean("3", true).apply();
            }
        });
        card4.setOnClickListener(v -> {
            if (sp.getBoolean("4", true)) {
                TransitionManager.beginDelayedTransition(root, new ChangeBounds());
                flow4.setVisibility(View.GONE);
                ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(img4, "rotation", 90f, 0f);
                objectAnimator.setDuration(400);
                objectAnimator.start();
                sp.edit().putBoolean("4", false).apply();
            } else {
                TransitionManager.beginDelayedTransition(root, new ChangeBounds());
                flow4.setVisibility(View.VISIBLE);
                ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(img4, "rotation", 0f, 90f);
                objectAnimator.setDuration(400);
                objectAnimator.start();
                sp.edit().putBoolean("4", true).apply();
            }
        });
        card5.setOnClickListener(v -> {
            if (sp.getBoolean("5", true)) {
                TransitionManager.beginDelayedTransition(root, new ChangeBounds());
                flow5.setVisibility(View.GONE);
                ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(img5, "rotation", 90f, 0f);
                objectAnimator.setDuration(400);
                objectAnimator.start();
                sp.edit().putBoolean("5", false).apply();
            } else {
                TransitionManager.beginDelayedTransition(root, new ChangeBounds());
                flow5.setVisibility(View.VISIBLE);
                ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(img5, "rotation", 0f, 90f);
                objectAnimator.setDuration(400);
                objectAnimator.start();
                sp.edit().putBoolean("5", true).apply();
            }
        });
        card6.setOnClickListener(v -> {
            if (sp.getBoolean("6", true)) {
                TransitionManager.beginDelayedTransition(root, new ChangeBounds());
                flow6.setVisibility(View.GONE);
                ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(img6, "rotation", 90f, 0f);
                objectAnimator.setDuration(400);
                objectAnimator.start();
                sp.edit().putBoolean("6", false).apply();
            } else {
                TransitionManager.beginDelayedTransition(root, new ChangeBounds());
                flow6.setVisibility(View.VISIBLE);
                ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(img6, "rotation", 0f, 90f);
                objectAnimator.setDuration(400);
                objectAnimator.start();
                sp.edit().putBoolean("6", true).apply();
            }
        });
        card7.setOnClickListener(v -> {
            if (sp.getBoolean("7", true)) {
                TransitionManager.beginDelayedTransition(root, new ChangeBounds());
                flow7.setVisibility(View.GONE);
                ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(img7, "rotation", 90f, 0f);
                objectAnimator.setDuration(400);
                objectAnimator.start();
                sp.edit().putBoolean("7", false).apply();
            } else {
                TransitionManager.beginDelayedTransition(root, new ChangeBounds());
                flow7.setVisibility(View.VISIBLE);
                ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(img7, "rotation", 0f, 90f);
                objectAnimator.setDuration(400);
                objectAnimator.start();
                sp.edit().putBoolean("7", true).apply();
            }
        });
        card8.setOnClickListener(v -> {
            if (sp.getBoolean("8", true)) {
                TransitionManager.beginDelayedTransition(root, new ChangeBounds());
                flow8.setVisibility(View.GONE);
                ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(img8, "rotation", 90f, 0f);
                objectAnimator.setDuration(400);
                objectAnimator.start();
                sp.edit().putBoolean("8", false).apply();
            } else {
                TransitionManager.beginDelayedTransition(root, new ChangeBounds());
                flow8.setVisibility(View.VISIBLE);
                ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(img8, "rotation", 0f, 90f);
                objectAnimator.setDuration(400);
                objectAnimator.start();
                sp.edit().putBoolean("8", true).apply();
            }
        });
    }
}