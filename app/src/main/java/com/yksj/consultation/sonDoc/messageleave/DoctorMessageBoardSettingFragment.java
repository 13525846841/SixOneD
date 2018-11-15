package com.yksj.consultation.sonDoc.messageleave;

import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yksj.healthtalk.net.http.ApiService;
import com.yksj.healthtalk.net.http.AsyncHttpResponseHandler;
import com.yksj.healthtalk.net.http.JsonHttpResponseHandler;
import com.yksj.healthtalk.net.http.RequestParams;
import com.yksj.healthtalk.net.socket.SmartFoxClient;
import com.yksj.consultation.comm.CommonExplainActivity;
import com.yksj.consultation.comm.DoubleBtnFragmentDialog;
import com.library.base.base.BaseFragment;
import com.yksj.consultation.comm.SingleBtnFragmentDialog;
import com.yksj.consultation.comm.DoubleBtnFragmentDialog.OnDilaogClickListener;
import com.yksj.consultation.sonDoc.R;
import com.yksj.healthtalk.utils.HStringUtil;
import com.yksj.healthtalk.utils.ToastUtil;
import com.yksj.healthtalk.utils.ViewFinder;
/**
 * 公告板设置
 * @author Administrator
 *
 */
public class DoctorMessageBoardSettingFragment extends BaseFragment implements OnClickListener{
	private String messageId;
	String releaseCount;//发布公告的字数限制
	private View mView;
	private ViewFinder mFinder;
	private TextView mContentText;
	
	@Override
	public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
		mView = inflater.inflate(R.layout.doctor_message_board_setting_fragment, null);
		mFinder = new ViewFinder(mView);
		initView();
		return mView;
	}
	
	private void initView() {
		mFinder.onClick(this, new int[]{R.id.edit_bt,R.id.delete_bt,R.id.edit_message});
			initData();
			mContentText = mFinder.find(R.id.message_content);
	}
	
	private void initData() {
		RequestParams params=new RequestParams();
		params.put("OPTION", "QUERYPUBLISH");
		params.put("CUSTOMERID", SmartFoxClient.getLoginUserId());
		ApiService.doHttpLookDoctorMessage(params,new JsonHttpResponseHandler(mActivity){
			@Override
			public void onSuccess(int statusCode, JSONObject response) {
				super.onSuccess(statusCode, response);
				releaseCount =response.optString("POST");
				if(response.has("MESSAGE_CONTENT")){
					mContentText.setText(response.optString("MESSAGE_CONTENT"));
					mFinder.setText(R.id.time, response.optString("MESSAGE_TIME"));
					messageId= response.optString("MESSAGE_ID");
				}
				changeStatus(response.has("MESSAGE_CONTENT"));
			}
		});
	}
	
	/**
	 * 改变布局
	 * true 表示有内容
	 * false 表示没有
	 */
	private void changeStatus(boolean isEmpty){
		if(isEmpty){
			mFinder.find(R.id.empty_content).setVisibility(View.GONE);
			mFinder.find(R.id.edit_message).setVisibility(View.GONE);
			mFinder.find(R.id.topview).setVisibility(View.VISIBLE);
			mFinder.find(R.id.edit_view).setVisibility(View.VISIBLE);
		}else{
			mFinder.find(R.id.empty_content).setVisibility(View.VISIBLE);
			mFinder.find(R.id.edit_message).setVisibility(View.VISIBLE);
			mFinder.find(R.id.topview).setVisibility(View.GONE);
			mFinder.find(R.id.edit_view).setVisibility(View.GONE);
			mContentText.setText("");
		}
	}
    
	@Override
	public void onClick(View v) {
		Intent intent;
			switch (v.getId()) {
			case R.id.edit_message://填写公告
				intent =new Intent(mActivity,CommonExplainActivity.class);
				intent.putExtra(CommonExplainActivity.TITLE_NAME, "填写公告");  
				intent.putExtra("hintcontent", "请输入公告内容("+releaseCount+"字)");  
				intent.putExtra(CommonExplainActivity.TEXT_CONUT, Integer.valueOf(releaseCount)); 
				startActivityForResult(intent, 3000);
				break;
			case R.id.edit_bt://编辑
				intent =new Intent(mActivity,CommonExplainActivity.class);
				intent.putExtra(CommonExplainActivity.TITLE_NAME, "发布公告");  
				intent.putExtra(CommonExplainActivity.TEXT_CONTENT, mContentText.getText().toString());  
				intent.putExtra(CommonExplainActivity.TEXT_CONUT, Integer.valueOf(releaseCount)); 
				startActivityForResult(intent, 4000);
				break;
			case R.id.delete_bt://删除
				DoubleBtnFragmentDialog.showDefault(getChildFragmentManager(), "您确定要删除吗?", "取消", "确定", new OnDilaogClickListener() {
					@Override
					public void onDismiss(DialogFragment fragment) {
					}
					@Override
					public void onClick(DialogFragment fragment, View v) {
						deleteMessage(messageId);
					}
				});
				break;
			}
		}
	
	private void deleteMessage(String str){
		ApiService.doHttpdeleteDoctorMessage(str,new AsyncHttpResponseHandler(mActivity){
			@Override
			public void onSuccess(String content) {
				super.onSuccess(content);
				try {//{"info":"操作成功","code":"0"}
					JSONObject object=new JSONObject(content);
					ToastUtil.showBasicShortToast(mActivity, object.getString("info"));
					if("0".equals(object.getString("code"))){
						SingleBtnFragmentDialog.showDefault(getChildFragmentManager(),  "操作成功");
						changeStatus(false);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	} 


	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode!=mActivity.RESULT_OK||data==null)return ;
		switch (requestCode) {
		case 3000:
			if(data.hasExtra("content")){
			String content=data.getStringExtra("content");
				onHttpSubmit(content);
			}
			break;
		case 4000:
			if(data.hasExtra("content")){
			String content=data.getStringExtra("content");
				onHttpSubmit(content);
			}
			break;
		}
	}

	private void onHttpSubmit(final String content) {
		if(HStringUtil.isEmpty(content)){
			ToastUtil.showShort("通知不能为空。");	
			return;
		}
		RequestParams params=new RequestParams();
		params.put("OPTION", "SAVEPUBLISH");
		params.put("CUSTOMERID", SmartFoxClient.getLoginUserId());
		params.put("MESSAGECUSTOMERID", SmartFoxClient.getLoginUserId());
		params.put("CONTENT", content);
		params.put("TYPE", "10");
		ApiService.doHttpLookDoctorMessage(params, new JsonHttpResponseHandler(mActivity){
			@Override
			public void onSuccess(int statusCode, JSONObject response) {
				super.onSuccess(statusCode, response);
				//{"info":"公告覆盖编辑成功","code":"0"}
				if(0==response.optInt("code")){
					initData();
				}
				ToastUtil.showShort(response.optString("info"));
				
			}
		});
	}
}
