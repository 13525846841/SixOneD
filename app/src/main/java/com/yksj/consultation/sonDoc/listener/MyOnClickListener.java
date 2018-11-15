package com.yksj.consultation.sonDoc.listener;

import android.view.View;

import com.yksj.consultation.sonDoc.R;
import com.yksj.healthtalk.utils.ToastUtil;

/**
 * Created by ${chen} on 2017/1/9.
 * 记事本更改状态接口
 */
public interface MyOnClickListener extends View.OnClickListener {
    void onStarClick(View view,int position ,int id);
}
