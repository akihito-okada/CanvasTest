package com.mellowsnow.countdownanimationsample;

import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

/**
 * Created by Akihito on 2016/10/30.
 */

public class CountDownView extends View {

    private ValueAnimator mAnimator;
    private final Paint mPaint;
    private RectF mRectF;
    private Rect mRect = new Rect();

    // Arc Width
    private int mStrokeWidth;
    private final static int CIRCLE_STROKE_WIDTH = 1;
    // Text Size
    private int mTextSizePx;
    private final static int TEXT_SIZE_DP = 70;
    // Animation Start Angle
    private static final int mAngleTarget = 270;
    // Diff inner/outer circle radius
    private int mDiffCircleRaidusPx;
    private static final int DIFF_CIRCLE_RADIUS_DP = 10;
    // Initial Angle
    private float mAngle = 20;
    // Count Number
    private String mCountNumber = 4 + "";

    private AudioAttributes mAudioAttributes;
    private SoundPool mSoundPool;
    private int mSoundOne;

    private final static int COUNT_IN_DURATION = 4000;

    public CountDownView(Context context, AttributeSet attrs) {
        super(context, attrs);

        // set initial values
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mRectF = new RectF();
        mStrokeWidth = dpToPx(CIRCLE_STROKE_WIDTH, getResources());
        mTextSizePx = dpToPx(TEXT_SIZE_DP, getResources());
        mDiffCircleRaidusPx = dpToPx(DIFF_CIRCLE_RADIUS_DP, getResources());

        // set soundpool
        mSoundPool = buildSoundPool(1);
        mSoundOne = mSoundPool.load(context, R.raw.tick, 1);

        // start animation
        mAnimator = ValueAnimator.ofFloat(0.f, 0.f);
        mAnimator.setDuration(COUNT_IN_DURATION);
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
        float radius = canvas.getWidth()/4;
        float radiusArc = radius/2;

        // draw background
        canvas.drawARGB(200, 0, 0, 0);

        // draw arc
        long time = (long) mAnimator.getCurrentPlayTime();
        mAngle = getAngleAndClearCanvas(time);
        mRectF.set(x - radiusArc, y - radiusArc, x + radiusArc, y + radiusArc);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(radius);
        mPaint.setARGB(200, 255, 255, 255);
        canvas.drawArc(mRectF, mAngleTarget, mAngle, false, mPaint);

        // draw outer/inner circle
        mPaint.setStrokeWidth(mStrokeWidth);
        mPaint.setARGB(255, 255, 255, 255);
        canvas.drawCircle(x, y, radius + mDiffCircleRaidusPx, mPaint);
        canvas.drawCircle(x, y, radius, mPaint);

        // draw number
        mPaint.setARGB(255, 221, 49, 110);
        mPaint.setTextSize(mTextSizePx);
        mPaint.setStyle(Paint.Style.FILL);
        drawCenter(canvas, mPaint , mCountNumber);
    }

    private int mLastPart = -1;
    private float getAngleAndClearCanvas(long time) {
        if (time <= COUNT_IN_DURATION/4)  {
            mCountNumber = 4 + "";
            if (mLastPart == -1) playTick();
            mLastPart = 0;
        } else if (time <= COUNT_IN_DURATION/4*2) {
            time -= COUNT_IN_DURATION/4;
            mCountNumber = 3 + "";
            if (mLastPart == 0) playTick();
            mLastPart = 1;
        } else if (time <= COUNT_IN_DURATION/4*3) {
            time -= COUNT_IN_DURATION/4*2;
            mCountNumber = 2 + "";
            if (mLastPart == 1) playTick();
            mLastPart = 2;
        } else if (time <= COUNT_IN_DURATION) {
            time -= COUNT_IN_DURATION/4*3;
            mCountNumber = 1 + "";
            if (mLastPart == 2) playTick();
            mLastPart = 3;
        }
        return 360 * time / (COUNT_IN_DURATION/4);
    }

    private void playTick() {
        // play(ID, left volume, right volume, priority, loop count, play speed)
        mSoundPool.play(mSoundOne, 1.0f, 1.0f, 2, 0, 1);
    }

    /**
     * Convert Dp to Pixel
     */
    public static int dpToPx(float dp, Resources resources) {
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.getDisplayMetrics());
        return (int) px;
    }

    private void drawCenter(Canvas canvas, Paint paint, String text) {
        canvas.getClipBounds(mRect);
        float cHeight = mRect.height();
        float cWidth = mRect.width();
        paint.setTextAlign(Paint.Align.LEFT);
        paint.getTextBounds(text, 0, text.length(), mRect);
        float x = cWidth / 2f - mRect.width() / 2f - mRect.left;
        float y = cHeight / 2f + mRect.height() / 2f - mRect.bottom;
        canvas.drawText(text, x, y, paint);
    }

    @SuppressWarnings("deprecation")
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private SoundPool buildSoundPool(int poolMax)
    {
        SoundPool pool = null;

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            pool = new SoundPool(poolMax, AudioManager.STREAM_MUSIC, 0);
        }
        else {
            AudioAttributes attr = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build();
            pool = new SoundPool.Builder()
                    .setAudioAttributes(attr)
                    .setMaxStreams(poolMax)
                    .build();
        }
        return pool;
    }

}
