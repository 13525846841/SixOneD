package com.yksj.consultation.adapter;

import android.view.View;
import android.widget.CompoundButton;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yksj.consultation.bean.FollowTemplateBean;
import com.yksj.consultation.sonDoc.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 随访模版适配器
 */
public class FollowTemplateAdapter extends BaseQuickAdapter<FollowTemplateBean, BaseViewHolder> {

    private List<CompoundButton> deleteViewsCache = new ArrayList<>();

    public FollowTemplateAdapter() {
        super(R.layout.item_futemp);
    }

    @Override
    protected void convert(BaseViewHolder helper, FollowTemplateBean item) {
        helper.setText(R.id.plan_name, item.name);
        CompoundButton deleteRb = helper.getView(R.id.delete_rg);
        deleteViewsCache.add(deleteRb);
    }

    /**
     * 设置是否可编辑
     * @param editable
     */
    public void setEditable(boolean editable) {
        for (int i = 0; i < deleteViewsCache.size(); i++) {
            CompoundButton deleteView = deleteViewsCache.get(i);
            deleteView.setVisibility(editable ? View.VISIBLE : View.GONE);
        }
    }

    public List<String> getDeleteTemplate(){
        List<String> temp = new ArrayList<>();
        for (int i = 0; i < deleteViewsCache.size(); i++) {
            CompoundButton rb = deleteViewsCache.get(i);
            if (rb.isChecked()) {
                temp.add(getData().get(i).id);
            }
        }
        return temp;
    }
}
