package com.yksj.consultation.comm;

import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.library.base.base.BaseTitleActivity;
import com.library.base.imageLoader.ImageLoader;
import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.sonDoc.views.CirclePageIndicator;

/**
 * 查看图片
 * Intent intent = new Intent(this,ImageGalleryActivity.class);
 * intent.putExtra(ImageGalleryActivity.URLS_KEY,new String[]{});
 * intent.putExtra(ImageGalleryActivity.TYPE_KEY,0);//0,1单个,多个
 *
 * @author zhao
 */
public class ImageGalleryActivity extends BaseTitleActivity implements OnClickListener {

    public static final String URLS_KEY = "urls";
    public static final String TYPE_KEY = "type";//0,1单个,多个
    public static final String POSITION = "position";//0,1单个,多个

    protected ViewPager mViewPager;
    protected PagerAdapter mAdapter;
    protected String[] mImages;

    @Override
    public int createLayoutRes() {
        return R.layout.gallery_viewpage_layout;
    }

    @Override
    public @ColorRes
    int getStatusBarColor() {
        return R.color.black;
    }

    @Override
    public void initialize(Bundle bundle) {
        super.initialize(bundle);
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mImages = getIntent().getStringArrayExtra(URLS_KEY);
        mAdapter = new GalleryPagerAdapter(mImages);
        mViewPager.setAdapter(mAdapter);
        CirclePageIndicator indicator = (CirclePageIndicator) findViewById(R.id.indicator);
        if (mImages.length <= 1) {
            indicator.setVisibility(View.GONE);
        } else {
            indicator.setVisibility(View.INVISIBLE);
        }
        indicator.setViewPager(mViewPager);
        indicator.setSnap(true);

        if (getIntent().hasExtra(POSITION)) {
            mViewPager.setCurrentItem(getIntent().getIntExtra(POSITION, 0));
            mAdapter.notifyDataSetChanged();
        }
    }

    private class GalleryPagerAdapter extends PagerAdapter {
        String[] images;
        LayoutInflater mLayoutInflater;

        public GalleryPagerAdapter(String[] urls) {
            images = urls;
            mLayoutInflater = getLayoutInflater();
        }

        @Override
        public int getCount() {
            return images.length;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            final View view = mLayoutInflater.inflate(R.layout.gallery_list_item, null);
            final ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.galleryProgressBar);
            final ImageView imageView = (ImageView) view.findViewById(R.id.galleryImageV);
            String imagePath = images[position];
            ImageLoader.load(imagePath).into(imageView);
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public boolean isViewFromObject(View view, Object arg1) {
            return view == arg1;
        }
    }
}
