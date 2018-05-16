package com.airtf.geslib.inner;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.airtf.geslib.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 手势滑动组件，主要作用是滑动手势，并返回回调结果
 * 使用方法
 * 布局文件：
 * 1.添加属性：xmlns:gesture="http://schemas.android.com/apk/res-auto
 * 2.定义组件：
 * <com.airtf.baselib.widget.GestureLockView
 *      android:id="@+id/glv"
 *      android:layout_width="match_parent"
 *      android:layout_height="match_parent"
 *      android:background="#ffffff"
 *      gesture:line_color="@color/sandybrown"
 *      gesture:line_width="@dimen/w_12px"
 *      gesture:point_normal_color="@color/silver"
 *      gesture:point_selected_color="@color/cornflowerblue"
 *      gesture:point_normal_drawable="@drawable/ges_point_normal"
 *      gesture:point_selected_drawable="@drawable/ges_point_selected"
 *      />
 * 3.注册事件：
 * glv.setOnGestureLockListener(new GestureLockView.OnGestureLockListener() {
 *        public void onGesture(int status, int resultCode, String result) {
 *        }
 * });
 *
 * Author: freyrao
 * Time: 18/5/8 15:15
 * Desc:
 */

public class GestureLockView extends View {

    /**返回结果码：成功**/
    public static final int RESULT_OK = 0;
    /**返回结果码：点数太少，至少4个**/
    public static final int RESULT_ERROR_POINT_SHORT = 1;
    /**其他未知错误**/
    public static final int RESULT_ERROR = 2;

    /**空闲状态**/
    public static final int STATUS_IDLE = 0;
    /**正在绘制**/
    public static final int STATUS_DRAWING = 1;
    /**连接点数达到最大**/
    public static final int STATUS_DRAWING_MAX = 2;
    /**绘制完成**/
    public static final int STATUS_OVER = 3;

    /**密码最小长度**/
    private static final int POINT_MIN_COUNT = 4;

    /**
     * 组件最小宽高
     */
    private static final int MIN_WIDHT = 300;
    private static final int MIN_HEIGHT = 300;
    /**点未选中颜色**/
    private static final String COLOR_POINT_NORMAL = "#969696";
    /**点选中颜色**/
    private static final String COLOR_POINT_SELECTED = "#6495ED";
    /**线条颜色**/
    private static final String COLOR_LINE = "#6495ED";
    /**线条宽度**/
    private static final int LINE_WIDTH = 5;
    private static final String COLOR_ERROR = "#CD5C5C";
    /**绘制图片时，每个点大于区域范围时，图片缩放倍数**/
    private static final float POINT_MAX_SCALE = 0.5f;

    /**
     * 组件宽高
     */
    private int gWidth = MIN_WIDHT;
    private int gHeight = MIN_HEIGHT;

    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    /**手势圆的圆心坐标**/
    private Point[][] points = new Point[3][3];
    /**圆的半径,当图片为空时使用**/
    private float radius = 30;
    /**圆的图片，未选中**/
    private Bitmap gesPointCircleNormal;
    /**圆的图片，选中状态**/
    private Bitmap gesPointCircleSelected;
    /**图片半径**/
    private float gesPointCircleNormalRadius;
    /**图片半径**/
    private float gesPointCircleSelectedRadius;
    /**每个点所在区域的半径**/
    private float rectSideRadius;
    /**线条颜色**/
    private int lineColor = Color.parseColor(COLOR_LINE);
    /**线条宽度**/
    private int lineWidth = LINE_WIDTH;
    /**未选中点颜色**/
    private int pointNormalColor = Color.parseColor(COLOR_POINT_NORMAL);
    /**选中点颜色**/
    private int pointSelectedColor = Color.parseColor(COLOR_POINT_SELECTED);
    /**错误颜色**/
    private int errorColor = Color.parseColor(COLOR_ERROR);

    private int resultCode = RESULT_OK;
    /**选中的点**/
    private List<Point> sPoints = new ArrayList<>();
    /**状态**/
    private int status = STATUS_IDLE;
    /**回调监听**/
    private OnGestureLockListener onGestureLockListener;
    /**手势结果**/
    private String result = "";

    /**绘制时的变量**/
    private Point p;
    private Point lastPoint = null;
    private Point nowPoint = null;
    private float moveX = 0;
    private float moveY = 0;
    /**手势完成，3秒重置**/
    private Timer timer;
    private TimerTask timerTask;
    private int countDownTime = 3;

    private boolean isGesEnable = true;

    public void clearLines() {
        countDownTime = 2;
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                if(countDownTime == 0) {
                    reset();
                    postInvalidate();
                    cancel();
                    timer.cancel();
                } else {
                    countDownTime--;
                }
            }
        };
        timer.schedule(timerTask, 0, 1 * 1000);
    }

    public GestureLockView(Context context) {
        super(context);
        init(context, null);
    }

    public GestureLockView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public GestureLockView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, @Nullable AttributeSet attrs) {
        if(attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.gesture_lock_view);
            BitmapDrawable normalDrawable = (BitmapDrawable) typedArray.getDrawable(R.styleable.gesture_lock_view_point_normal_drawable);
            BitmapDrawable selectedDrawble = (BitmapDrawable) typedArray.getDrawable(R.styleable.gesture_lock_view_point_selected_drawable);
            if(normalDrawable != null) {
                gesPointCircleNormal = normalDrawable.getBitmap();
                int side = Math.min(gesPointCircleNormal.getWidth(), gesPointCircleNormal.getHeight());
                gesPointCircleNormalRadius = side / 2;
            }
            if(selectedDrawble != null) {
                gesPointCircleSelected = selectedDrawble.getBitmap();
                int side = Math.min(gesPointCircleSelected.getWidth(), gesPointCircleSelected.getHeight());
                gesPointCircleSelectedRadius = side / 2;
            }

            lineColor = typedArray.getColor(R.styleable.gesture_lock_view_line_color, Color.parseColor(COLOR_LINE));
            lineWidth = typedArray.getDimensionPixelSize(R.styleable.gesture_lock_view_line_width, LINE_WIDTH);
            pointNormalColor = typedArray.getColor(R.styleable.gesture_lock_view_point_normal_color, Color.parseColor(COLOR_POINT_NORMAL));
            pointSelectedColor = typedArray.getColor(R.styleable.gesture_lock_view_point_selected_color, Color.parseColor(COLOR_POINT_SELECTED));
        }
    }

    /**
     * 设置默认点的图片
     * @param pointNormal
     */
    public void setPointNormalBitmap(Bitmap pointNormal) {
        gesPointCircleNormal = pointNormal;
        int side = Math.min(gesPointCircleNormal.getWidth(), gesPointCircleNormal.getHeight());
        gesPointCircleNormalRadius = side / 2;
    }

    /**
     * 设置选中点的图片
     * @param pointSelected
     */
    public void setPointSelectedBitmap(Bitmap pointSelected) {
        gesPointCircleSelected = pointSelected;
        int side = Math.min(gesPointCircleSelected.getWidth(), gesPointCircleSelected.getHeight());
        gesPointCircleSelectedRadius = side / 2;
    }

    /**
     * 设置线条颜色
     * @param lineColor
     */
    public void setLineColor(int lineColor) {
        this.lineColor = lineColor;
    }

    /**
     * 设置线条宽度
     * @param lineWidth
     */
    public void setLineWidth(int lineWidth) {
        this.lineWidth = lineWidth;
    }

    /**
     * 设置未选中点颜色
     * @param pointNormalColor
     */
    public void setPointNormalColor(int pointNormalColor) {
        this.pointNormalColor = pointNormalColor;
    }

    /**
     * 设置选中点颜色
     * @param pointSelectedColor
     */
    public void setPointSelectedColor(int pointSelectedColor) {
        this.pointSelectedColor = pointSelectedColor;
    }

    /**
     * 设置监听
     * @param onGestureLockListener
     */
    public void setOnGestureLockListener(OnGestureLockListener onGestureLockListener) {
        this.onGestureLockListener = onGestureLockListener;
    }

    public void setGesEnable(boolean gesEnable) {
        this.isGesEnable = gesEnable;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        // measure width
        if(widthMode == MeasureSpec.AT_MOST) {
            gWidth = Math.min(widthSize, MIN_WIDHT);
        } else if(widthMode == MeasureSpec.EXACTLY) {
            gWidth = widthSize;
        } else {
            gWidth = MIN_WIDHT;
        }

        // measure height
        if(heightMode == MeasureSpec.AT_MOST) {
            gHeight = Math.min(heightSize, MIN_HEIGHT);
        } else if(heightMode == MeasureSpec.EXACTLY) {
            gHeight = heightSize;
        } else {
            gHeight = MIN_HEIGHT;
        }

        initData();
        setMeasuredDimension(gWidth, gHeight);
    }

    private void initData() {
        Point center = new Point();
        center.x = gWidth / 2;
        center.y = gHeight / 2;
        points[1][1] = center;

        float side = Math.min(gWidth, gHeight);

        // 九个点，每个点的格子的边长
        float rectSide = side / 3;
        rectSideRadius = rectSide / 2;
        if(gesPointCircleNormalRadius > rectSideRadius * POINT_MAX_SCALE) {
            gesPointCircleNormal = zoomBitmap(gesPointCircleNormal,
                    rectSideRadius * POINT_MAX_SCALE / gesPointCircleNormalRadius);
            gesPointCircleNormalRadius = rectSideRadius * POINT_MAX_SCALE;
        }
        if(gesPointCircleSelectedRadius > rectSideRadius * POINT_MAX_SCALE) {
            gesPointCircleSelected = zoomBitmap(gesPointCircleSelected,
                    rectSideRadius * POINT_MAX_SCALE / gesPointCircleSelectedRadius);
            gesPointCircleSelectedRadius = rectSideRadius * POINT_MAX_SCALE;
        }

        points[0][0] = new Point(center.x - rectSide, center.y - rectSide);
        points[0][1] = new Point(center.x, center.y - rectSide);
        points[0][2] = new Point(center.x + rectSide, center.y - rectSide);
        points[1][0] = new Point(center.x - rectSide, center.y);
        points[1][1] = new Point(center.x, center.y);
        points[1][2] = new Point(center.x + rectSide, center.y);
        points[2][0] = new Point(center.x - rectSide, center.y + rectSide);
        points[2][1] = new Point(center.x, center.y + rectSide);
        points[2][2] = new Point(center.x + rectSide, center.y + rectSide);

        for(int i = 0; i < points.length; i++) {
            for(int j = 0; j < points[i].length; j++) {
                points[i][j].index = i * 3 + j;
                points[i][j].state = Point.STATE_NORMAL;
            }
        }
    }


    @Override
    protected void onDraw(Canvas canvas) {
        // 绘制点
        drawPoints(canvas);
        // 绘制连线
        drawPointLine(canvas);
    }

    /**
     * 绘制点
     */

    private void drawPoints(Canvas canvas) {
        mPaint.reset();
        mPaint.setAntiAlias(true);
        for(int i = 0; i < points.length; i++) {
            for(int j = 0; j < points[i].length; j++) {
                p = points[i][j];
                if(p.state == Point.STATE_SELECTED) {
                    // 选中
                    if(gesPointCircleSelected != null) {
                        // 绘制图片
                        canvas.drawBitmap(gesPointCircleSelected, p.x - gesPointCircleSelectedRadius
                            , p.y - gesPointCircleSelectedRadius, mPaint);
                    } else {
                        // 没有图片，绘制带圈的圆
                        mPaint.setColor(pointSelectedColor);
                        mPaint.setStyle(Paint.Style.STROKE);
                        mPaint.setStrokeWidth(2);
                        canvas.drawCircle(p.x, p.y, radius, mPaint);
                        mPaint.setStyle(Paint.Style.FILL);
                        canvas.drawCircle(p.x, p.y, radius - 10, mPaint);
                    }
                } else {
                    // 未选中
                    if(gesPointCircleNormal != null) {
                        // 绘制普通图片
                        canvas.drawBitmap(gesPointCircleNormal, p.x - gesPointCircleNormalRadius
                                , p.y - gesPointCircleNormalRadius, mPaint);
                    } else {
                        // 没有，绘制实心圆
                        mPaint.setColor(pointNormalColor);
                        mPaint.setStyle(Paint.Style.FILL);
                        canvas.drawCircle(p.x, p.y, radius - 10, mPaint);
                    }
                }
            }
        }
    }

    /**
     * 画线
     * @param canvas
     */
    private void drawPointLine(Canvas canvas) {
        if(sPoints != null && sPoints.size() > 0) {
            Point np = sPoints.get(0);
            for(int i = 1; i < sPoints.size(); i++) {
                Point tp = sPoints.get(i);
                drawLine(canvas, np, tp);
                np = sPoints.get(i);
            }
            if(moveX > 0 && moveY > 0) {
                drawLine(canvas, np.x, np.y, moveX, moveY);
            }
        }
    }

    /**
     * 绘制线
     * @param canvas
     * @param start
     * @param end
     */
    private void drawLine(Canvas canvas, Point start, Point end) {
        drawLine(canvas, start.x, start.y, end.x, end.y);
    }

    /**
     * 绘制线
     * @param canvas
     * @param startX
     * @param startY
     * @param endX
     * @param endY
     */
    private void drawLine(Canvas canvas, float startX, float startY, float endX, float endY) {
        mPaint.reset();
        mPaint.setAntiAlias(true);
        mPaint.setColor(lineColor);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(lineWidth);
        canvas.drawLine(startX, startY, endX, endY, mPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(!isGesEnable) {
            return false;
        }
        float ex = event.getX();
        float ey = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                reset();
                nowPoint = checkSelectPoint(ex, ey);
                lastPoint = nowPoint;
                if(nowPoint != null) {
                    status = STATUS_DRAWING;
                    sPoints.add(nowPoint);
                    nowPoint.state = Point.STATE_SELECTED;
                    moveX = 0;
                    moveY = 0;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if(status == STATUS_DRAWING) {
                    nowPoint = checkSelectPoint(ex, ey);
                    boolean isValidatePoint = isValidatePoint(nowPoint);
                    if(lastPoint != null && nowPoint != null && nowPoint != lastPoint && isValidatePoint) {
                        moveX = nowPoint.x;
                        moveY = nowPoint.y;
                        nowPoint.state = Point.STATE_SELECTED;
                        sPoints.add(nowPoint);
                        lastPoint = nowPoint;
                    } else {
                        moveX = ex;
                        moveY = ey;
                    }
                    if(sPoints.size() >= 9) {
                        status = STATUS_DRAWING_MAX;
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                moveX = 0;
                moveY = 0;
                if(status == STATUS_DRAWING || status == STATUS_DRAWING_MAX) {
                    status = STATUS_OVER;
                    result = generateResult();
                    clearLines();
                }
                break;
        }

        if(onGestureLockListener != null) {
            onGestureLockListener.onGesture(status, resultCode, result);
        }

        postInvalidate();
        return true;
    }

    /**
     * 计算并返回结果
     * @return
     */
    public String generateResult() {
        String ret = "";
        if(sPoints == null) {
            resultCode = RESULT_ERROR;
        } else if(sPoints.size() < POINT_MIN_COUNT) {
            resultCode = RESULT_ERROR_POINT_SHORT;
        } else {
            resultCode = RESULT_OK;
            for(Point p : sPoints) {
                ret += p.index + ",";
            }
            ret = ret.substring(0, ret.length() - 1);
        }

        return ret;
    }

    /**
     * 获取当前按下位置的点，没有返回null
     * @param ex
     * @param ey
     * @return
     */
    private Point checkSelectPoint(float ex, float ey) {
        Point ret = null;
        for(int i = 0; i < points.length; i++) {
            for(int j = 0; j < points[i].length; j++) {
                if(checkInRound(points[i][j].x, points[i][j].y, rectSideRadius, ex, ey)) {
                    ret = points[i][j];
                }
            }
        }
        return ret;
    }

    /**
     * 判断点是否在圆内
     * @param cx 圆心x
     * @param cy 圆心y
     * @param radius 半径
     * @param dx 目标点x
     * @param dy 目标点y
     * @return
     */
    private boolean checkInRound(float cx, float cy, float radius, float dx, float dy) {
        return Math.sqrt((cx - dx) * (cx - dx) + (cy - dy) * (cy - dy)) < radius;
    }

    /**
     * 判断点是否是合法点
     * @param point
     * @return
     */
    private boolean isValidatePoint(Point point) {
        if(sPoints == null) {
            return false;
        }
        if(sPoints.contains(point)) {
            return false;
        }
        return true;
    }

    /**
     * 重置各种状态
     */
    public void reset() {
        if(timerTask != null) {
            timerTask.cancel();
            timerTask = null;
        }
        if(sPoints != null) {
            for(Point p : sPoints) {
                p.state = Point.STATE_NORMAL;
            }
            sPoints.clear();
        }
        result = "";
        resultCode = RESULT_OK;
        status = STATUS_IDLE;
    }

    /**
     * 图片缩放
     * @param bitmap
     * @param zoom
     * @return
     */
    private Bitmap zoomBitmap(Bitmap bitmap, float zoom) {
        Matrix matrix = new Matrix();
        matrix.postScale(zoom, zoom);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    /**绘制的点**/
    public static class Point {
        public static final int STATE_NORMAL = 0;
        public static final int STATE_SELECTED = 1;
        public static final int STATE_SELECTED_ERROR = 2;

        /**坐标**/
        public float x;
        public float y;
        /**状态**/
        public int state = STATE_NORMAL;
        /**索引**/
        public int index;

        public Point() {
            this.x = 0;
            this.y = 0;
        }

        public Point(float x, float y) {
            this.x = x;
            this.y = y;
        }
    }

    public interface OnGestureLockListener {
        public void onGesture(int status, int resultCode, String result);
    }

    int x;
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        getParent().requestDisallowInterceptTouchEvent(true);
        return super.dispatchTouchEvent(ev);
    }

}
