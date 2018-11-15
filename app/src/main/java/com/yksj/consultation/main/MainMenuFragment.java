package com.yksj.consultation.main;

import android.content.Intent;
import android.view.View;

import com.library.base.base.BaseFragment;
import com.yksj.consultation.business.LoginBusiness;
import com.yksj.consultation.comm.SingleBtnFragmentDialog;
import com.yksj.consultation.doctor.ShareListActivity;
import com.yksj.consultation.news.NewsCenterActivity;
import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.sonDoc.consultation.main.BarCodeActivity;
import com.yksj.consultation.sonDoc.dossier.DiscussCaseListActivity;
import com.yksj.consultation.sonDoc.friend.MyCustomerActivity;
import com.yksj.consultation.utils.DoctorHelper;

import butterknife.OnClick;

public class MainMenuFragment extends BaseFragment {

    @Override
    public int createLayoutRes() {
        return R.layout.fragment_main_menu;
    }

    @Override
    public void initialize(View view) {
        super.initialize(view);
    }

    /**
     * 消息中心
     * @param v
     */
    @OnClick(R.id.rl_entry1)
    public void onEntry1(View v){
        Intent intent = new Intent(getContext(), NewsCenterActivity.class);
        intent.putExtra(NewsCenterActivity.TYPE, "Encyclopedia");
        startActivity(intent);
    }

    /**
     * 病历讨论
     * @param v
     */
    @OnClick(R.id.rl_entry2)
    public void onEntry2(View v){
        Intent intent = new Intent(getContext(), DiscussCaseListActivity.class);
        startActivity(intent);
    }

    /**
     * 患者管理
     * @param v
     */
    @OnClick(R.id.rl_entry3)
    public void onEntry3(View v){
        String roldid = DoctorHelper.getDoctorInfo().roleId;
        if (roldid.equals("666")) {
            SingleBtnFragmentDialog.showDefault(getChildFragmentManager(), "您的医生资质还在审核中...", new SingleBtnFragmentDialog.OnClickSureBtnListener() {
                @Override
                public void onClickSureHander() {
                }
            });
        } else if (roldid.equals("1")) {
            SingleBtnFragmentDialog.showDefault(getChildFragmentManager(), "您的医生资质审核失败，请重新编辑审核", new SingleBtnFragmentDialog.OnClickSureBtnListener() {
                @Override
                public void onClickSureHander() {
                }
            });
        } else {
            Intent intent = new Intent(getContext(), MyCustomerActivity.class);
            intent.putExtra("MAIN", "main");
            startActivity(intent);
        }
    }


    /**
     * 六一班
     * @param v
     */
    @OnClick(R.id.rl_entry4)
    public void onEntry4(View v){
        Intent intent = new Intent(getContext(), SixOneClassActivity.class);
        startActivity(intent);
    }

    /**
     * 名医分享
     * @param v
     */
    @OnClick(R.id.rl_entry5)
    public void onEntry5(View v){
        Intent intent = new Intent(getContext(), ShareListActivity.class);
        startActivity(intent);
    }

    /**
     * 二维码
     * @param v
     */
    @OnClick(R.id.rl_entry6)
    public void onEntry6(View v){
        String userId = DoctorHelper.getId();
        String realname = DoctorHelper.getNickName();
        String hospital = LoginBusiness.getInstance().getLoginEntity().getHospital();
        String office = LoginBusiness.getInstance().getLoginEntity().getOfficeName2();
        String titleName = LoginBusiness.getInstance().getLoginEntity().getDoctorTitleName();
        BarCodeActivity.from(getContext())
                .setId(userId)
                .setName(realname)
                .setHospital(hospital)
                .setSecendOffice(office)
                .setTitle(titleName)
                .toStart();
    }
}
