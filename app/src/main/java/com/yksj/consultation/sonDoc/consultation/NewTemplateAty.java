package com.yksj.consultation.sonDoc.consultation;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.LogUtils;
import com.library.base.base.BaseActivity;
import com.yksj.consultation.adapter.NewTemplateAtyAdapter;
import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.sonDoc.listener.TemplateOnClickListener;
import com.yksj.consultation.utils.DoctorHelper;
import com.yksj.healthtalk.net.http.ApiCallback;
import com.yksj.healthtalk.net.http.ApiService;
import com.yksj.healthtalk.utils.HStringUtil;
import com.yksj.healthtalk.utils.LogUtil;
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

;

/**
 * 新建模块界面
 */
public class NewTemplateAty extends BaseActivity implements TemplateOnClickListener {
    private ListView mListView;
    private NewTemplateAtyAdapter adapter;
    private EditText mTemplateName;

    private View wheelView;
    private View mainView;
    PopupWindow mPopupWindow, mAddressWindow;
    private List<Map<String, String>> contentList = null;
    private List<Map<String, String>> numberList = null;
    private List<Map<String, String>> mUnit = null;
    private String remindContent;//提醒内容
    private String remindTime;//提醒时间

    private List<JSONObject> list = new ArrayList<>();//存储的内容
    private JSONObject object = new JSONObject();
    private Map<String, String> map = new HashMap<>();
    public int pos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_template_aty);
        initView();
    }

    private void initView() {
        initializeTitle();
        titleTextV.setText("新建模板");
        titleLeftBtn.setOnClickListener(this);
        titleRightBtn2.setVisibility(View.VISIBLE);
        titleRightBtn2.setText("保存");
        titleRightBtn2.setOnClickListener(this);
        mTemplateName = (EditText) findViewById(R.id.et_template_name);
        findViewById(R.id.add_plan_item).setOnClickListener(this);
        mListView = (ListView) findViewById(R.id.newfollowuplist);
        adapter = new NewTemplateAtyAdapter(this, this);
        mListView.setAdapter(adapter);

        wheelView = getLayoutInflater().inflate(R.layout.wheel, null);
        wheelView.findViewById(R.id.wheel_cancel).setOnClickListener(this);
        wheelView.findViewById(R.id.wheel_sure).setOnClickListener(this);
        mPopupWindow = new PopupWindow(wheelView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mAddressWindow = new PopupWindow(wheelView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mainView = getLayoutInflater().inflate(R.layout.activity_new_template_aty, null);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_back:
                onBackPressed();
                break;
            case R.id.title_right2:
                saveTamplate();
                break;
            case R.id.add_plan_item:
                addMod();
                break;
            case R.id.wheel_cancel:
                if (mAddressWindow != null)
                    mAddressWindow.dismiss();
                break;
            case R.id.wheel_sure:
                if (mAddressWindow != null)
                    mAddressWindow.dismiss();
                if (WheelUtils.getCurrent() != null) {
                    remindTime = WheelUtils.getCurrentt();
                    if (WheelUtils.getCurrent2().equals("天")) {
                        adapter.list.get(pos).put("template_sub_timetype", "10");
                    } else if (WheelUtils.getCurrent2().equals("周")) {
                        adapter.list.get(pos).put("template_sub_timetype", "20");
                    } else if (WheelUtils.getCurrent2().equals("月")) {
                        adapter.list.get(pos).put("template_sub_timetype", "30");
                    } else if (WheelUtils.getCurrent2().equals("年")) {
                        adapter.list.get(pos).put("template_sub_timetype", "40");
                    }
                    adapter.list.get(pos).put("timetype_count", WheelUtils.getCurrent1());
                    adapter.notifyDataSetChanged();
                }
                break;
        }
    }

    /**
     * 添加新建模板
     */
    private void addMod() {
        Map<String, String> map = new HashMap<>();
        map.put("template_seq", String.valueOf(pos));
        map.put("timetype_count", "");
        map.put("template_sub_timetype", "");
        map.put("template_sub_content", "");
        adapter.list.add(map);
        adapter.notifyDataSetChanged();
    }


    private String customer_id = DoctorHelper.getId();

    /**
     * 保存
     */
    private void saveTamplate() {
        String name = mTemplateName.getText().toString().trim();
        if (TextUtils.isEmpty(name)) {
            ToastUtil.showToastPanl("请填写模板名称");
            return;
        }

        JSONArray array = new JSONArray();
        int count = adapter.list.size();


        for (int i = 0; i < count; i++) {
            JSONObject object = new JSONObject();
            LogUtils.d("M++++++++++++==============", String.valueOf(adapter.list.get(i).get("timetype_count")));
            LogUtil.d("OM+++++++++++==============", String.valueOf(adapter.list.get(i).get("template_sub_timetype")));
            LogUtil.d("OMOM+++++++++==============", String.valueOf(adapter.list.get(i).get("template_sub_content")));

            if (HStringUtil.isEmpty(adapter.list.get(i).get("template_sub_timetype"))){
                ToastUtil.showToastPanl("请填写完整信息");
                return;
            }
            if (HStringUtil.isEmpty(adapter.list.get(i).get("template_sub_content"))){
                ToastUtil.showToastPanl("请填写完整信息");
                return;
            }


            try {
                object.put("template_seq", i);
                object.put("timetype_count", adapter.list.get(i).get("timetype_count"));
                object.put("template_sub_timetype", adapter.list.get(i).get("template_sub_timetype"));
                object.put("template_sub_content", adapter.list.get(i).get("template_sub_content"));

            } catch (JSONException e) {
                e.printStackTrace();
            }
            array.put(object);
        }

        LogUtil.d("OMOMOMOMOMOM+++++++++++===========", array.toString());

        Map<String, String> map = new HashMap<>();
        map.put("customer_id", customer_id);
        map.put("template_name", name);
        map.put("data", array.toString());

        ApiService.OKHttpAddSelfTemplate(map, new ApiCallback<String>() {
            @Override
            public void onError(Request request, Exception e) {
                ToastUtil.showShort("保存失败");
            }

            @Override
            public void onResponse(String content) {
                try {
                    JSONObject obj = new JSONObject(content);
                    if ("0".equals(obj.optString("code"))) {
                        ToastUtil.showShort("完成");
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },this);
    }

    public TextView template_time;

    @Override
    public void onStarClick(View view, final int position, int id) {
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

                WheelUtils.setDoubleWheel1(NewTemplateAty.this, numberList, mUnit, mainView, mAddressWindow,
                        wheelView);


//                times.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(final View v) {
//
//                        numberList = new ArrayList<Map<String, String>>();
//                        mUnit = new ArrayList<Map<String, String>>();
//
//                        if (mAddressWindow != null && mAddressWindow.isShowing()) {
//                            mAddressWindow.dismiss();
//                        }
//                        String[] number = new String[50];
//                        for (int i = 0; i < 50; i++) {
//                            number[i] = i + "";
//                        }
//
//                        for (int i = 0; i < number.length; i++) {
//                            HashMap<String, String> map = new HashMap<String, String>();
//                            map.put("name", number[i]);
//                            numberList.add(map);
//                        }
//
//                        String[] contentUnit = {"天", "周", "月", "年"};
//                        for (int i = 0; i < contentUnit.length; i++) {
//                            HashMap<String, String> map = new HashMap<String, String>();
//                            map.put("name", contentUnit[i]);
//                            mUnit.add(map);
//                        }
//
//                        WheelUtils.setDoubleWheel1(NewTemplateAty.this, numberList, mUnit, mainView, mAddressWindow,
//                                wheelView);
//                    }
//                });
                break;
            case R.id.rl_add_template:
                RelativeLayout template = (RelativeLayout) view.findViewById(R.id.rl_template);
                final TextView template_content = (TextView) view.findViewById(R.id.template_content);


                contentList = new ArrayList<Map<String, String>>();
                String[] content = {"复诊提醒", "用药提醒", "换药提醒", "手术提醒", "其他"};
                for (int i = 0; i < content.length; i++) {
                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put("name", content[i]);
                    contentList.add(map);
                }
                mPopupWindow = WheelUtils.showSingleWheel(NewTemplateAty.this, contentList, template_content, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int index1 = (Integer) v.getTag(R.id.wheel_one);
                        Map<String, String> map1 = contentList.get(index1);
                        remindContent = map1.get("name");
//                      template_content.setText(remindContent);
                        List<Map<String, String>> map2 = adapter.list;
                        adapter.list.get(pos).put("template_sub_content", remindContent);
                        List<Map<String, String>> map3 = adapter.list;
                        adapter.notifyDataSetChanged();
                    }
                });


//                template.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(final View v) {
//                        contentList = new ArrayList<Map<String, String>>();
//                        String[] content = {"复诊提醒", "用药提醒", "换药提醒", "手术提醒", "其他"};
//                        for (int i = 0; i < content.length; i++) {
//                            HashMap<String, String> map = new HashMap<String, String>();
//                            map.put("name", content[i]);
//                            contentList.add(map);
//                        }
//                        mPopupWindow = WheelUtils.showSingleWheel(NewTemplateAty.this, contentList, template_content, new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                int index1 = (Integer) v.getTag(R.id.wheel_one);
//                                Map<String, String> map1 = contentList.get(index1);
//                                remindContent = map1.get("name");
////                                template_content.setText(remindContent);
//                                List<Map<String, String>> map2 = adapter.list;
//                                adapter.list.get(pos).put("template_sub_content", remindContent);
//                                List<Map<String, String>> map3 = adapter.list;
//                                adapter.notifyDataSetChanged();
//                            }
//                        });
//                    }
//                });
                break;
        }
    }
}
