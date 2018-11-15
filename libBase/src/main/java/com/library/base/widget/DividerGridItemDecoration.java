package com.library.base.widget;

import android.graphics.Rect;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;

/**
 * Created by Vincent Woo
 * Date: 2016/10/13
 * Time: 17:26
 */

public class DividerGridItemDecoration extends RecyclerView.ItemDecoration {
    private int mSpanCount;
    private int mSpacing;
    private boolean mIncludeEdge;

    public DividerGridItemDecoration(int spacing, boolean includeEdge) {
        this.mSpacing = spacing;
        this.mIncludeEdge = includeEdge;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        mSpanCount = ((GridLayoutManager) parent.getLayoutManager()).getSpanCount();
        int position = parent.getChildAdapterPosition(view); // item position
        int column = position % mSpanCount; // item column
        RecyclerView.Adapter adapter = parent.getAdapter();

        boolean hasHeader = hasHeader(adapter);
        if (hasHeader && position == 0){
            return;
        }
        if (hasHeader) {
            position -= headerCount(adapter);
            column = position % mSpanCount; // item column
        }

        if (mIncludeEdge) {
            // spacing - column * ((1f / spanCount) * spacing)
            outRect.left = mSpacing - column * mSpacing / mSpanCount;
            // (column + 1) * ((1f / spanCount) * spacing)
            outRect.right = (column + 1) * mSpacing / mSpanCount;

            if (position < mSpanCount) { // top edge
                outRect.top = mSpacing;
            }
            outRect.bottom = mSpacing; // item bottom
        } else {
            // column * ((1f / spanCount) * spacing)
            outRect.left = column * mSpacing / mSpanCount;
            // spacing - (column + 1) * ((1f / spanCount) * spacing)
            outRect.right = mSpacing - (column + 1) * mSpacing / mSpanCount;
            if (position >= mSpanCount) {
                outRect.top = mSpacing; // item top
            }
        }
    }

    private boolean hasHeader(RecyclerView.Adapter adapter) {
        return headerCount(adapter) > 0;
    }

    private int headerCount(RecyclerView.Adapter adapter){
        if (adapter instanceof BaseQuickAdapter){
            return ((BaseQuickAdapter) adapter).getHeaderLayoutCount();
        }
        return -1;
    }
}
