package com.yksj.consultation.sonDoc.consultation.salon.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.SimpleAdapter;

import com.library.base.base.BaseTitleActivity;
import com.yksj.consultation.sonDoc.R;
import com.yksj.healthtalk.net.http.ApiService;
import com.yksj.healthtalk.net.http.ObjectHttpResponseHandler;
import com.yksj.healthtalk.utils.SalonHttpUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * 创建话题或者个人分享时选择所属的标签的Activity
 * 点击标签直接返回去
 * @author lmk
 */
public class SelectLabelActivity extends BaseTitleActivity implements OnClickListener,
	OnItemClickListener{
	private GridView mgridView;
	private ArrayList<HashMap<String, Object>> data=new ArrayList<HashMap<String,Object>>();
	private SimpleAdapter mAdapter;
	private HashMap<String, Object> mTag;//传过来以前选择的TagEntity或者,我当前选中的要返回的
	private String type;//标签类型   groupInfoLay-话题标签,或者interestInfoLay-个人分享标签

	@Override
	public int createLayoutRes() {
		return R.layout.health_topic_select_label_ui;
	}

	@Override
	public void initialize(Bundle bundle) {
		super.initialize(bundle);
		initView();
		initData();
	}

	/**
	 * 初始化数据,获取所有标签信息
	 */
	private void initData() {
		type=getIntent().getStringExtra("type");
		ApiService.doHttpRequestSearchInterest(type, new ObjectHttpResponseHandler() {
			@Override
			public Object onParseResponse(String content) {
				return SalonHttpUtil.jsonAnalysisInfolys(content);
			}

			@Override
			public void onSuccess(Object response) {
				if(response instanceof List){
					data.clear();
					data.addAll((ArrayList<HashMap<String, Object>>) response);
					if(data!=null&&data.size()!=0){
						mAdapter.notifyDataSetChanged();
					}
				}
				super.onSuccess(response);
			}
		});
	}

	//初始化控件
	private void initView() {
		titleLeftBtn.setOnClickListener(this);
		titleTextV.setText(R.string.select_interest);
		mgridView=(GridView) findViewById(R.id.health_topic_label_gridview);
		mAdapter=new SimpleAdapter(this, data, R.layout.label_gridview_item, 
				new String[]{"name"}, new int[]{R.id.label_grid_item_btn});
		mgridView.setAdapter(mAdapter);
		mgridView.setOnItemClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.title_back:
			onBackPressed();
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		mTag=data.get(position);
		Intent intent = new Intent();
		intent.putExtra("tag", mTag);
		setResult(RESULT_OK, intent);
		onBackPressed();
	}
}
