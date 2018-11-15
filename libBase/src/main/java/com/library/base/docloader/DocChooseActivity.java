package com.library.base.docloader;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.library.base.R;
import com.library.base.base.BaseTitleActivity;
import com.library.base.widget.DividerListItemDecoration;

import java.util.List;

/**
 * 文档文件选择界面
 */
public class DocChooseActivity extends BaseTitleActivity implements BaseQuickAdapter.OnItemClickListener {

    private static final String DOC_RESULT = "doc_result";

    private RecyclerView mRecyclerView;
    private DocChooseAdapter mAdapter;

    public static Intent getCallingIntent(Context context){
        Intent intent = new Intent(context, DocChooseActivity.class);
        return intent;
    }

    @Override
    public int createLayoutRes() {
        return R.layout.activity_doc_choose;
    }

    @Override
    public void initialize(Bundle bundle) {
        super.initialize(bundle);
        setTitle("文档选择");
        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.addItemDecoration(new DividerListItemDecoration());
        mRecyclerView.setAdapter(mAdapter = new DocChooseAdapter());
        mAdapter.setOnItemClickListener(this);
        readLocalMedia();
    }

    private void readLocalMedia() {
        getSupportLoaderManager().initLoader(1, null, new LocalDocLoader(this, new LocalDocLoader.LocalDocLoaderListener() {
            @Override
            public void onLoaderComplete(List<DocEntity> docFiles) {
                mAdapter.setNewData(docFiles);
            }
        }));
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        DocEntity item = mAdapter.getItem(position);
        Intent intent = new Intent();
        intent.putExtra(DOC_RESULT, item);
        setResult(RESULT_OK, intent);
        finish();
    }

    public static DocEntity obtainResult(Intent data){
        return data.getParcelableExtra(DOC_RESULT);
    }
}
