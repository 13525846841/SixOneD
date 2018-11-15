package com.yksj.consultation.sonDoc.shopping;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.library.base.base.BaseTitleActivity;
import com.yksj.consultation.adapter.CategorizeProductAdapter;
import com.yksj.consultation.adapter.ShopGoodsSearchAdapter;
import com.yksj.consultation.bean.GoodsEvent;
import com.yksj.consultation.sonDoc.R;
import com.yksj.healthtalk.net.http.ApiService;
import com.yksj.healthtalk.net.http.HttpResultZero;
import com.yksj.healthtalk.net.http.ApiCallbackWrapper;
import com.yksj.healthtalk.utils.HStringUtil;
import com.yksj.healthtalk.utils.ToastUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.handmark.pulltorefresh.library.PullToRefreshListView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Request;

public class SearchShopActivity extends BaseTitleActivity implements TextView.OnEditorActionListener, View.OnClickListener, AdapterView.OnItemClickListener, CompoundButton.OnCheckedChangeListener {

    private CategorizeProductAdapter categorizeProductAdapter;
    private String text = "";

    private ListView mSearchList;
    private ShopGoodsSearchAdapter adapter;

    private PullToRefreshListView mRefreshableView;
    private boolean isSearch = false;
    private List<JSONObject> mList = null;//搜索结果

    private CheckBox allSelect;
    private CheckBox hotSelect;
    private CheckBox priceSelect;

    private String comprehensive = "1";//综合排序
    private String price = "0";//价格
    private String saleCount = "0";//销量

    @Override
    public int createLayoutRes() {
        return R.layout.activity_search_shop;
    }

    @Override
    public int createTitleLayoutRes() {
        return R.layout.commn_input_search3;
    }

    @Override
    public void initialize(Bundle bundle) {
        super.initialize(bundle);
        initView();
    }

    private void initView() {
        titleLeftBtn = (ImageView) findViewById(R.id.title_back);
        editSearch = (EditText) findViewById(R.id.seach_text);
        editSearch.setHint("输入搜索商品名称");
        editSearch.setOnEditorActionListener(this);

        titleLeftBtn.setOnClickListener(this);

        mRefreshableView = (PullToRefreshListView) findViewById(R.id.search_goods);
        mSearchList = mRefreshableView.getRefreshableView();
        adapter = new ShopGoodsSearchAdapter(this);
        mSearchList.setAdapter(adapter);
        mSearchList.setOnItemClickListener(this);

        allSelect = (CheckBox) findViewById(R.id.room_region);
        hotSelect = (CheckBox) findViewById(R.id.intelligent_sorting);
        priceSelect = (CheckBox) findViewById(R.id.intelligent_sorting2);

        allSelect.setOnCheckedChangeListener(this);
        if (getIntent().hasExtra("result")) {
            text = getIntent().getStringExtra("result");
            editSearch.setText(text);
        }
        initData();
    }

    /**
     * 加载搜索到的数据
     */
    private void initData() {

        isSearch = true;
        Map<String, String> map = new HashMap<>();
        map.put("Type", "findGoodsByClassId");
        map.put("pageNum", "1");
        map.put("pageSize", "20");
        map.put("search", text);
        map.put("comprehensive", comprehensive);
        map.put("price", price);
        map.put("saleCount", saleCount);
        ApiService.OKHttGoodsServlet(map, new ApiCallbackWrapper<String>(this) {
            @Override
            public void onError(Request request, Exception e) {
            }

            @Override
            public void onResponse(String content) {
                try {
                    JSONObject obj = new JSONObject(content);
                    mList = new ArrayList<>();
                    if (HttpResultZero.SUCCESS.equals(obj.optString("code"))) {
                        if (!HStringUtil.isEmpty(obj.optString("server_params"))) {
                            JSONObject object = obj.optJSONObject("server_params");
                            JSONArray array = object.optJSONArray("result");
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject jsonobject = array.getJSONObject(i);
                                mList.add(jsonobject);
                            }
                            adapter.onBoundData(mList);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onBefore(Request request) {
                super.onBefore(request);
                mRefreshableView.setRefreshing();
                mRefreshableView.onRefreshComplete();
            }

            @Override
            public void onAfter() {
                mRefreshableView.onRefreshComplete();
                super.onAfter();
            }
        }, this);
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        boolean handled = false;
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            text = editSearch.getText().toString().trim();
            if (text != null && text.length() != 0) {
                mSearchList.setVisibility(View.VISIBLE);
                initData();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(editSearch.getWindowToken(), 0);//关闭软键盘
                handled = true;
            }
        } else {
            ToastUtil.showShort("请输入内容");
        }
        return handled;
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
        Intent intent = new Intent(this, ProductDetailAty.class);
        intent.putExtra("good_id", mList.get(position - 1).optString("GOODS_ID"));
        startActivity(intent);
    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.room_region:
                if (isChecked) {
                    comprehensive = "1";
                } else {
                    comprehensive = "1";
                }
                initData();
                break;
            case R.id.intelligent_sorting:
                if (isChecked) {
                    price = "1";
                } else {
                    price = "0";
                }
                initData();
                break;
            case R.id.intelligent_sorting2:
                if (isChecked) {
                    saleCount = "1";
                } else {
                    saleCount = "0";
                }
                initData();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(GoodsEvent event) {
        if (event.code == 1) {//推荐商品
            finish();
        }
    }
}
