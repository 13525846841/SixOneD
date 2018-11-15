package com.yksj.consultation.adapter;

import android.support.annotation.IntRange;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.library.base.imageLoader.ImageLoader;
import com.yksj.consultation.bean.CommentPicture;
import com.yksj.consultation.sonDoc.R;

import java.util.List;

/**
 * 发布图片适配器
 */
public class SharePictureAdapte extends RecyclerView.Adapter {
    private static final int ADD_ITEM = 968;
    private static final int PICTURE_ITEM = ADD_ITEM + 1;
    private List<CommentPicture> mPictures;
    private SharePictureObserver mListener;
    private boolean mSelectable = true;
    private int mMaxNum;
    private RecyclerView.AdapterDataObserver mDataObserver = new RecyclerView.AdapterDataObserver() {
        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            super.onItemRangeChanged(positionStart, itemCount);
            if (mPictures.size() < mMaxNum) {
                setChoosable(true);
            }
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            super.onItemRangeInserted(positionStart, itemCount);
            if (mPictures.size() == mMaxNum) {
                setChoosable(false);
            }
        }
    };

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        registerAdapterDataObserver(mDataObserver);
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        unregisterAdapterDataObserver(mDataObserver);
    }

    public SharePictureAdapte(List<CommentPicture> pictures, int maxPictureNum) {
        this.mPictures = pictures;
        mMaxNum = maxPictureNum;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        if (viewType == ADD_ITEM) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_share_add_picture, parent, false);
        } else{
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_share_picture, parent, false);
        }
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case ADD_ITEM:
                ((ViewHolder) holder).imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mListener != null) {
                            mListener.onChoose(v);
                        }
                    }
                });
                break;
            case PICTURE_ITEM:
                CommentPicture item = getItem(position);
                ImageLoader.load(item.PICTURE_PATH).into(((ViewHolder) holder).imageView);
                ((ViewHolder) holder).deleteView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        remove(position);
                        if (mListener != null) {
                            mListener.onDelete(v, position);
                        }
                    }
                });
                break;
        }
    }

    public CommentPicture getItem(int position) {
        return mPictures.get(position);
    }

    public void setSharePictureObserver(SharePictureObserver listern) {
        this.mListener = listern;
    }

    /**
     * 移除图片
     * @param position
     */
    public void remove(@IntRange(from = 0) int position) {
        mPictures.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mPictures.size() - position);
    }

    /**
     * 添加图片
     * @param picture
     */
    public void addData(CommentPicture picture) {
        if (mPictures.size() < mMaxNum) {
            this.mPictures.add(picture);
            notifyItemInserted(mPictures.size());
        }
    }

    /**
     * 添加图片
     * @param pictures
     */
    public void addData(List<CommentPicture> pictures) {
        if (mPictures.size() < mMaxNum) {
            this.mPictures.addAll(pictures);
            notifyItemRangeInserted(this.mPictures.size() - pictures.size(), pictures.size());
        }
    }

    /**
     * 是否可选择图片
     * @param choosable
     */
    public void setChoosable(boolean choosable){
        this.mSelectable = choosable;
    }

    /**
     * 获取可选择的数量
     * @return
     */
    public int getChoosableNum() {
        return mMaxNum - mPictures.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mSelectable && position == mPictures.size() ? ADD_ITEM : PICTURE_ITEM;
    }

    @Override
    public int getItemCount() {
        return mSelectable ? mPictures.size() + 1 : mPictures.size();
    }

    private static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public ImageView deleteView;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.iv_picture);
            deleteView = itemView.findViewById(R.id.iv_delete);
        }
    }

    public abstract static class SharePictureObserver {
        public void onDelete(View v, int position) {
        }

        public void onChoose(View v) {
        }
    }
}
