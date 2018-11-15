package com.yksj.consultation.adapter;

import android.content.Context;
import android.graphics.drawable.LevelListDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.yksj.consultation.sonDoc.R;
import com.library.base.widget.CircleImageView;
import com.yksj.healthtalk.entity.PersonEntity;

import org.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

/**
 * 我的患者带拼音排序的列表适配器
 * Created by lmk on 2015/7/8.
 */
public class PatientSortAdapter extends BaseAdapter implements SectionIndexer {

    private Context context;
    private ImageLoader mImageloader;
    public ArrayList<PersonEntity> datas=new ArrayList<PersonEntity>();

    public PatientSortAdapter(Context context) {
        this.context = context;
        mImageloader=ImageLoader.getInstance();
    }

    public PatientSortAdapter(Context context, ArrayList<PersonEntity> datas) {
        this.context = context;
        this.datas = datas;
        mImageloader=ImageLoader.getInstance();
    }

    /**
     * 当ListView数据发生变化时,调用此方法来更新ListView
     *
     * @param list
     */
    public void updateListView(ArrayList<PersonEntity> list) {
        datas.clear();
        datas.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public Object getItem(int position) {
        return datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        PersonEntity entity=datas.get(position);
        ViewHolder holder=null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.find_friend_list_item, null);
            holder=new ViewHolder(convertView);
            convertView.setTag(holder);

        } else {
            holder= (ViewHolder) convertView.getTag();
        }
        holder.findlistitemname.setText(entity.getCustomerNickname());
        holder.findlistitemmood.setText(entity.getCustomerAccounts());
        LevelListDrawable listDrawable = (LevelListDrawable)holder.findlistitemsex.getDrawable();
        listDrawable.setLevel(Integer.valueOf(entity.getCustomerSex()));

        mImageloader.displayImage(""+entity.getCustomerSex(),entity.getBigIconBackground(),holder.findlistitemheadicon);

        return convertView;
    }


    /**
     * 根据ListView的当前位置获取分类的首字母的Char ascii值
     */
    public int getSectionForPosition(int position) {
        return datas.get(position).getSortLetters().charAt(0);
    }

    /**
     * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
     */
    public int getPositionForSection(int section) {
        for (int i = 0; i < getCount(); i++) {
            String sortStr = datas.get(i).getSortLetters();
            char firstChar = sortStr.toUpperCase().charAt(0);
            if (firstChar == section) {
                return i;
            }
        }

        return -1;
    }

    /**
     * 提取英文的首字母，非英文字母用#代替。
     *
     * @param str
     * @return
     */
    private String getAlpha(String str) {
        String  sortStr = str.trim().substring(0, 1).toUpperCase();
        // 正则表达式，判断首字母是否是英文字母
        if (sortStr.matches("[A-Z]")) {
            return sortStr;
        } else {
            return "#";
        }
    }

    @Override
    public Object[] getSections() {
        return null;
    }

    public class ViewHolder {
        public final CircleImageView findlistitemheadicon;
        public final TextView findlistitemname;
        public final ImageView findlistitemsex;
        public final TextView findlistitemmood;
        public final ImageView findlistitemfollow;
        public final View root;

        public ViewHolder(View root) {
            findlistitemheadicon = (CircleImageView) root.findViewById(R.id.find_list_item_headicon);
            findlistitemname = (TextView) root.findViewById(R.id.find_list_item_name);
            findlistitemsex = (ImageView) root.findViewById(R.id.find_list_item_sex);
            findlistitemmood = (TextView) root.findViewById(R.id.find_list_item_mood);
            findlistitemfollow = (ImageView) root.findViewById(R.id.find_list_item_follow);
            findlistitemfollow.setVisibility(View.INVISIBLE);
            this.root = root;
        }
    }
}
