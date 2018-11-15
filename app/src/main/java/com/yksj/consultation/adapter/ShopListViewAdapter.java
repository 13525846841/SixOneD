package com.yksj.consultation.adapter;

import java.util.ArrayList;
import java.util.List;

import org.universalimageloader.core.ImageLoader;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.yksj.healthtalk.entity.ShopListItemEntity;
import com.yksj.consultation.sonDoc.R;

public class ShopListViewAdapter extends BaseAdapter {

	public List<ShopListItemEntity> list =new ArrayList<ShopListItemEntity>();
	private final LayoutInflater mLayoutInflater;
	private ImageLoader imageLoader;
	private Drawable loadingDrawable,defaultDrawable;
	public ShopListViewAdapter(Context context,List<ShopListItemEntity> list ) {
		if(list!=null)
		this.list=list;
		mLayoutInflater = LayoutInflater.from(context);
		imageLoader = ImageLoader.getInstance();
		loadingDrawable = context.getResources().getDrawable(R.drawable.store_pic);
		defaultDrawable = context.getResources().getDrawable(R.drawable.store_pic);
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int arg0) {
		return list.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int arg0, View conview, ViewGroup arg2) {
		ShopListItemEntity entity = list.get(arg0);
		ViewHolder holder;
		if(conview==null){
			holder=new ViewHolder();
			conview=mLayoutInflater.inflate(R.layout.shop_list_item_adapter, null);
			holder.name=(TextView)conview.findViewById(R.id.name);
			holder.guige=(TextView)conview.findViewById(R.id.guige);
			holder.shichang_jiage=(TextView)conview.findViewById(R.id.shichang_jiage);
			holder.putong_jiage=(TextView)conview.findViewById(R.id.putong_jiage);
			holder.address=(TextView)conview.findViewById(R.id.address);
			holder.view=(ImageView)conview.findViewById(R.id.image_pic);
			holder.count = (TextView)conview.findViewById(R.id.count);
			conview.setTag(holder);
		}else{
			holder=(ViewHolder) conview.getTag();
		}
		holder.view.setImageDrawable(loadingDrawable);
		holder.name.setText(entity.getGOODS_NAME());
		holder.guige.setText("商品规格:"+entity.getSPECIFICATIONS());
		
		String price ;
		if (TextUtils.isEmpty(entity.getCURRENT_PRICE())) {
			price = entity.getSHOP_PRICE();
		}else {
			price = entity.getCURRENT_PRICE();
		}
		
		if (entity.getBUY_COUNT() != null && entity.getBUY_COUNT() != 0) {
			holder.count.setText("X"+entity.getBUY_COUNT());
		}else {
			holder.count.setText("");
		}
		
		if (!TextUtils.isEmpty(price)) {
			
			holder.shichang_jiage.setText("市场价格: ￥"+ price+isDou(price));
		}else {
			holder.shichang_jiage.setText("市场价格:");
		}
		
		if (!TextUtils.isEmpty(entity.getVIP_PRICE())) {
			holder.putong_jiage.setText(" ￥"+ entity.getVIP_PRICE()+isDou(entity.getVIP_PRICE()));
		}else {
			holder.putong_jiage.setText("");
		}
		
		
		holder.address.setText(entity.getFACTORY());
		if(TextUtils.isEmpty(entity.getPICTURE_ADDR())){
			holder.view.setImageDrawable(defaultDrawable);
		}else{
			imageLoader.displayImage(entity.getPICTURE_ADDR(), holder.view);
		}
		return conview;
	}

	public void changeData(List<ShopListItemEntity> list) {
		this.list.addAll(list);
		notifyDataSetChanged();
	}
	
	static class ViewHolder{
		ImageView view;
		TextView name;
		TextView guige;
		TextView shichang_jiage;
		TextView putong_jiage;
		TextView address;
		TextView count;
	}

	public void clear() {
		this.list.clear();
		notifyDataSetChanged();
		
	}
	
	public String  isDou(String str){
		if(str.contains(".")){
			String[] split = str.split("[.]");
			if(split[1].length()==1){
				return "0";
			}else {
				return "";
			}
			
		}else{
			return ".00";
		}
	}
	
}
