package com.yksj.consultation.sonDoc.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.library.base.base.BaseActivity;
import com.yksj.consultation.adapter.memberAdapter;
import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.sonDoc.listener.DialogOnClickListener;
import com.yksj.consultation.utils.DoctorHelper;
import com.yksj.healthtalk.entity.DocPlanMemberEntity;
import com.yksj.healthtalk.net.http.ApiCallback;
import com.yksj.healthtalk.net.http.ApiService;
import com.yksj.healthtalk.net.http.AsyncHttpResponseHandler;
import com.yksj.healthtalk.utils.ToastUtil;

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
 * 成员列表
 */
public class MemberActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener {
    private PullToRefreshListView mPullRefreshListView;
    private ListView mListView;
    private memberAdapter adapter;
    private MyAlertDialog dialog;
    private DocPlanMemberEntity docPlanMemberEntity;
    private List<DocPlanMemberEntity> data;
    private String children_id;//宝贝ID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member);
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    private void initView() {
        initializeTitle();
        Intent intent = getIntent();
        children_id = intent.getStringExtra("CHILDREN_ID");

        titleTextV.setText("成员");
        titleRightBtn2.setVisibility(View.VISIBLE);
        titleRightBtn2.setText("添加");
        titleLeftBtn.setOnClickListener(this);
        titleRightBtn2.setOnClickListener(this);
        mPullRefreshListView=(PullToRefreshListView) findViewById(R.id.member_list);
        mListView=mPullRefreshListView.getRefreshableView();
        adapter = new memberAdapter(this);
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(this);
        data = new ArrayList<DocPlanMemberEntity>();
    }

    private List<String> list = new ArrayList<>();
    /**
     * 加载数据
     */
    private String CHILDREN_IDchildren_id = "100003";

    private void initData() {
        if (data!=null){
            data.clear();
        }
        Map<String,String> map=new HashMap<>();
        map.put("children_id",children_id);
        ApiService.OKHttpGetMemberList(map, new ApiCallback<String>() {

            @Override
            public void onError(Request request, Exception e) {

            }
            @Override
            public void onResponse(String content) {
                try {
                    JSONObject obj = new JSONObject(content);
                    JSONArray array = obj.getJSONArray("plans");
                    JSONObject item;
                    for (int i = 0; i < array.length(); i++) {
                        item = array.getJSONObject(i);
                        docPlanMemberEntity = new DocPlanMemberEntity();
                        docPlanMemberEntity.setName(item.optString("CUSTOMER_NICKNAME"));
                        docPlanMemberEntity.setImage(item.optString("CLIENT_ICON_BACKGROUND"));
                        docPlanMemberEntity.setSex(item.optString("CUSTOMER_SEX"));
                        docPlanMemberEntity.setCREATOR_ID(item.optString("CREATOR_ID"));
                        docPlanMemberEntity.setCustomer_id(item.optString("CUSTOMER_ID"));
                        docPlanMemberEntity.setCustomer_remark(item.optString("CUSTOMER_REMARK"));
                        data.add(docPlanMemberEntity);
                        list.add(item.optString("CUSTOMER_ID"));
                    }
                    adapter.onBoundData(data);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()){
            case R.id.title_back:
                onBackPressed();
                break;
            case R.id.title_right2:
                intent = new Intent(this,AddMemberActivity.class);
                intent.putExtra("CHILDREN_ID",children_id);
                intent.putExtra("customer_id",list.toString());
                startActivity(intent);
                break;
        }
    }
    private String member_id;
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        member_id = adapter.datas.get(position-1).getCustomer_id();
        initMDDialog(position,member_id);
        dialog.show();
    }

    /**
     * 弹出dialog
     */
    private void initMDDialog(int position, final String member_id) {
        final int itemPosition = position;
        dialog = new MyAlertDialog.Builder(MemberActivity.this)
                .setHeight(0.21f)  //屏幕高度*0.21
                .setWidth(0.7f)  //屏幕宽度*0.7
                .setTitleText("添加备注名称")
                .setOnclickListener(new DialogOnClickListener() {
                    @Override
                    public void clickButton(View view) {
                        modificationName(itemPosition,member_id);
                    }
                })
                .build();
    }

    /**
     * 添加备注姓名
     */
    private String name;
    private String customer_id = DoctorHelper.getId();;//3176
    private void modificationName(final int itemPosition,String member_id) {
        name = dialog.edittext().toString();
        ApiService.OKHttpUpdateMemberRemark(children_id,member_id,name, new AsyncHttpResponseHandler(this){
            @Override
            public void onSuccess(String content) {
                super.onSuccess(content);
                try {
                    JSONObject obj = new JSONObject(content);
                    if ("0".equals(obj.optString("code"))){
                        dialog.dismiss();
                        ToastUtil.showShort(obj.optString("message"));
                        adapter.datas.get(itemPosition-1).setCustomer_remark(name);
                        adapter.notifyDataSetChanged();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Throwable error, String content) {
                super.onFailure(error, content);
            }
        });
    }
}
