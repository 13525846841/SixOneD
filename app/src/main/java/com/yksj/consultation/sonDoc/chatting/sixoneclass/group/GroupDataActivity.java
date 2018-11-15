package com.yksj.consultation.sonDoc.chatting.sixoneclass.group;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.library.base.base.BaseTitleActivity;
import com.library.base.widget.SuperTextView;
import com.yksj.consultation.adapter.GroupDataAdapter;
import com.yksj.consultation.bean.ResponseBean;
import com.yksj.consultation.comm.AddTextActivity;
import com.yksj.consultation.im.AddGroupChatMemberActivity;
import com.yksj.consultation.im.ReduceGroupChatMemberActivity;
import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.im.GroupFileActivity;
import com.yksj.consultation.utils.DoctorHelper;
import com.yksj.healthtalk.net.http.ApiCallbackWrapper;
import com.yksj.healthtalk.net.http.ApiService;
import com.yksj.healthtalk.net.http.HttpResult;
import com.yksj.healthtalk.utils.HStringUtil;
import com.yksj.healthtalk.utils.SharePreHelper;
import com.yksj.healthtalk.utils.ToastUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 群资料
 */
public class GroupDataActivity extends BaseTitleActivity implements AdapterView.OnItemClickListener {

    @BindView(R.id.group_name_stv) SuperTextView mGroupNameStv;
    @BindView(R.id.group_file_stv) SuperTextView mGroupFileStv;
    @BindView(R.id.group_notice_stv) SuperTextView mGroupNoticeStv;
    @BindView(R.id.group_quiet_stv) SuperTextView mGroupQuietStv;

    private GridView gv;
    private GroupDataAdapter adapter;
    private List<JSONObject> list = null;

    private static final int ADD_MEMBER = 1001;
    private static final int REDUCE_MEMBER = 1002;

    private String groupName = "";//群名称
    private String groupDes = "";//群公告
    private String groupMainId = "";//群主Id


    public static final String GROUPID = "GROUPID";
    private String groupId = "";

    @Override
    public int createLayoutRes() {
        return R.layout.activity_group_data;
    }

    @Override
    public void initialize(Bundle bundle) {
        super.initialize(bundle);
        initView();
    }

    private void initView() {
        setTitle("群资料");
        if (getIntent().hasExtra(GROUPID)) {
            groupId = getIntent().getStringExtra(GROUPID);
        }
        gv = (GridView) findViewById(R.id.number_head);
        adapter = new GroupDataAdapter(this);
        gv.setAdapter(adapter);
        gv.setOnItemClickListener(this);
        list = new ArrayList<>();
        getGroupData(groupId);

        mGroupQuietStv.setSwitchIsChecked(SharePreHelper.getGroupMsgTip(groupId));
        mGroupQuietStv.setSwitchCheckedChangeListener(new SuperTextView.OnSwitchCheckedChangeListener() {
            @Override
            public void onCheckedChanged(View buttonView, boolean isChecked) {
                SharePreHelper.updateGroupMsgTip(groupId, isChecked);
            }
        });
    }

    /**
     * 编辑群名称
     * @param v
     */
    @OnClick(R.id.group_name_stv)
    public void onEditGroupName(View v) {
        //TODO 未提交数据？？？？？？
        AddTextActivity.from(this)
                       .setTitle("群名称")
                       .setContent(groupName)
                       .setListener(new AddTextActivity.OnAddTextClickListener() {
                           @Override
                           public void onConfrimClick(View v, String content, AddTextActivity activity) {
                               requestResetGroupName(groupId, content);
                               activity.finish();
                           }
                       })
                       .startActivity();
    }

    /**
     * 群文件
     * @param v
     */
    @OnClick(R.id.group_file_stv)
    public void onGroupFile(View v) {
        Intent intent = GroupFileActivity.getCallingIntent(this, groupId);
        startActivity(intent);
    }

    /**
     * 群公告
     * @param v
     */
    @OnClick(R.id.group_notice_stv)
    public void onGroupNotice(View v){
        AddTextActivity
                .from(this)
                .setTitle("群公告")
                .setContent(groupDes)
                .setListener(new AddTextActivity.OnAddTextClickListener() {
                    @Override
                    public void onConfrimClick(View v, String content, AddTextActivity activity) {
                        requestResetGroupNotice(groupId, content);
                        activity.finish();
                    }
                })
                .startActivity();
    }

    /**
     * 退出群
     * @param v
     */
    @OnClick(R.id.out)
    public void onGroupExit(View v){
        quitGroup(groupId);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position == parent.getChildCount() - 2) {
            Intent intent = new Intent(GroupDataActivity.this, AddGroupChatMemberActivity.class);
            intent.putExtra(AddGroupChatMemberActivity.GROUP_ID, groupId);
            startActivityForResult(intent, ADD_MEMBER);
        } else if (position == parent.getChildCount() - 1) {
            Intent intent = new Intent(GroupDataActivity.this, ReduceGroupChatMemberActivity.class);
            intent.putExtra(ReduceGroupChatMemberActivity.GROUP_ID, groupId);
            intent.putExtra(ReduceGroupChatMemberActivity.GROUP_MEM, groupPersons);
            startActivityForResult(intent, REDUCE_MEMBER);
        }
    }

    String groupPersons = "";

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ADD_MEMBER:
                if (resultCode == RESULT_OK) {
                    getGroupData(groupId);
                }
                break;
            case REDUCE_MEMBER:
                if (resultCode == RESULT_OK) {
                    getGroupData(groupId);
                }
                break;
            default:
                break;
        }
    }

    /**
     * 修改群名称
     */
    private void requestResetGroupName(final String groupId, String groupName) {
        if (HStringUtil.isEmpty("groupId")) {
            return;
        }
        if (HStringUtil.isEmpty(groupName)) {
            ToastUtil.showShort("内容不能为空");
            return;
        }
        Map<String, String> map = new HashMap<>();
        map.put("group_id", groupId);
        map.put("record_name", groupName);
        map.put("op", "settingGroupName");
        ApiService.OKHttpGetFriends(map, new ApiCallbackWrapper<ResponseBean>() {
            @Override
            public void onResponse(ResponseBean response) {
                super.onResponse(response);
                if (response.isSuccess()){
                    mGroupNameStv.setRightString(groupName);
                }else{
                    // TODO 修改群名称失败
                }
            }
        }, this);
    }

    /**
     * 修改群公告
     */
    private void requestResetGroupNotice(String groupId, String notice) {
        if (HStringUtil.isEmpty("groupId")) {
            return;
        }
        if (HStringUtil.isEmpty(notice)) {
            ToastUtil.showShort("内容不能为空");
            return;
        }
        Map<String, String> map = new HashMap<>();
        map.put("group_id", groupId);
        map.put("record_desc", notice);
        map.put("op", "settiogGroupNotice");
        ApiService.OKHttpGetFriends(map, new ApiCallbackWrapper<ResponseBean>() {
            @Override
            public void onResponse(ResponseBean response) {
                super.onResponse(response);
                if (response.isSuccess()){
                    mGroupNoticeStv.setRightString(notice);
                }
            }
        }, this);
    }

    /**
     * 获取群资料
     */
    private void getGroupData(final String groupId) {
        if (HStringUtil.isEmpty(groupId)) {
            return;
        }
        Map<String, String> map = new HashMap<>();
        map.put("group_id", groupId);
        map.put("op", "queryGroupPerson");
        ApiService.OKHttpGetFriends(map, new ApiCallbackWrapper<String>(this) {
            @Override
            public void onResponse(String response) {
                super.onResponse(response);

                if (!HStringUtil.isEmpty(response)) {
                    try {
                        if (list.size() > 0) {
                            list.clear();
                        }
                        JSONObject object = new JSONObject(response);
                        if (HttpResult.SUCCESS.equals(object.optString("code"))) {
                            groupName = object.getJSONObject("result").optString("record_name");
                            groupDes = object.getJSONObject("result").optString("record_desc");
                            groupMainId = object.getJSONObject("result").optString("create_customer_id");

                            if (!HStringUtil.isEmpty(groupName)) {
                                mGroupNameStv.setRightString(groupName);
                            } else {
                                mGroupNoticeStv.setRightString("未设置");
                            }
                            if (!HStringUtil.isEmpty(groupDes)) {
                                mGroupNoticeStv.setRightString(groupDes);
                            } else {
                                mGroupNoticeStv.setRightString("未设置");
                            }

                            JSONArray array = object.getJSONObject("result").getJSONArray("groupPerson");
                            groupPersons = array.toString();
                            int count = array.length();
                            if (count > 0) {
                                for (int i = 0; i < count; i++) {
                                    JSONObject obj = array.getJSONObject(i);
                                    list.add(obj);
                                }
                            }
                            JSONObject obj = new JSONObject();
                            obj.put("ADD", "ADD");
                            JSONObject obj2 = new JSONObject();
                            obj2.put("REDUCE", "REDUCE");
                            list.add(obj2);
                            list.add(obj);
                            adapter.onBoundData(list);
                        } else {
                            ToastUtil.showShort(object.optString("message"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, this);
    }


    /**
     * 退出群
     */
    private void quitGroup(final String groupId) {
        if (HStringUtil.isEmpty("groupId")) {
            return;
        }
        Map<String, String> map = new HashMap<>();
        map.put("group_id", groupId);
        map.put("customer_id", DoctorHelper.getId());
        map.put("op", "outOffGroup");
        ApiService.OKHttpGetFriends(map, new ApiCallbackWrapper<String>(this) {
            @Override
            public void onResponse(String response) {
                super.onResponse(response);
                if (!HStringUtil.isEmpty(response)) {
                    try {
                        JSONObject object = new JSONObject(response);
                        ToastUtil.showShort(object.optString("message"));
                        if (HttpResult.SUCCESS.equals(object.optString("code"))) {
                            setResult(RESULT_OK);
                            finish();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, this);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("groupName", groupName);
        setResult(RESULT_OK, intent);
        super.onBackPressed();
    }
}