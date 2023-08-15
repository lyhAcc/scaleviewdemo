package com.example.viewtest1.customviewtest_gestureprocess.example2_scaleview.twofingerscale.way4;

import android.graphics.Matrix;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.example.viewtest1.R;
import com.example.viewtest1.customview_util.Constant;

import androidx.appcompat.app.AppCompatActivity;

//https://blog.csdn.net/qq_41677326/article/details/130293906(自己将作者的kt代码转为java，仅实现了缩放逻辑)
//在onTouch中根据action类型实现缩放
//自己将作者的kt代码转为java，仅实现了缩放逻辑
public class MainActivityJava extends AppCompatActivity {

    ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.customview_twofingerscaleview4);

        mImageView = findViewById(R.id.iv_example);
        Matrix startMatrix = new Matrix();
        mImageView.setOnTouchListener(new View.OnTouchListener() {

            MODE mode = MODE.NONE;
            float currentDistance, lastDistance;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mImageView.setScaleType(ImageView.ScaleType.MATRIX);  //需要这句，否则无法缩放
                //获取action类型
//                int actionMasked = event.getAction() & MotionEvent.ACTION_MASK; //写法1
                int actionMasked = event.getActionMasked(); //写法2
                //进行action处理
                switch (actionMasked) {
                    case MotionEvent.ACTION_POINTER_DOWN:
                        Log.d(Constant.TAG, "ACTION_POINTER_DOWN：");
                        //记录双指按下的位置和距离
                        lastDistance = getDistance(event);
                        startMatrix.set(mImageView.getImageMatrix());  //获取ImageView中当前的变换矩阵，并保存为startMatrix
                        mode = MODE.ZOOM;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        Log.d(Constant.TAG, "ACTION_MOVE：");
                        if (mode == MODE.ZOOM) {  //需要这个if，否则会出现{单指可缩放}和{两指中的一根手指松开后图像自动放大}问题，因event.getX/Y(1)在单指时返回0.0
                            //双指缩放
                            currentDistance = getDistance(event);
                            float scale = currentDistance / lastDistance;

                            Log.d(Constant.TAG, "startMatrix=" + startMatrix);
                            startMatrix.postScale(scale, scale, getMidX(event), getMidY(event));  //x和y方向的缩放比例均是scale，缩放枢轴点均是两指的中点
                            mImageView.setImageMatrix(startMatrix);  //内部会调invalidate()

                            lastDistance = currentDistance;
                        }
                        break;
                    case MotionEvent.ACTION_POINTER_UP:
                        Log.d(Constant.TAG, "ACTION_POINTER_UP：");
                        mode = MODE.NONE;
                        break;
                }
                //返回true
                return true;
            }
        });
    }

    /**
     * 根据勾股定理计算两手指间的距离
     *
     * @param event
     * @return
     */
    private float getDistance(MotionEvent event) {
        Log.d(Constant.TAG, "event.getX(0)=" + event.getX(0) + ",event.getX(1)=" + event.getX(1)
                + ",event.getY(0)=" + event.getY(0) + ",event.getY(1)=" + event.getY(1));
        float dx = event.getX(0) - event.getX(1);
        float dy = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    //x轴上两指的中间值
    private float getMidX(MotionEvent event) {
        return (event.getX(0) + event.getX(1)) / 2;
    }

    //y轴上两指的中间值
    private float getMidY(MotionEvent event) {
        return (event.getY(0) + event.getY(1)) / 2;
    }
}