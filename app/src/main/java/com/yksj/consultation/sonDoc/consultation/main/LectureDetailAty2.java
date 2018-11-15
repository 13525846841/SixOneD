package com.yksj.consultation.sonDoc.consultation.main;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.library.base.base.BaseActivity;
import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.app.AppContext;
import com.yksj.healthtalk.bean.CourseClass;
import com.yksj.healthtalk.filemanager.FileUtil;
import com.yksj.consultation.business.LoginBusiness;
import com.yksj.healthtalk.net.http.ApiCallbackWrapper;
import com.yksj.healthtalk.net.http.ApiService;
import com.yksj.healthtalk.utils.ToastUtil;
import com.yksj.healthtalk.utils.coreprogress.ProgressHelper;
import com.yksj.healthtalk.utils.coreprogress.ProgressUIListener;
import com.yksj.healthtalk.views.progress.NumberProgressBar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;;;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;

import static com.library.base.utils.StorageUtils.getUserImagePath;

/**
 * 六一班文件详情
 */
public class LectureDetailAty2 extends BaseActivity {
    private TextView textName, priceW, priceN, textIntroduce, preSee;
    private CheckBox checkPriceW, checkPriceN;
    private Button btnPass, btnUnpass;
    private NumberProgressBar numberProgressBar;
    public static final String COURSE_ID = "course_id";
    public static final String COURSE_CLASS = "course_class";//10文档 20图片 30shipin
    public static final String COURSE_STATUS = "course_stauts";//课件状态 10已申请待审核 20审核通过发布中 30审核失败已驳回 40 已下架
    public static final String CUSTOMER_TYPE = "customer_type";//10文档 20图片 30shipin
    public static final String COURSE_UP_ID = "customer_up_id";//上传id
    public static final String COURSE_NAME = "course_name";//上传id
    public static final String COURSE_ADDRESS = "course_address";//上传id
    private String course_id = "";
    private String course_name = "";
    private String course_class = "";
    private String customer_type = "";
    private String course_status = "";
    private String payPrice = "";
    private String pay_id = "";//订单id
    private String course_adress = "";//订单地址
    private String customer_up_id = "";//上传id

    private String payStatus = "";//10 已生成订单，未支付 //20已支付 //30超时支付
    private boolean isDownLoad = false;//未下载

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lecture_detail_aty2);
        initView();
    }

    private void initView() {
        initializeTitle();
        titleTextV.setText("文件详情");
        titleLeftBtn.setOnClickListener(this);
        textName = (TextView) findViewById(R.id.lecture_name);
        priceW = (TextView) findViewById(R.id.tv_price_w);
        priceN = (TextView) findViewById(R.id.tv_price_n);
        preSee = (TextView) findViewById(R.id.pre_video);
        checkPriceW = (CheckBox) findViewById(R.id.check1);
        checkPriceN = (CheckBox) findViewById(R.id.check2);
        textIntroduce = (TextView) findViewById(R.id.tv_introduce_detail);
        btnPass = (Button) findViewById(R.id.btn1);
        btnUnpass = (Button) findViewById(R.id.btn2);
        numberProgressBar = (NumberProgressBar) findViewById(R.id.loadingProgress);
        if (getIntent().hasExtra(COURSE_ID))
            course_id = getIntent().getStringExtra(COURSE_ID);
        if (getIntent().hasExtra(COURSE_NAME))
            course_name = getIntent().getStringExtra(COURSE_NAME);
        if (getIntent().hasExtra(COURSE_ADDRESS))
            course_adress = getIntent().getStringExtra(COURSE_ADDRESS);
        if (getIntent().hasExtra(COURSE_CLASS))
            course_class = getIntent().getStringExtra(COURSE_CLASS);
        if (getIntent().hasExtra(CUSTOMER_TYPE))
            customer_type = getIntent().getStringExtra(CUSTOMER_TYPE);
        if (getIntent().hasExtra(COURSE_STATUS))
            course_status = getIntent().getStringExtra(COURSE_STATUS);
        if (getIntent().hasExtra(COURSE_UP_ID))
            customer_up_id = getIntent().getStringExtra(COURSE_UP_ID);
        checkPriceW.setFocusable(false);
        checkPriceW.setFocusableInTouchMode(false);
        checkPriceN.setFocusable(false);
        checkPriceN.setFocusableInTouchMode(false);
        btnPass.setOnClickListener(this);
        btnUnpass.setOnClickListener(this);
        textName.setText(course_name);
        makeOption();
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.title_back:
                onBackPressed();
                break;
            case R.id.pre_video://预览
//                doThePre();
                break;
            case R.id.btn1://按钮1
                doTheOption();
                break;
            case R.id.btn2://按钮2
                if ("10".equals(customer_type)) {//审核
                    updateLecture("30");
                }
                break;
        }
    }


    /**
     * {
     * "code": "1",
     * "message": "操作完成",
     * "result": {
     * "COURSE_IN_LIST": "1",
     * "COURSE_OUT_LIST": "0",
     * "SMALL_COURSE_ADDRESS": "''",
     * "COURSE_ADDRESS": "/classroomFile/1506067400323.txt",
     * "SMALL_PIC": "''",
     * "COURSE_ID": "35",
     * "SITE_ID": "243",
     * "COURSE_UP_ID": "124984",
     * "COURSE_NAME": "文档课件",
     * "COURSE_IN_PRICE": 0.01,
     * "COURSE_OUT_PRICE": 0.02,
     * "COURSE_DESC": "uvuv与一次一次一宠成瘾",
     * "COURSE_STATUS": 10
     * }
     */
    String pre_video = "";//预览视频地址

    private void initData() {
        if (LoginBusiness.getInstance().getLoginEntity() != null) {
            Map<String, String> map = new HashMap<>();
            if ("10".equals(customer_type) || customer_up_id.equals(LoginBusiness.getInstance().getLoginEntity().getId())) {
                map.put("op", "queryCourseInfo");
            } else {
                map.put("op", "queryBuyPageCourseInfo");
                map.put("customer_id", LoginBusiness.getInstance().getLoginEntity().getId());
            }
            map.put("course_id", course_id);//1
            ApiService.OKHttLectureServlet(map, new ApiCallbackWrapper<String>(this) {


                @Override
                public void onResponse(String content) {
                    try {
                        JSONObject obj = new JSONObject(content);

                        if ("1".equals(obj.optString("code"))) {
                            textName.setText(obj.getJSONObject("result").optString("COURSE_NAME"));
                            priceW.setText("站外价格：¥" + obj.getJSONObject("result").optString("COURSE_OUT_PRICE"));
                            priceN.setText("站内价格：¥" + obj.getJSONObject("result").optString("COURSE_IN_PRICE"));
                            textIntroduce.setText(obj.getJSONObject("result").optString("COURSE_DESC"));
                            course_adress = obj.getJSONObject("result").optString("COURSE_ADDRESS");
                            if ("1".equals(obj.getJSONObject("result").optString("COURSE_IN_LIST"))) {
                                payPrice = obj.getJSONObject("result").optString("COURSE_IN_PRICE");
                                checkPriceN.setChecked(true);
                                checkPriceW.setChecked(false);
                            } else {
                                payPrice = obj.getJSONObject("result").optString("COURSE_OUT_PRICE");
                                checkPriceN.setChecked(false);
                                checkPriceW.setChecked(true);
                            }
                            if (obj.getJSONObject("result").has("pay_status")) {//已支付
                                payStatus = obj.getJSONObject("result").optString("pay_status");
                                pay_id = obj.getJSONObject("result").optString("pay_id");

                            }
                            makeOption();
                        } else {
                            ToastUtil.showShort(obj.optString("message"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onAfter() {
                    super.onAfter();
                }
            }, this);
        }

    }

    /**
     * 文件是否下载
     */
    private void hasDownLoad() {
        String[] all = course_adress.split("\\.");
        File outFile = null;
        String fileName = LoginBusiness.getInstance().getLoginEntity().getSixOneAccount() + course_id;
        String path = getUserImagePath();
        String paths = path + fileName + "." + all[1];
        try {
            outFile = new File(paths);
            if (outFile.exists()) {
                isDownLoad = true;
            }
        } catch (Exception e) {
        }
    }

    /**
     * 审核
     */
    private void updateLecture(String status) {
        if (LoginBusiness.getInstance().getLoginEntity() != null) {
            Map<String, String> map = new HashMap<>();
            map.put("op", "updateCourseStatus");
            map.put("status", status);
            map.put("course_id", course_id);//1
            ApiService.OKHttLectureServlet(map, new ApiCallbackWrapper<String>(this) {
                @Override
                public void onResponse(String content) {
                    try {
                        JSONObject obj = new JSONObject(content);
                        ToastUtil.showShort(obj.optString("message"));
                        if ("1".equals(obj.optString("code"))) {
                            finish();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }, this);
        }

    }


    /**
     * 打开文件
     */
    private void openFile() {
        String[] all = course_adress.split("\\.");
        String fileName = LoginBusiness.getInstance().getLoginEntity().getSixOneAccount() + course_id;
        String path = getUserImagePath();
        String paths = path + fileName + "." + all[1];
        Intent intent = new Intent("android.intent.action.VIEW");
        // 判断版本大于等于7.0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
        }
        intent = FileUtil.openFile(paths);
        startActivity(intent);
    }

    /**
     * 下载文件
     */
    private void download() {
//        String url = "http://192.168.1.136:8080/DuoMeiHealth/DownloadCourseServlet?course_id=49";
//        String url = AppContext.getmRepository().DOWNLOADCOURSESERVLET + "?course_id=" + course_id;
        String url = AppContext.getApiRepository().DOWNLOADGROUPFILESERVLET + "?file_id=" + course_id;
        OkHttpClient okHttpClient = new OkHttpClient();
        Request.Builder builder = new Request.Builder();
        builder.url(url);
        builder.get();
        Call call = okHttpClient.newCall(builder.build());

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("TAG", "=============onFailure===============");
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
//                Log.e("TAG", "=============onResponse===============");
//                Log.e("TAG", "request headers:" + response.request().headers());
//                Log.e("TAG", "response headers:" + response.headers().get("filename"));
                ResponseBody responseBody = ProgressHelper.withProgress(response.body(), new ProgressUIListener() {

                    //if you don't need this method, don't override this methd. It isn't an abstract method, just an empty method.
                    @Override
                    public void onUIProgressStart(long totalBytes) {
                        super.onUIProgressStart(totalBytes);
//                        Log.e("TAG", "onUIProgressStart:" + totalBytes);
//                        Toast.makeText(getApplicationContext(), "开始下载：" + totalBytes, Toast.LENGTH_SHORT).show();
                        Toast.makeText(getApplicationContext(), "开始下载", Toast.LENGTH_SHORT).show();
                        numberProgressBar.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onUIProgressChanged(long numBytes, long totalBytes, float percent, float speed) {
//                        Log.e("TAG", "=============start===============");
//                        Log.e("TAG", "numBytes:" + numBytes);
//                        Log.e("TAG", "totalBytes:" + totalBytes);
//                        Log.e("TAG", "percent:" + percent);
//                        Log.e("TAG", "speed:" + speed);
//                        Log.e("TAG", "============= end ===============");
                        numberProgressBar.setProgress((int) (100 * percent));
//                        downloadInfo.setText("numBytes:" + numBytes + " bytes" + "\ntotalBytes:" + totalBytes + " bytes" + "\npercent:" + percent * 100 + " %" + "\nspeed:" + speed * 1000 / 1024 / 1024 + " MB/秒");
                    }

                    //if you don't need this method, don't override this methd. It isn't an abstract method, just an empty method.
                    @Override
                    public void onUIProgressFinish() {
                        super.onUIProgressFinish();
//                        Log.e("TAG", "onUIProgressFinish:");
                        Toast.makeText(getApplicationContext(), "完成下载", Toast.LENGTH_SHORT).show();
                        numberProgressBar.setVisibility(View.GONE);
                        isDownLoad = true;
                        makeOption();
                    }
                });
                String[] all = course_adress.split("\\.");
                if (all.length > 1) {
                    BufferedSource source = responseBody.source();
                    File outFile = null;
                    String fileName = LoginBusiness.getInstance().getLoginEntity().getSixOneAccount() + course_id;
                    String path = getUserImagePath();
                    String paths = path + fileName + "." + all[1];
                    try {
                        outFile = new File(paths);
                        if (!outFile.exists()) {
                            outFile.getParentFile().mkdirs();
                            outFile.createNewFile();
                        }
                    } catch (Exception e) {
                    }
                    BufferedSink sink = Okio.buffer(Okio.sink(outFile));
                    source.readAll(sink);
                    sink.flush();
                    source.close();
                }

            }
        });

    }

//    /**
//     * 预览操作
//     * http://192.168.1.136:8080/DuoMeiHealth/DownloadCourseServlet?course_id=49
//     */
//    private void doThePre() {
//        if ("10".equals(customer_type)) {
//            if (CourseClass.WD.equals(course_class) && !isDownLoad) {
//                download();
//            } else if (CourseClass.WD.equals(course_class) && isDownLoad) {
//                openFile();
//            } else if (CourseClass.TP.equals(course_class) && !isDownLoad) {
//                download();
//            } else if (CourseClass.TP.equals(course_class) && isDownLoad) {
//                openFile();
//            } else if (CourseClass.SP.equals(course_class)) {
//                Intent intent = new Intent(Intent.ACTION_VIEW);
//                String type = "video/* ";
//                String url = AppContext.getmRepository().DOWNLOADCOURSESERVLET + "?course_id=" + course_id;
//                Uri uri = Uri.parse(url);
//                intent.setDataAndType(uri, type);
//                startActivity(intent);
//            }
//        } else {//站内成员短视频
//            Intent intent = new Intent(Intent.ACTION_VIEW);
//            String type = "video/* ";
////            String url = AppContext.getmRepository().DOWNLOADCOURSESERVLET + "?course_id=" + course_id+"&mini=1";
//            String url = AppContext.getmRepository().DOWNLOADCOURSESERVLET + "?course_id=" + course_id;
//            Uri uri = Uri.parse(url);
//            intent.setDataAndType(uri, type);
//            startActivity(intent);
//        }
//    }


    /**
     * 按钮支持
     */
    private void doTheOption() {
        if (CourseClass.WD.equals(course_class) && !isDownLoad) {
            download();
        } else if (CourseClass.WD.equals(course_class) && isDownLoad) {
            openFile();
        } else if (CourseClass.TP.equals(course_class) && !isDownLoad) {
            download();
        } else if (CourseClass.TP.equals(course_class) && isDownLoad) {
            openFile();
        } else if (CourseClass.SP.equals(course_class) && !isDownLoad) {
            download();


//            Intent intent2 = new Intent(Intent.ACTION_VIEW);
//            String type = "video/* ";
//            String url = AppContext.getmRepository().DOWNLOADCOURSESERVLET + "?course_id=" + course_id;
//            Uri uri = Uri.parse(url);
//            intent2.setDataAndType(uri, type);
//            startActivity(intent2);
        } else if (CourseClass.SP.equals(course_class) && isDownLoad) {
            openFile();

//            Intent intent2 = new Intent(Intent.ACTION_VIEW);
//            String type = "video/* ";
//            String url = AppContext.getmRepository().DOWNLOADCOURSESERVLET + "?course_id=" + course_id;
//            Uri uri = Uri.parse(url);
//            intent2.setDataAndType(uri, type);
//            startActivity(intent2);
        }


    }

    /**
     * 设置操作
     */
    private void makeOption() {
        hasDownLoad();
        findViewById(R.id.ll_option).setVisibility(View.VISIBLE);
        findViewById(R.id.btn1).setVisibility(View.VISIBLE);
        if (isDownLoad) {//已支付
            findViewById(R.id.btn1).setVisibility(View.VISIBLE);
            btnPass.setText("打开");
        } else {
            btnPass.setText("下载");
        }
    }


}
