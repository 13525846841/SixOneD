package com.yksj.consultation.sonDoc.consultation.consultationorders;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import okhttp3.Request;

import com.library.base.base.BaseActivity;
import com.yksj.consultation.comm.ImageGalleryActivity;
import com.yksj.consultation.sonDoc.R;
import com.yksj.healthtalk.net.http.ApiService;
import com.yksj.healthtalk.net.http.MyApiCallback;
import com.yksj.healthtalk.utils.TimeUtil;

import org.handmark.pulltorefresh.library.PullToRefreshBase;
import org.handmark.pulltorefresh.library.PullToRefreshScrollView;
import org.json.JSONException;
import org.json.JSONObject;
import org.universalimageloader.core.DefaultConfigurationFactory;
import org.universalimageloader.core.DisplayImageOptions;
import org.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by HEKL on 15/10/13.
 * Used for 医生会诊意见_
 */
public class AtyConsultOpinionD extends BaseActivity implements View.OnClickListener, PullToRefreshBase.OnRefreshListener<ScrollView> {
    private int conId;
    private TextView textOpinion, textSupply;
    private int questionFlag;
    private int answerFlag;
    private LinearLayout llQuestion;
    private HorizontalScrollView mView2;//图片横滑布局
    private LinearLayout mGallery;//图片画廊
    private String[] array = null;//病历图片
    private LayoutInflater mInflater;//图片布局
    private ImageLoader mImageLoader;//异步加载图片
    private DisplayImageOptions mOptions;//画廊异步读取操作
    private ArrayList<HashMap<String, String>> list = null;//储存图片
    private PullToRefreshScrollView mPullToRefreshScrollView;//整体滑动布局

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.aty_consult_opinion_d);
        initView();
    }

    private void initView() {
        initializeTitle();
        conId = getIntent().getIntExtra("conId", 0);
        titleTextV.setText("会诊意见");
        textOpinion = (TextView) findViewById(R.id.tv_opinion);
        mPullToRefreshScrollView = (PullToRefreshScrollView) findViewById(R.id.pull_refresh_scrollview);
        mPullToRefreshScrollView.setOnRefreshListener(this);
        titleLeftBtn.setOnClickListener(this);
        llQuestion = (LinearLayout) findViewById(R.id.ll_question);
        mImageLoader = ImageLoader.getInstance();
        mOptions = DefaultConfigurationFactory.createApplyPicDisplayImageOptions(this);
        mInflater = LayoutInflater.from(this);
        loadOpinion();
    }

    private void loadOpinion() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("CONSULTATIONID", conId + "");
        map.put("OPTION", "9");
        ApiService.OKHttpgetOpinion(map,  new MyApiCallback<String>(this) {
            @Override
            public void onError(Request request, Exception e) {

            }

            @Override
            public void onAfter() {
                super.onAfter();
                mPullToRefreshScrollView.onRefreshComplete();
            }

            @Override
            public void onResponse(String response) {
                if (!TextUtils.isEmpty(response)) {
                    try {
                        JSONObject object = new JSONObject(response);
                        View view = getLayoutInflater().inflate(R.layout.view_question, null);
                        View viewSec = getLayoutInflater().inflate(R.layout.view_question, null);
                        TextView textFrom = (TextView) view.findViewById(R.id.tv_fromwhere);
                        TextView textTime = (TextView) view.findViewById(R.id.tv_time);
                        TextView textQuestion = (TextView) view.findViewById(R.id.tv_question);

                        TextView textFrom2 = (TextView) viewSec.findViewById(R.id.tv_fromwhere);
                        TextView textTime2 = (TextView) viewSec.findViewById(R.id.tv_time);
                        TextView textQuestion2 = (TextView) viewSec.findViewById(R.id.tv_question);
                        String time = TimeUtil.formatTime(object.getJSONObject("result").optString("QUESTIONTIME"));
                        String time2 = TimeUtil.formatTime(object.getJSONObject("result").optString("ANSWERTIME"));
                        mView2 = (HorizontalScrollView) view.findViewById(R.id.hs_gallery);
                        mGallery = (LinearLayout) view.findViewById(R.id.ll_gallery);
                        mGallery.setOnClickListener(AtyConsultOpinionD.this);
                        list = new ArrayList<>();
                        llQuestion.setVisibility(View.GONE);
                        llQuestion.removeAllViews();
                        if (object.optInt("code") == 1) {
                            textOpinion.setText(object.getJSONObject("result").optString("CONTENT"));
                            questionFlag = object.getJSONObject("result").optInt("QUESTIONFLAG");
                            answerFlag = object.getJSONObject("result").optInt("ANSWERFLAG");
                            if (questionFlag == 1) {
                                llQuestion.setVisibility(View.VISIBLE);
                                textFrom.setText("患者疑问:");
                                textTime.setText(time);
                                textQuestion.setText(object.getJSONObject("result").optString("QUESTION"));
                                llQuestion.addView(view);
                                if (answerFlag == 1) {
                                    textFrom2.setText("专家解答:");
                                    textQuestion2.setText(object.getJSONObject("result").optString("ANSWER"));
                                    textTime2.setText(time2);
                                    llQuestion.addView(viewSec);
                                }
                                if (object.getJSONObject("result").getJSONArray("QUESTIONFILE").length() != 0) {
                                    int count = object.getJSONObject("result").getJSONArray("QUESTIONFILE").length();//图片数量
                                    //图片的适配
                                    mView2.setVisibility(View.VISIBLE);
                                    for (int t = 0; t < count; t++) {
                                        JSONObject ob = object.getJSONObject("result").getJSONArray("QUESTIONFILE").getJSONObject(t);
                                        HashMap<String, String> map = new HashMap<String, String>();
                                        map.put("ID", "" + ob.optInt("PIC_ID"));
                                        map.put("SMALL", ob.optString("SMALL_PICTURE"));
                                        map.put("BIG", ob.optString("BIG_PICTURE"));
                                        map.put("SEQ", "" + ob.optInt("PICTURE_SEQ"));
                                        list.add(map);
                                    }
                                    array = new String[count];
                                    for (int t = 0; t < count; t++) {
                                        array[t] = list.get(t).get("BIG");
                                    }
                                    mGallery.removeAllViews();
                                    for (int i = 0; i < count; i++) {
                                        final int index = i;
                                        View view2 = mInflater.inflate(R.layout.view_gallery, mGallery, false);
                                        ImageView img = (ImageView) view2.findViewById(R.id.image_illpic);
                                        mImageLoader.displayImage(list.get(i).get("SMALL"), img, mOptions);
                                        img.setOnClickListener(new View.OnClickListener() {

                                            @Override
                                            public void onClick(View v) {
                                                Intent intent = new Intent(AtyConsultOpinionD.this, ImageGalleryActivity.class);
                                                intent.putExtra(ImageGalleryActivity.URLS_KEY, array);
                                                intent.putExtra(ImageGalleryActivity.TYPE_KEY, 1);
                                                intent.putExtra("type", 1);// 0,1单个,多个
                                                intent.putExtra("position", index);
                                                startActivity(intent);
                                            }
                                        });
                                        mGallery.addView(view2);
                                    }
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, this);
    }


    @Override
    public void onClick(View view) {
        Intent i;
        switch (view.getId()) {
            case R.id.title_back:
                onBackPressed();
                break;
            case R.id.tv_supply:
                i = new Intent(AtyConsultOpinionD.this, AtyExpertOpinion.class);
                i.putExtra("TYPE", 2);
                i.putExtra("conId", conId);
                startActivity(i);
                break;
        }
    }

    @Override
    public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {
        loadOpinion();
    }
}
