package com.yksj.consultation.sonDoc.consultation.consultationorders;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.blankj.utilcode.util.AppUtils;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;
import com.yksj.consultation.event.MyEvent;
import com.library.base.base.BaseActivity;
import com.yksj.consultation.comm.DoubleBtnFragmentDialog;
import com.yksj.consultation.comm.SingleBtnFragmentDialog;
import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.utils.DoctorHelper;
import com.yksj.consultation.business.LoginBusiness;
import com.yksj.healthtalk.net.http.ApiService;
import com.yksj.healthtalk.net.http.AsyncHttpResponseHandler;
import com.yksj.healthtalk.net.http.RequestParams;
import com.yksj.healthtalk.utils.HStringUtil;
import com.yksj.healthtalk.utils.JsonParser;
import com.yksj.healthtalk.utils.SharePreHelper;
import com.yksj.healthtalk.utils.ToastUtil;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by HEKL on 15/9/30.
 * 专家给意见/专家要求补充病历
 */
public class AtyExpertOpinion extends BaseActivity implements View.OnClickListener {
    private int type;//0病历补充//1给意见
    private EditText editText, editSupply, editSupplyOpinion;
    private String content = null;
    private int conId;
    StringBuffer resultBuffer = null;
    private boolean finish = false;
    private int clickCount;
    SpeechRecognizer mIat;
    RecognizerDialog mIatDialog;
    SharedPreferences mSharedPreferences;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.aty_expertopinion);
        initView();
    }

    private void initView() {
        type = getIntent().getIntExtra("TYPE", 0);
        resultBuffer = new StringBuffer();
        conId = getIntent().getIntExtra("conId", 0);
        initializeTitle();
        titleLeftBtn.setOnClickListener(this);
        TextView textTip = (TextView) findViewById(R.id.tv_tip);
        Button btnCommit = (Button) findViewById(R.id.commit);
        btnCommit.setOnClickListener(this);
        editText = (EditText) findViewById(R.id.edit_opinion);
        editSupply = (EditText) findViewById(R.id.edit_supply);
        if (type == 0) {
            textTip.setText("请输入要求");
            titleTextV.setText("补充要求");
            editSupply.setVisibility(View.VISIBLE);
        } else if (type == 1) {
            titleTextV.setText("给出意见");
            editText.setVisibility(View.VISIBLE);
            if (((conId + "").equals(SharePreHelper.feachStringFromUserID(this, "CONSULTATIONID", null))) && SharePreHelper.feachStringFromUserID(this, "CONSULTATIONOPINION", null) != null) {
                editText.setText(SharePreHelper.feachStringFromUserID(this, "CONSULTATIONOPINION", null));
            } else {
                editText.setText("");
            }
        }
        editSupply.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() >= 1000) {
                    ToastUtil.showShort("您输入的字数已经超过了限制！");
                }
            }
        });
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() >= 5000) {
                    ToastUtil.showShort("您输入的字数已经超过了限制！");
                }
                String mConsultOpinion = editText.getText().toString();// 会诊报告内容
                SharePreHelper.editorStringFromUserID(AtyExpertOpinion.this, "CONSULTATIONOPINION", mConsultOpinion);
                SharePreHelper.editorStringFromUserID(AtyExpertOpinion.this, "CONSULTATIONID", conId + "");
                if (editable.length() != 0) {
                    resultBuffer.append(editable.toString());
                } else {
                    resultBuffer.setLength(0);
                }
            }
        });
        findViewById(R.id.image_voice).setOnClickListener(this);
        initVoiceData();
    }

    @Override
    public void onClick(View view) {
        if (type == 0) {
            content = editSupply.getText().toString().trim();
        } else {
            content = editText.getText().toString().trim();
        }
        String str11 = "您确认提交您的会诊意见吗?";
        String str12 = "请输入会诊意见";
        String str21 = "您确认提交您的病历补充要求吗?";
        String str22 = "请输入病历补充要求";
        String str3 = "您确定要退出编辑吗?";
        switch (view.getId()) {
            case R.id.title_back:
                if (!TextUtils.isEmpty(content)) {
                    DoubleBtnFragmentDialog.showDefault(getSupportFragmentManager(), str3, "取消", "确认", new DoubleBtnFragmentDialog.OnDilaogClickListener() {
                        @Override
                        public void onDismiss(DialogFragment fragment) {

                        }

                        @Override
                        public void onClick(DialogFragment fragment, View v) {
                            onBackPressed();
                        }
                    });
                } else {
                    onBackPressed();
                }
                break;
            case R.id.commit:
                String message = null;
                String tip = null;
                if (type == 0) {
                    message = str22;
                    tip = str21;
                } else {
                    message = str12;
                    tip = str11;
                }
                if (!TextUtils.isEmpty(content)) {
                    DoubleBtnFragmentDialog.showDefault(getSupportFragmentManager(), tip, "取消", "确认", new DoubleBtnFragmentDialog.OnDilaogClickListener() {
                        @Override
                        public void onDismiss(DialogFragment fragment) {

                        }

                        @Override
                        public void onClick(DialogFragment fragment, View v) {
                            if (type == 0) {
                                sendSupply();
                            } else {
                                SendOpinion();
                            }

                        }
                    });
                } else {
                    SingleBtnFragmentDialog.showDefault(getSupportFragmentManager(), message, new SingleBtnFragmentDialog.OnClickSureBtnListener() {
                        @Override
                        public void onClickSureHander() {

                        }
                    });
                }
                break;
            case R.id.image_voice:
                setParam();
                mIatDialog.setListener(recognizerDialogListener);
                mIatDialog.show();
                break;
        }
    }

    /***
     * 发送专家意见
     */
    private void SendOpinion() {
        if (clickCount > 0) {
            return;
        }
        clickCount++;
        RequestParams params = new RequestParams();
        params.put("CONSULTATIONID", conId + "");
        params.put("CUSTID", LoginBusiness.getInstance().getLoginEntity().getId());
        params.put("CONTENT", content);
        ApiService.doHttpPostConsultOpinion(params, new AsyncHttpResponseHandler(AtyExpertOpinion.this) {
            JSONObject object = null;

            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onFinish() {
                if (type == 1) {
                    if (finish) {
                        AtyExpertOpinion.this.finish();
                        SharedPreferences sp = getSharedPreferences(DoctorHelper.getMD5Id()
                                + "version" + AppUtils.getAppVersionName() + "_comment", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString("CONSULTATIONOPINION", "");
                        editor.commit();
                    }
                }
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
                        finish = true;
                        EventBus.getDefault().post(new MyEvent("refresh", 2));
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                super.onSuccess(content);
            }
        });
    }

    /**
     * 病历补充
     */
    private void sendSupply() {
        if (clickCount > 0) {
            return;
        }
        clickCount++;
        RequestParams params = new RequestParams();
        params.put("CONSULTATIONID", conId + "");
        params.put("OPTION", 12 + "");
        params.put("CONTENT", content);
        ApiService.doOKHttpSendSupply(params, new AsyncHttpResponseHandler(AtyExpertOpinion.this) {
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
                        finish = true;
                        EventBus.getDefault().post(new MyEvent("refresh", 2));
                        finish();
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
        String mEngineType = SpeechConstant.TYPE_CLOUD;
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

    private void initVoiceData() {
        SpeechUtility.createUtility(AtyExpertOpinion.this, "appid=" + getString(R.string.app_id));
        // 使用SpeechRecognizer对象，可根据回调消息自定义界面；
        mIat = SpeechRecognizer.createRecognizer(AtyExpertOpinion.this, mInitListener);
        // 初始化听写Dialog，如果只使用有UI听写功能，无需创建SpeechRecognizer
        // 使用UI听写功能，请根据sdk文件目录下的notice.txt,放置布局文件和图片资源
        mIatDialog = new RecognizerDialog(AtyExpertOpinion.this, mInitListener);
        String PREFER_NAME = "com.iflytek.setting";
        mSharedPreferences = AtyExpertOpinion.this.getSharedPreferences(PREFER_NAME, Activity.MODE_PRIVATE);
    }

    /**
     * 初始化监听器。
     */
    private InitListener mInitListener = new InitListener() {

        @Override
        public void onInit(int code) {
            if (code != ErrorCode.SUCCESS) {
//                ToastUtil.showToastPanl("初始化失败，错误码：" + code);
                ToastUtil.showShort(AtyExpertOpinion.this, "初始化失败，错误码：" + code);
//				showTip("初始化失败，错误码：" + code);
            }
        }
    };
    /**
     * 听写UI监听器
     */
    private RecognizerDialogListener recognizerDialogListener = new RecognizerDialogListener() {
        public void onResult(RecognizerResult results, boolean isLast) {
            printResult(results);
        }

        /**
         * 识别回调错误.
         */
        public void onError(SpeechError error) {
            ToastUtil.showShort(AtyExpertOpinion.this, error.getPlainDescription(true));

        }
    };

    private void printResult(RecognizerResult results) {
        String text = JsonParser.parseIatResult(results.getResultString());
        if (!HStringUtil.isEmpty(text)) {
            if (type == 0) {
                insertText(editSupply, text);
            } else {
                insertText(editText, text);
            }
        }
        editText.setFocusable(true);
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
