package com.yksj.healthtalk.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.Utils;
import com.yksj.consultation.app.AppContext;
import com.yksj.consultation.utils.DoctorHelper;
import com.yksj.healthtalk.entity.MainOptionEntity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * xml操作
 * @author jack_tang
 */
public class SharePreHelper {

    public static final String NEWS_READED_IDS_CACHE = "news_read_cache";//已读新闻缓存
    public static final String DYNMES_READED_IDS_CACHE = "dynmes_read_ids_cache";//已读动态消息ids
    public static final String SYMPTOM_DATA_JSON = "symptom_data_json";//症状列表
    @Deprecated public static final String FRIST_INSTALL = "install";//判断是否是第一次安装
    public static final String INTEREST_COLLECTION = "_interest_collection.cach";//兴趣墙收藏
    public static final String SERVER_NEWS_COLLECTION = "_news_collection.cach";//兴趣墙收藏
    @Deprecated public static final String LOGIN_CACHE = "LoginBean";
    public static final String FILE_CACHE = "sixOnetable";
    public static final String AVCHAT_STATE = "AVCHAT_CACHE";
    public static final String GROUP_SETTING = "GROUP_SETTING";
    private static final String CHAT_LATE_MESSAGE_LIST = "chat_late_message_list.cach";//消息厅消息历史
    public static final String QUICK_CHAT_CONTENT = "quick_chat_content.cach";//快速回复
    public static final String DOCTOR_SERVICE = "DOCTOR_SERVICE";//我的工作室-是否开通服务
    public static final String APP_CONFIG = "app_config";//应用全局配置文件

    /**
     * 是否第一次使用
     * @return
     */
    public static boolean isFirstUse() {
        SharedPreferences sp = getSharedPreferences(APP_CONFIG);
        return sp.getBoolean("is_first", true);
    }

    /**
     * 设置是否第一次使用
     * @param first
     */
    public static void setFirstUse(boolean first) {
        SPUtils.getInstance(APP_CONFIG).put("is_first", first);
    }

    /**
     * 设置wifi自动更新
     */
    public static void setWifiUpdate(boolean isUpdata) {
        SPUtils.getInstance(APP_CONFIG).put("is_wifi_update", isUpdata);
    }

    /**
     * 是否wifi自动更新
     * @return
     */
    public static boolean isWifiUpdate() {
        return SPUtils.getInstance(APP_CONFIG).getBoolean("is_wifi_update", true);
    }

    /**
     * 是否显示说明
     * @param type
     * @return
     */
    public static boolean isShowInstructions(int type) {
        String md5Str = DoctorHelper.getMD5Id();
        String vStr1 = SystemUtils.getAppVersionName(Utils.getApp());
        String key = "INSTRUCTIONS_" + md5Str + "_" + String.valueOf(type) + "_V" + vStr1;
        SharedPreferences preferences = getSharedPreferences(key);
        boolean b = preferences.getBoolean("ISFIRST", false);
        if (!b) {
            Editor editor = preferences.edit();
            editor.putBoolean("ISFIRST", true);
            editor.commit();
        }
        return b;
    }

    /**
     * 是否第一次登录
     * @return
     */
    public static boolean isFirstLogin() {
        String md5Str = DoctorHelper.getMD5Id();
        SharedPreferences preferences = getSharedPreferences("ISLOGIN_FIRST_" + md5Str);
        String vStr1 = SystemUtils.getAppVersionName(Utils.getApp());
        String vStr2 = preferences.getString("VERSION", "");
        if (vStr1.equals(vStr2)) {
            return false;
        } else {
            Editor editor = preferences.edit();
            editor.putString("VERSION", vStr1);
            editor.commit();
            return true;
        }
    }

    /**
     * 获取登录用户缓存信息
     * @return String[]{name,paswd} 没有缓存返回null
     */
    @Deprecated
    public static String[] fatchUserLoginCache() {
        SharedPreferences preferences = getSharedPreferences(LOGIN_CACHE);
        String[] str = new String[]{
                preferences.getString("name", null),
                preferences.getString("paswd", null)
        };
        return str;
    }

    /**
     * 获取登录用户缓存信息
     * @return true 缓存数据
     */
    @Deprecated
    public static boolean fatchisLoginCache() {
        SharedPreferences preferences = getSharedPreferences(LOGIN_CACHE);
        return preferences.getBoolean("isCache", false);
    }

    public static String getIMEI() {
        SharedPreferences preferences = getSharedPreferences("IMEI");
        String uid = preferences.getString("uid", null);
        if (uid != null) return uid;
        Editor editor = preferences.edit();
        uid = UUID.randomUUID().toString();
        editor.putString("uid", uid);
        editor.commit();
        return uid;
    }

    @Deprecated
    public static void deleteUserLoginCache() {
        SharedPreferences preferences = getSharedPreferences(LOGIN_CACHE);
        Editor editor = preferences.edit();
        editor.clear();
        editor.commit();
    }

    /**
     * 保存登录用户的密码和用户名
     * @param name
     * @param paswd
     * @param 是否缓存
     */
    @Deprecated
    public static void saveUserLoginCache(String name, String paswd, boolean isCache) {
        SharedPreferences preferences = getSharedPreferences(LOGIN_CACHE);
        Editor editor = preferences.edit();
        editor.putString("name", name);
        editor.putString("paswd", paswd);
        editor.putBoolean("isCache", isCache);//登录是否缓存密码
        editor.putInt("vcode", AppUtils.getAppVersionCode());
        editor.commit();
    }

    /**
     * 保存登录用户资料
     * @param json
     */
    @Deprecated
    public static void saveLoginUserInfo(String json) {
        SharedPreferences preferences = getSharedPreferences(LOGIN_CACHE);
        Editor editor = preferences.edit();
        editor.putString("user", json);
        editor.putInt("vcode", AppUtils.getAppVersionCode());
        editor.commit();
    }

    /**
     * 保存文件加载
     */
    public static void saveFileCache() {
        SharedPreferences preferences = getSharedPreferences(FILE_CACHE);
        Editor editor = preferences.edit();
        editor.putBoolean("firstWord", true);
        editor.putBoolean("firstVideo", true);
        editor.putBoolean("firstImage", true);
        editor.putInt("numWord", 0);
        editor.putLong("WordTime", 0L);
        editor.putInt("numVideo", 0);
        editor.putLong("VideoTime", 0L);
        editor.putInt("numImage", 0);
        editor.putLong("ImageTime", 0L);
        editor.commit();
    }

    /**
     * 获取文件是否为第一次读取
     * @return
     */
    public static boolean getFileCache(String key) {
        SharedPreferences preferences = getSharedPreferences(FILE_CACHE);
        return preferences.getBoolean(key, true);
    }

    /**
     * 获取文件数目
     * @return
     */
    public static Integer getFileCacheNum(String key) {
        SharedPreferences preferences = getSharedPreferences(FILE_CACHE);
        return preferences.getInt(key, 0);
    }

    /**
     * 获取文件数目
     * @return
     */
    public static long getFileCacheTime(String key) {
        SharedPreferences preferences = getSharedPreferences(FILE_CACHE);
        return preferences.getLong(key, 0L);
    }

    /**
     * 更新文件缓存
     */
    public static void updateFileCache(String key, String num, String time, boolean keyValue, Integer numValue, long timeValue) {
        SharedPreferences preferences = getSharedPreferences(FILE_CACHE);
        Editor editor = preferences.edit();
        editor.putBoolean(key, keyValue);
        editor.putInt(num, numValue);
        editor.putLong(time, timeValue);
        editor.commit();
    }

    /**
     * 获取登录用户资料
     * @param json
     */
    @Deprecated
    public static String getLoginUserInfo() {
        SharedPreferences preferences = getSharedPreferences(LOGIN_CACHE);
        return preferences.getString("user", null);
    }


    /**
     * 更新用户登录缓存密码
     * @param pas
     */
    @Deprecated
    public static void updateUserLoginPasswd(String pas) {
        SharedPreferences preferences = getSharedPreferences(LOGIN_CACHE);
        Editor editor = preferences.edit();
        editor.putString("paswd", pas);
        editor.commit();
    }

    /**
     * 更改登录状态
     * @param b true已经登录 ,false还未登录
     */
    @Deprecated
    public static void updateLoginState(boolean b) {
        SharedPreferences preferences = getSharedPreferences(LOGIN_CACHE);
        Editor editor = preferences.edit();
        editor.putBoolean("login_state", b);
        editor.commit();
    }

    /**
     * 更改视频通话状态
     * @param
     */
    public static void updateAvChateState(boolean b) {
        SharedPreferences preferences = getSharedPreferences(AVCHAT_STATE);
        Editor editor = preferences.edit();
        editor.putBoolean("avchat_state", b);
        editor.commit();
    }

    /**
     * 获取登录状态
     * @return
     */
    @Deprecated
    public static boolean getLoginState() {
        SharedPreferences preferences = getSharedPreferences(LOGIN_CACHE);
        boolean b = preferences.getBoolean("login_state", false);
        return b;
    }

    /**
     * 获取登录状态
     * @return
     */
    public static boolean getisAvChatState() {
        SharedPreferences preferences = getSharedPreferences(AVCHAT_STATE);
        return preferences.getBoolean("avchat_state", false);
    }

    /**
     * 主页锁定状态更新
     * @param b
     */
    public static boolean updateLockState() {
        String id = DoctorHelper.getMD5Id() + "_LOCK";
        boolean lockState = fatchLockState();
        editorPreference(Utils.getApp(), id, "LOCK", !lockState);
        return !lockState;
    }

    /**
     * 主页锁定状态
     * @return
     */
    public static boolean fatchLockState() {
        String id = DoctorHelper.getMD5Id() + "_LOCK";
        SharedPreferences preferences = getSharedPreferences(id);
        return preferences.getBoolean("LOCK", false);
    }

    /**
     * 保存背景图
     * @param context
     * @param path
     * @param userId
     * @param chatId
     */
    public static void saveChatBg(Context context, String path, String userId, String chatId) {
        String name = userId + "_" + chatId;
        editorPreference(context, name, name, path);
    }

    /**
     * 获取背景图路径
     * @param context
     * @param userId
     * @param chatId
     * @return
     */
    public static String fatchChatBg(Context context, String userId, String chatId) {
        String name = userId + "_" + chatId;
        SharedPreferences preferences = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        String path = preferences.getString(name, null);
        return path;
    }

    public static SharedPreferences getSharedPreferences(String name) {
        return AppContext.getApplication().getSharedPreferences(name, Context.MODE_PRIVATE);
    }

    /**
     * 清除背景图
     * @param context
     * @param userId
     * @param chatId
     */
    public static void clearChatBg(Context context, String userId, String chatId) {
        String name = userId + "_" + chatId;
        clearPreference(context, name);
    }

    /**
     * 编辑配置文件
     * @param context
     * @param fileName
     * @param key
     * @param value
     */
    public static void editorPreference(Context context, String fileName, String key, String value) {
        SharedPreferences preferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    /**
     * 根据用户id以及fileName创建一个preferences,并向里面存储数据
     * @param context
     * @param fileName
     * @param key
     * @param value
     */
    public static void editorPreferenceFromUserID(String fileName, HashMap<String, String> data) {
        SharedPreferences preferences = SharePreHelper.getSharedPreferences(createUserInfoFileName(fileName));
        if (data.size() < 1) {
            return;
        }
        Editor editor = preferences.edit();
        for (Map.Entry<String, String> entry : data.entrySet()) {
            editor.putString(entry.getKey(), entry.getValue());
        }
        editor.commit();
    }

    /**
     * 根据用户id创建一个preferences,并向里面存储数据
     * @param key
     * @param value
     */
    public static void editorStringFromUserID(Context context, String key, String data) {
        SharedPreferences sp = SharePreHelper.getSharedPreferences(createCommentGoodName());
        if (data == null || data.length() < 1) {
            return;
        }
        Editor editor = sp.edit();
        editor.putString(key, data);
        editor.commit();
    }

    /**
     * 清除配置文件
     * @param context
     * @param fileName
     */
    public static void clearPreferenceFromUserID(String fileName) {
        SharedPreferences preferences = AppContext.getApplication().getSharedPreferences(fileName, Context.MODE_PRIVATE);
        Editor editor = preferences.edit();
        editor.clear();
        editor.commit();
    }


    /**
     * 根据用户id操作
     * @param context
     * @param fileName
     * @param key
     * @param value
     */
    public static String feachPreferenceFromUserID(Context context, String fileName, String key, String defValue) {
        SharedPreferences preferences = SharePreHelper.getSharedPreferences(createCommentGoodName());
        return preferences.getString(key, defValue);
    }

    /**
     * 根据用户id获得意见内容
     * @param context
     * @param fileName
     * @param key
     * @param value
     */
    public static String feachStringFromUserID(Context context, String key, String defValue) {
        SharedPreferences preferences = SharePreHelper.getSharedPreferences(createCommentGoodName());
        return preferences.getString(key, defValue);
    }

    /**
     * 编辑配置文件
     * @param context
     * @param fileName
     * @param key
     * @param value
     */
    public static void editorPreference(Context context, String fileName, String key, boolean value) {
        SharedPreferences preferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        Editor editor = preferences.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    /**
     * 清除配置文件
     * @param context
     * @param fileName
     */
    public static void clearPreference(Context context, String fileName) {
        SharedPreferences preferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        Editor editor = preferences.edit();
        editor.clear();
        editor.commit();
    }

    /**
     * 保存已读新闻id
     * @param type   新闻类型
     * @param newsId 新闻id
     */
    public static void saveNewsReadedId(String newsId) {
        SharedPreferences preferences = getSharedPreferences(getNewCachePreferFile());
        Editor editor = preferences.edit();
        editor.putString("id", newsId);
        editor.commit();
    }

    /**
     * 保存已读动态消息id
     * @param type   新闻类型
     * @param newsId 新闻id
     */
    public static void saveDynamicReadedId(String dynId) {
        SharedPreferences preferences = getSharedPreferences(getDynCachePreferFile());
        Editor editor = preferences.edit();
        editor.putString("dynId", dynId);
        editor.commit();
    }

    /**
     * @param id 兴趣墙id
     */
    public static void savInterestWallId(String id) {
        SharedPreferences preferences = getSharedPreferences(getInterestWallPreferFile());
        Editor editor = preferences.edit();
        editor.putString("id", id);
        editor.commit();
    }

    /**
     * 最后读取的新闻id
     * @param type
     * @return
     */
    public static String fatchNewsReadedLastId() {
        SharedPreferences preferences = getSharedPreferences(getNewCachePreferFile());
        return preferences.getString("id", "0");
    }

    public static String fatchInterestWallId() {
        SharedPreferences preferences = getSharedPreferences(getInterestWallPreferFile());
        return preferences.getString("id", "0");
    }

    public static String getInterestWallPreferFile() {
        String md5Str = DoctorHelper.getMD5Id();
        return md5Str + "_INTERESTWALL_READED_CACHE";
    }

    public static String getNewCachePreferFile() {
        String md5Str = DoctorHelper.getMD5Id();
        return md5Str + "_NEWS_READED_CACHE";
    }

    public static String getDynCachePreferFile() {
        String md5Str = DoctorHelper.getMD5Id();
        return md5Str + "_DYNMES_READED_CACHE";
    }

    /**
     * APP状态更新
     * @param b
     */
    public static void updateAppState(boolean b) {
        String id = DoctorHelper.getMD5Id() + "_DOLOAD_APP";
        editorPreference(Utils.getApp(), id, "DOLOAD_APP", b);
    }

    /**
     * APP更新的状态,默认是提示更新
     * @return
     */
    public static boolean fatchAppState() {
        String id = DoctorHelper.getMD5Id() + "_DOLOAD_APP";
        SharedPreferences preferences = getSharedPreferences(id);
        return preferences.getBoolean("DOLOAD_APP", true);
    }


    /**
     * 获得虚拟医生人体图数据版本
     * @param context
     * @return 版本 如果返回null表示当前还未存
     */
    public static String getSymptomJsonVersion(Context context) {
        String version = getSharePreFernces(context, SYMPTOM_DATA_JSON).getString("symptom_datas_version", null);
        return version;
    }

    /**
     * 更新虚拟人体数据版本
     * @param context
     */
    public static void updateSymptomVersion(Context context, String version, String jsonStr) {
        SharedPreferences preferences = getSharePreFernces(context, SYMPTOM_DATA_JSON);
        Editor editor = preferences.edit();
        editor.remove("symptom_datas_version");
        editor.remove("symptom_datas");
        editor.putString("symptom_datas_version", version);
        editor.putString("symptom_datas", jsonStr);
        editor.commit();
    }

    /**
     * 返回人体数据
     * @param context
     * @return JSONArray
     */
    public static JSONArray getSymptomJsonArray(Context context) {
        SharedPreferences preferences = getSharePreFernces(context, SYMPTOM_DATA_JSON);
        String string = preferences.getString("symptom_datas", null);
        JSONArray jsonArray = null;
        if (string != null) {
            try {
                jsonArray = new JSONArray(string);
            } catch (JSONException e) {
            }
        }
        return jsonArray;
    }

    public static void saveBoolean(SharedPreferences preferences, String key,
                                   boolean value) {
        getEditor(preferences).putBoolean(key, value).commit();
    }

    public static Editor getEditor(SharedPreferences preferences) {
        return preferences.edit();
    }

    public static SharedPreferences getSharePreFernces(Context context, String name) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        return sharedPreferences;
    }


    /**
     * @param @param context
     * @return void
     * @throws
     */
    @Deprecated
    public static void setSharePreInstall(Context context, String userAccount, String password) {
        SharedPreferences preferences = context.getSharedPreferences(FRIST_INSTALL, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("isFrist", true);
        editor.putString("user_account", userAccount);
        editor.putString("user_password", password);
        editor.commit();
    }

    /**
     * @param @param context
     * @param @param isCancel
     * @return void
     * @throws
     */
    @Deprecated
    public static void setCancel(Context context, boolean isCancel) {
        SharedPreferences preferences = context.getSharedPreferences(FRIST_INSTALL, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("isCancel", isCancel);
        editor.commit();
    }

    /**
     * @param @param  context
     * @param @return
     * @return boolean
     * @throws
     */
    @Deprecated
    public static boolean isCancel(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(FRIST_INSTALL, Context.MODE_PRIVATE);
        return preferences.getBoolean("isCancel", false);
    }

    /**
     * @param @param context
     * @param @param isRemember
     * @return void
     * @throws
     */
    @Deprecated
    public static void setRememberPassWord(Context context, boolean isRemember) {
        SharedPreferences preferences = context.getSharedPreferences(FRIST_INSTALL, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("isRemember", isRemember);
        editor.commit();
    }

    /**
     * @param @param  context
     * @param @return
     * @return boolean
     * @throws
     */
    @Deprecated
    public static boolean isRememberPassWord(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(FRIST_INSTALL, Context.MODE_PRIVATE);
        return preferences.getBoolean("isRemember", false);
    }

    public static boolean isInterestCollection(Context context, String id) {
        return false;
    }

    /**
     * 保存最近联系人
     * @param map
     */
    public static void saveChatLatelyList(Map<String, List<String>> map) {
        ObjectOutputStream outputStream = null;
        try {
            outputStream = new ObjectOutputStream(AppContext.getApplication().openFileOutput(getChatLateMesgListFileName(), Context.MODE_PRIVATE));
            outputStream.writeObject(map);
            outputStream.flush();
        } catch (Exception e) {
            e.printStackTrace();
            AppContext.getApplication().deleteFile(getChatLateMesgListFileName());
        } finally {
            if (outputStream != null)
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }

    /**
     * 获得最近联系人的文件名字
     * @return
     */
    private static String getChatLateMesgListFileName() {
        String string = DoctorHelper.getMD5Id();
        return string + CHAT_LATE_MESSAGE_LIST;
    }

    /**
     * 最近联系人
     * @return
     */
    public static Map<String, List<String>> fatchChatLatelyList() {
        ObjectInputStream inputStream = null;
        try {
            inputStream = new ObjectInputStream(AppContext.getApplication().openFileInput(getChatLateMesgListFileName()));
            return (Map<String, List<String>>) inputStream.readObject();
        } catch (Exception e) {
            AppContext.getApplication().deleteFile(getChatLateMesgListFileName());
            return new HashMap<String, List<String>>();
        } finally {
            if (inputStream != null)
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }


    /**
     * 创建好评存储文件的名字
     * 加上版本号
     */
    private static String createCommentGoodName() {
        return DoctorHelper.getMD5Id() + "version" + AppUtils.getAppVersionName() + "_comment";
    }

    /**
     * 创建用户信息存储文件的名字
     * 加上版本号
     */
    private static String createUserInfoFileName(String fileName) {
        return DoctorHelper.getMD5Id() + "version" + AppUtils.getAppVersionName() + fileName;
    }

    /**
     * 查询是否好评 默认false
     * 如果返回false 再判断今天是否出现过
     * @return
     */
    public static boolean feachCommentGood() {
        SharedPreferences sharedPreferences = SharePreHelper.getSharedPreferences(createCommentGoodName());
        if (sharedPreferences.getBoolean("isCommentGood", false)) {
            return true;
        } else {//再判断今天是否出现过
            return sharedPreferences.getBoolean("isDayComment", false);
        }
    }

    /**
     * 更新好评
     * @return
     */
    public static boolean updateCommentGood() {
        Editor edit = SharePreHelper.getSharedPreferences(createCommentGoodName()).edit();
        boolean isCommentG = feachCommentGood();
        edit.putBoolean("isCommentGood", !isCommentG).putBoolean("isDayComment", false).commit();
        return !isCommentG;
    }

    /**
     * 再说吧  表示今天不更新了
     */
    public static void updateCommentGoodDay() {
        Editor edit = SharePreHelper.getSharedPreferences(createCommentGoodName()).edit();
        boolean isCommentG = feachCommentGood();
        edit.putBoolean("isCommentGood", isCommentG).putBoolean("isDayComment", true).commit();
    }

    //主页添加按钮排版
    public static void upDataMainTabOption(MainOptionEntity str) {
        SharedPreferences sharedPreferences = SharePreHelper.getSharedPreferences(createCommentGoodName());
        Editor edit = sharedPreferences.edit();
        try {
            JSONObject formatJson = MainOptionEntity.formatJson(str);
            String string = sharedPreferences.getString("main_param", "");
            JSONArray array;
            if (HStringUtil.isEmpty(string))
                array = new JSONArray();
            else
                array = new JSONArray(string);

            array.put(formatJson);
            edit.putString("main_param", array.toString()).commit();
        } catch (Exception e) {
            edit.commit();
        }
    }

    //主页查询按钮排版
    public static List<MainOptionEntity> feachDataMainTabOption() {
        List<MainOptionEntity> mEntities;
        try {
            SharedPreferences sharedPreferences = SharePreHelper.getSharedPreferences(createCommentGoodName());
            String string = sharedPreferences.getString("main_param", "");
            JSONArray array = new JSONArray(string);
            MainOptionEntity entity;
            mEntities = new ArrayList<MainOptionEntity>();
            for (int i = 0; i < array.length(); i++) {
                entity = new MainOptionEntity();
                JSONObject object = array.getJSONObject(i);
//				entity.drawble=object.getInt("IconId");
                entity.id = object.getInt("Id");
                entity.text = object.getString("Text");
                if (mEntities.contains(entity)) continue;
                mEntities.add(entity);
            }
            return mEntities;
        } catch (JSONException e) {
            return new ArrayList<MainOptionEntity>();
        }
    }

    //主页按钮排版
    public static void removeDataMainTabOption(MainOptionEntity str) {
        SharedPreferences sharedPreferences = SharePreHelper.getSharedPreferences(createCommentGoodName());
        Editor edit = sharedPreferences.edit();
        try {
            com.alibaba.fastjson.JSONObject object = com.alibaba.fastjson.JSONObject.parseObject(MainOptionEntity.formatJson(str).toString());
            String string = sharedPreferences.getString("main_param", "");
            if (!HStringUtil.isEmpty(string)) {
                com.alibaba.fastjson.JSONArray jsonArray = com.alibaba.fastjson.JSONArray.parseArray(string);
                jsonArray.remove(object);
                edit.putString("main_param", jsonArray.toString()).commit();
            }
        } catch (Exception e) {
            edit.commit();
        }
    }

    /**
     * 清空搜索记录
     */
    public static void clearSearchHistory(Activity activity, String keyName) {
        SharedPreferences sp = SharePreHelper.getSharedPreferences(createCommentGoodName());
        sp.edit().putString(keyName, "").commit();
    }

    /**
     * 删除单条搜索记录
     */
    public static void saveResultHistory(Activity activity, String keyName, String result) {
        SharedPreferences sp = SharePreHelper.getSharedPreferences(createCommentGoodName());
        sp.edit().putString(keyName, result).commit();

    }

    /**
     * 获取搜索记录
     */
    public static ArrayList<HashMap<String, String>> getSearchHistory(Activity activity, String keyName) {
        ArrayList<HashMap<String, String>> history = new ArrayList<HashMap<String, String>>();
        SharedPreferences sp = SharePreHelper.getSharedPreferences(createCommentGoodName());
        String save_history = sp.getString(keyName, "");
        if (!"".equals(save_history)) {//有数据
            String[] hisArrays = save_history.split(",");
            for (int i = 0; i < hisArrays.length; i++) {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("name", hisArrays[i]);
                history.add(map);
            }
        }
        return history;
    }

    /**
     * 保存日志记录
     * @param activity
     * @param keyName
     * @param text
     */
    public static void saveConlog(Activity activity, String keyName, String text) {
        SharedPreferences sp = SharePreHelper.getSharedPreferences(createCommentGoodName());
        String save_Str = sp.getString(keyName, "");
        String[] hisArrays = save_Str.split(",");
        for (int i = 0; i < hisArrays.length; i++) {
            if (hisArrays[i].equals(text))
                return;
        }
        if (hisArrays.length > 2) {//存放数量为7条时删除最后一条
            save_Str = save_Str.substring(0, save_Str.lastIndexOf(","));
        }
        StringBuilder sb = new StringBuilder(text);
        sb.append("," + save_Str);
        sp.edit().putString(keyName, sb.toString()).commit();
    }

    /**
     * 保存搜索历史记录
     * @param activity
     * @param keyName  保存的数据对应的key
     * @param text     保存的数据
     */
    public static void saveSearchHistory(Activity activity, String keyName, String text) {
        SharedPreferences sp = SharePreHelper.getSharedPreferences(createCommentGoodName());
        String save_Str = sp.getString(keyName, "");
        String[] hisArrays = save_Str.split(",");
        for (int i = 0; i < hisArrays.length; i++) {
            if (hisArrays[i].equals(text))
                return;
        }
        if (hisArrays.length > 2) {//存放数量为7条时删除最后一条
            save_Str = save_Str.substring(0, save_Str.lastIndexOf(","));
        }
        StringBuilder sb = new StringBuilder(text);
        sb.append("," + save_Str);
        sp.edit().putString(keyName, sb.toString()).commit();
    }

    //    public static void saveDossierKey(List<Map<String,String>> datas){
//        SharedPreferences sp = getSharedPreferences(SmartFoxClient.getLoginUserId());
//        Editor editor = sp.edit();
//        for(int i=0;i<datas.size();i++){
//            Map<String,String> map=datas.get(i);
//            editor.putString(,)
//        }
//    }
    public static void applyConsult() {
        SharedPreferences applydata = SharePreHelper.getSharedPreferences("APPLYDATA");
    }


    /**
     * 保存群聊消息提醒状态
     * @param list 群列表 默认 关闭
     */
    public static void saveGroupMsgTips(List<String> list) {
        SharedPreferences preferences = getSharedPreferences(GROUP_SETTING);
        Editor editor = preferences.edit();
        for (int i = 0; i < list.size(); i++) {
            editor.putBoolean(list.get(i), false);
            editor.apply();
        }
    }

    /**
     * 保存群聊消息提醒状态
     * @param id 单个群聊 默认 关闭
     */
    public static void saveGroupMsgTip(String id) {
        SharedPreferences preferences = getSharedPreferences(GROUP_SETTING);
        Editor editor = preferences.edit();
        editor.putBoolean(id, false);
        editor.apply();
    }

    /**
     * 获取群聊消息提醒
     * @param id 群聊id
     */
    public static boolean getGroupMsgTip(String id) {
        SharedPreferences preferences = getSharedPreferences(GROUP_SETTING);
        boolean type = preferences.getBoolean(id, false);
        return type;
    }

    /**
     * 更新群聊消息提醒
     * @param id 群聊id
     */
    public static void updateGroupMsgTip(String id, boolean status) {
        SharedPreferences preferences = getSharedPreferences(GROUP_SETTING);
        Editor editor = preferences.edit();
        editor.putBoolean(id, status);
        editor.apply();
    }

    /**
     * 获取服务是否开通
     * @param id 服务类型ID
     */
    public static boolean getDoctorServiceTip(String id) {
        SharedPreferences preferences = getSharedPreferences(DOCTOR_SERVICE);
        boolean type = preferences.getBoolean(id, false);
        return type;
    }

    /**
     * 更新服务是否开通
     * @param id 服务类型ID
     */
    public static void updateDoctorServiceMsgTip(String id, boolean status) {
        SharedPreferences preferences = getSharedPreferences(DOCTOR_SERVICE);
        Editor editor = preferences.edit();
        editor.putBoolean(id, status);
        editor.apply();
    }


}
