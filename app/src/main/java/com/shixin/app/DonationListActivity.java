package com.shixin.app;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.TransitionManager;

import com.google.android.material.button.MaterialButton;
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

import static com.shixin.app.utils.Utils.LoadingDialog;
import static com.shixin.app.utils.Utils.loadDialog;

public class DonationListActivity extends AppCompatActivity {

    @BindView(R.id.root)
    ViewGroup root;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.rv)
    RecyclerView rv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donation_list);
        ButterKnife.bind(this);

        ImmersionBar.with(this)
                .fitsSystemWindows(true)
                .statusBarColor(R.color.appbarColor)
                .navigationBarColor(R.color.backgroundColor)
                .autoDarkModeEnable(true)
                .init();

        toolbar.setTitle(getString(R.string.捐赠榜单));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        rv.setItemViewCacheSize(9999);

        LoadingDialog(DonationListActivity.this);
        HttpRequest.build(DonationListActivity.this, "")
                .addHeaders("Charset", "UTF-8")
                .setResponseListener(new ResponseListener() {
                    @Override
                    public void onResponse(String response, Exception error) {
                        loadDialog.dismiss();
                        try {
                            ArrayList<HashMap<String, Object>> listmap = new Gson().fromJson(response, new TypeToken<ArrayList<HashMap<String, Object>>>() {}.getType());
                            TransitionManager.beginDelayedTransition(rv, new androidx.transition.AutoTransition());
                            rv.setAdapter(new Recyclerview1Adapter(listmap));
                            rv.getAdapter().notifyDataSetChanged();
                        } catch (Exception e){
                        }
                    }
                }).doGet();

    }

    public class Recyclerview1Adapter extends RecyclerView.Adapter<Recyclerview1Adapter.ViewHolder> {
        ArrayList<HashMap<String, Object>> _data;
        public Recyclerview1Adapter(ArrayList<HashMap<String, Object>> _arr) {
            _data = _arr;
        }

        @Override
        public @NotNull Recyclerview1Adapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater _inflater = (LayoutInflater) parent.getContext().getSystemService(LAYOUT_INFLATER_SERVICE);
            View _v = _inflater.inflate(R.layout.item_donation_list, null);
            RecyclerView.LayoutParams _lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            _v.setLayoutParams(_lp);
            return new Recyclerview1Adapter.ViewHolder(_v);
        }

        @Override
        public void onBindViewHolder(Recyclerview1Adapter.ViewHolder _holder, final int _position) {
            View _view = _holder.itemView;

            final MaterialButton button1 = _view.findViewById(R.id.button1);
            final MaterialButton button2 = _view.findViewById(R.id.button2);
            final MaterialButton button3 = _view.findViewById(R.id.button3);

            button1.setText((CharSequence) _data.get(_position).get("昵称"));
            button2.setText((CharSequence) _data.get(_position).get("联系方式"));
            button3.setText((CharSequence) _data.get(_position).get("捐赠金额"));

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