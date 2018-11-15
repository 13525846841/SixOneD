package com.yksj.consultation.sonDoc.home;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.sonDoc.listener.DialogOnClickListener;
import com.yksj.healthtalk.utils.ScreenSizeUtils;

/**
 * Created by ${chen} on 2017/4/12.
 */
public class InviteAlertDialog implements View.OnClickListener {

    private Dialog mDialog;
    private View mDialogView;
    private static Context mContext;
    private Builder mBuilder;
    private Button finish;
    public static EditText mEt;
    private TextView mTitle;
    public InviteAlertDialog(Builder builder) {
        mBuilder = builder;
        mDialog = new Dialog(mContext, R.style.MyDialogStyle);
        mDialogView = View.inflate(mContext, R.layout.dialog_invite, null);
        finish = (Button) mDialogView.findViewById(R.id.sure);
        mTitle = (TextView) mDialogView.findViewById(R.id.title);
        finish.setOnClickListener(this);
        mDialogView.setMinimumHeight((int)(ScreenSizeUtils.getInstance(mContext).getScreenHeight
                () * builder.getHeight()));
        mDialog.setContentView(mDialogView);
        Window dialogWindow = mDialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = (int) (ScreenSizeUtils.getInstance(mContext).getScreenWidth() * builder.getWidth());
        lp.gravity = Gravity.CENTER;
        dialogWindow.setAttributes(lp);
        initDialog();
    }
    private void initDialog() {
        mDialog.setCanceledOnTouchOutside(mBuilder.isTouchOutside());
        mTitle.setText(mBuilder.getTitleText());
    }

    public void show() {
        mDialog.show();
    }

    public void dismiss() {
        mDialog.dismiss();
    }

    public String edittext(){
        String text = mEt.getText().toString().trim();
        return text;
    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.sure && mBuilder.getListener() != null) {
            mBuilder.getListener().clickButton(finish);
            return;
        }
    }
    public static class Builder {

        private String titleText;
        private boolean isTouchOutside;
        private float height;
        private float width;
        private DialogOnClickListener listener;

        public Builder(Context context) {
            mContext = context;
            listener = null;
            isTouchOutside = true;
            height = 0.21f;
            width = 0.73f;
        }
        public boolean isTouchOutside() {
            return isTouchOutside;
        }

        public Builder setCanceledOnTouchOutside(boolean touchOutside) {
            isTouchOutside = touchOutside;
            return this;
        }
        public String getTitleText(){
            return titleText;
        }
        public Builder setTitleText(String titleText){
            this.titleText=titleText;
            return this;
        }


        public float getHeight() {
            return height;
        }

        public Builder setHeight(float height) {
            this.height = height;
            return this;
        }

        public float getWidth() {
            return width;
        }

        public Builder setWidth(float width) {
            this.width = width;
            return this;
        }

        public DialogOnClickListener getListener() {

            return listener;
        }

        public Builder setOnclickListener(DialogOnClickListener listener) {
            this.listener = listener;
            return this;
        }

        public InviteAlertDialog build() {
            return new InviteAlertDialog(this);
        }

    }
}
