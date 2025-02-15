package com.shixin.app;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.transition.TransitionManager;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.gyf.immersionbar.ImmersionBar;
import com.tapadoo.alerter.Alerter;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SplitWordActivity extends AppCompatActivity {

    @BindView(R.id.root)
    ViewGroup root;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.textInputLayout)
    TextInputLayout textInputLayout;
    @BindView(R.id.textInputEditText)
    TextInputEditText textInputEditText;
    @BindView(R.id.fab)
    ExtendedFloatingActionButton fab;
    @BindView(R.id.textView)
    AutoCompleteTextView textView;
    @BindView(R.id.card)
    MaterialCardView card;
    @BindView(R.id.copy)
    MaterialCardView copy;

    public static final String SD_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/";
    public static final String DB_NAME = "db_data.db";
    private final String KEY = "notIsFirstRun";//更新数据库要更新
    private SharedPreferences sharedPreferences;
    private SharedPreferences preferences;
    private Thread thread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_split_word);
        ButterKnife.bind(this);

        ImmersionBar.with(this)
                .fitsSystemWindows(true)
                .statusBarColor(R.color.appbarColor)
                .navigationBarColor(R.color.backgroundColor)
                .autoDarkModeEnable(true)
                .init();

        toolbar.setTitle(getString(R.string.拆字));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        sharedPreferences = getSharedPreferences("data", MODE_PRIVATE);

        preferences = android.preference.PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if (!sharedPreferences.getBoolean(KEY, false) || !new File(SD_PATH + DB_NAME).exists()) {
            IO.copyAssetToSD(this, DB_NAME, SD_PATH);
            sharedPreferences.edit().putBoolean(KEY, true).apply();
        }

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

        fab.setOnClickListener(view -> {
            if (TextUtils.isEmpty(textInputEditText.getText().toString())){
                textInputLayout.setError(getString(R.string.请输入文本内容));
                textInputLayout.setErrorEnabled(true);
            }else {
                TransitionManager.beginDelayedTransition(root, new androidx.transition.AutoTransition());
                card.setVisibility(View.VISIBLE);
                try {
                    final char[] chs = textInputEditText.getText().toString().toCharArray();
                    thread= new Thread(() -> {
                        SQLiteDAOImpl sqLiteDAO=new SQLiteDAOImpl(SplitWordActivity.this);
                        final StringBuilder sb=new StringBuilder();
                        int n=Integer.parseInt(preferences.getString("space_count","0"));
                        for (int i=0;i<chs.length;i++)
                        {
                            String temp=sqLiteDAO.find(String.valueOf(chs[i]));
                            sb.append(temp);
                            for(int j=0;j<n;j++)
                            {
                                sb.append(" ");
                            }
                        }
                        runOnUiThread(() -> textView.setText(sb.toString()));
                    });
                    thread.start();
                } catch (Exception e) {
                }
            }
        });

        copy.setOnClickListener(v -> {
            ((ClipboardManager) v.getContext().getSystemService(CLIPBOARD_SERVICE)).setPrimaryClip(ClipData.newPlainText("clipboard", textView.getText().toString()));
            Alerter.create((Activity) v.getContext())
                    .setTitle(R.string.复制成功)
                    .setText(R.string.已成功将内容复制到剪切板)
                    .setBackgroundColorInt(getResources().getColor(R.color.success))
                    .show();
        });

    }

    public static class DBOpenHandler extends android.database.sqlite.SQLiteOpenHelper {

        /**
         * @param context 上下文
         * @param name    数据库名
         * @param factory 可选的数据库游标工厂类，当查询(query)被提交时，该对象会被调用来实例化一个游标。默认为null。
         * @param version 数据库版本号
         */
        public DBOpenHandler(Context context, String name, android.database.sqlite.SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(android.database.sqlite.SQLiteDatabase db) {
            //db.execSQL("create table words(id integer primary key autoincrement,source varchar(200),chai varchar(200) )");

        }

        @Override
        public void onUpgrade(android.database.sqlite.SQLiteDatabase db, int oldVersion, int newVersion) {
            // TODO Auto-generated method stub

        }

    }

    public static class SQLiteDAOImpl {
        private DBOpenHandler dbOpenHandler;
        private Words words;

        public SQLiteDAOImpl(Context context) {
            this.dbOpenHandler = new DBOpenHandler(context, SplitWordActivity.SD_PATH + SplitWordActivity.DB_NAME, null, 1);
        }

        /**
         * 往表中添加元素
         *
         * @param words
         */
        public void add(Words words) {
            android.database.sqlite.SQLiteDatabase db = dbOpenHandler.getWritableDatabase();// 取得数据库操作
            db.execSQL("insert into words (source,chai) values(?,?)", new Object[]{words.getSource(), words.getChai()});
            db.close();// 记得关闭数据库
        }

        /**
         * 删除记录
         *
         * @param id
         */
        public void remove(Integer id) {
            android.database.sqlite.SQLiteDatabase db = dbOpenHandler.getWritableDatabase();
            db.execSQL("delete from words where id=?", new Object[]{id.toString()});
            db.close();
        }

        public void update(Words words) {// 修改纪录
            android.database.sqlite.SQLiteDatabase db = dbOpenHandler.getWritableDatabase();
            db.execSQL("update words set source=?,chai=? where" + " id=?", new Object[]{words.getSource(), words.getChai(), words.getId()});
            db.close();
        }

        /**
         * 根据source查寻纪录
         *
         * @param source
         * @return
         */
        public String find(String source) {
            String chai = null;
            android.database.sqlite.SQLiteDatabase db = dbOpenHandler.getReadableDatabase();
            // 用游标android.database.Cursor接收从数据库检索到的数据
            android.database.Cursor cursor = db.rawQuery("select * from words where source=?", new String[]{source.toString()});
            if (cursor.moveToFirst()) {// 依次取出数据

                chai = cursor.getString(cursor.getColumnIndex("chai"));

            }
            cursor.close();
            db.close();
            if (chai == null) {
                return source;
            }
            return chai;
        }

        /**
         * 获取所有记录
         *
         * @return
         */
        public List<Words> findAll() {
            List<Words> lists = new ArrayList<Words>();
            Words words = null;
            android.database.sqlite.SQLiteDatabase db = dbOpenHandler.getReadableDatabase();

            android.database.Cursor cursor = db.rawQuery("select * from words ", null);
            while (cursor.moveToNext()) {
                words = new Words();
                words.setId(cursor.getInt(cursor.getColumnIndex("id")));
                words.setSource(cursor.getString(cursor.getColumnIndex("source")));
                words.setChai(cursor.getString(cursor.getColumnIndex("chai")));
                lists.add(words);
            }
            db.close();
            return lists;
        }

        /**
         * 获取所有条目
         *
         * @return
         */
        public long getCount() {
            android.database.sqlite.SQLiteDatabase db = dbOpenHandler.getReadableDatabase();
            android.database.Cursor cursor = db.rawQuery("select count(*) from words ", null);
            cursor.moveToFirst();
            db.close();
            return cursor.getLong(0);
        }
    }

    public static class Words {
        public String getChai() {
            return chai;
        }

        public void setChai(String chai) {
            this.chai = chai;
        }

        public String getSource() {
            return source;
        }

        public void setSource(String source) {
            this.source = source;
        }


        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        private int id;

        private String source;
        private String chai;
    }

    public static class IO {

        /**
         * 拷贝文件到内存卡
         *
         * @param path
         */
        public static void copyAssetToSD(Context context, String fileName, String path) {
            File f = new File(path);
            if (!f.exists()) {
                f.mkdirs();
            }
            f = new File(path + "db_data.db");
            android.content.res.AssetManager assetManager = context.getAssets();
            BufferedInputStream bufferedInputStream = null;
            BufferedOutputStream bufferedOutputStream = null;
            try {
                bufferedInputStream = new BufferedInputStream(assetManager.open(fileName));
                bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(f));
                byte[] temp = new byte[1024];
                while ((bufferedInputStream.read(temp)) != -1) {
                    bufferedOutputStream.write(temp);
                    bufferedOutputStream.flush();
                }

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (bufferedInputStream != null)
                        bufferedInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    if (bufferedOutputStream != null)
                        bufferedOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

        /**
         * 写内容到数据库
         */
        public static void writeToDB(Context context) {
            SQLiteDAOImpl op = new SQLiteDAOImpl(context);
            BufferedReader bufferedReader = null;
            try {
                bufferedReader = new BufferedReader(new FileReader("/sdcard/chaizi.txt"));
                String line = null;
                while ((line = bufferedReader.readLine()) != null) {
                    Words words = new Words();
                    words.setSource(line.substring(0, 1));
                    words.setChai(line.substring(3, line.length()));
                    op.add(words);
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } finally {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

        }

    }
}