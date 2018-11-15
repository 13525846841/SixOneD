package com.yksj.consultation.sonDoc.doctorstation;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.yksj.consultation.caledar.SelectCaledarViewDialogFragment;
import com.yksj.consultation.caledar.SelectCaledarViewDialogFragment.OnBackDataListener;
import com.yksj.consultation.caledar.SelectCaledarViewDialogFragment.OnItemClickCaladerListener;
import com.library.base.base.BaseActivity;
import com.yksj.consultation.comm.SingleBtnFragmentDialog;
import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.sonDoc.views.WheelView;
import com.yksj.healthtalk.entity.DoctorServiceAddTimeEntity;
import com.yksj.consultation.business.LoginBusiness;
import com.yksj.healthtalk.net.http.ApiService;
import com.yksj.healthtalk.net.http.AsyncHttpResponseHandler;
import com.yksj.healthtalk.net.http.RequestParams;
import com.yksj.healthtalk.net.socket.SmartFoxClient;
import com.yksj.healthtalk.utils.DoctorServiceTimeUtile;
import com.yksj.healthtalk.utils.HStringUtil;
import com.yksj.healthtalk.utils.SystemUtils;
import com.yksj.healthtalk.utils.TimeUtil;
import com.yksj.healthtalk.utils.ToastUtil;
import com.yksj.healthtalk.utils.ViewFinder;
import com.yksj.healthtalk.utils.WheelUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
/**
 * 添加服务
 * @author jack_tang
 *
 */
public class DoctorServiceAddTimeActivity extends BaseActivity implements OnClickListener, OnBackDataListener, OnItemClickCaladerListener, CompoundButton.OnCheckedChangeListener {

	private TextView date;//日期
	String mServiceTypeId;//2预约时段   3预约面访
	private TextView mStart_time_content;
	private TextView mEnd_time_content;
	private TextView addressContent;//
	private String PERSON_MIN;
	private String PERSON_MAX;
	private String PRICE_MIN;
	private String PRICE_MAX;
	private String SERVICE_ITEM_ID;
	private RelativeLayout priceLayout;
	private boolean isExpert=false;//是否是专家

	private ToggleButton mBtnImage;//参加义诊按钮
	private EditText mEdittext;//义诊价格
	private boolean isCheck = false;//义诊按钮
	private static String free = "";
	private RelativeLayout rl_yizhen;


	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.doctor_service_add_time_layout);
		initView();
	}

	private void initView() {
		initializeTitle();
		mainView = getLayoutInflater().inflate(R.layout.doctor_service_add_time_layout, null);
		ViewFinder finder =new ViewFinder(this);
		titleLeftBtn.setOnClickListener(this);
		titleTextV.setText("添加");
		setRight("存储", this);
		date = (TextView) finder.onClick(R.id.date,this);
		finder.onClick(this,new int[]{R.id.start_time,R.id.end_time,R.id.reply_action,R.id.service_person});
		mBtnImage= (ToggleButton) findViewById(R.id.iv_addyizhen);
		mEdittext = (EditText) findViewById(R.id.person_count_price);

		rl_yizhen = (RelativeLayout) findViewById(R.id.rl_yizhen);
		mStart_time_content = finder.find(R.id.start_time_content);
		mEnd_time_content = finder.find(R.id.end_time_content);
		reply_count = finder.find(R.id.reply_count);
		reply_count.setTag("0");
		reply_count.setText("永不");
		personCount = finder.find(R.id.person_count);
		priceNumber = finder.find(R.id.price_number);
		addressContent = finder.find(R.id.address);
		priceLayout= (RelativeLayout) findViewById(R.id.service_set_price_layout);
		mServiceTypeId =getIntent().getStringExtra("serviceType");
		PERSON_MIN=getIntent().getStringExtra("PERSON_MIN");
		PERSON_MAX=getIntent().getStringExtra("PERSON_MAX");
		PRICE_MIN=getIntent().getStringExtra("PRICE_MIN");
		PRICE_MAX=getIntent().getStringExtra("PRICE_MAX");
//		if ("0".equals(LoginBusiness.getInstance().getLoginEntity().getDoctorPosition()))
//			priceLayout.setVisibility(View.GONE);
//		else
//			isExpert=true;

		if("3".equals(mServiceTypeId)){
			findViewById(R.id.address_action).setVisibility(View.VISIBLE);
			findViewById(R.id.address_action).setOnClickListener(this);
			finder.setText(R.id.price_text, "门诊预约价格("+PRICE_MIN+"~"+PRICE_MAX+"元/人)");
			finder.setText(R.id.person_text, "门诊预约人数("+PERSON_MIN+"~"+PERSON_MAX+"人)");
		}else{
			findViewById(R.id.address_action).setVisibility(View.GONE);
			finder.setText(R.id.price_text, "预约咨询价格("+PRICE_MIN+"~"+PRICE_MAX+"元/人)");
			finder.setText(R.id.person_text, "预约咨询人数("+PERSON_MIN+"~"+PERSON_MAX+"人)");
			finder.setText(R.id.start_time_text, "选择预约咨询开始时间");
			finder.setText(R.id.end_time_text, "选择预约咨询结束时间");
		}
		
		if(getIntent().hasExtra("entity")){
			try {
				entity = (DoctorServiceAddTimeEntity) getIntent().getSerializableExtra("entity");
//				findViewById(R.id.service_person).setVisibility(View.VISIBLE);
				SERVICE_ITEM_ID = entity.getSERVICE_ITEM_ID();
				mStart_time_content.setText(DoctorServiceTimeUtile.getTime(entity.getSERVICE_TIME_BEGIN()) );
				mStart_time_content.setTag(DoctorServiceTimeUtile.getTimeObje(entity.getSERVICE_TIME_BEGIN()) );
				mEnd_time_content.setText(DoctorServiceTimeUtile.getTime(entity.getSERVICE_TIME_END()) );
				mEnd_time_content.setTag(DoctorServiceTimeUtile.getTimeObje(entity.getSERVICE_TIME_END()) );
				personCount.setText(entity.getSERVICE_MAX());
				priceNumber.setText(entity.getSERVICE_PRICE());
				if("3".equals(mServiceTypeId)){
					addressContent.setText(entity.getSERVICE_PLACE());
					addressContent.setVisibility(View.VISIBLE);
				}
				String time_ = entity.getSERVICE_TIME_BEGIN();
				date.setText(time_.substring(0, 4)+"-"+time_.substring(4, 6)+"-"+time_.substring(6,8));
				date.setTag(time_.substring(0, 8));
				
//				findViewById(R.id.reply_action).setVisibility(View.GONE);
//				findViewById(R.id.date).setVisibility(View.GONE);
				titleTextV.setText("编辑");
			} catch (Exception e) {
			}
		}else{
			findViewById(R.id.service_person).setVisibility(View.GONE);
			//http://220.194.46.204/DuoMeiHealth/ServiceSetServlet42?Type=queryDefinePlace&CUSTOMER_ID=
			RequestParams params =new RequestParams();
			params.put("Type", "queryDefinePlace");
			params.put("CUSTOMER_ID", SmartFoxClient.getLoginUserId());
			ApiService.doHttpServiceSetServlet420(params, new AsyncHttpResponseHandler(this){
				@Override
				public void onSuccess(int statusCode, String content) {
					super.onSuccess(statusCode, content);
					//addressContent
				}
			});
		}
		
		
		if(getIntent().hasExtra("mPressDate") && !HStringUtil.isEmpty(getIntent().getStringExtra("mPressDate"))){
			String stringExtra = getIntent().getStringExtra("mPressDate");
			date.setText(stringExtra.substring(0, 4)+"-"+stringExtra.substring(4, 6)+"-"+stringExtra.substring(6,8));
			date.setTag(stringExtra);
			list.clear();
			list.add(stringExtra);
		}


		mBtnImage.setOnCheckedChangeListener(this);
		if (isCheck==false){
			mBtnImage.setChecked(false);
			free = "0";
		}else if (isCheck){
			mBtnImage.setChecked(true);
			free = "1";
			rl_yizhen.setVisibility(View.VISIBLE);
		}


	}
	
	private String replyDateLists;
	private int indexOf2;
	private List<String> list =new ArrayList<String>();
	
	@Override
	protected void onActivityResult(int arg0, int arg1, Intent data) {
		super.onActivityResult(arg0, arg1, data);
		if(data ==null || arg1 !=RESULT_OK)return ;
		switch (arg0) {
		case 1000:
			reply_count.setText(data.getStringExtra("text"));
			reply_count.setTag(data.getIntExtra("id",0)+"");
			if(data.hasExtra("dates")) {
				replyDateLists = data.getStringExtra("dates");
			}
			break;
		case 2000:
			addressContent.setText(data.getStringExtra("address"));
			addressContent.setTag(data.getStringExtra("id"));
			addressContent.setVisibility(View.VISIBLE);
			break;
		}
	}

	@Override
	public void onClick(View v) {
		Intent intent;
		SystemUtils.hideSoftBord(getApplicationContext(), personCount);
		switch (v.getId()) {
		case R.id.title_back:
			onBackPressed();
			break;
		case R.id.title_right2:
			subMit("");
			break;
		case R.id.date:
			SelectCaledarViewDialogFragment showCaledar = SelectCaledarViewDialogFragment.showLodingDialog(getSupportFragmentManager(),1);
			showCaledar.setOnBackDataListener(this);
			showCaledar.setOnItemClickListener(this);
			break;
		case R.id.start_time:
			showTime(v,mStart_time_content);
			break;
		case R.id.end_time:
			showTime(v,mEnd_time_content);
			break;
		case R.id.reply_action:
			intent =new Intent (this,DoctorReplySetActivity.class);
			try {
				if(!HStringUtil.isEmpty(replyDateLists)){
					JSONArray array =new JSONArray(replyDateLists);
					for (int i = 0; i < array.length(); i++) {
						if(!list.contains(array.getString(i)))
							list.add(array.getString(i));
					}
				}
				intent.putExtra("tag",reply_count.getTag().toString());
				intent.putStringArrayListExtra("data",(ArrayList<String>) list);
				startActivityForResult(intent,1000);
			} catch (Exception e) {
				// TODO: handle exception
			}
			break;
		case R.id.service_person://特殊收费人群
			intent = new Intent(this,DoctorSpecialServiceListActivity.class);
			intent.putExtra("SERVICE_ITEM_ID", SERVICE_ITEM_ID);
			startActivity(intent);
			break;
		case R.id.address_action://服务地点
			intent = new Intent(this, DoctorServiceSetAddressActivity.class);
			startActivityForResult(intent,2000);
			break;
		}
	}

	private void subMit(String type) {
		SystemUtils.hideSoftBord(getApplicationContext(), personCount);
		RequestParams params =new RequestParams();
		if(!HStringUtil.isEmpty(type)){
			params.put("Type", type);
		}else if(entity != null){
			params.put("Type", "updateYuyueTime");
		}else{
			params.put("Type", "addYuyueTime");
		}
		if(entity != null)
		params.put("SERVICE_ITEM_ID",entity.getSERVICE_ITEM_ID());
		String  repeatFlage = (String) reply_count.getTag();
		
		params.put("REPEAT_FLAG", repeatFlage);//是否重复
		
		if("4".equals(repeatFlage)){//自定义时段
			params.put("REPEATDATES",replyDateLists);
		}
		
		if((mStart_time_content.getTag()==null||"".equals(mStart_time_content.getTag().toString()))){
		ToastUtil.showBasicShortToast(this, "请选择开始时间");
		return ;
		}
		
		if((mEnd_time_content.getTag()==null || "".equals(mEnd_time_content.getTag().toString()))){
			ToastUtil.showBasicShortToast(this, "请选择结束时间");
			return ;
		}
		
	
		//结束时间要大于开始时间
		if(!(Integer.valueOf(mStart_time_content.getTag().toString())<Integer.valueOf(mEnd_time_content.getTag().toString()))){
			ToastUtil.showBasicShortToast(this, "结束时间要大于开始时间");
			return ;
		}
		
		if( Long.valueOf(TimeUtil.getTime().substring(0, TimeUtil.getTime().length()-2)) > Long.valueOf(date.getTag().toString()+mStart_time_content.getTag().toString())){
			ToastUtil.showBasicShortToast(this, "服务开始时间不能小于当前系统时间");
			return ;
		}
		
		if( Long.valueOf(TimeUtil.getTime().substring(0, TimeUtil.getTime().length()-2)) > Long.valueOf(date.getTag().toString()+mEnd_time_content.getTag().toString())){
			ToastUtil.showBasicShortToast(this, "服务结束时间不能小于当前系统时间");
			return ;
		}
		
		String toastPerson = null;
		String toastPrise = null;
		if("3".equals(mServiceTypeId)){
			toastPerson = "请填写门诊预约名额("+PERSON_MIN+"-"+PERSON_MAX+"人)";
			toastPrise = "请填写门诊预约价格("+PRICE_MIN+"-"+PRICE_MAX+"元/人)";
		}else{
			toastPerson = "请填写预约咨询名额("+PERSON_MIN+"-"+PERSON_MAX+"人)";
			toastPrise = "请填写预约咨询价格("+PRICE_MIN+"-"+PRICE_MAX+"元/人)";
		}
		
		String personNum = personCount.getText().toString();
		
		if(HStringUtil.isEmpty(personNum) || (Integer.valueOf(personNum)>Integer.valueOf(PERSON_MAX))||(Integer.valueOf(personNum)<Integer.valueOf(PERSON_MIN))){
			ToastUtil.showBasicShortToast(getApplicationContext(), toastPerson);
			return ;
		}
		String moneyNum = priceNumber.getText().toString();

		if (isExpert) {
			if (HStringUtil.isEmpty(moneyNum) || (Double.parseDouble(moneyNum) > Double.parseDouble(PRICE_MAX)) || (Double.parseDouble(moneyNum) < Double.parseDouble(PRICE_MIN))) {
				ToastUtil.showBasicShortToast(getApplicationContext(), toastPrise);
				return;
			}
		}else {
			//moneyNum="0";
			if (HStringUtil.isEmpty(moneyNum) || (Double.parseDouble(moneyNum) > Double.parseDouble(PRICE_MAX)) || (Double.parseDouble(moneyNum) < Double.parseDouble(PRICE_MIN))) {
				ToastUtil.showBasicShortToast(getApplicationContext(), toastPrise);
				return;
			}

		}
		
		params.put("SERVICE_TIME_BEGIN", date.getTag().toString()+mStart_time_content.getTag().toString()+"00");//开始事件
		params.put("SERVICE_TIME_END", date.getTag().toString()+mEnd_time_content.getTag().toString()+"00");//结束事件201503290315
		params.put("SERVICE_MAX", personCount.getText().toString());//人数
		params.put("SERVICE_PRICE", moneyNum);//价格
		
		if("3".equals(mServiceTypeId)){
			if(HStringUtil.isEmpty(addressContent.getText().toString())){
				ToastUtil.showBasicShortToast(this,"请输入服务地点");
				return ;
			}
			params.put("SERVICE_PLACE", addressContent.getText().toString());
			if(addressContent.getTag() != null)
			params.put("PLACE_ID", addressContent.getTag().toString());
		}
		params.put("SERVICE_TYPE_ID", mServiceTypeId);
		params.put("CUSTOMER_ID", LoginBusiness.getInstance().getLoginEntity().getId());
		

		String freePrice = mEdittext.getText().toString().trim();
		if ("1".equals(free)){
			if(HStringUtil.isEmpty(freePrice)){
				ToastUtil.showBasicShortToast(this,"请输入义诊价格");
				return ;
			}
		}
		params.put("free", free);
		params.put("freePrice", freePrice);


		ApiService.doHttpSERVICESETSERVLET420(params, new AsyncHttpResponseHandler(this){
			@Override
			public void onSuccess(String content) {
				super.onSuccess(content);
					try {
						final JSONObject object = new JSONObject(content);
						if(!"1".equals(object.optString("code"))){
							SingleBtnFragmentDialog.showDefault(getSupportFragmentManager(),object.optString("message"));
						}else{
							if(entity==null){
//								DoubleBtnFragmentDialog.showDefault(getSupportFragmentManager(),  "添加服务时段成功,可以为所添加时段设置特殊收费人群.", "以后再说","现在设置",new OnDilaogClickListener() {
//									@Override
//									public void onDismiss(DialogFragment fragment) {
//										fragment.dismissAllowingStateLoss();
										setResult(RESULT_OK, getIntent());
										finish();
										ToastUtil.showShort("处理成功");
//									}
//									@Override
//									public void onClick(DialogFragment fragment, View v) {
//										Intent intent = new Intent(DoctorServiceAddTimeActivity.this,DoctorSpecialServiceListActivity.class);
//										if(TextUtils.isEmpty(object.optString("SERVICE_ITEM_ID")))return;
//										intent.putExtra("SERVICE_ITEM_ID",object.optString("SERVICE_ITEM_ID"));
//										startActivity(intent);
//										DoctorServiceAddTimeActivity.this.finish();
//									}
//								});
							}else{
								Intent intent = getIntent();
								Bundle bundle = new Bundle();
								bundle.putParcelable("entity", entity);
								intent.putExtras(bundle);
								setResult(RESULT_OK, intent);
								finish();
								ToastUtil.showShort("处理成功");
							}
						}
					} catch (Exception e) {
					}
			}
		});
	}

	private void showTime(View v,final TextView data) {
		PopupWindow showDateSelect = WheelUtils.showDateSelect(this,getLayoutInflater(),new OnClickListener() {
			@Override
			public void onClick(View v) {
				String[]time= (String[]) v.getTag();
				SimpleDateFormat format=new SimpleDateFormat("HHmm");
				int nowTime =Integer.valueOf(format.format(new Date()));
				data.setTag(time[0]+time[1]);
				data.setText(time[0]+":"+time[1]);//time[0]+time[1]
				if(nowTime>Integer.valueOf(time[0]+time[1])&&(TimeUtil.getNowDateFormat().equals(date.getTag().toString()))){
					ToastUtil.showBasicShortToast(getApplicationContext(), "所选时间不能小于当前系统时间");
				}else{
					data.setTag(time[0]+time[1]);
					data.setText(time[0]+":"+time[1]);
				}
			}
		});
		
		View contentView = showDateSelect.getContentView();
		final WheelView wheel1 = (WheelView)contentView.findViewById(R.id.wheel);
		final WheelView wheel2 = (WheelView)contentView.findViewById(R.id.wheel_right);
		List<String> hourList =(List<String>) wheel1.getTag();
		List<String> minueList =(List<String>) wheel2.getTag();
		if(data.getTag() != null){
			String index = (String) data.getTag();
			int indexOf = hourList.indexOf(index.substring(0, 2));
			int indexOf2 = minueList.indexOf(index.substring(2,4));
			wheel1.setCurrentItem(indexOf);
			wheel2.setCurrentItem(indexOf2);
		}
		
		
	}
	
	
	
	/**
	 * 设置日期
	 */
//	private PopupWindow agepop;// 年龄的pop
	private View mainView;
	private TextView reply_count;
	private DoctorServiceAddTimeEntity entity;
	private EditText priceNumber;
	private EditText personCount;

	@Override
	public void onBackData(List<String> lists) {
		if(lists.size() ==0) return ;
		String str = lists.get(0);
		date.setTag(str);
		list.clear();
		list.add(str);
		date.setText(str.substring(0, 4)+"-"+str.substring(4, 6)+"-"+str.substring(6, 8));
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
		
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if (isChecked){
			free = "1";
			rl_yizhen.setVisibility(View.VISIBLE);
		}else if (!isChecked){
			free = "0";
			rl_yizhen.setVisibility(View.GONE);
		}
		mBtnImage.setChecked(isChecked);
	}
}
