package com.yksj.consultation.sonDoc.casehistory;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.library.base.base.BaseActivity;
import com.yksj.consultation.adapter.CaseDisCommentAdapter;
import com.yksj.consultation.business.LoginBusiness;
import com.yksj.consultation.sonDoc.R;
import com.yksj.healthtalk.bean.BaseBean;
import com.yksj.healthtalk.bean.CommentBean;
import com.yksj.healthtalk.net.http.ApiCallback;
import com.yksj.healthtalk.net.http.ApiService;
import com.yksj.healthtalk.net.http.MyApiCallback;
import com.yksj.healthtalk.utils.ToastUtil;

import org.apache.http.message.BasicNameValuePair;
import org.handmark.pulltorefresh.library.PullToRefreshBase;
import org.handmark.pulltorefresh.library.PullToRefreshListView;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Request;

/**
 * 病历讨论查看详细界面
 * Created by lmk on 15/10/14.
 */
public class CaseDiscussDetailsActivity extends BaseActivity implements View.OnClickListener,
        PullToRefreshBase.OnRefreshListener2<ListView>{

    private TextView tvCaseName,tvCaseInfo,tvExpert,tvSuggest,tvCommentNum;
    private PullToRefreshListView mPullListView;
    private ListView mListView;
    private CaseDisCommentAdapter mAdapter;
    private String result;
    private String recordId;
    private EditText editComment;
    private Button btnSendComment;
    private int pageSize=1;
    private int attentState=0;//0表示没有关注   1表示已关注   2表示是创建者

    private JSONObject dataObject;


    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.aty_case_discuss_details);
        if (getIntent().hasExtra("recordId")){
            recordId=getIntent().getStringExtra("recordId");
        }else {
            recordId=getIntent().getStringExtra("url");
        }
        initView();
        initData();
    }

    private void initData() {
      //RecordDiscussServlet?OPTION=DETAIL&RECORDID=125103&DOCTORID=3778
        List<BasicNameValuePair> pairs=new ArrayList<>();
        pairs.add(new BasicNameValuePair("OPTION", "DETAIL"));
        pairs.add(new BasicNameValuePair("DOCTORID", LoginBusiness.getInstance().getLoginEntity().getId()));
        pairs.add(new BasicNameValuePair("RECORDID", recordId));
        ApiService.doGetRecordDiscussServlet(pairs, new MyApiCallback<String>(this) {
            @Override
            public void onError(Request request, Exception e) {

            }

            @Override
            public void onResponse(String response) {
                BaseBean bb = com.alibaba.fastjson.JSONObject.parseObject(response, BaseBean.class);
                if ("1".equals(bb.code)) {
                    onBoundData(bb.result);

                } else if (response != null && response instanceof String) {
                    ToastUtil.showShort(bb.message);
                }
            }
        }, this);

    }

    private void onBoundData(String result) {
        this.result=result;
        try {
            dataObject = new JSONObject(result);
            if(dataObject.optString("SHARE_CUSTOMER_ID").equals(LoginBusiness.getInstance().getLoginEntity().getId())){
                titleRightBtn2.setText("删除");
                attentState=2;
            }else {
                attentState=dataObject.optInt("ATTENTION");
                if (attentState==0)
                    titleRightBtn2.setText("关注");
                else
                    titleRightBtn2.setText("取消关注");
            }
            JSONObject obj=new JSONObject(dataObject.optString("EXPERT"));
            tvExpert.setText(obj.optString("EXPERTNAME")+"  "+obj.optString("EXPERTTITLE"));
            tvCaseName.setText(dataObject.optString("MEDICAL_NAME"));
            tvCaseInfo.setText(dataObject.optString("CONDESC"));
//          tvExpert.setText(object.optString("CONSULTATION_CONTENT"));
            tvSuggest.setText(dataObject.optString("DOCTOR_ADVICE"));
            int count=dataObject.optInt("COMMENTCOUNT");
            if (count>0)
                pageSize++;
            tvCommentNum.setText("共"+count+"条评论");
            String commentContent=dataObject.optString("COMMENT");
            if (commentContent!=null&&commentContent.length()>0){

                List<CommentBean> list= JSON.parseArray(commentContent,CommentBean.class);
                mAdapter.addAll(list);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void initView() {
        initializeTitle();
        titleTextV.setText("查看详细");
        titleLeftBtn.setOnClickListener(this);
        titleRightBtn2.setVisibility(View.VISIBLE);
        titleRightBtn2.setText("关注");
        titleRightBtn2.setOnClickListener(this);
        tvCaseName= (TextView) findViewById(R.id.case_discuss_details_name);
        tvCaseInfo= (TextView) findViewById(R.id.case_discuss_details_case);
        tvCaseName.setOnClickListener(this);
        tvCaseInfo.setOnClickListener(this);
        tvExpert= (TextView) findViewById(R.id.case_discuss_details_expert);
        tvSuggest= (TextView) findViewById(R.id.case_discuss_details_suggest);//会诊意见
        tvExpert.setOnClickListener(this);
        tvSuggest.setOnClickListener(this);
        tvCommentNum= (TextView) findViewById(R.id.case_discuss_details_comment);
        btnSendComment= (Button) findViewById(R.id.case_discuss_comment_send);
        editComment= (EditText) findViewById(R.id.case_discuss_comment_edittext);
        mPullListView= (PullToRefreshListView) findViewById(R.id.case_discuss_comment_list);
        mPullListView.setOnRefreshListener(this);
        mListView=mPullListView.getRefreshableView();
        mAdapter=new CaseDisCommentAdapter(CaseDiscussDetailsActivity.this);
        mListView.setAdapter(mAdapter);
        btnSendComment.setOnClickListener(this);
    }

    private void sendComment(String comment){
        //RecordDiscussServlet?OPTION=COMMENT&RECORDID=225885&UPPER_REPLY_ID=0&CONTENT=ZZZZZ&CUSTID=
        List<BasicNameValuePair> pairs=new ArrayList<>();
        pairs.add(new BasicNameValuePair("OPTION", "COMMENT"));
        pairs.add(new BasicNameValuePair("CUSTID", LoginBusiness.getInstance().getLoginEntity().getId()));
        pairs.add(new BasicNameValuePair("RECORDID", recordId));
        pairs.add(new BasicNameValuePair("UPPER_REPLY_ID", "0"));
        pairs.add(new BasicNameValuePair("CONTENT", comment));
        ApiService.doGetRecordDiscussServlet(pairs, new ApiCallback<String>() {
            @Override
            public void onError(Request request, Exception e) {

            }

            @Override
            public void onResponse(String response) {
                BaseBean bb = com.alibaba.fastjson.JSONObject.parseObject(response, BaseBean.class);
                if ("1".equals(bb.code)) {
                    editComment.setText("");
                    InputMethodManager imm = (InputMethodManager) getSystemService(CaseDiscussDetailsActivity.this.INPUT_METHOD_SERVICE);
                    // 隐藏软键盘
                    imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
                    pageSize=1;
                    showComment();
                }
                ToastUtil.showShort(bb.message);
            }
        }, this);
    }
    //查看评论
    private void showComment(){
      //http://220.194.46.204/DuoMeiHealth/RecordDiscussServlet?OPTION=COMMENTCONTENT&RECORDID=225885&PAGENUM=1&PAGESIZE=20
        List<BasicNameValuePair> pairs=new ArrayList<>();
        pairs.add(new BasicNameValuePair("OPTION", "COMMENTCONTENT"));
        pairs.add(new BasicNameValuePair("RECORDID", recordId));
        pairs.add(new BasicNameValuePair("PAGENUM", pageSize+""));
        pairs.add(new BasicNameValuePair("PAGESIZE", "10"));
        ApiService.doGetRecordDiscussServlet(pairs, new ApiCallback<String>() {
            @Override
            public void onError(Request request, Exception e) {

            }
            @Override
            public void onBefore(Request request) {
                super.onBefore(request);
                mPullListView.setRefreshing();
            }

            @Override
            public void onAfter() {
                mPullListView.onRefreshComplete();
                super.onAfter();
            }

            @Override
            public void onResponse(String response) {
                BaseBean bb = com.alibaba.fastjson.JSONObject.parseObject(response, BaseBean.class);
                if ("1".equals(bb.code)) {
                    try {
                        JSONObject jsonObject=new JSONObject(bb.result);
                        tvCommentNum.setText("共"+jsonObject.optInt("COUNT") +"条评论");
                        List<CommentBean> list = JSON.parseArray(jsonObject.optString("DETAILS"), CommentBean.class);
                        if (pageSize==1)
                            mAdapter.removeAll();

                        if (list!=null&&list.size()>0){
                            mAdapter.addAll(list);
                            pageSize++;
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }else
                    ToastUtil.showShort(bb.message);
            }
        }, this);
    }

    //关注病历
    private void attentCaseDiscuss(){
        //http://220.194.46.204/DuoMeiHealth/RecordDiscussServlet?OPTION=COMMENTCONTENT&RECORDID=225885&PAGENUM=1&PAGESIZE=20
        List<BasicNameValuePair> pairs=new ArrayList<>();
        pairs.add(new BasicNameValuePair("OPTION", "COMMENTCONTENT"));
        pairs.add(new BasicNameValuePair("RECORDID", recordId));
        pairs.add(new BasicNameValuePair("PAGENUM", pageSize+""));
        pairs.add(new BasicNameValuePair("PAGESIZE", "10"));
        ApiService.doGetRecordDiscussServlet(pairs, new ApiCallback<String>() {
            @Override
            public void onError(Request request, Exception e) {

            }

            @Override
            public void onResponse(String response) {
                BaseBean bb = com.alibaba.fastjson.JSONObject.parseObject(response, BaseBean.class);
                if ("1".equals(bb.code)) {
                    try {
                        JSONObject jsonObject=new JSONObject(bb.result);
                        tvCommentNum.setText("共"+jsonObject.optInt("COUNT") +"条评论");
                        List<CommentBean> list = JSON.parseArray(jsonObject.optString("DETAILS"), CommentBean.class);
                        if (pageSize==1)
                            mAdapter.removeAll();

                        if (list!=null&&list.size()>0){
                            mAdapter.addAll(list);
                            pageSize++;
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }else
                    ToastUtil.showShort(bb.message);
            }
        }, this);
    }


    @Override
    public void onClick(View v) {
        Intent intent=null;
        switch (v.getId()){
            case R.id.title_back:
                onBackPressed();
                break;
            case R.id.title_right2:
                if (recordId!=null)
                    operateCaseDiscuss();
                break;
            case R.id.case_discuss_comment_send:
                String comment=editComment.getText().toString().trim();
                if (!TextUtils.isEmpty(comment))
                    sendComment(comment);
                break;
            case R.id.case_discuss_details_name:
            case R.id.case_discuss_details_case:
                intent=new Intent(CaseDiscussDetailsActivity.this,CaseShowActivity.class);
                intent.putExtra("result",result);
                startActivity(intent);
                break;
            case R.id.case_discuss_details_expert:
            case R.id.case_discuss_details_suggest:
                intent=new Intent(CaseDiscussDetailsActivity.this,CaseShowSuggestActivity.class);
                intent.putExtra("expert","邢研");
                intent.putExtra("suggest",dataObject.optString("DOCTOR_ADVICE"));
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {

    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
        showComment();
    }

    //对比你讨论进行操作  关注 取消关注 删除
    private void operateCaseDiscuss(){
//192.168.16.45:8899/DuoMeiHealth/ConsultationInfoSet?TYPE=cancelFocusMedicalRecord&CUSTOMERID=&MEDICAL_RECORD_ID=
        List<BasicNameValuePair> pairs=new ArrayList<>();
        if (attentState==0){//0表示没有关注   1表示已关注   2表示是创建者
            pairs.add(new BasicNameValuePair("TYPE","focusMedicalRecord"));//去关注
        }else if (attentState==1){
            pairs.add(new BasicNameValuePair("TYPE","cancelFocusMedicalRecord"));//取消关注
        }else {
            pairs.add(new BasicNameValuePair("TYPE","removeMedicalRecord"));

        }
        pairs.add(new BasicNameValuePair("CUSTOMERID", LoginBusiness.getInstance().getLoginEntity().getId()));
        pairs.add(new BasicNameValuePair("MEDICAL_RECORD_ID", recordId));
        ApiService.doGetConsultationInfoSet(pairs, new MyApiCallback<String>(this) {
            @Override
            public void onError(Request request, Exception e) {

            }

            @Override
            public void onResponse(String response) {
                if (response==null||response.length()==0)
                    return;
                try {
                    JSONObject object=new JSONObject(response);
                    if ("1".equals(object.optString("code"))){
                        if (attentState==0){
                            attentState=1;
                            titleRightBtn2.setText("取消关注");
                        }else if (attentState==1){
                            attentState=0;
                            titleRightBtn2.setText("关注");
                        }else {
                            setResult(RESULT_OK);
                            onBackPressed();
                        }
                    }
                    ToastUtil.showShort(CaseDiscussDetailsActivity.this,object.optString("message"));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },this);

    }

}