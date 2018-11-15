package com.yksj.consultation.sonDoc.chatting.sixoneclass.group;

import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.library.base.base.BaseActivity;
import com.yksj.consultation.dialog.WaitDialog;
import com.yksj.consultation.adapter.SortAdapter;
import com.yksj.consultation.bean.SortModel;
import com.yksj.consultation.comm.DoubleBtnFragmentDialog;
import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.utils.DoctorHelper;
import com.yksj.healthtalk.function.sortlistview.CharacterParser;
import com.yksj.healthtalk.function.sortlistview.PinyinComparator2;
import com.yksj.healthtalk.net.http.ApiService;
import com.yksj.healthtalk.net.http.HttpResult;
import com.yksj.healthtalk.net.http.ApiCallbackWrapper;
import com.yksj.healthtalk.utils.HStringUtil;
import com.yksj.healthtalk.utils.ThreadManager;
import com.yksj.healthtalk.utils.ToastUtil;
import com.yksj.healthtalk.utils.ValidatorUtil;
import com.yksj.healthtalk.utils.WeakHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hekl on 17/1/10.
 * Used for
 */

public class ContactInfoActivity extends BaseActivity implements View.OnClickListener {
    private ListView sortListView;
    private SortAdapter adapter;
    private List<SortModel> mList = null;
    private WaitDialog mLoadDialog;

    /**
     * 汉字转换成拼音的类
     */
    private CharacterParser characterParser;

    /**
     * 根据拼音来排列ListView里面的数据类
     */
    private PinyinComparator2 pinyinComparator;
    private List<SortModel> SourceDateList;

    WeakHandler mHandler = new WeakHandler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 1://更新UI

                    long startTime2 = System.nanoTime();  //開始時間

                    if (SourceDateList.size() > 0) {
                        findViewById(R.id.no_contact).setVisibility(View.GONE);
                        adapter.onBoundData(SourceDateList);
                    } else {
                        findViewById(R.id.no_contact).setVisibility(View.VISIBLE);
                    }
                    if (mLoadDialog != null && !mLoadDialog.isDetached()) {
                        mLoadDialog.dismissAllowingStateLoss();
                    }
                    long consumingTime2 = System.nanoTime() - startTime2; //消耗時間
                    System.out.println("handleMessage" + consumingTime2 / 1000 + "微秒");
                    break;
                case 2://新群消息

                    break;
            }
            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_contact_layout);
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mLoadDialog = WaitDialog.showLodingDialog(getSupportFragmentManager(), getResources());
        mLoadDialog.setCancelable(false);
        ThreadManager.getInstance().createLongPool().execute(new Runnable() {
            @Override
            public void run() {

                GetLocalContact();
                SourceDateList = filledData(mList);
                // 根据a-z进行排序源数据
                Collections.sort(SourceDateList, pinyinComparator);
                mHandler.sendEmptyMessageDelayed(1, 100);
            }
        });
    }

    private void initView() {
        initializeTitle();
        titleTextV.setText("手机通讯录");
        titleLeftBtn.setOnClickListener(this);
        //实例化汉字转拼音类
        characterParser = CharacterParser.getInstance();

        pinyinComparator = new PinyinComparator2();
        sortListView = (ListView) findViewById(R.id.country_lvcountry);
        sortListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    final int position, long id) {

                getFriends(((SortModel) adapter.getItem(position)).getPhone());
//                sendMsg(((SortModel) adapter.getItem(position)).getPhone());

            }
        });


//        SourceDateList = filledData(getResources().getStringArray(R.array.date));

        adapter = new SortAdapter(this);
        sortListView.setAdapter(adapter);
        mList = new ArrayList<>();
    }


    /**
     * 为ListView填充数据
     *
     * @param data
     * @return
     */
    private List<SortModel> filledData(List<SortModel> data) {
        List<SortModel> mSortList = new ArrayList<SortModel>();
        for (int i = 0; i < data.size(); i++) {
            SortModel sortModel = new SortModel();
            sortModel.setName(data.get(i).getName());
            sortModel.setPhone(data.get(i).getPhone());
            sortModel.setHeadPath(data.get(i).getHeadPath());
            sortModel.setHeadBitmap(data.get(i).getHeadBitmap());
            //汉字转换成拼音
            String pinyin = characterParser.getSelling(data.get(i).getName());
            String sortString = pinyin.substring(0, 1).toUpperCase();

            // 正则表达式，判断首字母是否是英文字母
            if (sortString.matches("[A-Z]")) {
                sortModel.setSortLetters(sortString.toUpperCase());
            } else {
                sortModel.setSortLetters("#");
            }

            mSortList.add(sortModel);
        }
        return mSortList;

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_back:
                onBackPressed();
                break;
            default:
                break;
        }
    }


    /**
     * 获取手机通讯录信息的方法
     */
    private void GetLocalContact() {
        // 得到ContentResolver对象
        ContentResolver cr = this.getContentResolver();
        // 取得电话本中开始一项的光标
        Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        // 取得联系人ID列
        int idColumn = cursor.getColumnIndex(ContactsContract.Contacts._ID);
        // 取得联系人名字列
        int nameFieldColumnIndex = cursor
                .getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME);
        while (cursor.moveToNext()) {
            String contactId = cursor.getString(idColumn);// 得到单个联系人的id
            String displayName = cursor.getString(nameFieldColumnIndex);
            // 查看联系人有多少个号码，如果没有号码，返回0
            int phoneCount = cursor.getInt(cursor
                    .getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
            if (phoneCount > 0) {
                // 取得电话号码(可能存在多个号码)
                // 再类ContactsContract.CommonDataKinds.Phone中根据查询相应id联系人的所有电话；
                Cursor phone = this.getContentResolver().query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + contactId, null, null);

                while (phone.moveToNext()) {
                    SortModel model = new SortModel();
                    String strPhoneNumber = phone.getString(phone
                            .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    model.setHeadBitmap(GetLinkManPhoto(phone));// 调用获取头像的方法并设置到bean里面
                    model.setName(displayName);
                    model.setPhone(GetNumber(strPhoneNumber));
                    mList.add(model);
                }
                phone.close();
            }
        }
        cursor.close();
    }

    /**
     * 获取联系人头像的方法 phone 查询指针
     */
    private Bitmap GetLinkManPhoto(Cursor phone) {
        String photoid = phone.getString(phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_ID));
        if (photoid != null) {
            Cursor c = this.getContentResolver().query(
                    ContactsContract.Data.CONTENT_URI,
                    new String[]{ContactsContract.Data.DATA15},
                    ContactsContract.Data._ID + "=" + photoid, null, null);
            c.moveToFirst();
            byte[] ss = c.getBlob(0);
            if (ss != null) {
                Bitmap map = BitmapFactory.decodeByteArray(ss, 0, ss.length);
                c.close();
                return map;
            }
            c.close();
            return null;
        }
        return null;

    }

    /**
     * 还原11位的手机号码，包括取出空格和“-”
     * <p>
     * 传入的号码
     *
     * @return 满足条件则返回还原后的号码，否则返回自身
     */
    public static String GetNumber(String num) {
        if (num != null) {
            num = num.replaceAll("-", "");
            num = num.replaceAll(" ", "");
            if (num.startsWith("+86")) {
                num = num.substring(3);
            } else if (num.startsWith("86")) {
                num = num.substring(2);
            } else if (num.startsWith("17951")) {
                num = num.substring(5);
            } else if (num.startsWith("12593")) {
                num = num.substring(5);
            }
        }
        return num.trim();
    }

    /**
     * 发送邀请
     *
     * @param phone
     */
    private void sendMsg(final String phone) {
        if (!ValidatorUtil.isPhone(phone)) {
            ToastUtil.showShort("手机格式无法发送短信邀请");
            return;
        }
        Map<String, String> map = new HashMap<>();
        map.put("phone", phone);
        map.put("op", "sendMsg");
        ApiService.OKHttpGetFriends(map, new ApiCallbackWrapper<String>(this) {
            @Override
            public void onResponse(String response) {
                super.onResponse(response);
                if (!HStringUtil.isEmpty(response)) {
                    try {
                        JSONObject obj = new JSONObject(response);
                        if (HttpResult.SUCCESS.equals(obj.optString("code"))) {
                            if (HttpResult.EVENT_TURE.equals(obj.getJSONObject("result").optString("result"))) {//发送短信成功
                                ToastUtil.showShort("发送短信成功");
                                finish();
                            } else {//发送短信失败
                                ToastUtil.showShort("发送短信失败");
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, this);
    }


    /**
     * 搜索系统内医生
     *
     * @param phone 查询电话
     */
    private void getFriends(final String phone) {

        Map<String, String> map = new HashMap<>();
        map.put("op", "queryCustomerByPhone");
        map.put("customer_id", DoctorHelper.getId());
        map.put("phone", phone);
        ApiService.OKHttpGetFriends(map, new ApiCallbackWrapper<String>(this) {

            @Override
            public void onResponse(String response) {
                super.onResponse(response);
                if (!HStringUtil.isEmpty(response)) {
                    try {
                        JSONObject object = new JSONObject(response);
                        if (HttpResult.SUCCESS.endsWith(object.optString("code"))) {
                            List<JSONObject> mList = new ArrayList<JSONObject>();
                            if (!HStringUtil.isEmpty(object.optString("result"))) {
                                mList.add(object.getJSONObject("result"));
                            }
                            if (mList.size() > 0) {//有该用户
                                int friendFlag = object.getJSONObject("result").optInt("status");
                                if (friendFlag == 1) {//已是好友
                                    ToastUtil.showShort("对方已是好友");
                                } else {
                                    addFriends((object.getJSONObject("result").optString("CUSTOMER_ID")));
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    //客户id
                    if (getInvitingTime(phone)) {
                        //没有该用户
                        DoubleBtnFragmentDialog.showDefault(getSupportFragmentManager(), "该好友暂未注册六一健康快车，是否邀请好友加入", "取消", "邀请",
                                new DoubleBtnFragmentDialog.OnDilaogClickListener() {
                                    @Override
                                    public void onDismiss(DialogFragment fragment) {

                                    }

                                    @Override
                                    public void onClick(DialogFragment fragment, View v) {
                                        sendMsg(phone);
                                    }
                                });
                        startCountDownTime(countTime, phone);
                    }


                }
            }
        }, this);
    }

    /**
     * 添加好友
     */
    private void addFriends(String addId) {
        Map<String, String> map = new HashMap<>();
        map.put("customer_id", DoctorHelper.getId());
        map.put("relation_customer_id", addId);
        map.put("op", "addFriends");
        ApiService.OKHttpGetFriends(map, new ApiCallbackWrapper<String>(this) {

            @Override
            public void onResponse(String response) {
                super.onResponse(response);
                if (!HStringUtil.isEmpty(response)) {
                    try {
                        JSONObject object = new JSONObject(response);
                        if (HttpResult.SUCCESS.endsWith(object.optString("code"))) {
                            finish();
                        }
                        ToastUtil.showShort(object.optString("message"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, this);
    }

    int countTime = 60;
    Map<String, String> mapIds = new HashMap<>();

    /**
     * 判断是否可邀请
     */
    private boolean getInvitingTime(String id) {
        String time = mapIds.get(id);
        if (!HStringUtil.isEmpty(time)) {
            if ("0".equals(time)) {
                mapIds.put(id, countTime + "");
                return true;
            } else {
                ToastUtil.showShort("添加时间间隔小于" + countTime + "秒禁止添加，剩余" + time + "秒");
                return false;
            }
        } else {
            mapIds.put(id, countTime + "");
            return true;
        }
    }

    /**
     * 邀请倒计时
     *
     * @param time
     */
    private void startCountDownTime(long time, final String id) {
        /**
         * 最简单的倒计时类，实现了官方的CountDownTimer类（没有特殊要求的话可以使用）
         * 即使退出activity，倒计时还能进行，因为是创建了后台的线程。
         * 有onTick，onFinsh、cancel和start方法
         */
        new CountDownTimer(time * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mapIds.put(id, String.valueOf(millisUntilFinished / 1000));
            }

            @Override
            public void onFinish() {
                mapIds.put(id, "0");
            }
        }.start();
    }
}
