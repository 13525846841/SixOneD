package com.library.base.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.blankj.utilcode.util.ImageUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.library.base.R;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshKernel;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;

public class SimpleRefreshHeader extends RelativeLayout implements RefreshHeader {

    private RotateAnimation mRotateAnimation;
    private ImageView mImageView;
    private int mRotationPivotX;
    private int mRotationPivotY;
    private Matrix mRotateMatrix;

    public SimpleRefreshHeader(Context context) {
        this(context, null);
    }

    public SimpleRefreshHeader(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SimpleRefreshHeader(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setMinimumHeight( SizeUtils.dp2px(80));
    }

    @NonNull
    @Override
    public View getView() {
        return this;
    }

    @NonNull
    @Override
    public SpinnerStyle getSpinnerStyle() {
        return SpinnerStyle.Scale;
    }

    @Override
    public void onInitialized(@NonNull RefreshKernel kernel, int height, int maxDragHeight) {
        mImageView = new ImageView(getContext());
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.addRule(CENTER_IN_PARENT);
        mImageView.setLayoutParams(params);
        addView(mImageView);

        Bitmap bitmap = ImageUtils.getBitmap(R.drawable.default_ptr_rotate);
        mRotationPivotX = bitmap.getWidth() / 2;
        mRotationPivotY = bitmap.getHeight() / 2;
        mImageView.setImageBitmap(bitmap);
        mImageView.setScaleType(ImageView.ScaleType.MATRIX);

        mRotateMatrix = new Matrix();

        mRotateAnimation = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mRotateAnimation.setInterpolator(new LinearInterpolator());
        mRotateAnimation.setDuration(1200);
        mRotateAnimation.setRepeatCount(Animation.INFINITE);
        mRotateAnimation.setRepeatMode(Animation.RESTART);
    }

    @Override
    public void onMoving(boolean isDragging, float percent, int offset, int height, int maxDragHeight) {
        if (isDragging) {
            mRotateMatrix.setRotate(offset, mRotationPivotX, mRotationPivotY);
            mImageView.setImageMatrix(mRotateMatrix);
        }
    }

    @Override
    public void onStartAnimator(@NonNull RefreshLayout refreshLayout, int height, int maxDragHeight) {
        mImageView.startAnimation(mRotateAnimation);
    }

    @Override
    public int onFinish(@NonNull RefreshLayout refreshLayout, boolean success) {
        mImageView.clearAnimation();
        return 0;
    }

    @Override
    public void onStateChanged(@NonNull RefreshLayout refreshLayout, @NonNull RefreshState oldState, @NonNull RefreshState newState) {
        switch (newState) {
            case None:
            case PullDownToRefresh:
            case Refreshing:
            case ReleaseToRefresh:
                break;
        }
    }

    @Override
    public void onReleased(@NonNull RefreshLayout refreshLayout, int height, int maxDragHeight) {
        mRotateMatrix.reset();
        mImageView.setImageMatrix(mRotateMatrix);
    }

    @Override
    public void setPrimaryColors(int... colors) {
    }

    @Override
    public void onHorizontalDrag(float percentX, int offsetX, int offsetMax) {
    }

    @Override
    public boolean isSupportHorizontalDrag() {
        return false;
    }
}
