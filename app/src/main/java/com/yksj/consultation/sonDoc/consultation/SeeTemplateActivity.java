package com.yksj.consultation.sonDoc.consultation;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

import okhttp3.Request;;
import com.yksj.consultation.adapter.SeeTemplateAdapter;
import com.library.base.base.BaseActivity;
import com.yksj.consultation.sonDoc.R;
import com.yksj.healthtalk.net.http.ApiCallbackWrapper;
import com.yksj.healthtalk.net.http.ApiService;
import com.yksj.healthtalk.utils.HStringUtil;
import com.yksj.healthtalk.utils.TimeUtil;
import com.yksj.healthtalk.utils.ToastUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 查看随访计划
 */
public class SeeTemplateActivity extends BaseActivity {
    public final static String TYPE = "TYPE";
    public static int LOOKTYPE = 1;//1 不可修改 2 可修改
    private ListView mListView;
    private SeeTemplateAdapter adapter;
    private TextView title;
    private static String follow_id;
    private List<JSONObject> mList;
    private TextView temp_time;//随访开始时间
    private ToggleButton remindme;//提醒我
    private ToggleButton remindcus;//提醒患者
   // private ToggleButton cusseeplan;//患者是否可见
    private TextView remindtime;//提醒时间
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_see_template);
        initView();
    }

    private void initView() {
        initializeTitle();
        titleTextV.setText("随访计划");
        titleLeftBtn.setOnClickListener(this);

        title = (TextView) findViewById(R.id.template_title);
        temp_time = (TextView) findViewById(R.id.temp_time);
        remindme = (ToggleButton) findViewById(R.id.remindme);
        remindcus = (ToggleButton) findViewById(R.id.remindcus);

        remindtime = (TextView) findViewById(R.id.remind_time);

        if (getIntent().hasExtra("follow_id"))
            follow_id = getIntent().getStringExtra("follow_id");

        mListView = (ListView) findViewById(R.id.followuplist);
        adapter = new SeeTemplateAdapter(this);
        mListView.setAdapter(adapter);
        initPlanData();
//        if (getIntent().hasExtra("type")) {//查看数据
//            initPlanData();
//        } else if (getIntent().hasExtra("type1")) { //保存计划
//            titleRightBtn2.setVisibility(View.VISIBLE);
//            titleRightBtn2.setText("保存");
//            titleRightBtn2.setOnClickListener(this);
//            initTemplate();
//        }
    }
    /**
     * 查看计划
     */
    private void initPlanData() {
        Map<String, String> map = new HashMap<>();
        map.put("follow_id", follow_id);
        ApiService.OKHttpFindFollowSubListById(map, new ApiCallbackWrapper<String>(this) {

            @Override
            public void onError(Request request, Exception e) {
                ToastUtil.showShort("查询失败");
            }

            @Override
            public void onResponse(String content) {
                try {
                    JSONObject obj = new JSONObject(content);
                    if ("0".equals(obj.optString("code"))) {
                        mList = new ArrayList<>();
                        JSONArray array = obj.getJSONArray("sbus");
                        JSONObject item;
                        for (int i = 0; i < array.length(); i++) {
                            item = array.getJSONObject(i);
                            mList.add(item);
                        }
                        adapter.onBoundData(mList);

                        JSONObject object = obj.getJSONObject("follow");
                        title.setText(object.optString("FOLLOW_UP_NAME"));
                        if (!HStringUtil.isEmpty(object.optString("CREATE_TIME"))){
                            temp_time.setText(TimeUtil.getFormatDate2(object.optString("CREATE_TIME")));
                        }


                        if ("1".equals(object.optString("ALERT_ME"))) {
                            remindme.setChecked(true);
                        } else if ("0".equals(object.optString("ALERT_ME")) ) {
                            remindme.setChecked(false);
                        }

                        if ("1".equals(object.optString("ALERT_SICK"))) {
                            remindcus.setChecked(true);
                        } else if ("0".equals(object.optString("ALERT_SICK"))) {
                            remindcus.setChecked(false);
                        }


                        if ("10".equals(object.optString("ALERT_TIMETYPE"))) {
                            remindtime.setText("提前" + object.optString("ALERT_TIMECOUNT") + "天");
                        } else if ("20".equals(object.optString("ALERT_TIMETYPE"))) {
                            remindtime.setText("提前" + object.optString("ALERT_TIMECOUNT") + "周");
                        } else if ( "30".equals(object.optString("ALERT_TIMETYPE"))) {
                            remindtime.setText("提前" + object.optString("ALERT_TIMECOUNT") + "月");
                        } else if ("40".equals(object.optString("ALERT_TIMETYPE"))) {
                            remindtime.setText("提前" + object.optString("ALERT_TIMECOUNT") + "年");
                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, this);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_back:
                onBackPressed();
                break;
        }
    }
}
