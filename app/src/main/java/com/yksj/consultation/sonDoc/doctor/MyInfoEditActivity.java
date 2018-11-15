package com.yksj.consultation.sonDoc.doctor;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.blankj.utilcode.util.AppUtils;
import com.library.base.base.BaseTitleActivity;
import com.library.base.utils.StorageUtils;
import com.yksj.consultation.app.AppContext;
import com.yksj.consultation.business.LoginBusiness;
import com.yksj.consultation.comm.CommonExplainActivity;
import com.yksj.consultation.comm.SingleBtnFragmentDialog;
import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.utils.CropUtils;
import com.yksj.healthtalk.bean.BaseBean;
import com.yksj.healthtalk.db.DictionaryHelper;
import com.yksj.healthtalk.entity.CustomerInfoEntity;
import com.yksj.healthtalk.net.http.ApiCallback;
import com.yksj.healthtalk.net.http.ApiConnection;
import com.yksj.healthtalk.net.http.ApiService;
import com.yksj.healthtalk.net.http.MyApiCallback;
import com.yksj.healthtalk.utils.BitmapUtils;
import com.yksj.healthtalk.utils.DataParseUtil;
import com.yksj.healthtalk.utils.HStringUtil;
import com.yksj.healthtalk.utils.PopWindowUtil;
import com.yksj.healthtalk.utils.SystemUtils;
import com.yksj.healthtalk.utils.ToastUtil;
import com.yksj.healthtalk.utils.WheelUtils;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import org.universalimageloader.core.ImageLoader;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Request;

/**
 * 医生端我的信息界面,可编辑
 * Created by lmk on 15/10/13.
 */
public class MyInfoEditActivity extends BaseTitleActivity implements View.OnClickListener {

    private EditText editName, mEditGetName, mEditGetTele, mEditCode, mEditName, mEditAddr;
    private TextView editHospital, editAddr, editOffice, editDocTitle;
    private ImageView imgRealPic, imgQualification;
    private ImageView indexSpecial, indexResume;
    private TextView editSpecial, editResume;
    private boolean specialExpanded = false, resumeExpanded = false;

    private CustomerInfoEntity mCusInfoEctity;
    private ImageLoader mInstance;
    private String docName, docDesc, docSpecial;
    private String strAddr, addrCode, strHospital, hospitalCode;
    private String officeCode, docTitleCode;
    private String hospitalName;//医院名称

    PopupWindow mPopupWindow, mAddressWindow;
    private List<Map<String, String>> mProList = null;//工作地点
    private List<Map<String, String>> officeList = null;//科室列表
    private Map<String, List<Map<String, String>>> mCityMap = new LinkedHashMap<String, List<Map<String, String>>>();
    private DictionaryHelper mDictionaryHelper;
    private List<Map<String, String>> mZhiChen;//职称数据
    private String editGetName, editGetTele, editBankName, editCode, editAddrs;

    private View mainView;
    private View wheelView;

    File mCameraFile;//相机获取照片
    File mHeaderFile;//头像
    File mCertificateFile;//证件照片

    @Override public int createLayoutRes() {
        return R.layout.aty_my_info_edit;
    }

    @Override public void initialize(Bundle bundle) {
        super.initialize(bundle);
        setTitle(R.string.edit_my_info);
        mInstance = ImageLoader.getInstance();
        mDictionaryHelper = DictionaryHelper.getInstance(this);
        setRight("保存", this::onSaveClick);
        initView();
        mCusInfoEctity = (CustomerInfoEntity) getIntent().getSerializableExtra("entity");
        if (mCusInfoEctity != null) {
            onBoundData();
        }
    }

    /**
     * 保存点击
     * @param v
     */
    public void onSaveClick(View v) {
        if (verifyData()) {
            postRegisterData();
        }
    }

    private void initView() {
        mainView = getLayoutInflater().inflate(R.layout.person_info_edit_layout, null);
        titleTextV.setText(R.string.edit_my_info);
        titleLeftBtn.setOnClickListener(this);
        editOffice = (TextView) findViewById(R.id.my_info_office);
        editName = (EditText) findViewById(R.id.my_info_name);
        editHospital = (TextView) findViewById(R.id.my_info_hospital);
        editAddr = (TextView) findViewById(R.id.my_info_addr);
        editDocTitle = (TextView) findViewById(R.id.my_info_doc_title);
        editSpecial = (TextView) findViewById(R.id.my_info_specialty_content);
        editResume = (TextView) findViewById(R.id.my_info_resume_content);
        indexSpecial = (ImageView) findViewById(R.id.my_info_specialty_index);
        indexResume = (ImageView) findViewById(R.id.my_info_resume_index);
        imgRealPic = (ImageView) findViewById(R.id.my_info_img_real);
        imgQualification = (ImageView) findViewById(R.id.my_info_img_qualification);
        mEditGetName = (EditText) findViewById(R.id.et_transfer_getname);
        mEditGetTele = (EditText) findViewById(R.id.et_transfer_gettele);
        mEditName = (EditText) findViewById(R.id.et_transfer_name);
        mEditCode = (EditText) findViewById(R.id.et_transfer_code);
        mEditAddr = (EditText) findViewById(R.id.et_transfer_addr);
        imgRealPic.setOnClickListener(this);
        imgQualification.setOnClickListener(this);
        indexResume.setOnClickListener(this);
        indexSpecial.setOnClickListener(this);
        editSpecial.setOnClickListener(this);
        editResume.setOnClickListener(this);
        editOffice.setOnClickListener(this);
        editDocTitle.setOnClickListener(this);
        editHospital.setOnClickListener(this);
        editAddr.setOnClickListener(this);

        wheelView = getLayoutInflater().inflate(R.layout.wheel, null);
        wheelView.findViewById(R.id.wheel_cancel).setOnClickListener(this);
        wheelView.findViewById(R.id.wheel_sure).setOnClickListener(this);
        mPopupWindow = new PopupWindow(wheelView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mAddressWindow = new PopupWindow(wheelView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    private void initData() {
        //DuoMeiHealth/ConsultationInfoSet?TYPE=findCustomerInfo&CUSTOMERID=
        List<BasicNameValuePair> pairs = new ArrayList<>();
        pairs.add(new BasicNameValuePair("TYPE", "findCustomerInfo"));
        pairs.add(new BasicNameValuePair("CUSTOMERID", LoginBusiness.getInstance().getLoginEntity().getId()));
        ApiService.doGetConsultationInfoSet(pairs, new ApiCallback<String>() {
            @Override
            public void onError(Request request, Exception e) {
            }

            @Override
            public void onResponse(String response) {
                try {
                    JSONObject obj = new JSONObject(response);
                    if ("1".equals(obj.optString("code"))) {
                        mCusInfoEctity = DataParseUtil.JsonToDocCustmerInfo(obj.getJSONObject("result"));
                        onBoundData();

                    } else {
                        ToastUtil.showShort(MyInfoEditActivity.this, obj.optString("message"));
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }, this);

    }

    private void onBoundData() {
        editOffice.setText(mCusInfoEctity.getOfficeName2());
        officeCode = mCusInfoEctity.getOfficeCode2();
        editAddr.setText(mCusInfoEctity.getDoctorWorkaddress());
        addrCode = mCusInfoEctity.getDoctorWorkaddressCode();
        editDocTitle.setText(mCusInfoEctity.getDoctorTitleName());
        docTitleCode = mCusInfoEctity.getDoctorTitle();
        editName.setText(mCusInfoEctity.getRealname());
        editHospital.setText(mCusInfoEctity.getHospital());
        hospitalCode = mCusInfoEctity.getHospitalCode();
        editSpecial.setText(mCusInfoEctity.getSpecial());
        addrCode = mCusInfoEctity.getDoctorWorkaddressCode();
        strAddr = mCusInfoEctity.getDoctorWorkaddress();
        docName = mCusInfoEctity.getDoctorName();
        hospitalName = mCusInfoEctity.getHospital();
        docSpecial = mCusInfoEctity.getSpecial();
        String str = mCusInfoEctity.getIntroduction();
        editResume.setText(str);
//        if (HStringUtil.isEmpty(mCusInfoEctity.getIntroduction())) {
//            findViewById(R.id.rl_jianjie).setVisibility(View.GONE);
//        }
//        if (HStringUtil.isEmpty(mCusInfoEctity.getSpecial())) {
//            findViewById(R.id.rl_special).setVisibility(View.GONE);
//        }
        docDesc = mCusInfoEctity.getIntroduction();
        mEditGetName.setText(mCusInfoEctity.getTransferGetName());
        mEditGetTele.setText(mCusInfoEctity.getTransferGetTele());
        mEditName.setText(mCusInfoEctity.getTransferName());
        mEditCode.setText(mCusInfoEctity.getTransferCode());
        mEditAddr.setText(mCusInfoEctity.getTransferAddr());
        if (!HStringUtil.isEmpty(mCusInfoEctity.getDoctorClientPicture())) {
            mInstance.displayImage(mCusInfoEctity.getDoctorClientPicture(), imgRealPic);
        }
        if (!HStringUtil.isEmpty(mCusInfoEctity.getDoctorCertificate())) {
            mInstance.displayImage(mCusInfoEctity.getDoctorCertificate(), imgQualification);
        }
    }


    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.my_info_addr:
                hideSoftBord();
                showCity();
                break;
            case R.id.my_info_hospital:
                if (addrCode == null || addrCode.length() < 2) {
                    ToastUtil.showShort(MyInfoEditActivity.this, "请先选择地区");
                    return;
                }
                queryHospital(addrCode);
                break;
            case R.id.my_info_office:
                hideSoftBord();
                onGetOfficeData();
                break;
            case R.id.my_info_doc_title:
                hideSoftBord();
                onShowZhichen();
                break;
            case R.id.my_info_specialty_content:
                intent = new Intent(this, CommonExplainActivity.class);
                intent.putExtra(CommonExplainActivity.TITLE_NAME, "专长");
                intent.putExtra(CommonExplainActivity.TEXT_CONUT, 1000);  //字数限制  默认是1000
                intent.putExtra(CommonExplainActivity.TEXT_CONTENT, editSpecial.getText().toString());  //内容
                startActivityForResult(intent, 221);
                break;
            case R.id.my_info_specialty_index:
                if (specialExpanded) {
                    specialExpanded = false;
                    editSpecial.setMaxLines(2);
                    indexSpecial.setImageResource(R.drawable.gengduos);
                } else {
                    specialExpanded = true;
                    editSpecial.setMaxLines(100);
                    indexSpecial.setImageResource(R.drawable.shouqis);
                }
                break;
            case R.id.my_info_resume_content:
                intent = new Intent(this, CommonExplainActivity.class);
                intent.putExtra(CommonExplainActivity.TITLE_NAME, "简介");
                intent.putExtra(CommonExplainActivity.TEXT_CONUT, 1000);  //字数限制  默认是1000
                intent.putExtra(CommonExplainActivity.TEXT_CONTENT, editResume.getText().toString());  //内容
                startActivityForResult(intent, 222);
                break;
            case R.id.my_info_resume_index:
                if (resumeExpanded) {
                    resumeExpanded = false;
                    editResume.setMaxLines(2);
                    indexResume.setImageResource(R.drawable.gengduos);
                } else {
                    resumeExpanded = true;
                    editResume.setMaxLines(100);
                    indexResume.setImageResource(R.drawable.shouqis);
                }
                break;
            case R.id.my_info_img_real:
                hideSoftBord();
                PopWindowUtil.showSelectPhoto(v, this, getLayoutInflater(), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!SystemUtils.getScdExit()) {
                            ToastUtil.showSDCardBusy();
                            return;
                        }
                        switch (v.getId()) {
                            case R.id.bendifenjian://本地
                                try {
                                    Intent intent = CropUtils.createPickForFileIntent();
                                    startActivityForResult(intent, 3000);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                break;
                            case R.id.paizhao://相机获取
                                try {
                                    mCameraFile = StorageUtils.createImageFile();
                                    Uri outUri = Uri.fromFile(mCameraFile);
                                    Intent intent = CropUtils.createPickForCameraIntent(outUri);
                                    startActivityForResult(intent, 3001);
                                } catch (Exception e) {
                                    ToastUtil.showLong(getApplicationContext(), "系统相机异常");
                                }
                                break;
                        }
                    }
                });
                break;
            case R.id.my_info_img_qualification:
                hideSoftBord();
                PopWindowUtil.showSelectPhoto(v, this, getLayoutInflater(), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!SystemUtils.getScdExit()) {
                            ToastUtil.showSDCardBusy();
                            return;
                        }
                        switch (v.getId()) {
                            case R.id.bendifenjian://本地
                                try {
                                    Intent intent = CropUtils.createPickForFileIntent();
                                    startActivityForResult(intent, 4000);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                break;
                            case R.id.paizhao://相机获取
                                try {
                                    mCameraFile = StorageUtils.createImageFile();
                                    Uri outUri = Uri.fromFile(mCameraFile);
                                    Intent intent = CropUtils.createPickForCameraIntent(outUri);
                                    startActivityForResult(intent, 4001);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                break;
                        }
                    }
                });
                break;
            case R.id.wheel_cancel:
                if (mAddressWindow != null)
                    mAddressWindow.dismiss();
                break;
            case R.id.wheel_sure:
                if (mAddressWindow != null)
                    mAddressWindow.dismiss();
                if (WheelUtils.getCurrent() != null) {
                    editAddr.setText(WheelUtils.getCurrent());
                    strAddr = WheelUtils.getCurrent();
                    addrCode = WheelUtils.getCode();
                }
                break;
        }
    }

    //验证医生信息
    private boolean verifyData() {
        docName = editName.getText().toString().trim();
        editBankName = mEditName.getText().toString().trim();
        editGetName = mEditGetName.getText().toString().trim();
        editGetTele = mEditGetTele.getText().toString().trim();
        editCode = mEditCode.getText().toString().trim();
        editAddrs = mEditAddr.getText().toString().trim();
        hospitalName = editHospital.getText().toString().trim();

        if (docName.length() < 2) {
            ToastUtil.showShort(MyInfoEditActivity.this, "请正确输入姓名");
            return false;
        }
        if (editAddr.getText().toString().trim().length() == 0 || addrCode == null) {
            ToastUtil.showShort(MyInfoEditActivity.this, "请选择工作地点");
            return false;
        }
        if (editHospital.getText().toString().trim().length() == 0 || hospitalCode == null) {
            ToastUtil.showShort(MyInfoEditActivity.this, "请选择所在医院");
            return false;
        }
        if (editOffice.getText().toString().trim().length() == 0 || officeCode == null) {
            ToastUtil.showShort(MyInfoEditActivity.this, "请选择科室");
            return false;
        }
        if (editDocTitle.getText().toString().trim().length() == 0 || docTitleCode == null) {
            ToastUtil.showShort(MyInfoEditActivity.this, "请选择职称");
            return false;
        }
        if (editSpecial.getText().toString().trim().length() == 0) {
            ToastUtil.showShort(MyInfoEditActivity.this, "请输入您的专长");
            return false;
        }
        if (editResume.getText().toString().trim().length() == 0) {
            String str = editResume.getText().toString().trim();
            ToastUtil.showShort(MyInfoEditActivity.this, "请输入您的简介");
            return false;
        }
        if (editGetName.length() == 0) {
            ToastUtil.showShort(MyInfoEditActivity.this, "请输入收款人姓名");
            return false;
        }
        if (editGetTele.length() == 0) {
            ToastUtil.showShort(MyInfoEditActivity.this, "请输入收款人电话");
            return false;
        }
        if (!isMobileNO(editGetTele)) {
            ToastUtil.showShort(MyInfoEditActivity.this, "请输入正确收款人电话");
            return false;
        }
        if (editCode.length() == 0) {
            ToastUtil.showShort(MyInfoEditActivity.this, "请输入银行卡账号");
            return false;
        }
        if (!checkBankCard(editCode)) {
            ToastUtil.showShort(MyInfoEditActivity.this, "请输入正确的银行卡账号");
            return false;
        }
        if (editBankName.length() == 0) {
            ToastUtil.showShort(MyInfoEditActivity.this, "请输入开户银行名称");
            return false;
        }
        if (editAddrs.length() == 0) {
            ToastUtil.showShort(MyInfoEditActivity.this, "请输入开户支行名称");
            return false;
        }
        return true;
    }


    private void postRegisterData() {
        org.json.JSONObject jsonObject = new org.json.JSONObject();
        try {
            jsonObject.put("CUSTOMERID", LoginBusiness.getInstance().getLoginEntity().getId());
            jsonObject.put("DOCTOR_REAL_NAME", docName);
            jsonObject.put("DOCTOR_OFFICE", officeCode);
            jsonObject.put("CONSULTATION_CENTER_ID", AppContext.APP_CONSULTATION_CENTERID);
            jsonObject.put("DOCTOR_TITLE", docTitleCode);
            jsonObject.put("DOCTOR_PICTURE", mCusInfoEctity.getDoctorBigPicture());
            jsonObject.put("ICON_DOCTOR_PICTURE", mCusInfoEctity.getDoctorClientPicture());
            jsonObject.put("DOCTOR_CERTIFICATE", mCusInfoEctity.getDoctorCertificate());
            jsonObject.put("UNIT_CODE", hospitalCode);

            jsonObject.put("DOCTOR_HOSPITAL", hospitalName);

            jsonObject.put("DOCTOR_SPECIALLY", docSpecial);
            jsonObject.put("WORK_LOCATION", addrCode);
            jsonObject.put("WORK_LOCATION_DESC", strAddr);
            jsonObject.put("INTRODUCTION", docDesc);
            jsonObject.put("INFO_VERSION", AppUtils.getAppVersionName());
            jsonObject.put("TRANSFER_GETNAME", editGetName);
            jsonObject.put("TRANSFER_GETTELE", editGetTele);
            jsonObject.put("TRANSFER_NAME", editBankName);
            jsonObject.put("TRANSFER_CODE", editCode);
            jsonObject.put("TRANSFER_ADDR", editAddrs);
            ApiConnection.Param param = new ApiConnection.Param("PARAMETER", jsonObject.toString());
            ApiConnection.Param[] params = new ApiConnection.Param[]{param};

            File[] files = new File[]{mHeaderFile, mCertificateFile};
            ApiService.doPostDoctorUpdate(new String[]{"doctorPicture", "doctorCertificate"}, files, params, new MyApiCallback<String>(this) {
                @Override
                public void onError(Request request, Exception e) {

                }

                @Override
                public void onResponse(String response) {
                    BaseBean bb = com.alibaba.fastjson.JSONObject.parseObject(response, BaseBean.class);
                    if ("1".equals(bb.code)) {
                        SingleBtnFragmentDialog.show(getSupportFragmentManager(), "六一健康", bb.message, "确定", new SingleBtnFragmentDialog.OnClickSureBtnListener() {
                            @Override
                            public void onClickSureHander() {
                                setResult(RESULT_OK);
//                                mCusInfoEctity.setRoldid(777);
                                MyInfoEditActivity.this.finish();
                            }
                        });
                    } else {
                        ToastUtil.showShort(MyInfoEditActivity.this, bb.message);
                    }
                }
            }, this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (!SystemUtils.getScdExit()) {
            ToastUtil.showSDCardBusy();
            return;
        }

        switch (requestCode) {
            case 4000://相册获取执照
                if (resultCode == RESULT_OK && data != null) {
                    String path = queryAlbumPath(data);
                    onHandlerPapers(path);
                }
                break;
            case 4001://相机获取执照
                if (resultCode == RESULT_OK) {
                    onHandlerPapers(mCameraFile.getAbsolutePath());
                }
                break;
            case 3000://相册获取
                if (resultCode == Activity.RESULT_OK && data != null) {
                    String path = queryAlbumPath(data);
                    onHandlerCropImage(path);
                }
                break;
            case 3001://相机获取
                if (resultCode == Activity.RESULT_OK) {
                    String strFilePath = mCameraFile.getAbsolutePath();
                    onHandlerCropImage(strFilePath);
                }
                mCameraFile = null;
                break;
            case 3002://图片裁剪返回
                if (resultCode == RESULT_OK) {
                    Bitmap bitmap = BitmapUtils.decodeBitmap(
                            mHeaderFile.getAbsolutePath(),
                            CropUtils.HEADER_WIDTH,
                            CropUtils.HEADER_HEIGTH);
//                    onReleaseBg(mHeaderImageV);
                    imgRealPic.setImageBitmap(bitmap);
                } else {
                    if (mHeaderFile != null) mHeaderFile.deleteOnExit();
                    mHeaderFile = null;
                }
                break;
            case 222://简介
                if (resultCode == RESULT_OK) {
                    docDesc = data.getStringExtra("content");
                    editResume.setText(docDesc);
                }
                break;
            case 221://专长
                if (resultCode == RESULT_OK) {
                    docSpecial = data.getStringExtra("content");
                    editSpecial.setText(docSpecial);
                }
                break;
            case 223://没有的医院
                if (resultCode == RESULT_OK) {
                    strHospital = data.getStringExtra("content");
                    editHospital.setText(strHospital);
                }
                break;
        }
    }

    /**
     * 处理证件照片
     * @param path
     */
    private void onHandlerPapers(String path) {
        Bitmap bitmap = BitmapUtils.decodeBitmap(
                path,
                CropUtils.PAPERS_HEIGTH,
                CropUtils.PAPERS_WIDTH);
        if (bitmap == null) {
            ToastUtil.showGetImageFail();
            return;
        }
        mCertificateFile = StorageUtils.createImageFile();
        boolean b = StorageUtils.saveImageOnImagsDir(bitmap, mCertificateFile);
        if (!b) {
            ToastUtil.showGetImageFail();
            return;
        }
//            onReleaseBg(mCertificateIamgeV);
        imgQualification.setImageBitmap(bitmap);
    }


    /**
     * 头像图片裁剪
     * @param path
     */
    private void onHandlerCropImage(String path) {
        if (!SystemUtils.getScdExit()) {
            ToastUtil.showSDCardBusy();
            return;
        }
        try {
            mHeaderFile = StorageUtils.createHeaderFile();
            Uri outUri = Uri.fromFile(new File(path));
            Uri saveUri = Uri.fromFile(mHeaderFile);
            Intent intent = CropUtils.createHeaderCropIntent(this, outUri, saveUri, true);
            startActivityForResult(intent, 3002);
        } catch (Exception e) {
            ToastUtil.showCreateFail();
        }
    }

    /**
     * 查询相册图片路径
     * @param data
     * @return
     */
    private String queryAlbumPath(Intent data) {
        if (data == null) return null;
        Uri uri = data.getData();
        String scheme = uri.getScheme();
        String strFilePath = null;//图片地址
        // url类型content or file
        if ("content".equals(scheme)) {
            strFilePath = getImageUrlByAlbum(uri);
        } else {
            strFilePath = uri.getPath();
        }
        return strFilePath;
    }

    /**
     * 根据uri查询相册所对应的图片地址
     * @param uri
     * @return
     */
    private String getImageUrlByAlbum(Uri uri) {
        String[] imageItems = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(uri, imageItems, null, null, null);
        int index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String path = cursor.getString(index);
        return path;
    }

    private void showCity() {
        if (mAddressWindow != null && mAddressWindow.isShowing()) {
            mAddressWindow.dismiss();
        }

        if (mProList == null || mCityMap == null) {
            mProList = DictionaryHelper.getInstance(MyInfoEditActivity.this).setCityData(
                    MyInfoEditActivity.this, mCityMap);
        }
        WheelUtils.setDoubleWheel(this, mProList, mCityMap, mainView, mAddressWindow,
                wheelView);
    }


    //根据地区查询医院
    private void queryHospital(String areaCode) {
        if (mPopupWindow != null && mPopupWindow.isShowing()) mPopupWindow.dismiss();
        //192.168.16.45:8899/DuoMeiHealth/ConsultationInfoSet?TYPE=findUnitByAreaCode&AREACODE=
        List<BasicNameValuePair> valuePairs = new ArrayList<>();
        valuePairs.add(new BasicNameValuePair("TYPE", "findUnitByAreaCode"));
        valuePairs.add(new BasicNameValuePair("AREACODE", areaCode));
        ApiService.doGetConsultationInfoSet(valuePairs, new ApiCallback<JSONObject>() {

            @Override
            public void onError(Request request, Exception e) {

            }

            @Override
            public void onResponse(org.json.JSONObject response) {
                if ("1".equals(response.optString("code"))) {
                    final ArrayList<Map<String, String>> datas = new ArrayList<Map<String, String>>();
                    try {
                        org.json.JSONArray array = response.getJSONArray("result");
                        for (int i = 0; i < array.length(); i++) {
                            HashMap<String, String> map = new HashMap<String, String>();
                            org.json.JSONObject obj = array.getJSONObject(i);
                            map.put("name", obj.optString("UNIT_NAME"));
                            map.put("code", "" + obj.optInt("UNIT_CODE"));
                            datas.add(map);
                        }

                        mPopupWindow = WheelUtils.showSingleWheel(MyInfoEditActivity.this, datas, editHospital, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                int index1 = (Integer) v.getTag(R.id.wheel_one);
                                Map<String, String> map = datas.get(index1);
                                String name = map.get("name");
                                hospitalCode = map.get("code");
                                if ("其他".equals(name)) {
                                    Intent intent = new Intent(MyInfoEditActivity.this, CommonExplainActivity.class);
                                    intent.putExtra(CommonExplainActivity.TITLE_NAME, "医院");
                                    intent.putExtra(CommonExplainActivity.TEXT_CONUT, 1000);  //字数限制  默认是1000
                                    startActivityForResult(intent, 223);
                                } else {
                                    editHospital.setText(name);

                                }

                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    ToastUtil.showShort(MyInfoEditActivity.this, response.optString("message"));
                }
            }
        }, this);
    }

    /**
     * 查询科室数据
     */
    private void onGetOfficeData() {
//        if (officeList == null || officeList.size() == 0) {
        //http://220.194.46.204/DuoMeiHealth/ConsultationInfoSet?TYPE=findConsultationOffice
        List<BasicNameValuePair> valuePairs = new ArrayList<>();
        valuePairs.add(new BasicNameValuePair("TYPE", "findAllOffice"));
        valuePairs.add(new BasicNameValuePair("CONSULTATION_CENTER_ID", AppContext.APP_CONSULTATION_CENTERID));
        ApiService.addHttpHeader("client_type", AppContext.CLIENT_TYPE);
        ApiService.doGetConsultationInfoSet(valuePairs, new ApiCallback<JSONObject>() {

            @Override
            public void onError(Request request, Exception e) {

            }

            @Override
            public void onResponse(org.json.JSONObject response) {
                if ("1".equals(response.optString("code"))) {
                    officeList = new ArrayList<Map<String, String>>();
                    try {
                        org.json.JSONArray array = response.getJSONArray("result");
                        for (int i = 0; i < array.length(); i++) {
                            HashMap<String, String> map = new HashMap<String, String>();
                            org.json.JSONObject obj = array.getJSONObject(i);
//                            if (AppContext.APP_CONSULTATION_CENTERID.equals(obj.optString("UPPER_OFFICE_ID"))) {
                            map.put("name", obj.optString("OFFICE_NAME"));
                            map.put("code", "" + obj.optString("OFFICE_CODE"));
                            officeList.add(map);
//                            }
                        }
                        showOffice();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    ToastUtil.showShort(MyInfoEditActivity.this, response.optString("message"));
                }
            }
        }, this);
//        } else {
//            showOffice();
//        }
    }

    private void showOffice() {
        mPopupWindow = WheelUtils.showSingleWheel(MyInfoEditActivity.this, officeList, editOffice, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int index1 = (Integer) v.getTag(R.id.wheel_one);
                Map<String, String> map = officeList.get(index1);
                String name = map.get("name");
                officeCode = map.get("code");
                editOffice.setText(name);
            }
        });

    }

    /**
     * 职称
     */
    private void onShowZhichen() {
        if (mZhiChen == null) {
            new KeShiQuery().execute(2);
        } else {
            if (mPopupWindow != null && mPopupWindow.isShowing())
                mPopupWindow.dismiss();
            mPopupWindow = WheelUtils.showSingleWheel(this, mZhiChen, editDocTitle, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int index1 = (Integer) v.getTag(R.id.wheel_one);
                    Map<String, String> map = mZhiChen.get(index1);
                    String name = map.get("name");
                    docTitleCode = map.get("code");
                    editDocTitle.setText(name);
                }
            });
        }
    }

    /**
     * 科室查询
     * @author origin
     */
    class KeShiQuery extends AsyncTask<Integer, Void, Void> {
        List<Map<String, String>> keshiList;
        List<Map<String, String>> zhiChen;
        int type = 0;

        @Override
        protected Void doInBackground(Integer... params) {
            type = params[0];
            LinkedHashMap<String, String> keshiData = new LinkedHashMap<String, String>();
            LinkedHashMap<String, String> titles = new LinkedHashMap<String, String>();

            keshiData.put(getString(R.string.limit_no), "5");
            keshiData = mDictionaryHelper.queryKeShi(getBaseContext());
            titles.clear();

            titles.put(getString(R.string.limit_no), "5");
            titles = mDictionaryHelper.querydoctorTitles(getBaseContext());
            titles.remove("未知");

            //科室数据
            keshiList = new ArrayList<Map<String, String>>();
            Set<String> set = keshiData.keySet();
            for (String string : set) {
                Map<String, String> map = new HashMap<String, String>();
                map.put("name", string);
                map.put("code", keshiData.get(string));
                keshiList.add(map);
            }
            zhiChen = new ArrayList<Map<String, String>>();
            set = titles.keySet();
            for (String string : set) {
                Map<String, String> map = new HashMap<String, String>();
                map.put("name", string);
                map.put("code", titles.get(string));
                zhiChen.add(map);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            mZhiChen = zhiChen;
            if (type == 2) {
                onShowZhichen();
            }
        }
    }

    /**
     * 隐藏输入键盘
     */
    private void hideSoftBord() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(MyInfoEditActivity.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * 校验银行卡卡号
     * @param cardId
     * @return
     */
    public static boolean checkBankCard(String cardId) {
        char bit = getBankCardCheckCode(cardId.substring(0, cardId.length() - 1));
        if (bit == 'N') {
            return false;
        }
        return cardId.charAt(cardId.length() - 1) == bit;
    }

    /**
     * 从不含校验位的银行卡卡号采用 Luhm 校验算法获得校验位
     * @param nonCheckCodeCardId
     * @return
     */
    public static char getBankCardCheckCode(String nonCheckCodeCardId) {
        if (nonCheckCodeCardId == null || nonCheckCodeCardId.trim().length() == 0
                || !nonCheckCodeCardId.matches("\\d+")) {
//如果传的不是数据返回N
            return 'N';
        }
        char[] chs = nonCheckCodeCardId.trim().toCharArray();
        int luhmSum = 0;
        for (int i = chs.length - 1, j = 0; i >= 0; i--, j++) {
            int k = chs[i] - '0';
            if (j % 2 == 0) {
                k *= 2;
                k = k / 10 + k % 10;
            }
            luhmSum += k;
        }
        return (luhmSum % 10 == 0) ? '0' : (char) ((10 - luhmSum % 10) + '0');
    }

    // 手機號碼驗證
    public static boolean isMobileNO(String mobiles) {
/**
 * 匹配以下开头的号码 移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188
 * 联通：130、131、132、152、155、156、185、186 电信：133、153、180、189、（1349卫通）
 * 增加183
 ***/
        Pattern p = Pattern
                .compile("^((13[0-9])|(15[^4,\\D])|(18[0,3,5-9]))\\d{8}$");
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }
}
