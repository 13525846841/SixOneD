package com.yksj.consultation.sonDoc.dossier;

import android.os.Bundle;
import android.widget.ListView;

import com.library.base.base.BaseActivity;
import com.yksj.consultation.adapter.LookHistoryAdapter;
import com.yksj.consultation.sonDoc.R;
import org.handmark.pulltorefresh.library.PullToRefreshListView;

/**
 *
 */
public class AtyLookHistoryDossier extends BaseActivity {

    private PullToRefreshListView mPullListView;
    private ListView mListView;
    private LookHistoryAdapter mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aty_look_history_dossier);
        initView();
    }

    private void initView() {
        mPullListView = (PullToRefreshListView) findViewById(R.id.look_history_dossier);
        mListView = mPullListView.getRefreshableView();
        mAdapter = new LookHistoryAdapter(AtyLookHistoryDossier.this);
        mListView.setAdapter(mAdapter);
        initData();
    }

    private void initData() {

    }
}
