package com.yksj.consultation.comm.imageload;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import com.yksj.healthtalk.net.http.ApiService;
import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.app.AppContext;
import com.yksj.healthtalk.utils.ImageUtils;
import com.yksj.healthtalk.utils.SystemUtils;

public class ImageLoader {
	// 图片下载路径
	final String downPath;

	MemoryCache memoryCache = new MemoryCache();

	FileCache fileCache;
	/**
	 * 图片缓存
	 */
	private Map<ImageView, String> imageViews = Collections
			.synchronizedMap(new WeakHashMap<ImageView, String>());
	ExecutorService executorService;
	Context mContext;

	public ImageLoader(Context context) {
		mContext = context.getApplicationContext();
		downPath = AppContext.getHTalkApplication().getApiRepository().URL_QUERYHEADIMAGE;
		fileCache = new FileCache(context);
		executorService = Executors.newFixedThreadPool(5);
	}


	/**
	 *  显示图片
	 * @param type 下载类型  1头像 2图片 3地图
	 * @param url
	 * @param mImageView
	 */
/*	public void displayImage(int type,String url,ImageView mImageView){
		if(!SettingManager.isSDCardMount||url==null){
			getDefutImage(type, mImageView, null);
			return;
		}
		Bitmap bitmap=memoryCache.get(url);
		if(bitmap!=null){
			mImageView.setImageBitmap(bitmap);
		}else{
			queuePhoto(url, type, mImageView);
			getDefutImage(type, mImageView, null);
		}
	}*/

	/**
	 * 获取默认的图片
	 * @param type
	 * 	1.头像  2.下载图片
	 * @param mImageView
	 * @param sex
	 */
	@SuppressWarnings("ResourceType")
	private void getDefutImage(int type, ImageView mImageView, String sex) {
		Bitmap bitmap=null;
		try {
			if(type==1){
				bitmap=memoryCache.get(sex);
				if(bitmap!=null){
					mImageView.setImageBitmap(bitmap);
					return ;
				}
				bitmap = getHeaderBitmap(sex);
			}else if(type==2){
				bitmap=memoryCache.get("defut_down_image");
				if(bitmap !=null){
					mImageView.setImageBitmap(bitmap);
					return ;
				}
				InputStream  inputStream=mContext.getResources().openRawResource(R.drawable.chat_default_bg);
				bitmap=BitmapFactory.decodeStream(inputStream);
				if(inputStream!=null){
					inputStream.close();
					memoryCache.put("defut_map_image", bitmap);
				}
			}
		} catch (Exception e) {
			
		}
		mImageView.setImageBitmap(bitmap);
	}


	/**
	 * 设置默认的头像
	 * @param sex
	 * @return
	 */
	private Bitmap getHeaderBitmap(String sex) {
		if(sex == null)sex="Z";
		Bitmap bitmap=memoryCache.get(sex);
		if(bitmap==null){
			if("M".equalsIgnoreCase(sex)){
				bitmap=getDefutImage(R.drawable.default_head_mankind);
				memoryCache.put(sex, bitmap);
			}else if("W".equalsIgnoreCase(sex)){
				bitmap=getDefutImage(R.drawable.default_head_female);
				memoryCache.put(sex, bitmap);
			}else{
				bitmap=getDefutImage(R.drawable.default_head_mankind);
				memoryCache.put(sex, bitmap);
			}
		}
		return bitmap;
	}

/**
 * 从资源文件中获得图片并转换为bitmap
 * @param id 
 * @return
 */
	private Bitmap getDefutImage(int id) {
		Bitmap bitmap=null;
		try {
			InputStream inputStream=mContext.getResources().openRawResource(id);
			bitmap =BitmapFactory.decodeStream(inputStream);
			if(inputStream!=null){
				inputStream.close();
			}
		} catch (IOException e) {
			
		}
		return bitmap;
	}


	/**
	 * 设置性别图标
	 * 
	 * @param imageView
	 * @param sex
	 */
	public void displayImageSexIcn(ImageView imageView, String sex) {
		String sexCache = sex + "icn";
		imageViews.put(imageView,sexCache);
		Bitmap bitmap = memoryCache.get(sexCache);
		if (bitmap == null) {
			int id = -1;
			if ("M".equalsIgnoreCase(sex)) {
				id = R.drawable.sex_man;
			} else if ("W".equalsIgnoreCase(sex)) {
				id = R.drawable.sex_women;
			}
			if (id != -1) {
				bitmap = getDefutImage(id);
				memoryCache.put(sexCache, bitmap);
			}
		}
		imageView.setImageBitmap(bitmap);
	}

	/**
	 * 显示头像
	 * 
	 * @param type
	 *            下载类型 ,1头像,2图片 ,3地图
	 * @param url
	 * @param imageView
	 */
	public void displayImage(String url, ImageView imageView, String sex,
			boolean isGroup) {
		if(url == null || "".equals(url)){
			Bitmap bitmap = getHeaderBitmap(sex);
			imageView.setImageBitmap(bitmap);
			return;
		}
		imageViews.put(imageView,url);
		Bitmap bitmap = memoryCache.get(url);
		if (bitmap != null) {
			imageView.setImageBitmap(bitmap);
		} else {
			if (isGroup) {
				if (url.startsWith("assets")) {
					bitmap = getSystemHeader(url);
					imageView.setImageBitmap(bitmap);
					return;
				}
			} else {
				if (url.startsWith("assets")) {
					bitmap = getHeaderBitmap(sex);
					imageView.setImageBitmap(bitmap);
					return;
				}
			}
			if(url != null && !"".equals(url) && SystemUtils.getScdExit())queuePhoto(url, 1, sex, imageView);
		}
	}
	

	/**
	 * 获取系统头像
	 * 
	 * @param imageView
	 * @param savepath
	 */
	private Bitmap getSystemHeader(String savepath) {
		Bitmap bitmap = memoryCache.get(savepath);
		if (bitmap != null)
			return bitmap;
		String path = savepath.substring(savepath.indexOf("/") + 1);
		InputStream in = null;
		// 系统头像
		try {
			in = mContext.getAssets().open(path);
			bitmap = BitmapFactory.decodeStream(in);
			memoryCache.put(savepath, bitmap);
		} catch (IOException e) {
		} finally {
			if (in != null)
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		return bitmap;
	}

	/**
	 * 获得默认的头像
	 * 
	 * @param imageView
	 * @param sex
	 * @param isGroup
	 */
	private void getDefaultHeaderImage(ImageView imageView, String sex,
			boolean isGroup) {
		Bitmap bitmap = null;
		if (isGroup) {
			bitmap = memoryCache.get("group_header");
			if (bitmap == null) {
				bitmap = getDefutImage(R.drawable.default_head_group);
				memoryCache.put("group_header", bitmap);
			}
		} else {
			bitmap = getHeaderBitmap(sex);
		}
		imageView.setImageBitmap(bitmap);
	}


	/**
	 * 图片下载失败默认图
	 * 
	 * @return
	 */
	public Bitmap getDownFailImage() {
		return getDefutImage(R.drawable.chat_fail_default_bg);
	}



	/**
	 * 头像查询
	 * 
	 * @param url
	 * @param type
	 * @param sex
	 * @param imageView
	 */
	private void queuePhoto(String url, int type, String sex,
			ImageView imageView) {
		PhotoToLoad p = new PhotoToLoad(type, url, sex, imageView);
		executorService.submit(new PhotosLoader(p));
	}

	public Bitmap getBitmap(String key) {
		Bitmap bitmap = memoryCache.get(key);
		return bitmap;
	}

	/**
	 * 
	 * 获得一个下载图片对象
	 * 
	 * @param url
	 * @param key
	 * @param type
	 * @return
	 */
	private Bitmap getBitmap(String url, int type, String sex) {
		File f = fileCache.getFile(url, type);
		Bitmap bitmap = decodeFile(f, type);
		// 本地头像已经下载,但是头像不存在
		if (bitmap == null && fileCache.isLocationUrl(url) && type == 1) {
			return getHeaderBitmap(sex);
		}
		if (bitmap != null)
			return bitmap;

		String URL = null;
		// 头像和图片下载
		if (type == 1) {
			URL = downPath + url;
		} else if (type == 2) {
			String[] path = url.split("&");
			URL = downPath + path[0];
		} else if (type == 3) {
			String[] locations = url.split("&");
			if (locations != null && locations.length >= 2) {
				URL = ApiService.getGoogleMapUrl(locations[0], locations[1]);
			} else {
				return null;
			}
		}
		try {
			URL imageUrl = new URL(URL);
			HttpURLConnection conn = (HttpURLConnection) imageUrl
					.openConnection();
			conn.setConnectTimeout(30000);
			conn.setReadTimeout(30000);
			conn.setInstanceFollowRedirects(true);
			InputStream is = conn.getInputStream();
			OutputStream os = new FileOutputStream(f);
			Utils.CopyStream(is, os);
			os.close();
			bitmap = decodeFile(f, type);
			return bitmap;
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	/**
	 * 
	 * 解析下载图片,将图片保存的内存卡 //decodes image and scales it to reduce memory
	 * consumption
	 * 
	 * @param f
	 * @return
	 */
	private Bitmap decodeFile(File f, int type) {
		FileInputStream fileInputStream;
		try {
			// decode image size
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(fileInputStream=new FileInputStream(f), null, o);
			if(fileInputStream != null)fileInputStream.close();
			int REQUIRED_SIZE = 60;
			if (type == 1) {

			} else if (type == 2) {//聊天图片大小
				REQUIRED_SIZE = 100;
			} else if (type == 3) {//地图图片大小
				REQUIRED_SIZE = 300;
			}
			// Find the correct scale value. It should be the power of 2.
			// final int REQUIRED_SIZE=60;
			int width_tmp = o.outWidth, height_tmp = o.outHeight;
			int maxSize = Math.max(width_tmp, height_tmp);
			int scale = 1;
			while (true) {
				if (maxSize / 2 < REQUIRED_SIZE)
					break;
				maxSize /= 2;
				scale *= 2;
				/*
				 * if(width_tmp/2<REQUIRED_SIZE || height_tmp/2<REQUIRED_SIZE)
				 * break; width_tmp/=2; height_tmp/=2; scale*=2;
				 */
			}

			// decode with inSampleSize
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize = scale;

			Bitmap bitmap = BitmapFactory.decodeStream(fileInputStream = new FileInputStream(f),
					null, o2);
			if(fileInputStream != null)fileInputStream.close();
			if (type == 1 && bitmap != null)
				bitmap = ImageUtils.toRoundCorner(bitmap, 5);
			return bitmap;
		} catch (FileNotFoundException e) {
			
		} catch(IOException e){
			
		}
		return null;
	}

	/**
	 * 图片下载类
	 * 
	 * @author zhao
	 */
	private class PhotoToLoad {
		public String sex;
		public int type;
		public String url;
		public ImageView imageView;

		public PhotoToLoad(int type, String u, String sex, ImageView i) {
			url = u;
			imageView = i;
			this.type = type;
			this.sex = sex;
		}
	}

	/**
	 * 
	 * 图片异步类下载
	 * 
	 * @author zhao
	 * 
	 */
	class PhotosLoader implements Runnable {
		PhotoToLoad photoToLoad;

		PhotosLoader(PhotoToLoad photoToLoad) {
			this.photoToLoad = photoToLoad;
		}

		@Override
		public void run() {
			if (imageViewReused(photoToLoad))return;

			Bitmap bmp = getBitmap(photoToLoad.url, photoToLoad.type,
					photoToLoad.sex);

			if (bmp == null && photoToLoad.type == 2) {
				bmp = getDownFailImage();
			}

			memoryCache.put(photoToLoad.url, bmp);
			
			if (imageViewReused(photoToLoad))
				return;

			BitmapDisplayer bd = new BitmapDisplayer(bmp, photoToLoad);

			Activity a = (Activity) photoToLoad.imageView.getContext();

			a.runOnUiThread(bd);
		}
	}

	/**
	 * 
	 * 判断当前的下载地址,是否正在下载
	 * 
	 * @param photoToLoad
	 * @return 正在下载返回true,否则返回false
	 */
	boolean imageViewReused(PhotoToLoad photoToLoad) {
		String tag = imageViews.get(photoToLoad.imageView);
		if (tag == null || !tag.equals(photoToLoad.url))
			return true;
		return false;
	}

	/**
	 * 显示bitmap运行在主线程上
	 * 
	 * @author zhao
	 * 
	 */
	class BitmapDisplayer implements Runnable {
		Bitmap bitmap;
		PhotoToLoad photoToLoad;

		public BitmapDisplayer(Bitmap b, PhotoToLoad p) {
			bitmap = b;
			photoToLoad = p;
		}

		public void run() {
			if (imageViewReused(photoToLoad))
				return;

			if (bitmap != null) {
				bitmap = ImageUtils.toRoundCorner(bitmap, 5);
				photoToLoad.imageView.setImageBitmap(bitmap);
			} else {
				getDefutImage(photoToLoad.type, photoToLoad.imageView,
						photoToLoad.sex);
			}
		}
	}

	public void clearCache() {
		executorService.shutdown();
		memoryCache.clear();
		executorService = null;
	}

	/**
	 * 关闭正在下载的线程
	 */
	public void clearDowloading() {
		executorService.shutdown();
	}
	

}
