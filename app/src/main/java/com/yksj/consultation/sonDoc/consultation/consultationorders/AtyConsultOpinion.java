package com.yksj.consultation.sonDoc.consultation.consultationorders;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;

import com.library.base.base.BaseActivity;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;
import com.yksj.consultation.comm.DoubleBtnFragmentDialog;
import com.yksj.consultation.comm.ImageGalleryActivity;
import com.yksj.consultation.event.MyEvent;
import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.utils.DoctorHelper;
import com.yksj.consultation.business.LoginBusiness;
import com.yksj.healthtalk.net.http.ApiService;
import com.yksj.healthtalk.net.http.AsyncHttpResponseHandler;
import com.yksj.healthtalk.net.http.MyApiCallback;
import com.yksj.healthtalk.net.http.RequestParams;
import com.yksj.healthtalk.utils.JsonParser;
import com.yksj.healthtalk.utils.TimeUtil;
import com.yksj.healthtalk.utils.ToastUtil;

import org.greenrobot.eventbus.EventBus;
import org.handmark.pulltorefresh.library.PullToRefreshBase;
import org.handmark.pulltorefresh.library.PullToRefreshScrollView;
import org.json.JSONException;
import org.json.JSONObject;
import org.universalimageloader.core.DefaultConfigurationFactory;
import org.universalimageloader.core.DisplayImageOptions;
import org.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Request;

/**
 * Created by HEKL on 15/10/13.
 * Used for 专家会诊意见_
 */
public class AtyConsultOpinion extends BaseActivity implements View.OnClickListener, PullToRefreshBase.OnRefreshListener<ScrollView> {
    private int conId;
    private int supplyAdd;
    private TextView textOpinion, textSupply, textAnswer;
    private int questionFlag, answerFlag;
    private LinearLayout llQuestion;
    private HorizontalScrollView mView2;//图片横滑布局
    private LinearLayout mGallery;//图片画廊
    private String[] array = null;//病历图片
    private LayoutInflater mInflater;//图片布局
    private ImageLoader mImageLoader;//异步加载图片
    private DisplayImageOptions mOptions;//画廊异步读取操作
    private ArrayList<HashMap<String, String>> list = null;//储存图片
    private PullToRefreshScrollView mPullToRefreshScrollView;//整体滑动布局
    private PopupWindow mSupplyPopupWindow, mAnswerPopupWindow;
    private int clickCount;
    private int clickCount2;
    private EditText editSupply, editSupply2;
    private String editSly;
    SpeechRecognizer mIat;
    RecognizerDialog mIatDialog;
    // 引擎类型
    private String mEngineType = SpeechConstant.TYPE_CLOUD;
    SharedPreferences mSharedPreferences;
    private final String PREFER_NAME = "com.iflytek.setting";

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.aty_consult_opinion);
        initView();
    }


    public void onEvent(MyEvent event) {
        if ("opinion".equals(event.what)) {
            loadOpinion();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void initView() {
        initializeTitle();
        conId = getIntent().getIntExtra("conId", 0);
        titleTextV.setText("会诊意见");
        textOpinion = (TextView) findViewById(R.id.tv_opinion);
        textSupply = (TextView) findViewById(R.id.tv_supply);
        textSupply.setOnClickListener(this);
        titleLeftBtn.setOnClickListener(this);
        llQuestion = (LinearLayout) findViewById(R.id.ll_question);
        mPullToRefreshScrollView = (PullToRefreshScrollView) findViewById(R.id.pull_refresh_scrollview);
        mPullToRefreshScrollView.setOnRefreshListener(this);
        mImageLoader = ImageLoader.getInstance();
        mOptions = DefaultConfigurationFactory.createApplyPicDisplayImageOptions(this);
        mInflater = LayoutInflater.from(this);
        initVoiceData();
        loadOpinion();
    }

    private void loadOpinion() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("CONSULTATIONID", conId + "");
        map.put("OPTION", "9");
        ApiService.OKHttpgetOpinion(map,  new MyApiCallback<String>(this) {
            @Override
            public void onError(Request request, Exception e) {

            }

            @Override
            public void onBefore(Request request) {
                super.onBefore(request);
            }

            @Override
            public void onAfter() {
                super.onAfter();
                mPullToRefreshScrollView.onRefreshComplete();
            }

            @Override
            public void onResponse(String response) {
                if (!TextUtils.isEmpty(response)) {
                    try {
                        JSONObject object = new JSONObject(response);
                        View view = getLayoutInflater().inflate(R.layout.view_question, null);
                        View viewSec = getLayoutInflater().inflate(R.layout.view_question, null);
                        TextView textFrom = (TextView) view.findViewById(R.id.tv_fromwhere);
                        TextView textTime = (TextView) view.findViewById(R.id.tv_time);
                        TextView textQuestion = (TextView) view.findViewById(R.id.tv_question);
                        TextView textFrom2 = (TextView) viewSec.findViewById(R.id.tv_fromwhere);
                        TextView textTime2 = (TextView) viewSec.findViewById(R.id.tv_time);
                        TextView textQuestion2 = (TextView) viewSec.findViewById(R.id.tv_question);

                        String time = TimeUtil.formatTime(object.getJSONObject("result").optString("QUESTIONTIME"));
                        String time2 = TimeUtil.formatTime(object.getJSONObject("result").optString("ANSWERTIME"));

                        TextView answer = (TextView) view.findViewById(R.id.tv_answer);
                        answer.setOnClickListener(AtyConsultOpinion.this);
                        mView2 = (HorizontalScrollView) view.findViewById(R.id.hs_gallery);
                        mGallery = (LinearLayout) view.findViewById(R.id.ll_gallery);
                        mGallery.setOnClickListener(AtyConsultOpinion.this);
                        list = new ArrayList<>();
                        textSupply.setVisibility(View.GONE);
                        llQuestion.setVisibility(View.GONE);
                        answer.setVisibility(View.GONE);
                        llQuestion.removeAllViews();
                        if (object.optInt("code") == 1) {
                            textOpinion.setText(object.getJSONObject("result").optString("CONTENT"));
                            supplyAdd = object.getJSONObject("result").optInt("SUPPLYADD");
                            questionFlag = object.getJSONObject("result").optInt("QUESTIONFLAG");
                            answerFlag = object.getJSONObject("result").optInt("ANSWERFLAG");
                            if (supplyAdd == 1) {
                                textSupply.setVisibility(View.VISIBLE);
                            }
                            if (answerFlag == 0) {
                                answer.setVisibility(View.VISIBLE);
                            } else {
                                answer.setVisibility(View.GONE);
                            }
                            if (questionFlag == 1) {
                                llQuestion.setVisibility(View.VISIBLE);
                                textFrom.setText("患者疑问:");
                                textTime.setText(time);
                                textQuestion.setText(object.getJSONObject("result").optString("QUESTION"));
                                llQuestion.addView(view);
                                if (answerFlag == 1) {
                                    textFrom2.setText("我的解答:");
                                    textQuestion2.setText(object.getJSONObject("result").optString("ANSWER"));
                                    textTime2.setText(time2);
                                    llQuestion.addView(viewSec);
                                }
                                if (object.getJSONObject("result").getJSONArray("QUESTIONFILE").length() != 0) {
                                    int count = object.getJSONObject("result").getJSONArray("QUESTIONFILE").length();//图片数量
                                    //图片的适配
                                    mView2.setVisibility(View.VISIBLE);
                                    for (int t = 0; t < count; t++) {
                                        JSONObject ob = object.getJSONObject("result").getJSONArray("QUESTIONFILE").getJSONObject(t);
                                        HashMap<String, String> map = new HashMap<String, String>();
                                        map.put("ID", "" + ob.optInt("PIC_ID"));
                                        map.put("SMALL", ob.optString("SMALL_PICTURE"));
                                        map.put("BIG", ob.optString("BIG_PICTURE"));
                                        map.put("SEQ", "" + ob.optInt("PICTURE_SEQ"));
                                        list.add(map);
                                    }
                                    array = new String[count];
                                    for (int t = 0; t < count; t++) {
                                        array[t] = list.get(t).get("BIG");
                                    }
                                    mGallery.removeAllViews();
                                    for (int i = 0; i < count; i++) {
                                        final int index = i;
                                        View view2 = mInflater.inflate(R.layout.view_gallery, mGallery, false);
                                        ImageView img = (ImageView) view2.findViewById(R.id.image_illpic);
                                        mImageLoader.displayImage(list.get(i).get("SMALL"), img, mOptions);
                                        img.setOnClickListener(new View.OnClickListener() {

                                            @Override
                                            public void onClick(View v) {
                                                Intent intent = new Intent(AtyConsultOpinion.this, ImageGalleryActivity.class);
                                                intent.putExtra(ImageGalleryActivity.URLS_KEY, array);
                                                intent.putExtra(ImageGalleryActivity.TYPE_KEY, 1);
                                                intent.putExtra("type", 1);// 0,1单个,多个
                                                intent.putExtra("position", index);
                                                startActivity(intent);
                                            }
                                        });
                                        mGallery.addView(view2);
                                    }
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, this);
    }

    @Override
    public void onClick(View view) {
        Intent i;
        String supply;

        switch (view.getId()) {
            case R.id.title_back:
                onBackPressed();
                break;
            case R.id.tv_supply:
                showupSupplyPopWindow();
//                i = new Intent(AtyConsultOpinion.this, AtyExpertOpinion.class);
//                i.putExtra("TYPE", 2);
//                i.putExtra("conId", conId);
//                startActivity(i);
                break;
            case R.id.tv_answer:
                showupAnswerPopWindow();
                break;
            case R.id.image_voice:
                setParam();
                mIatDialog.setListener(recognizerDialogListener);
                mIatDialog.show();
                break;
            case R.id.image_voice2:
                setParam();
                mIatDialog.setListener(recognizerDialogListener2);
                mIatDialog.show();
                break;
            case R.id.btn_send:
                editSly = editSupply.getText().toString();
                if (TextUtils.isEmpty(editSly)) {
                    ToastUtil.showShort("输入内容不能为空");
                } else {
                    DoubleBtnFragmentDialog.showDefault(getSupportFragmentManager(), "您确认要提交该内容吗?", "取消", "确定", new DoubleBtnFragmentDialog.OnDilaogClickListener() {
                        @Override
                        public void onDismiss(DialogFragment fragment) {

                        }

                        @Override
                        public void onClick(DialogFragment fragment, View v) {
                            SendOpinion(editSly);
                        }
                    });
                }
                break;
            case R.id.btn_send2:
                editSly = editSupply2.getText().toString();
                if (TextUtils.isEmpty(editSly)) {
                    ToastUtil.showShort("输入内容不能为空");
                } else {
                    DoubleBtnFragmentDialog.showDefault(getSupportFragmentManager(), "您确认要提交该内容吗?", "取消", "确定", new DoubleBtnFragmentDialog.OnDilaogClickListener() {
                        @Override
                        public void onDismiss(DialogFragment fragment) {

                        }

                        @Override
                        public void onClick(DialogFragment fragment, View v) {
                            SendAnswerOpinion(editSly);
                        }
                    });
                }
                break;
        }
    }

    /**
     * 弹出补充意见布局
     */
    public void showupSupplyPopWindow() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.view_talk, null);
        if (mSupplyPopupWindow == null) {
            mSupplyPopupWindow = new PopupWindow(view, ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            mSupplyPopupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
            ImageView imageVoice = (ImageView) view.findViewById(R.id.image_voice);
            editSupply = (EditText) view.findViewById(R.id.edit_content);
            Button btnSend = (Button) view.findViewById(R.id.btn_send);
            editSupply.setHint("补充意见...");
            imageVoice.setOnClickListener(this);
            btnSend.setOnClickListener(this);
        }
        mSupplyPopupWindow.setAnimationStyle(R.style.anim_popupwindow_share);
        mSupplyPopupWindow.setBackgroundDrawable(new BitmapDrawable());
        mSupplyPopupWindow.setFocusable(true);
        mSupplyPopupWindow.setOutsideTouchable(true);
        mSupplyPopupWindow.showAtLocation(view, Gravity.BOTTOM, 0, 0);
    }

    /**
     * 弹出解答疑难布局
     */
    public void showupAnswerPopWindow() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.view_answer, null);
        if (mAnswerPopupWindow == null) {
            mAnswerPopupWindow = new PopupWindow(view, ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            mAnswerPopupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
            ImageView imageVoice = (ImageView) view.findViewById(R.id.image_voice2);
            editSupply2 = (EditText) view.findViewById(R.id.edit_content);
            Button btnSend = (Button) view.findViewById(R.id.btn_send2);
            editSupply2.setHint("解答疑问...");
            imageVoice.setOnClickListener(this);
            btnSend.setOnClickListener(this);
        }
        mAnswerPopupWindow.setAnimationStyle(R.style.anim_popupwindow_share);
        mAnswerPopupWindow.setBackgroundDrawable(new BitmapDrawable());
        mAnswerPopupWindow.setFocusable(true);
        mAnswerPopupWindow.setOutsideTouchable(true);
        mAnswerPopupWindow.showAtLocation(view, Gravity.BOTTOM, 0, 0);
    }

    /***
     * 发送专家补充意见
     */
    private void SendOpinion(String content) {
        if (clickCount > 0) {
            return;
        }
        clickCount++;
        RequestParams params = new RequestParams();
        params.put("CONSULTATIONID", conId + "");
        params.put("CUSTID", LoginBusiness.getInstance().getLoginEntity().getId());
        params.put("CONTENT", content);
        ApiService.doHttpPostConsultOpinion(params, new AsyncHttpResponseHandler(this) {
            JSONObject object = null;

            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onFinish() {
                super.onFinish();
            }

            @Override
            public void onFailure(Throwable error, String content) {
                super.onFailure(error, content);
                clickCount = 0;
            }

            @Override
            public void onSuccess(String content) {
                try {
                    object = new JSONObject(content);
                    ToastUtil.showShort(object.optString("message"));
                    if (object.optInt("code") == 1) {
                        editSupply.setText("");
                        mSupplyPopupWindow.dismiss();
                        clickCount = 0;
                        loadOpinion();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                super.onSuccess(content);
            }
        });
    }

    /***
     * 发送解答意见
     */
    private void SendAnswerOpinion(String content) {
        if (clickCount2 > 0) {
            return;
        }
        clickCount2++;
        RequestParams params = new RequestParams();
        params.put("CONSULTATIONID", conId + "");
        params.put("CUSTID", DoctorHelper.getId());
        params.put("CONTENT", content);
        ApiService.doHttpPostAnswerOpinion(params, new AsyncHttpResponseHandler(this) {
            JSONObject object = null;

            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onFinish() {
                super.onFinish();
            }

            @Override
            public void onFailure(Throwable error, String content) {
                super.onFailure(error, content);
                clickCount2 = 0;
            }

            @Override
            public void onSuccess(String content) {
                try {
                    object = new JSONObject(content);
                    ToastUtil.showShort(object.optString("message"));
                    if (object.optInt("code") == 1) {
                        editSupply2.setText("");
                        mAnswerPopupWindow.dismiss();
                        clickCount2 = 0;
                        loadOpinion();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                super.onSuccess(content);
            }
        });
    }

    /**
     * 参数设置
     *
     * @param
     * @return
     */
    public void setParam() {
        // 清空参数
        mIat.setParameter(SpeechConstant.PARAMS, null);

        // 设置听写引擎
        mIat.setParameter(SpeechConstant.ENGINE_TYPE, mEngineType);
        // 设置返回结果格式
        mIat.setParameter(SpeechConstant.RESULT_TYPE, "json");

        String lag = mSharedPreferences.getString("iat_language_preference",
                "mandarin");
        if (lag.equals("en_us")) {
            // 设置语言
            mIat.setParameter(SpeechConstant.LANGUAGE, "en_us");
        } else {
            // 设置语言
            mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
            // 设置语言区域
            mIat.setParameter(SpeechConstant.ACCENT, lag);
        }

        // 设置语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理
        mIat.setParameter(SpeechConstant.VAD_BOS, mSharedPreferences.getString("iat_vadbos_preference", "4000"));

        // 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入， 自动停止录音
        mIat.setParameter(SpeechConstant.VAD_EOS, mSharedPreferences.getString("iat_vadeos_preference", "1000"));

        // 设置标点符号,设置为"0"返回结果无标点,设置为"1"返回结果有标点
        mIat.setParameter(SpeechConstant.ASR_PTT, mSharedPreferences.getString("iat_punc_preference", "1"));
        // 设置音频保存路径，保存音频格式仅为pcm，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
        mIat.setParameter(SpeechConstant.ASR_AUDIO_PATH, Environment.getExternalStorageDirectory() + "/iflytek/wavaudio.pcm");

        // 设置听写结果是否结果动态修正，为“1”则在听写过程中动态递增地返回结果，否则只在听写结束之后返回最终结果
        // 注：该参数暂时只对在线听写有效
        mIat.setParameter(SpeechConstant.ASR_DWA, mSharedPreferences.getString("iat_dwa_preference", "0"));
    }

    /**
     * 补充听写UI监听器
     */
    private RecognizerDialogListener recognizerDialogListener = new RecognizerDialogListener() {
        public void onResult(RecognizerResult results, boolean isLast) {
            printResult(results, editSupply);
        }

        /**
         * 识别回调错误.
         */
        public void onError(SpeechError error) {
            ToastUtil.showShort(AtyConsultOpinion.this, error.getPlainDescription(true));

        }
    };
    /**
     * 解答听写UI监听器
     */
    private RecognizerDialogListener recognizerDialogListener2 = new RecognizerDialogListener() {
        public void onResult(RecognizerResult results, boolean isLast) {
            printResult(results, editSupply2);
        }

        /**
         * 识别回调错误.
         */
        public void onError(SpeechError error) {
            ToastUtil.showShort(AtyConsultOpinion.this, error.getPlainDescription(true));

        }
    };

    private void printResult(RecognizerResult results, EditText editText) {
        String text = JsonParser.parseIatResult(results.getResultString());
        insertText(editText, text);
    }

    private void initVoiceData() {
        SpeechUtility.createUtility(AtyConsultOpinion.this, "appid=" + getString(R.string.app_id));
        // 使用SpeechRecognizer对象，可根据回调消息自定义界面；
        mIat = SpeechRecognizer.createRecognizer(AtyConsultOpinion.this, mInitListener);
        // 初始化听写Dialog，如果只使用有UI听写功能，无需创建SpeechRecognizer
        // 使用UI听写功能，请根据sdk文件目录下的notice.txt,放置布局文件和图片资源
        mIatDialog = new RecognizerDialog(AtyConsultOpinion.this, mInitListener);
        mSharedPreferences = AtyConsultOpinion.this.getSharedPreferences(PREFER_NAME, Activity.MODE_PRIVATE);
    }

    /**
     * 初始化监听器。
     */
    private InitListener mInitListener = new InitListener() {

        @Override
        public void onInit(int code) {
            if (code != ErrorCode.SUCCESS) {
//                ToastUtil.showToastPanl("初始化失败，错误码：" + code);
                ToastUtil.showShort(AtyConsultOpinion.this, "初始化失败，错误码：" + code);
//				showTip("初始化失败，错误码：" + code);
            }
        }
    };

    @Override
    public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {
        loadOpinion();
    }

    /**
     * 获取EditText光标所在的位置
     */
    private int getEditTextCursorIndex(EditText mEditText) {
        return mEditText.getSelectionStart();
    }

    /**
     * 向EditText指定光标位置插入字符串
     */
    private void insertText(EditText mEditText, String mText) {
        mEditText.getText().insert(getEditTextCursorIndex(mEditText), mText);
    }
}
