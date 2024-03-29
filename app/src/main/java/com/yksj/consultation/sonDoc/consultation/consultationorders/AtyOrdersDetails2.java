package com.yksj.consultation.sonDoc.consultation.consultationorders;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.library.base.base.BaseActivity;
import com.yksj.consultation.app.AppContext;
import com.yksj.consultation.app.AppData;
import com.yksj.consultation.business.LoginBusiness;
import com.yksj.consultation.comm.ImageGalleryActivity;
import com.yksj.consultation.event.MyEvent;
import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.sonDoc.doctor.AtyDoctorMassage;
import com.yksj.healthtalk.net.http.ApiCallback;
import com.yksj.healthtalk.net.http.ApiCallbackWrapper;
import com.yksj.healthtalk.net.http.ApiService;
import com.yksj.healthtalk.utils.HStringUtil;
import com.yksj.healthtalk.utils.ImageItem;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Request;


/**
 * Created by HEKL on 15/9/22.
 * 初始订单详情_
 */
public class AtyOrdersDetails2 extends BaseActivity implements View.OnClickListener, PullToRefreshBase.OnRefreshListener<ScrollView> {
    private TextView mTextNameP, mTextGendar, mTextAge, mTextPhone, mTextAddress, mTextIntroduce, mTextMainIntroduce, mTextNameD, mTextNameE, mTextTime, mTextPrice, mTextTip;
    private TextView mAllergy;//过敏史
    private ImageView mImageHeadP, mImageHeadD, mImageHeadE, mImageMore, mImageMore1;//患者医生专家的头像
    private PullToRefreshScrollView mPullToRefreshScrollView;//整体滑动布局
    private ArrayList<HashMap<String, String>> list = null;//储存图片
    private HorizontalScrollView mView2;//图片横滑布局
    private LinearLayout mGallery;//图片画廊
    private String[] array = null;//病历图片
    private LayoutInflater mInflater;//图片布局
    private ImageLoader mImageLoader;//异步加载图片
    private DisplayImageOptions mOptions;//画廊异步读取操作
    private DisplayImageOptions mOption;//异步加载图片的操作
    private Button mCommit;//提交按钮
    private Button mDelete;//删除按钮
    private String areaCode;//患者地区code
    private String officeName;//科室名称
    private boolean Expanded = false;//true展开,false隐藏
    private boolean Expanded2 = false;//true展开,false隐藏
    private String conId = "";//会诊id
    private int docId, expId;//医生和专家id
    private int type;//订单状态 10-患者申请，基层医生未接单
    private Bundle bundle;

    private HorizontalScrollView mVideo;//视频横滑布局
    private LinearLayout mVideoGallery;//视频画廊

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.aty_orders_details2);
        initView();
        getDataFromServer();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void initView() {
        initializeTitle();
        bundle = new Bundle();
        conId = getIntent().getStringExtra("CONID");
        titleTextV.setText("详情");
        titleLeftBtn.setOnClickListener(this);
        mView2 = (HorizontalScrollView) findViewById(R.id.hs_gallery);
        mVideo = (HorizontalScrollView) findViewById(R.id.hs_video_gallery);
        mTextIntroduce = (TextView) findViewById(R.id.tv_introduce);//病情介绍
        mTextMainIntroduce = (TextView) findViewById(R.id.tv_mainillness_introduce);//主诉
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
        mCommit = (Button) findViewById(R.id.commit);
        mDelete = (Button) findViewById(R.id.delete);
        mGallery = (LinearLayout) findViewById(R.id.ll_gallery);
        mVideoGallery = (LinearLayout) findViewById(R.id.ll_video_gallery);
        mPullToRefreshScrollView = (PullToRefreshScrollView) findViewById(R.id.pull_refresh_scrollview);
        mOptions = DefaultConfigurationFactory.createApplyPicDisplayImageOptions(this);
        mPullToRefreshScrollView.setOnRefreshListener(this);
        mImageLoader = ImageLoader.getInstance();
        mOption = DefaultConfigurationFactory.createHeadDisplayImageOptions(this);
        mInflater = LayoutInflater.from(this);
        mImageHeadD.setOnClickListener(this);
        mImageHeadE.setOnClickListener(this);
        mCommit.setOnClickListener(this);
        mDelete.setOnClickListener(this);
        mGallery.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Intent i = null;
        switch (view.getId()) {
            case R.id.title_back:
                onBackPressed();
                break;
            case R.id.title_right2://授权

                break;
            case R.id.title_right:
                mend();
                break;
            case R.id.imag_head_d://医生头像点击
                if (docId != 0) {
                    i = new Intent(AtyOrdersDetails2.this, AtyDoctorMassage.class);
                    i.putExtra("id", docId + "");
                    i.putExtra("type", 1);
                    i.putExtra("ORDER", 0);
                    startActivity(i);
                }
                break;
            case R.id.imag_head_e://专家头像点击
                if (expId != 0) {
                    i = new Intent(AtyOrdersDetails2.this, AtyDoctorMassage.class);
                    i.putExtra("id", expId + "");
                    i.putExtra("type", 0);
                    i.putExtra("ORDER", 0);
                    startActivity(i);
                }
                break;
            case R.id.commit:
                mend();
                break;
            case R.id.delete:
                delete();
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

    private void delete() {
        List<BasicNameValuePair> pairs = new ArrayList<>();
        pairs.add(new BasicNameValuePair("case_discussion_id", conId));
        pairs.add(new BasicNameValuePair("op", "deletePatientCase"));
        ApiService.OKHttpCaseInfo(pairs, new ApiCallback<String>() {
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
                list = new ArrayList<>();
                if (!TextUtils.isEmpty(response)) {
                    try {
                        JSONObject object = new JSONObject(response);
                        ToastUtil.showShort(object.optString("message"));
                        if (object.optInt("code") == 1) {
                            finish();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        }, this);

    }

    private ArrayList<JSONObject> picList = new ArrayList<>();
    private ArrayList<JSONObject> videoList = new ArrayList<>();
    private ArrayList<JSONObject> thumbnailList = new ArrayList<>();

    ArrayList<ImageItem> videosList = new ArrayList<>();//视频list类

    @SuppressWarnings("deprecation")
    private void getDataFromServer() {
        Map<String, String> map = new HashMap<>();
        map.put("case_discussion_id", conId);
        map.put("op", "queryPatientCaseInfo");
        ApiService.OKHttpCaseList(map, new ApiCallback<String>() {
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
                list = new ArrayList<>();
                if (!TextUtils.isEmpty(response)) {
                    try {
                        JSONObject object = new JSONObject(response);
                        obj = object.getJSONObject("result");
                        if (object.optInt("code") == 1) {
                            if (obj != null) {
                                String time = TimeUtil.format(obj.optString("RECORD_TIME"));//创建时间
                                int count = obj.getJSONArray("PICS").length();//图片数量

                                areaCode = obj.optString("AREA_CODE");
                                officeName = obj.optString("OFFICENAME");
                                mTextAge.setText(obj.optString("AGE"));
                                mTextPhone.setText(obj.optString("MOBIL_PHONE"));
                                mTextAddress.setText(obj.optString("AREA"));
                                mTextMainIntroduce.setText(obj.optString("MEDICAL_NAME"));
                                if (!"".equals(obj.optString("DISEASE_STATEMENT")) && (!"null".equals(obj.optString("DISEASE_STATEMENT")))) {
                                    illness = obj.optString("DISEASE_STATEMENT");
                                    mTextIntroduce.setText(illness);
                                }
                                mTextNameP.setText(obj.optString("REAL_NAME"));
                                //判断男女
                                if ("M".equals(obj.optString("CUSTOMER_SEX"))) {
                                    mTextGendar.setText("男");
                                } else if ("W".equals(obj.optString("CUSTOMER_SEX"))) {
                                    mTextGendar.setText("女");
                                }


//                                if (!"".equals(obj.getJSONObject("PATIENT").optString("ALLERGY")) && (!"null".equals(obj.getJSONObject("PATIENT").optString("ALLERGY")))) {
//                                    allergy = obj.optString("ALLERGY");
//                                    mAllergy.setText(allergy);
//                                }

                                mTextTip.setText(obj.optString("STATUSNAME"));
                                mTextPrice.setText(obj.optString("PRICE"));
                                mTextTime.setText(time);

                                //专家头像
                                if (!"".equals(obj.optString("EXPERT"))) {
                                    expId = obj.getJSONObject("EXPERT").optInt("EXPERTID");
                                    mTextNameE.setText(obj.getJSONObject("EXPERT").optString("EXPERTNAME"));
                                    mImageLoader.displayImage(obj.getJSONObject("EXPERT").optString("EXPERTICON"), mImageHeadE, mOption);
                                } else {
                                    findViewById(R.id.view_line1).setVisibility(View.GONE);
                                    findViewById(R.id.rl_conexp).setVisibility(View.GONE);
                                }
                                //医生头像
                                if (!"".equals(obj.optString("DOCTOR"))) {
                                    docId = obj.getJSONObject("DOCTOR").optInt("DOCTORID");
                                    mTextNameD.setText(obj.getJSONObject("DOCTOR").optString("DOCTORNAME"));
                                    mImageLoader.displayImage(obj.getJSONObject("DOCTOR").optString("DOCTORICON"), mImageHeadD, mOption);
                                } else {
                                    findViewById(R.id.view_line1).setVisibility(View.GONE);
                                    findViewById(R.id.rl_condoc).setVisibility(View.GONE);
                                }
                                if ("".equals(obj.optString("DOCTOR")) && "".equals(obj.optString("EXPERT"))) {
                                    findViewById(R.id.ll_doctors).setVisibility(View.GONE);
                                }
                                //判断是否展开
                                if (mTextIntroduce.getLineCount() < 2) {//行数小于2,将展开按钮隐藏
                                    findViewById(R.id.image_more).setVisibility(View.INVISIBLE);
                                } else {
                                    findViewById(R.id.image_more).setOnClickListener(AtyOrdersDetails2.this);
                                }
                                //判断是否展开
                                if (mAllergy.getLineCount() < 2) {//行数小于2,将展开按钮隐藏
                                    findViewById(R.id.image_more1).setVisibility(View.INVISIBLE);
                                } else {
                                    findViewById(R.id.image_more1).setOnClickListener(AtyOrdersDetails2.this);
                                }

                                picList.clear();
                                videoList.clear();
                                thumbnailList.clear();
                                videosList.clear();
                                mGallery.removeAllViews();
                                mVideoGallery.removeAllViews();

                                //资源准备
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

                                lineCount();

                                int videoCount = videoList.size();
                                if (videoCount > 0) {
                                    for (int i = 0; i < videoCount; i++) {
                                        ImageItem imageItem = new ImageItem();
                                        imageItem.pidId = videoList.get(i).optInt("PICTURE_ID");
                                        imageItem.setThumbnailPath(videoList.get(i).optString("SMALL_PICTURE"));
                                        imageItem.setImagePath(videoList.get(i).optString("BIG_PICTURE"));
                                        if (thumbnailList.size() >= videoCount) {
                                            imageItem.thumbnailId = thumbnailList.get(i).optInt("IPICTURE_IDD");
                                            imageItem.set_thumbnailPath(thumbnailList.get(i).optString("SMALL_PICTURE"));
                                            imageItem.set_imagePath(thumbnailList.get(i).optString("BIG_PICTURE"));
                                        }
                                        imageItem.isNetPic = true;
                                        videosList.add(imageItem);
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
                                    findViewById(R.id.hs_video_gallery).setVisibility(View.VISIBLE);
                                } else {
                                    findViewById(R.id.hs_video_gallery).setVisibility(View.GONE);
                                }

                                //图片key集合
                                array = new String[picList.size()];
                                for (int t = 0; t < picList.size(); t++) {
                                    array[t] = picList.get(t).optString("BIG_PICTURE");
                                }

                                for (int i = 0; i < picList.size(); i++) {
                                    final int index = i;
                                    View view = mInflater.inflate(R.layout.aty_applyform_gallery, mGallery, false);
                                    ImageView img = (ImageView) view.findViewById(R.id.image_illpic);
                                    mImageLoader.displayImage(picList.get(i).optString("SMALL_PICTURE"), img, mOptions);
                                    img.setOnClickListener(new View.OnClickListener() {

                                        @Override
                                        public void onClick(View v) {
                                            Intent intent = new Intent(AtyOrdersDetails2.this, ImageGalleryActivity.class);
                                            intent.putExtra(ImageGalleryActivity.URLS_KEY, array);
                                            intent.putExtra(ImageGalleryActivity.TYPE_KEY, 1);
                                            intent.putExtra("type", 1);// 0,1单个,多个
                                            intent.putExtra("position", index);
                                            startActivity(intent);
                                        }
                                    });
                                    mGallery.addView(view);
                                }
                                for (int j = 0; j < videosList.size(); j++) {
                                    final int index = j;
                                    View view = mInflater.inflate(R.layout.aty_applyform_gallery_video, mGallery, false);
                                    ImageView img = (ImageView) view.findViewById(R.id.image_illpic);
                                    String thumbnail = videosList.get(j).get_imagePath();
                                    if (!HStringUtil.isEmpty(thumbnail)) {
                                        mImageLoader.displayImage(thumbnail, img, mOptions);
                                    } else {
                                        img.setBackgroundResource(R.drawable.video_src_erral);
                                    }
                                    img.setOnClickListener(new View.OnClickListener() {

                                        @Override
                                        public void onClick(View v) {
                                            Uri uri = Uri.parse(AppContext.getApiRepository().URL_DOWNLOAVIDEO + videosList.get(index).getImagePath());
                                            Intent intent = new Intent(Intent.ACTION_VIEW);
                                            intent.setDataAndType(uri, "video/mp4");
                                            startActivity(intent);

//                                            Intent i = new Intent(AtyOrdersDetails.this, InternetVideoDemo.class);
//                                            i.putExtra("url", AppContext.getmRepository().URL_DOWNLOAVIDEO2 + videosList.get(index).getImagePath());
//                                            startActivity(i);
                                        }
                                    });
                                    mVideoGallery.addView(view);
                                }
                                if (expId != 0) {
                                    findViewById(R.id.ll_price).setVisibility(View.VISIBLE);
                                }

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


    /**
     * 患者确认信息
     */
    @SuppressWarnings("deprecation")
    private void confirmInfo() {
        List<BasicNameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("OPTION", "1"));
        params.add(new BasicNameValuePair("CONSULTATIONID", conId + ""));
        params.add(new BasicNameValuePair("CUSTID", LoginBusiness.getInstance().getLoginEntity().getId()));
        ApiService.OKHttpConfirm(params, new ApiCallbackWrapper<String>(this) {
            @Override
            public void onError(Request request, Exception e) {

            }

            @Override
            public void onResponse(String response) {
                if (!TextUtils.isEmpty(response)) {
                    try {
                        JSONObject object = new JSONObject(response);
                        ToastUtil.showShort(object.optString("message"));
                        if (object.optInt("code") == 1) {
                            finish();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }

        }, this);
    }

    /**
     * 患者个人信息确认无空
     */
    private void isBlank() {
        String name = mTextNameP.getText().toString();
        String sex = mTextGendar.getText().toString();
        String age = mTextAge.getText().toString();
        String phone = mTextPhone.getText().toString();
        String address = mTextAddress.getText().toString();
        String introduce = mTextIntroduce.getText().toString();
        if (TextUtils.isEmpty(name)) {
            ToastUtil.showShort("请填写您的姓名");
        } else if (TextUtils.isEmpty(sex)) {
            ToastUtil.showShort("请填写您的性别");
        } else if (TextUtils.isEmpty(age)) {
            ToastUtil.showShort("请填写您的年龄");
        } else if (TextUtils.isEmpty(phone)) {
            ToastUtil.showShort("请填写您的手机");
        } else if (TextUtils.isEmpty(address)) {
            ToastUtil.showShort("请填写您的位置");
        } else if (TextUtils.isEmpty(introduce)) {
            ToastUtil.showShort("请填写您的病情简单说明");
        } else {
            confirmInfo();
        }
    }

    /**
     * 跳转修改界面
     */
    private void mend() {
//        Intent i = new Intent(AtyOrdersDetails2.this, FlowMassageActivity2.class);
//        bundle.putString("CONSULTATION_ID", conId + "");
//        bundle.putString("CONSULTATION_DESC", mTextIntroduce.getText().toString());
//        bundle.putString("PATIENTTEL_PHONE", mTextPhone.getText().toString());
//        bundle.putString("AREA_CODE", areaCode);
//        bundle.putString("OFFICENAME", officeName);
//        bundle.putString("DWELLING_PLACE", mTextAddress.getText().toString());
//        bundle.putString("PATIENT_NAME", mTextNameP.getText().toString());
//        bundle.putString("PATIENT_SEX", mTextGendar.getText().toString());
//        bundle.putString("PATIENT_AGE", mTextAge.getText().toString());
//        bundle.putString("ALLERGY", mAllergy.getText().toString());
//        bundle.putString("MAINCASE", getIntent().getStringExtra("MAINCASE"));
//        bundle.putString("MEDICAL_NAME", mTextMainIntroduce.getText().toString());
//        bundle.putString("TYPE", "1");
//        i.putExtras(bundle);
//        startActivity(i);
    }


    /**
     * 接收更新通知
     */
    public void onEvent(MyEvent event) {
        if ("refresh".equals(event.what)) {
            getDataFromServer();
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

    @Override
    protected void onResume() {
        super.onResume();
        getDataFromServer();
    }
}