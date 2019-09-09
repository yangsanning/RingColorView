package ysn.com.view.ringcolorview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

/**
 * @Author yangsanning
 * @ClassName ColorPickerView
 * @Description 一句话概括作用
 * @Date 2019/9/9
 * @History 2019/9/9 author: description:
 */
public class RingColorView extends View implements IPaintView {

    /**
     * 渐变色环颜色
     */
    private int[] ringColors = new int[]{
            0xFFff0000,
            0xFFff7f00,
            0xFFffff00,
            0xFF7fff00,
            0xFF00ff00,
            0xFF00ff7f,
            0xFF007dff,
            0xFF0000ff,
            0xFF7f00ff,
            0xFFff00ff,
            0xFFff007f,
            0xFFff0000};

    private int ringWidth;
    private float ringRatio;
    private Paint ringPaint;
    private RectF ringRectF;
    private float ringRadius;
    private int ringColor = 0xFFff0000;

    private Paint circlePaint;

    private int viewHeight;
    private int viewWidth;

    private float circleRatio;
    private float circleRadius;

    /**
     * 渐变数组
     */
    private String[] alphas;
    private OnColorChangedListener onColorChangedListener;
    private OnAngleChangeListener onAngleChangeListener;
    private OnMultiChangeListener onMultiChangeListener;

    public RingColorView(Context context) {
        this(context, null);
    }

    public RingColorView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RingColorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(attrs);
        initPaint();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public RingColorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initAttrs(attrs);
        initPaint();
    }

    @Override
    public void initAttrs(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.RingColorView);

        ringWidth = typedArray.getDimensionPixelSize(R.styleable.RingColorView_rcv_ring_width, 50);
        ringRatio = typedArray.getFloat(R.styleable.RingColorView_rcv_ring_ratio, 0.8f);
        circleRatio = typedArray.getFloat(R.styleable.RingColorView_rcv_circle_ratio, 0.7f);

        typedArray.recycle();
    }

    @Override
    public void initPaint() {
        Shader shader = new SweepGradient(0, 0, ringColors, null);
        ringPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        ringPaint.setShader(shader);
        ringPaint.setStyle(Paint.Style.STROKE);
        ringPaint.setStrokeWidth(ringWidth);

        circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        alphas = new String[]{"F2", "F2", "E6", "E6", "CC", "B3", "99", "80", "73", "66", "59", "4D", "05", "03"};
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();

        canvas.translate(viewWidth / 2, viewHeight / 2);

        //画中心圆
        int[] colors = new int[alphas.length];
        for (int i = 0; i < alphas.length; i++) {
            colors[i] = ColorUtils.convert(ringColor, alphas[i]);
        }
        RadialGradient gradient = new RadialGradient(0, 0, circleRadius,colors, (null), Shader.TileMode.CLAMP);
        circlePaint.setShader(gradient);
        canvas.drawCircle(0, 0, circleRadius, circlePaint);

        //画圆环
        canvas.drawOval(ringRectF, ringPaint);

        canvas.restore();
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldw, int oldh) {
        super.onSizeChanged(width, height, oldw, oldh);
        viewWidth = width;
        viewHeight = height;
        ringRadius = width / 2f * ringRatio - ringPaint.getStrokeWidth() * 0.5f;
        ringRectF = new RectF(-ringRadius, -ringRadius, ringRadius, ringRadius);
        circleRadius = (ringRadius - ringPaint.getStrokeWidth() / 2) * circleRatio;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX() - viewWidth / 2f;
        float y = event.getY() - viewHeight / 2f;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                float angle = (float) Math.atan2(y, x);
                float unit = (float) (angle / (2 * Math.PI));
                if (unit < 0) {
                    unit += 1;
                }
                ringColor = getRingColor(ringColors, unit);
                invalidate();

                if (onColorChangedListener != null) {
                    onColorChangedListener.colorChanged(ringColor);
                }

                if (onAngleChangeListener != null) {
                    onAngleChangeListener.onAngle(angle);
                }

                if (onMultiChangeListener != null) {
                    onMultiChangeListener.colorChanged(ringColor);
                    onMultiChangeListener.onAngle(angle);
                }
                break;
            default:
                break;
        }
        return true;
    }

    /**
     * 获取圆环上颜色
     */
    private int getRingColor(int[] colors, float unit) {
        if (unit <= 0) {
            return colors[0];
        }
        if (unit >= 1) {
            return colors[colors.length - 1];
        }

        float p = unit * (colors.length - 1);
        int i = (int) p;
        p -= i;

        int c0 = colors[i];
        int c1 = colors[i + 1];
        int a = ave(Color.alpha(c0), Color.alpha(c1), p);
        int r = ave(Color.red(c0), Color.red(c1), p);
        int g = ave(Color.green(c0), Color.green(c1), p);
        int b = ave(Color.blue(c0), Color.blue(c1), p);
        return Color.argb(a, r, g, b);
    }

    private int ave(int s, int d, float p) {
        return s + Math.round(p * (d - s));
    }

    /**
     * 颜色数值, 用于渐变
     */
    public void setAlphas(String[] alphas) {
        this.alphas = alphas;
    }

    public void setOnColorChangedListener(OnColorChangedListener onColorChangedListener) {
        this.onColorChangedListener = onColorChangedListener;
    }

    public void setOnAngleChangeListener(OnAngleChangeListener onAngleChangeListener) {
        this.onAngleChangeListener = onAngleChangeListener;
    }

    public void setOnMultiChangeListener(OnMultiChangeListener onMultiChangeListener) {
        this.onMultiChangeListener = onMultiChangeListener;
    }

    public interface OnColorChangedListener {

        /**
         * @param color 颜色
         */
        void colorChanged(int color);
    }

    public interface OnAngleChangeListener {

        /**
         * @param angle 角度 (若要角度自行计算 angle*(180/Math.PI))
         */
        void onAngle(float angle);
    }

    public interface OnMultiChangeListener {

        /**
         * @param color 颜色
         */
        void colorChanged(int color);

        /**
         * @param angle 角度 (若要角度自行计算 angle*(180/Math.PI))
         */
        void onAngle(float angle);
    }
}
