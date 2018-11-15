package com.yksj.consultation.im;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.yksj.consultation.adapter.GroupChatAdapter;
import com.library.base.base.BaseTitleActivity;
import com.yksj.consultation.constant.Constant;
import com.yksj.consultation.event.MyEvent;
import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.app.AppData;
import com.yksj.consultation.utils.DoctorHelper;
import com.yksj.healthtalk.net.http.ApiService;
import com.yksj.healthtalk.net.http.HttpResult;
import com.yksj.healthtalk.net.http.ApiCallbackWrapper;
import com.yksj.healthtalk.utils.HStringUtil;
import com.yksj.healthtalk.utils.SharePreHelper;
import com.yksj.healthtalk.utils.ThreadManager;
import com.yksj.healthtalk.utils.ToastUtil;
import com.yksj.healthtalk.utils.WeakHandler;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Request;

/**
 * 群聊界面_
 * by chen
 */
public class GroupChatActivity extends BaseTitleActivity implements AdapterView.OnItemClickListener {
    private final String TAG = this.getClass().getSimpleName();
    private ListView mLv;
    private GroupChatAdapter adapter;
    private View mEmptyView;
    public static final int GROUP_CHAT = 1001;//删除离线
    List<JSONObject> mList = null;//网络数据
    List<String> mListIds = null;//群id集合
    List<String> mListOfflineIds = null;//未读群id集合
    int getCount = 0;//加载次数

    WeakHandler mHandler = new WeakHandler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 1://没有新消息
                    adapter.onBoundData(mList);
                    break;
                case 2://新群消息
                    requestDataList();
                    break;
            }
            return false;
        }
    });

    @Override
    public int createLayoutRes() {
        return R.layout.activity_group_chat;
    }

    @Override
    public void initialize(Bundle bundle) {
        super.initialize(bundle);
        setTitle("群聊");

        setRight("添加", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GroupChatActivity.this, AddGroupChatActivity.class);
                startActivityForResult(intent, GROUP_CHAT);
            }
        });

        mEmptyView = findViewById(R.id.empty_view);
        mLv = (ListView) findViewById(R.id.chat_lv);
        adapter = new GroupChatAdapter(this);
        mLv.setAdapter(adapter);
        mLv.setOnItemClickListener(this);
        mList = new ArrayList<JSONObject>();
        mListIds = new ArrayList<>();
        mListOfflineIds = new ArrayList<>();

        requestDataList();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    private void requestDataList() {
        Map<String, String> map = new HashMap<>();
        map.put("customer_id", DoctorHelper.getId());
        map.put("op", "queryGroupList");
        ApiService.OKHttpGetFriends(map, new ApiCallbackWrapper<String>(this) {
            @Override
            public void onAfter() {
                super.onAfter();
                getCount = 1;
            }

            @Override
            public void onError(Request request, Exception e) {
                super.onError(request, e);
                getCount = 0;
            }

            @Override
            public void onResponse(String response) {
                super.onResponse(response);
                if (!HStringUtil.isEmpty(response)) {
                    try {
                        JSONObject object = new JSONObject(response);
                        if (mList.size() > 0) {
                            mList.clear();
                        }
                        if (mListIds.size() > 0) {
                            mListIds.clear();
                        }
                        if (mListOfflineIds.size() > 0) {
                            mListOfflineIds.clear();
                        }
                        if (HttpResult.SUCCESS.endsWith(object.optString("code"))) {
                            JSONArray array = object.getJSONArray("result");

                            int count = array.length();
                            if (count > 0) {
                                for (int i = 0; i < count; i++) {
                                    final JSONObject obj = array.getJSONObject(i);
                                    mList.add(obj);
                                    mListIds.add(obj.optString("GROUP_ID"));//
                                }
                                mLv.setVisibility(View.VISIBLE);
                                mEmptyView.setVisibility(View.GONE);
                                adapter.onBoundData(mList);
                                if (getCount == 0) {
                                    ThreadManager.getInstance().createShortPool().execute(new Runnable() {
                                        @Override
                                        public void run() {
                                            SharePreHelper.saveGroupMsgTips(mListIds);
                                        }
                                    });
                                }
                            } else {
                                adapter.removeAll();
                                mEmptyView.setVisibility(View.VISIBLE);
                                mLv.setVisibility(View.GONE);
                            }
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

    @Override
    protected void onStart() {
        super.onStart();
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    /**
     * 新消息
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(final MyEvent event) {
        if (event.code == AppData.NEWMSG && !HStringUtil.isEmpty(event.what)) {
            ThreadManager.getInstance().createLongPool().execute(new Runnable() {
                @Override
                public void run() {
                    String msg = event.what;
                    String groupId = "";
                    int newMsgFlag = 0;//0 新群1 没有新群
                    int groupType = 0;
                    JSONObject obj = null;
                    try {
                        obj = new JSONObject(msg);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (obj != null) {
                        groupType = obj.optInt("isGroupMessage");
                        //群聊消息
                        if (groupType == 1) {
                            groupId = obj.optString("sms_target_id");

                            for (int i = 0; i < mList.size(); i++) {
                                if (groupId.equals(mList.get(i).optString("GROUP_ID"))) {
                                    int number = mList.get(i).optInt("massage_number");
                                    try {
                                        mList.get(i).put("massage_number", number + 1);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    newMsgFlag = 1;
                                } else {
                                }
                            }

                            if (newMsgFlag > 0) {//没有新群
                                mHandler.sendEmptyMessageDelayed(1, 100);
                            } else {//新群
                                SharePreHelper.saveGroupMsgTip(groupId);
                                mHandler.sendEmptyMessageDelayed(2, 100);
                            }
                        }
                    }
                }
            });
        }
    }

    /**
     * 更新群聊列表数据
     */
    private void updateData(String id, String name) {
        for (int i = 0; i < mList.size(); i++) {
            if (id.equals(mList.get(i).optString("GROUP_ID"))) {
                try {
                    mList.get(i).put("massage_number", 0);
                    mList.get(i).put("RECORD_NAME", name);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GROUP_CHAT && resultCode == RESULT_OK) {
            if (data != null) {
                //更新未读
                String id = data.getStringExtra(Constant.Chat.GROUP_ID);
                String name = data.getStringExtra("groupName");
                if (!HStringUtil.isEmpty(id)) {
                    updateData(id, name);
                }

            } else {
                requestDataList();
            }

        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        if (mListIds != null && mListIds.size() > 0) {
            intent.putStringArrayListExtra("groupIds", backGroupTip());
            setResult(RESULT_OK, intent);
        }
        super.onBackPressed();
    }

    /**
     * 首页群聊提示
     */
    private ArrayList<String> backGroupTip() {
        ArrayList<String> ids = new ArrayList<>();
        int idSize = mList.size();
        for (int i = 0; i < idSize; i++) {
            if (mList.get(i).optInt("massage_number") > 0) {
                ids.add(mList.get(i).optString("GROUP_ID"));
            }
        }
        return ids;
    }
}
