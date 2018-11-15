package com.yksj.consultation.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.app.AppContext;
import com.yksj.consultation.constant.Constant;
import com.yksj.consultation.bean.ExpertStatus;
import com.yksj.healthtalk.utils.HStringUtil;

import org.json.JSONObject;
import org.universalimageloader.core.DefaultConfigurationFactory;
import org.universalimageloader.core.DisplayImageOptions;
import org.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ${chen} on 2017/4/11.
 * 会诊详情中会诊成员的适配器
 */
public class OrderDetailAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater mInflater;
    public int lastPosition = 0;
    public List<JSONObject> mData = null;
    private boolean isExpert = false;
    private ImageLoader mImageLoader;
    private DisplayImageOptions mOptionp;//异步加载图片的操作
    private DisplayImageOptions mOptiond;//异步加载图片的操作

    public OrderDetailAdapter(Context context) {
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        mData = new ArrayList<>();
        mImageLoader = ImageLoader.getInstance();//头像异步加载初始化
        mOptionp = DefaultConfigurationFactory.patientPicDisplayImageOptions(context);
        mOptiond = DefaultConfigurationFactory.doctorPicDisplayImageOptions(context);
    }


    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        if (isExpert) {
            if (position == getCount() - 1) {
                return 0;
            } else {
                return 1;
            }
        } else {
            return 1;
        }

    }

    @Override
    public int getCount() {
        if (isExpert) {
            return mData.size() + 1;
        } else {
            return mData.size();
        }
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int type = getItemViewType(position);
        ViewHolder holder = new ViewHolder();
        if (convertView == null) {
            switch (type) {
                case 0://添加按钮
                    convertView = mInflater.inflate(R.layout.item_order_detail_add, null);
                    holder.addHeadView = (ImageView) convertView.findViewById(R.id.addHeadview);
                    convertView.setTag(holder);
                    break;
                case 1:
                    convertView = mInflater.inflate(R.layout.item_orderdetail, null);
                    // holder = new ViewHolder();
                    holder.name = (TextView) convertView.findViewById(R.id.name);
                    holder.headView = (ImageView) convertView.findViewById(R.id.chat_head);
                    holder.imageState = (ImageView) convertView.findViewById(R.id.image_state);
                    convertView.setTag(holder);
                    break;
                default:
                    break;
            }
        } else {
            holder = (ViewHolder) convertView.getTag();

        }
        if (holder.imageState != null && type == 1) {
            holder.imageState.setVisibility(View.GONE);
            if (!HStringUtil.isEmpty(mData.get(position).optString("name"))) {
                holder.name.setText(mData.get(position).optString("name"));
            } else {
                holder.name.setText("");
            }
            String pic = AppContext.getApiRepository().URL_QUERYHEADIMAGE + mData.get(position).optString("icon");
//            String pic = AppContext.getmRepository().URL_QUERYHEADIMAGE_NEW + mData.get(position).optString("icon");
            if (position == 0) {
                mImageLoader.displayImage(pic, holder.headView, mOptionp);
//                Picasso.with(context).load(pic).connectError(R.drawable.default_head_patient).placeholder(R.drawable.default_head_patient).into(holder.headView);
            } else {
                mImageLoader.displayImage(pic, holder.headView, mOptiond);
//                Picasso.with(context).load(pic).connectError(R.drawable.default_head_doctor).placeholder(R.drawable.default_head_doctor).into(holder.headView);
            }

            if (ExpertStatus.INVITING_STATE.equals(mData.get(position).optString(Constant.EXPERT_STATE))) {
                holder.imageState.setVisibility(View.VISIBLE);
            }
        }
        return convertView;
    }

    /**
     * 存放控件
     */
    public final class ViewHolder {
        public TextView name;
        public ImageView headView;
        public ImageView addHeadView;
        public ImageView imageState;
    }

    public void onBoundData(List<JSONObject> data) {
        this.mData.clear();
        this.mData.addAll(data);
        notifyDataSetChanged();
    }

    public void onBoundData(List<JSONObject> data, boolean isExpert) {
        this.isExpert = isExpert;
        this.mData.clear();
        this.mData.addAll(data);
        notifyDataSetChanged();
    }

    public void removeAll() {
        this.mData.clear();
        notifyDataSetChanged();
    }
}
