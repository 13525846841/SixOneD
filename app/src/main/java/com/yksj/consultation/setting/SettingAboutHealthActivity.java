package com.yksj.consultation.setting;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.NetworkUtils;
import com.library.base.base.BaseActivity;
import com.yksj.consultation.app.AppContext;
import com.yksj.consultation.app.AppUpdateManager;
import com.yksj.consultation.sonDoc.R;
import com.library.base.utils.ResourceHelper;
import com.yksj.healthtalk.net.http.ApiCallbackWrapper;
import com.yksj.healthtalk.net.http.ApiService;
import com.yksj.healthtalk.utils.SystemUtils;
import com.yksj.healthtalk.utils.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * 关于六一健康
 *
 * @author root
 */
public class SettingAboutHealthActivity extends BaseActivity implements OnClickListener {
    private TextView mVersion;
    String version;//版本号

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.setting_about_health_layout);
        initView();
    }

    private void initView() {
        initializeTitle();
        titleLeftBtn.setOnClickListener(this);
        titleTextV.setText("关于我们");
        //SystemUtils.getAppVersionName(SettingAboutHealthActivity.this)
        findViewById(R.id.setting_grade).setOnClickListener(this);
        findViewById(R.id.tv_version_number).setOnClickListener(this);
        findViewById(R.id.rl_version_number).setOnClickListener(this);
//		findViewById(R.id.setting_update).setOnClickListener(this);
        findViewById(R.id.setting_make).setOnClickListener(this);
        findViewById(R.id.setting_jieshao).setOnClickListener(this);
        mVersion = (TextView) findViewById(R.id.tv_version_number);
        PackageManager manager = this.getPackageManager();
        PackageInfo info = null;
        try {
            info = manager.getPackageInfo(getPackageName(),0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        version = info.versionName;
        mVersion.setText(version);

        initData();
    }

    private String url;
    /**
     * 查询版本说明URL
     */
    private void initData() {
        Map<String, String> map = new HashMap<>();
        map.put("version", AppUtils.getAppVersionName());//doctor_id
        map.put("device", "Android");//doctor_id
        map.put("client_type", AppContext.CLIENT_TYPE );//doctor_id

        ApiService.OKHttpFindVersionInfoServlet(map, new ApiCallbackWrapper<String>(this) {
            @Override
            public void onResponse(String response) {
                super.onResponse(response);
                try {
                    JSONObject obj = new JSONObject(response);
                    if ("1".equals(obj.optString("code"))) {
                        url = obj.optString("url");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, this);
    }
    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.title_back:
                finish();
                break;
            case R.id.setting_grade://给六一健康平分吧
//			ToastUtil.showShort("当前为公测版本，请您下载正式版本。");
                SystemUtils.commentGood(this);
                break;
//		case R.id.setting_update://检查更新
////			ToastUtil.showShort("当前为公测版本，请您下载正式版本。");
//			updateApp();
//			break;
//            case R.id.tv_version_number://更新
////			ToastUtil.showShort("当前为公测版本，请您下载正式版本。");
//                updateApp();
//                break;
            case R.id.rl_version_number://更新
                updateApp();
                break;
            case R.id.setting_make://用户条款与隐私协议
                intent = new Intent(this, SettingWebUIActivity.class);
                intent.putExtra("title", "用户协议与隐私条款");
                intent.putExtra("url", ResourceHelper.getString(R.string.agent_path_1));
                startActivity(intent);
//			ToastUtil.showShort("当前为公测版本，请您下载正式版本。");
                break;
            case R.id.setting_jieshao://版本说明
                intent = new Intent(this, SettingWebUIActivity.class);
                intent.putExtra("title", "版本说明");
                intent.putExtra("TextSize", 100);
//                intent.putExtra("url", AppContext.getApplication().getFunctionIntroduction());
                intent.putExtra("url", AppContext.getApiRepository().VERSIONHTML+url);
                startActivity(intent);
                break;
        }
    }

    /**
     * 更新程序
     */
    private void updateApp() {
        if (!NetworkUtils.isConnected()) {
            ToastUtil.showToastPanl("网络无效");
            return;
        }
        AppUpdateManager.getInstance().checkeUpdate(this, true);
    }
}
