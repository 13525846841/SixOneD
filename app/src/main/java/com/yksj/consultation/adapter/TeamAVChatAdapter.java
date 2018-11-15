package com.yksj.consultation.adapter;

import android.support.v7.widget.RecyclerView;

import com.netease.nimlib.sdk.avchat.model.AVChatSurfaceViewRenderer;
import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.sonDoc.chatting.avchat.module.input.robot.BaseMultiItemFetchLoadAdapter;
import com.yksj.consultation.sonDoc.chatting.avchat.module.input.robot.BaseViewHolder;
import com.yksj.consultation.sonDoc.chatting.avchat.module.input.robot.animation.RecyclerViewHolder;
import com.yksj.consultation.sonDoc.chatting.avchat.team.TeamAVChatEmptyViewHolder;
import com.yksj.consultation.sonDoc.chatting.avchat.team.TeamAVChatItem;
import com.yksj.consultation.sonDoc.chatting.avchat.team.TeamAVChatItemViewHolder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by huangjun on 2017/5/4.
 */

public class TeamAVChatAdapter extends BaseMultiItemFetchLoadAdapter<TeamAVChatItem, BaseViewHolder> {

    private static final int VIEW_TYPE_DATA = 1;
    private static final int VIEW_TYPE_ADD = 2;
    private static final int VIEW_TYPE_HOLDER = 3;

    private Map<Class<? extends RecyclerViewHolder>, Integer> holder2ViewType;

    public TeamAVChatAdapter(RecyclerView recyclerView, List<TeamAVChatItem> data,int type) {
        super(recyclerView, data);

        holder2ViewType = new HashMap<>();
        if (type==0){
            addItemType(VIEW_TYPE_DATA, R.layout.team_avchat_item, TeamAVChatItemViewHolder.class);
            addItemType(VIEW_TYPE_HOLDER, R.layout.team_avchat_holder, TeamAVChatEmptyViewHolder.class);
        }else {
            addItemType(VIEW_TYPE_DATA, R.layout.team_avchat_item2, TeamAVChatItemViewHolder.class);
            addItemType(VIEW_TYPE_HOLDER, R.layout.team_avchat_holder2, TeamAVChatEmptyViewHolder.class);
        }

        holder2ViewType.put(TeamAVChatItemViewHolder.class, VIEW_TYPE_DATA);
        holder2ViewType.put(TeamAVChatEmptyViewHolder.class, VIEW_TYPE_HOLDER);
    }

    @Override
    protected int getViewType(TeamAVChatItem item) {
        if (item.type == TeamAVChatItem.TYPE.TYPE_DATA) {
            return VIEW_TYPE_DATA;
        } else if (item.type == TeamAVChatItem.TYPE.TYPE_HOLDER) {
            return VIEW_TYPE_HOLDER;
        } else {
            return VIEW_TYPE_ADD;
        }
    }

    @Override
    protected String getItemKey(TeamAVChatItem item) {
        return item.type + "_" + item.teamId + "_" + item.account;
    }

    public AVChatSurfaceViewRenderer getViewHolderSurfaceView(TeamAVChatItem item) {
        RecyclerViewHolder holder = getViewHolder(VIEW_TYPE_DATA, getItemKey(item));
//        Log.e(getItemKey(item),holder.toString());
        if (holder instanceof TeamAVChatItemViewHolder) {
            return ((TeamAVChatItemViewHolder) holder).getSurfaceView();
        }
        return null;
//        return ((TeamAVChatItemViewHolder) holder).getSurfaceView();
    }

    public void updateVolumeBar(TeamAVChatItem item) {
        RecyclerViewHolder holder = getViewHolder(VIEW_TYPE_DATA, getItemKey(item));
        if (holder instanceof TeamAVChatItemViewHolder) {
            ((TeamAVChatItemViewHolder) holder).updateVolume(item.volume);
        }
    }
}
