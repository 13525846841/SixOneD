package com.yksj.consultation.sonDoc.consultation;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.blankj.utilcode.util.SizeUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.library.base.base.BaseTitleActivity;
import com.library.base.dialog.SelectorDialog;
import com.library.base.widget.DividerListItemDecoration;
import com.library.base.widget.SuperTextView;
import com.yksj.consultation.adapter.CreateTmpPlanAdapter;
import com.yksj.consultation.comm.AddTextActivity;
import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.utils.DoctorHelper;
import com.yksj.healthtalk.net.http.ApiCallback;
import com.yksj.healthtalk.net.http.ApiService;
import com.yksj.healthtalk.utils.HStringUtil;
import com.yksj.healthtalk.utils.ToastUtil;
import com.yksj.healthtalk.utils.WheelUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Request;

/**
 * 新建模块界面
 */
public class CreateTempleteActivity extends BaseTitleActivity implements BaseQuickAdapter.OnItemChildClickListener {
    private RecyclerView mRecyclerView;
    private CreateTmpPlanAdapter mAdapter;

    @BindView(R.id.add_plan_item) TextView mAddPlanView;
    @BindView(R.id.name_stv) SuperTextView mNameStv;
    @BindView(R.id.start_time_stv) SuperTextView mStartTimeStv;

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
    public int createLayoutRes() {
        return R.layout.activity_new_template_aty;
    }

    @Override
    public void initialize(Bundle bundle) {
        super.initialize(bundle);
        setTitle("新建模板");
        initView();
        mAddPlanView.performClick();
    }

    private void initView() {
        setRight("保存", v -> saveTamplate());
        mRecyclerView = findViewById(R.id.recycler_view);
        mAdapter = new CreateTmpPlanAdapter();
        mRecyclerView.addItemDecoration(new DividerListItemDecoration(LinearLayoutManager.VERTICAL, SizeUtils.dp2px(8)));
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemChildClickListener(this);

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
            case R.id.wheel_cancel:
                if (mAddressWindow != null)
                    mAddressWindow.dismiss();
                break;
            case R.id.wheel_sure:
                if (mAddressWindow != null)
                    mAddressWindow.dismiss();
                if (WheelUtils.getCurrent() != null) {
                    try {
                        if (mAddressWindow != null)
                            mAddressWindow.dismiss();
                        if (WheelUtils.getCurrent() != null) {
                            if (WheelUtils.getCurrent2().equals("天")) {
                                mAdapter.getItem(pos).put("template_sub_timetype", "10");
                            } else if (WheelUtils.getCurrent2().equals("周")) {
                                mAdapter.getItem(pos).put("template_sub_timetype", "20");
                            } else if (WheelUtils.getCurrent2().equals("月")) {
                                mAdapter.getItem(pos).put("template_sub_timetype", "30");
                            } else if (WheelUtils.getCurrent2().equals("年")) {
                                mAdapter.getItem(pos).put("template_sub_timetype", "40");
                            }
                            mAdapter.getItem(pos).put("timetype_count", WheelUtils.getCurrent1());
                            mAdapter.notifyDataSetChanged();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

    @OnClick(R.id.name_stv)
    public void onNameClick(View v) {
        AddTextActivity.from(this)
                       .setTitle("请输入模板名称")
                       .setListener(new AddTextActivity.OnAddTextClickListener() {
                           @Override
                           public void onConfrimClick(View v, String content, AddTextActivity activity) {
                               activity.finish();
                               mNameStv.setRightString(content);
                           }
                       })
                       .startActivity();
    }

    @OnClick(R.id.start_time_stv)
    public void onStartTimeClick(View v) {
        AddTextActivity.from(this)
                       .setTitle("请输入随访开始时间")
                       .setListener(new AddTextActivity.OnAddTextClickListener() {
                           @Override
                           public void onConfrimClick(View v, String content, AddTextActivity activity) {
                               mStartTimeStv.setRightString(content);
                           }
                       })
                       .startActivity();
    }

    /**
     * 添加新建模板
     */
    @OnClick(R.id.add_plan_item)
    public void addMod(View v) {
        try {
            JSONObject obj = new JSONObject();
            obj.put("template_seq", String.valueOf(pos));
            obj.put("timetype_count", "");
            obj.put("template_sub_timetype", "");
            obj.put("template_sub_content", "");
            mAdapter.addData(obj);
            mAdapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 保存
     */
    private void saveTamplate() {
        String name = mNameStv.getRightString().trim();
        if (TextUtils.isEmpty(name)) {
            ToastUtil.showToastPanl("请填写模板名称");
            return;
        }

        JSONArray array = new JSONArray();
        int count = mAdapter.getData().size();
        for (int i = 0; i < count; i++) {
            try {
                JSONObject object = new JSONObject();
                String templateSubTimetype = mAdapter.getData().get(i).getString("template_sub_timetype");
                if (HStringUtil.isEmpty(templateSubTimetype)) {
                    ToastUtil.showToastPanl("请填写完整信息");
                    return;
                }
                String templateSubContent = mAdapter.getData().get(i).getString("template_sub_content");
                if (HStringUtil.isEmpty(templateSubContent)) {
                    ToastUtil.showToastPanl("请填写完整信息");
                    return;
                }
                object.put("template_seq", i);
                object.put("timetype_count", mAdapter.getData().get(i).get("timetype_count"));
                object.put("template_sub_timetype", templateSubTimetype);
                object.put("template_sub_content", templateSubContent);

                array.put(object);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        Map<String, String> map = new HashMap<>();
        map.put("customer_id", DoctorHelper.getId());
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
        }, this);
    }

    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
        pos = position;
        if (view.getId() == R.id.time_stv) {
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
            WheelUtils.setDoubleWheel1(CreateTempleteActivity.this, numberList, mUnit, mainView, mAddressWindow, wheelView);
        } else if (view.getId() == R.id.action_stv) {
            String[] content = {"复诊提醒", "用药提醒", "换药提醒", "手术提醒", "其他"};
            SelectorDialog.newInstance(content)
                          .setOnItemClickListener(new SelectorDialog.OnMenuItemClickListener() {
                              @Override
                              public void onItemClick(SelectorDialog dialog, int position) {
                                  try {
                                      remindContent = content[position];
                                      mAdapter.getItem(pos).put("template_sub_content", remindContent);
                                      ((SuperTextView) view).setRightString(remindContent);
                                  } catch (JSONException e) {
                                      e.printStackTrace();
                                  }
                              }
                          })
                          .show(getSupportFragmentManager());
        } else if (view.getId() == R.id.close_iv) {
            mAdapter.remove(position);
        }
    }
}
