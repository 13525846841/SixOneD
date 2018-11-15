/**
 *
 */
package com.yksj.consultation.sonDoc.consultation;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.library.base.base.BaseActivity;
import com.yksj.consultation.news.NewsInfoActivity;
import com.yksj.consultation.adapter.DynamicMesAllAdapter;
import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.app.AppContext;
import com.yksj.healthtalk.entity.DynamicMessageListEntity;
import com.yksj.healthtalk.net.http.ApiService;
import com.yksj.healthtalk.net.http.ObjectHttpResponseHandler;
import com.yksj.healthtalk.utils.FileUtils;
import com.yksj.healthtalk.utils.SharePreHelper;

import org.handmark.pulltorefresh.library.PullToRefreshBase;
import org.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import org.handmark.pulltorefresh.library.PullToRefreshListView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 患者端动态消息
 *
 * @author zheng
 */
public class PAtyDynamicMessage extends BaseActivity implements OnRefreshListener2<ListView>, OnClickListener,
        OnItemClickListener {
    private PullToRefreshListView mPullToRefreshListView;
    private ListView mListView;
    private DynamicMesAllAdapter nfeAdapter;
    private int mPagesize = 1;// 页码
    private List<DynamicMessageListEntity> nfeList;
    private DynamicMessageListEntity dnlEntity;
    private View mNullView;
    private HashMap<String, String> mAlreadyRead;// 已读

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.consultant_center_viewpager_listview3);
        initView();
    }

    private void initView() {
        initializeTitle();
        titleTextV.setText("学术资讯");
        mAlreadyRead = FileUtils.fatchReadedDynMes();
        titleLeftBtn.setOnClickListener(this);
        mNullView = findViewById(R.id.mnullview);
        mPullToRefreshListView = (PullToRefreshListView) findViewById(R.id.consultation_newsfeed);
        mListView = mPullToRefreshListView.getRefreshableView();
        nfeAdapter = new DynamicMesAllAdapter(PAtyDynamicMessage.this, 0, mAlreadyRead);
        mListView.setAdapter(nfeAdapter);
        mListView.setOnItemClickListener(this);
        mPullToRefreshListView.setOnRefreshListener(this);
        mPullToRefreshListView.setRefreshing();
        initData3();
    }

    /*
     * 全部动态消息数据加载
     */
    private void initData3() {
        ApiService.doHttpDynamicMessageList(mPagesize, new ObjectHttpResponseHandler() {

            @Override
            public Object onParseResponse(String content) {
                nfeList = new ArrayList<DynamicMessageListEntity>();
                try {
                    JSONObject obj = new JSONObject(content);
                    JSONArray array = obj.getJSONArray("result");
                    JSONObject item;
                    for (int i = 0; i < array.length(); i++) {
                        item = array.getJSONObject(i);
                        dnlEntity = new DynamicMessageListEntity();
                        dnlEntity.setConsultationCenterId(item.optInt("CONSULTATION_CENTER_ID"));
                        dnlEntity.setCustomerId(item.optInt("CUSTOMER_ID"));
                        dnlEntity.setInfoId(item.optInt("INFO_ID"));
                        dnlEntity.setInfoPicture(item.optString("INFO_PICTURE"));
                        dnlEntity.setPublishTime(item.optString("PUBLISH_TIME"));
                        dnlEntity.setStatusTime(item.optString("STATUS_TIME"));
                        dnlEntity.setInfoStaus(item.optString("INFO_STATUS"));
                        dnlEntity.setInfoName(item.optString("INFO_NAME"));
                        dnlEntity.setColorchage(0);
                        nfeList.add(dnlEntity);
                    }
                    SharePreHelper.saveDynamicReadedId(dnlEntity.getInfoId() + "");
                    return nfeList;
                } catch (JSONException e) {
                    return null;
                }
            }


            @Override
            public void onFinish() {
                super.onFinish();
                mPullToRefreshListView.onRefreshComplete();
            }

            @Override
            public void onSuccess(Object response) {
                super.onSuccess(response);
                if (response != null) {
                    if (mPagesize == 1) {
                        nfeAdapter.replaceAll((List<DynamicMessageListEntity>) response);
                    } else {
                        nfeAdapter.addAll((List<DynamicMessageListEntity>) response);
                    }

                }


                if (nfeAdapter.getCount() == 0) {
                    mNullView.setVisibility(View.VISIBLE);
                    mPullToRefreshListView.setVisibility(View.GONE);
                } else {
                    mNullView.setVisibility(View.GONE);
                    mPullToRefreshListView.setVisibility(View.VISIBLE);
                }
            }
        });

    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
        mPagesize = 1;
        initData3();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
        ++mPagesize;
        initData3();
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
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String infoId = nfeAdapter.datas.get(position - 1).getInfoId() + "";
        mAlreadyRead.put(infoId, infoId);
        FileUtils.updateReadedDynMesIds(mAlreadyRead);
        TextView textView = (TextView) view.findViewById(R.id.tv_messtitle);
        textView.setTextColor(getResources().getColor(R.color.color_text_gray));
//        Intent intent = new Intent(PAtyDynamicMessage.this, DAtyConslutDynMesContent2.class);
        Intent intent = new Intent(PAtyDynamicMessage.this, NewsInfoActivity.class);
        intent.putExtra("conId", AppContext.APP_CONSULTATION_CENTERID);
        intent.putExtra("infoId", "" + nfeAdapter.datas.get(position - 1).getInfoId());
        startActivity(intent);
    }
}
