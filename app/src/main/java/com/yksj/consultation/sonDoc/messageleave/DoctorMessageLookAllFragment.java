package com.yksj.consultation.sonDoc.messageleave;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.yksj.consultation.adapter.DoctorMessageLookAllAdapter;
import com.yksj.consultation.sonDoc.R;
import com.yksj.healthtalk.entity.LeaveMessage;
import com.yksj.healthtalk.net.http.ApiService;
import com.yksj.healthtalk.net.http.ObjectHttpResponseHandler;
import com.yksj.healthtalk.net.socket.SmartFoxClient;

import org.handmark.pulltorefresh.library.PullToRefreshBase;
import org.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import org.handmark.pulltorefresh.library.PullToRefreshListView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
/**
 * 公告板的listview
 * @author Administrator
 *
 */
public class DoctorMessageLookAllFragment extends Fragment implements OnRefreshListener<ListView>{
	private PullToRefreshListView mPullToRefreshListView;
	private String id;
	private int loadNum=0;
	private ListView mListView;
	private DoctorMessageLookAllAdapter adapter;
	List<LeaveMessage> list=new ArrayList<LeaveMessage>();
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.doctor_message_all, null);
		mPullToRefreshListView = (PullToRefreshListView) view.findViewById(R.id.hall);
		mListView = mPullToRefreshListView.getRefreshableView();
		mPullToRefreshListView.setOnRefreshListener(this);
		return view;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		int type=getArguments().getInt("type", 0);
		adapter = new DoctorMessageLookAllAdapter(list,getActivity(),type);
		mListView.setAdapter(adapter);
		id = getArguments().getString("id");
		initData(id);
	}

	private void initData(String id) {
		ApiService.doHttpLookLeaveMessage(SmartFoxClient.getLoginUserId(),loadNum+"","",new ObjectHttpResponseHandler(getActivity()) {
			@Override
			public Object onParseResponse(String content) {
				return adapter.mList.addAll(onparseJson(content));
			}
			@Override
			public void onSuccess(Object response) {
				super.onSuccess(response);
				adapter.notifyDataSetInvalidated();
			}
		});
	}
	
	protected List<LeaveMessage> onparseJson(String content) {
		try {
			JSONObject object=new JSONObject(content);
			List<LeaveMessage> listEntitys=new ArrayList<LeaveMessage>();
			if(loadNum==0){
			LeaveMessage notice=new LeaveMessage();
			JSONObject notices=object.optJSONObject("notice");
			notice.setBIG_ICON_BACKGROUND(notices.optString("BIG_ICON_BACKGROUND"));
			notice.setCLIENT_ICON_BACKGROUND(notices.optString("CLIENT_ICON_BACKGROUND"));
			notice.setMESSAGE_CONTENT(notices.optString("MESSAGE_CONTENT"));
			notice.setCUSTOMER_ID(notices.optString("CUSTOMER_ID"));
			notice.setMESSAGE_TIME(notices.optString("MESSAGE_TIME"));
			notice.setMESSAGE_TYPE(notices.optString("MESSAGE_TYPE"));
			listEntitys.add(notice);
			}
				JSONArray messages = object.optJSONArray("message");
				for (int i = 0; i < messages.length(); i++) {
					JSONObject obj = (JSONObject) messages.get(i);
					LeaveMessage message=new LeaveMessage();
					message.setBIG_ICON_BACKGROUND(obj.optString("BIG_ICON_BACKGROUND"));
					message.setCLIENT_ICON_BACKGROUND(obj.optString("CLIENT_ICON_BACKGROUND"));
					message.setMESSAGE_CONTENT(obj.optString("MESSAGE_CONTENT"));
					message.setCUSTOMER_ID(obj.optString("CUSTOMER_ID"));
					message.setMESSAGE_TIME(obj.optString("MESSAGE_TIME"));
					message.setMESSAGE_TYPE(obj.optString("MESSAGE_TYPE"));
					listEntitys.add(message);
			}
				return listEntitys;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void onRefresh(PullToRefreshBase<ListView> refreshView) {
		loadNum++;
		initData(id);
	}
}
