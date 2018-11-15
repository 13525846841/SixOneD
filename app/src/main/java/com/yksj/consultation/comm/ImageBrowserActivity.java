package com.yksj.consultation.comm;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.alexvasilkov.gestures.Settings;
import com.alexvasilkov.gestures.views.GestureImageView;
import com.library.base.base.BaseActivity;
import com.library.base.imageLoader.ImageLoader;
import com.luck.picture.lib.photoview.PhotoView;
import com.yksj.consultation.sonDoc.R;

import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;

public class ImageBrowserActivity extends BaseActivity {

    @BindView(R.id.background_view)
    View mBackgroundView;

    @BindView(R.id.view_pager)
    ViewPager mViewPager;

    @BindView(R.id.indicator_view)
    TextView mIndicatorView;

    private static BrowserSpace mBrowserSpace;
    private ImageAdapter mAdapter;

    public static BrowserSpace from(Context context) {
        mBrowserSpace = new BrowserSpace(context);
        return mBrowserSpace;
    }

    @Override
    protected void onCreate(Bundle arg0) {
        setWindowFullScreen();
        super.onCreate(arg0);
    }

    /**
     * 设置全屏
     */
    private void setWindowFullScreen() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            // 虚拟导航栏透明
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
    }

    @Override
    public int getStatusBarColor() {
        return R.color.transparent;
    }

    @Override
    public int createLayoutRes() {
        return R.layout.activity_image_browser;
    }

    @Override
    public void initialize(Bundle bundle) {
        super.initialize(bundle);
        mBrowserSpace = BrowserSpace.getInstance();
        initializeView();
    }

    private void initializeView() {
        mIndicatorView.setVisibility(mBrowserSpace.pictures.size() > 1 ? View.VISIBLE : View.GONE);
        mAdapter = new ImageAdapter();
        mViewPager.setAdapter(mAdapter);
        mViewPager.setCurrentItem(mBrowserSpace.pictures.size() > 1 ? mBrowserSpace.curPosition : 1, false);
        mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if (mIndicatorView.getVisibility() == View.VISIBLE) {
                    setPositionLabel(position);
                }
            }
        });
        setPositionLabel(mBrowserSpace.curPosition);
    }

    /**
     * 设置图片浏览角标
     * @param position
     */
    private void setPositionLabel(int position){
        position += 1;
        mIndicatorView.setText(String.format("%s/%s", position, mBrowserSpace.pictures.size()));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.anim_browser_enter, R.anim.anim_browser_exit);
    }

    private class ImageAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return mBrowserSpace.pictures.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = LayoutInflater.from(container.getContext()).inflate(R.layout.item_browser_image, container, false);
            PhotoView previewView = view.findViewById(R.id.preview_image);
            ImageLoader.load(mBrowserSpace.pictures.get(position)).into(previewView);
            container.addView(view);
            return view;
        }

        private void settingGestureView(GestureImageView gestureImageView) {
            gestureImageView.getController()
                    .getSettings()
                    .setPanEnabled(true)
                    .setZoomEnabled(true)
                    .setDoubleTapEnabled(true)
                    .setOverscrollDistance(gestureImageView.getContext(), 0f, 0f)
                    .setOverzoomFactor(2f)
                    .setFillViewport(true)
                    .setFitMethod(Settings.Fit.INSIDE)
                    .setBoundsType(Settings.Bounds.NORMAL)
                    .setGravity(Gravity.CENTER)
                    .setAnimationsDuration(Settings.ANIMATIONS_DURATION);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

    public static class BrowserSpace {
        private WeakReference<Context> weakContext;
        private List<String> pictures;
        private int curPosition;
        private static BrowserSpace INSTANCE;

        private static BrowserSpace getInstance() {
            return INSTANCE;
        }

        public static BrowserSpace from(Context context) {
            return new BrowserSpace(context);
        }

        public BrowserSpace(Context context) {
            this.weakContext = new WeakReference<>(context);
            INSTANCE = this;
        }

        public BrowserSpace setImagePaths(List<String> imagePaths) {
            this.pictures = imagePaths;
            return this;
        }

        public BrowserSpace setImagePath(String imagePath) {
            List<String> list = Arrays.asList(new String[]{imagePath});
            setImagePaths(list);
            return this;
        }

        public BrowserSpace setCurPosition(int curPosition) {
            this.curPosition = curPosition;
            return this;
        }

        public void startActivity() {
            Context context = weakContext.get();
            if (context != null) {
                Intent intent = new Intent(weakContext.get(), ImageBrowserActivity.class);
                context.startActivity(intent);
                if (context instanceof Activity) {
                    ((Activity) weakContext.get()).overridePendingTransition(R.anim.anim_browser_enter, R.anim.anim_browser_exit);
                }
            }
        }
    }
}
