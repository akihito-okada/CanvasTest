package com.mellowsnow.countdownanimationsample;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by Akihito on 2016/10/30.
 */

public class CountDownView extends View {

    private ValueAnimator mAnimator;
    private final Paint mPaint;
    private RectF mRect;
    private RectF mRect2;


    // Arcの幅
    private final int strokeWidth = 100;
    // Animation 開始地点をセット
    private static final int AngleTarget = 270;
    // 初期 Angle
    private float angle = 10;
    // 半径
    private float radius;
    private float radius2;

    private Canvas mCanvas;

    private boolean mInit;

    public CountDownView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mInit = true;
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(10);
        // Arcの色
        //mPaint.setColor(Color.argb(255, 0, 0, 255));
        // Arcの範囲
        mRect = new RectF();
        mRect2 = new RectF();

        mAnimator = ValueAnimator.ofFloat(0.f, 240.0f);
        // アニメーションの時間(4秒)を設定する
        mAnimator.setDuration(4000);
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                invalidate();
            }
        });
        mAnimator.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // Canvas 中心点
        float x = canvas.getWidth()/2;
        float y = canvas.getHeight()/2;
        radius = canvas.getWidth()/4;
        radius2 = canvas.getWidth()/10;

        mCanvas = canvas;

        mCanvas.drawARGB(255, 0, 0, 0);

        float time = (Float) (mAnimator.getAnimatedValue());
        angle = getAngleAndClearCanvas(time);

        // 円弧の領域設定
        mRect.set(x - radius2, y - radius2, x + radius2, y + radius2);

        // 円弧の描画
        mPaint.setStyle(Paint.Style.STROKE);

        mPaint.setStrokeWidth(300);
        mPaint.setARGB(100, 255, 255, 255);
        mCanvas.drawArc(mRect, AngleTarget, angle, false, mPaint);

        // 四角
        //mRect.offset(200, 0);
        //mCanvas.drawRoundRect(mRect, 30, 30, mPaint);

        // 円弧の領域設定
        mPaint.setStyle(Paint.Style.STROKE);

        mPaint.setStrokeWidth(1);
        mPaint.setARGB(255, 255, 255, 255);
        mCanvas.drawCircle(x, y, radius + 100, mPaint);
        mInit = false;

        // 円弧の領域設定
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(1);
        mPaint.setARGB(255, 255, 255, 255);
        mCanvas.drawCircle(x, y, radius, mPaint);
        mInit = false;
    }

    private int mLastPart = 0;
    private float getAngleAndClearCanvas(float time) {
        if (time <= 60)  {
            mLastPart = 0;
        } else if (time <= 120) {
            time -= 360;
            if (mLastPart != 1) {
                Log.d("draw", "clear canvas 1 : " + time);
                clearCanvas();
            }
            mLastPart = 1;
        } else if (time <= 180) {
            time -= 120;
            if (mLastPart != 2) {
                Log.d("draw", "clear canvas 2 : " + time);
                clearCanvas();
            }
            mLastPart = 2;
        } else if (time <= 240) {
            time -= 180;
            if (mLastPart != 3) {
                Log.d("draw", "clear canvas 3 : " + time);
                clearCanvas();
            }
            mLastPart = 3;
        }
        return time * 6.0f;
    }

    private void clearCanvas() {
        mPaint.setColor(Color.TRANSPARENT);
        mCanvas.drawArc(mRect, AngleTarget, 360, false, mPaint);
        mPaint.setColor(Color.BLACK);
        mInit = true;
    }
}
