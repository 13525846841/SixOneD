package com.yksj.consultation.im;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.blankj.utilcode.util.LogUtils;
import com.library.base.base.BaseTitleActivity;
import com.yksj.consultation.adapter.AddTmpPlanAdapter;
import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.sonDoc.listener.TemplateOnClickListener;
import com.yksj.consultation.utils.DoctorHelper;
import com.yksj.healthtalk.net.http.ApiCallbackWrapper;
import com.yksj.healthtalk.net.http.ApiService;
import com.yksj.healthtalk.utils.TimeUtil;
import com.yksj.healthtalk.utils.ToastUtil;
import com.yksj.healthtalk.utils.WheelUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Request;

/**
 * 添加随访计划
 */
public class AddTmpPlanActivity extends BaseTitleActivity implements CompoundButton.OnCheckedChangeListener, TemplateOnClickListener {

    public final static String TYPE = "TYPE";
    public static int LOOKTYPE = 1;//1 不可修改 2 可修改
    private ListView mListView;
    private AddTmpPlanAdapter adapter;
    private String follow_id;
    private List<JSONObject> mList;
    private TextView temp_time;//随访开始时间
    private ToggleButton remindme;//提醒我
    private ToggleButton remindcus;//提醒患者
    private ToggleButton cusseeplan;//患者是否可见
    private TextView remindtime;//提醒时间

    private String customer_id;
    private String doctor_id = DoctorHelper.getId();
    private String mTemplateName;
    private PopupWindow birPop;

    private List<JSONObject> list = new ArrayList<>();//存储的内容
    private JSONObject object = new JSONObject();
    private Map<String,String> map=new HashMap<>();
    public int pos;
    private View wheelView;
    private View mainView;
    PopupWindow mPopupWindow, mAddressWindow;
    private List<Map<String, String>> contentList = null;
    private List<Map<String, String>> numberList = null;
    private List<Map<String, String>> mUnit = null;
    private String remindContent;//提醒内容
    private String remindTime;//提醒时间
    public  TextView template_time;

    private boolean REMINDTYPE = true;

    private String alert_timeCount;//提示时间
    private String alert_timeType;//提醒时间类型，天，周，月，年
    private String tempTime;//随访开始时间

    @Override
    public int createLayoutRes() {
        return R.layout.activity_add_tmp_plan;
    }

    @Override
    public void initialize(Bundle bundle) {
        super.initialize(bundle);
        setTitle("随访计划");
        initView();
    }

    private void initView() {
        findViewById(R.id.rl_temp_time).setOnClickListener(this);
        findViewById(R.id.rl_remind_time).setOnClickListener(this);
        temp_time = (TextView) findViewById(R.id.temp_time);

        remindme = (ToggleButton) findViewById(R.id.remindme);
        remindcus = (ToggleButton) findViewById(R.id.remindcus);
        cusseeplan = (ToggleButton) findViewById(R.id.cusseeplan);
        remindme.setOnCheckedChangeListener(this);
        remindcus.setOnCheckedChangeListener(this);
        cusseeplan.setOnCheckedChangeListener(this);

        remindtime = (TextView) findViewById(R.id.remind_time);
        setRight("保存", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddTemplatePlan();
            }
        });

        if (getIntent().hasExtra("follow_id"))
            follow_id = getIntent().getStringExtra("follow_id");
            customer_id = getIntent().getStringExtra("customer_id");

        mListView = (ListView) findViewById(R.id.followuplist);
        adapter = new AddTmpPlanAdapter(this,this);
        mListView.setAdapter(adapter);


        wheelView = getLayoutInflater().inflate(R.layout.wheel, null);
        wheelView.findViewById(R.id.wheel_cancel).setOnClickListener(this);
        wheelView.findViewById(R.id.wheel_sure).setOnClickListener(this);
        mPopupWindow = new PopupWindow(wheelView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mAddressWindow = new PopupWindow(wheelView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mainView = getLayoutInflater().inflate(R.layout.activity_new_template_aty, null);

        initTemplate();
    }

    /**
     * 查看模板
     */
    private void initTemplate() {
        Map<String, String> map = new HashMap<>();
        map.put("template_id", follow_id);
        ApiService.OKHttpFindSubFollowTemplate(map, new ApiCallbackWrapper<String>(this) {
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
                        JSONObject object = obj.getJSONObject("template");
                        JSONArray array = object.optJSONArray("subs");
                        JSONObject item;
                        for (int i = 0; i < array.length(); i++) {
                            item = array.getJSONObject(i);
                            mList.add(item);
                        }
                        adapter.onBoundData(mList);
                        mTemplateName = object.optJSONObject("template").optString("TEMPLATE_NAME");
                        setTitle(mTemplateName);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_temp_time:
                initTime();
                break;
            case R.id.rl_remind_time:
                remindTime();
                break;
            case R.id.wheel_cancel:
                if (mAddressWindow != null)
                    mAddressWindow.dismiss();
                break;
            case R.id.wheel_sure:
                if (REMINDTYPE) {
                    if (mAddressWindow != null)
                        mAddressWindow.dismiss();
                    if (WheelUtils.getCurrent() != null) {
                        try {
                            if (WheelUtils.getCurrent2().equals("天")) {
                                adapter.datas.get(pos).put("TEMPLATE_SUB_TIMETYPE", "10");
                            } else if (WheelUtils.getCurrent2().equals("周")) {
                                adapter.datas.get(pos).put("TEMPLATE_SUB_TIMETYPE", "20");
                            } else if (WheelUtils.getCurrent2().equals("月")) {
                                adapter.datas.get(pos).put("TEMPLATE_SUB_TIMETYPE", "30");
                            } else if (WheelUtils.getCurrent2().equals("年")) {
                                adapter.datas.get(pos).put("TEMPLATE_SUB_TIMETYPE", "40");
                            }

                            adapter.datas.get(pos).put("TIMETYPE_COUNT", WheelUtils.getCurrent1());
                            adapter.notifyDataSetChanged();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    if (mAddressWindow != null)
                        mAddressWindow.dismiss();
                    if (WheelUtils.getCurrent() != null) {
                        alert_timeCount = WheelUtils.getCurrent1();
                        remindtime.setText(WheelUtils.getCurrentt());
                        if (WheelUtils.getCurrent2().equals("天")) {
                            alert_timeType = "10";
                        } else if (WheelUtils.getCurrent2().equals("周")) {
                            alert_timeType = "20";
                        } else if (WheelUtils.getCurrent2().equals("月")) {
                            alert_timeType = "30";
                        } else if (WheelUtils.getCurrent2().equals("年")) {
                            alert_timeType = "40";
                        }
                    }
                }
                break;

        }
    }

    /**
     * 提醒时间
     */
    private void remindTime() {

        REMINDTYPE = false;
        numberList = new ArrayList<Map<String, String>>();
        mUnit = new ArrayList<Map<String, String>>();

        if (mAddressWindow != null && mAddressWindow.isShowing()) {
            mAddressWindow.dismiss();
        }
        String[] number = new String[50];
        for (int i = 0; i < 50; i++) {
            number[i] = i + "";
        }

        for (int i = 0; i < number.length; i++) {
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("name", number[i]);
            numberList.add(map);
        }

        String[] contentUnit = {"天", "周", "月", "年"};
        for (int i = 0; i < contentUnit.length; i++) {
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("name", contentUnit[i]);
            mUnit.add(map);
        }

        WheelUtils.setDoubleWheel1(AddTmpPlanActivity.this, numberList, mUnit, mainView, mAddressWindow,
                wheelView);
    }


    /**
     * 获取提醒时间
     */
    private void initTime() {
        if(birPop == null ){
            birPop= WheelUtils.showThreeDateWheel(this, getLayoutInflater().inflate(R.layout.activity_add_tmp_plan, null), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (v.getId()) {
                        case R.id.wheel_sure_age:
                            String[] str = (String[]) v.getTag();
                            tempTime = TimeUtil.getFormatDate3(str[0] + str[1] + str[2]);
                            temp_time.setText(str[0] + str[1] + str[2]);

                            break;
                    }
                }
            });
        }else if(birPop.isShowing()){
            birPop.dismiss();
        }else{
            birPop.showAtLocation(getLayoutInflater().inflate(R.layout.activity_add_tmp_plan, null), Gravity.BOTTOM, 0, 0);
        }
    }

    /**
     * 添加随访计划给患者
     */
    private void AddTemplatePlan() {
        JSONArray array=new JSONArray();

        for (int j = 0; j <adapter.datas.size() ; j++) {
            JSONObject object = new JSONObject();

            LogUtils.d("M++++++++==========",String.valueOf(adapter.datas.get(j).optString("TIMETYPE_COUNT")));
            LogUtils.d("OM+++++++=========",String.valueOf(adapter.datas.get(j).optString("TEMPLATE_SUB_TIMETYPE")));
            LogUtils.d("OMOM+++++==========",String.valueOf(adapter.datas.get(j).optString("TEMPLATE_SUB_CONTENT")));

            try {
                object.put("follow_seq",j);
                object.put("timetype_count",adapter.datas.get(j).optString("TIMETYPE_COUNT"));
                object.put("follow_sub_timetype",adapter.datas.get(j).optString("TEMPLATE_SUB_TIMETYPE"));
                object.put("follow_content",adapter.datas.get(j).optString("TEMPLATE_SUB_CONTENT"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
           array.put(object);
        }

        if (TextUtils.isEmpty(alert_timeCount)) {
            ToastUtil.showToastPanl("请填写提醒时间");
            return;
        }
        if (TextUtils.isEmpty(alert_timeType)) {
            ToastUtil.showToastPanl("请填写提醒时间");
            return;
        }
        if (TextUtils.isEmpty(tempTime)) {
            ToastUtil.showToastPanl("请填写开始时间");
            return;
        }

        Map<String, String> map = new HashMap<>();
        map.put("op", "addFollow");
        map.put("alert_me", sRemindme);//提醒医生
        map.put("alert_sick", sRemindcus);//提醒患者
        map.put("alert_timecount", alert_timeCount);
        map.put("alert_timetype", alert_timeType);
        map.put("customer_id", customer_id);//
        map.put("doctor_id", doctor_id);//
        map.put("sick_see_flag", sCusseeplan);//患者可见不可见
        map.put("template_id", follow_id);//模板ID
        map.put("template_name", mTemplateName);//模板名称
        map.put("createtime",tempTime);//tempTime
        map.put("data", array.toString());

        ApiService.OKHttpAddFollow(map, new ApiCallbackWrapper<String>(this) {

            @Override
            public void onResponse(String content) {
                try {
                    JSONObject obj = new JSONObject(content);
                    if ("0".equals(obj.optString("code"))) {
                       ToastUtil.showShort(obj.optString("message"));
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },this);
    }

    private String sRemindme = "0";//提醒我
    private String sRemindcus = "0";//提醒患者
    private String sCusseeplan = "0";//患者可见不可见
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()){
            case R.id.remindme:
                if (isChecked){
                    sRemindme = "1";
                }else {
                    sRemindme = "0";
                }
                break;
            case R.id.remindcus:
                if (isChecked){
                    sRemindcus = "1";
                }else {
                    sRemindcus = "0";
                }
                break;
            case R.id.cusseeplan:
                if (isChecked){
                    sCusseeplan = "1";
                }else {
                    sCusseeplan = "0";
                }
                break;

        }
    }

    @Override
    public void onStarClick(View view, int position, int id) {
        pos = position;
        switch (id) {
            case R.id.rl_consul:
                template_time = (TextView) view.findViewById(R.id.template_time);
                RelativeLayout times = (RelativeLayout) view.findViewById(R.id.rl_consul);

                numberList = new ArrayList<Map<String, String>>();
                mUnit = new ArrayList<Map<String, String>>();

                if (mAddressWindow != null && mAddressWindow.isShowing()) {
                    mAddressWindow.dismiss();
                }
                String[] number = new String[50];
                for (int i = 0; i < 50; i++) {
                    number[i] = i + "";
                }
                for (int i = 0; i < number.length; i++) {
                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put("name", number[i]);
                    numberList.add(map);
                }
                String[] contentUnit = {"天", "周", "月", "年"};
                for (int i = 0; i < contentUnit.length; i++) {
                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put("name", contentUnit[i]);
                    mUnit.add(map);
                }
                WheelUtils.setDoubleWheel1(AddTmpPlanActivity.this, numberList, mUnit, mainView, mAddressWindow,
                        wheelView);
                break;
            case R.id.rl_add_template:

                RelativeLayout template = (RelativeLayout) view.findViewById(R.id.rl_add_template);
                final TextView template_content = (TextView) view.findViewById(R.id.template_content);

                contentList = new ArrayList<Map<String, String>>();
                String[] content = {"复诊提醒", "用药提醒", "换药提醒", "手术提醒", "其他"};
                for (int i = 0; i < content.length; i++) {
                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put("name", content[i]);
                    contentList.add(map);
                }
                mPopupWindow = WheelUtils.showSingleWheel(AddTmpPlanActivity.this, contentList, template_content, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int index1 = (Integer) v.getTag(R.id.wheel_one);
                        Map<String, String> map1 = contentList.get(index1);
                        remindContent = map1.get("name");
                        template_content.setText(remindContent);

                        List<Map<String, String>> map2 = adapter.list;
                        try {
                            adapter.datas.get(pos).put("TEMPLATE_SUB_CONTENT", remindContent);
                            List<Map<String, String>> map3 = adapter.list;
                            adapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        //map.put("template_sub_content", remindContent);
                    }
                });

                break;
        }

    }
}
