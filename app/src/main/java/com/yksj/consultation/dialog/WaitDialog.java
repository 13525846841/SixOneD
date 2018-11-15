package com.yksj.consultation.dialog;

import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.yksj.consultation.sonDoc.R;

/**
 * Created by HEKL on 16/5/9.
 * Used for
 */

public class WaitDialog extends DialogFragment {

    public static final String TAG = WaitDialog.class.getSimpleName();

    private String mContent;
    private TextView mContentView;
    private boolean mBackenable = true;//返回键是否可用

    /**
     * @param manager
     * @param content 文字内容
     */
    @Deprecated
    public static WaitDialog showLodingDialog(FragmentManager manager, String content) {
        WaitDialog dialog = WaitDialog.newInstance(content);
        dialog.show(manager);
        return dialog;
    }

    /**
     * 默认的文字提示
     *
     * @param manager
     * @param resources
     */
    @Deprecated
    public static WaitDialog showLodingDialog(FragmentManager manager, Resources resources) {
        String content = resources.getString(R.string.loading);
        return showLodingDialog(manager, content);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.dialog);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.loading_dialog_layout, container, false);
        initializeView(view);
        return view;
    }

    private void initializeView(View view) {
        mContentView = view.findViewById(R.id.loadingTxt);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mContent = getArguments().getString("content");
        getDialog().setCanceledOnTouchOutside(false);
        getDialog().setCancelable(false);
        getDialog().setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                return !mBackenable && keyCode == KeyEvent.KEYCODE_BACK;
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!TextUtils.isEmpty(mContent)) {
            mContentView.setVisibility(View.VISIBLE);
            mContentView.setText(mContent);
        } else {
            mContentView.setVisibility(View.GONE);
        }
        Window window = getDialog().getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.dimAmount = 0;
        window.setAttributes(params);
    }

    public WaitDialog setContent(String content){
        mContent = content;
        if (mContentView != null){
            mContentView.setText(content);
        }
        return this;
    }

    public WaitDialog show(FragmentManager manager) {
        FragmentTransaction ft = manager.beginTransaction();
        if (this.isAdded()) {
            ft.remove(this);
        }
        ft.add(this, TAG);
        ft.commitAllowingStateLoss();
        return this;
    }

    public static void dismiss(FragmentManager fragmentManager) {
        if (fragmentManager == null) return;
        DialogFragment fragment = (DialogFragment) fragmentManager.findFragmentByTag(TAG);
        if (fragment != null) fragment.dismissAllowingStateLoss();
    }

    public boolean isShowing() {
        if (getDialog() == null) {
            return false;
        }
        return getDialog().isShowing();
    }

    public WaitDialog setBackenable(boolean enable){
        this.mBackenable = enable;
        return this;
    }

    public static WaitDialog newInstance(String content) {
        WaitDialog dialog = new WaitDialog();
        Bundle bundle = new Bundle();
        bundle.putString("content", content);
        dialog.setArguments(bundle);
        return dialog;
    }
}

