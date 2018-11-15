package com.yksj.consultation.news;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.blankj.utilcode.util.ActivityUtils;
import com.library.base.base.BaseTitleActivity;
import com.library.base.umeng.UmengShare;
import com.library.base.widget.DividerListItemDecoration;
import com.library.base.widget.Html5WebView;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.yksj.consultation.adapter.NewsCommentAdapter;
import com.yksj.consultation.bean.NewsPraiseBean;
import com.yksj.consultation.bean.ResponseBean;
import com.yksj.consultation.bean.ResponseNewsBean;
import com.yksj.consultation.constant.NewsConstant;
import com.yksj.consultation.dialog.DialogManager;
import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.utils.DoctorHelper;
import com.yksj.healthtalk.net.http.ApiCallbackWrapper;
import com.yksj.healthtalk.net.http.ApiService;
import com.yksj.healthtalk.net.http.AsyncHttpResponseHandler;
import com.yksj.healthtalk.net.http.RequestParams;
import com.yksj.healthtalk.net.socket.SmartFoxClient;
import com.yksj.healthtalk.utils.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author HEKL
 * 动态消息详情界面
 */
public class NewsInfoActivity extends BaseTitleActivity implements OnClickListener {

    @BindView(R.id.web_view)
    Html5WebView mWebView;

    @BindView(R.id.praise_active)
    TextView mPraiseView;

    @BindView(R.id.comment_recycler)
    RecyclerView mCommentRecycler;

    @BindView(R.id.comment_num)
    TextView mCommentNumView;

    private String infoId;
    private NewsCommentAdapter mAdapter;
    private ResponseNewsBean mNewsInfoBean;

    @Override
    public int createLayoutRes() {
        return R.layout.aty_dynmaicmessage_layout;
    }

    @Override
    public void initialize(Bundle bundle) {
        super.initialize(bundle);
        setTitle("信息详情");
//        setRight("分享", this::onShareClick);
        infoId = getIntent().getStringExtra(NewsConstant.INFO_ID);
        initView();
    }

    /**
     * 分享
     * @param view
     */
    private void onShareClick(View view) {
        UmengShare.from(this)
                  .share(SHARE_MEDIA.WEIXIN)
                  .setTitle(getString(R.string.string_share_title))
                  .setContent(getString(R.string.string_share_content))
                  .setThumb(R.drawable.ic_launcher)
                  .setUrl(getString(R.string.string_share_website))
                  .startShare();
    }

    /**
     * 初始化view
     */
    private void initView() {
        mCommentRecycler.setLayoutManager(new LinearLayoutManager(this));
        mCommentRecycler.addItemDecoration(new DividerListItemDecoration());
        mAdapter = new NewsCommentAdapter();
        mAdapter.bindToRecyclerView(mCommentRecycler);
        requestData();
    }

    /**
     * 添加评论
     * @param v
     */
    @OnClick(R.id.comment_active)
    public void onCommentClick(View v) {
        DialogManager.getInputDialog()
                     .setOnCommentClickListener((dialog, view, content) -> addComment(content))
                     .show(getSupportFragmentManager());
    }

    /**
     * 点赞
     * @param v
     */
    @OnClick(R.id.praise_active)
    public void onPraiseClick(View v) {
        actionPraise();
    }

    /**
     * 添加评论  没有这个接口   17年8月30日
     */
    private void addComment(String comment) {
        ApiService.OKHttpNewsAddComment(comment, DoctorHelper.getId(), infoId, new ApiCallbackWrapper<ResponseBean>(this) {
            @Override
            public void onResponse(ResponseBean response) {
                super.onResponse(response);
                if (response.code == 0) {
                    requestData();
                }
            }
        }, this);
    }

    /**
     * 是否已经销毁
     * @return
     */
    private boolean isExists() {
        return ActivityUtils.isActivityExistsInStack(NewsInfoActivity.class);
    }

    /**
     * 加载消息内容
     */
    private void requestData() {
        ApiService.OKHttpNewsComment(infoId, new ApiCallbackWrapper<ResponseNewsBean>(true) {
            @Override
            public void onResponse(ResponseNewsBean response) {
                super.onResponse(response);
                if (response != null) {
                    mNewsInfoBean = response;
                    String title = response.news.arts.get(0).INFO_NAME;
                    String content = response.news.arts.get(0).INFO_CONTENT;
                    mWebView.setupBody(title, "", "", "", content);
                    mWebView.postDelayed(() -> {
                        if (isExists() && mNewsInfoBean != null) {
                            mCommentRecycler.setVisibility(View.VISIBLE);
                            mAdapter.setNewData(mNewsInfoBean.news.comments);//评论列表
                            mPraiseView.setText(mNewsInfoBean.news.arts.get(0).PRAISE_COUNT + "");//点赞数量
                            mCommentNumView.setText(String.format("全部评论(%s)", mNewsInfoBean.news.comments.size()));
                        }
                    }, 500);
                }
            }
        }, this);
    }

    /**
     * 点赞操作
     * GroupConsultationList?TYPE=praiseConsuInfo&INFOID=&CUSTOMERID=
     */
    private void actionPraise() {
        ApiService.newsPraise(infoId, SmartFoxClient.getLoginUserId(), new ApiCallbackWrapper<ResponseBean<NewsPraiseBean>>() {
            @Override
            public void onResponse(ResponseBean<NewsPraiseBean> response) {
                super.onResponse(response);
                if (response.isSuccess()) {
                    mPraiseView.setSelected(true);
                    mNewsInfoBean.news.arts.get(0).PRAISE_COUNT ++;
                    mPraiseView.setText(String.valueOf(mNewsInfoBean.news.arts.get(0).PRAISE_COUNT ++));
                } else {

                }
            }
        });
    }

    /**
     * 收藏计数更新
     * 收藏
     * http://220.194.46.204:8080/DuoMeiHealth/
     * GroupConsultationList?TYPE=collectedConsuInfo&CUSTOMERID=&INFOID=
     * 取消收藏
     * GroupConsultationList?TYPE=cancelCollectedInfoBatch&CUSTOMERIDS=&INFOID=
     * @param isCollection
     */
    private void onCollectionCountUpdate(final boolean isCollection) {
        RequestParams params = new RequestParams();
        params.put("CUSTOMERID", SmartFoxClient.getLoginUserId());
        if (isCollection) {
            params.put("INFOID", infoId);
            params.put("TYPE", "collectedConsuInfo");
        } else {
            params.put("INFOIDS", infoId);
            params.put("TYPE", "cancelCollectedInfoBatch");
        }
        ApiService.doHttpGroupConsultationList(params, new AsyncHttpResponseHandler(this) {
            @Override
            public void onSuccess(int statusCode, String content) {
//				if ("1".equals(content)) {
//					onUpdateCollectionNumber(isCollection);
//				}
                try {
                    JSONObject object = new JSONObject(content);
                    if (object.has("message")) {
                        ToastUtil.showShort(object.optString("message"));
                    } else if (object.has("error_message")) {
                        ToastUtil.showShort(object.optString("error_message"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

}
