package com.yksj.consultation.agency.fragment;

import android.os.Bundle;
import android.view.View;

import com.library.base.base.BaseFragment;
import com.library.base.widget.ScrollableHelper;
import com.yksj.consultation.agency.constant.AgencyConst;
import com.yksj.consultation.agency.view.AgencyCommentView;

/**
 * 机构详情子页面评论
 */
public class AgencyCommentFragment extends BaseFragment implements ScrollableHelper.ScrollableContainer, AgencyCommentView.IPresenter {

    private AgencyCommentView mView;

    public static AgencyCommentFragment newInstance(String id) {

        Bundle args = new Bundle();
        args.putString(AgencyConst.ID_EXTRA, id);

        AgencyCommentFragment fragment = new AgencyCommentFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View createLayout() {
        return mView = new AgencyCommentView(getContext(), this);
    }

    @Override
    public void initialize(View view) {
        super.initialize(view);
    }

    @Override
    public View getScrollableView() {
        return mView.getScrollableView();
    }
}
