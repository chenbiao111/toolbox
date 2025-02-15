package com.shixin.app;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.transition.TransitionManager;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.gson.Gson;
import com.gyf.immersionbar.ImmersionBar;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.shixin.app.fragment.Home2Fragment.mData1;
import static com.shixin.app.fragment.Home2Fragment.mData2;
import static com.shixin.app.fragment.Home2Fragment.mData3;
import static com.shixin.app.fragment.Home2Fragment.mData4;
import static com.shixin.app.fragment.Home2Fragment.mData5;
import static com.shixin.app.fragment.Home2Fragment.mData6;
import static com.shixin.app.fragment.Home2Fragment.mData7;
import static com.shixin.app.fragment.Home2Fragment.mData8;

public class CollectActivity extends AppCompatActivity {

    @BindView(R.id.root)
    ViewGroup root;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.rv)
    RecyclerView rv;
    @BindView(R.id.fab)
    ExtendedFloatingActionButton fab;

    private HashMap<String, Object> map = new HashMap<>();
    private final ArrayList<HashMap<String, Object>> listmap = new ArrayList<>();
    private SparseArray<Boolean> checkStates;
    private SharedPreferences collect;

    @SuppressLint("UseSparseArrays")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collect);
        ButterKnife.bind(this);

        ImmersionBar.with(this)
                .fitsSystemWindows(true)
                .statusBarColor(R.color.appbarColor)
                .navigationBarColor(R.color.backgroundColor)
                .autoDarkModeEnable(true)
                .init();

        toolbar.setTitle(getString(R.string.功能收藏));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        collect = getSharedPreferences("collect", Activity.MODE_PRIVATE);
        rv.setItemViewCacheSize(9999);

        String[] strings = (mData1 + "," + mData2 + "," + mData3 + "," + mData4 + "," + mData5 + "," + mData6 + "," + mData7 + "," + mData8).split(",");
        int position = 0;
        checkStates = new SparseArray<>();
        for (String string : strings) {
            map = new HashMap<>();
            map.put("name", string);
            listmap.add(map);
            if (collect.getString("collect", "[]").contains(string)) {
                checkStates.put(position++, true);
            } else {
                checkStates.put(position++, false);
            }
        }
        TransitionManager.beginDelayedTransition(root, new androidx.transition.AutoTransition());
        rv.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
        CompanyInfoAdapter companyInfoAdapter = new CompanyInfoAdapter(this, listmap);
        rv.setAdapter(companyInfoAdapter);
        rv.getAdapter().notifyDataSetChanged();

        fab.setOnClickListener(v -> {
            ArrayList<HashMap<String, Object>> list = new ArrayList<>();
            for (int i = 0; i < listmap.size(); i++) {
                if (checkStates.valueAt(i)) {
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("name", String.valueOf(listmap.get(i).get("name")));
                    list.add(map);
                }
            }

            collect.edit().putString("collect", new Gson().toJson(list)).apply();
            finish();
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_collect,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem){
        if (menuItem.getTitle().equals(getString(R.string.全部选择))){
            for (int i = 0; i < checkStates.size(); i++) {
                checkStates.setValueAt(i, true);
            }
            rv.getAdapter().notifyDataSetChanged();
        }
        if (menuItem.getTitle().equals(getString(R.string.取消全选))){
            for (int i = 0; i < checkStates.size(); i++) {
                checkStates.setValueAt(i, false);
            }
            rv.getAdapter().notifyDataSetChanged();
        }
        return super.onOptionsItemSelected(menuItem);
    }

    public class CompanyInfoAdapter extends RecyclerView.Adapter<CompanyInfoAdapter.MyViewHolder> {
        LayoutInflater inflater;
        Context mContext;
        ArrayList<HashMap<String, Object>> mDatas;

        /**
         * @param context
         * @param datas
         */
        public CompanyInfoAdapter(Context context, ArrayList<HashMap<String, Object>> datas) {
            this.mContext = context;
            this.mDatas = datas;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public @NotNull MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = View.inflate(mContext, R.layout.item_collect, null);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, int position) {
            holder.checkbox.setText((CharSequence) mDatas.get(position).get("name"));
            holder.checkbox.setChecked(checkStates.valueAt(position));

            holder.checkbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                checkStates.setValueAt(position, isChecked);
                if (isChecked) {

                } else {

                }
            });
        }
        @Override
        public int getItemCount() {
            return mDatas.size();
        }


        class MyViewHolder extends RecyclerView.ViewHolder {
            AppCompatCheckBox checkbox;

            public MyViewHolder(View itemView) {
                super(itemView);
                checkbox = (AppCompatCheckBox) itemView.findViewById(R.id.checkbox);

            }
        }

    }


}