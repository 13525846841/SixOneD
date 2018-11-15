package com.yksj.consultation.sonDoc.consultation;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.library.base.base.BaseActivity;
import com.yksj.consultation.comm.DoubleBtnFragmentDialog;
import com.yksj.consultation.comm.DoubleBtnFragmentDialog.OnDilaogClickListener;
import com.yksj.consultation.sonDoc.R;
import com.yksj.healthtalk.net.http.AsyncHttpResponseHandler;
import com.yksj.healthtalk.net.http.ApiService;
import com.yksj.healthtalk.net.http.RequestParams;
import com.yksj.healthtalk.utils.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;
import org.universalimageloader.core.DefaultConfigurationFactory;
import org.universalimageloader.core.DisplayImageOptions;
import org.universalimageloader.core.ImageLoader;

/**
 * @author HEKL
 * 
 *         评价界面
 */
public class PAtyConsultEvaluale extends BaseActivity implements OnClickListener {
	private TextView mTextName;//会诊医生姓名
	private TextView mTextType;//会诊医生职位/科室
	private TextView mTextHospital;//医院
	private EditText mEditText;//填写评价
	private ImageView mHeadView;// 头像
	private RatingBar mBar;// 评价星级
	private ImageLoader mImageLoader;
	private DisplayImageOptions mOptions;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.aty_consult_evaluate);
		initView();
	}

	public void initView() {
		initializeTitle();
		titleTextV.setText("评价");
		titleLeftBtn.setOnClickListener(this);
		mImageLoader = ImageLoader.getInstance();
		mOptions = DefaultConfigurationFactory.createSeniorDoctorDisplayImageOptions(this);
		mTextHospital = (TextView) findViewById(R.id.tv_hospital);
		mTextName = (TextView) findViewById(R.id.tv_assisName);
		mTextType = (TextView) findViewById(R.id.tv_type);
		mEditText = (EditText) findViewById(R.id.et_evaluate);
		mHeadView = (ImageView) findViewById(R.id.assistant_head);
		mBar = (RatingBar) findViewById(R.id.rb_speed);
		mBar.setStepSize(1f);
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.title_back:
			onBackPressed();
			break;
		case R.id.title_right2:
			if ((int) mBar.getRating() == 0 ) {
				ToastUtil.showShort("你还没有评价，不要偷懒哦，亲！");
			} else {
				DoubleBtnFragmentDialog.showDefault(getSupportFragmentManager(), "感谢您的评价，确定要提交吗？", "取消", "确定",
						new OnDilaogClickListener() {

							@Override
							public void onDismiss(DialogFragment fragment) {
							}

							@Override
							public void onClick(DialogFragment fragment, View v) {
//								sendData();
							}
						});
			}

			break;

		}
	}

	/**
	 * 发送评价
	 */
	private void sendData() {
		String str = mEditText.getText().toString();
		RequestParams params = new RequestParams();
		params.put("OPTION", "11");
//		params.put("CONSULTATIONID", conId + "");
		params.put("RESPONSELEVEL", (int) mBar.getRating() + "");// 星级
		params.put("CONTENT", str);
		ApiService.doHttpDoctorService(params, new AsyncHttpResponseHandler(this) {
			JSONObject obj = null;

			@Override
			public void onSuccess(int statusCode, String content) {
				try {
					obj = new JSONObject(content);
					if (content.contains("errorcode")) {
						ToastUtil.showShort(obj.getString("errormessage"));
					} else {
						ToastUtil.showShort(obj.getString("INFO"));
						setResult(20);
						finish();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				super.onSuccess(statusCode, content);
			}
		});
	}

	/**
	 * 显示评价
	 */
	private void showData() {
		RequestParams params = new RequestParams();
		params.put("OPTION", "10");
//		params.put("CONSULTATIONID", conId + "");
		ApiService.doHttpDoctorService(params, new AsyncHttpResponseHandler(this) {
			JSONObject obj = null;

			@Override
			public void onSuccess(int statusCode, String content) {
				try {
					obj = new JSONObject(content);
					if (content.contains("errorcode")) {
						ToastUtil.showShort(obj.getString("errormessage"));
					} else {
						mBar.setRating(obj.getInt("RESPONSE_LEVEL"));
						mTextName.setText(obj.getString("CUSTOMER_NICKNAME"));
						mImageLoader.displayImage(obj.getString("CLIENT_ICON_BACKGROUND"), mHeadView, mOptions);
						if ("".equals(obj.getString("OFFICE_NAME"))) {
							mTextHospital.setText(obj.getString("TITLE_NAME"));
						} else if (!("".equals(obj.getString("OFFICE_NAME")))) {
							mTextHospital.setText(obj.getString("TITLE_NAME") + "/" + obj.getString("OFFICE_NAME"));
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				super.onSuccess(statusCode, content);
			}
		});
	}

	/**
	 * 显示助理信息
	 */
	private void showDataOfAssistant() {
		RequestParams params = new RequestParams();
		params.put("OPTION", "10");
//		params.put("CONSULTATIONID", conId + "");
		ApiService.doHttpDoctorService(params, new AsyncHttpResponseHandler(this) {
			JSONObject obj = null;

			@Override
			public void onSuccess(int statusCode, String content) {
				try {
					obj = new JSONObject(content);
					if (!("1".equals(obj.getString("errorcode")))) {
						ToastUtil.showShort(obj.getString("errormessage"));
					} else {
						mTextName.setText(obj.getString("CUSTOMER_NICKNAME"));
						mImageLoader.displayImage(obj.getString("CLIENT_ICON_BACKGROUND"), mHeadView, mOptions);
						mTextHospital.setText(obj.getString("OFFICE_NAME"));
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				super.onSuccess(statusCode, content);
			}
		});
	}
}
