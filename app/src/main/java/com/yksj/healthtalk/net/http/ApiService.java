package com.yksj.healthtalk.net.http;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.IntRange;
import android.text.TextUtils;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.blankj.utilcode.util.LogUtils;
import com.library.base.utils.StorageUtils;
import com.yksj.consultation.app.AppContext;
import com.yksj.consultation.business.LoginBusiness;
import com.yksj.consultation.constant.DoctorHomeType;
import com.yksj.consultation.sonDoc.consultation.PlayVideoActiviy;
import com.yksj.consultation.utils.DoctorHelper;
import com.yksj.healthtalk.entity.CustomerInfoEntity;
import com.yksj.healthtalk.entity.GroupInfoEntity;
import com.yksj.healthtalk.entity.MessageEntity;
import com.yksj.healthtalk.net.socket.SmartControlClient;
import com.yksj.healthtalk.net.socket.SmartFoxClient;
import com.yksj.healthtalk.utils.FileUtils;

import org.apache.http.message.BasicNameValuePair;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 所有执行http请求操作调用此类
 * @author zhao
 */

public class ApiService {

    private static ApiRepository mRepository;// = new ApiRepository();

    public static AsyncHttpClient mAsyncHttpClient = new AsyncHttpClient();

    public static Map<String, String> getDefaultHeaders() {
        return mAsyncHttpClient.getHeaders();
    }

    public static ApiRepository getRepository() {
        return mRepository;
    }

    public static void setmRepository(ApiRepository mRepository) {
        ApiService.mRepository = mRepository;
    }

    public static void addHttpHeader(String key, String value) {
        if (mAsyncHttpClient != null) {
            mAsyncHttpClient.addHeader(key, value);
        }
        ApiConnection.addHeader(key, value);
    }

    /**
     * 取消网络请求
     * @param tag
     */
    public static void cancelRequest(Object tag) {
        ApiConnection.cancelTag(tag);
    }

    /**
     * 检查版本更新
     */
    public static void doHttpCheckAppVersion(String version, ApiCallbackWrapper callback) {
        Map<String, String> params = new HashMap<>();
        params.put("version", version);
        params.put("iostype", "3");

        ApiConnection.postAsyn(mRepository.URL_APP_VERSIONCHECK, params, callback);
    }

    /**
     * 绑定手机
     * @param userId
     */
    public static void doHttpConsultationSetPhoneBound(String phone, String code, String psw, String userId, AsyncHttpResponseHandler handler) {
        RequestParams requestParams = new RequestParams();
        requestParams.put("phone", phone);
        requestParams.put("code", code);
        requestParams.put("psw", psw);
        requestParams.put("customerid", userId);
        requestParams.put("consultation_center_id", AppContext.APP_CONSULTATION_CENTERID);
        mAsyncHttpClient.post(mRepository.HZCHANGEBINDINGPHONE210SERVLET, requestParams, handler);
    }

    /**
     * 解除手机绑定
     * @param userId
     * @param handler
     */
    public static void doHttpUnPhoneBind(String userId, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("customerid", userId);
        params.put("Type", "phone");
        params.put("CONSULTATION_CENTER_ID", AppContext.APP_CONSULTATION_CENTERID);
        mAsyncHttpClient.post(mRepository.URL_UNBIND_PHONE_EMAIL, params, handler);
    }


    public static String getGoogleMapUrl(String lt, String ln) {
        StringBuffer stringBuffer = new StringBuffer(
                "http://api.map.baidu.com/staticimage?zoom=17&markerStyles=s,A,0xff0000");
        stringBuffer.append("&width=200&height=200");
        stringBuffer.append("&center=");
        stringBuffer.append(ln);
        stringBuffer.append(",");
        stringBuffer.append(lt);
        stringBuffer.append("&markers=");
        stringBuffer.append(ln);
        stringBuffer.append(",");
        stringBuffer.append(lt);
        /*stringBuffer
                .append("&markerStyles=-1,http://www.h-tlk.mobi/DuoMeiHealth/small_location_mark.png");*/
//		stringBuffer.append(String.valueOf(lt));
//		stringBuffer.append(",");
//		stringBuffer.append(ln);
//		stringBuffer.append("&zoom=15");
/*		stringBuffer.append("&sensor=false");
		stringBuffer.append("&maptype=roadmap");
		stringBuffer.append("&format=png");
		stringBuffer.append("&mobile=true");*/
        return stringBuffer.toString();
    }

    /**
     * 程序异常日志报告
     */
    public static void doHttpReportAppException(String log, AsyncHttpResponseHandler handler) {
        RequestParams requestParams = new RequestParams();
        requestParams.put("log", log);
        requestParams.put("OSType", "1");
        mAsyncHttpClient.post(mRepository.URL_APPEXCEPTION_REPORT, requestParams, handler);
    }

    /**
     * 获取群文件列表
     * @param groupId
     * @param callback
     */
    public static void groupFiles(String groupId, ApiCallbackWrapper callback){
        Map<String, String> params = new HashMap<>();
        params.put("op", "queryGroupFileList");
        params.put("group_id", groupId);
        ApiConnection.postAsyn(mRepository.TALKHISTORYSERVLETS, params, callback, callback);
    }

    /**
     * 群文件上传
     */
    public static void groupFileUpload(String doctorId, String groupId, String uploadPath, String uploadType, ApiCallbackWrapper callback){
        Map<String, File> fileParams = new HashMap<>();
        fileParams.put("file", new File(uploadPath));

        Map<String, String> params = new HashMap<>();
        params.put("customer_id", doctorId);
        params.put("group_id", groupId);
        params.put("file_type", uploadType);

        ApiConnection.getUploadDelegate().postAsyn(mRepository.UPLOADGROUPFILESERVLET, fileParams, params, true, false, callback, callback);
    }

    /**
     * 查找话题
     * @param mGroupInfoEntity
     * @param sourceType       (0---全部，1---最新，2---最热，3---推荐)
     * @param handler
     */
    public static void doHttpRequestSearchGroup(
            GroupInfoEntity mGroupInfoEntity, int sourceType,
            AsyncHttpResponseHandler handler) {
        // PAGESIZE int
        // PAGENUM int
        // CUSTOMERID int
        try {
            JSONObject object = new JSONObject();
            object.put("TYPE", mGroupInfoEntity.getType());
            // 页数
            object.put("PAGESIZE", mGroupInfoEntity.getPagesize());
            // 数量
            object.put("PAGENUM", mGroupInfoEntity.getPagenum());
            // userid
            if (mGroupInfoEntity.getCreateCustomerID().equals("")) {
                return;
            } else {
                object.put("CUSTOMERID",
                           Integer.valueOf(mGroupInfoEntity.getCreateCustomerID()));
            }

            switch (mGroupInfoEntity.getType()) {
                // 按照名称搜索话题
                /*
                 * case 0: object.put("GROUPNAME", mGroupInfoEntity.getAccount());
                 * break;
                 */
                // 最新最热推荐
                case 1:
                    // UPPERID String
                    // INFOID String
                    // FLAG int
                    // SOURCETYPE int (0---全部，1---最新，2---最热，3---推荐)
                    String name = mGroupInfoEntity.getName();
                    object.put("GROUPNAME", name == null ? "" : name);
                    object.put(
                            "UPPERID",
                            mGroupInfoEntity.getUpperId() != null ? mGroupInfoEntity
                                    .getUpperId() : "0");
                    object.put("FLAG", mGroupInfoEntity.getFlag());
                    object.put("FLAGPLACING", mGroupInfoEntity.getFlagPlacing());
                    object.put("SOURCETYPE", sourceType);
                    object.put(
                            "INFOID",
                            mGroupInfoEntity.getInfoId() != null ? mGroupInfoEntity
                                    .getInfoId() : "0");
                    break;
                // 点击广告搜索话题
                case 2:
                    object.put("ADVERID", "");
                    object.put("FLAG", 2);
                    break;
                default:
                    break;
            }

            // 网络请求
            RequestParams params = new RequestParams();
            params.put("PARAMETER", object.toString());
            mAsyncHttpClient.post(mRepository.URL_SEARCH_SALON, params, handler);

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }


    /**
     * 加载话题数据
     * @param customerId  用户Id
     * @param flagPlacing 第几页的标记
     * @param chargeFlag  是否收费   0-免费  1-收费  2-全部   100-生活话题
     * @param handler
     */
    public static void doHttpRequestLoadTopic(
            String customerId, String flagPlacing, int chargeFlag,
            AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("CUSTOMERID", customerId);
        params.put("FLAGPLACING", flagPlacing);
        if (chargeFlag < 3) {
            params.put("CHARGINGFLAG", "" + chargeFlag);
            params.put("TYPE", "findDocGroupList");
        } else {//findNotDocGroupList
            params.put("TYPE", "findNotDocGroupList");
        }
        mAsyncHttpClient.get(mRepository.NEWFINDGROUPS420, params, handler);

    }


    /**
     * 加载搜索的话题数据
     * @param secondName 可能是id或者搜索的名字
     * @param searchType 加载数据的类型1-根据话题标签Id查询,2-根据话题名字查询
     * @param handler
     */
    public static void doHttpRequestLoadSearchTopic(
            String customerId, int pageSize, int searchType, String secondName,
            AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("CUSTOMERID", customerId);
        if (searchType == 1) {
            params.put("TYPE", "findGroupListByInfoLayId");
            params.put("INFOLAYID", secondName);
        } else if (searchType == 2) {
            params.put("TYPE", "findGroupListByName");
            params.put("RECORDNAME", secondName);
        }
        params.put("PAGESIZE", "" + pageSize);
        params.put("PAGENUM", "" + 20);//每页显示20条
        mAsyncHttpClient.get(mRepository.NEWFINDGROUPS420, params, handler);
    }


    /**
     * 加载搜索时的各种标签及其对应的id
     */
    public static void doHttpRequestSearchInterest(
            String type, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("TYPE", type);
        mAsyncHttpClient.get(mRepository.SHARESERVLET, params, handler);
//        http://220.194.46.204:80/DuoMeiHealth/InterestwallInfo
//        mAsyncHttpClient.get("http://220.194.46.204:80/sms_web/InterestwallInfo", params, handler);
    }


    /**
     * 按类型显示好友列表
     * sourceType 0--最新 1--活跃 2--推荐 3--附近 4--同城
     * TYPE   findFriendsByThreeCate之前的三种情况  附近  活跃  个性
     */
    public static void doHttpRequestSearchFriends1(CustomerInfoEntity c,
                                                   int sourceType, AsyncHttpResponseHandler handler) {
        // SOURCETYPE 0--最新 1--活跃 2--推荐 3--附近 4--同城
        int sex = 0;
        String mSex;
        // 判断性别
        if ((mSex = c.getSex()) != null) {
            if (mSex.equalsIgnoreCase("2")) {
                sex = 2;
            } else if (mSex.equalsIgnoreCase("1")) {
                sex = 1;
            }
        }
        JSONObject object = new JSONObject();
        try {
            object.put("LATITUDE", c.getLatitude() != null ? c.getLatitude() : "");
            // 纬度
            object.put("LONGITUDE", c.getLongitude() != null ? c.getLongitude() : "");
            // 下一页的标记
            object.put("FLAG", c.getFlag());
            // TYPE 0--找朋友 1--找医生 2--相同经历
            object.put("TYPE", c.getType());
            object.put("CUSTOMERID", c.getId());
            // 0--最新 1--活跃 2--推荐 3--附近 4--同城
            object.put("SOURCETYPE", sourceType);
            object.put("COUNT", "");

            if (c.getType() == 0) {
                object.put("MINAGE", c.getMinAge());
                object.put("MAXAGE", c.getMaxAge() != 0 ? c.getMaxAge() : 100);
                // 个性标签
                object.put("INTEREST", c.getInterestCode() != null ? c.getInterestCode() : "");
                object.put("SEX", sex);
                // 地区编码
                object.put("AREA", c.getAreaCode());
                object.put("USERNAME", c.getUsername() != null ? c.getUsername() : "");
            } else if (c.getType() == 1) {
                // 医生姓名
                object.put("DOCTORNAME", c.getRealname() != null ? c.getRealname() : "");
                // 专长
                object.put("DOCTORSPECIALLY", c.getSpecial() != null ? c.getSpecial() : "");
                // 医生职称编码
                object.put(
                        "DOCTORTITLE",
                        c.getDoctorTitle() != null ? Integer.valueOf(c.getDoctorTitle()) : 0);
                // 科室编码
                object.put("DOCTOROFFICE", c.getOfficeCode1());
                object.put("DOCTORHOSPITAL",
                           c.getHospital() != null ? c.getHospital() : "");
                object.put("ORDERONOFF", c.getOrderOnOff());
                // 地区编码
                object.put("AREA", c.getAreaCode());
            } else {
                // 相同经历
                object.put("BODY", c.getSameExperienceCode());
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // 网络请求
        RequestParams params = new RequestParams();
        //我的修改
//		LogUtil.d("DDD", object.toString()+"<<<<-");
//		params.put("VALID_MARK", "40");
        params.put("PARAMETER", object.toString());
        params.put("TYPE", "findFriendsByThreeCate");
        mAsyncHttpClient.post(mRepository.URL_FIND_FRIENDS, params, handler);
    }

    /**
     * 按条件搜索好友    TYPE=findFriendsByParam 按条件查询
     * @param c
     * @param pageSize
     * @param handler
     */
    public static void doHttpRequestSearchFriends2(CustomerInfoEntity c,
                                                   int pageSize, AsyncHttpResponseHandler handler) {
        // SOURCETYPE 0--最新 1--活跃 2--推荐 3--附近 4--同城
        int sex = 0;
        String mSex;
        // 判断性别
        if ((mSex = c.getSex()) != null) {
            if (mSex.equalsIgnoreCase("2")) {
                sex = 2;
            } else if (mSex.equalsIgnoreCase("1")) {
                sex = 1;
            }
        }
        JSONObject object = new JSONObject();
        try {
            object.put("LATITUDE", c.getLatitude() != null ? c.getLatitude() : "");
            // 纬度
            object.put("LONGITUDE", c.getLongitude() != null ? c.getLongitude() : "");
            // 下一页的标记
            object.put("FLAG", c.getFlag());
            // TYPE 0--找朋友 1--找医生 2--相同经历
            object.put("TYPE", c.getType());
            object.put("CUSTOMERID", c.getId());
            // 0--最新 1--活跃 2--推荐 3--附近 4--同城
            object.put("SOURCETYPE", "");
            object.put("COUNT", "");
            object.put("PAGESIZE", pageSize);//第几页
            object.put("PAGENUM", 20);//每页显示20条
            if (c.getType() == 0) {
                object.put("MINAGE", c.getMinAge());
                object.put("MAXAGE", c.getMaxAge() != 0 ? c.getMaxAge() : 100);
                // 个性标签
                object.put("INTEREST", c.getInterestCode() != null ? c.getInterestCode() : "");
                object.put("SEX", sex);
                // 地区编码
                object.put("AREA", c.getAreaCode());
                object.put("USERNAME", c.getUsername() != null ? c.getUsername() : "");
            } else if (c.getType() == 1) {
                // 医生姓名
                object.put("DOCTORNAME", c.getRealname() != null ? c.getRealname() : "");
                // 专长
                object.put("DOCTORSPECIALLY", c.getSpecial() != null ? c.getSpecial() : "");
                // 医生职称编码
                object.put(
                        "DOCTORTITLE",
                        c.getDoctorTitle() != null ? Integer.valueOf(c.getDoctorTitle()) : 0);
                // 科室编码
                object.put("DOCTOROFFICE", c.getOfficeCode1());
                object.put("DOCTORHOSPITAL",
                           c.getHospital() != null ? c.getHospital() : "");
                object.put("ORDERONOFF", c.getOrderOnOff());
                // 地区编码
                object.put("AREA", c.getAreaCode());
            } else {
                // 相同经历
                object.put("BODY", c.getSameExperienceCode());
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // 网络请求
        RequestParams params = new RequestParams();
        params.put("PARAMETER", object.toString());
        //我的修改
//		LogUtil.d("DDD", object.toString()+"<<<__");
//		params.put("VALID_MARK", "40");
        params.put("TYPE", "findFriendsByParam");
        mAsyncHttpClient.post(mRepository.URL_FIND_FRIENDS, params, handler);
//		mAsyncHttpClient.post("http://192.168.16.157:8899/DuoMeiHealth/NewFindFriends420", params, handler);
    }


    /**
     * 更新广告点击次数
     */
    public static void doHttpUpdateAdverCount(String adverid, String userid) {
        RequestParams params = new RequestParams();
        params.put("ADVERID", adverid);
        params.put("CUSTOMERID", userid);
        mAsyncHttpClient.post(mRepository.URL_ADDADVCLICKREQ, null);
    }


    /**
     * 新闻请求
     * @param params
     * @param handler
     */
    public static void doHttpNews(RequestParams params,
                                  AsyncHttpResponseHandler handler) {
        mAsyncHttpClient.get(mRepository.URL_NEWS, params, handler);
    }


    /**
     * 医书查询症状
     * @param position  部位id
     * @param situation 症状id
     */
    public static void doHttpQuerySituations(String position, String situation, String nameKeys,
                                             AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        if (position != null)
            params.put("positionCode", position);
        if (situation != null)
            params.put("situationCode", situation);
        if (nameKeys != null) params.put("nameKeys", nameKeys);
        mAsyncHttpClient.get(mRepository.URL_QUERYSITUATIONSSERVLET, params,
                             handler);
    }


    /**
     * 疾病查询
     * @param option
     * @param positionCode
     * @param diseaseCode
     */
    public static void doHttpQueryDiseasesServlet(String option,
                                                  String positionCode, String diseaseCode, String diseaseSystemName,
                                                  String officeCode, String nameKeys, AsyncHttpResponseHandler handler) {
        RequestParams requestParams = new RequestParams();
        if (option != null)
            requestParams.put("option", option);
        if (positionCode != null)
            requestParams.put("positionCode", positionCode);
        if (diseaseCode != null)
            requestParams.put("diseaseCode", diseaseCode);
        if (diseaseSystemName != null)
            requestParams.put("diseaseSystemName", diseaseSystemName);
        if (officeCode != null)
            requestParams.put("officeCode", officeCode);
        if (nameKeys != null) requestParams.put("nameKeys", nameKeys);
        mAsyncHttpClient.get(mRepository.URL_QUERYDISEASESSERVLET, requestParams,
                             handler);
    }

    /**
     * 医院查询
     * @param option
     * @param level
     * @param areaCode
     * @param unitType
     * @param unitLevel
     * @param handler
     */
    public static void doHttpQueryUnitsServlet(String option, String level,
                                               String areaCode, String unitType, String unitLevel,
                                               String unitCode, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        if (option != null)
            params.put("option", option);
        if (level != null)
            params.put("level", level);
        if (areaCode != null)
            params.put("areaCode", areaCode);
        if (unitLevel != null)
            params.put("areaCode", unitLevel);
        if (unitType != null)
            params.put("unitType", unitType);
        if (unitCode != null)
            params.put("unitCode", unitCode);
        mAsyncHttpClient.get(mRepository.URL_QUERYUNITSSERVLET, params, handler);
    }

    /**
     * 药品查询
     * @param option
     * @param generalCode
     * @param medicineCode
     * @param diseaseCode
     * @param scopesID
     * @param handler
     */
    public static void doHttpQueryMedicinesServlet(String option,
                                                   String generalCode, String medicineCode, String diseaseCode,
                                                   String scopesID, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        if (option != null)
            params.put("option", option);
        if (generalCode != null)
            params.put("generalCode", generalCode);
        if (medicineCode != null)
            params.put("medicineCode", medicineCode);
        if (diseaseCode != null)
            params.put("diseaseCode", diseaseCode);
        if (scopesID != null)
            params.put("scopesID", scopesID);
        mAsyncHttpClient.get(mRepository.URL_QUERYMEDICINESSERVLET, params,
                             handler);
    }


    public static void doHttpQueryUserData(String cusid, String shopId,
                                           String classId, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("CUSTOMERID", cusid);
        params.put("SHOPID", shopId);
        params.put("CLASSID", classId);
        mAsyncHttpClient.post(mRepository.QUERY_USER_DATA, params, handler);
    }


    /**
     * 创建新话题
     * @param json
     */
    public static void doHttpNewSalon(Context context, String json,
                                      File file, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        try {
            if (file != null && file.exists()) {
                params.put("file1", file);
            } else {
                params.putNullFile("file1", new File(""));
            }
            org.json.JSONObject jsonObject = new org.json.JSONObject(json);
            Iterator<String> itr = jsonObject.keys();
            while (itr.hasNext()) {
                String str = itr.next();
                params.put(str, jsonObject.getString(str));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
//		mAsyncHttpClient.post("http://220.194.46.204:80/DuoMeiHealth/NewGroupServlet420",params,handler);
        mAsyncHttpClient.post(mRepository.URL_CREATE_SALON, params, handler);
    }


    public static void doHttpSetAuthority(String type, String classId,
                                          String shopid, String cusids, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("TYPE", type);
        params.put("CLASSID", classId);
        params.put("SHOPID", shopid);
        if (type.equals("3")) {
            params.put("CUSTOMERIDS", cusids);
        }
        mAsyncHttpClient.get(mRepository.PHOTO_OPEN_LEVEL, params, handler);
    }


    /**
     * 发送语音文件
     * @param entity
     * @param handler
     */
    public static void doHttpSendChatVoiceMesg(MessageEntity entity,
                                               int groupType, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        if (groupType == 2) {
            params.put("server_code", "7045");
            params.put("allCustomerId", entity.getAllCustomerId());
        } else if (groupType == 1) {
            params.put("server_code", SmartControlClient.SIX_ONE_SEND_MSG + "");
            params.put("allCustomerId", entity.getAllCustomerId());
        } else if (groupType == 3) {//特殊服务
            params.put("server_code", SmartControlClient.SERVICE_SINGLE_SEND_MSG + "");
        } else {
            params.put("server_code", "7018");
        }
        params.put("serverId", entity.getId());
        params.put("customerId", entity.getSenderId());
        params.put("isGroupMessage", groupType + "");
        params.put("timeStamp", String.valueOf(System.currentTimeMillis()));
        params.put("sms_target_id", entity.getReceiverId());
        params.put("type", String.valueOf(entity.getType()));
        params.put("Object_Type", "10");
        params.put("consultation_id", entity.getConsultationId());
        params.put("duration", entity.getVoiceLength());
        params.put("isDoctorMessage", entity.getIsDoctorMessage());
        params.put("order_id", entity.getOrderId());
        try {
            ;
            File file = new File(StorageUtils.getVoicePath(),
                                 entity.getContent());
            params.put("FILE", file);
            mAsyncHttpClient
                    .post(mRepository.URL_SENDPICSERVLET, params, handler);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            handler.onFailure(null, null);
        }
    }

    /**
     * 聊天发送图片
     * @param entity
     */
    public static void doHttpSendChatImageMesg(MessageEntity entity, int groupType, ApiCallback callback) {
        HashMap<String, String> params = new HashMap<>();
        if (groupType == 2) {
            params.put("server_code", "7045");
            params.put("allCustomerId", entity.getAllCustomerId());
        } else if (groupType == 1) {
            params.put("server_code", SmartControlClient.SIX_ONE_SEND_MSG + "");
            params.put("allCustomerId", entity.getAllCustomerId());
        } else if (groupType == 3) {//特殊服务
            params.put("server_code", SmartControlClient.SERVICE_SINGLE_SEND_MSG + "");
        } else {
            params.put("server_code", "7018");
        }
        params.put("serverId", entity.getId());
        params.put("customerId", entity.getSenderId());
        params.put("isGroupMessage", groupType + "");
        params.put("timeStamp", String.valueOf(System.currentTimeMillis()));
        params.put("sms_target_id", entity.getReceiverId());
        params.put("type", String.valueOf(entity.getType()));
        params.put("Object_Type", "10");
        params.put("consultation_id", entity.getConsultationId());
        params.put("duration", entity.getVoiceLength());
        params.put("sms_req_content", "您有一张新图片");
        params.put("isDoctorMessage", entity.getIsDoctorMessage());
//        params.put("isDoctorMessage", "1");
        params.put("order_id", entity.getOrderId());

        Map<String, File> fileParams = new HashMap<>();
        fileParams.put("icon", new File(entity.getContent()));
        fileParams.put("image", new File(entity.getContent()));
        ApiConnection.addHeader("username", DoctorHelper.getAccount());
        ApiConnection.addHeader("client_type", AppContext.CLIENT_TYPE);
        ApiConnection.getUploadDelegate().postAsyn(mRepository.URL_SENDPICSERVLET, fileParams, params, true, false, callback, callback);
    }

    /**
     * 聊天发送视频
     * @param entity
     * @param handler
     */
    public static void doHttpSendChatVideoMesg(MessageEntity entity,
                                               int groupType, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        if (groupType == 2) {
            params.put("server_code", "7045");
            params.put("allCustomerId", entity.getAllCustomerId());
        } else if (groupType == 1) {
            params.put("server_code", SmartControlClient.SIX_ONE_SEND_MSG + "");
            params.put("allCustomerId", entity.getAllCustomerId());
        } else if (groupType == 3) {//特殊服务
            params.put("server_code", SmartControlClient.SERVICE_SINGLE_SEND_MSG + "");
        } else {
            params.put("server_code", "7018");
        }
        params.put("serverId", entity.getId());
        params.put("customerId", entity.getSenderId());
        params.put("isGroupMessage", groupType + "");
        params.put("timeStamp", String.valueOf(System.currentTimeMillis()));
        params.put("sms_target_id", entity.getReceiverId());
        params.put("type", String.valueOf(entity.getType()));
        params.put("Object_Type", "10");
        params.put("consultation_id", entity.getConsultationId());
        params.put("duration", entity.getVoiceLength());
        params.put("sms_req_content", "视频");
        params.put("isDoctorMessage", entity.getIsDoctorMessage());
        params.put("order_id", entity.getOrderId());
        try {
            File bigFile2 = new File(entity.getContent());
            File miniFile2 = FileUtils.saveChatPhotoBitmapToFile(PlayVideoActiviy.getVideoThumbnail(entity.getContent()));
            params.put("icon", miniFile2);
            params.put("image", bigFile2);
            mAsyncHttpClient
                    .post(mRepository.URL_SENDPICSERVLET, params, handler);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            handler.onFailure(null, null);
        }

    }


    /**
     * 根据经纬度查询位置
     * @param latitude
     * @param longitude
     */
    public static void doHttpQueryMapAddress(String latitude, String longitude,
                                             AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("latlng", longitude + "," + latitude);
        params.put("sensor", "true");
        params.put("language", "zh-CN");
        mAsyncHttpClient.get(mRepository.GOOLE_MAP_GECODE, params, handler);
    }

    /**
     * 语音文件下载
     * @param path
     * @param handler
     */
    public static void doHttpDownChatFile(String path,
                                          AsyncHttpResponseHandler handler) {
        mAsyncHttpClient.get(mRepository.URL_QUERYHEADIMAGE + path, handler);
    }

    /**
     * 按昵称和账号查找好友
     * @param userid
     * @param pagenum
     * @param pagesize
     * @param duomeiNum
     * @param type      如果TYPE=1 只搜索出医生， 如果TYPE=0 只搜索出客户
     * @param handler
     */
    public static void doHttpFriendExactSearch(String userid, String pagenum,
                                               String pagesize, String duomeiNum, int type,
                                               AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("CUSTOMERID", userid);
        params.put("ACCOUNTS", duomeiNum);
        params.put("PAGENUM", pagenum);
        params.put("PAGESIZE", pagesize);
        params.put("TYPE", String.valueOf(type));
        mAsyncHttpClient.get(mRepository.URL_FRIENDEXACTSEARCH, params, handler);
    }


    /**
     * 购买门票
     * @param groupid
     * @param customerid
     * @param tickettype
     * @param handler
     */
    public static void doHttpBuyTicket(String groupid, String customerid,
                                       String tickettype, String type, AsyncHttpResponseHandler handler) {
        // String groupId = request.getParameter("GROUPID");
        // String customerId = request.getParameter("CUSTOMERID");
        // String ticType = request.getParameter("TICKETTYPE");
        // String payAccount = request.getParameter("PAYACCOUNT");//支付账户
        // String payType =
        // request.getParameter("PAYTYPE");//--10-支付宝--20-银行类型--30-财富通
        RequestParams params = new RequestParams();
        params.put("GROUPID", groupid);
        params.put("CUSTOMERID", customerid);
        params.put("TICKETTYPE", tickettype);
        params.put("PAYTYPE", type);
        params.put("PAYACCOUNT", "");
        mAsyncHttpClient.get(mRepository.URL_BUYTICKET, params, handler);
    }

    /**
     * 购买门票
     * @param groupid
     * @param customerid
     * @param tickettype
     * @param handler
     */
    public static void doHttpUnionBuyTicket(String groupid, String customerid,
                                            String tickettype, String type, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("GROUPID", groupid);
        params.put("CUSTOMERID", customerid);
        params.put("TICKETTYPE", tickettype);
        params.put("PAYTYPE", type);
        params.put("PAYACCOUNT", "");
        mAsyncHttpClient.get(mRepository.SALONUNIONPAYPAYMENT, params, handler);
    }


    /**
     * 购买门票
     * @param groupid
     * @param customerid
     * @param tickettype
     * @param handler
     */
    public static void doHttpWalletBuyTicket(String PAYMENT_PASSWORD, String groupid, String customerid,
                                             String tickettype, String type, AsyncHttpResponseHandler handler) {
        // String groupId = request.getParameter("GROUPID");
        // String customerId = request.getParameter("CUSTOMERID");
        // String ticType = request.getParameter("TICKETTYPE");
        // String payAccount = request.getParameter("PAYACCOUNT");//支付账户
        // String payType =
        // request.getParameter("PAYTYPE");//--10-支付宝--20-银行类型--30-财富通
        RequestParams params = new RequestParams();
        params.put("GROUPID", groupid);
        params.put("CUSTOMERID", customerid);
        params.put("TICKETTYPE", tickettype);
        params.put("PAYTYPE", type);
        params.put("PAYACCOUNT", "");
        params.put("PAYMENT_PASSWORD", PAYMENT_PASSWORD);
        params.put("validMark", "40");
        mAsyncHttpClient.get(mRepository.SALONWALLETPAYMENT, params, handler);
    }


    /**
     */
    public static void doHttpJoinGroupChating(String cusid, String groupID,
                                              String messge, String isBL, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("CUSTOMERID", cusid);
        params.put("GROUPID", groupID);
        params.put("GROUPMESSAGE", messge);
        params.put("isBL", isBL);
        mAsyncHttpClient.get(mRepository.HZINGROUPSERVLET, params, handler);
    }


    public static void doHttpUpdateGroupInceptMsg(String customerId,
                                                  String groupId, Boolean inceptMessage,
                                                  Boolean releaseSystemMessage, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("CUSTOMERID", customerId);
        params.put("GROUPID", groupId);
        if (inceptMessage) {
            params.put("INCEPTMESSAGE", "Y");
        } else {
            params.put("INCEPTMESSAGE", "N");
        }
        if (releaseSystemMessage) {
            params.put("RELEASESYSTEMMESSAGE", "1");
        } else {
            params.put("RELEASESYSTEMMESSAGE", "0");
        }
        mAsyncHttpClient.get(mRepository.URL_UPDATEGROUPINCEPTMSG, params,
                             handler);
    }


    /**
     * 地图操作
     * @param params
     * @param handler
     */
    private static void doHttpAreaMap(RequestParams params,
                                      AsyncHttpResponseHandler handler) {
        mAsyncHttpClient.get(mRepository.URL_QUERYDOMAINPROPDEFINE, params,
                             handler);
    }

    /**
     * @param customerid
     * @param id         商户id
     */
    public static void doHttpServerCneterBg(String width, String height,
                                            String customerid, String id,
                                            AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("customerid", customerid);
        params.put("merchantid", id);
        params.put("SYS_CLASS_ID", "Android");
        params.put("SCREEN_X", 480 + "");
        params.put("SCREEN_Y", 800 + "");
        params.put("PICTYPE", "1");
//		params.put("PROP_ID", propId);
        mAsyncHttpClient.get(mRepository.URL_SERVER_BG_ALL_NEW, params, handler);
    }


    /**
     * @param customerid
     * @param id         二维码扫描商户Id
     */
    public static void doHttpServerMerchant(String width, String height,
                                            String customerid, String id, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("customerid", customerid);
        params.put("merchantid", id);
        params.put("SYS_CLASS_ID", "Android");
        params.put("SCREEN_X", 480 + "");
        params.put("SCREEN_Y", 800 + "");
        params.put("PICTYPE", "1");
        params.put("Type", "queryCenterMerchantByDc");
        mAsyncHttpClient.get(mRepository.URL_SERVER_BG_ALL33, params, handler);
    }


    /**
     * 预约
     * @param cusId
     * @param doctorId
     * @param handler
     * @param SERVICE_TYPE_ID 1为普通服务，2为预约时段服务,3为预约面访服务
     */
    public static void doHttpEngageTheDialogue(String cusId, String doctorId, String SERVICE_TYPE_ID,
                                               AsyncHttpResponseHandler handler) {
        // CUSTOMER_ID自己的客户id
        // DOCTORID 被点击人的客户id
        RequestParams params = new RequestParams();
        params.put("Type", "IsCanTalk");
        params.put("CUSTOMER_ID", cusId);
        params.put("DOCTORID", doctorId);
        params.put("SERVICE_TYPE_ID", SERVICE_TYPE_ID);
        mAsyncHttpClient.get(mRepository.URL_SERVICESETSERVLET42, params, handler);
    }


    /**
     * 根据Id查询个人资料
     * @param friendId
     * @param userId
     * @param handler
     */
    public static void doHttpFindCustomerInfoByCustId(String type,
                                                      String qrCode, String friendId, String userId,
                                                      AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        if (!TextUtils.isEmpty(qrCode)) {
            params.put("TYPE", type);
            params.put("QRCODE", qrCode);
        } else {
            params.put("CUSTOMERID", friendId);
        }
        params.put("MYCUSTOMERID", SmartFoxClient.getLoginUserId());
        params.put("VALID_MARK", AppContext.APP_VALID_MARK);
        mAsyncHttpClient.get(mRepository.URL_FINDCUSTOMERINFOBYCUSTID, params, handler);
    }

    /**
     * 根据Id查询个人资料(新版六一健康)
     * 192.168.16.45:8899/DuoMeiHealth/ConsultationInfoSet?TYPE=findPatientInfo&CUSTOMERID=
     */
    public static void doGetCustomerInfoByCustId(String friendId, ApiCallback callback, Object tag) {
        List<BasicNameValuePair> pairs = new ArrayList<>();
        pairs.add(new BasicNameValuePair("TYPE", "findPatientInfo"));
        pairs.add(new BasicNameValuePair("CUSTOMERID", friendId));
        pairs.add(new BasicNameValuePair("FLAG", "0"));
        ApiConnection.getAsyn(mRepository.DUOMEIHEALTH, pairs, callback, tag);
    }

    /**
     * 关于点赞的操作,
     * @param context    上下文
     * @param praiseFlag 现在是否已经点过赞,点过赞则执行取消点赞操作,未点赞则执行点赞操作
     * @param entity     对方实体
     * @param handler
     */
    public static void doHttpOperatePraiseToFriend(Context context, boolean praiseFlag,
                                                   CustomerInfoEntity entity, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("CUSTOMERID", entity.getId());
        params.put("MYCUSTOMERID", SmartFoxClient.getLoginUserId());
        if (praiseFlag) {
            mAsyncHttpClient.post(mRepository.FRIENDINFOCANCELLIKEDOC, params, handler);//取消点赞
        } else {
            mAsyncHttpClient.post(mRepository.FRIENDINFOLIKEDOC, params, handler);//点赞
        }
    }


    /**
     * 删除群消息
     * @param grouId  群id
     * @param type    1全部删除
     * @param ids     id字符串
     * @param handler
     */
    public static void deleteGroupMessages(String grouId, int type, String ids,
                                           AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        if (type == 1) {// 全部删除
            params.put("Type", "deleteGroupAllTalkmessage");
        } else {
            params.put("Type", "deleteGroupTalkmessage");
            params.put("offids", ids);
        }
        params.put("groupid", grouId);
        mAsyncHttpClient.get(
                mRepository.URL_DeleteCustomerGroupChatLog, params,
                handler);
    }


    /**
     * 删除单聊
     * @param type    1全部删除
     * @param ids     id字符串
     * @param handler "Type" ＝ ”deleteTalkHistory"
     *                "customerId" ＝ SelfID
     *                "offids" ＝ str;
     *                <p/>
     *                "Type" ＝ ”deleteAllTalkHistory"
     *                "customerId" ＝ SelfID
     *                "sms_target_id" ＝ self.customer.customerid //对方id
     */
    public static void deleteCustomPersonMessages(String chatUId, int type, String ids,
                                                  AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        if (type == 1) {// 全部删除
            params.put("Type", "deleteAllTalkHistory");
            params.put("sms_target_id", chatUId);
        } else {
            params.put("Type", "deleteTalkHistory");
            params.put("offids", ids);
        }
        params.put("customerId", SmartFoxClient.getLoginUserId());
        mAsyncHttpClient.get(mRepository.DELETETALKHISTORYSERVLET, params,
                             handler);
    }


    /**
     * 更改医师资料(独立app)
     * @param requestParams
     */
    public static void doHttpDoctorQualificationConsultation(RequestParams requestParams,
                                                             AsyncHttpResponseHandler handler) {

//		mAsyncHttpClient.post("http://192.168.16.45:8899/DuoMeiHealth/DoctorQualificationConsultation", requestParams,
//				handler);
        mAsyncHttpClient.post(mRepository.DOCTORQUALIFICATIONCONSULTATION, requestParams,
                              handler);
    }

    /**
     * 修改个人资料
     * @param requestParams
     * @param handler
     */
    public static void doHttpUpdatePerson(RequestParams requestParams,
                                          AsyncHttpResponseHandler handler) {
        mAsyncHttpClient.post(mRepository.URL_UPDATE_CUSTOMINFO, requestParams,
                              handler);
    }


    /**
     * 会诊生成二维码
     * @param id      商户ID 或者是 用户ID
     * @param handler
     * @param type    0 个人 1 是商户2一健康
     */
    public static void doHttpHZGenerateDimensionalCode(int type, String id,
                                                       AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        switch (type) {
            case 2:
                mAsyncHttpClient.post(mRepository.URL_YIJIANKANG,
                                      params, handler);
                break;
            case 1:
                params.put("MERCHANT_ID", id);
                mAsyncHttpClient.post(mRepository.URL_CENTERDIMENSIONALCODESERVLET,
                                      params, handler);
                break;
            default:
                params.put("customerid", id);
                mAsyncHttpClient.post(mRepository.URL_HZGENERATEDIMENSIONALCODESERVLET,
                                      params, handler);
                break;
        }

    }


    /**
     * 医生馆选择服务类型
     * @param handler
     */
    public static void doHttpDoctorSelectService(AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        mAsyncHttpClient.get(mRepository.FINDSERVICETYPE, params, handler);
    }


    /**
     * 科室查询
     * @param handler
     */
    public static void doHttpFindkeshi(AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("flag", "2");
        mAsyncHttpClient.get(mRepository.FindOfficeHasDoctor, params, handler);
    }


    /**
     * 创建特殊人群
     * Type=insertSpecialPriceGroup
     * SPECIAL_GROUP 服务特殊收费人群名称
     * SPECIAL_PRICE 特殊服务金额
     */
    public static void doHttpCreateDoctorServiceGroup(
            String SPECIAL_GROUP, int SPECIAL_PRICE, String SERVICE_ITEM_ID, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("SPECIAL_GROUP", SPECIAL_GROUP);
        params.put("Type", "insertSpecialPriceGroup");
        params.put("SPECIAL_PRICE", SPECIAL_PRICE + "");
        params.put("SERVICE_ITEM_ID", SERVICE_ITEM_ID);
        mAsyncHttpClient.get(mRepository.DOCTORFREE_ZONE, params, handler);
    }

    /**
     * 查询特殊收费人群
     * @param id
     * @param handler Type=querySpecialPriceGroup
     *                SERVICE_ITEM_ID 医生服务项目ID
     */
    public static void doHttpDoctorGroupList(String id, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("SERVICE_ITEM_ID", id);
        params.put("Type", "querySpecialPriceGroup");
        mAsyncHttpClient.get(mRepository.DOCTORSETTINGUI, params, handler);
    }


    /**
     * 删除特殊收费人群
     * Type=deleteSpecialPriceGroup
     * SPECIAL_GROUP_ID 服务特殊收费人群ID
     * @param id
     * @param handler
     */
    public static void doHttpDeleteServiceGroup(String id, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("SPECIAL_GROUP_ID", id);
        params.put("Type", "deleteSpecialPriceGroup");
        mAsyncHttpClient.get(mRepository.DOCTORFREE_ZONE, params, handler);
    }

    /**
     * 客户查询医生公告板
     * OPTION=QUERYALL
     * CUSTOMERID 接受留言的客户ID
     * MESSAGECUSTOMERID 留言的客户ID
     * LOADPAGE 加载方式，默认为0
     * 注：医生客户时：CUSTOMERID与MESSAGECUSTOMERID均为医生客户ID
     */
    public static void doHttpLookLeaveMessage(String CUSTOMERID, String loadNum, String date, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
//		params.put("OPTION", "QUERYALL");
        params.put("OPTION", "QUERYALLPUBLISH");
        params.put("CUSTOMERID", CUSTOMERID);
        params.put("LOADPAGE", loadNum);
        params.put("DATE", date);
        params.put("MESSAGECUSTOMERID", SmartFoxClient.getLoginUserId());
        mAsyncHttpClient.get(mRepository.LEAVEMESSAGE, params, handler);
    }


    /**
     * 删除留言
     * @param str
     * @param handler
     */
    public static void doHttpdeleteDoctorMessage(String str, AsyncHttpResponseHandler handler) {
        // TODO Auto-generated method stub
        RequestParams params = new RequestParams();
        params.put("OPTION", "DELETE");
        params.put("CUSTOMERID", SmartFoxClient.getLoginUserId());
        params.put("INFO", str);
        mAsyncHttpClient.get(mRepository.LEAVEMESSAGE, params, handler);
    }


    /**
     * 修改特殊收费人群
     * Type=updateSpecialPriceGroup
     * SERVICE_ITEM_ID 医生服务项目ID
     * SPECIAL_GROUP_ID 服务特殊收费人群ID
     * SPECIAL_GROUP 服务特殊收费人群名称
     * SPECIAL_PRICE 服务金额
     */
    public static void doHttpUpdateDoctorServiceGroup(String iTEM_ID,
                                                      String gROUP_ID, String sPECIAL_PRICE, String sPECIAL_GROUP,
                                                      AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("Type", "updateSpecialPriceGroup");
        params.put("SERVICE_ITEM_ID", iTEM_ID);
        params.put("SPECIAL_GROUP_ID", gROUP_ID);
        params.put("SPECIAL_GROUP", sPECIAL_GROUP);
        params.put("SPECIAL_PRICE", sPECIAL_PRICE);
        mAsyncHttpClient.get(mRepository.DOCTORFREE_ZONE, params, handler);
    }


    /**
     * 查询话题门票
     * @param groupId
     * @param type
     * @param handler
     */
    public static void doHttpQuerySalonPrice(String groupId, String type, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("groupId", groupId);
        params.put("type", type);
        mAsyncHttpClient.get(mRepository.SALONSPECIALPRICEGROUPSET, params, handler);
    }

    /**
     * 添加特殊收费组
     * @param groupId
     */
    public static void doHttpQuerySalonSpecialGroup(String groupId, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("groupId", groupId);
        params.put("type", "findSalonSpecialPriceGroup");
        mAsyncHttpClient.post(mRepository.SALONSPECIALPRICEGROUPSET, params, handler);
    }


    /**
     * 话题特殊成员接口
     * @param params
     * @param handler
     */
    public static void doHttpSalonSpecialPriceGroupSet(RequestParams params, AsyncHttpResponseHandler handler) {
        mAsyncHttpClient.post(mRepository.SALONSPECIALPRICEGROUPSET, params, handler);
    }


    /**
     * 服务内容查询
     * @param paramsm
     * @param handler
     */
    public static void doHttpServiceSetServlet320(RequestParams paramsm, AsyncHttpResponseHandler handler) {
        mAsyncHttpClient.get(mRepository.DOCTORSETTINGUI, paramsm, handler);
    }


    public static void doHttpSERVICESETSERVLET420(RequestParams paramsm, AsyncHttpResponseHandler handler) {
        mAsyncHttpClient.get(mRepository.SERVICESETSERVLET420, paramsm, handler);
    }


    public static void doHttpServicesetservletrj320(RequestParams paramsm, AsyncHttpResponseHandler handler) {
        mAsyncHttpClient.get(mRepository.DOCTORFREE_ZONE, paramsm, handler);
    }


    public static void doHttpFindmypatientdetails32(RequestParams params, AsyncHttpResponseHandler handler) {
        mAsyncHttpClient.get(mRepository.FINDMYSERVICELIST, params, handler);
    }

    /**
     * 我的门诊预约订单
     */
    public static void outpatientOrder(String userId, String type, int pageIndex, ApiCallbackWrapper callback) {
        Map<String, String> params = new HashMap<>();
        params.put("TERMINAL_TYPE", "doctor");
        params.put("TYPE", type);
        params.put("PAGESIZE", String.valueOf(pageIndex));
        params.put("PAGENUM", "10");
        params.put("CUSTOMERID", userId);
        ApiConnection.postAsyn(mRepository.FINDMYSERVICELIST, params, callback);
    }

    /**
     * 聊天初始化
     * @param senderId
     * @param reciveId
     * @param handler
     */
    public static void doHttpInitChat(String senderId, String reciveId, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("CUSTOMER_ID", senderId);
        params.put("DOCTORID", reciveId);
        params.put("INTALK", "1");
        params.put("Type", "IsCanTalk");
        params.put("HZCHAT", "1");
        mAsyncHttpClient.get(mRepository.HZSERVICESETSERVLET42, params, handler);
    }


    /**
     * 钱包设置
     * @param params
     * @param handler
     */
    public static void doHttpWalletSetting(RequestParams params, AsyncHttpResponseHandler handler) {
        mAsyncHttpClient.get(mRepository.SETWALLETINFOSERVLET, params, handler);
    }

    /**
     * 钱包支付
     * @param PAYMENT_PASSWORD
     */
    public static void doHttpWalletPay(String url, String PAYMENT_PASSWORD, AsyncHttpResponseHandler handler) {
        String[] str = url.split("&");
        RequestParams params = new RequestParams();
        for (int i = 0; i < str.length; i++) {
            String[] keys = str[i].split("=");
            if (keys.length == 1) {
                params.put(keys[0], "");
            } else {
                params.put(keys[0], keys[1]);
            }
        }
        params.put("PAYMENT_PASSWORD", PAYMENT_PASSWORD);
        params.put("VALID_MARK", AppContext.APP_VALID_MARK);
        mAsyncHttpClient.post(mRepository.HZSERVICESETSERVLET44, params, handler);
    }

    public static void doHttpWalletBalanceServlet(String SELECTDATE, String url, AsyncHttpResponseHandler handler) {
        String[] str = url.split("&");
        RequestParams params = new RequestParams();
        params.put("SELECTDATE", SELECTDATE);
        params.put("VALID_MARK", AppContext.APP_VALID_MARK);
        for (int i = 0; i < str.length; i++) {
            String[] keys = str[i].split("=");
            if (keys[0].equalsIgnoreCase("type")) {
                params.put("Type", "getWalletBalance");
            } else {
                if (keys.length == 1) {
                    params.put(keys[0], "");
                } else {
                    params.put(keys[0], keys[1]);
                }
            }
        }
        mAsyncHttpClient.get(mRepository.HZWalletBalanceServlet, params, handler);
    }

    public static void doHttpGetQianBao(String id, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("CUSTOMER_ID", SmartFoxClient.getLoginUserId());
        params.put("Type", "getQianbao");
        params.put("VALID_MARK", AppContext.APP_VALID_MARK);
        mAsyncHttpClient.get(mRepository.HZWalletBalanceServlet, params, handler);
    }

    //得到钱包信息
    public static void doHttpGetQianBaoInfo(String option, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("CUSTOMERID", SmartFoxClient.getLoginUserId());
        params.put("OPTION", option);
        mAsyncHttpClient.get(mRepository.GETYELLOWBOYSERVLET, params, handler);
    }

    /**
     * 支付宝支付方式
     * @param url
     * @param handler
     */
    public static void doHttpGetAliPay(String url, AsyncHttpResponseHandler handler) {
        String[] str = url.split("&");
        RequestParams params = new RequestParams();
        for (int i = 0; i < str.length; i++) {
            String[] keys = str[i].split("=");
            if (keys[0].equalsIgnoreCase("type")) {
                params.put("Type", "MedicallyRegistered330");
                params.put("VALID_MARK", AppContext.APP_VALID_MARK);
            } else {
                if (keys.length == 1)
                    params.put(keys[0], "");
                else
                    params.put(keys[0], keys[1]);
            }
        }
        mAsyncHttpClient.post(mRepository.HZSERVICESETSERVLET44, params, handler);
    }

    /**
     * 银联支付方式
     * @param url
     * @param handler
     */
    public static void doHttpGetUnionPay(String url, AsyncHttpResponseHandler handler) {
        String[] str = url.split("&");
        RequestParams params = new RequestParams();
        for (int i = 0; i < str.length; i++) {
            String[] keys = str[i].split("=");
            if (keys[0].equalsIgnoreCase("type")) {
                params.put("Type", "MedicallyRegistered");
                params.put("VALID_MARK", AppContext.APP_VALID_MARK);
            } else {
                if (keys.length == 1)
                    params.put(keys[0], "");
                else
                    params.put(keys[0], keys[1]);
            }
        }
        mAsyncHttpClient.post(mRepository.HZSERVICESETSERVLET44, params, handler);
    }


    /**
     * 删除服务时段之前调用接口
     * @param pARAME
     * @param handler
     */
    public static void doHttpDeleteServiceTimeBefor(String type, String userId, String pARAME, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("Type", type);
        params.put("CUSTOMER_ID", LoginBusiness.getInstance().getLoginEntity().getId());
        if (!type.equals("deleteAllYuyueTime")) {
            params.put("PARAME", pARAME);
        }
        mAsyncHttpClient.get(mRepository.URL_SERVICESETSERVLET33, params, handler);
    }


    /**
     * 查询已关注的人,医生已关注的是我的患者,患者已关注的是已关注的病友
     * @param params
     * @param handler
     */
    public static void doHttpFINDMYFOCUSFRIENDS(RequestParams params,
                                                AsyncHttpResponseHandler handler) {
//			if(SmartFoxClient.getLoginUserInfo().isDoctor()){//是医生
//				mAsyncHttpClient.get(mRepository.FINDMYPATIENTDETAILS32, params,handler);
//			}else{
////				mAsyncHttpClient.get(mRepository.URL_FIND_FRIENDS, params,handler);
//				mAsyncHttpClient.get(mRepository.GROUPCONSULTATIONLIST, params,handler);
//			}
        mAsyncHttpClient.get(mRepository.GROUPCONSULTATIONLIST, params, handler);
    }

    /**
     * 医生端查询我的患者,查询到的数据是按照拼音排好序的
     * @param params
     * @param handler
     */
    public static void doHttpFINDMYPatients(RequestParams params,
                                            AsyncHttpResponseHandler handler) {
        mAsyncHttpClient.get(mRepository.GROUPCONSULTATIONLIST200, params, handler);
//				mAsyncHttpClient.get(mRepository.GROUPCONSULTATIONLIST, params,handler);
    }

    public static void doHttpLookDoctorMessage(RequestParams params,
                                               AsyncHttpResponseHandler handler) {
        mAsyncHttpClient.get(mRepository.DOCTORMESSAGEBOARDSSERVLET42, params, handler);

    }


    /**
     * 根据指定类型获取医生的服务情况数据
     */
    public static void doHttpDoctorServiceQueryData(String type,
                                                    String loginUserId, int pagNum, int pageSize,
                                                    AsyncHttpResponseHandler handler) {
        RequestParams requestParams = new RequestParams();
        requestParams.put("customerId", loginUserId);
        requestParams.put("type", type);
        requestParams.put("pageNum", "" + pagNum);
        requestParams.put("pageSize", "" + pageSize);
        mAsyncHttpClient.get(mRepository.FINDMYPATIENTDETAILS32, requestParams, handler);
    }


    /**
     * 我的粉丝
     */
    public static void doHttpFINDMYFRIENDS32(RequestParams requestParams,
                                             AsyncHttpResponseHandler handler) {
        mAsyncHttpClient.get(mRepository.URL_FINDMYFRIENDS, requestParams, handler);
    }

    /**
     * 我的黑名单
     */
    public static void doHttpFINDMYBLACK32(RequestParams requestParams,
                                           AsyncHttpResponseHandler handler) {
        mAsyncHttpClient.get(mRepository.URL_FINDMYBLACKS, requestParams, handler);
    }


    public static void doHttpServiceSetServlet420(RequestParams params,
                                                  AsyncHttpResponseHandler handler) {
        mAsyncHttpClient.get(mRepository.SERVICESETSERVLET420, params, handler);

    }


    public static void doHttpSERVICESETSERVLETRJ420(RequestParams params,
                                                    AsyncHttpResponseHandler handler) {
        mAsyncHttpClient.get(mRepository.SERVICEPATIENTSERVLET, params, handler);
    }


    public static void doHttpFRIENDSINFOSET(RequestParams params,
                                            AsyncHttpResponseHandler handler) {
        mAsyncHttpClient.get(mRepository.FRIENDSINFOSET, params, handler);
    }


    public static void doHttpFINDCENTERCLASSANDGOODSERVLET33(RequestParams params,
                                                             AsyncHttpResponseHandler handler) {
        mAsyncHttpClient.get(mRepository.FINDCENTERCLASSANDGOODSERVLET33, params, handler);
    }


    /**
     * 六一健康二期专家助理主动创建病历
     */
    public static void doHttpConsultionGetContent(RequestParams params, AsyncHttpResponseHandler handler) {
        mAsyncHttpClient.get(mRepository.GETCONTENTMRTSERVLET, params, handler);
    }

    /**
     * 加载会诊病历模板接口
     * TempletClassMRTServlet?OPTION=6&CONSULTATIONID=1
     * @param option       itemId   基层医生推荐模板给患者是需要传1,其他情况传递null
     * @param consultionId 会诊id
     * @param handler
     */
    public static void doHttpConsultionCaseTemplate(String option, String itemId, String consultionId,
                                                    AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        if (itemId != null) {
            params.put("TEMID", itemId);
        }
        params.put("OPTION", option);
        if ("4".equals(option))
            params.put("CUSTID", SmartFoxClient.getLoginUserId());
        params.put("CONSULTATIONID", consultionId);
        mAsyncHttpClient.get(mRepository.TEMPLETCLASSMRTSERVLET, params, handler);
    }

    /**
     * 医生端共享六一健康
     */
    public static void doHttpConsultionCaseTemplateShare(RequestParams params, AsyncHttpResponseHandler handler) {
        mAsyncHttpClient.get(mRepository.TEMPLETCLASSMRTSERVLET, params, handler);
    }

    /**
     * 上传病历模板接口
     * @param handler
     */
    public static void doHttpPostConsultionCaseTemplate(RequestParams params,
                                                        AsyncHttpResponseHandler handler) {
//			mAsyncHttpClient.post("http://192.168.16.118:8080/DuoMeiHealth/SaveOrEditMedicalRecordServlet2", params,handler);
        mAsyncHttpClient.post(mRepository.SAVEOREDITMEDICALRECORDSERVLET2, params, handler);
    }

    /**
     * 专家助理主动创建病历上传病历模板接口
     * @param handler
     */
    public static void doHttpDoctorPostConsultionCase(RequestParams params,
                                                      AsyncHttpResponseHandler handler) {
//			mAsyncHttpClient.post("http://192.168.16.118:8080/DuoMeiHealth/DoctorCreateMedicalRecord", params,handler);
        mAsyncHttpClient.post(mRepository.DOCTORCREATEMEDICALRECORD, params, handler);
    }


    /*
     * 发布动态消息
     *
     * @param json
     * @param path
     */
    public static void doHttpDynamicNewsRelease(Context context, String json,
                                                File file, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("PARAMETER", json);
        try {
            if (file != null && file.exists()) {
                params.put("contentPicture", file);
            } else {
                params.putNullFile("contentPicture", new File(""));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        mAsyncHttpClient.post(mRepository.DYNAMICNEWSRELEASE, params, handler);
    }

    /*
     * 编辑动态消息
     *
     * @param json
     * @param path
     */
    public static void doHttpEditMessage(Context context, String json,
                                         File file, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("PARAMETER", json);
        try {
            if (file != null && file.exists()) {
                params.put("contentPicture", file);
            } else {
                params.putNullFile("contentPicture", new File(""));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
//			mAsyncHttpClient.post("http://192.168.16.45:8899/DuoMeiHealth/UpdateDynamicMessage", params, handler);
        mAsyncHttpClient.post(mRepository.DYNAMICNEWEDIT, params, handler);
    }

    /**
     * 六一健康我的服务列表
     * @param terminaltytype [findExpertByPatient  	患者端],[findPatByAssistant	基层医生端],[findPatByExpert		会诊专家端]
     * @param type           对于患者[0全部，1已申请，2填病历，3待付款，4待服务，5待退款]
     *                       对于基层医生	[0全部，1待接诊，2填病历，3待同意，4待付款，5待服务]
     *                       对于专家	[0全部，1待同意，2待付款，3给意见]
     * @param pagesize       第几页
     * @param handler
     */
    public static void doHttpFindMyConsuServiceList(String customerId, String terminaltytype, int type, int pagesize, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("TERMINAL_TYPE", terminaltytype);
        params.put("TYPE", type + "");
        params.put("PAGESIZE", pagesize + "");
        params.put("PAGENUM", "15");
        params.put("CUSTOMERID", customerId);
        params.put("VALID_MARK", AppContext.APP_VALID_MARK);
        mAsyncHttpClient.post(mRepository.FINDMYCONSUSERVICELIST, params, handler);
    }

    /**
     * 申请会诊
     * @param handler
     */
    public static void doHttpApplyConsultation(RequestParams params, AsyncHttpResponseHandler handler) {

//			mAsyncHttpClient.post("http://192.168.16.45:8899/DuoMeiHealth/ApplyConsultation?PARAMETER="+json, params, handler);
//			mAsyncHttpClient.post("http://192.168.16.45:8899/DuoMeiHealth/ApplyConsultation", params, handler);
        mAsyncHttpClient.post(mRepository.APPLYCONSULTATION, params, handler);
    }

    /**
     * 申请会诊验证码
     * @param phone
     * @param handler
     */
    public static void doHttpSendVerificationCode(String phone, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("CONSULTATION_CENTER_ID", AppContext.APP_CONSULTATION_CENTERID);
        params.put("TYPE", "sendVerificationCode");
        params.put("PATIENTTEL_PHONE", phone);
        mAsyncHttpClient.post(mRepository.GROUPCONSULTATIONLIST, params, handler);
    }

    /**
     * 基层医生将病历提交给专家接口
     * @param handler
     */
    public static void doHttpSubmitCaseTemplate(RequestParams params,
                                                AsyncHttpResponseHandler handler) {
//			mAsyncHttpClient.post("http://192.168.16.118:8080/DuoMeiHealth/SaveOrEditMedicalRecordServlet", params,handler);
        mAsyncHttpClient.post(mRepository.SERVERDETAILSERVLET, params, handler);
    }


    /**
     * 全部动态消息列表
     */
    public static void doHttpDynamicMessageList(int pagesize, ObjectHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("TYPE", "findConsuInfoList");
        params.put("CONSULTATION_CENTER_ID", AppContext.APP_CONSULTATION_CENTERID);
        params.put("PAGESIZE", pagesize + "");
        params.put("PAGENUM", "20");
        params.put("CUSTOMERID", DoctorHelper.getId());
        mAsyncHttpClient.post(mRepository.DUOMEIHEALTH, params, handler);
    }

    /**
     * 全部动态消息列表
     */
    public static void doHttpDynamicMessageLists(RequestParams params, ObjectHttpResponseHandler handler) {
        mAsyncHttpClient.post(mRepository.FRIENDSINFOSET, params, handler);
    }

    /**
     * 删除动态消息
     * @param infoId
     */
    public static void doHttpDeleteMessage(String infoId, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("TYPE", "deleteConsuInfo");
        params.put("CUSTOMERID", SmartFoxClient.getLoginUserId());
        params.put("INFOID", infoId);
        mAsyncHttpClient.post(mRepository.GROUPCONSULTATIONLIST, params, handler);
    }

    /**
     * 用户动态消息列表
     */
    public static void doHttpUserDynamicMessageList(String consultCenterId, int pagesize, String customerId, ObjectHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("TYPE", "findMyConsuInfoList");
        params.put("CONSULTATION_CENTER_ID", consultCenterId);
        params.put("PAGESIZE", pagesize + "");
        params.put("PAGENUM", "15");
        params.put("CUSTOMERID", customerId);
        mAsyncHttpClient.post(mRepository.GROUPCONSULTATIONLIST, params, handler);
    }

    /**
     * 医生接诊
     * @param
     * @param option
     * @param handler
     */
    public static void doHttpReceiveConsultation(int consultationId, int option, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("CONSULTATIONID", consultationId + "");
        params.put("OPTION", "" + option);
        params.put("DOCTORID", SmartFoxClient.getLoginUserId());
        mAsyncHttpClient.get(mRepository.SERVERDETAILSERVLET, params, handler);
    }

    /**
     * 患者个人中心-我的服务-申请单
     * @param consultationId
     * @param handler
     */
    public static void doHttpServerDetailServlet(String consultationId, ObjectHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("OPTION", "1");
        params.put("CUSTID", SmartFoxClient.getLoginUserId());
        params.put("CONSULTATIONID", consultationId);
        mAsyncHttpClient.get(mRepository.SERVERDETAILSERVLET, params, handler);
    }

    /**
     * 专家接诊
     * @param
     * @param option
     * @param handler
     */
    public static void doHttpSeniorReceiveConsultation(int consultationId, int option, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("CONSULTATIONID", consultationId + "");
        params.put("OPTION", "" + option);
        params.put("DOCTORID", SmartFoxClient.getLoginUserId());
        mAsyncHttpClient.get(mRepository.SERVERDETAILSERVLET, params, handler);
    }

    /**
     * 六一健康钱包支付
     * OPTION=3  钱包支付标志 写死
     * CONSULTATIONID  会诊ID
     * CUSTID            客户ID
     * PAYACCOUNT      付款客户ID
     * PASSWORD        MD5封装后的密码
     */
    public static void doHttpConsultationWalletPay(String consultationId, String PASSWORD, int option, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("CONSULTATIONID", consultationId);
        params.put("OPTION", "" + option);
        params.put("CUSTID", SmartFoxClient.getLoginUserId());
        if (option == 3) {//钱包支付
            params.put("PASSWORD", PASSWORD);
            params.put("PAYACCOUNT", SmartFoxClient.getLoginUserId());
        }
        mAsyncHttpClient.get(mRepository.CONSULTATIONbUYSERVLET, params, handler);
    }

    /**
     * 六一健康退款
     * 退款到钱包       ConsultationBackServlet?OPTION=1&CONSULTATIONID=1
     * 退款到支付宝
     * ConsultationBackServlet?OPTION=2&CONSULTATIONID=1&ACCOUNT=517@qq.com&PHONE=18811170904
     * 退款到银行卡
     * ConsultationBackServlet?OPTION=3&CONSULTATIONID=1&ACCOUNT=6222...&PHONE=18811170904&NAME=陈琴&OPENADDR=开户行地址
     */
    public static void doHttpConsultationBackPay(RequestParams params, AsyncHttpResponseHandler handler) {
        mAsyncHttpClient.get(mRepository.CONSULTATIONBACKSERVLET, params, handler);
    }

    /**
     * 查看流程
     * @param consultationId
     * @param handler
     */
    public static void doHttpfindConsuStatusList(String consultationId, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("TYPE", "findConsuStatusList");
        params.put("CONSULTATION_ID", consultationId);
        mAsyncHttpClient.post(mRepository.GROUPCONSULTATIONLIST, params, handler);
    }

    /**
     * 查询六一健康医生招募信息
     * @param handler
     */
    public static void doHttpfindConsultationCenterRecruit(AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("TYPE", "findConsultationCenterRecruit");
        params.put("CUSTOMERID", SmartFoxClient.getLoginUserId());
        params.put("CONSULTATION_CENTER_ID", AppContext.APP_CONSULTATION_CENTERID);
        mAsyncHttpClient.post(mRepository.GROUPCONSULTATIONLIST, params, handler);
    }

    /**
     * 获取删除服务/拒绝服务原因列表
     * @param reasonType
     * @param handler
     */
    public static void doHttpGetCancelReason(String reasonType, ObjectHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("OPTION", "2");
        params.put("REASONTYPE", reasonType);
        mAsyncHttpClient.post(mRepository.SERVERDETAILSERVLET, params, handler);
    }

    /**
     * 发送删除服务/拒绝服务原因列表
     * @param
     * @param handler
     */
    public static void doHttpPostCancelReason(String option, String reason, String conID, String customerId, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("OPTION", option);
        params.put("REASON", reason);
        params.put("CONSULTATIONID", conID);
        params.put("DOCTORID", customerId);
        mAsyncHttpClient.post(mRepository.SERVERDETAILSERVLET, params, handler);
//			mAsyncHttpClient.post("http://192.168.16.138:8080/DuoMeiHealth/ServerDetailServlet",params,handler);
    }

    /**
     * 发送会诊意见
     * @param params
     * @param handler
     */
    public static void doHttpPostConsultOpinion(RequestParams params,
                                                AsyncHttpResponseHandler handler) {
//			mAsyncHttpClient.post("http://192.168.16.118:8080/DuoMeiHealth/SaveConsultationAdvice",params,handler);
        mAsyncHttpClient.post(mRepository.SAVECONSULTATIONADVICE, params, handler);
//        mAsyncHttpClient.post("http://192.168.16.138:8080/DuoMeiHealth/SaveConsultationAdvice", params, handler);
//			mAsyncHttpClient.post("http://192.168.16.118:8080/DuoMeiHealth/SaveConsultationAdvice",params,handler);
    }

    /**
     * 发送解答意见
     * @param params
     * @param handler
     */
    public static void doHttpPostAnswerOpinion(RequestParams params,
                                               AsyncHttpResponseHandler handler) {
        mAsyncHttpClient.post(mRepository.ANSWERQUESTIONSERVLET, params, handler);
//			mAsyncHttpClient.post("http://192.168.16.118:8080/DuoMeiHealth/SaveConsultationAdvice",params,handler);
    }

    /**
     * 查看会诊意见
     * @param handler
     */
    public static void doHttpShowOpinion(String conID, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("OPTION", "9");
        params.put("CONSULTATIONID", conID);
        mAsyncHttpClient.post(mRepository.SERVERDETAILSERVLET, params, handler);
    }

    /**
     * 基层医生服务细节
     * @param handler
     */
    public static void doHttpDoctorService(RequestParams params,
                                           AsyncHttpResponseHandler handler) {
        mAsyncHttpClient.post(mRepository.SERVERDETAILSERVLET, params, handler);
    }

    /**
     * 六一健康动态消息接口
     * http://220.194.46.204:8080/DuoMeiHealth/GroupConsultationList?TYPE=findConsuInfo&CONSULTATION_CENTER_ID=1&INFOID=256&CUSTOMERID=2403
     * @param params  参数
     * @param handler
     */
    public static void doHttpGroupConsultationList(RequestParams params, AsyncHttpResponseHandler handler) {
        mAsyncHttpClient.get(mRepository.GROUPCONSULTATIONLIST100, params, handler);
    }

    /**
     * 信息中心点赞
     */
    public static void newsPraise(String infoId, String CUSTID, ApiCallbackWrapper callback){
        Map<String, String> params = new HashMap<>();
        params.put("TYPE", "praiseConsuInfo");
        params.put("INFOID", infoId);
        params.put("CUSTID", CUSTID);
        ApiConnection.postAsyn(mRepository.GROUPCONSULTATIONLIST100, params, callback);
    }

    /**
     * 个人中心查询我的医生，患者，话题，粉丝列表接口
     * http://220.194.46.204:8080/DuoMeiHealth/GroupConsultationList?
     * TYPE=findMyList&CUSTOMERID=124031&PAGESIZE=1&PAGENUM=20&FLAG=0&VALID_MARK=40
     * @param pageSize 第几页
     * @param flag     FLAG  (0-我的医生  1-我的患者  2-我的话题  3-我的粉丝  4-我创建的话题  5-我购买的话题)
     * @param handler
     */
    public static void doHttpConsultationFindMyList(int pageSize, String flag, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("TYPE", "findMyList");
        params.put("CUSTOMERID", SmartFoxClient.getLoginUserId());
        params.put("PAGESIZE", "" + pageSize);
        params.put("PAGENUM", "20");
        params.put("FLAG", flag);
        params.put("VALID_MARK", AppContext.APP_VALID_MARK);
        mAsyncHttpClient.get(mRepository.GROUPCONSULTATIONLIST, params, handler);
    }

    /**
     * benern
     * @param consultationId
     * @param handler
     */
    public static void doHttpConsultationCenterBanner(String consultationId, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("TYPE", "indConsultationCenterBanner");
        params.put("CONSULTATION_CENTER_ID", consultationId);
        mAsyncHttpClient.post(mRepository.GROUPCONSULTATIONLIST, params, handler);
    }

    /**
     * 会诊服务列表弹出框接口
     * @param consultationid
     * @param handler
     */
    public static void doHttpServerDetailServletChatMessage(String consultationid, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("OPTION", "13");
        params.put("CONSULTATIONID", consultationid);
        params.put("CUSTID", SmartFoxClient.getLoginUserId());
        mAsyncHttpClient.post(mRepository.SERVERDETAILSERVLET, params, handler);
    }


    /**
     * 我的收藏中的内容
     * @param pageSize
     * @param handler
     */
    public static void doHttpFindMyCollectedConsuInfo(int pageSize, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("TYPE", "findMyCollectedConsuInfo");
        params.put("CUSTOMERID", SmartFoxClient.getLoginUserId());
        params.put("PAGESIZE", pageSize + "");
        params.put("PAGENUM", "20");
        mAsyncHttpClient.post(mRepository.GROUPCONSULTATIONLIST, params, handler);
    }

    /**
     * 批量取消收藏动态消息
     * @param handler
     */
    public static void doHttpCancelCollectedInfoBatch(String infoIds, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("TYPE", "cancelCollectedInfoBatch");
        params.put("CUSTOMERID", SmartFoxClient.getLoginUserId());
        params.put("INFOIDS", infoIds);
        mAsyncHttpClient.post(mRepository.GROUPCONSULTATIONLIST, params, handler);
    }


    /**
     * 病历关键词
     * @param handler
     */
    public static void doHttpFindMedicalKeywordTag(AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("TYPE", "findMedicalKeywordTag");
        mAsyncHttpClient.post(mRepository.GROUPCONSULTATIONLIST200, params, handler);

    }


    /**
     * 患者端会诊服务列表
     * @param type
     * @param pagesize
     * @param handler
     */
    //192.168.16.44:8899/DuoMeiHealth/FindMyConsuServiceList?TERMINAL_TYPE=&TYPE=&PAGESIZE=&PAGENUM=&CUSTOMERID=
    public static void doHttpFindMyConsuServiceList1(int type, int pagesize, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("TERMINAL_TYPE", "findExpertByPatient");
        params.put("TYPE", type + "");
        params.put("PAGESIZE", pagesize + "");
        params.put("PAGENUM", "15");
        params.put("CUSTOMERID", SmartFoxClient.getLoginUserId());
        params.put("VALID_MARK", AppContext.APP_VALID_MARK);
        mAsyncHttpClient.post(mRepository.FINDMYCONSUSERVICELIST, params, handler);
    }


    /**
     * 量表
     */
    public static void doHttpCommonTools(AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("CONSULTATION_CENTER_ID", AppContext.APP_CONSULTATION_CENTERID);
        params.put("TYPE", "medicalCheckData");
        mAsyncHttpClient.get(mRepository.DUOMEIHEALTH, params, handler);
    }

    /**
     * 病历成员
     */
    public static void doHttpCaseMembers(String consultation, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("CONSULTATIONID", consultation);
        params.put("CUSTID", SmartFoxClient.getLoginUserId());
        params.put("OPTION", "13");
        mAsyncHttpClient.get(mRepository.SERVERDETAILSERVLET, params, handler);
    }


    /*--------------------------------------以下为新版六一健康接口----------------------------------------------*/


    /**
     * 患者端选择科室列表
     * @param name
     * @param handler
     */
    public static void doHttpFindOfficeDoctor(String name, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("TYPE", "findOfficeDoctor");
//        params.put("TYPE", "findOfficePatient");
        params.put("CONSULTATION_CENTER_ID", AppContext.APP_CONSULTATION_CENTERID);
        params.put("OFFICENAME", name);
//        params.put("CUSTOMERID", SmartFoxClient.getLoginUserId());
        params.put("OFFICEID", LoginBusiness.getInstance().getLoginEntity().getOfficeCode2());
        mAsyncHttpClient.get(mRepository.DUOMEIHEALTH, params, handler);
    }

    /**
     * 患者端查询可服务的专家人数
     * @param upperOfficeId
     * @param handler
     */
    public static void doHttpfindDocNum(String upperOfficeId, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("TYPE", "findDocNum");
        params.put("CONSULTATION_CENTER_ID", AppContext.APP_CONSULTATION_CENTERID);
        params.put("UPPER_OFFICE_ID", upperOfficeId);
        params.put("CUSTOMERID", SmartFoxClient.getLoginUserId());
        mAsyncHttpClient.get(mRepository.DUOMEIHEALTH, params, handler);
    }

    /**
     * 医生专家资料获取
     * @param type
     * @param handler
     */
    public static void doHttpFindInfo(String flag, String customerid, String type, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("TYPE", type);
        params.put("CONSULTATION_CENTER_ID", AppContext.APP_CONSULTATION_CENTERID);
        params.put("MYCUSTOMERID", LoginBusiness.getInstance().getLoginEntity().getId());
        params.put("CUSTOMERID", customerid);
        params.put("FLAG", flag);
        mAsyncHttpClient.get(mRepository.DUOMEIHEALTH, params, handler);
    }

    /**
     * 验证码
     * @param phoneNum
     * @param handler
     */
    public static void doHttpSendApplyConsuCode(String phoneNum, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("TYPE", phoneNum);
//		params.put("CONSULTATION_CENTER_ID", AppContext.APP_CONSULTATION_CENTERID);
        params.put("CUSTOMERID", SmartFoxClient.getLoginUserId());
        mAsyncHttpClient.get(mRepository.DUOMEIHEALTH, params, handler);
    }

    /**
     * ConsultationInfoSet接口
     * 获取专家列表
     * @param params
     * @param handler
     */
    public static void doGetConsultationInfoSet(RequestParams params, AsyncHttpResponseHandler handler) {
        mAsyncHttpClient.get(mRepository.DUOMEIHEALTH, params, handler);
    }

    /**
     * ConsultationInfoSet接口
     * 获取专家列表
     */
    public static void doGetConsultationInfoSet(List<BasicNameValuePair> params, ApiCallback callback, Object tag) {
        ApiConnection.getAsyn(mRepository.DUOMEIHEALTH, params, callback, tag);
    }

    /**
     * 获取医生信息
     * @param doctorId
     * @param callback
     */
    public static void doctorInfo(String doctorId, ApiCallbackWrapper callback) {
        Map<String, String> params = new HashMap<>();
        params.put("TYPE", "findCustomerInfo");
        params.put("CUSTOMERID", doctorId);
        ApiConnection.postAsyn(mRepository.DUOMEIHEALTH, params, callback, callback);
    }

    /**
     * 请求医院数据
     * @param areaCode
     * @param callback
     */
    public static void requestHospital(String areaCode, ApiCallbackWrapper callback) {
        Map<String, String> params = new HashMap<>();
        params.put("TYPE", "findUnitByAreaCode");
        params.put("AREACODE", areaCode);
        ApiConnection.postAsyn(mRepository.DUOMEIHEALTH, params, callback);
    }

    /**
     * 请求科室数据
     * @param centerId
     * @param callback
     */
    public static void requestOffice(String centerId, ApiCallbackWrapper callback) {
        Map<String, String> params = new HashMap<>();
        params.put("TYPE", "findAllOffice");
        params.put("CONSULTATION_CENTER_ID", centerId);
        params.put("CONSULTATION_CENTER_ID", centerId);
        ApiConnection.postAsyn(mRepository.DUOMEIHEALTH, params, callback);
    }

    /**
     * 发送短信验证码
     * @param phone
     * @param clienType
     * @param callback
     */
    public static void sendProveCode(String phone, String clienType, ApiCallbackWrapper callback) {
        Map<String, String> params = new HashMap<>();
        params.put("TYPE", "sendDoctorResgistCode");
        params.put("PHONENUM", phone);
        ApiConnection.addHeader("client_type", clienType);
        ApiConnection.postAsyn(mRepository.DUOMEIHEALTH, params, callback);
    }

    /**
     * 请求注册
     * @param phone     手机号
     * @param proveCode 验证码
     * @param callback
     */
    public static void requestRegister(String phone, String proveCode, ApiCallbackWrapper callback) {
        Map<String, String> params = new HashMap<>();
        params.put("TYPE", "verifyDoctorResgistCode");
        params.put("PHONENUM", phone);
        params.put("VERIFICATION_CODE", proveCode);
        ApiConnection.postAsyn(mRepository.DUOMEIHEALTH, params, callback);
    }

    /**
     * 信息中心详情
     * @param doctorId
     * @param infoId
     * @param callback
     * @param tag
     */
    public static void OkHttpNewsInfo(String doctorId, String infoId, ApiCallbackWrapper callback, Object tag) {
        Map<String, String> params = new HashMap<>();
        params.put("CUSTOMERID", doctorId);
        params.put("INFOID", infoId);
        params.put("TYPE", "findConsuInfo");
        ApiConnection.postAsyn(mRepository.DUOMEIHEALTH, params, callback, tag);
    }

    /**
     * 查询详细内容，评论信息
     */
    public static void OKHttpNewsComment(String infoId, ApiCallbackWrapper callback, Object tag) {
        Map<String, String> map = new HashMap<>();
        map.put("info_id", infoId);
        map.put("pageNum", "1");
        ApiConnection.postAsyn(mRepository.DOCTORQUERYCOMMENT, map, callback, tag);
    }

    /**
     * 获取病例列表
     * @param pageIndex
     * @param callback
     */
    public static void OkHttpCaseList(int pageIndex, ApiCallbackWrapper callback) {
        Map<String, String> params = new HashMap<>();
        params.put("TYPE", "myParticipateMedicalCaseDiscussion");
        params.put("CUSTOMERID", DoctorHelper.getId());
        params.put("PAGESIZE", String.valueOf(pageIndex));
        params.put("PAGENUM", String.valueOf(10));
        params.put("FLAG", DoctorHelper.isExpert() ? "1" : "0");
        ApiConnection.addHeader("client_type", AppContext.CLIENT_TYPE);
        ApiConnection.postAsyn(mRepository.FRIENDSINFOSET, params, callback, callback);
    }

    /**
     * 病例详情
     * @param caseId
     * @param callback
     */
    public static void OkHttpCaseDetail(String caseId, ApiCallbackWrapper callback) {
        Map<String, String> params = new HashMap<>();
        params.put("TYPE", "CONSULTATION");
        params.put("OPTION", "DETAIL");
        params.put("DOCTORID", DoctorHelper.getId());
        params.put("RECORDID", caseId);
        ApiConnection.addHeader("client_type", AppContext.CLIENT_TYPE);
        ApiConnection.postAsyn(mRepository.RECORDDISCUSSSERVLET, params, callback, callback);
    }

    /**
     * 上传病例
     * @param caseName
     * @param caseId
     * @param callback
     */
    public static void OkHttpCaseUpload(String caseName, String caseId, ApiCallbackWrapper callback) {
        Map<String, String> params = new HashMap<>();
        params.put("TYPE", "CONSULTATION");
        params.put("OPTION", "SHARE");
        params.put("DOCTORID", DoctorHelper.getId());
        params.put("RECORDID", caseId);
        params.put("RECORDNAME", caseName);
        ApiConnection.addHeader("client_type", AppContext.CLIENT_TYPE);
        ApiConnection.postAsyn(mRepository.RECORDDISCUSSSERVLET, params, callback, callback);
    }

    /**
     * 首页banner
     * @param callback
     */
    public static void OkHttpMainBanner(ApiCallbackWrapper callback) {
        Map<String, String> params = new HashMap<>();
        params.put("CUSTID", DoctorHelper.getId());
        params.put("DEVICEFLAG", "ANDROID");
        params.put("CENTERID", AppContext.APP_CONSULTATION_CENTERID);
        ApiConnection.addHeader("client_type", AppContext.CLIENT_TYPE);
        ApiConnection.postAsyn(mRepository.GROUPCONSULTATIONLIST100, params, callback);
    }

    /**
     * 获取我的消息
     * @param doctorId
     * @param callback
     */
    public static void OkHttpDoctorMessage(String doctorId, ApiCallbackWrapper callback) {
        List<BasicNameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("CUSTOMERID", doctorId));
        params.add(new BasicNameValuePair("TYPE", "findMessageList"));
        ApiConnection.getAsyn(mRepository.DUOMEIHEALTH, params, callback, callback);
    }

    /**
     * 删除离线消息
     * @param doctorId
     * @param msgId
     * @param callback
     */
    public static void OkHttpDoctorDelMessage(String doctorId, String msgId, ApiCallbackWrapper callback) {
        Map<String, String> params = new HashMap<>();
        params.put("TYPE", "deleteLastMessage");
        params.put("BELONGID", doctorId);
        params.put("OFFLINEID", msgId);
        ApiConnection.postAsyn(mRepository.FRIENDSINFOSET, params, callback);
    }

    /**
     * DoctorRegistered接口
     * 医生注册
     */
    public static void doPostDoctorRegistered(String[] fileKeys, File[] files, ApiConnection.Param[] params, ApiCallback callback, Object tag) {
        ApiConnection.getUploadDelegate().postAsyn(mRepository.DOCTORREGISTERED, fileKeys, files, params, false, callback, tag);
    }

    /**
     * 医生注册
     * @param doctorPicture
     * @param doctorCertificate
     * @param json
     * @param callback
     */
    public static void doctorRegister(File doctorPicture, File doctorCertificate, String json, ApiCallbackWrapper callback) {
        Map<String, File> files = new HashMap<>();
        files.put("doctorPicture", doctorPicture);
        files.put("doctorCertificate", doctorCertificate);
        Map<String, String> params = new HashMap<>();
        params.put("PARAMETER", json);
        ApiConnection.addHeader("client_type", AppContext.CLIENT_TYPE);
        ApiConnection.getUploadDelegate().postAsyn(mRepository.DOCTORREGISTERED, files, params, true, false, callback, callback);
    }

    /**
     * DoctorUpdate接口  医生更新资料
     * /DuoMeiHealth/DoctorUpdate?PARAMETER=json数据
     */
    public static void doPostDoctorUpdate(String[] fileKeys, File[] files, ApiConnection.Param[] params, ApiCallback callback, Object tag) {
        ApiConnection.getUploadDelegate().postAsyn(mRepository.DOCTORUPDATE, fileKeys, files, params, true, callback, tag);
    }

    public static void doctorUpdate(String id, String name, String officeCode, String centerId, String docTitleCode
            , String doctorPicture, String clientPicture, String certificate, String hospitalCode, String hospitalName, String docSpecial
            , String addrCode, String strAddr, String docDesc, String infoVersion, String editGetName, String editGetTele, String editBankName
            , String editCode, String editAddrs, File selfFile, File certificateFile, ApiCallbackWrapper callback) {

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("CUSTOMERID", id);
        jsonObject.put("DOCTOR_REAL_NAME", name);
        jsonObject.put("DOCTOR_OFFICE", officeCode);
        jsonObject.put("CONSULTATION_CENTER_ID", centerId);
        jsonObject.put("DOCTOR_TITLE", docTitleCode);
        jsonObject.put("DOCTOR_PICTURE", doctorPicture);
        jsonObject.put("ICON_DOCTOR_PICTURE", clientPicture);
        jsonObject.put("DOCTOR_CERTIFICATE", certificate);
        jsonObject.put("UNIT_CODE", hospitalCode);
        jsonObject.put("DOCTOR_HOSPITAL", hospitalName);
        jsonObject.put("DOCTOR_SPECIALLY", docSpecial);
        jsonObject.put("WORK_LOCATION", addrCode);
        jsonObject.put("WORK_LOCATION_DESC", strAddr);
        jsonObject.put("INTRODUCTION", docDesc);
        jsonObject.put("INFO_VERSION", infoVersion);
        jsonObject.put("TRANSFER_GETNAME", editGetName);
        jsonObject.put("TRANSFER_GETTELE", editGetTele);
        jsonObject.put("TRANSFER_NAME", editBankName);
        jsonObject.put("TRANSFER_CODE", editCode);
        jsonObject.put("TRANSFER_ADDR", editAddrs);

        Map<String, String> params = new HashMap<>();
        params.put("PARAMETER", jsonObject.toString());

        Map<String, File> fileParams = new HashMap<>();
        fileParams.put("doctorPicture", selfFile);
        fileParams.put("doctorCertificate", certificateFile);

        ApiConnection.getUploadDelegate().postAsyn(mRepository.DOCTORUPDATE, fileParams, params, true, false, callback, callback);
    }

    /**
     * MedicalRecordServlet接口
     * 病历接口
     */
    public static void doGetMedicalRecordServlet(List<BasicNameValuePair> params, ApiCallback callback, Object tag) {
        ApiConnection.getAsyn(mRepository.MEDICALRECORDSERVLET, params, callback, tag);
    }

    /**
     * InvitatClinicServlet接口
     * 请他门诊
     */
    public static void doGetInvitatClinicServlet(List<BasicNameValuePair> params, ApiCallback callback, Object tag) {
        ApiConnection.getAsyn(mRepository.INVITATCLINICSERVLET, params, callback, tag);
    }

    /**
     * 病历保存SAVEOREDITMEDICALRECORDSERVLET
     * 病历接口
     */
    public static void doPostSaveOrEditMedicalRecordServlet(String[] fileKeys, File[] files, ApiConnection.Param[] params, ApiCallback callback, Object tag) {
        ApiConnection.getUploadDelegate().postAsyn(mRepository.SAVEOREDITMEDICALRECORDSERVLET, fileKeys, files, params, true, callback, tag);
    }


    /**
     * ConsultationInfoSet接口
     * 获取专家列表
     */
    public static void doFindbeforeConsuPatientInfo(ApiCallback callback, Object tag) {
        BasicNameValuePair param = new BasicNameValuePair("CUSTOMERID", SmartFoxClient.getLoginUserId());
        BasicNameValuePair param1 = new BasicNameValuePair("TYPE", "findbeforeConsuPatientInfo");
        List<BasicNameValuePair> params = new ArrayList<>();
        params.add(param);
        params.add(param1);
        ApiConnection.getAsyn(mRepository.DUOMEIHEALTH, params, callback, tag);
    }

    /**
     * 会诊列表
     */
    public static void OKHttpConsultationList(List<BasicNameValuePair> params, ApiCallback callback, Object tag) {
        ApiConnection.getAsyn(mRepository.FINDMYCONSUSERVICELIST, params, callback, tag);
    }

    /**
     * 医生接单
     */
    public static void OKHttpAccept(List<BasicNameValuePair> params, ApiCallback callback, Object tag) {
        ApiConnection.getAsyn(mRepository.SERVERDETAILSERVLET, params, callback, tag);
//		ApiConnection.getAsyn("http://192.168.16.138:8080/DuoMeiHealth/ServerDetailServlet",params,callback,tag);
    }

    /**
     * 会诊订单详情
     */
    public static void OKHttpConsultInfo(int type, List<BasicNameValuePair> params, ApiCallback callback, Object tag) {
        switch (type) {
            case 0://医生
                ApiConnection.getAsyn(mRepository.CONSULTATIONDETAILSDOCTORSERVLET, params, callback, tag);
                break;
            case 1://专家
                ApiConnection.getAsyn(mRepository.CONSULTATIONDETAILSEXPERTSERVLET, params, callback, tag);
                break;
        }
    }

    /**
     * 患者会诊订单详情
     */
    public static void OKHttpConsultInfoP(List<BasicNameValuePair> params, ApiCallback callback, Object tag) {
        ApiConnection.getAsyn(mRepository.CONSULTATIONDETAILSSERVLET, params, callback, tag);
    }

    /**
     * 病历详情
     */
    public static void OKHttpCaseInfo(List<BasicNameValuePair> params, ApiCallback callback, Object tag) {
        ApiConnection.getAsyn(mRepository.RECORDDISCUSSSERVLET, params, callback, tag);
    }

    /**
     * 发送给专家
     */
    public static void OKHttpSendToExpert(List<BasicNameValuePair> params, ApiCallback callback, Object tag) {
        ApiConnection.getAsyn(mRepository.SERVERDETAILSERVLET, params, callback, tag);
//
    }

    /**
     * 我的患者
     * @param params
     * @param callback
     */
    public static void OKHttpFindMyPatient(List<BasicNameValuePair> params, ApiCallback callback, Object tag) {
        ApiConnection.getAsyn(mRepository.DUOMEIHEALTH, params, callback, tag);
    }

    /**
     * 医生发起
     * @param callback
     * @param tag
     */
    public static void OKHttpApplyConsuByAssistant(List<BasicNameValuePair> params, ApiCallback callback, Object tag) {
//		ApiConnection.getAsyn(mRepository.APPLYCONSUBYASSISTANT,"PAREMETER",json.toString(),callback,tag);
//		ApiConnection.Param[] params = {new ApiConnection.Param("PAREMETER",json.toString())};
//		ApiConnection.postAsyn(mRepository.APPLYCONSUBYASSISTANT,params,callback);
        ApiConnection.getAsyn(mRepository.APPLYCONSUBYASSISTANT, params, callback, tag);
//		ApiConnection.getAsyn("http://192.168.16.45:8899/DuoMeiHealth/ApplyConsuByAssistant",params, callback, tag);
    }

    /**
     * 查看患者资料
     * @param id
     * @param callback
     * @param tag
     */
    public static void OKHttpFindPatientInfot(String id, ApiCallback callback, Object tag) {
        List<BasicNameValuePair> params = new ArrayList<>();
        BasicNameValuePair param = new BasicNameValuePair("TYPE", "findPatientInfo");
        BasicNameValuePair param1 = new BasicNameValuePair("CUSTOMERID", id);
        params.add(param);
        params.add(param1);
//		ApiConnection.getAsyn(mRepository.DUOMEIHEALTH, params, callback, tag);
        ApiConnection.getAsyn(mRepository.DUOMEIHEALTH, params, callback, tag);
    }

    /**
     * 查看患者资料
     * @param id
     * @param callback
     * @param tag
     */
    public static void OKHttpFindPatientInfot2(String id, String id2, ApiCallback callback, Object tag) {
        List<BasicNameValuePair> params = new ArrayList<>();
        BasicNameValuePair param = new BasicNameValuePair("TYPE", "findPatientInfo");
        BasicNameValuePair param1 = new BasicNameValuePair("CUSTOMERID", id);
        BasicNameValuePair param2 = new BasicNameValuePair("doctor_id", id2);
        params.add(param);
        params.add(param1);
        params.add(param2);
//		ApiConnection.getAsyn(mRepository.DUOMEIHEALTH, params, callback, tag);
        ApiConnection.getAsyn(mRepository.DUOMEIHEALTH, params, callback, tag);
    }


    /**
     * 绑定银行卡接口
     * @param params
     * @param callback
     * @param tag
     */
    public static void doGetSetWalletInfoServlet(List<BasicNameValuePair> params, ApiCallback callback, Object tag) {
//        ApiConnection.getAsyn("http://192.168.16.138:8080/DuoMeiHealth/SetWalletInfoServlet", params, callback, tag);
        ApiConnection.getAsyn(mRepository.SETWALLETINFOSERVLET, params, callback, tag);
    }

    /**
     * 我的病历-我上传的
     * @param pagesize
     * @param callback
     * @param tag
     */
    //192.168.16.45:8899/DuoMeiHealth/ConsultationInfoSet?TYPE=medicalCaseDiscussionMy&PAGESIZE=&PAGENUM=&CUSTOMERID=&CONSULTATION_CENTER_ID=
    public static void OKHttpMedicalCaseDiscussionMy(int pagesize, ApiCallback callback, Object tag) {
        List<BasicNameValuePair> params = new ArrayList<>();
        BasicNameValuePair param = new BasicNameValuePair("PAGESIZE", pagesize + "");
        BasicNameValuePair param1 = new BasicNameValuePair("PAGENUM", "20");
        BasicNameValuePair param2 = new BasicNameValuePair("CUSTOMERID", LoginBusiness.getInstance().getLoginEntity().getId());
        BasicNameValuePair param3 = new BasicNameValuePair("TYPE", "medicalCaseDiscussionMy");
        BasicNameValuePair param4 = new BasicNameValuePair("CONSULTATION_CENTER_ID", AppContext.APP_CONSULTATION_CENTERID);
        params.add(param);
        params.add(param1);
        params.add(param2);
        params.add(param3);
        params.add(param4);
        ApiConnection.getAsyn(mRepository.DUOMEIHEALTH, params, callback, tag);
    }

    /**
     * 我的病历-我关注的
     * @param pagesize
     * @param callback
     * @param tag
     */
    //192.168.16.45:8899/DuoMeiHealth/ConsultationInfoSet?TYPE=medicalCaseDiscussionFocus&PAGESIZE=&PAGENUM=&CUSTOMERID=&CONSULTATION_CENTER_ID=
    public static void OKHttpMedicalCaseDiscussionFocus(int pagesize, ApiCallback callback, Object tag) {
        List<BasicNameValuePair> params = new ArrayList<>();
        BasicNameValuePair param = new BasicNameValuePair("PAGESIZE", pagesize + "");
        BasicNameValuePair param1 = new BasicNameValuePair("PAGENUM", "20");
        BasicNameValuePair param2 = new BasicNameValuePair("CUSTOMERID", LoginBusiness.getInstance().getLoginEntity().getId());
        BasicNameValuePair param3 = new BasicNameValuePair("TYPE", "medicalCaseDiscussionFocus");
        BasicNameValuePair param4 = new BasicNameValuePair("CONSULTATION_CENTER_ID", AppContext.APP_CONSULTATION_CENTERID);
        params.add(param);
        params.add(param1);
        params.add(param2);
        params.add(param3);
        params.add(param4);
        ApiConnection.getAsyn(mRepository.DUOMEIHEALTH, params, callback, tag);
    }

    /**
     * 我的病历——病历讨论
     * @param pagesize
     * @param callback
     * @param tag
     */
    //http://220.194.46.204/DuoMeiHealth/ConsultationInfoSet?TYPE=medicalCaseDiscussion&PAGESIZE=1&PAGENUM=20&CONSULTATION_CENTER_ID=1&CUSTOMERID=225043
    public static void OKHttpMedicalCaseDiscussion(int pagesize, ApiCallback callback, Object tag) {
        String flag = LoginBusiness.getInstance().getLoginEntity().getDoctorPosition().equals("0") ? "0" : "1";
        List<BasicNameValuePair> params = new ArrayList<>();
        BasicNameValuePair param = new BasicNameValuePair("PAGESIZE", pagesize + "");
        BasicNameValuePair param1 = new BasicNameValuePair("PAGENUM", "20");
        BasicNameValuePair param2 = new BasicNameValuePair("CUSTOMERID", LoginBusiness.getInstance().getLoginEntity().getId());
//        BasicNameValuePair param3 = new BasicNameValuePair("TYPE", "medicalCaseDiscussion");
        BasicNameValuePair param3 = new BasicNameValuePair("TYPE", "myParticipateMedicalCaseDiscussion");
        BasicNameValuePair param4 = new BasicNameValuePair("CONSULTATION_CENTER_ID", AppContext.APP_CONSULTATION_CENTERID);
        BasicNameValuePair param5 = new BasicNameValuePair("FLAG", flag);
        params.add(param);
        params.add(param1);
        params.add(param2);
        params.add(param3);
        params.add(param4);
        params.add(param5);
        ApiConnection.getAsyn(mRepository.DUOMEIHEALTH, params, callback, tag);
    }

    /**
     * 聊天历史列表
     * @param doctorId
     * @param serviceId
     * @param chatId
     * @param objectType
     * @param consultationId
     * @param callback
     */
    public static void OkHttpSystemHistoryMessage(String doctorId, String serviceId
            , String chatId, String objectType, String consultationId, ApiCallbackWrapper callback) {
        Map<String, String> params = new HashMap<>();
        params.put("customerId", doctorId);
        params.put("sms_target_id", chatId);
        params.put("offline_id", serviceId);
        params.put("Object_Type", objectType);
        params.put("consultationId", consultationId);
        ApiConnection.postAsyn(mRepository.TALKHISTORYSERVLET, params, callback, callback);
    }

    /**
     * TalkHistoryServlet接口
     * 聊天历史列表
     */
    public static void doGetTalkHistoryServlet(List<BasicNameValuePair> params, ApiCallback callback, Object tag) {
        ApiConnection.getAsyn(mRepository.TALKHISTORYSERVLET, params, callback, tag);
    }

    /**
     * TalkHistoryServlet接口
     * 特殊服务聊天历史列表
     */
    public static void doGetTalkHistoryServletS(List<BasicNameValuePair> params, ApiCallback callback, Object tag) {
        ApiConnection.getAsyn(mRepository.TALKHISTORYSERVLETS, params, callback, tag);
    }

    /**
     * DeleteLixianServlet接口
     * 删除离线
     */
    public static void doGetDeleteLixianServlet(List<BasicNameValuePair> params, ApiCallback callback, Object tag) {
        ApiConnection.getAsyn(mRepository.DELETELIXIAN42, params, callback, tag);
    }

    /**
     * 删除离线消息
     */
    public static void OkHttpChatDelMsg(String doctorId, String delId, String chatId, String objectType, ApiCallbackWrapper callback) {
        Map<String, String> params = new HashMap<>();
        params.put("Type", "deleteLixian42");
        params.put("customerId", doctorId);
        params.put("consultationId", delId);
        params.put("Object_Type", objectType);
        params.put("offid", String.valueOf(Long.MAX_VALUE));
        params.put("sms_target_id", chatId);
        ApiConnection.postAsyn(mRepository.DELETELIXIAN42, params, callback);
    }

    /**
     * RecordDiscussServlet接口
     * 病历讨论相关接口
     */
    public static void doGetRecordDiscussServlet(List<BasicNameValuePair> params, ApiCallback callback, Object tag) {
        ApiConnection.getAsyn(mRepository.RECORDDISCUSSSERVLET, params, callback, tag);
    }

    /**
     * HZPushManagementServlet接口
     * 群聊消息
     */
    public static void doGetHZPushManagementServlet(List<BasicNameValuePair> params, ApiCallback callback, Object tag) {
        ApiConnection.getAsyn(mRepository.URL_HZPUSHMANGAGER, params, callback, tag);
    }

    /**
     * 获取群聊消息
     */
    public static void OkHttpGroupMeg(String groupId, String serverid, String objectType, ApiCallbackWrapper callback) {
        Map<String, String> params = new HashMap<>();
        params.put("groupid", groupId);
        params.put("TYPE", "queryLixianLISHIJILU");
        params.put("serverid", serverid);
        params.put("Object_Type", objectType);
        params.put("pagenum", "10");
        ApiConnection.postAsyn(mRepository.URL_HZPUSHMANGAGER, params, callback, callback);
    }

    /**
     * 查询个人资料
     */
    public static void OKHttpFindCustomerInfo(ApiCallback callback, Object tag) {
        List<BasicNameValuePair> params = new ArrayList<>();
        BasicNameValuePair param = new BasicNameValuePair("TYPE", "findCustomerInfo");
        BasicNameValuePair param1 = new BasicNameValuePair("CUSTOMERID", LoginBusiness.getInstance().getLoginEntity().getId());
        params.add(param);
        params.add(param1);
        ApiConnection.getAsyn(mRepository.DUOMEIHEALTH, params, callback, tag);
    }

    /**
     * 查看会诊意见
     */
    public static void OKHttpgetOpinion(Map<String, String> map, ApiCallback callback, Object tag) {
//		ApiConnection.getAsyn(mRepository.SERVERDETAILSERVLET,params,callback,tag);
        ApiConnection.postAsyn(mRepository.SERVERDETAILSERVLET, map, callback, tag);
    }

    /**
     * 病历补充
     * @param params
     * @param handler
     */
    public static void doOKHttpSendSupply(RequestParams params, AsyncHttpResponseHandler handler) {
        mAsyncHttpClient.post(mRepository.SERVERDETAILSERVLET, params, handler);
//        mAsyncHttpClient.post("http://192.168.16.138:8080/DuoMeiHealth/ServerDetailServlet", params, handler);
    }

    /**
     * 修改密码发送验证码
     * @param customerid
     * @param callback
     * @param tag
     */
    public static void OKHttpSendUpdatePasswordCode(String customerid, ApiCallback callback, Object tag) {
        List<BasicNameValuePair> params = new ArrayList<>();
        BasicNameValuePair param = new BasicNameValuePair("CUSTOMERID", customerid);
        BasicNameValuePair param1 = new BasicNameValuePair("TYPE", "sendUpdatePasswordCode");
        params.add(param);
        params.add(param1);
        ApiConnection.getAsyn(mRepository.DUOMEIHEALTH, params, callback, tag);
    }

    /**
     * 修改密码
     * @param customerid
     * @param password
     * @param code
     * @param callback
     * @param tag
     */
    public static void OKHttpUpdatePassword(String customerid, String password, String code, ApiCallback callback, Object tag) {
        List<BasicNameValuePair> params = new ArrayList<>();
        BasicNameValuePair param = new BasicNameValuePair("CUSTOMERID", customerid);
        BasicNameValuePair param1 = new BasicNameValuePair("TYPE", "updatePassword");
        BasicNameValuePair param2 = new BasicNameValuePair("PASSWORD", password);
        BasicNameValuePair param3 = new BasicNameValuePair("VERIFICATION_CODE", code);
        params.add(param);
        params.add(param1);
        params.add(param2);
        params.add(param3);
        ApiConnection.getAsyn(mRepository.DUOMEIHEALTH, params, callback, tag);
    }

    /**
     * 修改绑定手机发送验证码
     * @param phone
     * @param callback
     * @param tag
     */
    public static void OKHttpSendUpdatePhoneNumCode(String phone, ApiCallback callback, Object tag) {
        List<BasicNameValuePair> params = new ArrayList<>();
        BasicNameValuePair param = new BasicNameValuePair("PHONENUM", phone);
        BasicNameValuePair param1 = new BasicNameValuePair("TYPE", "sendUpdatePhoneNumCode");
        params.add(param);
        params.add(param1);
        ApiConnection.getAsyn(mRepository.DUOMEIHEALTH, params, callback, tag);
    }

    /**
     * 修改绑定手机
     * @param customerid
     * @param phone
     * @param code
     * @param callback
     * @param tag
     */
    public static void OKHttppdatePhoneNum(String lastphone, String customerid, String phone, String code, ApiCallback callback, Object tag) {
        List<BasicNameValuePair> params = new ArrayList<>();
        BasicNameValuePair param = new BasicNameValuePair("CUSTOMERID", customerid);
        BasicNameValuePair param1 = new BasicNameValuePair("TYPE", "updatePhoneNum");
        BasicNameValuePair param2 = new BasicNameValuePair("PHONENUM", phone);
        BasicNameValuePair param4 = new BasicNameValuePair("BEFORE_PHONENUM", lastphone);
        BasicNameValuePair param3 = new BasicNameValuePair("VERIFICATION_CODE", code);
        params.add(param);
        params.add(param1);
        params.add(param2);
        params.add(param3);
        params.add(param4);
//        ApiConnection.getAsyn("192.168.16.45:8899/DuoMeiHealth/ConsultationInfoSet", params, callback, tag);
        ApiConnection.getAsyn(mRepository.DUOMEIHEALTH, params, callback, tag);
    }

    /**
     * 找回修改密码
     * @param phone
     * @param password
     * @param code
     * @param callback
     * @param tag
     */
    public static void OKHttpUpdateFindPassword(String phone, String password, String code, ApiCallback callback, Object tag) {
        List<BasicNameValuePair> params = new ArrayList<>();
        BasicNameValuePair param = new BasicNameValuePair("PHONENUM", phone);
        BasicNameValuePair param1 = new BasicNameValuePair("TYPE", "updateFindPassword");
        BasicNameValuePair param2 = new BasicNameValuePair("PASSWORD", password);
        BasicNameValuePair param3 = new BasicNameValuePair("VERIFICATION_CODE", code);
        params.add(param);
        params.add(param1);
        params.add(param2);
        params.add(param3);
        ApiConnection.getAsyn(mRepository.DUOMEIHEALTH, params, callback, tag);
    }


    /**
     * 医生订单提示
     */
    public static void OKHttpOrderTip(ApiCallback callback) {
        List<BasicNameValuePair> pairs = new ArrayList<>();
        pairs.add(new BasicNameValuePair("TYPE", DoctorHelper.getHomeMsgType()));
        pairs.add(new BasicNameValuePair("CUSTOMERID", DoctorHelper.getId()));
        pairs.add(new BasicNameValuePair("PAGESIZE", "1"));
        pairs.add(new BasicNameValuePair("PAGENUM", "0"));
        pairs.add(new BasicNameValuePair("CONSULTATION_CENTER_ID", "1"));
        ApiConnection.getAsyn(mRepository.DUOMEIHEALTH, pairs, callback, callback);
    }

    /**
     * 取消预约
     */
    public static void doHttpCancelOutpatient(String order_id, String customer_id, String doctorid, String reason, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("Type", "DoctorsCancel");
        params.put("ORDER_ID", order_id);
        params.put("CUSTOMER_ID", customer_id);
        params.put("DOCTORID", doctorid);
        params.put("CANCEL_REASON", reason);
        mAsyncHttpClient.get(mRepository.SERVICESETSERVLET, params, handler);
//        mAsyncHttpClient.get("http://192.168.16.138:8080/DuoMeiHealth/ServicePatientServlet", params, handler);
    }

    /**
     * 发送已读
     */
    public static void OKHttpSendRead(String conId, String flag, ApiCallback callback, Object tag) {
        List<BasicNameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("TYPE", "updateNewChange"));
        params.add(new BasicNameValuePair("FLAG", flag));
        params.add(new BasicNameValuePair("CONSULTATIONID", conId));
        ApiConnection.getAsyn(mRepository.DUOMEIHEALTH, params, callback, tag);
    }
    /**
     * 删除朋友
     */
    public static void OKHttpDeleteFriend(List<BasicNameValuePair> params,ApiCallback callback, Object tag){
        ApiConnection.getAsyn(mRepository.DELETEFRIEND,params,callback,tag);
    }
    /**
     * 绑定手机
     * @param customerid
     * @param phone
     * @param code
     * @param callback
     * @param tag
     */
    public static void OKHttpBindPhoneNum(String customerid, String phone, String code, ApiCallback callback, Object tag) {
        List<BasicNameValuePair> params = new ArrayList<>();
        BasicNameValuePair param = new BasicNameValuePair("CUSTOMERID", customerid);
        BasicNameValuePair param1 = new BasicNameValuePair("TYPE", "bindPhoneNum");
        BasicNameValuePair param2 = new BasicNameValuePair("PHONENUM", phone);
        BasicNameValuePair param3 = new BasicNameValuePair("VERIFICATION_CODE", code);
        params.add(param);
        params.add(param1);
        params.add(param2);
        params.add(param3);
        ApiConnection.getAsyn(mRepository.DUOMEIHEALTH, params, callback, tag);
    }

    /**
     * 绑定手机发送验证码
     * @param phone
     * @param callback
     * @param tag
     */
    public static void OKHttpSendBindPhoneNumCode(String phone, ApiCallback callback, Object tag) {
        List<BasicNameValuePair> params = new ArrayList<>();
        BasicNameValuePair param = new BasicNameValuePair("PHONENUM", phone);
        BasicNameValuePair param1 = new BasicNameValuePair("TYPE", "sendBindPhoneNumCode");
        params.add(param);
        params.add(param1);
        ApiConnection.getAsyn(mRepository.DUOMEIHEALTH, params, callback, tag);
    }

    /**
     * 关联手机
     */
    public static void OKHttpConPhone(List<BasicNameValuePair> params, ApiCallback callback, Object tag) {
        ApiConnection.getAsyn(mRepository.DUOMEIHEALTH, params, callback, tag);
    }

    /**
     * 意见反馈
     * @param conent
     * @param callback
     * @param tag
     */
    public static void OKHttpSaveFeedBackHZ(String conent, ApiCallback callback, Object tag) {
        List<BasicNameValuePair> params = new ArrayList<>();
        BasicNameValuePair param = new BasicNameValuePair("CUSTOMERID", DoctorHelper.getId());
        BasicNameValuePair param1 = new BasicNameValuePair("TYPE", "saveFeedBackHZ");
        BasicNameValuePair param2 = new BasicNameValuePair("CONSULTATION_CENTER_ID", AppContext.APP_CONSULTATION_CENTERID);
        BasicNameValuePair param3 = new BasicNameValuePair("CONTENT", conent);
        params.add(param);
        params.add(param1);
        params.add(param2);
        params.add(param3);
        ApiConnection.getAsyn(mRepository.DUOMEIHEALTH, params, callback, tag);
    }

    /**
     * 新闻
     */
    public static void OKHttpGetTitle(List<BasicNameValuePair> params, ApiCallback callback, Object tag) {
        ApiConnection.getAsyn(mRepository.FRIENDSINFOSET, params, callback, tag);
    }

    public static void OKHttpNewsTab(String type, ApiCallback callback) {
        Map<String, String> params = new HashMap<>();
        params.put("TYPE", "queryTitle");
//        ApiService.addHttpHeader("client_type", AppContext.CLIENT_TYPE);
        params.put("CATEGORY_NAME", type);
        ApiConnection.addHeader("client_type", "60");
        ApiConnection.postAsyn(mRepository.FRIENDSINFOSET, params, callback, callback);
        ApiConnection.clearHeader();
    }

    /**
     * @param typeId
     * @param callback
     */
    public static void OkHttpNews(String typeId, ApiCallbackWrapper callback) {
        Map<String, String> params = new HashMap<>();
        params.put("TYPE", "queryConsultation_Info_BasicByCid");
        params.put("INFO_CLASS_ID", typeId);
        ApiConnection.postAsyn(mRepository.FRIENDSINFOSET, params, callback, callback);
    }

    /**
     * 根据类型查询常见疾病
     */
    public static void OKHttpGetEvaluate(Map<String, String> map, ApiCallback callback, Object tag) {
        ApiConnection.postAsyn(mRepository.INFOCENTERSERVLET, map, callback, tag);
    }


    /**
     * 六一班查看医生好友(创建群聊)
     */
    public static void OKHttpGetFriends(Map<String, String> map, ApiCallback callback, Object tag) {
        ApiConnection.postAsyn(mRepository.INFOCENTERSERVLET, map, callback, tag);
    }

    /**
     * 聊天获取好友列表
     */
    public static void chatGetFirends(){

    }

    /**
     * 删除未读消息提醒
     * @param doctorId
     * @param sendId
     * @param callback
     */
    public static void deleteUnreadMessageWarn(String doctorId, String sendId, ApiCallbackWrapper callback){
        Map<String, String> params = new HashMap<>();
        params.put("op", "deleteOffLineMsgRecord");
        params.put("customer_id", doctorId);
        params.put("friend_id", sendId);
        params.put("isgroup", "0");
        ApiConnection.postAsyn(mRepository.INFOCENTERSERVLET, params, callback, callback);
    }

    /**
     * 回复评论
     */
    public static void OkHttpReplyComment(String id, String content, ApiCallbackWrapper callback) {
        Map<String, String> params = new HashMap<>();
        params.put("evaluate_id", id);
        params.put("reply_content", content);
        params.put("op", "replyEvaluate");
        ApiConnection.postAsyn(mRepository.INFOCENTERSERVLET, params, callback, callback);
    }

    /**
     * 记事本添加计划
     * @param warnDate
     * @param warnContent
     * @param callback
     */
    public static void notAddPlan(String warnDate, String warnContent, ApiCallbackWrapper callback) {
//        RequestParams params = new RequestParams();
//        params.put("doctor_id", adverid);
//        params.put("CUSTOMERID", userid);
//        params.put("ADVERID", adverid);
//        params.put("CUSTOMERID", userid);
        Map<String, String> map = new HashMap<>();
        map.put("doctor_id", DoctorHelper.getId());
        map.put("op", "addNotepadRecord" +
                "");
        map.put("notepad_time", warnDate);
        map.put("notepad_content", warnContent);
        ApiConnection.postAsyn(mRepository.ADDPLAN, map, callback);
    }

    /**
     * 记事本计划状态改变
     */
    public static void OKHttpPlanChangeState(Map<String, String> map, ApiCallback callback, Object tag) {
        ApiConnection.postAsyn(mRepository.INFOCENTERSERVLET, map, callback, tag);
    }

    /**
     * 记事本数据
     * @param map
     * @param callback
     * @param tag
     */
    public static void OKHttpPlanData(Map<String, String> map, ApiCallback callback, Object tag) {
        ApiConnection.postAsyn(mRepository.INFOCENTERSERVLET, map, callback, tag);
    }

    /**
     * 删除记事本、 病例点击
     */
    public static void OKHttpDelectData(Map<String, String> map, ApiCallback callback, Object tag) {
        ApiConnection.postAsyn(mRepository.INFOCENTERSERVLET, map, callback, tag);
    }

    /**
     * 查询可提现金额
     * @param map
     * @param callback
     * @param tag
     */
    public static void OKHttpGetMoney(Map<String, String> map, ApiCallback callback, Object tag) {
        ApiConnection.postAsyn(mRepository.DOCTORGETMONEY, map, callback, tag);
    }


    /**
     * 在线会诊
     */
    public static void OKHttpConsultation(Map<String, String> map, ApiCallback callback, Object tag) {
        ApiConnection.postAsyn(mRepository.CONSULTATION, map, callback, tag);
    }

    /**
     * 我的工作室中，开通、不开通服务，修改服务价格
     * @param map
     * @param callback
     * @param tag
     */
    public static void OKHttpOpenDoctorService(Map<String, String> map, ApiCallback callback, Object tag) {
        ApiConnection.postAsyn(mRepository.OPENDOCTORSETVICE, map, callback, tag);
    }

    /**
     * 添加工具箱
     * @param map
     * @param callback
     * @param tag
     */
    public static void OKHttpAddTools(Map<String, String> map, ApiCallback callback, Object tag) {
        ApiConnection.postAsyn(mRepository.INFOCENTERSERVLET, map, callback, tag);
    }

    /**
     * 加载在线会诊数据
     * @param map
     * @param callback
     * @param tag
     */
    public static void OKHttpIsConsultation(Map<String, String> map, ApiCallback callback, Object tag) {
        ApiConnection.postAsyn(mRepository.INFOCENTERSERVLET, map, callback, tag);
    }

    /**
     * 加载病例讨论的类型
     */
    public static void OKHttpAddType(AsyncHttpResponseHandler handler) {
        mAsyncHttpClient.post(mRepository.DOCTORFINDOFFICE, handler);
    }

    /**
     * 账户管理之账户余额
     * @param customer_id
     * @param handler
     */
    public static void OKHttpACCOUNTBALANCE(String customer_id, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("customer_id", customer_id);
        mAsyncHttpClient.post(mRepository.DOCTORMANAGERBALANCE, params, handler);
    }

    /**
     * 账户管理之账户余额
     */
    public static void OKHttpACCOUNTBALANCE(Map<String, String> map, ApiCallback callback, Object tag) {
        ApiConnection.postAsyn(mRepository.DOCTORMANAGERBALANCE, map, callback, tag);
    }

    /**
     * 账户管理之查询账户变更记录
     */
    public static void OKHttpACCOUNTCHANGE(Map<String, String> map, ApiCallback callback, Object tag) {
        ApiConnection.postAsyn(mRepository.DOCTORACCOUNTCHANGE, map, callback, tag);
    }

    /**
     * 充值
     */
    public static void OKHttpFillMoney(Map<String, String> map, ApiCallback callback, Object tag) {
        ApiConnection.postAsyn(mRepository.ADDBALANCE, map, callback, tag);
    }

    /**
     * 加载我的工作室预约是否开通
     * @param map
     * @param callback
     * @param tag
     */
    public static void OKHttpIsYue(Map<String, String> map, ApiCallback callback, Object tag) {
        ApiConnection.postAsyn(mRepository.JUDGEISOPEN, map, callback, tag);
    }

    /**
     * 加载工具箱
     * @param map
     * @param callback
     * @param tag
     */
    public static void OKHttpLoadingTools(Map<String, String> map, ApiCallback callback, Object tag) {
        ApiConnection.postAsyn(mRepository.LOADINGTOOLS, map, callback, tag);
    }

    /**
     * ServicePatientServlet接口
     * 患者端门诊详情
     */
    public static void doGetServicePatientServlet(List<BasicNameValuePair> params, ApiCallback callback, Object tag) {
        ApiConnection.getAsyn(mRepository.SERVICEPATIENTSERVLET, params, callback, tag);
    }

    /**
     * 二维码
     * @param callback
     * @param tag
     */
    public static void doGetBarCode(String userId, ApiCallback callback, Object tag) {
        Map<String, String> map = new HashMap<>();
        map.put("doctor_id", userId);
        ApiConnection.postAsyn(mRepository.BARCODE, map, callback, tag);
    }


    /*-------医教计划------------*/

    /**
     * 添加宝贝
     */
    public static void OKHttpSaveInformation(String fileKey, File file, ApiConnection.Param[] params, ApiCallback callback, Object tag) {
        ApiConnection.getUploadDelegate().postAsyn(mRepository.DOCTORADDBABY, fileKey, file, params, callback, tag);
    }

    /**
     * 修改宝贝
     * @param fileKey
     * @param file
     * @param params
     * @param callback
     * @param tag
     */
    public static void OKHttpModityInformation(String fileKey, File file, ApiConnection.Param[] params, ApiCallback callback, Object tag) {
        ApiConnection.getUploadDelegate().postAsyn(mRepository.DOCTORMODIFYBABY, fileKey, file, params, callback, tag);
    }

    /**
     * 健康讲堂提交文件
     */
    public static void OKHttpLectureFile(String fileKey, File file, ApiConnection.Param[] params, ApiCallback callback, Object tag) {
        ApiConnection.getUploadDelegate().postAsyn(mRepository.UPLOADCLASSROOMFILE, fileKey, file, params, callback, tag);
    }

    /**
     * 医教联盟首页数据
     */
    public static void OKHttpGetPlanList(Map<String, String> map, ApiCallback callback, Object tag) {
        ApiConnection.postAsyn(mRepository.DOCTORTEACH, map, callback, tag);
    }
    /**
     * 删除宝贝
     * @param map
     * @param callback
     * @param tag
     */
//    public static void OKHttpDelectBaby(Map<String,String> map, ApiCallback callback, Object tag) {
//        ApiConnection.postAsyn(mRepository.DOCTORSEARCH, map, callback, tag);
//    }

    /**
     * 医教计划成员
     * @param
     */
    public static void OKHttpGetMemberList(Map<String, String> map, ApiCallback callback, Object tag) {
        ApiConnection.postAsyn(mRepository.DOCTORTEACHMEMBER, map, callback, tag);
    }

    /**
     * 查询单个宝贝详细信息
     * @param
     */
//    public static void OKHttpGetBabyInfo(String children_id, AsyncHttpResponseHandler handler) {
//        RequestParams requestParams = new RequestParams();
//        requestParams.put("children_id", children_id);
//        mAsyncHttpClient.post(mRepository.DOCTORBABYINFO, requestParams, handler);
//    }
    public static void OKHttpGetBabyInfo(Map<String, String> map, ApiCallback callback, Object tag) {
        ApiConnection.postAsyn(mRepository.DOCTORBABYINFO, map, callback, tag);
    }

    /**
     * 根据电话号码查询客户
     * @param phone   手机号码
     * @param handler
     */
    public static void OKHttpGetPhoneCustom(String phone, AsyncHttpResponseHandler handler) {
        RequestParams requestParams = new RequestParams();
        requestParams.put("phone", phone);
        mAsyncHttpClient.post(mRepository.DOCTORPHONECUSTOM, requestParams, handler);
    }

    /**
     * 添加成员
     */
    public static void OKHttpAddMember(Map<String, String> map, ApiCallback callback, Object tag) {
        ApiConnection.postAsyn(mRepository.DOCTORADDMEMBER, map, callback, tag);
    }

    /**
     * 添加计划
     */

    public static void OKHttpAddPlan(Map<String, String> map, ApiCallback callback, Object tag) {
        ApiConnection.postAsyn(mRepository.DOCTORADDPLAN, map, callback, tag);
    }

    /**
     * 填写关爱记录
     */
    public static void OKHttpAddCare(Map<String, String> map, ApiCallback callback, Object tag) {
        ApiConnection.postAsyn(mRepository.DOCTORADDCARE, map, callback, tag);
    }

    /**
     * 删除关爱计划
     * @param map
     * @param callback
     * @param tag
     */
    public static void OKHttpDelectPlan(Map<String, String> map, ApiCallback callback, Object tag) {
        ApiConnection.postAsyn(mRepository.DOCTORSELECTBABY, map, callback, tag);
    }

    /**
     * 修改成员备注
     * @param children_id
     * @param customer_id
     * @param customer_remark 成员备注
     * @param handler
     */
    public static void OKHttpUpdateMemberRemark(String children_id, String customer_id,
                                                String customer_remark, AsyncHttpResponseHandler handler) {
        RequestParams params = new RequestParams();
        params.put("children_id", children_id);
        params.put("customer_id", customer_id);
        params.put("customer_remark", customer_remark);
        mAsyncHttpClient.post(mRepository.DOCTORUPDATEMEMBERREMARK, params, handler);
    }

    /**
     * 根据宝贝计划是否正在执行进行查询
     */
    public static void OKHttpISRun(Map<String, String> map, ApiCallback callback, Object tag) {
        ApiConnection.postAsyn(mRepository.DCOTORISRUN, map, callback, tag);
    }

    /**
     * 计划详情
     */
    public static void OKHttpPlanDetail(Map<String, String> map, ApiCallback callback, Object tag) {
        ApiConnection.postAsyn(mRepository.DOCTORPLANDETAIL, map, callback, tag);
    }

    /**
     * 计划变更状态
     */
    public static void OKHttpPlanChange(Map<String, String> map, ApiCallback callback, Object tag) {
        ApiConnection.postAsyn(mRepository.DOCTORPLANCHANGE, map, callback, tag);
    }

    /**
     * 填写关爱记录
     * @param params
     * @param handler
     */
    public static void doHttpFillCareRecord(RequestParams params, AsyncHttpResponseHandler handler) {
        mAsyncHttpClient.post(mRepository.DOCTORADDCARE, params, handler);
    }

    /**
     * 体现
     * @param map
     * @param callback
     * @param tag
     */

    public static void OKHttpEmbodyMoney(Map<String, String> map, ApiCallback callback, Object tag) {
        ApiConnection.postAsyn(mRepository.EMBODY, map, callback, tag);
    }

    /**
     * 查询随访计划
     * @param map
     * @param callback
     * @param tag
     */
    public static void OKHttpFindFollowUpPlAN(Map<String, String> map, ApiCallback callback, Object tag) {
        ApiConnection.postAsyn(mRepository.FINDFOLLOWPLAN, map, callback, tag);
    }

    /**
     * 病历管理 统一的连网方法
     */
    public static void OKHttpCaseList(Map<String, String> map, ApiCallback callback, Object tag) {
        ApiConnection.postAsyn(mRepository.TALKHISTORYSERVLETS, map, callback, tag);
    }

    /**
     * 已有模板库
     * @param map
     * @param callback
     * @param tag
     */
    public static void OKHttpFindTemplate(Map<String, String> map, ApiCallback callback, Object tag) {
        ApiConnection.postAsyn(mRepository.FINDTEMPLATE, map, callback, tag);
    }

    /**
     * 获取模版库
     */
    public static void followTemplateList(String doctorId, ApiCallbackWrapper callback){
        Map<String, String> params = new HashMap<>();
        params.put("customer_id", doctorId);
        params.put("flag", "1");//公有数据库
        ApiConnection.postAsyn(mRepository.FINDTEMPLATE, params, callback);
    }

    /**
     * 删除模板库
     * @param map
     * @param callback
     * @param tag
     */
    public static void OKHttpDeleteSelfTemplate(Map<String, String> map, ApiCallback callback, Object tag) {
        ApiConnection.postAsyn(mRepository.DELETESELFTEMPLATE, map, callback, tag);
    }

    /**
     * 加载模版数据
     */
    public static void listTemplate(String doctorId, String flag, ApiCallbackWrapper callback){
        Map<String, String> params = new HashMap<>();
        params.put("customer_id", doctorId);
        params.put("flag", flag);
        ApiConnection.postAsyn(mRepository.FINDTEMPLATE, params, callback, callback);
    }

    /**
     * 删除随访模版
     */
    public static void deleteTemplate(String doctorId, String templateId, ApiCallbackWrapper callback){
        Map<String, String> params = new HashMap<>();
        params.put("customer_id", doctorId);
        params.put("template_id", templateId);
        ApiConnection.postAsyn(mRepository.DELETESELFTEMPLATE, params, callback, callback);
    }

    /**
     * 查询随访计划详情
     * @param map
     * @param callback
     * @param tag
     */
    public static void OKHttpFindFollowSubListById(Map<String, String> map, ApiCallback callback, Object tag) {
        ApiConnection.postAsyn(mRepository.FINDFOLLOWSUBLISTBYID, map, callback, tag);
    }

    /**
     * 查询私有模板详情
     * @param map
     * @param callback
     * @param tag
     */
    public static void OKHttpFindSubFollowTemplate(Map<String, String> map, ApiCallback callback, Object tag) {
        ApiConnection.postAsyn(mRepository.FINDSUBFOLLOWTEMPLATE, map, callback, tag);
    }

    /**
     * 添加模板库到自己的模板库
     * @param map
     * @param callback
     * @param tag      setPrivateTemplate
     */
    public static void OKHttpsetPrivateTemplate(Map<String, String> map, ApiCallback callback, Object tag) {
        ApiConnection.postAsyn(mRepository.SETPRIVATETEMPLATE, map, callback, tag);
    }

    /**
     * 新建模板
     * @param map
     * @param callback
     * @param tag
     */
    public static void OKHttpAddSelfTemplate(Map<String, String> map, ApiCallback callback, Object tag) {
        ApiConnection.postAsyn(mRepository.ADDSELFTEMPLATE, map, callback, tag);
    }


    /**
     * 添加随访计划
     * @param sRemindme
     * @param sRemindcus
     * @param alert_timeCount
     * @param alert_timeType
     * @param mSickId
     * @param doctorId
     * @param sCusseeplan
     * @param mFollowId
     * @param mFollowName
     * @param tempTime
     * @param data
     * @param callback
     */
    public static void OKHttpAddFollow(String sRemindme, String sRemindcus, String alert_timeCount, String alert_timeType,
                                       String mSickId, String doctorId, String sCusseeplan, String mFollowId, String mFollowName,
                                       String tempTime, String data, ApiCallbackWrapper callback) {
        Map<String, String> map = new HashMap<>();
        map.put("op", "addFollow");
        map.put("alert_me", sRemindme);//提醒医生
        map.put("alert_sick", sRemindcus);//提醒患者
        map.put("alert_timecount", alert_timeCount);
        map.put("alert_timetype", alert_timeType);
        map.put("customer_id", mSickId);//
        map.put("doctor_id", doctorId);//
        map.put("sick_see_flag", sCusseeplan);//患者可见不可见
        map.put("template_id", mFollowId);//模板ID
        map.put("template_name", mFollowName);//模板名称
        map.put("createtime", tempTime);//tempTime
        map.put("data", data);
        ApiConnection.postAsyn(mRepository.ADDFOLLOW, map, callback, callback);
    }


    /**
     * 查询订单
     * @param doctorId
     * @param serviceType
     * @param status
     * @param callback
     * @param tag
     */
    public static void OKHttpFindOrderByDoctor(String doctorId, String serviceType, String status, ApiCallbackWrapper callback, Object tag) {
        Map<String, String> params = new HashMap<>();
        params.put("customer_id", doctorId);
        params.put("service_type_id", serviceType);
        params.put("status", status);
        ApiConnection.postAsyn(mRepository.FINDORDERBYDOCTOR, params, callback, tag);
    }

    /**
     * 查询工作站订单
     * @param stationId
     * @param type
     * @param callback
     */
    public static void OkHttpStationOrder(String doctorId, String stationId, String type, ApiCallbackWrapper callback) {
        Map<String, String> params = new HashMap<>();
        params.put("op", "queryWorkSiteOrderList");
        params.put("site_id", stationId);
        params.put("status", type);
        params.put("customer_id", doctorId);
        ApiConnection.postAsyn(mRepository.COMMONURL, params, callback);
    }

    /**
     * 工作站订单分配
     * @param doctorId
     * @param orderId
     * @param callback
     */
    public static void OkHttpStationOrderDispatch(String doctorId, String orderId, String status, ApiCallbackWrapper callback) {
        Map<String, String> map = new HashMap<>();
        map.put("op", "distributionOrder");
        map.put("doctor_id", doctorId);
        map.put("order_id", orderId);
        map.put("status", status);//1-抢单阶段, 2 - 分配给某个医生
        ApiConnection.postAsyn(mRepository.COMMONURL, map, callback);
    }

    /**
     * 工作站订单邀请医生
     * @param formId
     * @param toId
     * @param orderId
     * @param groupId
     * @param callback
     */
    public static void OkHttpStationOrderInvited(String formId, String toId, String orderId, String groupId, ApiCallbackWrapper callback) {
        Map<String, String> map = new HashMap<>();
        map.put("op", "inviteDoctor");
        map.put("customer_id", formId);
        map.put("invite_doctor_id", toId);
        map.put("order_id", orderId);
        map.put("group_id", groupId);
        ApiConnection.postAsyn(mRepository.COMMONURL, map, callback);
    }

    /**
     * 设置中查看设置的价格
     * @param map
     * @param callback
     * @param tag
     */
    public static void OKHttpFindServiceSetting(Map<String, String> map, ApiCallback callback, Object tag) {
        ApiConnection.postAsyn(mRepository.FINDSERVICESETTING, map, callback, tag);
    }

    /**
     * 添加分享
     */
    public static void OKHttpShareUploadServlet(String content, List<File> files, ApiCallbackWrapper callback) {
        Map<String, String> params = new HashMap<>();
        params.put("customer_id", DoctorHelper.getId());
        params.put("content", content);

        Map<String, File> fileParams = new HashMap<>();
        if (files != null && !files.isEmpty()) {
            for (int i = 0; i < files.size(); i++) {
                File file = files.get(i);
                fileParams.put(String.format("%s.jpg", i), file);
            }
        }
        ApiConnection.getUploadDelegate().postAsyn(mRepository.UPLOADDOCTORSARESERVLET, fileParams, params, true, callback, callback);
    }

    /**
     * 查询版本说明URL
     * @param map
     * @param callback
     * @param tag
     */
    public static void OKHttpFindVersionInfoServlet(Map<String, String> map, ApiCallback callback, Object tag) {
        ApiConnection.postAsyn(mRepository.FINDVERSIONINFOSERVICE, map, callback, tag);
    }

    /**
     * 医师医生集团 统一的连网方法
     */
    public static void OKHttpStationCommonUrl(Map<String, String> map, ApiCallback callback, Object tag) {
        ApiConnection.postAsyn(mRepository.COMMONURL, map, callback, tag);
    }

    /**
     * 根据地区、医院查询医生列表
     * @param areaCode  地区code
     * @param unitCode  医院code
     * @param pageIndex
     * @param callback
     */
    public static void OkHttpStationQueryDoctor(String areaCode, String unitCode, int pageIndex, ApiCallbackWrapper callback) {
        Map<String, String> map = new HashMap<>();
        map.put("op", "queryDoctorList");
        map.put("areaCode", areaCode);
        map.put("unitCode", unitCode);
        map.put("page", String.valueOf(pageIndex));
        map.put("pageSize", "10");
        ApiConnection.postAsyn(mRepository.COMMONURL, map, callback, callback);
    }

    /**
     * 修改工作站背景
     * @param stationId
     * @param coverPath
     * @param callback
     */
    public static void OkHttpStationCoverSetting(String stationId, File coverPath, ApiCallbackWrapper callback) {
        Map<String, String> params = new HashMap<>();
        params.put("op", "setBackGround");
        params.put("site_id", stationId);
        Map<String, File> files = new HashMap<>();
        files.put("photo", coverPath);
        ApiConnection.addHeader("username", DoctorHelper.getAccount());
        ApiConnection.getUploadDelegate().postAsyn(mRepository.COMMONURL, files, params, callback, callback);
    }

    /**
     * 工作站设置服务
     */
    public static void OKHttpStationPrice(String order_on_off, String service_Price, String service_type_id, String serviceCreator, String siteId, ApiCallback callback, Object tag) {
        Map<String, String> map = new HashMap<>();
        map.put("op", "settingService");
        map.put("order_on_off", order_on_off);
        map.put("service_price", service_Price);
        map.put("service_type_id", service_type_id);
        map.put("service_creator", serviceCreator);
        map.put("site_id", siteId);
        ApiConnection.postAsyn(mRepository.COMMONURL, map, callback, tag);
    }

    /**
     * 医生接收工作站的邀请
     */
    public static void OKHttpStationInviteDoctor(String stationId, int status, ApiCallbackWrapper callback, final Object tag) {
        Map<String, String> map = new HashMap<>();
        map.put("invite_id", DoctorHelper.getId());
        map.put("invite_status", String.valueOf(status));//201=同意、202=拒绝
        map.put("invite_status_desc", "");
        map.put("site_id", stationId);
        map.put("op", "updateInviteStatus");
        ApiConnection.postAsyn(mRepository.COMMONURL, map, callback, tag);
    }

    /**
     * 更新工作站信息
     * @param stationDesc
     * @param stationName
     * @param hospitalDesc
     * @param founderDesc
     * @param callback
     */
    public static void OkHttpStationUpdata(String stationId, String stationDesc, String stationName, String hospitalDesc, String founderDesc, ApiCallbackWrapper callback) {
        Map<String, String> params = new HashMap();
        params.put("op", "updateWorkSite");
        params.put("site_id", stationId);
        if (!TextUtils.isEmpty(stationDesc)) {
            params.put("site_desc", stationDesc);
        }
        if (!TextUtils.isEmpty(stationName)) {
            params.put("site_name", stationName);
        }
        if (!TextUtils.isEmpty(hospitalDesc)) {
            params.put("hospital_desc", hospitalDesc);
        }
        if (!TextUtils.isEmpty(founderDesc)) {
            params.put("site_createor_desc", founderDesc);
        }
        ApiConnection.postAsyn(mRepository.COMMONURL, params, callback, callback);
    }

    /**
     * 获取所有医生分享列表
     * @param pageIndex
     * @param callback
     */
    public static void OkHttpShareList(int pageIndex, ApiCallbackWrapper callback) {
        Map<String, String> params = new HashMap<>();
        params.put("op", "queryShareList");
        params.put("customer_id", DoctorHelper.getId());
        params.put("pageNum", String.valueOf(pageIndex));
        params.put("pageSize", String.valueOf(10));
        ApiConnection.postAsyn(mRepository.COMMONURL, params, callback, callback);
    }

    /**
     * 根据医生Id获取分享列表
     * @param pageIndex
     * @param callback
     */
    public static void OkHttpDoctorShareList(int pageIndex, String userId, ApiCallbackWrapper callback) {
        Map<String, String> params = new HashMap<>();
        params.put("Type", "shareInDoctor");
        params.put("Doctor_ID", userId);
        params.put("PageSize", String.valueOf(10));
        params.put("Page", String.valueOf(pageIndex));
        ApiConnection.postAsyn(mRepository.COMMONURLWSS, params, callback, callback);
    }

    /**
     * 分享添加评论
     * @param shareId
     * @param comment
     * @param toDoctorId
     * @param callback
     */
    public static void OkHttpDoctorShareCommentAdd(String shareId, String comment, String toDoctorId, ApiCallbackWrapper callback) {
        Map<String, String> params = new HashMap<>();
        params.put("op", "commentSare");
        params.put("comment_content", comment);
        params.put("customer_id", DoctorHelper.getId());//评论者
        if (!TextUtils.isEmpty(toDoctorId)) params.put("comment_customer_id", toDoctorId);
        params.put("share_id", shareId);
        ApiConnection.postAsyn(mRepository.COMMONURL, params, callback, callback);
    }

    /**
     * 分享被点赞
     * @param shareId
     * @param isLike
     * @param callback
     */
    public static void OkHttpDoctorShareLike(String shareId, boolean isLike, ApiCallbackWrapper callback) {
        Map<String, String> params = new HashMap<>();
        params.put("op", "likeShare");
        params.put("share_id", shareId);
        params.put("customer_id", DoctorHelper.getId());
        params.put("status", isLike ? "2" : "1");
        ApiConnection.postAsyn(mRepository.COMMONURL, params, callback, callback);
    }

    /**
     * 分享被删除
     * @param shareId
     * @param callback
     */
    public static void OkHttpDoctorShareDel(String shareId, ApiCallbackWrapper callback) {
        Map<String, String> params = new HashMap<>();
        params.put("op", "deleteShare");
        params.put("share_id", shareId);
        ApiConnection.postAsyn(mRepository.COMMONURL, params, callback, callback);
    }

    /**
     * 申请加入工作站
     */
    public static void OKHttpStationApplyJoinStation(String stationId, String content, ApiCallbackWrapper callback, final Object tag) {
        Map<String, String> map = new HashMap<>();
        map.put("op", "applyJoinWorkSite");
        map.put("apply_id", DoctorHelper.getId());
        map.put("site_id", stationId);
        map.put("apply_desc", content);
        ApiConnection.postAsyn(mRepository.COMMONURL, map, callback, tag);
    }

    /**
     * 邀请医生加入工作站
     */
    public static void OKHttpInviteJoinStation(String doctorId, ApiCallbackWrapper callback, final Object tag) {
        Map<String, String> map = new HashMap<>();
        map.put("op", "inviteMember");
        map.put("site_invite_id", DoctorHelper.getId());
        map.put("site_id", LoginBusiness.getInstance().getLoginEntity().getSiteId());
        map.put("invite_id", doctorId);
        map.put("invite_desc", "1");
        LogUtils.e(map);
        ApiConnection.postAsyn(mRepository.COMMONURL, map, callback, tag);
    }

    /**
     * 更新医生在工作站的状态
     */
    public static void OKHttpStationDoctorStatue(String stationId, String applyUserId, String status, ApiCallbackWrapper callback, final Object tag) {
        Map<String, String> map = new HashMap<>();
        map.put("op", "updateApplyStatus");
        map.put("site_id", stationId);
        map.put("manage_desc", "1");
        map.put("manage_id", DoctorHelper.getId());
        map.put("apply_id", applyUserId);
        map.put("manage_status", "" + status);
        ApiConnection.postAsyn(mRepository.COMMONURL, map, callback, tag);
    }

    /**
     * 退出工作站
     */
    public static void OKHttpStationQuitStation(String stationId, ApiCallbackWrapper callback, final Object tag) {
        Map<String, String> map = new HashMap<>();
        map.put("op", "deleteSiteMember");
        map.put("site_id", stationId);
        map.put("type", "2");//2 被移除
        String doctorId = DoctorHelper.getId();
        map.put("customer_id", doctorId);
        ApiConnection.postAsyn(mRepository.COMMONURL, map, callback, tag);
    }

    /**
     * 请求工作站（医生集团）详情
     * @param detailId
     * @param callback
     * @param tag
     */
    public static void OKHttpStationDetail(String detailId, ApiCallback callback, Object tag) {
        Map<String, String> map = new HashMap<>();
        map.put("Type", "workSiteHome");
        map.put("Site_Id", detailId);
        ApiConnection.postAsyn(mRepository.COMMONURLWSS, map, callback, tag);
    }

    /**
     * 请求工作站服务
     * @param stationId
     * @param callback
     * @param tag
     */
    public static void OKHttpStationService(String stationId, ApiCallbackWrapper callback, Object tag) {
        Map<String, String> map = new HashMap<>();
        map.put("Type", "workSiteSercive");
        map.put("Site_Id", stationId);
        ApiConnection.postAsyn(mRepository.COMMONURLWSS, map, callback, tag);
    }

    /**
     * 请求工作站评论列表
     * @param callback
     */
    public static void OkHttpStationCommentList(String stationId, int page, ApiCallback callback) {
        Map<String, String> map = new HashMap<>();
        map.put("Type", "evaluateInWorkSite");
        map.put("PageSize", String.valueOf(10));
        map.put("Page", String.valueOf(page));
        map.put("Site_Id", stationId);
        ApiConnection.postAsyn(mRepository.COMMONURLWSS, map, callback, callback);
    }

    /**
     * 获取医生主页评论列表数据
     * @param pageIndex
     * @param callback
     */
    public static void OkHttpDoctorHomeCommentList(int pageIndex, String doctorId, ApiCallbackWrapper callback) {
        List<BasicNameValuePair> pairs = new ArrayList<>();
        pairs.add(new BasicNameValuePair("TYPE", "findCommentList"));
        pairs.add(new BasicNameValuePair("PAGESIZE", String.valueOf(pageIndex)));
        pairs.add(new BasicNameValuePair("PAGENUM", String.valueOf(10)));
        pairs.add(new BasicNameValuePair("CUSTOMERID", doctorId));
        ApiConnection.getAsyn(mRepository.DUOMEIHEALTH, pairs, callback, callback);
    }

    /**
     * 请求工作站成员（医生集团)
     * @param siteId
     * @param callback
     * @param tag
     */
    public static void OKHttpStationMembers(String siteId, ApiCallback callback, Object tag) {
        Map<String, String> map = new HashMap<>();
        map.put("op", "querySitePerson");
        map.put("site_id", siteId);//1
        ApiConnection.postAsyn(mRepository.COMMONURL, map, callback, tag);
    }

    /**
     * 请求工作站成员（医生集团)
     * @param callback
     * @param tag
     */
    public static void OKHttpDoctorInfo(int type, String userId, String stationId, ApiCallback callback, Object tag) {
        Map<String, String> map = new HashMap<>();
        String op;
//        if ("2".equals(type)) {
//            op = "queryApplyInfo";//申请的医生详情
//            map.put("site_id", stationId);
//        } else {
//            op = "queryDoctorInfo";//医生详情
//        }/* else {
//
//        }*/
        if (type == DoctorHomeType.DOCTOR_HOME_INVITE) {
            op = "queryInviteInfo";//邀请医生详情
        } else {
            op = "queryDoctorInfo";//医生详情
        }
        map.put("op", op);
        map.put("customer_id", userId);
        map.put("loginUserId", DoctorHelper.getId());//1
        ApiConnection.postAsyn(mRepository.COMMONURL, map, callback, tag);
    }

    /**
     * 退出工作站（医生集团）
     * @param siteId
     * @param customerId
     * @param callback
     * @param tag
     */
    public static void OKHttpStationQuery(String siteId, String customerId, ApiCallback callback, Object tag) {
        Map<String, String> map = new HashMap<>();
        map.put("op", "deleteSiteMember");
        map.put("site_id", siteId);
        map.put("customer_id", customerId);//siteId
        ApiConnection.postAsyn(mRepository.COMMONURL, map, callback, tag);
    }

    /**
     * 获取工作站列表
     */
    public static void OKHttpStationList(String doctorId, ApiCallback callback) {
        Map<String, String> map = new HashMap<>();
        map.put("Type", "findWorkSiteByCus");
        map.put("Customer_Id", doctorId);
        ApiConnection.postAsyn(mRepository.COMMONURLWSS, map, callback);
    }

    /**
     * 创建医生集团
     * @param callback
     * @param tag
     */
    public static void OKHttpCreatStation(String stationNameStr, String stationIntroStr, String stationHosStr, String hosIntroStr, String officeId, String siteArea, String leaderIntroStr, String fileKey, File file, ApiCallback callback, Object tag) {
        Map<String, String> params = new HashMap<>();
        params.put("site_createor", DoctorHelper.getId());
        params.put("site_name", stationNameStr);
        params.put("site_desc", stationIntroStr);
        params.put("site_hospotal", stationHosStr);
        params.put("hospital_desc", hosIntroStr);
        params.put("site_createor_desc", leaderIntroStr);
        params.put("office_id", officeId);
        params.put("site_area", "" + siteArea);

        Map<String, File> fileParams = new HashMap<>();
        fileParams.put("photo", file);

        ApiConnection.getUploadDelegate().postAsyn(mRepository.CREATSTATION, fileParams, params, true, false, callback, tag);
    }

    /**
     * 会诊邀请状态改变
     */
    public static void OKHttpConInvited(Map<String, String> map, ApiCallback callback, Object tag) {
        ApiConnection.postAsyn(mRepository.INFOCENTERSERVLET, map, callback, tag);
    }

    /**
     * 商城 统一联网接口
     * @param map
     * @param callback
     * @param tag
     */
    public static void OKHttGoodsServlet(Map<String, String> map, ApiCallback callback, Object tag) {
        ApiConnection.postAsyn(mRepository.GOODSSERVLET, map, callback, tag);
    }

    /**
     * 健康讲堂 统一联网接口
     * @param map
     * @param callback
     * @param tag
     */
    public static void OKHttLectureServlet(Map<String, String> map, ApiCallback callback, Object tag) {
        ApiConnection.postAsyn(mRepository.CLASSROOMSERVLET, map, callback, tag);
    }

    /**
     * 获取健康讲堂列表数据
     * @param callback
     */
    public static void OkHttpLectureList(String siteId, ApiCallbackWrapper callback) {
        Map<String, String> map = new HashMap<>();
        map.put("op", "querySiteClassroomList");
        map.put("site_id", siteId);//1
        map.put("customer_id", LoginBusiness.getInstance().getLoginEntity().getId());//1
        ApiConnection.postAsyn(mRepository.CLASSROOMSERVLET, map, callback, callback);
    }

    /**
     * 获取健康讲堂详情数据
     * @param callback
     */
    public static void OkHttpLectureDetail(String id, ApiCallbackWrapper callback) {
        Map<String, String> map = new HashMap<>();
        map.put("op", "queryCourseInfo");
//        map.put("op", "queryBuyPageCourseInfo");
        map.put("course_id", id);//1
        map.put("customer_id", LoginBusiness.getInstance().getLoginEntity().getId());//1
        ApiConnection.postAsyn(mRepository.CLASSROOMSERVLET, map, callback, callback);
    }

    /**
     * 查询支付方式
     */
    public static void OKHttpQueryYellowBoy(Map<String, String> map, ApiCallback callback, Object tag) {
        ApiConnection.postAsyn(mRepository.QUERYYELLOWBOY, map, callback, tag);
    }

    /**
     * 购买具体服务支付接口
     * @param params
     * @param callback
     * @param tag
     */
    public static void doGetConsultationBuyStudioServlet(List<BasicNameValuePair> params, ApiCallback callback, Object tag) {
        ApiConnection.getAsyn(mRepository.BUYDOCTORSERVICE, params, callback, tag);
    }

    /**
     * 获取我的评论
     * @param doctorId
     * @param type
     * @param callback
     */
    public static void OkHttpMyComment(String doctorId, String type, ApiCallbackWrapper callback) {
        Map<String, String> params = new HashMap<>();
        params.put("op", "queryDoctorStudioCommentInfo");
        params.put("type", type);
        params.put("customer_id", doctorId);
        ApiConnection.postAsyn(mRepository.BUYDOCTORSERVICE, params, callback, callback);
    }
    /**
     * 回复评论
     * @param params
     * @param tag
     * @param callback
     */
    public static void OkHttpAddReply(List<BasicNameValuePair> params, ApiCallback callback, Object tag) {
        ApiConnection.getAsyn(mRepository.DOCTORCOMMENT,params,callback,tag);
    }
    /**
     * ConsultationCouponsCount
     * 支付获取可用优惠券数量
     */
    public static void doGetConsultationCouponsCount(List<BasicNameValuePair> params, ApiCallback callback, Object tag) {
        ApiConnection.getAsyn(mRepository.CONSULTATIONCOUPONSCOUNT, params, callback, tag);
    }

    /**
     * ConsultationCouponsListServlet
     * 支付获取可用优惠券列表
     */
    public static void doGetConsultationCouponsList(List<BasicNameValuePair> params, ApiCallback callback, Object tag) {
        ApiConnection.getAsyn(mRepository.CONSULTATIONCOUPONSLIST, params, callback, tag);
    }

    /**
     * 购买具体服务支付接口
     * @param params
     * @param callback
     * @param tag
     */
    public static void doGetLectureBuyStudioServlet(List<BasicNameValuePair> params, ApiCallback callback, Object tag) {
//		ApiConnection.getAsyn("http://192.168.16.138:8080/DuoMeiHealth/ConsultationBuyServlet",params,callback,tag);
        ApiConnection.getAsyn(mRepository.CLASSROOMSERVLET, params, callback, tag);
    }

    /**
     * 添加评论
     * @param callback
     * @param tag
     */
    public static void OKHttpNewsAddComment(String content, String accountId, String infoId, ApiCallbackWrapper callback, Object tag) {
        Map<String, String> map = new HashMap<>();
        map.put("comment_content", content);
        map.put("customer_id", accountId);
        map.put("info_id", infoId);
        ApiConnection.postAsyn(mRepository.DOCTORADDCOMMENT, map, callback, tag);
    }

    /**
     * 患者确认信息
     */
    public static void OKHttpConfirm(List<BasicNameValuePair> params, ApiCallback callback, Object tag) {
        ApiConnection.getAsyn(mRepository.SERVERDETAILSERVLET, params, callback, tag);
    }

    /**
     * 医生联盟列表
     * @param searchKey
     * @param page
     * @param callback
     */
    public static void OkHttpUnionList(String searchKey, int page, ApiCallbackWrapper callback) {
        Map<String, String> params = new HashMap<>();
        params.put("Type", "queryDoctorUnion");
        params.put("search_str", searchKey);
        params.put("search_str", searchKey);
        params.put("Page", String.valueOf(page));
        params.put("PageSize", String.valueOf(10));
        ApiConnection.postAsyn(mRepository.COMMONURLWSS, params, callback);
    }

    /**
     * 医生联盟详情
     * @param doctorId
     * @param unionId
     * @param callback
     */
    public static void OkHttpUnionInfo(String doctorId, String unionId, ApiCallbackWrapper callback) {
        Map<String, String> params = new HashMap<>();
        params.put("Type", "doctorUnionHome");
        params.put("customer_id", doctorId);
        params.put("union_id", unionId);
        ApiConnection.postAsyn(mRepository.COMMONURLWSS, params, callback);
    }

    /**
     * 医生联盟大事件
     * @param unionId
     * @param page
     * @param callback
     */
    public static void OkHttpUnionIncident(String unionId, int page, ApiCallbackWrapper callback) {
        Map<String, String> params = new HashMap<>();
        params.put("Type", "queryUnionEvent");
        params.put("union_id", unionId);
        params.put("Page", String.valueOf(page));
        params.put("PageSize", String.valueOf(10));
        ApiConnection.postAsyn(mRepository.COMMONURLWSS, params, callback);
    }

    /**
     * 医生联盟加入或退出
     * @param unionId   联盟Id
     * @param stationId 工作站Id
     * @param flag      为1时进行退出联盟操作  为2时进行加入联盟操作
     * @param callback
     */
    public static void OkHttpUnionJoinOrExit(String unionId, String stationId, @IntRange(from = 1, to = 2) int flag, ApiCallbackWrapper callback) {
        Map<String, String> params = new HashMap<>();
        params.put("Type", "updateSiteUnion");
        params.put("union_id", unionId);
        params.put("customer_id", stationId);
        params.put("flag", String.valueOf(flag));
        ApiConnection.postAsyn(mRepository.COMMONURLWSS, params, callback);
    }

    /**
     * 医生联盟关注
     * @param unionId  联盟Id
     * @param doctorId 医生Id
     * @param callback
     */
    public static void OkHttpUnionFollow(String unionId, String doctorId, ApiCallbackWrapper callback) {
        Map<String, String> params = new HashMap<>();
        params.put("Type", "followUnion");
        params.put("union_id", unionId);
        params.put("customer_id", doctorId);
        ApiConnection.postAsyn(mRepository.COMMONURLWSS, params, callback);
    }

    /**
     * 医生联盟 专家团
     * @param unionId
     * @param searchKey
     * @param page
     * @param callback
     */
    public static void OkHttpUnionMember(String unionId, String searchKey, int page, ApiCallbackWrapper callback) {
        Map<String, String> params = new HashMap<>();
        params.put("Type", "findWorkSiteByHospital");
        params.put("Union_Id", unionId);
        params.put("Search_Str", searchKey);
        params.put("PageSize", String.valueOf(10));
        params.put("Page", String.valueOf(page));
        ApiConnection.postAsyn(mRepository.COMMONURLWSS, params, callback);
    }

    /**
     * 获取医生工具箱
     * @param doctorId
     * @param callback
     */
    public static void doctorTools(String doctorId, ApiCallbackWrapper callback) {
        Map<String, String> params = new HashMap<>();
        params.put("doctor_id", doctorId);
        params.put("consultation_center_id", "5");
        ApiConnection.postAsyn(mRepository.LOADINGTOOLS, params, callback);
    }

    /**
     * 健康讲堂列表
     * @param type
     * @param category
     * @param cast
     * @param callback
     */
    public static void lectureList(String stationId, int type, int category, int cast, int pageIndex, ApiCallbackWrapper callback) {
        Map<String, String> params = new HashMap<>();
        params.put("op", "queryClass");
        params.put("site_id", stationId);
        if (type != 0) params.put("type", String.valueOf(type));
        if (type != 0) params.put("category", category == 1 ? "in" : "out");
        if (type != 0) params.put("cast", String.valueOf(cast));
        params.put("pageNum", String.valueOf(pageIndex));
        ApiConnection.postAsyn(mRepository.LECTURE_LIST, params, callback);
    }

    /**
     * 根据医生id查询健康讲堂
     * @param doctorId
     * @param callback
     */
    public static void lectureListByDoctor(String doctorId, int pageIndex, ApiCallbackWrapper callback) {
        Map<String, String> params = new HashMap<>();
        params.put("op", "queryPersonClassroom");
        params.put("customer_id", doctorId);
        params.put("pageNum", String.valueOf(pageIndex));
        ApiConnection.postAsyn(mRepository.LECTURE, params, callback);
    }

    /**
     * 健康课堂图文上传
     * @param courseClass    课件类型
     * @param courseDesc     课件描述
     * @param courseInList   站内
     * @param courseInPrice  站内价格
     * @param courseListType
     * @param courseName     课件标题
     * @param courseOutList  站外
     * @param curseOutPrice  站外价格
     * @param site_id        工作站ID
     * @param uploadCustomer 上传人
     * @param path           课件文件路径
     */
    public static void lectureUploadArticle(String courseClass, String courseDesc, String courseInList,
                                            String courseInPrice, String courseListType, String courseName,
                                            String courseOutList, String curseOutPrice, String site_id,
                                            String uploadCustomer, String path, ApiCallbackWrapper callback, Activity tag) {
        Map<String, String> params = new HashMap<>();
        params.put("courseClass", courseClass);
        params.put("courseDesc", courseDesc);
        params.put("courseInList", courseInList);
        params.put("courseInPrice", courseInPrice);
        params.put("courseListType", courseListType);
        params.put("courseName", courseName);
        params.put("courseOutList", courseOutList);
        params.put("curseOutPrice", curseOutPrice);
        params.put("site_id", site_id);
        params.put("uploadCustomer", uploadCustomer);

        Map<String, File> files = new HashMap<>();
        if (!TextUtils.isEmpty(path)) {
            files.put("file", new File(path));
        }
        ApiConnection.getUploadDelegate().postAsyn(mRepository.LECTURE_UPLOAD_ARTICLE, files, params, true, callback, tag);
    }

    /**
     * @param courseDesc     课件内容描述
     * @param courseInList   医生服务集团内是否可见标记1-是 0 -否
     * @param courseInPrice  课件，医生服务集团内价格（元），0元为免费查看courseListType--课件生命周期类型：10-默认、永久；20-有时间周期；
     * @param courseName     课件名称
     * @param courseOutList  医生服务集团外是否可见标记；
     * @param curseOutPrice  课件，医生服务集团外价格（元），0元为免费查看
     * @param site_id        医生服务集团ID
     * @param uploadCustomer 课件上传人id
     * @param VideoId        视频id
     * @param callback
     */
    public static void lectureUploadVideo(String courseDesc, String courseInList, String courseInPrice,
                                          String courseName, String courseOutList, String curseOutPrice,
                                          String site_id, String uploadCustomer, String VideoId, String videoPath,
                                          String avatarPath, ApiCallbackWrapper callback, Object tag) {
        Map<String, String> params = new HashMap<>();
        params.put("courseDesc", courseDesc);
        params.put("courseInList", courseInList);
        params.put("courseInPrice", courseInPrice);
        params.put("courseName", courseName);
        params.put("courseOutList", courseOutList);
        params.put("curseOutPrice", curseOutPrice);
        params.put("site_id", site_id);
        params.put("courseListType", "10");
        params.put("uploadCustomer", uploadCustomer);
        params.put("VideoId", VideoId);
        params.put("courseAddress", videoPath);

        Map<String, File> fileParams = new HashMap<>();
        fileParams.put("small_pic", new File(avatarPath));
        ApiConnection.getUploadDelegate().postAsyn(mRepository.LECTURE_UPLOAD_VIDEO, fileParams, params, true, callback, tag);
    }

    /**
     * 获取视频上传Token
     * @param id
     * @param callback
     */
    public static void lectureToken(String id, ApiCallbackWrapper callback) {
        Map<String, String> params = new HashMap<>();
        params.put("uploadCustomer", id);
        ApiConnection.postAsyn(mRepository.LECTURE_TOKEN, params, callback);
    }

    /**
     * 查询视频地址
     * @param appKey
     * @param accid
     * @param token
     * @param body
     * @param callback
     */
    public static void lectureQueryVideo(String appKey, String accid, String token, String body, ApiCallbackWrapper callback) {
        ApiConnection.addHeader("AppKey", "appKey");
        ApiConnection.addHeader("Accid", "accid");
        ApiConnection.addHeader("Token", "token");
        ApiConnection.postAsyn(mRepository.LECTURE_TOKEN, body, callback);
    }

    /**
     * 获取课件付费详情
     */
    public static void lecturePayInfo(String id, String doctorId, ApiCallbackWrapper callback) {
        Map<String, String> params = new HashMap<>();
        params.put("op", "queryBuyPageCourseInfo");
        params.put("course_ID", id);
        params.put("customer_id", doctorId);
        ApiConnection.postAsyn(mRepository.LECTURE, params, callback);
    }

    /**
     * 获取课件详情
     * @param id
     * @param callback
     */
    public static void lectureInfo(String id, String doctorId, ApiCallbackWrapper callback) {
        Map<String, String> params = new HashMap<>();
        params.put("op", "queryBuyPageCourseInfo");
        params.put("course_ID", id);
        params.put("customer_id", doctorId);
        ApiConnection.postAsyn(mRepository.LECTURE, params, callback);
    }

    /**
     * 课件打赏
     * @param payType
     * @param siteId
     * @param doctorId
     * @param price
     * @param lectureId
     * @param callback
     */
    public static void lectureReward(int payType, String siteId, String doctorId, float price, String lectureId, ApiCallbackWrapper callback) {
        Map<String, String> params = new HashMap<>();
        params.put("op", "Reward");
        params.put("site_id", siteId);
        params.put("type", String.valueOf(payType));
        params.put("customer_id", doctorId);
        params.put("course_price", String.valueOf(price));
        params.put("course_id", lectureId);
        ApiConnection.postAsyn(mRepository.LECTURE, params, callback);
    }

    /**
     * 购买课件
     * @param payType
     * @param siteId
     * @param doctorId
     * @param price
     * @param lectureId
     * @param callback
     */
    public static void lectureOrderPay(int payType, String siteId, String doctorId, float price, String lectureId, ApiCallbackWrapper callback) {
        Map<String, String> params = new HashMap<>();
        params.put("op", "buyCourse");
        params.put("site_id", siteId);
        params.put("type", String.valueOf(payType));
        params.put("customer_id", doctorId);
        params.put("course_price", String.valueOf(price));
        params.put("course_id", lectureId);
        ApiConnection.postAsyn(mRepository.CLASSROOMSERVLET, params, callback);
    }

    /**
     * 根据医生Id查询工作站列表
     * @param id
     * @param callback
     */
    public static void stationListByDoctor(String id, ApiCallbackWrapper callback) {
        Map<String, String> params = new HashMap<>();
        params.put("op", "queryAllWorkSite");
        params.put("site_id", id);
        ApiConnection.postAsyn(mRepository.LECTURE, params, callback);
    }

    /**
     * 根据课件Id 查找健康讲堂评论列表
     * @param id
     * @param pageIndex
     * @param callback
     */
    public static void lectureCommentList(String id, int pageIndex, ApiCallbackWrapper callback) {
        Map<String, String> params = new HashMap<>();
        params.put("op", "queryEvaluation");
        params.put("course_ID", id);
        params.put("pageNum", String.valueOf(pageIndex));
        ApiConnection.postAsyn(mRepository.LECTURE, params, callback);
    }

    /**
     * 查询余额
     * @param doctorId
     * @param callback
     */
    public static void inquireOverage(String doctorId, ApiCallbackWrapper callback) {
        Map<String, String> params = new HashMap<>();
        params.put("customer_id", doctorId);
        ApiConnection.postAsyn(mRepository.DOCTORMANAGERBALANCE, params, callback);
    }

    /**
     * 上传机构
     * @param name
     * @param picturePath
     * @param desc
     * @param type
     * @param location
     * @param telephone
     * @param callback
     */
    public static void agencySubmit(String doctorId, String name, String picturePath, String desc, String type
            , String locationCode, String location, String defailLocation, String telephone, ApiCallbackWrapper callback) {
        Map<String, File> files = new HashMap<>();
        files.put("Unit_pic1", new File(picturePath));

        Map<String, String> params = new HashMap<>();
        params.put("customerId", doctorId);
        params.put("Unit_Name", name);
        params.put("Unit_specialty_Desc", desc);
        params.put("class_type", type);
        params.put("address", location);
        params.put("Area_Code", locationCode);
        params.put("Unit_Address_Desc", defailLocation);
        params.put("Unit_Tel1", telephone);
        ApiConnection.getUploadDelegate().postAsyn(mRepository.AGENCY_SUBMIT, files, params, callback, callback);
    }

    /**
     * 机构 上传活动
     * @param agencyId
     * @param name
     * @param time
     * @param desc
     * @param callback
     */
    public static void agencyActiveSubmit(String agencyId, String activeId, String name, String time, String desc, ApiCallbackWrapper callback) {
        Map<String, String> params = new HashMap<>();
        params.put("op", "createActive");
        params.put("Unit_Code", agencyId);
        params.put("activ_Code", activeId);
        params.put("activ_title", name);
        params.put("time", time);
        params.put("activ_desc", desc);
        ApiConnection.postAsyn(mRepository.AGENCY, params, callback);
    }

    /**
     * 机构 修改活动
     * @param agencyId
     * @param name
     * @param time
     * @param desc
     * @param callback
     */
    public static void agencyActiveAlter(String agencyId, String activeId, String name, String time, String desc, ApiCallbackWrapper callback) {
        Map<String, String> params = new HashMap<>();
        params.put("op", "updateActive");
        params.put("Unit_Code", agencyId);
        params.put("activ_Code", activeId);
        params.put("activ_title", name);
        params.put("activ_time", time);
        params.put("activ_desc", desc);
        ApiConnection.postAsyn(mRepository.AGENCY, params, callback);
    }

    /**
     * 机构详情
     * @param id
     * @param callback
     */
    public static void agencyInfo(String id, String type, ApiCallbackWrapper callback) {
        Map<String, String> params = new HashMap<>();
        params.put("op", "queryDetails");
        params.put("Unit_Code", id);
        params.put("att", type);
        ApiConnection.postAsyn(mRepository.AGENCY, params, callback);
    }

    /**
     * 我的机构
     * @param doctorId
     * @param index
     */
    public static void agencySelf(String doctorId, int index, ApiCallbackWrapper callback) {
        Map<String, String> params = new HashMap<>();
        params.put("op", "myOwn");
        params.put("customer_id", doctorId);
        params.put("pageNum", String.valueOf(index));
        ApiConnection.postAsyn(mRepository.AGENCY, params, callback);
    }

    /**
     * 机构列表
     */
    public static void agencyList(String areaCode, String center, String type, int pageIndex, ApiCallbackWrapper callback) {
        Map<String, String> params = new HashMap<>();
        params.put("op", "queryCenter");
        params.put("center", center);
        params.put("type", type);
        params.put("code", areaCode);
        params.put("pageNum", String.valueOf(pageIndex));
        ApiConnection.postAsyn(mRepository.AGENCY, params, callback);
    }

    /**
     * 机构推荐列表
     */
    public static void agencyRecommentList(String areaCode, String type, int pageIndex, ApiCallbackWrapper callback) {
        Map<String, String> params = new HashMap<>();
        params.put("op", "queryCenter");
        params.put("type", type);
        params.put("code", areaCode);
        params.put("pageNum", String.valueOf(pageIndex));
        ApiConnection.postAsyn(mRepository.AGENCY, params, callback);
    }
}