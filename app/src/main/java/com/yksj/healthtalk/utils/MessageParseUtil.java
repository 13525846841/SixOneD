package com.yksj.healthtalk.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.math.NumberUtils;
import org.json.JSONObject;

import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.text.style.UnderlineSpan;

import com.yksj.consultation.comm.imageload.NotifyImageLoader;
import com.yksj.healthtalk.entity.ATagEntity;
import com.yksj.healthtalk.entity.ImgTagEntity;
/**
 * 消息格式解析
 * @author zhao
 */
public class MessageParseUtil {
		/*static String str = "欢迎<img src=\"assets/customerIcons/s_zcmale.png\" placeHolder=\"assets/group/groupdefault.png\" width=\"24\" height=\"24\"/><a type=\"customer\" id=\"24861\">1019527</a>加入六一健康!";
		static String str1 = "<img src=\"assets/customerIcons/s_zcmale.png\" placeHolder=\"assets/group/groupdefault.png\" width=\"24\" height=\"24\"/><a type=\"customer\" id=\"9845\" >就算</a>在话题<img src=\"assets/group/m_qjbing.png\" placeHolder=\"assets/group/groupdefault.png\" width=\"24\" height=\"24\"/><a type=\"group\" id=\"24832\">骨灰盒</a>中说：滑稽剧;感兴趣请加入!";
		static String str2 = "<img placeHolder=\"s_zcmale_24*24.png\" src=\"assets/customerIcons/s_zcmale.png\" width=\"24\" height=\"24\"/><a type=\"customer\" id=\"25266\">1019882</a>创建了话题：<img placeHolder=\"groupdefault_24*24.png\" src=\"assets/group/m_qysheng.png\" width=\"24\" height=\"24\"/><a type=\"group\" id=\"25286\">王一</a>";
		static String str3 = "欢迎<img src=\"assets/customerIcons/s_zcmale.png\" placeHolder=\"groupdefault_24*24.png\" width=\"24\" height=\"24\"/><a type=\"customer\" id=\"25305\">1019901</a>加入六一健康!";
		static String str4 = "<img src=\"/CusZiYuan/9845/smoll20120927070619.png\" placeHolder=\"groupdefault_24*24.png\" width=\"24\" height=\"24\"/><a type=\"customer\" id=\"9845\" >就算</a>在话题<img src=\"assets/group/m_qjbing.png\" placeHolder=\"groupdefault_24*24.png\" width=\"24\" height=\"24\"/><a type=\"group\" id=\"10345\">医疗</a>中说：<img src=\"得瑟.png\" placeHolder=\"得瑟.png\" width=\"24\" height=\"24\" /><img src=\"叼烟.png\" placeHolder=\"叼烟.png\" width=\"24\" height=\"24\" /><img src=\"屌丝笑.png\" placeHolder=\"屌丝笑.png\" width=\"24\" height=\"24\" /><img src=\"屌丝笑.png\" placeHolder=\"屌丝笑.png\" width=\"24\" height=\"24\" />；感兴趣请加入！";
		static String str5 = "<img src=\"/CusZiYuan/9845/smoll20120927070619.png\" placeHolder=\"groupdefault_24*24.png\" width=\"24\" height=\"24\"/><a type=\"customer\" id=\"9845\" >就算</a>在话题<img src=\"assets/group/m_qjbing.png\" placeHolder=\"groupdefault_24*24.png\" width=\"24\" height=\"24\"/><a type=\"group\" id=\"10345\">医疗</a>中说：<img src=\"爱死你啦.png\" placeHolder=\"爱死你啦.png\" width=\"24\" height=\"24\" /><img src=\"爱死你啦.png\" placeHolder=\"爱死你啦.png\" width=\"24\" height=\"24\" /><img src=\"爱死你啦.png\" placeHolder=\"爱死你啦.png\" width=\"24\" height=\"24\" /><img src=\"爱死你啦.png\" placeHolder=\"爱死你啦.png\" width=\"24\" height=\"24\" /><img src=\"爱死你啦.png\" placeHolder=\"爱死你啦.png\" width=\"24\" height=\"24\" />...；感兴趣请加入！";
		static String str6 = "<img src=\"/CusZiYuan/9845/smoll20120927070619.png\" placeHolder=\"groupdefault_24*24.png\" width=\"24\" height=\"24\"/><a type=\"customer\" id=\"9845\" >就算</a>在话题<img src=\"assets/group/m_qjbing.png\" placeHolder=\"groupdefault_24*24.png\" width=\"24\" height=\"24\"/><a type=\"group\" id=\"10345\">医疗</a>中说：〉<<m//><img src=\"爱死你啦.png\" placeHolder=\"爱死你啦.png\" width=\"24\" height=\"24\" /><img src=\"爱死你啦.png\" placeHolder=\"爱死你啦.png\" width=\"24\" height=\"24\" />/>；感兴趣请加入！";
*/		
		/**
		 * 
		 * 拆分html标签为单个标签
		 * @param html
		 */
		public static List<String> parseHtmlToSplit(String html){
			int index = 0;
			String tagStart = null;//开始标签
			String regEx ="<[^>]+>";
			Pattern pattern = Pattern.compile(regEx);
			Matcher matcher = pattern.matcher(html);
			List<String> list = new ArrayList<String>();//数据集合
			while(matcher.find()){
				int start = matcher.start();
				int end = matcher.end();
				if(tagStart == null && index != start){//当开始标签为null与当前的索引位置不等于上一次结束位置的时候,表示是不在标签内的纯文字
					String value = html.substring(index,start);
					list.add(value);
				}
				String group = matcher.group();
				if(group.endsWith("/>")){
					list.add(group);
				}else if(group.contains("</")){
					String value = html.substring(index,start);
					if(tagStart != null){
						tagStart = tagStart.concat(value).concat(group);
						list.add(tagStart);
						tagStart = null;
					}
				}else if(group.contains("<") && group.contains(">")){//不完整标签
					tagStart = group;
				}
				index = end;
			}
			if(index != html.length()){
				String value = html.substring(index,html.length());
				list.add(value);
			}
			return list;
		}
		/**
		 * 公共消息解析html
		 * @param html
		 * @return
		 */
		public static List<Object> parseToNotifyMessage(String html){
			List<String> list = parseHtmlToSplit(html);
			List<Object> listObjects = new ArrayList<Object>();
			StringBuffer lastStr = null;//上一个拼接还未完成的字符串
			for (int i = 0; i < list.size(); i++) {
				String string = list.get(i);
				if(string.startsWith("<img")){//img标签表情需要分析
					String src = null;
					String placeHolder = null;
					String regxp = "tag=\"([^\"]+)\"";
					regxp = regxp.replace("tag","src");
					Matcher matcher = Pattern.compile(regxp).matcher(string);
					while(matcher.find()){
						String value = matcher.group();
						String[] arrys = value.split("\"");
						src = arrys[1];
						break;
					}
					regxp = regxp.replace("src","placeHolder");
					matcher = Pattern.compile(regxp).matcher(string);
					
					while(matcher.find()){
						String value = matcher.group();
						String[] arrys = value.split("\"");
						placeHolder = arrys[1];
						break;
					}
					if(src==null && placeHolder==null){//不是标准的标签，可能是用户输入的内容
						if(lastStr == null){//纯文字的开头
							lastStr = new StringBuffer(string);
						}else{
							lastStr.append(string);
						}
					}else{//处理图片标签,再处理标签之前检查之前有没有纯文字没有添加到集合中
						//_24 表情处理
						if(src.equals(placeHolder) && !src.endsWith("_24.png")){
							src = "["+src.replace(".png","]");
							if(lastStr == null){//纯文字的开头
								lastStr = new StringBuffer(string);
							}else{
								lastStr.append(src);
							}
						//图片	
						}else{
							if(lastStr !=null ){
								listObjects.add(lastStr.toString());
								lastStr = null;
							}
							ImgTagEntity imgTagEntity = new ImgTagEntity();
							imgTagEntity.setPlaceHolder(placeHolder);
							imgTagEntity.setSrc(src);
							listObjects.add(imgTagEntity);
						}
					}
				}else if(string.startsWith("<a")){//a标签
					//<a type="customer" id="24861">1019527</a>
					String type = null;
					String id = null;
					String content = null;
					
					int indexStart = string.indexOf(">")+1;
					int indexEnd = string.lastIndexOf("</a>");
					content = string.substring(indexStart,indexEnd);//内容
					String regxp = "type=\"([^\"]+)\"";
					Matcher matcher = Pattern.compile(regxp).matcher(string);
					while(matcher.find()){
						String aType = matcher.group();
						String[] arrys = aType.split("\"");
						type = arrys[1];
						break;
					}
					regxp = "id=\"([^\"]+)\"";
					matcher = matcher.pattern().compile(regxp).matcher(string);
					while(matcher.find()){
						String aId = matcher.group();
						String[] arrys = aId.split("\"");
						id = arrys[1];
						break;
					}
					if(type == null && content == null){//不是标准的a标签当纯文字处理
						if(lastStr == null){//如果纯文本为null将当前字符串作为纯文本的开始
							lastStr = new StringBuffer(string);
						}else{
							lastStr.append(string);
						}
					}else{
						if(lastStr !=null ){
							listObjects.add(lastStr.toString());
							lastStr = null;
						}
						ATagEntity aTagEntity = new ATagEntity();
						if(content.length() == 0)content = " ";
						aTagEntity.setContent(content);
						aTagEntity.setId(id);
						//跳转类型
						if("group".equalsIgnoreCase(type)){
							aTagEntity.setType(1);
						}else if("customer".equalsIgnoreCase(type)||"chat_group_id".equalsIgnoreCase(type)){
							aTagEntity.setType(2);
						}else if("news".equalsIgnoreCase(type)){
							aTagEntity.setType(3);
						}
//						aTagEntity.setType("group".equalsIgnoreCase(type)?1:2);
						listObjects.add(aTagEntity);
					}
				}else{//纯文字
					if(lastStr == null){//如果纯文本为null将当前字符串作为纯文本的开始
						lastStr = new StringBuffer(string);
					}else{
						lastStr.append(string);
					}
				}
			}
			if(lastStr != null){
				listObjects.add(lastStr.toString());
				lastStr = null;
			}
			list = null;
			return listObjects;
		}
		
		/**
		 * 绑定公告文字
		 * @param list
		 * @param textView
		 * @param mSmileParse
		 * @param imageLoader
		 */
		public static SpannableStringBuilder bindTextViewSpaneble(List<Object> list,FaceParse parse,NotifyImageLoader imageLoader){
			SpannableStringBuilder  spannableStringBuilder = new SpannableStringBuilder();
			for (Object object : list) {
				if(object instanceof String){
					spannableStringBuilder.append(parse.parseSmileTxt((String)object));
				}else if(object instanceof ATagEntity){
					ATagEntity aTagEntity = (ATagEntity)object;
					//ClickMessageSpan clickMessageSpan = new ClickMessageSpan(aTagEntity.getType(),aTagEntity.getId(),aTagEntity.getContent());
					UnderlineSpan underlineSpan = new UnderlineSpan();
					SpannableString spannableString = new SpannableString(aTagEntity.getContent());
					spannableString.setSpan(underlineSpan,0,spannableString.length(),Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
					spannableStringBuilder.append(spannableString);
					//spannableStringBuilder.setSpan(clickMessageSpan,spannableStringBuilder.getSpanStart(underlineSpan),spannableStringBuilder.getSpanEnd(underlineSpan), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				}else if(object instanceof ImgTagEntity){
					ImgTagEntity aTagEntity = (ImgTagEntity)object;
					String srcImage = aTagEntity.getSrc();//源图片可能需要下载
					String defaultImage = aTagEntity.getPlaceHolder();//默认显示替代图片
					Drawable drawable = null;
					if(srcImage.startsWith("assets/group")||srcImage.startsWith("assets/customerIcons")){//src为默认本地图片无需下载
						drawable = imageLoader.getBitmapFromAssets(srcImage);
					}else{//远程图片需要去下载
						drawable = imageLoader.getBitmapDrawableForCache(srcImage);
						if(drawable == null){//远程图片当前还未下载
							drawable = imageLoader.getBitmapFromAssets(defaultImage);
						}
					}
					ImageSpan imageSpan = new ImageSpan(drawable);
					SpannableString spannableString = new SpannableString(aTagEntity.getSrc());
					spannableString.setSpan(imageSpan,0,spannableString.length(),Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
					spannableStringBuilder.append(spannableString);
				}
			}
			return spannableStringBuilder;
		}
		
		
		/**
		 * 解析json返回数据
		 * 解析话题消息
		 * @param jsonData
		 */
		public static List<Map<String, Object>> parseJsonData(String jsonData,int msgType) {

			List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
			if (jsonData == null)
				return list;
			try {
				org.json.JSONArray jsonArray = new org.json.JSONArray(jsonData);
				for (int i = 0; i < jsonArray.length(); i++) {
					Map<String, Object> map = new HashMap<String, Object>();
					JSONObject jsonObject = jsonArray.getJSONObject(i);
					String id = jsonObject.optString("announcementsHistoryid");
					String content = jsonObject.optString("content");
					String date = jsonObject.optString("date");
					String type = jsonObject.optString("type");
					map.put("msgType", msgType);
					if (NumberUtils.isNumber(type)) {
						map.put("type", NumberUtils.toInt(type));
					} else {
						map.put("type", 4);
					}
					map.put("id", id);
					map.put("time", date);
					if(msgType != 4){
						List<Object> objects = MessageParseUtil.parseToNotifyMessage(content);
						map.put("content", objects);
					}else{
						map.put("content", content);
					}
					list.add(map);
				}
			} catch (org.json.JSONException e) {
				e.printStackTrace();
			}
			return list;
		}
		
		/**
		 * 医患对话
		 * @param jsonData
		 * @param msgType
		 * @return
		 */
		public static List<Map<String, Object>> parseJsonDataList(String jsonData,int msgType) {
			List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
			if (jsonData == null)
				return list;
			try {
				org.json.JSONArray jsonArray = new org.json.JSONArray(jsonData);
				for (int i = 0; i < jsonArray.length(); i++) {
					Map<String, Object> map = new HashMap<String, Object>();
					JSONObject jsonObject = jsonArray.getJSONObject(i);
					
					String id = jsonObject.optString("CHAT_GROUP_ID");
					String content = jsonObject.optString("CONTENT");
					String date = jsonObject.optString("date","-1");
					String type = jsonObject.optString("type");
					if (NumberUtils.isNumber(type)) {
						map.put("type", NumberUtils.toInt(type));
					} else {
						map.put("type", 4);
					}
					map.put("id", id);
					map.put("time", date);
					map.put("msgType", msgType);
					if(msgType != 4){
						List<Object> objects = MessageParseUtil.parseToNotifyMessage(content);
						map.put("content", objects);
					}else{
						map.put("content", content);
					}
					list.add(map);
				}
			} catch (org.json.JSONException e) {
				e.printStackTrace();
			}
			return list;
		}
		
}
