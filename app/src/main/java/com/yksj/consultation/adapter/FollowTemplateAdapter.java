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

    private List<CompoundButton> selectViewsCache;

    public FollowTemplateAdapter() {
        super(R.layout.item_futemp);
    }

    @Override
    protected void convert(BaseViewHolder helper, FollowTemplateBean item) {
        if (helper.getAdapterPosition() == 1) {
            selectViewsCache = new ArrayList<>();//清空缓存
        }
        helper.setText(R.id.plan_name, item.name);
        CompoundButton selectView = helper
                .setChecked(R.id.delete_rg, false)//默认设置未选中
                .getView(R.id.delete_rg);
        selectViewsCache.add(selectView);
    }

    /**
     * 根据id删除
     * @param ids
     */
    public void removeById(List<String> ids) {
        for (int j = 0; j < ids.size(); j++) {
            String deleteId = ids.get(j);
            for (int i = 0; i < getData().size(); i++) {
                FollowTemplateBean templateBean = getItem(i);
                if (templateBean.id.equals(deleteId)) {
                    remove(i);
                }
            }
        }
    }

    /**
     * 设置是否可编辑
     * @param editable
     */
    public void setEditable(boolean editable) {
        if (null != selectViewsCache && !selectViewsCache.isEmpty()) {
            for (int i = 0; i < selectViewsCache.size(); i++) {
                CompoundButton deleteView = selectViewsCache.get(i);
                deleteView.setVisibility(editable ? View.VISIBLE : View.GONE);
            }
        }
    }

    /**
     * 获取选中的模版id
     * @return
     */
    public List<String> getSelectTemplate() {
        List<String> temp = new ArrayList<>();
        for (int i = 0; i < selectViewsCache.size(); i++) {
            CompoundButton rb = selectViewsCache.get(i);
            if (rb.isChecked()) {
                temp.add(getData().get(i).id);
            }
        }
        return temp;
    }
}
