package com.yksj.consultation.doctor;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.library.base.base.BaseTitleActivity;
import com.yksj.consultation.business.LoginBusiness;
import com.yksj.consultation.caledar.CaledarViewFragment;
import com.yksj.consultation.caledar.CaledarViewFragment.OnItemClickCaladerListener;
import com.yksj.consultation.comm.DoubleBtnFragmentDialog.OnDilaogClickListener;
import com.yksj.consultation.comm.SingleBtnFragmentDialog;
import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.sonDoc.doctorstation.DoctorServiceAddTimeActivity;
import com.yksj.healthtalk.entity.CaledarObject;
import com.yksj.healthtalk.entity.DoctorServiceAddTimeEntity;
import com.yksj.healthtalk.net.http.ApiService;
import com.yksj.healthtalk.net.http.AsyncHttpResponseHandler;
import com.yksj.healthtalk.net.http.ObjectHttpResponseHandler;
import com.yksj.healthtalk.net.http.RequestParams;
import com.yksj.healthtalk.net.socket.SmartControlClient;
import com.yksj.healthtalk.net.socket.SmartFoxClient;
import com.yksj.healthtalk.utils.HStringUtil;
import com.yksj.healthtalk.utils.PopWindowUtil;
import com.yksj.healthtalk.utils.TimeUtil;
import com.yksj.healthtalk.utils.ViewFinder;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
/**
 * 预约咨询 门诊预约 (日历)
 * @author jack_tang
 *
 */
public class DoctorSeeServiceActivity extends BaseTitleActivity implements OnClickListener, OnItemClickCaladerListener, OnDilaogClickListener {

	CaledarViewFragment mFragmentView;
	String mServiceTypeId;//2预约时段   3预约面访
	private List<DoctorServiceAddTimeEntity> entitys;
	View serviceItemlayoutView;
	private LinearLayout mGroupView;
	private String PARAME="";//删除的数组数据
	Map<String,JSONArray> map = new LinkedHashMap<String,JSONArray>();
//	private View currentView;
	private String PERSON_MIN;
	private String PERSON_MAX;
	private String PRICE_MIN;
	private String PRICE_MAX;
	private String mPressDate;

	public static Intent getCallingIntent(Context context, String type, String title){
	    Intent intent = new Intent(context, DoctorSeeServiceActivity.class);
	    intent.putExtra("type", type);
	    intent.putExtra("titleName", title);
	    return intent;
	}

	@Override
	public int createLayoutRes() {
		return R.layout.doctor_see_service_layout;
	}

	@Override
	public void initialize(Bundle bundle) {
		super.initialize(bundle);
		initView();
		mFinder = new ViewFinder(this);
	}

	private void initView() {
		setRight("确认", this::onConfrim);
		setTitle(getIntent().getStringExtra("titleName"));
		mFragmentView = (CaledarViewFragment)getSupportFragmentManager().findFragmentByTag("calendar");
		mFragmentView.setOnItemClickListener(this);
		mGroupView = (LinearLayout) findViewById(R.id.line_view_group);
		if("3".equals(getIntent().getStringExtra("type"))){
//			 AnimationUtils.startGuiPager(this, getClass().getAccounts());
		}
//		initData();
	}

	/**
	 * 确认
	 * @param view
	 */
	private void onConfrim(View view) {
		Intent intent =new Intent(this,DoctorServiceAddTimeActivity.class);
		intent.putExtra("serviceType",mServiceTypeId);
		intent.putExtra("PERSON_MAX",PERSON_MAX);
		intent.putExtra("PERSON_MIN",PERSON_MIN);
		intent.putExtra("PRICE_MAX",PRICE_MAX);
		intent.putExtra("PRICE_MIN",PRICE_MIN);
		intent.putExtra("mPressDate",mPressDate);
		startActivityForResult(intent,1000);
	}

	private void initData() {
		RequestParams params = new RequestParams();
		params.put("Type", "queryRepeatTime");
		params.put("SERVICE_TYPE_ID",mServiceTypeId =getIntent().getStringExtra("type"));
		params.put("CUSTOMER_ID", LoginBusiness.getInstance().getLoginEntity().getId());
		ApiService.doHttpServiceSetServlet420(params, new ObjectHttpResponseHandler(this) {
			@Override
			public Object onParseResponse(String content) {
				Map<Calendar,String> dates=new HashMap<Calendar, String>();
				try {
					JSONObject data=new JSONObject(content);
					JSONObject object =data.optJSONObject("result");
					PERSON_MIN = object.optString("PERSON_MIN");
					PERSON_MAX = object.optString("PERSON_MAX");
					PRICE_MIN = object.optString("PRICE_MIN");
					PRICE_MAX = object.optString("PRICE_MAX");
					JSONArray optJSONArray = object.optJSONArray("list");
					entitys = DoctorServiceAddTimeEntity.parToList(optJSONArray.toString());
					SimpleDateFormat.getDateInstance(SimpleDateFormat.FULL, Locale.CHINA);
					SimpleDateFormat format1 = new SimpleDateFormat("yyyyMMdd");
					for (int i = 0; i < entitys.size(); i++) {
						try {
							//闲
							DoctorServiceAddTimeEntity entity = entitys.get(i);
							Calendar cal = Calendar.getInstance();
							cal.setTime(format1.parse(entity.getSERVICE_TIME_BEGIN().substring(0, 8)));
							dates.put(cal, CaledarObject.busy);
//							if("0".equals(entity.getISBUZY())){
//								dates.put(cal, CaledarObject.noBusy);
//							}else{
//								dates.put(cal, CaledarObject.busy);
//							}
						} catch (ParseException e) {
						}
					}
				} catch (Exception e) {
					return null;
				}
				return dates;
			}
			@Override
			public void onSuccess(Object dates) {
				super.onSuccess(dates);
				if(dates!=null)
				mFragmentView.addApplyDate((Map<Calendar, String>) dates);
				if(entitys==null ||entitys.size() == 0){
					findViewById(R.id.empty).setVisibility(View.VISIBLE);
				}else{
					findViewById(R.id.empty).setVisibility(View.GONE);
				}
				if(!HStringUtil.isEmpty(cacheDate)){
					updateBottomView(cacheDate);
				}else
					updateBottomView(TimeUtil.getNowDateFormat());
			}
		});
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		initData();
	}
	
	public void onBackPressed() {
		Intent intent2 = getIntent();
		intent2.putExtra("isUpdate", "isUpdate");
		setResult(RESULT_OK, getIntent());
		finish();
	};
	
	SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyyMMdd");
	private String TYPE;
	private String cacheDate;
	private ViewFinder mFinder;
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		CaledarObject descriptor= (CaledarObject) view.getTag();
		cacheDate = mDateFormat.format(descriptor.getDate().getTime());
		updateBottomView(cacheDate);
	}
	
	
	/**
	 * 更新下面布局
	 * @param calendar 日期 (20141012)
	 */
	private void updateBottomView(String calendar){
		if(HStringUtil.isEmpty(calendar)) return ;
		mFinder.setText(R.id.selectdate, TimeUtil.getFormatDate2(calendar)+"\t 已开通时段");
		mGroupView.removeAllViews();
		mPressDate =calendar;
		for (final DoctorServiceAddTimeEntity entity : entitys) {
			String sub = entity.getSERVICE_TIME_BEGIN().substring(0, 8);
			if(sub.equals(mPressDate)){
				final View itemView =getLayoutInflater().inflate(R.layout.doctor_see_service_item_layout,null);
				TextView time = (TextView) itemView.findViewById(R.id.time);
				TextView reply_flg = (TextView) itemView.findViewById(R.id.reply_flg);
				switch (Integer.valueOf(entity.getREPEAT_FLAG())) {
				case 0://永不
					reply_flg.setText("永不");
					break;
				case 1://每天
					reply_flg.setText("每天");
					break;
				case 2://工作日
					reply_flg.setText("工作日");
					break;
				case 3://每周
					reply_flg.setText("每周");
					break;
				case 4://自定义
					reply_flg.setText("自定义");
					break;
				}
				time.setText(TimeUtil.getTime2(entity.getSERVICE_TIME_BEGIN()) + "-" +TimeUtil.getTime2(entity.getSERVICE_TIME_END()));
				itemView.setTag(entity);
				itemView.findViewById(R.id.edit).setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						onEditService((DoctorServiceAddTimeEntity) itemView.getTag());
					}
				});
				itemView.findViewById(R.id.delete).setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						PopWindowUtil.newShowWindow(v, DoctorSeeServiceActivity.this,DoctorSeeServiceActivity.this.getLayoutInflater(),
								R.layout.window_delete_service_layout,new OnClickListener() {
									@Override
									public void onClick(View v) {
										switch (v.getId()) {
										case R.id.delete_current://删除当前时段
										deleteCurrentTime((DoctorServiceAddTimeEntity) itemView.getTag());
											break;
										case R.id.delete_repeat://删除重复时段
//											mDeleteCache = (DoctorServiceAddTimeEntity) itemView.getTag();
											deleteReplyTime((DoctorServiceAddTimeEntity) itemView.getTag());
											break;
										}
									}
								},Integer.valueOf(entity.getREPEAT_FLAG())==0);
					}
				});
				
				mGroupView.addView(itemView);
			}
		}
		if(mGroupView.getChildCount() == 0){
			findViewById(R.id.empty).setVisibility(View.VISIBLE);
		}else{
			findViewById(R.id.empty).setVisibility(View.GONE);
		}
	}

	DoctorServiceAddTimeEntity mDeleteCache;//删除的时候,缓存的实体
	
	
	
	
	private void deleteCurrentTime(DoctorServiceAddTimeEntity entity){
		try {
			JSONArray array=new JSONArray();
			JSONObject object=new JSONObject();
			object.put("SERVICE_ITEM_ID", entity.getSERVICE_ITEM_ID());
			object.put("SERVICE_DAY", entity.getSERVICE_TIME_BEGIN().substring(0, 8));
			object.put("REPEAT_FLAG", entity.getREPEAT_FLAG());
			array.put(object);
			RequestParams params =new RequestParams();
			params.put("SERVICE_TYPE_ID",entity.getSERVICE_TYPE_ID());
			params.put("Type", "deleteSeleteYuyueTime");
			params.put("CUSTOMER_ID", SmartControlClient.getControlClient().getUserId());
			params.put("PARAME",  array.toString());
			ApiService.doHttpSERVICESETSERVLET420(params, new AsyncHttpResponseHandler(this) {
				@Override
				public void onSuccess(String content) {
					super.onSuccess(content);
					try {
						JSONObject object = new JSONObject(content);
						if (HStringUtil.isEmpty(content) || !"1".equals(object.optString("code"))) {
							SingleBtnFragmentDialog.showDefault(getSupportFragmentManager(), object.getString("message"));
						} else {
							initData();
						}
					} catch (Exception e) {
					}
				}
			});
		} catch (Exception e) {
		}
	}
	
	/**
	 * 编辑
	 * @param entity
	 */
	protected void onEditService(DoctorServiceAddTimeEntity entity) {
		Intent intent =new Intent(this,DoctorServiceAddTimeActivity.class);
		Bundle bundle=new Bundle();
		bundle.putParcelable("entity", entity);
		intent.putExtras(bundle);
		intent.putParcelableArrayListExtra("entitys",(ArrayList<? extends Parcelable>) entitys);
		intent.putExtra("serviceType",entity.getSERVICE_TYPE_ID());
		intent.putExtra("PERSON_MAX",PERSON_MAX);
		intent.putExtra("PERSON_MIN",PERSON_MIN);
		intent.putExtra("PRICE_MAX",PRICE_MAX);
		intent.putExtra("PRICE_MIN",PRICE_MIN);
		startActivity(intent);
	}

	
	@Override
	public void onDismiss(DialogFragment fragment) {
	}
	
	
	//删除操作
	@Override
	public void onClick(DialogFragment fragment, View v) {
		if(PARAME == null){
			deleteReplyTime(mDeleteCache);
		}else{
			ApiService.doHttpDeleteServiceTimeBefor(TYPE, LoginBusiness.getInstance().getLoginEntity().getId(), PARAME, new AsyncHttpResponseHandler(this){
				@Override
				public void onSuccess(String content) {
					super.onSuccess(content);
					try {
						JSONObject object =new JSONObject(content);
						if(HStringUtil.isEmpty(content) || !"1".equals(object.optString("code"))){
							SingleBtnFragmentDialog.showDefault(getSupportFragmentManager(),  object.getString("message"));
						}else{
							initData();
						}
					} catch (Exception e) {
					}
				}
			});
		}

	}
	
	/**
	 删除复用链条
	ServiceSetServlet44
	Type=deleteRepeatServices
	CUSTOMER_ID
	REPEAT_BATCH
	 */
	private void deleteReplyTime(DoctorServiceAddTimeEntity entity){
		RequestParams params =new RequestParams();
		params.put("Type","deleteRepeatServices");
		params.put("SERVICE_ITEM_ID",entity.getSERVICE_ITEM_ID());
		params.put("REPEAT_BATCH",entity.getREPEAT_BATCH());
		params.put("CUSTOMER_ID",SmartFoxClient.getLoginUserId());
		ApiService.doHttpSERVICESETSERVLET420(params, new AsyncHttpResponseHandler(this){
			@Override
			public void onSuccess(String content) {
				super.onSuccess(content);
				try {
					JSONObject object =new JSONObject(content);
					if(HStringUtil.isEmpty(content) || !"1".equals(object.optString("code"))){
						SingleBtnFragmentDialog.showDefault(getSupportFragmentManager(),  object.getString("message"));
					}else{
						initData();
					}
				} catch (Exception e) {
				}
			}
		});
	}
}
