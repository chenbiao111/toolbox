package com.shixin.app;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.PictureDrawable;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.TransitionManager;

import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGImageView;
import com.caverock.androidsvg.SVGParseException;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.gyf.immersionbar.ImmersionBar;
import com.kongzue.baseokhttp.HttpRequest;
import com.kongzue.baseokhttp.listener.ResponseListener;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.shixin.app.utils.Utils;
import com.tapadoo.alerter.Alerter;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.shixin.app.utils.Utils.loadDialog;

public class IconSearchActivity extends AppCompatActivity {

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
    @BindView(R.id.srl)
    SmartRefreshLayout srl;

    private HashMap<String, Object> map = new HashMap<>();
    private HashMap<String, Object> map1 = new HashMap<>();
    private ArrayList<HashMap<String, Object>> listmap = new ArrayList<>();
    private ArrayList<HashMap<String, Object>> listmap1 = new ArrayList<>();
    private int ye = 1;
    private String search = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_icon_search);
        ButterKnife.bind(this);

        ImmersionBar.with(this)
                .fitsSystemWindows(true)
                .statusBarColor(R.color.appbarColor)
                .navigationBarColor(R.color.backgroundColor)
                .autoDarkModeEnable(true)
                .init();

        toolbar.setTitle(getString(R.string.阿里图标库搜索));
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
            if (TextUtils.isEmpty(textInputEditText.getText().toString())){
                textInputLayout.setError(getString(R.string.请输入关键字));
                textInputLayout.setErrorEnabled(true);
            }else {
                if (!Utils.isVPNConnected(this)) {
                    search = String.valueOf(textInputEditText.getText());
                    ye = 1;
                    Utils.LoadingDialog(IconSearchActivity.this);
                    HttpRequest.build(IconSearchActivity.this, "https://www.iconfont.cn/api/icon/search.json")
                            .addHeaders("Accept-Language", "zh-cn,zh;q=0.5")
                            .addHeaders("Accept-Charset", "utf8")
                            .addHeaders("Cookie", "ctoken=sh6GKQur-48KjRv0-IDssPrQ")
                            .addHeaders("Content-Type", "application/x-www-form-urlencoded")
                            .addHeaders("User-Agent", WebSettings.getDefaultUserAgent(IconSearchActivity.this))
                            .addHeaders("Host", "www.iconfont.cn")
                            .addParameter("q", search)
                            .addParameter("sortType", "updated_at")
                            .addParameter("page", String.valueOf(ye++))
                            .addParameter("pageSize", "54")
                            .addParameter("fromCollection", "1")
                            .addParameter("fills", "null")
                            .addParameter("ctoken", "sh6GKQur-48KjRv0-IDssPrQ")
                            .setResponseListener(new ResponseListener() {
                                @Override
                                public void onResponse(String response, Exception error) {
                                    try {
                                        Utils.loadDialog.dismiss();
                                        map = new Gson().fromJson(response, new TypeToken<HashMap<String, Object>>() {}.getType());
                                        map = new Gson().fromJson(new Gson().toJson(map.get("data")), new TypeToken<HashMap<String, Object>>() {}.getType());
                                        listmap = new Gson().fromJson(new Gson().toJson(map.get("icons")), new TypeToken<ArrayList<HashMap<String, Object>>>() {}.getType());
                                        TransitionManager.beginDelayedTransition(srl, new androidx.transition.AutoTransition());
                                        rv.setAdapter(new Recyclerview1Adapter(listmap));
                                        rv.getAdapter().notifyDataSetChanged();
                                    } catch (Exception e) {
                                    }
                                }
                            }).doPost();
                }
            }
        });

        srl.setOnLoadMoreListener(refreshLayout -> {
            if (!Utils.isVPNConnected(IconSearchActivity.this)) {
                HttpRequest.build(IconSearchActivity.this, "https://www.iconfont.cn/api/icon/search.json")
                        .addHeaders("Accept-Language", "zh-cn,zh;q=0.5")
                        .addHeaders("Accept-Charset", "utf8")
                        .addHeaders("Cookie", "ctoken=sh6GKQur-48KjRv0-IDssPrQ")
                        .addHeaders("Content-Type", "application/x-www-form-urlencoded")
                        .addHeaders("User-Agent", WebSettings.getDefaultUserAgent(IconSearchActivity.this))
                        .addHeaders("Host", "www.iconfont.cn")
                        .addParameter("q", search)
                        .addParameter("sortType", "updated_at")
                        .addParameter("page", String.valueOf(ye++))
                        .addParameter("pageSize", "54")
                        .addParameter("fromCollection", "1")
                        .addParameter("fills", "null")
                        .addParameter("ctoken", "sh6GKQur-48KjRv0-IDssPrQ")
                        .setResponseListener(new ResponseListener() {
                            @Override
                            public void onResponse(String response, Exception error) {
                                srl.finishLoadMore(false);
                                try {
                                    map = new Gson().fromJson(response, new TypeToken<HashMap<String, Object>>() {}.getType());
                                    map = new Gson().fromJson(new Gson().toJson(map.get("data")), new TypeToken<HashMap<String, Object>>() {}.getType());
                                    listmap1 = new Gson().fromJson(new Gson().toJson(map.get("icons")), new TypeToken<ArrayList<HashMap<String, Object>>>() {}.getType());
                                    TransitionManager.beginDelayedTransition(srl, new androidx.transition.AutoTransition());
                                    listmap.addAll(listmap1);
                                    rv.getAdapter().notifyItemRangeChanged(listmap.size() - listmap1.size(), listmap1.size());
                                } catch (Exception e) {
                                }
                            }
                        }).doPost();
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
            View _v = _inflater.inflate(R.layout.item_icon, null);
            RecyclerView.LayoutParams _lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            _v.setLayoutParams(_lp);
            return new ViewHolder(_v);
        }

        @Override
        public void onBindViewHolder(ViewHolder _holder, final int _position) {
            View _view = _holder.itemView;

            final MaterialCardView cardview1 = _view.findViewById(R.id.cardview1);
            final SVGImageView img = _view.findViewById(R.id.img);
            final TextView txt = _view.findViewById(R.id.txt);

            try {
                txt.setText((String) _data.get(_position).get("name"));
                SVG svg = SVG.getFromString(String.valueOf(_data.get(_position).get("show_svg")));
                img.setSVG(svg);
            } catch (SVGParseException e) {
                e.printStackTrace();
            }
            cardview1.setOnClickListener(_view1 -> {
                final AlertDialog mDialog = new MaterialAlertDialogBuilder(IconSearchActivity.this)
                        .setPositiveButton(R.string.导出,null)
                        .setNegativeButton(R.string.取消,null)
                        .create();
                mDialog.setTitle(getString(R.string.导出为图片));
                final View contentView = getLayoutInflater().inflate(R.layout.dialog_icon,null);
                mDialog.setView(contentView);
                final TextInputLayout textInputLayout1 = contentView.findViewById(R.id.textInputLayout1);
                final TextInputLayout textInputLayout2 = contentView.findViewById(R.id.textInputLayout2);
                final TextInputLayout textInputLayout3 = contentView.findViewById(R.id.textInputLayout3);
                final TextInputLayout textInputLayout4 = contentView.findViewById(R.id.textInputLayout4);
                final TextInputEditText tp_name = contentView.findViewById(R.id.textInputEditText1);
                final TextInputEditText tp_width = contentView.findViewById(R.id.textInputEditText2);
                final TextInputEditText tp_height = contentView.findViewById(R.id.textInputEditText3);
                final TextInputEditText tp_path = contentView.findViewById(R.id.textInputEditText4);
                final MaterialCheckBox checkBox = contentView.findViewById(R.id.checkbox);
                tp_name.addTextChangedListener(new TextWatcher() {
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
                tp_width.addTextChangedListener(new TextWatcher() {
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
                tp_height.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        textInputLayout3.setErrorEnabled(false);
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                    }
                });
                tp_path.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        textInputLayout4.setErrorEnabled(false);
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                    }
                });
                tp_name.setText((String) _data.get(_position).get("name"));
                mDialog.setOnShowListener(dialog1 -> {
                    Button positiveButton = mDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                    Button negativeButton = mDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                    positiveButton.setOnClickListener(v2 -> {
                        mDialog.dismiss();
                        try {
                            if (TextUtils.isEmpty(tp_name.getText())) {
                                textInputLayout1.setError("请输入图片名称");
                                textInputLayout1.setErrorEnabled(true);
                            } else if (TextUtils.isEmpty(tp_width.getText())){
                                textInputLayout2.setError("请输入图片宽度");
                                textInputLayout2.setErrorEnabled(true);
                            } else if (TextUtils.isEmpty(tp_height.getText())){
                                textInputLayout3.setError("请输入图片高度");
                                textInputLayout3.setErrorEnabled(true);
                            } else if (TextUtils.isEmpty(tp_path.getText())){
                                textInputLayout4.setError("请输入保存路径");
                                textInputLayout4.setErrorEnabled(true);
                            } else {
                                String svg_string = _data.get(_position).get("show_svg").toString();
                                SVG tp_svg = SVG.getFromString(svg_string);
                                final PictureDrawable drawable = new PictureDrawable(tp_svg.renderToPicture(Integer.parseInt(String.valueOf(tp_width.getText())),Integer.parseInt(String.valueOf(tp_height.getText()))));
                                Utils.LoadingDialog(IconSearchActivity.this);
                                new Thread((Runnable) () -> {
                                    String savedFile = Utils.SaveImage(IconSearchActivity.this, Utils.pictureDrawable2Bitmap(drawable), String.valueOf(tp_path.getText()), tp_name.getText() + ".png");
                                    if (savedFile != null) {
                                        MediaScannerConnection.scanFile((Activity) IconSearchActivity.this, new String[]{savedFile}, null, (MediaScannerConnection.OnScanCompletedListener) (str, uri) -> {
                                            Intent intent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
                                            intent.setData(uri);
                                            ((Activity) IconSearchActivity.this).sendBroadcast(intent);
                                            loadDialog.dismiss();
                                            Alerter.create((Activity) IconSearchActivity.this)
                                                    .setTitle(R.string.保存成功)
                                                    .setText(getString(R.string.已保存到) + savedFile)
                                                    .setBackgroundColorInt(getResources().getColor(R.color.success))
                                                    .show();
                                        });
                                    } else {
                                        loadDialog.dismiss();
                                    }
                                }).start();
                            }
                        } catch (SVGParseException e) {
                            e.printStackTrace();
                            Utils.loadDialog.dismiss();
                        }
                    });
                    negativeButton.setOnClickListener(v2 -> {
                        mDialog.dismiss();
                    });
                });
                mDialog.show();
                WindowManager.LayoutParams layoutParams = mDialog.getWindow().getAttributes();
                layoutParams.width = getResources().getDisplayMetrics().widthPixels / 10 * 9;
                mDialog.getWindow().setAttributes(layoutParams);
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