package com.yksj.consultation.login;

import android.app.Activity;
import android.app.Dialog;
import android.app.Service;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.blankj.utilcode.util.LogUtils;
import com.library.base.base.BaseActivity;
import com.yksj.consultation.app.AppContext;
import com.yksj.consultation.comm.CommonExplainActivity;
import com.yksj.consultation.comm.SingleBtnFragmentDialog;
import com.yksj.consultation.setting.SettingWebUIActivity;
import com.yksj.consultation.sonDoc.R;
import com.library.base.utils.ResourceHelper;
import com.yksj.healthtalk.db.DictionaryHelper;
import com.yksj.healthtalk.entity.CustomerInfoEntity;
import com.yksj.healthtalk.net.http.ApiService;
import com.yksj.healthtalk.net.http.JsonsfHttpResponseHandler;
import com.yksj.healthtalk.net.http.RequestParams;
import com.yksj.healthtalk.net.socket.SmartControlClient;
import com.yksj.healthtalk.utils.BitmapUtils;
import com.yksj.healthtalk.utils.DataParseUtil;
import com.yksj.healthtalk.utils.HStringUtil;
import com.yksj.healthtalk.utils.PopWindowUtil;
import com.yksj.healthtalk.utils.SharePreHelper;
import com.yksj.healthtalk.utils.SystemUtils;
import com.yksj.healthtalk.utils.ToastUtil;
import com.yksj.healthtalk.utils.ValidatorUtil;
import com.yksj.healthtalk.utils.WheelUtils;

import com.yksj.consultation.utils.CropUtils;
import org.universalimageloader.core.ImageLoader;
import com.library.base.utils.StorageUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class DoctorRegisteUI extends BaseActivity implements OnClickListener {

    private Map<String, List<Map<String, String>>> mKeshiLevel2;//二级菜单数据科室
    private List<Map<String, String>> mKeshiLevel1;//一级菜单数据科室

    private List<Map<String, String>> mZhiChen;//职称数据
    private DictionaryHelper mDictionaryHelper;

    private List<Map<String, String>> mProList = null;//工作地点
    private Map<String, List<Map<String, String>>> mCityMap = new LinkedHashMap<String, List<Map<String, String>>>();
    private View wheelView;
    private View mainView;

    String keshiLevel1Code;//一级科室
    String keshiLevel2Code;//二级科室

    String keshiLevel1Name;//一级科室
    String keshiLevel2Name;

    String zhichengCode;// 职称编码
    String addressName;//地区名字
    String addressCode;//地区编码

    PopupWindow mPopupWindow, mAddressWindow;
    ImageView mHeaderImageV;
    ImageView mCertificateIamgeV;

    EditText mNameEditText;
    TextView mAddressTextV;//工作地点
    EditText mHospitalEditText;
    EditText mSpecialEditText, mDutyEditText, mExperienceEditText;//专长职务经历
    CheckBox mArgmentBox;
    TextView mArgmentTextV;
    TextView mZhiChenTextV;//职称
    TextView mKeShi;//科室
    //EditText mPhoneEditText;
    EditText mBankAccount;//银行账号
    EditText mBankName;//开户银行
    //EditText mBanckAdress;//开户行地址
    EditText mBankCompany;//收款人
    EditText emailEditText;

    EditText mPhoneEditText2;
    EditText mMobileEditText;

    View mAddressView;
    String userId;

    File mCameraFile;//相机获取照片
    File mHeaderFile;//头像
    File mCertificateFile;//证件照片

    CustomerInfoEntity mInfoEntity;
    private Intent intent;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        setContentView(R.layout.doctor_registe_activity_layout);


        mDictionaryHelper = DictionaryHelper.getInstance(this);
        //注册进入
        if (getIntent().hasExtra("userid")) {
            userId = getIntent().getStringExtra("userid");
        } else {
            userId = SmartControlClient.getControlClient().getUserId();
            mInfoEntity = SmartControlClient.getControlClient().getInfoEntity();
//			mInfoEntity = SmartFoxClient.getLoginUserInfo();
        }

        initUI();

        initData();
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
                    onReleaseBg(mHeaderImageV);
                    mHeaderImageV.setImageBitmap(bitmap);
                } else {
                    if (mHeaderFile != null) mHeaderFile.deleteOnExit();
                    mHeaderFile = null;
                }
                break;
            case 222://医生专长
                if (resultCode == RESULT_OK) {
                    String content = data.getStringExtra("content");
                    mSpecialEditText.setText(content);
                    mSpecialEditText.setSelection(content.length());
                }
                break;
            case 221://职务
                if (resultCode == RESULT_OK) {
                    String content = data.getStringExtra("content");
                    mDutyEditText.setText(content);
                    mDutyEditText.setSelection(content.length());
                }
                break;
            case 220://经历
                if (resultCode == RESULT_OK) {
                    String content = data.getStringExtra("content");
                    mExperienceEditText.setText(content);
                    mExperienceEditText.setSelection(content.length());
                }
                break;
        }
    }

    /**
     * 查询相册图片路径
     *
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
     * 头像图片裁剪
     *
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
     * 处理证件照片
     *
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
        onReleaseBg(mCertificateIamgeV);
        mCertificateIamgeV.setImageBitmap(bitmap);
    }


    /**
     * 释放背景图
     */
    private void onReleaseBg(ImageView view) {
        BitmapDrawable drawable = (BitmapDrawable) view.getBackground();
        if (drawable == null) return;
        view.setBackgroundDrawable(null);
        drawable.setCallback(null);
        if (!drawable.getBitmap().isRecycled()) {
            drawable.getBitmap().recycle();
        }
        drawable = null;
        System.gc();
    }


    /**
     * 根据uri查询相册所对应的图片地址
     *
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

    @Override
    public void onClick(View v) {
        if (mInfoEntity != null) {
            //审核当中屏蔽所有点击事件
//			boolean isa=mInfoEntity.isAudit();
//			boolean isb=mInfoEntity.isFristAudit();
            if (mInfoEntity.isAudit()) {
//			if(mInfoEntity.isAudit()|| mInfoEntity.isFristAudit()){
                SingleBtnFragmentDialog.showDefault(getSupportFragmentManager(), "审核中的数据,您不可以修改或者点击");
                return;
            }
        }
        switch (v.getId()) {
            case R.id.title_right2:
                onChangeProfile();

                break;
            case R.id.doctor_icon://头像
                hideSoftBord();
                PopWindowUtil.showSelectPhoto(v, this, getLayoutInflater(), new OnClickListener() {
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
            case R.id.doctor_sum://执照
                hideSoftBord();
                PopWindowUtil.showSelectPhoto(v, this, getLayoutInflater(), new OnClickListener() {
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
            case R.id.work_city://地址
                hideSoftBord();
                showCity();
                break;
            case R.id.doctorZhiChengPop:
                hideSoftBord();
                onShowZhichen();
                break;
            case R.id.doctorKeShiPop://科室1
                hideSoftBord();
                onShowKeShi1();
                break;
            case R.id.agreetment:
                intent = new Intent(this, SettingWebUIActivity.class);
                intent.putExtra("title", "医生服务协议");
                intent.putExtra("url", ResourceHelper.getString(R.string.agent_path_2));
                startActivity(intent);
                break;

            case R.id.doctor_special:
            case R.id.doctor_special_view:
                intent = new Intent(this, CommonExplainActivity.class);
                intent.putExtra(CommonExplainActivity.TITLE_NAME, "专长");
                intent.putExtra(CommonExplainActivity.TEXT_CONUT, 1000);  //字数限制  默认是1000
                intent.putExtra(CommonExplainActivity.TEXT_CONTENT, mSpecialEditText.getText().toString());  //内容
                startActivityForResult(intent, 222);
                break;
            case R.id.doctor_duty_view:
            case R.id.doctor_duty://职务
                intent = new Intent(this, CommonExplainActivity.class);
                intent.putExtra(CommonExplainActivity.TITLE_NAME, "职务");
                intent.putExtra(CommonExplainActivity.TEXT_CONUT, 1000);  //字数限制  默认是1000
                intent.putExtra(CommonExplainActivity.TEXT_CONTENT, mDutyEditText.getText().toString());  //内容
                startActivityForResult(intent, 221);
                break;
            case R.id.doctor_experience://经历
            case R.id.doctor_experience_view:
                intent = new Intent(this, CommonExplainActivity.class);
                intent.putExtra(CommonExplainActivity.TITLE_NAME, "经历");
                intent.putExtra(CommonExplainActivity.TEXT_CONUT, 1000);  //字数限制  默认是1000
                intent.putExtra(CommonExplainActivity.TEXT_CONTENT, mExperienceEditText.getText().toString());  //内容
                startActivityForResult(intent, 220);
                break;
            case R.id.wheel_cancel:
                if (mAddressWindow != null)
                    mAddressWindow.dismiss();
                break;
            case R.id.wheel_sure:
                if (mAddressWindow != null)
                    mAddressWindow.dismiss();
                if (WheelUtils.getCurrent() != null) {
                    mAddressTextV.setText(WheelUtils.getCurrent());
                    addressName = WheelUtils.getCurrent();
                    addressCode = WheelUtils.getCode();
                }
                break;
        }
    }


    @Override
    protected Dialog onCreateDialog(int id, Bundle args) {
        final Dialog dialog = new Dialog(this, R.style.translucent_dialog);
        dialog.setContentView(R.layout.dialog_singlebtn_layout);
        TextView titleTxtV = (TextView) dialog.findViewById(R.id.dialog_title);
        TextView contentTxtV = (TextView) dialog.findViewById(R.id.dialog_note);
        Button button = (Button) dialog.findViewById(R.id.dialog_ok);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                onHandlerUploadComplete();
            }
        });
        titleTxtV.setText("提示");
        contentTxtV.setText("您的资质已经提交成功，我们将在3-5个工作日内对您的资质进行审核确定，并通过提示信息告知您审核结果");
        button.setText("知道了");
        return dialog;
    }

    private void initUI() {

        initializeTitle();

        titleLeftBtn.setOnClickListener(this);
        titleLeftBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        if (getIntent().hasExtra("title")) {
            String ll = getIntent().getStringExtra("title");
            setTitle(getIntent().getStringExtra("title"));
        } else {
            setTitle("医师资料");
        }
        setRight("提交", this);

        TextView textView = (TextView) findViewById(R.id.agreetment);
        textView.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);

        mainView = getLayoutInflater().inflate(R.layout.person_info_edit_layout, null);
        wheelView = getLayoutInflater().inflate(R.layout.wheel, null);
        wheelView.findViewById(R.id.wheel_cancel).setOnClickListener(this);
        wheelView.findViewById(R.id.wheel_sure).setOnClickListener(this);
        mPopupWindow = new PopupWindow(wheelView, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        mAddressWindow = new PopupWindow(wheelView, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

        mHeaderImageV = (ImageView) findViewById(R.id.doctor_icon);
        mHeaderImageV.setOnClickListener(this);
        mCertificateIamgeV = (ImageView) findViewById(R.id.doctor_sum);
        mCertificateIamgeV.setOnClickListener(this);
        mNameEditText = (EditText) findViewById(R.id.doctor_name);
        mAddressTextV = (TextView) findViewById(R.id.workcity_selete);//工作地点
        mHospitalEditText = (EditText) findViewById(R.id.doctor_hos);
        mSpecialEditText = (EditText) findViewById(R.id.doctor_special);
        mDutyEditText = (EditText) findViewById(R.id.doctor_duty);//职务
        mExperienceEditText = (EditText) findViewById(R.id.doctor_experience);//经历
        mArgmentBox = (CheckBox) findViewById(R.id.doctor_protocol_check);
        //mPhoneEditText = (EditText)findViewById(R.id.bank_tel);

        mBankAccount = (EditText) findViewById(R.id.pay_number);
        mBankName = (EditText) findViewById(R.id.bank_this);
        //mBanckAdress = (EditText)findViewById(R.id.bank_address);
        mBankCompany = (EditText) findViewById(R.id.pay_name);
        emailEditText = (EditText) findViewById(R.id.doctor_email);


        mPhoneEditText2 = (EditText) findViewById(R.id.doctor_tele);
        mMobileEditText = (EditText) findViewById(R.id.doctor_mobile);


        mArgmentTextV = (TextView) findViewById(R.id.agreetment);
        mArgmentTextV.setOnClickListener(this);

        mAddressView = findViewById(R.id.work_city);
        mAddressView.setOnClickListener(this);

        mZhiChenTextV = (TextView) findViewById(R.id.doctor_zhicheng);
        findViewById(R.id.doctorZhiChengPop).setOnClickListener(this);
        findViewById(R.id.doctor_special_view).setOnClickListener(this);
        findViewById(R.id.doctor_duty_view).setOnClickListener(this);//职务
        findViewById(R.id.doctor_experience_view).setOnClickListener(this);//经历

        findViewById(R.id.doctorKeShiPop).setOnClickListener(this);
        mKeShi = (TextView) findViewById(R.id.doctor_keshi);
        mSpecialEditText.setOnClickListener(this);
        mSpecialEditText.setKeyListener(null);
    }

    private void initData() {
        if (mInfoEntity != null) {
            //if (mInfoEntity.getRoldid() == 1)//医生从来没有注册
            //return;
            if (mInfoEntity.getRoldid() != 1) {
                if (mInfoEntity.isAudit() || mInfoEntity.isFristAudit()) {
                    titleTextV.setText("审核中");
                    mArgmentBox.setEnabled(false);
                }
            }
            if (mInfoEntity.isShow()) {
                titleRightBtn2.setVisibility(View.VISIBLE);
                titleRightBtn2.setText("提交");
                titleTextV.setText("医师资料");
                mArgmentBox.setEnabled(false);
            }
            mNameEditText.setText(mInfoEntity.getRealname());
            emailEditText.setText(mInfoEntity.getDoctorEmail());
            mPhoneEditText2.setText(mInfoEntity.getTelePhone());
            mMobileEditText.setText(mInfoEntity.getMobilePhone());
            mHospitalEditText.setText(mInfoEntity.getHospital());//所在医院
            mSpecialEditText.setText(mInfoEntity.getSpecial());//特长
            mZhiChenTextV.setText(mInfoEntity.getDoctorTitleName());//职称
            mKeShi.setText(mInfoEntity.getOfficeName1());//科室

            mBankName.setText(mInfoEntity.getTransferName());
            //mPhoneEditText.setText(mInfoEntity.getTransferGetTele());
            mBankAccount.setText(mInfoEntity.getTransferCode());
            //mBanckAdress.setText(mInfoEntity.getTransferAddr());
            mAddressTextV.setText(mInfoEntity.getDoctorWorkaddress());
            mBankCompany.setText(mInfoEntity.getTransferGetName());//transferName收款人getTransferGetName()

            if (mInfoEntity.isAudit() || mInfoEntity.isFristAudit()) {
                titleTextV.setText("审核中...");
                titleRightBtn2.setVisibility(View.GONE);
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                mNameEditText.setEnabled(false);
                emailEditText.setEnabled(false);
                mPhoneEditText2.setEnabled(false);
                mMobileEditText.setEnabled(false);
                mHospitalEditText.setEnabled(false);

                mSpecialEditText.setEnabled(false);
                mZhiChenTextV.setEnabled(false);
                mKeShi.setEnabled(false);
                mBankName.setEnabled(false);
                //mPhoneEditText.setEnabled(false);
                mBankAccount.setEnabled(false);
                //mBanckAdress.setEnabled(false);
                mAddressTextV.setEnabled(false);
                mBankCompany.setEnabled(false);
                //屏蔽所有事件
            }

            ImageLoader.getInstance().displayImage(mInfoEntity.getSex(), mInfoEntity.getDoctorClientPicture(), mHeaderImageV);
            ImageLoader.getInstance().displayImage("5", mInfoEntity.getDoctorCertificate(), mCertificateIamgeV);
        }
    }


    /**
     * 用户注册医师完成，跳转
     */
    private void onHandlerUploadComplete() {
        if (mInfoEntity == null) {
            if (getIntent().getIntExtra("JOIN", 235) == 352) {
                finish();
            } else {
                Intent intent = new Intent(this, UserRegisteComplet.class);
                intent.putExtras(getIntent().getExtras());
                startActivity(intent);
            }
        } else {
            onBackPressed();
        }
    }

    /**
     * 改变资料
     */
    private void onChangeProfile() {

        if (mInfoEntity == null && mHeaderFile == null) {
            ToastUtil.showToastPanl("请上传真实照片");
            return;
        } else if (mInfoEntity != null) {
            String str = mInfoEntity.getDoctorClientPicture();
            if (!(str != null && str.trim().length() > 0) && mHeaderFile == null) {
                ToastUtil.showToastPanl("请上传真实照片");
                return;
            }
        }

        if (TextUtils.isEmpty(mNameEditText.getText().toString())) {
            ToastUtil.showToastPanl("名字不能为空");
            return;
        }
        if (TextUtils.isEmpty(mHospitalEditText.getText().toString())) {
            ToastUtil.showToastPanl("所在医院不能为空");
            return;
        }
        //工作地点
        if (TextUtils.isEmpty(mAddressTextV.getText().toString())) {
            ToastUtil.showToastPanl("工作地点不能为空");
            return;
        }
        if (TextUtils.isEmpty(mKeShi.getText().toString())) {
            ToastUtil.showToastPanl("科室不能为空");
            return;
        }
        if (TextUtils.isEmpty(mZhiChenTextV.getText().toString())) {
            ToastUtil.showToastPanl("职称不能为空");
            return;
        }
        if (TextUtils.isEmpty(mSpecialEditText.getText().toString())) {
            ToastUtil.showToastPanl("专长不能为空");
            return;
        }
        //if(TextUtils.isEmpty(mDutyEditText.getText().toString())){
        //	ToastUtil.showToastPanl("职务不能为空");
        //	return;
        //	}
        //if(TextUtils.isEmpty(mExperienceEditText.getText().toString())){
        //	ToastUtil.showToastPanl("经历不能为空");
        //	return;
        //}
        if (mInfoEntity == null && mCertificateFile == null) {
            ToastUtil.showToastPanl("请上传证件照片");
            return;
        } else if (mInfoEntity != null) {
            String str = mInfoEntity.getDoctorCertificate();
            if (!(str != null && str.trim().length() > 0) && mCertificateFile == null) {
                ToastUtil.showToastPanl("请上传证件照片");
                return;
            }
        }
        if (TextUtils.isEmpty(emailEditText.getText().toString().trim())) {
            ToastUtil.showToastPanl("邮箱不能为空");
            return;
        }
        if (!ValidatorUtil.emailFormat(emailEditText.getText().toString())) {
            ToastUtil.showToastPanl("邮箱填写有误");
            return;
        }
        //if(!ValidatorUtil.emailFormat(emailEditText.getText().toString())){
        //	ToastUtil.showToastPanl("邮箱填写有误");
        //	return;
        //}

        if (HStringUtil.isEmpty(mPhoneEditText2.getText().toString())) {
            ToastUtil.showToastPanl("电话号码有误");
            return;
        }
        //if(!ValidatorUtil.PhoneRegular(mMobileEditText.getText().toString())){
        //	ToastUtil.showToastPanl("手机号码有误");
        //	return;
        //}
        if (!ValidatorUtil.checkMobile(mMobileEditText.getText().toString())) {
            ToastUtil.showToastPanl("手机号码有误");
            return;
        }

        //收款人(单位)
        if (TextUtils.isEmpty(mBankCompany.getText().toString())) {
            //mBankCompany.setError("不能为空");
            ToastUtil.showToastPanl("收款人不能为空");
            return;
        }

        if (TextUtils.isEmpty(mBankAccount.getText().toString())) {
            //mBankAccount.setError("不能为空");
            ToastUtil.showToastPanl("银行卡(账)号不能为空");
            return;
        }

        if (TextUtils.isEmpty(mBankName.getText().toString())) {
            //mBankName.setError("不能为空");
            ToastUtil.showToastPanl("开户银行不能为空");
            return;
        }

        if (!mArgmentBox.isChecked()) {
            ToastUtil.showToastPanl("你必须同意医生服务协议与隐私条款");
            return;

        }

        final JSONObject jsonObject = new JSONObject();
        jsonObject.put("customerId", userId);
        jsonObject.put("doctorRealName", mNameEditText.getText().toString());
        jsonObject.put("doctorEmail", emailEditText.getText().toString());
        jsonObject.put("doctorTelephone", mPhoneEditText2.getText().toString());
        jsonObject.put("doctorMobilePhone", mMobileEditText.getText().toString());
        jsonObject.put("doctorHospital", mHospitalEditText.getText().toString());
        jsonObject.put("doctorSpecially", mSpecialEditText.getText().toString());
        jsonObject.put("ACADEMIC_JOB", mDutyEditText.getText().toString());
        jsonObject.put("RESUME_CONTENT", mExperienceEditText.getText().toString());
        jsonObject.put("CONSULTATION_CENTER_ID", AppContext.APP_CONSULTATION_CENTERID);
        jsonObject.put("validMark", AppContext.APP_VALID_MARK);

        jsonObject.put("transferGetName", mBankCompany.getText().toString());
        if (mInfoEntity != null)//收款人联系电话
            jsonObject.put("transferGetTele", mInfoEntity.getTransferGetTele());
        else
            jsonObject.put("transferGetTele", "111");

        jsonObject.put("transferCode", mBankAccount.getText().toString());
        jsonObject.put("transferName", mBankName.getText().toString());

        if (mInfoEntity != null)//转账途径地址(开户行地址)，
            jsonObject.put("transferAddr", mInfoEntity.getTransferAddr());
        else
            jsonObject.put("transferAddr", "111");
        if (mInfoEntity != null) {
            jsonObject.put("doctorClientBackground", mInfoEntity.getDoctorClientPicture());
            jsonObject.put("doctorBigIconbackground", mInfoEntity.getDoctorBigPicture());
            jsonObject.put("doctorCertificate", mInfoEntity.getDoctorCertificate());
        }

        if (keshiLevel1Code != null) {
            jsonObject.put("doctorOffice2", keshiLevel1Code);//
        } else if (mInfoEntity != null) {
            if (mInfoEntity.getOfficeCode2() != null && mInfoEntity.getOfficeCode2().trim().length() > 0) {
                jsonObject.put("doctorOffice2", mInfoEntity.getOfficeCode2());//
            } else {
                ToastUtil.showToastPanl("科室不能为空");
                return;
            }
        }

        if (keshiLevel2Code != null) {
            jsonObject.put("doctorOffice", keshiLevel2Code);//
        } else if (mInfoEntity != null) {
            jsonObject.put("doctorOffice", mInfoEntity.getOfficeCode1());//
        }

        if (zhichengCode != null) {
            //jsonObject.put("doctorTitleCode",zhichengCode);//
            jsonObject.put("doctorTitle", zhichengCode);//
        } else if (mInfoEntity != null) {
            //jsonObject.put("doctorTitleCode",mInfoEntity.getDoctorTitle());//
            jsonObject.put("doctorTitle", mInfoEntity.getDoctorTitle());//
        }
        if (null != addressCode) {
            jsonObject.put("workLocation", addressCode);
            if (addressName != null) {
                if (mInfoEntity != null) {
                    if (mInfoEntity.getDoctorWorkaddress().equals(addressName)) {
                        jsonObject.put("workLocationName", mInfoEntity.getDoctorWorkaddress());
                    } else {
                        jsonObject.put("workLocationName", addressName);
                    }
                } else {
                    jsonObject.put("workLocationName", addressName);
                }
            } else {
                if (mInfoEntity != null) {
                    jsonObject.put("workLocationName", mInfoEntity.getDoctorWorkaddress());
                }
            }
        } else if (mInfoEntity != null) {
            jsonObject.put("workLocation", mInfoEntity.getDoctorWorkaddressCode());
            jsonObject.put("workLocationName", mInfoEntity.getDoctorWorkaddress());
        }

        RequestParams params = new RequestParams();
        try {
            //上传证件照片
            if (mCertificateFile != null) {
                params.put("doctorCertificate", mCertificateFile);
            } else {
                params.putNullFile("doctorCertificate", new File(""));//医生照片

            }
            //上传医生头像照片
            if (mHeaderFile != null) {
                params.put("doctorPicture", mHeaderFile);
            } else {
                params.putNullFile("doctorPicture", new File(""));
            }
        } catch (Exception e) {
            ToastUtil.showToastPanl("读取照片错误");
            return;
        }
        params.put("json", jsonObject.toString());
        //params.put("VALID_MARK", AppContext.APP_VALID_MARK);

        ApiService.doHttpDoctorQualificationConsultation(params, new JsonsfHttpResponseHandler(this) {
            @Override
            public void onSuccess(int statusCode, JSONObject response) {
                if (response.containsKey("error_message")) {
                    ToastUtil.showToastPanl(response.getString("error_message"));
                } else {
                    try {
                        CustomerInfoEntity entity = null;
                        String json = SharePreHelper.getLoginUserInfo();
                        if (json != null) {
                            JSONObject userJsonObject = JSON.parseObject(json);
                            userJsonObject.putAll(response);
                            SharePreHelper.saveLoginUserInfo(userJsonObject.toJSONString());
                            entity = DataParseUtil.jsonToCustomerInfo(userJsonObject.toString());
                        }

                        if (entity != null && mInfoEntity != null) {
                            mInfoEntity = entity;
                            SmartControlClient.getControlClient().setCustomerInfoEntity(mInfoEntity);
                            AppContext.getAppData().updateCacheInfomation(mInfoEntity);
                        }

                        showDialog(1);
                    } catch (Exception e) {
                        LogUtils.d("DDD", "Exception");
                    }
                }
            }
        });
    }

    /*
     *
     */
    private void showCity() {
        if (mAddressWindow != null && mAddressWindow.isShowing()) {
            mAddressWindow.dismiss();
        }

        if (addressName == null) {
            queryData();
        }

        if (mProList == null || mCityMap == null) {
        } else {
            WheelUtils.setDoubleWheel(this, mProList, mCityMap, mainView, mAddressWindow,
                    wheelView);
        }
    }

    /**
     * 职称
     */
    private void onShowZhichen() {
        if (mZhiChen == null) {
            new KeShiQuery().execute(2);
        } else {
            if (mPopupWindow != null && mPopupWindow.isShowing()) mPopupWindow.dismiss();
            mPopupWindow = WheelUtils.showSingleWheel(this, mZhiChen, mZhiChenTextV, new OnClickListener() {
                @Override
                public void onClick(View v) {
                    int index1 = (Integer) v.getTag(R.id.wheel_one);
                    Map<String, String> map = mZhiChen.get(index1);
                    String name = map.get("name");
                    zhichengCode = map.get("code");
                    mZhiChenTextV.setText(name);
                }
            });
        }
    }

    /**
     * 查询科室
     */
    private void onShowKeShi1() {
        if (mKeshiLevel1 == null) {
            ApiService.doHttpFindkeshi(new JsonsfHttpResponseHandler(this) {
                @Override
                public void onSuccess(int statusCode, JSONArray response) {
                    mKeshiLevel1 = new ArrayList<Map<String, String>>();
                    mKeshiLevel2 = new HashMap<String, List<Map<String, String>>>();
                    int size = response.size();
                    for (int i = 0; i < size; i++) {
                        JSONObject object = response.getJSONObject(i);
                        String code = object.getString("OFFICE_CODE");
                        String name = object.getString("OFFICE_NAME");
                        Map<String, String> map = new HashMap<String, String>();
                        map.put("name", name);
                        map.put("code", code);
                        mKeshiLevel1.add(map);
                        JSONArray array = object.getJSONArray("SUB_OFFICE");
                        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
                        mKeshiLevel2.put(name, list);
                        if (array != null) {
                            int size1 = array.size();
                            for (int j = 0; j < size1; j++) {
                                object = array.getJSONObject(j);
                                map = new HashMap<String, String>();
                                code = object.getString("OFFICE_CODE");
                                name = object.getString("OFFICE_NAME");
                                map.put("name", name);
                                map.put("code", code);
                                list.add(map);
                            }
                        }
                    }
                    onShowKeShi1();
                }
            });
        } else {
            if (mPopupWindow != null && mPopupWindow.isShowing())
                mPopupWindow.dismiss();
            mPopupWindow = WheelUtils.showDoubleWheel(this, mKeshiLevel1, mKeshiLevel2, mZhiChenTextV, new OnClickListener() {
                @Override
                public void onClick(View v) {
                    int index1 = (Integer) v.getTag(R.id.wheel_one);
                    int index2 = (Integer) v.getTag(R.id.wheel_two);
                    //已经科室编码
                    keshiLevel1Code = mKeshiLevel1.get(index1).get("code");
                    String name = mKeshiLevel1.get(index1).get("name");
                    String name2 = mKeshiLevel2.get(name).get(index2).get("name");
                    if (name2 == null) {
                        mKeShi.setText(name);
                    } else {
                        mKeShi.setText(name2);
                        //二级科室编码
                        keshiLevel2Code = mKeshiLevel2.get(name).get(index2).get("code");
                    }
                }
            });
        }
    }

    /**
     * 隐藏输入键盘
     */
    private void hideSoftBord() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Service.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(mSpecialEditText.getWindowToken(), 0);
    }

    /**
     * 城市查询
     *
     * @author origin
     */
    //	class CityQuery extends AsyncTask<Void, Void, Void>{
    //		List<Map<String, String>> proList = new ArrayList<Map<String,String>>();
    //		Map<String, List<Map<String, String>>> cityMap = new LinkedHashMap<String, List<Map<String,String>>>();
    //		@Override
    //		protected Void doInBackground(Void... params) {
    //			cityMap = new LinkedHashMap<String,List<Map<String,String>>>();
    //			proList = mDictionaryHelper.setCityData(DoctorRegisteUI.this,cityMap);
    //			return null;
    //		}
    //		@Override
    //		protected void onPostExecute(Void result) {
    //			mProList = proList;
    //			mCityMap = cityMap;
    //			showCity();
    //		}
    //	}
    private void queryData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                mProList = DictionaryHelper.getInstance(DoctorRegisteUI.this).setCityData(
                        DoctorRegisteUI.this, mCityMap);
            }
        }).start();
    }

    /**
     * 科室查询
     *
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

}
