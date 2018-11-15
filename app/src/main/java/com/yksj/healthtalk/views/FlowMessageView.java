package com.yksj.healthtalk.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.yksj.consultation.sonDoc.R;

/**
 * Created by Administrator on 2015/7/14.
 */
public class FlowMessageView extends RelativeLayout {
    private Context mcontext;
    private EditText tvNum;
    private Button imageDelete;//删除button
    public FlowMessageView(Context context) {
        super(context);
        this.mcontext=context;
        init(null);
    }

    public FlowMessageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mcontext=context;
        init(attrs);
    }

    public FlowMessageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void init(AttributeSet attrs){
        LayoutInflater.from(mcontext).inflate(R.layout.flow_message_view, this,true);
        tvNum=(EditText) findViewById(R.id.message_tv_num);
        imageDelete=(Button) findViewById(R.id.message_btn_delete);
        if(attrs!=null){
            TypedArray array=mcontext.obtainStyledAttributes(attrs, R.styleable.MessageImageView);
            CharSequence ch=array.getText(R.styleable.MessageImageView_android_text);
            if(ch!=null){
                tvNum.setText(ch);//设置文本
            }
            array.recycle();
        }
    }
    /**
     * 设置小Button 的点击事件
     * @param deleteListener
     */
    public void setDeleteListener(OnClickListener deleteListener) {
        imageDelete.setOnClickListener(deleteListener);
    }
}
