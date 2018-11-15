package com.yksj.consultation.comm;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.library.base.base.BaseActivity;
import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.sonDoc.views.CirclePageIndicator;

import org.universalimageloader.core.DefaultConfigurationFactory;
import org.universalimageloader.core.DisplayImageOptions;
import org.universalimageloader.core.ImageLoader;
import org.universalimageloader.core.assist.FailReason;
import org.universalimageloader.core.assist.SimpleImageLoadingListener;

/**
 * 查看图片
 * Intent intent = new Intent(this,ImageGalleryActivity.class);
   intent.putExtra(ImageGalleryActivity.URLS_KEY,new String[]{});
   intent.putExtra(ImageGalleryActivity.TYPE_KEY,0);//0,1单个,多个
 * @author zhao
 */
public class ImageOpenActivity extends BaseActivity {
	
	public static final String URLS_KEY = "URLS";
	public static final String TYPE_KEY = "TYPE";//0,1单个,多个
	private int mType = 0;
	
	ViewPager mViewPager;
	DisplayImageOptions mDisplayImageOptions;
	ImageLoader mImageLoader;
	private Button mButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.open_gallery_viewpage_layout);
		mType = getIntent().getIntExtra("type",0);
		if(getIntent().hasExtra("title")){
			titleTextV.setText(getIntent().getStringExtra("title"));
		}
		mButton=(Button) findViewById(R.id.fl_button);
		mViewPager = (ViewPager)findViewById(R.id.pager);
		int type = getIntent().getIntExtra(TYPE_KEY,0);
		String[] mImages = getIntent().getStringArrayExtra(URLS_KEY);
		if(mImages == null){
			mImages = new String[]{};
		}
		mImageLoader = ImageLoader.getInstance();
		mDisplayImageOptions = DefaultConfigurationFactory.createGalleryDisplayImageOptions(this);
		GalleryPagerAdapter galleryPagerAdapter = new GalleryPagerAdapter(mImages);
		mViewPager.setAdapter(galleryPagerAdapter);
		CirclePageIndicator indicator = (CirclePageIndicator)findViewById(R.id.indicator);
		if(type == 0){
			indicator.setVisibility(View.GONE);
		}else{
			indicator.setVisibility(View.INVISIBLE);
		}
		indicator.setViewPager(mViewPager);
		indicator.setSnap(true);
		
		if(getIntent().hasExtra("position")){
			mViewPager.setCurrentItem(getIntent().getIntExtra("position",0));
			galleryPagerAdapter.notifyDataSetChanged();
		}
		
//		if(mType == 1){//聊天跳转
//		}
//		findViewById(R.id.footerbar).setVisibility(View.GONE);
		
		mButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
	}


//	@Override
//	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		if (keyCode == KeyEvent.KEYCODE_BACK) {
//			startActivity(new Intent(ImageOpenActivity.this, MainTabActivity.class));
//			ImageOpenActivity.this.finish();
//			return true;
//		}
//		 return super.onKeyDown(keyCode, event);
//	}


	private class GalleryPagerAdapter extends  PagerAdapter{
		String[] images;
		LayoutInflater mLayoutInflater;
		
		public GalleryPagerAdapter(String[] urls) {
			images = urls;
			mLayoutInflater = getLayoutInflater();
		}
		
		@Override
		public void destroyItem(View container, int position, Object object) {
			((ViewPager)container).removeView((View)object);
		}
		
		@Override
		public int getCount() {
			return images.length;
		}

		@Override
		public Object instantiateItem(View container, int position) {
			final View view = mLayoutInflater.inflate(R.layout.gallery_list_item,null);
			final ProgressBar progressBar = (ProgressBar)view.findViewById(R.id.galleryProgressBar);
			final ImageView imageView = (ImageView)view.findViewById(R.id.galleryImageV);
			mImageLoader.displayImage(images[position],imageView,mDisplayImageOptions,new SimpleImageLoadingListener(){
				@Override
				public void onLoadingStarted() {
					progressBar.setVisibility(View.INVISIBLE);
				}
				@Override
				public void onLoadingFailed(FailReason failReason) {
					progressBar.setVisibility(View.GONE);
				}
				@Override
				public void onLoadingComplete(Bitmap loadedImage) {
					progressBar.setVisibility(View.GONE);
					imageView.setImageBitmap(loadedImage);
				}
			});
			((ViewPager)container).addView(view,0);
			return view;
		}
		@Override
		public boolean isViewFromObject(View view, Object arg1) {
			return view == arg1;
		}
	}

}
