package com.yksj.consultation.sonDoc.chatting.avchat.module.input.robot.animation;

import android.support.v7.widget.RecyclerView;

import com.yksj.consultation.sonDoc.chatting.avchat.module.input.robot.BaseViewHolder;


/**
 * Created by huangjun on 2016/12/11.
 */

public abstract class RecyclerViewHolder<T extends RecyclerView.Adapter, V extends BaseViewHolder, K> {
    final private T adapter;

    public RecyclerViewHolder(T adapter) {
        this.adapter = adapter;
    }

    public T getAdapter() {
        return adapter;
    }

    public abstract void convert(V holder, K data, int position, boolean isScrolling);
}
