package com.yksj.consultation.plan;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.library.base.base.BaseActivity;
import com.library.base.umeng.UmengShare;
import com.library.base.utils.UriUtils;
import com.squareup.picasso.Picasso;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.yksj.consultation.adapter.SeePlanAdapter;
import com.yksj.consultation.app.AppContext;
import com.yksj.consultation.comm.DoubleBtnFragmentDialog;
import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.utils.DoctorHelper;
import com.yksj.healthtalk.net.http.ApiCallback;
import com.yksj.healthtalk.net.http.ApiService;
import com.yksj.healthtalk.utils.TimeUtil;
import com.yksj.healthtalk.utils.ToastUtil;

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
 * 查看计划
 */
public class SeePlanActivity extends BaseActivity implements View.OnClickListener {

    private ListView mListView;
    private SeePlanAdapter mAdapter;
    private View header;
    private ImageView headImage;
    private TextView textname;
    private TextView textsex;
    private TextView textage;
    private TextView title;
    private TextView tv_targe;
    private TextView tv_time_text;
    private List<JSONObject> mList = null;
    private ImageView success;
    private ImageView failure;
    private ImageView share;
    private boolean isSuccess = false;
    private boolean isFailure = false;
    public String name;
    public String sex;
    public String age;
    private String plan_status;//成功与否状态
    private static final int WRITFLAG = 10002;
    //弹出分享窗口
    PopupWindow mPopupWindow;
    private Uri uri;
    public String PLAN_START;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_see_plan);
        initView();
        initData();
    }

    private String PLAN_ID = "";
    private String children_id = "";
    public String CUSTOMER_REMARK = "";

    private void initView() {
        initializeTitle();
        Intent intent = getIntent();
        PLAN_ID = intent.getStringExtra("plan_id");
        children_id = intent.getStringExtra("children_id");
        PLAN_START = intent.getStringExtra("PLAN_START");
        name = intent.getStringExtra("name");
        age = intent.getStringExtra("age");
        sex = intent.getStringExtra("sex");

        titleTextV.setText("计划详情");
        titleRightBtn2.setVisibility(View.VISIBLE);
        titleRightBtn2.setText("填写关爱计划");
        titleLeftBtn.setOnClickListener(this);
        titleRightBtn2.setOnClickListener(this);
        mListView = (ListView) findViewById(R.id.lv_seeplan);

        header = View.inflate(this, R.layout.head_see_plan, null);
        headImage = (ImageView) header.findViewById(R.id.image_head);
        textname = (TextView) header.findViewById(R.id.tv_member_name);
        textsex = (TextView) header.findViewById(R.id.tv_member_sex);
        textage = (TextView) header.findViewById(R.id.tv_member_age);

        textname.setText(name);
        if ("null".equals(intent.getStringExtra("age"))) {
            textage.setText("");
        } else {
            textage.setText(intent.getStringExtra("age"));
        }

        if ("1".equals(intent.getStringExtra("sex"))) {
            textsex.setText("男");
        } else if ("0".equals(intent.getStringExtra("sex"))) {
            textsex.setText("女");
        } else {
            textsex.setText("");
        }


        Picasso.with(SeePlanActivity.this).load(intent.getStringExtra("url")).placeholder(R.drawable.waterfall_default).into(headImage);
        title = (TextView) header.findViewById(R.id.title);
        tv_targe = (TextView) header.findViewById(R.id.tv_targe);
        tv_time_text = (TextView) header.findViewById(R.id.tv_time_text);

        success = (ImageView) header.findViewById(R.id.success);
        failure = (ImageView) header.findViewById(R.id.failure);
        share = (ImageView) header.findViewById(R.id.plan_share);

        if ("20".equals(PLAN_START)) {
            success.setSelected(true);
        } else if ("30".equals(PLAN_START)) {
            failure.setSelected(true);
        }

        success.setOnClickListener(this);
        failure.setOnClickListener(this);
        share.setOnClickListener(this);

        mListView.addHeaderView(header);
        mList = new ArrayList<JSONObject>();
        mAdapter = new SeePlanAdapter(this, mList);
        mListView.setAdapter(mAdapter);
        uri = Uri.parse("android.resource://" + AppContext.getApplication().getPackageName() + "/"
                + R.drawable.ic_launcher);
        // initData();
    }

    public void initData() {//100084 100013
        Map<String, String> map = new HashMap<>();
        map.put("plan_id", PLAN_ID);
        ApiService.OKHttpPlanDetail(map, new ApiCallback<String>() {
            @Override
            public void onError(Request request, Exception e) {

            }

            @Override
            public void onResponse(String content) {
                try {
                    DoctorHelper.getId();
                    if (content != null) {
                        JSONObject object = new JSONObject(content);
                        JSONObject massage = object.optJSONObject("plan");
                        title.setText(massage.optString("PLAN_TITLE"));
                        tv_targe.setText(massage.optString("PLAN_TARGET"));
                        CUSTOMER_REMARK = massage.optString("CUSTOMER_REMARK");
                        tv_time_text.setText(massage.optString("PLAN_CYCLE") + "周    " + "(" + TimeUtil.getFormatDate2(massage.optString("PLAN_START")) + "至" + TimeUtil.getFormatDate2(massage.optString("PLAN_END")) + ")");

                        mList = new ArrayList<JSONObject>();
                        JSONArray record = object.getJSONArray("record");
                        int count = record.length();
                        if (count > 0) {
                            for (int i = 0; i < record.length(); i++) {
                                JSONObject jsonObject = record.getJSONObject(i);
//                                for (int j = 0; j < 40; j++) {
                                mList.add(jsonObject);
//                                }
                            }
                            mAdapter.onBoundData(mList);
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
        Intent intent = null;
        switch (v.getId()) {
            case R.id.title_back:
                onBackPressed();
                break;
            case R.id.title_right2:
                intent = new Intent(SeePlanActivity.this, WritePlanActivity.class);
                intent.putExtra("plan_id", PLAN_ID);
                intent.putExtra("children_id", children_id);
                intent.putExtra("CUSTOMER_REMARK", CUSTOMER_REMARK);
                startActivityForResult(intent, WRITFLAG);
                break;
            case R.id.success:
                DoubleBtnFragmentDialog.showDefault(getSupportFragmentManager(), "您确定计划成功了吗", "取消", "确定", new DoubleBtnFragmentDialog.OnDilaogClickListener() {
                    @Override
                    public void onDismiss(DialogFragment fragment) {
                    }

                    @Override
                    public void onClick(DialogFragment fragment, View v) {
                        plan_status = "20";
                        planChange();
                        finish();
                    }
                });

                if (isSuccess == false) {
                    isSuccess = true;
                    success.setSelected(true);
                    failure.setSelected(false);

                } else if (isSuccess == true) {
                    isSuccess = false;
                    success.setSelected(false);
                    failure.setSelected(true);
                }
                break;
            case R.id.failure:
                DoubleBtnFragmentDialog.showDefault(getSupportFragmentManager(), "您确定计划失败了吗", "取消", "确定", new DoubleBtnFragmentDialog.OnDilaogClickListener() {
                    @Override
                    public void onDismiss(DialogFragment fragment) {

                    }

                    @Override
                    public void onClick(DialogFragment fragment, View v) {
                        plan_status = "30";
                        planChange();
                        finish();
                    }
                });
                if (isFailure == false) {
                    isFailure = true;
                    success.setSelected(false);
                    failure.setSelected(true);

                } else if (isFailure == true) {
                    isFailure = false;
                    success.setSelected(true);
                    failure.setSelected(false);
                }
                break;
            case R.id.plan_share:
                showShare();
                break;
            case R.id.friendcircle://微信朋友圈
                sendWXMS();
                quitPopWindow();
                break;
            case R.id.wechat://微信好友
                sendWX();
                quitPopWindow();
                break;
            case R.id.weibo://新浪微博分享
                shareSina();
                quitPopWindow();
                break;
            case R.id.qqroom://qq空间
                shareQQZone();
                quitPopWindow();
                break;
            case R.id.btn_cancel:
                ToastUtil.showShort("取消");
                quitPopWindow();
                break;
        }
    }

    /**
     * 微信朋友圈
     */
    private void sendWXMS() {
        UmengShare.from(this)
                .share(SHARE_MEDIA.WEIXIN_CIRCLE)
                .setTitle(getString(R.string.string_share_title))
                .setContent(getString(R.string.string_share_content))
                .setThumb(UriUtils.getPathForUri(this, uri))
                .setUrl(AppContext.getApiRepository().HTML + "/plan.html?" + "plan_id=" + PLAN_ID)
                .startShare();
    }

    /**
     * 微信分享
     */
    private void sendWX() {
        UmengShare.from(this)
                .share(SHARE_MEDIA.WEIXIN)
                .setTitle(getString(R.string.string_share_title))
                .setContent(getString(R.string.string_share_content))
                .setThumb(UriUtils.getPathForUri(this, uri))
                .setUrl(AppContext.getApiRepository().HTML + "/plan.html?" + "plan_id=" + PLAN_ID)
                .startShare();
    }

    /**
     * 分享到新浪
     */
    private void shareSina() {
        UmengShare.from(this)
                .share(SHARE_MEDIA.SINA)
                .setTitle(getString(R.string.string_share_title))
                .setContent(getString(R.string.string_share_content))
                .setThumb(UriUtils.getPathForUri(this, uri))
                .setUrl(AppContext.getApiRepository().HTML + "/plan.html?" + "plan_id=" + PLAN_ID)
                .startShare();
    }

    /**
     * 分享到qq空间
     */
    private void shareQQZone() {
        UmengShare.from(this)
                .share(SHARE_MEDIA.QZONE)
                .setTitle(getString(R.string.string_share_title))
                .setContent(getString(R.string.string_share_content))
                .setThumb(UriUtils.getPathForUri(this, uri))
                .setUrl(AppContext.getApiRepository().HTML + "/plan.html?" + "plan_id=" + PLAN_ID)
                .startShare();
    }

    private void quitPopWindow() {
        if (mPopupWindow != null && mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
        }
    }

    private void showShare() {
        if (mPopupWindow == null) {
            LayoutInflater inflater = LayoutInflater.from(this);
            View view = inflater.inflate(R.layout.alert_dialog_share, null);
            view.findViewById(R.id.friendcircle).setOnClickListener(this);
            view.findViewById(R.id.wechat).setOnClickListener(this);
            view.findViewById(R.id.weibo).setOnClickListener(this);
            view.findViewById(R.id.qqroom).setOnClickListener(this);
            view.findViewById(R.id.btn_cancel).setOnClickListener(this);

            mPopupWindow = new PopupWindow(view, ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
            mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
            mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    WindowManager.LayoutParams params = SeePlanActivity.this.getWindow()
                            .getAttributes();
                    params.alpha = 1.0f;
                    SeePlanActivity.this.getWindow().setAttributes(params);
                }
            });
        } else if (mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
            return;
        }

        WindowManager.LayoutParams params = this.getWindow().getAttributes();
        params.alpha = 0.5f;
        this.getWindow().setAttributes(params);
        mPopupWindow.showAtLocation(SeePlanActivity.this.findViewById(R.id.ll_main_seeplan), Gravity.BOTTOM, 0, 0);
    }

    /**
     * 变更计划状态
     */
    private void planChange() {
        Map<String, String> map = new HashMap<>();
        map.put("plan_id", PLAN_ID);
        map.put("plan_status", plan_status);
        ApiService.OKHttpPlanChange(map, new ApiCallback<String>() {
            @Override
            public void onError(Request request, Exception e) {
            }

            @Override
            public void onResponse(String content) {
                try {
                    JSONObject object = new JSONObject(content);
                    if ("0".equals(object.optString("code"))) {
                        ToastUtil.showShort(object.optString("message"));
                    } else {
                        ToastUtil.showShort(object.optString("message"));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }, this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == WRITFLAG && resultCode == RESULT_OK) {
            initData();
        }
    }

    /**
     * 获取图片Bitmap
     * @param uri
     * @return
     */
    private Bitmap getBitmapFromUri(Uri uri) {
        try {
            // 读取uri所在的图片
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
