//package com.yksj.healthtalk.utils;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//
//import org.apache.commons.lang.math.NumberUtils;
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import android.content.Context;
//import android.content.Intent;
//
//import com.yksj.healthtalk.db.ChatUserHelper;
//import com.yksj.healthtalk.db.Tables;
//import com.yksj.healthtalk.entity.CustomerInfoEntity;
//import com.yksj.healthtalk.entity.GroupInfoEntity;
//import com.yksj.consultation.service.CoreService;
//import com.yksj.consultation.ui.R;
//import com.yksj.consultation.ui.app.AppData;
//import com.yksj.consultation.ui.app.AppContext;
//
//public class CollectUtils {
//
//	public static final String ACTION_FRIEND_INFO = "com.yksj.health.ACTION_FRIEND_INFO";
//	/**
//	 * 下载头像
//	 * @param context
//	 * @param memInfo
//	 * @param json
//	 * @param customerId
//	 * @param type
//	 * @return
//	 */
//	public static CustomerInfoEntity setCustomerInfo(Context context,Map<String, Object> memInfo,
//			JSONObject json,String customerId,int type,ChatUserHelper mDictionaryHelper){
//		return setCustomerInfo(context, memInfo, json, customerId, type, true,mDictionaryHelper);
//	}
//
//	/**
//	 * 封装群资料实体
//	 * @param json 数据
//	 * @param isSave 是否保存
//	 * @return
//	 */
//	public static GroupInfoEntity setGroupInfo(Context context, Map<String, Object> memInfo,
//			JSONObject json,String customerId,ChatUserHelper mDictionaryHelper){
//		return setGroupInfo(context, memInfo, json, customerId, true, mDictionaryHelper);
//	}
//
//
//	/**
//	 * 更新最近联系人/最近参与
//	 * @param context
//	 * @param entity
//	 * @param id  登录用户的ID
//	 * @param appData
//	 * @param mDictionaryHelper
//	 */
//	public static void updateFriendRecent(Context context,CustomerInfoEntity entity,
//			String id,AppData appData,ChatUserHelper mDictionaryHelper){
//
//		switch (entity.getIsAttentionFriend()) {
//		case 4:
//			if(appData.getLatelyDoctordList().contains(entity.getId())){
//				appData.getLatelyDoctordList().remove(entity.getId());
//			}
//			appData.getLatelyDoctordList().add(0,entity.getId());
//			break;
//		case 3:
//			if(appData.getLatelyCustomList().contains(entity.getId())){
//				appData.getLatelyCustomList().remove(entity.getId());
//			}
//			appData.getLatelyCustomList().add(0,entity.getId());
//			break;
//		case 1:
//			if(appData.getLatelyFriendIdList().contains(entity.getId())){
//				appData.getLatelyFriendIdList().remove(entity.getId());
//			}
//			appData.getLatelyFriendIdList().add(0,entity.getId());
//			break;
//		default:
//			if(appData.getStrangerFriendIdList().contains(entity.getId())){
//				appData.getStrangerFriendIdList().remove(entity.getId());
//			}
//			appData.getStrangerFriendIdList().add(0,entity.getId());
//			break;
//		}
//		appData.cacheInformation.put(entity.getId(), entity);
////		mDictionaryHelper.saveFriendInfo(context, entity, SmartControlClient.getControlClient().getUserId(), null, Tables.TableFriend.TYPE_RECENT);
//	}
//
//	/**
//	 * 封装群资料实体
//	 * @param json 数据
//	 * @param isSave 是否保存
//	 * @return
//	 */
//	public static GroupInfoEntity setGroupInfo(Context context, Map<String, Object> memInfo,
//			JSONObject json,String customerId,boolean isDown,ChatUserHelper chatUserHelper){
//
//		GroupInfoEntity group = new GroupInfoEntity();
//		try {
//			group.setBigHeadIcon(json.getString("biggb"));
//			String cilentbg = json.getString("cilentbg");
//			group.setNormalHeadIcon(cilentbg);
//			group.setId(json.getString("groupId"));
//			group.setName(json.getString("recordName"));
//			group.setCreateCustomerID(json.getString("createCustomerID"));
//			group.setCreateTime(json.getString("createTime"));
//			group.setInfoId(json.getString("infoLayid"));
//			group.setInfoLayName(json.getString("infoLayName"));
//
//			group.setIsConnection(json.getString("groupflag").equals("1")?1:0);
//			//group.setNote(json.getString("note"));
//			group.setLimitNumber(json.getString("limitNnum"));
//		//	group.setNote(json.getString("note"));
//			group.setDescription(json.getString("recordDesc"));
//
//			group.setSalon(json.getString("groupClass").equals("1"));
//			group.setPublicCustInfo(json.getString("publicCustInfo").equals("Y"));
//			group.setInceptMessage(json.getString("inceptMessage").equals("N")?false : true);
//			group.setCusMessag("");
//
//			group.setOnLineNumber(json.getString("onlineNum"));
//			group.setPersonNumber(json.getString("personNum"));
//			group.setShowPersonNumber(json.getBoolean("showPersonnum"));
//			if(memInfo != null){
//				String key = context.getResources().getString(R.string.groupAtt);
//				memInfo.put(group.getId(), group);
////				if(customerId != null)
////					chatUserHelper.saveGroupInfo(context, group,customerId);
//			}
//		} catch (JSONException e) {
//			e.printStackTrace();
//		}
//		return group;
//	}
//
//	/**
//	 * 封装客户实体
//	 * @param json 数据
//	 * @param type 类型 0 医生 1 健康友
//	 * @param isSave 是否保存
//	 * @return
//	 */
//	public static CustomerInfoEntity setCustomerInfo(Context context,Map<String, Object> memInfo,
//			JSONObject json,String customerId,int type,boolean isDown,ChatUserHelper mDictionaryHelper){
//		CustomerInfoEntity cus = new CustomerInfoEntity();
//		try {
//			cus.setEmail(json.getString("mail"));
//			cus.setAge(json.getString("age"));
//			cus.setId(json.getString("customerid"));
//			cus.setSex(json.getString("sex"));
//			cus.setHunyin(json.getString("familysize"));
//			cus.setXueli(json.getString("culturelevel"));
//			cus.setPoneNumber(json.getString("phone"));
//			cus.setMetier(json.getString("metier"));
//			String path = json.getString("clientIconbackground");
//			//cus.setNormalHeadIcon(isDown?imageDown(context,path):path);
//			cus.setNormalHeadIcon(path);
//			cus.setBigHeadIcon(json.getString("bigIconbackground"));
//			cus.setAccomplishedname(json.getString("accomplishedname"));
//			//String customerlocus = DBUtils.queryAddress(context, json.getString("customerlocus"));
//			cus.setBirthday(json.getString("birthday"));
//			cus.setCustomerlocus(json.getString("customerlocus"));
//			cus.setDoctorTitle(json.getString("doctorTitle"));
//			//cus.setDoctorunit(json.getString("doctorunit"));
//			//cus.setDoctorunitAdd(json.getString("doctorunitAdd"));
//			cus.setDwellingplace(json.getString("dwellingplace"));
//			cus.setIsConnection(json.getInt("flag"));
//		    cus.setName(json.getString("nickname"));
//			cus.setUsername(json.getString("username"));
//			cus.setCusMessag(json.getString("cusMessage"));
//			cus.setDescription(json.getString("doctorDesc"));
//			cus.setLableJson(json.getString("infolayname"));
//
//			String roleId = json.getString("roleID");
//			if(NumberUtils.isNumber(roleId)){
//				cus.setRoldid(Integer.parseInt(roleId));
//			}
//
//			String gold = json.getString("customerGold");
//			if(NumberUtils.isNumber(gold)){
//				cus.setMoney(Integer.parseInt(gold));
//			}else{
//				cus.setMoney(0);
//			}
//			cus.setRealname(json.getString("doctorRealName"));
//			cus.setDoctorEmail(json.getString("doctorEmail"));
//			cus.setTelePhone(json.getString("doctorTelephone"));
//			cus.setMobilePhone(json.getString("doctorMobilePhone"));
//			cus.setHospital(json.getString("doctorHospital"));
//			cus.setSpecial(json.getString("doctorSpecially"));
//
//
//			cus.setOfficeCode1(json.optString("doctorOfficeCode"));
//			cus.setOfficeCode2(json.optString("doctorOffice2Code"));
//			cus.setOfficeName1(json.optString("doctorOffice"));
//			cus.setOfficeName2(json.optString("doctorOffice2"));
//
//			cus.setDoctorTitle(json.getString("doctorTitle"));
//			//cus.setAuditMark(json.getString("verifyFlag"));
//			cus.setPhoneName(json.getString("friendPhoneName"));
//
//			if(memInfo != null){
//				memInfo.put(cus.getId(), cus);
//				if(customerId != null)mDictionaryHelper.saveFriendInfo(context, cus,customerId,null,type);
//			}
//		} catch (JSONException e) {
//			e.printStackTrace();
//		}
//		return cus;
//	}
//
//	/**
//	* @Title: updateGroupRecent
//	* @Description:
//	* @param @param mDictionaryHelper
//	* @param @param group
//	* @param @param customerId
//	* @return void
//	* @throws
//	 */
////	private static void updateGroupRecent(ChatUserHelper mDictionaryHelper,GroupInfoEntity group,
////			String customerId){
//////		AppData appData = AppContext.getAppData();
//////		if (appData.getLatelyGroupIdList().contains(group.getId())) {
//////			appData.getLatelyGroupIdList().remove(group.getId());
//////		}else {
//////			appData.getLatelyGroupIdList().add(group.getId());
//////		}
////
////		mDictionaryHelper.saveOrUpdateRecentGroupInfo( group,
////				 customerId);
////	}
//
//	/**
//	 * 删除沙龙信息
//	 * @param mDictionaryHelper
//	 */
//	public static void deleteSalon(ChatUserHelper mDictionaryHelper,String customerId){
//		mDictionaryHelper.deleteSalon(customerId);
//	}
//
//	/**
//	 * 删除成员信息
//	 * @param mDictionaryHelper
//	 */
//	public static void deleteFriend(Context context ,ChatUserHelper mDictionaryHelper,String customerId){
//		mDictionaryHelper.deleteFriend(customerId);
//	}
//
//	/**
//	 * 请求健康友和聊天室
//	 * @param context
//	 * @param value
//	 */
//	private static void setLoadInit(Context context,Map<String, List<String>> friend,Map<String, List<String>> group,
//			Map<String, Object> memInfo,String customerID,String value,boolean isSave,ChatUserHelper mDictionaryHelper){
//		try {
//			if(null == value)
//				return;
//			String recent = context.getString(R.string.recentContact);
//			if(!friend.containsKey(recent)){
//				ArrayList<String> recentlist = new ArrayList<String>();
//				friend.put(recent, recentlist);
//			}
//
//			recent = context.getString(R.string.groupNew);
//			if(!group.containsKey(recent))
//			    group.put(recent, new ArrayList<String>());
//
//			if(!group.containsKey(recent)){
//				ArrayList<String> recentlist = new ArrayList<String>();
//				group.put(recent, recentlist);
//			}
//
//			recent = context.getString(R.string.recentGroup);
//			if(!group.containsKey(recent)){
//				ArrayList<String> recentlist = new ArrayList<String>();
//				group.put(recent, recentlist);
//			}
//
//			//获取Json数据数组
//			JSONArray jsonArr = new JSONArray(value);
////			获取FRIEND对象里面的数据
////			JSONArray arr = jsonArr.getJSONArray(1);
//			//获取好友列表数组
////			JSONObject object = arr.getJSONObject(0);
////			JSONArray array = object.get
//			//加载我关注的好友
////			setFriendList(context,friend,memInfo,jsonArr.get(1).toString(),customerID,DBUtils.TYPE_ATTENTION_FOR_ME,isSave);
////			//加载关注我的好友
////			setFriendList(context,friend,memInfo,jsonArr.get(2).toString(),customerID,DBUtils.TYPE_ATTENTION_TO_ME,isSave);
////			//加载黑名单
//			setFriendList(context,friend,memInfo,jsonArr.get(1).toString(),customerID,-1,isSave,mDictionaryHelper);
//			//话题圈里面的数据
//			setGroupList(context,group,memInfo,jsonArr.get(0).toString(),customerID,isSave,mDictionaryHelper);
//			AppData appData = AppContext.getAppData();
//
//			appData.attentionUserSize = jsonArr.getInt(2);
//			appData.blackListSize = jsonArr.getInt(3);
//		} catch (JSONException e) {
//			e.printStackTrace();
//		}
//	}
//
//	/**
//	 * 健康友列表返回数据处理
//	 * @param context
//	 * @param map
//	 * @param value 后台返回数据
//	 * @param customerID 当前用户ID
//	 * @param type  类型  3 我关注的  2 关注我的
//	 * @param isSave 是否保存到数据库
//	 */
//	private static void setFriendList(Context context,Map<String, List<String>> map,
//			Map<String, Object> memInfo,String value,String customerID,int type,boolean isSave,ChatUserHelper mDictionaryHelper){
//		String groupNames[]={context.getString(R.string.friendList),context.getString(R.string.stranger),context.getString(R.string.blacklist)};
//		List<String> friendList = map.get(groupNames[0]) ;
//		List<String> strangerList = map.get(groupNames[1]) ;
//		List<String> blacklistList = map.get(groupNames[2]) ;
//		try {
//			//初始话好友列表
//			if (friendList != null)
//				friendList.clear();
//			else {
//				friendList = new ArrayList<String>();
//				map.put(groupNames[0], friendList);
//			}
//
//			//初始话好友列表
//			if (strangerList != null)
//				strangerList.clear();
//			else {
//				strangerList = new ArrayList<String>();
//				map.put(groupNames[1], strangerList);
//			}
//
//			//初始话好友列表
//			if (blacklistList != null)
//				blacklistList.clear();
//			else {
//				blacklistList = new ArrayList<String>();
//				map.put(groupNames[2], blacklistList);
//			}
//
//			JSONArray jsonArr = new JSONArray(value);
//			for(int i = 0; i< jsonArr.length(); i++){
//				JSONObject json = jsonArr.getJSONObject(i);
//				JSONObject jsonObject_FRIEND = (JSONObject) json.get("FRIEND");
//				int FRIENDTYPE = (Integer) json.get("FRIENDTYPE");
//				//0-查询我的双向好友，Y;
//				if (FRIENDTYPE == 0) {
//					addFriend(context, memInfo, customerID, isSave, friendList,
//							jsonObject_FRIEND, mDictionaryHelper);
//					addStranger(context, memInfo, customerID, isSave,
//							strangerList, jsonObject_FRIEND,mDictionaryHelper);
//				// 1-查询我的单向好友--我关注的，Y;
//				}else if(FRIENDTYPE == 1){
//					addFriend(context, memInfo, customerID, isSave, friendList,
//							jsonObject_FRIEND,mDictionaryHelper);
//				}else if(FRIENDTYPE == 2){
//					addStranger(context, memInfo, customerID, isSave,
//							strangerList, jsonObject_FRIEND,mDictionaryHelper);
//				}else if(FRIENDTYPE == 3){
//					addBlacklist(context, memInfo, customerID, isSave,
//							blacklistList, jsonObject_FRIEND,mDictionaryHelper);
//					//查询关注我的和黑名交集
//				}else if(FRIENDTYPE == 4){
//					addStranger(context, memInfo, customerID, isSave,
//							strangerList, jsonObject_FRIEND,mDictionaryHelper);
//					addBlacklist(context, memInfo, customerID, isSave,
//							blacklistList, jsonObject_FRIEND,mDictionaryHelper);
//				}
////				if(!list.contains(id))
////				    list.add(id);
//			}
//			//添加最近联系人
//			List<String> recentList = map.get(context.getString(R.string.recentContact));
//			if(recentList == null)
//				recentList = new ArrayList<String>();
//			if(recentList.size() < 20)
////				ChatUserHelper.getInstance().queryFriendRecentInfo(context, recentList, SmartControlClient.getControlClient().getUserId());
//			map.put(context.getString(R.string.recentContact), recentList);
//
//			if(isSave)
//			   context.sendBroadcast(new Intent(CoreService.ACTION_FRIENDLIST));
//		} catch (JSONException e) {
//			e.printStackTrace();
//		}
//	}
//
//	/**
//	 *
//	* @Title: addStranger
//	* @Description: 添加-关注我的人-数据到friendList<我关注的人，id>和memInfo<id，个人信息>中
//	* @param @param context
//	* @param @param memInfo
//	* @param @param customerID
//	* @param @param isSave
//	* @param @param strangerList
//	* @param @param jsonObject_FRIEND
//	* @return void
//	* @throws
//	 */
//	private static void addStranger(Context context,
//			Map<String, Object> memInfo, String customerID, boolean isSave,
//			List<String> strangerList, JSONObject jsonObject_FRIEND,ChatUserHelper mDictionaryHelper) {
//		int type;
//		String groupName;
//		String id;
//		type = Tables.TableFriend.TYPE_ATTENTION_TO_ME;
//		groupName = context.getString(R.string.stranger);
//		id = CollectUtils.setCustomerInfo(context,memInfo, jsonObject_FRIEND,isSave?customerID:null,type, mDictionaryHelper).getId();
//		if(!strangerList.contains(id))
//			strangerList.add(id);
//	}
//
//	/**
//	 *
//	* @Title: addFriend
//	* @Description: 添加好友数据到friendList<我关注的人，id>和memInfo<id，个人信息>中
//	* @param @param context
//	* @param @param memInfo
//	* @param @param customerID
//	* @param @param isSave
//	* @param @param friendList
//	* @param @param jsonObject_FRIEND
//	* @return void
//	* @throws
//	 */
//	private static void addFriend(Context context, Map<String, Object> memInfo,
//			String customerID, boolean isSave, List<String> friendList,
//			JSONObject jsonObject_FRIEND,ChatUserHelper mDictionaryHelper) {
//		int type;
//		String groupName;
//		String id;
//		type = Tables.TableFriend.TYPE_ATTENTION_FOR_ME;
//		groupName = context.getString(R.string.friendList);
//		id = CollectUtils.setCustomerInfo(context,memInfo, jsonObject_FRIEND,isSave?customerID:null,type,mDictionaryHelper).getId();
//		if(!friendList.contains(id))
//			friendList.add(id);
//	}
//
//	/**
//	 *
//	* @Title: addBlacklist
//	* @Description: 添加-黑名单-数据到friendList<我关注的人，id>和memInfo<id，个人信息>中
//	* @param @param context
//	* @param @param memInfo
//	* @param @param customerID
//	* @param @param isSave
//	* @param @param blacklistList
//	* @param @param jsonObject_FRIEND
//	* @return void
//	* @throws
//	 */
//	private static void addBlacklist(Context context,
//			Map<String, Object> memInfo, String customerID, boolean isSave,
//			List<String> blacklistList, JSONObject jsonObject_FRIEND,ChatUserHelper mDictionaryHelper) {
//		int type;
//		String groupName;
//		String id;
//		type = Tables.TableFriend.TYPE_BLACKLIST;
//		groupName = context.getString(R.string.blacklist);
//		id = CollectUtils.setCustomerInfo(context,memInfo, jsonObject_FRIEND,isSave?customerID:null,type,mDictionaryHelper).getId();
//		if(!blacklistList.contains(id))
//			blacklistList.add(id);
//	}
//
//
//	/**
//	 * 聊天室列表返回数据处理
//	 * @param value
//	 */
//	private static void setGroupList(Context context,Map<String, List<String>> map,
//			Map<String, Object> memInfo,String value,String customerID,boolean isSave,ChatUserHelper mDictionaryHelper){
//		try {
//			//已关注的
//			String groupAtt =  context.getString(R.string.groupAtt);
//			//自己创建的
//			String groupCreate = context.getString(R.string.auditTopic);
//			map.put(groupAtt, new ArrayList<String>());
//			map.put(groupCreate, new ArrayList<String>());
//
//			JSONArray jsonArr = new JSONArray(value);
//			for(int i = 0; i< jsonArr.length(); i++){
//				JSONObject json = jsonArr.getJSONObject(i);
//				GroupInfoEntity group = CollectUtils.setGroupInfo(context,memInfo,json,
//						isSave?customerID:null,mDictionaryHelper);
//				String id = group.getId();
//
//				String groupName = "";
//				if(!group.getCreateCustomerID().equals(customerID))
//					groupName = groupAtt;
//				else
//					groupName = groupCreate;
//				List<String> list = map.get(groupName);
//				if(list != null){
//					if(!list.contains(id))
//							map.get(groupName).add(id);
//				}else{
//					list = new ArrayList<String>();
//					list.add(id);
//					map.put(groupName, list);
//				}
//			}
//			if(isSave)
//			  context.sendBroadcast(new Intent(CoreService.ACTION_GROUPLIST));
//		} catch (JSONException e) {
//			e.printStackTrace();
//		}
//	}
//}
