package com.yksj.consultation.sonDoc.consultation.salon.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.library.base.base.BaseActivity;
import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.sonDoc.consultation.salon.TextImageIndexAdapter;
import com.yksj.healthtalk.net.http.ApiService;
import com.yksj.healthtalk.net.http.ObjectHttpResponseHandler;
import com.yksj.healthtalk.utils.SalonHttpUtil;
import com.yksj.healthtalk.utils.SharePreHelper;
import com.yksj.healthtalk.utils.ToastUtil;

import org.handmark.pulltorefresh.library.PullToRefreshListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * 话题搜索界面
 * @author lmk
 */
public class TopicSearchMainUi extends BaseActivity implements OnClickListener,
	OnItemClickListener{
	private PullToRefreshListView mPullListView;
	private ListView mListView;
	private TextImageIndexAdapter mAdapter;
	private ArrayList<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();
	private ArrayList<HashMap<String, String>> history=new ArrayList<HashMap<String,String>>();
	private String keyName="topic_search_historyStrs";
	
	private RelativeLayout mHistoryLayout;//显示搜索历史布局
	private TextView tvClear;//清空历史
	private GridView mGridView;//搜索历史列表
	private SimpleAdapter historyAdapter;//历史列表的适配器

	public Button titleRightBtn;
	public EditText editSearch;

	@Override
	public int createLayoutRes() {
		return R.layout.topic_search_main_ui;
	}

	@Override
	public void initialize(Bundle bundle) {
		super.initialize(bundle);
		initView();
		initData();
	}

	@Override
	protected void onStart() {
		super.onStart();
		history.clear();
		history.addAll(SharePreHelper.getSearchHistory(this,keyName));
		if(history.size()!=0){
			mHistoryLayout.setVisibility(View.VISIBLE);
			historyAdapter.notifyDataSetChanged();
		}else{
			mHistoryLayout.setVisibility(View.GONE);
			return;
		}
	}
	
	//初始化数据
	private void initData() {
		mAdapter = new TextImageIndexAdapter(TopicSearchMainUi.this);
		mListView.setAdapter(mAdapter);
		loadData();
		mListView.setOnItemClickListener(this);
	}

	//去网上加载各种类型及其对应的id
	private void loadData() {
		ApiService.doHttpRequestSearchInterest("groupInfoLay", new ObjectHttpResponseHandler() {
			@Override
			public Object onParseResponse(String content) {
				return SalonHttpUtil.jsonAnalysisInfolys(content);
			}

			@Override
			public void onSuccess(Object response) {
				if(response instanceof List){
					data=(ArrayList<HashMap<String, Object>>) response;
					if(data!=null&&data.size()!=0){
						mAdapter.addAll(data);
					}
				}
				super.onSuccess(response);
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

	public void initSearchView() {
		titleRightBtn = (Button) findViewById(R.id.title_right);
		editSearch = (EditText) findViewById(R.id.title_edit_search);
	}

	//初始化控件
	private void initView() {
		initSearchView();
		titleRightBtn.setText(R.string.cancel);
		titleRightBtn.setOnClickListener(this);
		mHistoryLayout=(RelativeLayout) findViewById(R.id.search_history_layout);
		mGridView=(GridView) findViewById(R.id.search_topic_gridView);
		historyAdapter=new SimpleAdapter(TopicSearchMainUi.this, history, R.layout.search_doctor_history_item, 
				new String[]{"name"}, new int[]{R.id.search_doc_item_text});
		mGridView.setAdapter(historyAdapter);
		mGridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent=new Intent(TopicSearchMainUi.this,TopicSearchResultActivity.class);
				intent.putExtra("secondName", history.get(position).get("name"));
				intent.putExtra("searchType", 2);
				startActivity(intent);
			}
		});
		tvClear=(TextView) findViewById(R.id.search_history_clear);
		mPullListView = (PullToRefreshListView)findViewById(R.id.topic_search_listview);
		mListView=mPullListView.getRefreshableView();
		//设置监听
		tvClear.setOnClickListener(this);
		editSearch.setOnEditorActionListener(new OnEditorActionListener() {
			
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				boolean handled=false;
				if(actionId==EditorInfo.IME_ACTION_SEARCH){
					String text=editSearch.getText().toString().trim();
					if(text!=null&&text.length()!=0){
						SharePreHelper.saveSearchHistory(TopicSearchMainUi.this,keyName, text);
						editSearch.setText("");
						jumpToResult(2, text);
					}else{
						ToastUtil.showShort(getString(R.string.inputThemeName));
					}
					handled=true;
				}
				return handled;
			}
		});
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		
		case R.id.search_history_clear:
			SharePreHelper.clearSearchHistory(this,keyName);//清空历史
			mHistoryLayout.setVisibility(View.GONE);
			break;
		case R.id.title_right://点击取消
			onBackPressed();
			break;
		}
	}
	
	
	//下面各个分类的条目点击事件
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		jumpToResult(1, ""+mAdapter.datas.get(position-1).get("id"));
	}
	
	//跳转
	private void jumpToResult(int type,String name){
		Intent intent=new Intent(TopicSearchMainUi.this,TopicSearchResultActivity.class);
		intent.putExtra("searchType", type);//1表示按话题类型id查询
		intent.putExtra("secondName", name);
		startActivity(intent);
	}
	
	
	
}
