package com.yksj.consultation.main;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;

import com.blankj.utilcode.util.ScreenUtils;
import com.library.base.base.BaseFragment;
import com.library.base.imageLoader.GlideImageEngine;
import com.yksj.consultation.constant.Constant;
import com.yksj.consultation.bean.MainBannerBean;
import com.yksj.consultation.bean.ResponseBean;
import com.yksj.consultation.comm.SingleBtnFragmentDialog;
import com.yksj.consultation.event.EMainRefresh;
import com.yksj.consultation.im.ChatActivity;
import com.yksj.consultation.setting.SettingWebUIActivity;
import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.sonDoc.casehistory.CaseDiscussDetailsActivity;
import com.yksj.consultation.sonDoc.consultation.DAtyConslutDynMesContent;
import com.yksj.consultation.sonDoc.salon.SalonSelectPaymentOptionActivity.OnBuyTicketHandlerListener;
import com.yksj.healthtalk.entity.GroupInfoEntity;
import com.yksj.healthtalk.entity.ShopListItemEntity;
import com.yksj.healthtalk.net.http.ApiCallbackWrapper;
import com.yksj.healthtalk.net.http.ApiService;
import com.yksj.healthtalk.net.http.AsyncHttpResponseHandler;
import com.yksj.healthtalk.net.http.RequestParams;
import com.yksj.healthtalk.net.socket.SmartFoxClient;
import com.yksj.healthtalk.utils.SalonHttpUtil;
import com.yksj.healthtalk.utils.ToastUtil;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;

import org.greenrobot.eventbus.Subscribe;
import org.json.JSONObject;

import java.util.List;

import butterknife.BindView;

/**
 * 主页顶部view
 *
 * @author jack_tang
 */
public class MainBannerFragment extends BaseFragment implements OnBuyTicketHandlerListener {

    private int CHATTINGCODE = 2;

    @BindView(R.id.banner_view)
    Banner mBannerView;

    private List<MainBannerBean.BannerInfo> mBanners;

    @Override
    public int createLayoutRes() {
        return R.layout.main_top_view_layout;
    }

    @Override
    public void initialize(View view) {
        super.initialize(view);
        initializeView(view);
        requestBanner();
    }

    @Override
    public void onResume() {
        super.onResume();
        mBannerView.startAutoPlay();
    }

    @Override
    public void onStop() {
        super.onStop();
        mBannerView.stopAutoPlay();
    }

    private void initializeView(View view) {
        mBannerView.setBannerStyle(BannerConfig.NUM_INDICATOR);
        mBannerView.setImageLoader(new GlideImageEngine());
        mBannerView.setOnBannerListener(position -> onBannerClick(position));
    }

    /**
     * 1001-小壹，1002-指定条件医生诊所列表， 1003-指定医生诊所首页1004-指定条件话题列表
     * 1005-指定话题对话页面1006-指定条件药品列表 1007-指定药品购买页面1008-医院中心列表
     * 1009-指定医院中心首页1010-医药商家列表1011-指定医药商家首页2001-内网网页3001-外网网页
     *
     * @param position
     */
    private void onBannerClick(int position) {
        MainBannerBean.BannerInfo bannerInfo = mBanners.get(position);
        int type = mBanners.get(position).BANNER_TYPE;
        Intent intent;
        switch (type) {
            case 0:
//                array[0] = bannerInfo.ANDROID_BANNER_2X;
//                intent = new Intent(getActivity(), ImageOpenActivity.class);
//                intent.putExtra(ImageGalleryActivity.URLS_KEY, array);
//                intent.putExtra(ImageGalleryActivity.TYPE_KEY, 0);// 0,1单个,多个
//                intent.putExtra("position", position);
//                intent.putExtra("type", 1);// 标题是否显示，1不显示
//                getActivity().startActivity(intent);
                break;
            case 1001:// 小壹
//                intent = new Intent(getActivity(), DoctorChatActivity.class);
//                startActivity(intent);
                break;
            case 1002://病历详情// 指定条件医生诊所列表，
                intent = new Intent(mActivity, CaseDiscussDetailsActivity.class);
                intent.putExtra("url", bannerInfo.PARAMETERS);
                startActivity(intent);
                break;
            case 1012:// 指定条件医生诊所列表，
//                intent = new Intent(getActivity(), SearchDoctorResultActivity.class);
//                intent.putExtra("url", jsonObject.optString("PARAMETERS"));
//                intent.putExtra("title", jsonObject.optString("PAGE_TITLE"));
//                startActivity(intent);
                break;
            case 1003://学术咨询// 指定医生诊所首页
                intent = new Intent(mActivity, DAtyConslutDynMesContent.class);
                intent.putExtra("url", bannerInfo.PARAMETERS);
                startActivity(intent);
                break;
            case 1004:// 指定条件话题列表
//                String url = jsonObject.optString("PARAMETERS");
//                intent = new Intent(getActivity(), TopicSearchResultActivity.class);
//                intent.putExtra("url", url);
//                intent.putExtra("title", jsonObject.optString("PAGE_TITLE"));
//                startActivity(intent);
                break;
            case 1005:// 指定话题对话页面
                String groupId = bannerInfo.PARAMETERS;
                GroupInfoEntity grouEntity = new GroupInfoEntity();
                grouEntity.setId(groupId);
                grouEntity.setName(bannerInfo.PAGE_TITLE);
                SalonHttpUtil.onItemClick(getActivity(), this, getChildFragmentManager(), grouEntity, true);
                break;
            case 1006:// 指定条件药品列表
//                intent = new Intent(getActivity(), ServiceShopFromBanner.class);
//                intent.putExtra("url", jsonObject.optString("PARAMETERS"));
//                intent.putExtra("title", jsonObject.optString("PAGE_TITLE"));
//                startActivity(intent);
                break;
            case 1007:// 指定药品购买页面 //goodsId=1002149,merchantId=1
                String[] str = bannerInfo.PARAMETERS.split(",");
                String goodsId = str[0].split("=")[1];
                String merchantId = str[1].split("=")[1];
                responsGoodServlet(goodsId, merchantId);
                break;
            case 1008:// 医院中心列表
//                intent = new Intent(getActivity(), ServerCenterSeachListActivity.class);
//                intent.putExtra("url", jsonObject.optString("PARAMETERS"));
//                intent.putExtra("title", jsonObject.optString("PAGE_TITLE"));
//                startActivity(intent);
                break;
            case 1009:// 指定医院中心首页
            case 1011:// 指定医药商家首页
                ApiService.doHttpServerCneterBg(String.valueOf(ScreenUtils.getScreenWidth()),
                        String.valueOf(ScreenUtils.getScreenHeight()), SmartFoxClient.getLoginUserId(),
                        bannerInfo.PARAMETERS, new AsyncHttpResponseHandler(mActivity) {
                            @Override
                            public void onSuccess(String content) {
                                super.onSuccess(content);
                                try {
                                    JSONObject response = new JSONObject(content);
                                    String str = response.getString("VALID_FLAG");
                                    if ("2".equals(str)) {// 不可以进入
                                        SingleBtnFragmentDialog.showDefault(getChildFragmentManager(), "即将开通,敬请期待!");
                                    }
                                } catch (Exception e) {
                                }

                            }
                        });
                break;
            case 1010:// 医药商家列表
//                intent = new Intent(getActivity(), ServerCenterSeachListActivity.class);
//                intent.putExtra("url", jsonObject.optString("PARAMETERS"));
//                intent.putExtra("title", jsonObject.optString("PAGE_TITLE"));
//                startActivity(intent);
                break;
            case 2001:// 内网网页
                intent = new Intent(getActivity(), SettingWebUIActivity.class);
                intent.putExtra("url", bannerInfo.PARAMETERS);
                startActivity(intent);
                break;
            case 3001:// 外网网页
                Uri uri = Uri.parse(bannerInfo.PARAMETERS);
                intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                break;
        }
    }

    /**
     * 请求banner数据
     */
    private void requestBanner() {
        ApiService.OkHttpMainBanner(new ApiCallbackWrapper<ResponseBean<MainBannerBean>>() {
            @Override
            public void onResponse(ResponseBean<MainBannerBean> response) {
                super.onResponse(response);
                Log.e("qwewqe", "onResponse: "+response.toString() );
                if (response.isSuccess()) {
                    MainBannerBean result = response.result;
                    mBanners = result.info;
                    mBannerView.setImages(result.getImages());
                    mBannerView.start();
                }
            }
        });
    }

    /**
     * 刷新banner数据
     * @param event
     */
    @Subscribe
    public void onRefresh(EMainRefresh event) {
        if (isAdded()) {
            requestBanner();
        }
    }

    private void responsGoodServlet(String goodsId, final String merchantId) {
        RequestParams params = new RequestParams();
        params.put("GOODS_ID", goodsId);
        params.put("MERCHANT_ID", merchantId);
        params.put("Type", "queryGoodsMessageById");
        ApiService.doHttpFINDCENTERCLASSANDGOODSERVLET33(params, new AsyncHttpResponseHandler(mActivity) {
            @Override
            public void onSuccess(String content) {
                super.onSuccess(content);
                ShopListItemEntity Entity = ShopListItemEntity.parseEntity(content);
                if (Entity != null) {
//                    Intent inetnt = new Intent(mActivity, ServerCenterDescription.class);
//                    // inetnt.putExtra("id", value);
//                    inetnt.putExtra("MERCHANT_ID", merchantId);
//                    inetnt.putExtra("entity", Entity);
//                    startActivity(inetnt);

                }
                /*
                 * senderId = getIntent().getStringExtra("id"); MERCHANT_ID =
                 * getIntent().getStringExtra("MERCHANT_ID");
                 * (ShopListItemEntity)
                 * getIntent().getSerializableExtra("entity")
                 */
            }
        });

    }

    @Override
    public void onTicketHandler(String state, GroupInfoEntity entity) {
        if ("0".equals(state)) {
        } else if ("-1".equals(state)) {
            ToastUtil.showBasicErrorShortToast(getActivity());
        } else {
            Intent intent1 = new Intent();
            intent1.putExtra(Constant.Chat.KEY_PARAME, entity);
            intent1.setClass(getActivity(), ChatActivity.class);
            startActivityForResult(intent1, CHATTINGCODE);
        }
    }
}
