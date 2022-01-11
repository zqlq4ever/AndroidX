package com.luqian.androidx.widget.ecgview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import com.luqian.androidx.R;

import java.util.ArrayList;


public class ECGAllDataView extends View {

    private int width, height;//  本页面宽，高
    private ArrayList<String> data_source;
    private int data_num;//  总的数据个数
    private float rect_gap_x;//  下方矩形区域心电图数据间的横坐标间隙
    private float rectY_center;//  下方矩形区域心电图的中心Y值

    public ECGAllDataView(Context context, AttributeSet attrs) {
        super(context, attrs);
        //  背景色 透明
        this.setBackgroundColor(getResources().getColor(R.color.transparent));
    }

    public ECGAllDataView(Context context) {
        super(context);
        //  背景色 透明
        this.setBackgroundColor(getResources().getColor(R.color.transparent));
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (changed) {
            width = getWidth();
            height = getHeight();
            data_num = data_source.size();
            rect_gap_x = (float) width / data_num;
            rectY_center = (float) height / 2;
//            Log.v("json","两点间横坐标间距:" + gap_x + "矩形区域两点间横坐标间距：" + rect_gap_x);
        }
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        DrawAllData(canvas);
    }

    /**
     * 画下方矩形区域的心电图
     */
    private void DrawAllData(Canvas canvas) {
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(getResources().getColor(R.color.red1));
        paint.setStrokeWidth(1.0f);
        Path path = new Path();

        path.moveTo(0, getRectY_coordinate(data_source.get(0)));

        for (int i = 1; i < this.data_source.size(); i++) {
            path.lineTo(rect_gap_x * i, getRectY_coordinate(data_source.get(i)));
        }
        canvas.drawPath(path, paint);
    }

    /**
     * 将数值转换为y坐标，下方矩形 显示心电图的区域
     */
    private float getRectY_coordinate(String data) {
        int y_int = Integer.parseInt(data);
        y_int = (y_int - 2048) * (-1);
        float y_coor = 0.0f;

        y_coor = y_int / 8 + rectY_center;
//        Log.v("json","<rectY_center> " + rectY_center + " < y_coor >" + y_coor +"  height:" + height +" rect_hight " + rect_high);
        return y_coor;
    }

    /**
     * 暴露接口，设置数据源
     */
    public void setData(ArrayList<String> data) {
        this.data_source = data;
    }
}
