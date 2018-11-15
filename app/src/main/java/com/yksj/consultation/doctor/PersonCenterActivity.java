package com.yksj.consultation.doctor;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.library.base.base.BaseTitleActivity;
import com.library.base.dialog.ConfirmDialog;
import com.library.base.imageLoader.ImageLoader;
import com.library.base.widget.SuperTextView;
import com.yksj.consultation.agency.AgencyHomeActivity;
import com.yksj.consultation.app.AppContext;
import com.yksj.consultation.business.LoginBusiness;
import com.yksj.consultation.constant.Constant;
import com.yksj.consultation.event.EDoctorUpdata;
import com.yksj.consultation.login.UserLoginActivity;
import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.sonDoc.doctor.MyInfoActivity;
import com.yksj.consultation.station.StationListActivity;
import com.yksj.consultation.union.UnionListActivity;
import com.yksj.consultation.utils.DoctorHelper;

import org.greenrobot.eventbus.Subscribe;

/**
 * Created by HEKL on 2015/9/15.
 * Used for 医生端个人中心_
 */
public class PersonCenterActivity extends BaseTitleActivity implements View.OnClickListener {
    private String position;
    private SuperTextView mPersonStv;

    public static Intent getCallingIntent(Context context){
        Intent intent = new Intent(context, PersonCenterActivity.class);
        return intent;
    }

    @Override
    public int createLayoutRes() {
        return R.layout.aty_personcenter;
    }

    @Override
    public void initialize(Bundle bundle) {
        super.initialize(bundle);
        setTitle("个人中心");
        initView();
    }

    private void initView() {
        findViewById(R.id.rl_mywallet).setOnClickListener(this);
        findViewById(R.id.rl_workstation).setOnClickListener(this);//我的医生集团
        findViewById(R.id.rl_myorders).setOnClickListener(this);
        findViewById(R.id.my_evaluate1).setOnClickListener(this);//名医分享
        findViewById(R.id.rl_plan).setOnClickListener(this);
        findViewById(R.id.rl_notebook).setOnClickListener(this);
        findViewById(R.id.rl_union).setOnClickListener(this);
        findViewById(R.id.rl_agency).setOnClickListener(this);
        findViewById(R.id.my_comments).setOnClickListener(this);
        findViewById(R.id.my_servers).setOnClickListener(this);
        findViewById(R.id.person_stv).setOnClickListener(this);
        findViewById(R.id.rl_settings).setOnClickListener(this);
        findViewById(R.id.logout_btn).setOnClickListener(this);
        findViewById(R.id.rl_tools).setOnClickListener(this);

        mPersonStv = findViewById(R.id.person_stv);
        if (DoctorHelper.hasLoagin()) {
            position = LoginBusiness.getInstance().getLoginEntity().getDoctorPosition();
            mPersonStv.setLeftString(DoctorHelper.getNickName());
            mPersonStv.setLeftBottomString(String.format("六一账号: %s", DoctorHelper.getAccount()));
            String avatarPath = AppContext.getApiRepository().URL_QUERYHEADIMAGE_NEW + LoginBusiness.getInstance().getLoginEntity().getNormalHeadIcon();
            ImageLoader.loadAvatar(avatarPath).into(mPersonStv.getLeftIconIV());
        }
        if ("0".equals(position) && "40".equals(LoginBusiness.getInstance().getLoginEntity().getVerifyFlag())) {
            mPersonStv.setRightIcon(R.drawable.check_fail);
        }else{
            mPersonStv.setRightString("个人信息");
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.person_stv://我的信息
                intent = MyInfoActivity.getCallingIntent(this, DoctorHelper.getId());
                startActivity(intent);
                break;
            case R.id.rl_mywallet://我的钱包
                intent = AtyAccountInfo.getCallingIntent(this);
                startActivity(intent);
                break;
            case R.id.rl_workstation://我的医生集团
                intent = new Intent(this, StationListActivity.class);
                startActivity(intent);
                break;
            case R.id.rl_agency://找机构
                intent = new Intent(this, AgencyHomeActivity.class);
                startActivity(intent);
                break;
            case R.id.rl_myorders://我的订单
                intent = new Intent(this, MyOrdersMenuActivity.class);
                startActivity(intent);
                break;
            case R.id.my_servers://我的服务
                intent = DoctorServiceActivity.getCallingIntent(this);
                startActivity(intent);
                break;
            case R.id.my_comments://我的评价
                intent = MyCommentActivity.getCallingIntent(this);
                startActivity(intent);
                break;
            case R.id.my_evaluate1://名医分享
                intent = new Intent(this, ShareListActivity.class);
                intent.putExtra(Constant.USER_ID, DoctorHelper.getId());
                startActivity(intent);
                break;
            case R.id.rl_union://医生联盟
                startActivity(UnionListActivity.getCallingIntent(this));
                break;
//            case R.id.rl_assistant://使用助理
//                intent = new Intent(this, MyOrdersMenuActivity.class);
//                startActivity(intent);
//                break;
            case R.id.rl_plan://医教计划
                intent = new Intent(this, MyDoctorPlan.class);
                startActivity(intent);
                break;
            case R.id.rl_notebook://记事本
                //   intent = new Intent(this, MyOrdersMenuActivity.class);
                intent = new Intent(this, MyNoteBookActivity.class);
                startActivity(intent);
                break;
            case R.id.rl_tools:// 工具箱
                intent = DoctorToolsListActivity.getCallingIntent(this, DoctorHelper.getId());
                startActivity(intent);
                break;
//            case R.id.outpatient_time://门诊时间
//                if (state == 666) {
//                    SingleBtnFragmentDialog.showDefault(getSupportFragmentManager(), "您的医生资质还在审核中...", new SingleBtnFragmentDialog.OnClickSureBtnListener() {
//                        @Override
//                        public void onClickSureHander() {
//                            return;
//                        }
//                    });
//                } else {
//                    intent = new Intent(this, DoctorSeeServiceActivity.class);
//                    intent.putExtra("type", "3");
//                    intent.putExtra("titleName", "门诊预约");
//                    startActivity(intent);
//                }
//                break;
//            case R.id.rl_myrecord://我的病历
//                if (state == 666) {
//                    SingleBtnFragmentDialog.showDefault(getSupportFragmentManager(), "您的医生资质还在审核中...", new SingleBtnFragmentDialog.OnClickSureBtnListener() {
//                        @Override
//                        public void onClickSureHander() {
//                            return;
//                        }
//                    });
//                } else {
//                    intent = new Intent(this, DAtyCasehistoryDiscussion.class);
//                    startActivity(intent);
//                }
//
//                break;

//            case R.id.rl_commontools://常用工具
//                intent = new Intent(this, DAtyCommonTools.class);
//                startActivity(intent);
//                break;
            case R.id.rl_settings://设置
                intent = new Intent(this, ContentActivity.class);
                intent.putExtra("type", 0);
                startActivity(intent);
                break;
            case R.id.logout_btn://退出登录
                ConfirmDialog.newInstance("", getResources().getString(R.string.quit_tip))
                        .addListener(new ConfirmDialog.SimpleConfirmDialogListener(){
                            @Override public void onPositiveClick(ConfirmDialog dialog, View v) {
                                super.onPositiveClick(dialog, v);
                                LoginBusiness.getInstance().loginOut();
                                Intent intent = new Intent(getApplicationContext(), UserLoginActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                PersonCenterActivity.this.finish();
                            }
                        }).show(getSupportFragmentManager());
                break;
        }
    }

    @Subscribe
    public void onDoctorUpdata(EDoctorUpdata e){
        position = LoginBusiness.getInstance().getLoginEntity().getDoctorPosition();
        mPersonStv.setLeftString(DoctorHelper.getNickName());
        mPersonStv.setLeftBottomString(String.format("六一账号: %s", DoctorHelper.getId()));
        String avatarPath = AppContext.getApiRepository().URL_QUERYHEADIMAGE_NEW + LoginBusiness.getInstance().getLoginEntity().getNormalHeadIcon();
        ImageLoader.loadAvatar(avatarPath).into(mPersonStv.getLeftIconIV());
    }
}