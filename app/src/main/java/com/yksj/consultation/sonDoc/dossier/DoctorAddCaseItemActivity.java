package com.yksj.consultation.sonDoc.dossier;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.library.base.base.BaseActivity;
import com.yksj.consultation.adapter.CaseItemChoiceAdapter;
import com.yksj.consultation.sonDoc.R;
import com.yksj.healthtalk.net.http.ApiService;
import com.yksj.healthtalk.net.http.AsyncHttpResponseHandler;
import com.yksj.healthtalk.net.http.RequestParams;
import com.yksj.healthtalk.function.sortlistview.CharacterParser;
import com.yksj.healthtalk.function.sortlistview.PinyinComparator;
import com.yksj.healthtalk.function.sortlistview.SideBar;
import com.yksj.healthtalk.utils.ClearEditText;
import com.yksj.healthtalk.utils.JsonParseUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 基层医生主动创建病历添加病历项界面
 * Created by lmk on 2015/7/9.
 */
public class DoctorAddCaseItemActivity extends BaseActivity implements View.OnClickListener{

    JSONArray backArray;
    /**
     * 根据拼音来排列ListView里面的数据类
     */
    private PinyinComparator pinyinComparator;
    CharacterParser characterParser;
    private ListView mListView;
    private CaseItemChoiceAdapter mAdapter;
    private ArrayList<Map<String,Object>> datas;
    private SideBar sideBar;
    private ClearEditText mClearEditText;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.aty_case_add_item_multi_choise);
        initView();
        if(DoctorCreateCaseActivity.itemsList!=null&&DoctorCreateCaseActivity.itemsList.size()>0){
//            datas= (ArrayList<Map<String, Object>>) getIntent().getSerializableExtra("selected");
//            onBountData();
            mAdapter.addAll(DoctorCreateCaseActivity.itemsList);
        }else{
            initData();
        }


    }

    //加载数据
    private void initData() {
        //GetContentMRTServlet?OPTION=9&CUSTID=4358
        RequestParams params=new RequestParams();
        params.put("OPTION", "7");
        ApiService.doHttpConsultionGetContent(params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(String content) {
                super.onSuccess(content);
                try {
                    JSONObject jsonObject = new JSONObject(content);
                    DoctorCreateCaseActivity.itemsList=JsonParseUtils.getCaseItemDatas(characterParser,jsonObject.getJSONArray("CONTENT"));
                    mAdapter.addAll(DoctorCreateCaseActivity.itemsList);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }

//    private void onBountData() {
//        backArray = new JSONArray();
//        datas = new ArrayList<Map<String, Object>>();
//        try {
//            for (int i = 0; i < DoctorCreateCaseActivity.itemsArray.length(); i++) {
//                JSONObject obj = DoctorCreateCaseActivity.itemsArray.getJSONObject(i);
//                HashMap<String, Object> map = new HashMap<String, Object>();
//                map.put("name", obj.optString("CLASSNAME"));
//                map.put("isChecked", false);
//                datas.add(map);
//            }
//            mAdapter.addAll(datas);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }

    private void initView() {
        initializeTitle();
        titleLeftBtn.setOnClickListener(this);
        titleTextV.setText(R.string.add_case_item);
        mClearEditText= (ClearEditText) findViewById(R.id.edit_search_top);
        //实例化汉字转拼音类
        characterParser = CharacterParser.getInstance();
        pinyinComparator = new PinyinComparator();
        mListView= (ListView) findViewById(R.id.multiple_choise_listview);
        mAdapter=new CaseItemChoiceAdapter(this);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                mAdapter.itemCheck(position);
            }
        });
        sideBar = (SideBar) findViewById(R.id.select_persion_sidrbar);
        //设置右侧触摸监听
        sideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {

            @Override
            public void onTouchingLetterChanged(String s) {
                //该字母首次出现的位置
                int position = mAdapter.getPositionForSection(s.charAt(0));
                if (position != -1) {
                    mListView.setSelection(position);
                }

            }
        });
        //根据输入框输入值的改变来过滤搜索
        mClearEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //当输入框里面的值为空，更新为原来的列表，否则为过滤数据列表
                filterData(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
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

    @Override
    public void onBackPressed() {
        Intent d=new Intent();
        d.putExtra("result",datas);
        setResult(RESULT_OK,d);
        finish();
        super.onBackPressed();

    }

    /**
     * 根据输入框中的值来过滤数据并更新ListView
     * @param filterStr
     */
    private void filterData(String filterStr){
        List<CaseItemEntity> filterDateList = new ArrayList<CaseItemEntity>();

        if(TextUtils.isEmpty(filterStr)){
            filterDateList = DoctorCreateCaseActivity.itemsList;
        }else{
            filterDateList.clear();
            for(CaseItemEntity sortModel : mAdapter.datas){
                String name = sortModel.ITEMNAME;
                if(name.indexOf(filterStr.toString()) != -1 || characterParser.getSelling(name).startsWith(filterStr.toString())){
                    filterDateList.add(sortModel);
                }
            }
        }

        // 根据a-z进行排序
        Collections.sort(filterDateList, pinyinComparator);
        mAdapter.removeAll();
        mAdapter.addAll(filterDateList);
    }

}
