package com.yksj.consultation.dialog;

import com.library.base.dialog.ConfirmDialog;
import com.library.base.dialog.InputDialog;
import com.library.base.dialog.MessageDialog;
import com.library.base.dialog.ShareDialog;

public class DialogManager {

    public static InputDialog getInputDialog(){
        InputDialog inputDialog = new InputDialog();
        return inputDialog;
    }

    public static WaitDialog getWaitDialog(String wait){
        WaitDialog whitDialog = WaitDialog.newInstance(wait);
        return whitDialog;
    }

    public static MessageDialog getMessageDialog(String msg){
        MessageDialog messageDialog = MessageDialog.newInstance("", msg);
        return messageDialog;
    }

    public static ConfirmDialog getConfrimDialog(String msg){
        ConfirmDialog confirmDialog = ConfirmDialog.newInstance("", msg);
        return confirmDialog;
    }

    public static ShareDialog getShareDialog(){
        ShareDialog shareDialog = ShareDialog.newInstance();
        return shareDialog;
    }
}
