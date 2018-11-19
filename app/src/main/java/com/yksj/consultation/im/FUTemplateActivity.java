package com.yksj.consultation.im;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.library.base.base.BaseTitleActivity;
import com.library.base.widget.DividerListItemDecoration;
import com.yksj.consultation.adapter.FollowTemplateAdapter;
import com.yksj.consultation.bean.FollowTemplateBean;
import com.yksj.consultation.bean.ResponseBean;
import com.yksj.consultation.bean.serializer.GsonSerializer;
import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.sonDoc.consultation.CreateTempleteActivity;
import com.yksj.consultation.sonDoc.consultation.TemplatelibAty;
import com.yksj.consultation.utils.DoctorHelper;
import com.yksj.healthtalk.net.http.ApiCallbackWrapper;
import com.yksj.healthtalk.net.http.ApiService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;

/**
 * 随访模板
 */
public class FUTemplateActivity extends BaseTitleActivity implements BaseQuickAdapter.OnItemClickListener {

    @BindView(R.id.recycler_view) RecyclerView mRecyclerView;
    private FollowTemplateAdapter mTemplateAdapter;

    private View headView;
    private final static String flag = "0";//私有数据库
    public boolean isEdit = false;
    private List<String> mDeleteIds;

    @Override
    public int createLayoutRes() {
        return R.layout.activity_futemplate;
    }

    @Override
    public void initialize(Bundle bundle) {
        super.initialize(bundle);
        setTitle("随访模板");
        initView();
    }

    private void initView() {
        mRecyclerView.addItemDecoration(new DividerListItemDecoration());
        mRecyclerView.setAdapter(mTemplateAdapter = new FollowTemplateAdapter());
        mTemplateAdapter.setOnItemClickListener(this);
        headView = View.inflate(this, R.layout.futemp_head, null);
        mTemplateAdapter.addHeaderView(headView);
        headView.findViewById(R.id.add_template).setOnClickListener(this::onAddTemplate);
        headView.findViewById(R.id.temp_lib).setOnClickListener(this::onTemplateLibs);
    }

    @Override
    protected void onResume() {
        super.onResume();
        requestTemplateData();
    }

    /**
     * 添加模块
     * @param v
     */
    private void onAddTemplate(View v){
        Intent intent = new Intent(FUTemplateActivity.this, CreateTempleteActivity.class);
        startActivity(intent);
    }

    /**
     * 模版库
     * @param v
     */
    private void onTemplateLibs(View v){
        Intent intent = new Intent(this, TemplatelibAty.class);
        startActivity(intent);
    }

    /**
     * 编辑模板
     * @param view
     */
    private void onEditTemplate(View view) {
        isEdit = !isEdit;
        if (isEdit) {// 编辑状态
            setRight("删除");
            mTemplateAdapter.setEditable(true);
        } else {// 编辑完成
            setRight("编辑");
            mTemplateAdapter.setEditable(false);
            mDeleteIds = mTemplateAdapter.getSelectTemplate();
            if (!mDeleteIds.isEmpty()){
                requestDeleteTemplate();
            }
        }
    }

    /**
     * 加载模版数据
     */
    private void requestTemplateData() {
        ApiService.listTemplate(DoctorHelper.getId(), flag, new ApiCallbackWrapper<ResponseBean<List<FollowTemplateBean>>>(true) {
            @Override
            public void onResponse(ResponseBean<List<FollowTemplateBean>> resp) {
                if (resp.code == 0) {
                    List<FollowTemplateBean> templates = resp.templates;
                    if (!templates.isEmpty()) {
                        // 有模版才能编辑
                        setRight("编辑",FUTemplateActivity.this::onEditTemplate);
                        mTemplateAdapter.setNewData(templates);
                        mTemplateAdapter.setEditable(false);
                    }
                }
            }
        });
    }

    /**
     * 编辑个人模板。（删除）
     */
    private void requestDeleteTemplate() {
        List<Map<String, String>> jsonList = new ArrayList<>();
        for (String id : mDeleteIds) {
            Map<String, String> cell = new HashMap<>();
            cell.put("template_id", id);
            jsonList.add(cell);
        }
        String json = GsonSerializer.serialize(jsonList);
        ApiService.deleteTemplate(DoctorHelper.getId(), json, new ApiCallbackWrapper<ResponseBean>() {
            @Override
            public void onResponse(ResponseBean response) {
                super.onResponse(response);
                if (response.code == 0){
                    ToastUtils.showShort("删除成功");
                    if (mTemplateAdapter.getData().isEmpty()){
                        setRight("");
                    }
                    requestTemplateData();
                }else{
                    ToastUtils.showShort("删除失败");
                }
            }
        });
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        FollowTemplateBean item = mTemplateAdapter.getItem(position);
        Intent intent = new Intent(this, AddTmpPlanActivity.class);
        // intent.putExtra("type1","modify");
        intent.putExtra("customer_id", DoctorHelper.getId());
        intent.putExtra("follow_id", item.id);
        startActivity(intent);
    }
}
