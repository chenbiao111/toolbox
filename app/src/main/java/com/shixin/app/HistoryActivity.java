package com.shixin.app;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.transition.TransitionManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.google.android.material.card.MaterialCardView;
import com.gyf.immersionbar.ImmersionBar;
import com.kongzue.baseokhttp.HttpRequest;
import com.kongzue.baseokhttp.listener.ResponseListener;
import com.shixin.app.utils.Utils;

import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.shixin.app.utils.Utils.JieQu;
import static com.shixin.app.utils.Utils.LoadingDialog;
import static com.shixin.app.utils.Utils.loadDialog;

public class HistoryActivity extends AppCompatActivity {

    @BindView(R.id.root)
    ViewGroup root;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.rv)
    RecyclerView rv;

    private HashMap<String, Object> map = new HashMap<>();
    private final ArrayList<HashMap<String, Object>> listmap = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        ButterKnife.bind(this);

        ImmersionBar.with(this)
                .fitsSystemWindows(true)
                .statusBarColor(R.color.appbarColor)
                .navigationBarColor(R.color.backgroundColor)
                .autoDarkModeEnable(true)
                .init();

        toolbar.setTitle(getString(R.string.历史上的今天));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        rv.setItemViewCacheSize(9999);

        if (!Utils.isVPNConnected(this)) {
            LoadingDialog(HistoryActivity.this);
            HttpRequest.build(this,"http://hao.360.com/histoday/")
                    .addHeaders("Charset","UTF-8")
                    .setResponseListener(new ResponseListener() {
                        @Override
                        public void onResponse(String response, Exception error) {
                            loadDialog.dismiss();
                            try {
                                String[] data = JieQu(HistoryActivity.this,response,"<dl class=\"tih-item open\">","<div class=\"doc-ft\">").split("<dl class=\"tih-item\">");
                                for (String a: data){
                                    map = new HashMap<>();
                                    map.put("time",JieQu(HistoryActivity.this, a,"</em>. ","-"));
                                    //Toast.makeText(DayActivity.this,"http" + JieQu(DayActivity.this, a,"src=\"","\""),Toast.LENGTH_SHORT).show();
                                    map.put("name",JieQu(HistoryActivity.this, a, JieQu(HistoryActivity.this, a,"</em>. ","-") + "-","</dt>"));
                                    map.put("img","http:" + JieQu(HistoryActivity.this, a,"src=\"","\""));
                                    map.put("gk",JieQu(HistoryActivity.this, a,"<div class=\"desc\">","</div>"));
                                    listmap.add(map);
                                }
                                TransitionManager.beginDelayedTransition(root, new androidx.transition.AutoTransition());
                                rv.setAdapter(new Recyclerview1Adapter(listmap));
                                rv.getAdapter().notifyDataSetChanged();
                            } catch (Exception e){
                            }
                        }
                    }).doGet();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_history,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem){
        final String title = (String) menuItem.getTitle();
        if (title.equals(getString(R.string.切换日期))){
            final Calendar c = Calendar.getInstance();
            int mYear = c.get(Calendar.YEAR);
            int mMonth = c.get(Calendar.MONTH);
            int mDay = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(HistoryActivity.this, (view, year, monthOfYear, dayOfMonth) -> {
                DecimalFormat df = new DecimalFormat("00");
                //Utils.TextDialog(DayActivity.this, "事件概况", df.format(dayOfMonth) + "-" + df.format((monthOfYear + 1)) + "-" + year);
                if (!Utils.isVPNConnected(HistoryActivity.this)) {
                    LoadingDialog(HistoryActivity.this);
                    HttpRequest.build(HistoryActivity.this, "http://hao.360.com/histoday/" + df.format((monthOfYear + 1)) + df.format(dayOfMonth) + ".html")
                            .addHeaders("Charset", "UTF-8")
                            .setResponseListener(new ResponseListener() {
                                @Override
                                public void onResponse(String response, Exception error) {
                                    loadDialog.dismiss();
                                    try {
                                        map.clear();
                                        listmap.clear();
                                        String[] data = JieQu(HistoryActivity.this, response, "<dl class=\"tih-item open\">", "<div class=\"doc-ft\">").split("<dl class=\"tih-item\">");
                                        for (String a : data) {
                                            map = new HashMap<>();
                                            map.put("time", JieQu(HistoryActivity.this, a, "</em>. ", "-"));
                                            map.put("name", JieQu(HistoryActivity.this, a, JieQu(HistoryActivity.this, a, "</em>. ", "-") + "-", "</dt>"));
                                            map.put("img", "http:" + JieQu(HistoryActivity.this, a, "src=\"", "\""));
                                            map.put("gk", JieQu(HistoryActivity.this, a, "<div class=\"desc\">", "</div>"));
                                            listmap.add(map);
                                        }
                                        TransitionManager.beginDelayedTransition(root, new androidx.transition.AutoTransition());
                                        rv.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
                                        rv.setAdapter(new Recyclerview1Adapter(listmap));
                                        rv.getAdapter().notifyDataSetChanged();
                                    } catch (Exception e) {
                                    }
                                }
                            }).doGet();
                }
            }, mYear, mMonth, mDay);
            datePickerDialog.show();
        }
        return super.onOptionsItemSelected(menuItem);
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
            View _v = _inflater.inflate(R.layout.item_day, null);
            RecyclerView.LayoutParams _lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            _v.setLayoutParams(_lp);
            return new ViewHolder(_v);
        }

        @Override
        public void onBindViewHolder(ViewHolder _holder, final int _position) {
            View _view = _holder.itemView;

            final MaterialCardView cardview1 = (MaterialCardView) _view.findViewById(R.id.cardview1);
            final ImageView tp1 = (ImageView) _view.findViewById(R.id.tp1);
            final TextView time = (TextView) _view.findViewById(R.id.time);
            final TextView name = (TextView) _view.findViewById(R.id.song);

            Glide.with(HistoryActivity.this).load(_data.get(_position).get("img")).thumbnail(0.1f).fitCenter().priority(Priority.IMMEDIATE).into(tp1);
            time.setText((CharSequence) _data.get(_position).get("time"));
            name.setText((CharSequence) _data.get(_position).get("name"));
            cardview1.setOnClickListener(_view1 -> {
                Utils.CopyDialog(HistoryActivity.this, getString(R.string.事件概况), _data.get(_position).get("gk").toString().trim());
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