package com.shixin.app;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.transition.TransitionManager;

import com.google.android.material.button.MaterialButton;
import com.gyf.immersionbar.ImmersionBar;
import com.shixin.app.click.itemOnClick;

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

public class SearchActivity extends AppCompatActivity {

    @BindView(R.id.root)
    ViewGroup root;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.rv)
    RecyclerView rv;
    @BindView(R.id.edittext1)
    EditText edittext1;

    private HashMap<String, Object> map = new HashMap<>();
    private final ArrayList<HashMap<String, Object>> listmap = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);

        ImmersionBar.with(this)
                .fitsSystemWindows(true)
                .statusBarColor(R.color.appbarColor)
                .navigationBarColor(R.color.backgroundColor)
                .autoDarkModeEnable(true)
                .init();

        toolbar.setTitle(getString(R.string.搜索));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        rv.setItemViewCacheSize(9999);

        String[] strings = String.valueOf(mData1 + "," + mData2 + "," + mData3 + "," + mData4 + "," + mData5 + "," + mData6 + "," + mData7 + "," + mData8).split(",");
        for (String string : strings) {
            map = new HashMap<>();
            map.put("name", string);
            listmap.add(map);
        }
        TransitionManager.beginDelayedTransition(root, new androidx.transition.AutoTransition());
        rv.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
        CompanyInfoAdapter companyInfoAdapter = new CompanyInfoAdapter(this, listmap);
        rv.setAdapter(companyInfoAdapter);
        rv.getAdapter().notifyDataSetChanged();

        edittext1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                TransitionManager.beginDelayedTransition(root, new androidx.transition.AutoTransition());
                companyInfoAdapter.getFilter().filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    public class CompanyInfoAdapter extends RecyclerView.Adapter<CompanyInfoAdapter.MyViewHolder> implements Filterable {
        LayoutInflater inflater;
        Context mContext;
        ArrayList<HashMap<String, Object>> mDatas;
        ArrayList<HashMap<String, Object>> filterDatas;

        /**
         * @param context
         * @param datas
         */
        public CompanyInfoAdapter(Context context, ArrayList<HashMap<String, Object>> datas) {
            this.mContext = context;
            this.mDatas = datas;
            this.filterDatas = datas;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = View.inflate(mContext, R.layout.item_button, null);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, int position) {
            holder.button.setText((CharSequence) filterDatas.get(position).get("name"));
            holder.button.setOnClickListener(v -> {
                itemOnClick.item_1(SearchActivity.this, String.valueOf(filterDatas.get(position).get("name")));
                itemOnClick.item_2(SearchActivity.this, String.valueOf(filterDatas.get(position).get("name")));
                itemOnClick.item_3(SearchActivity.this, String.valueOf(filterDatas.get(position).get("name")));
                itemOnClick.item_4(SearchActivity.this, String.valueOf(filterDatas.get(position).get("name")));
                itemOnClick.item_5(SearchActivity.this, String.valueOf(filterDatas.get(position).get("name")));
                itemOnClick.item_6(SearchActivity.this, String.valueOf(filterDatas.get(position).get("name")));
                itemOnClick.item_7(SearchActivity.this, String.valueOf(filterDatas.get(position).get("name")));
                itemOnClick.item_8(SearchActivity.this, String.valueOf(filterDatas.get(position).get("name")));
            });
        }
        @Override
        public int getItemCount() {
            return filterDatas.size();
        }

        @Override
        public Filter getFilter() {

            return new Filter() {
                //执行过滤操作
                @Override
                protected FilterResults performFiltering(CharSequence charSequence) {
                    String charString = charSequence.toString();
                    if (charString.isEmpty()) {
                        //没有过滤的内容，则使用源数据
                        filterDatas = mDatas;
                    } else {
                        ArrayList<HashMap<String, Object>> filteredList = new ArrayList<>();


                        for (int i = 0; i < mDatas.size(); i++) {

                            if (String.valueOf(mDatas.get(i).get("name")).toLowerCase().contains(charString)) {
                                filteredList.add(mDatas.get(i));
                            }
                        }

                        filterDatas = filteredList;
                    }

                    FilterResults filterResults = new FilterResults();
                    filterResults.values = filterDatas;
                    return filterResults;
                }

                //把过滤后的值返回出来
                @Override
                protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                    filterDatas = (ArrayList<HashMap<String, Object>>) filterResults.values;
                    notifyDataSetChanged();
                }
            };
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            MaterialButton button;


            public MyViewHolder(View itemView) {
                super(itemView);
                button = (MaterialButton) itemView.findViewById(R.id.button1);

            }
        }

    }

}