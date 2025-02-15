package com.shixin.app;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.transition.TransitionManager;

import com.google.android.material.button.MaterialButton;
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

import static com.shixin.app.utils.Utils.LoadingDialog;
import static com.shixin.app.utils.Utils.isVPNConnected;
import static com.shixin.app.utils.Utils.loadDialog;

public class AbbreviatedActivity extends AppCompatActivity {

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
    private final ArrayList<HashMap<String, Object>> listmap = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_abbreviated);
        ButterKnife.bind(this);

        ImmersionBar.with(this)
                .fitsSystemWindows(true)
                .statusBarColor(R.color.appbarColor)
                .navigationBarColor(R.color.backgroundColor)
                .autoDarkModeEnable(true)
                .init();

        toolbar.setTitle(getString(R.string.缩写查询));
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
                textInputLayout.setError(getString(R.string.请输入缩写内容));
                textInputLayout.setErrorEnabled(true);
            }else {
                try {
                    if (!isVPNConnected(AbbreviatedActivity.this)) {
                        LoadingDialog(AbbreviatedActivity.this);
                        HttpRequest.build(AbbreviatedActivity.this, "https://lab.magiconch.com/api/nbnhhsh/guess")
                                .addHeaders("Charset", "UTF-8")
                                .addParameter("text", String.valueOf(textInputEditText.getText()))
                                .setResponseListener(new ResponseListener() {
                                    @Override
                                    public void onResponse(String response, Exception error) {
                                        loadDialog.dismiss();
                                        map.clear();
                                        listmap.clear();
                                        try {
                                            HashMap<String, Object> data = new Gson().fromJson(response.substring((int)(1), (int)(response.length() - 1)), new TypeToken<HashMap<String, Object>>(){}.getType());
                                            ArrayList<String> list = new Gson().fromJson(new Gson().toJson(data.get("trans")), new TypeToken<ArrayList<String>>() {}.getType());
                                            String[] strings = list.toArray(new String[list.size()]);
                                            for (String string : strings) {
                                                map = new HashMap<>();
                                                map.put("key", string);
                                                listmap.add(map);
                                            }
                                            TransitionManager.beginDelayedTransition(root, new androidx.transition.AutoTransition());
                                            rv.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
                                            rv.setAdapter(new Recyclerview1Adapter(listmap));
                                            rv.getAdapter().notifyDataSetChanged();
                                        } catch (Exception e) {
                                        }
                                    }
                                }).doPost();
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
            View _v = _inflater.inflate(R.layout.item_button, null);
            RecyclerView.LayoutParams _lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            _v.setLayoutParams(_lp);
            return new ViewHolder(_v);
        }

        @Override
        public void onBindViewHolder(ViewHolder _holder, final int _position) {
            View _view = _holder.itemView;
            final MaterialButton button1 = _view.findViewById(R.id.button1);
            button1.setText((CharSequence) _data.get((int)_position).get("key"));
            button1.setOnLongClickListener(v -> {
                ((ClipboardManager) v.getContext().getSystemService(CLIPBOARD_SERVICE)).setPrimaryClip(ClipData.newPlainText("clipboard", (CharSequence) _data.get((int)_position).get("key")));
                Alerter.create((Activity) v.getContext())
                        .setTitle(R.string.复制成功)
                        .setText(R.string.已成功将内容复制到剪切板)
                        .setBackgroundColorInt(getResources().getColor(R.color.success))
                        .show();
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
}