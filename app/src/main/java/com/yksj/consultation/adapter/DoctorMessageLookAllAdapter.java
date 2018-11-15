package com.yksj.consultation.adapter;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.universalimageloader.core.ImageLoader;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.yksj.healthtalk.entity.LeaveMessage;
import com.yksj.consultation.sonDoc.R;
import com.yksj.healthtalk.utils.PersonInfoUtil;

public class DoctorMessageLookAllAdapter extends BaseAdapter {

	public final List<LeaveMessage> mList;
	private final LayoutInflater from;
	private int type=0;
	private ImageLoader imageLoader;
	private OnclickReplyListener onclickReplyListener;
	private Context context;
	public DoctorMessageLookAllAdapter(List<LeaveMessage> list, Activity context, int type){
		this.mList=list;
		from = LayoutInflater.from(context);
		this.type=type;
		imageLoader = ImageLoader.getInstance();
		this.context=context;
	}
	
	public List<LeaveMessage> onparseJson(String content,int loadNum) {
		try {
			JSONObject object=new JSONObject(content);
			List<LeaveMessage> listEntitys=new ArrayList<LeaveMessage>();
			JSONArray messages = object.optJSONArray("notice");
			for (int i = 0; i < messages.length(); i++) {
				JSONObject obj = (JSONObject) messages.get(i);
				LeaveMessage message=new LeaveMessage();
				message.setBIG_ICON_BACKGROUND(obj.optString("BIG_ICON_BACKGROUND"));
				message.setCLIENT_ICON_BACKGROUND(obj.optString("CLIENT_ICON_BACKGROUND"));
				message.setMESSAGE_CONTENT(obj.optString("MESSAGE_CONTENT"));
				message.setCUSTOMER_ID(obj.optString("CUSTOMER_ID"));
				message.setMESSAGE_TYPE(obj.optString("MESSAGE_TYPE"));
				message.setMESSAGE_ID(obj.optString("MESSAGE_ID"));
				message.setMESSAGE_TIME(obj.optString("MESSAGE_TIME"));
				message.setREPLY_NICKNAME(obj.optString("REPLY_NICKNAME",""));
				message.setCUSTOMER_NICKNAME(obj.optString("CUSTOMER_NICKNAME",""));
				message.setCUSTOMER_SEX(obj.optString("CUSTOMER_SEX"));
				message.setDoctor(obj.optString("ROLE_ID","0"));
				listEntitys.add(message);
			}
			return listEntitys;
		} catch (JSONException e) {
			return null;
		}
	}
	
	public List<LeaveMessage> onparseJson(String content) {
		try {
			List<LeaveMessage> listEntitys=new ArrayList<LeaveMessage>();
				JSONArray messages =new  JSONArray(content); 
				for (int i = 0; i < messages.length(); i++) {
					JSONObject obj = (JSONObject) messages.get(i);
					LeaveMessage message=new LeaveMessage();
					message.setBIG_ICON_BACKGROUND(obj.optString("BIG_ICON_BACKGROUND"));
					message.setCLIENT_ICON_BACKGROUND(obj.optString("CLIENT_ICON_BACKGROUND"));
					message.setMESSAGE_CONTENT(obj.optString("MESSAGE_CONTENT"));
					message.setCUSTOMER_ID(obj.optString("CUSTOMER_ID"));
					message.setMESSAGE_TIME(obj.optString("MESSAGE_TIME"));
					message.setMESSAGE_TYPE(obj.optString("MESSAGE_TYPE"));
					message.setCUSTOMER_SEX(obj.optString("CUSTOMER_SEX"));
					message.setMESSAGE_ID(obj.optString("MESSAGE_ID"));
					message.setREPLY_NICKNAME(obj.optString("REPLY_NICKNAME",""));
					message.setCUSTOMER_NICKNAME(obj.optString("CUSTOMER_NICKNAME",""));
					message.setDoctor(obj.optString("ISDOCTOR","0"));
					listEntitys.add(message);
			}
				return listEntitys;
		} catch (JSONException e) {
			return null;
		}
	}
	
	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public Object getItem(int position) {
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public void onBountData(List<LeaveMessage> list){
		mList.addAll(list);
		notifyDataSetInvalidated();
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		final LeaveMessage leaveMessageEntity = mList.get(position);
		if(convertView==null||convertView.getTag()==null){
			holder=new ViewHolder();
			convertView=from.inflate(R.layout.doctor_message_item,null);
			holder.bj=(ImageView) convertView.findViewById(R.id.image);
			holder.titleText=(TextView) convertView.findViewById(R.id.biaoti);
			holder.titleTIme=(TextView) convertView.findViewById(R.id.time);
			holder.Content=(TextView) convertView.findViewById(R.id.content);
			holder.box= (CheckBox) convertView.findViewById(R.id.check);
			holder.report=(ImageView) convertView.findViewById(R.id.report);
			convertView.setTag(holder);
		}else{
			holder=(ViewHolder) convertView.getTag();
		}
		
		if(type==0){//客户进来 隐藏回复和修改按钮
			holder.box.setVisibility(View.INVISIBLE);
			holder.report.setVisibility(View.INVISIBLE);
		}else if(type==1){//医生进来 显示回复按钮
			holder.box.setVisibility(View.GONE);
//			holder.report.setVisibility(View.VISIBLE);
		}else{//显示修改删除按钮
//			holder.box.setVisibility(View.VISIBLE);
			holder.report.setVisibility(View.INVISIBLE);
		}
		
		
		imageLoader.displayImage(leaveMessageEntity.getCUSTOMER_SEX(),leaveMessageEntity.getCLIENT_ICON_BACKGROUND(), holder.bj);
		holder.bj.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				PersonInfoUtil.choiceActivity(leaveMessageEntity.getCUSTOMER_ID(), context, leaveMessageEntity.isDoctor());
			}
		});
		
		
		holder.titleTIme.setText(leaveMessageEntity.getMESSAGE_TIME());
		holder.Content.setText(leaveMessageEntity.getMESSAGE_CONTENT());
		if("20".equals(leaveMessageEntity.getMESSAGE_TYPE())){
			holder.titleText.setText(leaveMessageEntity.getCUSTOMER_NICKNAME()+"说:");
			convertView.setPadding(0,0,0,0);
		}else if("10".equals(leaveMessageEntity.getMESSAGE_TYPE())){
			holder.titleText.setText("医生公告:");
			holder.report.setVisibility(View.INVISIBLE);
			convertView.setPadding(0,0,0,0);
		}else if("30".equals(leaveMessageEntity.getMESSAGE_TYPE())){
			holder.titleText.setText("医生回复"+leaveMessageEntity.getREPLY_NICKNAME()+":");
			holder.report.setVisibility(View.INVISIBLE);
			convertView.setPadding(50,0,0,0);
		}
		
		holder.box.setOnCheckedChangeListener(null);
		
		if(leaveMessageEntity.isCheck()==1){
			holder.box.setChecked(false);
		}else{
			holder.box.setChecked(true);
		}
		
		holder.box.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					leaveMessageEntity.setCheck(2);
				}else{
					leaveMessageEntity.setCheck(1);
				}
			}
		});
		
		holder.report.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(onclickReplyListener!=null)
					onclickReplyListener.Onclick(leaveMessageEntity);
			}
		});
		
		return convertView;
	}
	
	public interface OnclickReplyListener {
		void Onclick(LeaveMessage entity );
	}
	
	public void setOnclickReplyListener(OnclickReplyListener onclick){
		this.onclickReplyListener=onclick;
	}
	
	
	class ViewHolder{
		TextView titleText;
		TextView titleTIme;
		TextView Content;
		ImageView bj;
		CheckBox box;
		ImageView report;
	}
}
