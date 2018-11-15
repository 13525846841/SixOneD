package com.library.base.widget;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.blankj.utilcode.util.Utils;
import com.library.base.R;

/**
 * Created by Vincent Woo
 * Date: 2016/10/25
 * Time: 14:44
 */

public class DividerListItemDecoration extends RecyclerView.ItemDecoration {
    private int mOrientation;
    private int mSpacing;
    private ColorDrawable mColorDrawable;

    public DividerListItemDecoration() {
        this(LinearLayoutManager.VERTICAL);
    }

    public DividerListItemDecoration(int orientation) {
        this(orientation, 1, Utils.getApp().getResources().getColor(R.color.divider));
    }

    public DividerListItemDecoration(int orientation, int spacing) {
        this(orientation, spacing, Utils.getApp().getResources().getColor(R.color.divider));
    }

    public DividerListItemDecoration(int orientation, int spacing, int color) {
        if (orientation != LinearLayoutManager.VERTICAL && orientation != LinearLayoutManager.HORIZONTAL) {
            throw new IllegalArgumentException("Orientation Only Support LinearLayoutManager.VERTICAL " +
                    "or LinearLayoutManager.HORIZONTAL");
        }
        this.mOrientation = orientation;
        this.mSpacing = spacing;
        if (spacing != 0) mColorDrawable = new ColorDrawable(color);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        if (mSpacing == 0){
            return;
        }
        int itemCount = parent.getAdapter().getItemCount();
        int position = parent.getChildAdapterPosition(view);
        if (mOrientation == LinearLayoutManager.VERTICAL) {
            //如果是最后一行不画下边
            outRect.set(0, 0, 0, (position + 1) == itemCount ? 0 : mSpacing);
        } else {
            //如果是最后一行不画右边
            outRect.set(0, 0, (position + 1) == itemCount ? 0 : mSpacing, 0);
        }
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
        if (mColorDrawable == null) {
            return;
        }
        if (mOrientation == LinearLayoutManager.VERTICAL) {
            drawVertical(c, parent);
        } else {
            drawHorizontal(c, parent);
        }
    }

    private void drawHorizontal(Canvas c, RecyclerView parent) {
        final int itemCount = parent.getChildCount();
        final int top = parent.getPaddingTop();
        for (int i = 0; i < itemCount; i++) {
            final View child = parent.getChildAt(i);
            if (child == null) return;
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            final int bottom = child.getHeight() - parent.getPaddingBottom();
            final int left = child.getRight() + params.rightMargin;
            final int right = left + mSpacing;
            mColorDrawable.setBounds(left, top, right, bottom);
            mColorDrawable.draw(c);
        }
    }

    private void drawVertical(Canvas c, RecyclerView parent) {
        final int itemCount = parent.getChildCount();
        final int left = parent.getPaddingLeft();
        final int right = parent.getWidth() - parent.getPaddingRight();
        for (int i = 0; i < itemCount; i++) {
            final View child = parent.getChildAt(i);
            if (child == null) return;
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            final int top = child.getBottom() + params.bottomMargin;
            final int bottom = top + mSpacing;
            mColorDrawable.setBounds(left, top, right, bottom);
            mColorDrawable.draw(c);
        }
    }
}
