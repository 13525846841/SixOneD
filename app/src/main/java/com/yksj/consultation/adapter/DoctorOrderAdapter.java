package com.yksj.consultation.adapter;

import android.app.Activity;
import android.content.Context;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yksj.consultation.bean.OutpatientOrderBean;
import com.yksj.consultation.sonDoc.R;
import com.yksj.healthtalk.utils.TimeUtil;

import org.universalimageloader.core.DefaultConfigurationFactory;
import org.universalimageloader.core.DisplayImageOptions;
import org.universalimageloader.core.ImageLoader;

/**
 * 医生订单 adapter
 *
 * @author jack_tang
 */
public class DoctorOrderAdapter extends BaseQuickAdapter<OutpatientOrderBean, BaseViewHolder> {

    private ImageLoader instance;
    private Context context;
    private Activity maActivity;
    private int color;
    private DisplayImageOptions mOptions;

    public DoctorOrderAdapter(Context context) {
        super(R.layout.doctor_order_item_layout);
        this.context = context;
        maActivity = (Activity) context;
        color = context.getResources().getColor(R.color.color_text_gray);
        instance = ImageLoader.getInstance();
        mOptions = DefaultConfigurationFactory.createHeadDisplayImageOptions(maActivity);
    }

    @Override protected void convert(BaseViewHolder holder, OutpatientOrderBean item) {
        TextView name = (TextView) holder.getView(R.id.name);
        SpannableStringBuilder ss = new SpannableStringBuilder();
        //昵称
        ss.clear();
        if (!TextUtils.isEmpty(item.REAL_NAME)){
            ss.append("患者姓名:   " + item.REAL_NAME);
            ss.setSpan(new ForegroundColorSpan(color), 0, 5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            name.setText(ss);
        }
        //时间
        TextView time = (TextView) holder.getView(R.id.time);

        ss.clear();
        String serviceTime = TimeUtil.formatTime(item.SERVICE_START);
        ss.append("服务时间:   " + serviceTime);
        ss.setSpan(new ForegroundColorSpan(color), 0, 5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        time.setText(ss);

        Button status = (Button) holder.getView(R.id.status);
        status.setText(item.serviceStatusInfo);


        //类型
        TextView type = (TextView) holder.getView(R.id.type);
        ss.clear();
        ss.append("服务地址:   " + item.SERVICE_PLACE);
        ss.setSpan(new ForegroundColorSpan(color), 0, 5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        type.setText(ss);

        ImageView chatHead = (ImageView) holder.getView(R.id.chat_head);
//        chatHead.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                PersonInfoUtil.choiceActivity(object.optString("customerId"), context, object.optString("roleId"));
//            }
//        });
        instance.displayImage(item.CLIENT_ICON_BACKGROUND, chatHead, mOptions);
//        getInstance.displayImage(object.optString("customerSex"), object.optString("clientIconBackground"), chatHead);
//        convertView.findViewById(R.id.ll_entry).setOnClickListener(new OnClickListener() {

//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(maActivity, AtyOutPatientDetail.class);
//                intent.putExtra("ORIDERID", item.optString("ORDER_ID"));
//                intent.putExtra("CUSTOMER_ID", object.optString("CUSTOMER_ID"));
//                intent.putExtra("DOCTORID", LoginBusiness.getInstance().getLoginEntity().getId());
//                intent.putExtra("data", object.toString());
//                maActivity.startActivity(intent);
//				String serviceStatusCode = object.optString("serviceStatusCode");
//				Intent intent;
//				if(serviceStatusCode.equals("175")||serviceStatusCode.equals("180")){
//					intent=new Intent(context,DoctorServiceStatusContent.class);
//					intent.putExtra("ORIDERID",object.optString("orderId"));
//					intent.putExtra("CUSTOMER_ID",object.optString("customerId"));
//					intent.putExtra("DOCTORID", SmartFoxClient.getLoginUserId());
//					intent.putExtra("tag", 1000);
//					maActivity.startActivityForResult(intent,5000);
//				}else if("1".equals(object.optString("serviceTypeId"))){//普通
//					intent=new Intent(context,DoctorServiceStatusContent.class);
//					intent.putExtra("entity", object.toString());
//					intent.putExtra("tag", 2000);
//					maActivity.startActivityForResult(intent,5000);
//
//				}else if("2".equals(object.optString("serviceTypeId"))||"3".equals(object.optString("serviceTypeId"))||"50".equals(serviceStatusCode)){//预约
//					intent=new Intent(context,DoctorServiceStatusContent.class);
//					intent.putExtra("entity", object.toString());
//					intent.putExtra("tag", 3000);
//					maActivity.startActivityForResult(intent,5000);
//				}
//            }
//        });
    }
}
