package com.shixin.app.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.chip.Chip;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kongzue.baseokhttp.HttpRequest;
import com.kongzue.baseokhttp.listener.ResponseListener;
import com.shixin.app.BrowserActivity;
import com.shixin.app.R;
import com.shixin.app.click.itemOnClick;
import com.shixin.app.widget.AutoFlowLayout;
import com.shixin.app.widget.FlowAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Home3Fragment extends Fragment {

    private AutoFlowLayout flow1;
    private AutoFlowLayout flow2;
    private AutoFlowLayout flow3;

    private final String[] mData2 = {"支付宝","微信","QQ","捐赠榜单"};

    public static Home3Fragment newInstance() {
        return new Home3Fragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home3, container, false);

        flow1 = view.findViewById(R.id.flow1);
        flow2 = view.findViewById(R.id.flow2);
        flow3 = view.findViewById(R.id.flow3);
        AutoFlowLayout_1();
        AutoFlowLayout_2();

        return view;
    }

    public void AutoFlowLayout_1(){
        HttpRequest.build(getContext(), "https://gitee.com/alex12075/ToolsBox/raw/master/config.json")
                .addHeaders("Charset","UTF-8")
                .setResponseListener(new ResponseListener() {
                    @Override
                    public void onResponse(String response, Exception error) {
                        try {
                            HashMap<String, Object> data = new Gson().fromJson(response, new TypeToken<HashMap<String, Object>>(){}.getType());
                            ArrayList<String> list = new Gson().fromJson(new Gson().toJson(data.get("近期更新")), new TypeToken<ArrayList<String>>() {}.getType());
                            String[] mData1 = list.toArray(new String[0]);
                            flow1.setAdapter(new FlowAdapter(Arrays.asList(mData1)) {
                                @Override
                                public View getView(final int position) {
                                    View item = getLayoutInflater().inflate(R.layout.item_gn, null);
                                    Chip chip = item.findViewById(R.id.chip);
                                    chip.setText(mData1[position]);
                                    chip.setOnClickListener(v -> {
                                        itemOnClick.item_1(getContext(), mData1[position]);
                                        itemOnClick.item_2(getContext(), mData1[position]);
                                        itemOnClick.item_3(getContext(), mData1[position]);
                                        itemOnClick.item_4(getContext(), mData1[position]);
                                        itemOnClick.item_5(getContext(), mData1[position]);
                                        itemOnClick.item_6(getContext(), mData1[position]);
                                        itemOnClick.item_7(getContext(), mData1[position]);
                                        itemOnClick.item_8(getContext(), mData1[position]);
                                        itemOnClick.item_yellow(getContext(), mData1[position]);
                                    });
                                    return item;
                                }
                            });

                            ArrayList<HashMap<String, Object>> listmap = new Gson().fromJson(new Gson().toJson(data.get("更多选项")), new TypeToken<ArrayList<HashMap<String, Object>>>(){}.getType());
                            final String[] mData3 = new String[listmap.size()];
                            for(int i = 0; i < (int)(listmap.size());i++) {
                                mData3[i] = (String) listmap.get(i).get("名称");
                            }
                            flow3.setAdapter(new FlowAdapter(Arrays.asList(mData3)) {
                                @Override
                                public View getView(final int position) {
                                    View item = getLayoutInflater().inflate(R.layout.item_gn, null);
                                    Chip chip = item.findViewById(R.id.chip);
                                    chip.setText(mData3[position]);
                                    chip.setOnClickListener(v -> {
                                        if (listmap.get(position).get("网址").toString().length() != 0) {
                                            Intent intent = new Intent();
                                            intent.putExtra("网址", (String) listmap.get(position).get("网址"));
                                            intent.setClass(getContext(), BrowserActivity.class);
                                            startActivity(intent);
                                        }
                                    });
                                    return item;
                                }
                            });
                        } catch (Exception e){
                        }
                    }
                }).doGet();
    }

    public void AutoFlowLayout_2(){
        flow2.setAdapter(new FlowAdapter(Arrays.asList(mData2)) {
            @Override
            public View getView(final int position) {
                View item = getLayoutInflater().inflate(R.layout.item_gn, null);
                Chip chip = item.findViewById(R.id.chip);
                chip.setText(mData2[position]);
                chip.setOnClickListener(v -> itemOnClick.item_jz(v.getContext(),mData2[position]));
                return item;
            }
        });
    }
}