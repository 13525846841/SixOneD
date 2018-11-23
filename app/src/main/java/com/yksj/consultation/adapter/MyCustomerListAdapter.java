package com.yksj.consultation.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.library.base.imageLoader.ImageLoader;
import com.yksj.consultation.app.AppContext;
import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.sonDoc.consultation.SickInfoActivity;
import com.yksj.consultation.sonDoc.consultation.PConsultMainActivity;

import org.universalimageloader.core.DefaultConfigurationFactory;
import org.universalimageloader.core.DisplayImageOptions;

import java.util.Map;

/**
 * 我的患者adapter
 * Created by zheng on 15/9/23.
 */
public class MyCustomerListAdapter extends SimpleBaseAdapter<Map<String, String>> {

    private ImageLoader mInstance;
    private DisplayImageOptions mOptions;
    private Activity mActivity;
    private Context context;

    public MyCustomerListAdapter(Context context) {
        super(context);
        mInstance = ImageLoader.getInstance();
        this.context = context;
        mActivity = (Activity) context;
        mOptions = DefaultConfigurationFactory.createSeniorDoctorDisplayImageOptions(mActivity);
    }

    @Override
    public int getItemResource() {
        return R.layout.my_customer_list_item;
    }

    @Override
    public View getItemView(int position, View convertView, ViewHolder holder) {
        final Map map = datas.get(position);
        ImageView custHaed = ((ImageView) holder.getView(R.id.my_customer_haeder));
        TextView custName = ((TextView) holder.getView(R.id.my_cust_name));
        TextView consultnum = ((TextView) holder.getView(R.id.my_cust_num));
        Button selectBtn = ((Button) holder.getView(R.id.select_cust));
        LinearLayout layout = ((LinearLayout) holder.getView(R.id.ll_info));
//        mInstance.displayImage((String) map.get("CLIENT_ICON_BACKGROUND"), custHaed, mOptions);

        String pic = AppContext.getApiRepository().URL_QUERYHEADIMAGE + (String) map.get("CLIENT_ICON_BACKGROUND");
        ImageLoader
                .load(pic)
                .placeholder(R.drawable.default_head_patient)
                .into(custHaed);

        String name = "";
        if ("null".equals((String) map.get("REAL_NAME"))) {
            name = "暂无";
        } else {
            name = (String) map.get("REAL_NAME");
        }
        String doumei = (String) map.get("CUSTOMER_ACCOUNTS");
        String text = name.concat("(").concat(doumei).concat(")");
        custName.setText(text);
//        consultnum.setText((String) map.get("NUMS"));
//        consultnum.setText("女 "+"27 "+"北京 "+"急性盲肠炎");
        if ("0".equals((String) map.get("ISEX"))) {//医生选择他
            selectBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mActivity, PConsultMainActivity.class);
                    intent.putExtra("PID", (String) map.get("CUSTOMER_ID"));
                    mActivity.startActivity(intent);
                }
            });
        } else {
            selectBtn.setVisibility(View.GONE);
        }
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, SickInfoActivity.class);
                intent.putExtra("PID", (String) map.get("CUSTOMER_ID"));
                intent.putExtra(SickInfoActivity.OPEN_PLAN, 1);
                if ("0".equals((String) map.get("ISEX"))) {
                } else {
                    intent.putExtra("MAIN", "main");
                }
                mActivity.startActivity(intent);
            }
        });
        return convertView;
    }
}