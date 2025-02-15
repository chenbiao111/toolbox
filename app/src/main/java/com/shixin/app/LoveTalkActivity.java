package com.shixin.app;

import static com.shixin.app.utils.Utils.LoadingDialog;
import static com.shixin.app.utils.Utils.isVPNConnected;
import static com.shixin.app.utils.Utils.loadDialog;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.AutoTransition;
import androidx.transition.TransitionManager;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.gyf.immersionbar.ImmersionBar;
import com.kongzue.baseokhttp.HttpRequest;
import com.kongzue.baseokhttp.listener.ResponseListener;
import com.tapadoo.alerter.Alerter;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoveTalkActivity extends AppCompatActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_love_talk);
        ButterKnife.bind(this);

        ImmersionBar.with(this)
                .fitsSystemWindows(true)
                .statusBarColor(R.color.appbarColor)
                .navigationBarColor(R.color.backgroundColor)
                .autoDarkModeEnable(true)
                .init();

        toolbar.setTitle(getString(R.string.恋爱话术));
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
                textInputLayout.setError(getString(R.string.请输入关键字));
                textInputLayout.setErrorEnabled(true);
            }else {
                try {
                    if (!isVPNConnected(LoveTalkActivity.this)) {
                        LoadingDialog(LoveTalkActivity.this);
                        HttpRequest.build(LoveTalkActivity.this,"https://lover.leholady.com/s/api")
                                .addHeaders("Charset", "UTF-8")
                                .addHeaders("User-Agent", WebSettings.getDefaultUserAgent(LoveTalkActivity.this))
                                .addParameter("cmd","LoveTalk.getDialogListByWordsForApplet")
                                .addParameter("searchWords",String.valueOf(textInputEditText.getText()))
                                .addParameter("offset","0")
                                .addParameter("limit","30")
                                .addParameter("_time","631900344")
                                .addParameter("_sign","b7c1901676dfb06a3ef846711850db45")
                                .skipSSLCheck()
                                .setResponseListener(new ResponseListener() {
                                    @Override
                                    public void onResponse(String response, Exception error) {
                                        if (error == null) {
                                            loadDialog.dismiss();
                                            try {
                                                HashMap<String, Object> map = new Gson().fromJson(response, new TypeToken<HashMap<String, Object>>(){}.getType());
                                                map = new Gson().fromJson(new Gson().toJson(map.get("res")), new TypeToken<HashMap<String, Object>>(){}.getType());
                                                ArrayList<HashMap<String, Object>> listmap = new Gson().fromJson(new Gson().toJson(map.get("datas")), new TypeToken<ArrayList<HashMap<String, Object>>>() {}.getType());
                                                TransitionManager.beginDelayedTransition(root, new AutoTransition());
                                                rv.setAdapter(new Recyclerview1Adapter(listmap));
                                                rv.getAdapter().notifyDataSetChanged();
                                            } catch (Exception e){
                                            }
                                        } else {
                                            loadDialog.dismiss();
                                            Alerter.create((Activity) LoveTalkActivity.this)
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
            View _v = _inflater.inflate(R.layout.item_love, null);
            RecyclerView.LayoutParams _lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            _v.setLayoutParams(_lp);
            return new ViewHolder(_v);
        }

        @Override
        public void onBindViewHolder(ViewHolder _holder, final int _position) {
            View _view = _holder.itemView;

            final MaterialCardView cardview1 = _view.findViewById(R.id.cardview1);
            final TextView textview1 =  _view.findViewById(R.id.textview1);
            final TextView textview2 =  _view.findViewById(R.id.textview2);

            ArrayList<HashMap<String, Object>> list = new Gson().fromJson(new Gson().toJson(_data.get(_position).get("dialog")), new TypeToken<ArrayList<HashMap<String, Object>>>() {}.getType());

            textview1.setText(Html.fromHtml(String.valueOf(list.get(0).get("htmlChatTitle"))));
            StringBuffer sb = new StringBuffer();
            for (int i1 = 1; i1 < (int) (list.size()); i1++) {
                sb.append(list.get(i1).get("htmlChatTitle")).append("<br/><br/>");
            }
            textview2.setText(Html.fromHtml(sb.toString()));

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