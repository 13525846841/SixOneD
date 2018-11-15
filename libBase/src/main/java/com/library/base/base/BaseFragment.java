package com.library.base.base;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.library.base.R;
import com.library.base.event.EExitApp;
import com.library.base.utils.EventManager;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 *
 *
 * @author origin
 */
public class BaseFragment extends Fragment {
    private static final String TAG = BaseFragment.class.getName();
    public ImageView titleLeftBtn;
    public ImageView titleRightBtn;
    public TextView titleRightBtn2;
    public TextView titleTextV;
    public OnBackPressedClickListener mBackPressedClickListener;
    public FragmentActivity mActivity;
    private Unbinder mBind;
    protected View mContentView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContentView = createLayout();
        int layoutRes = createLayoutRes();
        if (mContentView == null && layoutRes != 0) {
            mContentView = inflater.inflate(layoutRes, container, false);
        }
        return mContentView;
    }

    public int createLayoutRes() {
        return 0;
    }

    public View createLayout(){
        return null;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBind = ButterKnife.bind(this, view);
        EventManager.register(this);
        initialize(view);
    }

    public void initialize(View view) {
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onExitAppEvent(EExitApp e) {
    }

    @Override
    public void onDestroyView() {
        if (mBind != null){
            mBind.unbind();
        }
        EventManager.unregister(this);
        mActivity = null;
        super.onDestroyView();
    }

    /**
     * 返回按键监听事件
     *
     * @author origin
     */
    public interface OnBackPressedClickListener {
        void onBackClick(View view);

        //区域选择点击
        void onAreaItemClick(JSONArray array, String titlePicPath);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mActivity = (FragmentActivity) activity;
            mBackPressedClickListener = (OnBackPressedClickListener) activity;
        } catch (ClassCastException e) {
        }
    }

    /**
     * title 返回按钮
     *
     * @param view
     */
    public void onBackPressed(View view) {
        if (mBackPressedClickListener != null) {
            mBackPressedClickListener.onBackClick(view);
        } else {
            getActivity().onBackPressed();
        }
    }

    public void initTitleView(View view) {
        titleLeftBtn = (ImageView) view.findViewById(R.id.title_back);
        titleRightBtn = (ImageView) view.findViewById(R.id.title_right);
        titleTextV = (TextView) view.findViewById(R.id.title_lable);
        titleRightBtn2 = (TextView) view.findViewById(R.id.title_right2);
    }
}
