package com.yksj.consultation.sonDoc.dossier;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;

import com.library.base.base.BaseActivity;
import com.yksj.consultation.comm.DoubleBtnFragmentDialog;
import com.yksj.consultation.comm.SingleBtnFragmentDialog;
import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.app.AppContext;
import com.yksj.consultation.sonDoc.consultation.DAtyAssistantConsultService;
import com.yksj.consultation.sonDoc.consultation.TemplateItemMultipleChoiceActivity;
import com.yksj.consultation.sonDoc.views.widget.Tag;
import com.yksj.healthtalk.net.http.AsyncHttpResponseHandler;
import com.yksj.healthtalk.net.http.ApiService;
import com.yksj.healthtalk.net.http.RequestParams;
import com.yksj.healthtalk.net.socket.SmartFoxClient;
import com.yksj.healthtalk.function.photoutil.AlbumActivity;
import com.yksj.healthtalk.function.photoutil.GalleryActivity;
import com.yksj.healthtalk.utils.Bimp;
import com.yksj.healthtalk.utils.BitmapUtils;
import com.yksj.healthtalk.utils.CaseItemComparator;
import com.yksj.healthtalk.utils.DensityUtils;
import com.yksj.healthtalk.utils.HStringUtil;
import com.yksj.healthtalk.utils.ImageItem;
import com.yksj.healthtalk.utils.JsonParseUtils;
import com.yksj.healthtalk.utils.StringFormatUtils;
import com.yksj.healthtalk.utils.SystemUtils;
import com.yksj.healthtalk.utils.ToastUtil;
import com.yksj.healthtalk.utils.WheelUtils;
import com.yksj.healthtalk.views.MessageImageView;

import com.yksj.consultation.utils.CropUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.universalimageloader.core.ImageLoader;
import com.library.base.utils.StorageUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 基层医生主动创建病历的创建界面
 * Created by lmk on 2015/7/8.
 */
public class DoctorCreateCaseActivity extends BaseActivity implements View.OnClickListener{

    private static final int TAKE_PICTURE = 0xfff0;
    private static final int TASK_SELECT_DATA = 0xfff1;
    private static final int TASK_KEY_WORDS_DATA = 0xfff2;
    public static ArrayList<CaseItemEntity> itemsList;//病历项所有的数据,在下一个界面加载
    private ArrayList<JSONObject> postJson=new ArrayList<JSONObject>();//上传的json字符数据

    private HashMap<Integer, TextView> multipleTexts;//多选的结果返回应填入对应的EditText
    private TextView tvAddItem,editAge,editSex;
    private EditText editName,editBirthDay,editAddr,editZhiye,editCodeNum;
    private LinearLayout phoneLayout;
    private ImageView imgAddImg,imgAddAudio,imgAddVideo;
    private LinearLayout contentLayout,imgLayout;//病历项内容布局,影音图像布局
    private ScrollView mScrolview;
    private boolean isUploading=false;//是否正在上传
    private Button addKey;

    SingleBtnFragmentDialog postDialog=null;//上传提示对话框
    PopupWindow mPopupWindow,addPhotoPop,agepop;
    private EditText dateEdit;
    private File storageFile=null;
    private String patientId,doctorId;
    private HashMap<Integer,String> saveDatas=new HashMap<Integer, String>();//存放用户填写保存的信息
    private ArrayList<CaseItemEntity> selectItems;//选择的多个病历项
    private org.universalimageloader.core.ImageLoader mImageLoader;

    private JSONArray keysData;//用户选择的关键字
    private ArrayList<Tag> systemTags,customerTags;
    private LinearLayout keysLayout;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.aty_doctor_cre_case);
        patientId=getIntent().getStringExtra("patientId");
        doctorId=getIntent().getStringExtra("doctorId");
        initView();
        initData();

    }

    private void initData() {
        //GetContentMRTServlet?OPTION=9&CUSTID=4358
        RequestParams params=new RequestParams();
        params.put("OPTION","9");
        params.put("CUSTID", patientId);

        ApiService.doHttpConsultionGetContent(params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(String content) {
                super.onSuccess(content);
                onBoundData(content);
            }
        });
    }

    private void onBoundData(String content) {
        try {
            JSONObject object=new JSONObject(content);
            editName.setText(object.optString("NAME"));
//            editAge.setText(object.optString("NAME"));
            editZhiye.setText(object.optString("METIER"));
            if("W".equals(object.optString("SEX"))){
                editSex.setText("女");
            }else if("M".equals(object.optString("SEX"))){
                editSex.setText("男");
            }
            final ArrayList<Map<String,String>> sexList=new ArrayList<Map<String, String>>();
            HashMap< String, String> map=new HashMap<String, String>();
            map.put("name", "男");
            map.put("code", "M");
            HashMap< String, String> map2=new HashMap<String, String>();
            map2.put("name", "女");
            map2.put("code", "W");
            sexList.add(map);
            sexList.add(map2);
            editSex.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setSingleSelector(sexList,editSex,0,true);
                }
            });
            editCodeNum.setText(object.optString("CODE"));
            String bir=object.optString("BIRTHDAY");
            if(bir!=null&&bir.length()!=0){

                String nian=bir.substring(0,4);
                String yue=bir.substring(4,6);
                String ri=bir.substring(6, 8);
                Calendar cc=Calendar.getInstance();
                int age=cc.get(Calendar.YEAR)-Integer.parseInt(nian);
                if(age>=0){
                    editAge.setText(age+"");
                    editBirthDay.setText(nian+"-"+yue+"-"+ri);
                }
            }
            if(object.has("PHONE")){//包含这个键
                editAddr.setText(object.optString("PHONE").trim());
            }else{//不包含
                findViewById(R.id.create_case_tv_phone_left).setVisibility(View.GONE);
                editAddr.setVisibility(View.GONE);
            }

        } catch (JSONException e) {
        e.printStackTrace();
    }

    }

    private void initView() {
        initializeTitle();
        titleLeftBtn.setOnClickListener(this);
        titleTextV.setText(R.string.consultation_flow_text3);
        titleRightBtn2.setText(R.string.save);
        titleRightBtn2.setVisibility(View.VISIBLE);
        titleRightBtn2.setOnClickListener(this);
        //添加关键词
        addKey= (Button) findViewById(R.id.create_case_keywords_add);
        addKey.setOnClickListener(this);

        editName= (EditText) findViewById(R.id.create_case_input_name);
        editAddr= (EditText) findViewById(R.id.create_case_input_address);
        editAge= (TextView) findViewById(R.id.create_case_input_age);
        editCodeNum= (EditText) findViewById(R.id.create_case_input_card_num);
        editZhiye= (EditText) findViewById(R.id.create_case_input_zhiye);
        editSex= (TextView) findViewById(R.id.create_case_input_sex);
        editBirthDay= (EditText) findViewById(R.id.create_case_input_birthday);
        editBirthDay.setOnClickListener(this);
        contentLayout= (LinearLayout) findViewById(R.id.create_case_patient_condition_content);
        mScrolview= (ScrollView) findViewById(R.id.create_case_scrollview);
        imgLayout= (LinearLayout) findViewById(R.id.create_case_img_layout);
        keysLayout= (LinearLayout) findViewById(R.id.create_case_keywords);

        findViewById(R.id.create_case_click_add_item).setOnClickListener(this);
        imgAddImg= (ImageView) findViewById(R.id.create_case_add_image);
        imgAddAudio= (ImageView) findViewById(R.id.create_case_add_audio);
        imgAddVideo= (ImageView) findViewById(R.id.create_case_add_video);
        imgAddImg.setOnClickListener(this);
        imgAddAudio.setOnClickListener(this);
        imgAddVideo.setOnClickListener(this);

        ArrayList<ImageItem> itemImgs=new ArrayList<ImageItem>();//
        Bimp.dataMap.put(DoctorCreateCaseActivity.class.getSimpleName(), itemImgs);
        Bimp.imgMaxs.put(DoctorCreateCaseActivity.class.getSimpleName(), 32);
        mImageLoader=ImageLoader.getInstance();

        selectItems=new ArrayList<CaseItemEntity>();
    }


    @Override
    public void onClick(View v) {
        Intent intent=null;
        switch (v.getId()){
            case R.id.title_back:
                onBackPressed();
                break;
            case R.id.title_right2://保存
                if(verifyData()) {//验证完成,提交
                    titleRightBtn2.setClickable(false);
                    final RequestParams params=new RequestParams();
                    params.putNullFile("file", new File(""));
                    params.put("CUSTID", patientId);
                    params.put("DOCTORID", SmartFoxClient.getLoginUserId());
                    params.put("EXPERTID", doctorId);
                    params.put("CENTERID", AppContext.APP_CONSULTATION_CENTERID);
                    params.put("NAME", editName.getText().toString().trim());
                    params.put("METIER", editZhiye.getText().toString().trim());
                    params.put("PHONE", editAddr.getText().toString().trim());
                    String bir=editBirthDay.getText().toString().trim();
                    if(bir!=null&&bir.length()!=0){
                        String[] srr=bir.split("-");
                        params.put("BIRTHDAY", srr[0]+srr[1]+srr[2]+"000000");
                    }
                    if("男".equals(editSex.getText().toString().trim())){
                        params.put("SEX", "M");
                    }else if("女".equals(editSex.getText().toString().trim())){
                        params.put("SEX", "W");
                    }
                    params.put("CODE", editCodeNum.getText().toString().trim());

                    params.put("CONTENT", postJson.toString());
                    if(keysData!=null){
                        params.put("FLAGCONTENT", keysData.toString());
                    }else
                        try {
                            params.put("FLAGCONTENT", new JSONArray("").toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
//                    if(dePics.length()!=0)
//                        params.put("DEPIC", dePics.toString());
                    putFile(params);

                    DoubleBtnFragmentDialog.show(getSupportFragmentManager(), "提示", "您确定现在就保存并且并提交给医生吗?", "取消", "确定",
                            new DoubleBtnFragmentDialog.OnDilaogClickListener() {
                                @Override
                                public void onDismiss(DialogFragment fragment) {
                                    isUploading=false;
                                    titleRightBtn2.setClickable(true);
                                }

                                @Override
                                public void onClick(DialogFragment fragment, View v) {
                                    doUpLoad(params);
                                }
                            });


                }else{
                    isUploading=!isUploading;
                }

                break;
            case R.id.create_case_click_add_item:
                setSaveDatas();
                intent=new Intent(this,DoctorAddCaseItemActivity.class);
                startActivityForResult(intent, TASK_SELECT_DATA);
                break;
            case R.id.create_case_add_image://添加图片
                showuploadPopWindow();
                break;
            case R.id.create_case_add_audio://添加音频

                break;
            case R.id.create_case_add_video://添加视频

                break;
            case R.id.create_case_keywords_add://添加关键字
//                intent =new Intent(this,AtyAddKeyWord.class);
//                startActivityForResult(intent, TASK_KEY_WORDS_DATA);
                intent =new Intent(this,AtyAddKey.class);
                intent.putExtra("result1",systemTags);
//                intent.putExtra("result2",customerTags);
                startActivityForResult(intent, TASK_KEY_WORDS_DATA);
                break;
            case R.id.create_case_input_birthday://添加生日
                dateEdit=editBirthDay;
                setAgeDate(-1);
                break;
            case R.id.cameraadd://相机
                if (addPhotoPop.isShowing()) {
                    addPhotoPop.dismiss();
                }
                photo();
                break;
            case R.id.cancel://取消
                if (addPhotoPop!=null &&  addPhotoPop.isShowing()) {
                    addPhotoPop.dismiss();
                }
                break;
            case R.id.galleryadd://从相册获取
                if (addPhotoPop.isShowing()) {
                    addPhotoPop.dismiss();
                }
                intent = new Intent(this,AlbumActivity.class);
                intent.putExtra("key", DoctorCreateCaseActivity.class.getSimpleName());
                startActivity(intent);
//			getActivity().overridePendingTransition(R.anim.activity_translate_in, R.anim.activity_translate_out);
                break;
            case R.id.wheel_sure_age:
                String[] str = (String[]) v.getTag();
                if(dateEdit==editBirthDay){
                    Calendar cc=Calendar.getInstance();
                    int age=cc.get(Calendar.YEAR)-Integer.parseInt(str[0].substring(0, str[0].length()-1));//得到年领
                    if (age<=0){
                        ToastUtil.showShort(this,"请正确输入出生日期!");
                        return;
                    }
                    editAge.setText(age+"");
                }
                dateEdit.setText(str[0].substring(0, str[0].length() - 1) + "-" + str[1].substring(0, str[1].length() - 1)
                        + "-" + str[2].substring(0, str[2].length() - 1));
                break;

        }
    }

    private void doUpLoad(RequestParams params) {
        ApiService.doHttpDoctorPostConsultionCase(params,
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onStart() {
                        postDialog = SingleBtnFragmentDialog.show(getSupportFragmentManager(), "提示:",
                                "正在上传,请耐心等待!", "确定");
                        postDialog.setCancelable(false);//不可被取消
                        super.onStart();
                    }

                    @Override
                    public void onFinish() {
                        postDialog.dismiss();
                        isUploading = false;
                        titleRightBtn2.setClickable(true);
                        super.onFinish();
                    }

                    @Override
                    public void onFailure(Throwable error,
                                          String content) {
                        isUploading = false;
                        titleRightBtn2.setClickable(true);
                        super.onFailure(error, content);
                    }

                    @Override
                    public void onSuccess(String content) {
                        try {
                            JSONObject object = new JSONObject(content);
                            if (object.has("errorcode")) {
                                ToastUtil.showShort(object.optString("errormessage"));
                            } else {
                                ToastUtil.showShort(object.optString("INFO"));
                                Intent intent = new Intent(DoctorCreateCaseActivity.this, DAtyAssistantConsultService.class);
                                startActivity(intent);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        super.onSuccess(content);
                    }
                });
    }


    /**
     * 弹出上传图片的选择布局
     */
    public void showuploadPopWindow() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.interest_image_add_action, null);
        View mainView = inflater.inflate(R.layout.interest_content, null);
        if (addPhotoPop == null) {
            addPhotoPop = new PopupWindow(view, ViewGroup.LayoutParams.FILL_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            addPhotoPop.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        }
        WheelUtils.setPopeWindow(this, mainView, addPhotoPop);
        Button cameraAdd = (Button) view.findViewById(R.id.cameraadd);
        Button galleryAdd = (Button) view.findViewById(R.id.galleryadd);
        Button cancel = (Button) view.findViewById(R.id.cancel);

        cameraAdd.setOnClickListener(this);
        galleryAdd.setOnClickListener(this);
        cancel.setOnClickListener(this);
    }

    //相机
    public void photo() {
        storageFile=null;
        if(StorageUtils.isSDMounted()){
            try {
                storageFile = StorageUtils.createCameraFile();
                Uri uri = Uri.fromFile(storageFile);
                Intent intent = CropUtils.createPickForCameraIntent(uri);
                startActivityForResult(intent, TAKE_PICTURE);
            } catch (Exception e) {
                ToastUtil.showLong(this, "系统相机异常");
            }
        }else{
            SingleBtnFragmentDialog.showDefault(getSupportFragmentManager(), "sdcard未加载");
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TAKE_PICTURE:
                if ( resultCode == Activity.RESULT_OK) {
                    if(storageFile!=null){
                        ImageItem takePhoto = new ImageItem();
                        takePhoto.setImagePath(storageFile.getAbsolutePath());
                        ArrayList<ImageItem> list=Bimp.dataMap.get(DoctorCreateCaseActivity.class.getSimpleName());
                        list.add(takePhoto);
                    }
                }
                break;
//            case
            case TASK_SELECT_DATA:
                if ( resultCode == Activity.RESULT_OK) {
                    boundCaseItems();
                }
                break;
            case TASK_KEY_WORDS_DATA://返回关键词标签
                if ( resultCode == Activity.RESULT_OK) {
                    try {
                        systemTags= (ArrayList<Tag>) data.getSerializableExtra("result1");
//                        customerTags= (ArrayList<Tag>) data.getSerializableExtra("result2");
                        getTagArray();
                        keysLayout.removeAllViews();
                        for(int i=0;i<keysData.length();i++){
                            JSONObject object=keysData.getJSONObject(i);
                            Button btn=new Button(DoctorCreateCaseActivity.this);
                            btn.setText(object.optString("NAME"));
                            btn.setTextColor(getResources().getColor(R.color.color_text_gray));
                            btn.setGravity(Gravity.CENTER);
                            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            layoutParams.setMargins(6, 0, 6, 0);
                            btn.setLayoutParams(layoutParams);
                            btn.setBackgroundResource(R.drawable.btn_topic_label_bg);

                            keysLayout.addView(btn);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
                break;
            default://多选返回
                if(resultCode==Activity.RESULT_OK){
                    TextView multiText=multipleTexts.get(requestCode);
                    StringBuilder sb=new StringBuilder();
                    StringBuilder ids=new StringBuilder();
                    ArrayList<Map<String,Object>> res=(ArrayList<Map<String, Object>>) data.getSerializableExtra("result");
                    for(int i=0;i<res.size();i++){
                        if((Boolean) res.get(i).get("isChecked")){
                            ids.append(res.get(i).get("code").toString()+",");
                            sb.append(res.get(i).get("name")+"\t\t");
                        }
//					}else{
//						if((Boolean) res.get(i).get("isChecked")){
//							sb.append(+res.get(i).get("name"));
//						}
//					}
                    }
                    if(ids.length()>0)
                        ids.deleteCharAt(ids.length()-1);
                    selectItems.get(requestCode).SELECTION=ids.toString();
                    selectItems.get(requestCode).isEdited=true;
                    multiText.setText(sb.toString().trim());
                    multiText.setOnClickListener(new MultipleChoiseClickListener(res, data.getStringExtra("title"), requestCode));

                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //在每次跳转去添加病历项的时候先将用户已经填写的信息进行保存,保存在hashmap中
    private void setSaveDatas(){
        if(selectItems==null)
            return;
        for(int i=0;i<selectItems.size();i++){
            CaseItemEntity entity=selectItems.get(i);
            switch (entity.ITEMTYPE) {
                case 10://文本
                case 40://单数字
                case 80://带小数
                    EditText editresult=(EditText) contentLayout.getChildAt(i).findViewById(R.id.apt_case_template_item_edit_right);
                    String input=editresult.getText().toString().trim();
                    if(input!=null&&input.length()!=0){
                        entity.isEdited=true;
                        entity.INFO=input;
                    }
                    break;
                case 20://单选
                case 30://多选
                    break;
                case 50://区域文字
                    EditText editLeft=(EditText) contentLayout.getChildAt(i).findViewById(R.id.apt_case_template_item_edit_left);
                    EditText editRight=(EditText) contentLayout.getChildAt(i).findViewById(R.id.apt_case_template_item_edit_right);
                    String strLeft=editLeft.getText().toString().trim();
                    String strRight=editRight.getText().toString().trim();
                    if(strLeft!=null&&strLeft.length()!=0||strRight!=null&&strRight.length()!=0){
                        entity.isEdited=true;
                        entity.INFO=strLeft;
                        entity.INFO2=strRight;
                    }
                    break;
                case 60://日期
                    EditText tvdate=(EditText) contentLayout.getChildAt(i).findViewById(R.id.apt_case_template_item_edit_right);
                    String datestr=tvdate.getText().toString().trim();
                    if(datestr!=null&&datestr.length()!=0){
                        entity.isEdited=true;
                        entity.INFO=datestr;
                    }
                    break;
                case 70://大文本域
                    EditText bigeditresult=(EditText) contentLayout.getChildAt(i).findViewById(R.id.apt_case_template_item_text_edit_big);
                    String biginput=bigeditresult.getText().toString().trim();
                    if(biginput!=null&&biginput.length()!=0){
                        entity.isEdited=true;
                        entity.INFO=biginput;
                    }
                    break;

            }

        }
    }


    //将文件放入参数
    private void putFile(RequestParams params) {
        ArrayList<ImageItem> list=Bimp.dataMap.get(DoctorCreateCaseActivity.class.getSimpleName());//根据ID寻找
        for(int i=0;i<list.size();i++){
            int index=i+1;
            if(!(list.get(i).isNetPic)){//不是网络图片才上传
                try {
                    params.put(index+".jpg", BitmapUtils.onGetDecodeFileByPath(
                            DoctorCreateCaseActivity.this,list.get(i).getImagePath()));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 得到并且去验证用户输入的数据
     * @return  true表示可以上传   false 表示不可以上传
     */
    private boolean verifyData() {
        int[] location=new int[2];
        int[] scrLocation=new int[2];
        contentLayout.getLocationInWindow(scrLocation);
        System.out.println(isUploading+"--------");
        if(isUploading){
            return false;
        }
        isUploading=true;//正在上传
        if(editAddr.getText().toString().trim().length()>0){//用户输入才检验
            if(!StringFormatUtils.isPhoneNum(editAddr.getText().toString().trim())){
                ToastUtil.showShort("请输入正确的手机号");
                return false;
            }
        }
        if(editCodeNum.getText().toString().trim().length()>0){
            if(!StringFormatUtils.isIDCardNumber(editCodeNum.getText().toString().trim())){
                //身份证号码有误
                ToastUtil.showShort("请输入正确的身份证号码");
                return false;
            }
        }

        postJson.clear();
        try {
            for(int i=0;i<selectItems.size();i++){
                CaseItemEntity entity=selectItems.get(i);
                JSONObject postObject=new JSONObject();
                postJson.add(postObject);
                postObject.put("CLASSID", entity.CLASSID);
                postObject.put("ITEMID",entity.ITEMID);
                postObject.put("INFO2","");
                postObject.put("INFO","");
                postObject.put("NEFILL",entity.NEFILL);
                postObject.put("SPIC",entity.SPIC);
                postObject.put("SEQ",entity.SEQ);
                postObject.put("ITEMTYPE",entity.ITEMTYPE);
                postObject.put("SELECTION","");

                switch (entity.ITEMTYPE) {
                    case 10://文本
                    case 40://单数字
                    case 80://带小数
                        EditText editresult=(EditText) contentLayout.getChildAt(i).findViewById(R.id.apt_case_template_item_edit_right);
                        String input=editresult.getText().toString().trim();
                        if(input==null||"".equals(input)){
                            if(entity.NEFILL!=1)//不是必填,可以直接跳过判断
                                continue;
                            editresult.getLocationInWindow(location);
                            mScrolview.scrollTo(location[0], location[1] - scrLocation[1] - 100);
                            ToastUtil.showShort("请正确输入" + entity.ITEMNAME);
                            return false;
                        }
                        postObject.put("INFO",input);
                        break;
                    case 20://单选
                    case 30://多选
                        TextView tvRight=(TextView) contentLayout.getChildAt(i).findViewById(R.id.apt_case_template_item_text_right);
                        String singlestr=entity.SELECTION;
                        if(singlestr==null||"".equals(singlestr)){
                            if(entity.NEFILL!=1)//不是必填,可以直接跳过判断
                                continue;
                            tvRight.getLocationOnScreen(location);
                            mScrolview.scrollTo(location[0], location[1] - scrLocation[1] - 100);
                            ToastUtil.showShort("请正确输入"+entity.ITEMNAME);
                            return false;
                        }
                        postObject.put("SELECTION",entity.SELECTION);
                        break;
                    case 50://区域文字
                        EditText editLeft=(EditText) contentLayout.getChildAt(i).findViewById(R.id.apt_case_template_item_edit_left);
                        EditText editRight=(EditText) contentLayout.getChildAt(i).findViewById(R.id.apt_case_template_item_edit_right);
                        String strLeft=editLeft.getText().toString().trim();
                        String strRight=editRight.getText().toString().trim();
                        if(HStringUtil.isEmpty(strLeft)||HStringUtil.isEmpty(strRight)){//为空或空字符串
                            if(entity.NEFILL!=1)//不是必填,可以直接跳过判断
                                continue;
                            editLeft.getLocationInWindow(location);
                            mScrolview.scrollTo(location[0], location[1] - scrLocation[1] - 100);
                            ToastUtil.showShort("请正确输入"+entity.ITEMNAME);
                            return false;
                        }else{
                            postObject.put("INFO", strLeft);
                            postObject.put("INFO2", strRight);
                        }
                        break;
                    case 60://日期
                        EditText tvdate=(EditText) contentLayout.getChildAt(i).findViewById(R.id.apt_case_template_item_edit_right);
                        String datestr=tvdate.getText().toString().trim();
                        if(datestr==null||"".equals(datestr)){
                            if(entity.NEFILL!=1)//不是必填,可以直接跳过判断
                                continue;
                            tvdate.getLocationInWindow(location);
                            mScrolview.scrollTo(location[0], location[1] - scrLocation[1] - 100);
                            ToastUtil.showShort("请正确输入"+entity.ITEMNAME);
                            return false;
                        }else{
                            postObject.put("INFO", datestr);
                        }
                        break;
                    case 70://大文本域
                        EditText bigeditresult=(EditText) contentLayout.getChildAt(i).findViewById(R.id.apt_case_template_item_text_edit_big);
                        String biginput=bigeditresult.getText().toString().trim();
                        if(biginput==null||"".equals(biginput)){
                            if(entity.NEFILL!=1)//不是必填,可以直接跳过判断
                                continue;
                            bigeditresult.getLocationInWindow(location);
                            mScrolview.scrollTo(location[0], location[1] - scrLocation[1]-100);
                            ToastUtil.showShort("请正确输入"+entity.ITEMNAME);
                            return false;
                        }else{
                            postObject.put("INFO", biginput);
                        }
                        break;
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return true;
    }

    private void boundCaseItems() {
        selectItems.clear();
        for(int i=0;i<itemsList.size();i++){
            if(itemsList.get(i).isChecked){
                selectItems.add(itemsList.get(i));
            }
        }
        Collections.sort(selectItems, new CaseItemComparator());
        boundCaseItems2(selectItems);

    }

    /**
     * 绑定LinearLayout数据
     */
    private void boundCaseItems2(ArrayList<CaseItemEntity> datas) {
        contentLayout.removeAllViews();
//        btnComplate.setVisibility(View.VISIBLE);
        multipleTexts=new HashMap<Integer, TextView>();
        for(int i=0;i<datas.size();i++){
            final int index=i;
            final CaseItemEntity entity=datas.get(i);
            View itemView=LayoutInflater.from(DoctorCreateCaseActivity.this).inflate( R.layout.apt_consultion_case_item_text, null,true);
            TextView tvCategoryTitle=(TextView) itemView.findViewById(R.id.apt_case_template_item_title);
            TextView tvEditLeft=(TextView) itemView.findViewById(R.id.apt_case_template_item_name);
            final TextView tvRight=(TextView) itemView.findViewById(R.id.apt_case_template_item_text_right);//右边文本,
            TextView tvStar=(TextView) itemView.findViewById(R.id.apt_case_template_item_text_star);//必填选填标记
            final EditText editText=(EditText) itemView.findViewById(R.id.apt_case_template_item_edit_right);//右边的右输入框
            EditText editTextLeft=(EditText) itemView.findViewById(R.id.apt_case_template_item_edit_left);//右边的左输入框
            LinearLayout horLayout=(LinearLayout) itemView.findViewById(R.id.apt_case_template_item_text_layout);
            EditText bigEditText=(EditText) itemView.findViewById(R.id.apt_case_template_item_text_edit_big);//下面大输入框
            LinearLayout imgLayout=(LinearLayout) itemView.findViewById(R.id.apt_case_template_item_img_layout);
//            LinearLayout images=(LinearLayout) itemView.findViewById(R.id.apt_case_template_item_images);
            final Button imgAdd=(Button) itemView.findViewById(R.id.apt_case_template_item_img_add);

            if(i==0){//第一个一定是开始,显示CLASSNAME
                tvCategoryTitle.setVisibility(View.VISIBLE);
                tvCategoryTitle.setText(entity.CLASSNAME);//分类名称
            }else{//后面的家判断是否显示CLASSNAME
                CaseItemEntity entity2=datas.get(i-1);
                if(!(entity2.CLASSID==entity.CLASSID)){//分类开始
                    tvCategoryTitle.setVisibility(View.VISIBLE);
                    tvCategoryTitle.setText(entity.CLASSNAME);//分类名称
                }
            }
            if(entity.NEFILL==1){//必填
                tvStar.setVisibility(View.VISIBLE);
            }
            switch (entity.ITEMTYPE) {
                case 10://文字填写
                    tvEditLeft.setText(entity.ITEMNAME);
                    editText.setVisibility(View.VISIBLE);
                    tvRight.setVisibility(View.GONE);
                    if(entity.isEdited)
                        editText.setText(entity.INFO);
                    break;
                case 20://单选

                    tvEditLeft.setText(entity.ITEMNAME);
                    tvRight.setVisibility(View.VISIBLE);
                    tvRight.setHint(DoctorCreateCaseActivity.this.getResources().getString(R.string.please_choise));
                    editText.setVisibility(View.GONE);
                    final ArrayList<Map<String, String>> selectors= JsonParseUtils.parseTemplateItemData(entity.OPTION);
                    tvRight.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            setSingleSelector(selectors, tvRight, index,false);
                        }
                    });
                    HashMap<String, String> keyMap=new HashMap<String, String>();
                    if(entity.isEdited){
                        try {
                            JSONArray option=new JSONArray(entity.OPTION);
                            for(int l=0;l<option.length();l++){
                                JSONObject optionObject=option.getJSONObject(l);
                                keyMap.put(""+optionObject.optInt("OPTIONID"), optionObject.optString("OPTIONNAME"));
                            }
                            tvRight.setText(keyMap.get(entity.SELECTION));
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                    }

                    break;
                case 30://多选

                    tvEditLeft.setText(entity.ITEMNAME);
                    tvRight.setVisibility(View.VISIBLE);
                    tvRight.setHint(DoctorCreateCaseActivity.this.getResources().getString(R.string.please_choise));
                    editText.setVisibility(View.GONE);
                    multipleTexts.put(index, tvRight);
                    bigEditText.setFocusable(false);
                    bigEditText.setEnabled(false);
                    ArrayList<Map<String, Object>> list=new ArrayList<Map<String,Object>>();
                    if(entity.isEdited){
                        String selectStr2="";
                        String[] keys2=entity.SELECTION.split(",");
                        try {
                            JSONArray option=new JSONArray(entity.OPTION);
                            for(int l=0;l<option.length();l++){
                                JSONObject object=option.getJSONObject(l);
                                HashMap< String, Object> map=new HashMap<String, Object>();
                                map.put("name", object.optString("OPTIONNAME"));
                                map.put("code", ""+object.optInt("OPTIONID"));
                                map.put("isChecked", false);
                                list.add(map);
                            }
                            if(!"".equals(keys2[0])){
                                for(int m=0;m<keys2.length;m++){
                                    for(int n=0;n<list.size();n++){
                                        if(Integer.parseInt((String)list.get(n).get("code"))==Integer.parseInt(keys2[m])){
                                            if(m==0)
                                                selectStr2=(String)list.get(n).get("name");
                                            else
                                                selectStr2=selectStr2+"，"+(String)list.get(n).get("name");
                                            list.get(n).put("isChecked", true);
                                        }
                                    }
                                }
                            }
                            tvRight.setText(selectStr2);
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                    }else{
                        list=JsonParseUtils.parseMultipleChoiseData(entity.OPTION);
                    }
                    tvRight.setOnClickListener(new MultipleChoiseClickListener(list,entity.ITEMNAME, index));
                    break;
                case 40://单数字填写
                    tvEditLeft.setText(entity.ITEMNAME);
                    editText.setVisibility(View.VISIBLE);
                    editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                    tvRight.setVisibility(View.GONE);
                    if(entity.isEdited)
                        editText.setText(entity.INFO);
                    break;
                case 50://区域数字填写90~100
                    tvEditLeft.setText(entity.ITEMNAME);
                    editText.setVisibility(View.VISIBLE);
                    editTextLeft.setVisibility(View.VISIBLE);
                    editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                    tvRight.setVisibility(View.VISIBLE);
                    tvRight.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    tvRight.setText("~");
                    tvRight.setBackgroundColor(Color.TRANSPARENT);
                    tvRight.setTextColor(getResources().getColor(R.color.color_blue));
                    if(entity.isEdited){
                        editTextLeft.setText(entity.INFO);
                        editText.setText(entity.INFO2);
                    }
                    break;
                case 60://日期
                    tvEditLeft.setText(entity.ITEMNAME);
                    editText.setVisibility(View.VISIBLE);
                    editText.setHint(DoctorCreateCaseActivity.this.getResources().getString(R.string.please_choise));
                    editText.setFocusable(false);
//				editText.setEnabled(false);
//				editText.setVisibility(View.GONE);
                    editText.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            dateEdit=editText;
                            setAgeDate(index);
                        }
                    });
                    if(entity.isEdited)
                        editText.setText(entity.INFO);
                    break;
                case 70://大文本域填写
                    tvEditLeft.setText(entity.ITEMNAME);
                    tvRight.setVisibility(View.GONE);
                    editText.setVisibility(View.GONE);
                    bigEditText.setVisibility(View.VISIBLE);
                    bigEditText.setHint(getResources().getString(R.string.please_input)+entity.ITEMNAME);
                    bigEditText.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            v.getParent().requestDisallowInterceptTouchEvent(true);
                            return false;
                        }
                    });
                    if(entity.isEdited)
                        bigEditText.setText(entity.INFO);
                    break;
                case 80://有小数点的情况
                    tvEditLeft.setText(entity.ITEMNAME);
                    editText.setVisibility(View.VISIBLE);
                    editText.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL);
                    editText.addTextChangedListener(new TextWatcher() {

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                        }
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count,
                                                      int after) {
                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                        }
                    });
                    tvRight.setVisibility(View.GONE);
                    if(entity.isEdited)
                        editText.setText(entity.INFO);
                    break;
                case 90:
                    tvEditLeft.setText(entity.ITEMNAME);
                    tvRight.setVisibility(View.GONE);
                    editText.setVisibility(View.GONE);
                    break;
            }
            contentLayout.addView(itemView);
        }

    }

    /**
     * 设置单选滑动选择器
     *
     * @param list
     * @param tv
     * @param isSex  如果是单选性别则只是显示选择的结果,不放入上传数据
     */
    private void setSingleSelector(final ArrayList<Map<String,String>> list,final TextView tv,final int index,final boolean isSex){
        SystemUtils.hideSoftAnyHow(DoctorCreateCaseActivity.this);
        if(mPopupWindow != null && mPopupWindow.isShowing())mPopupWindow.dismiss();
        mPopupWindow = WheelUtils.showSingleWheel(DoctorCreateCaseActivity.this,list,tv,new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int index1 = (Integer)v.getTag(R.id.wheel_one);
                Map<String,String> map = list.get(index1);
                String name = map.get("name");
                if(!isSex){
                    selectItems.get(index).SELECTION=map.get("code");
                    selectItems.get(index).isEdited=true;
                }
                tv.setText(name);
            }
        });
    }

    /**
     * 设置出生日期
     */
    private void setAgeDate(final int index) {
        if(agepop == null ){
            agepop=WheelUtils.showThreeDateWheel(DoctorCreateCaseActivity.this, contentLayout, this);
        }else if(agepop.isShowing()){
            agepop.dismiss();
        }else{
//			agepop.showAsDropDown(view);
            agepop.showAtLocation(contentLayout, Gravity.BOTTOM, 0, 0);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        update();
    }


    //更新显示图片
    private void update() {
        imgLayout.removeAllViews();
        ArrayList<ImageItem> list=Bimp.dataMap.get(DoctorCreateCaseActivity.class.getSimpleName());
        for(int i=0;i<list.size();i++){
            final int index=i;
            MessageImageView image=new MessageImageView(DoctorCreateCaseActivity.this);
            image.setLayoutParams(new ViewGroup.LayoutParams(DensityUtils.dip2px(DoctorCreateCaseActivity.this, 78), DensityUtils.dip2px(DoctorCreateCaseActivity.this, 78)));
            image.setScaleType(ImageView.ScaleType.CENTER_CROP);
            ImageItem ii=list.get(index);
            if(ii.isNetPic)//如果是网络图片
                mImageLoader.displayImage(list.get(i).thumbnailPath, image.getImage());
            else
                image.setImageBitmap(list.get(i).getBitmap());
            image.setDeleteListener(new View.OnClickListener() {//删除图片

                @Override
                public void onClick(View v) {

                    Bimp.dataMap.get(DoctorCreateCaseActivity.class.getSimpleName()).remove(index);
                    update();
                }
            });
            image.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(DoctorCreateCaseActivity.this,GalleryActivity.class);
                    intent.putExtra("key", DoctorCreateCaseActivity.class.getSimpleName());
                    intent.putExtra("position", "1");
                    intent.putExtra("ID", index);
                    startActivity(intent);
                }
            });
            imgLayout.addView(image);
        }
    }


    /**
     * 作用是:多选跳转的点击监听器
     */
    class MultipleChoiseClickListener implements View.OnClickListener {
        ArrayList<Map<String,Object>> list;
        String name;
        int index;

        public MultipleChoiseClickListener(ArrayList<Map<String,Object>> list,String name, int index) {
            super();
            this.list = list;
            this.name=name;
            this.index = index;
        }

        @Override
        public void onClick(View v) {
            Intent intent=new Intent(DoctorCreateCaseActivity.this,TemplateItemMultipleChoiceActivity.class);
            intent.putExtra("list", list);
            intent.putExtra("title", name);
            startActivityForResult(intent,index);
        }
    }

    @Override
    public void onBackPressed() {
        if(itemsList!=null)
            itemsList.clear();
        super.onBackPressed();
    }

    private void getTagArray(){
        keysData= new JSONArray();
        try {
            for (int i=0;i<systemTags.size();i++){
                JSONObject object=new JSONObject();
                if(systemTags.get(i).getsID()==-2)
                    object.put("ID","");//是患者自定义的标签
                else
                    object.put("ID",""+systemTags.get(i).getsID());
                object.put("NAME",systemTags.get(i).getTitle());
                keysData.put(object);
            }
//            for (int i=0;i<customerTags.size();i++){
//                JSONObject object=new JSONObject();
//                if(customerTags.get(i).getsID()==-2)
//                    object.put("ID","");//是患者自定义的标签
//                else
//                    object.put("ID",""+customerTags.get(i).getsID());
//                object.put("NAME",customerTags.get(i).getTitle());
//                keysData.put(object);
//            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
