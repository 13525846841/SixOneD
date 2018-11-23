package com.yksj.consultation.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.library.base.imageLoader.ImageLoader;
import com.yksj.consultation.app.AppContext;
import com.yksj.consultation.sonDoc.R;
import com.yksj.healthtalk.bean.CaseBean;
import com.yksj.healthtalk.utils.HStringUtil;
import com.yksj.healthtalk.utils.TimeUtil;

/**
 * 病历讨论适配器
 * Created by zheng on 2015/9/17.
 */
public class DiscussCaseAdapter extends SimpleBaseAdapter<CaseBean> {

    public DiscussCaseAdapter(Context context) {
        super(context);
    }

    @Override
    public int getItemResource() {
        return R.layout.case_dis_list_item2;
    }

    @Override
    public View getItemView(int position, View convertView, ViewHolder holder) {
        CaseBean cb = datas.get(position);
        TextView name = (TextView) holder.getView(R.id.case_name);
        TextView time = (TextView) holder.getView(R.id.case_time);
        TextView keshi = (TextView) holder.getView(R.id.case_keshi);
        TextView talkNum = (TextView) holder.getView(R.id.case_talk_num);
        TextView seeNum = (TextView) holder.getView(R.id.case_see_num);
        ImageView pic = holder.getView(R.id.list_image);
        name.setText(cb.MEDICAL_NAME);

        if (HStringUtil.isEmpty(cb.DOCTOR_REAL_NAME)) {
            keshi.setVisibility(View.GONE);
        } else {
            keshi.setText("上传者:" + cb.DOCTOR_REAL_NAME);
        }

        String commitTime = "上传时间：" + TimeUtil.format(cb.SHARE_TIME);
        time.setText(commitTime);
        talkNum.setText(cb.NUMS + "");
        seeNum.setText(cb.CLICK_VALUE + "");

        String url= AppContext.getApiRepository().URL_QUERYHEADIMAGE + cb.CLIENT_ICON_BACKGROUND;
        ImageLoader.load(url).into(pic);
        return convertView;
    }
}
