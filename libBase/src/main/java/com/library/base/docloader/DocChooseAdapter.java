package com.library.base.docloader;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.library.base.R;

/**
 * 文档选择界面适配器
 */
public class DocChooseAdapter extends BaseQuickAdapter<DocEntity, BaseViewHolder> {

    public DocChooseAdapter() {
        super(R.layout.item_doc_choose);
    }

    @Override
    protected void convert(BaseViewHolder helper, DocEntity item) {
        helper.setImageResource(R.id.iv_type, item.typeRes);
        helper.setText(R.id.tv_title, item.title);
    }
}
