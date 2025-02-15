package com.shixin.app;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.TransitionManager;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.gyf.immersionbar.ImmersionBar;
import com.kongzue.baseokhttp.HttpRequest;
import com.kongzue.baseokhttp.listener.ResponseListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.shixin.app.utils.Utils.JieQu;
import static com.shixin.app.utils.Utils.LoadingDialog;
import static com.shixin.app.utils.Utils.isVPNConnected;
import static com.shixin.app.utils.Utils.loadDialog;

public class DictionaryActivity extends AppCompatActivity {

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
    private ArrayList<HashMap<String, Object>> listmap = new ArrayList<>();
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dictionary);
        ButterKnife.bind(this);

        ImmersionBar.with(this)
                .fitsSystemWindows(true)
                .statusBarColor(R.color.appbarColor)
                .navigationBarColor(R.color.backgroundColor)
                .autoDarkModeEnable(true)
                .init();

        toolbar.setTitle(getString(R.string.字典查询));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        mediaPlayer = new MediaPlayer();
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
                    if (!isVPNConnected(DictionaryActivity.this)) {
                        LoadingDialog(DictionaryActivity.this);
                        HttpRequest.build(DictionaryActivity.this,"https://www.zidianvip.com/api/search/index?word=" + textInputEditText.getText())
                                .addHeaders("Charset","UTF-8")
                                .setResponseListener(new ResponseListener() {
                                    @Override
                                    public void onResponse(String response, Exception error) {
                                        //loadDialog.dismiss();
                                        try {
                                            map = new Gson().fromJson(JieQu(DictionaryActivity.this, response, "\"wordObj\":", "},").concat("}"), new TypeToken<HashMap<String, Object>>(){}.getType());
                                            if (map.get("pinyin").toString().contains(",")) {
                                                HttpRequest.build(DictionaryActivity.this,"https://www.81for.com/api/dyz/detail?name=" + textInputEditText.getText())
                                                        .addHeaders("Charset","UTF-8")
                                                        .setResponseListener(new ResponseListener() {
                                                            @Override
                                                            public void onResponse(String response, Exception error) {
                                                                loadDialog.dismiss();
                                                                try {
                                                                    map.clear();
                                                                    listmap.clear();
                                                                    listmap = new Gson().fromJson(JieQu(DictionaryActivity.this, response, "\"list\":", "]").concat("]"), new TypeToken<ArrayList<HashMap<String, Object>>>(){}.getType());
                                                                    TransitionManager.beginDelayedTransition(root, new androidx.transition.AutoTransition());
                                                                    rv.setVisibility(View.VISIBLE);
                                                                    rv.setAdapter(new Recyclerview1Adapter(listmap));
                                                                    rv.getAdapter().notifyDataSetChanged();
                                                                } catch (Exception e){
                                                                }
                                                            }
                                                        }).doGet();
                                            }
                                            else {
                                                HttpRequest.build(DictionaryActivity.this,"https://www.zidianvip.com/api/zidian/detail?word=" + textInputEditText.getText())
                                                        .addHeaders("Charset","UTF-8")
                                                        .setResponseListener(new ResponseListener() {
                                                            @Override
                                                            public void onResponse(String response, Exception error) {
                                                                loadDialog.dismiss();
                                                                try {
                                                                    map.clear();
                                                                    listmap.clear();
                                                                    map = new Gson().fromJson(response, new TypeToken<HashMap<String, Object>>(){}.getType());
                                                                    map = new Gson().fromJson(new Gson().toJson(map.get("data")), new TypeToken<HashMap<String, Object>>(){}.getType());
                                                                    map = new Gson().fromJson(new Gson().toJson(map.get("blog")), new TypeToken<HashMap<String, Object>>(){}.getType());
                                                                    listmap.add(map);
                                                                    TransitionManager.beginDelayedTransition(root, new androidx.transition.AutoTransition());
                                                                    rv.setVisibility(View.VISIBLE);
                                                                    rv.setAdapter(new Recyclerview1Adapter(listmap));
                                                                    rv.getAdapter().notifyDataSetChanged();
                                                                } catch (Exception e){
                                                                }
                                                            }
                                                        }).doGet();
                                            }
                                        } catch (Exception e){
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
            View _v = _inflater.inflate(R.layout.item_zd, null);
            RecyclerView.LayoutParams _lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            _v.setLayoutParams(_lp);
            return new ViewHolder(_v);
        }

        @Override
        public void onBindViewHolder(ViewHolder _holder, final int _position) {
            View _view = _holder.itemView;


            final TextView textview13 = (TextView) _view.findViewById(R.id.textview13);
            final ImageView imageview1 = (ImageView) _view.findViewById(R.id.imageview1);
            final TextView textview1 = (TextView) _view.findViewById(R.id.textview1);
            final ImageView imageview2 = (ImageView) _view.findViewById(R.id.imageview2);
            final TextView textview8 = (TextView) _view.findViewById(R.id.textview8);
            final TextView textview9 = (TextView) _view.findViewById(R.id.textview9);
            final TextView textview10 = (TextView) _view.findViewById(R.id.textview10);
            final TextView textview11 = (TextView) _view.findViewById(R.id.textview11);

            Glide.with(getApplicationContext()).load(Uri.parse((String) _data.get((int)_position).get("picture"))).into(imageview1);
            textview1.setText((CharSequence) (_data.get((int)_position).get("name") + "：" + _data.get((int)_position).get("spell")));
            textview8.setText((CharSequence) _data.get((int)_position).get("radicals"));
            textview9.setText((CharSequence) _data.get((int)_position).get("structure"));
            textview10.setText(String.valueOf((long)(Double.parseDouble(String.valueOf(_data.get((int)_position).get("strokeNum"))))));
            textview11.setText(String.valueOf((long)(Double.parseDouble(String.valueOf(_data.get((int)_position).get("partNum"))))));
            textview13.setText(Html.fromHtml(_data.get((int)_position).get("baseDef").toString()));
            imageview2.setOnClickListener(_view1 -> {
                try {
                    mediaPlayer.reset();
                    mediaPlayer.setDataSource(_data.get((int)_position).get("voices").toString());
                    mediaPlayer.prepareAsync();
                    mediaPlayer.setOnPreparedListener(media_player -> mediaPlayer.start());
                    mediaPlayer.setOnCompletionListener(media_player -> {
                    });
                } catch(Exception e) {

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
}