package com.yksj.consultation.sonDoc.consultation.consultationorders;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.library.base.base.BaseActivity;
import com.yksj.consultation.comm.DoubleBtnFragmentDialog;
import com.yksj.consultation.comm.DoubleBtnFragmentDialog.OnDilaogClickListener;
import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.utils.DoctorHelper;
import com.yksj.consultation.business.LoginBusiness;
import com.yksj.healthtalk.net.http.ApiService;
import com.yksj.healthtalk.net.http.AsyncHttpResponseHandler;
import com.yksj.healthtalk.net.http.ObjectHttpResponseHandler;
import com.yksj.healthtalk.utils.ToastUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author HEKL
 *         <p>
 *         拒绝会诊原因_
 */
public class AtyConsultReject extends BaseActivity implements OnClickListener {
    private TextView tvReason;// 拒绝或者取消提示
    String conID;
    String reasons = null;
    private String code;
    private String type = null;
    private RadioGroup mRejectConsult;
    ArrayList<HashMap<String, String>> list = null;
    private String option;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.rejectconsultation_activity_layout);
        initView();
        getSensonsData();
    }


    private void initView() {
        initializeTitle();
        tvReason = (TextView) findViewById(R.id.tv_reason);
        titleRightBtn2.setOnClickListener(this);
        titleRightBtn2.setVisibility(View.VISIBLE);
        titleRightBtn2.setText("提交");
        titleLeftBtn.setOnClickListener(this);
        if (LoginBusiness.getInstance().getLoginEntity() != null) {
            String positionId = LoginBusiness.getInstance().getLoginEntity().getDoctorPosition();
            tvReason.setText("请选择拒绝接诊的原因:");
            conID = getIntent().getStringExtra("conId");
            mRejectConsult = (RadioGroup) findViewById(R.id.rg_reject);
            if ("0".equals(positionId)) {
                setTitle("拒绝接单");
                type = "Doctor";
                option = "4";
            } else {
                setTitle("拒绝接诊");
                type = "Expert";
                option = "14";
            }
        }
        setRadioGroupListner();
        mRejectConsult.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_back:
                onBackPressed();
                break;
            case R.id.title_right2:
                if (mRejectConsult.getCheckedRadioButtonId() == -1) {
                    ToastUtil.showShort("请选择拒绝接诊的原因");
                } else {
                    DoubleBtnFragmentDialog.showDefault(getSupportFragmentManager(), "您确认提交吗?", "取消", "确定",
                            new OnDilaogClickListener() {

                                @Override
                                public void onDismiss(DialogFragment fragment) {

                                }

                                @Override
                                public void onClick(DialogFragment fragment, View v) {
                                    sendSeasonsData();
                                }
                            });

                }
                break;
        }
    }

    public void setRadioGroupListner() {
        mRejectConsult.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                final RadioButton tempButton = (RadioButton) findViewById(checkedId);
                reasons = tempButton.getText().toString();
                code = tempButton.getTag().toString();
            }
        });
    }

    // 获取删除服务/拒绝服务原因列表
    private void getSensonsData() {
        ApiService.doHttpGetCancelReason(type, new ObjectHttpResponseHandler(AtyConsultReject.this) {
            JSONObject obj = null;

            @Override
            public Object onParseResponse(String content) {
                try {
                    obj = new JSONObject(content);
                    if (obj.optInt("code") == 1) {
                        JSONArray array = obj.getJSONArray("result");
                        list = new ArrayList<HashMap<String, String>>();
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject object = array.getJSONObject(i);
                            HashMap<String, String> map = new HashMap<String, String>();
                            map.put("ID", "" + object.optInt("ID"));
                            map.put("NAME", object.optString("NAME"));
                            list.add(map);
                        }
                    } else {
                        ToastUtil.showShort(obj.optString("message"));
                    }
                } catch (JSONException e) {
                    return null;
                }
                return list;
            }

            @SuppressLint("ResourceAsColor")
            @Override
            public void onSuccess(Object response) {
                for (int t = 0; t < list.size(); t++) {
                    RadioButton btn = new RadioButton(getApplicationContext());
                    btn.setButtonDrawable(R.drawable.check_box1); //设置按钮的样式
                    btn.setPadding(40, 20, 0, 20); //设置文字距离按钮四周的距离
                    btn.setTextColor(getResources().getColor(R.color.color_text_gray));
                    btn.setText(list.get(t).get("NAME"));
                    btn.setTag(list.get(t).get("ID"));


                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.MATCH_PARENT);
                    mRejectConsult.addView(btn,params);

                }
                super.onSuccess(response);
            }
        });
    }


    // 发送患者删除服务/专家拒绝服务
    private void sendSeasonsData() {
        ApiService.doHttpPostCancelReason(option, code, conID, DoctorHelper.getId(),
                new AsyncHttpResponseHandler(this) {
                    JSONObject obj = null;

                    @Override
                    public void onSuccess(int statusCode, String content) {
                        try {
                            obj = new JSONObject(content);
                            ToastUtil.showShort(obj.optString("message"));
                            if (obj.optInt("code") == 1) {
                                startActivity(new Intent(AtyConsultReject.this, MyConsultationActivity.class));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        super.onSuccess(statusCode, content);
                    }
                });

    }
}
