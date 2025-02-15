package com.shixin.app;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.transition.TransitionManager;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
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
import me.wcy.lrcview.LrcView;

import static com.shixin.app.utils.Utils.Download;
import static com.shixin.app.utils.Utils.JieQu;
import static com.shixin.app.utils.Utils.LoadingDialog;
import static com.shixin.app.utils.Utils.isVPNConnected;
import static com.shixin.app.utils.Utils.loadDialog;

public class MusicActivity extends AppCompatActivity {

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
    private HashMap<String, Object> music = new HashMap<>();
    private ArrayList<HashMap<String, Object>> listmap = new ArrayList<>();
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);
        ButterKnife.bind(this);

        ImmersionBar.with(this)
                .fitsSystemWindows(true)
                .statusBarColor(R.color.appbarColor)
                .navigationBarColor(R.color.backgroundColor)
                .autoDarkModeEnable(true)
                .init();

        toolbar.setTitle(getString(R.string.音乐搜索器));
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
                textInputLayout.setError(getString(R.string.请输入歌曲或者歌手名称));
                textInputLayout.setErrorEnabled(true);
            }else {
                if (!isVPNConnected(MusicActivity.this)) {
                    LoadingDialog(MusicActivity.this);
                    HttpRequest.build(MusicActivity.this, "https://search.kuwo.cn/r.s?pn=0&rn=30&all=" + textInputEditText.getText() + "&ft=music&newsearch=1&alflac=1&itemset=web_2013&client=kt&cluster=0&vermerge=1&rformat=json&encoding=utf8&show_copyright_off=1&pcmp4=1&ver=mbox&plat=pc&vipver=MUSIC_9.1.1.2_BCS2&devid=38668888&newver=1&issubtitle=1&pcjson=1")
                            .addHeaders("Charset", "UTF-8")
                            .setResponseListener(new ResponseListener() {
                                @Override
                                public void onResponse(String response, Exception error) {
                                    loadDialog.dismiss();
                                    try {
                                        map.clear();
                                        listmap.clear();
                                        map = new Gson().fromJson(response, new TypeToken<HashMap<String, Object>>() {}.getType());
                                        listmap = new Gson().fromJson(new Gson().toJson(map.get("abslist")), new TypeToken<ArrayList<HashMap<String, Object>>>() {}.getType());
                                        TransitionManager.beginDelayedTransition(root, new androidx.transition.AutoTransition());
                                        rv.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
                                        rv.setAdapter(new Recyclerview1Adapter(listmap));
                                        rv.getAdapter().notifyDataSetChanged();
                                    } catch (Exception e) {
                                    }
                                }
                            }).doGet();
                }
            }
        });

    }

    private Handler handler = new Handler();
    private SeekBar seekBar;
    private LrcView lrcView;
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (mediaPlayer.isPlaying()) {
                long time = mediaPlayer.getCurrentPosition();
                lrcView.updateTime(time);
                seekBar.setProgress((int) time);
            }
            handler.postDelayed(this, 300);
        }
    };

    public class Recyclerview1Adapter extends RecyclerView.Adapter<Recyclerview1Adapter.ViewHolder> {
        ArrayList<HashMap<String, Object>> _data;
        public Recyclerview1Adapter(ArrayList<HashMap<String, Object>> _arr) {
            _data = _arr;
        }

        @Override
        public @NotNull ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater _inflater = (LayoutInflater) parent.getContext().getSystemService(LAYOUT_INFLATER_SERVICE);
            View _v = _inflater.inflate(R.layout.item_music, null);
            RecyclerView.LayoutParams _lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            _v.setLayoutParams(_lp);
            return new Recyclerview1Adapter.ViewHolder(_v);
        }

        @Override
        public void onBindViewHolder(ViewHolder _holder, final int _position) {
            View _view = _holder.itemView;

            final MaterialCardView cardview1 = _view.findViewById(R.id.cardview1);
            final TextView song = _view.findViewById(R.id.song);
            final TextView singer = _view.findViewById(R.id.singer);

            song.setText((CharSequence) _data.get(_position).get("SONGNAME"));
            singer.setText((CharSequence) _data.get(_position).get("ARTIST"));
            cardview1.setOnClickListener(v -> {
                final AlertDialog mDialog = new MaterialAlertDialogBuilder(MusicActivity.this)
                        .create();
                View contentView = View.inflate(MusicActivity.this, R.layout.dialog_music,null);
                mDialog.setView(contentView);
                mDialog.setOnDismissListener(dialog -> {
                    lrcView.updateTime(0);
                    mediaPlayer.reset();
                    handler.removeCallbacks(runnable);
                });
                final MaterialButton button1 = contentView.findViewById(R.id.button1);
                final MaterialButton button2 = contentView.findViewById(R.id.button2);
                final TextView name1 = contentView.findViewById(R.id.name);
                final TextView name2 = contentView.findViewById(R.id.singer);
                seekBar = contentView.findViewById(R.id.seekbar);
                lrcView = contentView.findViewById(R.id.lrcview);
                if (!isVPNConnected(MusicActivity.this)) {
                    LoadingDialog(MusicActivity.this);
                    HttpRequest.build(MusicActivity.this, "http://m.kuwo.cn/newh5/singles/songinfoandlrc?musicId=" + JieQu(MusicActivity.this, (String) _data.get(_position).get("MUSICRID") + "\"","MUSIC_","\"") + "&httpsStatus=1&reqId=969ba290-4b49-11eb-8db2-ebd372233623")
                            .addHeaders("Charset", "UTF-8")
                            .setResponseListener(new ResponseListener() {
                                @Override
                                public void onResponse(String response, Exception error) {
                                    try {
                                        HashMap<String, Object> info = new Gson().fromJson(response, new TypeToken<HashMap<String, Object>>() {}.getType());
                                        HashMap<String, Object> data = new Gson().fromJson(new Gson().toJson(info.get("data")), new TypeToken<HashMap<String, Object>>() {}.getType());
                                        ArrayList<HashMap<String, Object>> lrc = new Gson().fromJson(new Gson().toJson(data.get("lrclist")), new TypeToken<ArrayList<HashMap<String, Object>>>(){}.getType());
                                        HashMap<String, Object> songinfo = new Gson().fromJson(new Gson().toJson(data.get("songinfo")), new TypeToken<HashMap<String, Object>>(){}.getType());
                                        name1.setText((CharSequence) songinfo.get("songName"));
                                        name2.setText((CharSequence) songinfo.get("artist"));
                                        //Glide.with(MusicActivity.this).load(songinfo.get("pic")).into(img);
                                        HttpRequest.build(MusicActivity.this, "https://kuwo.cn/url?format=mp3&rid=" + songinfo.get("id") + "&response=url&type=convert_url3&br=128kmp3&from=web&t=1609141904970&httpsStatus=1&reqId=87e511b1-48e1-11eb-adc3-bdfbd8d9306e")
                                                .setResponseListener(new ResponseListener() {
                                                    @Override
                                                    public void onResponse(String response, Exception error) {
                                                        try {
                                                            music = new Gson().fromJson(response, new TypeToken<HashMap<String, Object>>() {}.getType());
                                                            mediaPlayer.reset();
                                                            mediaPlayer.setDataSource((String) music.get("url"));
                                                            mediaPlayer.prepareAsync();
                                                            mediaPlayer.setOnPreparedListener(media_player -> {
                                                                loadDialog.dismiss();
                                                                //play.setImageResource(R.drawable.ic_twotone_pause_circle_filled_24);
                                                                mediaPlayer.start();
                                                                seekBar.setMax(mediaPlayer.getDuration());
                                                                seekBar.setProgress(0);
                                                                seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                                                                    @Override
                                                                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                                                    }

                                                                    @Override
                                                                    public void onStartTrackingTouch(SeekBar seekBar) {
                                                                    }

                                                                    @Override
                                                                    public void onStopTrackingTouch(SeekBar seekBar) {
                                                                        lrcView.updateTime(seekBar.getProgress());
                                                                        mediaPlayer.seekTo(seekBar.getProgress());
                                                                    }
                                                                });
                                                                mDialog.show();
                                                                handler.post(runnable);
                                                            });
                                                            mediaPlayer.setOnCompletionListener(media_player -> {
                                                                mDialog.dismiss();
                                                            });
                                                        } catch (Exception e) {
                                                            loadDialog.dismiss();
                                                        }
                                                    }
                                                }).doGet();
                                    } catch (Exception e) {
                                        loadDialog.dismiss();
                                    }
                                }
                            }).doGet();
                }
                if (!isVPNConnected(MusicActivity.this)) {
                    HttpRequest.build(MusicActivity.this, "http://iecoxe.top:5000/v1/kuwo/lyric?rid=" + JieQu(MusicActivity.this, (String) _data.get(_position).get("MUSICRID") + "\"","MUSIC_","\""))
                            .addHeaders("Charset", "UTF-8")
                            .setResponseListener(new ResponseListener() {
                                @Override
                                public void onResponse(String response, Exception error) {
                                    try {
                                        HashMap<String, Object> lrc = new Gson().fromJson(response, new TypeToken<HashMap<String, Object>>() {}.getType());
                                        lrcView.loadLrc((String) lrc.get("lyric_str"));
                                    } catch (Exception e) {
                                        loadDialog.dismiss();
                                    }
                                }
                            }).doGet();
                }
                button1.setText(R.string.取消);
                button1.setBackgroundColor(getResources().getColor(R.color.itemBackColor));
                button2.setText(R.string.下载);
                button2.setBackgroundColor(getResources().getColor(R.color.zts));
                button1.setOnClickListener(v1 -> {
                    mDialog.dismiss();
                });
                button2.setOnClickListener(v1 -> {
                    Download(MusicActivity.this, getString(R.string.保存歌曲), getString(R.string.音乐保存路径), String.valueOf(music.get("url")), "/噬心工具箱/音乐搜索器/", name1.getText() +"-"+name2.getText() + ".mp3");
                });
                WindowManager.LayoutParams layoutParams = mDialog.getWindow().getAttributes();
                //layoutParams.width = getResources().getDisplayMetrics().widthPixels / 10 * 9;
                layoutParams.height = getResources().getDisplayMetrics().heightPixels / 10 * 8;
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