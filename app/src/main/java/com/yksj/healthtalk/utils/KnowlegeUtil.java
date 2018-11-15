package com.yksj.healthtalk.utils;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.widget.Toast;

import com.yksj.consultation.adapter.IndexAdapter;
import com.yksj.consultation.sonDoc.R;

/**
* @ClassName: KnowlegeUtil 
* @Description: 医库的工具类
* @author wangtao
* @date 2012-12-4 上午11:54:04
 */
public class KnowlegeUtil {
	/**
	* @Title: analysisJsonAboutArr 
	* @Description: 解析JSONArray
	* @param @param arr    
	* @return void    
	* @throws
	 */
	public static  void analysisJsonAboutArr(Context context , JSONArray arr  , HashMap<String, ArrayList<HashMap<String, Object>>> data ,String type) {
		// TODO Auto-generated method stub
		HashMap<String, Object> map ;
		if (data!=null) {
			data.clear();
		}
		for (int i = 0; i < arr.length(); i++) {
			try {
				JSONObject json = arr.getJSONObject(i);
				String name = json.getString("MENUNAME");
				String firstChar = PingYinUtil.getPingYin(context,
						name.substring(0, 1));
				map = new HashMap<String, Object>();
				map.put("zname", name);
				map.put("zid", json.getString("MENUCODE"));
				map.put("type", type);
				map.put(IndexAdapter.SORT_KEY, firstChar);
				map.put("extra", "");
				
				if (data.containsKey(firstChar)) {
					data.get(firstChar).add(map);
				} else {
					ArrayList<HashMap<String, Object>> al = new ArrayList<HashMap<String, Object>>();
					al.add(map);
					data.put(firstChar, al);
				}
				
			} catch (JSONException e) {
//				Toast.makeText(context, R.string.json_error, 1).show();
//				e.printStackTrace();
				throw new RuntimeException();
			}
			
		}
	}
	
	
	/**
	* @Title: analysisJsonAboutArr 
	* @Description: 解析JSONArray
	* @param @param arr    
	* @return void    
	* @throws
	 */
	public static  void analysisJsonAboutArr(Context context , JSONArray arr  , HashMap<String, ArrayList<HashMap<String, Object>>> data ,String type,boolean isMedicine) {
		// TODO Auto-generated method stub
		HashMap<String, Object> map ;
		if (data!=null) {
			data.clear();
		}
		for (int i = 0; i < arr.length(); i++) {
			try {
				JSONObject json = arr.getJSONObject(i);
				String name = json.getString("MENUNAME");
				String firstChar = PingYinUtil.getPingYin(context,
						name.substring(0, 1));
				map = new HashMap<String, Object>();
				map.put("zname", name);
				map.put("zid", json.getString("MENUCODE"));
				map.put("type", type);
				map.put(IndexAdapter.SORT_KEY, firstChar);
				map.put("extra", "");
				
				if (isMedicine) {
					map.put("zname", name.substring(0, name.length() - 2));
					// [{"MENUCODE":396,"MENUNAME":"苹芳淑_1","STANDARDS":"20mg*8片","FACTORY":"北京康蒂尼药业有限公司","ISPRICED":1}]
					map.put("factory", json.getString("FACTORY"));
					map.put("count", json.getString("STANDARDS"));
				}
				if (data.containsKey(firstChar)) {
					data.get(firstChar).add(map);
				} else {
					ArrayList<HashMap<String, Object>> al = new ArrayList<HashMap<String, Object>>();
					al.add(map);
					data.put(firstChar, al);
				}
				
			} catch (JSONException e) {
			 throw new RuntimeException();
			}
		}
	}
	
	
	
	public static  void analysisJsonAboutArr(Context context , JSONArray arr  , HashMap<String, ArrayList<HashMap<String, Object>>> data ,String type,boolean isDataClear,String Flag) {
		// TODO Auto-generated method stub
		HashMap<String, Object> map ;
		if (data!=null) {
			if (isDataClear) {
				data.clear();
			}
		}
		for (int i = 0; i < arr.length(); i++) {
			try {
				JSONObject json = arr.getJSONObject(i);
				String name = json.getString("MENUNAME");
				String firstChar = PingYinUtil.getPingYin(context,
						name.substring(0, 1));
				map = new HashMap<String, Object>();
				map.put("zname", name);
				map.put("zid", json.getString("MENUCODE"));
				map.put("type", type);
				map.put(IndexAdapter.SORT_KEY, firstChar);
				map.put("extra", "");
				
				if (data.containsKey(firstChar)) {
					data.get(firstChar).add(map);
				} else {
					ArrayList<HashMap<String, Object>> al = new ArrayList<HashMap<String, Object>>();
					al.add(map);
					data.put(firstChar, al);
				}
				
			} catch (JSONException e) {
				Toast.makeText(context, R.string.json_error, 1).show();
				e.printStackTrace();
			}
			
		}
	}
	
	

}

