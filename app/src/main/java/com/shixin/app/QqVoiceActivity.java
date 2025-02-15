package com.shixin.app;

import static com.shixin.app.utils.Utils.dp2px;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.AutoTransition;
import androidx.transition.TransitionManager;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.gyf.immersionbar.ImmersionBar;
import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.lzf.easyfloat.EasyFloat;
import com.lzf.easyfloat.enums.ShowPattern;
import com.lzf.easyfloat.enums.SidePattern;
import com.lzf.easyfloat.interfaces.OnInvokeView;
import com.shixin.app.utils.BackgroundTask;
import com.shixin.app.utils.DataTools;
import com.shixin.app.utils.FileUtil;
import com.tapadoo.alerter.Alerter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import butterknife.BindView;
import butterknife.ButterKnife;

public class QqVoiceActivity extends AppCompatActivity {

    @BindView(R.id.root)
    ViewGroup root;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.qx_card)
    MaterialCardView qx_card;
    @BindView(R.id.qx)
    MaterialButton qx;
    @BindView(R.id.qx_card1)
    MaterialCardView qx_card1;
    @BindView(R.id.qx1)
    MaterialButton qx1;
    @BindView(R.id.qx_card2)
    MaterialCardView qx_card2;
    @BindView(R.id.qx2)
    MaterialButton qx2;
    @BindView(R.id.fab)
    ExtendedFloatingActionButton fab;
    @BindView(R.id.rv)
    RecyclerView rv;
    @BindView(R.id.qqzh)
    TextView qqzh;

    public final int REQ_CD_MUSIC = 101;
    private SharedPreferences qq;
    private boolean show = false;

    private List<File> length;
    private DataTools dataTools;
    private Intent music = new Intent(Intent.ACTION_GET_CONTENT);
    private HashMap<String, Object> map = new HashMap<>();
    private ArrayList<HashMap<String, Object>> listmap = new ArrayList<>();

    private String[] permissions = {Permission.SYSTEM_ALERT_WINDOW,Permission.NOTIFICATION_SERVICE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qq_voice);
        ButterKnife.bind(this);

        ImmersionBar.with(this)
                .fitsSystemWindows(true)
                .statusBarColor(R.color.appbarColor)
                .navigationBarColor(R.color.backgroundColor)
                .autoDarkModeEnable(true)
                .init();

        toolbar.setTitle(getString(R.string.QQ变音));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        music.setType("audio/*");
        music.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);

        qq = getSharedPreferences("qq", Activity.MODE_PRIVATE);

        if (XXPermissions.isGrantedPermission(this, permissions)){
            qx_card.setVisibility(View.GONE);
        }
        dataTools = new DataTools(this,11);
        if (Build.VERSION.SDK_INT >= 30){
            if (dataTools.isPermissions()){
                qx_card1.setVisibility(View.GONE);
            }
        } else {
            qx_card1.setVisibility(View.GONE);
        }

        length = listFileSortByModifyTime(FileUtil.getExternalStorageDir().concat("/噬心工具箱/QQ语音/"));
        if (length.size() == 0){
            try {
                unZip("QQ语音.zip", FileUtil.getExternalStorageDir().concat("/噬心工具箱/QQ语音/"));
                length = listFileSortByModifyTime(FileUtil.getExternalStorageDir().concat("/噬心工具箱/QQ语音/"));
            }catch (IOException e){
            }
        }
        
        for (File file : length) {
            map = new HashMap<>();
            map.put("name", file.toString());
            listmap.add(map);
            rv.setAdapter(new Recyclerview1Adapter(listmap));
            rv.getAdapter().notifyDataSetChanged();
        }

        qx.setOnClickListener(v -> {
            XXPermissions.with(this)
                    .permission(permissions)
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
                                XXPermissions.startPermissionActivity(QqVoiceActivity.this, permissions);
                            } else {
                            }
                        }
                    });
        });

        qx1.setOnClickListener(v -> {
            dataTools.requestPermission();
        });

        qx2.setOnClickListener(v -> {
            final AlertDialog mDialog = new MaterialAlertDialogBuilder(QqVoiceActivity.this)
                    .create();
            mDialog.setTitle(getString(R.string.绑定QQ));
            mDialog.setMessage(getString(R.string.绑定你当前使用的QQ账号));
            View contentView = View.inflate(QqVoiceActivity.this, R.layout.dialog_edit,null);
            mDialog.setView(contentView);
            MaterialButton button1 = contentView.findViewById(R.id.button1);
            MaterialButton button2 = contentView.findViewById(R.id.button2);
            TextInputLayout textInputLayout = contentView.findViewById(R.id.textInputLayout);
            TextInputEditText textInputEditText = contentView.findViewById(R.id.textInputEditText);
            textInputLayout.setHint(R.string.请输入QQ号);
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
            button1.setBackgroundColor(getResources().getColor(R.color.itemBackColor));
            button2.setText(R.string.确定);
            button2.setBackgroundColor(getResources().getColor(R.color.zts));
            button1.setOnClickListener(v1 -> {
                mDialog.dismiss();
            });
            button2.setOnClickListener(v1 -> {
                if (TextUtils.isEmpty(textInputEditText.getText())){
                    textInputLayout.setError(getString(R.string.请输入QQ号));
                    textInputLayout.setErrorEnabled(true);
                } else {
                    mDialog.dismiss();
                    TransitionManager.beginDelayedTransition(root, new AutoTransition());
                    qq.edit().putString("qq", String.valueOf(textInputEditText.getText())).apply();
                    if (qq.getString("qq", "无").equals("无")) {
                        qx_card2.setStrokeColor(0xFFF44336);
                        qx_card2.setStrokeWidth(dp2px(QqVoiceActivity.this, 1.8f));
                        qqzh.setText(R.string.你还未绑定QQ);
                        qx2.setText(R.string.绑定);
                    } else {
                        qx_card2.setStrokeColor(0xFF4CAF50);
                        qx_card2.setStrokeWidth(dp2px(QqVoiceActivity.this, 1.8f));
                        qqzh.setText(getString(R.string.当前绑定) + qq.getString("qq", "无"));
                        qx2.setText(R.string.切换QQ);
                    }
                }
            });
            mDialog.show();
            WindowManager.LayoutParams layoutParams = mDialog.getWindow().getAttributes();
            layoutParams.width = getResources().getDisplayMetrics().widthPixels / 10 * 9;
            mDialog.getWindow().setAttributes(layoutParams);
        });

        fab.setOnClickListener(v -> {
            if (!XXPermissions.isGrantedPermission(this, permissions)){
                Alerter.create((Activity) QqVoiceActivity.this)
                        .setTitle(R.string.温馨提示)
                        .setText(R.string.请先开启悬浮窗权限后才能够使用)
                        .setBackgroundColorInt(getResources().getColor(R.color.error))
                        .show();
            } else if (qq.getString("qq", "无").equals("无")){
                Alerter.create((Activity) QqVoiceActivity.this)
                        .setTitle(R.string.你还未绑定QQ)
                        .setText(R.string.绑定你当前使用的QQ账号)
                        .setBackgroundColorInt(getResources().getColor(R.color.error))
                        .show();
            } else if (Build.VERSION.SDK_INT >= 30){
                if (!dataTools.isPermissions()){
                    Alerter.create((Activity) QqVoiceActivity.this)
                            .setTitle(R.string.温馨提示)
                            .setText(R.string.请开启data的访问权限)
                            .setBackgroundColorInt(getResources().getColor(R.color.error))
                            .show();
                } else {
                    if (!show) {
                        show = true;
                        EasyFloat.with(QqVoiceActivity.this)
                                .setLayout(R.layout.voice_xfc, (OnInvokeView) view -> {
                                    final MaterialCardView card1 = view.findViewById(R.id.card1);
                                    final MaterialCardView card2 = view.findViewById(R.id.card2);
                                    final MaterialButton shut = view.findViewById(R.id.shut);
                                    final RecyclerView rv1 = view.findViewById(R.id.rv);
                                    map.clear();
                                    listmap.clear();
                                    //List<File> list = listFileSortByModifyTime(FileUtil.getExternalStorageDir() + "/噬心工具箱/QQ语音");
                                    for (File file : length) {
                                        map = new HashMap<>();
                                        map.put("name", file.toString());
                                        listmap.add(map);
                                        rv1.setAdapter(new Recyclerview2Adapter(listmap));
                                        rv1.getAdapter().notifyDataSetChanged();
                                    }
                                    shut.setOnClickListener(v13 -> {
                                        EasyFloat.dismiss();
                                        show = false;
                                    });
                                    card1.setOnClickListener(v12 -> {
                                        if (card2.getVisibility() == View.VISIBLE) {
                                            card2.setVisibility(View.GONE);
                                            shut.setVisibility(View.GONE);
                                            EasyFloat.dragEnable(true);
                                        } else {
                                            card2.setVisibility(View.VISIBLE);
                                            shut.setVisibility(View.VISIBLE);
                                            EasyFloat.dragEnable(false);
                                        }
                                    });
                                })
                                .setShowPattern(ShowPattern.ALL_TIME)
                                .setSidePattern(SidePattern.RESULT_HORIZONTAL)
                                .setDragEnable(true)
                                .setGravity(Gravity.CENTER_VERTICAL)
                                .show();
                    }
                }
            } else {
                if (!show) {
                    show = true;
                    EasyFloat.with(QqVoiceActivity.this)
                            .setLayout(R.layout.voice_xfc, (OnInvokeView) view -> {
                                final MaterialCardView card1 = view.findViewById(R.id.card1);
                                final MaterialCardView card2 = view.findViewById(R.id.card2);
                                final MaterialButton shut = view.findViewById(R.id.shut);
                                final RecyclerView rv1 = view.findViewById(R.id.rv);
                                map.clear();
                                listmap.clear();
                                //List<File> list = listFileSortByModifyTime(FileUtil.getExternalStorageDir() + "/噬心工具箱/QQ语音");
                                for (File file : length) {
                                    map = new HashMap<>();
                                    map.put("name", file.toString());
                                    listmap.add(map);
                                    rv1.setAdapter(new Recyclerview2Adapter(listmap));
                                    rv1.getAdapter().notifyDataSetChanged();
                                }
                                shut.setOnClickListener(v13 -> {
                                    EasyFloat.dismiss();
                                    show = false;
                                });
                                card1.setOnClickListener(v12 -> {
                                    if (card2.getVisibility() == View.VISIBLE) {
                                        card2.setVisibility(View.GONE);
                                        shut.setVisibility(View.GONE);
                                        EasyFloat.dragEnable(true);
                                    } else {
                                        card2.setVisibility(View.VISIBLE);
                                        shut.setVisibility(View.VISIBLE);
                                        EasyFloat.dragEnable(false);
                                    }
                                });
                            })
                            .setShowPattern(ShowPattern.ALL_TIME)
                            .setSidePattern(SidePattern.RESULT_HORIZONTAL)
                            .setDragEnable(true)
                            .setGravity(Gravity.CENTER_VERTICAL)
                            .show();
                }
            }
        });

        if (qq.getString("qq", "无").equals("无")) {
            qx_card2.setStrokeColor(0xFFF44336);
            qx_card2.setStrokeWidth(dp2px(QqVoiceActivity.this, 1.8f));
            qqzh.setText(R.string.你还未绑定QQ);
            qx2.setText(R.string.绑定);
        } else {
            qx_card2.setStrokeColor(0xFF4CAF50);
            qx_card2.setStrokeWidth(dp2px(QqVoiceActivity.this, 1.8f));
            qqzh.setText(getString(R.string.当前绑定) + qq.getString("qq", "无"));
            qx2.setText(R.string.切换QQ);
        }
    }

    public class Recyclerview1Adapter extends RecyclerView.Adapter<Recyclerview1Adapter.ViewHolder> {
        ArrayList<HashMap<String, Object>> _data;
        public Recyclerview1Adapter(ArrayList<HashMap<String, Object>> _arr) {
            _data = _arr;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater _inflater = (LayoutInflater)parent.getContext().getSystemService(parent.getContext().LAYOUT_INFLATER_SERVICE);
            View _v = _inflater.inflate(R.layout.item_button, null);
            RecyclerView.LayoutParams _lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            _v.setLayoutParams(_lp);
            return new ViewHolder(_v);
        }

        @Override
        public void onBindViewHolder(ViewHolder _holder, final int _position) {
            View _view = _holder.itemView;
            final MaterialButton button = _view.findViewById(R.id.button1);
            button.setGravity(Gravity.CENTER_VERTICAL);
            button.setText(new File(String.valueOf(_data.get((int)_position).get("name"))).getName());
            button.setOnLongClickListener(v -> {
                final AlertDialog mDialog = new MaterialAlertDialogBuilder(QqVoiceActivity.this)
                        .setPositiveButton(R.string.确定,null)
                        .setNegativeButton(R.string.取消,null)
                        .create();
                mDialog.setTitle(getString(R.string.温馨提示));
                mDialog.setMessage(Html.fromHtml("确定要删除<font color = '#5187F4'>\"" + new File(String.valueOf(_data.get((int)_position).get("name"))).getName() + "\"</font>吗？"));
                mDialog.setOnShowListener(dialog -> {
                    Button positiveButton = mDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                    Button negativeButton = mDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                    positiveButton.setOnClickListener(v1 -> {
                        mDialog.dismiss();
                        FileUtil.deleteFile(String.valueOf(_data.get((int)_position).get("name")));
                        length = listFileSortByModifyTime(FileUtil.getExternalStorageDir().concat("/噬心工具箱/QQ语音/"));
                        TransitionManager.beginDelayedTransition(root, new AutoTransition());
                        try {
                            listmap.clear();
                            for (File file : length) {
                                map = new HashMap<>();
                                map.put("name", file.toString());
                                listmap.add(map);
                                rv.setAdapter(new Recyclerview1Adapter(listmap));
                                rv.getAdapter().notifyDataSetChanged();
                            }
                        } catch (Exception ignored){
                        }
                    });
                    negativeButton.setOnClickListener(v1 -> mDialog.dismiss());
                });
                mDialog.show();
                WindowManager.LayoutParams layoutParams = mDialog.getWindow().getAttributes();
                layoutParams.width = getResources().getDisplayMetrics().widthPixels / 10 * 9;
                mDialog.getWindow().setAttributes(layoutParams);
                return false;
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

    }

    public class Recyclerview2Adapter extends RecyclerView.Adapter<Recyclerview2Adapter.ViewHolder> {
        ArrayList<HashMap<String, Object>> _data;
        public Recyclerview2Adapter(ArrayList<HashMap<String, Object>> _arr) {
            _data = _arr;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater _inflater = (LayoutInflater)parent.getContext().getSystemService(parent.getContext().LAYOUT_INFLATER_SERVICE);
            View _v = _inflater.inflate(R.layout.item_button_voice, null);
            RecyclerView.LayoutParams _lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            _v.setLayoutParams(_lp);
            return new ViewHolder(_v);
        }

        @Override
        public void onBindViewHolder(ViewHolder _holder, @SuppressLint("RecyclerView") final int _position) {
            View _view = _holder.itemView;
            final MaterialButton button = _view.findViewById(R.id.button1);
            button.setGravity(Gravity.CENTER_VERTICAL);
            button.setText(new File(String.valueOf(_data.get(_position).get("name"))).getName());
            button.setOnClickListener(v -> {
                if (Build.VERSION.SDK_INT >= 30){
                    @SuppressLint("SimpleDateFormat") String folder = "/com.tencent.mobileqq/Tencent/MobileQQ/" + qq.getString("qq", "无") + "/ptt/" + new SimpleDateFormat("yyyy").format(new Date()) + new SimpleDateFormat("MM").format(new Date()) + "/" + new SimpleDateFormat("dd").format(new Date()).replaceAll("^0*", "") + "/";
                    new BackgroundTask(QqVoiceActivity.this) {
                        @Override
                        public void doInBackground() {
                            dataTools.copyToData(String.valueOf(_data.get((int) _position).get("name")), folder, dataTools.getList1(folder)[0], "application/*");
                        }

                        @Override
                        public void onPostExecute() {
                            Toast.makeText(QqVoiceActivity.this,"替换成功，点击发送", Toast.LENGTH_SHORT).show();
                        }
                    }.execute();
                } else {
                    try {
                        @SuppressLint("SimpleDateFormat") String folder = "/storage/emulated/0/Android/data/com.tencent.mobileqq/Tencent/MobileQQ/" + qq.getString("qq", "无") + "/ptt/" + new SimpleDateFormat("yyyy").format(new Date()) + new SimpleDateFormat("MM").format(new Date()) + "/" + new SimpleDateFormat("dd").format(new Date()).replaceAll("^0*", "") + "/";
                        List<File> list = listFileSortByModifyTime(folder);
                        FileUtil.copyFile(String.valueOf(_data.get((int) _position).get("name")), list.get(0).toString());
                        Toast.makeText(QqVoiceActivity.this,"替换成功，点击发送", Toast.LENGTH_SHORT).show();
                    } catch (Exception ignored){
                    }
                }
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

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_qq_voice,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem){
        if (menuItem.getTitle().equals(getString(R.string.导入音频))){
            startActivityForResult(music, REQ_CD_MUSIC);
        }
        if (menuItem.getTitle().equals(getString(R.string.使用帮助))){
            final AlertDialog mDialog = new MaterialAlertDialogBuilder(this)
                    .setPositiveButton(R.string.确定,null)
                    .setNegativeButton(R.string.取消,null)
                    .create();
            mDialog.setTitle(getString(R.string.使用教程));
            mDialog.setMessage(getString(R.string.QQ变音教程内容));
            mDialog.setOnShowListener(dialog -> {
                Button positiveButton = mDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                Button negativeButton = mDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                positiveButton.setOnClickListener(v -> mDialog.dismiss());
                negativeButton.setOnClickListener(v -> mDialog.dismiss());
            });
            mDialog.show();
            WindowManager.LayoutParams layoutParams = mDialog.getWindow().getAttributes();
            layoutParams.width = getResources().getDisplayMetrics().widthPixels / 10 * 9;
            mDialog.getWindow().setAttributes(layoutParams);
        }
        return super.onOptionsItemSelected(menuItem);
    }

    public void unZip( String assetName, String savefilename) throws IOException {
        // 创建解压目标目录
        File file = new File(savefilename);
        // 如果目标目录不存在，则创建
        if (!file.exists()) {
            file.mkdirs();
        }

        InputStream inputStream = getAssets().open(assetName);
//      inputStream =getClass().getResourceAsStream(assetName;//也可以进行流解读
        ZipInputStream zipInputStream = new ZipInputStream(inputStream);
        // 读取一个进入点
        ZipEntry nextEntry = zipInputStream.getNextEntry();
        byte[] buffer = new byte[1024 * 1024];
        int count = 0;
        // 如果进入点为空说明已经遍历完所有压缩包中文件和目录
        while (nextEntry != null) {
            // 如果是一个文件夹
            if (nextEntry.isDirectory()) {
                file = new File(savefilename + File.separator + nextEntry.getName());
                if (  !file.exists()) {
                    file.mkdir();
                }
            } else {
                // 如果是文件那就保存
                file = new File(savefilename + File.separator + nextEntry.getName());
                // 则解压文件
                if ( !file.exists()) {
                    file.createNewFile();
                    FileOutputStream fos = new FileOutputStream(file);
                    while ((count = zipInputStream.read(buffer)) != -1) {
                        fos.write(buffer, 0, count);
                    }

                    fos.close();
                }
            }

            //这里很关键循环解读下一个文件
            nextEntry = zipInputStream.getNextEntry();
        }
        zipInputStream.close();

    }

    /**
     * 获取目录下所有文件
     *
     * @param realpath
     * @param files
     * @return
     */
    public static List<File> getFilesye(String realpath, List<File> files) {
        File realFile = new File(realpath);
        if (realFile.isDirectory()) {
            File[] subfiles = realFile.listFiles();
            for (File file : subfiles) {
                if (file.isDirectory()) {
                    getFilesye(file.getAbsolutePath(), files);
                } else {
                    files.add(file);
                }
            }
        }
        return files;
    }

    /**
     * 获取目录下所有文件(按时间排序)
     *
     * @param path
     * @return
     */
    public static List<File> listFileSortByModifyTime(String path) {
        List<File> list = getFilesye(path, new ArrayList<>());
        if (list != null && list.size() > 0) {
            Collections.sort(list, new Comparator<File>() {
                public int compare(File file, File newFile) {
                    if (file.lastModified() < newFile.lastModified()) {
                        return 1;
                    } else if (file.lastModified() == newFile.lastModified()) {
                        return 0;
                    } else {
                        return -1;
                    }
                }
            });
        }
        return list;
    }

    @Override
    protected void onActivityResult(int _requestCode, int _resultCode, Intent _data) {
        super.onActivityResult(_requestCode, _resultCode, _data);
        if (_requestCode == 11) {
            dataTools.savePermissions(_requestCode, _resultCode, _data);
            TransitionManager.beginDelayedTransition(root, new AutoTransition());
            qx_card1.setVisibility(View.GONE);
        }
        if (_requestCode == REQ_CD_MUSIC) {
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
                FileUtil.copyFile(_filePath.get((int) (0)), FileUtil.getExternalStorageDir() + "/噬心工具箱/QQ语音/" + new File(_filePath.get(0)).getName());
                Alerter.create((Activity) QqVoiceActivity.this)
                        .setTitle(R.string.导入成功)
                        .setText(FileUtil.getExternalStorageDir() + "/噬心工具箱/QQ语音/" + new File(_filePath.get(0)).getName())
                        .setBackgroundColorInt(getResources().getColor(R.color.success))
                        .show();
                try {
                    length = listFileSortByModifyTime(FileUtil.getExternalStorageDir().concat("/噬心工具箱/QQ语音/"));
                    TransitionManager.beginDelayedTransition(root, new AutoTransition());
                    listmap.clear();
                    for (File file : length) {
                        map = new HashMap<>();
                        map.put("name", file.toString());
                        listmap.add(map);
                        rv.setAdapter(new Recyclerview1Adapter(listmap));
                        rv.getAdapter().notifyDataSetChanged();
                    }
                } catch (Exception ignored){
                }

            }
        }
    }
}