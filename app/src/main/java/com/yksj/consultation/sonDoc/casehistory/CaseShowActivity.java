package com.yksj.consultation.sonDoc.casehistory;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.library.base.base.BaseTitleActivity;
import com.yksj.consultation.comm.ImageGalleryActivity;
import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.app.AppContext;
import com.yksj.consultation.app.AppData;
import com.yksj.healthtalk.bean.BaseBean;
import com.yksj.healthtalk.net.http.ApiService;
import com.yksj.healthtalk.net.http.ApiCallbackWrapper;
import com.yksj.healthtalk.utils.DensityUtils;
import com.yksj.healthtalk.utils.HStringUtil;
import com.yksj.healthtalk.utils.ToastUtil;
import com.yksj.healthtalk.utils.ViewFinder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

/**
 * 展示会诊信息的界面
 * Created by lmk on 15/9/25.
 */
public class CaseShowActivity extends BaseTitleActivity implements View.OnClickListener {
    private String content;
    private LinearLayout patientPicLayout, caseItemsLayout, caseImgLayout, videoLayout;
    private ArrayList<JSONObject> datas;//网络加载过来的数据
    private ImageLoader mImageLoader;

    private TextView tvName, tvSex, tvAge, tvPhone, tvAddr, tvDesc,tvAllergy;
    private String recordId;
    private int goalType = 0;//0展示病历   1共享病历时展示病历
    private Button btnSeeSuggest;
    private JSONObject dataObject;
    private LayoutInflater mInflater;//图片布局

    @Override
    public int createLayoutRes() {
        return R.layout.aty_case_content;
    }

    @Override
    public void initialize(Bundle bundle) {
        super.initialize(bundle);
        setTitle("病历详情");
        mImageLoader = ImageLoader.getInstance();
        initView();
        if (getIntent().hasExtra("result")) {
            content = getIntent().getStringExtra("result");
            initData();
        } else {
            recordId = getIntent().getStringExtra("recordId");
            setRight("上传", this::onUploadClick);
            btnSeeSuggest.setVisibility(View.VISIBLE);
            goalType = 1;
            requestData();
        }
    }

    /**
     * 上传
     * @param v
     */
    private void onUploadClick(View v){
        Intent intent = new Intent(CaseShowActivity.this, CaseUploadconfirmActivity.class);
        intent.putExtra("recordId", recordId);
        startActivity(intent);
    }

    /**
     * 加载数据
     */
    private void requestData() {
        ApiService.OkHttpCaseDetail(recordId, new ApiCallbackWrapper<String>(this) {
            @Override
            public void onResponse(String response) {
                super.onResponse(response);
                BaseBean bb = com.alibaba.fastjson.JSONObject.parseObject(response, BaseBean.class);
                if ("1".equals(bb.code)) {
                    content = bb.result;
                    initData();
                } else if (response != null && response instanceof String) {
                    ToastUtil.showShort(bb.message);
                }
            }
        });
    }

    private void initData() {
        if (content != null && content.length() > 0) {
            datas = new ArrayList<>();
            try {
                dataObject = new JSONObject(content);
                onBoundPatientInfo(dataObject);
                JSONObject record = dataObject.getJSONObject("RECORD");
                JSONArray array = record.getJSONArray("CONTENT");
                for (int i = 0; i < array.length(); i++) {
                    datas.add(array.getJSONObject(i));
                }
                if (datas.size() != 0) {
                    onBoundDetailData(datas);//绑定数据到LinearLayout
                }
                onBoundImgData(record.optString("RECORDFILE"));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    private void initView() {
        caseImgLayout = (LinearLayout) findViewById(R.id.fgt_case_img_layout);
        videoLayout = (LinearLayout) findViewById(R.id.fgt_case_vdo_layout);
        caseItemsLayout = (LinearLayout) findViewById(R.id.fgt_case_template_linearlayout);

        tvAddr = (TextView) findViewById(R.id.doctor_write_case_patient_addr2);
        tvName = (TextView) findViewById(R.id.doctor_write_case_patient_name);
        tvSex = (TextView) findViewById(R.id.doctor_write_case_patient_sex);
        tvAge = (TextView) findViewById(R.id.doctor_write_case_patient_age);
        tvPhone = (TextView) findViewById(R.id.doctor_write_case_patient_phone2);
        tvDesc = (TextView) findViewById(R.id.doctor_write_case_patient_desc2);
        tvAllergy = (TextView) findViewById(R.id.doctor_write_case_patient_desc2_allergy2);
        btnSeeSuggest = (Button) findViewById(R.id.aty_case_see_suggest);
        btnSeeSuggest.setOnClickListener(this);
        patientPicLayout = (LinearLayout) findViewById(R.id.doctor_write_case_patient_picture_layout);

        mInflater = LayoutInflater.from(CaseShowActivity.this);

    }


    private void onBoundPatientInfo(JSONObject jsonObject) throws JSONException {
        tvName.setText(jsonObject.optString("CUSTNAME"));
        final StringBuilder sb = new StringBuilder();
        if ("M".equals(jsonObject.optString("SEX")))
            tvSex.setText("男");
        else
            tvSex.setText("女");
        tvAge.setText(jsonObject.optString("AGE"));
        tvAddr.setText(jsonObject.optString("AREA"));
        tvPhone.setText(jsonObject.optString("PHONE"));
        tvDesc.setText(jsonObject.optString("CONDESC"));
        tvAllergy.setText(jsonObject.optString("ALLERGYd"));
        if (jsonObject.has("PICS")) {
            JSONArray array = jsonObject.optJSONArray("PICS");
            for (int m = 0; m < array.length(); m++) {
                sb.append(array.getJSONObject(m).optString("BIG") + ",");
            }
            if (sb.length() > 0)
                sb.deleteCharAt(sb.length() - 1);
            for (int k = 0; k < array.length(); k++) {
                final int imgPosition = k;
                final JSONObject imgObject = array.getJSONObject(k);
                ImageView imageview = new ImageView(CaseShowActivity.this);
                imageview.setLayoutParams(new ViewGroup.LayoutParams(DensityUtils.dip2px(CaseShowActivity.this, 78), DensityUtils.dip2px(CaseShowActivity.this, 78)));
                imageview.setScaleType(ImageView.ScaleType.CENTER_CROP);
                mImageLoader.displayImage(imgObject.optString("SMALL"), imageview);//加载小图片
                imageview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(CaseShowActivity.this, ImageGalleryActivity.class);
                        intent.putExtra(ImageGalleryActivity.URLS_KEY, sb.toString().split(","));
                        intent.putExtra(ImageGalleryActivity.TYPE_KEY, 1);
                        intent.putExtra("type", 1);// 0,1单个,多个
                        intent.putExtra("position", imgPosition);
                        startActivityForResult(intent, 100);
                    }
                });
                patientPicLayout.addView(imageview);

            }
            if (array.length() <= 0) {
                patientPicLayout.setVisibility(View.GONE);
                findViewById(R.id.doctor_write_case_patient_picture).setVisibility(View.GONE);
            }
        }


    }


    /**
     * 绑定LinearLayout数据
     * 患者已经填写完了病历并且上传成功,这时是加载显示病历
     */
    private void onBoundDetailData(ArrayList<JSONObject> datas) {
        for (int i = 0; i < datas.size(); i++) {
            ViewFinder finder;
            final int index = i;
            final JSONObject entity = datas.get(i);
            View itemView = LayoutInflater.from(CaseShowActivity.this).inflate(R.layout.apt_consultion_case_item_show, null, true);
            finder = new ViewFinder(itemView);

            TextView tvCategoryTitle = finder.find(R.id.case_template_item_show_title);
            ;
            if (i == 0) {//第一个一定是开始,显示CLASSNAME
                tvCategoryTitle.setVisibility(View.VISIBLE);
                tvCategoryTitle.setText(entity.optString("CLASSNAME"));//分类名称
            } else {//后面的家判断是否显示CLASSNAME
                JSONObject entity2 = datas.get(i - 1);
                if (!(entity2.optInt("CLASSID") == entity.optInt("CLASSID"))) {//分类开始
                    tvCategoryTitle.setVisibility(View.VISIBLE);
                    tvCategoryTitle.setText(entity.optString("CLASSNAME"));//分类名称
                }
            }
            if (entity.optInt("NEFILL") == 1) {//必填
                finder.find(R.id.case_template_item_show_star).setVisibility(View.VISIBLE);
            }
            finder.setText(R.id.case_template_item_show_name, entity.optString("ITEMNAME"));//先把本item的标题附上去

            TextView tvLeft = (TextView) itemView.findViewById(R.id.case_template_item_show_text_left);
            TextView tvMiddle = (TextView) itemView.findViewById(R.id.case_template_item_show_text_middle);
            TextView tvRight = (TextView) itemView.findViewById(R.id.case_template_item_show_text_right);
            LinearLayout imgLayout = finder.find(R.id.case_template_item_show_images);

            switch (entity.optInt("ITEMTYPE")) {
                case 10://文字填写
                case 20://单选
                case 30://多选
                case 40://单数字填写
                case 60://日期
                case 80://日期
                    tvLeft.setText(entity.optString("INFO"));
                    break;
                case 50://区域数字填写90~100
                    tvLeft.setText(entity.optString("INFO"));
                    tvMiddle.setVisibility(View.VISIBLE);
                    tvRight.setVisibility(View.VISIBLE);
                    tvRight.setText(entity.optString("INFO2"));
                    break;
                case 70://大文本域填写
                    tvLeft.setText(entity.optString("INFO"));
                    break;
                case 90://只有ItemName
                    tvLeft.setVisibility(View.GONE);
                    break;
            }
            caseItemsLayout.addView(itemView);
        }
    }

    private ArrayList<JSONObject> picList = new ArrayList<>();
    private ArrayList<JSONObject> videoList = new ArrayList<>();
    private ArrayList<JSONObject> thumbnailList = new ArrayList<>();


    int videoCount = 0;//视频数量
    int contentCount = 0;//影像资料数量

    //绑定图片
    private void onBoundImgData(String recordfiles) {
        caseImgLayout.removeAllViews();
        videoLayout.removeAllViews();

        picList.clear();
        videoList.clear();
        thumbnailList.clear();

        videoCount = 0;
        final StringBuilder sb = new StringBuilder();


        try {
            JSONArray imgArray = null;
            if (!HStringUtil.isEmpty(recordfiles)) {
                imgArray = new JSONArray(recordfiles);
            } else {
                return;
            }

            contentCount = imgArray.length();

            if (contentCount > 0) {  //含有影像资料
                //准备图片数据
                for (int m = 0; m < contentCount; m++) {
                    if (AppData.PIC_TYPE.equals(imgArray.getJSONObject(m).optString("TYPE"))) {
                        sb.append(imgArray.getJSONObject(m).optString("ICON").replace("-small", "") + ",");
                        picList.add(imgArray.getJSONObject(m));
                    } else if (AppData.VIDEO_TYPE.equals(imgArray.getJSONObject(m).optString("TYPE"))) {
                        videoList.add(imgArray.getJSONObject(m));
                    } else if (AppData.THUMBNAIL_TYPE.equals(imgArray.getJSONObject(m).optString("TYPE"))) {
                        thumbnailList.add(imgArray.getJSONObject(m));
                    }

                }
                if (sb.length() > 0)
                    sb.deleteCharAt(sb.length() - 1);

                //获取视频的数量
                for (int e = 0; e < contentCount; e++) {
                    final JSONObject vdoObject = imgArray.getJSONObject(e);
                    if (AppData.VIDEO_TYPE.equals(vdoObject.optString("TYPE"))) {
                        videoCount++;
                    }
                }

                if (imgArray != null && contentCount > 0) {
                    findViewById(R.id.tv_img_vdo).setVisibility(View.VISIBLE);//影像资料标题
                    if (videoCount > 0) {
                        findViewById(R.id.fgt_case_template_horscroll2_vdo).setVisibility(View.VISIBLE);//视频可见
                    } else {
                        findViewById(R.id.fgt_case_template_horscroll2_vdo).setVisibility(View.GONE);//视频可见
                    }
                    if (contentCount - 2 * videoCount > 0) {
                        findViewById(R.id.fgt_case_template_horscroll2).setVisibility(View.VISIBLE);//上传图片可见
                    } else {
                        findViewById(R.id.fgt_case_template_horscroll2).setVisibility(View.GONE);//上传图片可见
                    }
                } else {
                    return;
                }


                for (int k = 0; k < picList.size(); k++) {
                    final int imgPosition = k;
                    View view = mInflater.inflate(R.layout.aty_applyform_gallery, caseImgLayout, false);
                    ImageView imageview = (ImageView) view.findViewById(R.id.image_illpic);
                    mImageLoader.displayImage(picList.get(k).optString("ICON"), imageview);//加载小图片
                    imageview.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(CaseShowActivity.this, ImageGalleryActivity.class);
                            intent.putExtra(ImageGalleryActivity.URLS_KEY, sb.toString().split(","));
                            intent.putExtra(ImageGalleryActivity.TYPE_KEY, 1);
                            intent.putExtra("type", 1);// 0,1单个,多个
                            intent.putExtra("position", imgPosition);
                            startActivityForResult(intent, 100);
                        }
                    });
                    caseImgLayout.addView(view);
                }
                for (int j = 0; j < videoList.size(); j++) {
                    final int imgPosition = j;
                    View view = mInflater.inflate(R.layout.aty_applyform_video_gallery, videoLayout, false);
                    ImageView imageview = (ImageView) view.findViewById(R.id.image_illpic);
                    mImageLoader.displayImage(thumbnailList.get(j).optString("ICON"), imageview);//加载小图片
                    imageview.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
//                            Intent i = new Intent(getActivity(), InternetVideoDemo.class);
//                            i.putExtra("url", AppContext.getmRepository().URL_DOWNLOAVIDEO + imgObject.optString("URL"));
//                            startActivity(i);
                            Uri uri = Uri.parse(AppContext.getApiRepository().URL_DOWNLOAVIDEO + videoList.get(imgPosition).optString("URL"));
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setDataAndType(uri, "video/mp4");
                            startActivity(intent);


                        }
                    });
                    videoLayout.addView(view);
                }
            } else {//不含影像资料
                findViewById(R.id.tv_img_vdo).setVisibility(View.GONE);//影像资料标题不可见
                findViewById(R.id.fgt_case_template_horscroll2_vdo).setVisibility(View.GONE);//视频不可见
                findViewById(R.id.fgt_case_template_horscroll2).setVisibility(View.GONE);//上传图片不可见
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.aty_case_see_suggest:
                JSONObject expert = dataObject.optJSONObject("EXPERT");
                intent = new Intent(CaseShowActivity.this, CaseShowSuggestActivity.class);
                intent.putExtra("expert", expert.optString("EXPERTNAME") + " | " + expert.optString("EXPERTTITLE"));
                intent.putExtra("suggest", dataObject.optString("DOCTOR_ADVICE"));
                startActivity(intent);
                break;
        }
    }

}
