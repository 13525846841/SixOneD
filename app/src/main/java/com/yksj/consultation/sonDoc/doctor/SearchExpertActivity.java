package com.yksj.consultation.sonDoc.doctor;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.yksj.consultation.adapter.HistoryListAdapter;
import com.library.base.base.BaseActivity;
import com.yksj.consultation.sonDoc.R;
import com.yksj.healthtalk.utils.SharePreHelper;
import com.yksj.healthtalk.utils.ToastUtil;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 搜索专家界面
 * Created by lmk on 2015/9/15.
 */
public class SearchExpertActivity extends BaseActivity implements View.OnClickListener,
        HistoryListAdapter.OnClickDeleteHistoryListener{

    TextView tvClear,tvCancel;
    ListView historyList;
    EditText editSearch;
    private ArrayList<HashMap<String, String>> history;//搜索历史
    private HistoryListAdapter historyAdapter;//搜索历史适配器
    private String keyName;
    private String officeCode,consultId,pid,officeName;//科室ID  会诊ID  患者ID
    private int goalType=0;
    private LinearLayout searchLayout;


    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.aty_search_expert);
        keyName=getIntent().getClass().getName();
        if (getIntent().hasExtra("OFFICENAME")){
            officeName=getIntent().getStringExtra("OFFICENAME");
        }
        if (getIntent().hasExtra("OFFICECODE")){
            officeCode= getIntent().getStringExtra("OFFICECODE");
        }
        consultId=getIntent().getStringExtra("consultId");
        goalType=getIntent().getIntExtra("goalType", 0);
        pid=getIntent().getStringExtra("PID");
        initView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        showHistory();
    }

    private void showHistory() {
        history.clear();
        history.addAll(SharePreHelper.getSearchHistory(this, keyName));
        if(history.size()!=0){
            historyList.setVisibility(View.VISIBLE);
            findViewById(R.id.search_expert_clear_history).setVisibility(View.VISIBLE);
            historyAdapter.notifyDataSetChanged();
        }else{
            historyList.setVisibility(View.GONE);
            return;
        }
    }

    private void initView() {
        tvClear= (TextView) findViewById(R.id.search_expert_clear_history);
        tvCancel= (TextView) findViewById(R.id.cancel_onclick);
        historyList= (ListView) findViewById(R.id.search_expert_history);
        editSearch= (EditText) findViewById(R.id.seach_text);
        editSearch.setHint(R.string.search_expert_hint);
        tvClear.setOnClickListener(this);
        tvCancel.setOnClickListener(this);
        searchLayout= (LinearLayout) findViewById(R.id.include_search);
        searchLayout.setBackgroundResource(R.color.color_blue);
        history=new ArrayList<HashMap<String,String>>();
        historyAdapter=new HistoryListAdapter(SearchExpertActivity.this,history);
        historyAdapter.setDeleteListener(this);
        historyList.setAdapter(historyAdapter);
        editSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String text = editSearch.getText().toString().trim();
                    if (text != null && text.length() != 0) {
                        SharePreHelper.saveSearchHistory(SearchExpertActivity.this, keyName, text);
                        editSearch.setText("");
                        Intent intent = new Intent(SearchExpertActivity.this, SearchExpertResultActivity.class);
                        intent.putExtra("result", text);
                        intent.putExtra("OFFICECODE", officeCode);
                        intent.putExtra("OFFICENAME", officeName);
                        intent.putExtra("consultId", consultId);
                        intent.putExtra("PID", pid);
                        intent.putExtra("goalType", goalType);
                        startActivity(intent);
                        handled = true;
                    } else {
                        ToastUtil.showShort(getString(R.string.inputThemeName));
                    }
                }
                return handled;
            }
        });
        historyList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                editSearch.setText("");
                Intent intent = new Intent(SearchExpertActivity.this, SearchExpertResultActivity.class);
                intent.putExtra("result", history.get(position).get("name"));
                intent.putExtra("OFFICECODE", officeCode);
                intent.putExtra("OFFICENAME", officeName);
                intent.putExtra("consultId", consultId);
                intent.putExtra("PID", pid);
                intent.putExtra("goalType", goalType);
                startActivity(intent);
            }
        });

    }

    @Override
    public void onClick(View v) {
        Intent intent=null;
        switch (v.getId()){
            case R.id.search_expert_clear_history:
                ToastUtil.showShort(this, "清空历史");
                SharePreHelper.clearSearchHistory(this, keyName);
                historyList.setVisibility(View.GONE);

                break;
            case R.id.cancel_onclick:
                onBackPressed();
                break;

        }
    }

    @Override
    public void onDeleteItem(int pos) {
        history.remove(pos);
        StringBuilder sb=new StringBuilder("");
        for(int i=0;i<history.size();i++){
            sb.append(history.get(i).get("name")+",");

        }
        String result=sb.toString();
        if(history.size()>0){
            result=result.substring(0, sb.length()-1);
        }
        SharePreHelper.saveResultHistory(SearchExpertActivity.this,keyName,result);
        showHistory();
    }
}
