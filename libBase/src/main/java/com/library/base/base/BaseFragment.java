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

import com.library.base.event.EExitApp;
import com.library.base.utils.EventManager;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Fragment基类，新建的fragment应当继承该基类
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

    /**
     * 内容ID，R.layout.****
     * @return
     */
    public int createLayoutRes() {
        return 0;
    }

    /**
     * 内容View，new *****Layout()
     * @return
     */
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

    /**
     * 初始化方法，之类可实现该方法，初始化相关操作
     * @param view
     */
    public void initialize(View view) {
    }

    /**
     * 应用退出事件接收，之类可实现该方法，在应用退出时执行相关操作
     * @param e
     */
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
     * 返回按钮点击事件
     * @param view
     */
    public void onBackPressed(View view) {
        if (mBackPressedClickListener != null) {
            mBackPressedClickListener.onBackClick(view);
        } else {
            getActivity().onBackPressed();
        }
    }
}
