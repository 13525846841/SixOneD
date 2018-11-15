package com.yksj.consultation.sonDoc.dossier;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.library.base.base.BaseActivity;
import com.yksj.consultation.adapter.HistoryListAdapter;
import com.yksj.consultation.sonDoc.R;
import com.yksj.healthtalk.utils.SharePreHelper;
import com.yksj.healthtalk.utils.ToastUtil;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 搜索病历
 * Created by zheng on 2015/7/9.
 */
public class AtyDossierSearch extends BaseActivity implements View.OnClickListener, HistoryListAdapter.OnClickDeleteHistoryListener, AdapterView.OnItemClickListener {

    private EditText editSearch;//组内搜索
    TextView tvClear;
    ListView historyList;
    private ArrayList<HashMap<String, String>> history;//搜索历史
    private HistoryListAdapter historyAdapter;//搜索历史适配器
    private boolean isSearch = false;//是否是在搜索
    private String searchStr = "";//查询字符串
    private String keyName;
    private Intent intent;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.search_dossier_layout);
        initUI();
    }

    @Override
    protected void onStart() {
        super.onStart();
        showHistory();
    }

    private void initUI() {
        initializeTitle();
        keyName = getIntent().getClass().getName();
        titleTextV.setOnClickListener(this);
        titleLeftBtn.setOnClickListener(this);
        editSearch = (EditText) findViewById(R.id.edit_search_top);
        titleTextV.setText("搜索病历");//全部
        editSearch.setHint("请输入病历名称");
        tvClear = (TextView) findViewById(R.id.search_expert_clear_history);
        historyList = (ListView) findViewById(R.id.search_expert_history);
        tvClear.setOnClickListener(this);
        history = new ArrayList<HashMap<String, String>>();
        historyAdapter = new HistoryListAdapter(AtyDossierSearch.this, history);
        historyAdapter.setDeleteListener(this);
        historyList.setOnItemClickListener(this);
        historyList.setAdapter(historyAdapter);
        initSearch();

    }

    private void initSearch() {
        editSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String text = editSearch.getText().toString().trim();
                    if (text != null) {
                        SharePreHelper.saveSearchHistory(AtyDossierSearch.this, keyName, text);
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(editSearch.getWindowToken(), 0);//关闭软键盘
                        searchStr = text;
                        intent = new Intent(AtyDossierSearch.this, AtyDossierSearchResult.class);
                        intent.putExtra("SEARCHTEXT", searchStr);
                        startActivity(intent);
                        isSearch = true;
                    } else {
                        ToastUtil.showShort("请输入标签名称");
                    }
                    handled = true;
                }
                return handled;
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_lable:
                break;
            case R.id.title_back:
                setResult(RESULT_OK, getIntent());
                finish();
                break;
            case R.id.search_expert_clear_history:
                SharePreHelper.clearSearchHistory(this, keyName);
                historyList.setVisibility(View.GONE);
                break;
        }
    }


    @Override
    public void onDeleteItem(int pos) {
        history.remove(pos);
        StringBuilder sb = new StringBuilder("");
        for (int i = 0; i < history.size(); i++) {
            sb.append(history.get(i).get("name") + ",");

        }
        String result = sb.toString();
        if (history.size() > 0) {
            result = result.substring(0, sb.length() - 1);
        }
        SharePreHelper.saveResultHistory(AtyDossierSearch.this, keyName, result);
        showHistory();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        intent = new Intent(AtyDossierSearch.this, AtyDossierSearchResult.class);
        intent.putExtra("SEARCHTEXT", history.get(i).get("name"));
        startActivity(intent);
        isSearch = true;
    }

    private void showHistory() {
        history.clear();
        history.addAll(SharePreHelper.getSearchHistory(this, keyName));
        if (history.size() != 0) {
            historyList.setVisibility(View.VISIBLE);
            historyAdapter.notifyDataSetChanged();
        } else {
            historyList.setVisibility(View.GONE);
            return;
        }
    }
}
