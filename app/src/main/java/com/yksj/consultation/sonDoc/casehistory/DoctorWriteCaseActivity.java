package com.yksj.consultation.sonDoc.casehistory;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;

import com.library.base.base.BaseActivity;
import com.library.base.utils.StorageUtils;
import com.yksj.consultation.app.AppContext;
import com.yksj.consultation.app.AppData;
import com.yksj.consultation.business.LoginBusiness;
import com.yksj.consultation.comm.DoubleBtnFragmentDialog;
import com.yksj.consultation.comm.ImageGalleryActivity;
import com.yksj.consultation.comm.SingleBtnFragmentDialog;
import com.yksj.consultation.constant.Constant;
import com.yksj.consultation.constant.ObjectType;
import com.yksj.consultation.dialog.WaitDialog;
import com.yksj.consultation.doctor.DoctorSeeServiceActivity;
import com.yksj.consultation.event.MyEvent;
import com.yksj.consultation.im.ChatActivity;
import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.sonDoc.consultation.PlayVideoActiviy;
import com.yksj.consultation.sonDoc.consultation.PlayVideoActiviy2;
import com.yksj.consultation.sonDoc.consultation.RecordMadeAty;
import com.yksj.consultation.sonDoc.consultation.TemplateItemMultipleChoiceActivity;
import com.yksj.consultation.sonDoc.consultation.consultationorders.AtyOrderDetails;
import com.yksj.consultation.utils.CropUtils;
import com.yksj.healthtalk.bean.BaseBean;
import com.yksj.healthtalk.function.photoutil.AlbumActivity;
import com.yksj.healthtalk.function.photoutil.GalleryActivity;
import com.yksj.healthtalk.net.http.ApiCallback;
import com.yksj.healthtalk.net.http.ApiConnection;
import com.yksj.healthtalk.net.http.ApiService;
import com.yksj.healthtalk.net.http.MyApiCallback;
import com.yksj.healthtalk.utils.Bimp;
import com.yksj.healthtalk.utils.BitmapUtils;
import com.yksj.healthtalk.utils.DensityUtils;
import com.yksj.healthtalk.utils.FileUtils;
import com.yksj.healthtalk.utils.HStringUtil;
import com.yksj.healthtalk.utils.ImageItem;
import com.yksj.healthtalk.utils.JsonParseUtils;
import com.yksj.healthtalk.utils.SystemUtils;
import com.yksj.healthtalk.utils.ToastUtil;
import com.yksj.healthtalk.utils.WheelUtils;
import com.yksj.healthtalk.views.MessageImageView;
import com.yksj.healthtalk.views.MessageThumbnailImageView;

import org.apache.http.message.BasicNameValuePair;
import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.universalimageloader.core.DefaultConfigurationFactory;
import org.universalimageloader.core.DisplayImageOptions;
import org.universalimageloader.core.ImageLoader;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Request;

/**
 * 医生填写病历界面
 * Created by lmk on 15/9/22.
 */
public class DoctorWriteCaseActivity extends BaseActivity implements View.OnClickListener {
    private static final int TAKE_PICTURE = 0x000001;
    private static final int RECODE_FLAG = 1000;

    private TextView tvName, tvSex, tvAge, tvPhone, tvAddr, tvDesc;
    private LinearLayout patientPicLayout, patientVdoLayout, docPicLayout, docVideoLayout, caseItemsLayout;

    private JSONArray postJson;//上传的json字符数据
    private ArrayList<JSONObject> caseDatas;
    private boolean isUploading = false;
    private ScrollView mScrolview;
    private Button btnComplate, btnImgAdd;//完成
    private StringBuilder dePics = new StringBuilder();
    private HashMap<Integer, TextView> multipleTexts;//多选的结果返回应填入对应的EditText

    PopupWindow mPopupWindow, agepop, addPhotoPop;
    PopupWindow mMenuWindow;

    private EditText dateEdit;
    private String currentKey = "-1";
    private File storageFile = null;
    private ImageLoader mImageLoader;

    private ApiConnection.Param[] params;
    private String consultId = "", otherId, otherName;//会诊ID ,对方的ID
    private String phoneNum;
    private int INVITAT = 1;//是否邀请过门诊的状态
    private String INVITATINFO;//邀请门诊信息

    private boolean isEditing = true;
    private WaitDialog mLoadDialog;
    private DisplayImageOptions mOptions;//画廊异步读取操作

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.aty_doctor_write_case);
        currentKey = DoctorWriteCaseActivity.class.getSimpleName();//给图片集合赋值key,用来存和取
        ArrayList<ImageItem> itemImgs = new ArrayList<ImageItem>();//
        Bimp.dataMap.put(DoctorWriteCaseActivity.class.getSimpleName(), itemImgs);
        Bimp.imgMaxs.put(DoctorWriteCaseActivity.class.getSimpleName(), 32);
        mImageLoader = ImageLoader.getInstance();
        consultId = getIntent().getStringExtra("consultId");
        initView();
        initData();
    }

    private void initView() {
        initializeTitle();
        titleLeftBtn.setOnClickListener(this);
        titleTextV.setText("填写病历");
        titleRightBtn2.setText("沟通");
        titleRightBtn2.setVisibility(View.VISIBLE);
        titleRightBtn2.setOnClickListener(this);

        mOptions = DefaultConfigurationFactory.createApplyPicDisplayImageOptions(this);
        tvAddr = (TextView) findViewById(R.id.doctor_write_case_patient_addr2);
        tvName = (TextView) findViewById(R.id.doctor_write_case_patient_name);
        tvSex = (TextView) findViewById(R.id.doctor_write_case_patient_sex);
        tvAge = (TextView) findViewById(R.id.doctor_write_case_patient_age);
        tvPhone = (TextView) findViewById(R.id.doctor_write_case_patient_phone2);
        tvDesc = (TextView) findViewById(R.id.doctor_write_case_patient_desc2);

        patientPicLayout = (LinearLayout) findViewById(R.id.doctor_write_case_patient_picture_layout);
        patientVdoLayout = (LinearLayout) findViewById(R.id.doctor_write_case_patient_vdo_layout);
        docPicLayout = (LinearLayout) findViewById(R.id.doctor_write_case_pic_layout);
        docVideoLayout = (LinearLayout) findViewById(R.id.doctor_write_case_video_layout);
        mScrolview = (ScrollView) findViewById(R.id.doctor_write_case_scrollview);
        caseItemsLayout = (LinearLayout) findViewById(R.id.doctor_write_case_items_layout);
        btnComplate = (Button) findViewById(R.id.doctor_write_case_btn_upload);
        btnImgAdd = (Button) findViewById(R.id.doctor_write_case_pic_add);
        btnComplate.setOnClickListener(this);
        btnImgAdd.setOnClickListener(this);
        findViewById(R.id.doctor_write_case_video_add).setOnClickListener(this);
    }


    //初始化数据
    private void initData() {
        caseItemsLayout.removeAllViews();
        //MedicalRecordServlet?CONSULTATIONID=225539

        List<BasicNameValuePair> pairs = new ArrayList<>();
        pairs.add(new BasicNameValuePair("CONSULTATIONID", consultId));
        pairs.add(new BasicNameValuePair("CUSTID", LoginBusiness.getInstance().getLoginEntity().getId()));
        ApiService.doGetMedicalRecordServlet(pairs, new ApiCallback<String>() {
            @Override
            public void onError(Request request, Exception e) {

            }

            @Override
            public void onResponse(String response) {
                BaseBean baseBean = com.alibaba.fastjson.JSONObject.parseObject(response, BaseBean.class);
                if ("1".equals(baseBean.code)) {//成功
                    try {
                        JSONObject jsonObject = new JSONObject(baseBean.result);
                        JSONArray array = jsonObject.getJSONArray("CONTENT");
                        onBoundPatientInfo(jsonObject);
                        caseDatas = new ArrayList<JSONObject>();
                        postJson = new JSONArray();
                        if (array != null && array.length() > 0) {
                            for (int i = 0; i < array.length(); i++) {
                                postJson.put(JsonParseUtils.getPostTemplateObject(array.getJSONObject(i)));
                                caseDatas.add(array.getJSONObject(i));
                            }
                            onBoundData(caseDatas);//绑定数据到LinearLayout
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    ToastUtil.showShort(DoctorWriteCaseActivity.this, baseBean.message);
                }


            }
        }, this);


    }

    //患者信息
    private ArrayList<JSONObject> pPicList = new ArrayList<>();
    private ArrayList<JSONObject> pVideoList = new ArrayList<>();
    private ArrayList<JSONObject> pThumbnailList = new ArrayList<>();

    ArrayList<ImageItem> pVideosList = new ArrayList<>();//视频list类

    private void onBoundPatientInfo(JSONObject object) throws JSONException {
        pPicList.clear();
        pVideoList.clear();
        pThumbnailList.clear();
        pVideosList.clear();
        patientPicLayout.removeAllViews();
        patientVdoLayout.removeAllViews();

        otherId = object.optString("PATIENTID");
        otherName = object.optString("PATIENTNAME");
        tvName.setText(object.optString("CUSTNAME"));
        INVITAT = object.optInt("INVITAT");
        INVITATINFO = object.optString("INVITATINFO");
        final StringBuilder sb = new StringBuilder();
        if ("M".equals(object.optString("SEX")))
            tvSex.setText("男");
        else
            tvSex.setText("女");
        phoneNum = object.optString("PHONE");
        tvAge.setText(object.optString("AGE"));
        tvAddr.setText(object.optString("AREA"));
        tvPhone.setText(object.optString("PHONE"));
        tvDesc.setText(object.optString("CONDESC"));
        JSONArray array = object.getJSONArray("PICS");

        int count = array.length();

        try {
            patientPicLayout.removeAllViews();
            patientVdoLayout.removeAllViews();

            //资源准备
            for (int m = 0; m < count; m++) {
                JSONObject jsonObject = array.getJSONObject(m);
                if (AppData.PIC_TYPE.equals(jsonObject.optString("PIC_TYPE"))) {
                    pPicList.add(jsonObject);
                } else if (AppData.VIDEO_TYPE.equals(jsonObject.optString("PIC_TYPE"))) {
                    pVideoList.add(jsonObject);
                } else if (AppData.THUMBNAIL_TYPE.equals(jsonObject.optString("PIC_TYPE"))) {
                    pThumbnailList.add(jsonObject);
                }
            }

            int videoCount = pVideoList.size();
            if (videoCount > 0) {
                for (int i = 0; i < videoCount; i++) {
                    ImageItem imageItem = new ImageItem();
                    imageItem.pidId = pVideoList.get(i).optInt("ID");
                    imageItem.setThumbnailPath(pVideoList.get(i).optString("SMALL"));
                    imageItem.setImagePath(pVideoList.get(i).optString("BIG"));
                    if (pThumbnailList.size() >= videoCount) {
                        imageItem.thumbnailId = pThumbnailList.get(i).optInt("ID");
                        imageItem.set_thumbnailPath(pThumbnailList.get(i).optString("SMALL"));
                        imageItem.set_imagePath(pThumbnailList.get(i).optString("BIG"));
                    }
                    imageItem.isNetPic = true;
                    pVideosList.add(imageItem);
                }
            }
            //图片的适配
            if (count > 0) {
                findViewById(R.id.doctor_write_case_patient_picture).setVisibility(View.VISIBLE);
            } else {
                findViewById(R.id.doctor_write_case_patient_picture).setVisibility(View.GONE);
            }

            if (pPicList.size() > 0) {
                findViewById(R.id.hs_patient_pic).setVisibility(View.VISIBLE);
            } else {
                findViewById(R.id.hs_patient_pic).setVisibility(View.GONE);
            }

            if (pVideoList.size() > 0) {
                findViewById(R.id.hs_patient_vdo).setVisibility(View.VISIBLE);
            } else {
                findViewById(R.id.hs_patient_vdo).setVisibility(View.GONE);
            }
            String[] arrays = null;//病历图片
            //图片key集合
            arrays = new String[pPicList.size()];
            for (int t = 0; t < pPicList.size(); t++) {
                arrays[t] = pPicList.get(t).optString("BIG");
            }

            for (int i = 0; i < pPicList.size(); i++) {
                final int index = i;
                View view = LayoutInflater.from(this).inflate(R.layout.aty_applyform_gallery, patientPicLayout, false);
                ImageView img = (ImageView) view.findViewById(R.id.image_illpic);
                mImageLoader.displayImage(pPicList.get(i).optString("SMALL"), img, mOptions);
                final String[] finalArray = arrays;
                img.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(DoctorWriteCaseActivity.this, ImageGalleryActivity.class);
                        intent.putExtra(ImageGalleryActivity.URLS_KEY, finalArray);
                        intent.putExtra(ImageGalleryActivity.TYPE_KEY, 1);
                        intent.putExtra("type", 1);// 0,1单个,多个
                        intent.putExtra("position", index);
                        startActivity(intent);
                    }
                });
                patientPicLayout.addView(view);
            }
            for (int j = 0; j < pVideosList.size(); j++) {
                final int index = j;
                View view = LayoutInflater.from(this).inflate(R.layout.aty_applyform_gallery_video, patientVdoLayout, false);
                ImageView img = (ImageView) view.findViewById(R.id.image_illpic);
                String thumbnail = pVideosList.get(j).get_imagePath();
                if (!HStringUtil.isEmpty(thumbnail)) {
                    mImageLoader.displayImage(thumbnail, img, mOptions);
                } else {
                    img.setBackgroundResource(R.drawable.video_src_erral);
                }
                img.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Uri uri = Uri.parse(AppContext.getApiRepository().URL_DOWNLOAVIDEO + pVideosList.get(index).getImagePath());
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setDataAndType(uri, "video/mp4");
                        startActivity(intent);

                    }
                });
                patientVdoLayout.addView(view);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


//        int contentCount = array.length();
//        //如果有影像资料
//        if (contentCount > 0) {
//            findViewById(R.id.doctor_write_case_patient_picture).setVisibility(View.VISIBLE);
//            //准备图片数据
//            for (int m = 0; m < contentCount; m++) {
//                JSONObject object = array.getJSONObject(m);
//
//                if (AppData.PIC_TYPE.equals(object.optString("PIC_TYPE"))) {
//                    sb.append(object.optString("BIG") + ",");
//                    pPicList.add(object);
//                } else if (AppData.VIDEO_TYPE.equals(object.optString("PIC_TYPE"))) {
//                    pVideoList.add(object);
////                videoPathList.add(AppContext.getmRepository().URL_DOWNLOAVIDEO + imgArray.getJSONObject(m).optString("URL"));
//                } else if (AppData.THUMBNAIL_TYPE.equals(object.optString("PIC_TYPE"))) {
//                    pThumbnailList.add(object);
//                }
//            }
//            if (sb.length() > 0)
//                sb.deleteCharAt(sb.length() - 1);
//            //图片
//            if (pPicList.size() > 0) {
//                findViewById(R.id.hs_patient_pic).setVisibility(View.VISIBLE);
//                for (int k = 0; k < pPicList.size(); k++) {
//                    final int imgPosition = k;
//                    MessageImageView imageview = new MessageImageView(DoctorWriteCaseActivity.this);
//                    imageview.setLayoutParams(new ViewGroup.LayoutParams(DensityUtils.dip2px(DoctorWriteCaseActivity.this, 70), DensityUtils.dip2px(DoctorWriteCaseActivity.this, 70)));
//                    imageview.setScaleType(ImageView.ScaleType.CENTER_CROP);
//                    imageview.getDeleteImage().setVisibility(View.GONE);
//                    mImageLoader.displayImage(pPicList.get(k).optString("SMALL"), imageview.getImage());//加载小图片
//                    imageview.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            Intent intent = new Intent(DoctorWriteCaseActivity.this, ImageGalleryActivity.class);
//                            intent.putExtra(ImageGalleryActivity.URLS_KEY, sb.toString().split(","));
//                            intent.putExtra(ImageGalleryActivity.TYPE_KEY, 1);
//                            intent.putExtra("type", 1);// 0,1单个,多个
//                            intent.putExtra("position", imgPosition);
//                            startActivityForResult(intent, 100);
//                        }
//                    });
//                    patientPicLayout.addView(imageview);
//                }
//            }
//
//            //视频
//            if (pVideoList.size() > 0) {
//                findViewById(R.id.hs_patient_vdo).setVisibility(View.VISIBLE);
//                for (int m = 0; m < pPicList.size(); m++) {
//                    final int imgPosition = m;
//                    MessageThumbnailImageView imageview = new MessageThumbnailImageView(DoctorWriteCaseActivity.this);
//                    imageview.setLayoutParams(new ViewGroup.LayoutParams(DensityUtils.dip2px(DoctorWriteCaseActivity.this, 70), DensityUtils.dip2px(DoctorWriteCaseActivity.this, 70)));
//                    imageview.image.setScaleType(ImageView.ScaleType.CENTER_CROP);
//                    imageview.imageDelete.setVisibility(View.GONE);
//                    mImageLoader.displayImage(pThumbnailList.get(m).optString("SMALL"), imageview.getImage());//加载小图片
//                    imageview.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            Uri uri = Uri.parseDate(AppContext.getmRepository().URL_DOWNLOAVIDEO + pVideoList.get(imgPosition).optString("BIG"));
//                            Intent intent = new Intent(Intent.ACTION_VIEW);
//                            intent.setDataAndType(uri, "video/mp4");
//                            startActivity(intent);
//                        }
//                    });
//                    patientVdoLayout.addView(imageview);
//                }
//            }
//
//
//        } else {
//            findViewById(R.id.doctor_write_case_patient_picture).setVisibility(View.GONE);
//            findViewById(R.id.hs_patient_pic).setVisibility(View.GONE);
//            findViewById(R.id.hs_patient_vdo).setVisibility(View.GONE);
//        }


    }

    /**
     * 绑定LinearLayout数据
     */
    private void onBoundData(ArrayList<JSONObject> datas) {
        caseItemsLayout.setVisibility(View.VISIBLE);
        multipleTexts = new HashMap<Integer, TextView>();
        for (int i = 0; i < datas.size(); i++) {
            final int index = i;
            final JSONObject entity = datas.get(i);
            View itemView = LayoutInflater.from(this).inflate(R.layout.apt_consultion_case_item_text, null, true);
            TextView tvCategoryTitle = (TextView) itemView.findViewById(R.id.apt_case_template_item_title);
            TextView tvEditLeft = (TextView) itemView.findViewById(R.id.apt_case_template_item_name);
            final TextView tvRight = (TextView) itemView.findViewById(R.id.apt_case_template_item_text_right);//右边文本,
            TextView tvStar = (TextView) itemView.findViewById(R.id.apt_case_template_item_text_star);//必填选填标记
            final EditText editText = (EditText) itemView.findViewById(R.id.apt_case_template_item_edit_right);//右边的右输入框
            EditText editTextLeft = (EditText) itemView.findViewById(R.id.apt_case_template_item_edit_left);//右边的左输入框
            LinearLayout horLayout = (LinearLayout) itemView.findViewById(R.id.apt_case_template_item_text_layout);
            EditText bigEditText = (EditText) itemView.findViewById(R.id.apt_case_template_item_text_edit_big);//下面大输入框
            LinearLayout imgLayout = (LinearLayout) itemView.findViewById(R.id.apt_case_template_item_img_layout);
            LinearLayout images = (LinearLayout) itemView.findViewById(R.id.apt_case_template_item_images);
            final Button imgAdd = (Button) itemView.findViewById(R.id.apt_case_template_item_img_add);

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
                tvStar.setVisibility(View.VISIBLE);
            }
            switch (entity.optInt("ITEMTYPE")) {
                case 10://文字填写
                    tvEditLeft.setText(entity.optString("ITEMNAME"));
                    editText.setVisibility(View.VISIBLE);
                    tvRight.setVisibility(View.GONE);

                    break;
                case 20://单选

                    tvEditLeft.setText(entity.optString("ITEMNAME"));
                    tvRight.setVisibility(View.VISIBLE);
                    tvRight.setHint(getResources().getString(R.string.please_choise));
                    editText.setVisibility(View.GONE);
                    final ArrayList<Map<String, String>> selectors = JsonParseUtils.parseTemplateItemData(entity.optString("OPTION"));
                    tvRight.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            setSingleSelector(selectors, tvRight, index, false);
                        }
                    });

                    break;
                case 30://多选
                    tvEditLeft.setText(entity.optString("ITEMNAME"));
                    tvRight.setVisibility(View.VISIBLE);
                    tvRight.setHint(getResources().getString(R.string.please_choise));
                    editText.setVisibility(View.GONE);
                    multipleTexts.put(index, tvRight);
                    bigEditText.setFocusable(false);
                    bigEditText.setEnabled(false);
                    ArrayList<Map<String, Object>> list = JsonParseUtils.parseMultipleChoiseData(entity.optString("OPTION"));
                    tvRight.setOnClickListener(new MultipleChoiseClickListener(list, entity.optString("ITEMNAME"), index));

                    break;
                case 40://单数字填写
                    tvEditLeft.setText(entity.optString("ITEMNAME"));
                    editText.setVisibility(View.VISIBLE);
                    editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                    tvRight.setVisibility(View.GONE);
                    break;
                case 50://区域数字填写90~100
                    tvEditLeft.setText(entity.optString("ITEMNAME"));
                    editText.setVisibility(View.VISIBLE);
                    editTextLeft.setVisibility(View.VISIBLE);
                    editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                    tvRight.setVisibility(View.VISIBLE);
                    tvRight.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    tvRight.setText("~");
                    tvRight.setBackgroundColor(Color.TRANSPARENT);
                    tvRight.setTextColor(getResources().getColor(R.color.color_blue));
                    break;
                case 60://日期
                    tvEditLeft.setText(entity.optString("ITEMNAME"));
                    editText.setVisibility(View.VISIBLE);
                    editText.setHint(this.getResources().getString(R.string.please_choise));
                    editText.setFocusable(false);
                    editText.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            dateEdit = editText;
                            setAgeDate(index);
                        }
                    });
                    break;
                case 70://大文本域填写
                    tvEditLeft.setText(entity.optString("ITEMNAME"));
                    tvRight.setVisibility(View.GONE);
                    editText.setVisibility(View.GONE);
                    bigEditText.setVisibility(View.VISIBLE);
                    bigEditText.setHint(getResources().getString(R.string.please_input) + entity.optString("ITEMNAME"));
                    bigEditText.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            v.getParent().requestDisallowInterceptTouchEvent(true);
                            return false;
                        }
                    });
                    break;
                case 80://有小数点的情况
                    tvEditLeft.setText(entity.optString("ITEMNAME"));
                    editText.setVisibility(View.VISIBLE);
                    editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                    editText.addTextChangedListener(new TextWatcher() {

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                        }

                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count,
                                                      int after) {
                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                        }
                    });
                    tvRight.setVisibility(View.GONE);
                    break;
                case 90:
                    tvEditLeft.setText(entity.optString("ITEMNAME"));
                    tvRight.setVisibility(View.GONE);
                    editText.setVisibility(View.GONE);
                    break;
            }
            caseItemsLayout.addView(itemView);
        }

    }


    /**
     * 得到并且去验证用户输入的数据
     *
     * @return true表示可以上传   false 表示不可以上传
     */
    private boolean verifyData() {
        int[] location = new int[2];
        int[] scrLocation = new int[2];
        caseItemsLayout.getLocationInWindow(scrLocation);
        if (isUploading) {
            isUploading = false;
            return false;
        }
        isUploading = true;//正在上传
        try {
            for (int i = 0; i < postJson.length(); i++) {
                JSONObject object = caseDatas.get(i);
                JSONObject postObject = postJson.getJSONObject(i);

                switch (object.optInt("ITEMTYPE")) {
                    case 10://文本
                    case 40://单数字
                    case 80://带小数
                        EditText editresult = (EditText) caseItemsLayout.getChildAt(i).findViewById(R.id.apt_case_template_item_edit_right);
                        String input = editresult.getText().toString().trim();
                        if (input == null || "".equals(input)) {
                            if (object.optInt("NEFILL") != 1)//不是必填,可以直接跳过判断
                                continue;
                            editresult.getLocationInWindow(location);
                            mScrolview.scrollTo(location[0], location[1] - scrLocation[1] - 100);
                            ToastUtil.showShort("请正确输入" + object.optString("ITEMNAME"));
                            return false;
                        } else {
                            postObject.put("INFO", input);
                        }
                        break;
                    case 20://单选
                    case 30://多选
                        TextView tvRight = (TextView) caseItemsLayout.getChildAt(i).findViewById(R.id.apt_case_template_item_text_right);
                        String singlestr = postObject.optString("SELECTION");
                        if (singlestr == null || "".equals(singlestr)) {
                            if (object.optInt("NEFILL") != 1)//不是必填,可以直接跳过判断
                                continue;
                            tvRight.getLocationOnScreen(location);
                            mScrolview.scrollTo(location[0], location[1] - scrLocation[1] - 100);
                            ToastUtil.showShort("请正确输入" + object.optString("ITEMNAME"));
                            return false;
                        }
                        break;
                    case 50://区域文字
                        EditText editLeft = (EditText) caseItemsLayout.getChildAt(i).findViewById(R.id.apt_case_template_item_edit_left);
                        EditText editRight = (EditText) caseItemsLayout.getChildAt(i).findViewById(R.id.apt_case_template_item_edit_right);
                        String strLeft = editLeft.getText().toString().trim();
                        String strRight = editRight.getText().toString().trim();
                        if (HStringUtil.isEmpty(strLeft) || HStringUtil.isEmpty(strRight)) {//为空或空字符串
                            if (object.optInt("NEFILL") != 1)//不是必填,可以直接跳过判断
                                continue;
                            editLeft.getLocationInWindow(location);
                            mScrolview.scrollTo(location[0], location[1] - scrLocation[1] - 100);
                            ToastUtil.showShort("请正确输入" + object.optString("ITEMNAME"));
                            return false;
                        } else {
                            postObject.put("INFO", strLeft);
                            postObject.put("INFO2", strRight);
                        }
                        break;
                    case 60://日期
                        EditText tvdate = (EditText) caseItemsLayout.getChildAt(i).findViewById(R.id.apt_case_template_item_edit_right);
                        String datestr = tvdate.getText().toString().trim();
                        if (datestr == null || "".equals(datestr)) {
                            if (object.optInt("NEFILL") != 1)//不是必填,可以直接跳过判断
                                continue;
                            tvdate.getLocationInWindow(location);
                            mScrolview.scrollTo(location[0], location[1] - scrLocation[1] - 100);
                            ToastUtil.showShort("请正确输入" + object.optString("ITEMNAME"));
                            return false;
                        } else {
                            postObject.put("INFO", datestr);
                        }
                        break;
                    case 70://大文本域
                        EditText bigeditresult = (EditText) caseItemsLayout.getChildAt(i).findViewById(R.id.apt_case_template_item_text_edit_big);
                        String biginput = bigeditresult.getText().toString().trim();
                        if (biginput == null || "".equals(biginput)) {
                            if (object.optInt("NEFILL") != 1)//不是必填,可以直接跳过判断
                                continue;
                            bigeditresult.getLocationInWindow(location);
                            mScrolview.scrollTo(location[0], location[1] - scrLocation[1] - 100);
                            ToastUtil.showShort("请正确输入" + object.optString("ITEMNAME"));
                            return false;
                        } else {
                            postObject.put("INFO", biginput);
                        }
                        break;
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return true;
    }


    /**
     * 设置单选滑动选择器
     */
    private void setSingleSelector(final ArrayList<Map<String, String>> list, final TextView tv, final int index, final boolean isSex) {
        SystemUtils.hideSoftAnyHow(this);
        if (mPopupWindow != null && mPopupWindow.isShowing()) mPopupWindow.dismiss();
        mPopupWindow = WheelUtils.showSingleWheel(this, list, tv, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int index1 = (Integer) v.getTag(R.id.wheel_one);
                Map<String, String> map = list.get(index1);
                String name = map.get("name");
                if (!isSex) {
                    try {
                        postJson.getJSONObject(index).put("SELECTION", map.get("code"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                tv.setText(name);
            }
        });
    }


    /**
     * 设置出生日期
     */
    private void setAgeDate(final int index) {
        if (agepop == null) {
            agepop = WheelUtils.showThreeDateWheel(this, caseItemsLayout, this);
        } else if (agepop.isShowing()) {
            agepop.dismiss();
        } else {
//			agepop.showAsDropDown(view);
            agepop.showAtLocation(caseItemsLayout, Gravity.BOTTOM, 0, 0);
        }
    }


    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.title_back:
                onBackPressed();
//                intent=new Intent(this,DoctorEditCaseActivity.class);
//                intent.putExtra("id","225543");
//                startActivity(intent);
                break;
            case R.id.title_right2:
                showCommnicatePop();
                break;
            case R.id.doctor_write_case_btn_upload://点击提交
                if (verifyData()) {
                    btnComplate.setVisibility(View.INVISIBLE);
                    ApiConnection.Param param = new ApiConnection.Param("CUSTID", otherId);
//                    ApiConnection.Param param = new ApiConnection.Param("CUSTID", "3783");
                    ApiConnection.Param param1 = new ApiConnection.Param("RECORDID", "");
                    ApiConnection.Param param2 = new ApiConnection.Param("CONSULTATIONID", consultId);
                    ApiConnection.Param param3 = new ApiConnection.Param("CONTENT", postJson.toString());
                    ApiConnection.Param param4 = new ApiConnection.Param("DEPIC", dePics.toString());
                    params = new ApiConnection.Param[]{param, param1, param2, param3, param4};
                    DoubleBtnFragmentDialog.showDefault(getSupportFragmentManager(),
                            "您确定提交吗?", "取消", "确定", new DoubleBtnFragmentDialog.OnDilaogClickListener() {
                                @Override
                                public void onDismiss(DialogFragment fragment) {
                                    isUploading = false;
                                    btnComplate.setVisibility(View.VISIBLE);
                                }

                                @Override
                                public void onClick(DialogFragment fragment, View v) {
                                    doUpload();//执行上传
                                }

                            });
                }
                break;
            case R.id.doctor_write_case_pic_add:
                showuploadPopWindow();

                break;
            case R.id.wheel_sure_age:
                String[] str = (String[]) v.getTag();
//                if(dateEdit==editPatientBirthday){
//                    Calendar cc=Calendar.getInstance();
//                    int age=cc.get(Calendar.YEAR)-Integer.parseInt(str[0].substring(0, str[0].length()-1));//得到年领
//                    if (age<=0){
//                        ToastUtil.showShort(this,"请正确输入出生日期!");
//                        return;
//                    }
//                    editPatientAge.setText(age+"");
//                }
                dateEdit.setText(str[0].substring(0, str[0].length() - 1) + "-" + str[1].substring(0, str[1].length() - 1)
                        + "-" + str[2].substring(0, str[2].length() - 1));
                break;

            case R.id.cameraadd://相机
                if (addPhotoPop.isShowing()) {
                    addPhotoPop.dismiss();
                }
                photo();
                break;
            case R.id.cancel://取消
                if (addPhotoPop != null && addPhotoPop.isShowing()) {
                    addPhotoPop.dismiss();
                }
                break;
            case R.id.galleryadd://从相册获取
                if (addPhotoPop.isShowing()) {
                    addPhotoPop.dismiss();
                }
                intent = new Intent(this, AlbumActivity.class);
                intent.putExtra("key", currentKey);
                startActivity(intent);
                break;

            case R.id.doctor_write_case_video_add://影像资料
                if (videoImages.size() < 3) {
                    startActivityForResult(new Intent(this, RecordMadeAty.class), RECODE_FLAG);
                } else {
                    ToastUtil.showShort("最多只能上传3段视频");
                }
                break;

            case R.id.pop_communicate_first:
                jumpChat();//跳到聊天
                break;
            case R.id.pop_communicate_second:
                if (phoneNum != null && phoneNum.length() > 7) {
                    Intent phoneIntent = new Intent(
                            "android.intent.action.CALL", Uri.parse("tel:" + phoneNum));
                    // 启动
                    startActivity(phoneIntent);
                } else {
                    ToastUtil.showShort(DoctorWriteCaseActivity.this, "用户没有电话号码");
                }
                break;
            case R.id.pop_communicate_third://请他门诊
                SingleBtnFragmentDialog.showSinglebtnFloe(DoctorWriteCaseActivity.this, "请患者门诊流程", "您确定要预约患者到医院就诊吗?", "确定", new SingleBtnFragmentDialog.OnClickSureBtnListener() {
                    @Override
                    public void onClickSureHander() {
                        inviteClinic();

                    }
                }, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent mianzhen = new Intent(DoctorWriteCaseActivity.this, DoctorSeeServiceActivity.class);
                        mianzhen.putExtra("type", "3");
                        mianzhen.putExtra("titleName", "出诊设置");
                        startActivity(mianzhen);
                    }
                }).show();

                break;
        }
    }

    //请他门诊
    private void inviteClinic() {
        //InvitatClinicServlet?CONSULTATIONID=1111&CUSTID=ddd
        List<BasicNameValuePair> pairs = new ArrayList<>();
        pairs.add(new BasicNameValuePair("CONSULTATIONID", consultId));
        pairs.add(new BasicNameValuePair("CUSTID", LoginBusiness.getInstance().getLoginEntity().getId()));
        ApiService.doGetInvitatClinicServlet(pairs, new ApiCallback<String>() {
            @Override
            public void onError(Request request, Exception e) {

            }

            @Override
            public void onResponse(String response) {
                BaseBean baseBean = com.alibaba.fastjson.JSONObject.parseObject(response, BaseBean.class);
                if ("1".equals(baseBean.code)) {//成功
                    INVITAT = 0;
                    mMenuWindow.dismiss();
                } else if ("8".equals(baseBean.code)) {

                }
                ToastUtil.showShort(DoctorWriteCaseActivity.this, baseBean.message);


            }
        }, this);

    }

    /**
     * 私聊
     */
    private void jumpChat() {
        Intent intent = new Intent();
        intent.putExtra(Constant.Chat.CONSULTATION_ID, consultId);
        intent.putExtra(Constant.Chat.SINGLE_ID, otherId);
        intent.putExtra(Constant.Chat.SINGLE_NAME, otherName);
        intent.putExtra(Constant.Chat.OBJECT_TYPE, ObjectType.CONSULT);
        intent.setClass(this, ChatActivity.class);
        startActivity(intent);
    }

    //执行上传
    private void doUpload() {
        ArrayList<ImageItem> list = Bimp.dataMap.get(currentKey);//根据ID寻找
        int count1 = list.size();
        int count2 = videoImages.size();
        int count3 = videoImages.size();
        int count = count1 + count2 + count3;
//        int count = count1 + count2;
        File[] files = new File[count];
        String[] strs = new String[count];
        for (int i = 0; i < count1; i++) {
            if (!(list.get(i).isNetPic)) {//不是网络图片才上传
                strs[i] = i + ".jpg";
                files[i] = BitmapUtils.onGetDecodeFileByPath(this, list.get(i).getImagePath());

            }
        }
        try {
            for (int j = count1; j < count1 + count2; j++) {
                strs[j] = j + ".mp4";
                File mMediaFile = null;
                if (new File(videoImages.get(j - count1)).exists()) {
                    mMediaFile = new File(videoImages.get(j - count1));
                }
                files[j] = mMediaFile;
            }
            for (int x = count1 + count2; x < count; x++) {
                strs[x] = "[thumbnail]" + x + ".png";
                files[x] = FileUtils.saveChatPhotoBitmapToFile(PlayVideoActiviy.getVideoThumbnail(videoImages.get(x - count1 - count2)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        ApiService.doPostSaveOrEditMedicalRecordServlet(strs, files, params, new MyApiCallback<String>(this) {

            @Override
            public void onBefore(Request request) {
                super.onBefore(request);
                mLoadDialog = WaitDialog.showLodingDialog(getSupportFragmentManager(), getResources());
                mLoadDialog.setCancelable(false);
            }

            @Override
            public void onAfter() {
                super.onAfter();
                mLoadDialog.dismiss();

            }

            @Override
            public void onError(Request request, Exception e) {
                mLoadDialog.dismiss();

            }

            @Override
            public void onResponse(String response) {
                isEditing = false;
                BaseBean bb = com.alibaba.fastjson.JSONObject.parseObject(response, BaseBean.class);
                ToastUtil.showShort(DoctorWriteCaseActivity.this, bb.message);
                if ("1".equals(bb.code)) {
                    EventBus.getDefault().post(new MyEvent("refresh", 2));
                    Intent intent = new Intent(DoctorWriteCaseActivity.this, AtyOrderDetails.class);
                    intent.putExtra("CONID", Integer.valueOf(consultId));
                    intent.putExtra("BACK", 2);
                    startActivity(intent);
                    DoctorWriteCaseActivity.this.finish();
                }
            }
        }, this);
    }

    /**
     * 弹出上传图片的选择布局
     */
    public void showuploadPopWindow() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.interest_image_add_action, null);
        View mainView = inflater.inflate(R.layout.interest_content, null);
        if (addPhotoPop == null) {
            addPhotoPop = new PopupWindow(view, ViewGroup.LayoutParams.FILL_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            addPhotoPop.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        }
        WheelUtils.setPopeWindow(this, mainView, addPhotoPop);
        Button cameraAdd = (Button) view.findViewById(R.id.cameraadd);
        Button galleryAdd = (Button) view.findViewById(R.id.galleryadd);
        Button cancel = (Button) view.findViewById(R.id.cancel);

        cameraAdd.setOnClickListener(this);
        galleryAdd.setOnClickListener(this);
        cancel.setOnClickListener(this);
    }


    //相机
    public void photo() {
        storageFile = null;
        if (StorageUtils.isSDMounted()) {
            try {
                storageFile = StorageUtils.createCameraFile();
                Uri uri = Uri.fromFile(storageFile);
                Intent intent = CropUtils.createPickForCameraIntent(uri);
                startActivityForResult(intent, TAKE_PICTURE);
            } catch (Exception e) {
                ToastUtil.showLong(this, "系统相机异常");
            }
        } else {
            SingleBtnFragmentDialog.showDefault(getSupportFragmentManager(), "sdcard未加载");
        }
    }

    //更新显示图片
    private void update() {
        docPicLayout.removeAllViews();
        ArrayList<ImageItem> list = Bimp.dataMap.get(DoctorWriteCaseActivity.class.getSimpleName());
        for (int i = 0; i < list.size(); i++) {
            final int index = i;
            MessageImageView image = new MessageImageView(this);
            image.setLayoutParams(new ViewGroup.LayoutParams(DensityUtils.dip2px(this, 78), DensityUtils.dip2px(this, 78)));
            image.setScaleType(ImageView.ScaleType.CENTER_CROP);
            ImageItem ii = list.get(index);
            if (ii.isNetPic)//如果是网络图片
                mImageLoader.displayImage(list.get(i).thumbnailPath, image.getImage());
            else
                image.setImageBitmap(list.get(i).getBitmap());
            image.setDeleteListener(new View.OnClickListener() {//删除图片

                @Override
                public void onClick(View v) {
//                    if (dePics.length() == 0)
//                        dePics.append(Bimp.dataMap.get(currentKey).get(index).pidId);
//                    else
//                        dePics.append("," + Bimp.dataMap.get(currentKey).get(index).pidId);
                    Bimp.dataMap.get(DoctorWriteCaseActivity.class.getSimpleName()).remove(index);
                    update();
                }
            });
            image.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(DoctorWriteCaseActivity.this, GalleryActivity.class);
                    intent.putExtra("key", DoctorWriteCaseActivity.class.getSimpleName());
                    intent.putExtra("position", "1");
                    intent.putExtra("ID", index);
                    startActivity(intent);
                }
            });
            docPicLayout.addView(image);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TAKE_PICTURE:
                if (resultCode == Activity.RESULT_OK) {
                    if (storageFile != null) {
                        ImageItem takePhoto = new ImageItem();
                        takePhoto.setImagePath(storageFile.getAbsolutePath());
                        ArrayList<ImageItem> list = Bimp.dataMap.get(currentKey);
                        list.add(takePhoto);
                    }
                }
                break;
            case RECODE_FLAG://录制视频
                if (resultCode == Activity.RESULT_OK) {
                    String result = data.getExtras().getString("filePath");
                    videoImages.add(result);
                }
                updateVideo();
                break;
            default://多选返回
                if (resultCode == Activity.RESULT_OK) {
                    TextView multiText = multipleTexts.get(requestCode);
                    StringBuilder sb = new StringBuilder();
                    StringBuilder ids = new StringBuilder();
                    ArrayList<Map<String, Object>> res = (ArrayList<Map<String, Object>>) data.getSerializableExtra("result");
                    for (int i = 0; i < res.size(); i++) {
                        if ((Boolean) res.get(i).get("isChecked")) {
                            ids.append(res.get(i).get("code").toString() + ",");
                            sb.append(res.get(i).get("name") + "\t\t");
                        }
//					}else{
//						if((Boolean) res.get(i).get("isChecked")){
//							sb.append(+res.get(i).get("name"));
//						}
//					}
                    }
                    try {
                        if (ids.length() > 0)
                            ids.deleteCharAt(ids.length() - 1);
                        postJson.getJSONObject(requestCode).put("SELECTION", ids);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    multiText.setText(sb.toString().trim());
                    multiText.setOnClickListener(new MultipleChoiseClickListener(res, data.getStringExtra("title"), requestCode));

                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 作用是:多选跳转的点击监听器
     */
    class MultipleChoiseClickListener implements View.OnClickListener {
        ArrayList<Map<String, Object>> list;
        String name;
        int index;

        public MultipleChoiseClickListener(ArrayList<Map<String, Object>> list, String name, int index) {
            super();
            this.list = list;
            this.name = name;
            this.index = index;
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(DoctorWriteCaseActivity.this, TemplateItemMultipleChoiceActivity.class);
            intent.putExtra("list", list);
            intent.putExtra("title", name);
            startActivityForResult(intent, index);
        }
    }

    private void showCommnicatePop() {

        View view = getLayoutInflater().inflate(R.layout.pop_communicate_layout, null);
        view.findViewById(R.id.pop_communicate_first).setOnClickListener(this);
        view.findViewById(R.id.pop_communicate_second).setOnClickListener(this);
        TextView tvThird = (TextView) view.findViewById(R.id.pop_communicate_third);
        if (INVITAT == 1)
            tvThird.setOnClickListener(this);
        else
            tvThird.setTextColor(getResources().getColor(R.color.gray_color));
        mMenuWindow = new PopupWindow(view, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT, true);
        mMenuWindow.setTouchable(true);
        mMenuWindow.setBackgroundDrawable(new BitmapDrawable());
        mMenuWindow.setOutsideTouchable(true);
        mMenuWindow.setContentView(view);

        mMenuWindow.showAtLocation(titleRightBtn2, Gravity.TOP | Gravity.RIGHT, 0,
                titleRightBtn2.getMeasuredHeight() * 3);//居中且位于mImageView的下面显示
    }


    @Override
    public void onBackPressed() {
        if (isEditing) {
            DoubleBtnFragmentDialog.show(getSupportFragmentManager(), "提示", "病历尚未编辑完成，您确定要离开吗", "取消", "确定",
                    new DoubleBtnFragmentDialog.OnDilaogClickListener() {

                        @Override
                        public void onDismiss(DialogFragment fragment) {
                        }

                        @Override
                        public void onClick(DialogFragment fragment, View v) {
                            DoctorWriteCaseActivity.super.onBackPressed();
                        }
                    });
        } else
            super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        update();
        updateVideo();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    ArrayList<String> videoImages = new ArrayList<>();

    //更新显示图片
    private void updateVideo() {
        docVideoLayout.removeAllViews();


        for (int i = 0; i < videoImages.size(); i++) {
            final int index = i;
            MessageThumbnailImageView image = new MessageThumbnailImageView(this);
            image.setLayoutParams(new ViewGroup.LayoutParams(DensityUtils.dip2px(this, 78), DensityUtils.dip2px(this, 78)));
            image.setScaleType(ImageView.ScaleType.CENTER_CROP);
            image.setImageBitmap(PlayVideoActiviy.getVideoThumbnail(videoImages.get(i)));
            image.setDeleteListener(new View.OnClickListener() {//删除图片

                @Override
                public void onClick(View v) {
//                    if (dePics.length() == 0)
//                        dePics.append(Bimp.dataMap.get(currentKey).get(index).pidId);
//                    else
//                        dePics.append("," + Bimp.dataMap.get(currentKey).get(index).pidId);
                    videoImages.remove(index);
                    updateVideo();
                }
            });
            final int finalI = i;
            image.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    startActivity(new Intent(DoctorWriteCaseActivity.this, PlayVideoActiviy2.class).putExtra(PlayVideoActiviy2.KEY_FILE_PATH, videoImages.get(finalI)));
//                    Intent intent = new Intent(DoctorWriteCaseActivity.this, GalleryActivity.class);
//                    intent.putExtra("key", DoctorWriteCaseActivity.class.getSimpleName());
//                    intent.putExtra("position", "1");
//                    intent.putExtra("ID", index);
//                    startActivity(intent);
                }
            });
            docVideoLayout.addView(image);
        }
    }
}
