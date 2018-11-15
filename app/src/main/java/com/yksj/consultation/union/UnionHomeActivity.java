package com.yksj.consultation.union;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.blankj.utilcode.util.ToastUtils;
import com.library.base.base.BaseTitleActivity;
import com.library.base.utils.EventManager;
import com.yksj.consultation.bean.ResponseBean;
import com.yksj.consultation.bean.UnionBean;
import com.yksj.consultation.event.EUnionRefresh;
import com.yksj.consultation.union.view.UnionHomeView;
import com.yksj.consultation.utils.DoctorHelper;
import com.yksj.healthtalk.net.http.ApiService;
import com.yksj.healthtalk.net.http.ApiCallbackWrapper;

import org.greenrobot.eventbus.Subscribe;

/**
 * 医生联盟首页
 */
public class UnionHomeActivity extends BaseTitleActivity {

    private static final String ID_EXTRA = "id_extra";

    private UnionHomeView mView;
    private String mUnionId;
    private UnionBean mUnionBean;

    public static Intent getCallingIntent(Context context, String unionId) {
        Intent intent = new Intent(context, UnionHomeActivity.class);
        intent.putExtra(ID_EXTRA, unionId);
        return intent;
    }

    @Override
    public View createLayout() {
        return mView = new UnionHomeView(this);
    }

    @Override
    public void initialize(Bundle bundle) {
        super.initialize(bundle);
        mUnionId = getIntent().getStringExtra(ID_EXTRA);
        mView.setJoinClickListener(this::onJoinClick);
        requestData(true);
    }

    /**
     * 加入或者退出医生联盟
     */
    private void onJoinClick(View view) {
        ApiService.OkHttpUnionJoinOrExit(
                mUnionBean.UNION_ID,
                DoctorHelper.getId(),
                mUnionBean.JOIN_FLAG,
                new ApiCallbackWrapper<ResponseBean>() {
                    @Override
                    public void onResponse(ResponseBean response) {
                        super.onResponse(response);
                        if (response.isSuccess()) {
                            requestData(false);
                        }
                        ToastUtils.showShort(response.message);
                    }
                });
    }

    /**
     * 关注工作站点击事件
     * @param view
     */
    private void onFollowClick(View view) {
        ApiService.OkHttpUnionFollow(mUnionId, DoctorHelper.getId(), new ApiCallbackWrapper<ResponseBean>() {
            @Override
            public void onResponse(ResponseBean response) {
                super.onResponse(response);
                if (response.isSuccess()) {
                    EventManager.post(new EUnionRefresh());
                    ToastUtils.showShort(response.message);
                }
            }
        });
    }

    /**
     * 获取联盟详情
     * @param showWait
     */
    private void requestData(boolean showWait) {
        ApiService.OkHttpUnionInfo(DoctorHelper.getId(), mUnionId, new ApiCallbackWrapper<ResponseBean<UnionBean>>(showWait) {
            @Override
            public void onResponse(ResponseBean<UnionBean> response) {
                super.onResponse(response);
                if (response.isSuccess()) {
                    mUnionBean = response.result;
                    setTitle(mUnionBean.UNION_NAME);
                    setRight(mUnionBean.FOLLOW_FLAG == 0 ? "关注" : "取消关注", UnionHomeActivity.this::onFollowClick);
                    mView.bindData(mUnionBean);
                }
            }
        });
    }

    /**
     * 刷新联盟数据
     * @param e
     */
    @Subscribe
    public void onEventRefresh(EUnionRefresh e) {
        requestData(false);
    }
}
