package com.yksj.consultation.sonDoc.consultation.main;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.library.base.base.BaseActivity;
import com.yksj.consultation.sonDoc.R;
import com.yksj.healthtalk.filemanager.FileCategoryHelper;
import com.yksj.healthtalk.filemanager.FileCategoryHelper.FileCategory;
import com.yksj.healthtalk.filemanager.FileIconHelper;
import com.yksj.healthtalk.filemanager.FileInfo;
import com.yksj.healthtalk.filemanager.FileSortHelper;
import com.yksj.healthtalk.filemanager.FileViewInteractionHub;
import com.yksj.healthtalk.filemanager.IFileInteractionListener;
import com.yksj.healthtalk.filemanager.Util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;


/**
 * 健康讲堂首页
 */
public class FileChoiceAty extends BaseActivity implements IFileInteractionListener {
    private ListView mSingleListView;
    public static final String FILETYPE = "file_type";
    private String type = "";//1视频 2图片 3文件

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_choice);
        initView();
        initData();
    }

    private void initView() {
        initializeTitle();
        mSingleListView = (ListView) findViewById(R.id.lv_singleChoice);
        titleLeftBtn.setOnClickListener(this);
        titleRightBtn2.setOnClickListener(this);
        titleRightBtn2.setText("确定");
        titleRightBtn2.setVisibility(View.VISIBLE);
        if (getIntent().hasExtra(FILETYPE)) {
            type = getIntent().getStringExtra(FILETYPE);
        }
        String title = "";
        if ("1".equals(type)) {
            title = "视频";
        } else if ("2".equals(type)) {
            title = "图片";
        } else if ("3".equals(type)) {
            title = "文档";
        }
        titleTextV.setText(title);
    }












    private void initData() {
        mFileCagetoryHelper = new FileCategoryHelper(this);
        mFileViewInteractionHub = new FileViewInteractionHub(this);
//        mFileViewInteractionHub.setMode(Mode.View);
        mFileViewInteractionHub.setRootPath("/");
        if ("1".equals(type)) {
            onCategorySelected(FileCategory.Video);
        } else if ("2".equals(type)) {
            onCategorySelected(FileCategory.Picture);
        } else if ("3".equals(type)) {
            onCategorySelected(FileCategory.Doc);
        }
    }



    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.title_back:
                onBackPressed();
                break;
            case R.id.title_right2://选择文件

                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private FileCategoryHelper mFileCagetoryHelper;
    private FileViewInteractionHub mFileViewInteractionHub;
    private FileSortHelper mFileSortHelper;

    private void onCategorySelected(FileCategory f) {
        mFileCagetoryHelper.setCurCategory(f);
        mFileViewInteractionHub.setCurrentPath(mFileViewInteractionHub.getRootPath()
                + getString(mFileCagetoryHelper.getCurCategoryNameResId()));
        mFileViewInteractionHub.refreshFileList();


//        mFileSortHelper.setSortMethog(FileSortHelper.SortMethod.date);
        FileCategory curCategory = mFileCagetoryHelper.getCurCategory();
        Cursor c = mFileCagetoryHelper.query(curCategory, FileSortHelper.SortMethod.date);
        loaddata(c);
//        MyAdapter mAdapter=new MyAdapter(this,R.layout.item_lv_single_choice,fileList);
//        mSingleListView.setAdapter(mAdapter);
//        FileInfo info = mFileNameList.get(1);
    }

    private HashMap<Integer, FileInfo> mFileNameList = new HashMap<Integer, FileInfo>();
    private List<FileInfo> fileList = new ArrayList<>();

    private void loaddata(Cursor cursor) {
        if (cursor.moveToFirst()) {
            do {
                Integer position = Integer.valueOf(cursor.getPosition());
                if (mFileNameList.containsKey(position))
                    continue;
                FileInfo fileInfo = getFileInfo(cursor);
                if (fileInfo != null) {
                    fileInfo.canRead=false;
                    mFileNameList.put(position, fileInfo);
                    fileList.add(fileInfo);
                }
            } while (cursor.moveToNext());
        }
    }

    private FileInfo getFileInfo(Cursor cursor) {
        return (cursor == null || cursor.getCount() == 0) ? null : Util
                .GetFileInfo(cursor.getString(FileCategoryHelper.COLUMN_PATH));
    }

    @Override
    public View getViewById(int id) {
        return null;
    }

    @Override
    public Context getContext() {
        return null;
    }

    @Override
    public void onDataChanged() {

    }

    @Override
    public void onPick(FileInfo f) {

    }

    @Override
    public boolean shouldShowOperationPane() {
        return false;
    }

    @Override
    public boolean onOperation(int id) {
        return false;
    }

    @Override
    public String getDisplayPath(String path) {
        return null;
    }

    @Override
    public String getRealPath(String displayPath) {
        return null;
    }

    @Override
    public boolean onNavigation(String path) {
        return false;
    }

    @Override
    public boolean shouldHideMenu(int menu) {
        return false;
    }

    @Override
    public FileIconHelper getFileIconHelper() {
        return null;
    }

    @Override
    public FileInfo getItem(int pos) {
        return null;
    }

    @Override
    public void sortCurrentList(FileSortHelper sort) {

    }

    @Override
    public Collection<FileInfo> getAllFiles() {
        return null;
    }

    @Override
    public void addSingleFile(FileInfo file) {

    }

    @Override
    public boolean onRefreshFileList(String path, FileSortHelper sort) {
        return false;
    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
