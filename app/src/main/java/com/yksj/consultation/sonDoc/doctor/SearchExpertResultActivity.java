package com.yksj.consultation.sonDoc.doctor;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.alibaba.fastjson.JSONObject;
import com.yksj.consultation.adapter.SelectExpertListAdapter;
import com.yksj.consultation.app.AppContext;
import com.yksj.consultation.comm.RootListActivity;
import com.yksj.consultation.comm.SingleBtnFragmentDialog;
import com.yksj.consultation.constant.Configs;
import com.yksj.consultation.event.MyEvent;
import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.sonDoc.casehistory.DoctorWriteCaseActivity;
import com.yksj.consultation.sonDoc.consultation.consultationorders.AtyOrderDetails;
import com.yksj.consultation.sonDoc.friend.ConsultMessageActivity;
import com.yksj.healthtalk.bean.BaseBean;
import com.yksj.healthtalk.bean.DoctorListData;
import com.yksj.healthtalk.bean.DoctorSimpleBean;
import com.yksj.healthtalk.net.http.ApiCallback;
import com.yksj.healthtalk.net.http.ApiService;
import com.yksj.healthtalk.net.http.RequestParams;
import com.yksj.healthtalk.net.socket.SmartFoxClient;
import com.yksj.healthtalk.utils.ToastUtil;

import org.apache.http.message.BasicNameValuePair;
import org.greenrobot.eventbus.EventBus;
import org.handmark.pulltorefresh.library.PullToRefreshBase;
import org.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import org.handmark.pulltorefresh.library.PullToRefreshListView;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Request;

/**
 * 搜索专家的结果列表,一个单独的列表
 * @author lmk
 *
 */
public class SearchExpertResultActivity extends RootListActivity implements OnClickListener,SelectExpertListAdapter.OnClickSelectListener,
OnRefreshListener2<ListView>{

	private PullToRefreshListView mPullRefreshListView;
	private ListView mListView;
	private SelectExpertListAdapter mAdapter;
	private String duomeiNum="",url="",merchantId="";//分别是搜索的名称,URL,医疗机构id
	private int type=1;//1表示找专家按名称搜索,2表示按URL搜索,如首页的三个特色医生的跳转,3医疗机构的医生按名称搜索
	private int pageSize=1;
	private RelativeLayout mNullLayout;
	private String officeCode,consultId,pid,officeName;
	private int goalType=0;
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.search_result_layout);
		initView();
		initData();
	}

	//初始化数据
	private void initData() {
		Intent intent=getIntent();
		if (getIntent().hasExtra("OFFICECODE")){
			officeCode=getIntent().getStringExtra("OFFICECODE");
		}
		if (getIntent().hasExtra("OFFICENAME")){
			officeName=getIntent().getStringExtra("OFFICENAME");
		}
		consultId=getIntent().getStringExtra("consultId");
		goalType=getIntent().getIntExtra("goalType", 0);
		pid=getIntent().getStringExtra("PID");
		duomeiNum=getIntent().getStringExtra("result");
		loadData();
		
	}

	/**
	 * 按类型加载数据
	 * 每页加载20条
	 */
	private void loadData() {
		switch (type) {
		case 1:////1表示全局找专家按名称搜索
			//根据姓名职称专长模糊查询专家列表
			//TYPE=findExpertByOfficeAndName&UPPER_OFFICE_ID=&NAME=&UNITCODE=&CONSULTATION_CENTER_ID=&PAGESIZE=&PAGENUM=
			List<BasicNameValuePair> pairs=new ArrayList<>();
			pairs.add(new BasicNameValuePair("TYPE","findExpertByOfficeAndName"));
			pairs.add(new BasicNameValuePair("UPPER_OFFICE_ID",officeCode));
			pairs.add(new BasicNameValuePair("NAME",duomeiNum));
			pairs.add(new BasicNameValuePair("UNITCODE",""));
			pairs.add(new BasicNameValuePair("CONSULTATION_CENTER_ID", AppContext.APP_CONSULTATION_CENTERID));
			pairs.add(new BasicNameValuePair("PAGESIZE", pageSize+""));
			pairs.add(new BasicNameValuePair("PAGENUM", "20"));
			ApiService.doGetConsultationInfoSet(pairs, new ApiCallback<String>() {
				@Override
				public void onError(Request request, Exception e) {

				}

				@Override
				public void onResponse(String response) {
					DoctorListData dld= com.alibaba.fastjson.JSONObject.parseObject(response,DoctorListData.class);
					ArrayList<DoctorSimpleBean> list= dld.result;
					if (list != null) {
						if (pageSize == 1)//第一次加载
							mAdapter.removeAll();
						if (list.size() != 0) {//加载出了数据

							mAdapter.addAll(list);
						}
					} else if (response != null && response instanceof String) {
						ToastUtil.showShort(dld.message);
					}
				}

				@Override
				public void onBefore(Request request) {

					mPullRefreshListView.setRefreshing();
					super.onBefore(request);
				}

				@Override
				public void onAfter() {
					mPullRefreshListView.onRefreshComplete();
					super.onAfter();
				}
			}, this);
//			ApiService.doHttpDoctorSearchResult(duomeiNum, SmartFoxClient.getLoginUserId(), pageSize, 20, objectHandler);
			break;
		case 2://2表示按URL搜索
			String path= Configs.WEB_IP+url+"&PAGENUM=20&CUSTOMERID="+SmartFoxClient.getLoginUserId()+"&PAGESIZE="+pageSize;
//			ApiService.doHttpDoctorSearchResultByUrl(path, objectHandler);
			break;
		case 3://3表示医疗机构的医生按名称搜索
			RequestParams params = new RequestParams();
			params.put("customerId", SmartFoxClient.getLoginUserId());
			params.put("duomeihao", duomeiNum);
			params.put("merchantId", getIntent().getExtras().getString("merchantid"));
			params.put("publicFlag", "1");
			params.put("pageSize", pageSize+"");
			params.put("pageNum", "20");
//			ApiService.doHttpFindMerchantDocByNameOrSpeciallyOrDuomei(params, objectHandler);
			break;
		}
	}

	//初始化视图
	private void initView() {
		initializeTitle();
		titleLeftBtn.setOnClickListener(this);
		titleTextV.setText(R.string.knoweSearch);
//		mPullRefreshListView=(PullToRefreshListView) findViewById(R.id.search_result_pulllist);
		mNullLayout=(RelativeLayout) findViewById(R.id.load_faile_layout);
		mPullRefreshListView.setOnRefreshListener(this);
		mListView=mPullRefreshListView.getRefreshableView();
		mAdapter=new SelectExpertListAdapter(SearchExpertResultActivity.this,"");
		mAdapter.setSelectListener(this);
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent(SearchExpertResultActivity.this, AtyDoctorMassage.class);
				intent.putExtra("id", mAdapter.datas.get(position-1).CUSTOMER_ID+"");
				intent.putExtra("type", 0);//0是专家
				intent.putExtra("OFFICECODE", officeCode);//0是专家
				intent.putExtra("OFFICENAME", officeName);
				intent.putExtra("PID", pid);//0是专家
				intent.putExtra("goalType", goalType);//0是专家
				if (goalType!=1)
					intent.putExtra("consultId",consultId);
				startActivityForResult(intent, 201);
			}
		});
	}
	

	


	@Override
	public ListView getListView() {
		return mListView;
	}

	@Override
	public BaseAdapter getAdapter() {
		return mAdapter;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.title_back:
			onBackPressed();
			break;
		}
	}

	//下拉刷新
	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		pageSize=1;
		loadData();
	}

	//上拉加载
	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		loadData();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode==RESULT_OK){
			if(requestCode==11&&data!=null&&data.hasExtra("isChangeAttention")){
				if(data.getBooleanExtra("isChangeAttention", false)){
//					mAdapter.datas.get(data.getIntExtra("position", 0)).
//					setIsAttentionFriend(data.getIntExtra("attentionFriend", 0));
//					mAdapter.notifyDataSetChanged();
				}
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onClickSelect(DoctorSimpleBean dsb) {
		if (goalType==1){
			Intent intent = new Intent(SearchExpertResultActivity.this, ConsultMessageActivity.class);
			intent.putExtra("data", dsb);
			intent.putExtra("OFFICECODE", officeCode);
			intent.putExtra("OFFICENAME", officeName);
			intent.putExtra("PID",pid);
			startActivity(intent);
		}else {


		List<BasicNameValuePair> valuePairs=new ArrayList<>();
		valuePairs.add(new BasicNameValuePair("TYPE","reSelectedExpert"));
		valuePairs.add(new BasicNameValuePair("CUSTOMERID", dsb.CUSTOMER_ID+""));
		valuePairs.add(new BasicNameValuePair("CONSULTATIONID", consultId));
		valuePairs.add(new BasicNameValuePair("SERVICE_PRICE", "" + dsb.SERVICE_PRICE));
		ApiService.doGetConsultationInfoSet(valuePairs, new ApiCallback<String>() {
			@Override
			public void onError(Request request, Exception e) {

			}

			@Override
			public void onResponse(String response) {
				BaseBean bb = JSONObject.parseObject(response, BaseBean.class);
				if ("1".equals(bb.code)) {

					EventBus.getDefault().post(new MyEvent("refresh", 2));
					if (goalType==2){
						Intent intent=new Intent(SearchExpertResultActivity.this, AtyOrderDetails.class);
//						intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(intent);
					}else {
						SingleBtnFragmentDialog.showSinglebtn(SearchExpertResultActivity.this, "您已为患者选择专家,现在请为患者填写病历吧。", "填写病历", new SingleBtnFragmentDialog.OnClickSureBtnListener() {
							@Override
							public void onClickSureHander() {
								Intent intent = new Intent(SearchExpertResultActivity.this, DoctorWriteCaseActivity.class);
								intent.putExtra("consultId", consultId);
								startActivity(intent);
							}
						}).show();
					}
				}else
					ToastUtil.showShort(SearchExpertResultActivity.this, bb.message);

			}
		}, this);
		}
	}
}
