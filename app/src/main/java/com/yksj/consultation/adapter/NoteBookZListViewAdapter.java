package com.yksj.consultation.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.sonDoc.listener.MyOnClickListener;
import com.yksj.healthtalk.utils.TimeUtil;
import com.yksj.healthtalk.utils.ViewFinder;
import com.yksj.healthtalk.views.zlistview.enums.DragEdge;
import com.yksj.healthtalk.views.zlistview.enums.ShowMode;
import com.yksj.healthtalk.views.zlistview.widget.ZSwipeItem;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ${chen} on 2017/1/19.
 */
public class NoteBookZListViewAdapter extends BaseSwipeAdapter {

    public Context context;
    private static final int LAYOUTTYPECOUNT = 2;
    private LayoutInflater mIflatter;
    public List<JSONObject> list = new ArrayList<JSONObject>();
    public boolean isFinish;
    private MyOnClickListener itemsOnClick;

    public NoteBookZListViewAdapter(Activity activity, MyOnClickListener itemsOnClick) {
        context = activity;
        this.itemsOnClick = itemsOnClick;
        this.mIflatter = LayoutInflater.from(context);
    }

    onClickdeleteMsgeListener clickdeleteMsgeListener;

    public void setonClickdeleteMsgeListener(onClickdeleteMsgeListener attentionListener) {
        this.clickdeleteMsgeListener = attentionListener;
    }

    public interface onClickdeleteMsgeListener {
        void onClickDeleteMsg(int positon);
    }

    public String reword_id(int position) {
        return list.get(position).optString("RECORD_ID");
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
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe_item1;
    }

    @Override
    public View generateView(int position, ViewGroup parent) {
        int type = getItemViewType(position);
        if (type == 0) {
            return mIflatter.inflate(R.layout.item_note_title, parent, false);
        } else if (type == 1) {
            return mIflatter.inflate(R.layout.item_note_swipe, parent, false);
        }
        return mIflatter.inflate(R.layout.loading_fail_data_null, parent, false);
    }

    @Override
    public void fillValues(final int position, View convertView) {
        int type = getItemViewType(position);
        if (type == 0) {
            ViewFinder viewFinder = new ViewFinder(convertView);
            TextView mHead = viewFinder.textView(R.id.tv_tomorrow_title);
            mHead.setText(list.get(position).optString("title"));

        } else if (type == 1) {

            final View view = convertView;
            final int pos = position;
            final int id = R.id.select_star;

            final ZSwipeItem swipeItem = (ZSwipeItem) convertView.findViewById(R.id.swipe_item1);
            LinearLayout ll = (LinearLayout) convertView.findViewById(R.id.ll_delect1);
            swipeItem.setShowMode(ShowMode.PullOut);
            swipeItem.setDragEdge(DragEdge.Right);
            ll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (clickdeleteMsgeListener != null) {
                        clickdeleteMsgeListener.onClickDeleteMsg(position);
                    }
                    swipeItem.close();
                }
            });
            ViewFinder viewFinder1 = new ViewFinder(convertView);
            TextView mHead = viewFinder1.textView(R.id.tv_note_content);
            TextView time = (TextView) convertView.findViewById(R.id.tv_plan_time);
            ImageView select_star = (ImageView) convertView.findViewById(R.id.select_star);
            mHead.setText(list.get(position).optString("NOTEPAD_CONTENT"));
            time.setText(TimeUtil.format(list.get(position).optString("NOTEPAD_TIME")));
            select_star.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemsOnClick.onStarClick(view, pos, id);
                }
            });
            if ("1".equals(list.get(position).optString("REMIND_FLAG"))) {
                select_star.setSelected(true);
                isFinish = true;
            } else {
                select_star.setSelected(false);
                isFinish = false;
            }
        }
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
        return 0;
    }

    public void onBoundData(List<JSONObject> datas) {
        if (list != null)
            list.clear();
        list.addAll(datas);
        notifyDataSetChanged();
    }

    public void remove(int positon) {
        list.remove(positon);
        notifyDataSetChanged();
    }

}
