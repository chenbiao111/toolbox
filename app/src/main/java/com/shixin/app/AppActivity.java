package com.shixin.app;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.TransitionManager;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.gyf.immersionbar.ImmersionBar;
import com.shixin.app.utils.AppBean;
import com.shixin.app.utils.BackgroundTask;
import com.shixin.app.utils.FileUtil;
import com.tapadoo.alerter.Alerter;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.shixin.app.utils.Utils.LoadingDialog;
import static com.shixin.app.utils.Utils.loadDialog;

public class AppActivity extends AppCompatActivity {

    @BindView(R.id.root)
    ViewGroup root;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.rv)
    RecyclerView rv;

    private final ArrayList<AppBean> mAppAllList = new ArrayList<>();
    private final List<AppBean> mAppPersonalList = new ArrayList<>();
    private final List<AppBean> mAppSystemList = new ArrayList<>();
    private List<PackageInfo> list;
    private Recyclerview1Adapter adapter;
    private int position;
    private ArrayList<AppBean> arrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app);
        ButterKnife.bind(this);

        ImmersionBar.with(this)
                .fitsSystemWindows(true)
                .statusBarColor(R.color.appbarColor)
                .navigationBarColor(R.color.backgroundColor)
                .autoDarkModeEnable(true)
                .init();

        toolbar.setTitle(getString(R.string.应用管理));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        rv.setItemViewCacheSize(9999);

        LoadingDialog(AppActivity.this);

        new BackgroundTask(AppActivity.this) {
            @Override
            public void doInBackground() {
                getAllApk();
            }

            @Override
            public void onPostExecute() {
                loadDialog.dismiss();
                TransitionManager.beginDelayedTransition(root, new androidx.transition.AutoTransition());
                adapter = new Recyclerview1Adapter(mAppAllList);
                arrayList = mAppAllList;
                rv.setAdapter(adapter);
            }
        }.execute();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_app,menu);
        MenuItem menuItem = menu.findItem(R.id.app_bar_search);
        SearchView mSearchView = (SearchView) androidx.core.view.MenuItemCompat.getActionView(menuItem);
        //androidx.appcompat.widget.SearchView.SearchAutoComplete mSearchEditView = mSearchView.findViewById(R.id.search_src_text);
        mSearchView.setQueryHint(getString(R.string.请输入关键字));
        //mSearchView.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (!TextUtils.isEmpty(newText)) {
                    //TransitionManager.beginDelayedTransition(root, new androidx.transition.AutoTransition());
                    filter(newText);
                }else {
                    //TransitionManager.beginDelayedTransition(root, new androidx.transition.AutoTransition());
                    adapter.filteredList(mAppAllList);
                    arrayList = mAppAllList;
                }
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    public void getAllApk() {
        PackageManager packageManager = getPackageManager();
        list = packageManager.getInstalledPackages(0);
        for (PackageInfo p : list) {
            AppBean bean = new AppBean();
            bean.setAppIcon(p.applicationInfo.loadIcon(packageManager));
            bean.setAppName(packageManager.getApplicationLabel(p.applicationInfo).toString());
            bean.setAppPackageName(p.applicationInfo.packageName);
            bean.setAppVersion(p.versionName);
            bean.setApkPath(p.applicationInfo.sourceDir);
            File file = new File(p.applicationInfo.sourceDir);
            bean.setAppSize((double) file.length());
            bean.sourceDir = p.applicationInfo.sourceDir;
            int flags = p.applicationInfo.flags;
            //判断是否是属于系统的apk
            if ((flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
                bean.isSystem = true;
                mAppSystemList.add(bean);
            } else {
                bean.setSd(true);
                mAppPersonalList.add(bean);
            }
            mAppAllList.add(bean);
        }
        //loadDialog.dismiss();
    }

    public class Recyclerview1Adapter extends RecyclerView.Adapter<Recyclerview1Adapter.ViewHolder> {
        private List<AppBean> _data;
        public Recyclerview1Adapter(List<AppBean> _arr) {
            _data = _arr;
        }

        @NotNull
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater _inflater = (LayoutInflater) parent.getContext().getSystemService(LAYOUT_INFLATER_SERVICE);
            View _v = _inflater.inflate(R.layout.item_apk, null);
            RecyclerView.LayoutParams _lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            _v.setLayoutParams(_lp);
            return new Recyclerview1Adapter.ViewHolder(_v);
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(ViewHolder _holder, final int _position) {
            View _view = _holder.itemView;

            final MaterialCardView card = (MaterialCardView) _view.findViewById(R.id.cardview1);
            final ImageView img_icon = (ImageView) _view.findViewById(R.id.img_icon);
            final TextView name = (TextView) _view.findViewById(R.id.song);
            final TextView packagename = (TextView) _view.findViewById(R.id.packagename);
            final TextView size = (TextView) _view.findViewById(R.id.size);

            name.setText(_data.get(_position).getAppName());
            packagename.setText("V " + _data.get(_position).getAppVersion());
            img_icon.setImageDrawable(_data.get(_position).getAppIcon());
            DecimalFormat df = new DecimalFormat("0.00");
            double sz = _data.get(_position).getAppSize() / 1024 / 1024;
            size.setText(sz > 0 ? df.format(sz) + "MB" : df.format(sz * 1024) + "KB");

            card.setOnClickListener(v -> {
                CharSequence[] choices = {getString(R.string.打开软件),getString(R.string.卸载软件),getString(R.string.复制包名),getString(R.string.提取APK),getString(R.string.软件详情),getString(R.string.分享软件)};
                final AlertDialog mDialog = new MaterialAlertDialogBuilder(AppActivity.this)
                        .setPositiveButton(R.string.确定, (dialog, which) -> {
                            int position = ((AlertDialog)dialog).getListView().getCheckedItemPosition();
                            if (position != AdapterView.INVALID_POSITION) {
                                if (position == 0) {
                                    try {
                                        startActivity(new Intent(getPackageManager().getLaunchIntentForPackage(_data.get(_position).getAppPackageName())));
                                    } catch (Exception e) {
                                    }
                                }
                                if (position == 1) {
                                    try {
                                        startActivity(new Intent(Intent.ACTION_DELETE, Uri.fromParts("package", _data.get(_position).getAppPackageName(), null)));
                                    } catch (Exception e) {
                                    }
                                }
                                if (position == 2) {
                                    try {
                                        ((ClipboardManager) v.getContext().getSystemService(CLIPBOARD_SERVICE)).setPrimaryClip(ClipData.newPlainText("clipboard", _data.get(_position).getAppPackageName()));
                                        Alerter.create((Activity) v.getContext())
                                                .setTitle(R.string.复制成功)
                                                .setText(R.string.已成功将内容复制到剪切板)
                                                .setBackgroundColorInt(getResources().getColor(R.color.success))
                                                .show();
                                    } catch (Exception e) {
                                    }
                                }
                                if (position == 3) {
                                    try {
                                        LoadingDialog(AppActivity.this);
                                        new BackgroundTask(AppActivity.this) {
                                            @Override
                                            public void doInBackground() {
                                                saveFile(getBytes(arrayList.get(_position).getApkPath()), FileUtil.getExternalStorageDir().concat("/噬心工具箱/应用管理/安装包/"), arrayList.get(_position).getAppName() + "-" + arrayList.get(_position).getAppVersion() + ".apk");
                                            }

                                            @Override
                                            public void onPostExecute() {
                                                loadDialog.dismiss();
                                                Alerter.create(AppActivity.this)
                                                        .setTitle(R.string.提取成功)
                                                        .setText(getString(R.string.已保存到) + FileUtil.getExternalStorageDir().concat("/噬心工具箱/应用管理/安装包/") + arrayList.get(_position).getAppName() + "-" + arrayList.get(_position).getAppVersion() + ".apk")
                                                        .setBackgroundColorInt(getResources().getColor(R.color.success))
                                                        .show();
                                            }
                                        }.execute();

                                    } catch (Exception e) {
                                    }
                                }
                                if (position == 4) {
                                    try {
                                        startActivity(new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", _data.get(_position).getAppPackageName(), null)));
                                    } catch (Exception e) {
                                    }
                                }
                                if (position == 5) {
                                    try {
                                        sendFile(new File(_data.get(_position).getApkPath()));
                                    } catch (Exception e) {
                                    }
                                }
                            }
                        })
                        .setNegativeButton(R.string.取消,null)
                        .setSingleChoiceItems(choices, 3, null)
                        .create();
                mDialog.setTitle(_data.get(_position).getAppName());
                mDialog.show();
                WindowManager.LayoutParams layoutParams = mDialog.getWindow().getAttributes();
                layoutParams.width = getResources().getDisplayMetrics().widthPixels / 10 * 9;
                mDialog.getWindow().setAttributes(layoutParams);
            });

        }

        @Override
        public int getItemCount() {
            return _data.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder{
            public ViewHolder(View v){
                super(v);
            }
        }

        public void filteredList(ArrayList<AppBean> arrayList) {
            _data = arrayList;
            notifyDataSetChanged();
        }

    }

    public void filter(String s) {
        arrayList = new ArrayList<>();
        if (mAppAllList.size() == list.size()) {
            for (int i = 0; i < mAppAllList.size(); i++) {
                if (mAppAllList.get(i).getAppName().toLowerCase().contains(s.toLowerCase())) {
                    arrayList.add(mAppAllList.get(i));
                }
            }
            adapter.filteredList(arrayList);
            if (arrayList.size() == 0) {
            }
        } else {

        }
    }

    public void sendFile(File apkFile) {
        Uri data;
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("*/*");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // "com.codeape.apkextract"即是在清单文件中配置的authorities
            data = FileProvider.getUriForFile(AppActivity.this, "com.shixin.app.apkextract", apkFile);
            // 给目标应用一个临时授权
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            data = Uri.fromFile(apkFile);
        }
        intent.putExtra(Intent.EXTRA_STREAM, data);
        startActivity(intent);
    }

    /**
     * 获得指定文件的byte数组
     */
    private byte[] getBytes(String filePath) {
        byte[] buffer = null;
        try {
            File file = new File(filePath);
            FileInputStream fis = new FileInputStream(file);
            ByteArrayOutputStream bos = new ByteArrayOutputStream(1000);
            byte[] b = new byte[1000];
            int n;
            while ((n = fis.read(b)) != -1) {
                bos.write(b, 0, n);
            }
            fis.close();
            bos.close();
            buffer = bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buffer;
    }

    //参数一、文件的byte流
    //参数二、文件要保存的路径
    //参数三、文件保存的名字
    public static void saveFile(byte[] bfile, String filePath, String fileName) {
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;

        File file = null;
        try {
            //通过创建对应路径的下是否有相应的文件夹。
            File dir = new File(filePath);
            if (!dir.exists()) {// 判断文件目录是否存在
                dir.mkdirs();//如果文件存在则删除已存在的文件夹。
            }

            //如果文件存在则删除文件
            file = new File(filePath, fileName);
            if (file.exists()) {
                file.delete();
            }
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            bos.write(bfile);//把需要保存的文件保存到SD卡中
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }
}