package com.luqian.androidx.widget.ecgview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.luqian.androidx.R;

import java.util.ArrayList;


public class ScrollECGView extends View {

    private static final int RECT_HEIGHT = 80;
    private static final int DATA_NUM_PER_GRID = 18;
    private static final float GAP_GRID = 30.0f;
    private static final float[] DASH_PATTERN = {1, 5};

    private float gapGrid;
    private int width, height;
    private int xori;
    private int gridHori, gridVer;
    private float gapX;
    private float yCenter;
    private ArrayList<Integer> dataSource;

    private float xChanged;
    private float startX;
    private int dataNum;
    private float offsetXMax;

    private float rectWidth;
    private float rectGapX;
    private float multipleForRectWidth;

    private Paint gridPaint;
    private Paint ecgPaint;
    private Paint rectPaint;
    private Path gridPath;
    private Path ecgPath;
    private Path rectPath;
    private PathEffect dashPathEffect;

    public ScrollECGView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ScrollECGView(Context context) {
        super(context);
        init();
    }

    private void init() {
        setBackgroundColor(getResources().getColor(R.color.black));

        gridPaint = new Paint();
        gridPaint.setStyle(Paint.Style.STROKE);
        gridPaint.setColor(getResources().getColor(R.color.teal_200));
        gridPaint.setStrokeWidth(1.0f);

        ecgPaint = new Paint();
        ecgPaint.setStyle(Paint.Style.STROKE);
        ecgPaint.setColor(getResources().getColor(R.color.red1));
        ecgPaint.setStrokeWidth(2.0f);

        rectPaint = new Paint();
        rectPaint.setStyle(Paint.Style.FILL);
        rectPaint.setColor(getResources().getColor(R.color.teal_700));
        rectPaint.setStrokeWidth(1.0f);

        gridPath = new Path();
        ecgPath = new Path();
        rectPath = new Path();

        dashPathEffect = new DashPathEffect(DASH_PATTERN, 1);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (changed) {
            xori = 0;
            gapGrid = GAP_GRID;
            width = getWidth();
            height = getHeight();
            gridHori = height / (int) gapGrid;
            gridVer = width / (int) gapGrid;
            yCenter = (height - RECT_HEIGHT) / 2;
            gapX = gapGrid / DATA_NUM_PER_GRID;
            dataNum = dataSource != null ? dataSource.size() : 0;
            xChanged = 0.0f;
            offsetXMax = width - gapX * dataNum;
            rectGapX = (float) width / dataNum;
            rectWidth = (float) width * width / (gapX * dataNum);
            multipleForRectWidth = (float) width / rectWidth;
        }
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawGrid(canvas);
        drawECGWave(canvas);
    }

    private void drawGrid(Canvas canvas) {
        float offsetY = (height - gridHori * gapGrid) / 2;
        for (int i = 1; i < gridHori + 2; i++) {
            gridPath.reset();
            float y = gapGrid * (i - 1) + offsetY;
            gridPath.moveTo(xori, y);
            gridPath.lineTo(width, y);
            gridPaint.setPathEffect(i % 5 != 0 ? dashPathEffect : null);
            canvas.drawPath(gridPath, gridPaint);
        }

        float offsetX = (width - gridVer * gapGrid) / 2;
        for (int i = 1; i < gridVer + 2; i++) {
            gridPath.reset();
            float x = gapGrid * (i - 1) + offsetX;
            gridPath.moveTo(x, 0);
            gridPath.lineTo(x, height);
            gridPaint.setPathEffect(i % 5 != 0 ? dashPathEffect : null);
            canvas.drawPath(gridPath, gridPaint);
        }
    }

    private void drawECGWave(Canvas canvas) {
        if (dataSource == null || dataSource.isEmpty()) {
            return;
        }

        ecgPath.reset();

        if (xChanged > xori) {
            xChanged = xori;
        } else if (xChanged < offsetXMax) {
            xChanged = offsetXMax;
        }

        int iXor = 1;
        for (int i = 1; i < dataSource.size(); i++) {
            float nnn = xori + gapX * i + xChanged;
            if (nnn >= 0) {
                iXor = i;
                ecgPath.moveTo(nnn, getYCoordinate(dataSource.get(i)));
                break;
            }
        }

        for (int i = iXor; i < dataSource.size(); i++) {
            float nnn = xori + gapX * i + xChanged;
            if (nnn < width + gapX) {
                ecgPath.lineTo(xori + gapX * i + xChanged, getYCoordinate(dataSource.get(i)));
            }
        }

        canvas.drawPath(ecgPath, ecgPaint);

        rectPath.reset();
        float rectXori = (float) (0 - xChanged) / multipleForRectWidth;
        rectPath.moveTo(rectXori, height - RECT_HEIGHT - 20);
        rectPath.lineTo(rectXori + rectWidth, height - RECT_HEIGHT - 20);
        rectPath.lineTo(rectXori + rectWidth, height);
        rectPath.lineTo(rectXori, height);
        canvas.drawPath(rectPath, rectPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX = event.getX();
                break;

            case MotionEvent.ACTION_MOVE:
                float currentX = event.getX();
                xChanged += currentX - startX;
                startX = currentX;
                invalidate();
                break;
        }
        return true;
    }

    private float getYCoordinate(int data) {
        int yInt = (data - 2048) * (-1);
        return yInt * 3 / 4 + yCenter;
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
