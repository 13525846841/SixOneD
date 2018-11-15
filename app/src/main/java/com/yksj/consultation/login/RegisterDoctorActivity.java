package com.yksj.consultation.login;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.library.base.base.BaseTitleActivity;
import com.library.base.dialog.MessageDialog;
import com.library.base.dialog.SelectorDialog;
import com.library.base.imageLoader.ImageLoader;
import com.library.base.utils.RxChooseHelper;
import com.library.base.utils.StorageUtils;
import com.library.base.widget.SuperTextView;
import com.yksj.consultation.app.AppContext;
import com.yksj.consultation.bean.DoctorRegisterBean;
import com.yksj.consultation.bean.ResponseBean;
import com.yksj.consultation.comm.AddTextActivity;
import com.yksj.consultation.comm.CommonExplainActivity;
import com.yksj.consultation.dialog.WaitDialog;
import com.yksj.consultation.event.MyEvent;
import com.yksj.consultation.main.MainActivity;
import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.utils.CropUtils;
import com.yksj.healthtalk.db.DictionaryHelper;
import com.yksj.healthtalk.net.http.ApiCallback;
import com.yksj.healthtalk.net.http.ApiCallbackWrapper;
import com.yksj.healthtalk.net.http.ApiService;
import com.yksj.healthtalk.net.http.MyApiCallback;
import com.yksj.healthtalk.utils.BitmapUtils;
import com.yksj.healthtalk.utils.MD5Utils;
import com.yksj.healthtalk.utils.SharePreHelper;
import com.yksj.healthtalk.utils.StringFormatUtils;
import com.yksj.healthtalk.utils.SystemUtils;
import com.yksj.healthtalk.utils.ToastUtil;
import com.yksj.healthtalk.utils.WheelUtils;

import org.apache.http.message.BasicNameValuePair;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.functions.Consumer;
import okhttp3.Request;

/**
 * 注册医生填写资料界面
 * Created by lmk on 15/9/23.
 */
public class RegisterDoctorActivity extends BaseTitleActivity implements View.OnClickListener {

    @BindView(R.id.registe_doctor_edit_name) SuperTextView mNameStv;
    @BindView(R.id.registe_doctor_tv_addr) SuperTextView mAddressStv;
    @BindView(R.id.registe_doctor_tv_hospital) SuperTextView mHospitalStv;
    @BindView(R.id.registe_doctor_tv_office) SuperTextView mOfficeStv;
    @BindView(R.id.registe_doctor_tv_doctitle) SuperTextView mDoctitleStv;
    @BindView(R.id.registe_doctor_tv_special) SuperTextView mSpecialStv;
    @BindView(R.id.registe_doctor_tv_desc) SuperTextView mDescStv;
    @BindView(R.id.et_transfer_getname) SuperTextView mTransferGetnameStv;
    @BindView(R.id.et_transfer_gettele) SuperTextView mTransferGetteleStv;
    @BindView(R.id.et_transfer_code) SuperTextView mTransferCodeStv;
    @BindView(R.id.et_transfer_name) SuperTextView mTransferNameStv;
    @BindView(R.id.et_transfer_addr) SuperTextView mTransferAddressStv;

    private ImageView imgIcon, imgDocInfo;
    PopupWindow mPopupWindow, mAddressWindow;
    private List<Map<String, String>> mProList = null;//工作地点
    private List<Map<String, String>> officeList = null;//科室列表
    private Map<String, List<Map<String, String>>> mCityMap = new LinkedHashMap<String, List<Map<String, String>>>();

    private List<Map<String, String>> mKeshiLevel1;//一级菜单数据科室
    private Map<String, List<Map<String, String>>> mKeshiLevel2;//二级菜单数据科室

    private View mainView;
    private View wheelView;

    private DictionaryHelper mDictionaryHelper;
    private List<Map<String, String>> mZhiChen;//职称数据

    private String strAddr, addrCode, strHospital, hospitalCode;
    private String docSpecial, docDesc;
    private String officeName, officeCode, docTitleCode;
    private Bundle bundle;
    private File mCameraFile;//相机获取照片
    private File mHeaderFile;//头像
    private File mCertificateFile;//证件照片
    private String phone, psw, registerType;
    private WaitDialog mDialog, mLoadDialog;
    private int sureType;
    //医生图片选择监听
    private Consumer<String> mDoctorImgResult = new Consumer<String>() {
        @Override
        public void accept(String s) throws Exception {
            mHeaderFile = new File(s);
            ImageLoader.load(mHeaderFile).into(imgDocInfo);
        }
    };
    //医生执照图片选择监听
    private Consumer<String> mDoctorImgCredResult = new Consumer<String>() {
        @Override
        public void accept(String s) throws Exception {
            mCertificateFile = new File(s);
            ImageLoader.load(mCertificateFile).into(imgIcon);
        }
    };

    public static Intent getCallingIntent(Context context, String phone, String psw, String registerType) {
        Intent intent = new Intent(context, RegisterDoctorActivity.class);
        intent.putExtra("phone", phone);
        intent.putExtra("psw", psw);
        intent.putExtra("registerType", registerType);
        return intent;
    }

    @Override
    public int createLayoutRes() {
        return R.layout.aty_register_doctor;
    }

    @Override
    public void initialize(Bundle bundle) {
        super.initialize(bundle);
        setTitle("填写资料");
        setRight("完成", this::onRightClick);
        mDictionaryHelper = DictionaryHelper.getInstance(this);
        phone = getIntent().getStringExtra("phone");
        psw = getIntent().getStringExtra("psw");
        initView();
    }

    /**
     * 菜单点击
     * @param v
     */
    public void onRightClick(View v) {
        if (verifyData()) {
            requestRegisterData();
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initView() {
        bundle = this.getIntent().getExtras();
        if (getIntent().hasExtra("registerType")) {
            registerType = getIntent().getStringExtra("registerType");
        }

        imgIcon = (ImageView) findViewById(R.id.registe_doctor_img_icon);
        imgDocInfo = (ImageView) findViewById(R.id.registe_doctor_img_info);
        bundle = getIntent().getExtras();
        mainView = getLayoutInflater().inflate(R.layout.person_info_edit_layout, null);
        wheelView = getLayoutInflater().inflate(R.layout.wheel, null);
        wheelView.findViewById(R.id.wheel_cancel).setOnClickListener(this);
        wheelView.findViewById(R.id.wheel_sure).setOnClickListener(this);
        mPopupWindow = new PopupWindow(wheelView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mAddressWindow = new PopupWindow(wheelView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @OnClick(R.id.registe_doctor_edit_name)
    public void onName(View v) {
        startEdit(mNameStv, "请输入" + mNameStv.getLeftString(), mNameStv.getRightString());
    }

    @OnClick(R.id.registe_doctor_tv_addr)
    public void onAddress(View v) {
        KeyboardUtils.hideSoftInput(v);
        showCity();
    }

    @OnClick(R.id.registe_doctor_tv_hospital)
    public void onHospital(View v) {
        if (addrCode == null || addrCode.length() < 2) {
            ToastUtil.showShort(RegisterDoctorActivity.this, "请先选择地区");
            return;
        }
        queryHospital(addrCode);
    }

    @OnClick(R.id.registe_doctor_tv_office)
    public void onOffice(View v) {
        KeyboardUtils.hideSoftInput(v);
        onGetOfficeData();
    }

    @OnClick(R.id.registe_doctor_tv_doctitle)
    public void onDoctitle(View v) {
        KeyboardUtils.hideSoftInput(v);
        onShowZhichen();
    }

    @OnClick(R.id.registe_doctor_tv_special)
    public void onSpecial(View v) {
        startEdit(mSpecialStv, "请输入" + mSpecialStv.getLeftString(), mSpecialStv.getRightString());
    }

    @OnClick(R.id.registe_doctor_tv_desc)
    public void onDesc(View v) {
        startEdit(mDescStv, "请输入" + mDescStv.getLeftString(), mDescStv.getRightString());
    }

    @OnClick(R.id.et_transfer_getname)
    public void onTransferGetname(View v) {
        startEdit(mTransferGetnameStv, "请输入" + mTransferGetnameStv.getLeftString(), mTransferGetnameStv.getRightString());
    }

    @OnClick(R.id.et_transfer_gettele)
    public void onTransferGettele(View v) {
        startEdit(mTransferGetteleStv, "请输入" + mTransferGetteleStv.getLeftString(), mTransferGetteleStv.getRightString());
    }

    @OnClick(R.id.et_transfer_code)
    public void onTransferCode(View v) {
        startEdit(mTransferCodeStv, "请输入" + mTransferCodeStv.getLeftString(), mTransferCodeStv.getRightString());
    }

    @OnClick(R.id.et_transfer_name)
    public void onTransferName(View v) {
        startEdit(mTransferNameStv, "请输入" + mTransferNameStv.getLeftString(), mTransferNameStv.getRightString());
    }

    @OnClick(R.id.et_transfer_addr)
    public void onTransferAddress(View v) {
        startEdit(mTransferAddressStv, "请输入" + mTransferAddressStv.getLeftString(), mTransferAddressStv.getRightString());
    }

    @OnClick(R.id.registe_doctor_img_info)
    public void onDoctorImgInfo(View v) {
        doctorImgChoose(v, mDoctorImgResult);
    }

    @OnClick(R.id.registe_doctor_img_icon)
    public void onDoctorImgIcon(View v) {
        doctorImgChoose(v, mDoctorImgCredResult);
    }

    private void startEdit(SuperTextView view, String title, String content){
        AddTextActivity.from(this)
                       .setListener(new AddTextActivity.OnAddTextClickListener() {
                           @Override
                           public void onConfrimClick(View v, String content, AddTextActivity activity) {
                               view.setRightString(content);
                               KeyboardUtils.hideSoftInput(v);
                               activity.finish();
                           }
                       })
                       .setContent(content)
                       .setTitle(title)
                       .startActivity();
    }

    /**
     * 验证医生信息
     * @return
     */
    private boolean verifyData() {
        if (mNameStv.getRightString().trim().length() < 2) {
            ToastUtil.showShort(RegisterDoctorActivity.this, "请正确输入姓名");
            return false;
        }
        if (mAddressStv.getRightString().trim().length() == 0 || addrCode == null) {
            ToastUtil.showShort(RegisterDoctorActivity.this, "请选择工作地点");
            return false;
        }
        if (mHospitalStv.getRightString().trim().length() == 0 || hospitalCode == null) {
            ToastUtil.showShort(RegisterDoctorActivity.this, "请选择所在医院");
            return false;
        }
        if (mOfficeStv.getRightString().trim().length() == 0 || officeCode == null) {
            ToastUtil.showShort(RegisterDoctorActivity.this, "请选择科室");
            return false;
        }
        if (mDoctitleStv.getRightString().trim().length() == 0 || docTitleCode == null) {
            ToastUtil.showShort(RegisterDoctorActivity.this, "请选择职称");
            return false;
        }
        if (mSpecialStv.getRightString().trim().length() == 0) {
            ToastUtil.showShort(RegisterDoctorActivity.this, "请输入您的专长");
            return false;
        }
        if (mDescStv.getRightString().trim().length() == 0) {
            ToastUtil.showShort(RegisterDoctorActivity.this, "请输入您的简介");
            return false;
        }
        if (mHeaderFile == null) {
            ToastUtil.showShort(RegisterDoctorActivity.this, "请上传本人真实照片");
            return false;
        }
        if (mCertificateFile == null) {
            ToastUtil.showShort(RegisterDoctorActivity.this, "请上传本人医师证照片");
            return false;
        }
        if (mTransferGetnameStv.getRightString().length() == 0) {
            ToastUtil.showShort(RegisterDoctorActivity.this, "请输入收款人姓名");
            return false;
        }
        if (mTransferGetteleStv.getRightString().length() == 0) {
            ToastUtil.showShort(RegisterDoctorActivity.this, "请输入收款人电话");
            return false;
        }
        if (!StringFormatUtils.isPhoneNum(mTransferGetteleStv.getRightString())) {
            ToastUtil.showShort("请输入正确的手机号");
            return false;
        }
        if (mTransferCodeStv.getRightString().length() == 0) {
            ToastUtil.showShort(RegisterDoctorActivity.this, "请输入银行卡账号");
            return false;
        }
        if (mTransferGetnameStv.getRightString().length() == 0) {
            ToastUtil.showShort(RegisterDoctorActivity.this, "请输入开户银行名称");
            return false;
        }
        if (mTransferAddressStv.getRightString().length() == 0) {
            ToastUtil.showShort(RegisterDoctorActivity.this, "请输入开户支行名称");
            return false;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.wheel_cancel:
                if (mAddressWindow != null)
                    sureType = 0;
                mAddressWindow.dismiss();
                break;
            case R.id.wheel_sure:
                if (mAddressWindow != null)
                    mAddressWindow.dismiss();
                if (WheelUtils.getCurrent() != null) {
                    mAddressStv.setRightString(WheelUtils.getCurrent());
                    strAddr = WheelUtils.getCurrent();
                    addrCode = WheelUtils.getCode();
                }
                if (sureType == 1) {
                    mHospitalStv.setRightString("");
                    mOfficeStv.setRightString("");
                    sureType = 0;
                }
                break;
        }
    }

    /**
     * 医师照片选择
     * @param v
     */
    private void doctorImgChoose(View v, Consumer consumer) {
        KeyboardUtils.hideSoftInput(v);
        SelectorDialog.newInstance(new String[]{"本地照片", "拍照"})
                      .setOnItemClickListener(new SelectorDialog.OnMenuItemClickListener() {
                          @Override
                          public void onItemClick(SelectorDialog dialog, int position) {
                              switch (position) {
                                  case 0:
                                      RxChooseHelper.chooseImage(RegisterDoctorActivity.this)
                                                    .subscribe(consumer);
                                      break;
                                  case 1:
                                      RxChooseHelper.captureImage(RegisterDoctorActivity.this)
                                                    .subscribe(consumer);
                                      break;
                              }
                          }
                      })
                      .show(getSupportFragmentManager());
    }

    /**
     * 输入医院的判断
     */
    private void queryHos() {
        if (addrCode == null || addrCode.length() < 2) {
            ToastUtil.showShort(RegisterDoctorActivity.this, "请先选择地区");
            return;
        }
        queryHospital(addrCode);
    }

    private void requestRegisterData() {
        org.json.JSONObject jsonObject = new org.json.JSONObject();
        try {
            jsonObject.put("PASSWORD", psw);
            jsonObject.put("CONSULTATION_CENTER_ID", AppContext.APP_CONSULTATION_CENTERID);
            jsonObject.put("PHONENUM", phone);
            jsonObject.put("DOCTOR_REAL_NAME", mNameStv.getRightString());
            jsonObject.put("DOCTOR_OFFICE", officeCode);
            jsonObject.put("DOCTOR_TITLE", docTitleCode);
            jsonObject.put("DOCTOR_PICTURE", "");
            jsonObject.put("ICON_DOCTOR_PICTURE", "");
            jsonObject.put("DOCTOR_CERTIFICATE", "");
            jsonObject.put("UNIT_CODE", hospitalCode);
            jsonObject.put("DOCTOR_HOSPITAL", mHospitalStv.getRightString());


            jsonObject.put("DOCTOR_SPECIALLY", docSpecial);
            jsonObject.put("WORK_LOCATION", addrCode);
            jsonObject.put("WORK_LOCATION_DESC", strAddr);
            jsonObject.put("INTRODUCTION", docDesc);

            //银行卡信息
            jsonObject.put("TRANSFER_NAME", mTransferNameStv.getRightString());//转账途径名称
            jsonObject.put("TRANSFER_ADDR", mTransferAddressStv.getRightString());//开户行支行
            jsonObject.put("TRANSFER_CODE", mTransferCodeStv.getRightString());//银行转账账号
            jsonObject.put("TRANSFER_GETNAME", mTransferGetnameStv.getRightString());//收款人开户名
            jsonObject.put("TRANSFER_GETTELE", mTransferGetteleStv.getRightString());//收款人电话

            if (!TextUtils.isEmpty(registerType)) {
                jsonObject.put("FLAG", registerType);
            }
            if (bundle != null) {
                jsonObject.put("PLATFORM_NAME", bundle.getString("PLATFORM_NAME"));
                jsonObject.put("EXPIRESIN", String.valueOf(bundle.getLong("EXPIRESIN")));
                jsonObject.put("EXPIRESTIME", String.valueOf(bundle.getLong("EXPIRESTIME")));
                jsonObject.put("TOKEN", bundle.getString("TOKEN"));
                jsonObject.put("TOKENSECRET", bundle.getString("TOKENSECRET"));
                jsonObject.put("USERGENDER", bundle.getString("USERGENDER"));
                jsonObject.put("USERICON", bundle.getString("USERICON"));
                jsonObject.put("USERNAME", bundle.getString("USERNAME"));
                jsonObject.put("USERID", bundle.getString("USERID"));
            }

            jsonObject.put("INFO_VERSION", AppUtils.getAppVersionName());

            ApiService.doctorRegister(mHeaderFile, mCertificateFile, jsonObject.toString(), new ApiCallbackWrapper<ResponseBean<DoctorRegisterBean>>(true) {
                @Override
                public void onResponse(ResponseBean<DoctorRegisterBean> resp) {
                    super.onResponse(resp);
                    if (resp.isSuccess()) {//成功
                        MessageDialog.newInstance("提示", resp.message)
                                     .addListener(new MessageDialog.SimpleMessageDialogListener() {
                                         @Override
                                         public void onPositiveClick(MessageDialog dialog, View v) {
                                             psw = MD5Utils.getMD5(psw);
                                             EventBus.getDefault().post(new MyEvent("", 12));
                                             RegisterDoctorActivity.this.finish();
                                         }
                                     })
                                     .show(getSupportFragmentManager());
                    } else {
                        ToastUtil.showShort(resp.message);
                    }
                }

                @Override
                public void onError(Request request, Exception e) {
                    super.onError(request, e);
                    LogUtils.e(e);
                    ToastUtils.showShort("注册失败");
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 登陆之后,会调用此方法
     * @param log
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(MyEvent log) {
        mDialog.dismissAllowingStateLoss();
        if (log.code == 1) {//登陆成功
            ToastUtil.showShort("登录成功");
            Intent intent = new Intent(RegisterDoctorActivity.this, MainActivity.class);
            intent.putExtra("isFromLogin", true);
//            intent.putExtras(((Intent) msg.obj).getExtras());
            startActivity(intent);
            if (mDialog != null && mDialog.isShowing()) {
                mDialog.dismissAllowingStateLoss();
                mDialog = null;
            }
            SharePreHelper.updateLoginState(true);
            finish();
        } else if (log.code == 0) {//登陆失败
            ToastUtil.showShort(log.what);
        }
    }

    /*
     *
     */
    private void showCity() {
        if (mAddressWindow != null && mAddressWindow.isShowing()) {
            mAddressWindow.dismiss();
        }

        if (mProList == null || mCityMap == null) {
            mProList = DictionaryHelper.getInstance(RegisterDoctorActivity.this).setCityData(
                    RegisterDoctorActivity.this, mCityMap);
        }
        WheelUtils.setDoubleWheel(this, mProList, mCityMap, mainView, mAddressWindow,
                                  wheelView);
        sureType = 1;
    }

    //根据地区查询医院
    private void queryHospital(String areaCode) {
        if (mPopupWindow != null && mPopupWindow.isShowing()) mPopupWindow.dismiss();
        //192.168.16.45:8899/DuoMeiHealth/ConsultationInfoSet?TYPE=findUnitByAreaCode&AREACODE=
        List<BasicNameValuePair> valuePairs = new ArrayList<>();
        valuePairs.add(new BasicNameValuePair("TYPE", "findUnitByAreaCode"));
        valuePairs.add(new BasicNameValuePair("AREACODE", areaCode));
        ApiService.doGetConsultationInfoSet(valuePairs, new MyApiCallback<org.json.JSONObject>(this) {

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

                        mPopupWindow = WheelUtils.showSingleWheel(RegisterDoctorActivity.this, datas, mHospitalStv, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                int index1 = (Integer) v.getTag(R.id.wheel_one);
                                Map<String, String> map = datas.get(index1);
                                String name = map.get("name");
                                hospitalCode = map.get("code");
                                if ("其他".equals(name)) {
                                    Intent intent = new Intent(RegisterDoctorActivity.this, CommonExplainActivity.class);
                                    intent.putExtra(CommonExplainActivity.TITLE_NAME, "医院");
                                    intent.putExtra(CommonExplainActivity.TEXT_CONUT, 1000);  //字数限制  默认是1000
                                    startActivityForResult(intent, 223);
                                } else {
                                    mHospitalStv.setRightString(name);
                                    mOfficeStv.setRightString("");
                                }

                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    ToastUtil.showShort(RegisterDoctorActivity.this, response.optString("message"));
                }
            }
        }, this);
    }


    /**
     * 查询科室数据
     */
    private void onGetOfficeData() {
        if (officeList == null || officeList.size() == 0) {
            //http://220.194.46.204/DuoMeiHealth/ConsultationInfoSet?TYPE=findConsultationOffice
            List<BasicNameValuePair> valuePairs = new ArrayList<>();
            valuePairs.add(new BasicNameValuePair("TYPE", "findAllOffice"));
            ApiService.addHttpHeader("client_type", AppContext.CLIENT_TYPE);
            ApiService.doGetConsultationInfoSet(valuePairs, new ApiCallback<org.json.JSONObject>() {

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
//                                if (AppContext.APP_CONSULTATION_CENTERID.equals(obj.optString("UPPER_OFFICE_ID"))) {
                                map.put("name", obj.optString("OFFICE_NAME"));
                                map.put("code", "" + obj.optString("OFFICE_CODE"));
                                officeList.add(map);
//                                }
                            }
                            showOffice();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        ToastUtil.showShort(RegisterDoctorActivity.this, response.optString("message"));
                    }
                }
            }, this);
        } else {
            showOffice();
        }
    }

    private void showOffice() {
        mPopupWindow = WheelUtils.showSingleWheel(RegisterDoctorActivity.this, officeList, mOfficeStv, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int index1 = (Integer) v.getTag(R.id.wheel_one);
                Map<String, String> map = officeList.get(index1);
                String name = map.get("name");
                officeCode = map.get("code");
                mOfficeStv.setRightString(name);
            }
        });

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
                    imgIcon.setImageBitmap(bitmap);
                } else {
                    if (mHeaderFile != null) mHeaderFile.deleteOnExit();
                    mHeaderFile = null;
                }
                break;
            case 223://没有的医院
                if (resultCode == RESULT_OK) {
                    strHospital = data.getStringExtra("content");
                    mHospitalStv.setRightString(strHospital);
                }
                break;
        }
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
            mPopupWindow = WheelUtils.showSingleWheel(this, mZhiChen, mDoctitleStv, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int index1 = (Integer) v.getTag(R.id.wheel_one);
                    Map<String, String> map = mZhiChen.get(index1);
                    String name = map.get("name");
                    docTitleCode = map.get("code");
                    mDoctitleStv.setRightString(name);
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
        imgDocInfo.setImageBitmap(bitmap);
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


}
