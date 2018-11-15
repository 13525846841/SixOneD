package com.yksj.consultation.adapter;



import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.yksj.consultation.sonDoc.R;

/**
 * 
 * @author Administrator
 *
 */
public class BaseGridAdapter extends BaseAdapter{

	private int[] images;//图片
	private String[] strs;//文字
	
	 Context context;
     public BaseGridAdapter(Context context) {
         this.context = context;
     }
     
     public void setData(int[] images,String[] strs){
    	 this.images=images;
    	 this.strs=strs;
     }
      
     @Override
     public int getCount() {
         return images.length;
     }

     @Override
     public Object getItem(int position) {
         return strs[position];
     }

     @Override
     public long getItemId(int position) {
         return position;
     }

     @Override
     public View getView(int position, View convertView, ViewGroup parent) {
         Holder holder;
         if (convertView == null) {
             holder = new Holder();
             convertView = LayoutInflater.from(context).inflate(R.layout.share_grid_platform_item, null);
             holder.img = (ImageView) convertView.findViewById(R.id.grid_item_image);
             holder.desc = (TextView) convertView.findViewById(R.id.grid_item_text);
             convertView.setTag(holder);
         } else {
             holder = (Holder) convertView.getTag();
         }

         holder.img.setImageResource(images[position]);
         holder.desc.setText(strs[position]);
         return convertView;
     }

     class Holder {
         public ImageView img;
         public TextView desc;
     }

}
