package com.yksj.consultation.adapter;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.library.base.imageLoader.ImageLoader;
import com.yksj.consultation.app.AppContext;
import com.yksj.consultation.bean.MessageHistoryBean;
import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.utils.ViewHelper;
import com.yksj.healthtalk.entity.CustomerInfoEntity;
import com.yksj.healthtalk.net.http.ApiCallback;
import com.yksj.healthtalk.net.http.ApiService;
import com.yksj.healthtalk.utils.DataParseUtil;
import com.yksj.healthtalk.utils.EntityObjUtils;
import com.yksj.healthtalk.views.zlistview.enums.DragEdge;
import com.yksj.healthtalk.views.zlistview.enums.ShowMode;
import com.yksj.healthtalk.views.zlistview.widget.ZSwipeItem;

import org.json.JSONObject;

import okhttp3.Request;

public class MessageHistoryAdapter extends BaseQuickAdapter<MessageHistoryBean, BaseViewHolder> {

    private OnDoctorMessageAdapterListener mListener;

    public MessageHistoryAdapter() {
        super(R.layout.item_doctor_message);
    }

    public void setOnDoctorMessageAdapterListener(OnDoctorMessageAdapterListener listener) {
        this.mListener = listener;
    }

    @Override
    protected void convert(final BaseViewHolder helper, final MessageHistoryBean item) {
        final ZSwipeItem swipeItem = helper.getView(R.id.swipe_item);
        LinearLayout ll = helper.getView(R.id.ll);
        swipeItem.setShowMode(ShowMode.PullOut);
        swipeItem.setDragEdge(DragEdge.Right);
        ll.setOnClickListener(v -> {
            if (mListener != null) {
                mListener.onDeleteClick(v, helper.getAdapterPosition(), item);
            }
            swipeItem.close();
        });

        ImageView headImage = helper.getView(R.id.chat_head);
        if (item.isSystemMsg()) {
            headImage.setImageResource(R.drawable.ic_launcher);
        } else if (item.isGroup()) {
            String uri = AppContext.getApiRepository().LOADCUSHIDPICSERVLET42 + "?customerid=" + item.SEND_ID + "&isgroup=1";
            ImageLoader.loadGroupImage(uri).into(headImage);
        } else {
            final String userid = item.SEND_ID;
            String headerPath = null;
            CustomerInfoEntity baseInfoEntity = null;
            boolean isContain = AppContext.getAppData().cacheInformation.containsKey(userid);
            if (isContain) {// 包含资料
                baseInfoEntity = (CustomerInfoEntity) AppContext.getAppData().cacheInformation.get(userid);
                headerPath = baseInfoEntity.getNormalHeadIcon();
            } else {// 不包含资料请求
                baseInfoEntity = new CustomerInfoEntity();
                baseInfoEntity.setId(userid);
                AppContext.getAppData().cacheInformation.put(userid, baseInfoEntity);
                doQueryCustomerInfo(baseInfoEntity);
            }
            headerPath = AppContext.getApiRepository().URL_QUERYHEADIMAGE_NEW + headerPath;
            ImageLoader.loadAvatar(headerPath).into(headImage);
        }
        helper.setText(R.id.name, item.getName());
        helper.setText(R.id.message, item.getContent());
        helper.setText(R.id.time, item.SEND_TIME);
        ViewHelper.setTextForView(helper.getView(R.id.messagecount), item.NUMS == 0 ? "" : String.valueOf(item.NUMS));
    }

    /**
     * 清除已读消息提示
     * @param id
     */
    public void clearMessageHide(String id) {
        if (TextUtils.isEmpty(id)) return;
        for (MessageHistoryBean message : getData()) {
            if (!TextUtils.isEmpty(message.OBJECT_ID) && message.OBJECT_ID.equals(id)) {
                message.NUMS = 0;
                notifyItemChanged(getData().indexOf(message));
            }
        }
    }

    /**
     * 查询用户资料
     */
    private void doQueryCustomerInfo(final CustomerInfoEntity infoEntity) {
        ApiService.doGetCustomerInfoByCustId(infoEntity.getId(), new ApiCallback<JSONObject>() {
            @Override
            public void onError(Request request, Exception e) {
            }

            @Override
            public void onResponse(JSONObject response) {
                JSONObject object = response.optJSONObject("result");
                CustomerInfoEntity customerInfoEntity = DataParseUtil.JsonToCustmerInfo(object.optJSONObject("patientInfo"));
                try {
                    EntityObjUtils.copyProperties(customerInfoEntity, infoEntity);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }, this);
    }

    public interface OnDoctorMessageAdapterListener {
        void onDeleteClick(View v, int position, MessageHistoryBean item);
    }
}
