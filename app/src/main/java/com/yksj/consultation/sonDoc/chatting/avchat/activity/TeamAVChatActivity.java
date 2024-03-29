package com.yksj.consultation.sonDoc.chatting.avchat.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.ResponseCode;
import com.netease.nimlib.sdk.StatusCode;
import com.netease.nimlib.sdk.auth.AuthServiceObserver;
import com.netease.nimlib.sdk.avchat.AVChatCallback;
import com.netease.nimlib.sdk.avchat.AVChatManager;
import com.netease.nimlib.sdk.avchat.AVChatStateObserver;
import com.netease.nimlib.sdk.avchat.constant.AVChatControlCommand;
import com.netease.nimlib.sdk.avchat.constant.AVChatType;
import com.netease.nimlib.sdk.avchat.constant.AVChatUserRole;
import com.netease.nimlib.sdk.avchat.constant.AVChatVideoCropRatio;
import com.netease.nimlib.sdk.avchat.constant.AVChatVideoScalingType;
import com.netease.nimlib.sdk.avchat.model.AVChatCameraCapturer;
import com.netease.nimlib.sdk.avchat.model.AVChatControlEvent;
import com.netease.nimlib.sdk.avchat.model.AVChatData;
import com.netease.nimlib.sdk.avchat.model.AVChatParameters;
import com.netease.nimlib.sdk.avchat.model.AVChatSurfaceViewRenderer;
import com.netease.nimlib.sdk.avchat.model.AVChatVideoCapturer;
import com.netease.nimlib.sdk.avchat.model.AVChatVideoCapturerFactory;
import com.yksj.consultation.adapter.TeamAVChatAdapter;
import com.yksj.consultation.comm.DoubleBtnFragmentDialog;
import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.sonDoc.chatting.avchat.AVChatSoundPlayer;
import com.yksj.consultation.sonDoc.chatting.avchat.UI;
import com.yksj.consultation.sonDoc.chatting.avchat.cache.DemoCache;
import com.yksj.consultation.sonDoc.chatting.avchat.cache.TeamDataCache;
import com.yksj.consultation.sonDoc.chatting.avchat.module.input.robot.BaseFetchLoadAdapter;
import com.yksj.consultation.sonDoc.chatting.avchat.permission.annotation.MPermission;
import com.yksj.consultation.sonDoc.chatting.avchat.permission.annotation.OnMPermissionDenied;
import com.yksj.consultation.sonDoc.chatting.avchat.permission.annotation.OnMPermissionGranted;
import com.yksj.consultation.sonDoc.chatting.avchat.permission.annotation.OnMPermissionNeverAskAgain;
import com.yksj.consultation.sonDoc.chatting.avchat.team.SimpleAVChatStateObserver;
import com.yksj.consultation.sonDoc.chatting.avchat.team.SpacingDecoration;
import com.yksj.consultation.sonDoc.chatting.avchat.team.TeamAVChatHelper;
import com.yksj.consultation.sonDoc.chatting.avchat.team.TeamAVChatItem;
import com.yksj.consultation.sonDoc.chatting.avchat.team.TeamAVChatNotification;
import com.yksj.consultation.sonDoc.chatting.avchat.team.TeamAVChatVoiceMuteDialog;
import com.yksj.consultation.sonDoc.chatting.avchat.utils.ScreenUtil;
import com.yksj.consultation.business.LoginBusiness;
import com.yksj.healthtalk.utils.LogUtil;
import com.yksj.healthtalk.utils.ThreadManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import static com.yksj.consultation.sonDoc.chatting.avchat.team.TeamAVChatItem.TYPE.TYPE_DATA;


/**
 * 多人音视频界面：包含音视频通话界面和接受拒绝界面
 * Created by huangjun on 2017/5/3.
 * <p>互动直播/多人会议视频通话流程示例
 * <ol>
 * <li>主播或者管理员创建房间 {@link AVChatManager#createRoom(String, String, AVChatCallback)}。 创建房间仅仅是在服务器预留一个房间名，房间未使用时有效期为30天，使用后的房间在所有用户退出后回收。</li>
 * <li>注册音视频模块监听 {@link AVChatManager#observeAVChatState(AVChatStateObserver, boolean)}。</li>
 * <li>开启音视频引擎， {@link AVChatManager#enableRtc()}。 </li>
 * <li>设置互动直播模式，设置互动直播推流地址 [仅限互动直播] {@link AVChatParameters#KEY_SESSION_LIVE_MODE}, {@link AVChatParameters#KEY_SESSION_LIVE_URL}。</li>
 * <li>打开视频模块 {@link AVChatManager#enableVideo()}。</li>
 * <li>设置本地预览画布 {@link AVChatManager#setupLocalVideoRender(IVideoRender, boolean, int)} 。</li>
 * <li>设置视频通话可选参数[可以不设置] {@link AVChatManager#setParameter(AVChatParameters.Key, Object)}, {@link AVChatManager#setParameters(AVChatParameters)}。</li>
 * <li>创建并设置本地视频预览源 {@link AVChatVideoCapturerFactory#createCameraCapturer()}, {@link AVChatManager#setupVideoCapturer(AVChatVideoCapturer)}</li>
 * <li>打开本地视频预览 {@link AVChatManager#startVideoPreview()}。</li>
 * <li>加入房间 {@link AVChatManager#joinRoom2(String, AVChatType, AVChatCallback)}。</li>
 * <li>开始多人会议或者互动直播，以及各种音视频操作。</li>
 * <li>关闭本地预览 {@link AVChatManager#stopVideoPreview()} 。</li>
 * <li>关闭本地预览 {@link AVChatManager#disableVideo()} ()} 。</li>
 * <li>离开会话 {@link AVChatManager#leaveRoom2(String, AVChatCallback)}。</li>
 * <li>关闭音视频引擎, {@link AVChatManager#disableRtc()}。</li>
 * </ol></p>
 */

public class TeamAVChatActivity extends UI {
    // CONST
    private static final String TAG = "TeamAVChat";
    private static final String KEY_RECEIVED_CALL = "call";
    private static final String KEY_TEAM_ID = "teamid";
    private static final String KEY_ROOM_ID = "roomid";
    private static final String KEY_ACCOUNTS = "accounts";
    private static final String KEY_TNAME = "teamName";
    private static final int AUTO_REJECT_CALL_TIMEOUT = 45 * 1000;
    private static final int CHECK_RECEIVED_CALL_TIMEOUT = 45 * 1000;
    private static final int MAX_SUPPORT_ROOM_USERS_COUNT = 9;
    private static final int BASIC_PERMISSION_REQUEST_CODE = 0x100;
    // DATA
    private String teamId;
    private String roomId;
    private long chatId;
    private ArrayList<String> accounts;
    private boolean receivedCall;
    private boolean destroyRTC;
    private String teamName;
    // CONTEXT
    private Handler mainHandler;

    // LAYOUT
    private View callLayout;
    private View surfaceLayout;

    // VIEW
    private RecyclerView recyclerView;
    private TeamAVChatAdapter adapter;
    private List<TeamAVChatItem> data;
    private View voiceMuteButton;

    // TIMER
    private Timer timer;
    private int seconds;
    private TextView timerText;
    private Runnable autoRejectTask;

    // CONTROL STATE
    boolean videoMute = false;
    boolean microphoneMute = false;
    boolean speakerMode = true;

    // AVCAHT OBSERVER
    private AVChatStateObserver stateObserver;
    private Observer<AVChatControlEvent> notificationObserver;
    private AVChatCameraCapturer mVideoCapturer;
    private static boolean needFinish = true;

    private TeamAVChatNotification notifier;

    public static void startActivity(Context context, boolean receivedCall, String teamId, String roomId, ArrayList<String> accounts, String teamName) {
        needFinish = false;
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.setClass(context, TeamAVChatActivity.class);
        intent.putExtra(KEY_RECEIVED_CALL, receivedCall);
        intent.putExtra(KEY_ROOM_ID, roomId);
        intent.putExtra(KEY_TEAM_ID, teamId);
        intent.putExtra(KEY_ACCOUNTS, accounts);
        intent.putExtra(KEY_TNAME, teamName);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (needFinish) {
            finish();
            return;
        }
        LogUtil.i(TAG, "TeamAVChatActivity onCreate, savedInstanceState=" + savedInstanceState);
        dismissKeyguard();
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.team_avchat_activity);
        onInit();
        onIntent();
        initNotification();
        findLayouts();
        showViews();
        setChatting(true);
        NIMClient.getService(AuthServiceObserver.class).observeOnlineStatus(userStatusObserver, true);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 取消通知栏
        activeCallingNotifier(false);
        // 禁止自动锁屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    protected void onStop() {
        super.onStop();
        activeCallingNotifier(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtil.i(TAG, "TeamAVChatActivity onDestroy");

        needFinish = true;
        if (timer != null) {
            timer.cancel();
        }

        if (stateObserver != null) {
            AVChatManager.getInstance().observeAVChatState(stateObserver, false);
        }

        if (notificationObserver != null) {
            AVChatManager.getInstance().observeControlNotification(notificationObserver, false);
        }
        if (mainHandler != null) {
            mainHandler.removeCallbacksAndMessages(null);
        }
        hangup(); // 页面销毁的时候要保证离开房间，rtc释放。
        activeCallingNotifier(false);
        setChatting(false);
        NIMClient.getService(AuthServiceObserver.class).observeOnlineStatus(userStatusObserver, false);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        LogUtil.i(TAG, "TeamAVChatActivity onSaveInstanceState");
    }

    /**
     * ************************************ 初始化 ***************************************
     */


    private void onInit() {
        mainHandler = new Handler(this.getMainLooper());
    }

    private void onIntent() {
        Intent intent = getIntent();
        receivedCall = intent.getBooleanExtra(KEY_RECEIVED_CALL, false);
        roomId = intent.getStringExtra(KEY_ROOM_ID);
        teamId = intent.getStringExtra(KEY_TEAM_ID);
        accounts = (ArrayList<String>) intent.getSerializableExtra(KEY_ACCOUNTS);
        teamName = intent.getStringExtra(KEY_TNAME);
        LogUtil.i(TAG, "onIntent, roomId=" + roomId + ", teamId=" + teamId
                + ", receivedCall=" + receivedCall + ", accounts=" + accounts.size() + ", teamName = " + teamName);
    }


    private void onPermissionChecked() {
        startRtc(); // 启动音视频
    }

    /**
     * ************************************ 音视频事件 ***************************************
     */

    private void startRtc() {
        // rtc initialize
        AVChatManager.getInstance().enableRtc();
        AVChatManager.getInstance().enableVideo();
        LogUtil.i(TAG, "startRecorder rtc done");

        mVideoCapturer = AVChatVideoCapturerFactory.createCameraCapturer();
        AVChatManager.getInstance().setupVideoCapturer(mVideoCapturer);

        // state observer
        if (stateObserver != null) {
            AVChatManager.getInstance().observeAVChatState(stateObserver, false);
        }
        stateObserver = new SimpleAVChatStateObserver() {
            @Override
            public void onJoinedChannel(int code, String audioFile, String videoFile, int i) {
                if (code == 200) {
                    onJoinRoomSuccess();
                } else {
                    onJoinRoomFailed(code, null);
                }
            }

            @Override
            public void onUserJoined(String account) {
                onAVChatUserJoined(account);
            }

            @Override
            public void onUserLeave(String account, int event) {
                onAVChatUserLeave(account);
            }

            @Override
            public void onReportSpeaker(Map<String, Integer> speakers, int mixedEnergy) {
                onAudioVolume(speakers);
            }
        };
        AVChatManager.getInstance().observeAVChatState(stateObserver, true);
        LogUtil.i(TAG, "observe rtc state done");

        // notification observer
        if (notificationObserver != null) {
            AVChatManager.getInstance().observeControlNotification(notificationObserver, false);
        }
        notificationObserver = new Observer<AVChatControlEvent>() {

            @Override
            public void onEvent(AVChatControlEvent event) {
                final String account = event.getAccount();
                if (AVChatControlCommand.NOTIFY_VIDEO_ON == event.getControlCommand()) {
                    onVideoLive(account);
                } else if (AVChatControlCommand.NOTIFY_VIDEO_OFF == event.getControlCommand()) {
                    onVideoLiveEnd(account);
                }
            }
        };
        AVChatManager.getInstance().observeControlNotification(notificationObserver, true);

        // join
        AVChatManager.getInstance().setParameter(AVChatParameters.KEY_SESSION_MULTI_MODE_USER_ROLE, AVChatUserRole.NORMAL);
        AVChatManager.getInstance().setParameter(AVChatParameters.KEY_AUDIO_REPORT_SPEAKER, true);
        AVChatManager.getInstance().setParameter(AVChatParameters.KEY_VIDEO_FIXED_CROP_RATIO, AVChatVideoCropRatio.CROP_RATIO_1_1);
        AVChatManager.getInstance().joinRoom2(roomId, AVChatType.VIDEO, new AVChatCallback<AVChatData>() {
            @Override
            public void onSuccess(AVChatData data) {
                chatId = data.getChatId();
                LogUtil.i(TAG, "join room success, roomId=" + roomId + ", chatId=" + chatId);
            }

            @Override
            public void onFailed(int code) {
                onJoinRoomFailed(code, null);
                LogUtil.i(TAG, "join room failed, code=" + code + ", roomId=" + roomId);
            }

            @Override
            public void onException(Throwable exception) {
                onJoinRoomFailed(-1, exception);
                LogUtil.i(TAG, "join room failed, e=" + exception.getMessage() + ", roomId=" + roomId);
            }
        });
        LogUtil.i(TAG, "startRecorder join room, roomId=" + roomId);
    }

    private void onJoinRoomSuccess() {
        startTimer();
        startLocalPreview();
        startTimerForCheckReceivedCall();
        LogUtil.i(TAG, "team avchat running..." + ", roomId=" + roomId);
    }

    private void onJoinRoomFailed(int code, Throwable e) {
        if (code == ResponseCode.RES_ENONEXIST) {
            showToast(getString(R.string.t_avchat_join_fail_not_exist));
        } else {
            showToast("join room failed, code=" + code + ", e=" + (e == null ? "" : e.getMessage()));
        }
    }

    public void onAVChatUserJoined(String account) {
        final int index = getItemIndex(account);
        if (index >= 0) {
            TeamAVChatItem item = data.get(index);
            AVChatSurfaceViewRenderer surfaceView = adapter.getViewHolderSurfaceView(item);
            if (surfaceView != null) {
                item.state = TeamAVChatItem.STATE.STATE_PLAYING;
                item.videoLive = true;
                adapter.notifyItemChanged(index);
                AVChatManager.getInstance().setupRemoteVideoRender(account, surfaceView, false, AVChatVideoScalingType.SCALE_ASPECT_FIT);
            }
            ThreadManager.getInstance().createLongPool().execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyItemChanged(index);
                        }
                    });
                }
            });
        }
        updateAudioMuteButtonState();

        LogUtil.i(TAG, "on user joined, account=" + account);
    }


    public void onAVChatUserLeave(String account) {
        int index = getItemIndex(account);
        if (index >= 0) {
            TeamAVChatItem item = data.get(index);
            item.state = TeamAVChatItem.STATE.STATE_HANGUP;
            item.volume = 0;
            adapter.notifyItemChanged(index);
        }
        updateAudioMuteButtonState();
        if (makeLeave()){
            showDialog();
        }
        LogUtil.i(TAG, "on user leave, account=" + account);
    }

    private boolean makeLeave() {
        int m = 0;
        for (TeamAVChatItem i : data) {
            if (i.state == TeamAVChatItem.STATE.STATE_PLAYING) {
                m++;
            }
        }
        return m < 2;
    }
    /**
     * 提示挂断
     */
    private void showDialog() {
        DoubleBtnFragmentDialog.showDefault(getSupportFragmentManager(), "您确定要挂断吗？", "取消", "确定",
                new DoubleBtnFragmentDialog.OnDilaogClickListener() {
                    @Override
                    public void onDismiss(DialogFragment fragment) {
//											}
                    }

                    @Override
                    public void onClick(DialogFragment fragment, View v) {
                        // 挂断
                        hangup();
                        finish();
                    }
                });
    }

    private void startLocalPreview() {
        if (data.size() > 1 && data.get(0).account.equals(DemoCache.getAccount())) {
            AVChatSurfaceViewRenderer surfaceView = adapter.getViewHolderSurfaceView(data.get(0));
            if (surfaceView != null) {
                AVChatManager.getInstance().setupLocalVideoRender(surfaceView, false, AVChatVideoScalingType.SCALE_ASPECT_FIT);
                AVChatManager.getInstance().startVideoPreview();
                data.get(0).state = TeamAVChatItem.STATE.STATE_PLAYING;
                data.get(0).videoLive = true;
                adapter.notifyItemChanged(0);
            }
            ThreadManager.getInstance().createLongPool().execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyItemChanged(0);
                        }
                    });
                }
            });
        }
        // 确认数据源,自己放在首位
//        selfItem = new TeamAVChatItem(TYPE_DATA, teamId, DemoCache.getAccount());
//        selfItem.state = TeamAVChatItem.STATE.STATE_PLAYING; // 自己直接采集摄像头画面
//        AVChatSurfaceViewRenderer surfaceView = (AVChatSurfaceViewRenderer) surfaceLayout.findViewById(R.id.recycler_view_main);
//        AVChatManager.getInstance().setupLocalVideoRender(surfaceView, false, AVChatVideoScalingType.SCALE_ASPECT_FIT);
//        AVChatManager.getInstance().startVideoPreview();
//        selfItem.state = TeamAVChatItem.STATE.STATE_PLAYING;
//        selfItem.videoLive = true;
//        adapter.notifyItemChanged(0);

//        data.add(0, selfItem);


    }

    /**
     * ************************************ 音视频状态 ***************************************
     */

    private void onVideoLive(String account) {
        if (account.equals(DemoCache.getAccount())) {
            return;
        }

        notifyVideoLiveChanged(account, true);
    }

    private void onVideoLiveEnd(String account) {
        if (account.equals(DemoCache.getAccount())) {
            return;
        }

        notifyVideoLiveChanged(account, false);
    }

    private void notifyVideoLiveChanged(String account, boolean live) {
        int index = getItemIndex(account);
        if (index >= 0) {
            TeamAVChatItem item = data.get(index);
            item.videoLive = live;
            adapter.notifyItemChanged(index);
        }
    }

    private void onAudioVolume(Map<String, Integer> speakers) {
        for (TeamAVChatItem item : data) {
            if (speakers.containsKey(item.account)) {
                item.volume = speakers.get(item.account);
                adapter.updateVolumeBar(item);
            }
        }
    }

    private void updateSelfItemVideoState(boolean live) {
        int index = getItemIndex(DemoCache.getAccount());
        if (index >= 0) {
            TeamAVChatItem item = data.get(index);
            item.videoLive = live;
            adapter.notifyItemChanged(index);
        }
    }


    /**
     * ************************************ 定时任务 ***************************************
     */

    private void startTimer() {
        timer = new Timer();
        timer.schedule(timerTask, 0, 1000);
        timerText.setText("00:00");
    }

    private TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            seconds++;
            int m = seconds / 60;
            int s = seconds % 60;
            final String time = String.format(Locale.CHINA, "%02d:%02d", m, s);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    timerText.setText(time);
                }
            });
        }
    };

    private void startTimerForCheckReceivedCall() {
        mainHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                int index = 0;
                for (TeamAVChatItem item : data) {
                    if (item.type == TYPE_DATA && item.state == TeamAVChatItem.STATE.STATE_WAITING) {
                        item.state = TeamAVChatItem.STATE.STATE_END;
                        adapter.notifyItemChanged(index);
                    }
                    index++;
                }
                checkAllHangUp();
            }
        }, CHECK_RECEIVED_CALL_TIMEOUT);
    }

    private void startAutoRejectTask() {
        if (autoRejectTask == null) {
            autoRejectTask = new Runnable() {
                @Override
                public void run() {
                    AVChatSoundPlayer.instance().stop();
                    finish();
                }
            };
        }

        mainHandler.postDelayed(autoRejectTask, AUTO_REJECT_CALL_TIMEOUT);
    }

    private void cancelAutoRejectTask() {
        if (autoRejectTask != null) {
            mainHandler.removeCallbacks(autoRejectTask);
        }
    }

    /*
     * 除了所有人都没接通，其他情况不做自动挂断
     */
    private void checkAllHangUp() {
        for (TeamAVChatItem item : data) {
            if (item.account != null &&
                    !item.account.equals(DemoCache.getAccount()) &&
                    item.state != TeamAVChatItem.STATE.STATE_END) {
                return;
            }
        }
        mainHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                hangup();
                finish();
            }
        }, 200);
    }

    /**
     * 通知栏
     */
    private void activeCallingNotifier(boolean active) {
        if (notifier != null) {
            if (destroyRTC) {
                notifier.activeCallingNotification(false);
            } else {
                notifier.activeCallingNotification(active);
            }
        }
    }

    /**
     * ************************************ 点击事件 ***************************************
     */

    private View.OnClickListener settingBtnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.avchat_switch_camera:
                    // 切换前后摄像头
                    mVideoCapturer.switchCamera();
                    break;
                case R.id.avchat_enable_video:
                    // 视频
                    AVChatManager.getInstance().muteLocalVideo(videoMute = !videoMute);
                    // 发送控制指令
                    byte command = videoMute ? AVChatControlCommand.NOTIFY_VIDEO_OFF : AVChatControlCommand.NOTIFY_VIDEO_ON;
                    AVChatManager.getInstance().sendControlCommand(chatId, command, null);
                    v.setBackgroundResource(videoMute ? R.drawable.t_avchat_camera_mute_selector : R.drawable.t_avchat_camera_selector);
                    updateSelfItemVideoState(!videoMute);
                    break;
                case R.id.avchat_enable_audio:
                    // 麦克风开关
                    AVChatManager.getInstance().muteLocalAudio(microphoneMute = !microphoneMute);
                    v.setBackgroundResource(microphoneMute ? R.drawable.t_avchat_microphone_mute_selector : R.drawable.t_avchat_microphone_selector);
                    break;
                case R.id.avchat_volume:
                    // 听筒扬声器切换
                    AVChatManager.getInstance().setSpeaker(speakerMode = !speakerMode);
                    v.setBackgroundResource(speakerMode ? R.drawable.t_avchat_speaker_selector : R.drawable.t_avchat_speaker_mute_selector);
                    break;
                case R.id.avchat_shield_user:
                    // 屏蔽用户音频
                    disableUserAudio();
                    break;
                case R.id.hangup:
                    // 挂断
                    hangup();
                    finish();
                    break;
            }
        }
    };

    private void updateAudioMuteButtonState() {
        boolean enable = false;
        for (TeamAVChatItem item : data) {
            if (item.state == TeamAVChatItem.STATE.STATE_PLAYING &&
                    !DemoCache.getAccount().equals(item.account)) {
                enable = true;
                break;
            }
        }
        voiceMuteButton.setEnabled(enable);
        voiceMuteButton.invalidate();
    }

    private void disableUserAudio() {

        try {
            List<Pair<String, Boolean>> voiceMutes = new ArrayList<>();
            for (TeamAVChatItem item : data) {
                if (item.state == TeamAVChatItem.STATE.STATE_PLAYING &&
                        !DemoCache.getAccount().equals(item.account)) {
                    voiceMutes.add(new Pair<>(item.account, AVChatManager.getInstance().isRemoteAudioMuted(item.account)));
                }
            }
            TeamAVChatVoiceMuteDialog dialog = new TeamAVChatVoiceMuteDialog(this, teamId, voiceMutes);
            dialog.setTeamVoiceMuteListener(new TeamAVChatVoiceMuteDialog.TeamVoiceMuteListener() {
                @Override
                public void onVoiceMuteChange(List<Pair<String, Boolean>> voiceMuteAccounts) {
                    if (voiceMuteAccounts != null) {
                        for (Pair<String, Boolean> voiceMuteAccount : voiceMuteAccounts) {
                            AVChatManager.getInstance().muteRemoteAudio(voiceMuteAccount.first, voiceMuteAccount.second);
                        }
                    }
                }
            });
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        // 屏蔽BACK
    }

    /**
     * ************************************ 数据源 ***************************************
     */
    TeamAVChatItem selfItem = null;


    /**
     * RecyclerView 移动到当前位置，
     *
     * @param manager 设置RecyclerView对应的manager
     * @param n       要跳转的位置
     */
    public static void MoveToPosition(LinearLayoutManager manager, int n) {
        manager.scrollToPositionWithOffset(n, 0);
        manager.setStackFromEnd(true);
    }

    private int getItemIndex(final String account) {
        int index = 0;
        boolean find = false;
        for (TeamAVChatItem i : data) {
            if (i.account.equals(account)) {
                find = true;
                break;
            }
            index++;
        }

        return find ? index : -1;
    }

    private int getItemIndex2(final String account) {
        int index = 0;
        boolean find = false;
        for (TeamAVChatItem i : data) {
            if (i.account.equals(account)) {
                find = true;
                break;
            }
            index++;
        }

        return find ? index : -1;
    }


    /**
     * ************************************ helper ***************************************
     */

    private void showToast(String content) {
        Toast.makeText(TeamAVChatActivity.this, content, Toast.LENGTH_SHORT).show();
    }


    /**
     * ************************************ 主流程 ***************************************
     */

    private void showViews() {
        if (receivedCall) {
            showReceivedCallLayout();
        } else {
            showSurfaceLayout();
        }
    }

    /*
         * 接听界面
         */
    private void showReceivedCallLayout() {
        callLayout.setVisibility(View.VISIBLE);
        // 提示
        TextView textView = (TextView) callLayout.findViewById(R.id.received_call_tip);
        if (teamName == null) {
            teamName = TeamDataCache.getInstance().getTeamName(teamId);
        }
        textView.setText(teamName + " 的视频通话");

        // 播放铃声
        AVChatSoundPlayer.instance().play(AVChatSoundPlayer.RingerTypeEnum.RING);

        // 拒绝
        callLayout.findViewById(R.id.refuse).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AVChatSoundPlayer.instance().stop();
                cancelAutoRejectTask();
                finish();
            }
        });

        // 接听
        callLayout.findViewById(R.id.receive).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AVChatSoundPlayer.instance().stop();
                cancelAutoRejectTask();
                callLayout.setVisibility(View.GONE);
                showSurfaceLayout();
            }
        });

        startAutoRejectTask();
    }


    /*
         * 通话界面
         */
    private void showSurfaceLayout() {
        // 列表
        surfaceLayout.setVisibility(View.VISIBLE);
        recyclerView = (RecyclerView) surfaceLayout.findViewById(R.id.recycler_view);
        initRecyclerView();

        // 通话计时
        timerText = (TextView) surfaceLayout.findViewById(R.id.timer_text);

        // 控制按钮
        ViewGroup settingLayout = (ViewGroup) surfaceLayout.findViewById(R.id.avchat_setting_layout);
        for (int i = 0; i < settingLayout.getChildCount(); i++) {
            View v = settingLayout.getChildAt(i);
            if (v instanceof RelativeLayout) {
                ViewGroup vp = (ViewGroup) v;
                if (vp.getChildCount() == 1) {
                    vp.getChildAt(0).setOnClickListener(settingBtnClickListener);
                }
            }
        }

        // 音视频权限检查
        checkPermission();
    }

    /**
     * ************************************ 权限检查 ***************************************
     */

    private void checkPermission() {
        List<String> lackPermissions = AVChatManager.getInstance().checkPermission(TeamAVChatActivity.this);
        if (lackPermissions.isEmpty()) {
            onBasicPermissionSuccess();
        } else {
            String[] permissions = new String[lackPermissions.size()];
            for (int i = 0; i < lackPermissions.size(); i++) {
                permissions[i] = lackPermissions.get(i);
            }
            MPermission.with(TeamAVChatActivity.this)
                    .setRequestCode(BASIC_PERMISSION_REQUEST_CODE)
                    .permissions(permissions)
                    .request();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        MPermission.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

    @OnMPermissionGranted(BASIC_PERMISSION_REQUEST_CODE)
    public void onBasicPermissionSuccess() {
        onPermissionChecked();
    }

    @OnMPermissionDenied(BASIC_PERMISSION_REQUEST_CODE)
    @OnMPermissionNeverAskAgain(BASIC_PERMISSION_REQUEST_CODE)
    public void onBasicPermissionFailed() {
        Toast.makeText(this, "音视频通话所需权限未全部授权，部分功能可能无法正常运行！", Toast.LENGTH_SHORT).show();
        onPermissionChecked();
    }

    private TeamAVChatItem mainItem = null;

    private void initRecyclerView() {
        // 确认数据源,自己放在首位
        data = new ArrayList<>(accounts.size() + 1);
        for (String account : accounts) {
            if (account.equals(DemoCache.getAccount())) {
                continue;
            }

            data.add(new TeamAVChatItem(TYPE_DATA, teamId, account));
        }


//        // 确认数据源,自己放在首位
        selfItem = new TeamAVChatItem(TYPE_DATA, teamId, DemoCache.getAccount());
        selfItem.state = TeamAVChatItem.STATE.STATE_PLAYING; // 自己直接采集摄像头画面
        data.add(0, selfItem);
//        // 补充占位符
//        int holderLength = MAX_SUPPORT_ROOM_USERS_COUNT - data.size();
//        for (int i = 0; i < holderLength; i++) {
//            data.add(new TeamAVChatItem(teamId));
//        }
        // RecyclerView
        adapter = new TeamAVChatAdapter(recyclerView, data, 0);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerView.addItemDecoration(new SpacingDecoration(ScreenUtil.dip2px(1), ScreenUtil.dip2px(1), true));
        adapter.setOnItemClickListener(new BaseFetchLoadAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, final int position) {
                if (adapter.getData().get(position).state == TeamAVChatItem.STATE.STATE_PLAYING)
                    changeView(view, position);
            }
        });
    }


    AVChatSurfaceViewRenderer surfaceView = null;

    private void findLayouts() {
        callLayout = findView(R.id.team_avchat_call_layout);
        surfaceLayout = findView(R.id.team_avchat_surface_layout);
        voiceMuteButton = findView(R.id.avchat_shield_user);
    }

    /*
        * 设置通话状态
        */
    private void setChatting(boolean isChatting) {
        TeamAVChatHelper.sharedInstance().setTeamAVChatting(isChatting);
    }

    private void initNotification() {
        notifier = new TeamAVChatNotification(this);
        notifier.init(teamId);
    }

    // 设置窗口flag，亮屏并且解锁/覆盖在锁屏界面上
    private void dismissKeyguard() {
        getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                        WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                        WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
        );
    }

    /**
     * 在线状态观察者
     */
    private Observer<StatusCode> userStatusObserver = new Observer<StatusCode>() {

        @Override
        public void onEvent(StatusCode code) {
            if (code.wontAutoLogin()) {
                AVChatSoundPlayer.instance().stop();
                hangup();
                finish();
            }
        }
    };

    private void hangup() {
        if (destroyRTC) {
            return;
        }

        try {
            AVChatManager.getInstance().stopVideoPreview();
            AVChatManager.getInstance().leaveRoom2(roomId, null);
            AVChatManager.getInstance().disableRtc();
        } catch (Exception e) {
            e.printStackTrace();
        }

        destroyRTC = true;
        LogUtil.i(TAG, "destroy rtc & leave room, roomId=" + roomId);
    }

    private void changeView(View view, final int position) {
        try {
            selfItem = new TeamAVChatItem(TYPE_DATA, teamId, DemoCache.getAccount());
            selfItem.state = TeamAVChatItem.STATE.STATE_PLAYING; // 自己直接采集摄像头画面
            if (LoginBusiness.getInstance().getLoginEntity() == null) return;
            String myCount = LoginBusiness.getInstance().getLoginEntity().getSixOneAccount();
            String clickCount = adapter.getData().get(position).account;

            if (myCount.equals(clickCount) && mainItem == null) {//首次选中自己的摄像视频
                mainItem = selfItem;
                surfaceView = (AVChatSurfaceViewRenderer) surfaceLayout.findViewById(R.id.recycler_view_main);
                AVChatManager.getInstance().setupLocalVideoRender(surfaceView, false, AVChatVideoScalingType.SCALE_ASPECT_FIT);
                AVChatManager.getInstance().startVideoPreview();
            } else if (myCount.equals(clickCount) && mainItem != null && !mainItem.account.equals(myCount)) {
                AVChatSurfaceViewRenderer surfaceView2 = adapter.getViewHolderSurfaceView(mainItem);
                if (surfaceView2 != null) {
                    AVChatManager.getInstance().setupRemoteVideoRender(mainItem.account, surfaceView2, false, AVChatVideoScalingType.SCALE_ASPECT_FIT);
                }
                surfaceView = (AVChatSurfaceViewRenderer) surfaceLayout.findViewById(R.id.recycler_view_main);
                if (surfaceView != null) {
                    AVChatManager.getInstance().setupLocalVideoRender(surfaceView, false, AVChatVideoScalingType.SCALE_ASPECT_FIT);
                    AVChatManager.getInstance().startVideoPreview();
                }
                mainItem = selfItem;
            } else if (!myCount.equals(clickCount) && mainItem == null) {//首次选中不是自己的摄像视频
                mainItem = adapter.getData().get(position);
                surfaceView = (AVChatSurfaceViewRenderer) surfaceLayout.findViewById(R.id.recycler_view_main);
                if (surfaceView != null) {
                    AVChatManager.getInstance().setupRemoteVideoRender(mainItem.account, surfaceView, false, AVChatVideoScalingType.SCALE_ASPECT_FIT);
                }
            } else if (!myCount.equals(clickCount) && mainItem != null) {
                if (mainItem.account.equals(myCount)) {
                    AVChatSurfaceViewRenderer surfaceView2 = adapter.getViewHolderSurfaceView(mainItem);
                    if (surfaceView2 != null) {
                        AVChatManager.getInstance().setupLocalVideoRender(surfaceView2, false, AVChatVideoScalingType.SCALE_ASPECT_FIT);
                        AVChatManager.getInstance().startVideoPreview();
                        adapter.getData().get(0).state = TeamAVChatItem.STATE.STATE_PLAYING;
                        adapter.getData().get(0).videoLive = true;
                    }
                    surfaceView = (AVChatSurfaceViewRenderer) surfaceLayout.findViewById(R.id.recycler_view_main);
                    if (surfaceView != null) {
                        AVChatManager.getInstance().setupRemoteVideoRender(adapter.getData().get(position).account, surfaceView, false, AVChatVideoScalingType.SCALE_ASPECT_FIT);
                        mainItem = adapter.getData().get(position);
                    }
                } else {
                    if (!mainItem.account.equals(clickCount)) {
                        AVChatSurfaceViewRenderer surfaceView2 = adapter.getViewHolderSurfaceView(mainItem);
                        if (surfaceView2 != null) {
                            AVChatManager.getInstance().setupRemoteVideoRender(mainItem.account, surfaceView2, false, AVChatVideoScalingType.SCALE_ASPECT_FIT);
                        }
                        surfaceView = (AVChatSurfaceViewRenderer) surfaceLayout.findViewById(R.id.recycler_view_main);
                        if (surfaceView != null) {
                            AVChatManager.getInstance().setupRemoteVideoRender(adapter.getData().get(position).account, surfaceView, false, AVChatVideoScalingType.SCALE_ASPECT_FIT);
                            mainItem = adapter.getData().get(position);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
