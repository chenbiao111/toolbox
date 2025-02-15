package com.shixin.app;

import android.app.Activity;
import android.content.ClipData;
import android.content.ComponentName;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.gyf.immersionbar.ImmersionBar;
import com.shixin.app.utils.BitmapSlicer;
import com.shixin.app.utils.FileUtil;
import com.shixin.app.utils.NinePicBitmapSlicer;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

import static com.shixin.app.utils.Utils.LoadingDialog;
import static com.shixin.app.utils.Utils.loadDialog;
import static com.shixin.app.utils.Utils.startUCrop;

public class PictureNineActivity extends AppCompatActivity {

    @BindView(R.id.root)
    ViewGroup root;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    public final int REQ_CD_IMAGE = 101;
    private Intent image = new Intent(Intent.ACTION_GET_CONTENT);
    private BitmapSlicer ninePicBitmapSlicer = new NinePicBitmapSlicer();
    private Bitmap bitmap;
    private BitmapSlicer bitmapSlicer = ninePicBitmapSlicer;
    private List<Bitmap> lastDesBitmaps;
    private List<ImageView> ninePicImageViews = new ArrayList<>();
    private List<ImageView> currentImageViewList = ninePicImageViews;
    private View progressView;
    private View resultView;
    private TextView resultTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_nine);
        ButterKnife.bind(this);

        ImmersionBar.with(this)
                .fitsSystemWindows(true)
                .statusBarColor(R.color.appbarColor)
                .navigationBarColor(R.color.backgroundColor)
                .autoDarkModeEnable(true)
                .init();

        toolbar.setTitle(getString(R.string.九宫格切图));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        image.setType("image/*");
        image.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);

        progressView = findViewById(R.id.layout_progress);
        resultView = findViewById(R.id.layout_result);
        resultTv = findViewById(R.id.tv_result);

        ninePicImageViews.add(findViewById(R.id.iv_image1));
        ninePicImageViews.add(findViewById(R.id.iv_image2));
        ninePicImageViews.add(findViewById(R.id.iv_image3));
        ninePicImageViews.add(findViewById(R.id.iv_image4));
        ninePicImageViews.add(findViewById(R.id.iv_image5));
        ninePicImageViews.add(findViewById(R.id.iv_image6));
        ninePicImageViews.add(findViewById(R.id.iv_image7));
        ninePicImageViews.add(findViewById(R.id.iv_image8));
        ninePicImageViews.add(findViewById(R.id.iv_image9));

    }

    public void choose(View v){
        startActivityForResult(image, REQ_CD_IMAGE);
    }

    public void save(View v) {
        if (lastDesBitmaps == null) {
            //showToast("请先选择图片");
            return;
        }
        //progressView.setVisibility(View.VISIBLE);
        LoadingDialog(PictureNineActivity.this);
        final String time = new SimpleDateFormat("HHmmss").format(new Date());
        if (!FileUtil.isExistFile(FileUtil.getExternalStorageDir().concat("/噬心工具箱/九宫格切图/").concat(time).concat("/"))) {
            FileUtil.makeDir(FileUtil.getExternalStorageDir().concat("/噬心工具箱/九宫格切图/").concat(time).concat("/"));
        }
        final File parent = new File(FileUtil.getExternalStorageDir().concat("/噬心工具箱/九宫格切图/").concat(time).concat("/"));
        final String prefix = System.currentTimeMillis() + "";
        final ArrayList<File> slices = new ArrayList<>();
        if (!parent.exists()) {
            parent.mkdirs();
        }
        Disposable disposable = Observable.fromArray(lastDesBitmaps.toArray(new Bitmap[]{}))
                .map(bitmap -> {
                    int index = lastDesBitmaps.indexOf(bitmap);
                    File file = new File(parent, prefix + "_" + (index + 1) + ".jpg");
                    OutputStream os = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
                    os.close();
                    return file;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(file -> {
                    Uri uri = Uri.fromFile(file);
                    //Log.d("xsm-save-files", uri.toString());
                    slices.add(file);
                    sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
                }, throwable -> {
                    throwable.printStackTrace();
                    //Toast.makeText(MainActivity.this, "导出失败", Toast.LENGTH_SHORT).show();
                    //progressView.setVisibility(View.GONE);
                    loadDialog.dismiss();
                    resultView.setVisibility(View.GONE);
                }, () -> {
                    //progressView.setVisibility(View.GONE);
                    loadDialog.dismiss();
                    resultView.setVisibility(View.VISIBLE);
                    resultTv.setText(Html.fromHtml(getString(R.string.切片已保存) + parent.getAbsolutePath() + "</font><font color=\"#868686\"></font>"));
                    resultTv.setTag(slices);
                });
    }

    public void shareSlices(View v) {
        if (v.getTag() != null) {
            final ArrayList<File> slices = (ArrayList<File>) v.getTag();
            final ArrayList<Uri> sliceUris = new ArrayList<>();
            Observable.fromArray(slices.toArray(new File[]{}))
                    .map(file -> {
                        Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                                , new String[]{MediaStore.Images.ImageColumns._ID, MediaStore.Images.ImageColumns.DATA}
                                , MediaStore.Images.ImageColumns.DATA + "=?"
                                , new String[]{file.getAbsolutePath()}
                                , null);
                        if (cursor != null) {
                            if (cursor.getCount() != 0) {
                                cursor.moveToFirst();
                                int id = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.ImageColumns._ID));
                                String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA));
                                cursor.close();
                                //Log.d("xsm-read-media-database", "id = " + id + ", path = " + path);
                                return Uri.parse(MediaStore.Images.Media.EXTERNAL_CONTENT_URI.toString() + "/" + id);
                            } else {
                                cursor.close();
                                //Log.w("xsm-read-media-database", "cursor is empty");
                                return null;
                            }
                        } else {
                            //Log.e("xsm-read-media-database", "cursor is null");
                            return null;
                        }
                    }).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<Uri>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                            progressView.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onNext(Uri uri) {
                            //Log.d("xsm-collect-slice-uri", uri.toString());
                            sliceUris.add(uri);
                        }

                        @Override
                        public void onError(Throwable e) {
                            progressView.setVisibility(View.GONE);
                        }

                        @Override
                        public void onComplete() {
                            //Log.d("xsm-start-wechat", "start wechat with " + slices.size() + " pictures.");
                            progressView.setVisibility(View.GONE);
                            Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            intent.setType("image/jpeg");
                            ComponentName comp = new ComponentName("com.tencent.mm",
                                    "com.tencent.mm.ui.tools.ShareToTimeLineUI");
                            intent.setComponent(comp);
                            intent.setType("image/*");
                            intent.putExtra("Kdescription", "");
                            intent.putExtra(Intent.EXTRA_STREAM, sliceUris);
                            startActivity(intent);
                        }
                    });

        }
    }

    private BitmapSlicer.BitmapSliceListener bitmapSliceListener = new BitmapSlicer.BitmapSliceListener() {
        @Override
        public void onSliceSuccess(Bitmap srcBitmap, List<Bitmap> desBitmaps) {
            srcBitmap.recycle();
            bitmapSlicer.setSrcBitmap(null);
            for (ImageView imageView : ninePicImageViews) {
                imageView.setImageBitmap(null);
                imageView.setVisibility(View.GONE);
            }
            if (lastDesBitmaps != null) {
                for (Bitmap lastDesBitmap : lastDesBitmaps) {
                    lastDesBitmap.recycle();
                }
            }
            lastDesBitmaps = null;
            for (int i = 0; i < currentImageViewList.size(); i++) {
                currentImageViewList.get(i).setImageBitmap(desBitmaps.get(i));
                currentImageViewList.get(i).setVisibility(View.VISIBLE);
            }
            lastDesBitmaps = desBitmaps;
            progressView.setVisibility(View.GONE);
            resultView.setVisibility(View.GONE);
        }

        @Override
        public void onSliceFailed() {
            //Toast.makeText(MainActivity.this, "切片失败", Toast.LENGTH_SHORT).show();
            progressView.setVisibility(View.GONE);
            resultView.setVisibility(View.GONE);
        }
    };

    @Override
    protected void onActivityResult(int _requestCode, int _resultCode, Intent _data) {

        super.onActivityResult(_requestCode, _resultCode, _data);

        if (_resultCode == RESULT_OK && _requestCode == UCrop.REQUEST_CROP) {
            final Uri resultUri = UCrop.getOutput(_data);
            bitmap = FileUtil.decodeSampleBitmapFromPath(resultUri.getPath(), 1024, 1024);
            bitmapSlicer.setSrcBitmap(bitmap)
                    .registerListener(bitmapSliceListener)
                    .slice();
            progressView.setVisibility(View.VISIBLE);
        } else if (_resultCode == UCrop.RESULT_ERROR) {
            final Throwable cropError = UCrop.getError(_data);
        }

        switch (_requestCode) {
            case REQ_CD_IMAGE:
                if (_resultCode == Activity.RESULT_OK) {
                    ArrayList<String> _filePath = new ArrayList<>();
                    if (_data != null) {
                        if (_data.getClipData() != null) {
                            for (int _index = 0; _index < _data.getClipData().getItemCount(); _index++) {
                                ClipData.Item _item = _data.getClipData().getItemAt(_index);
                                _filePath.add(FileUtil.convertUriToFilePath(getApplicationContext(), _item.getUri()));
                            }
                        }
                        else {
                            _filePath.add(FileUtil.convertUriToFilePath(getApplicationContext(), _data.getData()));
                        }
                    }
                    startUCrop(PictureNineActivity.this, _filePath.get(0),1,1);
                }
                break;
            default:
                break;
        }
    }
}