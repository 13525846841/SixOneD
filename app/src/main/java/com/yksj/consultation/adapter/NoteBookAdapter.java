package com.yksj.consultation.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.sonDoc.listener.MyOnClickListener;
import com.yksj.healthtalk.utils.TimeUtil;
import com.yksj.healthtalk.views.SelectPopupWindow;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
/**
 * Created by ${chen} on 2016/11/22.记事本适配器
 */
public class NoteBookAdapter  extends BaseAdapter {
    private Context context;
    private LayoutInflater mIflatter;
    public List<JSONObject> list = null;
    private static final int LAYOUTTYPECOUNT=2;
    //自定义的弹出框类
    SelectPopupWindow menuWindow;
    public boolean isFinish;
    private MyOnClickListener itemsOnClick;

    public NoteBookAdapter(Context context,MyOnClickListener itemsOnClick){
        this.context = context;
        this.mIflatter = LayoutInflater.from(context);
        list = new ArrayList<JSONObject>();
        this.itemsOnClick = itemsOnClick;
    }

    @Override
    public int getItemViewType(int position) {
        return list.get(position).optInt("type");
    }

    @Override
    public int getViewTypeCount() {
        return LAYOUTTYPECOUNT;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int type = getItemViewType(position);
        ViewHolder holder ;
        if (convertView==null){
            holder=new ViewHolder();
            switch (type){
                case 0:
                    convertView = mIflatter.inflate(R.layout.item_note_title,null);
                    holder.tv_tmr_plan = (TextView) convertView.findViewById(R.id.tv_tomorrow_title);
                    break;
                case 1:
                    convertView = mIflatter.inflate(R.layout.item_todplan,null);
                    holder.select_star = (ImageView) convertView.findViewById(R.id.select_star);
                    holder.time = (TextView) convertView.findViewById(R.id.tv_plan_time);
                    holder.content = (TextView) convertView.findViewById(R.id.tv_note_content);
                    break;
            }
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder)convertView.getTag();
        }
        final View view = convertView;
        final int pos = position;

        final int id = R.id.select_star;


        switch (type){
            case 0:
                holder.tv_tmr_plan.setText(list.get(position).optString("title"));
                break;
            case 1:
                holder.content.setText(list.get(position).optString("NOTEPAD_CONTENT"));
                holder.time.setText(TimeUtil.format(list.get(position).optString("NOTEPAD_TIME")));

                if ("1".equals(list.get(position).optString("REMIND_FLAG"))){
                    holder.select_star.setSelected(true);
                    isFinish=true;
                }else {
                    holder.select_star.setSelected(false);
                    isFinish=false;
                    holder.select_star.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            itemsOnClick.onStarClick(view,pos,id);
                        }
                    });
//                    holder.select_star.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                        menuWindow = new SelectPopupWindow((Activity) context, new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                itemsOnClick.onStarClick(v,pos);
//                                menuWindow.dismiss();
//                            }
//                        });
//                        // 显示窗口
//                        menuWindow.showAtLocation(((Activity) context).findViewById(R.id.ll_main), Gravity.BOTTOM| Gravity.CENTER_HORIZONTAL, 0, 0); //设置layout在PopupWindow中显示的位置
//                        }
//                    });
                }

                break;
            default:
                break;
        }
        return convertView;
    }

    public void changeStar(int position){
        if (isFinish==true){
            isFinish= false;

        }else{
            isFinish=true;
        }
        notifyDataSetChanged();
    }

    public String reword_id(int position){
        return list.get(position).optString("RECORD_ID");
    }

    public static class ViewHolder{
        public TextView tv_tmr_plan;//明日计划标题
        public ImageView select_star;//今日计划星星
        public TextView content;//今日计划内容
        public TextView time;//今日时间

    }
    public void onBoundData(List<JSONObject> datas) {
        if (list != null) {
            list.clear();
            list.addAll(datas);
            notifyDataSetChanged();
        }
    }

    public void remove(int positon) {
        list.remove(positon);
        notifyDataSetChanged();
    }
}
