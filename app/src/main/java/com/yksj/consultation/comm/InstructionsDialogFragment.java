package com.yksj.consultation.comm;

import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
/**
 * 引导
 * @author jack_tang
 *
 */
public class InstructionsDialogFragment extends DialogFragment {
	private ImageView mImageView;
	private BitmapDrawable mDrawable;
	
	public static void show(int type,FragmentManager manager) {
		boolean isShow = true;//SharePreUtils.isShowInstructions(type);
		if(isShow)return;
		int resid = 0;
		switch(type){
		/*case 1:
			resid = R.drawable.introduce1;
			break;
		case 2:
			resid = R.drawable.introduce2;
			break;
		case 3:
			resid = R.drawable.introduce3;
			break;
		case 4:
			resid = R.drawable.introduce4;
			break;
		case 5:
			resid = R.drawable.introduce5;
			break;	
		case 6:
			resid = R.drawable.introduce6;
			break;
		case 7:
			resid = R.drawable.introduce7;
			break;	
		case 8:
			resid = R.drawable.introduce8;
			break;
		case 9:
			resid = R.drawable.introduce9;
			break;
		case 10:
			resid = R.drawable.introduce10;
			break;*/
		}
		InstructionsDialogFragment fragment = new InstructionsDialogFragment();
		Bundle bundle = new Bundle();
		bundle.putInt("resid",resid);
		fragment.setArguments(bundle);
		fragment.show(manager, "INSTRUCTIONS");
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setStyle(DialogFragment.STYLE_NO_TITLE, 0);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		getDialog().setCancelable(true);
		getDialog().setCanceledOnTouchOutside(true);
		getDialog().getWindow().setBackgroundDrawable(
				new ColorDrawable(Color.TRANSPARENT));
		int id = getArguments().getInt("resid");
		mImageView = new ImageView(getActivity());
		mDrawable = (BitmapDrawable) getResources().getDrawable(id);
		mImageView.setImageDrawable(mDrawable);
		mImageView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				InstructionsDialogFragment.this.dismissAllowingStateLoss();
			}
		});
		return mImageView;
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		mImageView.setImageResource(0);
		if (mDrawable != null) {
			mDrawable.setCallback(null);
			if (!mDrawable.getBitmap().isRecycled()) {
				mDrawable.getBitmap().recycle();
			}
		}
		mDrawable = null;
	}
}
