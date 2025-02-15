package com.shixin.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.AutoTransition;
import androidx.transition.TransitionManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.gyf.immersionbar.ImmersionBar;
import com.kongzue.baseokhttp.HttpRequest;
import com.kongzue.baseokhttp.listener.ResponseListener;
import com.tapadoo.alerter.Alerter;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.shixin.app.utils.Utils.JieQu;
import static com.shixin.app.utils.Utils.LoadingDialog;
import static com.shixin.app.utils.Utils.isVPNConnected;
import static com.shixin.app.utils.Utils.loadDialog;

public class VideoSearchActivity extends AppCompatActivity {

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
    @BindView(R.id.rv)
    RecyclerView rv;

    private HashMap<String, Object> map = new HashMap<>();
    private ArrayList<String> list = new ArrayList<>();
    private ArrayList<HashMap<String, Object>> listmap = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_search);
        ButterKnife.bind(this);

        ImmersionBar.with(this)
                .fitsSystemWindows(true)
                .statusBarColor(R.color.appbarColor)
                .navigationBarColor(R.color.backgroundColor)
                .autoDarkModeEnable(true)
                .init();

        toolbar.setTitle(getString(R.string.影视搜索));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        rv.setItemViewCacheSize(9999);

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
            if (TextUtils.isEmpty(textInputEditText.getText())){
                textInputLayout.setError(getString(R.string.请输入文字内容));
                textInputLayout.setErrorEnabled(true);
            }else {
                try {
                    if (!isVPNConnected(VideoSearchActivity.this)) {
                        LoadingDialog(VideoSearchActivity.this);
                        HttpRequest.build(VideoSearchActivity.this,"https://www.chok8.com/vodsearch/-------------.html?wd="+textInputEditText.getText()+"&submit=")
                                .addHeaders("Charset", "UTF-8")
                                .addHeaders("User-Agent", WebSettings.getDefaultUserAgent(VideoSearchActivity.this))
                                .skipSSLCheck()
                                .setResponseListener(new ResponseListener() {
                                    @Override
                                    public void onResponse(String response, Exception error) {
                                        if (error == null) {
                                            loadDialog.dismiss();
                                            try {
                                                map.clear();
                                                list.clear();
                                                listmap.clear();
                                                list = new ArrayList<String>(Arrays.asList(JieQu(VideoSearchActivity.this, response, "<li class=\"active  clearfix\">", "</ul>").split("<li class=\"active top-line-dot clearfix\">")));
                                                for (int i1 = 0; i1 < (int) (list.size()); i1++) {
                                                    map = new HashMap<>();
                                                    map.put("name", JieQu(VideoSearchActivity.this, list.get(i1), "title=\"", "\""));
                                                    map.put("url", "https://www.chok8.com" + JieQu(VideoSearchActivity.this, list.get(i1), "<a href=\"", "\""));
                                                    map.put("img", JieQu(VideoSearchActivity.this, list.get(i1), "data-original=\"", "\""));
                                                    map.put("name1", JieQu(VideoSearchActivity.this, list.get(i1), "<span class=\"pic-text text-right\">", "</span>"));
                                                    map.put("name2", JieQu(VideoSearchActivity.this, list.get(i1), "主演：</span>", "</p>"));
                                                    listmap.add(map);
                                                }
                                                TransitionManager.beginDelayedTransition(root, new AutoTransition());
                                                rv.setAdapter(new Recyclerview1Adapter(listmap));
                                                rv.getAdapter().notifyDataSetChanged();
                                            } catch (Exception e){
                                            }
                                        } else {
                                            loadDialog.dismiss();
                                            Alerter.create((Activity) VideoSearchActivity.this)
                                                    .setTitle(R.string.温馨提示)
                                                    .setText(R.string.搜索失败)
                                                    .setBackgroundColorInt(getResources().getColor(R.color.error))
                                                    .show();
                                        }


                                    }
                                }).doGet();
                    }
                } catch (Exception e) {
                }
            }
        });

    }

    public class Recyclerview1Adapter extends RecyclerView.Adapter<Recyclerview1Adapter.ViewHolder> {
        ArrayList<HashMap<String, Object>> _data;
        public Recyclerview1Adapter(ArrayList<HashMap<String, Object>> _arr) {
            _data = _arr;
        }

        @Override
        public @NotNull ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater _inflater = (LayoutInflater) parent.getContext().getSystemService(LAYOUT_INFLATER_SERVICE);
            View _v = _inflater.inflate(R.layout.item_video, null);
            RecyclerView.LayoutParams _lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            _v.setLayoutParams(_lp);
            return new Recyclerview1Adapter.ViewHolder(_v);
        }

        @Override
        public void onBindViewHolder(ViewHolder _holder, final int _position) {
            View _view = _holder.itemView;

            final MaterialCardView cardview1 = _view.findViewById(R.id.cardview1);
            final MaterialCardView cardview2 = _view.findViewById(R.id.cardview2);
            final ImageView imageview1 =  _view.findViewById(R.id.imageview1);
            final TextView textview1 =  _view.findViewById(R.id.textview1);
            final TextView textview2 =  _view.findViewById(R.id.textview2);
            final TextView textview3 =  _view.findViewById(R.id.textview3);

            //_setShape(textview2, "#305187f4", 20);
            Glide.with(VideoSearchActivity.this).load(listmap.get(_position).get("img")).thumbnail(0.1f).centerCrop().priority(Priority.IMMEDIATE).into(imageview1);
            textview1.setText((CharSequence) _data.get((int)_position).get("name"));
            textview2.setText((CharSequence) _data.get((int)_position).get("name1"));
            textview3.setText((CharSequence) _data.get((int)_position).get("name2"));

            cardview1.setOnClickListener(v -> {
                Intent intent = new Intent();
                intent.setClass(VideoSearchActivity.this, VideoDetailsActivity.class);
                intent.putExtra("name", _data.get((int)_position).get("name").toString());
                intent.putExtra("img", _data.get((int)_position).get("img").toString());
                intent.putExtra("url", _data.get((int)_position).get("url").toString());
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