package com.shixin.app;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.card.MaterialCardView;
import com.gyf.immersionbar.ImmersionBar;
import com.shixin.app.utils.FileUtil;
import com.shixin.app.utils.MultiClick;
import com.shixin.app.utils.Utils;
import com.shixin.app.widget.SelectView;
import com.tapadoo.alerter.Alerter;

import java.util.ArrayList;

public class PictureColorActivity extends AppCompatActivity {

    public final int REQ_CD_IMAGE = 101;
    private Intent image = new Intent(Intent.ACTION_GET_CONTENT);
    ImageView mimg;
    SelectView msv;
    MaterialCardView mLayout1;
    LinearLayout mLayout2;
    TextView mtxtx,mtxty,mtxta,mtxtr,mtxtg,mtxtb,mtxtc;
    View mmenu;
    Bitmap mbitmap;
    MultiClick mck;
    Button suo;
    private float screenW,screenH;//屏幕宽高
    private float statusBarH=0f;//状态栏的高度
    private String Pcpath="无";//图片路径
    private String Pcname="无";//图片名称
    private String Pcsize;//图片大小
    private boolean is = true;

    //图片缩放移动变量
    static final int NONE=0;
    static final int DRAG=1;//拖动中
    static final int ZOOM=2;//缩放中
    private int mode=NONE;//当前的事件
    private float scale;//缩放的比例 X Y方向都是这个值 越大缩放的越快
    private float beforeLenght;//两触点距离
    private float afterLenght;//两触点距离
    private Matrix matrix;//用于移动缩放的矩阵
    private Matrix savedMat;//保存现状
    private Matrix originalMat;//记录初始值
    private PointF start_Point;//开始点击的触点
    private PointF mid_Point;//记录两触点的中点

    //SelectView变量
    private PointF down_Point;//开始点击的触点
    private PointF img_Point;//图片上的触点

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_color);

        ImmersionBar.with(this)
                .fitsSystemWindows(true)
                .statusBarColor(R.color.backgroundColor)
                .navigationBarColor(R.color.appbarColor)
                .autoDarkModeEnable(true)
                .navigationBarDarkIcon(true)
                //.keyboardEnable(true)
                //.keyboardMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
                .init();

        image.setType("image/*");
        image.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
        init();

    }

    public void init(){
        screenW=getWindowManager().getDefaultDisplay().getWidth();//屏幕宽（像素，如：480px）
        screenH=getWindowManager().getDefaultDisplay().getHeight();//屏幕高（像素，如：800px）
        statusBarH=getStatusHeight(this);
        mmenu=findViewById(R.id.mMenu1);
        mimg=(ImageView)findViewById(R.id.mImageView1);
        msv=(SelectView)findViewById(R.id.mImageView2);
        mLayout1=(MaterialCardView)findViewById(R.id.mLayout1);
        mLayout2=(LinearLayout)findViewById(R.id.mLayout2);
        mtxtx=(TextView)findViewById(R.id.mTVx);
        mtxty=(TextView)findViewById(R.id.mTVy);
        mtxta=(TextView)findViewById(R.id.mTVa);
        mtxtr=(TextView)findViewById(R.id.mTVr);
        mtxtg=(TextView)findViewById(R.id.mTVg);
        mtxtb=(TextView)findViewById(R.id.mTVb);
        mtxtc=(TextView)findViewById(R.id.mTVc);
        suo = findViewById(R.id.suo);

        start_Point=new PointF();
        mid_Point=new PointF();
        matrix=new Matrix();//注意，这里是3个实例
        savedMat=new Matrix();//不是一个
        originalMat=new Matrix();//否则只是一个实例，而名字不同罢了
        down_Point=new PointF();
        img_Point=new PointF();
        msv.setVisibility(View.GONE);
        mimg.setImageBitmap(null);
        mtxtr.setText("请选择图片");
        mck=new MultiClick(){
            @Override
            protected void onClick()
            {
                setImgFitCenter();
            }
        };
    }

    public void choose(View v){
        startActivityForResult(image, REQ_CD_IMAGE);
    }

    public void suo(View v){
        if (is){
            is = false;
            mLayout1.setVisibility(View.GONE);
            msv.setVisibility(View.GONE);
            suo.setText("取色");
        } else {
            is = true;
            mLayout1.setVisibility(View.VISIBLE);
            msv.setVisibility(View.VISIBLE);
            suo.setText("缩放图片");
        }
    }

    public void copy(View v){
        ((ClipboardManager) this.getSystemService(this.CLIPBOARD_SERVICE)).setPrimaryClip(ClipData.newPlainText("clipboard", mtxtr.getText().toString()));
        Alerter.create((Activity) this)
                .setTitle("复制成功")
                .setText("已将颜色值复制到剪切板")
                .setBackgroundColorInt(0xFF4CAF50)
                .show();
    }

    public void setImgFitCenter() {
        float bitH=mbitmap.getHeight(),
                bitW=mbitmap.getWidth();
        scale=bitW/bitH<screenW/(screenH-statusBarH)?
                (screenH-statusBarH)/bitH:screenW/bitW;
        matrix.set(originalMat);
        matrix.postTranslate((screenW-bitW)/2,(screenH-statusBarH-bitH)/2);
        matrix.postScale(scale,scale,screenW/2,(screenH-statusBarH)/2);
        mimg.setImageMatrix(matrix);
    }

    //获取状态栏高度
    public float getStatusHeight(Activity a1) {
        int i1=0;
        try{
            Class<?>c1=Class.forName("com.android.internal.R$dimen");
            Object o1=c1.newInstance();
            int i2=Integer.parseInt(c1.getField("status_bar_height").get(o1).toString());
            i1=a1.getResources().getDimensionPixelSize(i2);
        }catch(Exception e)
        {
            e.printStackTrace();
        }
        return i1;
    }

    int a,r,g,b,tc;
    public void setLayoutPosition(int zx,int zy,int w,int h) {
        zx+= Utils.dp2px(this,16);zy-= Utils.dp2px(this,16);
        if(zx>screenW/2)zx-=mLayout1.getWidth()+ Utils.dp2px(this,32);
        if(zy<(screenH-statusBarH)/2)zy+=mLayout1.getHeight()+ Utils.dp2px(this,32);
        mLayout1.layout(zx,zy-mLayout1.getHeight(),zx+mLayout1.getWidth(),zy);
        if(w>=0&&w<mbitmap.getWidth()
                &&h>=0&&h<mbitmap.getHeight())
        {
            int pixelColor=mbitmap.getPixel(w,h);
            a=Color.alpha(pixelColor);
            r=Color.red(pixelColor);
            g=Color.green(pixelColor);
            b=Color.blue(pixelColor);
            mtxtx.setText("x="+(w+1));
            mtxty.setText("y="+(h+1));
            mtxta.setText("A="+a);
            mtxtc.setText("R="+r);
            mtxtg.setText("G="+g);
            mtxtb.setText("B="+b);
            mtxtr.setText("#"+getHexC(a,r,g,b).toUpperCase());
            setTextColor(r,g,b);
            //mLayout2.setBackgroundColor(pixelColor);
        }else
        {
            mtxtx.setText("");
            mtxty.setText("");
            mtxta.setText("");
            mtxtr.setText("脱离图片范围");
            mtxtg.setText("");
            mtxtb.setText("");
            mtxtc.setText("");
        }
    }

    private String getHexC(int a,int r,int g,int b) {
        /***
         String s=Integer.toHexString(b);
         if(s.length()==1)
         s="0"+s;
         s=Integer.toHexString(g)+s;
         if(s.length()==3)
         s="0"+s;
         s=Integer.toHexString(r)+s;
         if(s.length()==5)
         s="0"+s;
         s=Integer.toHexString(a)+s;
         if(s.length()==7)
         s="0"+s;
         /***/
        String s="";
        int i1=0;
        for(int i=0;i<4;i++)
        {
            switch(i)
            {
                case 0:i1=b;break;
                case 1:i1=g;break;
                case 2:i1=r;break;
                case 3:i1=a;break;
            }
            s=Integer.toHexString(i1)+s;
            if(s.length()%2!=0)s="0"+s;
        }
        return s;

    }

    private void setTextColor(int r,int g,int b) {
        //if(r>102&&r<153)r=0;
        //if(g>102&&g<153)g=0;
        //if(b>102&&b<153)b=0;
        if(b*0.114+g*0.578+r*0.229>191)
        {
            mtxtc.setTextColor(Color.parseColor("#000000"));
            mtxtx.setTextColor(Color.parseColor("#000000"));
            mtxty.setTextColor(Color.parseColor("#000000"));
            mtxta.setTextColor(Color.parseColor("#000000"));
            mtxtr.setTextColor(Color.parseColor("#000000"));
            mtxtg.setTextColor(Color.parseColor("#000000"));
            mtxtb.setTextColor(Color.parseColor("#000000"));
            mLayout1.setStrokeColor(Color.parseColor("#000000"));
            mLayout1.setCardBackgroundColor(Color.rgb(r,g,b));
            msv.setLineaColor(0,0,0);
        }
        else
        {
            mtxtc.setTextColor(Color.parseColor("#FFFFFF"));
            mtxtx.setTextColor(Color.parseColor("#FFFFFF"));
            mtxty.setTextColor(Color.parseColor("#FFFFFF"));
            mtxta.setTextColor(Color.parseColor("#FFFFFF"));
            mtxtr.setTextColor(Color.parseColor("#FFFFFF"));
            mtxtg.setTextColor(Color.parseColor("#FFFFFF"));
            mtxtb.setTextColor(Color.parseColor("#FFFFFF"));

            mLayout1.setStrokeColor(Color.parseColor("#FFFFFF"));
            mLayout1.setCardBackgroundColor(Color.rgb(r,g,b));
            msv.setLineaColor(255,255,255);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(mbitmap == null){
            mtxtr.setText("请选择图片");
        }else{
            if(is) {
                onSelectMove(event);
            }
            else{
                moveArate(event);
            }
        }

        return true;
    }

    //对SelectView的移动
    public void onSelectMove(MotionEvent me) {
        /*if(is){
            msv.setVisibility(View.VISIBLE);
            msv.setPosition(me.getX(),me.getY()-statusBarH);
        }
        else
        {*/
            msv.setVisibility(View.VISIBLE);
            switch(me.getAction())
            {
                case MotionEvent.ACTION_DOWN:
                    down_Point.set(me.getX(),me.getY());
                    break;
                case MotionEvent.ACTION_UP:
                    break;
                case MotionEvent.ACTION_MOVE:
                    msv.postPosition(me.getX()-down_Point.x,me.getY()-down_Point.y);
                    down_Point.set(me.getX(),me.getY());
                    break;
            }
        //}
        float[]f1=new float[9];
        //这个f1的值是相对初始的bitmap的
        matrix.getValues(f1);
        //当前触点在位图上的坐标=(十字架坐标-图片坐标)/缩放比例
        img_Point.set((msv.get_x()-f1[2])/f1[0],(msv.get_y()-f1[5])/f1[0]);
        setLayoutPosition((int)msv.get_x(),(int)msv.get_y(),(int)img_Point.x,(int)img_Point.y);
    }

    //对图片的移动和缩放处理
    public void moveArate(MotionEvent me) {
        switch(me.getAction()&MotionEvent.ACTION_MASK)
        {
            case MotionEvent.ACTION_DOWN:
                if(mck.onMultiClick())
                    break;
                mode=DRAG;
                savedMat.set(mimg.getImageMatrix());
                start_Point.set(me.getX(),me.getY());
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                if(me.getPointerCount()==2&&spacing(me)>10f)
                {
                    mode=ZOOM;
                    savedMat.set(mimg.getImageMatrix());
                    beforeLenght=spacing(me);
                    mid_Point.set((me.getX(1)+me.getX(0))/2,
                            (me.getY(1)+me.getY(0))/2);
                }
                break;
            case MotionEvent.ACTION_UP:
                mode=NONE;
                break;
            case MotionEvent.ACTION_POINTER_UP:
                mode=NONE;
                break;
            case MotionEvent.ACTION_MOVE:
                //处理拖动
                if(mode==DRAG)
                {
                    //在当前基础上移动
                    matrix.set(savedMat);
                    matrix.postTranslate(
                            me.getX()-start_Point.x,
                            me.getY()-start_Point.y);
                }//处理缩放
                else if(mode==ZOOM&&me.getPointerCount()==2
                        &&(afterLenght=spacing(me))>10f)
                {
                    scale=afterLenght/beforeLenght;
                    matrix.set(savedMat);
                    float[]p=new float[9];
                    matrix.getValues(p);
                    if((p[0]<0.25f&&scale<1f)
                            ||(p[0]>4f&&scale>1f))
                        scale=1f;
                    matrix.postScale(scale,scale,
                            mid_Point.x,mid_Point.y-statusBarH);
                }
                break;
        }
        //设置矩阵
        mimg.setImageMatrix(matrix);
    }

    //算两点间的距离
    public float spacing(MotionEvent me) {
        float x=me.getX(0)-me.getX(1);
        float y=me.getY(0)-me.getY(1);
        return (float)Math.sqrt(x*x+y*y);
    }

    @Override
    protected void onActivityResult(int _requestCode, int _resultCode, Intent _data) {
        super.onActivityResult(_requestCode, _resultCode, _data);

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
                    mbitmap = FileUtil.decodeSampleBitmapFromPath(_filePath.get(0), 1024, 1024);
                    mimg.setImageBitmap(mbitmap);
                    originalMat.set(mimg.getImageMatrix());
                    setImgFitCenter();
                }
                break;
            default:
                break;
        }
    }

}