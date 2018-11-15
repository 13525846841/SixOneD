package com.yksj.consultation.sonDoc.consultation.salon.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.yksj.consultation.constant.Constant;
import com.yksj.consultation.comm.RootListActivity;
import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.im.ChatActivity;
import com.yksj.consultation.sonDoc.consultation.salon.HealthTopicListAdapter;
import com.yksj.consultation.sonDoc.consultation.salon.OnClickChildItemListener;
import com.yksj.consultation.sonDoc.salon.SalonSelectPaymentOptionActivity;
import com.yksj.healthtalk.entity.GroupInfoEntity;
import com.yksj.healthtalk.net.http.ApiService;
import com.yksj.healthtalk.net.http.ObjectHttpResponseHandler;
import com.yksj.healthtalk.net.socket.SmartFoxClient;
import com.yksj.healthtalk.utils.SalonHttpUtil;
import com.yksj.healthtalk.utils.ToastUtil;

import org.handmark.pulltorefresh.library.PullToRefreshBase;
import org.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import org.handmark.pulltorefresh.library.PullToRefreshListView;

import java.util.ArrayList;


/**
 * 话题搜索结果
 * 是一个List列表显示
 * @author lmk
 *
 */
public class TopicSearchResultActivity extends RootListActivity implements OnClickListener,
		SalonSelectPaymentOptionActivity.OnBuyTicketHandlerListener,OnRefreshListener2<ListView>,OnClickChildItemListener {
	private int CHATTINGCODE = 2;
	private PullToRefreshListView mPullListView;
	private ListView mListView;
	private RelativeLayout mNullLayout;//什么数据都没有时显示
	private HealthTopicListAdapter mAdapter;
	private String secondName;//查询的参数id或者文字
	private int pageSize=1,searchType=1;//第一页,查询的类型
	private ArrayList<GroupInfoEntity> datas;
	
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
		if(intent.hasExtra("secondName")&&intent.hasExtra("searchType")){
			searchType=intent.getIntExtra("searchType",1);
			secondName=intent.getStringExtra("secondName");
			loadData();
		}
	}

	//初始化视图
	private void initView() {
		initializeTitle();
		titleLeftBtn.setOnClickListener(this);
		titleTextV.setText(R.string.knoweSearch);
//		mPullListView=(PullToRefreshListView) findViewById(R.id.search_result_pulllist);
		mNullLayout=(RelativeLayout) findViewById(R.id.load_faile_layout);
		mPullListView.setOnRefreshListener(this);
		mListView=mPullListView.getRefreshableView();
		mAdapter=new HealthTopicListAdapter(TopicSearchResultActivity.this);
		mAdapter.setClickListener(this);
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				SalonHttpUtil.onItemClick(TopicSearchResultActivity.this,TopicSearchResultActivity.this,
						getSupportFragmentManager(), mAdapter.datas.get(position - 1),true);
			}
		});
	}

	//加载数据
	private void loadData() {
		ApiService.doHttpRequestLoadSearchTopic(SmartFoxClient.getLoginUserId(), pageSize, searchType, secondName,
				new ObjectHttpResponseHandler(){
					@Override
					public Object onParseResponse(String content) {
						if(content!=null&&!content.contains("error_message")){//加载导数据
							return SalonHttpUtil.jsonAnalysisTopicEntitys(TopicSearchResultActivity.this, content);
						}else{
							return null;
						}
					}
					@Override
					public void onSuccess(int statusCode, Object response) {
						if(response==null){
							ToastUtil.showShort(getResources().getString(R.string.time_out));
							return;
						} 
						datas=(ArrayList<GroupInfoEntity>) response;
						if(pageSize==1){
							mAdapter.removeAll();//第一次加载先清空数据
							if(datas==null||datas.size()==0){
								mNullLayout.setVisibility(View.VISIBLE);
								mPullListView.setVisibility(View.GONE);
								return;
							}
						}
						if(datas!=null&&datas.size()!=0){
							pageSize++;
							mAdapter.addAll(datas);
						}
						super.onSuccess(statusCode, response);
					}
					
					@Override
					public void onStart() {
						mPullListView.setRefreshing();
						super.onStart();
					}
					
					@Override
					public void onFinish() {
						mPullListView.onRefreshComplete();
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
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.title_back://返回
			onBackPressed();
			break;
		}
	}
	
	@Override
	public void onTicketHandler(String state, GroupInfoEntity entity) {
		if ("0".equals(state)) {
		} else if ("-1".equals(state)) {
			ToastUtil.showBasicErrorShortToast(TopicSearchResultActivity.this);
		} else {
			Intent intent1 = new Intent();
			intent1.putExtra(Constant.Chat.KEY_PARAME, entity);
			intent1.setClass(TopicSearchResultActivity.this, ChatActivity.class);
			startActivityForResult(intent1, CHATTINGCODE);
		}
	}
}
