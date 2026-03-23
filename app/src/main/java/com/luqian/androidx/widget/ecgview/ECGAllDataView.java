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

    private int width, height;
    private ArrayList<Integer> dataSource;
    private int dataNum;
    private float rectGapX;
    private float rectYCenter;

    private Paint paint;
    private Path path;

    public ECGAllDataView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ECGAllDataView(Context context) {
        super(context);
        init();
    }

    private void init() {
        setBackgroundColor(getResources().getColor(R.color.transparent));

        paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(getResources().getColor(R.color.red1));
        paint.setStrokeWidth(1.0f);

        path = new Path();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (changed) {
            width = getWidth();
            height = getHeight();
            dataNum = dataSource != null ? dataSource.size() : 0;
            rectGapX = (float) width / dataNum;
            rectYCenter = (float) height / 2;
        }
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawAllData(canvas);
    }

    private void drawAllData(Canvas canvas) {
        if (dataSource == null || dataSource.isEmpty()) {
            return;
        }

        path.reset();
        path.moveTo(0, getRectYCoordinate(dataSource.get(0)));

        for (int i = 1; i < dataSource.size(); i++) {
            path.lineTo(rectGapX * i, getRectYCoordinate(dataSource.get(i)));
        }
        canvas.drawPath(path, paint);
    }

    private float getRectYCoordinate(int data) {
        int yInt = (data - 2048) * (-1);
        return yInt / 8 + rectYCenter;
    }

    public void setData(ArrayList<String> data) {
        if (data == null) {
            this.dataSource = null;
        } else {
            this.dataSource = new ArrayList<>(data.size());
            for (String s : data) {
                try {
                    this.dataSource.add(Integer.parseInt(s));
                } catch (NumberFormatException e) {
                    this.dataSource.add(2048);
                }
            }
        }
        invalidate();
    }

    public void setIntegerData(ArrayList<Integer> data) {
        this.dataSource = data != null ? new ArrayList<>(data) : null;
        invalidate();
    }

}
