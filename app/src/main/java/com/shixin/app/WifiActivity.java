package com.shixin.app;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.TransitionManager;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.gyf.immersionbar.ImmersionBar;
import com.shixin.app.utils.BackgroundTask;
import com.tapadoo.alerter.Alerter;

import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WifiActivity extends AppCompatActivity {

    @BindView(R.id.root)
    ViewGroup root;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.rv)
    RecyclerView rv;

    private final HashMap<String, Object> map = new HashMap<>();
    private final ArrayList<HashMap<String, Object>> listmap = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi);
        ButterKnife.bind(this);

        ImmersionBar.with(this)
                .fitsSystemWindows(true)
                .statusBarColor(R.color.appbarColor)
                .navigationBarColor(R.color.backgroundColor)
                .autoDarkModeEnable(true)
                .init();

        toolbar.setTitle(getString(R.string.WIFI密码查看));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        rv.setItemViewCacheSize(9999);

        new BackgroundTask(WifiActivity.this) {
            @Override
            public void doInBackground() {
                getWifiInfo();
            }

            @Override
            public void onPostExecute() {
                Collections.reverse(listmap);
                TransitionManager.beginDelayedTransition(root, new androidx.transition.AutoTransition());
                rv.setAdapter(new Recyclerview1Adapter(listmap));
                rv.getAdapter().notifyDataSetChanged();
            }
        }.execute();

    }

    private void getWifiInfo() {
        java.lang.Process process = null;
        java.io.DataOutputStream dataOutputStream = null;
        java.io.DataInputStream dataInputStream = null;
        StringBuffer wifiConf = new StringBuffer();
        try {
            process = java.lang.Runtime.getRuntime().exec("su");
            dataOutputStream = new java.io.DataOutputStream(process.getOutputStream());
            dataInputStream = new java.io.DataInputStream(process.getInputStream());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
                dataOutputStream.writeBytes("cat /data/misc/apexdata/com.android.wifi/WifiConfigStore.xml\n");
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                dataOutputStream.writeBytes("cat /data/misc/wifi/WifiConfigStore.xml\n");
            } else {
                dataOutputStream.writeBytes("cat /data/misc/wifi/wpa_supplicant.conf\n");
            }
            dataOutputStream.writeBytes("exit\n");
            dataOutputStream.flush();
            java.io.InputStreamReader inputStreamReader = new java.io.InputStreamReader(dataInputStream, StandardCharsets.UTF_8);
            java.io.BufferedReader bufferedReader = new java.io.BufferedReader(inputStreamReader);
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                wifiConf.append(line);
            }
            bufferedReader.close();
            inputStreamReader.close();
            process.waitFor();
        } catch (Exception e) {
            return;
        } finally {
            try {
                if (dataOutputStream != null) {
                    dataOutputStream.close();
                }
                if (dataInputStream != null) {
                    dataInputStream.close();
                }
                process.destroy();
            } catch (Exception e) {

            }
        }

        //mWifiInfoList = new ArrayList<>();
        String strO;
        String strO1;
        String strO2;
        String strO3;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            strO = "\"SSID\">&quot;([^\"]+)&quot;";
            strO1 = "\"PreSharedKey\">&quot;([^\"]+)&quot;";
            strO2 = "(<WifiConfiguration>[^>])([\\s\\S]*?)(<null name=\"WEPKeys\" />)";
        } else {
            strO = "network=\\{([^\\}]+)\\}";
            strO1 = "ssid=\"([^\"]+)\"";
            strO2 = "psk=\"([^\"]+)\"";
        }

        java.util.regex.Pattern network = java.util.regex.Pattern.compile(strO2, java.util.regex.Pattern.DOTALL);
        java.util.regex.Matcher networkMatcher = network.matcher(wifiConf.toString());
        //WifiInfo wifiInfo;
        while (networkMatcher.find()) {
            String networkBlock = networkMatcher.group();
            java.util.regex.Pattern ssid = java.util.regex.Pattern.compile(strO);
            java.util.regex.Matcher ssidMatcher = ssid.matcher(networkBlock);
            if (ssidMatcher.find()) {
                //wifiInfo = new WifiInfo();
                String name = ssidMatcher.group(1);
                //wifiInfo.setName(ssidMatcher.group(1));
                HashMap<String, Object> map = new HashMap<>();
                map.put("name", name);

                java.util.regex.Pattern psk = java.util.regex.Pattern.compile(strO1);
                java.util.regex.Matcher pskMatcher = psk.matcher(networkBlock);

                if (pskMatcher.find()) {
                    //wifiInfo.setPassword(pskMatcher.group(1));
                    map.put("pass", pskMatcher.group(1));

                } else {
                    //wifiInfo.setPassword(getString(R.string.empty_password));
                    map.put("pass","无密码");

                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    strO3 = "<string name=\"ConfigKey\">&quot;" + name + "&quot;([^\"]+)</string>";
                } else {
                    strO3 = "psk=\"([^\"]+)\"";
                }
                java.util.regex.Pattern keymgmt = java.util.regex.Pattern.compile(strO3);
                java.util.regex.Matcher mkeymgmt = keymgmt.matcher(networkBlock);
                if (mkeymgmt.find()) {
                    //wifiInfo.setKeymgmt(mkeymgmt.group(1));
                    map.put("key", mkeymgmt.group(1));

                } else {
                    //wifiInfo.setKeymgmt("");
                    map.put("key", "");

                }
                listmap.add(map);
            }
        }
    }

    public class Recyclerview1Adapter extends RecyclerView.Adapter<Recyclerview1Adapter.ViewHolder> {
        ArrayList<HashMap<String, Object>> _data;
        public Recyclerview1Adapter(ArrayList<HashMap<String, Object>> _arr) {
            _data = _arr;
        }

        @NotNull
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater _inflater = (LayoutInflater) parent.getContext().getSystemService(LAYOUT_INFLATER_SERVICE);
            View _v = _inflater.inflate(R.layout.item_wifi, null);
            RecyclerView.LayoutParams _lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            _v.setLayoutParams(_lp);
            return new ViewHolder(_v);
        }

        @Override
        public void onBindViewHolder(ViewHolder _holder, final int _position) {
            View _view = _holder.itemView;

            final MaterialCardView cardview1 = _view.findViewById(R.id.cardview1);
            final TextView textview3 = _view.findViewById(R.id.textview3);
            final TextView textview1 = _view.findViewById(R.id.textview1);
            final TextView textview2 = _view.findViewById(R.id.textview2);

            textview1.setText(getString(R.string.名称).concat(String.valueOf(_data.get((int)_position).get("name"))));
            textview2.setText(getString(R.string.密码).concat(String.valueOf(_data.get((int)_position).get("pass"))));
            textview3.setText(_data.get((int)_position).get("name").toString().substring((int)(0), (int)(1)).toUpperCase());

            cardview1.setOnClickListener(v -> {
                final AlertDialog mDialog = new MaterialAlertDialogBuilder(WifiActivity.this)
                        .setPositiveButton(R.string.复制密码, null)
                        .setNegativeButton(R.string.复制名称, null)
                        .create();
                mDialog.setTitle(R.string.复制);
                View contentView = View.inflate(WifiActivity.this, R.layout.dialog_wifi,null);
                mDialog.setView(contentView);
                TextInputEditText textInputEditText1 = contentView.findViewById(R.id.textInputEditText1);
                TextInputEditText textInputEditText2 = contentView.findViewById(R.id.textInputEditText2);
                textInputEditText1.setText(String.valueOf(_data.get((int)_position).get("name")));
                textInputEditText2.setText(String.valueOf(_data.get((int)_position).get("pass")));
                mDialog.setOnShowListener(dialog -> {
                    Button positiveButton = mDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                    Button negativeButton = mDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                    positiveButton.setOnClickListener(v1 -> {
                        mDialog.dismiss();
                        ((ClipboardManager) v.getContext().getSystemService(CLIPBOARD_SERVICE)).setPrimaryClip(ClipData.newPlainText("clipboard", textInputEditText1.getText().toString()));
                        Alerter.create((Activity) v.getContext())
                                .setTitle(R.string.复制成功)
                                .setText(R.string.已成功将内容复制到剪切板)
                                .setBackgroundColorInt(getResources().getColor(R.color.success))
                                .show();
                    });
                    negativeButton.setOnClickListener(v1 -> {
                        mDialog.dismiss();
                        ((ClipboardManager) v.getContext().getSystemService(CLIPBOARD_SERVICE)).setPrimaryClip(ClipData.newPlainText("clipboard", textInputEditText2.getText().toString()));
                        Alerter.create((Activity) v.getContext())
                                .setTitle(R.string.复制成功)
                                .setText(R.string.已成功将内容复制到剪切板)
                                .setBackgroundColorInt(getResources().getColor(R.color.success))
                                .show();
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