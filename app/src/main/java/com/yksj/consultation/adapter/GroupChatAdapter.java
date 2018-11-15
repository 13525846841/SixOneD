package com.yksj.consultation.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.yksj.consultation.constant.Constant;
import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.app.AppContext;
import com.yksj.consultation.im.ChatActivity;
import com.yksj.consultation.im.GroupChatActivity;
import com.yksj.consultation.sonDoc.salon.SalonSelectPaymentOptionActivity;
import com.yksj.healthtalk.entity.GroupInfoEntity;
import com.yksj.healthtalk.utils.SalonHttpUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ${chen} on 2016/11/17.
 * 群组适配器
 */
public class GroupChatAdapter extends BaseAdapter implements SalonSelectPaymentOptionActivity.OnBuyTicketHandlerListener {

    private Context context;
    private LayoutInflater mInflater;
    List<JSONObject> mData = null;
    private FragmentActivity maActivity;

    public GroupChatAdapter(Context context) {
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        mData = new ArrayList<>();
        maActivity = (FragmentActivity) context;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.layout_group_chat, null);
            holder = new ViewHolder();
            holder.name = (TextView) convertView.findViewById(R.id.doc_name);
            holder.headView = (ImageView) convertView.findViewById(R.id.image);
            holder.pro = (TextView) convertView.findViewById(R.id.gc_number);
            holder.pro.setVisibility(View.VISIBLE);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        final String mName = mData.get(position).optString("RECORD_NAME");
        final String id = mData.get(position).optString("GROUP_ID");
        int num = mData.get(position).optInt("massage_number");
        String headview = AppContext.getApiRepository().URL_QUERYHEADIMAGE + mData.get(position).optString("BIG_ICON_BACKGROUND");
//        String headview = "http://220.194.46.204:80/DuoMeiHealth/HeadDownLoadServlet.do?path=" + mData.get(position).optString("BIG_ICON_BACKGROUND");
        holder.name.setText(mName);
//        holder.name.setText(mName + " " + id + " " + position);
        if (num > 99) {
            holder.pro.setVisibility(View.VISIBLE);
            holder.pro.setText("99+");
        } else if (num == 0) {
            holder.pro.setVisibility(View.GONE);
        } else {
            holder.pro.setVisibility(View.VISIBLE);
//            holder.pro.setText(num + "");
            holder.pro.setText(num + "");
        }

        Picasso.with(context).load(headview).error(R.drawable.default_head_mankind).placeholder(R.drawable.default_head_mankind).into(holder.headView);
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    mData.get(position).put("massage_number",0);
                    notifyDataSetChanged();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                GroupInfoEntity entity = new GroupInfoEntity();
                entity.setId(id);
                entity.setIsBL("2");
                entity.setName(mName);
                Intent intent1 = new Intent();
                intent1.putExtra(Constant.Chat.KEY_PARAME, entity);
                intent1.setClass(maActivity, ChatActivity.class);
                maActivity.startActivityForResult(intent1, GroupChatActivity.GROUP_CHAT);
//                doChatGroup(id, "0", mName);
            }
        });
        return convertView;
    }

    @Override
    public void onTicketHandler(String state, GroupInfoEntity entity) {
//        if ("0".equals(state)) {
//        } else if ("-1".equals(state)) {
//            ToastUtil.showShort("服务器出错");
//        } else {
//            Intent intent1 = new Intent();
//            intent1.putExtra(ChatActivity.KEY_PARAME, entity);
//            intent1.setClass(context, ChatActivity.class);
//            context.startActivity(intent1);
//        }

    }

    /**
     * 存放控件
     */
    public final class ViewHolder {
        public TextView name;
        public ImageView headView;
        public TextView pro;
    }

    public void onBoundData(List<JSONObject> data) {
        this.mData.clear();
        this.mData.addAll(data);
        notifyDataSetChanged();
    }

    public void removeAll() {
        this.mData.clear();
        notifyDataSetChanged();
    }

    /**
     * chat
     *
     * @param id   groupid
     * @param isBl ;//是否是病历：（1是病历，0是会诊）
     * @param name groupname
     */
    public void doChatGroup(String id, String isBl, String name) {
        GroupInfoEntity entity = new GroupInfoEntity();
        entity.setId(id);
        entity.setIsBL(isBl);
        entity.setName(name);
        SalonHttpUtil.onItemClick(context, GroupChatAdapter.this, maActivity.getSupportFragmentManager(), entity, false);
    }


}
