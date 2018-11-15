package com.yksj.consultation.sonDoc.buyandsell;

import java.util.ArrayList;
import java.util.List;

import org.handmark.pulltorefresh.library.PullToRefreshBase;
import org.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import org.handmark.pulltorefresh.library.PullToRefreshListView;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.yksj.healthtalk.entity.CustomerInfoEntity;
import com.yksj.healthtalk.net.http.ApiService;
import com.yksj.healthtalk.net.http.ObjectHttpResponseHandler;
import com.yksj.healthtalk.net.socket.SmartFoxClient;
import com.yksj.consultation.adapter.MyOrdersListAdapter;
import com.yksj.consultation.sonDoc.R;
import com.yksj.healthtalk.utils.FriendHttpUtil;

/**
 * 我的订单的列表fragment
 * @author lmk
 *
 */
public class OrdersFragment extends Fragment implements OnRefreshListener2<ListView>{
	
	private CustomerInfoEntity entity;
	private int serveType;
	private PullToRefreshListView mPullListView;
	private ListView mListView;
	private MyOrdersListAdapter adapter;
	private ArrayList<CustomerInfoEntity> datas=new ArrayList<CustomerInfoEntity>();
	private int pageNum=20;//每页几条
	private int pageSize=1;// 第几页
	
	
	public static OrdersFragment newInstance(int type){
		OrdersFragment fragment=new OrdersFragment();
		Bundle bundle=new Bundle();
		bundle.putInt("type", type);
		fragment.setArguments(bundle);
		return fragment;
	}
	
	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Bundle bundle=getArguments();
		if(bundle!=null){
			serveType=bundle.getInt("type");
			loadFriendList(1);
		}
	}
	
	/**
	 * 加载列表数据
	 */
	private void loadFriendList(int page) {
//		if(serveType==0){
//			Toast.makeText(getActivity(), "--------"+serveType, Toast.LENGTH_LONG).show();
//		}else{
//			Toast.makeText(getActivity(), "---------------------------"+serveType, Toast.LENGTH_SHORT).show();
//		}
		pageSize=page;
		if(entity==null){
			entity=new CustomerInfoEntity();
		}
		entity.setId(SmartFoxClient.getLoginUserId());//
		ApiService.doHttpDoctorServiceQueryData(""+serveType, SmartFoxClient.getLoginUserId(), pageNum,
				pageSize, new ObjectHttpResponseHandler(getActivity()){
			@Override
			public void onSuccess(int statusCode, Object response) {
				super.onSuccess(statusCode, response);
				if(response!=null&&response instanceof List){
					if(pageSize==1){
						adapter.removeAll();
					}
					adapter.addAll((List<CustomerInfoEntity>) response);
				}
				pageSize++;
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

			@Override
			public Object onParseResponse(String content) {
				if(content!=null &&content.contains("error_message"))
					return content;
				return FriendHttpUtil.jsonAnalysisFriendEntity(content, false);
			}
		});
		
	}
	

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view=inflater.inflate(R.layout.orders_list_fragment, null);
		mPullListView=(PullToRefreshListView) view.findViewById(R.id.myorders_pulllist);
		mPullListView.setOnRefreshListener(this);
		mListView=mPullListView.getRefreshableView();
		adapter=new MyOrdersListAdapter(getActivity(),datas);
		mListView.setAdapter(adapter);
		
		return view;
	}

	
	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		loadFriendList(1);
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		loadFriendList(pageSize);
	}
}
