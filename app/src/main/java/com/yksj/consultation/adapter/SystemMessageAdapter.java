package com.yksj.consultation.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.TextView;

import com.blankj.utilcode.util.SpanUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yksj.consultation.comm.SingleBtnFragmentDialog;
import com.yksj.consultation.constant.Constant;
import com.yksj.consultation.doctor.MyOrderActivity;
import com.yksj.consultation.doctor.DoctorServiceSettingsActivity;
import com.yksj.consultation.im.LinkTypeConstant;
import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.sonDoc.consultation.AtyOutPatientDetail;
import com.yksj.consultation.sonDoc.consultation.consultationorders.AtyOrderDetails;
import com.yksj.consultation.sonDoc.consultation.consultationorders.AtyOrdersDetails;
import com.yksj.consultation.sonDoc.consultation.main.InviteMemActivity;
import com.yksj.consultation.sonDoc.doctor.MyInfoActivity;
import com.yksj.consultation.station.StationInvitedActivity;
import com.yksj.consultation.utils.ActivityHelper;
import com.yksj.consultation.utils.DoctorHelper;
import com.yksj.consultation.utils.ViewHelper;
import com.yksj.healthtalk.entity.MessageEntity;
import com.yksj.healthtalk.net.http.ApiCallback;
import com.yksj.healthtalk.net.http.ApiCallbackWrapper;
import com.yksj.healthtalk.net.http.ApiService;
import com.yksj.healthtalk.net.socket.SmartFoxClient;
import com.yksj.healthtalk.utils.FaceParse;
import com.yksj.healthtalk.utils.HStringUtil;
import com.yksj.healthtalk.utils.SpannableClickable;
import com.yksj.healthtalk.utils.TimeUtil;
import com.yksj.healthtalk.utils.ToastUtil;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Request;

public class SystemMessageAdapter extends BaseQuickAdapter<MessageEntity, BaseViewHolder> {

    private FaceParse mFaceParse;
    private String mOrderId;
    private Context mContext;

    public SystemMessageAdapter(Context context, String orderId) {
        super(R.layout.item_system_message);
        this.mContext = context;
        this.mFaceParse = FaceParse.getChatFaceParse(context);
        this.mOrderId = orderId;
    }

    public int getBottomPosition(){
        return getItemCount() - 1;
    }

    @Override
    protected void convert(BaseViewHolder helper, MessageEntity item) {
        if (item.contCharSequence == null) {
            final JSONArray array = item.getContentJsonArray();
            if (array == null) {// 非连接
                item.contCharSequence = mFaceParse.parseSmileTxt(item.getContent());
            } else {
                item.contCharSequence = onParseLinkTxt(array, helper, item);
            }
        }
        CharSequence content = "1".equals(item.getIsWeChat()) ? Html.fromHtml(item.contCharSequence.toString()) : item.contCharSequence;
        helper.setText(R.id.chat_content, content);
        ((TextView) helper.getView(R.id.chat_content)).setMovementMethod(LinkMovementMethod.getInstance());
        int position = helper.getAdapterPosition();
        ViewHelper.setTextForView(helper.getView(R.id.chat_time), TimeUtil.getChatTime(position <= 0 ? 0 : getItem(--position).getDate(), item.getDate()));
    }

    /**
     * 解析需要跳转的内容链接
     */
    private CharSequence onParseLinkTxt(JSONArray jsonArray, BaseViewHolder holder, MessageEntity entity) {
        String objectType = null;
        if (jsonArray == null)
            return "";
        SpannableStringBuilder builder = new SpannableStringBuilder();
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                int type = jsonObject.getInt("type");
                if (type == 1) {// 文字
                    final String content = jsonObject.getString("cont");
                    builder.append(content);
                } else if (type == 2) {// 表情
                    final String content = jsonObject.getString("cont");
                    builder.append(mFaceParse.parseSmileTxt(content));
                } else if (type == 3) {// 文字跳转
                    int typeTmp = 3;// 跳转类型
                    final String content = jsonObject.optString("cont", "  ");
                    String customerId;// jsonObject.optString("customerId","");
                    if (jsonObject.has("customerId")) {
                        customerId = jsonObject.optString("customerId", "  ");
                    } else {
                        customerId = entity.getSenderId();
                    }
                    String param = "";
                    String linkType = jsonObject.optString("linkType", null);
                    String object_id = jsonObject.optString("Object_ID");
                    if (LinkTypeConstant.GROUP_CHAT.equals(linkType)) {//salon
                        typeTmp = 1;
                    } else if (LinkTypeConstant.SINGLE_CHAT.equals(linkType)) {//person
                        typeTmp = 2;
                    } else if ("sarchDoctor".equals(linkType)) {//科室
                        typeTmp = 4;
                        customerId = jsonObject.optString("officeId");
                    } else if (LinkTypeConstant.ORDER_INFO.equals(linkType)) {//去订单详情
                        typeTmp = 5;
                        if (jsonObject.has("Object_ID")) {
                            customerId = jsonObject.optString("Object_ID");//这里存放会诊状态
                        } else if (jsonObject.has("object_id")) {
                            customerId = jsonObject.optString("object_id");//这里存放会诊状态
                        }
                        param = jsonObject.optString("consultationStatus");
                        if (jsonObject.has("Object_Type")) {
                            objectType = jsonObject.optString("Object_Type");
                        } else if (jsonObject.has("object_type")) {
                            objectType = jsonObject.optString("object_type");
                        }
                    } else if (LinkTypeConstant.TY.equals(linkType)) {//特殊服务体验
                        typeTmp = 6;
                    } else if (LinkTypeConstant.TW.equals(linkType)) {//特殊服务图文
                        typeTmp = 7;
                    } else if (LinkTypeConstant.BY.equals(linkType)) {//特殊服务包月
                        typeTmp = 8;
                    } else if (LinkTypeConstant.DH.equals(linkType)) {//特殊服务电话
                        typeTmp = 15;
                    } else if (LinkTypeConstant.SP.equals(linkType)) {//特殊服务视频
                        typeTmp = 16;
                    } else if (linkType.startsWith(LinkTypeConstant.S_TW)) {//医生集团图文
                        typeTmp = 9;
                    } else if (linkType.startsWith(LinkTypeConstant.S_DH)) {//医生集团电话
                        typeTmp = 10;
                    } else if (linkType.startsWith(LinkTypeConstant.S_SP)) {//医生集团视频
                        typeTmp = 11;
                    } else if (linkType.startsWith(LinkTypeConstant.INVITE)) {//医生集团邀请
                        typeTmp = 12;
                    } else if (linkType.startsWith(LinkTypeConstant.APPLY)) {//医生集团院长接受申请
                        typeTmp = 13;
                    } else if (linkType.startsWith(LinkTypeConstant.SUCCESS_APPLY)) {//医生集团申请成功
//                        setSiteId(linkType);
                    } else if (linkType.startsWith(LinkTypeConstant.INVITE_EXPERT)) {//会诊邀请
                        typeTmp = 14;
                    } else if (linkType.startsWith(LinkTypeConstant.INVITE_TUWEN)) {//图文邀请
                        typeTmp = 17;
                    } else {// 药品跳转
                        typeTmp = 3;
                    }
                    createLinkType(builder, typeTmp, content, object_id, customerId, param, objectType, linkType);
                } else if (type == 4) {// 图片显示
//                    String srcImage = jsonObject.optString("url", null);
//                    if (srcImage == null)
//                        continue;
//                    String path = mImageLoader.getDownPathUri(srcImage);
//                    path = MemoryCacheUtil.generateKey(path, mImageSize);
//                    Bitmap bitmap = mImageLoader.getMemoryCache().get(path);
//                    boolean isNeedDown = false;
//                    Drawable drawable;
//                    if (bitmap != null) {// 不要下載
//                        drawable = new BitmapDrawable(bitmap);
//                        drawable.setBounds(0, 0, mImageSize.getWidth(),
//                                mImageSize.getHeight());
//                    } else {// 下載
//                        isNeedDown = true;
//                        //assets/customerIcons/s_zcmale_24.png
//                        if ("头像不知名_24.png".equals(srcImage)) {
//                            drawable = mUserDrawable;
//                        } else if ("groupdefault_24.png".equals(srcImage)) {
//                            drawable = mGroupDrawable;
//                        } else if ("//assets/customerIcons/s_zcmale_24.png".equals(srcImage)) {
//                            drawable = manDrawable;
//                        } else {
//                            drawable = mUserDrawable;
//                        }
//                    }
//                    ImageSpan imageSpan = new ImageSpan(drawable);
//                    SpannableString spannableString = new SpannableString(
//                            srcImage);
//                    spannableString.setSpan(imageSpan, 0,
//                            spannableString.length(),
//                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//                    builder.append(spannableString);
//                    // 是否需要下载
//                    if (isNeedDown) {
//                        int startIndex = builder.getSpanStart(imageSpan);
//                        int endIndex = builder.getSpanEnd(imageSpan);
//                        mImageLoader.loadImage(mContext, srcImage,
//                                new SpanImageLoadListener(builder, mImageSize,
//                                        holder.contentTextV, srcImage,
//                                        new int[]{startIndex, endIndex}));
//                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return builder;
    }

    private void createLinkType(SpannableStringBuilder builder, int typeTmp, final String content, String order_id, String customerId, String param, String object_type, String linkType) {
        if (content.contains("&")) {
            String[] keys = content.split("&");
            if (keys.length > 1) {
                builder.append(createLikeClick(17, customerId, keys[0], param, object_type, linkType, order_id)
                        .create());
                builder.append("，");
                builder.append(createLikeClick(18, customerId, keys[1], param, object_type, linkType, order_id)
                        .create());
            }
        } else {
            builder.append(createLikeClick(typeTmp, customerId, content, param, object_type, linkType, order_id)
                    .create());
        }
    }

    private SpanUtils createLikeClick(int type, String id, String content, String param, String object_type, String linkType, String object_id) {
        return new SpanUtils()
                .append(content)
                .setClickSpan(new SpannableClickable() {
                    @Override
                    public void updateDrawState(TextPaint ds) {
                        super.updateDrawState(ds);
                        ds.setColor(Color.argb(255, 19, 109, 215));       //设置文件颜色
                        ds.setUnderlineText(false);      //设置下划线
                    }

                    @Override
                    public void onClick(View widget) {
                        switch (type) {
                            case 1://话题跳转
                                break;
                            case 2://用户资料跳转
                                gotoUserInfo(id);
                                break;
                            case 3:// 药品跳转
                                break;
                            case 4:
                                break;
                            case 5://订单详情
                                gotoOrderInfo(object_type, id, param);
                                break;
                            case 6://体验服务
                                gotoMyOrder("体验咨询", "9");
                                break;
                            case 7://图文咨询
                                gotoMyOrder("图文咨询", "5");
                                break;
                            case 8://包月咨询
                                gotoMyOrder("包月咨询", "7");
                                break;
                            case 15://电话咨询
                                sendCall(object_id);
                                break;
                            case 16://视频咨询
                                gotoMyOrder("视频咨询", "8");
                                break;
                            case 9://医生集团图文
                            case 10://医生集团电话
                            case 11://医生集团视频
                                getServiceOrder(mOrderId);
                                break;
                            case 12://医生集团院长发起邀请加入
                                gotoStationInvited();
                                break;
                            case 13://医生集团院长接受申请
                                goManageApply(linkType);
                                break;
                            case 14://会诊邀请
                                goManageOrder(linkType);
                                break;
                            case 17://图文邀请
                                agreeInvited(17, linkType);
                                break;
                            case 18://图文邀请
                                agreeInvited(18, linkType);
                                break;
                        }
                    }
                });
    }

    /**
     * 订单详情
     * @param object_type
     * @param id
     * @param param
     */
    private void gotoOrderInfo(String object_type, String id, String param) {
        if ("10".equals(object_type)) {
            getDataFromServer(id, param, (Activity) mContext);
        } else if ("20".equals(object_type)) {
            Intent intent = new Intent(mContext, AtyOutPatientDetail.class);
            intent.putExtra("ORIDERID", id);
            mContext.startActivity(intent);
        }
    }

    /**
     * 拨打电话
     */
    public void sendCall(String mOrderId) {
        if (HStringUtil.isEmpty(mOrderId)) {
            ToastUtil.showShort("数据异常");
            return;
        }
        Map<String, String> map = new HashMap<>();
        map.put("op", "call");
        map.put("order_id", mOrderId);
        ApiService.OKHttpConInvited(map, new ApiCallbackWrapper<JSONObject>() {
            @Override
            public void onBefore(Request request) {
                super.onBefore(request);
//                SingleBtnFragmentDialog.showDefault(getSupportFragmentManager(), "拨打电话中，请稍后。。。", new SingleBtnFragmentDialog.OnClickSureBtnListener() {
//                    @Override
//                    public void onClickSureHander() {
//
//                    }
//                });
                SingleBtnFragmentDialog.showSinglebtn(mContext, "拨打电话中，请稍后。。。", "知道了", new SingleBtnFragmentDialog.OnClickSureBtnListener() {
                    @Override
                    public void onClickSureHander() {

                    }
                }).show();
            }

            @Override
            public void onResponse(JSONObject response) {
                super.onResponse(response);
                if (response != null) {
//                    ToastUtil.showShort(response.optString("message"));
//                    makeSendDate(response);
                }
            }
        }, this);
    }

    /**
     * 我的订单
     * @param title
     * @param serviceType
     */
    private void gotoMyOrder(String title, String serviceType) {
        Intent intent = new Intent(mContext, MyOrderActivity.class);
        intent.putExtra(DoctorServiceSettingsActivity.TITLE, title);
        intent.putExtra(Constant.Station.SERVICE_TYPE_ID, serviceType);
        mContext.startActivity(intent);
    }

    /**
     * 是否可看订单
     */
    @SuppressWarnings("deprecation")
    public static void getDataFromServer(final String conId, final String params, final Activity activity) {
        List<BasicNameValuePair> pairs = new ArrayList<>();
        pairs.add(new BasicNameValuePair("CONSULTATIONID", conId));
        pairs.add(new BasicNameValuePair("CUSTID", DoctorHelper.getId()));
        ApiService.OKHttpConsultInfo(0, pairs, new ApiCallback<String>() {

            @Override
            public void onError(okhttp3.Request request, Exception e) {
            }

            @Override
            public void onResponse(String response) {
                if (!TextUtils.isEmpty(response)) {
                    try {
                        JSONObject object = new JSONObject(response);
                        if (1 == object.optInt("code")) {
                            Intent intent = null;
                            if ("10".equals(params) || "15".equals(params)) {
                                intent = new Intent(activity, AtyOrdersDetails.class);
                            } else {
                                intent = new Intent(activity, AtyOrderDetails.class);
                            }
                            intent.putExtra("CONID", Integer.parseInt(conId));
                            activity.startActivity(intent);
                        } else if (2 == object.optInt("code")) {
                            ToastUtil.showShort(object.optString("message"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, activity);
    }

    /**
     * 医生集团接单
     */
    public void getServiceOrder(String orderId) {
        Map<String, String> map = new HashMap<>();
        map.put("op", "updateWorkSiteOrderStatus");
        map.put("doctor_id", DoctorHelper.getId());
        map.put("order_id", orderId);
        map.put("status", Constant.StationOrderStatus.QDSUCESS);
        ApiService.OKHttpStationCommonUrl(map, new ApiCallbackWrapper<JSONObject>((FragmentActivity) mContext) {
            @Override
            public void onResponse(JSONObject response) {
                super.onResponse(response);
                if (response != null) {
                    ToastUtil.showShort(response.optString("message"));
//                    makeSendDate(response);
                }
            }
        }, this);
    }

    /**
     * 工作站邀请
     */
    public void gotoStationInvited() {
        Intent intent = new Intent(mContext, StationInvitedActivity.class);
        mContext.startActivity(intent);
    }

    /**
     * 去支付setGoSee+order_type+site_id++doctor_id+price
     */
    public void goManageApply(String content) {
        String[] keys = content.split("&");
        if (content.contains("&") && keys.length > 1) {
            Intent intent = new Intent(mContext, InviteMemActivity.class);
            intent.putExtra(Constant.Station.STATION_ID, keys[1]);
            mContext.startActivity(intent);
        } else {
            ToastUtil.showShort("数据异常");
        }
    }

    /**
     * 去处理邀请
     */
    public void goManageOrder(String content) {
        String[] keys = content.split("&");
        if (content.contains("&") && keys.length > 1) {
            Intent intent = new Intent(mContext, AtyOrderDetails.class);
            intent.putExtra("CONID", Integer.valueOf(keys[1]));
            mContext.startActivity(intent);
        } else {
            ToastUtil.showShort("数据异常");
        }
    }

    /**
     * 同意图文邀请
     */
    public void agreeInvited(int code, String content) {
        String[] keys = content.split("&");
        if (content.contains("&") && keys.length > 1) {
            if (17 == code) {
                makeTuwen(Constant.AcceptType.YES, keys[2]);
            } else {
                makeTuwen(Constant.AcceptType.NO, keys[2]);
            }
        } else {
            ToastUtil.showShort("数据异常");
        }
    }

    /**
     * 是否接受图文邀请
     */
    public void makeTuwen(String status, String group_id) {
        Map<String, String> map = new HashMap<>();
        map.put("op", "updateInviteStatus");
        map.put("customer_id", DoctorHelper.getId());
        map.put("group_id", group_id);
        map.put("status", status);
        ApiService.OKHttpStationCommonUrl(map, new ApiCallbackWrapper<JSONObject>((FragmentActivity) mContext) {
            @Override
            public void onResponse(JSONObject response) {
                super.onResponse(response);
                if (response != null) {
                    ToastUtil.showShort(response.optString("message"));
                }
            }
        }, this);
    }

    /**
     * 用户信息
     * @param id
     */
    private void gotoUserInfo(String id) {
        Intent intent;
        String loginUserId = SmartFoxClient.getLoginUserId();
        if (loginUserId.equals(id)) {
            intent = MyInfoActivity.getCallingIntent(mContext, DoctorHelper.getId());
            mContext.startActivity(intent);
        } else {
            FragmentActivity activity = (FragmentActivity) this.mContext;
            FragmentManager fm = ((FragmentActivity) this.mContext).getSupportFragmentManager();
            ActivityHelper.startUserInfoActivity(activity, fm, id);
        }
    }
}
