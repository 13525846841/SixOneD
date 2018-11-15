package com.yksj.consultation.adapter;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.util.SizeUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.library.base.imageLoader.ImageLoader;
import com.library.base.widget.DividerGridItemDecoration;
import com.yksj.consultation.app.AppContext;
import com.yksj.consultation.bean.CommentPicture;
import com.yksj.consultation.bean.DoctorShareBean;
import com.yksj.consultation.bean.DoctorShareCommentBean;
import com.yksj.consultation.comm.ImageBrowserActivity;
import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.utils.DoctorHelper;
import com.yksj.healthtalk.utils.TimeUtil;

import java.util.List;

public class ShareListAdapter extends BaseQuickAdapter<DoctorShareBean, BaseViewHolder> {
    private OnDoctorShareAdapterListener mListener;
    // 缓存likeview
    private SparseArray<TextView> mLikeViews = new SparseArray();

    public ShareListAdapter() {
        super(R.layout.item_doctor_share);
    }

    /**
     * 改变like状态
     * @param position
     */
    public void likeChange(int position) {
        DoctorShareBean shareBean = getItem(position);
        int likeDrawable = shareBean.isLike() ? R.drawable.ic_comment_like : R.drawable.ic_comment_unlike;
        TextView likeView = mLikeViews.get(position);
        likeView.setCompoundDrawablesWithIntrinsicBounds(0, 0, likeDrawable, 0);
        likeView.setText(shareBean.PRAISE_COUNT + "");
    }

    @Override
    protected void convert(final BaseViewHolder helper, final DoctorShareBean item) {
        helper.setText(R.id.text_content, item.SHARE_CONTENT);
        helper.setText(R.id.text_share_time, TimeUtil.getTimeStr(item.PUBLIC_TIME));

        //删除
        helper.setVisible(R.id.text_share_delete, DoctorHelper.isSelf(item.CUSTOMER_ID));
        helper.setOnClickListener(R.id.text_share_delete, v -> {
            if (mListener != null) {
                mListener.onDeleteClick(helper.itemView, helper.getAdapterPosition(), item);
            }
        });
        //头像
        bindUserInfo(helper, item);
        //评论
        bindComment(helper, item);
        //图片
        bindPicture(helper, item);
    }

    /**
     * 绑定用户信息
     * @param helper
     * @param item
     */
    private void bindUserInfo(BaseViewHolder helper, DoctorShareBean item) {
        helper.setText(R.id.text_doc_name, item.CUSTOMER_NICKNAME);
        ImageView headView = helper.getView(R.id.share_doctor_head);
        int likeDrawable = item.isLike() ? R.drawable.ic_comment_like : R.drawable.ic_comment_unlike;
        TextView likeView = helper.getView(R.id.text_good_number);
        likeView.setCompoundDrawablesWithIntrinsicBounds(0, 0, likeDrawable, 0);
        likeView.setText(item.PRAISE_COUNT + "");
        mLikeViews.append(helper.getAdapterPosition(), likeView);
        helper.setOnClickListener(R.id.text_good_number, v -> {
            if (mListener != null) {
                mListener.onLikeClick(helper.itemView, helper.getAdapterPosition(), item);
            }
        });
        helper.setOnClickListener(R.id.text_doc_name, view -> {
            if (mListener != null) {
                mListener.onUserClick(helper.itemView, item.CUSTOMER_ID);
            }
        });
        String headUrl = AppContext.getApiRepository().URL_QUERYHEADIMAGE_NEW + item.CLIENT_ICON_BACKGROUP;
        ImageLoader.loadAvatar(headUrl)
                .into(headView);
        headView.setOnClickListener(view -> {
            if (mListener != null) {
                mListener.onUserClick(helper.itemView, item.CUSTOMER_ID);
            }
        });
    }

    /**
     * 绑定评论数据
     * @param helper
     * @param item
     */
    private void bindComment(final BaseViewHolder helper, final DoctorShareBean item) {
        helper.setGone(R.id.digCommentBody, !item.comment.isEmpty());
        RecyclerView commentRecycler = helper.getView(R.id.comment_recycler);
        commentRecycler.setLayoutManager(new LinearLayoutManager(mContext));
        DoctorShareListCommentAdapter commentAdapter = new DoctorShareListCommentAdapter();
        commentRecycler.setAdapter(commentAdapter);
        commentAdapter.setNewData(item.comment);
        commentAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (mListener != null) {
                    DoctorShareCommentBean comment = ((DoctorShareListCommentAdapter) adapter).getItem(position);
                    mListener.onCommentClick(view, helper.getAdapterPosition(), item, comment);
                }
            }
        });
        commentAdapter.setListener(new DoctorShareListCommentAdapter.OnShareListCommentAdapterClickListener() {
            @Override
            public void onUserClick(View view, String doctorId) {
                if (mListener != null) {
                    mListener.onUserClick(view, doctorId);
                }
            }
        });
        helper.setOnClickListener(R.id.snsBtn, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onCommentClick(v, helper.getAdapterPosition(), item, null);
                }
            }
        });
    }

    /**
     * 处理图片
     * @param helper
     * @param item
     */
    private void bindPicture(BaseViewHolder helper, DoctorShareBean item) {
        ImageView singlePictureView = helper.getView(R.id.iv_single_picture);
        RecyclerView pictureRecycler = helper.getView(R.id.picture_recycler);
        if (item.picture.isEmpty()) {//没有图片
            pictureRecycler.setVisibility(View.GONE);
            singlePictureView.setVisibility(View.GONE);
        } else if (item.picture.size() > 1) {//多张图片
            singlePictureView.setVisibility(View.GONE);
            pictureRecycler.setVisibility(View.VISIBLE);
            handleMultiPicture(item, pictureRecycler);
        } else {//单张图片
            singlePictureView.setVisibility(View.VISIBLE);
            pictureRecycler.setVisibility(View.GONE);
            handleSinglePicture(helper, item);
        }
    }

    /**
     * 处理多张图片
     * @param item
     * @param pictureRecycler
     */
    private void handleMultiPicture(DoctorShareBean item, RecyclerView pictureRecycler) {
        pictureRecycler.setLayoutManager(new GridLayoutManager(mContext, 3));
        if (pictureRecycler.getItemDecorationCount() < 1) {// 避免多次重复添加
            pictureRecycler.addItemDecoration(new DividerGridItemDecoration(SizeUtils.dp2px(4), false));
        }
        DoctorShareListPictureAdapter pictureAdapter = new DoctorShareListPictureAdapter();
        pictureRecycler.setAdapter(pictureAdapter);
        pictureAdapter.setNewData(item.picture);
        pictureAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                ImageBrowserActivity
                        .from(mContext)
                        .setImagePaths(pictureAdapter.getPicturePaths())
                        .setCurPosition(position)
                        .startActivity();
            }
        });
    }

    /**
     * 处理单张图片
     * @param helper
     * @param item
     */
    private void handleSinglePicture(BaseViewHolder helper, DoctorShareBean item) {
        final ImageView singlePictureView = helper.getView(R.id.iv_single_picture);
        List<CommentPicture> pictures = item.picture;
        if (pictures.isEmpty() || pictures.get(0).WIDTH == 0 || pictures.get(0).HEIGHT == 0) {
            singlePictureView.setVisibility(View.GONE);
            return;
        }
        singlePictureView.setVisibility(View.VISIBLE);
        final CommentPicture picture = item.picture.get(0);
        String picturePath = AppContext.getApiRepository().URL_QUERYHEADIMAGE_NEW + picture.PICTURE_PATH;
        int width = picture.WIDTH;
        int height = picture.HEIGHT;
        float maxWidth = SizeUtils.dp2px(230);
        float maxHeight = SizeUtils.dp2px(200);
        float scale = 0;
        if (height > maxHeight || width > maxWidth) {//宽度或者高度大于最大值进行缩放
            if (height > width) {//高度大于宽度安高度比例缩放
                scale = maxHeight / height;
            }
            if (width > height) {//宽度大于高度安宽度比例缩放
                scale = maxWidth / width;
            }
            width = (int) (width * scale);
            height = (int) (height * scale);
        }
        // 加载图片
        ImageLoader.load(picturePath).into(singlePictureView);
        // 重新设置imageview大小
        ViewGroup.LayoutParams lp = singlePictureView.getLayoutParams();
        lp.width = width;
        lp.height = height;
        singlePictureView.requestLayout();

        singlePictureView.setOnClickListener(v ->
                ImageBrowserActivity.BrowserSpace
                        .from(mContext)
                        .setImagePath(picturePath)
                        .startActivity());
    }

    public void setOnCommentClickListener(OnDoctorShareAdapterListener listener) {
        this.mListener = listener;
    }

    public interface OnDoctorShareAdapterListener {
        void onCommentClick(View view, int position, DoctorShareBean doctorShare, DoctorShareCommentBean comment);

        void onDeleteClick(View view, int position, DoctorShareBean doctorShare);

        void onLikeClick(View view, int position, DoctorShareBean doctorShare);

        void onUserClick(View view, String doctorId);
    }
}
