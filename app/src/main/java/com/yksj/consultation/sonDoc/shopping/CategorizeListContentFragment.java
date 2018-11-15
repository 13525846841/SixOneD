package com.yksj.consultation.sonDoc.shopping;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import okhttp3.Request;
import com.yksj.consultation.adapter.CategorizeListContentAdapter;
import com.yksj.consultation.sonDoc.R;
import com.yksj.healthtalk.net.http.ApiCallbackWrapper;
import com.yksj.healthtalk.net.http.ApiService;
import com.yksj.healthtalk.net.http.HttpResultZero;
import com.yksj.healthtalk.utils.HStringUtil;

import org.handmark.pulltorefresh.library.PullToRefreshBase;
import org.handmark.pulltorefresh.library.PullToRefreshListView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ${chen} on 2017/8/9.
 */
public class CategorizeListContentFragment extends Fragment implements PullToRefreshBase.OnRefreshListener2<ListView> {

    private CategorizeListContentAdapter adapter;
    private List<JSONObject> list = null;
    private PullToRefreshListView mRefreshableView;
    public ListView mListView;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.productlist_layout, null);
        mRefreshableView = (PullToRefreshListView) view.findViewById(R.id.pull_refresh_listview);
        mListView = mRefreshableView.getRefreshableView();

        int classId = Integer.parseInt(getArguments().getString("index"));

        adapter = new CategorizeListContentAdapter(getActivity());
        mListView.setAdapter(adapter);
        mRefreshableView.setOnRefreshListener(this);
        initData(classId);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                Intent intent  = new Intent(getActivity(),ProductDetailAty.class);
                intent.putExtra("good_id",list.get(position-1).optString("GOODS_ID"));
                startActivity(intent);
            }
        });
        return view;
    }

    /**
     * 加载数据
     * @param classId
     */
    private void initData(int classId) {
        Map<String,String> map=new HashMap<>();
        map.put("Type", "findGoodsByClassId");
        map.put("class_id", String.valueOf(classId));
        map.put("pageNum", "1");
        map.put("pageSize", "20");
        ApiService.OKHttGoodsServlet(map, new ApiCallbackWrapper<String>(getActivity()) {
            @Override
            public void onError(Request request, Exception e) {
            }
            @Override
            public void onResponse(String content) {
                try {
                    JSONObject obj = new JSONObject(content);
                    list = new ArrayList<>();
                    if (HttpResultZero.SUCCESS.equals(obj.optString("code"))){
                        if (!HStringUtil.isEmpty(obj.optString("server_params"))){
                            JSONObject object = obj.optJSONObject("server_params");
                            JSONArray array = object.optJSONArray("result");
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject jsonobject = array.getJSONObject(i);
                                list.add(jsonobject);
                            }
                            adapter.onBoundData(list);
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
        },this);
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {

    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {

    }
}
