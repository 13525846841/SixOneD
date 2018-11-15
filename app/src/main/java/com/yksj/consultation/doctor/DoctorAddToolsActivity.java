package com.yksj.consultation.doctor;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.library.base.base.BaseActivity;
import com.yksj.consultation.comm.DoubleBtnFragmentDialog;
import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.sonDoc.consultation.main.CommonwealAidAty;
import com.yksj.healthtalk.net.http.ApiCallbackWrapper;
import com.yksj.healthtalk.net.http.ApiService;
import com.yksj.healthtalk.net.http.HttpResult;
import com.yksj.healthtalk.net.socket.SmartFoxClient;
import com.yksj.healthtalk.utils.HStringUtil;
import com.yksj.healthtalk.utils.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * 工具箱-添加
 */
public class DoctorAddToolsActivity extends BaseActivity {

    private Button close;//关闭
    private Button delete;//删除
    public static final String TYPE = "TYPE";
    public static final String LOGO = "LOGO";
    public String type = "";
    public EditText toolName;
    public EditText toolAddress;
    public String tool_name;
    public String tool_url;
    private String used_flag;//是否启用   1用，0停
    private String typeValue;//1 添加 2修改 3删除

    private String doctor_Id = SmartFoxClient.getLoginUserId();
    public boolean isClose = false;
    public String tool_code = "";
    private String logo;

    public static Intent getCallingIntent(Context context, String type){
        Intent intent = new Intent(context, DoctorAddToolsActivity.class);
        intent.putExtra(TYPE, type);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_utils_box_acivity);
        initView();
    }

    private void initView() {
        initializeTitle();
        titleTextV.setText("工具箱");
        titleLeftBtn.setOnClickListener(this);
        titleRightBtn2.setVisibility(View.VISIBLE);
        titleRightBtn2.setText("完成");
        titleRightBtn2.setOnClickListener(this);

        close = (Button) findViewById(R.id.close);
        delete = (Button) findViewById(R.id.delect);
        toolName = (EditText) findViewById(R.id.tool_name);
        toolAddress = (EditText) findViewById(R.id.tool_address);


        if (getIntent().hasExtra(TYPE)) type = getIntent().getStringExtra(TYPE);

        if ("add".equals(type)) {
            close.setVisibility(View.GONE);
            delete.setVisibility(View.GONE);
            typeValue = "1";
        } else if ("set".equals(type)) {
            close.setOnClickListener(this);
            delete.setOnClickListener(this);
            toolName.setText(getIntent().getStringExtra("TOOL_NAME"));
            toolAddress.setText(getIntent().getStringExtra("TOOL_URL"));
            toolAddress.setFocusable(false);
            toolAddress.setOnClickListener(this);
            tool_code = getIntent().getStringExtra("TOOL_CODE");
            typeValue = "2";
//            logo = getIntent().getStringExtra(LOGO);
//            if (("0").equals(logo)){
//                close.setText("开 通");
//            }else if (("1").equals(logo)){
//                close.setText("关 闭");
//            }
        }
        findViewById(R.id.copy).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_back:
                onBackPressed();
                break;
            case R.id.title_right2:
//                Intent intent = new Intent();
//                intent.putExtra("ISCLOSE",isClose);
//                setResult(RESULT_OK,intent);
                AddData();
                break;
            case R.id.tool_address:

                Intent intent = null;
                intent = new Intent(DoctorAddToolsActivity.this, CommonwealAidAty.class);
                intent.putExtra(CommonwealAidAty.URL, getIntent().getStringExtra("TOOL_URL"));
                intent.putExtra(CommonwealAidAty.TITLE, getIntent().getStringExtra("TOOL_NAME"));
                startActivity(intent);
                break;
            case R.id.copy://复制
                copyWord(toolAddress.getText().toString());
                break;
            case R.id.close:
//                if (logo.equals("0")){
//                    DoubleBtnFragmentDialog.showDefault(getSupportFragmentManager(), "你确定要开通吗？", "取消", "确定", new DoubleBtnFragmentDialog.OnDilaogClickListener() {
//                        @Override
//                        public void onDismiss(DialogFragment fragment) {
//                        }
//                        @Override
//                        public void onClick(DialogFragment fragment, View v) {
//                            StopData("0");
//                        }
//                    });
//                }else if (logo.equals("1")){
//                    DoubleBtnFragmentDialog.showDefault(getSupportFragmentManager(), "你确定要关闭吗？", "取消", "确定", new DoubleBtnFragmentDialog.OnDilaogClickListener() {
//                        @Override
//                        public void onDismiss(DialogFragment fragment) {
//                        }
//                        @Override
//                        public void onClick(DialogFragment fragment, View v) {
//                            StopData("1");
//                        }
//                    });
//                }
                finish();
                break;

            case R.id.delect:
                DoubleBtnFragmentDialog.showDefault(getSupportFragmentManager(), "你确定要删除吗？", "取消", "确定", new DoubleBtnFragmentDialog.OnDilaogClickListener() {
                    @Override
                    public void onDismiss(DialogFragment fragment) {
                    }

                    @Override
                    public void onClick(DialogFragment fragment, View v) {
                        DelectData();
                    }
                });
                break;
        }
    }

    /**
     * 关闭的数据 or 开通数据
     */
    private void StopData(String used_flag1) {
        used_flag = used_flag1; //1 开通，0 关闭
        if (used_flag == "1") {
            isClose = true;
        } else if (used_flag == "0") {
            isClose = false;
        }
        tool_name = toolName.getText().toString().trim();
        tool_url = toolAddress.getText().toString().trim();
        typeValue = "2";
        if (TextUtils.isEmpty(tool_name)) {
            ToastUtil.showToastPanl("请填写工具名称");
            return;
        }
        if (TextUtils.isEmpty(tool_url)) {
            ToastUtil.showToastPanl("请填写链接地址");
            return;
        }
        if (!tool_url.startsWith("https://") || !tool_url.startsWith("http://") || !tool_url.startsWith("ftp://")) {
            ToastUtil.showToastPanl("请填写正确的链接地址");
            return;
        }
        Map<String, String> map = new HashMap<>();
        map.put("op", "addTool");
        map.put("doctor_id", doctor_Id);
        map.put("tool_code", tool_code);//工具id
        map.put("tool_url", tool_url);
        map.put("used_flag", used_flag);//used_flag
        map.put("tool_name", tool_name);
        map.put("type", typeValue);
        map.put("consultation_center_id", "5");//consultation_center_id

        ApiService.OKHttpIsConsultation(map, new ApiCallbackWrapper<String>(this) {
            @Override
            public void onResponse(String response) {
                super.onResponse(response);
                if (!HStringUtil.isEmpty(response)) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (HttpResult.SUCCESS.equals(jsonObject.optString("code"))) {
                            ToastUtil.showShort(jsonObject.optString("message"));
                            Intent intent = new Intent();
                            intent.putExtra("ISCLOSE", isClose);
                            setResult(RESULT_OK, intent);
                            finish();
                        } else {
                            ToastUtil.showShort(jsonObject.optString("message"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, this);
    }

    /**
     * 删除数据
     */
    private void DelectData() {
        tool_name = toolName.getText().toString().trim();
        tool_url = toolAddress.getText().toString().trim();
        typeValue = "3";
        if (TextUtils.isEmpty(tool_name)) {
            ToastUtil.showToastPanl("请填写工具名称");
            return;
        }
        if (TextUtils.isEmpty(tool_url)) {
            ToastUtil.showToastPanl("请填写链接地址");
            return;
        }
        Map<String, String> map = new HashMap<>();
        map.put("op", "addTool");
        map.put("doctor_id", doctor_Id);
        map.put("tool_code", tool_code);//工具id
        map.put("tool_url", tool_url);
        map.put("used_flag", "1");//used_flag
        map.put("tool_name", tool_name);
        map.put("type", typeValue);
        map.put("consultation_center_id", "5");//consultation_center_id

        ApiService.OKHttpIsConsultation(map, new ApiCallbackWrapper<String>(this) {
            @Override
            public void onResponse(String response) {
                super.onResponse(response);
                if (!HStringUtil.isEmpty(response)) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (HttpResult.SUCCESS.equals(jsonObject.optString("code"))) {
                            ToastUtil.showShort(jsonObject.optString("message"));
                            finish();
                        } else {
                            ToastUtil.showShort(jsonObject.optString("message"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, this);
    }

    /**
     * 添加数据
     */
    private void AddData() {
        tool_name = toolName.getText().toString().trim();
        tool_url = toolAddress.getText().toString().trim();
        //  typeValue = "1";
        if (TextUtils.isEmpty(tool_name)) {
            ToastUtil.showToastPanl("请填写工具名称");
            return;
        }
        if (TextUtils.isEmpty(tool_url)) {
            ToastUtil.showToastPanl("请填写链接地址");
            return;
        }

        if (!Patterns.WEB_URL.matcher(tool_url).matches()) {
            ToastUtil.showToastPanl("请填写正确的网络链接地址");
            return;
        }

//        if(!tool_url.startsWith("https")){
//            ToastUtil.showToastPanl("请填写以https开头的链接地址");
//            return;
//        }

        Map<String, String> map = new HashMap<>();
        map.put("op", "addTool");
        map.put("doctor_id", doctor_Id);
        map.put("tool_code", tool_code);//工具id
        map.put("tool_url", tool_url);
        // map.put("used_flag", "1");//used_flag
        map.put("type", typeValue);
        map.put("tool_name", tool_name);
        map.put("consultation_center_id", "5");//consultation_center_id

        ApiService.OKHttpIsConsultation(map, new ApiCallbackWrapper<String>(this) {
            @Override
            public void onResponse(String response) {
                super.onResponse(response);
                if (!HStringUtil.isEmpty(response)) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (HttpResult.SUCCESS.equals(jsonObject.optString("code"))) {
                            ToastUtil.showShort(jsonObject.optString("message"));
                            Intent intent = new Intent();
                            intent.putExtra("ISCLOSE", isClose);
                            setResult(RESULT_OK, intent);
                            finish();
                        } else {
                            ToastUtil.showShort(jsonObject.optString("message"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, this);
    }

    private void copyWord(String url) {
        ClipboardManager myClipboard;
        myClipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData myClip;
        myClip = ClipData.newPlainText("text", url);//text是内容
        myClipboard.setPrimaryClip(myClip);
        ToastUtil.showShort("复制成功，可以发给朋友们了");
    }
}
