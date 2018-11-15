package com.yksj.healthtalk.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.yksj.consultation.sonDoc.R;

public class NineBitMapUtils {

	private Context context;
	private int defautMinSize = 30;

	public NineBitMapUtils(Context context) {
		this.context = context;
	}

	public Bitmap resizeBitMapImage(int id) {
		Bitmap bitMapImage = null;
		// First, get the dimensions of the image
		Options options = new Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeResource(context.getResources(), id, options);
		// Only scale if we need to
		// (16384 buffer for img processing)
		Boolean scaleByHeight = Math.abs(options.outHeight - defautMinSize) >= Math
				.abs(options.outWidth - defautMinSize);
		// Load, scaling to smallest power of 2 that'll get it <= desired
		// dimensions
		int sampleSize = scaleByHeight ? options.outHeight / defautMinSize
				: options.outWidth / defautMinSize;
		// sampleSize = (int) Math.pow(2d,
		// Math.floor(Math.log(sampleSize) / Math.log(2d)));
		// Do the actual decoding
		options.inSampleSize = sampleSize;
		options.inJustDecodeBounds = false;
		// options.inTempStorage = new byte[128];
		// while (true) {
		bitMapImage = BitmapFactory.decodeResource(context.getResources(), id,
				options);
		// }
		return bitMapImage;
	}

	public void setMinImageSize(int defautMinSize) {
		this.defautMinSize = defautMinSize;
	}

	
	
	public Bitmap createNineImage() {
		Bitmap resource = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.head_bg).copy(Bitmap.Config.ARGB_8888, true);
		;
		Bitmap result = resizeBitMapImage(R.drawable.default_head_mankind);
		Bitmap[] bitmaps = new Bitmap[9];
		for (int i = 0; i < bitmaps.length; i++) {
			bitmaps[i] = result;
		}
		int w_2 = resource.getWidth();
		int h_2 = resource.getHeight();
		// 缩放指定的图片
		// resource = Bitmap.createScaledBitmap(resource, 250/w_2,
		// resource.getHeight() * 2, true);
		Bitmap newBitmap = null;
		newBitmap = Bitmap.createBitmap(resource);
		Canvas canvas = new Canvas(newBitmap);
		Paint paint = new Paint();
		int w = result.getWidth();
		int h = result.getHeight();
		paint.setAntiAlias(true);
		w_2 = resource.getWidth();
		h_2 = resource.getHeight();
		int tep =( resource.getWidth() - 3*result.getWidth())/4;
		int with = result.getWidth();
//		for (int j = 0; j < bitmaps.length; j++) {
			int l = tep;
			int t = tep;
			int r = tep + with;
			int b = tep + with;
			canvas.drawBitmap(bitmaps[0], l,
					t, paint);
			canvas.drawBitmap(bitmaps[1], 2 * l + with,
					t, paint);
			canvas.drawBitmap(bitmaps[2], 3 * l + 2 * with,
					t, paint);
			
			l = tep;
			t = 2 * tep + with;
			r = tep + with;
			b = 2 * tep + 2 * with;
			canvas.drawBitmap(bitmaps[3], l,
					t, paint);
			canvas.drawBitmap(bitmaps[4], 2 * l + with,
					t, paint);
			canvas.drawBitmap(bitmaps[5], 3 * l + 2 * with,
					t, paint);
			
			l = tep;
			t = 3 * tep + 2 * with;
			r = tep + with;
			b = 3 * tep + 3 * with;
			canvas.drawBitmap(bitmaps[6], l,
					t, paint);
			canvas.drawBitmap(bitmaps[7], 2 * l + with,
					t, paint);
			canvas.drawBitmap(bitmaps[8], 3 * l + 2 * with,
					t, paint);
//		}
		canvas.save();
		// 存储新合成的图片
		canvas.restore();
		return newBitmap;
		// 保存
		// saveMyBitmap(codeBitmap);
	}

}
