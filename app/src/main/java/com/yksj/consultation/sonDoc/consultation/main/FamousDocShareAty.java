package com.yksj.consultation.sonDoc.consultation.main;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.library.base.base.BaseActivity;
import com.yksj.consultation.adapter.FamousDocShareAtyAdapter;
import com.yksj.consultation.comm.DoubleBtnFragmentDialog;
import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.doctor.ShareSubmitActivity;
import com.yksj.consultation.utils.DoctorHelper;
import com.yksj.consultation.business.LoginBusiness;
import com.yksj.healthtalk.net.http.ApiCallbackWrapper;
import com.yksj.healthtalk.net.http.ApiService;
import com.yksj.healthtalk.utils.CommonUtils;
import com.yksj.healthtalk.utils.ToastUtil;
import com.yksj.healthtalk.views.CommentItem;

import org.handmark.pulltorefresh.library.PullToRefreshBase;
import org.handmark.pulltorefresh.library.PullToRefreshListView;
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
 * 名医分享
 */
public class FamousDocShareAty extends BaseActivity implements FamousDocShareAtyAdapter.MyShareOnClickListener, PullToRefreshBase.OnRefreshListener2<ListView> {
    private ImageView addView;
    private PullToRefreshListView mPullRefreshListView;
    private ListView mListView;
    public FamousDocShareAtyAdapter mAdapter;
    private String customer_id = DoctorHelper.getId();
    private List<JSONObject> mList;
    private View mEmptyView;
    private int pageNum = 1;

    private RelativeLayout edittextbody;
    private EditText editText;
    private ImageView sendIv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_famous_doc_share_aty);
        initView();
    }

    private void initView() {
        initializeTitle();
        titleTextV.setText("名医分享");
        titleLeftBtn.setOnClickListener(this);
        addView = (ImageView) findViewById(R.id.main_listmenuP);
        addView.setVisibility(View.VISIBLE);
        addView.setImageResource(R.drawable.photo);
        addView.setOnClickListener(this);
        mPullRefreshListView = ((PullToRefreshListView) findViewById(R.id.my_share_list));
        mListView = mPullRefreshListView.getRefreshableView();
        mAdapter = new FamousDocShareAtyAdapter(this, FamousDocShareAty.this);
        mListView.setAdapter(mAdapter);
        mEmptyView = findViewById(R.id.empty_view_famous);
        mPullRefreshListView.setOnRefreshListener(this);
        edittextbody = (RelativeLayout) findViewById(R.id.editTextBodyLl);
        bodyLayout = (RelativeLayout) findViewById(R.id.bodyLayout);
        editText = (EditText) findViewById(R.id.circleEt);
        mListView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (edittextbody.getVisibility() == View.VISIBLE) {
                    updateEditTextBodyVisible(View.GONE);
                    return true;
                }
                return false;
            }
        });

        sendIv = (ImageView) findViewById(R.id.sendIv);
        sendIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //发布评论
                String content = editText.getText().toString().trim();
                if (TextUtils.isEmpty(content)) {
                    ToastUtil.showShort(FamousDocShareAty.this, "评论内容不能为空");
                    return;
                }
                addComment(content, adapterPos);
                updateEditTextBodyVisible(View.GONE);
            }
        });
//        setViewTreeObserver();
        addLayoutListener(bodyLayout, edittextbody);
    }

    boolean flag = false;
    CommentItem commentItem = null;

    /**
     * http://192.168.1.108:8080/DuoMeiHealth/DoctorWorkSiteServlet?op=commentSare&customer_id=3774&comment_customer_id=3773&comment_content=123123&share_id=7
     *
     * @param content
     */
    public void addComment(final String content, final int pos) {
        String share_id = mAdapter.datas.get(pos).optString("SHARE_ID");

        Map<String, String> map = new HashMap<>();
        map.put("op", "commentSare");
        map.put("comment_content", content);
        map.put("customer_id", LoginBusiness.getInstance().getLoginEntity().getId());//评论者
        if (flag) {
            String comment_customer_id = commentItem.getUser().getId();
            map.put("comment_customer_id", comment_customer_id);
        }
        map.put("share_id", share_id);
        ApiService.OKHttpStationCommonUrl(map, new ApiCallbackWrapper<String>(this) {
            @Override
            public void onResponse(String response) {
                super.onResponse(response);
                try {
                    JSONObject obj = new JSONObject(response);
                    if ("1".equals(obj.optString("code"))) {
                        mAdapter.datas.get(pos).getJSONArray("comment").put(obj.getJSONObject("result"));
                        mAdapter.notifyDataSetChanged();
//                        //清空评论文本
                        editText.setText("");
                        updateEditTextBodyVisible(View.GONE);
                    } else {
                        ToastUtil.showShort(obj.optString("message"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        pageNum = 1;
        if (mList != null) {
            mList.clear();
        }
        initData();
    }

    private void initData() {
        Map<String, String> map = new HashMap<>();
//        map.put("op","findSharesByCustomer");
//        map.put("customer_id",customer_id);//customer_id
//        map.put("pageCount","20");//customer_id
//        map.put("pageNum",String.valueOf(pageNum));//customer_id
        map.put("op", "queryShareList");
        map.put("pageNum", String.valueOf(pageNum));
        map.put("customer_id", DoctorHelper.getId());
        ApiService.OKHttpStationCommonUrl(map, new ApiCallbackWrapper<String>(this) {

            @Override
            public void onBefore(Request request) {
                super.onBefore(request);
                mPullRefreshListView.setRefreshing();
            }

            @Override
            public void onAfter() {
                super.onAfter();
                mPullRefreshListView.onRefreshComplete();
            }

            @Override
            public void onResponse(String response) {
                super.onResponse(response);
                try {
                    JSONObject obj = new JSONObject(response);
                    if ("1".equals(obj.optString("code"))) {
                        if (mList == null) {
                            mList = new ArrayList<JSONObject>();
                        }
                        JSONArray array = obj.getJSONArray("result");
                        JSONObject item;
                        for (int i = 0; i < array.length(); i++) {
                            item = array.getJSONObject(i);
                            item.put("show", false);
                            mList.add(item);
                        }
                        if (mList.size() > 0) {
                            if (pageNum == 1) {

                                mAdapter.onBoundData(mList);
                            } else {
                                mAdapter.addAll(mList);
                            }
                        } else {
                            if (pageNum > 1) {
                                ToastUtil.showShort("没有更多了");
                            } else {
                                mEmptyView.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, this);
    }

//    /**
//     * 删除分享
//     */
//    private void delete(String shareId){
//        Map<String, String> map = new HashMap<>();
////        map.put("op","deleteShareById");
////        map.put("doctor_id", customer_id);
////        map.put("share_id", shareId);
//        map.put("op", "queryShareList");
//        map.put("pageNum", "1");
////        ApiService.OKHttpDelectshare(map, new ApiCallbackWrapper<String>(this) {
//        ApiService.OKHttpStationCommonUrl(map, new ApiCallbackWrapper<String>(this) {
//            @Override
//            public void onResponse(String response) {
//                super.onResponse(response);
//                try {
//                    JSONObject obj = new JSONObject(response);
////                    if ("0".equals(obj.optString("code"))) {
////                        ToastUtil.showShort(obj.optString("message"));
////                    } else {
////                        ToastUtil.showShort(obj.optString("message"));
////                    }
////                    mAdapter.notifyDataSetChanged();
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        }, this);
//    }

    /**
     * 删除分享
     */
    private void delete(String shareId, final int pos) {
        Map<String, String> map = new HashMap<>();
        map.put("op", "deleteShare");
        map.put("share_id", shareId);
        ApiService.OKHttpStationCommonUrl(map, new ApiCallbackWrapper<String>(this) {
            @Override
            public void onResponse(String response) {
                super.onResponse(response);
                try {
                    JSONObject obj = new JSONObject(response);
                    ToastUtil.showShort(obj.optString("message"));
                    if ("1".equals(obj.optString("code"))) {
                        ToastUtil.showShort(obj.optString("message"));
                    }
                    mAdapter.datas.remove(pos);
                    mAdapter.notifyDataSetChanged();
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
            case R.id.main_listmenuP:
                Intent intent = new Intent(this, ShareSubmitActivity.class);
                startActivity(intent);
                break;
        }
    }

    int adapterPos = 0;//列表位置

    @Override
    public void onStarClick(final String shareId, int id, final int pos) {
        adapterPos = pos;
        switch (id) {
            case R.id.text_share_delete:
                DoubleBtnFragmentDialog.showDefault(getSupportFragmentManager(), "您确定要删除此分享吗？", "取消", "确定", new DoubleBtnFragmentDialog.OnDilaogClickListener() {

                    @Override
                    public void onDismiss(DialogFragment fragment) {

                    }

                    @Override
                    public void onClick(DialogFragment fragment, View v) {
                        delete(shareId, pos);
                    }
                });
                break;
            case R.id.snsBtn://评论
                flag = false;
                updateEditTextBodyVisible(View.VISIBLE);
                break;
            case R.id.image_zan://点赞
                makeGood(shareId, pos);
                break;
        }
    }

    public void setFlag(CommentItem commentItem,int pos) {
        flag = true;
        this.commentItem = commentItem;
        this.adapterPos = pos;
    }


    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
        pageNum = 1;
        if (mList != null) {
            mList.clear();
        }
        initData();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
        pageNum++;
        if (mList != null) {
            mList.clear();
        }
        initData();
    }

    public void updateEditTextBodyVisible(int visibility) {
        edittextbody.setVisibility(visibility);
        if (View.VISIBLE == visibility) {
            editText.requestFocus();
            //弹出键盘
            CommonUtils.showSoftInput(editText.getContext(), editText);
        } else if (View.GONE == visibility) {
            //隐藏键盘
            CommonUtils.hideSoftInput(editText.getContext(), editText);
        }
    }

    private RelativeLayout bodyLayout;
    private int currentKeyboardH;
    private int screenHeight;
    private int editTextBodyHeight;

    private void setViewTreeObserver() {

        final ViewTreeObserver swipeRefreshLayoutVTO = bodyLayout.getViewTreeObserver();
        swipeRefreshLayoutVTO.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                Rect r = new Rect();
                bodyLayout.getWindowVisibleDisplayFrame(r);
                int statusBarH = getStatusBarHeight();//状态栏高度
                int screenH = bodyLayout.getRootView().getHeight();
                if (r.top != statusBarH) {
                    //在这个demo中r.top代表的是状态栏高度，在沉浸式状态栏时r.top＝0，通过getStatusBarHeight获取状态栏高度
                    r.top = statusBarH;
                }
                int keyboardH = screenH - (r.bottom - r.top);
//                Log.d(TAG, "screenH＝ "+ screenH +" &keyboardH = " + keyboardH + " &r.bottom=" + r.bottom + " &top=" + r.top + " &statusBarH=" + statusBarH);

                if (keyboardH == currentKeyboardH) {//有变化时才处理，否则会陷入死循环
                    return;
                }

                currentKeyboardH = keyboardH;
                screenHeight = screenH;//应用屏幕的高度
                editTextBodyHeight = edittextbody.getHeight();

                if (keyboardH < 150) {//说明是隐藏键盘的情况
                    updateEditTextBodyVisible(View.GONE);
                    return;
                }
//                //偏移listview
//                if(layoutManager!=null && commentConfig != null){
//                    layoutManager.scrollToPositionWithOffset(commentConfig.circlePosition + CircleAdapter.HEADVIEW_SIZE, getListviewOffset(commentConfig));
//                }
            }
        });
    }

    /**
     * 获取状态栏高度
     *
     * @return
     */
    private int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if (edittextbody != null && edittextbody.getVisibility() == View.VISIBLE) {
                //edittextbody.setVisibility(View.GONE);
                updateEditTextBodyVisible(View.GONE);
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }


    private void addLayoutListener(final View main, final View scroll) {
        main.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect rect = new Rect();
                main.getWindowVisibleDisplayFrame(rect);//rect为输出参数,因此rect不允许为null
                int mainInvisibleHeight = main.getRootView().getHeight() - rect.bottom;
                System.out.println(scroll.getBottom());
                if (mainInvisibleHeight > 100) {
                    int[] location = new int[2];
                    scroll.getLocationOnScreen(location);//输入参数必须是一个长度为2的int数组
                    int scrollHeight = (location[1] + scroll.getHeight() - rect.bottom);
                    main.scrollTo(0, scrollHeight);
                } else {
                    main.scrollTo(0, 0);
                }

            }
        });
    }

    /**
     * 点赞  1 点赞  2取消点赞
     */
    private void makeGood(String shareId, final int pos) {
        Map<String, String> map = new HashMap<>();
        map.put("op", "likeShare");
        map.put("share_id", shareId);
        map.put("customer_id", DoctorHelper.getId());
        map.put("status", "1");
        ApiService.OKHttpStationCommonUrl(map, new ApiCallbackWrapper<String>(this) {
            @Override
            public void onResponse(String response) {
                super.onResponse(response);
                try {
                    JSONObject obj = new JSONObject(response);
                    if ("1".equals(obj.optString("code"))) {
                        int count = mAdapter.datas.get(pos).optInt("PRAISE_COUNT");
                        mAdapter.datas.get(pos).put("PRAISE_COUNT", count + 1);
                        mAdapter.datas.get(pos).put("ISLIKE", 1);
                        mAdapter.notifyDataSetChanged();
                    } else {
                        ToastUtil.showShort(obj.optString("message"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, this);
    }
}
