package com.yksj.consultation.sonDoc.doctor;

import java.util.HashMap;

import android.view.View;

public interface DoctorNavigationLisenter {
	
	void OnSelectService(View view);//服务访问网络请求数据
	
	void OnDcotorDepartments(View view); //科室
	
	void OnHttpGetDoctorListForNavigation(HashMap<Integer, String> allSelect,HashMap<Integer, String> HashMap, String position, int clickBtnId);//获取医生列表

}
