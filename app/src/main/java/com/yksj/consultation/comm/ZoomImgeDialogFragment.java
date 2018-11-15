package com.yksj.consultation.comm;

import org.universalimageloader.core.DefaultConfigurationFactory;
import org.universalimageloader.core.DisplayImageOptions;
import org.universalimageloader.core.ImageLoader;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.yksj.consultation.sonDoc.R;

public class ZoomImgeDialogFragment extends DialogFragment {
	ImageView mImageView;
	DisplayImageOptions mDisplayImageOptions;
	ImageLoader mImageLoader;
	
	public static ZoomImgeDialogFragment show(String path,FragmentManager fm){
		ZoomImgeDialogFragment fragment = new ZoomImgeDialogFragment();
		Bundle bundle = new Bundle();
		bundle.putString("path",path);
		fragment.setArguments(bundle);
		fragment.show(fm,"ZOOM_IMAGE_DIALOG");
		return fragment;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setStyle(DialogFragment.STYLE_NO_FRAME,android.R.style.Theme_Black_NoTitleBar);
		mImageLoader = ImageLoader.getInstance();
		mDisplayImageOptions = DefaultConfigurationFactory.createGalleryDisplayImageOptions(getActivity());
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.zoom_image_layout, null);
		mImageView = (ImageView)view.findViewById(R.id.galleryImageV);
		
		mImageView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ZoomImgeDialogFragment.this.dismissAllowingStateLoss();
			}
		});
		return view;
	}
	
	@Override
	public void onActivityCreated(Bundle arg0) {
		super.onActivityCreated(arg0);
		mImageLoader.displayImage(getArguments().getString("path"),mImageView,mDisplayImageOptions);
		
	}
}
