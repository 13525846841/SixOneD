package com.yksj.consultation.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.library.base.imageLoader.ImageLoader;
import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.sonDoc.consultation.main.ChoiceFileAty;
import com.yksj.healthtalk.filemanager.FileInfo;
import com.yksj.healthtalk.filemanager.FileUtil;
import com.yksj.healthtalk.filemanager.Util;

/**
 * Created by ${chen} on 2017/7/5.
 */
public class ChoiceFileAdapter extends SimpleBaseAdapter<FileInfo> {
    private ChoiceFileAty activity;

    public ChoiceFileAdapter(Context context) {
        super(context);
        this.activity = (ChoiceFileAty) context;
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public int getItemResource() {
        return R.layout.item_lv_single_choice;
    }

    @Override
    public View getItemView(final int position, View convertView, ViewHolder holder) {
        ImageView filePicture = holder.getView(R.id.image);
        TextView fileName = holder.getView(R.id.file_name);
        TextView fileInfo = holder.getView(R.id.file_info);
        CheckBox checkBox = holder.getView(R.id.tv_single_choice);
        LinearLayout intent = holder.getView(R.id.ll_intent);
        fileName.setText(datas.get(position).fileName);
        String size=Util.convertStorage(datas.get(position).fileSize);
        fileInfo.setText(Util.formatDateString(context, datas.get(position).ModifiedDate) + " " + (datas.get(position).IsDir ? "" : size));
        if (datas.get(position).Selected) {
            checkBox.setChecked(true);
        } else {
            checkBox.setChecked(false);
        }
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isPressed()) {
                    for (int i = 0; i < datas.size(); i++) {
                        if (datas.get(i).Selected) {
                            datas.get(i).Selected = false;
                        }
                    }
                    datas.get(position).Selected = isChecked;
                    activity.getPath(datas.get(position).filePath,datas.get(position).fileName);
                    notifyDataSetChanged();
                }
            }
        });
        intent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String path = datas.get(position).filePath;
                Intent intent = FileUtil.openFile(path);
                context.startActivity(intent);
            }
        });

        if ("1".equals(type)) {
            filePicture.setImageResource(R.drawable.file_mp4);
//
//
//
//            MediaMetadataRetriever media = new MediaMetadataRetriever();
//            try {
//                media.setDataSource(datas.get(position).filePath);
//            } catch (Exception e) {
//                Log.d("aaa", "onBindViewHolder: " + "有了");
//            }
//            Bitmap bitmap = media.getFrameAtTime();
//            filePicture.setImageBitmap(bitmap);
        } else if ("2".equals(type)) {
            ImageLoader
                    .load(datas.get(position).filePath)
                    .placeholder(R.drawable.file_png)
                    .error(R.drawable.file_png)
                    .into(filePicture);
        } else if ("3".equals(type)) {
            filePicture.setImageResource(R.drawable.file_word);
        }
        return convertView;
    }
}
