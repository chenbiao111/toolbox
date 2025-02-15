package com.shixin.app.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.TransitionManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.google.android.material.card.MaterialCardView;
import com.kongzue.baseokhttp.HttpRequest;
import com.kongzue.baseokhttp.listener.ResponseListener;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.shixin.app.Avatar1Activity;
import com.shixin.app.R;
import com.shixin.app.utils.Utils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import static com.shixin.app.utils.Utils.JieQu;
import static com.shixin.app.utils.Utils.isVPNConnected;

public class Txdq4Fragment extends Fragment {

    private RecyclerView rv;
    private SmartRefreshLayout srl;
    private HashMap<String, Object> map = new HashMap<>();
    private ArrayList<HashMap<String, Object>> listmap = new ArrayList<>();
    private ArrayList<HashMap<String, Object>> listmapa = new ArrayList<>();
    private ArrayList<String> list = new ArrayList<>();
    private int ye = 0;
    private boolean isFirstLoad = true;

    public static Txdq4Fragment newInstance() {
        return new Txdq4Fragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_txdq, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // TODO: Use the ViewModel
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rv = view.findViewById(R.id.rv);
        rv.setItemViewCacheSize(9999);
        srl = view.findViewById(R.id.srl);

        srl.setOnRefreshListener(refreshLayout -> {
            if (!isVPNConnected(getContext())) {
                ye = 1;
                HttpRequest.build(getContext(),"https://m.woyaogexing.com/touxiang/katong/index.html")
                        .addHeaders("Charset","UTF-8")
                        .setResponseListener(new ResponseListener() {
                            @Override
                            public void onResponse(String response, Exception error) {
                                srl.finishRefresh(false);
                                try {
                                    map.clear();
                                    list.clear();
                                    listmap.clear();
                                    list = new ArrayList<String>(Arrays.asList(JieQu(getContext(), response, "<div class=\"m-pic-list\">", "</ul>").split("<div class=\"m-pic-list\">")));
                                    for(int i = 0; i < (int)(list.size()); i++) {
                                        map = new HashMap<>();
                                        map.put("img", "http:".concat(JieQu(getContext(), list.get((int)(i)), "data-src=\"", "\"")));
                                        map.put("name", JieQu(getContext(), list.get((int)(i)), "title=\"", "\""));
                                        map.put("url", "https://m.woyaogexing.com".concat(JieQu(getContext(), list.get((int)(i)), "<a href=\"", "\"")));
                                        listmap.add(map);
                                    }
                                    TransitionManager.beginDelayedTransition(srl, new androidx.transition.AutoTransition());
                                    rv.setAdapter(new Recyclerview1Adapter(listmap));
                                    //rv.getAdapter().notifyDataSetChanged();
                                } catch (Exception e){
                                }
                            }
                        }).doGet();
            }
        });
        srl.setOnLoadMoreListener(refreshLayout -> {
            if (!isVPNConnected(getContext())) {
                ye++;
                HttpRequest.build(getContext(),"https://m.woyaogexing.com/touxiang/katong/index_" + ye + ".html")
                        .addHeaders("Charset","UTF-8")
                        .setResponseListener(new ResponseListener() {
                            @Override
                            public void onResponse(String response, Exception error) {
                                srl.finishLoadMore(false);
                                try {
                                    map.clear();
                                    list.clear();
                                    listmapa.clear();
                                    list = new ArrayList<String>(Arrays.asList(JieQu(getContext(), response, "<div class=\"m-pic-list\">", "</ul>").split("<div class=\"m-pic-list\">")));
                                    for(int i = 0; i < (int)(list.size()); i++) {
                                        map = new HashMap<>();
                                        map.put("img", "http:".concat(JieQu(getContext(), list.get((int)(i)), "data-src=\"", "\"")));
                                        map.put("name", JieQu(getContext(), list.get((int)(i)), "title=\"", "\""));
                                        map.put("url", "https://m.woyaogexing.com".concat(JieQu(getContext(), list.get((int)(i)), "<a href=\"", "\"")));
                                        listmapa.add(map);
                                    }
                                    TransitionManager.beginDelayedTransition(srl, new androidx.transition.AutoTransition());
                                    listmap.addAll(listmapa);
                                    rv.getAdapter().notifyItemRangeChanged(listmap.size()-listmapa.size(),listmapa.size());
                                } catch (Exception e){
                                }
                            }
                        }).doGet();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isFirstLoad) {
            // 将数据加载逻辑放到onResume()方法中
            srl.autoRefresh();
            isFirstLoad = false;
        }
    }

    public class Recyclerview1Adapter extends RecyclerView.Adapter<Recyclerview1Adapter.ViewHolder> {
        ArrayList<HashMap<String, Object>> _data;
        public Recyclerview1Adapter(ArrayList<HashMap<String, Object>> _arr) {
            _data = _arr;
        }

        @Override
        public @NotNull ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater _inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View _v = _inflater.inflate(R.layout.item_txdq, null);
            RecyclerView.LayoutParams _lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (getResources().getDisplayMetrics().widthPixels - Utils.dp2px(getContext(),20)) / 2);
            _v.setLayoutParams(_lp);
            return new ViewHolder(_v);
        }

        @Override
        public void onBindViewHolder(ViewHolder _holder, final int _position) {
            View _view = _holder.itemView;

            final MaterialCardView cardview1 = (MaterialCardView) _view.findViewById(R.id.cardview1);
            final TextView txt1 = (TextView) _view.findViewById(R.id.txt1);
            final ImageView tp1 = (ImageView) _view.findViewById(R.id.tp1);

            txt1.setText((CharSequence) listmap.get(_position).get("name"));
            Glide.with(getContext()).load(listmap.get(_position).get("img")).thumbnail(0.1f).fitCenter().priority(Priority.IMMEDIATE).into(tp1);

            cardview1.setOnClickListener((View.OnClickListener) _view1 -> {
                Intent intent = new Intent((Activity) getContext(), Avatar1Activity.class);
                intent.putExtra("name", (String) _data.get((int)_position).get("name"));
                intent.putExtra("img", (String) _data.get((int)_position).get("img"));
                intent.putExtra("url", (String) _data.get((int)_position).get("url"));
                startActivity(intent);
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
}