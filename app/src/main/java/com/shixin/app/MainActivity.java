package com.shixin.app;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.gyf.immersionbar.ImmersionBar;
import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.kongzue.baseokhttp.HttpRequest;
import com.kongzue.baseokhttp.listener.ResponseListener;
import com.shixin.app.adapter.MainPagerAdapter;
import com.shixin.app.utils.Utils;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.shixin.app.utils.Utils.LoadingDialog;
import static com.shixin.app.utils.Utils.dp2px;
import static com.shixin.app.utils.Utils.loadDialog;
import static com.shixin.app.utils.Utils.setRipple;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @BindView(R.id.drawer_root)
    LinearLayout drawer_root;
    @BindView(R.id.tabs)
    TabLayout tabs;
    @BindView(R.id.view_pager)
    ViewPager viewPager;

    private boolean isChangingTheme = false;
    protected boolean isDarkTheme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        ImmersionBar.with(this)
                .fitsSystemWindows(true)
                .statusBarColor(R.color.appbarColor)
                .navigationBarColor(R.color.backgroundColor)
                .autoDarkModeEnable(true)
                //.keyboardEnable(true)
                //.keyboardMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
                .init();

        toolbar.setTitle(getString(R.string.app_name));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setSubtitle("欢迎使用噬心工具箱");
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.app_name,R.string.app_name);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        //drawerLayout.setScrimColor(Color.TRANSPARENT);
        //drawerLayout.setDrawerElevation(dp2px(this,8));
        drawer_root.getLayoutParams().width = getResources().getDisplayMetrics().widthPixels/3*2;
        //drawer_root.setPadding(0,ImmersionBar.getStatusBarHeight(this),0,ImmersionBar.getNavigationBarHeight(this));
        drawerLayout.setScrimColor(Color.TRANSPARENT);
        drawerLayout.setDrawerElevation(dp2px(this,8));
        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                //滑动过程中不断回调 slideOffset:0~1
                View content = drawerLayout.getChildAt(0);
                View menu = drawerView;
                float scale = 1 - slideOffset;//1~0
                content.setTranslationX(menu.getMeasuredWidth() * (1 - scale));//0~width
            }

            @Override
            public void onDrawerOpened(View drawerView) {
            }
            @Override
            public void onDrawerClosed(View drawerView) {
            }
            @Override
            public void onDrawerStateChanged(int newState) {
            }
        });

        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(new MainPagerAdapter(getApplicationContext(),getSupportFragmentManager(),3));
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
        viewPager.setOffscreenPageLimit(4);

        initView();

        if (!XXPermissions.isGrantedPermission(MainActivity.this, Permission.MANAGE_EXTERNAL_STORAGE)){
            final AlertDialog mDialog = new MaterialAlertDialogBuilder(MainActivity.this)
                    .setPositiveButton(R.string.申请,null)
                    .setNegativeButton(R.string.拒绝,null)
                    .create();
            mDialog.setTitle(getString(R.string.申请权限));
            mDialog.setMessage(Html.fromHtml(getString(R.string.储存权限)));
            mDialog.setOnShowListener(dialog -> {
                Button positiveButton = mDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                Button negativeButton = mDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                positiveButton.setOnClickListener(v -> {
                    mDialog.dismiss();
                    XXPermissions.with(MainActivity.this)
                            .permission(Permission.MANAGE_EXTERNAL_STORAGE)
                            .request(new OnPermissionCallback() {
                                @Override
                                public void onGranted(List<String> permissions, boolean all) {
                                    if (all) {
                                        //toast("获取权限成功");
                                    }
                                }

                                @Override
                                public void onDenied(List<String> permissions, boolean never) {
                                    if (never) {
                                        //toast("被永久拒绝授权，请手动授予权限");
                                        // 如果是被永久拒绝就跳转到应用权限系统设置页面
                                        XXPermissions.startPermissionActivity(MainActivity.this, permissions);
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
            layoutParams.width = getResources().getDisplayMetrics().widthPixels / 10 * 9;
            mDialog.getWindow().setAttributes(layoutParams);
        }

        subTitle();

//        Utils.upData_home(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem){
        if (menuItem.getTitle().equals(getString(R.string.搜索))){
            startActivity(new Intent(MainActivity.this, SearchActivity.class));
            //setTheme(isDarkTheme);
        }
        return super.onOptionsItemSelected(menuItem);
    }


    public void initView() {
        LinearLayout home1 = findViewById(R.id.home1);
        LinearLayout home2 = findViewById(R.id.home2);
        LinearLayout home3 = findViewById(R.id.home3);
        ImageView img1 = findViewById(R.id.img1);
        ImageView img2 = findViewById(R.id.img2);
        ImageView img3 = findViewById(R.id.img3);
        TextView txt1 = findViewById(R.id.txt1);
        TextView txt2 = findViewById(R.id.txt2);
        TextView txt3 = findViewById(R.id.txt3);
        LinearLayout qun = findViewById(R.id.qun);
        LinearLayout gxrz = findViewById(R.id.gxrz);
        LinearLayout juan = findViewById(R.id.juan);
        LinearLayout yijian = findViewById(R.id.yijian);
        LinearLayout set = findViewById(R.id.set);

        setRipple(this, home1,0x205187F4,getResources().getColor(R.color.rippleColor), 0, 24, 0, 24);
        setRipple(this, home2,getResources().getColor(R.color.backgroundColor),getResources().getColor(R.color.rippleColor), 0, 24, 0, 24);
        setRipple(this, home3,getResources().getColor(R.color.backgroundColor),getResources().getColor(R.color.rippleColor), 0, 24, 0, 24);
        setRipple(this, qun,getResources().getColor(R.color.backgroundColor),getResources().getColor(R.color.rippleColor), 0, 24, 0, 24);
        setRipple(this, yijian,getResources().getColor(R.color.backgroundColor),getResources().getColor(R.color.rippleColor), 0, 24, 0, 24);
        setRipple(this, juan,getResources().getColor(R.color.backgroundColor),getResources().getColor(R.color.rippleColor), 0, 24, 0, 24);
        setRipple(this, set,getResources().getColor(R.color.backgroundColor),getResources().getColor(R.color.rippleColor), 0, 24, 0, 24);
        setRipple(this, gxrz,getResources().getColor(R.color.backgroundColor),getResources().getColor(R.color.rippleColor), 0, 24, 0, 24);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    setRipple(MainActivity.this,home1,0x205187F4,getResources().getColor(R.color.rippleColor), 0, 24, 0, 24);
                    setRipple(MainActivity.this,home2,getResources().getColor(R.color.backgroundColor),getResources().getColor(R.color.rippleColor), 0, 24, 0, 24);
                    setRipple(MainActivity.this,home3,getResources().getColor(R.color.backgroundColor),getResources().getColor(R.color.rippleColor), 0, 24, 0, 24);
                    img1.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.zts)));
                    img2.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.editTextColor)));
                    img3.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.editTextColor)));
                    txt1.setTextColor(getResources().getColor(R.color.zts));
                    txt2.setTextColor(getResources().getColor(R.color.editTextColor));
                    txt3.setTextColor(getResources().getColor(R.color.editTextColor));
                }
                if (position == 1) {
                    setRipple(MainActivity.this,home2,0x205187F4,getResources().getColor(R.color.rippleColor), 0, 24, 0, 24);
                    setRipple(MainActivity.this,home1,getResources().getColor(R.color.backgroundColor),getResources().getColor(R.color.rippleColor), 0, 24, 0, 24);
                    setRipple(MainActivity.this,home3,getResources().getColor(R.color.backgroundColor),getResources().getColor(R.color.rippleColor), 0, 24, 0, 24);
                    img2.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.zts)));
                    img1.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.editTextColor)));
                    img3.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.editTextColor)));
                    txt2.setTextColor(getResources().getColor(R.color.zts));
                    txt1.setTextColor(getResources().getColor(R.color.editTextColor));
                    txt3.setTextColor(getResources().getColor(R.color.editTextColor));
                }
                if (position == 2) {
                    setRipple(MainActivity.this,home3,0x205187F4,getResources().getColor(R.color.rippleColor), 0, 24, 0, 24);
                    setRipple(MainActivity.this,home2,getResources().getColor(R.color.backgroundColor),getResources().getColor(R.color.rippleColor), 0, 24, 0, 24);
                    setRipple(MainActivity.this,home1,getResources().getColor(R.color.backgroundColor),getResources().getColor(R.color.rippleColor), 0, 24, 0, 24);
                    img3.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.zts)));
                    img2.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.editTextColor)));
                    img1.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.editTextColor)));
                    txt3.setTextColor(getResources().getColor(R.color.zts));
                    txt2.setTextColor(getResources().getColor(R.color.editTextColor));
                    txt1.setTextColor(getResources().getColor(R.color.editTextColor));
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        home1.setOnClickListener(v -> {
            drawerLayout.closeDrawer(GravityCompat.START);
            viewPager.setCurrentItem(0);
        });
        home2.setOnClickListener(v -> {
            drawerLayout.closeDrawer(GravityCompat.START);
            viewPager.setCurrentItem(1);
        });
        home3.setOnClickListener(v -> {
            drawerLayout.closeDrawer(GravityCompat.START);
            viewPager.setCurrentItem(2);
        });
        gxrz.setOnClickListener(v -> {
            LoadingDialog(MainActivity.this);
            HttpRequest.build(MainActivity.this, "https://gitee.com/x1602965165/DaiMeng/raw/master/update_log")
                    .addHeaders("Charset", "UTF-8")
                    .setResponseListener(new ResponseListener() {
                        @Override
                        public void onResponse(String response, Exception error) {
                            loadDialog.dismiss();
                            try {
                                final AlertDialog mDialog = new MaterialAlertDialogBuilder(MainActivity.this)
                                        .setPositiveButton(R.string.确定,null)
                                        .setNegativeButton(R.string.取消,null)
                                        .create();
                                mDialog.setTitle(getString(R.string.更新日志));
                                mDialog.setMessage(response);
                                mDialog.setOnShowListener(dialog -> {
                                    Button positiveButton = mDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                                    Button negativeButton = mDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                                    positiveButton.setOnClickListener(v2 -> mDialog.dismiss());
                                    negativeButton.setOnClickListener(v2 -> mDialog.dismiss());
                                });
                                mDialog.show();
                                WindowManager.LayoutParams layoutParams = mDialog.getWindow().getAttributes();
                                layoutParams.width = MainActivity.this.getResources().getDisplayMetrics().widthPixels/ 10 * 9;
                                mDialog.getWindow().setAttributes(layoutParams);
                            } catch (Exception e) {
                            }
                        }
                    }).doGet();
        });
        qun.setOnClickListener(v -> {
            LoadingDialog(MainActivity.this);
            HttpRequest.build(MainActivity.this, "https://gitee.com/alex12075/ToolsBox/raw/master/config.json")
                    .addHeaders("Charset", "UTF-8")
                    .setResponseListener(new ResponseListener() {
                        @Override
                        public void onResponse(String response, Exception error) {
                            loadDialog.dismiss();
                            try {
                                try {
                                    HashMap<String, Object> key = new Gson().fromJson(response, new TypeToken<HashMap<String, Object>>(){}.getType());
                                    Utils.joinQQGroup(MainActivity.this, (String) key.get("群KEY"));
                                } catch (Exception e){
                                }
                            } catch (Exception e) {
                            }
                        }
                    }).doGet();
        });
        yijian.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.putExtra("网址", "https://support.qq.com/product/347192");
            intent.setClass(MainActivity.this, BrowserActivity.class);
            startActivity(intent);
        });

        set.setOnClickListener(v -> {
            Toast.makeText(MainActivity.this, "暂未完工", Toast.LENGTH_SHORT).show();
        });

        juan.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, DonationListActivity.class));
        });

    }

    //标题栏古诗
    public void subTitle(){
        HttpRequest.build(this,"https://v1.jinrishici.com/all.txt")
                .addHeaders("Charset", "UTF-8")
                .setResponseListener(new ResponseListener() {
                    @Override
                    public void onResponse(String response, Exception error) {
                        getSupportActionBar().setSubtitle(response);
                    }
                })
                .doGet();
    }

}