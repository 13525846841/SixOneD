package com.yksj.consultation.sonDoc.consultation;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.yksj.consultation.dialog.WaitDialog;
import com.yksj.consultation.utils.CropUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.universalimageloader.core.DefaultConfigurationFactory;
import org.universalimageloader.core.DisplayImageOptions;
import org.universalimageloader.core.ImageLoader;
import com.library.base.utils.StorageUtils;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.library.base.base.BaseActivity;
import com.yksj.consultation.comm.DoubleBtnFragmentDialog;
import com.yksj.consultation.comm.DoubleBtnFragmentDialog.OnDilaogClickListener;
import com.yksj.consultation.comm.SingleBtnFragmentDialog;
import com.yksj.consultation.comm.SingleBtnFragmentDialog.OnClickSureBtnListener;
import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.app.AppContext;
import com.library.base.widget.CircleImageView;
import com.yksj.healthtalk.net.http.ApiService;
import com.yksj.healthtalk.net.http.AsyncHttpResponseHandler;
import com.yksj.healthtalk.net.http.RequestParams;
import com.yksj.healthtalk.net.socket.SmartFoxClient;
import com.yksj.healthtalk.utils.BitmapUtils;
import com.yksj.healthtalk.utils.SystemUtils;
import com.yksj.healthtalk.utils.ToastUtil;
import com.yksj.healthtalk.utils.WheelUtils;

/**
 * @author HEKL
 * 
 *         会诊专家资讯窗口 发布(FLAG=0) 编辑(FLAG=1)
 */
public class DAtyConsultMessageEdit extends BaseActivity implements OnClickListener, OnEditorActionListener {
	private EditText mEditTitle, mEditContent;// 标题和内容
	private Button btnDelete;// 删除服务按钮
	private TextView mTextView;// 字数统计
	private ImageView mImagePic;// 添加图片按钮
	private CircleImageView mCircleImageView;// 显示圆形图片
	private CheckBox mDoctor, mPatient;
	private PopupWindow mPopupWindow;// 上传图片的弹出框
	private ImageLoader mImageLoader;// 图片加载器
	private WaitDialog dialog;
	private List<String> urlList = new ArrayList<String>();// 所有图片路径,便于点击
	public static final int PHOTO_PICKED_WITH_DATA = 1;// 相册标志
	public static final int CAMERA_REQUESTCODE = 3;// 相机标志
	private final int WHAT_FAILE = -1;// 上传失败标记
	private final int WHAT_SUCC = 1;// 上传成功标记
	private boolean isRelease;// 是否正在发布,如果在发布,不可以继续点击
	private boolean haseFile = false;// 图片文件是否存在，默认不存在
	private int mContentCount = 1000;// 字数限制
	private int textNumber = 0;
	private int FLAG;// 由编辑跳入还是发布
	private int VISIBLE_FLAG = 0;// 给谁看
	private int infoId = 0;
	private File storageFile, tempFile, headerFile;
	private JSONObject contentObject;// 消息详情内容JSONObject
	private JSONArray jArray, array;

	// 发布成功或失败的处理
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			// 失败异常
			case WHAT_FAILE:
				WaitDialog.dismiss(getSupportFragmentManager());
				if (msg.obj != null) {
					SingleBtnFragmentDialog.showDefault(getSupportFragmentManager(), String.valueOf(msg.obj));
				} else {
					ToastUtil.showShort(DAtyConsultMessageEdit.this, R.string.groupNewFail);
				}
				break;
			// 上传成功
			case WHAT_SUCC:
				SingleBtnFragmentDialog.showDefault(getSupportFragmentManager(),
						getResources().getString(R.string.dynimicmess_publish_hint), new OnClickSureBtnListener() {
							@Override
							public void onClickSureHander() {
								setResult(20);// 回跳后标志
								DAtyConsultMessageEdit.this.finish();
							}
						});
				break;
			}
			super.handleMessage(msg);
		}
	};

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.message_edit_activity_layout);
		initView();
	}

	private void initView() {
		initializeTitle();
		infoId = getIntent().getIntExtra("infoId", 0);
		FLAG = getIntent().getIntExtra("flag", 0);
		// 动态消息标题及内容
		mCircleImageView = (CircleImageView) findViewById(R.id.image_illpic);
		mImagePic = (ImageView) findViewById(R.id.image_illpicbg);
		mEditContent = (EditText) findViewById(R.id.et_message);
		mTextView = (TextView) findViewById(R.id.tv_textcount);
		mEditTitle = (EditText) findViewById(R.id.et_title);
		mDoctor = (CheckBox) findViewById(R.id.cb_doctor);
		mPatient = (CheckBox) findViewById(R.id.cb_patient);
		btnDelete = (Button) findViewById(R.id.btn_delete);
		titleRightBtn2.setVisibility(View.VISIBLE);
		mEditContent.setOnEditorActionListener(this);
		mEditTitle.setOnEditorActionListener(this);
		mCircleImageView.setOnClickListener(this);
		titleRightBtn2.setOnClickListener(this);
		titleLeftBtn.setOnClickListener(this);
		btnDelete.setOnClickListener(this);
		mImagePic.setOnClickListener(this);
		mEditContent.addTextChangedListener(textWatcher);
		mImageLoader = ImageLoader.getInstance();
		mTextView.setText("0/" + mContentCount);
		if (FLAG == 1) {
			titleTextV.setText("编辑消息");
			titleRightBtn2.setText("完成");
			btnDelete.setVisibility(View.VISIBLE);
			mTextView.setVisibility(View.GONE);
			mImagePic.setVisibility(View.GONE);
			mCircleImageView.setVisibility(View.VISIBLE);
			initData();// 加载消息内容
		} else {
			titleTextV.setText("发布消息");
			titleRightBtn2.setText("完成");
			btnDelete.setVisibility(View.GONE);
		}
	}

	@Override
	public void onClick(View v) {
		Intent intent;
		switch (v.getId()) {
		case R.id.title_back:// 回退
			onBackPressed();
			break;
		case R.id.title_right2:
			if (isRelease)// 正在创建返回
				return;
			if (FLAG == 1) {// 编辑动态
				if (!hasInputBaseInfoEdit()) {
					isRelease = false;
					return;
				} else {
					DoubleBtnFragmentDialog.showDefault(getSupportFragmentManager(), "您确认提交修改吗？", "取消", "确定",
							new OnDilaogClickListener() {
								@Override
								public void onDismiss(DialogFragment fragment) {
								
								}

								@Override
								public void onClick(DialogFragment fragment, View v) {
									editDynamicNews();
								}
							});
				}

			}

			if (FLAG == 0) {// 发布动态
				if (!hasInputBaseInfo()) {
					isRelease = false;
					return;
				} else {
					DoubleBtnFragmentDialog.showDefault(getSupportFragmentManager(), "您确认提交吗？", "取消", "确定",
							new OnDilaogClickListener() {
								@Override
								public void onDismiss(DialogFragment fragment) {

								}

								@Override
								public void onClick(DialogFragment fragment, View v) {
									releaseDynamicNews();
								}
							});
				}
			}
			break;
		case R.id.image_illpicbg:
			showuploadPopWindow();
			break;
		case R.id.image_illpic:
			showuploadPopWindow();
			break;
		case R.id.galleryadd:// 从相册获取
			if (mPopupWindow.isShowing()) {
				mPopupWindow.dismiss();
			}
			try {
				if (!SystemUtils.getScdExit()) {
					ToastUtil.showSDCardBusy();
					return;
				}
				intent = CropUtils.createPickForFileIntent();
				startActivityForResult(intent, PHOTO_PICKED_WITH_DATA);
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		case R.id.cameraadd:// 相机拍照
			if (mPopupWindow.isShowing()) {
				mPopupWindow.dismiss();
			}
			if (!SystemUtils.getScdExit()) {
				ToastUtil.showSDCardBusy();
				return;
			}
			try {
				storageFile = StorageUtils.createImageFile();
				Uri outUri = Uri.fromFile(storageFile);
				intent = CropUtils.createPickForCameraIntent(outUri);
				startActivityForResult(intent, CAMERA_REQUESTCODE);
			} catch (Exception e) {
				e.printStackTrace();
				ToastUtil.showCreateFail();
			}
			break;
		case R.id.cancel:// 取消
			if (mPopupWindow != null && mPopupWindow.isShowing()) {
				mPopupWindow.dismiss();
			}
			break;
		case R.id.btn_delete:// 删除服务
			DoubleBtnFragmentDialog.showDefault(getSupportFragmentManager(), "您确认要删除该消息吗？", "取消", "确定",
					new OnDilaogClickListener() {

						@Override
						public void onDismiss(DialogFragment fragment) {
							
						}

						@Override
						public void onClick(DialogFragment fragment, View v) {
							deleteMessage();
						}
					});
			break;

		}

	}

	// 发布动态消息
	private void releaseDynamicNews() {
		if (mDoctor.isChecked() && (!mPatient.isChecked())) {
			VISIBLE_FLAG = 20;
		}
		if ((!mDoctor.isChecked()) && mPatient.isChecked()) {
			VISIBLE_FLAG = 30;
		}
		if (mDoctor.isChecked() && mPatient.isChecked()) {
			VISIBLE_FLAG = 10;
		}
		isRelease = true;
		InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(DAtyConsultMessageEdit.this.getCurrentFocus().getWindowToken(),
				InputMethodManager.HIDE_NOT_ALWAYS);
		String title = mEditTitle.getText().toString();
		String content = mEditContent.getText().toString();
		JSONObject json = new JSONObject();
		try {
			json.put("INFO_NAME", title);
			json.put("VISIBLE_FLAG", VISIBLE_FLAG);
			json.put("INFO_CONTENT", content);
			json.put("CUSTOMER_ID", SmartFoxClient.getLoginUserId());
			json.put("CONSULTATION_CENTER_ID", SmartFoxClient.getLoginUserInfo().getDoctorPosition());

		} catch (JSONException e2) {
			e2.printStackTrace();
		}

		ApiService.doHttpDynamicNewsRelease(getApplicationContext(), json.toString(), headerFile,
				new AsyncHttpResponseHandler(DAtyConsultMessageEdit.this) {

					public void onSuccess(int statusCode, String content) {
						super.onSuccess(statusCode, content);
						try {
							if (content != null && content.contains("error_message")) {
								isRelease = false;// 已经创建失败了,将其设置为false可以继续创建
								JSONObject jsonObject = new JSONObject(content);
								ToastUtil.showToastPanl(jsonObject.optString("error_message"));
							} else {
								// 是否成功
								isRequestSuccess(content);
							}
						} catch (Exception e) {
						}
					}

					@Override
					public void onStart() {
						titleRightBtn.setClickable(false);
						super.onStart();
					}

					@Override
					public void onFinish() {
						titleRightBtn.setClickable(true);
						super.onFinish();
					}

					@Override
					public void onFailure(Throwable error, String content) {
						isRelease = false;// 已经创建失败了,将其设置为false可以继续创建
						super.onFailure(error, content);
					}
				});
	}

	// 编辑动态消息
	private void editDynamicNews() {
		isRelease = true;
		InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(DAtyConsultMessageEdit.this.getCurrentFocus().getWindowToken(),
				InputMethodManager.HIDE_NOT_ALWAYS);
		if (mDoctor.isChecked() && (!mPatient.isChecked())) {
			VISIBLE_FLAG = 20;
		}
		if ((!mDoctor.isChecked()) && mPatient.isChecked()) {
			VISIBLE_FLAG = 30;
		}
		if (mDoctor.isChecked() && mPatient.isChecked()) {
			VISIBLE_FLAG = 10;
		}
		String title = mEditTitle.getText().toString();
		String content = mEditContent.getText().toString();
		JSONObject json = new JSONObject();
		try {
			json.put("CONSULTATION_CENTER_ID", AppContext.APP_CONSULTATION_CENTERID);
			json.put("INFO_NAME", title);
			json.put("VISIBLE_FLAG", VISIBLE_FLAG);
			json.put("INFO_ID", contentObject.optInt("INFO_ID"));
			json.put("INFO_PICTURE", contentObject.optString("INFO_PICTURE").toString());
			json.put("SHARE_COUNT", contentObject.optInt("SHARE_COUNT"));
			json.put("COMMENT_COUNT", contentObject.optInt("COMMENT_COUNT"));
			json.put("FORWARD_COUNT", contentObject.optInt("FORWARD_COUNT"));
			json.put("PRAISE_COUNT", contentObject.optInt("PRAISE_COUNT"));
			json.put("PUBLISH_TIME", contentObject.optString("PUBLISH_TIME").toString());
			json.put("STATUS_TIME", contentObject.optString("STATUS_TIME").toString());
			json.put("INFO_STATUS", contentObject.optInt("INFO_STATUS"));
			json.put("CUSTOMER_ID", SmartFoxClient.getLoginUserId());
			json.put("CON", contentObject.optInt("CON"));

			JSONObject nameObj = (JSONObject) jArray.get(0);
			nameObj.put("INFO_ID", nameObj.optInt("INFO_ID"));
			nameObj.put("CONTENT_TYPE", nameObj.optInt("CONTENT_TYPE"));
			nameObj.put("INFO_CONTENT", content);
			nameObj.put("BIG_PICTURE", nameObj.optString("BIG_PICTURE").toString());
			nameObj.put("RECORD_TIME", nameObj.optString("RECORD_TIME").toString());

			JSONObject nameObj2 = (JSONObject) jArray.get(1);
			nameObj2.put("INFO_ID", nameObj2.optInt("INFO_ID"));
			nameObj2.put("CONTENT_TYPE", nameObj2.optInt("CONTENT_TYPE"));
			nameObj2.put("INFO_CONTENT", content);
			nameObj2.put("SMALL_PICTURE", nameObj2.optString("SMALL_PICTURE").toString());
			nameObj2.put("BIG_PICTURE", nameObj2.optString("BIG_PICTURE").toString());
			nameObj2.put("RECORD_TIME", nameObj2.optString("BIG_PICTURE").toString());
			json.put("CONTENTLIST", jArray);

		} catch (JSONException e2) {
			e2.printStackTrace();
		}

		ApiService.doHttpEditMessage(getApplicationContext(), json.toString(), headerFile,
				new AsyncHttpResponseHandler(DAtyConsultMessageEdit.this) {

					public void onSuccess(int statusCode, String content) {
						super.onSuccess(statusCode, content);
						try {
							if (content != null && content.contains("error_message")) {
								isRelease = false;// 已经创建失败了,将其设置为false可以继续创建
								JSONObject jsonObject = new JSONObject(content);
								ToastUtil.showToastPanl(jsonObject.optString("error_message"));
							} else {
								// 是否成功
								isRequestSuccess(content);
							}
						} catch (Exception e) {
						}
					}

					@Override
					public void onStart() {
						titleRightBtn.setClickable(false);
						super.onStart();
					}

					@Override
					public void onFinish() {
						titleRightBtn.setClickable(true);
						super.onFinish();
					}

					@Override
					public void onFailure(Throwable error, String content) {
						isRelease = false;// 已经创建失败了,将其设置为false可以继续创建
						super.onFailure(error, content);
					}
				});
	}

	// 是否发布成功
	private void isRequestSuccess(String content) {
		if (dialog != null) {
			dialog.dismissAllowingStateLoss();
			dialog = null;
		}
		if (content == null || "".equals(content) || !content.contains("{")) {
			Message message = handler.obtainMessage();
			message.what = WHAT_FAILE;
			message.obj = content;
			handler.sendMessage(message);
			return;
		} else {
			// if (path != null) {
			// ImageUtils.deleBitmap(path);
			// }
			Message message = handler.obtainMessage();
			message.obj = content;
			message.what = WHAT_SUCC;
			handler.sendMessage(message);
		}
	}

	// 判断是否已经填写信息
	public boolean hasInputBaseInfo() {
		String title = mEditTitle.getText().toString();
		String content = mEditContent.getText().toString();
		if (headerFile == null) {// 没有图片
			ToastUtil.showLong(this, R.string.contentpic);
			return false;
		}
		if ((!mDoctor.isChecked()) && (!mPatient.isChecked())) {
			ToastUtil.showLong(this, R.string.whocansee);
			return false;
		}
		if ("".equals(title.trim())) {
			ToastUtil.showLong(this, R.string.please_fill_title);
			return false;
		}
		if (title.trim().length() > 20) {// 标题小于20
			ToastUtil.showLong(this, R.string.titletoomuch);
			return false;
		}
		if ("".equals(content.trim())) {
			ToastUtil.showLong(this, R.string.pleasecontent);
			return false;
		}
		return true;
	}

	// 判断编辑时是否已经填写信息
	public boolean hasInputBaseInfoEdit() {
		String title = mEditTitle.getText().toString();
		String content = mEditContent.getText().toString();
		if ("".equals(title.trim())) {
			ToastUtil.showLong(this, R.string.please_fill_title);
			return false;
		}
		if ((!mDoctor.isChecked()) && (!mPatient.isChecked())) {
			ToastUtil.showLong(this, R.string.whocansee);
			return false;
		}
		if (title.trim().length() > 20) {// 标题小于20
			ToastUtil.showLong(this, R.string.titletoomuch);
			return false;
		}
		if ("".equals(content.trim())) {
			ToastUtil.showLong(this, R.string.pleasecontent);
			return false;
		}
		return true;
	}

	/**
	 * 加载消息内容
	 */
	private void initData() {
		RequestParams params = new RequestParams();
		params.put("TYPE", "findConsuInfo");
		params.put("CONSULTATION_CENTER_ID", AppContext.APP_CONSULTATION_CENTERID);
		params.put("INFOID", infoId + "");
		params.put("CUSTOMERID", SmartFoxClient.getLoginUserId());
		ApiService.doHttpGroupConsultationList(params, new AsyncHttpResponseHandler(this) {

			@Override
			public void onSuccess(String content) {
				super.onSuccess(content);
				try {
					JSONObject jsonObject = new JSONObject(content);
					if (jsonObject.has("findConsuInfo")) {
						array = jsonObject.getJSONArray("findConsuInfo");
						contentObject = array.getJSONObject(0);
						onParseData();// 适配数据
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

		});

	}

	/**
	 * 删除动态消息
	 * 
	 * @param infoId
	 *            新闻Id
	 */
	private void deleteMessage() {
		ApiService.doHttpDeleteMessage(infoId + "", new AsyncHttpResponseHandler(this) {
			@Override
			public void onSuccess(int statusCode, String content) {
				try {
					JSONObject obj = new JSONObject(content);
					if (content.contains("error_message")) {
						ToastUtil.showShort(obj.getString("error_message"));
					} else {
						ToastUtil.showShort(obj.getString("message"));
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				super.onSuccess(statusCode, content);
			}

			@Override
			public void onFinish() {
				setResult(20);// 返回后显示界面
				finish();
				super.onFinish();
			}
		});
	}

	private void onParseData() {
		DisplayImageOptions mDisplayImageOptions = DefaultConfigurationFactory.createGalleryDisplayImageOptions(this);
		mEditTitle.setText(contentObject.optString("INFO_NAME"));
		switch (contentObject.optInt("VISIBLE_FLAG")) {// 给谁看的状态
		case 10:
			mDoctor.setChecked(true);
			mPatient.setChecked(true);
			break;
		case 20:
			mDoctor.setChecked(true);
			mPatient.setChecked(false);
			break;
		case 30:
			mDoctor.setChecked(false);
			mPatient.setChecked(true);
			break;
		}
		// mImageLoader.displayImage(contentObject.optString("INFO_PICTURE"),
		// mImageView, mDisplayImageOptions);
		// mImageView.setOnClickListener(this);
		// 数组格式,为了实现图文混排,但是现在只考虑一张图片一段文字,只不过格式不变
		try {
			jArray = contentObject.getJSONArray("CONTENTLIST");
			for (int i = 0; i < jArray.length(); i++) {
				JSONObject object = jArray.getJSONObject(i);
				if (object.optInt("CONTENT_TYPE") == 10) {// 文字
					mEditContent.setText(object.optString("INFO_CONTENT"));
				} else if (object.optInt("CONTENT_TYPE") == 20) {// 图片
					urlList.add(object.optString("INFO_PICTURE"));
					mImageLoader.displayImage(contentObject.getString("INFO_PICTURE"), mCircleImageView,
							mDisplayImageOptions);
					// mImageLoader.displayImage(object.optString("BIG_PICTURE"),
					// mCircleImageView, mDisplayImageOptions);
					mImageView.setOnClickListener(this);
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case CAMERA_REQUESTCODE:// 相机拍照
			if (resultCode == Activity.RESULT_OK) {
				String strFilePath = storageFile.getAbsolutePath();
				onHandlerCropImage(strFilePath);
			}
			break;
		case PHOTO_PICKED_WITH_DATA:// 从相册获取
			if (resultCode == Activity.RESULT_OK) {
				if (data != null) {
					Uri uri = data.getData();
					String scheme = uri.getScheme();
					String strFilePath = null;// 图片地址
					// url类型content or file
					if ("content".equals(scheme)) {
						strFilePath = getImageUrlByAlbum(uri);
					} else {
						strFilePath = uri.getPath();
					}
					onHandlerCropImage(strFilePath);
				}
			}
			break;
		case 3002:// 裁剪后获取结果
			if (resultCode == this.RESULT_OK) {// 裁剪成功
				Bitmap bitmap = BitmapUtils.decodeBitmap(tempFile.getAbsolutePath(), CropUtils.HEADER_WIDTH,
						CropUtils.HEADER_HEIGTH);
				mCircleImageView.setImageBitmap(bitmap);
				haseFile = true;// 已经有图片了
				headerFile = tempFile;// 头像文件指向临时头像文件
				mCircleImageView.setVisibility(View.VISIBLE);
				mImagePic.setVisibility(View.GONE);
			} else {// 裁剪失败或者取消裁剪
				if (!haseFile) {// 还没有图片
					if (tempFile != null)
						tempFile.deleteOnExit();
					tempFile = null;
				}
			}
			break;
		}
	}

	/**
	 * 文字监听
	 */
	private TextWatcher textWatcher = new TextWatcher() {
		@Override
		public void afterTextChanged(Editable s) {

		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			if ((start + count) <= mContentCount && mEditContent.getText().toString().length() <= mContentCount) {
				textNumber = mEditContent.getText().toString().length();
				mTextView.setText(textNumber + "/" + mContentCount);
			} else {
				mEditContent.setText(s.subSequence(0, mContentCount));
				ToastUtil.showShort(DAtyConsultMessageEdit.this, "最多可输入" + mContentCount + "个字符");
			}
		}
	};

	/**
	 * 弹出上传图片的选择布局
	 */
	public void showuploadPopWindow() {
		LayoutInflater inflater = LayoutInflater.from(this);
		View view = inflater.inflate(R.layout.interest_image_add_action, null);
		View mainView = inflater.inflate(R.layout.interest_content, null);
		if (mPopupWindow == null) {
			mPopupWindow = new PopupWindow(view, LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
			mPopupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
		}
		WheelUtils.setPopeWindow(this, mainView, mPopupWindow);
		Button cameraAdd = (Button) view.findViewById(R.id.cameraadd);
		Button galleryAdd = (Button) view.findViewById(R.id.galleryadd);
		Button cancel = (Button) view.findViewById(R.id.cancel);
		cameraAdd.setOnClickListener(this);
		galleryAdd.setOnClickListener(this);
		cancel.setOnClickListener(this);
	}

	/**
	 * 图片裁剪
	 * 
	 * @param path
	 */
	private void onHandlerCropImage(String path) {
		if (!SystemUtils.getScdExit()) {
			ToastUtil.showSDCardBusy();
			return;
		}
		try {
			tempFile = StorageUtils.createHeaderFile();
			Uri outUri = Uri.fromFile(new File(path));
			Uri saveUri = Uri.fromFile(tempFile);
			Intent intent = CropUtils.createHeaderCropIntent(this, outUri, saveUri, true);
			startActivityForResult(intent, 3002);
		} catch (Exception e) {
			ToastUtil.showCreateFail();
		}
	}

	/**
	 * 根据uri查询相册所对应的图片地址
	 * 
	 * @param uri
	 * @return
	 */
	private String getImageUrlByAlbum(Uri uri) {
		String[] imageItems = { MediaStore.Images.Media.DATA };
		Cursor cursor = this.managedQuery(uri, imageItems, null, null, null);
		int index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();
		String path = cursor.getString(index);
		return path;
	}

	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		if (event == null)
			return false;
		return (event.getKeyCode() == KeyEvent.KEYCODE_ENTER);// 当用户输入回车是进行截获
	};
}
