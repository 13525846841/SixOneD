package com.yksj.consultation.station;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.library.base.base.BaseTitleActivity;
import com.library.base.dialog.SelectorDialog;
import com.library.base.imageLoader.ImageLoader;
import com.library.base.utils.RxChooseHelper;
import com.yksj.consultation.business.LoginBusiness;
import com.yksj.consultation.sonDoc.R;
import com.yksj.healthtalk.net.http.ApiCallbackWrapper;
import com.yksj.healthtalk.net.http.ApiService;
import com.yksj.healthtalk.utils.HStringUtil;
import com.yksj.healthtalk.utils.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import butterknife.OnClick;
import io.reactivex.functions.Consumer;

/**
 * 创建医生集团
 */
public class StationCreateActivity extends BaseTitleActivity {
    private File mStationHeaderFile;//拍照文件
    private ImageView headView;
    private EditText stationName, stationHos, hosIntro, stationIntro, leaderIntro;
    private String officeId;//医生集团的科室ID

    @Override
    public int createLayoutRes() {
        return R.layout.activity_stations_create;
    }

    @Override
    public void initialize(Bundle bundle) {
        super.initialize(bundle);
        setTitle("创建工作站");
        setRight("完成", this::submit);
        headView = (ImageView) findViewById(R.id.my_station_img_real);
        stationName = (EditText) findViewById(R.id.station_name);
        stationHos = (EditText) findViewById(R.id.station_hos);
        hosIntro = (EditText) findViewById(R.id.hos_intro);
        stationIntro = (EditText) findViewById(R.id.staion_intro);
        leaderIntro = (EditText) findViewById(R.id.leader_intro);

        View checklist = getLayoutInflater().inflate(R.layout.personal_photo_check, null);
        checklist.findViewById(R.id.paizhao).setOnClickListener(this);
        checklist.findViewById(R.id.bendifenjian).setOnClickListener(this);
        checklist.findViewById(R.id.quxiao).setOnClickListener(this);

        officeId = LoginBusiness.getInstance().getLoginEntity().getOfficeCode2();
    }

    /**
     * 添加工作站背景图片
     * @param v
     */
    @OnClick(R.id.my_station_img_real)
    public void onAddBackground(View v) {
        SelectorDialog.newInstance(new String[]{"本地照片", "拍摄"})
                      .setOnItemClickListener(new SelectorDialog.OnMenuItemClickListener() {
                          @Override
                          public void onItemClick(SelectorDialog dialog, int position) {
                              switch (position) {
                                  case 1://拍摄
                                      RxChooseHelper.captureImage(StationCreateActivity.this, 3, 2)
                                                    .subscribe(new Consumer<String>() {
                                                        @Override
                                                        public void accept(String s) throws Exception {
                                                            mStationHeaderFile = new File(s);
                                                            ImageLoader.load(s).into(headView);
                                                        }
                                                    });
                                      break;
                                  case 0://本地照片
                                      RxChooseHelper.chooseImage(StationCreateActivity.this, 3, 2)
                                                    .subscribe(new Consumer<String>() {
                                                        @Override
                                                        public void accept(String s) throws Exception {
                                                            mStationHeaderFile = new File(s);
                                                            ImageLoader.load(s).into(headView);
                                                        }
                                                    });
                                      break;
                              }
                          }
                      })
                      .show(getSupportFragmentManager());
    }

    /**
     * 提交
     */
    private void submit(View v) {
        if (mStationHeaderFile == null) {
            ToastUtil.showShort("请添加图片");
            return;
        }
        String stationNameStr = stationName.getText().toString();
        if (TextUtils.isEmpty(stationNameStr)) {
            ToastUtil.showToastPanl("请填写工作站名称");
            return;
        }
        String stationHosStr = stationHos.getText().toString();
        if (TextUtils.isEmpty(stationHosStr)) {
            ToastUtil.showToastPanl("请填写所属医院");
            return;
        }
        String hosIntroStr = hosIntro.getText().toString();
        if (TextUtils.isEmpty(hosIntroStr)) {
            ToastUtil.showToastPanl("请填写医院介绍");
            return;
        }
        String stationIntroStr = stationIntro.getText().toString();
        if (TextUtils.isEmpty(stationIntroStr)) {
            ToastUtil.showToastPanl("请填写工作站介绍");
            return;
        }
        String leaderIntroStr = leaderIntro.getText().toString();
        if (TextUtils.isEmpty(leaderIntroStr)) {
            ToastUtil.showToastPanl("请填写站长介绍");
            return;
        }

        ApiService.OKHttpCreatStation(stationNameStr//工作站名称
                , stationIntroStr//所属医院
                , stationHosStr//医院介绍
                , hosIntroStr
                , officeId
                , hosIntroStr
                , leaderIntroStr
                , hosIntroStr
                , mStationHeaderFile
                , new ApiCallbackWrapper<String>(true) {
                    @Override
                    public void onResponse(String content) {
                        if (!HStringUtil.isEmpty(content)) {
                            try {
                                JSONObject object = new JSONObject(content);
                                if ("1".equals(object.optString("code"))) {
                                    LoginBusiness.getInstance().getLoginEntity().setSiteId(object.optString("result"));
                                    finish();
                                }
                                ToastUtil.showShort(object.optString("message"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }, this);
    }
}
