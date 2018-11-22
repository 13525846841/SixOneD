package com.yksj.consultation.fragment;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.baidu.location.BDLocation;
import com.blankj.utilcode.util.LogUtils;
import com.library.base.baidu.BaiduLocationHelper;
import com.library.base.base.BaseFragment;
import com.library.base.utils.RxChooseHelper;
import com.library.base.utils.StorageUtils;
import com.netease.nimlib.sdk.avchat.constant.AVChatType;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.yksj.consultation.business.LoginBusiness;
import com.yksj.consultation.comm.FacePanelFragment;
import com.yksj.consultation.comm.FacePanelFragment.FaceItemOnClickListener;
import com.yksj.consultation.im.ChatActivity;
import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.sonDoc.chatting.avchat.team.TeamAVChatAction;
import com.yksj.consultation.sonDoc.chatting.avchat.viewholder.Container;
import com.yksj.consultation.sonDoc.chatting.avchat.viewholder.ModuleProxy;
import com.yksj.consultation.sonDoc.shopping.ShopActivity;
import com.yksj.consultation.sonDoc.views.VUMeterView;
import com.yksj.healthtalk.media.ArmMediaPlay;
import com.yksj.healthtalk.media.ArmMediaRecord;
import com.yksj.healthtalk.media.ArmMediaRecordListener;
import com.yksj.healthtalk.media.RecorderState;
import com.yksj.healthtalk.net.http.ApiCallbackWrapper;
import com.yksj.healthtalk.net.http.ApiService;
import com.yksj.healthtalk.net.http.HttpResult;
import com.yksj.healthtalk.net.socket.SmartControlClient;
import com.yksj.healthtalk.utils.FaceParse;
import com.yksj.healthtalk.utils.HStringUtil;
import com.yksj.healthtalk.utils.SystemUtils;
import com.yksj.healthtalk.utils.ToastUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.functions.Consumer;

/**
 * 聊天控制区域
 * @author jack_tang
 */
public class ChatInputControlFragment extends BaseFragment implements OnClickListener, FaceItemOnClickListener, ArmMediaRecordListener, ModuleProxy {
    private final String TAG = this.getClass().getSimpleName();
    public static final int REQUEST_GOODS_CODE = 2003;//商品推荐
    public static final int REQUEST_RECODE_CODE = 2002;//视频录制
    public static final int REQUEST_CAMERA_CODE = 2000;//相机
    public static final int REQUEST_FILE_CODE = 2001;//相册

    View mFacePanelV;//表情布局
    View mChatBoxPanelV;//更多菜单布局
    View mRecordPanelV;//语音录制布局
    View mDeletePanelV;//删除消息界面
    View mInputPanelV;//消息输入区域面板
    View mTxtPanelV;//文字消息输入布局

    EditText mEditText;//对话输入框
    CheckBox mArrowCheckBox;//更多菜单
    VUMeterView mChatVm;//语音录制View
    Button mDeleteBtn;//删除消息界面的删除按钮
    ImageButton mSpeakButton;//语音
    ImageButton mTxtButton;//键盘
    ImageButton mSendMsg;//发送消息

    ArmMediaPlay mediaPlay;

    ChatInputControlListener mInputControlListener;//消息处理接口
    File mChatImageFile;//当前相册或相机调用返回的图片
    private boolean isGroup = false;

    /**
     * 聊天操作
     */
    public interface ChatInputControlListener {
        //文字发送
        void onSendTxtMsg(String content);

        //位置发送
        void onSendLocationMsg(String longitude, String latitude, String address);

        //图片发送
        void onSendImageMsg(String path);

        //视频发送
        void onSendVideoMsg(String video);

        //语音
        void onSendVoiceMsg(String path, String timeStr, int time);

        //全选
        void onSelectAll();

        //删除所有
        void onDeletAll();

        //删除选中
        void onDeleteSelect();

        //删除选中
        void onDeleteCancel();

        //商品卡片发送
        void onSendGoodsMsg(String content);
    }

    @Override
    public int createLayoutRes() {
        return R.layout.chat_control_layout;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        FragmentActivity activity = getActivity();
        if (activity instanceof ChatInputControlListener) {
            mInputControlListener = (ChatInputControlListener) activity;
        }
        ChatActivity chatActivity = (ChatActivity) activity;
        this.mediaPlay = chatActivity.mediaPlay;
        ArmMediaRecord.getInstance().setRecordListener(this);
        mChatVm = chatActivity.mChatVm;
        if (SmartControlClient.helperId.equals(chatActivity.mChatId)) {
            mArrowCheckBox.setVisibility(View.GONE);
        }
    }

    @Override
    public void initialize(View view) {
        super.initialize(view);
        initializeView(view);
    }

    private void initializeView(View view) {
        view.findViewById(R.id.face_btn).setOnClickListener(this);
        view.findViewById(R.id.chat_video_btn).setOnClickListener(this);
        view.findViewById(R.id.chat_photo_btn).setOnClickListener(this);
        view.findViewById(R.id.chat_camera_btn).setOnClickListener(this);
        view.findViewById(R.id.chat_location_btn).setOnClickListener(this);
        view.findViewById(R.id.chat_goods_btn).setOnClickListener(this);
        view.findViewById(R.id.chat_delete_cancle).setOnClickListener(this);

        mSpeakButton = (ImageButton) view.findViewById(R.id.chat_send_btn);
        mTxtButton = (ImageButton) view.findViewById(R.id.chat_text_input_btn);
        mSpeakButton.setOnClickListener(this);
        mTxtButton.setOnClickListener(this);
        mDeleteBtn = (Button) view.findViewById(R.id.chat_delete_btn3);
        mDeleteBtn.setOnClickListener(this);
        mSendMsg = (ImageButton) view.findViewById(R.id.send_message);
        mSendMsg.setOnClickListener(this);
        mInputPanelV = view.findViewById(R.id.input_mesg_panel);
        mDeletePanelV = view.findViewById(R.id.delete_panel);
        mTxtPanelV = view.findViewById(R.id.txt_panel);
        mRecordPanelV = view.findViewById(R.id.recod_panel);

        mEditText = (EditText) view.findViewById(R.id.chat_edit);
        mArrowCheckBox = (CheckBox) view.findViewById(R.id.chat_arrow_btn);
        mArrowCheckBox.setOnClickListener(this);
        mChatBoxPanelV = view.findViewById(R.id.chat_box_panel);
        mFacePanelV = view.findViewById(R.id.face_panel);
        mEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                hideAllPanel();
            }
        });
        mEditText.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                hideAllPanel();
            }
        });
        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return false;
            }
        });
        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0) {
                    mSendMsg.setVisibility(View.GONE);
                    mArrowCheckBox.setVisibility(View.VISIBLE);

                } else {
                    mArrowCheckBox.setVisibility(View.GONE);
                    mSendMsg.setVisibility(View.VISIBLE);
                }
            }
        });


        mEditText.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event != null && KeyEvent.ACTION_DOWN == event.getAction() && keyCode == KeyEvent.KEYCODE_ENTER) {
                    //登录
                    onSendTxtMessage();
                    return true;
                } else
                    return false;
            }
        });


        mEditText.setOnEditorActionListener(new OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == KeyEvent.ACTION_DOWN || actionId == EditorInfo.IME_ACTION_SEND) {
                    //登录
                    onSendTxtMessage();
                    return true;
                }
                return false;
            }
        });

        //录音
        view.findViewById(R.id.chat_recod_btn).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_MOVE:
                        if (event.getY() < -10) {
                            ArmMediaRecord.getInstance().changeCancelState(true);
                        } else {
                            ArmMediaRecord.getInstance().changeCancelState(false);
                        }
                        break;
                    case MotionEvent.ACTION_DOWN:
                        ArmMediaRecord.getInstance().startRecorder();
                        return true;
                    case MotionEvent.ACTION_UP:
                        ArmMediaRecord.getInstance().stopRecorder();
                        break;
                }
                return false;
            }
        });
    }

    @Override
    public void onStart() {
        FacePanelFragment facePanelFragment = (FacePanelFragment) getChildFragmentManager().findFragmentByTag("face_fragment");
        facePanelFragment.setmFaceItemOnClickListener(this);
        super.onStart();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.face_btn://表情点击
                onFaceBtnClick();
                break;
            case R.id.chat_arrow_btn://箭头点击
                onArrowCheckBoxClick((CheckBox) v);
                break;
            case R.id.chat_send_btn://语音切换
                SystemUtils.hideSoftBord(getActivity(), mEditText);
                mEditText.setVisibility(View.GONE);
                mRecordPanelV.setVisibility(View.VISIBLE);
                mChatBoxPanelV.setVisibility(View.GONE);
                mTxtButton.setVisibility(View.VISIBLE);
                mSpeakButton.setVisibility(View.GONE);
                hideAllPanel();
                break;
            case R.id.chat_text_input_btn://键盘切换
                mEditText.setVisibility(View.VISIBLE);
                mRecordPanelV.setVisibility(View.GONE);
                mChatBoxPanelV.setVisibility(View.GONE);
                mSpeakButton.setVisibility(View.VISIBLE);
                mTxtButton.setVisibility(View.GONE);
                hideAllPanel();
                break;
            case R.id.chat_photo_btn://照片
                hideAllPanel();
                onPhotoClick();
                break;
            case R.id.chat_camera_btn://拍照
                hideAllPanel();
                onCameraClick();
                break;
            case R.id.chat_video_btn://视频录制
                //// O: 17/9/26 视频通话
                getGroupData(groupId);

//                startActivityForResult(new Intent(getActivity(), RecordMadeAty.class), REQUEST_RECODE_CODE);//小视频录制
                break;
            case R.id.chat_location_btn://地图
                hideAllPanel();
                onLocationClick();
                break;
            case R.id.chat_goods_btn://商品推送
                startActivityForResult(new Intent(getActivity(), ShopActivity.class), REQUEST_GOODS_CODE);
                break;
            case R.id.chat_delete_btn3://删除选中
                if (mInputControlListener != null) mInputControlListener.onDeleteSelect();
                break;
            case R.id.chat_delete_cancle://退出编辑模式
                if (mInputControlListener != null) mInputControlListener.onDeleteCancel();
                break;
            case R.id.send_message://发送消息
                onSendTxtMessage();
                break;
            default:
                break;
        }
    }

    @Override
    public void onItemClick(String text, Drawable drawable, FaceParse mFaceParse) {
        mFaceParse.insertToEdite(mEditText, drawable, text);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CAMERA_CODE://相机获取
                if (resultCode == Activity.RESULT_OK) {
                    onSendImageMessage(mChatImageFile.getAbsolutePath());
                }
                mChatImageFile = null;
                break;
            case REQUEST_RECODE_CODE://录制视频获取
                if (resultCode == Activity.RESULT_OK) {
                    String result = data.getExtras().getString("filePath");
                    onSendVideoMessage(result);
                }
//                mChatImageFile = null;
                break;
            case REQUEST_GOODS_CODE://商品推荐
                if (resultCode == Activity.RESULT_OK) {
                    ToastUtil.showShort("REQUEST_GOODS_CODE");
                }
                break;
            case REQUEST_FILE_CODE://文件获取
                if (resultCode == Activity.RESULT_OK) {
                    if (data != null) {
                        Uri uri = data.getData();
                        String strFilePath = getImageAbsolutePath(getActivity(), uri);
//                        String scheme = uri.getScheme();
//                        String strFilePath;//图片地址
//                        // url类型content or file
//                        if ("content".equals(scheme)) {
//                            strFilePath = getImageUrlByAlbum(uri);
//                        } else {
//                            strFilePath = uri.getPathForUri();
//                        }
                        onSendImageMessage(strFilePath);
                    }
                }
                break;
        }
    }

    @Override
    public void onRecordError(ArmMediaRecord record, int error) {//录音错误
        switch (error) {
            case RecorderState.ERROR_SHORT:
                ToastUtil.showToastPanl("录音时间太短");
                break;
            case RecorderState.ERROR_UNKNOWN:
                ToastUtil.showToastPanl("录音错误");
                mChatVm.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public void onRecordStateChnage(int state) {//录音状态改变
        switch (state) {
            case RecorderState.STATE_IDLE:
                mChatVm.setVisibility(View.GONE);
                break;
            case RecorderState.STATE_START:
                mChatVm.setVisibility(View.VISIBLE);
                break;
            case RecorderState.STATE_PARE:
                mChatVm.setVisibility(View.VISIBLE);
                break;
        }
    }

    /**
     * 录音完成
     */
    @Override
    public void onRecordOver(ArmMediaRecord record, File file, String time, long durationTime) {//1410598912.0
        if (mInputControlListener != null) {
            mInputControlListener.onSendVoiceMsg(file.getAbsolutePath(), time, (int) durationTime);
        }
    }

    /**
     * 发送图片
     * @param path 图片路径
     */
    private void onSendImageMessage(String path) {
        if (StorageUtils.isSDMounted()) {//内存卡处于加载状态
            if (mInputControlListener != null)
                mInputControlListener.onSendImageMsg(path);
        }
    }

    /**
     * 发送视频
     * @param path 文件路径
     */
    private void onSendVideoMessage(String path) {
        if (StorageUtils.isSDMounted()) {//内存卡处于加载状态
            //创建文件
            if (mInputControlListener != null) {
                mInputControlListener.onSendVideoMsg(path);
            }
        }
    }

    /**
     * 地图发送
     * @param longitude 经度
     * @param latitude  纬度
     */
    private void onSendLocationMessage(String longitude, String latitude, String address) {
        if (mInputControlListener != null)
            mInputControlListener.onSendLocationMsg(longitude, latitude, address);
    }

    /**
     * 发送文字
     */
    private void onSendTxtMessage() {
        String content = mEditText.getEditableText().toString();
        if (content.length() != 0) {
            mEditText.setText(null);
            if (mInputControlListener != null)
                mInputControlListener.onSendTxtMsg(content);
        }
    }

    /**
     * 更新选中个数
     */
    public void onUpdateSelectedNumber(int size) {
        String mSize = "删除选中(" + size + ")";
        mDeleteBtn.setText(mSize);
    }

    /**
     * 编辑模式
     */
    public void onChangeEditorMode(boolean b) {
        if (b) {//编辑模式下
            hideSoftBord();
            mInputPanelV.setVisibility(View.GONE);
            mDeletePanelV.setVisibility(View.VISIBLE);
        } else {//非编辑模式下
            mInputPanelV.setVisibility(View.VISIBLE);
            mDeletePanelV.setVisibility(View.GONE);
        }
    }

    /**
     * 所有的面板是否隐藏
     */
    public boolean isAllPanelGone() {
        if (mFacePanelV.getVisibility() == View.VISIBLE
                || mChatBoxPanelV.getVisibility() == View.VISIBLE) {
            mArrowCheckBox.setChecked(true);
            mFacePanelV.setVisibility(View.GONE);
            mChatBoxPanelV.setVisibility(View.GONE);
            return false;
        }
        return true;
    }

    /**
     * 隐藏所有的面板
     */
    private void hideAllPanel() {
        mArrowCheckBox.setChecked(true);
        mChatBoxPanelV.setVisibility(View.GONE);
        mFacePanelV.setVisibility(View.GONE);
    }

    /**
     * 隐藏键盘
     */
    public void hideSoftBord() {
        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
    }

    /**
     * 显示键盘
     */
    private void showSoftBord() {
        mEditText.setFocusable(true);
        mEditText.requestFocus();
        SystemUtils.showSoftMode(mEditText);
    }

    /**
     * 点击照片,相册获取
     */
    @SuppressLint("CheckResult")
    private void onPhotoClick() {
        RxChooseHelper.chooseImage(getActivity())
                      .subscribe(new Consumer<String>() {
                          @Override
                          public void accept(String s) throws Exception {
                              onSendImageMessage(s);
                          }
                      });
    }

    /**
     * 点击相机
     */
    @SuppressLint("CheckResult")
    private void onCameraClick() {
        RxChooseHelper.captureImage(getActivity())
                      .subscribe(new Consumer<String>() {
                          @Override
                          public void accept(String s) throws Exception {
                              LogUtils.e(s);
                              onSendImageMessage(s);
                          }
                      });
    }

    /**
     * 点击地图
     */
    private void onLocationClick() {
        BaiduLocationHelper
                .getInstance(getContext())
                .setChangeListener(new BaiduLocationHelper.OnLocationChangeListener() {
                    @Override
                    public void onLocationChange(BaiduLocationHelper helper, BDLocation location) {
                        String latitude = String.valueOf(location.getLatitude());
                        String longitude = String.valueOf(location.getLongitude());
                        String addrStr = location.getAddrStr();
                        onSendLocationMessage(longitude, latitude, addrStr);
                    }
                })
                .startLocation();
    }

    /**
     * 表情点击
     */
    private void onFaceBtnClick() {
        mEditText.setVisibility(View.VISIBLE);
        mRecordPanelV.setVisibility(View.GONE);
        if (mFacePanelV.getVisibility() == View.GONE) {
            hideSoftBord();
            mChatBoxPanelV.setVisibility(View.GONE);
            mFacePanelV.setVisibility(View.VISIBLE);
        } else {
            mFacePanelV.setVisibility(View.GONE);
        }
    }

    /**
     * 箭头点击
     */
    private void onArrowCheckBoxClick(CheckBox checkBox) {
        if (checkBox.isChecked()) {
            mChatBoxPanelV.setVisibility(View.GONE);
            mEditText.setVisibility(View.VISIBLE);
//            mQuickButton.setVisibility(View.VISIBLE);
            mRecordPanelV.setVisibility(View.GONE);

//			mTxtSelectorBtnV.setVisibility(View.GONE);
//			mVoiceSelectorBtnV.setVisibility(View.VISIBLE);
            showSoftBord();
            hideAllPanel();
        } else {
            hideSoftBord();
            mFacePanelV.setVisibility(View.GONE);
            mChatBoxPanelV.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 根据uri查询相册所对应的图片地址
     */
    public String getImageUrlByAlbum(Uri uri) {
//        String[] imageItems = {MediaColumns.DATA};
//        Cursor cursor = context.getContentResolver().query(uri, imageItems,
//                null, null, null);
////		Cursor cursor = getActivity().managedQuery(uri, imageItems, null, null, null);
//        if (cursor != null) {
//            cursor.moveToFirst();
//            int columIndex = cursor.getColumnIndexOrThrow(MediaColumns.DATA);
//            String ImagePath = cursor.getString(columIndex);
//            cursor.close();
//            return ImagePath;
//        }
//
//        return uri.toString();
////		int index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
////		cursor.moveToFirst();
////		String path = cursor.getString(index);
////		return path;

        if (null == uri) return null;
        final String scheme = uri.getScheme();
        String data = null;
        if (scheme == null)
            data = uri.getPath();
        else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            Cursor cursor = getContext().getContentResolver().query(uri, new String[]{MediaStore.Images.ImageColumns.DATA}, null, null, null);
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    if (index > -1) {
                        data = cursor.getString(index);
                    }
                }
                cursor.close();
            }
        }
        return data;
    }

    /**
     * 根据Uri获取图片绝对路径，解决Android4.4以上版本Uri转换
     * @param imageUri
     */
    @TargetApi(19)
    public static String getImageAbsolutePath(Activity context, Uri imageUri) {
        if (context == null || imageUri == null)
            return null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(context, imageUri)) {
            if (isExternalStorageDocument(imageUri)) {
                String docId = DocumentsContract.getDocumentId(imageUri);
                String[] split = docId.split(":");
                String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            } else if (isDownloadsDocument(imageUri)) {
                String id = DocumentsContract.getDocumentId(imageUri);
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                return getDataColumn(context, contentUri, null, null);
            } else if (isMediaDocument(imageUri)) {
                String docId = DocumentsContract.getDocumentId(imageUri);
                String[] split = docId.split(":");
                String type = split[0];
                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                String selection = MediaStore.Images.Media._ID + "=?";
                String[] selectionArgs = new String[]{split[1]};
                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        } // MediaStore (and general)
        else if ("content".equalsIgnoreCase(imageUri.getScheme())) {
            // Return the remote address
            if (isGooglePhotosUri(imageUri))
                return imageUri.getLastPathSegment();
            return getDataColumn(context, imageUri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(imageUri.getScheme())) {
            return imageUri.getPath();
        }
        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        String column = MediaStore.Images.Media.DATA;
        String[] projection = {column};
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    private String groupId = "";
    private ArrayList<String> accounts;//视频通话账号
    private String roomName;//视频通话房间名字

    /**
     * 设置聊天类型
     */
    public void setChatType(boolean isGroup, String id, String roomName, boolean canTalk) {
        this.isGroup = isGroup;
        this.groupId = id;
        if (canTalk) {
            if (isGroup) {
                getView().findViewById(R.id.selector_panel_video).setVisibility(View.VISIBLE);
            } else {
                getView().findViewById(R.id.selector_panel_video).setVisibility(View.GONE);
            }
        } else {
            getActivity().findViewById(R.id.chat_input_panel).setVisibility(View.GONE);
        }
    }

    private void onCreateRoomSuccess(ArrayList<String> accounts) {
        // 定制加号点开后可以包含的操作， 默认已经有图片，视频等消息了
        final TeamAVChatAction avChatAction = new TeamAVChatAction(AVChatType.VIDEO);
        Container container = new Container(getActivity(), groupId, SessionTypeEnum.ChatRoom, this);
        avChatAction.setContainer(container);
        avChatAction.onSelectedAccountsResult(getActivity(), accounts, groupId);
    }

    @Override
    public boolean sendMessage(IMMessage msg) {
        return false;
    }

    @Override
    public void onInputPanelExpand() {

    }

    @Override
    public void shouldCollapseInputPanel() {

    }

    @Override
    public boolean isLongClickEnabled() {
        return false;
    }

    @Override
    public void onItemFooterClick(IMMessage message) {

    }

    private ArrayList<String> list = null;

    /**
     * 获取群资料
     */
    private void getGroupData(final String groupId) {
        if (HStringUtil.isEmpty(groupId)) {
            return;
        }
        Map<String, String> map = new HashMap<>();
        map.put("group_id", groupId);
        map.put("op", "queryGroupPerson");
        ApiService.OKHttpGetFriends(map, new ApiCallbackWrapper<String>(getActivity()) {
            @Override
            public void onResponse(String response) {
                super.onResponse(response);

                if (!HStringUtil.isEmpty(response)) {
                    try {
                        list = new ArrayList<String>();
                        JSONObject object = new JSONObject(response);
                        if (HttpResult.SUCCESS.equals(object.optString("code")) && LoginBusiness.getInstance().getLoginEntity() != null) {
                            String customerAccount = LoginBusiness.getInstance().getLoginEntity().getSixOneAccount();
                            roomName = object.getJSONObject("result").optString("record_name");
                            JSONArray array = object.getJSONObject("result").getJSONArray("groupPerson");
                            int count = array.length();
                            if (count > 0) {
                                for (int i = 0; i < count; i++) {
                                    if (!customerAccount.equals(array.getJSONObject(i).optString("CUSTOMER_ACCOUNTS"))) {
                                        list.add(array.getJSONObject(i).optString("CUSTOMER_ACCOUNTS"));
                                    }
                                }
                            }
                            onCreateRoomSuccess(list);
                        } else {
                            ToastUtil.showShort(object.optString("message"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, this);
    }
}
