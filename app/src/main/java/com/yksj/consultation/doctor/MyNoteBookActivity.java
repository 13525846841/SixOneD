package com.yksj.consultation.doctor;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.library.base.base.BaseTitleActivity;
import com.yksj.consultation.adapter.NoteBookZListViewAdapter;
import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.sonDoc.home.AddNoteActivity;
import com.yksj.consultation.sonDoc.listener.MyOnClickListener;
import com.yksj.consultation.utils.DoctorHelper;
import com.yksj.healthtalk.net.http.ApiCallbackWrapper;
import com.yksj.healthtalk.net.http.ApiService;
import com.yksj.healthtalk.utils.HStringUtil;
import com.yksj.healthtalk.utils.TimeUtil;
import com.yksj.healthtalk.utils.ToastUtil;
import com.yksj.healthtalk.views.SelectWindow;
import com.yksj.healthtalk.views.zlistview.widget.ZListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * 记事本
 */
public class MyNoteBookActivity extends BaseTitleActivity implements MyOnClickListener, AdapterView.OnItemClickListener, NoteBookZListViewAdapter.onClickdeleteMsgeListener {

    private ListView mListView;
    //private NoteBookAdapter adapter;
    private List<JSONObject> mList = null;
    private static final int ADDFLAG = 10002;
    private ZListView mRefreshListView;
    private NoteBookZListViewAdapter mAdapter;
    private View mEmptyView;

    @Override
    public int createLayoutRes() {
        return R.layout.activity_my_note_book;
    }

    @Override
    public void initialize(Bundle bundle) {
        super.initialize(bundle);
        initView();
    }

    //自定义的弹出框类
    SelectWindow menuWindow;

    private void initView() {
        setTitle("记事本");
        setRight("添加", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyNoteBookActivity.this, AddNoteActivity.class);
                startActivityForResult(intent, ADDFLAG);
            }
        });
        mList = new ArrayList<>();

        mRefreshListView = (ZListView) findViewById(R.id.lv_note);
        mAdapter = new NoteBookZListViewAdapter(MyNoteBookActivity.this, this);
        mRefreshListView.setAdapter(mAdapter);
        mAdapter.setonClickdeleteMsgeListener(this);

        mRefreshListView.setPullLoadEnable(false);
        mRefreshListView.setPullRefreshEnable(false);
        mRefreshListView.setOnItemClickListener(this);
        mEmptyView = findViewById(R.id.empty_view_famous);
//      mListView = (ListView)findViewById(R.id.lv_note);
//      adapter = new NoteBookAdapter(this,this);
//      mListView.setAdapter(adapter);
        initData();
    }

    /**
     * 加载数据
     */
    private void initData() {
        Map<String, String> map = new HashMap<>();
        map.put("doctor_id", DoctorHelper.getId());//LoginBusiness.getInstance().DoctorHelper.getId()
        map.put("op", "doctorNotepadRecord");
        ApiService.OKHttpPlanData(map, new ApiCallbackWrapper<String>(this) {
            @Override
            public void onResponse(String response) {
                super.onResponse(response);
                if (!HStringUtil.isEmpty(response)) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if ("1".equals(jsonObject.optString("code"))) {
                            mAdapter.onBoundData(parseData(jsonObject));

                            if (parseData(jsonObject).size() == 0) {
                                mEmptyView.setVisibility(View.VISIBLE);
                                mRefreshListView.setVisibility(View.GONE);
                            } else {
                                mEmptyView.setVisibility(View.GONE);
                                mRefreshListView.setVisibility(View.VISIBLE);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, this);
    }


    private List<JSONObject> parseData(JSONObject jsonobject) {
        JSONArray array = jsonobject.optJSONArray("result");
        try {
            int countFlag = 0;//内容是否为空标记
            int count = array.length();
            if (null != array && array.length() > 0) {
                //今日计划
                JSONObject obj1 = new JSONObject();
                obj1.put("type", 0);
                obj1.put("title", "今日计划");
                mList.add(obj1);
                for (int i = 0; i < count; i++) {
                    JSONObject object = array.getJSONObject(i);
                    try {
                        boolean flag = isToday(TimeUtil.format(object.optString("NOTEPAD_TIME")));
                        if (flag == true) {
                            object.put("type", 1);
                            mList.add(object);
                            countFlag++;
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }

                if (countFlag == 0) {
                    mList.remove(0);
                } else {
                    countFlag = 0;
                }

                //昨日计划
                JSONObject obj2 = new JSONObject();
                obj2.put("type", 0);
                obj2.put("title", "明日计划");
                mList.add(obj2);
                for (int i = 0; i < count; i++) {
                    JSONObject object = array.getJSONObject(i);
                    try {

                        boolean flag1 = isYesterday(TimeUtil.format(object.optString("NOTEPAD_TIME")));
                        if (flag1 == true) {
                            object.put("type", 1);
                            mList.add(object);
                            countFlag++;
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                if (countFlag == 0) {
                    mList.remove(mList.size() - 1);
                } else {
                    countFlag = 0;
                }
                //更多计划
                JSONObject obj3 = new JSONObject();
                obj3.put("type", 0);
                obj3.put("title", "更多计划");
                mList.add(obj3);
                for (int i = 0; i < count; i++) {
                    JSONObject object = array.getJSONObject(i);
                    try {
                        boolean flag = isMoreday(TimeUtil.format(object.optString("NOTEPAD_TIME")));
                        if (flag == true) {
                            object.put("type", 1);
                            mList.add(object);
                            countFlag++;
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                if (countFlag == 0) {
                    mList.remove(mList.size() - 1);
                } else {
                    countFlag = 0;
                }
                //  过时计划
                JSONObject obj4 = new JSONObject();
                obj4.put("type", 0);
                obj4.put("title", "已过期");//更多
                mList.add(obj4);
                for (int i = 0; i < count; i++) {
                    JSONObject object = array.getJSONObject(i);
                    try {
                        boolean flag = morePlans(TimeUtil.format(object.optString("NOTEPAD_TIME")));
                        if (flag == true) {
                            object.put("type", 1);
                            mList.add(object);
                            countFlag++;
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                if (countFlag == 0) {
                    mList.remove(mList.size() - 1);
                } else {
                    countFlag = 0;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return mList;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADDFLAG && resultCode == RESULT_OK) {
            mList.clear();
            initData();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//            case R.id.select_star:
//                menuWindow = new SelectPopupWindow(this, new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        menuWindow.dismiss();
//                        record_id = adapter.reword_id(position);
//                        list.get(position).optString("RECORD_ID");
//                        changeState(position);
//                    }
//                });
//                // 显示窗口
//                menuWindow.showAtLocation(this.findViewById(R.id.ll_main), Gravity.BOTTOM| Gravity.CENTER_HORIZONTAL, 0, 0); //设置layout在PopupWindow中显示的位置
//
//                break;
        }
    }

    private String record_id = "";

    //弹窗点击事件
    @Override
    public void onStarClick(View view, final int position, int id) {
        if (!"1".equals(mAdapter.list.get(position).optString("REMIND_FLAG"))) {
            switch (id) {
                case R.id.select_star:
                    final ImageView star = (ImageView) view.findViewById(R.id.select_star);
                    star.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(final View v) {
                            menuWindow = new SelectWindow(MyNoteBookActivity.this, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    menuWindow.dismiss();
                                    //record_id = mAdapter.reword_id(position);
                                    changeState(position, star);
                                }
                            });
                            // 显示窗口
                            menuWindow.showAtLocation(view, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0); //设置layout在PopupWindow中显示的位置
                        }
                    });
                    break;
            }
        }
    }

    /**
     * 改变状态
     */
    private void changeState(final int position, final ImageView star) {
        record_id = mAdapter.reword_id(position);
        Map<String, String> map = new HashMap<>();
        map.put("record_id", record_id);
        map.put("op", "updateNotepadRemind");
        map.put("remind_flag", "1");

        ApiService.OKHttpPlanChangeState(map, new ApiCallbackWrapper<String>(this) {
            @Override
            public void onResponse(String response) {
                super.onResponse(response);
                if (!HStringUtil.isEmpty(response)) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if ("1".equals(jsonObject.optString("code"))) {
                            ToastUtil.showShort(jsonObject.optString("message"));
                            star.setSelected(true);
                            mList.clear();
                            initData();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, this);
    }

    /**
     * 删除记事本
     *
     * @param positon
     */
    @Override
    public void onClickDeleteMsg(final int positon) {
        record_id = mAdapter.reword_id(positon);
        Map<String, String> map = new HashMap<>();
        map.put("record_id", record_id);//记事本ID
        map.put("op", "deleteNotepadRecord");
        ApiService.OKHttpDelectData(map, new ApiCallbackWrapper<String>(this) {
            @Override
            public void onResponse(String response) {
                super.onResponse(response);
                if (!HStringUtil.isEmpty(response)) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if ("1".equals(jsonObject.optString("code"))) {
                            ToastUtil.showShort("删除成功");
                            mAdapter.remove(positon);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, this);

    }

    /**
     * 判断是否为今天
     *
     * @param day
     * @return
     * @throws ParseException
     */
    public static boolean isToday(String day) throws ParseException {

        Calendar pre = Calendar.getInstance();
        Date predate = new Date(System.currentTimeMillis());
        pre.setTime(predate);
        Calendar cal = Calendar.getInstance();
        Date date = getDateFormat().parse(day);
        cal.setTime(date);
        if (cal.get(Calendar.YEAR) == (pre.get(Calendar.YEAR))) {
            int diffDay = cal.get(Calendar.DAY_OF_YEAR)
                    - pre.get(Calendar.DAY_OF_YEAR);

            if (diffDay == 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断是否为明天
     *
     * @param day
     * @return
     * @throws ParseException
     */
    public static boolean isYesterday(String day) throws ParseException {

        Calendar pre = Calendar.getInstance();
        Date predate = new Date(System.currentTimeMillis());
        pre.setTime(predate);

        Calendar cal = Calendar.getInstance();
        Date date = getDateFormat().parse(day);
        cal.setTime(date);

        if (cal.get(Calendar.YEAR) == (pre.get(Calendar.YEAR))) {
            int diffDay = cal.get(Calendar.DAY_OF_YEAR)
                    - pre.get(Calendar.DAY_OF_YEAR);

            if (diffDay == 1) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断是否为以后的时间
     *
     * @param day
     * @return
     * @throws ParseException
     */
    public static boolean isMoreday(String day) throws ParseException {

        Calendar pre = Calendar.getInstance();
        Date predate = new Date(System.currentTimeMillis());
        pre.setTime(predate);

        Calendar cal = Calendar.getInstance();
        Date date = getDateFormat().parse(day);
        cal.setTime(date);

        if (cal.get(Calendar.YEAR) == (pre.get(Calendar.YEAR))) {
            int diffDay = cal.get(Calendar.DAY_OF_YEAR)
                    - pre.get(Calendar.DAY_OF_YEAR);

            if (diffDay > 1) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断是否为更多计划  //过时计划
     *
     * @param day
     * @return
     * @throws ParseException
     */
    public static boolean morePlans(String day) throws ParseException {
        return !isToday(day) && !isYesterday(day) && !isMoreday(day);
    }

    public static SimpleDateFormat getDateFormat() {
        if (null == DateLocal.get()) {
            DateLocal.set(new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA));
        }
        return DateLocal.get();
    }

    private static ThreadLocal<SimpleDateFormat> DateLocal = new ThreadLocal<SimpleDateFormat>();


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//        ZSwipeItem swipeItem = new ZSwipeItem(this);
//        if (mList.get(position).optString("type").equals("0")){
//            swipeItem.close();
//        }

    }
}
