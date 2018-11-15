package com.yksj.consultation.sonDoc.consultation;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RadioButton;
import android.widget.TextView;

import com.library.base.base.BaseTitleActivity;
import com.yksj.consultation.adapter.AdtDynamicComment;
import com.yksj.consultation.app.AppContext;
import com.yksj.consultation.comm.ImageGalleryActivity;
import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.utils.DoctorHelper;
import com.yksj.healthtalk.entity.NewEntity;
import com.yksj.healthtalk.net.http.ApiService;
import com.yksj.healthtalk.net.http.AsyncHttpResponseHandler;
import com.yksj.healthtalk.net.http.MyApiCallback;
import com.yksj.healthtalk.net.http.RequestParams;
import com.yksj.healthtalk.net.socket.SmartFoxClient;
import com.yksj.healthtalk.utils.HStringUtil;
import com.yksj.healthtalk.utils.ScreenUtils;
import com.yksj.healthtalk.utils.SystemUtils;
import com.yksj.healthtalk.utils.ToastUtil;

import org.apache.http.message.BasicNameValuePair;
import org.handmark.pulltorefresh.library.PullToRefreshBase;
import org.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import org.handmark.pulltorefresh.library.PullToRefreshListView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.universalimageloader.core.DefaultConfigurationFactory;
import org.universalimageloader.core.DisplayImageOptions;
import org.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Request;

;
//import com.yksj.healthtalk.function.share.OneClickShare;

/**
 * @author HEKL
 *         动态消息详情界面
 */
public class DAtyConslutDynMesContent extends BaseTitleActivity implements OnClickListener,
        OnRefreshListener<ListView>, OnItemClickListener, OnCheckedChangeListener {
    private View headView;
    private TextView mNewTitle;
    private TextView mNewTimes;// 动态消息时间
    private TextView mNewContent;
    private ImageView mImageView;
    private LinearLayout mImageLayout;
    private RadioButton mGoodAction;// 好评
    private ImageLoader mImageLoader;
    private PullToRefreshListView mRefreshListView;
    private AdtDynamicComment mAdapter;
    private String consultationId = "1", infoId = "256";//会诊id,动态消息id
    private int praiseFlag = 0, praiseCount = 0;//是否点过赞,点赞数量
    private JSONObject contentObject;//内容JSONObject
    private int pageSize = 1;//第几页
    private int commCount = 1;//评论次数
    private ArrayList<NewEntity> commDatas;//评论
    private boolean isReplay = false;//   true为回复某人   false 为评论
    private String otherCustomerId = "";
    private CheckBox mCollection;//是否收藏
    /**
     * 弹出评论
     */
    PopupWindow mPopBottom;
    private EditText mCommonContent;// 评论内容
    private View bottomLayout;
    private ListView mListView;
    private TextView mConnentNum;//评论次数
    private String url;

    private List<String> urlList = new ArrayList<String>();// 所有图片路径,便于点击
    String REPLY_ID = "123";
    Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            SystemUtils.showSoftMode(mCommonContent);
        }

        ;
    };

    @Override
    public int createLayoutRes() {
        return R.layout.aty_dynmaicmessage_content_layout;
    }

    @Override
    public void initialize(Bundle bundle) {
        super.initialize(bundle);
        setTitle("学术资讯");
        Intent intent = getIntent();
        if (intent.hasExtra("conId")) {
            consultationId = intent.getStringExtra("conId");
        }

        if (intent.hasExtra("infoId")) {
            infoId = intent.getStringExtra("infoId");
        } else if (intent.hasExtra("url")) {
            infoId = intent.getStringExtra("url");
        }
        initView();
    }

    private void initView() {
//		bottomLayout = findViewById(R.id.new_bottom_layout);
//		mRefreshListView = (PullToRefreshListView) findViewById(R.id.listview);
//		mListView = mRefreshListView.getRefreshableView();
        mAdapter = new AdtDynamicComment(DAtyConslutDynMesContent.this);
        mImageLoader = ImageLoader.getInstance();
        onCreateHeadView();
        initData();
//		onDoQueryComment();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_back:
                onBackPressed();
                break;
//		case R.id.comment_to_new:// 评论
//			isReplay=false;
//			showCommonentPop(v);
//			break;
//		case R.id.share:// 分享
////			sharePopOut();
//			break;
//		case R.id.release:// 评论
//			SystemUtils.hideSoftBord(this, mCommonContent);
//			if (HStringUtil.isEmpty(mCommonContent.getText().toString()))
//				return;
//			mPopBottom.dismiss();
//			 onComment();
//			break;
//		case R.id.cancle:// 退出pop
//			SystemUtils.hideSoftBord(this, mCommonContent);
//			if (mPopBottom != null && mPopBottom.isShowing()) {
//				mPopBottom.dismiss();
//			}
//			break;
            default:
                if (v instanceof ImageView) { // 图片点击
                    Intent intent = new Intent(this, ImageGalleryActivity.class);
                    intent.putExtra(ImageGalleryActivity.URLS_KEY, urlList.toArray(new String[urlList.size()]));
                    intent.putExtra(ImageGalleryActivity.TYPE_KEY, 1);// 0,1单个,多个
                    intent.putExtra("type", 1);// 0,1单个,多个
                    startActivityForResult(intent, 100);
                }
                break;
        }
    }

//    /**
//     * 弹出分享框
//     * DynamicMessagePageServlet?CONSULTATION_CENTER_ID=&INFOID=&CUSTOMERID=
//     */
//    private void sharePopOut() {
//        ShareSDK.initSDK(this);
//        OneClickShare ocs = new OneClickShare(this, bottomLayout);
//        ocs.disableSSOWhenAuthorize();
//        ocs.setNotification(R.drawable.launcher_logo, getString(R.string.app_name));
//        String title = mNewTitle.getText().toString().trim();
//        String txt = mNewContent.getText().toString().trim();
//        txt = title + "\r\n" + txt;
//        if (txt.length() > 65)
//            txt = txt.substring(0, 65);
//        txt = "我在六一健康看到一条值得分享的内容:" + txt + Configs.SHARE_WEB +
//                "/DuoMeiHealth/DynamicMessagePageServlet?CONSULTATION_CENTER_ID=" + consultationId + "&INFOID="
//                + infoId + "&CUSTOMERID=" + SmartFoxClient.getLoginUserId();
//        ocs.setText(txt);
//        ocs.setTitle(title);
//        ocs.setUrl(Configs.SHARE_WEB +
//                "/DuoMeiHealth/DynamicMessagePageServlet?CONSULTATION_CENTER_ID=" + consultationId + "&INFOID="
//                + infoId + "&CUSTOMERID=" + SmartFoxClient.getLoginUserId());
//        String imageFilePath = null;
////		  如果有图片的话 带图片一起分享
//        if (StorageUtils.isSDMounted() && (!urlList.isEmpty())) {
//            String dir = StorageUtils.getImagePath();
//            String imagePath = urlList.get(0);
//            File file = mImageLoader.getOnDiscFileName(new File(dir), imagePath);
//            if (file.exists()) {
//                imageFilePath = file.getAbsolutePath();
//                ocs.setImagePath(imageFilePath);
//            }
//        }
//        ocs.show();
//    }

    /**
     * 创建头部view
     *
     * @return
     */
    private void onCreateHeadView() {
        //headView = LayoutInflater.from(this).inflate(R.layout.dynamic_content_header_layout, null);
//		mNewTitle = (TextView) headView.findViewById(R.id.headerTxt);
//		mNewTimes = (TextView) headView.findViewById(R.id.new_time);
//		mNewContent = (TextView) headView.findViewById(R.id.contentTxt);
//		mImageLayout = (LinearLayout) headView.findViewById(R.id.news_count_images);
//		mImageView = (ImageView) headView.findViewById(R.id.image);
//		mGoodAction = (RadioButton) headView.findViewById(R.id.good_action);
//		mConnentNum = (TextView) headView.findViewById(R.id.totalCommentTxt);

        mNewTitle = (TextView) findViewById(R.id.headerTxt);
        mNewTimes = (TextView) findViewById(R.id.new_time);
//		mNewContent = (TextView) headView.findViewById(R.id.contentTxt);
//		mWebView= (WebView) headView.findViewById(R.id.wv_mp4);
//		mImageView = (ImageView) headView.findViewById(R.id.image);
        mImageLayout = (LinearLayout) findViewById(R.id.news_count_images);
        mGoodAction = (RadioButton) findViewById(R.id.good_action);
//		mConnentNum = (TextView) findViewById(R.id.totalCommentTxt);
    }

    /**
     * 加载消息内容
     * 192.168.16.44:8899/DuoMeiHealth/ConsultationInfoSet?TYPE=findConsuInfo&CONSULTATION_CENTER_ID=&INFOID=&CUSTOMERID=
     */
    private void initData() {
        List<BasicNameValuePair> pairs = new ArrayList<>();
        pairs.add(new BasicNameValuePair("CONSULTATION_CENTER_ID", AppContext.APP_CONSULTATION_CENTERID));
        pairs.add(new BasicNameValuePair("CUSTOMERID", DoctorHelper.getId()));
        pairs.add(new BasicNameValuePair("INFOID", infoId));
        pairs.add(new BasicNameValuePair("TYPE", "findConsuInfo"));
        ApiService.doGetConsultationInfoSet(pairs, new MyApiCallback<String>(this) {
            @Override
            public void onResponse(String response) {
                super.onResponse(response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if ("1".equals(jsonObject.optString("code"))) {
                        if (jsonObject.has("result")) {
                            JSONArray array = jsonObject.getJSONArray("result");
                            contentObject = array.getJSONObject(0);
                            onParseData();//适配数据
                        }
                    } else {
                        ToastUtil.showShort(DAtyConslutDynMesContent.this, jsonObject.optString("message"));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Request request, Exception e) {
                super.onError(request, e);
            }
        }, this);

    }

    /**
     * 解析数据
     *
     * @param
     */
    private void onParseData() {

        int picCount = 0;
        DisplayImageOptions mDisplayImageOptions = DefaultConfigurationFactory
                .createGalleryDisplayImageOptions(this);
        DisplayImageOptions mDisplayVideoOptions = DefaultConfigurationFactory
                .createVideoDisplayImageOptions(this);
        mNewTitle.setText(contentObject.optString("INFO_NAME"));//标题
        mNewTimes.setText(contentObject.optString("STATUS_TIME"));//时间
//		 mConnentNum.setText("" + contentObject.optInt("COMMENT_COUNT"));
//		 praiseCount = contentObject.optInt("PRAISE_COUNT");
//      headView.findViewById(R.id.ll_prase).setVisibility(View.GONE);
        mGoodAction.setText("" + praiseCount);
        praiseFlag = contentObject.optInt("CON");
        if (praiseFlag > 0) {//点过赞
            mGoodAction.setChecked(true);
            mGoodAction.setClickable(false);
            mGoodAction.setEnabled(false);
        } else {
            mGoodAction.setChecked(false);
            mGoodAction.setOnCheckedChangeListener(new OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    praiseFlag = 1;
                    actionPraise();
                }
            });
        }

        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        mImageLayout.setDividerDrawable(getResources().getDrawable(R.drawable.divider_horizontal_line));
        //数组格式,为了实现图文混排,但是现在只考虑一张图片一段文字,只不过格式不变
        try {
            JSONArray jArray = contentObject.getJSONArray("CONTENTLIST");
            for (int i = 0; i < jArray.length(); i++) {
                final JSONObject object = jArray.getJSONObject(i);
                if (object.optInt("CONTENT_TYPE") == 10) {//文字
                    TextView mNewContent = new TextView(this);
                    mNewContent.setTextColor(getResources().getColor(R.color.color_text1));
                    mNewContent.setTextSize(16);
                    mNewContent.setPadding(0, 20, 0, 20);
                    if (!HStringUtil.isEmpty(object.optString("INFO_CONTENT"))) {
                        mNewContent.setText(object.optString("INFO_CONTENT"));
                        mImageLayout.addView(mNewContent);
                    }
//					mNewContent.setText(object.optString("INFO_CONTENT"));
                } else if (object.optInt("CONTENT_TYPE") == 20) {//图片

                    final ImageView mImageView = new ImageView(this);
                    mImageView.setAdjustViewBounds(true);
                    mImageView.setLayoutParams(params);
                    mImageView.setPadding(0, 12, 0, 12);
                    mImageView.setScaleType(ImageView.ScaleType.FIT_XY);
                    mImageView.setMaxWidth(ScreenUtils.getScreenWidth(this));
                    mImageView.setMaxHeight(ScreenUtils.getScreenWidth(this) * 5);
                    mImageView.setTag(picCount);
                    picCount++;
                    urlList.add(object.optString("BIG_PICTURE"));
//					 mImageView.setVisibility(View.VISIBLE);
//					 Picasso.with(this).load(object.optString("BIG_PICTURE")).into(mImageView);
                    mImageLoader.displayImage(object.optString("BIG_PICTURE"), mImageView, mDisplayImageOptions);
                    mImageView.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(DAtyConslutDynMesContent.this, ImageGalleryActivity.class);
                            intent.putExtra(ImageGalleryActivity.URLS_KEY, urlList.toArray(new String[urlList.size()]));
                            intent.putExtra(ImageGalleryActivity.TYPE_KEY, 1);// 0,1单个,多个
                            intent.putExtra(ImageGalleryActivity.POSITION, (Integer) mImageView.getTag());// 0,1单个,多个
//                    intent.putExtra("type", 1);// 0,1单个,多个
                            startActivityForResult(intent, 100);
                        }
                    });
                    mImageLayout.addView(mImageView);

                } else if (object.optInt("CONTENT_TYPE") == 30) {
                    final View view = LayoutInflater.from(this).inflate(R.layout.item_video, null);
                    ImageView mImageView = (ImageView) view.findViewById(R.id.image);
                    mImageView.setPadding(0, 12, 0, 12);
                    mImageLoader.displayImage(object.optString("SMALL_PICTURE"), mImageView, mDisplayVideoOptions);
                    mImageView.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent i = new Intent(DAtyConslutDynMesContent.this, InternetVideoDemo.class);
                            i.putExtra("url", object.optString("BIG_PICTURE"));
                            startActivity(i);
                        }
                    });
                    mImageLayout.addView(view);
//					urlList.add(object.optString("BIG_PICTURE"));

                }
            }
//			for(int i=0;i<jArray.length();i++){
//				JSONObject object=jArray.getJSONObject(i);
//				if(object.optInt("CONTENT_TYPE")==10){//文字
////					mNewContent.setText(object.optString("INFO_CONTENT"));
//				}else if(object.optInt("CONTENT_TYPE")==20){//图片
//					urlList.add(object.optString("BIG_PICTURE"));
//					mImageView.setVisibility(View.VISIBLE);
//					mImageLoader.displayImage(object.optString("BIG_PICTURE"),mImageView,mDisplayImageOptions);
//					mImageView.setOnClickListener(this);
//				}else if (object.optInt("CONTENT_TYPE")==30){
////					urlList.add(object.optString("BIG_PICTURE"));
//					// 设置WebView属性，能够执行Javascript脚本
////					mWebView.getSettings().setJavaScriptEnabled(true);
//////					mWebView.getSettings().setPluginsEnabled(true);
////					mWebView.getSettings().setPluginState(WebSettings.PluginState.ON);
////					mWebView.setVisibility(View.VISIBLE);
////					mWebView.getSettings().setUseWideViewPort(true);
////					mWebView.loadUrl(object.optString("BIG_PICTURE"));
//				}
//			}
        } catch (JSONException e) {
            e.printStackTrace();
        }
//        mListView.addHeaderView(headView, null, false);
//        mListView.setAdapter(mAdapter);
//        mRefreshListView.setOnRefreshListener(this);
//        mListView.setOnItemClickListener(this);


        /************************************************************/


//		 findViewById(R.id.comment_to_new).setOnClickListener(this);
//		 findViewById(R.id.share).setOnClickListener(this);
//		 mCollection = (CheckBox) findViewById(R.id.collection_action);
//		 DisplayImageOptions mDisplayImageOptions = DefaultConfigurationFactory
//					.createGalleryDisplayImageOptions(this);
//		 mNewTitle.setText(contentObject.optString("INFO_NAME"));//标题
//		 mNewTimes.setText(contentObject.optString("STATUS_TIME"));//时间
//		 mConnentNum.setText(""+contentObject.optInt("COMMENT_COUNT"));
//		 praiseCount=contentObject.optInt("PRAISE_COUNT");
//		 mGoodAction.setText(""+praiseCount);
//		 // 是否收藏
//		 mCollection.setChecked(contentObject.optInt("COL")>0);
//		 praiseFlag=contentObject.optInt("CON");
//		 mCollection.setOnCheckedChangeListener(this);
//		if (praiseFlag>0) {//点过赞
//			mGoodAction.setChecked(true);
//			mGoodAction.setClickable(false);
//			mGoodAction.setEnabled(false);
//		}else{
//			mGoodAction.setChecked(false);
//			mGoodAction.setOnCheckedChangeListener(new OnCheckedChangeListener() {
//				@Override
//				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//					praiseFlag = 1;
//					actionPraise();
//				}
//			});
//		}
//		//数组格式,为了实现图文混排,但是现在只考虑一张图片一段文字,只不过格式不变
//		try {
//			JSONArray jArray = contentObject.getJSONArray("CONTENTLIST");
//			for(int i=0;i<jArray.length();i++){
//				JSONObject object=jArray.getJSONObject(i);
//				if(object.optInt("CONTENT_TYPE")==10){//文字
//					mNewContent.setText(object.optString("INFO_CONTENT"));
//				}else if(object.optInt("CONTENT_TYPE")==20){//图片
//					urlList.add(object.optString("BIG_PICTURE"));
//					if(urlList.size()!=0){
//						mImageView.setVisibility(View.VISIBLE);
//						mImageLoader.displayImage(object.optString("BIG_PICTURE"),mImageView,mDisplayImageOptions);
//						mImageView.setOnClickListener(this);
//					}
//				}
//			}
//		} catch (JSONException e) {
//			e.printStackTrace();
//		}
//		 mListView.addHeaderView(headView, null, false);
//		 mListView.setAdapter(mAdapter);
//		 mRefreshListView.setOnRefreshListener(this);
//		 mListView.setOnItemClickListener(this);
    }

    /*
     *  查询评论
     *  192.168.16.44:8899/DuoMeiHealth/ConsultationInfoSet?TYPE=findConsuInfoCommentList&INFOID=&PAGESIZE=&PAGENUM=
     */
    private void onDoQueryComment() {

        List<BasicNameValuePair> pairs = new ArrayList<>();
        pairs.add(new BasicNameValuePair("PAGENUM", "5"));
        pairs.add(new BasicNameValuePair("PAGESIZE", "" + pageSize));
        pairs.add(new BasicNameValuePair("INFOID", infoId));
        pairs.add(new BasicNameValuePair("TYPE", "findConsuInfoCommentList"));
        ApiService.doGetConsultationInfoSet(pairs, new MyApiCallback<String>(this) {
            @Override
            public void onAfter() {
                super.onAfter();
                mRefreshListView.onRefreshComplete();
            }

            @Override
            public void onResponse(String response) {
                super.onResponse(response);
                try {
                    JSONObject resObject = new JSONObject(response);
                    if ("1".equals(resObject.optString("code"))) {//加载成功
                        JSONObject jsonObject = resObject.getJSONObject("result");
                        commCount = jsonObject.optInt("count", 0);
                        JSONArray commArray = jsonObject.optJSONArray("commentList");
                        commDatas = new ArrayList<>();
                        NewEntity entity;
                        for (int i = 0; i < commArray.length(); i++) {
                            entity = new NewEntity();
                            entity.COMMENT_CONTENT = commArray.optJSONObject(i).optString("COMMENT_CONTENT");
                            entity.CUSTOMER_NICKNAME = commArray.optJSONObject(i).optString("CUSTOMER_NICKNAME");
                            entity.REPLY_ID = commArray.optJSONObject(i).optString("COMMENT_ID");
                            entity.REPLYTIME = commArray.optJSONObject(i).optString("COMMENT_TIME");
                            entity.CLIENT_ICON_BACKGROUND = commArray.optJSONObject(i).optString("CLIENT_ICON_BACKGROUND");
                            entity.CUSTOMER_ID = commArray.optJSONObject(i).optString("CUSTOMER_ID");
                            entity.UPPER_REPLY_ID = commArray.optJSONObject(i).optString("UPPER_COMMENT_ID");
                            commDatas.add(entity);
                        }
                        if (commDatas.size() > 0)
                            pageSize++;
                        if (pageSize == 2) {//第一次加载
                            NewEntity ne = new NewEntity();
                            ne.CUSTOMER_ID = "-1";
                            commDatas.add(0, ne);
                            mAdapter.onBoundData(commDatas);
                        } else {//第二次加载
                            if (commDatas.size() > 1) {
                                mAdapter.addAll(commDatas);
                            } else if (pageSize > 2) {
                                ToastUtil.showShort("没有更多了");
                            }
                        }

                        mAdapter.setNumberCount(commCount + "");

                    } else {
                        ToastUtil.showShort(DAtyConslutDynMesContent.this, resObject.optString("message"));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Request request, Exception e) {
                super.onError(request, e);
            }
        }, this);

    }

    /*
     * 点赞操作
     * GroupConsultationList?TYPE=praiseConsuInfo&INFOID=&CUSTOMERID=
     */
    private void actionPraise() {
        praiseCount++;
        mGoodAction.setText("" + praiseCount);
        RequestParams mParams = new RequestParams();
        mParams.put("TYPE", "praiseConsuInfo");
        mParams.put("INFOID", infoId);
        mParams.put("CUSTOMERID", SmartFoxClient.getLoginUserId());
        ApiService.doHttpGroupConsultationList(mParams, new AsyncHttpResponseHandler(this) {
            @Override
            public void onSuccess(int statusCode, String content) {
                super.onSuccess(statusCode, content);
                try {
                    JSONObject jsonObject = new JSONObject(content);
                    if (jsonObject.has("error_message")) {
                        praiseCount--;
                        mGoodAction.setText("" + praiseCount);
                        mGoodAction.setChecked(false);
                        mGoodAction.setClickable(true);
                        mGoodAction.setEnabled(true);
                        ToastUtil.showShort(jsonObject.optString("error_message", "操作失败"));
                    } else {
                        mGoodAction.setClickable(false);
                        mGoodAction.setEnabled(false);
                        ToastUtil.showShort(jsonObject.optString("message", "操作成功"));
                    }
                } catch (JSONException e) {

                }
            }
        });
    }


    /**
     * 回复某个人
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        NewEntity entity = mAdapter.datas.get(position - 2);
        REPLY_ID = entity.REPLY_ID;
        otherCustomerId = entity.CUSTOMER_ID;
        isReplay = true;
//		 showCommonentPop(findViewById(R.id.comment_to_new));
    }

    // @Override
    // public void onFinish() {
    // super.onFinish();
    // mCollection.setClickable(true);
    // }
    // });
    // } else {
    // ApiService.doHttpNewsCollectionRemove(mSmartUserId, mNewsId,
    // "news", new AsyncHttpResponseHandler(this) {
    // @Override
    // public void onSuccess(int statusCode, String content) {
    // if ("1".equals(content)) {
    // onUpdateCollectionNumber(isCollection);
    // }
    // mCollection.setClickable(true);
    // }
    // });
    // }
    // }

    private void showCommonentPop(View view) {
        if (mPopBottom == null) {
            LayoutInflater inflater = LayoutInflater.from(this);
            View v = inflater.inflate(R.layout.new_common_pop_layout, null);
            mCommonContent = (EditText) v.findViewById(R.id.content);
            v.findViewById(R.id.cancle).setOnClickListener(this);
            v.findViewById(R.id.release).setOnClickListener(this);
            mPopBottom = new PopupWindow(v, LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, true);
            mPopBottom.setBackgroundDrawable(new BitmapDrawable());
            mPopBottom.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
                    | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
            mPopBottom.setOnDismissListener(new OnDismissListener() {
                @Override
                public void onDismiss() {
                    android.view.WindowManager.LayoutParams params = DAtyConslutDynMesContent.this.getWindow()
                            .getAttributes();
                    params.alpha = 1.0f;
                    DAtyConslutDynMesContent.this.getWindow().setAttributes(params);
                }
            });
        } else if (mPopBottom.isShowing()) {
            mPopBottom.dismiss();
            return;
        }

        android.view.WindowManager.LayoutParams params = this.getWindow().getAttributes();
        params.alpha = 0.5f;
        this.getWindow().setAttributes(params);
        mPopBottom.showAtLocation(view, Gravity.BOTTOM, 0, bottomLayout.getHeight());
        mHandler.sendEmptyMessage(0);
    }

    /**
     * 评论提交
     * /DuoMeiHealth/ConsultationInfoSet?TYPE=commentOnconsultationInfo&INFOID=&CUSTOMERID=&COMMENT_CONTENT=
     */
    private void onComment() {
        List<BasicNameValuePair> pairs = new ArrayList<>();
        pairs.add(new BasicNameValuePair("CUSTOMERID", DoctorHelper.getId()));
        pairs.add(new BasicNameValuePair("COMMENT_CONTENT", mCommonContent.getText().toString()));
        pairs.add(new BasicNameValuePair("INFOID", infoId));
        pairs.add(new BasicNameValuePair("TYPE", "commentOnconsultationInfo"));
        ApiService.doGetConsultationInfoSet(pairs, new MyApiCallback<String>(this) {
            @Override
            public void onResponse(String response) {
                super.onResponse(response);
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    if ("1".equals(jsonObject.optString("code"))) {
                        ToastUtil.showShort(jsonObject.optString("message"));
                        pageSize = 1;
                        onDoQueryComment();
                    } else {
                        ToastUtil.showShort(jsonObject.optString("message"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Request request, Exception e) {
                super.onError(request, e);
            }
        }, this);


    }

    @Override
    public void onRefresh(PullToRefreshBase<ListView> refreshView) {
        mRefreshListView.onRefreshComplete();
//		onDoQueryComment();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
//		case R.id.collection_action:// 是否收藏
//			onCollectionCountUpdate(isChecked);
//			break;
        }
    }

    /**
     * 收藏计数更新
     * 收藏
     * http://220.194.46.204:8080/DuoMeiHealth/
     * GroupConsultationList?TYPE=collectedConsuInfo&CUSTOMERID=&INFOID=
     * 取消收藏
     * GroupConsultationList?TYPE=cancelCollectedInfoBatch&CUSTOMERIDS=&INFOID=
     *
     * @param isCollection
     */
    private void onCollectionCountUpdate(final boolean isCollection) {
        mCollection.setClickable(false);
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
                mCollection.setClickable(true);
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
