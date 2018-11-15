package com.yksj.consultation.main;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.library.base.base.BaseFragment;
import com.yksj.consultation.bean.MainMsgPointBean;
import com.yksj.consultation.bean.MessageTipBean;
import com.yksj.consultation.bean.ResponseBean;
import com.yksj.consultation.event.EMainMessage;
import com.yksj.consultation.event.EMainRefresh;
import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.sonDoc.message.MessageNotifyActivity;
import com.yksj.healthtalk.net.http.ApiCallbackWrapper;
import com.yksj.healthtalk.net.http.ApiService;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

/**
 * Created by HEKL on 2015/9/18.
 * Used for 首页消息_提示
 */
public class MainMessagePointFragment extends BaseFragment {

    private static final int LOOP_TIME = 20;
    private TextView mMsgFrom;//消息来源
    private TextView mMsgContent;//消息内容
    private TextView mNoMsg;//没有消息
    private ImageView imageDot;//提示红点
    private Disposable mLoopSubscribe;

    @Override
    public int createLayoutRes() {
        return R.layout.fgt_mainmsg;
    }

    @Override
    public void initialize(View view) {
        super.initialize(view);
        initView(view);
        view.setOnClickListener(v -> startActivity(new Intent(getActivity(), MessageNotifyActivity.class)));
        requestMessage();
    }

    private void initView(View view) {
        mMsgFrom = (TextView) view.findViewById(R.id.msg_from);
        mMsgContent = (TextView) view.findViewById(R.id.msg_content);
        mNoMsg = (TextView) view.findViewById(R.id.no_msg);
        imageDot = (ImageView) view.findViewById(R.id.dot);
    }

    @Override
    public void onStart() {
        super.onStart();
        startLoopRequest();
    }

    @Override
    public void onDestroyView() {
        stopLoopRequest();
        super.onDestroyView();
    }

    /**
     * 开始循环请求信息
     */
    private void startLoopRequest() {
        mLoopSubscribe = Observable.interval(LOOP_TIME, TimeUnit.SECONDS)
                .filter(aLong -> isResumed())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aLong -> {
                    if (isAdded()) {
                        requestMessage();
                    } else {
                        stopLoopRequest();
                    }
                });
    }

    /**
     * 停止请求
     */
    private void stopLoopRequest() {
        if (mLoopSubscribe != null) {
            mLoopSubscribe.dispose();
            mLoopSubscribe = null;
        }
    }

    /**
     * 首页消息提示
     */
    private void requestMessage() {
        ApiService.OKHttpOrderTip(new ApiCallbackWrapper<ResponseBean<MainMsgPointBean>>() {

            @Override
            public void onResponse(ResponseBean<MainMsgPointBean> response) {
                super.onResponse(response);
                if (response.isSuccess()) {
                    mMsgFrom.setVisibility(View.VISIBLE);
                    mMsgContent.setVisibility(View.VISIBLE);
                    mMsgFrom.setText("");
                    mMsgContent.setText("");
                    mNoMsg.setVisibility(View.GONE);
                    MainMsgPointBean result = response.result;
                    if (!result.hasNewMessage()) {
                        mMsgFrom.setVisibility(View.GONE);
                        mMsgContent.setVisibility(View.GONE);
                        mNoMsg.setVisibility(View.VISIBLE);
                        mNoMsg.setText("您暂时没有未读消息");
                        imageDot.setImageDrawable(getResources().getDrawable(R.drawable.gray_dot));
                    } else {
                        mMsgFrom.setText(result.getMessageFrom());
                        mMsgContent.setText(result.getMessageContent());
                        int msgNum = result.getNums();
                        if (msgNum > 0) {
                            imageDot.setImageDrawable(getResources().getDrawable(R.drawable.red_dot));
                        } else {
                            imageDot.setImageDrawable(getResources().getDrawable(R.drawable.gray_dot));
                        }
                    }
                }
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(EMainMessage event) {
        MessageTipBean data = event.data;
        if (data != null) {
            mMsgFrom.setVisibility(View.VISIBLE);
            mMsgContent.setVisibility(View.VISIBLE);
            mNoMsg.setVisibility(View.GONE);
            mMsgFrom.setText("");
            mMsgContent.setText("");
            mMsgFrom.setText(data.getFromMsg());
            mMsgContent.setText(data.getMessageContent());
            imageDot.setImageDrawable(getResources().getDrawable(R.drawable.red_dot));
        }
    }

    @Subscribe
    public void onRefresh(EMainRefresh event) {
        if (isAdded()) {
            requestMessage();
        }
    }

    /**
     * 加载提示
     */
    private void showLoading(String str) {
        if (isAdded()) {
            mMsgFrom.setVisibility(View.GONE);
            mMsgContent.setVisibility(View.GONE);
            mNoMsg.setVisibility(View.VISIBLE);
            mNoMsg.setText(str);
            imageDot.setImageDrawable(getResources().getDrawable(R.drawable.gray_dot));
        }
    }
}
