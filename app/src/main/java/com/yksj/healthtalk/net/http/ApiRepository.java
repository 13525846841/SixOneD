package com.yksj.healthtalk.net.http;

/**
 * 所有的http请求地址
 * @author zhao
 */
public class ApiRepository {

    public String FRIENDSINFOSET;
    public String FRIENDREMARKNAME;
    public String URL_LOAD_OFF_MESSAGES;
    public String URL_OPEN_DORTOR2;
    public String URL_DOCTOR_SETTING_CLIENT_HISTORY;
    public String URL_DOCTOR_SETTING_CLIENT;
    public String CUSTOMERCOMPLAINTSERVLET33;
    public String HOTSEARCHWORDSSERVLET;//HotSearchWordsServlet热门搜索
    /**
     * google MAP 地址
     */
    public String GOOGLE_MAP_URL = "http://maps.google.com/maps?hl=zh-CN&q=loc:";
    /**
     * 根据经纬度查询地址详情
     */
//	public String GOOLE_MAP_GECODE = "http://maps.googleapis.com/maps/api/geocode/json";
    public String GOOLE_MAP_GECODE = "http://maps.google.cn/maps/api/geocode/json";


    public String URL_FRIENDEXACTSEARCH;

    public String GROUPCONSULTATIONLIST;//GroupConsultationLisT六一健康列表接口
    public String GROUPCONSULTATIONLIST200;//GroupConsultationList200六一健康列表接口
    public String GROUPCONSULTATIONLIST100;

    public String FINDMYCONSUSERVICELIST; //FindMyConsuServiceList

    public String URL_FINDMYFRIENDS;

    public String URL_FINDMYBLACKS;

    public String USERAGREEMEN;// 用户条款

    public String USELEAD;// 引导页登录传参
    /**
     * 初次登录默认账号
     */
    // public String FRIST_LOGIN;

    // 信息新闻定制
    public String PHONEMAPCUSSERVLET;

    /**
     * 常见新账号
     */
    public String URL_CREAT_ACCOUNT;
    /**
     * 修改密码
     */
    public String CHANGE_PASSWORD;
    /**
     * 地点查询
     */
    public String GET_LOCATION;
    /**
     * 删除头像
     */
    public String DELETE_ALBUM_IMAGE;
    /**
     * 根路径
     */
    public String WEB_ROOT;
    /**
     * 初次登录默认账号
     */
    public String URL_FRIST_LOGIN;
    /**
     * 异常报告
     */
    public String URL_APPEXCEPTION_REPORT;
    /**
     * 虚拟人体查询
     */
    public String URL_QUERYSITUATION;

    /**
     * 密码找回
     */
    // public String URL_GETPASSWORD;

    /**
     * 修改密码
     */
    // public String URL_CHANGE_PASSWORD;

    /**
     * 语音文件上传
     */
    public String URL_VOICEFILE_UPLOAD;

    /**
     * 图片上传
     */
    public String URL_PHOTO_UPLOAD;

    /**
     * 图片下载
     */
    public String URL_PHOTO_DOWNLOAD;

    /**
     * 程序更新
     */
    public String URL_APP_VERSIONCHECK;

    /**
     * 信息新闻
     */
    public String URL_NEWS;

    public String APPLYCONSULTATION;//ApplyConsultation


    // 信息新闻定制
    // public String URL_PHONEMAPCUSSERVLET;

    /**
     * 虚拟医生对话
     */
    public String URL_DOCTOR_SERVLET;

    /**
     * 用户注册
     */
    public String URL_REGISTE;

    /**
     * 获取验证码
     */
    // public String URL_AUTH;

    /**
     * 获取邮箱验证码
     */
    public String URL_AUTH_EMAIL;

    /**
     * 获取手机注册的账号
     */
    public String URL_PONE_NUMBER;

    /**
     * 重置密码
     */
    public String URL_NEW_PAS;

    /**
     * 检查邮箱
     */
    public String URL_CHECKEMAIL;

    /**
     * 知识查询
     */
    public String URL_QUERYKNOWLEDGE;
    /**
     * 查询话题
     */
    public String URL_QUERYTHEME;
    /**
     * 创建话题
     */
    public String URL_CREATETHEME;

    /**
     * 个人资料查询
     */
    public String URL_QUERY_CUSTOMERINFO;

    /**
     * 群资料查询
     */
    public String URL_QUERY_GOUPINFO;

    /**
     * 图片,头像查询
     */
    public String URL_QUERYHEADIMAGE;
    public String URL_QUERYHEADIMAGE_NEW;
    /**
     * 视频下载地址
     */
    public String URL_DOWNLOAVIDEO;
    public String URL_DOWNLOAVIDEO2;

    /**
     * 上传通讯录
     */
    // public String URL_UPLOAD_CONTACTS;

    /**
     * 关注信息
     */
    // public String URL_SAVEATTENTION;

    /**
     * 更改个人头像
     */
    public String URL_HEAD_IMAGE;

    /**
     * 添加相册头像
     */
    public String URL_ALBUM_IMAGE;

    /**
     * 查询用户绑定邮箱状态
     */
    public String URL_QUERY_BINDEMAIL;

    /**
     * 新建聊天室
     */
    public String URL_NEW_GROUP;

    /**
     * 加载健康友和聊天室
     */
    public String URL_LOAD_INIT;

    /**
     * 登录后加载黑名单,好友,聊天室
     */
    public String URL_LOAD_INIT1;

    /**
     * 加载离线消息
     */
    // public String URL_LOAD_OFFIN_EMESSAGE;

    /**
     * 搜索健康友
     */
    public String URL_SEARCH_FRIENDS;

    /**
     * 搜索健康友(4.2版本用)
     */
    public String URL_FIND_FRIENDS;

    /**
     * 搜索聊天室
     */
    public String URL_SEARCH_GROUP;

    /**
     * 删除头像
     */
    // public String URL_DELETE_ALBUM_IMAGE;

    /**
     * 评分
     */
    public String URL_SETTING_GRADE;

    /**
     * 地点
     */
    // public String URL_GET_LOCATION;

    /**
     * 加载消息厅
     */
    public String URL_GROUP_HALL;

    /**
     * 获取验证码
     */
    public String URL_GET_AUTH_CODE;

    /**
     * 获取验证码
     */
    public String URL_GET_PHONE_BOUND;

    /**
     * 更改手机绑定
     */
    public String URL_CHANGE_PHONE_BOUND;
    public String HZCHANGEBINDINGPHONE210SERVLET;//HZChangeBindingPhone210Servlet

    /**
     * 按医生话题搜索
     */
    public String URL_SEARCH_DOCTOR_THEME;

    /**
     * 推送管理
     */
    public String URL_PUSH_MANGER;

    /**
     * 搜索话题
     */
    public String URL_SEARCH_SALON;

    /**
     * 抽奖
     */
    public String URL_LUCKY;

    /**
     * 查询聊天历史消息
     */
    public String URL_QUERY_HISTORY_MESSAGE;

    /**
     * 查询聊天历史消息(六一健康话题加载历史)
     */
    public String URL_HZPUSHMANGAGER;

    /**
     * 购买门票
     */
    public String URL_BUYTICKET;
    ;

    /**
     * 查询系统公告消息
     */
    public String URL_QUERY_ANNOUNCEMENTSHISTORY;

    /**
     * 广告点击次数记录
     */
    public String URL_ADDADVCLICKREQ;
    /**
     * 兴趣墙主页面
     */
    public String ALL_INTERESTWALL;
    /**
     * 兴趣墙 我的发布和我收藏
     */
    public String MY_INTERESTWALL;
    /**
     * 兴趣墙图片上传
     */
    public String INTERESTWALL_IMAGE_UPLOAD;
    /**
     * 兴趣墙修改图聊信息
     */
    public String INTERESTWALL_GROUP;
    /**
     * 兴趣墙收藏
     */
    public String INTERESTWALL_COLLECTION;
    /**
     * 图片信息
     */
    public String INTERESTWALL_MESSAGE;
    /**
     * 兴趣墙查看最近三条评论
     */
    public String INTERESTWALL_COMMEND;
    /**
     * 初始化图标
     */
    public String ICON_INIT;
    /**
     * 添加删除图标
     */
    public String ICON_ACTION;
    /**
     * 加载我的家园内各个馆的信息
     */
    public String MYZONE_FUNCTION_LOADIMAGE;
    /**
     * 加载我的家园内每个馆分别的分组信息
     */
    public String MYZONE_FUNCTION_LOADGROUP;
    /**
     * 各种馆上传
     */
    public String MYZONE_FUNCTION_UPLOADIMAGE;
    /**
     * 操作分组信息， 增删改
     */
    public String GROUP_ACTION;
    /**
     * 我发布和我收藏数量
     */
    public String USER_RELEASE_COUNT;
    /**
     * 删除图片
     */
    public String DELETE_IMAGE_INTEREST;
    public String URL_QUERYDISEASESSERVLET;// 医术症状
    public String URL_QUERYUNITSSERVLET;// 医术医院

    public String URL_QUERYMEDICINESSERVLET;// 药品
    public String URL_QUERYINSPECTIONSSERVLET;// 检查
    public String URL_QUERYTREATMENTSSERVLET;// 手术查询
    public String URL_QUERYOFFICESSERVLET;// 科室查询
    public String URL_SEEDOCTORSERVLET;// 附近就医
    public String URL_GETMONRY;
    public String FINDCENTERCLASSANDGOODSERVLET33;

    public String URL_FINDSALONFILE;// 话题文件查阅
    public String URL_DOWNLOADGROUPFILES;// 话题文件下载

    public String URL_INTERESTWALLMESSAGEBYNAME;// 兴趣墙按条件查找
    public String URL_SERVER_BG;
    public String URL_QUERYDOMAINPROPDEFINE;// 区域菜单
    /**
     * 查询用户关注和粉丝列表
     */
    public String QUERY_USER_DATA;
    /**
     * 摄影馆设置开放权限
     */
    public String PHOTO_OPEN_LEVEL;
    /**
     * 删除馆图片
     */
    public String PHOTO_DELETE_IMAGE;
    /**
     * 编辑馆图片
     */
    public String PHOTO_UPDATE_IMAGE;
    /**
     * 设置连接
     */
    public String SHOP_LINK_SET;

    /**
     * 健康讲堂
     */
    public String LECTURE;
    public String LECTURE_TOKEN;
    public String LECTURE_UPLOAD_ARTICLE;
    public String LECTURE_UPLOAD_VIDEO;
    public String LECTURE_LIST;

    /**
     * 找机构
     */
    public String AGENCY_SUBMIT;
    public String AGENCY;
    /**
     * 权限设置
     */
//	public String SHOP_AUTHORITY_SET;
    /**
     * 摄影馆收藏图片
     */
    public String SHOP_COLLECT_IMAGE;
    public String INTEREST_TRANSMIT_GROUPS;
    public String INTEREST_TRANSMIT_CUSTOMERS;

    public String CANCEL_COLLECTION;

    public String URL_SEARCH_FRIEND_H;
    public String URL_MMS_SERVLET300;

    public String URL_CREATE_SALON;
    public String URL_SINA_FRIEND;// 获取新浪好友
    public String URL_SINA_BIND;// 绑定微博
    public String URL_SINA_UNBIND;// 解除绑定
    public String URL_UNBIND_PHONE_EMAIL;// 解除手机 邮箱绑定
    public String URL_CREATOR_INFO;
    public String URL_QUERYSITUATIONSSERVLET;// 医书症状查询

    public String URL_BUYTICKETSRECORDS;// 查询门票购买记录
    public String URL_FINDTICKETMSG;// 进入话题设置时的接口
    public String URL_SALON_MEMBER;
    public String URL_UPDATE_CUSTOMINFO;// 修改个人资料
    public String URL_LOGINABOUTME;// 初始化加载我的话题和社交
    public String URL_UPLOADTICKETSETTING;// 上传设置接口
    public String URL_FINDGROUPFILE;// 文件管理
    public String URL_DELETEFILE;// 删除文件
    public String URL_FINDINCOME;// 话题收入统计
    public String URL_FINDINCOMEDETAIL;// 话题收入统计详细信息(精确到天)
    //	public String URL_OPEN_DORTOR;
//	public String URL_VIRTUALCURRENCYSERVLET;// 多美币领取
    public String URL_BUY_MONEY;// 购买多美币前,请求订单号
    public String URL_BUY_MONEY_RETURN;// 购买多美币后,通知后台
    public String URL_GET_RECORD_XIAOFEI;// 获取消费记录
    public String URL_GET_CHONGZHI_HOSTORY;// 充值历史
    public String URL_BUY_AND_SELL;// 我买我卖主页数据
    public String URL_BUY_AND_SELL_TO_FILETYPE;// 我买我卖主页数据
    public String URL_CENTERMERCHANTDEFINESERVLET;// 服务中心介绍查询
    public String URL_CENTERMERCHANTNEWSSERVLET;// 服务中心新闻或活动查询
    public String URL_YOU_INFO_MESSAGE;// 查看指定id的资料
    public String URL_CENTERMERCHANTCOMPLAINSERVLET;// 商户举报

    public String URL_SENDPICSERVLET;// 聊天语音,图片
    public String URL_JOINGROUPCHAT;// 进入群组
    public String HZINGROUPSERVLET;// 会诊进入群组
    public String GET_LOCATION_DATA;// 获取所有城市数据
    public String HZSERVICESETSERVLET42;//

    public String URL_GROUPBLACKSSET;// 黑名单设置
    public String URL_CANCELGROUPBLACKSSET;// 取消话题黑名单
    public String URL_FINDGROUPONLINECUSTOMERS;// 话题黑名单列表

    public String URL_INVITEBYNAME;// 根据昵称和账号邀请好友
    public String URL_INVITEBYNEARBY;// 要求周围的好友
    public String URL_FINDCUSTOMERSBYPARAME;// 按条件查找好友
    public String URL_SALONSAVANT;// 专家话题
    //	public String URL_SERVER_FREESHOP;// 获取自选商品的所有初始化数据
    public String URL_SERVER_SHOP_LIST;// 获取商品列表
    public String URL_FINDMYGROUPS;// 根据ID查询创建的话题
    public String URL_UPDATEGROUPINCEPTMSG;// 修改别人的话题
    public String URL_SERVER_SALON;
    public String URL_SERVER_QUERY_PINGLUN;
    public String URL_SERVER_QUERY_SEACH;
    public String URL_SALON_TO_ID;
    public String URL_SERVER_SHOP_LIST2;
    public String URL_SERVER_BG_ALL;
    public String URL_ANNOUNCEMENTSHISTORY300SERVLET;// 消息厅
    //	public String URL_DOCTOR_SETTING_INIT;
//	public String URL_SERVICESETSERVLET;// 进入聊天查看是否需要预约
    public String URL_FINDMYDOCTORS;// 我的医生
    public String URL_WITHDRAWINGREGISTRATION;// 退号
    public String URL_DELETEDATEININTERES;// 来自摄影馆照片删除
    public String URL_FINDCUSTOMERINFOBYCUSTID;// 根据ID查询个人资料
    public String URL_FINDCUSTOMERSBYCENTERGROUP;// 查询服务人员列表
    public String URL_DeleteCustomerGroupChatLog;// 群消息删除
    public String URL_FORGOTPASSWORDSERVLET;// 密码找回
    public String URL_RECOMMENDATION;// 查询药品推荐
    public String URL_GENERATEDIMENSIONALCODESERVLET;// 生成个人的二维码
    public String URL_HZGENERATEDIMENSIONALCODESERVLET;//会诊生成个人的二维码
    public String URL_CENTERDIMENSIONALCODESERVLET;// 生成商户的二维码
    public String URL_YIJIANKANG;// 生成一健康的二维码
    public String SERVER_COLLECT;// 服务中心 收藏or取消收藏(商户)
    //	public String SERVERCOLLECTDISPLAY;
    public String DELETEGROUPORDER;// 删除订单号
    public String FINDGROUPSLIST;// 我的话题
    public String MESSAGECOUNT;// 我的话题
    public String QUERYMEDICINESNEWSERVLET;// 虚拟医生聊天药品列表查询
    public String USERAGREE;// 刚进入时签订协议
    public String FINDMYPATIENTLIST;// 查询我的患者
    public String DOCTORSETTINGUI;// 医生设置主页
    public String DOCTORSETTINGUI330;// 医生设置主页
    public String DOCTORFREE_ZONE;// 医生设置主页 按钮免打扰
    //	public String DOCTORFINDMYPATIENTDETA;// 医生设置主页 按钮免打扰
    public String LEAVEMESSAGE;// 留言
    public String FINDMYDOCTORS32;// 我的医生列表
    public String MATCHINGCONTACTSSERVLET;// 匹配通讯录
    public String YAOFANGWANGSERVLET;// 药房网
    public String FINDSERVICETYPE;// 医生馆选择服务类型
    public String FINDDOCTORS32;// 医生馆列表
    public String FINDDOCTORBYNAMEORSPECIALLYORDUOMEI;// 医生馆精确查找
    public String FINDPATIENTGROUPINFO;// 查询分组
    public String ADDPATIENTGROUP;// 添加分组
    public String REMOVEPATIENTGROUP;// 移除分组
    public String FINDMYPATIENTDETAILS32;// 已经关注,包括分组
    public String FINDMYDOCTORDETAILS32;// 我的医生列表的详情
    // public String SERVICESETSERVLETRJ320;//我的医生列表按钮
//	public String ORDERDETAILS;// 查看订单详情
    public String SALONSPECIALPRICEGROUPSET;// 查看门票

    public String BUYORSELL302;
    public String BUYORSELLINFO302;
    public String RECOMMENDSHOPS302;
    public String URL_SERVICESETSERVLET32;
    public String URL_SERVICESETSERVLET33;
    public String FindCustomerByServiceItemId;
    public String AddPatientGroupMember;// 添加分组成员
    public String FINDPATIENTSBYCONTENTID;
    public String FindMerchantDocByNameOrSpeciallyOrDuomei;// 三甲医院精确查找
    public String FindDiseaseOfficeAll;// 搜索科室
    public String URL_GET_AUTH_CODE_SETTING;//
    public String HZPHONEVERIFICATIONSERVLET33;//HZPhoneVerificationServlet33

    public String URL_DownLoadPropagateViewServlet;
    public String URL_FindGuideDirectory;
    public String URL_FindActivities;

    public String URL_UpdateCustomerPrizeRecord;
    public String URL_FindIsGetPrize;
    public String GETYELLOWBOYSERVLET;//我的钱包余额
    public String SETWALLETINFOSERVLET;//我的钱包设置
    public String GETBALANCECHANGESERVLET;
    public String GOBALANCESERVLET;
    //	public String WalletBalanceServlet;//选择支付方式
    public String HZWalletBalanceServlet;//选择支付方式
    public String URL_GetPrize;
    public String SALONUNIONPAYPAYMENT;//话题银联支付
    public String SALONWALLETPAYMENT;//话题钱包支付
    public String RefundToWallet;//退款到钱包
    public String UpdateCustomerSexOrPic;//登录修改性别
    public String HomePageQueryServlet;//首页查询
    public String URL_SERVER_BG_ALL_NEW;//服务中心 新街口
    public String FindOfficeHasDoctor;
    public String URL_SERVER_BG_ALL33;
    public String FindInitialize132;
    public String FindFriendsByNicknameOrAccount;
    public String FindFriendsByGroup;
    public String GetActivitiesDesc;
    public String NewsServlet;
    public String DELTE_OFFONIE_MESSAGE;//删除离线消息
    public String SERVICESETSERVLET410;
    public String QUICKREPLYUPSAVSERVLET;//快速回复
    public String QUICKREPLYSERVLET;//快速查询
    public String ASKEDDRUGSERVLET;//求医问药
    public String DOCTORMYPARTNER;//合作伙伴列表
    public String TALKHISTORYSERVLET;//(单聊)聊天历史
    public String TALKHISTORYSERVLETS;//(单聊)聊天历史
    public String HZTalkHistoryServlet;//(六一健康单聊)聊天历史
    public String CAPTCHACODE;//找回密码第一步，验证码
    public String HZFORGOTPASSWORDSERVLET400;//HZForgotPasswordServlet400   忘记密码
    public String FINDDOCTORSBYASSISTANT;//搜素医生(4.1 新接口)
    public String FINDMYFOCUSCOUNT;//
    public String FINDMYFOCUSFRIENDS;//
    public String DELETETALKHISTORYSERVLET;//删除单聊消息
    public String DOCTORMESSAGEBOARDSSERVLET42;//医生留言
    public String BUYORSELLINFO42;//医生留言
    public String NEWSSERVLET;//收藏中的健康新闻
    public String FRIENDINFOLIKEDOC;//点赞
    public String FRIENDINFOCANCELLIKEDOC;//取消点赞
    public String URL_SERVICESETSERVLET42;
    public String NEWFINDGROUPS420;//NewFindGroups420,加载话题数据
    public String HOMEBANNERSERVLET;//首页banner
    public String SHARESERVLET;//删除个人中心新闻
    public String PERSONSHARE;//首页banner
    public String ASKEDDRUGSERVLET42;//消息直播
    public String FINDMYDOCTORDETAILS32_V42;//患者端 我的订单
    public String SERVICESETSERVLET420;//医生查看服务日历
    public String NEWSSERVLET42;//新闻内容页
    public String SERVICESETSERVLETRJ320;
    public String SERVICESETSERVLETRJ420;
    public String NEWFINDFRIENDS420;
    public String PERSONBILL;
    public String SERVICESETSERVLET44;
    public String HZSERVICESETSERVLET44;//六一健康服务
    public String SERVICESETSERVLET42;
    public String URL_QUERYHEADIMAGE_FROM_ID;//头像
    public String SERVICESETSERVLET32;//头像
    public String DELETELIXIAN42;//删除离线消息DeleteLixianServlet
    public String LOADCUSHIDPICSERVLET42;//加载头像
    public String TEMPLETCLASSMRTSERVLET;//会诊病历模板接口
    public String SAVEOREDITMEDICALRECORDSERVLET;//会诊病历保存接口SaveOrEditMedicalRecordServlet
    public String SAVEOREDITMEDICALRECORDSERVLET2;//会诊病历保存接口SaveOrEditMedicalRecordServlet
    public String SERVERDETAILSERVLET;//会诊病历提交专家接口ServerDetailServlet
    public String CONSULTATIONbUYSERVLET;//六一健康支付接口ConsultationBuyServlet
    public String CONSULTATIONBACKSERVLET;//六一健康退款接口ConsultationBackServlet
    public String SAVECONSULTATIONADVICE;//六一健康会诊专家SaveConsultationAdvice
    public String SERVICEPATIENTSERVLET;//ServicePatientServlet
    public String SERVICESETSERVLET;//ServiceSetServlet
    /**
     * 六一健康
     */
    public String PUBLICDONATE;//webwiew需要的地址
    public String DYNAMICNEWSRELEASE;//发布动态消息

    public String DYNAMICNEWEDIT;//编辑动态消息
    //	public String DYNAMICNEWSRELEASE;//GroupConsultationList
//	public String DYNAMICNEWSRELEASE;//sendVerificationCode
    public String DOCTORQUALIFICATIONCONSULTATION;//DoctorQualificationConsultation;
    public String GETCONTENTMRTSERVLET;//GetContentMRTServlet  六一健康专家助理主动创建病历
    public String DOCTORCREATEMEDICALRECORD;//六一健康专家助理创建病历上传DoctorCreateMedicalRecord
    public String DUOMEIHEALTH;//新六一健康接口DuoMeiHealth
    public String DOCTORREGISTERED;//新六一健康接口DoctorRegistered
    public String MEDICALRECORDSERVLET;//病历接口MedicalRecordServlet
    public String CONSULTATIONDETAILSDOCTORSERVLET;//ConsultationDetailsDoctorServlet病历详情医生
    public String APPLYCONSUBYASSISTANT;//ApplyConsuByAssistant医生发起
    public String CONSULTATIONDETAILSEXPERTSERVLET;//ConsultationDetailsExpertServlet病历详情专家
    public String BANDINGBANKCODE;//BandingBankCodeServlet绑定银行卡
    public String INVITATCLINICSERVLET;//InvitatClinicServlet请他门诊
    public String DOCTORUPDATE;//DoctorUpdate医生更新资料
    public String RECORDDISCUSSSERVLET;//RecordDiscussServlet  病历讨论接口
    public String ANSWERQUESTIONSERVLET;//AnswerQuestionServlet  会诊答疑
    public String FINDMYSERVICELIST;//FindMyServiceList 门诊预约列表
    public String CONSULTATIONDETAILSSERVLET;//
    public String LOADINGTOOLS;
    public String BARCODE;
    public static String HTML;//h5网页地址

    //六一班
    public String INFOCENTERSERVLET;//
    public String DOCTORFINDOFFICE;//病例分类
    public String DELETEFRIEND;//删除朋友
    //记事本
    public String ADDPLAN;
    public String CONSULTATION;
    public String ADDBALANCE;//六一健康充值
    public String JUDGEISOPEN;
    public String OPENDOCTORSETVICE;//开通服务

    /*******账户管理  ****/
    public String DOCTORMANAGERBALANCE;//账户余额
    public String DOCTORACCOUNTCHANGE;//查询账户变更记录
    public String DOCTORGETMONEY;//查询可提现余额
    /**********新接口  医教计划 2016.12.9********/
    public String DOCTORTEACH;//医教计划首页listview数据
    public String DOCTORTEACHMEMBER;//医教计划成员的listview的数据  ／／查询宝贝下的所有成员
    public String DOCTORADDBABY;//医教计划添加宝贝
    public String DOCTORBABYINFO;//查询单个宝贝的信息
    public String DOCTORPHONECUSTOM;//手机查询计划
    public String DOCTORADDPLAN;//添加计划
    public String DOCTORMODIFYBABY;//修改宝贝
    public String DOCTORADDMEMBER;//添加成员
    public String DOCTORSELECTBABY;//删除宝贝
    public String DOCTORADDCARE;//添加关心
    public String DOCTORUPDATEMEMBERREMARK;//修改成员备注
    public String DCOTORISRUN;//根据宝贝是否正在进行进行查询
    public String DOCTORPLANDETAIL;//查询某个计划以及计划下的评论信息
    public String DOCTORPLANCHANGE;//计划变更
    public String EMBODY;//六一健康体现
    public String FINDTEMPLATE;
    public String FINDFOLLOWPLAN;
    public String DELETESELFTEMPLATE;//删除个人模板库
    public String FINDFOLLOWSUBLISTBYID;//随访计划详情
    public String FINDSUBFOLLOWTEMPLATE;//私有模板详情详情
    public String SETPRIVATETEMPLATE;//模板库到自己的里面
    public String ADDSELFTEMPLATE;//新建模板
    public String ADDFOLLOW;//添加随访计划
    public String FINDORDERBYDOCTOR;//订单
    public String FINDSHARE;//分享
    public String SHAREUPLOADSERVLET;//添加分享
    public String UPLOADDOCTORSARESERVLET;//添加分享
    public String DELETESHAREBYID;//删除分享
    public String FINDSERVICESETTING;//服务开通价格
    public static String SHAREHTML;//h5网页地址（名医分享）

    public String FINDVERSIONINFOSERVICE;
    ;//查询版本说明
    public static String VERSIONHTML;//版本地址
    public String COMMONURL;//查询医生集团
    public String COMMONURLWSS;//查询医生集团
    public String UPDATEINVITESTATUS;//查询医生集团
    public String CREATSTATION;//创建医生集团

    public String GOODSSERVLET;//商城接口
    public String UPLOADCLASSROOMFILE;//健康讲堂
    public String UPLOADGROUPFILESERVLET;//六一班群文件上传
    public String CLASSROOMSERVLET;//健康讲堂
    public String QUERYYELLOWBOY;//查询支付方式
    public String BUYDOCTORSERVICE;//六一健康支付接口buyDoctorService
    public String CONSULTATIONCOUPONSCOUNT;//ConsultationCouponsCount
    public String CONSULTATIONCOUPONSLIST;//ConsultationCouponsListServlet
    public String DOWNLOADCOURSESERVLET;//健康讲堂视频地址
    public String DOWNLOADGROUPFILESERVLET;//六一班群文件下载地址
    public String DOCTORQUERYCOMMENT;//查询详细内容，评论信息
    public String DOCTORADDCOMMENT;//添加评论
    public String DOCTORCOMMENT;
    public ApiRepository(String webRoot) {
        setupWebRoot(webRoot);
    }

    /**
     * 设置服务器Ip地址
     * @param webRoot
     */
    public void setupWebRoot(String webRoot){
        WEB_ROOT = webRoot;
        NewsServlet = WEB_ROOT + "/DuoMeiHealth/NewsServlet";
        FindFriendsByGroup = WEB_ROOT + "/DuoMeiHealth/FindFriendsByGroup";
        FindFriendsByNicknameOrAccount = WEB_ROOT + "/DuoMeiHealth/FindFriendsByNicknameOrAccount";
        URL_FindGuideDirectory = WEB_ROOT + "/DuoMeiHealth/FindGuideDirectory";
        URL_DownLoadPropagateViewServlet = WEB_ROOT
                + "/DuoMeiHealth/DownLoadPropagateViewServlet";
        URL_FRIST_LOGIN = WEB_ROOT + "/DuoMeiHealth/Regist210Servlet.do";
        URL_APPEXCEPTION_REPORT = WEB_ROOT + "/DuoMeiHealth/AndroidLodServlet.do";
        URL_QUERYSITUATION = WEB_ROOT + "/DuoMeiHealth/QuerySituation";
        // URL_GETPASSWORD = WEB_ROOT + "/DuoMeiHealth/SendMailServlet.do";
        // URL_CHANGE_PASSWORD = WEB_ROOT + "/DuoMeiHealth/GetPass";
        URL_VOICEFILE_UPLOAD = WEB_ROOT + "/DuoMeiHealth/FileUpload";
        URL_PHOTO_UPLOAD = WEB_ROOT + "/DuoMeiHealth/UpLoadSer";
        URL_PHOTO_DOWNLOAD = WEB_ROOT + "/DuoMeiHealth/PotoDownSer";
        URL_APP_VERSIONCHECK = WEB_ROOT + "/DuoMeiHealth/HZVersionControlServlet";
        URL_NEWS = WEB_ROOT + "/DuoMeiHealth/News_Servlet";
        // URL_PHONEMAPCUSSERVLET = WEB_ROOT+ "/DuoMeiHealth/phoneMapCusServlet.do";
        URL_DOCTOR_SERVLET = WEB_ROOT + "/DuoMeiHealth/MMS_Servlet";
        URL_REGISTE = WEB_ROOT + "/DuoMeiHealth/RegistServlet.do";
        // URL_AUTH = WEB_ROOT + "/DuoMeiHealth/PhoneVerificationServlet.do";
        URL_AUTH_EMAIL = WEB_ROOT + "/DuoMeiHealth/SendMailServlet.do";
        URL_PONE_NUMBER = WEB_ROOT + "/DuoMeiHealth/LoginCheckServlet.do";
        URL_NEW_PAS = WEB_ROOT + "/DuoMeiHealth/UpdatePswServlet";
//		URL_CREAT_ACCOUNT = WEB_ROOT + "/DuoMeiHealth/RegistServlet300.do";
        URL_CREAT_ACCOUNT = WEB_ROOT + "/DuoMeiHealth/HZRegistServlet";
        URL_CHECKEMAIL = WEB_ROOT + "/DuoMeiHealth/CheckEmail";
        URL_QUERYKNOWLEDGE = WEB_ROOT + "/DuoMeiHealth/QueryKnowledge";
        URL_QUERYTHEME = WEB_ROOT + "/DuoMeiHealth/FindGroupServlet";
        URL_CREATETHEME = WEB_ROOT + "/DuoMeiHealth/FindCreatGroup";
        URL_QUERY_CUSTOMERINFO = WEB_ROOT + "/DuoMeiHealth/MyMessageServlet.do";
        URL_QUERY_GOUPINFO = WEB_ROOT + "/DuoMeiHealth/GroupMessageServlet.do";
        URL_QUERYHEADIMAGE = WEB_ROOT + "/DuoMeiHealth/HeadDownLoadServlet.do?path=";
        URL_DOWNLOAVIDEO = WEB_ROOT + "/DuoMeiHealth/DownLoadServlet?path=";
        URL_DOWNLOAVIDEO2 = WEB_ROOT + "/DuoMeiHealth/Mp4FileDownload?path=";
        // URL_UPLOAD_CONTACTS = WEB_ROOT + "/DuoMeiHealth/PushManagementServlet.do";
        URL_QUERYHEADIMAGE_NEW = WEB_ROOT + "/DuoMeiHealth/HeadDownLoadServlet.do?path=";//头像 图片
        // URL_LOAD_OFFIN_EMESSAGE = WEB_ROOT +
        // "/DuoMeiHealth/PushManagementServlet.do";
        URL_SEARCH_FRIENDS = WEB_ROOT + "/DuoMeiHealth/NewFindFriends32";
        URL_SEARCH_GROUP = WEB_ROOT + "/DuoMeiHealth/FindGroups";
        // URL_DELETE_ALBUM_IMAGE = WEB_ROOT +
        // "/DuoMeiHealth/DeletingAlbumPictureServlet.do";
        URL_SETTING_GRADE = WEB_ROOT + "/DuoMeiHealth/FindAndriodUrl";
        // URL_GET_LOCATION = WEB_ROOT + "/DuoMeiHealth/AccessUrbanServlet.do";
        URL_GROUP_HALL = WEB_ROOT + "/DuoMeiHealth/servlet/MessagesHallServlet";
        URL_GET_AUTH_CODE = WEB_ROOT + "/DuoMeiHealth/PhoneVerificationServlet.do";
        URL_GET_PHONE_BOUND = WEB_ROOT + "/DuoMeiHealth/CheckPhoneCodeServlet.do";
        URL_CHANGE_PHONE_BOUND = WEB_ROOT
                + "/DuoMeiHealth/ChangeBindingPhone210Servlet.do";
        URL_SEARCH_DOCTOR_THEME = WEB_ROOT
                + "/DuoMeiHealth/FindGroupByGroupdescServlet";
        URL_PUSH_MANGER = WEB_ROOT + "/DuoMeiHealth/PhoneMapCusService.do";
        URL_LUCKY = WEB_ROOT + "/DuoMeiHealth/servlet/WinningLiuChengServlet";
        URL_QUERY_HISTORY_MESSAGE = WEB_ROOT
                + "/DuoMeiHealth/PushManagementServlet.do";
        URL_QUERY_ANNOUNCEMENTSHISTORY = WEB_ROOT
                + "/DuoMeiHealth/AnnouncementsHistoryServlet.do";

        // URL_SAVEATTENTION = WEB_ROOT + "/DuoMeiHealth/phoneMapCusServlet.do";
        URL_HEAD_IMAGE = WEB_ROOT + "/DuoMeiHealth/HeadPictureUploadServlet.do";
        URL_ALBUM_IMAGE = WEB_ROOT + "/DuoMeiHealth/AvatarOperatingServlet.do";
        URL_QUERY_BINDEMAIL = WEB_ROOT + "/DuoMeiHealth/RegestUser";
        URL_NEW_GROUP = WEB_ROOT + "/DuoMeiHealth/GroupServlet.do";
        URL_LOAD_INIT = WEB_ROOT + "/DuoMeiHealth/LoginInit";
        URL_LOAD_INIT1 = WEB_ROOT + "/DuoMeiHealth/LoginInit1";
        DELETE_ALBUM_IMAGE = WEB_ROOT
                + "/DuoMeiHealth/DeletingAlbumPictureServlet.do";
        GET_LOCATION = WEB_ROOT + "/DuoMeiHealth/AccessUrbanServlet.do";
        CHANGE_PASSWORD = WEB_ROOT + "/DuoMeiHealth/GetPass";
        PHONEMAPCUSSERVLET = WEB_ROOT + "/DuoMeiHealth/phoneMapCusServlet.do";
        // FRIST_LOGIN = WEB_ROOT +"/DuoMeiHealth/Regist210Servlet.do";
        USERAGREEMEN = WEB_ROOT + "/DuoMeiHealth/UserAgreementServlet.do";
        USELEAD = WEB_ROOT + "/DuoMeiHealth/JudgeHelpPageServlet.do";
        URL_SEARCH_SALON = WEB_ROOT + "/DuoMeiHealth/NewFindGroups";
        URL_ADDADVCLICKREQ = WEB_ROOT + "/DuoMeiHealth/AddAdvClickReq";
        ALL_INTERESTWALL = WEB_ROOT + "/DuoMeiHealth/FindInterstedwall";
        MY_INTERESTWALL = WEB_ROOT + "/DuoMeiHealth/MyInterstedwallList";
        INTERESTWALL_IMAGE_UPLOAD = WEB_ROOT
                + "/DuoMeiHealth/UploadInterestedwall32";
        INTERESTWALL_COLLECTION = WEB_ROOT + "/DuoMeiHealth/AddCountForInterestwall";
        INTERESTWALL_MESSAGE = WEB_ROOT + "/DuoMeiHealth/InterestwallMessage";
        INTERESTWALL_COMMEND = WEB_ROOT
                + "/DuoMeiHealth/FindCommentsForInterestwall";
        ICON_INIT = WEB_ROOT + "/DuoMeiHealth/GetMyhomeIcon";
        ICON_ACTION = WEB_ROOT + "/DuoMeiHealth/IconServlet";
        URL_SEARCH_FRIEND_H = WEB_ROOT + "/DuoMeiHealth/NewFindFriends";
        URL_CREATE_SALON = WEB_ROOT + "/DuoMeiHealth/NewGroupServlet420";
        URL_CREATOR_INFO = WEB_ROOT + "/DuoMeiHealth/FindSalonCreateCustomer";
        MYZONE_FUNCTION_LOADIMAGE = WEB_ROOT + "/DuoMeiHealth/InitPicServlet";
        MYZONE_FUNCTION_LOADGROUP = WEB_ROOT + "/DuoMeiHealth/QueryPicClasses";
        MYZONE_FUNCTION_UPLOADIMAGE = WEB_ROOT + "/DuoMeiHealth/UploadAPic";
        GROUP_ACTION = WEB_ROOT + "/DuoMeiHealth/PictureClassServlet";
        USER_RELEASE_COUNT = WEB_ROOT + "/DuoMeiHealth/FindMyInterestsCount";
        URL_SINA_FRIEND = WEB_ROOT + "/DuoMeiHealth/FindSinaFocusList";
        URL_SINA_BIND = WEB_ROOT + "/DuoMeiHealth/BindSinaWeibo";
        URL_SINA_UNBIND = WEB_ROOT + "/DuoMeiHealth/CancelBindSinaWeibo";
//		URL_UNBIND_PHONE_EMAIL = WEB_ROOT+ "/DuoMeiHealth/UnbindEmailPhoneServlet.do";
        URL_UNBIND_PHONE_EMAIL = WEB_ROOT + "/DuoMeiHealth/HZUnbindEmailPhoneServlet";
        DELETE_IMAGE_INTEREST = WEB_ROOT + "/DuoMeiHealth/RemoveInterests";
        URL_QUERYSITUATIONSSERVLET = WEB_ROOT
                + "/DuoMeiHealth/QuerySituationsServlet";
//		URL_SALON_MEMBER = WEB_ROOT + "/DuoMeiHealth/findSalonCustomers420";
        URL_SALON_MEMBER = WEB_ROOT + "/DuoMeiHealth/NewFindFriends420";
        URL_QUERYDISEASESSERVLET = WEB_ROOT + "/DuoMeiHealth/QueryDiseasesServlet";
        URL_QUERYUNITSSERVLET = WEB_ROOT + "/DuoMeiHealth/QueryUnitsServlet";
        URL_QUERYMEDICINESSERVLET = WEB_ROOT + "/DuoMeiHealth/QueryMedicinesServlet";
        URL_QUERYINSPECTIONSSERVLET = WEB_ROOT
                + "/DuoMeiHealth/QueryInspectionsServlet";
        URL_QUERYTREATMENTSSERVLET = WEB_ROOT
                + "/DuoMeiHealth/QueryTreatmentsServlet";
        URL_QUERYOFFICESSERVLET = WEB_ROOT + "/DuoMeiHealth/QueryOfficesServlet";
        URL_SEEDOCTORSERVLET = WEB_ROOT + "/DuoMeiHealth/SeeDoctorServlet";

        URL_UPDATE_CUSTOMINFO = WEB_ROOT + "/DuoMeiHealth/UpdateCustomerInfo32";
        URL_BUYTICKETSRECORDS = WEB_ROOT + "/DuoMeiHealth/FindBuyTicketReq";
        URL_FINDTICKETMSG = WEB_ROOT + "/DuoMeiHealth/FindTicketMsg";
        URL_LOGINABOUTME = WEB_ROOT + "/DuoMeiHealth/FindInitialize32";
        URL_UPLOADTICKETSETTING = WEB_ROOT + "/DuoMeiHealth/GroupTicketSet";
        URL_FINDGROUPFILE = WEB_ROOT + "/DuoMeiHealth/FindGroupFile";
        URL_DELETEFILE = WEB_ROOT + "/DuoMeiHealth/RemoveGroupFile";
        URL_FINDINCOME = WEB_ROOT + "/DuoMeiHealth/FindIncomeByParame";
        INTERESTWALL_GROUP = WEB_ROOT + "/DuoMeiHealth/UpdateInterestwallGroup32";
        CANCEL_COLLECTION = WEB_ROOT + "/DuoMeiHealth/RemovePicCollection";
        QUERY_USER_DATA = WEB_ROOT + "/DuoMeiHealth/RelationShipList";
        PHOTO_OPEN_LEVEL = WEB_ROOT + "/DuoMeiHealth/PrivilegeSetting";
        PHOTO_DELETE_IMAGE = WEB_ROOT + "/DuoMeiHealth/DeletePic";
        PHOTO_UPDATE_IMAGE = WEB_ROOT + "/DuoMeiHealth/UpdatePic";
        URL_FINDINCOMEDETAIL = WEB_ROOT + "/DuoMeiHealth/FindIncome";
        URL_MMS_SERVLET300 = WEB_ROOT + "/DuoMeiHealth/MMS_Servlet400";
        SHOP_LINK_SET = WEB_ROOT + "/DuoMeiHealth/ShopLink";
        // SHOP_AUTHORITY_SET = WEB_ROOT+"/DuoMeiHealth/PrivilegeSetting";
        URL_GETMONRY = WEB_ROOT + "/DuoMeiHealth/VirtualCurrencyServlet";
        SHOP_COLLECT_IMAGE = WEB_ROOT + "/DuoMeiHealth/CollectionPic";
        // URL_OPEN_DORTOR = WEB_ROOT+"/DuoMeiHealth/DoctorQualification32";
        INTEREST_TRANSMIT_GROUPS = WEB_ROOT + "/DuoMeiHealth/FindMyFocusGroupsList";
        INTEREST_TRANSMIT_CUSTOMERS = WEB_ROOT
                + "/DuoMeiHealth/FindMyFocusCustomersList";
        // URL_VIRTUALCURRENCYSERVLET =
        // WEB_ROOT+"/DuoMeiHealth/VirtualCurrencyServlet";
        URL_BUY_MONEY = WEB_ROOT + "/DuoMeiHealth/CustomerBuyCurrencyServlet";
        URL_BUY_MONEY_RETURN = WEB_ROOT + "/DuoMeiHealth/BuyIncidentResponseServlet";
        URL_GET_RECORD_XIAOFEI = WEB_ROOT + "/DuoMeiHealth/FindConsumRec";
        URL_GET_CHONGZHI_HOSTORY = WEB_ROOT
                + "/DuoMeiHealth/CustomerGoldOrderServlet";
        URL_BUY_AND_SELL = WEB_ROOT + "/DuoMeiHealth/InInMyBoughtMysold301";
        URL_BUY_AND_SELL_TO_FILETYPE = WEB_ROOT + "/DuoMeiHealth/MyBoughtMySold301";
        URL_CENTERMERCHANTDEFINESERVLET = WEB_ROOT
                + "/DuoMeiHealth/CenterMerchantDefineServlet.do";
        URL_CENTERMERCHANTNEWSSERVLET = WEB_ROOT
                + "/DuoMeiHealth/CenterMerchantNewsServlet.do";
        URL_CENTERMERCHANTCOMPLAINSERVLET = WEB_ROOT
                + "/DuoMeiHealth/CenterMerchantComplainServlet";
        URL_SENDPICSERVLET = WEB_ROOT + "/DuoMeiHealth/SendPicServlet";
        URL_FRIENDEXACTSEARCH = WEB_ROOT
                + "/DuoMeiHealth/FindCustomerByDuomeiOrNickname32";
        URL_FINDMYFRIENDS = WEB_ROOT + "/DuoMeiHealth/FindMyFriends32";
        URL_FINDMYBLACKS = WEB_ROOT + "/DuoMeiHealth/FindMyBlacks32";
        URL_YOU_INFO_MESSAGE = WEB_ROOT + "/DuoMeiHealth/MyMessage300Servlet.do";
        URL_BUYTICKET = WEB_ROOT + "/DuoMeiHealth/CustomerBuyTicket32";
        URL_JOINGROUPCHAT = WEB_ROOT + "/DuoMeiHealth/InGroupServlet";
        URL_GROUPBLACKSSET = WEB_ROOT + "/DuoMeiHealth/GroupBlacksSet";
        URL_CANCELGROUPBLACKSSET = WEB_ROOT + "/DuoMeiHealth/CancelGroupBlacksSet";
        URL_FINDGROUPONLINECUSTOMERS = WEB_ROOT
                + "/DuoMeiHealth/FindGroupOnlineCustomers";
        // URL_JOINGROUPCHAT =WEB_ROOT+"/DuoMeiHealth/InGroupServlet";
        GET_LOCATION_DATA = WEB_ROOT + "/DuoMeiHealth/AllRegionServlet";
        URL_INVITEBYNAME = WEB_ROOT + "/DuoMeiHealth/FindCustomerByDuomeiOrNickname32";
        URL_INVITEBYNEARBY = WEB_ROOT + "/DuoMeiHealth/FindCustomersNearby32";
        URL_FINDCUSTOMERSBYPARAME = WEB_ROOT
                + "/DuoMeiHealth/FindCustomersByParame32";
        URL_SALONSAVANT = WEB_ROOT + "/DuoMeiHealth/FindCustomersByMerchantId32";
        // URL_SERVER_FREESHOP =
        // WEB_ROOT+"/DuoMeiHealth/FindCenterClassDefineServlet";
        URL_SERVER_SHOP_LIST = WEB_ROOT
                + "/DuoMeiHealth/FindCenterGoodsManageServlet";
        URL_FINDMYGROUPS = WEB_ROOT + "/DuoMeiHealth/FindMyGroups";
        URL_UPDATEGROUPINCEPTMSG = WEB_ROOT + "/DuoMeiHealth/UpdateGroupInceptMsg";
        URL_FINDSALONFILE = WEB_ROOT + "/DuoMeiHealth/FindSalonFile";
        URL_DOWNLOADGROUPFILES = WEB_ROOT + "/DuoMeiHealth/DownLoadGroupFiles";
        URL_SERVER_SALON = WEB_ROOT + "/DuoMeiHealth/FindMerchantCreGroup";
        // public String URL_FINDSALONFILE;//话题文件查阅
        // public String URL_DOWNLOADGROUPFILES;//话题文件下载
        URL_SERVER_QUERY_PINGLUN = WEB_ROOT
                + "/DuoMeiHealth/CenterMerchantNewsReplyServlet.do";
        URL_SERVER_QUERY_SEACH = WEB_ROOT
                + "/DuoMeiHealth/FindCenterClassDefineServlet";
        URL_SALON_TO_ID = WEB_ROOT + "/DuoMeiHealth/FindGroupById420";
        URL_SERVER_SHOP_LIST2 = WEB_ROOT
                + "/DuoMeiHealth/FindCenterClassAndGoodServlet";
        URL_INTERESTWALLMESSAGEBYNAME = WEB_ROOT
                + "/DuoMeiHealth/InterestwallMessageByName";
        URL_SERVER_BG = WEB_ROOT
                + "/DuoMeiHealth/FindCenterMerchantBackgroundServlet";
        URL_SERVER_BG_ALL = WEB_ROOT
                + "/DuoMeiHealth/CenterMerchantFunctionServlet.do";
        URL_SERVER_BG_ALL33 = WEB_ROOT
                + "/DuoMeiHealth/CenterMerchantFunctionServlet33";
        URL_ANNOUNCEMENTSHISTORY300SERVLET = WEB_ROOT
                + "/DuoMeiHealth/AnnouncementsHistory300Servlet";
        URL_QUERYDOMAINPROPDEFINE = WEB_ROOT + "/DuoMeiHealth/InitializeMapServlet";
        // URL_DOCTOR_SETTING_INIT=WEB_ROOT+"/DuoMeiHealth/ServiceSetServlet32";
        URL_DOCTOR_SETTING_CLIENT = WEB_ROOT + "/DuoMeiHealth/FindDocRegCustomers";
        // URL_SERVICESETSERVLET = WEB_ROOT +"/DuoMeiHealth/ServiceSetServlet32";
        URL_SERVICESETSERVLET32 = WEB_ROOT + "/DuoMeiHealth/ServiceSetServlet32";
        URL_SERVICESETSERVLET33 = WEB_ROOT + "/DuoMeiHealth/ServiceSetServlet33";
        URL_FINDMYDOCTORS = WEB_ROOT + "/DuoMeiHealth/FindMyDoctors";
        URL_WITHDRAWINGREGISTRATION = WEB_ROOT
                + "/DuoMeiHealth/CustomerBackOrderServlet32";
        URL_DELETEDATEININTERES = WEB_ROOT + "/DuoMeiHealth/DeleteDateInInteres";
        URL_FINDCUSTOMERINFOBYCUSTID = WEB_ROOT
                + "/DuoMeiHealth/FindCustomerInfoByCustId420";
        URL_FINDCUSTOMERSBYCENTERGROUP = WEB_ROOT
                + "/DuoMeiHealth/FindCustomersByCenterGroup";
        URL_DOCTOR_SETTING_CLIENT_HISTORY = WEB_ROOT
                + "/DuoMeiHealth/FindDocRegCustomersHis";
        URL_DeleteCustomerGroupChatLog = WEB_ROOT
                + "/DuoMeiHealth/DeleteCustomerGroupChatLog";
        URL_FORGOTPASSWORDSERVLET = WEB_ROOT
                + "/DuoMeiHealth/HZForgotPasswordServlet300";
        URL_RECOMMENDATION = WEB_ROOT + "/DuoMeiHealth/QueryMyRecommendGoods";
        URL_OPEN_DORTOR2 = WEB_ROOT + "/DuoMeiHealth/DoctorQualification33";
        URL_GENERATEDIMENSIONALCODESERVLET = WEB_ROOT
                + "/DuoMeiHealth/HZGenerateDimensionalCodeServlet";
        URL_HZGENERATEDIMENSIONALCODESERVLET = WEB_ROOT
                + "/DuoMeiHealth/HZGenerateDimensionalCodeServlet";
//		URL_LOAD_OFF_MESSAGES = WEB_ROOT+ "/DuoMeiHealth/LoadedOfflineMessageServlet.do";
        URL_LOAD_OFF_MESSAGES = WEB_ROOT + "/DuoMeiHealth/LoadedOfflineMessageServletHZ";
        SERVER_COLLECT = WEB_ROOT + "/DuoMeiHealth/MerchantCollectionServlet";
        // SERVERCOLLECTDISPLAY=WEB_ROOT+"/DuoMeiHealth/MerchantCollectionServlet";
        DELETEGROUPORDER = WEB_ROOT + "/DuoMeiHealth/DeleteGroupOrder";
        FINDGROUPSLIST = WEB_ROOT + "/DuoMeiHealth/FindGroupsList";
        MESSAGECOUNT = WEB_ROOT + "/DuoMeiHealth/messageCount";
        URL_CENTERDIMENSIONALCODESERVLET = WEB_ROOT
                + "/DuoMeiHealth/CenterDimensionalCodeServlet";
        QUERYMEDICINESNEWSERVLET = WEB_ROOT
                + "/DuoMeiHealth/QueryMedicinesNewServlet";
        USERAGREE = WEB_ROOT + "/DuoMeiHealth/UserAgreementServlet300";
        FRIENDREMARKNAME = WEB_ROOT + "/DuoMeiHealth/updateRemarksName";
        FINDMYPATIENTLIST = WEB_ROOT + "/DuoMeiHealth/FindMyPatients32";
        DOCTORSETTINGUI = WEB_ROOT + "/DuoMeiHealth/ServiceSetServlet320";
        DOCTORSETTINGUI330 = WEB_ROOT + "/DuoMeiHealth/ServiceSetServlet330";
        DOCTORFREE_ZONE = WEB_ROOT + "/DuoMeiHealth/ServiceSetServletRJ320";
        // DOCTORFINDMYPATIENTDETA=WEB_ROOT+"/DuoMeiHealth/FindMyPatientDetails32";
        LEAVEMESSAGE = WEB_ROOT + "/DuoMeiHealth/DoctorMessageBoardsServlet";
        FINDMYDOCTORS32 = WEB_ROOT + "/DuoMeiHealth/FindMyDoctors32";
        MATCHINGCONTACTSSERVLET = WEB_ROOT + "/DuoMeiHealth/MatchingContactsServlet";
        YAOFANGWANGSERVLET = WEB_ROOT + "/DuoMeiHealth/YaoFangWangServlet";
        FINDSERVICETYPE = WEB_ROOT + "/DuoMeiHealth/FindServiceType";
        FINDDOCTORS32 = WEB_ROOT + "/DuoMeiHealth/FindDoctors32";
        FINDDOCTORBYNAMEORSPECIALLYORDUOMEI = WEB_ROOT
                + "/DuoMeiHealth/FindDoctorByNameOrSpeciallyOrDuomei";
        FINDPATIENTGROUPINFO = WEB_ROOT + "/DuoMeiHealth/FindPatientGroupInfo";
        ADDPATIENTGROUP = WEB_ROOT + "/DuoMeiHealth/AddPatientGroup";
        REMOVEPATIENTGROUP = WEB_ROOT + "/DuoMeiHealth/RemovePatientGroup";
        FINDMYPATIENTDETAILS32 = WEB_ROOT + "/DuoMeiHealth/FindMyPatientDetails32";
        // ORDERDETAILS = WEB_ROOT+"/DuoMeiHealth/ServiceSetServlet32";
        BUYORSELL302 = WEB_ROOT + "/DuoMeiHealth/InInMyBoughtMySold33";
        BUYORSELLINFO302 = WEB_ROOT + "/DuoMeiHealth/MyBoughtMySold32";
        BUYORSELLINFO42 = WEB_ROOT + "/DuoMeiHealth/MyBoughtMySold42";
        RECOMMENDSHOPS302 = WEB_ROOT + "/DuoMeiHealth/IncomeServlet";
        FINDMYDOCTORDETAILS32 = WEB_ROOT + "/DuoMeiHealth/FindMyDoctorDetails32";
        // SERVICESETSERVLETRJ320 = WEB_ROOT+"/DuoMeiHealth/ServiceSetServletRJ320";
        SALONSPECIALPRICEGROUPSET = WEB_ROOT
                + "/DuoMeiHealth/SalonSpecialPriceGroupSet";
        FindCustomerByServiceItemId = WEB_ROOT
                + "/DuoMeiHealth/FindCustomerByServiceItemId";
        AddPatientGroupMember = WEB_ROOT + "/DuoMeiHealth/AddPatientGroupMember";
        FINDPATIENTSBYCONTENTID = WEB_ROOT + "/DuoMeiHealth/FindPatientsByContentId";
        FindMerchantDocByNameOrSpeciallyOrDuomei = WEB_ROOT
                + "/DuoMeiHealth/FindMerchantDocByNameOrSpeciallyOrDuomei";
        FindDiseaseOfficeAll = WEB_ROOT + "/DuoMeiHealth/FindDiseaseOfficeAll";
        URL_FindActivities = WEB_ROOT + "/DuoMeiHealth/FindActivities402";
        GetActivitiesDesc = WEB_ROOT + "/DuoMeiHealth/GetActivitiesDesc";
        URL_UpdateCustomerPrizeRecord = WEB_ROOT + "/DuoMeiHealth/UpdateCustomerPrizeRecord";
        URL_FindIsGetPrize = WEB_ROOT + "/DuoMeiHealth/FindIsGetPrize402";
        GETYELLOWBOYSERVLET = WEB_ROOT + "/DuoMeiHealth/GetYellowBoyServlet";
        SETWALLETINFOSERVLET = WEB_ROOT + "/DuoMeiHealth/SetWalletInfoServlet";
        GETBALANCECHANGESERVLET = WEB_ROOT + "/DuoMeiHealth/GetBalanceChangeServlet";
        GOBALANCESERVLET = WEB_ROOT + "/DuoMeiHealth/GoBalanceServlet";
        HZWalletBalanceServlet = WEB_ROOT + "/DuoMeiHealth/HZWalletBalanceServlet42";
        URL_GetPrize = WEB_ROOT + "/DuoMeiHealth/GetPrize";
        URL_GET_AUTH_CODE_SETTING = WEB_ROOT + "/DuoMeiHealth/PhoneVerificationServlet33";
        SALONUNIONPAYPAYMENT = WEB_ROOT + "/DuoMeiHealth/SalonUnionPayPayment";
        SALONWALLETPAYMENT = WEB_ROOT + "/DuoMeiHealth/SalonWalletPayment";
        RefundToWallet = WEB_ROOT + "/DuoMeiHealth/RefundToWallet";
        UpdateCustomerSexOrPic = WEB_ROOT + "/DuoMeiHealth/UpdateCustomerSexOrPic";
        HomePageQueryServlet = WEB_ROOT + "/DuoMeiHealth/HomePageQueryServlet2";
        URL_SERVER_BG_ALL_NEW = WEB_ROOT + "/DuoMeiHealth/CenterMerchantFunctionServlet33";
        FindOfficeHasDoctor = WEB_ROOT + "/DuoMeiHealth/FindOfficeHasDoctor";
        FINDCENTERCLASSANDGOODSERVLET33 = WEB_ROOT + "/DuoMeiHealth/FindCenterClassAndGoodServlet33";
        SERVICESETSERVLET410 = WEB_ROOT + "/DuoMeiHealth/ServiceSetServlet410";
        FindInitialize132 = WEB_ROOT + "/DuoMeiHealth/FindInitialize132";
        CUSTOMERCOMPLAINTSERVLET33 = WEB_ROOT + "/DuoMeiHealth/CustomerComplaintServlet33";
        URL_YIJIANKANG = WEB_ROOT + "/DuoMeiHealth/GenerateYijiankangDimensionalCodeServlet";
        DELTE_OFFONIE_MESSAGE = WEB_ROOT + "/DuoMeiHealth/DeleteLixianServlet";
        QUICKREPLYUPSAVSERVLET = WEB_ROOT + "/DuoMeiHealth/QuickReplyUpSavServlet";
        QUICKREPLYSERVLET = WEB_ROOT + "/DuoMeiHealth/QuickReplyServlet";
        ASKEDDRUGSERVLET = WEB_ROOT + "/DuoMeiHealth/AskedDrugServlet";
        DOCTORMYPARTNER = WEB_ROOT + "/DuoMeiHealth/QueryMyPartnersServlet";
        TALKHISTORYSERVLET = WEB_ROOT + "/DuoMeiHealth/TalkHistoryServlet";
        TALKHISTORYSERVLETS = WEB_ROOT + "/DuoMeiHealth/InfoCenterServlet";
        HZTalkHistoryServlet = WEB_ROOT + "/DuoMeiHealth/HZTalkHistoryServlet";
        CAPTCHACODE = WEB_ROOT + "/DuoMeiHealth/ForgotPasswordServlet400";
        FINDDOCTORSBYASSISTANT = WEB_ROOT + "/DuoMeiHealth/FindDoctorsByAssistant";
        FINDMYFOCUSCOUNT = WEB_ROOT + "/DuoMeiHealth/FindMyFocusCount";
        FINDMYFOCUSFRIENDS = WEB_ROOT + "/DuoMeiHealth/FindMyFocusFriends";
        DELETETALKHISTORYSERVLET = WEB_ROOT + "/DuoMeiHealth/DeleteTalkHistoryServlet";
        DOCTORMESSAGEBOARDSSERVLET42 = WEB_ROOT + "/DuoMeiHealth/DoctorMessageBoardsServlet42";
        URL_FIND_FRIENDS = WEB_ROOT + "/DuoMeiHealth/NewFindFriends420";
        NEWSSERVLET = WEB_ROOT + "/DuoMeiHealth/NewsServlet";
        FRIENDINFOLIKEDOC = WEB_ROOT + "/DuoMeiHealth/FriendsInfoSet?TYPE=likeDoc";
        FRIENDINFOCANCELLIKEDOC = WEB_ROOT + "/DuoMeiHealth/FriendsInfoSet?TYPE=cancelLikeDoc";
        URL_SERVICESETSERVLET42 = WEB_ROOT + "/DuoMeiHealth/ServiceSetServlet42";
        NEWFINDGROUPS420 = WEB_ROOT + "/DuoMeiHealth/NewFindGroups420";
        HOMEBANNERSERVLET = WEB_ROOT + "/DuoMeiHealth/HomeBannerServlet";
        PERSONSHARE = WEB_ROOT + "/DuoMeiHealth/MyInterstedwallList";
        SHARESERVLET = WEB_ROOT + "/DuoMeiHealth/InterestwallInfo";
        ASKEDDRUGSERVLET42 = WEB_ROOT + "/DuoMeiHealth/AskedDrugServlet42";
        FINDMYDOCTORDETAILS32_V42 = WEB_ROOT + "/DuoMeiHealth/FindMyDoctorDetails32";
        SERVICESETSERVLET420 = WEB_ROOT + "/DuoMeiHealth/ServiceSetServlet";
        HOTSEARCHWORDSSERVLET = WEB_ROOT + "/DuoMeiHealth/HotSearchWordsServlet";
        NEWSSERVLET42 = WEB_ROOT + "/DuoMeiHealth/NewsServlet42";
        SERVICESETSERVLETRJ320 = WEB_ROOT + "/DuoMeiHealth/ServiceSetServletRJ320";
        SERVICESETSERVLETRJ420 = WEB_ROOT + "/DuoMeiHealth/ServiceSetServletRJ420";
        NEWFINDFRIENDS420 = WEB_ROOT + "/DuoMeiHealth/NewFindFriends420";
        PERSONBILL = WEB_ROOT + "/DuoMeiHealth/GetBalanceChangeServlet";
        SERVICESETSERVLET44 = WEB_ROOT + "/DuoMeiHealth/ServiceSetServlet44";
        HZSERVICESETSERVLET44 = WEB_ROOT + "/DuoMeiHealth/HZServiceSetServlet44";
        SERVICESETSERVLET42 = WEB_ROOT + "/DuoMeiHealth/ServiceSetServlet42";
        FRIENDSINFOSET = WEB_ROOT + "/DuoMeiHealth/ConsultationInfoSet";
        URL_QUERYHEADIMAGE_FROM_ID = WEB_ROOT + "/DuoMeiHealth/HeadDownLoadServlet.do";
        SERVICESETSERVLET32 = WEB_ROOT + "/DuoMeiHealth/ServiceSetServlet32";
        DELETELIXIAN42 = WEB_ROOT + "/DuoMeiHealth/DeleteLixianServlet";
        LOADCUSHIDPICSERVLET42 = WEB_ROOT + "/DuoMeiHealth/LoadCusHidPicServlet42";
        TEMPLETCLASSMRTSERVLET = WEB_ROOT + "/DuoMeiHealth/TempletClassMRTServlet";
        GROUPCONSULTATIONLIST = WEB_ROOT + "/DuoMeiHealth/GroupConsultationList";
        GROUPCONSULTATIONLIST200 = WEB_ROOT + "/DuoMeiHealth/GroupConsultationList200";
//		GROUPCONSULTATIONLIST100 = WEB_ROOT + "/DuoMeiHealth/ConsultationBannerServlet";
        GROUPCONSULTATIONLIST100 = WEB_ROOT + "/DuoMeiHealth/ConsultationBannerServlet";
        SAVEOREDITMEDICALRECORDSERVLET = WEB_ROOT + "/DuoMeiHealth/SaveOrEditMedicalRecordServlet";
        SAVEOREDITMEDICALRECORDSERVLET2 = WEB_ROOT + "/DuoMeiHealth/SaveOrEditMedicalRecordServlet2";
        DYNAMICNEWSRELEASE = WEB_ROOT + "/DuoMeiHealth/ReleaseDynamicMessage";
        DYNAMICNEWEDIT = WEB_ROOT + "/DuoMeiHealth/UpdateDynamicMessage";
        FINDMYCONSUSERVICELIST = WEB_ROOT + "/DuoMeiHealth/FindMyConsuServiceList";
        APPLYCONSULTATION = WEB_ROOT + "/DuoMeiHealth/ApplyConsultation";
        SERVERDETAILSERVLET = WEB_ROOT + "/DuoMeiHealth/ServerDetailServlet";
        CONSULTATIONbUYSERVLET = WEB_ROOT + "/DuoMeiHealth/ConsultationBuyServlet";
        CONSULTATIONBACKSERVLET = WEB_ROOT + "/DuoMeiHealth/ConsultationBackServlet";
        SAVECONSULTATIONADVICE = WEB_ROOT + "/DuoMeiHealth/SaveConsultationAdvice";
        HZPHONEVERIFICATIONSERVLET33 = WEB_ROOT + "/DuoMeiHealth/HZPhoneVerificationServlet33";
        HZFORGOTPASSWORDSERVLET400 = WEB_ROOT + "/DuoMeiHealth/HZForgotPasswordServlet400";
        HZCHANGEBINDINGPHONE210SERVLET = WEB_ROOT + "/DuoMeiHealth/HZChangeBindingPhone210Servlet";
        DOCTORQUALIFICATIONCONSULTATION = WEB_ROOT + "/DuoMeiHealth/DoctorQualificationConsultation";
        GETCONTENTMRTSERVLET = WEB_ROOT + "/DuoMeiHealth/GetContentMRTServlet";
        DOCTORCREATEMEDICALRECORD = WEB_ROOT + "/DuoMeiHealth/DoctorCreateMedicalRecord";
        HZINGROUPSERVLET = WEB_ROOT + "/DuoMeiHealth/HZInGroupServlet";
        HZSERVICESETSERVLET42 = WEB_ROOT + "/DuoMeiHealth/HZServiceSetServlet42";
        DUOMEIHEALTH = WEB_ROOT + "/DuoMeiHealth/ConsultationInfoSet";
        DOCTORREGISTERED = WEB_ROOT + "/DuoMeiHealth/DoctorRegistered";
        MEDICALRECORDSERVLET = WEB_ROOT + "/DuoMeiHealth/MedicalRecordServlet";
        CONSULTATIONDETAILSDOCTORSERVLET = WEB_ROOT + "/DuoMeiHealth/ConsultationDetailsDoctorServlet";
        APPLYCONSUBYASSISTANT = WEB_ROOT + "/DuoMeiHealth/ApplyConsuByAssistant";
        CONSULTATIONDETAILSEXPERTSERVLET = WEB_ROOT + "/DuoMeiHealth/ConsultationDetailsExpertServlet";
        BANDINGBANKCODE = WEB_ROOT + "/DuoMeiHealth/BandingBankCodeServlet";
        INVITATCLINICSERVLET = WEB_ROOT + "/DuoMeiHealth/InvitatClinicServlet";
        DOCTORUPDATE = WEB_ROOT + "/DuoMeiHealth/DoctorUpdate";
        RECORDDISCUSSSERVLET = WEB_ROOT + "/DuoMeiHealth/RecordDiscussServlet";
        URL_HZPUSHMANGAGER = WEB_ROOT + "/DuoMeiHealth/HZPushManagementServlet";
        ANSWERQUESTIONSERVLET = WEB_ROOT + "/DuoMeiHealth/AnswerQuestionServlet";
        FINDMYSERVICELIST = WEB_ROOT + "/DuoMeiHealth/FindMyServiceList";
        SERVICEPATIENTSERVLET = WEB_ROOT + "/DuoMeiHealth/ServicePatientServlet";
        SERVICESETSERVLET = WEB_ROOT + "/DuoMeiHealth/ServiceSetServlet";
        CONSULTATIONDETAILSSERVLET = WEB_ROOT + "/DuoMeiHealth/ConsultationDetailsServlet";
        INFOCENTERSERVLET = WEB_ROOT + "/DuoMeiHealth/InfoCenterServlet";
        LOADINGTOOLS = WEB_ROOT + "/DuoMeiHealth/InfoCenterServlet?op=queryTool";
        ADDPLAN = WEB_ROOT + "/DuoMeiHealth/InfoCenterServlet";
        //PLANCHANGESTATE = WEB_ROOT + "/DuoMeiHealth/InfoCenterServlet";

        HTML = WEB_ROOT + "/DuoMeiHealth/html";
        /***新接口地址***/
        DOCTORTEACH = WEB_ROOT + "/DuoMeiHealth/mep.do?op=findChildrensByCustomerId";
        DOCTORTEACHMEMBER = WEB_ROOT + "/DuoMeiHealth/mep.do?op=findMembersByChildren";
        DOCTORADDBABY = WEB_ROOT + "/DuoMeiHealth/mepupload.do";
        DOCTORBABYINFO = WEB_ROOT + "/DuoMeiHealth/mep.do?op=findChildrenById";
        DOCTORPHONECUSTOM = WEB_ROOT + "/DuoMeiHealth/mep.do?op=findCustomerByPhone";
        DOCTORADDPLAN = WEB_ROOT + "/DuoMeiHealth/mep.do?op=addPlan";
        DOCTORADDMEMBER = WEB_ROOT + "/DuoMeiHealth/mep.do?op=addMember";
        DOCTORSELECTBABY = WEB_ROOT + "/DuoMeiHealth/mep.do?op=deleteChildren";
        DOCTORADDCARE = WEB_ROOT + "/DuoMeiHealth/mepupload.do";
        DOCTORUPDATEMEMBERREMARK = WEB_ROOT + "/DuoMeiHealth/mep.do?op=updateMemberRemark";
        DCOTORISRUN = WEB_ROOT + "/DuoMeiHealth/mep.do?op=findPlanByStatus";
        DOCTORPLANDETAIL = WEB_ROOT + "/DuoMeiHealth/mep.do?op=findPlanAndRecordById";
        DOCTORPLANCHANGE = WEB_ROOT + "/DuoMeiHealth/mep.do?op=updatePlanStatusById";
        DOCTORMODIFYBABY = WEB_ROOT + "/DuoMeiHealth/mepupload.do";
        CONSULTATION = WEB_ROOT + "/DuoMeiHealth/InfoCenterServlet?op=saveConsultationCenterSetting";
        JUDGEISOPEN = WEB_ROOT + "/DuoMeiHealth/InfoCenterServlet?op=queryOpenStatus";
        PUBLICDONATE = WEB_ROOT + "/DuoMeiHealth/liuyi_public_assistance.html";
        /***账户管理**/
        DOCTORMANAGERBALANCE = WEB_ROOT + "/DuoMeiHealth/balance.do?op=findBalanceByCustomerId";
        DOCTORACCOUNTCHANGE = WEB_ROOT + "/DuoMeiHealth/GetBalanceChangeServlet";
        ADDBALANCE = WEB_ROOT + "/DuoMeiHealth/addBalance.do?";
        EMBODY = WEB_ROOT + "/DuoMeiHealth/WithdrawServlet?";
        DOCTORGETMONEY = WEB_ROOT + "/DuoMeiHealth/InfoCenterServlet?op=queryAvailableBalance";
        DOCTORFINDOFFICE = WEB_ROOT + "/DuoMeiHealth/see.do?op=findOffice";
        DELETEFRIEND=WEB_ROOT+"/DuoMeiHealth/InfoCenterServlet";
        BARCODE = WEB_ROOT + "/DuoMeiHealth/see.do?op=getDoctorQr";

        FINDTEMPLATE = WEB_ROOT + "/DuoMeiHealth/see.do?op=findFollowTemplate";
        FINDFOLLOWPLAN = WEB_ROOT + "/DuoMeiHealth/see.do?op=findFollowListByCustomer";
        OPENDOCTORSETVICE = WEB_ROOT + "/DuoMeiHealth/see.do?op=openDoctorService";
        DELETESELFTEMPLATE = WEB_ROOT + "/DuoMeiHealth/see.do?op=deleteSelfTemplate";
        FINDFOLLOWSUBLISTBYID = WEB_ROOT + "/DuoMeiHealth/see.do?op=findFollowSubListById";
        FINDSUBFOLLOWTEMPLATE = WEB_ROOT + "/DuoMeiHealth/see.do?op=findSubFollowTemplate";
        ADDSELFTEMPLATE = WEB_ROOT + "/DuoMeiHealth/see.do?op=addSelfTemplate";
        FINDORDERBYDOCTOR = WEB_ROOT + "/DuoMeiHealth/see.do?op=findOrderByDoctor";
        FINDSHARE = WEB_ROOT + "/DuoMeiHealth/see.do";
        SHAREUPLOADSERVLET = WEB_ROOT + "/DuoMeiHealth/ShareUploadServlet";
        UPLOADDOCTORSARESERVLET = WEB_ROOT + "/DuoMeiHealth/UploadDoctorSareServlet";
        ADDFOLLOW = WEB_ROOT + "/DuoMeiHealth/see.do";
        DELETESHAREBYID = WEB_ROOT + "/DuoMeiHealth/see.do";
        FINDSERVICESETTING = WEB_ROOT + "/DuoMeiHealth/docspace.do";
        SHAREHTML = WEB_ROOT + "/DuoMeiHealth/html5/doctorSare.html";
        SETPRIVATETEMPLATE = WEB_ROOT + "/DuoMeiHealth/see.do?op=setPrivateTemplate";
//		URL_QUERYHEADIMAGE = "http://220.194.46.204:80" + "/DuoMeiHealth/HeadDownLoadServlet.do?path=";

        VERSIONHTML = WEB_ROOT + "/DuoMeiHealth";
        FINDVERSIONINFOSERVICE = WEB_ROOT + "/DuoMeiHealth/FindVersionInfoServlet";

        COMMONURL = WEB_ROOT + "/DuoMeiHealth/DoctorWorkSiteServlet";
        COMMONURLWSS = WEB_ROOT + "/DuoMeiHealth/wss";
        CREATSTATION = WEB_ROOT + "/DuoMeiHealth/CreateWorkSiteServlet";
        UPDATEINVITESTATUS = WEB_ROOT + "/DuoMeiHealth/updateInviteStatus";
        GOODSSERVLET = WEB_ROOT + "/DuoMeiHealth/goodsServlet";
        UPLOADCLASSROOMFILE = WEB_ROOT + "/DuoMeiHealth/uploadClassroomFile";
        UPLOADGROUPFILESERVLET = WEB_ROOT + "/DuoMeiHealth/UploadGroupFileServlet";
        CLASSROOMSERVLET = WEB_ROOT + "/DuoMeiHealth/classroomServlet";

        QUERYYELLOWBOY = WEB_ROOT + "/DuoMeiHealth/balance.do?op=queryYellowBoy";
        BUYDOCTORSERVICE = WEB_ROOT + "/DuoMeiHealth/see.do";
        CONSULTATIONCOUPONSCOUNT = WEB_ROOT + "/DuoMeiHealth/ConsultationCouponsCount";//
        CONSULTATIONCOUPONSLIST = WEB_ROOT + "/DuoMeiHealth/ConsultationCouponsListServlet";//
        DOWNLOADCOURSESERVLET = WEB_ROOT + "/DuoMeiHealth/DownloadCourseServlet";//
        DOWNLOADGROUPFILESERVLET = WEB_ROOT + "/DuoMeiHealth/DownLoadGroupFileServlet";//
        DOCTORQUERYCOMMENT = WEB_ROOT + "/DuoMeiHealth/see.do?op=findNewById";
        DOCTORADDCOMMENT = WEB_ROOT + "/DuoMeiHealth/see.do?op=addComment";
        DOCTORCOMMENT=WEB_ROOT+"/DuoMeiHealth/InfoCenterServlet";
        LECTURE_LIST = WEB_ROOT + "/DuoMeiHealth/dss";
        LECTURE = WEB_ROOT + "/DuoMeiHealth/PersonClassroom";
        LECTURE_TOKEN = WEB_ROOT + "/DuoMeiHealth/CreatToken";
        LECTURE_UPLOAD_ARTICLE = WEB_ROOT + "/DuoMeiHealth/uploadClassroomFile";
        LECTURE_UPLOAD_VIDEO = WEB_ROOT + "/DuoMeiHealth/UploadVideoServlet";

        AGENCY_SUBMIT = WEB_ROOT + "/DuoMeiHealth/CreateInstitutions";
        AGENCY = WEB_ROOT + "/DuoMeiHealth/InstitutionsServlet";
    }
}
