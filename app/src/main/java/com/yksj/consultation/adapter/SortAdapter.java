package com.yksj.consultation.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.bean.SortModel;

import java.util.ArrayList;
import java.util.List;




   public class SortAdapter extends BaseAdapter implements SectionIndexer {
        private List<SortModel> list = null;
        private Context mContext;

        public SortAdapter(Context mContext) {
            this.mContext = mContext;
            list = new ArrayList<>();
        }

        public void updateListView(List<SortModel> list) {
            this.list = list;
            notifyDataSetChanged();
        }

        public int getCount() {
            return this.list.size();
        }

        public Object getItem(int position) {
            return list.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(final int position, View view, ViewGroup arg2) {
            ViewHolder viewHolder = null;
            final SortModel mContent = list.get(position);
            if (view == null) {
                viewHolder = new ViewHolder();
                view = LayoutInflater.from(mContext).inflate(R.layout.item_sort_listview, null);
                viewHolder.tvTitle = (TextView) view.findViewById(R.id.title);
                viewHolder.tvLetter = (TextView) view.findViewById(R.id.catalog);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }


            int section = getSectionForPosition(position);


            if (position == getPositionForSection(section)) {
                viewHolder.tvLetter.setVisibility(View.VISIBLE);
                viewHolder.tvLetter.setText(mContent.getSortLetters());
            } else {
                viewHolder.tvLetter.setVisibility(View.GONE);
            }

            viewHolder.tvTitle.setText(this.list.get(position).getName());
//        viewHolder.tvTitle.setText(this.list.get(position).getPhone());

            return view;

        }


        final static class ViewHolder {
            TextView tvLetter;
            TextView tvTitle;
        }


        public int getSectionForPosition(int position) {
            return list.get(position).getSortLetters().charAt(0);
        }


        public int getPositionForSection(int section) {
            for (int i = 0; i < getCount(); i++) {
                String sortStr = list.get(i).getSortLetters();
                char firstChar = sortStr.toUpperCase().charAt(0);
                if (firstChar == section) {
                    return i;
                }
            }

            return -1;
        }


        private String getAlpha(String str) {
            String sortStr = str.trim().substring(0, 1).toUpperCase();
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

        public void onBoundData(List<SortModel> data) {
            this.list.clear();
            this.list.addAll(data);
            notifyDataSetChanged();
        }

        public void removeAll() {
            this.list.clear();
            notifyDataSetChanged();
        }

    }
