package com.yksj.consultation.adapter;

import java.util.List;

import org.universalimageloader.core.ImageLoader;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.yksj.healthtalk.entity.CustomerInfoEntity;
import com.yksj.consultation.sonDoc.R;
import com.yksj.healthtalk.utils.TimeUtil;
/**
 * 会诊服务界面的适配器
 * @author lmk
 *
 */
public class MyOrdersListAdapter extends SimpleBaseAdapter<CustomerInfoEntity> {

	private ImageLoader mInstance;
	private Context context;
	
	public MyOrdersListAdapter(Context context, List<CustomerInfoEntity> datas) {
		super(context, datas);
		this.context=context;
		mInstance=ImageLoader.getInstance();
	}

	@Override
	public int getItemResource() {
		return R.layout.orders_list_item;
	}

	@Override
	public View getItemView(int position, View convertView,
			com.yksj.consultation.adapter.SimpleBaseAdapter.ViewHolder holder) {
		final CustomerInfoEntity cus=datas.get(position);
		CustomerInfoEntity entity=(CustomerInfoEntity) getItem(position);
		TextView tvName= (TextView) holder.getView(R.id.orders_list_item_nickname);//名称
		TextView tvDuomeiId=(TextView) holder.getView(R.id.orders_list_item_duomeiid);//账号
		TextView tvSeeDetail=(TextView) holder.getView(R.id.orders_list_item_seedetail);//查看详细
		TextView tvServeTime=(TextView) holder.getView(R.id.orders_list_item_serveTime);//服务开始时间
		TextView tvServeType=(TextView) holder.getView(R.id.orders_list_item_serveCategory);//服务类型
		TextView tvServeState=(TextView) holder.getView(R.id.orders_list_item_state);//服务状态
		ImageView icon=(ImageView) holder.getView(R.id.orders_list_item_headicon);//用户头像
		
		tvName.setText(cus.getNickName());
		tvDuomeiId.setText("("+cus.getUsername()+")");
		int code=Integer.parseInt(cus.getServiceStatusCode());
		long startTime=TimeUtil.getChatMessageData(cus.getServiceTime());
		if(code==50&&System.currentTimeMillis()<startTime){//待服务
			tvServeState.setText(R.string.orders_str_notserve);
		}else if((code==50||code==60||code==70||code==150||code==155||code==160||code==170||code==185)
				&&System.currentTimeMillis()>=startTime){//服务中
			tvServeState.setText(R.string.orders_str_serving);
		}else if(code==242||code==90||code==100||code==110||code==120||code==130||code==140||code==165
				||code==198||code==199){//已服务
			tvServeState.setText(R.string.orders_str_isserved);
		}
		tvServeTime.setText(cus.getServiceTime());
		tvServeType.setText(cus.getServiceTypeName());
		tvSeeDetail.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
			}
		});
		
		mInstance.displayImage(cus.getSex(), cus.getNormalHeadIcon(), icon);
		
		
		return convertView;
	}

}
