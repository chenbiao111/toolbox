package com.shixin.app;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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
import com.shixin.app.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.shixin.app.utils.Utils.LoadingDialog;
import static com.shixin.app.utils.Utils.loadDialog;

public class RubbishActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.root)
    ViewGroup root;

    @BindView(R.id.textInputLayout)
    TextInputLayout textInputLayout;
    @BindView(R.id.textInputEditText)
    TextInputEditText textInputEditText;
    @BindView(R.id.fab)
    ExtendedFloatingActionButton fab;
    @BindView(R.id.textview1)
    TextView textview1;
    @BindView(R.id.textview2)
    TextView textview2;
    @BindView(R.id.cardview1)
    MaterialCardView cardview1;

    private ArrayList<HashMap<String, Object>> listmap = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rubbish);
        ButterKnife.bind(this);

        ImmersionBar.with(this)
                .fitsSystemWindows(true)
                .statusBarColor(R.color.appbarColor)
                .navigationBarColor(R.color.backgroundColor)
                .autoDarkModeEnable(true)
                .init();

        toolbar.setTitle(getString(R.string.垃圾分类查询));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        textInputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                textInputLayout.setErrorEnabled(false);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(textInputEditText.getText())){
                    TransitionManager.beginDelayedTransition(root, new androidx.transition.AutoTransition());
                    cardview1.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });


        fab.setOnClickListener(view -> {
            if (TextUtils.isEmpty(textInputEditText.getText())){
                textInputLayout.setError(getString(R.string.请输入垃圾名称));
                textInputLayout.setErrorEnabled(true);
            }else {
                if (!Utils.isVPNConnected(this)) {
                    LoadingDialog(RubbishActivity.this);
                    HttpRequest.build(this,"https://open.onebox.so.com/dataApi?query=" + textInputEditText.getText() + "&url=" + textInputEditText.getText() + "&type=lajifenlei&src=onebox")
                            .addHeaders("Charset","UTF-8")
                            .setResponseListener(new ResponseListener() {
                                @Override
                                public void onResponse(String response, Exception error) {
                                    loadDialog.dismiss();
                                    try {
                                        HashMap<String, Object> map = new Gson().fromJson(response, new TypeToken<HashMap<String, Object>>(){}.getType());
                                        map = new Gson().fromJson(new Gson().toJson(map.get("display")), new TypeToken<HashMap<String, Object>>() {}.getType());
                                        TransitionManager.beginDelayedTransition(root, new androidx.transition.AutoTransition());
                                        cardview1.setVisibility(View.VISIBLE);
                                        textview1.setText((CharSequence) map.get("type"));
                                        //textview2.setText(JieQu(RubbishActivity.this, String.valueOf(map.get("abstract")), "[","]"));
                                        ArrayList<String> list = new Gson().fromJson(new Gson().toJson(map.get("abstract")), new TypeToken<ArrayList<String>>() {}.getType());
                                        String[] strings = list.toArray(new String[list.size()]);
                                        StringBuilder sb = new StringBuilder();
                                        for (String string : strings) {
                                                sb.append(string).append("\n\n");
                                        }
                                        textview2.setText(sb.toString().trim());
                                    } catch (Exception e){
                                    }
                                }
                            }).doGet();
                }
            }
        });
    }

}