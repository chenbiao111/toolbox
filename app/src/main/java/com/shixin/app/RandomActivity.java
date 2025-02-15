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
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.gyf.immersionbar.ImmersionBar;
import com.tapadoo.alerter.Alerter;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RandomActivity extends AppCompatActivity {

    @BindView(R.id.root)
    ViewGroup root;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.textInputLayout1)
    TextInputLayout textInputLayout1;
    @BindView(R.id.textInputEditText1)
    TextInputEditText textInputEditText1;
    @BindView(R.id.textInputLayout2)
    TextInputLayout textInputLayout2;
    @BindView(R.id.textInputEditText2)
    TextInputEditText textInputEditText2;
    @BindView(R.id.seekbar1)
    DiscreteSeekBar seekbar1;
    @BindView(R.id.button1)
    MaterialButton button1;
    @BindView(R.id.button2)
    MaterialButton button2;
    @BindView(R.id.rv)
    RecyclerView rv;

    private HashMap<String, Object> map = new HashMap<>();
    private final ArrayList<HashMap<String, Object>> listmap = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_random);
        ButterKnife.bind(this);

        ImmersionBar.with(this)
                .fitsSystemWindows(true)
                .statusBarColor(R.color.appbarColor)
                .navigationBarColor(R.color.backgroundColor)
                .autoDarkModeEnable(true)
                .init();

        toolbar.setTitle(getString(R.string.随机数生成));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        rv.setItemViewCacheSize(9999);

        textInputEditText1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                textInputLayout1.setErrorEnabled(false);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        textInputEditText2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                textInputLayout2.setErrorEnabled(false);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        button1.setOnClickListener(v -> {
            TransitionManager.beginDelayedTransition(root, new androidx.transition.AutoTransition());
            textInputEditText1.setText("");
            textInputEditText2.setText("");
            listmap.clear();
            rv.setAdapter(new Recyclerview1Adapter(listmap));
        });

        button2.setOnClickListener(v -> {
            if (TextUtils.isEmpty(textInputEditText1.getText())) {
                textInputLayout1.setError(getString(R.string.请输入最小值));
                textInputLayout1.setErrorEnabled(true);
            } else if (TextUtils.isEmpty(textInputEditText2.getText())){
                textInputLayout2.setError(getString(R.string.请输入最大值));
                textInputLayout2.setErrorEnabled(true);
            } else if (Double.parseDouble(String.valueOf(textInputEditText1.getText())) > Double.parseDouble(String.valueOf(textInputEditText2.getText()))){
                textInputLayout2.setError(getString(R.string.最大值不能小于最小值));
                textInputLayout2.setErrorEnabled(true);
            } else {
                try {
                    map.clear();
                    listmap.clear();
                    String[] data = new String[seekbar1.getProgress()];
                    for(int i=0;i <seekbar1.getProgress();i++) {
                        int max=Integer.parseInt(String.valueOf(textInputEditText2.getText()));
                        int min=Integer.parseInt(String.valueOf(textInputEditText1.getText()));
                        Random random = new Random();
                        int s = random.nextInt(max)%(max-min+1) + min;
                        data[i] = String.valueOf(s);
                    }
                    for (String string : data) {
                        map = new HashMap<>();
                        map.put("key", string);
                        listmap.add(map);
                    }
                    TransitionManager.beginDelayedTransition(root, new androidx.transition.AutoTransition());
                    rv.setVisibility(View.VISIBLE);
                    rv.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
                    rv.setAdapter(new Recyclerview1Adapter(listmap));
                    rv.getAdapter().notifyDataSetChanged();
                } catch(Exception e) {
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
        public @NotNull Recyclerview1Adapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater _inflater = (LayoutInflater) parent.getContext().getSystemService(LAYOUT_INFLATER_SERVICE);
            View _v = _inflater.inflate(R.layout.item_button, null);
            RecyclerView.LayoutParams _lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            _v.setLayoutParams(_lp);
            return new Recyclerview1Adapter.ViewHolder(_v);
        }

        @Override
        public void onBindViewHolder(Recyclerview1Adapter.ViewHolder _holder, final int _position) {
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