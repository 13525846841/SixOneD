package com.yksj.consultation.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

import com.blankj.utilcode.util.ImageUtils;
import com.yksj.consultation.sonDoc.R;

public class DoctorAvatarView extends AppCompatImageView {

    private Paint mBitmapPaint;
    private Paint mBorderPaint;
    private int mCenterX, mCenterY;
    private Matrix mMatrix;
    private BitmapShader mBitmapShader;
    private int mWidth;
    private int mHeight;
    private Bitmap mBitmap;
    private int mFirstBorderWidth;
    private int mSecendBorderWidth;
    private int mFirstBorderColor;
    private int mSecendBorderColor;
    private int mFirstBorderRadius;
    private int mSecendBorderRadius;
    private int mBitmapRadius;

    public DoctorAvatarView(Context context) {
        this(context, null);
    }

    public DoctorAvatarView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DoctorAvatarView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.DoctorAvatarView);
        mFirstBorderWidth = array.getDimensionPixelSize(R.styleable.DoctorAvatarView_firstBorderWidth, 0);
        mSecendBorderWidth = array.getDimensionPixelSize(R.styleable.DoctorAvatarView_secendBorderWidth, 0);
        mFirstBorderColor = array.getColor(R.styleable.DoctorAvatarView_firstBorderColor, Color.WHITE);
        mSecendBorderColor = array.getColor(R.styleable.DoctorAvatarView_secendBorderColor, Color.WHITE);
        array.recycle();

        initialize();
    }

    private void initialize() {
        mBitmapPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBorderPaint.setStyle(Paint.Style.STROKE);
        mMatrix = new Matrix();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        mCenterX = w / 2;
        mCenterY = h / 2;
        if (mFirstBorderWidth != 0) {
            mFirstBorderRadius = Math.min(w, h) / 2 - mFirstBorderWidth / 2;
        }
        if (mSecendBorderWidth != 0){
            mSecendBorderRadius = Math.min(w, h) / 2 - mFirstBorderWidth - mSecendBorderWidth / 2;
        }
        mBitmapRadius = Math.min(w, h) / 2 - mSecendBorderWidth - mFirstBorderWidth;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (getDrawable() != null) {
            Bitmap bitmap = ImageUtils.drawable2Bitmap(getDrawable());
            if (bitmap != null) {
                drawBitmap(canvas, bitmap);
                drawInnerBoder(canvas);
                drawOutBorder(canvas);
            } else {
                super.onDraw(canvas);
            }
        }
    }

    private void drawOutBorder(Canvas canvas) {
        if (mFirstBorderWidth != 0){
            mBorderPaint.setColor(mFirstBorderColor);
            mBorderPaint.setStrokeWidth(mFirstBorderWidth);
            canvas.drawCircle(mCenterX, mCenterY, mFirstBorderRadius, mBorderPaint);
        }
    }

    private void drawInnerBoder(Canvas canvas) {
        if (mSecendBorderWidth != 0){
            mBorderPaint.setColor(mSecendBorderColor);
            mBorderPaint.setStrokeWidth(mSecendBorderWidth);
            canvas.drawCircle(mCenterX, mCenterY, mSecendBorderRadius, mBorderPaint);
        }
    }

    private void drawBitmap(Canvas canvas, Bitmap bitmap) {
        if (mBitmapShader == null || mBitmap != bitmap) {
            mBitmap = bitmap;
            mBitmapShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        }

        final int bwidth = mBitmap.getWidth();
        final int bheight = mBitmap.getHeight();
        final int vwidth = getWidth() - getPaddingLeft() - getPaddingRight();
        final int vheight = getHeight() - getPaddingTop() - getPaddingBottom();
        float scale;
        float dx = 0, dy = 0;
        if (bwidth * vheight > vwidth * bwidth) {
            scale = (float) vheight / (float) bheight;
            dx = (vwidth - bwidth * scale) * 0.5f;
        } else {
            scale = (float) vwidth / (float) bwidth;
            dy = (vheight - bheight * scale) * 0.5f;
        }
        mMatrix.setScale(scale, scale);
        mMatrix.postTranslate(Math.round(dx), Math.round(dy));
        mBitmapShader.setLocalMatrix(mMatrix);
        mBitmapPaint.setShader(mBitmapShader);
        canvas.drawCircle(mCenterX, mCenterY, mBitmapRadius, mBitmapPaint);
    }
}
