package com.yksj.consultation.comm;

import android.app.Dialog;
import android.content.res.Resources;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.widget.ImageView;
import android.widget.TextView;

import com.yksj.consultation.sonDoc.R;


/**
 * Created by HEKL on 16/5/9.
 * Used for
 */

public class LoadingProgressFragmentDialog extends DialogFragment {
    String content;
    private AnimationDrawable animationDrawable;
    private ImageView loading_image;
    /**
     * @param manager
     * @param content 文字内容
     */
    public static LoadingProgressFragmentDialog showLodingDialog(FragmentManager manager, String content){
        if(!manager.isDestroyed()){
            Fragment fragment = manager.findFragmentByTag("loading");
            FragmentTransaction ft = manager.beginTransaction();
            if(fragment != null){
                ft.remove(fragment);
            }
            LoadingProgressFragmentDialog dialog = LoadingProgressFragmentDialog.newInstance(content);
            ft.add(dialog,"loading");
            ft.commitAllowingStateLoss();
            return dialog;
        }else{
            return null;
        }
    }

    /**
     * 默认的文字提示
     * @param manager
     * @param resources
     */
    public static LoadingProgressFragmentDialog showLodingDialog(FragmentManager manager, Resources resources){
        String content = resources.getString(R.string.loading);
        return showLodingDialog(manager, content);
    }

    public static void dismiss(FragmentManager fragmentManager){
        if(fragmentManager == null)return;
        DialogFragment fragment = (DialogFragment)fragmentManager.findFragmentByTag("loading");
        if(fragment != null)fragment.dismissAllowingStateLoss();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getActivity(), R.style.dialog);
        dialog.setContentView(R.layout.loading_progress_dialog_layout);
        dialog.setCanceledOnTouchOutside(false);
        TextView textView = (TextView)dialog.findViewById(R.id.loadingTxt);
//		loading_image = (ImageView)dialog.findViewById(R.id.loading_image);
//		animationDrawable = (AnimationDrawable) loading_image.getBackground();
        textView.setText(content);
//		animationDrawable.startRecorder();
        return dialog;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser){
            if(animationDrawable == null){
                animationDrawable = (AnimationDrawable) loading_image.getBackground();
                animationDrawable.start();
            }else if(!animationDrawable.isRunning()){
                animationDrawable.start();
            }
        }else{
            if(animationDrawable !=null && animationDrawable.isRunning()){
                animationDrawable.stop();
            }
        }

    }

    public boolean isShowing(){
        if(getDialog() == null){
            return false;
        }
        return  getDialog().isShowing();
    }

    static LoadingProgressFragmentDialog newInstance(String content){
        LoadingProgressFragmentDialog dialog = new LoadingProgressFragmentDialog();
        Bundle bundle = new Bundle();
        bundle.putString("content",content);
        dialog.setArguments(bundle);
        return dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        content = getArguments().getString("content");
    }
}

