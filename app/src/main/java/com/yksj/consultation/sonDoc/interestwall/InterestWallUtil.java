package com.yksj.consultation.sonDoc.interestwall;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.yksj.healthtalk.entity.InterestWallEntity;
import com.yksj.healthtalk.utils.DataParseUtil;
import com.yksj.healthtalk.utils.SharePreHelper;

/**
 * 兴趣墙个人分享工具类,解析数据,加载等
 * @author Administrator
 *
 */
public class InterestWallUtil {

	
	/**
	 * 返回json数据解析（非ui线程）
	 * 
	 * @param content
	 * @param id
	 * @return
	 */
	public static ArrayList<InterestWallEntity> onParseData(String content, String id) {
		ArrayList<InterestWallEntity> data = new ArrayList<InterestWallEntity>();
		try {
			JSONObject jsonObject = new JSONObject(content);
			if (jsonObject.has("maxId")) {// 拿到兴趣墙的最大id
				int maxId = jsonObject.getInt("maxId");
				if (Integer.valueOf(SharePreHelper.fatchInterestWallId()) < maxId) {
					SharePreHelper.savInterestWallId(String.valueOf(maxId));
				}
			}
			// 兴趣墙数据
			JSONArray groupJsonArray = jsonObject.getJSONArray("group");
			for (int i = 0; i < groupJsonArray.length(); i++) {
				JSONObject tempJson = groupJsonArray.getJSONObject(i);
				InterestWallEntity entity = DataParseUtil.parseWallEntity(tempJson);
//				float scale = mWidthSize / entity.getPicWidth();
//				entity.setPicWidth((int) (entity.getPicWidth() * scale));
//				entity.setPicHeight((int) (entity.getPicHeight() * scale));
				entity.setPicWidth(entity.getPicWidth());
				entity.setPicHeight(entity.getPicHeight());
				
				data.add(entity);
			}
			return data;
		} catch (JSONException e) {
		}
		return null;
	}
}
