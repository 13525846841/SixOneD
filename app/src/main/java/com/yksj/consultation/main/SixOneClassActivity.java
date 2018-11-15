package com.yksj.consultation.main;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.LogUtils;
import com.library.base.base.BaseTitleActivity;
import com.yksj.consultation.adapter.SixOneAdapter;
import com.yksj.consultation.app.AppData;
import com.yksj.consultation.constant.Constant;
import com.yksj.consultation.event.MyEvent;
import com.yksj.consultation.im.GroupChatActivity;
import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.utils.DoctorHelper;
import com.yksj.healthtalk.net.http.ApiCallbackWrapper;
import com.yksj.healthtalk.net.http.ApiService;
import com.yksj.healthtalk.net.http.HttpResult;
import com.yksj.healthtalk.utils.FriendHttpUtil;
import com.yksj.healthtalk.utils.HStringUtil;
import com.yksj.healthtalk.utils.SharePreHelper;
import com.yksj.healthtalk.utils.ThreadManager;
import com.yksj.healthtalk.utils.ToastUtil;
import com.yksj.healthtalk.utils.WeakHandler;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 六一班
 */
public class SixOneClassActivity extends BaseTitleActivity implements AdapterView.OnItemClickListener {

    private RelativeLayout add_friend;
    private RelativeLayout rl_chat;
    private ListView mLv;
    private View header;
    private SixOneAdapter adapter;
    private int num = 9;
    private TextView gc_number;//群聊数字

    private EditText mEditText;
    private View mEmpty;
    List<JSONObject> mList = null;
    private List<String> mListIds = null;//群聊id集合
    private static final int GROUP_CHANGE = 1004;//群聊提示
    public static final int SINGLE_CHANGE = 1005;//单聊提示

    @Override
    public int createLayoutRes() {
        return R.layout.activity_six_one_class;
    }

    @Override
    public void initialize(Bundle bundle) {
        super.initialize(bundle);
        setTitle("六一班");
        initView();
    }

    private void initView() {
        mLv = (ListView) findViewById(R.id.chat_lv);
        header = View.inflate(SixOneClassActivity.this, R.layout.sixone_head, null);
        add_friend = (RelativeLayout) header.findViewById(R.id.add_friend);
        rl_chat = (RelativeLayout) header.findViewById(R.id.rl_chat);
        gc_number = (TextView) header.findViewById(R.id.gc_number);
        mEmpty = findViewById(R.id.empty_view);
        mEditText = (EditText) findViewById(R.id.include_search).findViewById(R.id.edit_search_top);
//        if (num > 0) {
//            gc_number.setVisibility(View.VISIBLE);
//            if (num > 0 && num < 10) {
//                gc_number.setText(num + "");
//                gc_number.setSelected(false);
//            } else if (num > 99) {
//                gc_number.setText(99 + "+");
//                gc_number.setSelected(true);
//            } else if (num > 10 && num < 100) {
//                gc_number.setText(num + "");
//                gc_number.setSelected(true);
//            }
//        }

        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (mEditText.getText().toString().length() == 0) {
                    getMyFriends("");
                }
            }
        });
        mEditText.setHint("请输入...");
        mEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == event.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (mEditText.getText().toString().length() > 0) {
                        getMyFriends(mEditText.getText().toString());
                    }

                }
                return false;
            }
        });
        adapter = new SixOneAdapter(this);
        mLv.addHeaderView(header);
        mLv.setAdapter(adapter);
        mLv.setOnItemClickListener(this);
        rl_chat.setOnClickListener(this);
        add_friend.setOnClickListener(this);
        mList = new ArrayList<>();
        mListIds = new ArrayList<>();

    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.add_friend://添加朋友
                intent = new Intent(this, AddDoctorActivity.class);
                startActivity(intent);
                break;
            case R.id.rl_chat://群聊
                intent = new Intent(this, GroupChatActivity.class);
                startActivityForResult(intent, GROUP_CHANGE);
                break;
        }
    }

    /**
     * 加载医生友 (搜素好友)
     *
     * @param searchContent 查询key
     *                      //     * @param type          0 医生好友 1 患者好友列表
     */
    private void getMyFriends(String searchContent) {
        Map<String, String> map = new HashMap<>();
        map.put("content", searchContent);
        map.put("customer_id", DoctorHelper.getId());
        map.put("op", "queryFriendsList");
        map.put("statement", "1");
        ApiService.OKHttpGetFriends(map, new ApiCallbackWrapper<String>(this) {
            @Override
            public void onResponse(String response) {
                super.onResponse(response);
                if (!HStringUtil.isEmpty(response)) {
                    try {
                        if (mListIds.size() > 0) {
                            mListIds.clear();
                        }
                        if (mList.size() > 0) {
                            mList.clear();
                        }
                        JSONObject object = new JSONObject(response);
                        if (HttpResult.SUCCESS.endsWith(object.optString("code"))) {
                            int groupNum = object.getJSONObject("result").optInt("group_msg_number");
                            if (groupNum > 99) {
                                gc_number.setVisibility(View.VISIBLE);
                                gc_number.setText("99+个群有新消息");
                            } else if (groupNum == 0) {
                                gc_number.setVisibility(View.GONE);
                            } else {
                                gc_number.setVisibility(View.VISIBLE);
                                gc_number.setText(groupNum + "个群有新消息");
                            }
                            //群提醒
                            JSONArray arrayIds = object.getJSONObject("result").getJSONArray("group_msg");
                            int countIds = arrayIds.length();
                            if (countIds > 0) {
                                for (int i = 0; i < countIds; i++) {
                                    String id = arrayIds.getJSONObject(i).optString("GROUP_ID");
                                    mListIds.add(id);
                                }
                            }

                            //单聊
                            JSONArray array = object.getJSONObject("result").getJSONArray("customerList");
                            int count = array.length();
                            if (count > 0) {
                                for (int i = 0; i < count; i++) {
                                    JSONObject obj = array.getJSONObject(i);
                                    mList.add(obj);
                                }
                                mEmpty.setVisibility(View.GONE);
                                adapter.onBoundData(mList);
                            } else {
                                adapter.removeAll();
                                mEmpty.setVisibility(View.VISIBLE);
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
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String chatId=adapter.mData.get(position - 1).optString("REL_CUSTOMER_ID");
        String name=adapter.mData.get(position - 1).optString("CUSTOMER_NICKNAME");
        FriendHttpUtil.chatFromPerson(this, chatId,name);
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
                    String groupId = "";//群id
                    String customerId = "";//单聊 发送者id
                    int newMsgFlag = 0;//0 新群1 没有新群
                    int newMsgSingleFlag = 0;//0 新单聊 1 不是新单聊
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

                            for (int i = 0; i < mListIds.size(); i++) {
                                if (groupId.equals(mListIds.get(i))) {
                                    newMsgFlag = 1;
                                }
                            }
                            if (newMsgFlag == 0) {//有新群
                                SharePreHelper.saveGroupMsgTip(groupId);
                                mListIds.add(groupId);
                                mHandler.sendEmptyMessageDelayed(2, 100);
                            }
                        } else if (groupType == 0) {//单聊
                            customerId = obj.optString("customerId");
                            for (int i = 0; i < mList.size(); i++) {
                                if (customerId.equals(mList.get(i).optString("REL_CUSTOMER_ID"))) {
                                    newMsgSingleFlag = 1;
                                    int num = mList.get(i).optInt("msg_number");
                                    try {
                                        mList.get(i).put("msg_number", num + 1);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                            if (newMsgSingleFlag == 0) {//有新单聊
                                mHandler.sendEmptyMessageDelayed(3, 100);
                            } else {//没有新单聊
                                mHandler.sendEmptyMessageDelayed(4, 100);
                            }
                        }
                    }
                }
            });
        }
    }

    WeakHandler mHandler = new WeakHandler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 2://新群消息
                    int groupSize = mListIds.size();
                    if (groupSize > 99) {
                        gc_number.setVisibility(View.VISIBLE);
                        gc_number.setText("99+个群有新消息");
                    } else if (groupSize == 0) {
                        gc_number.setVisibility(View.GONE);
                    } else {
                        gc_number.setVisibility(View.VISIBLE);
                        gc_number.setText(groupSize + "个群有新消息");
                    }
                    break;
                case 3://新单聊
                    getMyFriends("");
                    break;
                case 4://旧单聊
                    adapter.notifyDataSetChanged();
                    break;
            }
            return false;
        }
    });


    @Override
    protected void onStart() {
        super.onStart();
        getMyFriends("");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //群聊提示
        if (requestCode == GROUP_CHANGE && resultCode == RESULT_OK) {
            if (data != null) {
                mListIds = null;
                try {
                    mListIds = data.getStringArrayListExtra("groupIds");
                    mHandler.sendEmptyMessageDelayed(2, 100);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else if (requestCode == SINGLE_CHANGE && resultCode == RESULT_OK) {
            if (data != null) {
                //更新单聊未读
                String id = data.getStringExtra(Constant.Chat.SINGLE_ID);
                if (!HStringUtil.isEmpty(id)) {
                    updateData(id);
                }
            } else {
                getMyFriends("");
            }
        }
    }

    /**
     * 更新单聊未读
     *
     * @param id
     */
    private void updateData(String id) {
        for (int i = 0; i < mList.size(); i++) {
            if (id.equals(mList.get(i).optString("REL_CUSTOMER_ID"))) {
                try {
                    mList.get(i).put("msg_number", 0);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        adapter.notifyDataSetChanged();
    }
}
