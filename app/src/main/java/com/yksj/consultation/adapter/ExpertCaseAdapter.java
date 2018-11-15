package com.yksj.consultation.adapter;

import android.text.TextUtils;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yksj.consultation.sonDoc.R;
import com.yksj.healthtalk.bean.CaseBean;
import com.yksj.healthtalk.utils.TimeUtil;

public class ExpertCaseAdapter extends BaseQuickAdapter<CaseBean, BaseViewHolder> {

    public ExpertCaseAdapter() {
        super(R.layout.item_case);
    }

    @Override
    protected void convert(BaseViewHolder helper, CaseBean item) {
        helper.setText(R.id.case_name, TextUtils.isEmpty(item.MEDICAL_NAME) ? "暂无" : item.MEDICAL_NAME);
        helper.setText(R.id.case_keshi, TextUtils.isEmpty(item.OFFICE_NAME) ? "暂无" : item.OFFICE_NAME);
        helper.setText(R.id.case_time, TextUtils.isEmpty(item.RECORD_TIME) ? "" : TimeUtil.getTimeStr(item.RECORD_TIME));
    }
}
