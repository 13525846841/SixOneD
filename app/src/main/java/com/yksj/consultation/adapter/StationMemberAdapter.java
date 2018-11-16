package com.yksj.consultation.adapter;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseViewHolder;
import com.library.base.imageLoader.ImageLoader;
import com.yksj.consultation.app.AppContext;
import com.yksj.consultation.bean.StationMemberBean;
import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.utils.ViewHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ${chen} on 2017/7/5.
 */
public class StationMemberAdapter extends RecyclerView.Adapter {

    public static final int NORMAL_TYPE = 0;
    public static final int ADD_MEMBER_TYPE = 1;

    private List<StationMemberBean> mDatas = new ArrayList<>();
    private boolean isShowAddMember;
    private OnItemClickListener itemClickListener;

    public StationMemberAdapter(boolean isShowAddMember) {
        this.isShowAddMember = isShowAddMember;
    }

    public List<StationMemberBean> getDatas() {
        return mDatas;
    }

    @Override
    public int getItemViewType(int position) {
        if (isShowAddMember) {
            if (position == 0) {
                return ADD_MEMBER_TYPE;
            } else {
                return NORMAL_TYPE;
            }
        }
        return NORMAL_TYPE;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        if (viewType == ADD_MEMBER_TYPE) {
            view = layoutInflater.inflate(R.layout.item_station_add_member, parent, false);
        } else {
            view = layoutInflater.inflate(R.layout.item_station_member, parent, false);
        }
        return new BaseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final BaseViewHolder helper = (BaseViewHolder) holder;
        int itemViewType = getItemViewType(position);
        if (itemViewType == NORMAL_TYPE) {
            StationMemberBean item = getItem(position);
            ViewHelper.setTextForView(helper.getView(R.id.tv_name), item.DOCTOR_REAL_NAME);
            ViewHelper.setTextForView(helper.getView(R.id.tv_address), item.WORK_LOCATION_DESC);
            ViewHelper.setTextForView(helper.getView(R.id.tv_job), item.TITLE_NAME);
            ViewHelper.setTextForView(helper.getView(R.id.tv_departments), item.OFFICE_NAME);
            ViewHelper.setTextForView(helper.getView(R.id.tv_type), item.MEMBER_TYPE_NAME);
            helper.setImageResource(R.id.iv_type, item.getTypeIcon());
            String url = AppContext.getApiRepository().URL_QUERYHEADIMAGE_NEW
                    + (TextUtils.isEmpty(item.BIG_ICON_BACKGROUND) ? item.DOCTOR_PICTURE : item.BIG_ICON_BACKGROUND);
            ImageView headView = helper.getView(R.id.det_img_head);
            ImageLoader.loadAvatar(url).centerCrop().into(headView);
            helper.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (itemClickListener != null) {
                        itemClickListener.onItemClick(helper.getConvertView(), position);
                    }
                }
            });
        } else if (itemViewType == ADD_MEMBER_TYPE){
            helper.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (itemClickListener != null) {
                        itemClickListener.onAddClick(helper.getConvertView(), position);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return isShowAddMember ? mDatas.size() + 1 : mDatas.size();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.itemClickListener = listener;
    }

    public void setNewData(@Nullable List<StationMemberBean> data) {
        this.mDatas = data == null ? new ArrayList<StationMemberBean>() : data;
        notifyDataSetChanged();
    }

    public void addData(List<StationMemberBean> newData) {
        mDatas.addAll(newData);
        notifyItemRangeInserted(mDatas.size() - newData.size() + (isShowAddMember ? 1 : 0), newData.size());
    }

    public StationMemberBean getItem(int position) {
        return mDatas.get(getRealPosistion(position));
    }

    public int getRealPosistion(int position) {
        return isShowAddMember ? position - 1 : position;
    }

    public interface OnItemClickListener {
        void onAddClick(View view, int position);

        void onItemClick(View view, int position);
    }
}
