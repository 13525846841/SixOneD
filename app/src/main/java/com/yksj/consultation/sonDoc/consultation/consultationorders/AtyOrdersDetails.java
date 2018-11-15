package com.yksj.consultation.sonDoc.consultation.consultationorders;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.library.base.base.BaseActivity;
import com.squareup.picasso.Picasso;
import com.yksj.consultation.app.AppContext;
import com.yksj.consultation.app.AppData;
import com.yksj.consultation.business.LoginBusiness;
import com.yksj.consultation.comm.ImageGalleryActivity;
import com.yksj.consultation.comm.SingleBtnFragmentDialog;
import com.yksj.consultation.event.MyEvent;
import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.sonDoc.casehistory.DoctorWriteCaseActivity;
import com.yksj.consultation.sonDoc.doctor.AtyDoctorMassage;
import com.yksj.consultation.sonDoc.doctor.SelectExpertMainUI;
import com.yksj.healthtalk.net.http.ApiCallback;
import com.yksj.healthtalk.net.http.ApiService;
import com.yksj.healthtalk.utils.HStringUtil;
import com.yksj.healthtalk.utils.TimeUtil;
import com.yksj.healthtalk.utils.ToastUtil;

import org.apache.http.message.BasicNameValuePair;
import org.greenrobot.eventbus.EventBus;
import org.handmark.pulltorefresh.library.PullToRefreshBase;
import org.handmark.pulltorefresh.library.PullToRefreshScrollView;
import org.json.JSONException;
import org.json.JSONObject;
import org.universalimageloader.core.DefaultConfigurationFactory;
import org.universalimageloader.core.DisplayImageOptions;
import org.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Request;

/**
 * Created by HEKL on 15/9/22.
 * 初始订单详情_
 */
public class AtyOrdersDetails extends BaseActivity implements View.OnClickListener, PullToRefreshBase.OnRefreshListener<ScrollView> {
    private TextView mTextNameP, mTextGendar, mTextAge, mTextPhone, mTextAddress, mTextIntroduce, mTextNameD, mTextNameE, mTextTime, mTextPrice, mTextTip, mOutPatient, mEvaluate;
    private TextView mAllergy;//过敏史
    private ImageView mImageHeadP, mImageHeadD, mImageHeadE, mImageMore, mImageMore1;//患者医生专家的头像
    private PullToRefreshScrollView mPullToRefreshScrollView;//整体滑动布局
    private HorizontalScrollView mView2;//图片横滑布局
    private LinearLayout mGallery;//图片画廊
    private HorizontalScrollView mVideos;//视频横滑布局
    private LinearLayout mVideosGallery;//视频画廊
    private String[] array = null;//病历图片
    private LayoutInflater mInflater;//图片布局
    private ImageLoader mImageLoader;//异步加载图片
    private DisplayImageOptions mOptions;//画廊异步读取操作
    private DisplayImageOptions mOption;//异步加载图片的操作
    private boolean ISEXPERT = true;
    private boolean Expanded = false;//true展开,false隐藏
    private boolean Expanded2 = false;//true展开,false隐藏
    private int officeId = 0;//诊室id
    private int conId;//会诊id
    private int docId, expId;//医生和专家id
    private int type;//订单状态 10-患者申请，基层医生未接单\15-医生发起,等待患者确认
    private int clickCount = 0;
    private String userId;

    private ArrayList<JSONObject> picList = new ArrayList<>();
    private ArrayList<JSONObject> videoList = new ArrayList<>();
    private ArrayList<JSONObject> thumbnailList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.aty_orders_details);
        initView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void initView() {
        initializeTitle();
        conId = getIntent().getIntExtra("CONID", 0);
        userId = LoginBusiness.getInstance().getLoginEntity().getId();
        titleTextV.setText("会诊详情");
        titleLeftBtn.setOnClickListener(this);
        mView2 = (HorizontalScrollView) findViewById(R.id.hs_gallery);
        mVideos = (HorizontalScrollView) findViewById(R.id.hs_vdo_gallery);
        mTextIntroduce = (TextView) findViewById(R.id.tv_introduce);//病情介绍
        mAllergy = (TextView) findViewById(R.id.tv_introduce1);
        mImageHeadP = (ImageView) findViewById(R.id.imag_head_p);//患者头像
        mImageHeadD = (ImageView) findViewById(R.id.imag_head_d);//医生头像
        mImageHeadE = (ImageView) findViewById(R.id.imag_head_e);//专家头像
        mTextAddress = (TextView) findViewById(R.id.tv_address);//患者地址
        mImageMore = (ImageView) findViewById(R.id.image_more);//更多
        mImageMore1 = (ImageView) findViewById(R.id.image_more1);//更多(过敏史)
        mTextGendar = (TextView) findViewById(R.id.tv_gender);//患者性别
        mTextNameD = (TextView) findViewById(R.id.tv_name_d);//医生姓名
        mTextNameE = (TextView) findViewById(R.id.tv_name_e);//医生头像
        mTextNameP = (TextView) findViewById(R.id.tv_name_p);//患者姓名
        mTextTip = (TextView) findViewById(R.id.tv_ordertip);//会诊提醒
        mTextPrice = (TextView) findViewById(R.id.tv_price);//会诊费用
        mTextPhone = (TextView) findViewById(R.id.tv_tele);//患者手机
        mTextTime = (TextView) findViewById(R.id.tv_time);//创建时间
        mTextAge = (TextView) findViewById(R.id.tv_age);//患者年龄
        mGallery = (LinearLayout) findViewById(R.id.ll_gallery);
        mVideosGallery = (LinearLayout) findViewById(R.id.ll_vdo_gallery);
        mPullToRefreshScrollView = (PullToRefreshScrollView) findViewById(R.id.pull_refresh_scrollview);
        mOutPatient = (TextView) findViewById(R.id.outpatient);
        mOutPatient.setOnClickListener(this);
        mEvaluate = (TextView) findViewById(R.id.evaluate);
        mEvaluate.setOnClickListener(this);
        mOptions = DefaultConfigurationFactory.createApplyPicDisplayImageOptions(this);
        mPullToRefreshScrollView.setOnRefreshListener(this);
        mImageLoader = ImageLoader.getInstance();
        mOption = DefaultConfigurationFactory.createHeadDisplayImageOptions(this);
        mInflater = LayoutInflater.from(this);
        mImageHeadD.setOnClickListener(this);
        mImageHeadE.setOnClickListener(this);

        mGallery.setOnClickListener(this);
        getDataFromServer();
    }

    @Override
    public void onClick(View view) {
        Intent i;
        switch (view.getId()) {
            case R.id.title_back:
                onBackPressed();
                break;
            case R.id.outpatient:
                accept();
                break;
            case R.id.evaluate:
                //医生拒绝接单
                i = new Intent(AtyOrdersDetails.this, AtyConsultReject.class);
                i.putExtra("conId", conId + "");
                startActivity(i);
                break;
            case R.id.imag_head_d://医生头像点击
                if (docId != 0) {
                    i = new Intent(AtyOrdersDetails.this, AtyDoctorMassage.class);
                    i.putExtra("id", docId + "");
                    i.putExtra("type", 1);
                    i.putExtra("ORDER", 0);
                    startActivity(i);
                }
                break;
            case R.id.imag_head_e://专家头像点击
                if (expId != 0) {
                    i = new Intent(AtyOrdersDetails.this, AtyDoctorMassage.class);
                    i.putExtra("id", expId + "");
                    i.putExtra("type", 0);
                    i.putExtra("ORDER", 0);
                    startActivity(i);
                }
                break;
            case R.id.image_more://更多
                if (Expanded) {
                    Expanded = false;
                    mTextIntroduce.setMaxLines(2);
                    mImageMore.setImageResource(R.drawable.gengduos);
                } else {
                    Expanded = true;
                    mTextIntroduce.setMaxLines(100);
                    mImageMore.setImageResource(R.drawable.shouqis);
                }
                break;
            case R.id.image_more1://更多
                if (Expanded2) {
                    Expanded2 = false;
                    mAllergy.setMaxLines(2);
                    mImageMore1.setImageResource(R.drawable.gengduos);
                } else {
                    Expanded2 = true;
                    mAllergy.setMaxLines(100);
                    mImageMore1.setImageResource(R.drawable.shouqis);
                }
                break;
        }
    }


    @SuppressWarnings("deprecation")
    private void getDataFromServer() {
        List<BasicNameValuePair> pairs = new ArrayList<>();
        pairs.add(new BasicNameValuePair("CONSULTATIONID", conId + ""));
        pairs.add(new BasicNameValuePair("CUSTID", LoginBusiness.getInstance().getLoginEntity().getId()));
        ApiService.OKHttpConsultInfo(0, pairs, new ApiCallback<String>() {
            @Override
            public void onError(Request request, Exception e) {

            }

            @Override
            public void onBefore(Request request) {
                super.onBefore(request);
                mPullToRefreshScrollView.setRefreshing();
            }

            @Override
            public void onAfter() {
                super.onAfter();
                mPullToRefreshScrollView.onRefreshComplete();
            }

            @Override
            public void onResponse(String response) {
                JSONObject obj = null;
                picList.clear();
                videoList.clear();
                thumbnailList.clear();

                if (!TextUtils.isEmpty(response)) {
                    try {
                        JSONObject object = new JSONObject(response);
                        obj = object.getJSONObject("result");
                        if (object.optInt("code") == 1) {
                            officeId = obj.optInt("OFFICE_ID");
                            if ("".equals(obj.optString("EXPERT"))) {
                                ISEXPERT = false;
                            } else {
                                ISEXPERT = true;
                            }
                            type = obj.optInt("STATUS");
                            String time = TimeUtil.format(obj.optString("TIME"));//创建时间
                            int count = obj.getJSONArray("PICS").length();//图片数量
                            mTextAge.setText(obj.optString("AGE"));
                            mTextPhone.setText(obj.optString("PHONE"));
                            mTextAddress.setText(obj.optString("AREA"));
                            if (!"".equals(obj.optString("CONDESC")) && (!"null".equals(obj.optString("CONDESC")))) {
                                illness = obj.optString("CONDESC");
                                mTextIntroduce.setText(illness);
                            }

                            if (!"".equals(obj.getJSONObject("PATIENT").optString("ALLERGY")) && (!"null".equals(obj.getJSONObject("PATIENT").optString("ALLERGY")))) {
                                allergy = obj.optString("ALLERGY");
                                mAllergy.setText(allergy);
                            }
                            mTextPrice.setText(obj.optString("PRICE"));
                            mTextTip.setText(obj.optString("STATUSNAME"));
                            mTextTime.setText(time);
                            mImageLoader.displayImage(obj.getJSONObject("PATIENT").optString("PATIENTICON"), mImageHeadP, mOption);
                            if (ISEXPERT) {
                                findViewById(R.id.ll_price).setVisibility(View.VISIBLE);
                            }
                            //判断男女
                            mTextNameP.setText(obj.optString("CUSTNAME"));
                            if ("M".equals(obj.optString("SEX"))) {
                                mTextGendar.setText("男");
                            } else if ("W".equals(obj.optString("SEX"))) {
                                mTextGendar.setText("女");
                            }
                            //专家头像
                            if (!"".equals(obj.optString("EXPERT"))) {
                                expId = obj.getJSONObject("EXPERT").optInt("EXPERTID");
                                mTextNameE.setText(obj.getJSONObject("EXPERT").optString("EXPERTNAME"));
                                mImageLoader.displayImage(obj.getJSONObject("EXPERT").optString("EXPERTICON"), mImageHeadE, mOption);
                                String expUrl= AppContext.getApiRepository().URL_QUERYHEADIMAGE_NEW+obj.getJSONObject("EXPERT").optString("EXPERTICON");
                                Picasso.with(AtyOrdersDetails.this).load(expUrl).error(R.drawable.default_head_doctor).placeholder(R.drawable.default_head_doctor).into(mImageHeadE);
                            } else {
                                findViewById(R.id.rl_conexp).setVisibility(View.GONE);
                            }
                            //医生头像
                            if (!"".equals(obj.optString("DOCTOR"))) {
                                docId = obj.getJSONObject("DOCTOR").optInt("DOCTORID");
                                mTextNameD.setText(obj.getJSONObject("DOCTOR").optString("DOCTORNAME"));
                                mImageLoader.displayImage(obj.getJSONObject("DOCTOR").optString("DOCTORICON"), mImageHeadD, mOption);
                            } else {
                                findViewById(R.id.rl_condoc).setVisibility(View.GONE);
                            }

                            if (HStringUtil.isEmpty(obj.optString("DOCTOR")) && HStringUtil.isEmpty(obj.optString("EXPERT"))) {
                                findViewById(R.id.ll_doctors).setVisibility(View.GONE);
                            }
                            //判断是否展开
                            if (mTextIntroduce.getLineCount() < 2) {//行数小于2,将展开按钮隐藏
                                findViewById(R.id.image_more).setVisibility(View.INVISIBLE);
                            } else {
                                findViewById(R.id.image_more).setOnClickListener(AtyOrdersDetails.this);
                            }
                            //判断是否展开
                            if (mAllergy.getLineCount() < 2) {//行数小于2,将展开按钮隐藏
                                findViewById(R.id.image_more1).setVisibility(View.INVISIBLE);
                            } else {
                                findViewById(R.id.image_more1).setOnClickListener(AtyOrdersDetails.this);
                            }
                            lineCount();
                            //影像资源准备
                            for (int m = 0; m < count; m++) {
                                JSONObject jsonObject = obj.getJSONArray("PICS").getJSONObject(m);
                                if (AppData.PIC_TYPE.equals(jsonObject.optString("PIC_TYPE"))) {
                                    picList.add(jsonObject);
                                } else if (AppData.VIDEO_TYPE.equals(jsonObject.optString("PIC_TYPE"))) {
                                    videoList.add(jsonObject);
                                } else if (AppData.THUMBNAIL_TYPE.equals(jsonObject.optString("PIC_TYPE"))) {
                                    thumbnailList.add(jsonObject);
                                }
                            }
                            //图片的适配
                            if (count > 0) {
                                findViewById(R.id.tv_illpic).setVisibility(View.VISIBLE);
                                findViewById(R.id.view_line).setVisibility(View.VISIBLE);
                            } else {
                                findViewById(R.id.tv_illpic).setVisibility(View.GONE);
                                findViewById(R.id.view_line).setVisibility(View.GONE);
                            }

                            if (picList.size() > 0) {
                                findViewById(R.id.hs_gallery).setVisibility(View.VISIBLE);
                            } else {
                                findViewById(R.id.hs_gallery).setVisibility(View.GONE);
                            }

                            if (videoList.size() > 0) {
                                findViewById(R.id.hs_vdo_gallery).setVisibility(View.VISIBLE);
                            } else {
                                findViewById(R.id.hs_vdo_gallery).setVisibility(View.GONE);
                            }


                            //图片key集合
                            array = new String[picList.size()];
                            for (int t = 0; t < picList.size(); t++) {
                                array[t] = picList.get(t).optString("BIG");
                            }


                            mGallery.removeAllViews();
                            mVideosGallery.removeAllViews();

                            for (int i = 0; i < picList.size(); i++) {
                                final int index = i;
                                View view = mInflater.inflate(R.layout.aty_applyform_gallery, mGallery, false);
                                ImageView img = (ImageView) view.findViewById(R.id.image_illpic);
                                mImageLoader.displayImage(picList.get(i).optString("SMALL"), img, mOptions);
                                img.setOnClickListener(new View.OnClickListener() {

                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(AtyOrdersDetails.this, ImageGalleryActivity.class);
                                        intent.putExtra(ImageGalleryActivity.URLS_KEY, array);
                                        intent.putExtra(ImageGalleryActivity.TYPE_KEY, 1);
                                        intent.putExtra("type", 1);// 0,1单个,多个
                                        intent.putExtra("position", index);
                                        startActivity(intent);
                                    }
                                });
                                mGallery.addView(view);
                            }
                            for (int j = 0; j < videoList.size(); j++) {
                                final int index = j;
                                View view = mInflater.inflate(R.layout.aty_applyform_gallery_video, mGallery, false);
                                ImageView img = (ImageView) view.findViewById(R.id.image_illpic);
                                mImageLoader.displayImage(thumbnailList.get(j).optString("SMALL"), img, mOptions);
                                img.setOnClickListener(new View.OnClickListener() {

                                    @Override
                                    public void onClick(View v) {
                                        Uri uri = Uri.parse(AppContext.getApiRepository().URL_DOWNLOAVIDEO + videoList.get(index).optString("BIG"));
                                        Intent intent = new Intent(Intent.ACTION_VIEW);
                                        intent.setDataAndType(uri, "video/mp4");
                                        startActivity(intent);
                                    }
                                });
                                mVideosGallery.addView(view);
                            }


                            switch (type) {
                                case 10:
                                    findViewById(R.id.ll_done).setVisibility(View.VISIBLE);
                                    mOutPatient.setVisibility(View.VISIBLE);
                                    mEvaluate.setVisibility(View.VISIBLE);
                                    mOutPatient.setText("接单");
                                    mEvaluate.setText("拒绝");
                                    break;
                            }
                        } else {
                            ToastUtil.showShort(object.optString("message"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        }, this);
    }

    public void onEvent(MyEvent event) {
        if ("refresh".equals(event.what)) {
            getDataFromServer();
        }
    }


    /**
     * 接单
     */
    private void accept() {
        if (clickCount > 0) {
            return;
        }
        clickCount++;
        List<BasicNameValuePair> pairs = new ArrayList<>();
        pairs.add(new BasicNameValuePair("OPTION", 5 + ""));
        pairs.add(new BasicNameValuePair("CONSULTATIONID", conId + ""));
        pairs.add(new BasicNameValuePair("DOCTORID", userId));
        ApiService.OKHttpAccept(pairs, new ApiCallback<String>() {

            @Override
            public void onError(Request request, Exception e) {

            }

            @Override
            public void onResponse(String response) {
                if (!TextUtils.isEmpty(response)) {
                    try {
                        JSONObject obj = new JSONObject(response);
                        if ("1".equals(obj.optString("code"))) {
                            if (ISEXPERT == false) {
                                SingleBtnFragmentDialog.showSinglebtn(AtyOrdersDetails.this, "您已接单，根据患者病情，为他选择一个会诊专家吧。", "选择会诊专家", new SingleBtnFragmentDialog.OnClickSureBtnListener() {
                                    @Override
                                    public void onClickSureHander() {
                                        Intent intent = new Intent(AtyOrdersDetails.this, SelectExpertMainUI.class);
                                        intent.putExtra("consultId", conId + "");
                                        intent.putExtra("OFFICECODE", officeId + "");
                                        startActivity(intent);
                                    }
                                }).show();
                            } else {
                                SingleBtnFragmentDialog.showSinglebtn(AtyOrdersDetails.this, "您已接单，根据患者病情，为他填写病历吧。", "填写病历", new SingleBtnFragmentDialog.OnClickSureBtnListener() {
                                    @Override
                                    public void onClickSureHander() {
                                        Intent intent = new Intent(AtyOrdersDetails.this, DoctorWriteCaseActivity.class);
                                        intent.putExtra("consultId", conId + "");
                                        startActivity(intent);
                                    }
                                }).show();
                            }
                            findViewById(R.id.outpatient).setVisibility(View.GONE);
                            findViewById(R.id.evaluate).setVisibility(View.GONE);
                            mTextTip.setText("您已经成功接单!");
                        } else {
                            clickCount = 0;
                            ToastUtil.showShort(obj.optString("message"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        }, this);
    }

    @Override
    public void onBackPressed() {
        if (getIntent().hasExtra("BACK")) {
            int backMain = getIntent().getIntExtra("BACK", 0);
            if (backMain == 2) {
                Intent intent = new Intent(AtyOrdersDetails.this, MyConsultationActivity.class);
                intent.putExtra("BACK", 2);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            } else {
                super.onBackPressed();
            }
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {
        getDataFromServer();
    }

    String allergy = "";
    String illness = "";

    private void lineCount() {
        //疾病说明
        if (!TextUtils.isEmpty(illness)) {
            if (illness.length() < 50)//字数小雨,将展开按钮隐藏
                mImageMore.setVisibility(View.INVISIBLE);
            else
                mTextIntroduce.setMaxLines(2);
            mImageMore.setImageResource(R.drawable.gengduos);
            mImageMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Expanded) {
                        Expanded = false;
                        mTextIntroduce.setMaxLines(2);
                        mImageMore.setImageResource(R.drawable.gengduos);
                    } else {
                        Expanded = true;
                        mTextIntroduce.setMaxLines(100);
                        mImageMore.setImageResource(R.drawable.shouqis);
                    }
                }
            });
        } else {
            mTextIntroduce.setVisibility(View.GONE);
            mImageMore.setVisibility(View.GONE);
        }

        //过敏史
        if (!TextUtils.isEmpty(allergy)) {
            if (allergy.length() < 50)//字数小于,将展开按钮隐藏
                mImageMore1.setVisibility(View.INVISIBLE);
            else
                mAllergy.setMaxLines(2);
            mImageMore1.setImageResource(R.drawable.gengduos);
            mImageMore1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Expanded2) {
                        Expanded2 = false;
                        mAllergy.setMaxLines(2);
                        mImageMore1.setImageResource(R.drawable.gengduos);
                    } else {
                        Expanded2 = true;
                        mAllergy.setMaxLines(100);
                        mImageMore1.setImageResource(R.drawable.shouqis);
                    }
                }
            });
        } else {
            mAllergy.setVisibility(View.GONE);
            mImageMore1.setVisibility(View.GONE);
        }
    }
}