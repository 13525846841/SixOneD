package com.yksj.consultation.setting;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.library.base.base.BaseActivity;
import com.yksj.consultation.sonDoc.R;
import com.yksj.healthtalk.entity.CustomerInfoEntity;
import com.yksj.healthtalk.net.http.ApiService;
import com.yksj.healthtalk.net.http.JsonHttpResponseHandler;
import com.yksj.healthtalk.net.socket.SmartFoxClient;
import com.yksj.healthtalk.utils.ToastUtil;
import com.yksj.healthtalk.utils.Utils;
import com.yksj.healthtalk.utils.WheelUtils;

import org.json.JSONObject;
import org.universalimageloader.core.DisplayImageOptions;
import org.universalimageloader.core.ImageLoader;
import org.universalimageloader.core.assist.FailReason;
import org.universalimageloader.core.assist.ImageLoadingListener;
import org.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.library.base.utils.StorageUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

//import com.yksj.healthtalk.function.share.OneClickShare;

/**
 * 六一健康二维码
 * 
 * @author root
 * 
 */
public class SettingQrCodeActivity extends BaseActivity implements
		OnClickListener {
	PopupWindow mPopupWindow;
	private ImageView mCodeImageView;
	private Bitmap codeBitmap;
	private Button saveBtn;
//	private final int SAVAIMAGE = 1;
//	private final int SHARESINA = 2;
//	private final int SHARETENCENT = 3;
	private TextView title;
	private Button shareToTencent;
	private Button shareToSina;
	private String imagePath;
	private int type;// 0是个人 1 是商户
	private String id;
	private Bitmap icon;
	private TextView message;
	private LinearLayout parent;
	private  ImageLoader mImageLoader;
	private ImageView mCenterIv;
	public  DisplayImageOptions maleImageOptions;//男
	public  DisplayImageOptions femalImageOptions;//女
	public  DisplayImageOptions unkounwImageOptions;//未知
	public Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			if (msg.arg1 == -1 ) {
				createQRCodeBitmapWithPortrait(codeBitmap, Bitmap.createScaledBitmap(icon, 62,62, true));
			}else {
				createQRCodeBitmapWithPortrait(codeBitmap, Bitmap.createScaledBitmap((Bitmap) msg.obj, 62,62, true));
			}
			super.handleMessage(msg);
		}
	};
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.my_zxing);
		initWidget();
		initData();
	}

	private void initData() {
		mImageLoader = ImageLoader.getInstance();
		Intent intent = getIntent();
		if (intent.hasExtra("type")) {
			type = intent.getIntExtra("type", 0);
		}
		if (type == 1 || type == 2) {
			message.setVisibility(View.GONE);
		}
		if (intent.hasExtra("id")) {
			id = intent.getExtras().getString("id");
		}
		if (intent.hasExtra("title")) {
			titleTextV.setText(intent.getStringExtra("title"));
		} else {
			titleTextV.setText(R.string.my_zxing);
		}
//		titleRightBtn2.setOnClickListener(this);
//		titleRightBtn2.setText("选项");
		final CustomerInfoEntity entity = SmartFoxClient.getLoginUserInfo();
		ApiService.doHttpHZGenerateDimensionalCode(type, id,
				new JsonHttpResponseHandler(this) {
					@Override
					public void onSuccess(int statusCode, JSONObject response) {
						try {
							if (response != null && response.has("error_code")) {
								ToastUtil.showToastPanl(
										response.getString("error_message"));
							} else if (response != null
									&& response.has("content")) {
								Bitmap loadedImage = Utils.Create2DCode(response.getString("content"));
//								titleRightBtn2.setVisibility(View.VISIBLE);
								imagePath = response.getString("icon");
								codeBitmap = loadedImage.copy(Bitmap.Config.ARGB_8888, true);
								if (type == 0) {
									displayImage(entity.getSex(), entity.getNormalHeadIcon(), mCenterIv,new ImageLoadingListener() {

										@Override
										public void onLoadingStarted() {
											// TODO Auto-generated method stub
										}

										@Override
										public void onLoadingFailed(FailReason failReason) {
											Message message =handler.obtainMessage();
											message.arg1 = -1;
											handler.sendMessage(message);
										}

										@Override
										public void onLoadingComplete(final Bitmap loadedImage) {
											Message message =handler.obtainMessage();
											message.arg1 = 1;
											message.obj = loadedImage;
											handler.sendMessage(message);
										}

										@Override
										public void onLoadingCancelled() {
											// TODO Auto-generated method stub
//											System.out.println();
										}
									});
								}else {
									icon = BitmapFactory.decodeResource(
											getResources(), R.drawable.zx_icon)
											.copy(Bitmap.Config.ARGB_8888, true);;
									createQRCodeBitmapWithPortrait(codeBitmap, icon);
								}
							}
						} catch (Exception e) {
						}
						super.onSuccess(statusCode, response);
					}
				});
	}
	
	/**
	 * 显示头像
	 * @param type 头像类型
	 * @param uri
	 * @param imageView
	 */
	public void displayImage(String type,String uri,ImageView imageView, ImageLoadingListener listener){
		if("1".equals(type)){//男
			mImageLoader.displayImage(uri, imageView, maleImageOptions,listener);
			icon = BitmapFactory.decodeResource(
					getResources(), R.drawable.default_head_mankind);
		}else if("2".equals(type)){//女
			mImageLoader.displayImage(uri, imageView, femalImageOptions,listener);
			icon = BitmapFactory.decodeResource(
					getResources(), R.drawable.default_head_female);
		}else{//未知
			mImageLoader.displayImage(uri, imageView, unkounwImageOptions,listener);
			icon = BitmapFactory.decodeResource(
					getResources(), R.drawable.default_head_mankind);
		}
	}

	private void initWidget() {
		initializeTitle();
		mCodeImageView = (ImageView) findViewById(R.id.code_iv);
		message = (TextView) findViewById(R.id.message);
		titleLeftBtn.setOnClickListener(this);
		mCenterIv = (ImageView)findViewById(R.id.center_icon);
		parent=(LinearLayout) findViewById(R.id.qr_code_layout);
		
		maleImageOptions = new DisplayImageOptions.Builder(this)
		.showStubImage(R.drawable.default_head_mankind)
		.bitmapConfig(Bitmap.Config.RGB_565)
		.cacheOnDisc(new File(StorageUtils.getHeadersPath()))
		.cacheInMemory()
		.displayer(new RoundedBitmapDisplayer(5))
		.build();
		
		femalImageOptions = new DisplayImageOptions.Builder(this)
		.showStubImage(R.drawable.default_head_female)
		.bitmapConfig(Bitmap.Config.RGB_565)
		.cacheOnDisc(new File(StorageUtils.getHeadersPath()))
		.cacheInMemory()
		.displayer(new RoundedBitmapDisplayer(5))
		.build();
		
		unkounwImageOptions = new DisplayImageOptions.Builder(this)
		.showStubImage(R.drawable.default_head_mankind)
		.bitmapConfig(Bitmap.Config.RGB_565)
		.cacheOnDisc(new File(StorageUtils.getHeadersPath()))
		.cacheInMemory()
		.displayer(new RoundedBitmapDisplayer(5))
		.build();
		
	}

	/**
	 * 在二维码上绘制头像
	 */
	private void createQRCodeBitmapWithPortrait(Bitmap resource, Bitmap result) {
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
		canvas.drawBitmap(result, Math.abs(w - w_2) / 2, Math.abs(h - h_2) / 2,
				paint);
		canvas.save();
		// 存储新合成的图片
		canvas.restore();
		codeBitmap = newBitmap;
		mCodeImageView.setImageBitmap(newBitmap);
//		mCodeImageView.setBackgroundResource(R.drawable.kuang);
		mCodeImageView.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				ToastUtil.showShort("sdf");
				return true;
			}
		});
		//保存
		saveMyBitmap(codeBitmap);
	}

	
	public void saveMyBitmap(Bitmap mBitmap){
		  File f = new File(StorageUtils.getImagePath() + SmartFoxClient.getLoginUserId()+".png");
		  try {
		   f.createNewFile();
		  } catch (IOException e) {
		   // TODO Auto-generated catch block
		  }
		  FileOutputStream fOut = null;
		  try {
		   fOut = new FileOutputStream(f);
		   mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
		  } catch (FileNotFoundException e) {
		   e.printStackTrace();
		  }
		  try {
		   fOut.flush();
		  } catch (Exception e) {
		   e.printStackTrace();
		  }
		  try {
		   fOut.close();
		  } catch (Exception e) {
		   e.printStackTrace();
		  }
		 }
	
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.title_back:
			onBackPressed();
			break;
		case R.id.title_right2:
			shareImage(getString(R.string.code_share_message));
			break;
//		case SAVAIMAGE:
//			if (null != codeBitmap) {
//				if (!StorageUtils.isSDMounted()) {
//					ToastUtil.showShort(getApplicationContext(), "内存卡不可用");
//				} else {
//					File file = StorageUtils.createQrFile();
//					if (StorageUtils.saveImageOnImagsDir(codeBitmap, file)) {
//						Toast.makeText(this, "已保存二维码到本地相册", Toast.LENGTH_LONG)
//								.show();
//						sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED,
//								Uri.parseDate("file://"
//										+ Environment
//												.getExternalStorageDirectory())));
//					} else {
//						ToastUtil
//								.showToastPanl("保存失败");
//					}
//				}
//			}
//			dissPopwindow();
//			break;
//		case SHARESINA:
//			shareImage(OpenManager.SINA_WEIBO,
//					getString(R.string.code_share_message));
//			break;
//		case SHARETENCENT:
//			shareImage(OpenManager.TENCENT_WEIBO,
//					getString(R.string.code_share_message));
//			break;
		case R.id.cancel:
			dissPopwindow();
			break;
		}

	}

	private void dissPopwindow() {
		if (mPopupWindow != null && mPopupWindow.isShowing()) {
			mPopupWindow.dismiss();
			mPopupWindow = null;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		dissPopwindow();
		if (icon != null) {
			icon.recycle();
			icon = null;
		}
		
		if (codeBitmap != null) {
			codeBitmap.recycle();
			codeBitmap = null;
		}
	}

	/**
	 * 分享弹出框
	 */
	public void showuploadPopWindow() {
		LayoutInflater inflater = LayoutInflater.from(this);
		View view = inflater.inflate(R.layout.interest_image_add_action, null);
		View mainView = inflater.inflate(R.layout.interest_content, null);
		if (mPopupWindow == null) {
			mPopupWindow = new PopupWindow(view, LayoutParams.FILL_PARENT,
					LayoutParams.WRAP_CONTENT);
		}
		WheelUtils.setPopeWindow(this, mainView, mPopupWindow);
		title = (TextView) view.findViewById(R.id.title);
		shareToSina = (Button) view.findViewById(R.id.cameraadd);
		shareToTencent = (Button) view.findViewById(R.id.extraadd);
		saveBtn = (Button) view.findViewById(R.id.galleryadd);
		Button cancel = (Button) view.findViewById(R.id.cancel);
		title.setText(R.string.app_name);

//		saveBtn.setText("保存到本地");
//		saveBtn.setId(SAVAIMAGE);
//		shareToSina.setText("分享到新浪微博");
//		shareToSina.setId(SHARESINA);
//
//		shareToTencent.setText("分享到腾讯微博");
//		shareToTencent.setId(SHARETENCENT);
//		shareToTencent.setVisibility(View.VISIBLE);

		saveBtn.setOnClickListener(this);
		shareToTencent.setOnClickListener(this);
		shareToSina.setOnClickListener(this);
		cancel.setOnClickListener(this);
	}

	/**
	 * 分享图片+文字
	 * @param
	 * @param
	 * @param txt
	 */
	public void shareImage(String txt) {
//		OneClickShare ocs=new OneClickShare(this,parent);
//		ocs.setNotification(R.drawable.launcher_logo, getString(R.string.app_name));
//		ocs.disableSSOWhenAuthorize();
//		if (StorageUtils.isSDMounted()) {
//			String dir = StorageUtils.getImagePath();
//			if (TextUtils.isEmpty(imagePath)) {
//				ToastUtil.showShort(getApplicationContext(),
//						R.string.canot_find_image);
//			}
//			 File file = new File(dir + SmartFoxClient.getLoginUserId()+".png");
//
//			if (file.exists()) {
//				if (txt.length() > 65)
//					txt = txt.substring(0, 65);
//				txt = String.format(AppData.ShareContent, txt);
//				ocs.setText(txt);
//				ocs.setImagePath(file.getAbsolutePath());
//				ocs.show();
//			} else {
//				ToastUtil.showShort(getApplicationContext(), "图片不存在...");
//			}
//		} else {
//			ToastUtil.showShort(getApplicationContext(), "对不起,手机内存不足,请及时处理");
//		}
	}
}
