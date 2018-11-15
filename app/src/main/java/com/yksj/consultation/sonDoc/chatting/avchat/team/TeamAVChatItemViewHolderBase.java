package com.yksj.consultation.sonDoc.chatting.avchat.team;


import com.yksj.consultation.sonDoc.chatting.avchat.module.input.robot.BaseMultiItemFetchLoadAdapter;
import com.yksj.consultation.sonDoc.chatting.avchat.module.input.robot.animation.RecyclerViewHolder;
import com.yksj.consultation.sonDoc.chatting.avchat.module.input.robot.BaseViewHolder;
/**
 * Created by huangjun on 2017/5/9.
 */

abstract class TeamAVChatItemViewHolderBase extends RecyclerViewHolder<BaseMultiItemFetchLoadAdapter, BaseViewHolder, TeamAVChatItem> {

    TeamAVChatItemViewHolderBase(BaseMultiItemFetchLoadAdapter adapter) {
        super(adapter);
    }

    @Override
    public void convert(final BaseViewHolder holder, TeamAVChatItem data, int position, boolean isScrolling) {
        inflate(holder);
        refresh(data);
    }

    protected abstract void inflate(final BaseViewHolder holder);

    protected abstract void refresh(final TeamAVChatItem data);
}