package com.yksj.consultation.im;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.library.base.base.BaseTitleActivity;
import com.library.base.widget.DividerListItemDecoration;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.yksj.consultation.adapter.GroupFileAdapter;
import com.yksj.consultation.app.AppContext;
import com.yksj.consultation.bean.GroupFileBean;
import com.yksj.consultation.bean.ResponseBean;
import com.yksj.consultation.comm.ImageBrowserActivity;
import com.yksj.consultation.sonDoc.R;
import com.yksj.healthtalk.net.http.ApiCallbackWrapper;
import com.yksj.healthtalk.net.http.ApiService;

import java.io.File;
import java.util.List;

import butterknife.BindView;

/**
 * 六一班上传群文件首页
 */
public class GroupFileActivity extends BaseTitleActivity implements BaseQuickAdapter.OnItemClickListener {
    private static final String GROUP_ID = "group_id";

    @BindView(R.id.refresh_layout) SmartRefreshLayout mRefreshLayout;
    @BindView(R.id.recycler_view) RecyclerView mRecyclerView;
    @BindView(R.id.empty_layout) View mEmptyView;

    private String mGroupId = "";//工作站id
    private GroupFileAdapter adapter;

    public static Intent getCallingIntent(Context context, String groupId) {
        Intent intent = new Intent(context, GroupFileActivity.class);
        intent.putExtra(GROUP_ID, groupId);
        return intent;
    }

    @Override
    public int createLayoutRes() {
        return R.layout.activity_station_lecture_aty2;
    }

    @Override
    public void initialize(Bundle bundle) {
        super.initialize(bundle);
        setTitle("群文件");
        setRight("上传", this::onUploadFile);
        mGroupId = getIntent().getStringExtra(GROUP_ID);
        initView();
    }

    private void initView() {
        mRefreshLayout.setOnRefreshListener(refreshLayout -> requestGroupFile());
        mRecyclerView.addItemDecoration(new DividerListItemDecoration());
        mRecyclerView.setAdapter(adapter = new GroupFileAdapter());
        adapter.setOnItemClickListener(this);
        mRefreshLayout.autoRefresh();
    }

    /**
     * 上传群文件
     * @param view
     */
    private void onUploadFile(View view) {
        Intent intent = GroupFileTypeChooseActivity.getCallingIntent(this, mGroupId);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        requestGroupFile();
    }

    /**
     * 获得群文件
     */
    private void requestGroupFile() {
        ApiService.groupFiles(mGroupId, new ApiCallbackWrapper<ResponseBean<List<GroupFileBean>>>() {
            @Override
            public void onResponse(ResponseBean<List<GroupFileBean>> response) {
                super.onResponse(response);
                if (response.isSuccess()) {
                    List<GroupFileBean> result = response.result;
                    if (result.isEmpty()) {
                        mEmptyView.setVisibility(View.VISIBLE);
                    } else {
                        adapter.setNewData(result);
                        mEmptyView.setVisibility(View.GONE);
                    }
                } else {

                }
                mRefreshLayout.finishRefresh();
            }
        });
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        GroupFileBean fileBean = (GroupFileBean) adapter.getItem(position);
        if (fileBean.fileType.equals("20")) {//图片
            String imagePath = AppContext.getApiRepository().URL_QUERYHEADIMAGE + fileBean.filePath;
            ImageBrowserActivity.from(this)
                                .setImagePath(imagePath)
                                .startActivity();
        } else if (fileBean.fileType.equals("30")) {//视频

        } else {//文档

        }
    }

    //android获取一个用于打开Excel文件的intent
    public static Intent getExcelFileIntent(String param) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(new File(param));
        intent.setDataAndType(uri, "application/vnd.ms-excel");
        return intent;
    }

    //android获取一个用于打开PPT文件的intent
    public static Intent getPptFileIntent(String param) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(new File(param));
        intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
        return intent;
    }

    //android获取一个用于打开Word文件的intent
    public static Intent getWordFileIntent(String param) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(new File(param));
        intent.setDataAndType(uri, "application/msword");
        return intent;
    }

    //android获取一个用于打开CHM文件的intent
    public static Intent getChmFileIntent(String param) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(new File(param));
        intent.setDataAndType(uri, "application/x-chm");
        return intent;
    }

    //android获取一个用于打开文本文件的intent
    public static Intent getTextFileIntent(String param, boolean paramBoolean) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (paramBoolean) {
            Uri uri1 = Uri.parse(param);
            intent.setDataAndType(uri1, "text/plain");
        } else {
            Uri uri2 = Uri.fromFile(new File(param));
            intent.setDataAndType(uri2, "text/plain");
        }
        return intent;
    }

    //android获取一个用于打开PDF文件的intent
    public static Intent getPdfFileIntent(String param) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(new File(param));
        intent.setDataAndType(uri, "application/pdf");
        return intent;
    }
}
