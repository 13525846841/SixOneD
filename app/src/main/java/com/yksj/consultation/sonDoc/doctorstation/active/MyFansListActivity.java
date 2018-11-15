package com.yksj.consultation.sonDoc.doctorstation.active;

import java.util.ArrayList;
import java.util.List;

import org.handmark.pulltorefresh.library.PullToRefreshBase;
import org.handmark.pulltorefresh.library.PullToRefreshListView;
import org.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.yksj.healthtalk.entity.CustomerInfoEntity;
import com.yksj.healthtalk.net.http.ApiService;
import com.yksj.healthtalk.net.http.ObjectHttpResponseHandler;
import com.yksj.healthtalk.net.http.RequestParams;
import com.yksj.healthtalk.net.socket.SmartFoxClient;
import com.yksj.consultation.adapter.FindFriendListAdapter;
import com.yksj.consultation.comm.RootListActivity;
import com.yksj.consultation.sonDoc.R;
import com.yksj.healthtalk.utils.FriendHttpUtil;
import com.yksj.healthtalk.utils.LogUtil;
import com.yksj.healthtalk.utils.ToastUtil;

/**
 * 医生端我的粉丝(我的朋友)列表
 * @author lmk
 *
 */
public class MyFansListActivity extends RootListActivity implements OnClickListener,
OnRefreshListener2<ListView>{

	private PullToRefreshListView mPullToRefreshListView;
	private ListView mListView;
	private FindFriendListAdapter mAdapter;
	private int pageSize = 1;//第几页
	private int pageNum = 15;//每页显示几条
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.doctorstation_fans_list_layout);
		initView();
		loadData();
	}
	
	//初始化视图
	private void initView() {
		initializeTitle();
		titleTextV.setText(R.string.who);
		titleLeftBtn.setOnClickListener(this);
		mPullToRefreshListView=(PullToRefreshListView) findViewById(R.id.fans_pulllist);
		mListView=mPullToRefreshListView.getRefreshableView();
		mPullToRefreshListView.setOnRefreshListener(this);
		mAdapter=new FindFriendListAdapter(this);
		mAdapter.setOnClickFollowListener(this);
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				FriendHttpUtil.chatFromPerson(MyFansListActivity.this, mAdapter.datas.get(position-1));
			}
		});
	}
	
	//加载数据
	private void loadData(){//FLAG  (0-我的医生  1-我的患者  2-我的话题  3-我的粉丝
		//TYPE=findMyList&CUSTOMERID=&PAGESIZE=&PAGENUM=&FLAG=&VALID_MARK=
        //http://220.194.46.204:8080/DuoMeiHealth/GroupConsultationList200?VALID_MARK=40&PAGENUM=20&NAME=&TYPE=findMyList&CUSTOMERID=3779&FLAG=1&PATIENT_GROUP_ID=0&PAGESIZE=1
		RequestParams params = new RequestParams();
		params.put("CUSTOMERID",SmartFoxClient.getLoginUserId());
		params.put("TYPE","findMyList");
		params.put("FLAG","3");
		params.put("PAGESIZE",pageSize+"");
		params.put("PAGENUM",pageNum+"");
		params.put("VALID_MARK","40");
//		ApiService.doHttpFINDMYFRIENDS32(params, new ObjectHttpResponseHandler() {
		ApiService.doHttpGroupConsultationList(params, new ObjectHttpResponseHandler() {
			
			@Override
			public Object onParseResponse(String content) {
                LogUtil.d("DDD","sssss"+content);
				return FriendHttpUtil.jsonAnalysisFriendEntity(content, false);
			}
						
			@Override
			public void onSuccess(int statusCode, Object response) {
				
				super.onSuccess(statusCode, response);
                LogUtil.d("DDD","1111111"+response.toString());
				if(response!=null&&response instanceof List){
					ArrayList<CustomerInfoEntity> datas=(ArrayList<CustomerInfoEntity>) response;
					if(datas!=null&&datas.size()!=0){
						pageSize++;
						mAdapter.addAll(datas);
					}
				}else if(response!=null&&response instanceof String){
					if(response.toString().contains("error_message")){
						try {
							JSONObject object=new JSONObject(response.toString());
							ToastUtil.showShort(object.optString("error_message"));
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				}
			}

			@Override
			public void onStart() {
				mPullToRefreshListView.setRefreshing();
				super.onStart();
			}
			@Override
			public void onFinish() {
				mPullToRefreshListView.onRefreshComplete();
				super.onFinish();
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
		mAdapter.removeAll();
		loadData();
	}

	//上拉加载
	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		loadData();
	}

}
