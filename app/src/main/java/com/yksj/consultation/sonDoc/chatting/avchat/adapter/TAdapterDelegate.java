package com.yksj.consultation.sonDoc.chatting.avchat.adapter;

public interface TAdapterDelegate {

	public int getViewTypeCount();

	public Class<? extends TViewHolder> viewHolderAtPosition(int position);

	public boolean enabled(int position);
}