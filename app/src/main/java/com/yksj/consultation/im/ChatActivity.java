package com.yksj.consultation.im;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.library.base.base.BaseTitleActivity;
import com.library.base.utils.EventManager;
import com.library.base.utils.StorageUtils;
import com.orhanobut.logger.Logger;
import com.yksj.consultation.adapter.ChatAdapter;
import com.yksj.consultation.app.AppContext;
import com.yksj.consultation.app.AppData;
import com.yksj.consultation.bean.GoodsEvent;
import com.yksj.consultation.bean.MyConstant;
import com.yksj.consultation.bean.ResponseBean;
import com.yksj.consultation.comm.DoubleBtnFragmentDialog;
import com.yksj.consultation.comm.DoubleBtnFragmentDialog.OnDilaogClickListener;
import com.yksj.consultation.comm.ImageBrowserActivity;
import com.yksj.consultation.comm.SingleBtnFragmentDialog;
import com.yksj.consultation.constant.Constant;
import com.yksj.consultation.constant.ObjectType;
import com.yksj.consultation.event.EChatClearHide;
import com.yksj.consultation.event.MyEvent;
import com.yksj.consultation.fragment.ChatInputControlFragment;
import com.yksj.consultation.fragment.ChatInputControlFragment.ChatInputControlListener;
import com.yksj.consultation.service.CoreService;
import com.yksj.consultation.service.CoreService.CoreServiceBinder;
import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.sonDoc.chatting.sixoneclass.group.GroupDataActivity;
import com.yksj.consultation.sonDoc.consultation.consultationorders.AtyOrderDetails;
import com.yksj.consultation.sonDoc.consultation.main.InviteMemActivity;
import com.yksj.consultation.sonDoc.friend.DoctorClinicMainActivity;
import com.yksj.consultation.sonDoc.views.VUMeterView;
import com.yksj.consultation.utils.ActivityHelper;
import com.yksj.consultation.utils.CropUtils;
import com.yksj.consultation.utils.DoctorHelper;
import com.yksj.healthtalk.db.ChatUserHelper;
import com.yksj.healthtalk.entity.GroupInfoEntity;
import com.yksj.healthtalk.entity.MessageEntity;
import com.yksj.healthtalk.media.ArmMediaPlay;
import com.yksj.healthtalk.media.ArmMediaPlay.ArmMediaPlayListener;
import com.yksj.healthtalk.media.ArmMediaRecord;
import com.yksj.healthtalk.net.http.ApiCallback;
import com.yksj.healthtalk.net.http.ApiCallbackWrapper;
import com.yksj.healthtalk.net.http.ApiService;
import com.yksj.healthtalk.net.http.AsyncHttpResponseHandler;
import com.yksj.healthtalk.net.http.BinaryHttpResponseHandler;
import com.yksj.healthtalk.net.http.HttpResult;
import com.yksj.healthtalk.net.socket.SmartFoxClient;
import com.yksj.healthtalk.utils.BitmapUtils;
import com.yksj.healthtalk.utils.DataParseUtil;
import com.yksj.healthtalk.utils.HStringUtil;
import com.yksj.healthtalk.utils.SharePreHelper;
import com.yksj.healthtalk.utils.SystemUtils;
import com.yksj.healthtalk.utils.ToastUtil;

import org.apache.http.message.BasicNameValuePair;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import org.handmark.pulltorefresh.library.PullToRefreshListView;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import okhttp3.Request;

/**
 * 聊天UI
 * @author jack_tang
 */
public class ChatActivity extends BaseTitleActivity implements OnClickListener, ChatInputControlListener, ArmMediaPlayListener {
    private final static String TAG = ChatActivity.class.getName();

    ListView mListView;
    public VUMeterView mChatVm;
    private PopupWindow mPopupWindow;
    private PopupWindow mOptionWindow;
    public ArmMediaPlay mediaPlay;
    public ArmMediaRecord mediaRecord;
    PullToRefreshListView mPullToRefreshListView;
    private ImageView imgExpert, imgDoctor, imgPatient;
    private ImageView mChatBgImageV;//聊天背景
    private TextView tvPatientName;
    //当前需要复制的view
    TextView mCopyTxtV;

    private Looper mLooper;
    ChatAdapter mChatAdapter;
    private ChatHandler mChatHandler;
    ChatUserHelper mDbUserHelper;//数据库操作
    CoreService mPushService;//后台服务对象
    ChatInputControlFragment mChatInputControlFragment;//聊天操作面板
    private RelativeLayout memberLayout;//群聊成员布局

    MessageEntity mPlayingEntity;
    GroupInfoEntity mGroupInfoEntity;//群聊实体
    //    CustomerInfoEntity mCustomerInfoEntity;//单聊实体
    AppData mAppData;
    File mChatBgFile;//聊天背景图片

    String caseFlag = null;//多人聊天是否显示成员
    private boolean isGroupChat = false;//是否是群聊
    private boolean isFristLoad = true;//是否是第一次加载
    private String allCustomerId;//三个人的id拼的字符串
    //	private boolean isPayed = true;//是否已经支付


    private String mConsultationId = "";
    public String mChatId = "";//聊天id
    private String mObjectType = "";//单聊聊天类型 10 会诊 20 门诊预约 30 特殊服务 40图文群聊
    private String mOrderId = "";//订单id
    private int mGroupType = 0;// 0 单聊 1群聊 3特殊服务单聊
    public String mName = "";//聊天name
    boolean consultType = true;// false 不可以聊天 true 可以聊天

    @Override
    public int createLayoutRes() {
        return R.layout.chat_layout;
    }

    @Override
    public void initialize(Bundle bundle) {
        super.initialize(bundle);
        initData(bundle);
        initializeView();
        initHistoryMsg();
        bindService();
        registerReceiver();
    }

    /**
     * 初始化数据
     */
    private void initData(Bundle bundle) {
        //线程管理
        HandlerThread handlerThread = new HandlerThread("chathandler");
        handlerThread.start();
        mLooper = handlerThread.getLooper();
        mChatHandler = new ChatHandler(mLooper);

        //数据库帮助类实例
        mDbUserHelper = ChatUserHelper.getInstance();

        //录音,播放初始化
        mediaPlay = new ArmMediaPlay();
        mediaRecord = new ArmMediaRecord();
        mediaPlay.setMediaPlayListener(this);

        //缓存数据
        mAppData = AppContext.getAppData();

        //数据不为空
        if (bundle != null) {
            if (bundle.containsKey(Constant.Chat.ORDER_ID)) {
                mOrderId = bundle.getString(Constant.Chat.ORDER_ID);
            }
            //群聊
            if (bundle.containsKey("group_entity")) {
                mGroupInfoEntity = (GroupInfoEntity) bundle.getParcelable("group_entity");
                mChatId = mGroupInfoEntity.getId();
                if (getIntent().hasExtra(Constant.Chat.OBJECT_TYPE)) {
                    mObjectType = getIntent().getStringExtra(Constant.Chat.OBJECT_TYPE);
                }
                isGroupChat = true;
            } else {//单聊
//                mCustomerInfoEntity = (CustomerInfoEntity) bundle.getSerializable("user_entity");
//                mChatId = mCustomerInfoEntity.getId();
//                mObjectType = mCustomerInfoEntity.getObjectType();
//                mConsultationId = mCustomerInfoEntity.getConsultationId();
                isGroupChat = false;
            }
        } else {
            Object object = getIntent().getParcelableExtra(Constant.Chat.KEY_PARAME);
            mObjectType = getIntent().getStringExtra(Constant.Chat.OBJECT_TYPE);
            mOrderId = getIntent().getStringExtra(Constant.Chat.ORDER_ID);

            if (object instanceof GroupInfoEntity) {//群聊
                mGroupType = 1;
                mGroupInfoEntity = (GroupInfoEntity) object;
                mChatId = mGroupInfoEntity.getId();
                isGroupChat = true;
            } else if (getIntent().hasExtra(Constant.Chat.GROUP_ID)) {//会诊群聊
                mGroupType = 1;
                mChatId = getIntent().getStringExtra(Constant.Chat.GROUP_ID);
                mConsultationId = getIntent().getStringExtra(Constant.Chat.CONSULTATION_ID);
                isGroupChat = true;
            } else if (getIntent().hasExtra(MyConstant.SERVICE_CHAT)) {//特殊服务单聊
                mGroupType = 3;
                mChatId = getIntent().getStringExtra(MyConstant.SERVICE_CHAT);
                isGroupChat = false;
            } else {//会诊单聊
                mChatId = getIntent().getStringExtra(Constant.Chat.SINGLE_ID);
                mObjectType = getIntent().getStringExtra(Constant.Chat.OBJECT_TYPE);
                mConsultationId = getIntent().getStringExtra(Constant.Chat.CONSULTATION_ID);
                isGroupChat = false;
                if ("30".equals(mObjectType) || "10".equals(mObjectType)) {
                    mGroupType = 3;
                }
            }
        }
        if (TextUtils.isEmpty(mChatId)) {
            ToastUtils.showShort("加入聊天室失败, 聊天室ID不能为空");
            finish();
            return;
        }
    }

    private void initializeView() {
        mChatBgImageV = (ImageView) findViewById(R.id.chat_bg);
        mChatVm = (VUMeterView) findViewById(R.id.chat_vm);
        mChatVm.setMediaRecord(mediaRecord);
        if (getIntent().hasExtra(Constant.Chat.CONSULTATION_TYPE)) {
            consultType = false;
        }
        mChatInputControlFragment = (ChatInputControlFragment) getSupportFragmentManager().findFragmentById(R.id.input_control);
        mPullToRefreshListView = (PullToRefreshListView) findViewById(R.id.pull_refresh_listview);
        mPullToRefreshListView.setOnRefreshListener(mOnRefreshListener2);
        mListView = mPullToRefreshListView.getRefreshableView();
        mListView.setAdapter(mChatAdapter = new ChatAdapter(this, isGroupChat, mChatId, mObjectType));
        mListView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_NORMAL);
        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                switch (scrollState) {
                    case OnScrollListener.SCROLL_STATE_IDLE:
                        if (view.getLastVisiblePosition() == (view.getCount() - 1)) {//最后一条显示状态
                            mListView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);//始终滑动到最后
                        } else {
                            mListView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_DISABLED);
                        }
                        break;
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            }
        });
        mListView.setOnTouchListener((v, event) -> mGestureDetector.onTouchEvent(event));

        if (isGroupChat) {
            if (getIntent().hasExtra(Constant.Chat.CONSULTATION_NAME)) {
                titleTextV.setText(getIntent().getStringExtra(Constant.Chat.CONSULTATION_NAME));
            } else if (mGroupInfoEntity != null) {
                mName = mGroupInfoEntity.getName();
                titleTextV.setText(mGroupInfoEntity.getName());
            }
            setRight(R.drawable.icon_more, v -> onGroupMenu());
        } else if (getIntent().hasExtra(MyConstant.SERVICE_CHAT)) {
            String name = getIntent().getStringExtra(MyConstant.SERVICE_CHAT_NAME);
            if (!HStringUtil.isEmpty(name)) {
                titleTextV.setText(name);
            } else {
                titleTextV.setText(getIntent().getStringExtra(MyConstant.SERVICE_CHAT));
            }
        } else {
            if (SmartFoxClient.helperId.equals(mChatId)) {
                mName = "系统通知";
                titleTextV.setText(mName);//系统通知,不显显示输入框
                titleRightBtn2.setVisibility(View.INVISIBLE);
                findViewById(R.id.input_control).setVisibility(View.GONE);
            } else {
                mName = getIntent().getStringExtra(Constant.Chat.SINGLE_NAME);
                if (!HStringUtil.isEmpty(mName)) {
                    titleTextV.setText(mName);
                }
            }
        }
        if (isGroupChat)
            findViewById(R.id.selector_panel_goods).setVisibility(View.GONE);
        registerForContextMenu(mListView);
        //if(!isGroupChat)isPayedUser();
        //从支付宝页面过来的
//		if(getIntent().hasExtra("pay_type")){
//			SingleBtnFragmentDialog.showDefault(getSupportFragmentManager(), "您可以咨询医生,如预约时间未到也可留言,离开此页面再次进入,请到\"我的六一健康-我的医生\"找该医生诊所");
//		}
        mChatInputControlFragment.setChatType(isGroupChat, mChatId, titleTextV.getText().toString(), consultType);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        mChatHandler.sendEmptyMessage(1000);
    }

    @Override
    protected void onStop() {
        if (mediaPlay.isPlaying()) {
            mediaPlay.stop();
        }
        onReleaseBg();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
        }
        if (mServiceConnection != null) {
            unbindService(mServiceConnection);
        }
        if (mLooper != null) {
            mLooper.quit();
        }
        if (mPopupWindow != null && mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
        }
        if (mOptionWindow != null && mOptionWindow.isShowing()) {
            mOptionWindow.dismiss();
            mOptionWindow = null;
        }
        super.onDestroy();
    }

    /**
     * 加载历史消息
     */
    private void initHistoryMsg() {
        if (mGroupType == 1) {
            onLoadMsgForHttp();//群聊
        } else {
            mAppData.messageCllection.remove(mChatId);
            if (ObjectType.SPECIAL_SERVER.equals(mObjectType) && SmartFoxClient.helperId.equals(mChatId)) {
                onLoadMsgForDB();
            } else if (ObjectType.SPECIAL_SERVER.equals(mObjectType)) {
                onLoadMsgFromServer();
            } else {
                onLoadMsgForDB();
            }
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        switch (v.getId()) {
            case R.id.chat_content:
                mCopyTxtV = (TextView) v;
                menu.add(0, 1, 0, "复制");
                menu.add(0, 2, 0, "重发此消息");
                break;
        }
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 1://复制
                if (mCopyTxtV != null)
                    SystemUtils.clipTxt(mCopyTxtV.getText(), this);
                return true;
            case 2:
                if (mCopyTxtV != null)
                    onSendTxtMsg(mCopyTxtV.getText().toString());
                break;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public void onContextMenuClosed(Menu menu) {
        mCopyTxtV = null;
        super.onContextMenuClosed(menu);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            boolean b = mChatInputControlFragment.isAllPanelGone();//隐藏未隐藏的面板
            if (!b) return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 绑定后台服务
     */
    private void bindService() {
        Intent intent = new Intent(this, CoreService.class);
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    /**
     * 注册通知
     */
    private void registerReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(CoreService.ACTION_MESSAGE);
        filter.addAction(CoreService.ACTION_PAY_MESSAGE);
        filter.addAction(CoreService.ACTION_OFFLINE_MESSAGE);
        filter.addAction(CoreService.MESSAGE_STATUS);
        if (SmartFoxClient.helperId.equals(mChatId))
            filter.addAction(CoreService.ACTION_GROUP_INVITE);//好友邀请
        registerReceiver(mReceiver, filter);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
//                if (isGroupChat && "1".equals(caseFlag)) {//三人聊天
//                mChatInputControlFragment.hideSoftBord();
//                intent = new Intent(this, AtyChatMembers.class);
//                intent.putExtra("mConsultationId", mChatId);
//                startActivity(intent);
////				onShowOptionMenu(v);
//            } else if (isGroupChat && "2".equals(caseFlag)) {//群聊天
//                mChatInputControlFragment.hideSoftBord();
//                intent = new Intent(this, GroupDataActivity.class);
//                intent.putExtra(GroupDataActivity.GROUPID, mGroupInfoEntity.getId());
//                startActivityForResult(intent, GROUP_INFO);
//            } else {
//                manageGerChat();//管理聊天
//            }
            case R.id.popup_menu1://相册获取
                mPopupWindow.dismiss();
                onChatBgPhotoClick();
                break;
            case R.id.popup_menu2://相机获取图片
                mPopupWindow.dismiss();
                onChatBgCameraClick();
                break;
            case R.id.popup_menu3://使用默认图片
                mPopupWindow.dismiss();
                SharePreHelper.clearChatBg(this, DoctorHelper.getId(), mChatId);
                onSetBg();
                break;
            case R.id.popup_menu_cancel://退出
                mPopupWindow.dismiss();
                break;
            case R.id.popup_menu4://关注的人
                mPopupWindow.dismiss();
                onInviteFriend(1);
                break;
            case R.id.popup_menu5://邀请多么号,昵称
//                mPopupWindow.dismiss();
//                intent = new Intent(this, InviteByNameActivity.class);
//                intent.putExtra("groupId", mChatId);
//                startActivity(intent);
                break;
            case R.id.popup_menu6://邀请条件
                mPopupWindow.dismiss();
                onInviteFriend(3);
                break;
            case R.id.popup_menu7://邀请身边的人
                mPopupWindow.dismiss();
                onInviteFriend(4);
                break;
//            case R.id.option_popmenu7://管理发言
//            case R.id.option_popmenu1://删除对话
//                manageGerChat();
//
//                break;
//            case R.id.option_popmenu2://聊天背景
//                mOptionWindow.dismiss();
//                onShowBgPopUpMenu();
//                break;
//            case R.id.option_popmenu3://服务内容
//                mOptionWindow.dismiss();
//                intent = new Intent(this, SettingWebUIActivity.class);
//                intent.putExtra("TextSize", 100);
//                intent.putExtra("url", "http://www.h-tlk.com/JumpPage/JumpPageServlet?Type=DoctorServiceIntroduce");
//                intent.putExtra("title", "服务内容");
//                startActivity(intent);
//                break;
//            case R.id.option_popmenu4://邀请加入
//                mOptionWindow.dismiss();
//                onInviteFriend(1);
////			onShowInvitePopUpMenu();
//                break;
//            case R.id.option_popmenu5://在线成员
//                mOptionWindow.dismiss();
//                onShowGroupListNumber();
//                break;
//            case R.id.option_popmenu6://禁止发言
////                mOptionWindow.dismiss();
////                intent = new Intent(this, ForbiddenWordsListActivity.class);
////                intent.putExtra("groupId", mChatId);
////                startActivity(intent);
//                break;
//            case R.id.option_popmenu8://只看群主
//                mOptionWindow.dismiss();
//                break;
//            case R.id.option_popmenu9://医生资料
//                mOptionWindow.dismiss();
//                intent = new Intent(this, PersonInfoActivity.class);
//                intent.putExtra("id", mCustomerInfoEntity.getId());
//                startActivity(intent);
//                break;
        /*case R.id.option_popmenu11://文件浏览
            mOptionWindow.dismiss();
			intent = new Intent(this,DocumentsChatActivity.class);
			intent.putExtra("groupId",mChatId);
			intent.putExtra("userId", DoctorHelper.getId());
			startActivityForResult(intent,-1);
			break;*/
        }
    }

    /**
     * 群聊菜单
     */
    private void onGroupMenu() {
        Intent intent;
        if (isGroupChat) {//群聊天
            mChatInputControlFragment.hideSoftBord();
            intent = new Intent(this, GroupDataActivity.class);
            if (mGroupInfoEntity == null) {
                intent.putExtra(GroupDataActivity.GROUPID, mChatId);
            } else {
                intent.putExtra(GroupDataActivity.GROUPID, mGroupInfoEntity.getId());
            }
            startActivityForResult(intent, Constant.Chat.GROUP_INFO);
        } else {
            manageGerChat();//管理聊天
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 3000://聊天背景设置相册获取
                if (resultCode == Activity.RESULT_OK) {
                    if (data != null) {
                        Uri uri = data.getData();
                        String scheme = uri.getScheme();
                        String strFilePath = null;//图片地址
                        // url类型content or file
                        if ("content".equals(scheme)) {
                            strFilePath = mChatInputControlFragment.getImageUrlByAlbum(uri);
                        } else {
                            strFilePath = uri.getPath();
                        }
                        onHandlerChatBg(strFilePath);
                    }
                }
                break;
            case 3001://聊天背景设置相机获取
                if (resultCode == Activity.RESULT_OK) {
                    String strFilePath = mChatBgFile.getAbsolutePath();
                    onHandlerChatBg(strFilePath);
                }
                mChatBgFile = null;
                break;
            case Constant.Chat.GROUP_INFO://
                if (resultCode == Activity.RESULT_OK) {
                    if (data != null) {
                        mName = data.getStringExtra("groupName");
                        if (!HStringUtil.isEmpty(mName)) {
                            titleTextV.setText(mName);
                        }
                    } else {
                        setResult(RESULT_OK);
                        finish();
                    }
                }
                break;
        }
    }


    @Override
    public void onSelectAll() {
        mChatAdapter.onSelectAll();
    }

    @Override
    public void onDeletAll() {
        List<MessageEntity> list = mChatAdapter.getList();
        if (list.isEmpty()) return;
        Message message = mChatHandler.obtainMessage();
        message.obj = new ArrayList<MessageEntity>(list);
        message.what = 1002;
        mChatHandler.sendMessage(message);
    }

    public void onDeleteSelect() {
        List<MessageEntity> list = mChatAdapter.onDeletSelectedMesg();
        if (list.isEmpty()) return;
        Message message = mChatHandler.obtainMessage();
        message.obj = list;
        message.what = 1003;
        mChatHandler.sendMessage(message);
    }


    /**
     * 播放错误
     */
    @Override
    public void onPlayError() {

    }

    //退出编辑模式
    @Override
    public void onDeleteCancel() {
        manageGerChat();
    }

    @Override
    public void onBackPressed() {
        EventManager.post(new MyEvent("refresh", 2));
        if (mGroupType == 0) {  //六一班单聊刷新
            Intent intent = new Intent();
            intent.putExtra(Constant.Chat.SINGLE_ID, mChatId);
            setResult(RESULT_OK, intent);
        } else if (mGroupType == 1) {
            //六一班群聊刷新
            Intent intent = new Intent();
            intent.putExtra(Constant.Chat.GROUP_ID, mChatId);
            intent.putExtra("groupName", mName);
            setResult(RESULT_OK, intent);
        }
        super.onBackPressed();
    }


    /**
     * 删除离线消息
     * list 消息集合
     * isAddList 是否将消息添加到adapter中
     */
    private void deleteOffLineMessage(final List<MessageEntity> list, final boolean isAddList) {
        runOnUiThread(new Runnable() {
            public void run() {
                /**
                 * DeleteLixian42
                 Type=deleteLixian42
                 customerId
                 sms_target_id
                 offid
                 删除离线
                 */
                MessageEntity messageEntity = list.get(list.size() - 1);
                if (mGroupType == 1) {//群聊
                    deleteOfflineMessage();
                } else {//单聊
                    deleteOfflineMessageSingle();
                    onDeleteMessageFromMessgae();
                }
//				ApiService.doHttpDeleteLeaveOnlineMessage(SmartFoxClient.getLoginUserId(), leavemessages.toString(), new RequestHttpSendMessage(leavemessages));
                if (list != null && list.size() > 0 && isAddList) {
                    mChatAdapter.addCollectionToEnd(list);
                    mListView.setSelection(mChatAdapter.getCount());
                } else if (!isAddList && list != null) {
                    if (ObjectType.SPECIAL_SERVER.equals(mObjectType)) {
                        pageNum = 1;
                        onLoadMsgFromServer();
                    } else {
                        onLoadMsgForDB();
                    }
                }
                mPullToRefreshListView.onRefreshComplete();
            }
        });
    }


    /**
     * 管理聊天历史
     */
    private void manageGerChat() {
        boolean isEditor = mChatAdapter.isEditor();
        if (isEditor) {
            mChatAdapter.onUnEditorMode();
        } else {
            mChatAdapter.onEditorMode();
        }
        mChatInputControlFragment.onChangeEditorMode(!isEditor);
        if (mOptionWindow != null) mOptionWindow.dismiss();
    }

    /**
     * 显示聊天大图片
     * @param str
     */
    public void onShowBigImage(String str) {
        ImageBrowserActivity.from(this)
                            .setImagePath(str)
                            .startActivity();
    }

    /**
     * 视频播放
     * @param str
     */
    public void onShowVideo(String str) {
//        Uri uri = Uri.parseDate(AppContext.getHttpUrls().URL_DOWNLOAVIDEO + pVideosList.get(index).getImagePath());

//        Uri uri = Uri.parseDate(str);
//        Intent intent = new Intent(Intent.ACTION_VIEW);
//        intent.setDataAndType(uri, "video/mp4");
//        startActivity(intent);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        String type = "video/* ";
        Uri uri = Uri.parse(str);
        intent.setDataAndType(uri, type);
        startActivity(intent);
    }

    /**
     * 语音播放
     * @param entity
     */
    public void onVoicePlay(MessageEntity entity) {
        if (StorageUtils.isSDMounted()) {
            mPlayingEntity = entity;
            String path = entity.getContent();
            if (path.contains("/")) {//远程路径
                String name = StorageUtils.getFileName(path);
                File file = new File(StorageUtils.getVoicePath(), name);
                if (file.exists()) {//文件不存在需要去下载
                    mediaPlay.play(entity);
                } else {
                    ApiService.doHttpDownChatFile(path, new VoiceFileHttpResponseHandler(entity));
                }
            } else {
                mediaPlay.play(entity);
            }
        }
    }

    /**
     * 处理聊天背景图片
     * @param path
     */
    private void onHandlerChatBg(String path) {
        int size = getDisplayMaxSlidSize();
        Bitmap bitmap = BitmapUtils.decodeBitmap(path, size, size);
        if (bitmap == null) return;
        File file = StorageUtils.createThemeFile();
        if (file != null) {
            StorageUtils.saveImageOnImagsDir(bitmap, file);
            SharePreHelper.saveChatBg(this, file.getAbsolutePath(), DoctorHelper.getId(), mChatId);
            mChatBgImageV.setBackgroundDrawable(new BitmapDrawable(bitmap));
        }
    }

    /**
     * 地图消息点击
     * @param messageEntity 消息实体
     */
    public void onLocationMsgClick(MessageEntity messageEntity) {
        try {
            Intent intent = new Intent(this, ChatMapActivity.class);
            String[] str = messageEntity.getContent().split("&");
            intent.putExtra("lo", str[0]);
            intent.putExtra("la", str[1]);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.e(e);
        }
    }

    /**
     * 设置背景图
     */
    @SuppressWarnings("ResourceType")
    private void onSetBg() {
        onReleaseBg();
        BitmapDrawable drawable = null;
        String path = SharePreHelper.fatchChatBg(this, DoctorHelper.getId(), mChatId);
        if (path == null) {//没有图片路径默认设置
            try {
                int resId = R.drawable.chat_bg1;
                InputStream inputStream = getResources().openRawResource(resId);
                drawable = new BitmapDrawable(inputStream);
                if (inputStream != null) inputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            File file = new File(path);
            if (file.exists()) {//文件存在
                try {
                    int size = getDisplayMaxSlidSize();
                    Bitmap bitmap = BitmapUtils.decodeBitmap(path, size, size);
                    if (bitmap == null) return;
                    drawable = new BitmapDrawable(bitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        if (drawable != null) {
            drawable.setCallback(null);
            mChatBgImageV.setBackgroundDrawable(drawable);
        }
    }

    /**
     * 释放背景图
     */
    private void onReleaseBg() {
        BitmapDrawable drawable = (BitmapDrawable) mChatBgImageV.getBackground();
        if (drawable == null) return;
        mChatBgImageV.setBackgroundDrawable(null);
        drawable.setCallback(null);
        if (!drawable.getBitmap().isRecycled()) {
            drawable.getBitmap().recycle();
        }
        drawable = null;
        System.gc();
    }

    /**
     * 获得屏幕分辨率最大的边
     */
    private int getDisplayMaxSlidSize() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return Math.max(metrics.heightPixels, metrics.widthPixels);
    }

    /**
     * 点击照片,相册获取
     */
    private void onChatBgPhotoClick() {
        Intent intent = CropUtils.createPickForFileIntent();
        startActivityForResult(intent, 3000);
    }

    /**
     * 点击相机
     */
    private void onChatBgCameraClick() {
        try {
            mChatBgFile = StorageUtils.createImageFile();
            Uri outUri = Uri.fromFile(mChatBgFile);
            Intent intent = CropUtils.createPickForCameraIntent(outUri);
            startActivityForResult(intent, 3001);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    /**
//     * 显示menu菜单
//     *
//     * @param view 目标View
//     */
//    private void onShowOptionMenu(View view) {
//        if (mOptionWindow != null && mOptionWindow.isShowing()) {
//            mOptionWindow.dismiss();
//            return;
//        }
//        if (mChatMenuV == null) {
//            final LayoutInflater inflater = getLayoutInflater();
//            if (isGroupChat) {//群聊菜单
//                mChatMenuV = inflater.inflate(R.layout.window_menu_chat_group_layout, null);
//                ViewGroup group = (ViewGroup) mChatMenuV.findViewById(R.id.contain);
//                boolean isGrouper = DoctorHelper.getId().equals(mGroupInfoEntity.getCreateCustomerID());//是否是群主
//                for (int i = 0; i < group.getChildCount(); i++) {
//                    group.getChildAt(i).setOnClickListener(this);
//                }
//                if (!isGrouper) {//非群主菜单
//                    group.findViewById(R.id.option_popmenu7).setVisibility(View.GONE);
//                    group.findViewById(R.id.option_popmenu6).setVisibility(View.GONE);//
//                    group.findViewById(R.id.option_popmenu8).setVisibility(View.GONE);//只看群主
//                }
//                //单聊菜单
//            } else {
//                mChatMenuV = inflater.inflate(R.layout.window_menu_chat_layout, null);
//                ViewGroup group = (ViewGroup) mChatMenuV.findViewById(R.id.contain);
//                for (int i = 0; i < group.getChildCount(); i++) {
//                    group.getChildAt(i).setOnClickListener(this);
//                }
//                boolean isDoctor = mCustomerInfoEntity.isShowDoctorV() || SmartFoxClient.getLoginUserInfo().isShowDoctorV();//是否是医生
////				group.findViewById(R.id.option_popmenu9).setVisibility(isDoctor ? View.VISIBLE : View.GONE);
//                group.findViewById(R.id.option_popmenu2).setVisibility(isDoctor ? View.GONE : View.VISIBLE);//聊天背景
////				group.findViewById(R.id.option_popmenu3).setVisibility(isDoctor ? View.VISIBLE : View.GONE);
//            }
//            if (mOptionWindow == null)
//                mOptionWindow = new PopupWindow(mChatMenuV, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);
//            mOptionWindow.setTouchable(true);
//            mOptionWindow.setBackgroundDrawable(new BitmapDrawable());
//            mOptionWindow.setOutsideTouchable(true);
//        }
//        mOptionWindow.setContentView(mChatMenuV);
//        mOptionWindow.showAsDropDown(view);
//    }

    View mChangeBgV;//选着背景图

    private void onShowBgPopUpMenu() {// 设置聊天背景图片
        onShowPopUpMenu(1);
    }

    View mInviteV;//好友邀请

    private void onShowInvitePopUpMenu() {
        onShowPopUpMenu(0);
    }

    private void onShowPopUpMenu(int type) {
        View contentView = null;
        final LayoutInflater inflater = getLayoutInflater();
        if (type == 0) {//好友邀请
            if (mInviteV == null) {
                mInviteV = inflater.inflate(R.layout.window_popup_invite_layout, null);
                ViewGroup group = (ViewGroup) mInviteV;
                for (int i = 0; i < group.getChildCount(); i++) {
                    group.getChildAt(i).setOnClickListener(this);
                }
            }
            contentView = mInviteV;
        }
        if (type == 1) {//背景图片
            if (mChangeBgV == null) {
                mChangeBgV = inflater.inflate(R.layout.window_popup_choosebg_layout, null);
                ViewGroup group = (ViewGroup) mChangeBgV;
                for (int i = 0; i < group.getChildCount(); i++) {
                    group.getChildAt(i).setOnClickListener(this);
                }
            }
            contentView = mChangeBgV;
        }
        if (mPopupWindow == null) {
            mPopupWindow = new PopupWindow(contentView, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, true);
            mPopupWindow.setTouchable(true);
            mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
            mPopupWindow.setOutsideTouchable(true);
            mPopupWindow.setAnimationStyle(R.style.AnimationPreview);
        }
        mPopupWindow.setContentView(contentView);
        mPopupWindow.showAtLocation(contentView, Gravity.BOTTOM, 0, 0);
    }

    /**
     * 查询在线成员
     */
    public void onShowGroupListNumber() {
//        Intent intent = new Intent(this, TopicMemberInfoUI.class);
//        intent.putExtra("groupId", mChatId);
////		intent.putExtra("type",1000);
//        intent.putExtra("type", 0);//0表示成员信息
//        startActivityForResult(intent, -1);
    }

    /**
     * 邀请好友
     * @param type
     */
    private void onInviteFriend(int type) {
//        Intent intent;
//        if (type == 3) {
//            intent = new Intent(this, FriendSearchAboutFriendActivity.class);
//            intent.putExtra("type", type);
//        } else {
//            intent = new Intent(this, InviteMyAttentionMainUI.class);//邀请我关注的人
//        }
//        intent.putExtra("groupId", mChatId);
//        startActivity(intent);
    }

    /**
     * 消息处理
     * @author zhao
     */
    private class ChatHandler extends Handler {
        public ChatHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(final Message msg) {//异步线程
            switch (msg.what) {
//                case 999://删除群聊离线消息
//                    deleteOfflineMessage();
//                    break;
                case 1000://获取的新消息
                    final List<MessageEntity> list = onLoadMesgForCache();
                    if (list == null || list.size() == 0) return;
                    deleteOffLineMessage(list, true);
                    break;
                case 1100://获取特殊服务删除离线
                    onDeleteMessageFromMessgae();
                    break;
                case 9999://缓存消息(不需要加载到adapter中去)
                    final List<MessageEntity> lists = onLoadMesgForCache();
                    if (lists == null || lists.size() == 0) {
                        deleteOfflineMessageSingle();
                        return;
                    }
                    deleteOffLineMessage(lists, false);

                    break;
                case 1001://db获取消息
                    final List<MessageEntity> entities = mDbUserHelper.queryChatMessageByAfterId(DoctorHelper.getId(), mChatId, isGroupChat, (String) msg.obj, "10");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (entities != null && entities.size() > 0) {
                                mChatAdapter.addCollectionToTop(entities);
                            }
                            mPullToRefreshListView.onRefreshComplete();
                        }
                    });
                    break;
                case 1002://全部删除
                    if (isGroupChat) {
                        //群聊删除
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ApiService.deleteGroupMessages(mChatId, 1, null, new AsyncHttpResponseHandler(ChatActivity.this) {
                                    @Override
                                    public void onSuccess(int statusCode, String content) {
                                        if ("0".equals(content)) {
                                        } else {
                                            mChatAdapter.onDeleteAll();
                                        }
                                    }
                                });
                            }
                        });
                    } else {
                        //单聊删除
                        mDbUserHelper.deleteAllMessageByChatId(mChatId, DoctorHelper.getId());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                ApiService.deleteCustomPersonMessages(mChatId, 1, null, new AsyncHttpResponseHandler(ChatActivity.this) {
                                    @Override
                                    public void onSuccess(int statusCode, String content) {
                                        if (!"0".equals(content)) {
                                            mChatAdapter.onDeleteAll();
                                        }
                                    }
                                });
                            }
                        });
                    }
                    break;
                case 1003://选中删除
                    final List<MessageEntity> messageEntities = (List<MessageEntity>) msg.obj;
                    final StringBuffer messageBuffer = new StringBuffer();
                    for (MessageEntity messageEntity : messageEntities) {
                        String id = messageEntity.getId();
                        if (id == null) continue;
                        messageBuffer.append(id);
                        messageBuffer.append(",");
                    }
                    if (isGroupChat) {
                        //群聊删除
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ApiService.deleteGroupMessages(mChatId, 0, messageBuffer.toString(), new AsyncHttpResponseHandler(ChatActivity.this) {
                                    @Override
                                    public void onSuccess(int statusCode, String content) {
                                        mChatAdapter.onDeleteSelected(messageEntities);
                                    }
                                });
                            }
                        });
                    } else {
                        //单聊删除
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mChatAdapter.onDeleteSelected(messageEntities);
                                ApiService.deleteCustomPersonMessages(mChatId, 0, messageBuffer.toString(), new AsyncHttpResponseHandler(ChatActivity.this) {
                                    @Override
                                    public void onSuccess(int statusCode, String content) {
                                        if (!"0".equals(content)) {
                                            mChatAdapter.onDeleteSelected(messageEntities);
                                        }
                                    }
                                });
                            }
                        });
                    }
                    break;
                case 1005://收费框弹出
//				final String note = (String)msg.obj;
//				runOnUiThread(new Runnable() {
//					@Override
//					public void run() {
//						onShowPayDialog(note);
//					}
//				});
                    break;
                case 1006://发送一些转发的消息
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (getIntent().hasExtra(Constant.Chat.KEY_CONTENT) && mPushService != null) {
                                String content = getIntent().getStringExtra(Constant.Chat.KEY_CONTENT);
                                if (!TextUtils.isEmpty(content)) onSendTxtMsg(content);
                                getIntent().removeExtra(Constant.Chat.KEY_CONTENT);
                            }
                        }
                    });
                    break;
                case 1007:
                    final Bundle data = msg.getData();
                    final String note = data.getString("note");
//				final String note = (String)msg.obj;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mChatAdapter.onChangMesge(data.getString("mesgId"));
                            onShowPayDialog(note);
                        }
                    });
                    break;
            }
        }
    }

    /**
     * 从缓存中加载消息
     */
    private List<MessageEntity> onLoadMesgForCache() {
        ConcurrentHashMap<String, List<MessageEntity>> messageCllection = mAppData.messageCllection;
        if (messageCllection != null) {
            final List<MessageEntity> list = messageCllection.get(mChatId);
            messageCllection.remove(mChatId);
            return list;
        }
        return null;
    }

    /**
     * 从单聊中查询
     */
    private void onLoadMsgForDB() {
        int size = mChatAdapter.getCount();
        String serverid = null;//获取最大的id
        //判断小壹加载消息
        if (mChatId.equals(SmartFoxClient.helperId)) {
            serverid = mChatAdapter.getHelperMesgId();
        } else if (size == 0) {
            serverid = String.valueOf(Long.MAX_VALUE);
        } else {
            serverid = mChatAdapter.getFirstMesgId();
        }

        List<BasicNameValuePair> pairs = new ArrayList<>();
        pairs.add(new BasicNameValuePair("customerId", DoctorHelper.getId()));
        pairs.add(new BasicNameValuePair("sms_target_id", mChatId));
        pairs.add(new BasicNameValuePair("offline_id", serverid));
        pairs.add(new BasicNameValuePair("Object_Type", mObjectType));
        pairs.add(new BasicNameValuePair("consultationId", mConsultationId));
        ApiService.doGetTalkHistoryServlet(pairs, new ApiCallback<String>() {

            @Override
            public void onBefore(okhttp3.Request request) {
                super.onBefore(request);
                if (mPullToRefreshListView != null && !mPullToRefreshListView.isRefreshing())
                    mPullToRefreshListView.setRefreshing();
            }

            @Override
            public void onAfter() {
                mPullToRefreshListView.onRefreshComplete();
                super.onAfter();
            }

            @Override
            public void onError(okhttp3.Request request, Exception e) {

            }

            @Override
            public void onResponse(String response) {
                List<MessageEntity> list = DataParseUtil.parseGroupMessage(response, DoctorHelper.getId());
                List<MessageEntity> caches = new ArrayList<MessageEntity>();
                if (mChatAdapter.getCount() != 0 && AppData.DYHSID.equals(mChatId)) {
                    int size = list.size();
                    for (int i = 0; i < size; i++) {
                        if ("0".equals(list.get(i).getServerId())) ;
                        caches.add(list.get(i));
                    }
                }
                list.removeAll(caches);
                if (list.size() > 0) {
                    mChatAdapter.addCollectionToTopOutP(list);
                    if (mChatAdapter.getCount() != 0) {
                        MessageEntity messageEntity = mChatAdapter.getList().get(mChatAdapter.getList().size() - 1);
                        onDeleteMessageFromMessgae();
                    }
                }
            }
        }, this);
    }

    private int pageNum = 1;//分页页数

    /**
     * 从特殊服务单聊中查询历史记录http
     */
    private void onLoadMsgFromServer() {
        List<BasicNameValuePair> pairs = new ArrayList<>();
        pairs.add(new BasicNameValuePair("op", "queryOrderMsg"));
        pairs.add(new BasicNameValuePair("friend_id", mChatId));
        pairs.add(new BasicNameValuePair("customer_id", DoctorHelper.getId()));
        pairs.add(new BasicNameValuePair("num", "20"));
        pairs.add(new BasicNameValuePair("page", pageNum + ""));
        ApiService.doGetTalkHistoryServletS(pairs, new ApiCallback<String>() {

            @Override
            public void onError(Request request, Exception e) {

            }

            @Override
            public void onBefore(Request request) {
                super.onBefore(request);
                if (mPullToRefreshListView != null && !mPullToRefreshListView.isRefreshing())
                    mPullToRefreshListView.setRefreshing();
            }

            @Override
            public void onAfter() {
                mPullToRefreshListView.onRefreshComplete();
                super.onAfter();
            }

            @Override
            public void onResponse(String response) {
                try {
                    JSONObject obj = new JSONObject(response);
                    if ("1".equals(obj.optString("code"))) {
                        String content = obj.optString("result");
                        final List<MessageEntity> list = DataParseUtil.parseGroupMessage(content, DoctorHelper.getId());
                        if (list != null) {
                            if (pageNum == 1)//第一次加载
                            {
                                mChatAdapter.removeAll();
                                onDeleteMessageFromMessgae();
                            }
                            if (list.size() != 0) {//加载出了数据
                                mChatAdapter.addCollectionToTop(list);

                                mListView.post(new Runnable() {
                                    @Override
                                    public void run() {
//                                        mListView.smoothScrollToPosition(currentP + list.size());
                                        mListView.setSelection(currentP + 1 + list.size());
                                    }
                                });
                                currentP = mListView.getSelectedItemPosition();
                            } else {
                                if (pageNum != 1) {
                                    ToastUtil.showShort("没有更多了");
                                }
                            }
                            pageNum++;

                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, this);

    }

    private int currentP = 0;
    /**
     * 加载历史消息
     */
    private final OnRefreshListener2<ListView> mOnRefreshListener2 = new OnRefreshListener2<ListView>() {
        public void onPullDownToRefresh(org.handmark.pulltorefresh.library.PullToRefreshBase<ListView> refreshView) {
            currentP = mListView.getSelectedItemPosition();
            if (mGroupType == 1) {
                onLoadMsgForHttp();
            } else {
                mAppData.messageCllection.remove(mChatId);
                if (ObjectType.SPECIAL_SERVER.equals(mObjectType) && SmartFoxClient.helperId.equals(mChatId)) {
                    onLoadMsgForDB();
                } else if (ObjectType.SPECIAL_SERVER.equals(mObjectType)) {
                    onLoadMsgFromServer();
                } else {
                    onLoadMsgForDB();
                }
            }
        }

        public void onPullUpToRefresh(org.handmark.pulltorefresh.library.PullToRefreshBase<ListView> refreshView) {

        }
    };

    /**
     * 删除离线
     */
    public void onDeleteMessageFromMessgae() {
        String delId = TextUtils.isEmpty(mConsultationId) ? mOrderId : mConsultationId;
        ApiService.OkHttpChatDelMsg(DoctorHelper.getId(), delId, mChatId, mObjectType, new ApiCallbackWrapper<ResponseBean>() {
            @Override
            public void onResponse(ResponseBean response) {
                super.onResponse(response);
                if (response.isSuccess()) {
                    EChatClearHide event = new EChatClearHide();
                    event.id = delId;
                    EventManager.post(event);
                }
            }
        });
    }

//    /**
//     * 删除离线
//     *
//     * @param messageEntity
//     */
//    public void onDeleteMessageFromMessgae(MessageEntity messageEntity) {
//        String id = "";
//        List<BasicNameValuePair> pairs = new ArrayList<>();
//        pairs.add(new BasicNameValuePair("customerId", DoctorHelper.getId()));
//        pairs.add(new BasicNameValuePair("Type", "deleteLixian42"));
//        if (!HStringUtil.isEmpty(mConsultationId)) {
//            id = mConsultationId;
//        } else {
//            id = mOrderId;
//        }
//        pairs.add(new BasicNameValuePair("mConsultationId", id));
//        pairs.add(new BasicNameValuePair("Object_Type", mObjectType));
////        pairs.add(new BasicNameValuePair("offid", messageEntity.getServerId()));
//        pairs.add(new BasicNameValuePair("offid", Long.MAX_VALUE + ""));
//        pairs.add(new BasicNameValuePair("sms_target_id", mChatId));
//        LogUtil.d(TAG, "删除离线消息");
//        ApiService.doGetDeleteLixianServlet(pairs, new ApiCallback<String>() {
//
//            @Override
//            public void onError(Request request, Exception e) {
//
//            }
//
//            @Override
//            public void onResponse(String response) {
//                List<String> msgsId = new ArrayList<String>();
//                try {
//                    org.json.JSONObject object = new org.json.JSONObject(response);
//                    if (1 == object.optInt("error_code", -1)) {
////					for (int i = 0; i < arrays.length(); i++) {
////						msgsId.add(arrays.get(i).toString());
////					}
//                    } else {
//                        LogUtil.e("=======CHAT", "聊天 发送删除离线消息失败");
//                    }
//                } catch (Exception e) {
//                    LogUtil.e("=======CHAT", "聊天 发送删除离线消息失败");
//                }
//
//            }
//        }, this);
//
//    }

    /**
     * 删除群聊离线
     */
    public void deleteOfflineMessage() {
        Map<String, String> map = new HashMap<>();
        map.put("customer_id", DoctorHelper.getId());
        map.put("op", "deleteOffLineMsgRecord");
        map.put("group_id", mChatId);
        map.put("isgroup", "1");//1删除群离线消息,0删除单聊离线消息
        ApiService.OKHttpGetFriends(map, new ApiCallback<String>() {

            @Override
            public void onError(Request request, Exception e) {

            }

            @Override
            public void onResponse(String response) {
                try {
                    JSONObject object = new JSONObject(response);
                    if (HttpResult.SUCCESS.equals(object.optString("code"))) {
                        EChatClearHide event = new EChatClearHide();
                        event.id = mOrderId;
                        EventManager.post(event);
                    } else {
                        Logger.e(TAG, "聊天 发送删除离线消息失败");
                    }
                } catch (Exception e) {
                    Logger.e(TAG, "聊天 发送删除离线消息失败" + e.toString());
                }
            }
        }, this);
    }

    /**
     * 删除未读单聊提示
     */
    public void deleteOfflineMessageSingle() {
        ApiService.deleteUnreadMessageWarn(DoctorHelper.getId(), mChatId, new ApiCallbackWrapper<ResponseBean>() {
            @Override
            public void onResponse(ResponseBean resp) {
                super.onResponse(resp);
                if (resp.isSuccess()){
                    EChatClearHide event = new EChatClearHide();
                    event.id = mOrderId;
                    EventManager.post(event);
                }else{
                    LogUtils.e("未读消息删除失败");
                }
            }
        });
    }

    /**
     * 语音文件下载
     * @author zhao
     */
    protected class VoiceFileHttpResponseHandler extends BinaryHttpResponseHandler {
        final MessageEntity mEntity;

        public VoiceFileHttpResponseHandler(MessageEntity entity) {
            mEntity = entity;
        }

        @Override
        public boolean onProcess(byte[] bytes) throws IOException {
            if (bytes == null || bytes.length == 0) throw new IOException("Save file fail");
            String fileName = StorageUtils.getFileName(mEntity.getContent());
            File file = StorageUtils.createVoiceFile(fileName);
            if (file == null) throw new IOException("Save file fail");
            FileOutputStream fileOutputStream = null;
            try {
                fileOutputStream = new FileOutputStream(file);
                fileOutputStream.write(bytes);
            } finally {
                if (fileOutputStream != null) {
                    fileOutputStream.flush();
                    fileOutputStream.close();
                }
            }
            return false;
        }

        @Override
        public void onSuccess(int statusCode, byte[] binaryData) {
            super.onSuccess(statusCode, binaryData);
            if (mPlayingEntity == mEntity) {//当前下载的等于需要播放的
                mPlayingEntity = null;
                mediaPlay.play(mEntity);
            }
        }
    }


    /**
     * 群历史消息加载
     * /DuoMeiHealth/HZPushManagementServlet?groupid=&serverid=&pagenum=&TYPE=queryLixianLISHIJILU
     */
    private void onLoadMsgForHttp() {
        int size = mChatAdapter.getCount();
        String serverid = null;//获取最大的id
        if (size == 0) {
            serverid = String.valueOf(Long.MAX_VALUE);
        } else {
            serverid = mChatAdapter.getFirstMesgId();
        }

        List<BasicNameValuePair> pairs = new ArrayList<>();
        pairs.add(new BasicNameValuePair("groupid", mChatId));
        pairs.add(new BasicNameValuePair("TYPE", "queryLixianLISHIJILU"));
        pairs.add(new BasicNameValuePair("serverid", serverid));
        pairs.add(new BasicNameValuePair("Object_Type", mObjectType));
        pairs.add(new BasicNameValuePair("pagenum", "10"));
        ApiService.doGetHZPushManagementServlet(pairs, new ApiCallback<String>() {

            @Override
            public void onError(Request request, Exception e) {

            }

            @Override
            public void onBefore(Request request) {
                super.onBefore(request);
                if (mPullToRefreshListView != null && !mPullToRefreshListView.isRefreshing())
                    mPullToRefreshListView.setRefreshing();
            }

            @Override
            public void onAfter() {
                mPullToRefreshListView.onRefreshComplete();
                super.onAfter();
            }

            @Override
            public void onResponse(String response) {
                List<MessageEntity> list = DataParseUtil.parseGroupMessage(response, DoctorHelper.getId());
                List<MessageEntity> caches = new ArrayList<MessageEntity>();
                if (mChatAdapter.getCount() != 0 && AppData.DYHSID.equals(mChatId)) {
                    int size = list.size();
                    for (int i = 0; i < size; i++) {
                        if ("0".equals(list.get(i).getServerId())) ;
                        caches.add(list.get(i));
                    }
                }
                list.removeAll(caches);
                if (list.size() > 0) {
                    mChatAdapter.addCollectionToTop(list);
                    if (mChatAdapter.getCount() != 0) {
                        MessageEntity messageEntity = mChatAdapter.getList().get(mChatAdapter.getList().size() - 1);
                        onDeleteMessageFromMessgae();
                        deleteOfflineMessage();
                    }
                }
            }
        }, this);
    }

    private void saveMessage(MessageEntity messageEntity) {//保存消息到db
//		if(!isGroupChat){
//			mDbUserHelper.insertChatMessage(messageEntity,false);
//		}
    }

    public final void onUpdateSelectedNumber(int size) {
        mChatInputControlFragment.onUpdateSelectedNumber(size);
    }

    /**
     * 弹出支付窗口
     */
    private void onShowPayDialog(String note) {
        DoubleBtnFragmentDialog.showDefault(getSupportFragmentManager(), note, "知道了", "现在去购买", new OnDilaogClickListener() {
            @Override
            public void onDismiss(DialogFragment fragment) {
                fragment.dismissAllowingStateLoss();
            }

            @Override
            public void onClick(DialogFragment fragment, View v) {
                toPay();
            }
        });
    }

    /**
     * 去购买医生服务
     */
    public void toPay() {
        String name = getIntent().getComponent().getClassName();
        if (name.equals(DoctorClinicMainActivity.class.getName())) {
            onBackPressed();
        } else {
            Intent intent = new Intent(this, DoctorClinicMainActivity.class);
            intent.putExtra("id", mChatId);
            startActivity(intent);
        }
    }

    @Override
    public void onSendGoodsMsg(String content) {
        MessageEntity messageEntity = new MessageEntity();
        messageEntity.setId(String.valueOf(System.currentTimeMillis()));//时间id
        messageEntity.setSenderId(DoctorHelper.getId());
        messageEntity.setReceiverId(mChatId);
        messageEntity.setType(MessageEntity.TYPE_TEXT);
        messageEntity.setSendFlag(true);
        messageEntity.setContent(content);
        messageEntity.setConsultationId(mConsultationId);
        messageEntity.setSendState(MessageEntity.STATE_PROCESING);
        if (!HStringUtil.isEmpty(mConsultationId)) {
            messageEntity.setOrderId(mConsultationId);
        } else {
            messageEntity.setOrderId(mOrderId);
        }

        if (isGroupChat) {
            if (null != mGroupInfoEntity) {
                messageEntity.setIsBL(mGroupInfoEntity.getIsBL());
            }
        }
        saveMessage(messageEntity);
        mChatAdapter.addNew(messageEntity);
        mListView.setSelection(mChatAdapter.getCount());
//        //图文群聊
//        if (ObjectType.TUWEN.equals(mObjectType)){
//            mPushService.onSendChatMessage(messageEntity, 3, MessageEntity.TYPE_TEXT);
//        }else {
        mPushService.onSendChatMessage(messageEntity, mGroupType, MessageEntity.TYPE_TEXT);
//        }

    }

    @Override
    public void onSendTxtMsg(String content) {
        MessageEntity messageEntity = new MessageEntity();
        messageEntity.setId(String.valueOf(System.currentTimeMillis()));//时间id
        messageEntity.setSenderId(DoctorHelper.getId());
        messageEntity.setReceiverId(mChatId);
        messageEntity.setType(MessageEntity.TYPE_TEXT);
        messageEntity.setSendFlag(true);
        messageEntity.setContent(content);
        messageEntity.setConsultationId(mConsultationId);
        messageEntity.setSendState(MessageEntity.STATE_PROCESING);
        if (!HStringUtil.isEmpty(mConsultationId)) {
            messageEntity.setOrderId(mConsultationId);
        } else {
            messageEntity.setOrderId(mOrderId);
        }
        if (isGroupChat) {
            if (null != mGroupInfoEntity) {
                messageEntity.setIsBL(mGroupInfoEntity.getIsBL());
            }
        }
        saveMessage(messageEntity);
        mChatAdapter.addNew(messageEntity);
        mListView.setSelection(mChatAdapter.getCount());

//        //图文群聊
//        if (ObjectType.TUWEN.equals(mObjectType)){
//            mPushService.onSendChatMessage(messageEntity, 3, MessageEntity.TYPE_TEXT);
//        }else {
        mPushService.onSendChatMessage(messageEntity, mGroupType, MessageEntity.TYPE_TEXT);
//        }
    }

    @Override
    public void onSendLocationMsg(String longitude, String latitude, String address) {
        MessageEntity messageEntity = new MessageEntity();
        messageEntity.setSenderId(DoctorHelper.getId());
        messageEntity.setReceiverId(mChatId);
        messageEntity.setId(String.valueOf(System.currentTimeMillis()));
        messageEntity.setType(MessageEntity.TYPE_LOCATION);
        messageEntity.setSendState(MessageEntity.STATE_PROCESING);
        messageEntity.setSendFlag(true);
        messageEntity.setConsultationId(mConsultationId);
        messageEntity.setContent(latitude + "&" + longitude);
        messageEntity.setAddress(address);
        if (!HStringUtil.isEmpty(mConsultationId)) {
            messageEntity.setOrderId(mConsultationId);
        } else {
            messageEntity.setOrderId(mOrderId);
        }
        if (isGroupChat) {
            if (null != mGroupInfoEntity) {
                messageEntity.setIsBL(mGroupInfoEntity.getIsBL());
            }
        }
        saveMessage(messageEntity);
        mChatAdapter.addNew(messageEntity);
        mPushService.onSendChatMessage(messageEntity, mGroupType, MessageEntity.TYPE_LOCATION);
        mListView.setSelection(mChatAdapter.getCount());
    }

    @Override
    public void onSendImageMsg(String path) {
        MessageEntity messageEntity = new MessageEntity();
        messageEntity.setId(String.valueOf(System.currentTimeMillis()));
        messageEntity.setSenderId(DoctorHelper.getId());
        messageEntity.setReceiverId(mChatId);
        messageEntity.setType(MessageEntity.TYPE_PICTURE);
        messageEntity.setSendState(MessageEntity.STATE_PROCESING);
        messageEntity.setSendFlag(true);
        messageEntity.setConsultationId(mConsultationId);
        messageEntity.setContent(path);
        if (!HStringUtil.isEmpty(mConsultationId)) {
            messageEntity.setOrderId(mConsultationId);
        } else {
            messageEntity.setOrderId(mOrderId);
        }
        if (isGroupChat) {
            if (null != mGroupInfoEntity) {
                messageEntity.setIsBL(mGroupInfoEntity.getIsBL());
            }
        }
        saveMessage(messageEntity);
        mChatAdapter.addNew(messageEntity);
//        //图文群聊
//        if (ObjectType.TUWEN.equals(mObjectType)){
//            mPushService.onSendChatMessage(messageEntity, 3, MessageEntity.TYPE_PICTURE);
//        }else {
        mPushService.onSendChatMessage(messageEntity, mGroupType, MessageEntity.TYPE_PICTURE);
//        }
        mListView.setSelection(mChatAdapter.getCount());
    }

    @Override
    public void onSendVideoMsg(String video) {
        MessageEntity messageEntity = new MessageEntity();
        messageEntity.setId(String.valueOf(System.currentTimeMillis()));
        messageEntity.setSenderId(DoctorHelper.getId());
        messageEntity.setReceiverId(mChatId);
        messageEntity.setType(MessageEntity.TYPE_VIDEO);
        messageEntity.setSendState(MessageEntity.STATE_PROCESING);
        messageEntity.setSendFlag(true);
        messageEntity.setConsultationId(mConsultationId);
        messageEntity.setContent(video);
        if (!HStringUtil.isEmpty(mConsultationId)) {
            messageEntity.setOrderId(mConsultationId);
        } else {
            messageEntity.setOrderId(mOrderId);
        }
        if (isGroupChat) {
            if (null != mGroupInfoEntity) {
                messageEntity.setIsBL(mGroupInfoEntity.getIsBL());
            }
        }
        saveMessage(messageEntity);
        mChatAdapter.addNew(messageEntity);
//        //图文群聊
//        if (ObjectType.TUWEN.equals(mObjectType)){
//            mPushService.onSendChatMessage(messageEntity, 3, MessageEntity.TYPE_VIDEO);
//        }else {
        mPushService.onSendChatMessage(messageEntity, mGroupType, MessageEntity.TYPE_VIDEO);
//        }
        mListView.setSelection(mChatAdapter.getCount());
    }

    /**
     * 发送语音
     */
    @Override
    public void onSendVoiceMsg(String path, String timeStr, int time) {
        MessageEntity entity = new MessageEntity();
        int resultTime = (int) Math.floor(time / 1000);
        float floatTime = resultTime;
        entity.setVoiceLength(floatTime + "");
        entity.setSendFlag(true);
        String name = new File(path).getName();
        entity.setContent(name);
        entity.setSenderId(DoctorHelper.getId());
        entity.setReceiverId(mChatId);
        entity.setConsultationId(mConsultationId);
        entity.setSendState(MessageEntity.STATE_PROCESING);
        entity.setType(MessageEntity.TYPE_VOICE);
        if (!HStringUtil.isEmpty(mConsultationId)) {
            entity.setOrderId(mConsultationId);
        } else {
            entity.setOrderId(mOrderId);
        }
        if (isGroupChat) {
            if (null != mGroupInfoEntity) {
                entity.setIsBL(mGroupInfoEntity.getIsBL());
            }
        }
        saveMessage(entity);
        mChatAdapter.addNew(entity);

//        //图文群聊
//        if (ObjectType.TUWEN.equals(mObjectType)){
//            mPushService.onSendChatMessage(entity, 3, MessageEntity.TYPE_VOICE);
//        }else {
        mPushService.onSendChatMessage(entity, mGroupType, MessageEntity.TYPE_VOICE);
//        }

        mListView.setSelection(mChatAdapter.getCount());
    }


    //list view长按事件
    @SuppressWarnings("deprecation")
    final GestureDetector mGestureDetector = new GestureDetector(new ChatGestureListener() {
        public void onLongPress(MotionEvent e) {
            boolean isEditor = mChatAdapter.isEditor();
            if (isEditor) {
                mChatAdapter.onUnEditorMode();
            } else {
                mChatAdapter.onEditorMode();
            }
            mChatInputControlFragment.onChangeEditorMode(!isEditor);
        }

        ;
    });

    //后台服务绑定
    final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            CoreServiceBinder binder = (CoreServiceBinder) service;
            mPushService = binder.getService();
            mChatHandler.sendEmptyMessage(1006);//发送一些转发的消息
        }
    };

    //注册通知
    final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (isFinishing()) return;
            String action = intent.getAction();
            if (action.equals(CoreService.ACTION_MESSAGE)) {//新消息
                String senderId = intent.getStringExtra("senderId");//发送者id
                if (mChatId.equals(senderId)) {//更新到界面上
                    if (SmartFoxClient.helperId.equals(senderId)) {
                        mChatHandler.sendEmptyMessage(1100);
                    } else {
                        mChatHandler.sendEmptyMessage(1000);
                    }
                    //弹出收费框
                }
            } else if (CoreService.ACTION_OFFLINE_MESSAGE.equals(action)) {
                mChatHandler.sendEmptyMessage(1000);
            } else if (CoreService.ACTION_PAY_MESSAGE.equals(action)) {
                String mesgId = intent.getStringExtra(CoreService.PARAME_KEY);//消息id
                String note = intent.getStringExtra("tickNote");
                String senderId = intent.getStringExtra("senderId");//发送者id
                Bundle bundle = new Bundle();
                bundle.putString("note", note);
                bundle.putString("mesgId", mesgId);
                Message message = mChatHandler.obtainMessage();
                message.setData(bundle);
                message.what = 1007;
                mChatHandler.sendMessage(message);
            } else if (CoreService.MESSAGE_STATUS.equals(action)) {

            }
        }
    };

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mGroupInfoEntity != null) outState.putParcelable("group_entity", mGroupInfoEntity);
//        if (mCustomerInfoEntity != null)
//            outState.putSerializable("user_entity", mCustomerInfoEntity);
        if (!HStringUtil.isEmpty(mOrderId)) outState.putString(Constant.Chat.ORDER_ID, mOrderId);
    }

    /**
     * 医生集团接单
     */
    public void getServiceOrder() {
        Map<String, String> map = new HashMap<>();
        map.put("op", "updateWorkSiteOrderStatus");
        map.put("doctor_id", DoctorHelper.getId());
        map.put("order_id", mOrderId);
        map.put("status", Constant.StationOrderStatus.QDSUCESS);
        ApiService.OKHttpStationCommonUrl(map, new ApiCallbackWrapper<JSONObject>(this) {
            @Override
            public void onResponse(JSONObject response) {
                super.onResponse(response);
                if (response != null) {
                    ToastUtil.showShort(response.optString("message"));
//                    makeSendDate(response);
                }
            }
        }, this);
    }

    /**
     * 去支付setGoSee+order_type+site_id++doctor_id+price
     */
    public void GoManageApply(String content) {
        String[] keys = content.split("&");
        if (content.contains("&") && keys.length > 1) {
            Intent intent = new Intent(this, InviteMemActivity.class);
            intent.putExtra(Constant.Station.STATION_ID, keys[1]);
            startActivity(intent);
        } else {
            ToastUtil.showShort("数据异常");
        }
    }

    /**
     * 同意图文邀请
     */
    public void agreeInvited(int code, String content) {
        String[] keys = content.split("&");
        if (content.contains("&") && keys.length > 1) {
            if (17 == code) {
                makeTuwen(Constant.AcceptType.YES, keys[2]);
            } else {
                makeTuwen(Constant.AcceptType.NO, keys[2]);
            }

        } else {
            ToastUtil.showShort("数据异常");
        }
    }

    /**
     * 去处理邀请
     */
    public void GoManageOrder(String content) {
        String[] keys = content.split("&");
        if (content.contains("&") && keys.length > 1) {
            Intent intent = new Intent(this, AtyOrderDetails.class);
            intent.putExtra("CONID", Integer.valueOf(keys[1]));
            startActivity(intent);
        } else {
            ToastUtil.showShort("数据异常");
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(GoodsEvent event) {
        if (event.code == 1) {//推荐商品
            ToastUtil.showShort(event.what);
            onSendGoodsMsg(event.what);
        }
    }

    private boolean isCalling = false;

    /**
     * 拨打电话
     */
    public void sendCall(String mOrderId) {
        if (HStringUtil.isEmpty(mOrderId)) {
            ToastUtil.showShort("数据异常");
            return;
        }
        if (isCalling) {
            ToastUtil.showShort("拨打电话中，请稍后。。。");
            return;
        }
        Map<String, String> map = new HashMap<>();
        map.put("op", "call");
        map.put("order_id", mOrderId);
        ApiService.OKHttpConInvited(map, new ApiCallbackWrapper<JSONObject>() {
            @Override
            public void onBefore(Request request) {
                super.onBefore(request);
                isCalling = true;
                SingleBtnFragmentDialog.showSinglebtn(ChatActivity.this, "拨打电话中，请稍后。。。", "知道了", new SingleBtnFragmentDialog.OnClickSureBtnListener() {
                    @Override
                    public void onClickSureHander() {

                    }
                }).show();
            }

            @Override
            public void onError(Request request, Exception e) {
                super.onError(request, e);
                isCalling = false;
            }

            @Override
            public void onResponse(JSONObject response) {
                super.onResponse(response);
                if (response != null) {
                    isCalling = false;
//                    ToastUtil.showShort(response.optString("message"));
//                    makeSendDate(response);
                }
            }
        }, this);
    }

    /**
     * 医生集团接单
     */
    public void sendVideo() {
        final Intent intent = getPackageManager().getLaunchIntentForPackage("com.moor.cc");
        if (intent != null) {
            DoubleBtnFragmentDialog.showDefault(getSupportFragmentManager(), "打开视频软件", "取消", "确定",
                    new OnDilaogClickListener() {
                        @Override
                        public void onDismiss(DialogFragment fragment) {
                        }

                        @Override
                        public void onClick(DialogFragment fragment, View v) {
                            startActivity(intent);
                        }
                    });

        } else {
            DoubleBtnFragmentDialog.showDefault(getSupportFragmentManager(), "您还没有该视频app，需要下载安装吗", "取消", "下载",
                    new OnDilaogClickListener() {
                        @Override
                        public void onDismiss(DialogFragment fragment) {
                        }

                        @Override
                        public void onClick(DialogFragment fragment, View v) {
                            long sdcardSize = SystemUtils.getAvailableExternalMemorySize();
                            if (sdcardSize > 25 * 1024 * 1024) {
                                ActivityHelper.downLoadApp(AppContext.getApplication(), getResources().getString(R.string.video_app_url));
                            } else {
                                SingleBtnFragmentDialog.showDefault(getSupportFragmentManager(), getResources().getString(R.string.sdcard_not_enough));
                            }
                        }
                    });
        }
    }

    /**
     * 是否接受图文邀请
     */
    public void makeTuwen(String status, String group_id) {
        Map<String, String> map = new HashMap<>();
        map.put("op", "updateInviteStatus");
        map.put("customer_id", DoctorHelper.getId());
        map.put("group_id", group_id);
        map.put("status", status);
        ApiService.OKHttpStationCommonUrl(map, new ApiCallbackWrapper<JSONObject>(this) {
            @Override
            public void onResponse(JSONObject response) {
                super.onResponse(response);
                if (response != null) {
                    ToastUtil.showShort(response.optString("message"));
                }
            }
        }, this);
    }
}

