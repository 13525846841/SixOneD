package com.yksj.consultation.sonDoc.consultation;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.library.base.base.BaseActivity;
import com.yksj.consultation.adapter.AdtCommonTools;
import com.yksj.consultation.sonDoc.R;
import com.yksj.healthtalk.net.http.AsyncHttpResponseHandler;
import com.yksj.healthtalk.net.http.ApiService;
import com.yksj.healthtalk.utils.ToastUtil;

import org.handmark.pulltorefresh.library.PullToRefreshBase;
import org.handmark.pulltorefresh.library.PullToRefreshListView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by HEKL on 2015/7/8.
 * Used for 常用工具_
 */
public class DAtyCommonTools extends BaseActivity implements View.OnClickListener, PullToRefreshBase.OnRefreshListener2<ListView> {
    private PullToRefreshListView mRefreshableView;
    private ListView mListView;
    private View mEmptyView;
    private AdtCommonTools mAdapter;
    private List<JSONObject> list = null;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.aty_commontools);
        initView();
    }

    private void initView() {
        initializeTitle();
        titleTextV.setText("常用工具");
        titleLeftBtn.setOnClickListener(this);
        list = new ArrayList<JSONObject>();
        mEmptyView = findViewById(R.id.load_faile_layout);
        initDate();
        mRefreshableView = (PullToRefreshListView) findViewById(R.id.pull_refresh_listview);
        mEmptyView = findViewById(R.id.load_faile_layout);
        mListView = mRefreshableView.getRefreshableView();
        mRefreshableView.setOnRefreshListener(this);
        mAdapter = new AdtCommonTools(list, this);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), DAtyCommonTool.class);
                String strUrl = list.get(position - 1).optString("TOOL_URL");
                String strName = list.get(position - 1).optString("TOOL_NAME");
                intent.putExtra("URL", strUrl);
                intent.putExtra("NAME", strName);
                startActivity(intent);
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_back:
                onBackPressed();
                break;
        }
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
        list.clear();
        initDate();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
        list.clear();
        initDate();
    }

    private void initDate() {
        ApiService.doHttpCommonTools(new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                mRefreshableView.setRefreshing();
                super.onStart();
            }

            @Override
            public void onFinish() {
                mRefreshableView.onRefreshComplete();
                super.onFinish();
            }

            @Override
            public void onSuccess(String content) {
                super.onSuccess(content);
                try {
                    JSONObject obj = new JSONObject(content);
                    if ("1".equals(obj.optString("code"))) {
                        JSONArray array = obj.getJSONArray("result");
                        int count = array.length();
                        JSONObject object;
                        if (count == 0) {
                            mEmptyView.setVisibility(View.VISIBLE);
                        } else {
                            for (int i = 0; i < count; i++) {
                                object = array.getJSONObject(i);
                                list.add(object);
                            }
                        }
                    } else {
                        ToastUtil.showShort(obj.optString("message"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }

}
