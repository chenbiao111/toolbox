package com.shixin.app;

import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.transition.AutoTransition;
import androidx.transition.TransitionManager;

import com.google.android.material.button.MaterialButton;
import com.gyf.immersionbar.ImmersionBar;
import com.shixin.app.utils.FileUtil;
import com.shixin.app.utils.MediaScanner;
import com.tapadoo.alerter.Alerter;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PictureCompressActivity extends AppCompatActivity {

    @BindView(R.id.root)
    ViewGroup root;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.button1)
    MaterialButton button1;
    @BindView(R.id.button2)
    MaterialButton button2;
    @BindView(R.id.image)
    LinearLayout linear;
    @BindView(R.id.tp1)
    ImageView tp1;
    @BindView(R.id.tp2)
    ImageView tp2;
    @BindView(R.id.seekbar1)
    DiscreteSeekBar seekbar1;
    @BindView(R.id.txt1)
    TextView txt1;
    @BindView(R.id.txt2)
    TextView txt2;


    public final int REQ_CD_IMAGE = 101;
    private Intent image = new Intent(Intent.ACTION_GET_CONTENT);
    private Bitmap bitmap = null;
    private MediaScanner mediaScanner;
    private File oldFile;
    private File newFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_compress);
        ButterKnife.bind(this);

        ImmersionBar.with(this)
                .fitsSystemWindows(true)
                .statusBarColor(R.color.appbarColor)
                .navigationBarColor(R.color.backgroundColor)
                .autoDarkModeEnable(true)
                .init();

        toolbar.setTitle(getString(R.string.图片压缩));
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
            if (bitmap == null) {
                Alerter.create(PictureCompressActivity.this)
                        .setTitle(R.string.温馨提示)
                        .setText(R.string.请先选择图片)
                        .setBackgroundColorInt(getResources().getColor(R.color.error))
                        .show();
            }
            else {
                try {
                    final String yourFileName = oldFile.getName();
                    newFile = new CompressHelper.Builder(PictureCompressActivity.this)
                            .setMaxWidth(bitmap.getWidth())
                            .setMaxHeight(bitmap.getHeight())
                            .setQuality((int) seekbar1.getProgress())
                            .setCompressFormat(Bitmap.CompressFormat.JPEG)
                            .setFileName(yourFileName)
                            .setDestinationDirectoryPath(FileUtil.getExternalStorageDir().concat("/噬心工具箱/图片压缩/"))
                            .build()
                            .compressToFile(oldFile);
                    tp2.setImageBitmap(BitmapFactory.decodeFile(newFile.getAbsolutePath()));
                    txt2.setText(String.format("Size : %s", getReadableFileSize(newFile.length())));
                    Alerter.create(PictureCompressActivity.this)
                            .setTitle(R.string.压缩成功)
                            .setText(getString(R.string.已保存到) + FileUtil.getExternalStorageDir().concat("/噬心工具箱/图片压缩/") + yourFileName)
                            .setBackgroundColorInt(getResources().getColor(R.color.success))
                            .show();
                    if (mediaScanner == null) {
                        mediaScanner = new MediaScanner(PictureCompressActivity.this);
                    }
                    mediaScanner.scanFile(FileUtil.getExternalStorageDir().concat("/噬心工具箱/图片压缩/"), "image/*");
                } catch (Exception e){
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int _requestCode, int _resultCode, Intent _data) {
        super.onActivityResult(_requestCode, _resultCode, _data);
        if (_requestCode == REQ_CD_IMAGE) {
            if (_resultCode == Activity.RESULT_OK) {
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
                }

                oldFile = new File(_filePath.get(0));
                TransitionManager.beginDelayedTransition(root, new AutoTransition());
                linear.setVisibility(View.VISIBLE);
                clearImage();
                bitmap = BitmapFactory.decodeFile(_filePath.get(0)).copy(Bitmap.Config.ARGB_8888, true);
                tp1.setImageBitmap(bitmap);
                txt1.setText(String.format("Size : %s", getReadableFileSize(oldFile.length())));

            }
        }
    }

    private void clearImage() {
        tp2.setImageDrawable(null);
        txt2.setText("Size : -");
    }
    public String getReadableFileSize(long size) {
        if (size <= 0) {
            return "0";
        }
        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }
    public static class FileUtils {
        static final String FILES_PATH = "CompressHelper";
        private static final int EOF = -1;
        private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;

        private FileUtils() {
            throw new UnsupportedOperationException("u can't instantiate me...");
        }

        /**
         * 根据文件路径获取文件
         *
         * @param filePath 文件路径
         * @return 文件
         */
        public static File getFileByPath(String filePath) {
            return StringUtil.isSpace(filePath) ? null : new File(filePath);
        }

        /**
         * 判断文件是否存在
         *
         * @param filePath 文件路径
         * @return {@code true}: 存在<br>{@code false}: 不存在
         */
        public static boolean isFileExists(String filePath) {
            return isFileExists(getFileByPath(filePath));
        }

        /**
         * 判断文件是否存在
         *
         * @param file 文件
         * @return {@code true}: 存在<br>{@code false}: 不存在
         */
        public static boolean isFileExists(File file) {
            return file != null && file.exists();
        }


        /**
         * 重命名文件
         *
         * @param filePath 文件路径
         * @param newName  新名称
         * @return {@code true}: 重命名成功<br>{@code false}: 重命名失败
         */
        public static boolean rename(String filePath, String newName) {
            return rename(getFileByPath(filePath), newName);
        }

        /**
         * 重命名文件
         *
         * @param file    文件
         * @param newName 新名称
         * @return {@code true}: 重命名成功<br>{@code false}: 重命名失败
         */
        public static boolean rename(File file, String newName) {
            // 文件为空返回false
            if (file == null) return false;
            // 文件不存在返回false
            if (!file.exists()) return false;
            // 新的文件名为空返回false
            if (StringUtil.isSpace(newName)) return false;
            // 如果文件名没有改变返回true
            if (newName.equals(file.getName())) return true;
            File newFile = new File(file.getParent() + File.separator + newName);
            // 如果重命名的文件已存在返回false
            return !newFile.exists()
                    && file.renameTo(newFile);
        }

        /**
         * 判断是否是目录
         *
         * @param dirPath 目录路径
         * @return {@code true}: 是<br>{@code false}: 否
         */
        public static boolean isDir(String dirPath) {
            return isDir(getFileByPath(dirPath));
        }

        /**
         * 判断是否是目录
         *
         * @param file 文件
         * @return {@code true}: 是<br>{@code false}: 否
         */
        public static boolean isDir(File file) {
            return isFileExists(file) && file.isDirectory();
        }

        /**
         * 判断是否是文件
         *
         * @param filePath 文件路径
         * @return {@code true}: 是<br>{@code false}: 否
         */
        public static boolean isFile(String filePath) {
            return isFile(getFileByPath(filePath));
        }

        /**
         * 判断是否是文件
         *
         * @param file 文件
         * @return {@code true}: 是<br>{@code false}: 否
         */
        public static boolean isFile(File file) {
            return isFileExists(file) && file.isFile();
        }


        /**
         * 重命名文件
         * @param file      文件
         * @param newName   新名字
         * @return          新文件
         */
        public static File renameFile(File file, String newName) {
            File newFile = new File(file.getParent(), newName);
            if (!newFile.equals(file)) {
                if (newFile.exists()) {
                    if (newFile.delete()) {
                        Log.d("FileUtils", "Delete old " + newName + " file");
                    }
                }
                if (file.renameTo(newFile)) {
                    Log.d("FileUtils", "Rename file to " + newName);
                }
            }
            return newFile;
        }


        /**
         * 获取临时文件
         * @param context   上下文
         * @param uri       url
         * @return          临时文件
         * @throws java.io.IOException
         */
        public static File getTempFile(Context context, Uri uri) throws IOException {
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            String fileName = getFileName(context, uri);
            String[] splitName = splitFileName(fileName);
            File tempFile = File.createTempFile(splitName[0], splitName[1]);
            tempFile = renameFile(tempFile, fileName);
            tempFile.deleteOnExit();
            FileOutputStream out = null;
            try {
                out = new FileOutputStream(tempFile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            if (inputStream != null) {
                copy(inputStream, out);
                inputStream.close();
            }

            if (out != null) {
                out.close();
            }
            return tempFile;
        }

        /**
         * 截取文件名称
         * @param fileName  文件名称
         */
        static String[] splitFileName(String fileName) {
            String name = fileName;
            String extension = "";
            int i = fileName.lastIndexOf(".");
            if (i != -1) {
                name = fileName.substring(0, i);
                extension = fileName.substring(i);
            }

            return new String[]{name, extension};
        }

        /**
         * 获取文件名称
         * @param context   上下文
         * @param uri       uri
         * @return          文件名称
         */
        static String getFileName(Context context, Uri uri) {
            String result = null;
            if (uri.getScheme().equals("content")) {
                android.database.Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
                try {
                    if (cursor != null && cursor.moveToFirst()) {
                        result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (cursor != null) {
                        cursor.close();
                    }
                }
            }
            if (result == null) {
                result = uri.getPath();
                int cut = result.lastIndexOf(File.separator);
                if (cut != -1) {
                    result = result.substring(cut + 1);
                }
            }
            return result;
        }

        /**
         * 获取真实的路径
         * @param context   上下文
         * @param uri       uri
         * @return          文件路径
         */
        static String getRealPathFromURI(Context context, Uri uri) {
            android.database.Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
            if (cursor == null) {
                return uri.getPath();
            } else {
                cursor.moveToFirst();
                int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                String realPath = cursor.getString(index);
                cursor.close();
                return realPath;
            }
        }



        static int copy(InputStream input, OutputStream output) throws IOException {
            long count = copyLarge(input, output);
            if (count > Integer.MAX_VALUE) {
                return -1;
            }
            return (int) count;
        }

        static long copyLarge(InputStream input, OutputStream output)
                throws IOException {
            return copyLarge(input, output, new byte[DEFAULT_BUFFER_SIZE]);
        }

        static long copyLarge(InputStream input, OutputStream output, byte[] buffer)
                throws IOException {
            long count = 0;
            int n;
            while (EOF != (n = input.read(buffer))) {
                output.write(buffer, 0, n);
                count += n;
            }
            return count;
        }
    }
    public static class StringUtil {

        private StringUtil() {
            throw new UnsupportedOperationException("u can't instantiate me...");
        }

        /**
         * 判断字符串是否为null或长度为0
         *
         * @param s 待校验字符串
         * @return {@code true}: 空<br> {@code false}: 不为空
         */
        public static boolean isEmpty(CharSequence s) {
            return s == null || s.length() == 0;
        }

        /**
         * 判断字符串是否为null或全为空格
         *
         * @param s 待校验字符串
         * @return {@code true}: null或全空格<br> {@code false}: 不为null且不全空格
         */
        public static boolean isSpace(String s) {
            return (s == null || s.trim().length() == 0);
        }

        /**
         * 判断两字符串是否相等
         *
         * @param a 待校验字符串a
         * @param b 待校验字符串b
         * @return {@code true}: 相等<br>{@code false}: 不相等
         */
        public static boolean equals(CharSequence a, CharSequence b) {
            if (a == b) return true;
            int length;
            if (a != null && b != null && (length = a.length()) == b.length()) {
                if (a instanceof String && b instanceof String) {
                    return a.equals(b);
                } else {
                    for (int i = 0; i < length; i++) {
                        if (a.charAt(i) != b.charAt(i)) return false;
                    }
                    return true;
                }
            }
            return false;
        }

        /**
         * 判断两字符串忽略大小写是否相等
         *
         * @param a 待校验字符串a
         * @param b 待校验字符串b
         * @return {@code true}: 相等<br>{@code false}: 不相等
         */
        public static boolean equalsIgnoreCase(String a, String b) {
            return (a == b) || (b != null) && (a.length() == b.length()) && a.regionMatches(true, 0, b, 0, b.length());
        }

        /**
         * null转为长度为0的字符串
         *
         * @param s 待转字符串
         * @return s为null转为长度为0字符串，否则不改变
         */
        public static String null2Length0(String s) {
            return s == null ? "" : s;
        }

        /**
         * 返回字符串长度
         *
         * @param s 字符串
         * @return null返回0，其他返回自身长度
         */
        public static int length(CharSequence s) {
            return s == null ? 0 : s.length();
        }

        /**
         * 首字母大写
         *
         * @param s 待转字符串
         * @return 首字母大写字符串
         */
        public static String upperFirstLetter(String s) {
            if (isEmpty(s) || !Character.isLowerCase(s.charAt(0))) return s;
            return String.valueOf((char) (s.charAt(0) - 32)) + s.substring(1);
        }

        /**
         * 首字母小写
         *
         * @param s 待转字符串
         * @return 首字母小写字符串
         */
        public static String lowerFirstLetter(String s) {
            if (isEmpty(s) || !Character.isUpperCase(s.charAt(0))) return s;
            return String.valueOf((char) (s.charAt(0) + 32)) + s.substring(1);
        }

        /**
         * 反转字符串
         *
         * @param s 待反转字符串
         * @return 反转字符串
         */
        public static String reverse(String s) {
            int len = length(s);
            if (len <= 1) return s;
            int mid = len >> 1;
            char[] chars = s.toCharArray();
            char c;
            for (int i = 0; i < mid; ++i) {
                c = chars[i];
                chars[i] = chars[len - i - 1];
                chars[len - i - 1] = c;
            }
            return new String(chars);
        }

        /**
         * 转化为半角字符
         *
         * @param s 待转字符串
         * @return 半角字符串
         */
        public static String toDBC(String s) {
            if (isEmpty(s)) return s;
            char[] chars = s.toCharArray();
            for (int i = 0, len = chars.length; i < len; i++) {
                if (chars[i] == 12288) {
                    chars[i] = ' ';
                } else if (65281 <= chars[i] && chars[i] <= 65374) {
                    chars[i] = (char) (chars[i] - 65248);
                } else {
                    chars[i] = chars[i];
                }
            }
            return new String(chars);
        }

        /**
         * 转化为全角字符
         *
         * @param s 待转字符串
         * @return 全角字符串
         */
        public static String toSBC(String s) {
            if (isEmpty(s)) return s;
            char[] chars = s.toCharArray();
            for (int i = 0, len = chars.length; i < len; i++) {
                if (chars[i] == ' ') {
                    chars[i] = (char) 12288;
                } else if (33 <= chars[i] && chars[i] <= 126) {
                    chars[i] = (char) (chars[i] + 65248);
                } else {
                    chars[i] = chars[i];
                }
            }
            return new String(chars);
        }
    }
    public static class ImageUtil {
        /**
         * 计算图片的压缩比率
         *
         * @param options   参数
         * @param reqWidth  目标的宽度
         * @param reqHeight 目标的高度
         * @param pathName  路径
         * @return          计算的SampleSize
         */
        private static int calculateInSampleSize(BitmapFactory.Options options, String pathName, int reqWidth, int reqHeight) {
            // 源图片的高度和宽度
            int height = options.outHeight;
            int width = options.outWidth;
            if (height == -1 || width == -1) {
                try {
                    android.media.ExifInterface exifInterface = new android.media.ExifInterface(pathName);
                    height = exifInterface.getAttributeInt(android.media.ExifInterface.TAG_IMAGE_LENGTH, android.media.ExifInterface.ORIENTATION_NORMAL);//获取图片的高度
                    width = exifInterface.getAttributeInt(android.media.ExifInterface.TAG_IMAGE_WIDTH, android.media.ExifInterface.ORIENTATION_NORMAL);//获取图片的宽度
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            int inSampleSize = 1;
            if (height > reqHeight || width > reqWidth) {
                final int halfHeight = height / 2;
                final int halfWidth = width / 2;
                // Calculate the largest inSampleSize value that is a power of 2 and keeps both
                // height and width larger than the requested height and width.
                while ((halfHeight / inSampleSize) > reqHeight
                        && (halfWidth / inSampleSize) > reqWidth) {
                    inSampleSize *= 2;
                }
            }
            return inSampleSize;
        }


        /**
         * 计算图片的压缩比率
         *
         * @param options   参数
         * @param reqWidth  目标的宽度
         * @param reqHeight 目标的高度
         * @return          计算的SampleSize
         */
        private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
            // 源图片的高度和宽度
            int height = options.outHeight;
            int width = options.outWidth;
            int inSampleSize = 1;
            if (height > reqHeight || width > reqWidth) {
                final int halfHeight = height / 2;
                final int halfWidth = width / 2;
                // Calculate the largest inSampleSize value that is a power of 2 and keeps both
                // height and width larger than the requested height and width.
                while ((halfHeight / inSampleSize) > reqHeight
                        && (halfWidth / inSampleSize) > reqWidth) {
                    inSampleSize *= 2;
                }
            }
            return inSampleSize;
        }

        /**
         * 从Resources中加载图片
         *
         * @param res       Resource
         * @param resId     资源id
         * @param reqWidth  请求宽度
         * @param reqHeight 请求高度
         * @return          Bitmap
         */
        public static Bitmap decodeSampledBitmapFromResource(android.content.res.Resources res, int resId, int reqWidth, int reqHeight) {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true; // 设置成了true,不占用内存，只获取bitmap宽高
            BitmapFactory.decodeResource(res, resId, options); // 读取图片长款
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight); // 调用上面定义的方法计算inSampleSize值
            // 使用获取到的inSampleSize值再次解析图片
            options.inJustDecodeBounds = false;
            Bitmap src = BitmapFactory.decodeResource(res, resId, options); // 载入一个稍大的缩略图
            return createScaleBitmap(src, reqWidth, reqHeight, options.inSampleSize); // 进一步得到目标大小的缩略图
        }

        /**
         * 通过传入的bitmap，进行压缩，得到符合标准的bitmap
         *
         * @param src           Bitmap源图
         * @param dstWidth      宽度
         * @param dstHeight     高度
         * @return              新的Bitmap
         */
        private static Bitmap createScaleBitmap(Bitmap src, int dstWidth, int dstHeight, int inSampleSize) {
            //如果inSampleSize是2的倍数，也就说这个src已经是我们想要的缩略图了，直接返回即可。
            if (inSampleSize % 2 == 0) {
                return src;
            }
            if (src == null){
                return null;
            }
            // 如果是放大图片，filter决定是否平滑，如果是缩小图片，filter无影响，我们这里是缩小图片，所以直接设置为false
            Bitmap dst = Bitmap.createScaledBitmap(src, dstWidth, dstHeight, false);
            if (src != dst) { // 如果没有缩放，那么不回收
                src.recycle(); // 释放Bitmap的native像素数组
            }
            return dst;
        }

        /**
         * 从SD卡上加载图片
         *
         * @param pathName      路径
         * @param reqWidth      请求宽度
         * @param reqHeight     请求高度
         * @return              Bitmap
         */
        public static Bitmap decodeSampledBitmapFromFile(String pathName, int reqWidth, int reqHeight) {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(pathName, options);
            options.inSampleSize = calculateInSampleSize(options, pathName,reqWidth, reqHeight);
            options.inJustDecodeBounds = false;
            Bitmap src = BitmapFactory.decodeFile(pathName, options);
            return createScaleBitmap(src, reqWidth, reqHeight, options.inSampleSize);
        }

        /**
         * 删除临时图片
         * @param path  图片路径
         */
        public static void deleteTempFile(String path){
            File file = new File(path);
            if (file.exists()){
                file.delete();
            }
        }
    }
    public static class BitmapUtil {

        private BitmapUtil() {
            throw new UnsupportedOperationException("u can't instantiate me...");
        }

        static Bitmap getScaledBitmap(Context context, Uri imageUri, float maxWidth, float maxHeight, Bitmap.Config bitmapConfig) {
            String filePath = FileUtils.getRealPathFromURI(context, imageUri);
            Bitmap scaledBitmap = null;

            BitmapFactory.Options options = new BitmapFactory.Options();

            //by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
            //you try the use the bitmap here, you will get null.
            options.inJustDecodeBounds = true;
            Bitmap bmp = BitmapFactory.decodeFile(filePath, options);
            if (bmp == null) {
                InputStream inputStream = null;
                try {
                    inputStream = new FileInputStream(filePath);
                    BitmapFactory.decodeStream(inputStream, null, options);
                    inputStream.close();
                } catch (FileNotFoundException exception) {
                    exception.printStackTrace();
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            }

            int actualHeight = options.outHeight;
            int actualWidth = options.outWidth;

            if (actualHeight == -1 || actualWidth == -1){
                try {
                    ExifInterface exifInterface = new ExifInterface(filePath);
                    actualHeight = exifInterface.getAttributeInt(ExifInterface.TAG_IMAGE_LENGTH, ExifInterface.ORIENTATION_NORMAL);//获取图片的高度
                    actualWidth = exifInterface.getAttributeInt(ExifInterface.TAG_IMAGE_WIDTH, ExifInterface.ORIENTATION_NORMAL);//获取图片的宽度
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (actualWidth <= 0 || actualHeight <= 0) {
                Bitmap bitmap2 = BitmapFactory.decodeFile(filePath);
                if (bitmap2 != null){
                    actualWidth = bitmap2.getWidth();
                    actualHeight = bitmap2.getHeight();
                }else{
                    return null;
                }
            }

            float imgRatio = (float) actualWidth / actualHeight;
            float maxRatio = maxWidth / maxHeight;

            //width and height values are set maintaining the aspect ratio of the image
            if (actualHeight > maxHeight || actualWidth > maxWidth) {
                if (imgRatio < maxRatio) {
                    imgRatio = maxHeight / actualHeight;
                    actualWidth = (int) (imgRatio * actualWidth);
                    actualHeight = (int) maxHeight;
                } else if (imgRatio > maxRatio) {
                    imgRatio = maxWidth / actualWidth;
                    actualHeight = (int) (imgRatio * actualHeight);
                    actualWidth = (int) maxWidth;
                } else {
                    actualHeight = (int) maxHeight;
                    actualWidth = (int) maxWidth;
                }
            }

            //setting inSampleSize value allows to load a scaled down version of the original image
            options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);

            //inJustDecodeBounds set to false to load the actual bitmap
            options.inJustDecodeBounds = false;

            //this options allow android to claim the bitmap memory if it runs low on memory
            options.inPurgeable = true;
            options.inInputShareable = true;
            options.inTempStorage = new byte[16 * 1024];

            try {
                // load the bitmap getTempFile its path
                bmp = BitmapFactory.decodeFile(filePath, options);
                if (bmp == null) {
                    InputStream inputStream = null;
                    try {
                        inputStream = new FileInputStream(filePath);
                        BitmapFactory.decodeStream(inputStream, null, options);
                        inputStream.close();
                    } catch (IOException exception) {
                        exception.printStackTrace();
                    }
                }
            } catch (OutOfMemoryError exception) {
                exception.printStackTrace();
            }
            if (actualHeight <= 0 || actualWidth <= 0){
                return null;
            }

            try {
                scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, bitmapConfig);
            } catch (OutOfMemoryError exception) {
                exception.printStackTrace();
            }

            float ratioX = actualWidth / (float) options.outWidth;
            float ratioY = actualHeight / (float) options.outHeight;

            Matrix scaleMatrix = new Matrix();
            scaleMatrix.setScale(ratioX, ratioY, 0, 0);

            Canvas canvas = new Canvas(scaledBitmap);
            canvas.setMatrix(scaleMatrix);
            canvas.drawBitmap(bmp, 0, 0, new Paint(Paint.FILTER_BITMAP_FLAG));

            // 采用 ExitInterface 设置图片旋转方向
            ExifInterface exif;
            try {
                exif = new ExifInterface(filePath);
                int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0);
                Matrix matrix = new Matrix();
                if (orientation == 6) {
                    matrix.postRotate(90);
                } else if (orientation == 3) {
                    matrix.postRotate(180);
                } else if (orientation == 8) {
                    matrix.postRotate(270);
                }
                scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
                        scaledBitmap.getWidth(), scaledBitmap.getHeight(),
                        matrix, true);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return scaledBitmap;
        }

        static File compressImage(Context context, Uri imageUri, float maxWidth, float maxHeight,
                                  Bitmap.CompressFormat compressFormat, Bitmap.Config bitmapConfig,
                                  int quality, String parentPath, String prefix, String fileName) {
            FileOutputStream out = null;
            String filename = generateFilePath(context, parentPath, imageUri, compressFormat.name().toLowerCase(), prefix, fileName);
            try {
                out = new FileOutputStream(filename);
                // 通过文件名写入
                Bitmap newBmp = BitmapUtil.getScaledBitmap(context, imageUri, maxWidth, maxHeight, bitmapConfig);
                if (newBmp != null){
                    newBmp.compress(compressFormat, quality, out);
                }

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (out != null) {
                        out.close();
                    }
                } catch (IOException ignored) {
                }
            }

            return new File(filename);
        }

        private static String generateFilePath(Context context, String parentPath, Uri uri,
                                               String extension, String prefix, String fileName) {
            File file = new File(parentPath);
            if (!file.exists()) {
                file.mkdirs();
            }
            /** if prefix is null, set prefix "" */
            prefix = TextUtils.isEmpty(prefix) ? "" : prefix;
            /** reset fileName by prefix and custom file name */
            fileName = TextUtils.isEmpty(fileName) ? prefix + FileUtils.splitFileName(FileUtils.getFileName(context, uri))[0] : fileName;
            return file.getAbsolutePath() + File.separator + fileName + "." + extension;
        }


        /**
         * 计算inSampleSize
         */
        private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
            final int height = options.outHeight;
            final int width = options.outWidth;
            int inSampleSize = 1;

            if (height > reqHeight || width > reqWidth) {
                final int heightRatio = Math.round((float) height / (float) reqHeight);
                final int widthRatio = Math.round((float) width / (float) reqWidth);
                inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
            }

            final float totalPixels = width * height;
            final float totalReqPixelsCap = reqWidth * reqHeight * 2;

            while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
                inSampleSize++;
            }

            return inSampleSize;
        }
    }
    public static class CompressHelper {
        private static volatile CompressHelper INSTANCE;

        private Context context;
        /**
         * 最大宽度，默认为720
         */
        private float maxWidth = 720.0f;
        /**
         * 最大高度,默认为960
         */
        private float maxHeight = 960.0f;
        /**
         * 默认压缩后的方式为JPEG
         */
        private Bitmap.CompressFormat compressFormat = Bitmap.CompressFormat.JPEG;

        /**
         * 默认的图片处理方式是ARGB_8888
         */
        private Bitmap.Config bitmapConfig = Bitmap.Config.ARGB_8888;
        /**
         * 默认压缩质量为80
         */
        private int quality = 80;
        /**
         * 存储路径
         */
        private String destinationDirectoryPath;
        /**
         * 文件名前缀
         */
        private String fileNamePrefix;
        /**
         * 文件名
         */
        private String fileName;

        public static CompressHelper getDefault(Context context) {
            if (INSTANCE == null) {
                synchronized (CompressHelper.class) {
                    if (INSTANCE == null) {
                        INSTANCE = new CompressHelper(context);
                    }
                }
            }
            return INSTANCE;
        }


        private CompressHelper(Context context) {
            this.context = context;
            destinationDirectoryPath = context.getCacheDir().getPath() + File.pathSeparator + FileUtils.FILES_PATH;
        }

        /**
         * 压缩成文件
         * @param file  原始文件
         * @return      压缩后的文件
         */
        public File compressToFile(File file) {
            return BitmapUtil.compressImage(context, Uri.fromFile(file), maxWidth, maxHeight,
                    compressFormat, bitmapConfig, quality, destinationDirectoryPath,
                    fileNamePrefix, fileName);
        }

        /**
         * 压缩为Bitmap
         * @param file  原始文件
         * @return      压缩后的Bitmap
         */
        public Bitmap compressToBitmap(File file) {
            return BitmapUtil.getScaledBitmap(context, Uri.fromFile(file), maxWidth, maxHeight, bitmapConfig);
        }


        /**
         * 采用建造者模式，设置Builder
         */
        public static class Builder {
            private CompressHelper mCompressHelper;

            public Builder(Context context) {
                mCompressHelper = new CompressHelper(context);
            }

            /**
             * 设置图片最大宽度
             * @param maxWidth  最大宽度
             */
            public Builder setMaxWidth(float maxWidth) {
                mCompressHelper.maxWidth = maxWidth;
                return this;
            }

            /**
             * 设置图片最大高度
             * @param maxHeight 最大高度
             */
            public Builder setMaxHeight(float maxHeight) {
                mCompressHelper.maxHeight = maxHeight;
                return this;
            }

            /**
             * 设置压缩的后缀格式
             */
            public Builder setCompressFormat(Bitmap.CompressFormat compressFormat) {
                mCompressHelper.compressFormat = compressFormat;
                return this;
            }

            /**
             * 设置Bitmap的参数
             */
            public Builder setBitmapConfig(Bitmap.Config bitmapConfig) {
                mCompressHelper.bitmapConfig = bitmapConfig;
                return this;
            }

            /**
             * 设置压缩质量，建议80
             * @param quality   压缩质量，[0,100]
             */
            public Builder setQuality(int quality) {
                mCompressHelper.quality = quality;
                return this;
            }

            /**
             * 设置目的存储路径
             * @param destinationDirectoryPath  目的路径
             */
            public Builder setDestinationDirectoryPath(String destinationDirectoryPath) {
                mCompressHelper.destinationDirectoryPath = destinationDirectoryPath;
                return this;
            }

            /**
             * 设置文件前缀
             * @param prefix    前缀
             */
            public Builder setFileNamePrefix(String prefix) {
                mCompressHelper.fileNamePrefix = prefix;
                return this;
            }

            /**
             * 设置文件名称
             * @param fileName  文件名
             */
            public Builder setFileName(String fileName) {
                mCompressHelper.fileName = fileName;
                return this;
            }

            public CompressHelper build() {
                return mCompressHelper;
            }
        }
    }
}