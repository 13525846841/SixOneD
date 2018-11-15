package com.yksj.consultation.sonDoc.consultation.salon.ui;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.ToggleButton;

import com.library.base.base.BaseTitleActivity;
import com.yksj.consultation.constant.Constant;
import com.yksj.consultation.comm.DoubleBtnFragmentDialog;
import com.yksj.consultation.comm.SingleBtnFragmentDialog;
import com.yksj.consultation.dialog.WaitDialog;
import com.yksj.consultation.im.ChatActivity;
import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.sonDoc.salon.SalonSelectPaymentOptionActivity;
import com.yksj.healthtalk.db.ChatUserHelper;
import com.yksj.healthtalk.entity.GroupInfoEntity;
import com.yksj.healthtalk.net.http.ApiService;
import com.yksj.healthtalk.net.http.AsyncHttpResponseHandler;
import com.yksj.healthtalk.net.socket.SmartFoxClient;
import com.yksj.healthtalk.utils.BitmapUtils;
import com.yksj.healthtalk.utils.SalonHttpUtil;
import com.yksj.healthtalk.utils.SystemUtils;
import com.yksj.healthtalk.utils.ToastUtil;
import com.yksj.healthtalk.utils.WheelUtils;

import com.yksj.consultation.utils.CropUtils;
import org.handmark.pulltorefresh.library.PullToRefreshScrollView;
import org.json.JSONException;
import org.json.JSONObject;
import com.library.base.utils.StorageUtils;

import java.io.File;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;


/**
 * 创建话题的Activity
 * 两张情况 1:会员创建  2:医生创建
 *
 * @author lmk
 */
public class CreateTopicInfoUI extends BaseTitleActivity implements OnClickListener,
        SalonSelectPaymentOptionActivity.OnBuyTicketHandlerListener, OnEditorActionListener {
    private final int WHAT_FAILE = -1;
    private final int WHAT_SUCC = 1;
    public static final int CAMERA_REQUESTCODE = 3;
    private static final int PHOTO_PICKED_WITH_DATA = 1;
    private static final int TAG_CHOOSE_TOPICTAG = 2;//
    private static final int TAG_SETTING_TICKET = 4;//设置门票返回
    private ImageView headIcon, headClick;//头像,头像点击前的图片
    private EditText editTitle, editDesc;//标题,描述
    private Button btnCategory;//分类
    private ToggleButton toggleButton;//是否开通门票
    private EditText editDayPrice, editMonthPrice;//日票,月票价格
    private LinearLayout openTicketLayout, specialPriceLayout;//开通门票,特殊收费布局
    private PullToRefreshScrollView mScrollView;

    private WaitDialog dialog;
    private File headerFile;//
    private PopupWindow mPopupWindow;//上传头像的弹出框
    private File storageFile;
    private String imgPath;//
    private HashMap<String, Object> mTag;//创建话题时选择的TagEntity或者我去选中后返回的
    private GroupInfoEntity entity;//创建成功后返回的话题实体
    private boolean isCreating = false;//是否正在创建,如果在创建,不可以继续点击

    //创建成功或这失败的处理
    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case WHAT_FAILE:
                    WaitDialog.dismiss(getSupportFragmentManager());
                    if (msg.obj != null) {
                        SingleBtnFragmentDialog.showDefault(getSupportFragmentManager(), String.valueOf(msg.obj));
                    } else {
                        ToastUtil.showShort(CreateTopicInfoUI.this, R.string.groupNewFail);
                    }
                    break;
                case WHAT_SUCC:
                    WaitDialog.dismiss(getSupportFragmentManager());
                    entity = SalonHttpUtil.jsonAnalysisSalonEntity((String) msg.obj).get(0);
                    ChatUserHelper.getInstance().changeRelType(entity);

                    if (SmartFoxClient.getLoginUserInfo().isDoctor()) {//是医生提示是否开通门票
                        DoubleBtnFragmentDialog.show(getSupportFragmentManager(), getString(R.string.tishi),
                                getString(R.string.confirm_to_open_ticket), getString(R.string.confirm_not_open),
                                getString(R.string.confirm_open), new DoubleBtnFragmentDialog.OnDilaogClickListener() {

                                    @Override
                                    public void onDismiss(DialogFragment fragment) {
                                        SalonHttpUtil.onItemClick(CreateTopicInfoUI.this, CreateTopicInfoUI.this, getSupportFragmentManager(), entity, true);
//									setResult(RESULT_OK);
                                        CreateTopicInfoUI.this.finish();
                                    }

                                    @Override
                                    public void onClick(DialogFragment fragment, View v) {
                                        //跳转去设置门票
                                        Intent intent = new Intent(CreateTopicInfoUI.this, TopicTicketSettingActivity.class);
                                        intent.putExtra("topicId", entity.getId());
                                        startActivityForResult(intent, TAG_SETTING_TICKET);
                                    }
                                });
//					dialog.setCancelable(false);
//					dialog.setCanceledOnTouchOutside(false);
                    } else {
                        //不是医生,直接去聊天界面
                        goChating();
                    }
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    public int createLayoutRes() {
        return R.layout.health_topic_info_create_ui;
    }

    @Override
    public void initialize(Bundle bundle) {
        super.initialize(bundle);
        initView();
        initData();
    }

    //初始化控件
    private void initView() {
        titleTextV.setText(R.string.groupNew);
        titleRightBtn2.setVisibility(View.VISIBLE);
        titleRightBtn2.setText(R.string.sure);
        titleLeftBtn.setOnClickListener(this);
        titleRightBtn2.setOnClickListener(this);
        mScrollView = (PullToRefreshScrollView) findViewById(R.id.pull_refresh_scrollview);
        headIcon = (ImageView) findViewById(R.id.health_topic_info_headicon);
        headIcon.setVisibility(View.GONE);
        headClick = (ImageView) findViewById(R.id.health_topic_info_headicon_click);
        headClick.setVisibility(View.VISIBLE);
        editTitle = (EditText) findViewById(R.id.health_topic_info_title2);
        editDesc = (EditText) findViewById(R.id.health_topic_info_des2);
//		editTitle.setOnEditorActionListener(this);
        editDesc.setOnEditorActionListener(this);
        editDayPrice = (EditText) findViewById(R.id.health_topic_info_day_price);
        editMonthPrice = (EditText) findViewById(R.id.health_topic_info_monrh_price);
        btnCategory = (Button) findViewById(R.id.health_topic_info_category2);
        toggleButton = (ToggleButton) findViewById(R.id.health_topic_open_ticket_toggleButton);
//        openTicketLayout = (LinearLayout) findViewById(R.id.health_topic_info_ticket_layout);
        specialPriceLayout = (LinearLayout) findViewById(R.id.health_topic_info_special_layout);
        btnCategory.setOnClickListener(this);
        headIcon.setOnClickListener(this);
        headClick.setOnClickListener(this);
        specialPriceLayout.setOnClickListener(this);
        mScrollView.setLayoutInvisible();
        editDesc.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
    }


    //初始化数据
    private void initData() {
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_back:
                setResult(RESULT_OK);
                onBackPressed();
                break;
            case R.id.title_right2://创建
                if (isCreating)//正在创建返回
                    return;
                createGroup();
                break;
            case R.id.health_topic_info_special_layout://点击特殊人群收费

                break;
            case R.id.health_topic_info_category2://进入分类
                Intent tagintent = new Intent(CreateTopicInfoUI.this, SelectLabelActivity.class);//跳转去选择标签
                tagintent.putExtra("type", "groupInfoLay");
                startActivityForResult(tagintent, TAG_CHOOSE_TOPICTAG);
                break;
            case R.id.health_topic_info_headicon://头像
                showuploadPopWindow();
                break;
            case R.id.health_topic_info_headicon_click://头像
                showuploadPopWindow();
                break;
            case R.id.galleryadd://从相册获取
                if (mPopupWindow.isShowing()) {
                    mPopupWindow.dismiss();
                }
                try {
                    if (!SystemUtils.getScdExit()) {
                        ToastUtil.showSDCardBusy();
                        return;
                    }
                    Intent intent = CropUtils.createPickForFileIntent();
                    startActivityForResult(intent, PHOTO_PICKED_WITH_DATA);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;
            case R.id.cameraadd://相机拍照
                if (mPopupWindow.isShowing()) {
                    mPopupWindow.dismiss();
                }
                if (!SystemUtils.getScdExit()) {
                    ToastUtil.showSDCardBusy();
                    return;
                }
                try {
                    storageFile = StorageUtils.createImageFile();
                    Uri outUri = Uri.fromFile(storageFile);
                    Intent intent = CropUtils.createPickForCameraIntent(outUri);
                    startActivityForResult(intent, CAMERA_REQUESTCODE);
                } catch (Exception e) {
                    e.printStackTrace();
                    ToastUtil.showCreateFail();
                }

                break;
            case R.id.cancel:
                if (mPopupWindow != null && mPopupWindow.isShowing()) {
                    mPopupWindow.dismiss();
                }
                break;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPopupWindow != null && mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
        }

    }

    /**
     * 弹出上传图片的选择布局
     */
    public void showuploadPopWindow() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.interest_image_add_action, null);
        View mainView = inflater.inflate(R.layout.interest_content, null);
        if (mPopupWindow == null) {
            mPopupWindow = new PopupWindow(view, LayoutParams.FILL_PARENT,
                    LayoutParams.WRAP_CONTENT);
            mPopupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        }
        WheelUtils.setPopeWindow(this, mainView, mPopupWindow);
        Button cameraAdd = (Button) view.findViewById(R.id.cameraadd);
        Button galleryAdd = (Button) view.findViewById(R.id.galleryadd);
        Button cancel = (Button) view.findViewById(R.id.cancel);

        cameraAdd.setOnClickListener(this);
        galleryAdd.setOnClickListener(this);
        cancel.setOnClickListener(this);
    }

    /**
     * 图片裁剪
     *
     * @param path
     */
    private void onHandlerCropImage(String path) {
        if (!SystemUtils.getScdExit()) {
            ToastUtil.showSDCardBusy();
            return;
        }
        try {
            headerFile = StorageUtils.createHeaderFile();
            Uri outUri = Uri.fromFile(new File(path));
            Uri saveUri = Uri.fromFile(headerFile);
            Intent intent = CropUtils.createHeaderCropIntent(this, outUri, saveUri, true);
            startActivityForResult(intent, 3002);
        } catch (Exception e) {
            ToastUtil.showCreateFail();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CAMERA_REQUESTCODE://相机拍照
                if (resultCode == Activity.RESULT_OK) {
                    String strFilePath = storageFile.getAbsolutePath();
                    onHandlerCropImage(strFilePath);
                }
                break;
            case PHOTO_PICKED_WITH_DATA://从相册获取
                if (resultCode == Activity.RESULT_OK) {
                    if (data != null) {
                        Uri uri = data.getData();
                        String scheme = uri.getScheme();
                        String strFilePath = null;//图片地址
                        // url类型content or file
                        if ("content".equals(scheme)) {
                            strFilePath = getImageUrlByAlbum(uri);
                        } else {
                            strFilePath = uri.getPath();
                        }
                        onHandlerCropImage(strFilePath);
                    }
                }

                break;
            case 3002://裁剪后获取结果
                if (resultCode == Activity.RESULT_OK) {
                    if (resultCode == RESULT_OK) {
                        Bitmap bitmap = BitmapUtils.decodeBitmap(headerFile.getAbsolutePath(),
                                CropUtils.HEADER_WIDTH,
                                CropUtils.HEADER_HEIGTH);
                        headIcon.setImageBitmap(bitmap);
                        headIcon.setVisibility(View.VISIBLE);
                        headClick.setVisibility(View.GONE);
                    } else {
                        if (headerFile != null) headerFile.deleteOnExit();
                        headerFile = null;
                    }
                }

                break;
            case TAG_CHOOSE_TOPICTAG://标签返回
                if (resultCode == Activity.RESULT_OK) {
                    mTag = (HashMap<String, Object>) data.getSerializableExtra("tag");
                    btnCategory.setBackgroundResource(R.drawable.btn_topic_label_bg);
                    btnCategory.setText((CharSequence) mTag.get("name"));
                }
                break;
            case TAG_SETTING_TICKET://设置门票返回
                if (resultCode == Activity.RESULT_OK && entity != null) {
                    ToastUtil.showShort(getString(R.string.create_salon_success));
                    goChating();
                }
                break;
        }
    }

    /**
     * 跳转 去聊天接界面
     */
    private void goChating() {
        SalonHttpUtil.onItemClick(CreateTopicInfoUI.this, CreateTopicInfoUI.this, getSupportFragmentManager(), entity, true);
        Timer timer = new Timer();
        TimerTask task = new TimerTask() { //设置定时器让话题创建界面定时销毁
            @Override
            public void run() {
                CreateTopicInfoUI.this.finish();
            }
        };
        timer.schedule(task, 1000 * 2);
    }

    /**
     * 根据uri查询相册所对应的图片地址
     *
     * @param uri
     * @return
     */
    private String getImageUrlByAlbum(Uri uri) {
        String[] imageItems = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(uri, imageItems, null, null, null);
        int index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String path = cursor.getString(index);
        return path;
    }

    //判断是否已经填写了基本信息
    public boolean hasInputBaseInfo() {
        String name = editTitle.getText().toString();
        String describe = editDesc.getText().toString();
        if ("".equals(name.trim())) {
            ToastUtil.showLong(this, R.string.inputTitle);
            return false;
        }
        if (name.trim().length() > 10) {//标题长度小于10
            ToastUtil.showLong(this, R.string.inputThemeLength);
            return false;
        }
        if (describe.trim().length() > 1000) {//描述文本小于1000
            ToastUtil.showLong(this, R.string.inputDescLength);
            return false;
        }
        if ("".equals(describe.trim())) {
            ToastUtil.showLong(this, R.string.inputAddDesc);
            return false;
        }
//        if (mTag == null || "".equals(mTag.get("id").toString().trim())) {
//            ToastUtil.showShort(getApplicationContext(), "请添加话题类型~");
//            return false;
//        }
        return true;
    }

    //创建话题
    private void createGroup() {
        isCreating = true;
//		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);  
//		imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS); //隐藏软键盘,反之则显示
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(CreateTopicInfoUI.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        if (!hasInputBaseInfo()) {
            isCreating = false;
            return;
        }
        final String name = editTitle.getText().toString();
        final String describe = editDesc.getText().toString();
        try {
            JSONObject json = new JSONObject();
            json.put("groupId", "");
            json.put("userFileName", "0");
            try {
                json.put("recordName", name);
                json.put("record_desc", describe);
//                json.put("infoLayid", mTag.get("id").toString().trim());
                json.put("infoLayid", "健康");
                json.put("limitNumber", "");
                json.put("inceptMessage", "Y");//Y
                json.put("releaseSystemMessage", "0");// 默认不发布,是否将消息发布到消息厅(1-发布，0-不发布)
                if (SmartFoxClient.getLoginUserId() != null) {
                    json.put("custId", SmartFoxClient.getLoginUserId());
                } else {
                    Message message = handler.obtainMessage();
                    message.what = WHAT_FAILE;
                    handler.sendMessage(message);
                }
                json.put("publicCustInfo", "Y");// 是否公开创建者信息(Y/N,默认为Y)
                json.put("infoLayName", mTag.get("name").toString().trim());
            } catch (Exception e) {
                e.printStackTrace();
            }//1默认是医生,2是患者创建
            json.put("groupClass", SmartFoxClient.getLoginUserInfo().isDoctor() ? 1 : 2);
            ApiService.doHttpNewSalon(getApplicationContext(),
                    json.toString(), headerFile,
                    new AsyncHttpResponseHandler(CreateTopicInfoUI.this) {

                        public void onSuccess(int statusCode,
                                              String content) {
                            super.onSuccess(statusCode, content);
                            try {
                                if (content != null && content.contains("error_message")) {
                                    isCreating = false;//已经创建失败了,将其设置为false可以继续创建
                                    JSONObject jsonObject = new JSONObject(content);
                                    ToastUtil.showToastPanl(jsonObject.optString("error_message"));
                                } else {
                                    // 是否成功
                                    isRequestSuccess(content);
                                }
                            } catch (Exception e) {
                            }
                        }

                        @Override
                        public void onFailure(Throwable error, String content) {
                            isCreating = false;//已经创建失败了,将其设置为false可以继续创建
                            super.onFailure(error, content);
                        }
                    });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //是否创建成功
    private void isRequestSuccess(String content) {
        if (dialog != null) {
            dialog.dismissAllowingStateLoss();
            dialog = null;
        }
        if (content == null || "".equals(content) || !content.contains("{")) {
            Message message = handler.obtainMessage();
            message.what = WHAT_FAILE;
            message.obj = content;
            handler.sendMessage(message);
            return;
        } else {
//			if (path != null) {
//				ImageUtils.deleBitmap(path);
//			}
            Message message = handler.obtainMessage();
            message.obj = content;
            message.what = WHAT_SUCC;
            handler.sendMessage(message);
        }
    }

    @Override
    public void onTicketHandler(String state, GroupInfoEntity entity) {
        if ("0".equals(state)) {
        } else if ("-1".equals(state)) {
            ToastUtil.showShort(this, "服务器出错");
        } else {
            Intent intent1 = new Intent();
            intent1.putExtra(Constant.Chat.KEY_PARAME, entity);
            intent1.setClass(this, ChatActivity.class);
            startActivity(intent1);
        }
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (event == null)
            return false;
        return (event.getKeyCode() == KeyEvent.KEYCODE_ENTER);//当用户输入回车是进行截获
    }

    ;

}
