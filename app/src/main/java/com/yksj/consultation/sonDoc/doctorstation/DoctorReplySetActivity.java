package com.yksj.consultation.sonDoc.doctorstation;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.library.base.base.BaseActivity;
import com.yksj.consultation.caledar.SelectCaledarViewDialogFragment;
import com.yksj.consultation.caledar.SelectCaledarViewDialogFragment.OnBackDataListener;
import com.yksj.consultation.caledar.SelectCaledarViewDialogFragment.OnItemClickCaladerListener;
import com.yksj.consultation.sonDoc.R;
import com.yksj.healthtalk.utils.ToastUtil;
/**
 * 出诊设置 重复设置页面
 * @author jack_tang
 *
 */
public class DoctorReplySetActivity extends BaseActivity implements OnClickListener, OnCheckedChangeListener, OnBackDataListener, OnItemClickCaladerListener {

	private TextView mCustomContent;
	private int selectId = 0;
	private String selectText = "永不";
	private RadioGroup mGroup;
	private List<String> mSecltedDates =new ArrayList<String>();
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.doctor_reply_set_layout);
		initializeTitle();
		titleLeftBtn.setOnClickListener(this);
		titleTextV.setText("重复");
		mGroup = (RadioGroup) findViewById(R.id.radgroup);
		mGroup.setOnCheckedChangeListener(this);
		mCustomContent = (TextView) findViewById(R.id.customer_content);
		mCustomContent.setOnClickListener(this);
		mSecltedDates =getIntent().getStringArrayListExtra("data");
		selectId = Integer.valueOf(getIntent().getStringExtra("tag"));
		RadioButton button =  (RadioButton) mGroup.getChildAt(selectId);
		button.setChecked(true);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.title_back:
			submit();
			break;
		case R.id.customer_content:
			RadioButton button = (RadioButton) mGroup.getChildAt(mGroup.getChildCount()-1);
			button.setChecked(true);
			SelectCaledarViewDialogFragment showCaledar = SelectCaledarViewDialogFragment.showLodingDialog(getSupportFragmentManager(),0,mSecltedDates);
			showCaledar.setOnBackDataListener(this);
			showCaledar.setOnItemClickListener(this);
			break;
		}
	}

	@Override
	public void onBackPressed() {
		submit();
	}
	
	
	private void submit(){
		Intent intent = getIntent();
		intent.putExtra("id", selectId);
		if(selectId == 4){
			if(mCustomContent.getTag()==null){
				ToastUtil.showShort("请选择自定义的日期");
				return ;
			}
			intent.putExtra("dates", mCustomContent.getTag().toString());
		}
		intent.putExtra("text", selectText);
		setResult(RESULT_OK,intent );
		finish();
	}
	
	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		for (int i = 0; i < group.getChildCount(); i++) {
			RadioButton button = (RadioButton) group.getChildAt(i);
			if(button.getId() == checkedId){
				selectId=i;
				selectText= button.getText().toString();
				button.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.bt_reply_selected), null);
			}else{
				button.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
			}
		}
		
	}

	@Override
	public void onBackData(List<String> str) {
		mSecltedDates.clear();
		mSecltedDates.addAll(str);
		try {
			StringBuffer buffer =new StringBuffer();
			JSONArray array =new JSONArray();
			for (int i = 0; i < str.size(); i++) {
				buffer.append(str.get(i));
				array.put( str.get(i));
				if(i!=str.size()-1){
					buffer.append(",");	
				}
			}
			mCustomContent.setTag(array.toString());
			mCustomContent.setText(buffer.toString());
		} catch (Exception e) {
//			System.out.println("");
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
	}
}
