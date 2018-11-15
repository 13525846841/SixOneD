package com.library.base.dialog;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.library.base.R;
import com.library.base.base.BaseDialog;
import com.library.base.base.ViewHolder;
import com.library.base.widget.DividerListItemDecoration;

import java.util.Arrays;

public class SelectorDialog extends BaseDialog {

    private static final String ITEM_EXTRA = "item_extra";
    private OnMenuItemClickListener mItemClickListener;

    public static SelectorDialog newInstance(String[] items) {
        Bundle args = new Bundle();
        args.putStringArray(ITEM_EXTRA, items);

        SelectorDialog dialog = new SelectorDialog();
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public int createContentLayoutRes() {
        return R.layout.dialog_photo_choose_menu;
    }

    @Override
    public void convertView(ViewHolder holder, final BaseDialog dialog) {
        RecyclerView recyclerView = holder.getView(R.id.recycler_view);
        SimpleAdapter adapter = new SimpleAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DividerListItemDecoration());
        adapter.setOnItemClickListener(this::onItemClick);
        String[] items = getArguments().getStringArray(ITEM_EXTRA);
        adapter.setNewData(Arrays.asList(items));
        holder.setOnClickListener(R.id.tv_cancel, this::onCancel);
        setShowBottom(true);
    }

    /**
     * 菜单条目点击事件
     * @param adapter
     * @param view
     * @param position
     */
    private void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        dismiss();
        if (mItemClickListener != null) {
            mItemClickListener.onItemClick(this, position);
        }
    }

    /**
     * 设置菜单条目点击监听
     * @param listener
     */
    public SelectorDialog setOnItemClickListener(OnMenuItemClickListener listener){
        this.mItemClickListener = listener;
        return this;
    }

    /**
     * 取消
     * @param view
     */
    private void onCancel(View view) {
        dismiss();
    }

    public static class SimpleAdapter extends BaseQuickAdapter<String, BaseViewHolder>{

        public SimpleAdapter() {
            super(R.layout.simple_menu_item);
        }

        @Override
        protected void convert(BaseViewHolder helper, String item) {
            helper.setText(R.id.menu_text, item);
        }
    }

    public interface OnMenuItemClickListener{
        void onItemClick(SelectorDialog dialog, int position);
    }
}
