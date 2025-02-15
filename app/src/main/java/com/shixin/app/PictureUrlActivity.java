package com.shixin.app;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.transition.TransitionManager;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.gyf.immersionbar.ImmersionBar;
import com.kongzue.baseokhttp.HttpRequest;
import com.shixin.app.utils.FileUtil;
import com.shixin.app.utils.Utils;
import com.tapadoo.alerter.Alerter;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PictureUrlActivity extends AppCompatActivity {

    @BindView(R.id.root)
    ViewGroup root;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.button1)
    MaterialButton button1;
    @BindView(R.id.button2)
    MaterialButton button2;
    @BindView(R.id.card)
    MaterialCardView card;
    @BindView(R.id.card1)
    MaterialCardView card1;
    @BindView(R.id.card3)
    MaterialCardView card3;
    @BindView(R.id.lj)
    TextView lj;
    @BindView(R.id.textView)
    AutoCompleteTextView textView;

    private String path = "";

    public final int REQ_CD_IMAGE = 101;
    private Intent image = new Intent(Intent.ACTION_GET_CONTENT);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_url);
        ButterKnife.bind(this);

        ImmersionBar.with(this)
                .fitsSystemWindows(true)
                .statusBarColor(R.color.appbarColor)
                .navigationBarColor(R.color.backgroundColor)
                .autoDarkModeEnable(true)
                .init();

        toolbar.setTitle(getString(R.string.图片取直链));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        image.setType("image/*");
        image.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
        button1.setOnClickListener(v -> {
            startActivityForResult(image, REQ_CD_IMAGE);
        });

        button2.setOnClickListener(v -> {
            if (TextUtils.isEmpty(lj.getText().toString())){
                Alerter.create(PictureUrlActivity.this)
                        .setTitle(R.string.温馨提示)
                        .setText(R.string.请先选择图片)
                        .setBackgroundColorInt(getResources().getColor(R.color.error))
                        .show();
            } else {
                Utils.LoadingDialog(PictureUrlActivity.this);
                HttpRequest.build((Activity) PictureUrlActivity.this,"http://pic.sogou.com/pic/upload_pic.jsp")
                        .addHeaders("Charset", "UTF-8")
                        .addParameter("File", new File(lj.getText().toString()))
                        .setResponseListener(new com.kongzue.baseokhttp.listener.ResponseListener() {
                            @Override
                            public void onResponse(String response, Exception error) {
                                Utils.loadDialog.dismiss();
                                TransitionManager.beginDelayedTransition(root, new androidx.transition.AutoTransition());
                                card1.setVisibility(View.VISIBLE);
                                textView.setText(response);
                            }
                        })
                        .doPost();
            }

        });

        card3.setOnClickListener(v -> {
            ((ClipboardManager) v.getContext().getSystemService(CLIPBOARD_SERVICE)).setPrimaryClip(ClipData.newPlainText("clipboard", textView.getText().toString()));
            Alerter.create((Activity) v.getContext())
                    .setTitle(R.string.复制成功)
                    .setText(R.string.已成功将内容复制到剪切板)
                    .setBackgroundColorInt(getResources().getColor(R.color.success))
                    .show();
        });

    }

    @Override
    protected void onActivityResult(int _requestCode, int _resultCode, Intent _data) {
        super.onActivityResult(_requestCode, _resultCode, _data);

        if (_requestCode == REQ_CD_IMAGE && _resultCode == Activity.RESULT_OK) {
            ArrayList<String> _filePath = new ArrayList<>();
            if (_data != null) {
                if (_data.getClipData() != null) {
                    for (int _index = 0; _index < _data.getClipData().getItemCount(); _index++) {
                        ClipData.Item _item = _data.getClipData().getItemAt(_index);
                        _filePath.add(FileUtil.convertUriToFilePath(getApplicationContext(), _item.getUri()));
                    }
                } else {
                    _filePath.add(FileUtil.convertUriToFilePath(getApplicationContext(), _data.getData()));
                }

                TransitionManager.beginDelayedTransition(root, new androidx.transition.AutoTransition());
                card.setVisibility(View.VISIBLE);
                lj.setText(_filePath.get(0));
            }
        }
    }
}