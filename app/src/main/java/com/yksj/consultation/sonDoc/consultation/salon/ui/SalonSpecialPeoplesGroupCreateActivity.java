package com.yksj.consultation.sonDoc.consultation.salon.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.library.base.base.BaseTitleActivity;
import com.yksj.consultation.comm.DoubleBtnFragmentDialog;
import com.yksj.consultation.sonDoc.R;
import com.yksj.healthtalk.net.http.ApiService;
import com.yksj.healthtalk.net.http.JsonsfHttpResponseHandler;
import com.yksj.healthtalk.net.http.RequestParams;
import com.yksj.healthtalk.net.socket.SmartFoxClient;
import com.yksj.healthtalk.utils.ToastUtil;

import org.apache.commons.lang.math.NumberRange;
import org.apache.commons.lang.math.NumberUtils;


/**
 * 特殊收费人群组创建
 *
 * @author zhao
 */
public class SalonSpecialPeoplesGroupCreateActivity extends BaseTitleActivity implements OnClickListener {
    EditText mNameEditText;
    EditText mMonthPriceEditText;
    EditText mDayPriceEditText;
    String mGroupId;
    JSONObject mPriceJsonObject;
    JSONObject mValueJSONObject;

    @Override
    public int createLayoutRes() {
        return R.layout.salon_special_peoples_layout;
    }

    @Override
    public void initialize(Bundle bundle) {
        super.initialize(bundle);

        mGroupId = getIntent().getStringExtra("groupid");
        //是否是更新操作
        String json = getIntent().getStringExtra("SPECIAL_GROUP_ID");
        if (json != null) {
            mValueJSONObject = JSON.parseObject(json);
        }

        initUI();

        onQueryPriceLimit();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("mGroupId", mGroupId);
        outState.putString("SPECIAL_GROUP_ID", mValueJSONObject.toJSONString());
        if (mPriceJsonObject != null)
            outState.putString("mPriceJsonObject", mPriceJsonObject.toString());
    }

    private void initUI() {
        titleTextV.setText("特殊收费人群");
        titleLeftBtn.setOnClickListener(this);
        setRight("确定", this);
        mNameEditText = (EditText) findViewById(R.id.group_name);
        mMonthPriceEditText = (EditText) findViewById(R.id.month_price);
        mDayPriceEditText = (EditText) findViewById(R.id.day_price);
        if (mValueJSONObject == null) {//更新,修改操作
            findViewById(R.id.delte_btn).setVisibility(View.GONE);
        } else {
            View v = findViewById(R.id.delte_btn);
            v.setVisibility(View.VISIBLE);
            v.setOnClickListener(this);
            mNameEditText.setText(mValueJSONObject.getString("SPECIAL_GROUP"));
            mMonthPriceEditText.setText(mValueJSONObject.getString("SPECIAL_PRICE_MONTH"));
            mDayPriceEditText.setText(mValueJSONObject.getString("SPECIAL_PRICE"));
        }
    }

    /**
     * 查询价格范围
     */
    private void onQueryPriceLimit() {
        if (mPriceJsonObject != null) return;
        RequestParams params = new RequestParams();
        params.put("type", "findPriceMaxAndMin");
        ApiService.doHttpSalonSpecialPriceGroupSet(params, new JsonsfHttpResponseHandler(this) {
            @Override
            public void onSuccess(int statusCode, JSONObject response) {
                mPriceJsonObject = response;
                onParsePriceLimit();
            }
        });
    }

    /**
     * 解析价格范围
     */
    private void onParsePriceLimit() {
        if (mPriceJsonObject == null) return;
        String max = mPriceJsonObject.getString("PRICE_MAX");
        String min = mPriceJsonObject.getString("PRICE_MIN");
        mDayPriceEditText.setHint(min + "-" + max + "元/人");
        mMonthPriceEditText.setHint(min + "-" + max + "元/人");
    }

    class HttpResponseHandler extends JsonsfHttpResponseHandler {
        public HttpResponseHandler() {
            super(SalonSpecialPeoplesGroupCreateActivity.this);
        }

        @Override
        public void onSuccess(JSONArray response) {
            super.onSuccess(response);
            //返回到列表
            Intent data = new Intent();
            data.putExtra("date", response.toJSONString());
            setResult(RESULT_OK, data);
            finish();
        }

        @Override
        public void onSuccess(int statusCode, JSONObject response) {
            super.onSuccess(statusCode, response);
            if (response.containsKey("error_message")) {
                ToastUtil.showShort(response.getString("error_message"));
            }
        }
    }

    /**
     * 添加修改操作
     */
    private void onSubmit() {
        if (mPriceJsonObject == null) return;
        String name = mNameEditText.getText().toString().trim();
        if (name.length() == 0) {
            mNameEditText.setError("名字不能为空");
            return;
        }
        int max = mPriceJsonObject.getIntValue("PRICE_MAX");
        int min = mPriceJsonObject.getIntValue("PRICE_MIN");

        String str = mDayPriceEditText.getText().toString();
        NumberRange range = new NumberRange(min, max);
        if (!NumberUtils.isNumber(str) || !range.containsFloat(NumberUtils.toFloat(str))) {
            ToastUtil.showLong(this, "日票设置错误" + mPriceJsonObject.getString("PRICE_MIN") + "-" + mPriceJsonObject.getString("PRICE_MAX"));
            return;
        }
        String str1 = mMonthPriceEditText.getText().toString();
        if (!NumberUtils.isNumber(str1) || !range.containsFloat(NumberUtils.toFloat(str1))) {
            ToastUtil.showShort("月票设置错误" + mPriceJsonObject.getString("PRICE_MIN") + "-" + mPriceJsonObject.getString("PRICE_MAX"));
            return;
        }
        RequestParams params = new RequestParams();
        params.put("groupId", mGroupId);
        params.put("specialGroup", name);
        params.put("specialPrice", mDayPriceEditText.getText().toString());
        params.put("specialPriceMonth", mMonthPriceEditText.getText().toString());
        if (mValueJSONObject == null) {//添加
            params.put("type", "addSalonSpecialPriceGroup");
        } else {//修改
            params.put("specialGroupId", mValueJSONObject.getString("SPECIAL_GROUP_ID"));
            params.put("type", "updateSalonSpecialPriceGroup");
        }
        ApiService.doHttpSalonSpecialPriceGroupSet(params, new HttpResponseHandler());
//		ApiService.doHttpAddSpecialGroup(mGroupId,name,mDayPriceEditText.getText().toString(),mMonthPriceEditText.getText().toString(),new HttpResponseHandler());
    }
/*	private void onChange(){
//		specialGroupId 话题特殊收费人群id specialGroup 人群名称 specialPrice 收费人群价格 customerId 客户id type=updateSalonSpecialPriceGroup
		RequestParams params  = new RequestParams();
		params.put("specialGroupId",mValueJSONObject.getString("SPECIAL_GROUP_ID"));
		params.put("customerId",SmartFoxClient.getLoginUserId());
		params.put("type","deleteSalonSpecialPriceGroup");
		ApiService.doHttpSalonSpecialPriceGroupSet(params,new HttpResponseHandler());
	}*/

    /**
     * 删除操作
     */
    private void onDelete() {
        RequestParams params = new RequestParams();
        params.put("specialGroupId", mValueJSONObject.getString("SPECIAL_GROUP_ID"));
        params.put("customerId", SmartFoxClient.getLoginUserId());
        params.put("type", "deleteSalonSpecialPriceGroup");
        ApiService.doHttpSalonSpecialPriceGroupSet(params, new HttpResponseHandler());
        //doHttpSalonSpecialPriceGroupSet
//		specialGroupId 话题特殊收费人群id customerId 客户id type=deleteSalonSpecialPriceGroup
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_back:
                setResult(RESULT_CANCELED);
                finish();
                break;
            case R.id.title_right2://创建
                onSubmit();
                break;
            case R.id.delte_btn://删除
                DoubleBtnFragmentDialog.showDefault(getSupportFragmentManager(),

                        "删除该特殊人群后，群内的成员将不再享受特殊收费价格。确认要删除(" + mValueJSONObject.getString("SPECIAL_GROUP") + ")人群吗",
                        "放弃",
                        "确定",
                        new DoubleBtnFragmentDialog.OnDilaogClickListener() {
                            @Override
                            public void onDismiss(DialogFragment fragment) {
                            }

                            @Override
                            public void onClick(DialogFragment fragment, View v) {
                                onDelete();
                            }
                        });
                break;
        }
    }

}
