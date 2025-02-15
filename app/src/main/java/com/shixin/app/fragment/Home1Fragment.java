package com.shixin.app.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.transition.TransitionManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kongzue.baseokhttp.HttpRequest;
import com.kongzue.baseokhttp.listener.ResponseListener;
import com.shixin.app.CollectActivity;
import com.shixin.app.R;
import com.shixin.app.click.itemOnClick;
import com.stx.xhb.androidx.XBanner;
import com.stx.xhb.androidx.entity.BaseBannerInfo;
import com.stx.xhb.androidx.transformers.Transformer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class Home1Fragment extends Fragment {

    private MaterialCardView add;
    private MaterialCardView banner;
    private ViewGroup root;
    private RecyclerView rv;
    private LinearLayout no;
    private ExtendedFloatingActionButton fab;
    private XBanner banner_view;

    public static SharedPreferences collect;

    public static Home1Fragment newInstance() {
        return new Home1Fragment();
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home1, container, false);

        add = view.findViewById(R.id.add);
        root = view.findViewById(R.id.root);
        rv = view.findViewById(R.id.rv);
        no = view.findViewById(R.id.no);
        fab = view.findViewById(R.id.fab);
        banner = view.findViewById(R.id.banner);
        banner_view = view.findViewById(R.id.banner_view);
        banner_view.setPageTransformer(Transformer.Default);

        collect = getContext().getSharedPreferences("collect", Activity.MODE_PRIVATE);
        add.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), CollectActivity.class));
        });
        fab.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), CollectActivity.class));
        });

        HttpRequest.build(getContext(),"https://gitee.com/alex12075/ToolsBox/raw/master/config.json")
                .addHeaders("Charset","UTF-8")
                .addHeaders("User-Agent", WebSettings.getDefaultUserAgent(getContext()))
                .setResponseListener(new ResponseListener() {
                    @Override
                    public void onResponse(String response, Exception error) {
                        try {
                            HashMap<String, Object> map = new Gson().fromJson(response, new TypeToken<HashMap<String, Object>>(){}.getType());
                            if (String.valueOf(map.get("轮播开关")).equals("开启")) {
                                TransitionManager.beginDelayedTransition(root, new androidx.transition.AutoTransition());
                                banner.setVisibility(View.VISIBLE);
                                ArrayList<HashMap<String, Object>> listmap = new Gson().fromJson(new Gson().toJson(map.get("轮播列表")), new TypeToken<ArrayList<HashMap<String, Object>>>(){}.getType());
                                final List<CustomViewsInfo> data = new ArrayList<>();
                                for(int i = 0; i < (int)(listmap.size());i++) {
                                    data.add(new CustomViewsInfo(listmap.get(i).get("轮播图片").toString(), listmap.get(i).get("轮播地址").toString()));
                                }
                                banner_view.setBannerData(R.layout.item_banner, data);
                                banner_view.loadImage((banner, model, view1, position) -> {
                                    ImageView img1 = view1.findViewById(R.id.imageview1);
                                    MaterialCardView card1 = view1.findViewById(R.id.cardview1);
                                    GlideUrl newUrl= new GlideUrl(((CustomViewsInfo) model).getXBannerUrl(), new LazyHeaders.Builder() .addHeader("User-Agent", WebSettings.getDefaultUserAgent(getContext())).build());
                                    Glide.with(getContext()).load(newUrl).into(img1);
                                    card1.setOnClickListener(_view -> {
                                        if (!(((CustomViewsInfo) model).getXBannerTitle().length() == 0)) {
                                        /*
                                        Intent intent = new Intent();
                                        intent.putExtra("网址", listmap.get((int)position).get("轮播地址").toString());
                                        intent.setClass(getContext(), BrowserActivity.class);
                                        startActivity(intent);
                                         */
                                            Uri uri = Uri.parse(listmap.get((int)position).get("轮播地址").toString());
                                            Intent intent = new Intent();
                                            intent.setAction("android.intent.action.VIEW");
                                            intent.setData(uri);
                                            startActivity(intent);
                                        }
                                    });
                                });
                            } else {
                                TransitionManager.beginDelayedTransition(root, new androidx.transition.AutoTransition());
                                banner.setVisibility(View.GONE);
                            }
                        } catch (Exception e){
                        }
                    }
                }).doGet();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            ArrayList<HashMap<String, Object>> listmap = new Gson().fromJson(collect.getString("collect",null), new TypeToken<ArrayList<HashMap<String, Object>>>() {}.getType());
            if (listmap.size() == 0) {
                no.setVisibility(View.VISIBLE);
                rv.setVisibility(View.GONE);
                fab.setVisibility(View.GONE);
            } else {
                rv.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
                CompanyInfoAdapter companyInfoAdapter = new CompanyInfoAdapter(getContext(), listmap);
                no.setVisibility(View.GONE);
                rv.setVisibility(View.VISIBLE);
                fab.setVisibility(View.VISIBLE);
                rv.setAdapter(companyInfoAdapter);
                rv.getAdapter().notifyDataSetChanged();
            }
        } catch (Exception e) {
        }
    }

    public static class CustomViewsInfo implements BaseBannerInfo {

        private String imgurl;
        private String tzurl;

        public CustomViewsInfo(String imgurl, String tzurl) {
            this.imgurl = imgurl;
            this.tzurl = tzurl;
        }

        @Override
        public String getXBannerUrl() {
            return imgurl;
        }

        @Override
        public String getXBannerTitle() {
            return tzurl;
        }
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
            return new CompanyInfoAdapter.MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final CompanyInfoAdapter.MyViewHolder holder, int position) {
            holder.button.setText((CharSequence) filterDatas.get(position).get("name"));
            holder.button.setOnClickListener(v -> {
                itemOnClick.item_1(getContext(), String.valueOf(filterDatas.get(position).get("name")));
                itemOnClick.item_2(getContext(), String.valueOf(filterDatas.get(position).get("name")));
                itemOnClick.item_3(getContext(), String.valueOf(filterDatas.get(position).get("name")));
                itemOnClick.item_4(getContext(), String.valueOf(filterDatas.get(position).get("name")));
                itemOnClick.item_5(getContext(), String.valueOf(filterDatas.get(position).get("name")));
                itemOnClick.item_6(getContext(), String.valueOf(filterDatas.get(position).get("name")));
                itemOnClick.item_7(getContext(), String.valueOf(filterDatas.get(position).get("name")));
                itemOnClick.item_8(getContext(), String.valueOf(filterDatas.get(position).get("name")));
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