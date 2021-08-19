package com.luqian.androidx.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.blankj.utilcode.util.ConvertUtils;
import com.luqian.androidx.R;

import java.util.ArrayList;
import java.util.List;


public class WaveView2 extends View {

    /**
     * 颜色
     */
    private int color = Color.BLUE;
    /**
     * 速度
     */
    private int speed = 20;
    /**
     * 圆圈之间最大间距
     */
    private float mGap = 10;
    /**
     * 是否填充
     */
    private boolean isFill = true;
    //  View 的宽
    private float mWidth;
    //  View 的高
    private float mHeight;
    //  声波的圆圈集合
    private List<Circle> mCirles;
    //  画笔
    private Paint mPaint;
    private Context mContext;

    public WaveView2(Context context) {
        this(context, null);
    }

    public WaveView2(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WaveView2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.mSpreadView);
        color = typedArray.getColor(R.styleable.mSpreadView_color, color);
        speed = typedArray.getInt(R.styleable.mSpreadView_speed, speed);
        mGap = typedArray.getFloat(R.styleable.mSpreadView_gap, mGap);
        isFill = typedArray.getBoolean(R.styleable.mSpreadView_isFill, isFill);
        typedArray.recycle();

        mContext = context;
        init();
    }

    private void init() {
        //  创建画笔
        mPaint = new Paint();
        //  设置画笔颜色
        mPaint.setColor(color);
        //  设置空心/实心
        if (isFill) {
            mPaint.setStyle(Paint.Style.FILL);
        } else {
            mPaint.setStrokeWidth(ConvertUtils.dp2px(1));
            mPaint.setStyle(Paint.Style.STROKE);
        }
        //  设置形状为圆形
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        //  抗锯齿
        mPaint.setAntiAlias(true);

        //  添加第一个圆圈
        mCirles = new ArrayList<>();
        //Circle circle = new Circle(0, 255);
        //mCirles.add(circle);

        mGap = ConvertUtils.dp2px(mGap);

        //  设置View的圆为半透明
        setBackgroundColor(Color.TRANSPARENT);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);//    宽的测量大小，模式
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);

        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);//  高的测量大小，模式
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);

        mWidth = widthSpecSize;   //    定义测量宽，高(不包含测量模式),并设置默认值，查看 View#getDefaultSize 可知
        mHeight = heightSpecSize;

        //  处理 wrap_content 的几种特殊情况
        if (widthSpecMode == MeasureSpec.AT_MOST && heightSpecMode == MeasureSpec.AT_MOST) {
            mWidth = 200;  //   单位是 px
            mHeight = 200;
        } else if (widthSpecMode == MeasureSpec.AT_MOST) {
            //  只要宽度布局参数为 wrap_content， 宽度给固定值 200dp
            mWidth = 200;
            mHeight = heightSpecSize;
        } else if (heightSpecMode == MeasureSpec.AT_MOST) {
            mWidth = widthSpecSize;
            mHeight = 200;
        }

        //  给两个字段设置值，完成最终测量
        setMeasuredDimension((int) mWidth, (int) mHeight);
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        //  初始化圆的个数
        int number = (int) mWidth / 2 / ConvertUtils.dp2px(mGap);
        mCirles.add(new Circle(0, 255));
        for (int i = 1; i <= number; i++) {
            Circle circle = new Circle(ConvertUtils.dp2px(mGap) * i,
                    255 - ConvertUtils.dp2px(mGap) * i * (255 / ((int) mWidth / 2)));
            mCirles.add(circle);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawSpecialCircles(canvas);
    }

    private void drawSpecialCircles(Canvas canvas) {
        canvas.save();

        // 处理每个圆的宽度和透明度
        for (int i = 0; i < mCirles.size(); i++) {
            Circle c = mCirles.get(i);
            mPaint.setAlpha(c.alpha);// （透明）0-255（不透明）
            canvas.drawCircle(mWidth / 2, mHeight / 2, c.width - mPaint.getStrokeWidth(), mPaint);

            if (c.alpha == 0) {
                //到最后一圈后,降透明度降为0，并逐渐缩小至最小的圆
                if (c.width > mGap) {
                    c.width--;
                    c.alpha = 0;
                } else {
                    // 为了防止抖动，缩小至最小的圆宽度并且为透明的就删除
                    mCirles.remove(i);
                }
            } else {
                // 改变透明度
                double alpha = 255 - c.width * (255 / ((double) mWidth / 2));
                c.alpha = (int) alpha;
                // 宽度逐渐增加
                c.width++;
            }
        }

        // 添加圆圈
        if (mCirles.size() > 0) {
            // 控制接下来出来的圆的间距
            if (mCirles.get(mCirles.size() - 1).alpha != 0 &&
                    mCirles.get(mCirles.size() - 1).width > ConvertUtils.dp2px(mGap)) {
                mCirles.add(new Circle(0, 255));
            }
        }

        postInvalidateDelayed(speed);

        canvas.restore();
    }

    private void drawCircles(Canvas canvas) {
        canvas.save();

        // 处理每个圆的宽度和透明度
        for (int i = 0; i < mCirles.size(); i++) {
            Circle c = mCirles.get(i);
            mPaint.setAlpha(c.alpha);// （透明）0-255（不透明）
            canvas.drawCircle(mWidth / 2, mHeight / 2, c.width - mPaint.getStrokeWidth(), mPaint);

            if (c.alpha == 0) {
                //到最后一圈后,降透明度降为0，并逐渐缩小至最小的圆
                if (c.width > mGap) {
                    c.width--;
                    c.alpha = 0;
                } else {
                    // 为了防止抖动，缩小至最小的圆宽度并且为透明的就删除
                    mCirles.remove(i);
                }
            } else {
                // 改变透明度
                double alpha = 255 - c.width * (255 / ((double) mWidth / 2));
                c.alpha = (int) alpha;
                // 宽度逐渐增加
                c.width++;
            }
        }

        // 添加圆圈
        if (mCirles.size() > 0) {
            // 控制第二个圆出来的间距
            if (mCirles.get(mCirles.size() - 1).width > ConvertUtils.dp2px(mGap)) {
                mCirles.add(new Circle(0, 255));
            }
        }

        postInvalidateDelayed(speed);

        canvas.restore();
    }

    class Circle {
        private int width;
        private int alpha;

        Circle(int width, int alpha) {
            this.width = width;
            this.alpha = alpha;
        }
    }
}
