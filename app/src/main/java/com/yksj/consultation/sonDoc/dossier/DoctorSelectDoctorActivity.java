package com.yksj.consultation.sonDoc.dossier;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.library.base.base.BaseActivity;
import com.yksj.consultation.adapter.PatientSortAdapter;
import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.app.AppContext;
import com.yksj.healthtalk.entity.PersonEntity;
import com.yksj.healthtalk.net.http.ApiService;
import com.yksj.healthtalk.net.http.AsyncHttpResponseHandler;
import com.yksj.healthtalk.net.http.RequestParams;
import com.yksj.healthtalk.net.socket.SmartFoxClient;
import com.yksj.healthtalk.function.sortlistview.CharacterParser;
import com.yksj.healthtalk.function.sortlistview.PinyinComparator;
import com.yksj.healthtalk.function.sortlistview.SideBar;
import com.yksj.healthtalk.utils.ClearEditText;
import com.yksj.healthtalk.utils.ToastUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * 医生创建病理时  选择患者界面
 * Created by lmk on 2015/7/8.
 */
public class DoctorSelectDoctorActivity extends BaseActivity implements View.OnClickListener{

    CharacterParser characterParser;
    /**
     * 根据拼音来排列ListView里面的数据类
     */
    private PinyinComparator pinyinComparator;
    private ListView sortListView;
    private SideBar sideBar;
    private PatientSortAdapter mAdapter;
    private ClearEditText editSer;
    private String searchStr="";
    private boolean isSearch=false;
    private String patientId;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.aty_doc_select_patient);
        patientId=getIntent().getStringExtra("patientId");
        initView();
        initData();
    }

    //加载六一健康资深专家列表
    private void initData() {//GROUPCONSULTATIONLIST200
        //http://220.194.46.204:8080/DuoMeiHealth/GroupConsultationList200?
        //TYPE=consultationCenterDoctorList&CONSULTATION_CENTER_ID=1&PAGESIZE=1&PAGENUM=20&CUSTOMERID=3783&V
        RequestParams params=new RequestParams();
        params.put("TYPE","consultationCenterDoctorList");
        params.put("CUSTOMERID", SmartFoxClient.getLoginUserId());
        params.put("CONSULTATION_CENTER_ID", AppContext.APP_CONSULTATION_CENTERID);
        params.put("PAGESIZE","1");//这两个参数传了也不用
        params.put("PAGENUM","100");
        params.put("VALID_MARK", "40");
        params.put("NAME", searchStr);
        ApiService.doHttpFINDMYPatients(params, new AsyncHttpResponseHandler() {

            @Override
            public void onFailure(Throwable error, String content) {
                super.onFailure(error, content);
            }

            @Override
            public void onSuccess(String content) {

                super.onSuccess(content);
                ArrayList<PersonEntity> data=onParseData(content);
                if(data==null){
                    ToastUtil.showShort("加载失败");
                }else if(data.size()!=0){
                    // 根据a-z进行排序源数据
//                    Collections.sort(data, pinyinComparator);
                    mAdapter.updateListView(data);
                }else{
                    ToastUtil.showShort("没有该专家");
                    mAdapter.updateListView(data);
                }

            }
        });

    }

    private void initView() {
        initializeTitle();
        titleTextV.setText(R.string.select_expert);
        titleLeftBtn.setOnClickListener(this);
        //实例化汉字转拼音类
        characterParser = CharacterParser.getInstance();
        editSer=(ClearEditText) findViewById(R.id.edit_search_top);
        editSer.setHint("请输入专家姓名/账号");
        pinyinComparator = new PinyinComparator();
        mAdapter=new PatientSortAdapter(this);
        sortListView= (ListView) findViewById(R.id.select_person_listview);
        sideBar = (SideBar) findViewById(R.id.select_persion_sidrbar);
        //设置右侧触摸监听
        sideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {

            @Override
            public void onTouchingLetterChanged(String s) {
                //该字母首次出现的位置
                int position = mAdapter.getPositionForSection(s.charAt(0));
                if(position != -1){
                    sortListView.setSelection(position);
                }

            }
        });
        sortListView.setAdapter(mAdapter);
//        dialog = (TextView) findViewById(R.id.dialog);

        sortListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PersonEntity pe=mAdapter.datas.get(position);
                Intent intent=new Intent(DoctorSelectDoctorActivity.this,DoctorCreateCaseActivity.class);
                intent.putExtra("patientId",patientId);
                intent.putExtra("doctorId",pe.getCustomerId());
                startActivity(intent);
            }
        });
        editSer.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String text = editSer.getText().toString().trim();
                    if (text != null) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(editSer.getWindowToken(), 0);//关闭软键盘
                        searchStr = text;
                        isSearch = true;
                        initData();
                    } else {
                        ToastUtil.showShort(getString(R.string.please_input_name_or_account));
                    }
                    handled = true;
                }
                return handled;
            }
        });
        editSer.setClearLinsener(new ClearEditText.ClearEditTextLinsener() {
            @Override
            public void onEditClear() {
                if("".equals(searchStr))
                    return;
                searchStr = "";
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
//                if(imm.isActive())
//                    imm.hideSoftInputFromWindow(editSer.getWindowToken(), 0) ;//关闭软键盘
                initData();
            }
        });


    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.title_back:
                onBackPressed();
                break;
        }
    }



    private ArrayList<PersonEntity> onParseData(String content){
        ArrayList<PersonEntity> array=null;
        try {
            JSONObject dataObject=new JSONObject(content);
            JSONArray list=dataObject.getJSONArray("consultationCenterDoctorList");
            array=new ArrayList<PersonEntity>();
            for(int i=0;i<list.length();i++){
                JSONObject object=list.getJSONObject(i);
                PersonEntity pe=new PersonEntity();
                pe.setCustomerNickname(object.optString("customerNickname"));
                pe.setBigIconBackground(object.optString("bigIconBackground"));
                pe.setCustomerAccounts(object.optString("customerAccounts"));
                pe.setCustomerId(object.optString("customerId"));
                pe.setCustomerSex(object.optInt("customerSex"));
                //汉字转换成拼音
                String pinyin = characterParser.getSelling(object.optString("customerNickname"));
                String sortString = pinyin.substring(0, 1).toUpperCase();
                // 正则表达式，判断首字母是否是英文字母
                if(sortString.matches("[A-Z]")){
                    pe.setSortLetters(sortString.toUpperCase());
                }else{
                    pe.setSortLetters("#");
                }
                array.add(pe);

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return array;
    }
}
