package com.yksj.consultation.sonDoc.doctor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.library.base.base.BaseTitleActivity;
import com.library.base.dialog.SelectorDialog;
import com.library.base.imageLoader.ImageLoader;
import com.library.base.utils.RxChooseHelper;
import com.library.base.widget.SuperButton;
import com.library.base.widget.SuperTextView;
import com.yksj.consultation.adapter.EvelateAdapter;
import com.yksj.consultation.agency.view.LocationPopu;
import com.yksj.consultation.app.AppContext;
import com.yksj.consultation.bean.DoctorInfoBean;
import com.yksj.consultation.bean.HospitalBean;
import com.yksj.consultation.bean.OfficeBean;
import com.yksj.consultation.bean.ResponseBean;
import com.yksj.consultation.business.LoginBusiness;
import com.yksj.consultation.comm.AddTextActivity;
import com.yksj.consultation.comm.ImageBrowserActivity;
import com.yksj.consultation.comm.ImageGalleryActivity;
import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.utils.DoctorHelper;
import com.yksj.healthtalk.db.DictionaryHelper;
import com.yksj.healthtalk.entity.CustomerInfoEntity;
import com.yksj.healthtalk.net.http.ApiCallback;
import com.yksj.healthtalk.net.http.ApiCallbackWrapper;
import com.yksj.healthtalk.net.http.ApiService;
import com.yksj.healthtalk.utils.DataParseUtil;
import com.yksj.healthtalk.utils.HStringUtil;
import com.yksj.healthtalk.utils.ToastUtil;

import org.apache.http.message.BasicNameValuePair;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.subjects.PublishSubject;
import okhttp3.Request;

/**
 * 医生端我的信息界面,可编辑
 * Created by lmk on 15/10/13.
 */
public class MyInfoActivity extends BaseTitleActivity implements View.OnClickListener {

    private static final int HOSPITAL_PICTURE_CODE = 650;

    @BindView(R.id.review_tv) TextView mReviewTv;
    @BindView(R.id.account_stv) SuperTextView mAccountStv;
    @BindView(R.id.nickname_stv) SuperTextView mNicknameStv;
    @BindView(R.id.address_stv) SuperTextView mAddressStv;
    @BindView(R.id.hospital_stv) SuperTextView mHospitalStv;
    @BindView(R.id.office_stv) SuperTextView mOfficeStv;
    @BindView(R.id.job_title_stv) SuperTextView mJobTitleStv;
    @BindView(R.id.receipt_stv) SuperTextView mReceiptStv;
    @BindView(R.id.receipt_phone_stv) SuperTextView mReceiptPhoneStv;
    @BindView(R.id.bank_account_stv) SuperTextView mBankAcountStv;
    @BindView(R.id.bank_stv) SuperTextView mBankStv;
    @BindView(R.id.bank_branch_stv) SuperTextView mBankBranchStv;
    @BindView(R.id.expertise_stv) SuperTextView mExpertiseStv;
    @BindView(R.id.description_stv) SuperTextView mDescriptionStv;
    @BindView(R.id.self_upload_sb) SuperButton mSelfUploadSb;
    @BindView(R.id.certificate_upload_sb) SuperButton mCertificateUploadSb;
    @BindView(R.id.my_info_img_real) ImageView mSelfImg;
    @BindView(R.id.my_info_img_qualification) ImageView mCertificateImg;
    @BindView(R.id.my_info_comment_num) TextView tvCommentNum;
    @BindView(R.id.my_info_comment_tv) TextView tvComment;
    @BindView(R.id.my_info_comment_list) ListView commentList;

    private EvelateAdapter mAdapter;
    private CustomerInfoEntity mCusInfoEctity;
    private String mDoctorId;

    private String mCertificatePath;
    private String mSelfPath;
    private String mAddressCode;
    private String mJobCode;
    private String mOfficeCode;
    private String mHospitalCode;
    private List<OfficeBean> mOfficeDatas;

    public static Intent getCallingIntent(Context context, String doctorId){
        Intent intent = new Intent(context, MyInfoActivity.class);
        intent.putExtra("mDoctorId", doctorId);
        return intent;
    }

    @Override
    public int createLayoutRes() {
        return R.layout.aty_my_info;
    }

    @Override
    public void initialize(Bundle bundle) {
        super.initialize(bundle);
        setTitle(R.string.my_massage);
        mDoctorId = getIntent().getStringExtra("mDoctorId");
        initView();
        requestData();
    }

    private void initView() {
        if (!"0".equals(LoginBusiness.getInstance().getLoginEntity().getDoctorPosition()))
            findViewById(R.id.my_info_comment_layout).setVisibility(View.GONE);
        findViewById(R.id.my_info_comment_more).setOnClickListener(this);
        mAdapter = new EvelateAdapter(MyInfoActivity.this);
        commentList.setAdapter(mAdapter);
    }

    private void requestData() {
        if (TextUtils.isEmpty(mDoctorId)){
            return;
        }
        ApiService.doctorInfo(mDoctorId, new ApiCallbackWrapper<String>(true) {
            @Override public void onResponse(String response) {
                super.onResponse(response);
                try {
                    JSONObject obj = new JSONObject(response);
                    if ("1".equals(obj.optString("code"))) {
                        mCusInfoEctity = DataParseUtil.JsonToDocCustmerInfo(obj.getJSONObject("result"));
                        onBoundData();
                        if ("0".equals(LoginBusiness.getInstance().getLoginEntity().getDoctorPosition()))
                            loadComment();
                    } else {
                        ToastUtil.showShort(MyInfoActivity.this, obj.optString("message"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void onBoundData() {
        mAccountStv.setRightString(mCusInfoEctity.getUsername());
        mNicknameStv.setRightString(mCusInfoEctity.getRealname());
        mAddressStv.setRightString(mCusInfoEctity.getDoctorWorkaddress());
        mHospitalStv.setRightString(mCusInfoEctity.getHospital());
        mOfficeStv.setRightString(mCusInfoEctity.getOfficeName2());
        mJobTitleStv.setRightString(mCusInfoEctity.getDoctorTitleName());
        mReceiptStv.setRightString(mCusInfoEctity.getTransferGetName());
        mReceiptPhoneStv.setRightString(mCusInfoEctity.getTransferGetTele());
        mBankAcountStv.setRightString(mCusInfoEctity.getTransferCode());
        mBankStv.setRightString(mCusInfoEctity.getTransferName());
        mBankBranchStv.setRightString(mCusInfoEctity.getTransferAddr());
        mExpertiseStv.setRightString(mCusInfoEctity.getSpecial());
        mDescriptionStv.setRightString(mCusInfoEctity.getIntroduction());

        mAddressCode = mCusInfoEctity.getDoctorWorkaddressCode();
        mOfficeCode = mCusInfoEctity.getOfficeCode2();
        mJobCode = mCusInfoEctity.getDoctorTitle();

        mSelfPath = AppContext.getApiRepository().URL_QUERYHEADIMAGE + mCusInfoEctity.getDoctorClientPicture();
        ImageLoader.load(mSelfPath).into(mSelfImg);

        mCertificatePath = AppContext.getApiRepository().URL_QUERYHEADIMAGE + mCusInfoEctity.getDoctorCertificate();
        ImageLoader.load(mCertificatePath).into(mCertificateImg);

        if (HStringUtil.isEmpty(mCusInfoEctity.getIntroduction())) {
            findViewById(R.id.rl_jianjie).setVisibility(View.GONE);
        }
        if (HStringUtil.isEmpty(mCusInfoEctity.getSpecial())) {
            findViewById(R.id.rl_special).setVisibility(View.GONE);
        }

        if (!"40".equals(mCusInfoEctity.getVerifyFlag())) {
            if (888 != mCusInfoEctity.getRoldid()) {//审核中
                mReviewTv.setVisibility(View.VISIBLE);
                mReviewTv.setText("信息认证审核中，暂时不能操作");
                setEditEnabled(false);
            } else {
                mReviewTv.setVisibility(View.GONE);
            }
        } else if ("40".equals(mCusInfoEctity.getVerifyFlag())) {
            mReviewTv.setVisibility(View.VISIBLE);
            mReviewTv.setText(String.format("信息认证审核失败%s", "，失败原因：" + mCusInfoEctity.getRefusal_reason()));
            setEditEnabled(true);
        } else {
            mReviewTv.setVisibility(View.GONE);
            setEditEnabled(true);
        }
    }

    /**
     * 设置信息是否可编辑
     * @param enabled
     */
    public void setEditEnabled(boolean enabled) {
        mNicknameStv.setEnabled(enabled);
        mAddressStv.setEnabled(enabled);
        mHospitalStv.setEnabled(enabled);
        mOfficeStv.setEnabled(enabled);
        mJobTitleStv.setEnabled(enabled);
        mReceiptStv.setEnabled(enabled);
        mReceiptPhoneStv.setEnabled(enabled);
        mBankAcountStv.setEnabled(enabled);
        mBankStv.setEnabled(enabled);
        mBankBranchStv.setEnabled(enabled);
        mExpertiseStv.setEnabled(enabled);
        mDescriptionStv.setEnabled(enabled);
        mDescriptionStv.setEnabled(enabled);
        mSelfUploadSb.setEnabled(enabled);
        mCertificateUploadSb.setEnabled(enabled);
    }

    /**
     * 昵称
     * @param view
     */
    @OnClick(R.id.nickname_stv)
    public void onNickname(View view) {
        AddTextActivity.from(this)
                .setTitle("请输入昵称")
                .setContent(((SuperTextView) view).getRightString())
                .setListener(new AddTextActivity.OnAddTextClickListener() {
                    @Override public void onConfrimClick(View v, String content, AddTextActivity activity) {
                        activity.finish();
                        mNicknameStv.setRightString(content);
                        requestUpdate();
                    }
                })
                .startActivity();
    }

    /**
     * 所在地
     * @param view
     */
    @OnClick(R.id.address_stv)
    public void onAddress(View view) {
        KeyboardUtils.hideSoftInput(view);

        LocationPopu locationPopu = new LocationPopu(this);
        locationPopu.setOnChangeListener(new LocationPopu.OnChangeListener() {
            @Override public void onChanged(@NotNull String completeCity, @NotNull String city, @NotNull String code) {
                mAddressStv.setRightString(completeCity);
                mHospitalStv.setRightString("");
                mAddressCode = code;
                requestUpdate();
            }
        }).showScreenBottom(view);
    }

    /**
     * 医院
     * @param view
     */
    @OnClick(R.id.hospital_stv)
    public void onHospital(View view) {
        KeyboardUtils.hideSoftInput(view);
        Intent intent = HospitalListActivity.getCallingIntent(this, mAddressCode);
        startActivityForResult(intent, HOSPITAL_PICTURE_CODE);
    }

    /**
     * 科室
     * @param view
     */
    @SuppressLint("CheckResult")
    @OnClick(R.id.office_stv)
    public void onOffice(View view) {
        PublishSubject<List<OfficeBean>> subject = PublishSubject.create();
        ApiService.requestOffice(AppContext.APP_CONSULTATION_CENTERID, new ApiCallbackWrapper<ResponseBean<List<OfficeBean>>>() {
            @Override public void onResponse(ResponseBean<List<OfficeBean>> response) {
                super.onResponse(response);
                if (response.isSuccess()) {
                    mOfficeDatas = response.result;
                    subject.onNext(mOfficeDatas);
                } else {
                    subject.onError(new IllegalArgumentException(response.message));
                }
            }
        });
        subject.map(new Function<List<OfficeBean>, String[]>() {
            @Override public String[] apply(List<OfficeBean> offices) throws Exception {
                String[] titles = new String[offices.size()];
                for (int i = 0; i < offices.size(); i++) {
                    titles[i] = offices.get(i).OFFICE_NAME;
                }
                return titles;
            }
        }).doOnSubscribe(new Consumer<Disposable>() {
            @Override public void accept(Disposable disposable) throws Exception {
                KeyboardUtils.hideSoftInput(view);
            }
        }).subscribe(new Consumer<String[]>() {
            @Override public void accept(String[] titles) throws Exception {
                SelectorDialog.newInstance(titles)
                        .setOnItemClickListener(new SelectorDialog.OnMenuItemClickListener() {
                            @Override public void onItemClick(SelectorDialog dialog, int position) {
                                mOfficeCode = mOfficeDatas.get(position).OFFICE_CODE;
                                mOfficeStv.setRightString(mOfficeDatas.get(position).OFFICE_NAME);
                                requestUpdate();
                            }
                        }).show(getSupportFragmentManager());
            }
        });
    }

    /**
     * 职称
     * @param view
     */
    @OnClick(R.id.job_title_stv)
    public void onJobTitle(View view) {
        // 隐藏输入键盘
        KeyboardUtils.hideSoftInput(view);

        // 获取职称数据
        LinkedHashMap<String, String> job = DictionaryHelper.getInstance(this)
                .querydoctorTitles(this);
        job.remove("未知");

        // map转list
        ArrayList<String> titleList = new ArrayList<>(job.keySet());
        String[] titles = new String[titleList.size()];
        // list转数组
        titleList.toArray(titles);

        // 显示选择dailog
        SelectorDialog.newInstance(titles)
                .setOnItemClickListener(new SelectorDialog.OnMenuItemClickListener() {
                    @Override public void onItemClick(SelectorDialog dialog, int position) {
                        mJobCode = job.get(titles[position]);
                        mJobTitleStv.setRightString(titles[position]);
                        requestUpdate();
                    }
                }).show(getSupportFragmentManager());
    }

    /**
     * 专长
     * @param view
     */
    @OnClick(R.id.expertise_stv)
    public void onExpertise(View view) {
        AddTextActivity.from(this)
                .setTitle("请输入专长")
                .setContent(((SuperTextView) view).getRightString())
                .setListener(new AddTextActivity.OnAddTextClickListener() {
                    @Override public void onConfrimClick(View v, String content, AddTextActivity activity) {
                        activity.finish();
                        mExpertiseStv.setRightString(content);
                        requestUpdate();
                    }
                })
                .startActivity();
    }

    /**
     * 简介
     * @param view
     */
    @OnClick(R.id.description_stv)
    public void onDescription(View view) {
        AddTextActivity.from(this)
                .setTitle("请输入简介")
                .setContent(((SuperTextView) view).getRightString())
                .setListener(new AddTextActivity.OnAddTextClickListener() {
                    @Override public void onConfrimClick(View v, String content, AddTextActivity activity) {
                        activity.finish();
                        mDescriptionStv.setRightString(content);
                        requestUpdate();
                    }
                })
                .startActivity();
    }

    /**
     * 收款人
     * @param view
     */
    @OnClick(R.id.receipt_stv)
    public void onReceipt(View view) {
        AddTextActivity.from(this)
                .setTitle("请输入收款人")
                .setContent(((SuperTextView) view).getRightString())
                .setListener(new AddTextActivity.OnAddTextClickListener() {
                    @Override public void onConfrimClick(View v, String content, AddTextActivity activity) {
                        activity.finish();
                        mReceiptStv.setRightString(content);
                        requestUpdate();
                    }
                })
                .startActivity();
    }

    /**
     * 收款人手机
     * @param view
     */
    @OnClick(R.id.receipt_phone_stv)
    public void onReceiptPhone(View view) {
        AddTextActivity.from(this)
                .setTitle("请输入收款人手机")
                .setContent(((SuperTextView) view).getRightString())
                .setInputType(InputType.TYPE_CLASS_NUMBER)
                .setListener(new AddTextActivity.OnAddTextClickListener() {
                    @Override public void onConfrimClick(View v, String content, AddTextActivity activity) {
                        activity.finish();
                        mReceiptPhoneStv.setRightString(content);
                        requestUpdate();
                    }
                })
                .startActivity();
    }

    /**
     * 银行卡(账)号
     * @param view
     */
    @OnClick(R.id.bank_account_stv)
    public void onBankAccount(View view) {
        AddTextActivity.from(this)
                .setTitle("请输入银行卡(账)号")
                .setContent(((SuperTextView) view).getRightString())
                .setInputType(InputType.TYPE_CLASS_NUMBER)
                .setListener(new AddTextActivity.OnAddTextClickListener() {
                    @Override public void onConfrimClick(View v, String content, AddTextActivity activity) {
                        activity.finish();
                        mBankAcountStv.setRightString(content);
                        requestUpdate();
                    }
                })
                .startActivity();
    }

    /**
     * 开户银行
     * @param view
     */
    @OnClick(R.id.bank_stv)
    public void onBank(View view) {
        AddTextActivity.from(this)
                .setTitle("请输入开户银行")
                .setContent(((SuperTextView) view).getRightString())
                .setListener(new AddTextActivity.OnAddTextClickListener() {
                    @Override public void onConfrimClick(View v, String content, AddTextActivity activity) {
                        activity.finish();
                        mBankStv.setRightString(content);
                        requestUpdate();
                    }
                })
                .startActivity();
    }

    /**
     * 开户支行
     * @param view
     */
    @OnClick(R.id.bank_branch_stv)
    public void onBankBranch(View view) {
        AddTextActivity.from(this)
                .setTitle("请输入开户支行")
                .setContent(((SuperTextView) view).getRightString())
                .setListener(new AddTextActivity.OnAddTextClickListener() {
                    @Override public void onConfrimClick(View v, String content, AddTextActivity activity) {
                        activity.finish();
                        mBankBranchStv.setRightString(content);
                        requestUpdate();
                    }
                })
                .startActivity();
    }

    /**
     * 上传本人真实照片
     * @param v
     */
    @SuppressLint("CheckResult")
    @OnClick(R.id.self_upload_sb)
    public void onSelfUpload(View v) {
        RxChooseHelper.chooseImage(this)
                      .subscribe(new Consumer<String>() {
                          @Override
                          public void accept(String s) throws Exception {
                              mSelfPath = s;
                              ImageLoader.load(mSelfPath)
                                         .error(R.drawable.icon_upload_doc_head)
                                         .placeholder(R.drawable.icon_upload_doc_head)
                                         .into(mSelfImg);
                              requestUpdate();
                          }
                      });
    }

    /**
     * 上传医师证照片
     * @param v
     */
    @SuppressLint("CheckResult")
    @OnClick(R.id.certificate_upload_sb)
    public void onCertificateUpload(View v) {
        RxChooseHelper.chooseImage(this, 3, 2)
                      .subscribe(new Consumer<String>() {
                          @Override
                          public void accept(String s) throws Exception {
                              mCertificatePath = s;
                              ImageLoader.load(mCertificatePath)
                                         .error(R.drawable.icon_upload_doc_paper)
                                         .placeholder(R.drawable.icon_upload_doc_paper)
                                         .into(mCertificateImg);
                              requestUpdate();
                          }
                      });
    }

    /**
     * 医师图片点击
     * @param v
     */
    @OnClick(R.id.my_info_img_real)
    public void onSelfImgClick(View v){
        if (!TextUtils.isEmpty(mSelfPath)) {
            ImageBrowserActivity.from(this)
                    .setImagePath(mSelfPath)
                    .startActivity();
        }
    }

    /**
     * 医师证图片点击
     * @param v
     */
    @OnClick(R.id.my_info_img_qualification)
    public void onCertificateImgClick(View v){
        if (!TextUtils.isEmpty(mCertificatePath)) {
            ImageBrowserActivity.from(this)
                    .setImagePath(mCertificatePath)
                    .startActivity();
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.my_info_img_real://真实照片
                intent = new Intent(MyInfoActivity.this, ImageGalleryActivity.class);
                intent.putExtra(ImageGalleryActivity.URLS_KEY, new String[]{mCusInfoEctity.getDoctorClientPicture()});
                intent.putExtra(ImageGalleryActivity.TYPE_KEY, 0);//0,1单个,多个
                intent.putExtra("type", 1);
                startActivityForResult(intent, 3000);
                break;
            case R.id.my_info_img_qualification:
                intent = new Intent(MyInfoActivity.this, ImageGalleryActivity.class);
                intent.putExtra(ImageGalleryActivity.URLS_KEY, new String[]{mCusInfoEctity.getDoctorCertificate()});
                intent.putExtra(ImageGalleryActivity.TYPE_KEY, 0);//0,1单个,多个
                intent.putExtra("type", 1);
                startActivityForResult(intent, 3000);
                break;
            case R.id.my_info_comment_more://更多评论
                intent = new Intent(MyInfoActivity.this, DoctorCommentListActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 201:
                    requestData();
                    break;
                case HOSPITAL_PICTURE_CODE:
                    HospitalBean hospitalBean = HospitalListActivity.obtainData(data);
                    mHospitalCode = hospitalBean.UNIT_CODE;
                    mHospitalStv.setRightString(hospitalBean.UNIT_NAME);
                    requestUpdate();
                    break;
            }
        }
    }

    /**
     * 更新个人信息
     */
    private void requestUpdate() {
        String id = DoctorHelper.getId();
        String docName = mNicknameStv.getRightString();// 昵称
        String officeCode = mOfficeCode;// 科室编码
        String docTitleCode = mJobCode;// 职称编码
        String doctorPicture = mCusInfoEctity.getDoctorBigPicture();
        String clientPicture = mCusInfoEctity.getDoctorClientPicture();
        String certificate = mCusInfoEctity.getDoctorCertificate();
        String hospitalCode = mHospitalCode;// 医院编码
        String hospitalName = mHospitalStv.getRightString();// 医院名称
        String docSpecial = mExpertiseStv.getRightString();// 专长
        String addrCode = mAddressCode;// 所在地编码
        String strAddr = mAddressStv.getRightString();// 所在地
        String docDesc = mDescriptionStv.getRightString();// 简介
        String editGetName = mReceiptStv.getRightString();// 收款人
        String editGetTele = mReceiptPhoneStv.getRightString();// 收款人号码
        String editBankName = mBankStv.getRightString();// 开户银行
        String editCode = mBankAcountStv.getRightString();// 银行卡(账)号
        String editAddrs = mBankBranchStv.getRightString();// 开户支行
        String versionInfo = AppUtils.getAppVersionName();// 版本号

        ApiService.doctorUpdate(id,
                docName,
                officeCode,
                AppContext.APP_CONSULTATION_CENTERID,
                docTitleCode,
                doctorPicture,
                clientPicture,
                certificate,
                hospitalCode,
                hospitalName,
                docSpecial,
                addrCode,
                strAddr,
                docDesc,
                versionInfo,
                editGetName,
                editGetTele,
                editBankName,
                editCode,
                editAddrs,
                TextUtils.isEmpty(mSelfPath) ? null : new File(mSelfPath),
                TextUtils.isEmpty(mCertificatePath) ? null : new File(mCertificatePath),
                new ApiCallbackWrapper<ResponseBean<DoctorInfoBean>>(true) {
                    @Override public void onResponse(ResponseBean<DoctorInfoBean> response) {
                        super.onResponse(response);
                        if (response.isSuccess()) {
                            updataLocalInfo();
                            ToastUtils.showLong(response.message);
                        } else {
                            ToastUtils.showShort(response.message);
                        }
                    }
                });
    }

    /**
     * 更新本地医生信息
     */
    private void updataLocalInfo() {
        DoctorInfoBean doctorInfo = DoctorHelper.getDoctorInfo();
        doctorInfo.doctorRealName = mNicknameStv.getRightString();
        doctorInfo.officeCode = mOfficeCode;
        doctorInfo.officeName = mOfficeStv.getRightString();
        doctorInfo.hospitalAddress = mAddressStv.getRightString();
        doctorInfo.hospitalAddressCode = mAddressCode;
        doctorInfo.hospital = mHospitalStv.getRightString();
        doctorInfo.hospitalCode = mHospitalCode;
        doctorInfo.job = mJobTitleStv.getRightString();
        doctorInfo.jobCode = mJobCode;
        doctorInfo.expertise = mExpertiseStv.getRightString();
        doctorInfo.introduction = mDescriptionStv.getRightString();
        doctorInfo.receipt = mReceiptStv.getRightString();
        doctorInfo.receiptPhone = mReceiptPhoneStv.getRightString();
        doctorInfo.bankAcount = mBankAcountStv.getRightString();
        doctorInfo.bankName = mBankStv.getRightString();
        doctorInfo.bankBranch = mBankBranchStv.getRightString();
        DoctorHelper.saveLoginInfo(doctorInfo);
    }

    /**
     * 加载评论列表
     */
    private void loadComment() {
        List<BasicNameValuePair> pairs = new ArrayList<>();
        pairs.add(new BasicNameValuePair("TYPE", "findCommentList"));
        pairs.add(new BasicNameValuePair("PAGESIZE", "1"));
        pairs.add(new BasicNameValuePair("PAGENUM", "3"));//只加载3条数据
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
                        JSONObject result = obj.optJSONObject("result");
                        tvCommentNum.setText("(" + result.optInt("commentNum") + ")");
                        ArrayList<Map<String, String>> list = new ArrayList<Map<String, String>>();
                        JSONArray array = result.getJSONArray("commentList");
                        Map<String, String> map = null;
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject jsonObject = array.getJSONObject(i);
                            map = new HashMap<String, String>();
                            map.put("COMMENT_RESULT", jsonObject.optString("COMMENT_RESULT"));
                            map.put("PATIENT_ID", jsonObject.optString("PATIENT_ID") + "");
                            map.put("SERVICE_LEVEL", jsonObject.optString("SERVICE_LEVEL"));
                            map.put("REAL_NAME", jsonObject.optString("REAL_NAME"));
                            list.add(map);

                        }
                        if (list.size() > 0) {
                            mAdapter.add(list);
                        }

                    } else {
                        ToastUtil.showShort(MyInfoActivity.this, obj.optString("message"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, this);
    }
}
