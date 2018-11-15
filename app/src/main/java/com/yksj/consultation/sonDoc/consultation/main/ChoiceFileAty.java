package com.yksj.consultation.sonDoc.consultation.main;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.yksj.consultation.adapter.ChoiceFileAdapter;
import com.library.base.base.BaseActivity;
import com.yksj.consultation.dialog.WaitDialog;
import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.bean.FileType;
import com.yksj.healthtalk.filemanager.FileCategoryHelper;
import com.yksj.healthtalk.filemanager.FileCategoryHelper.FileCategory;
import com.yksj.healthtalk.filemanager.FileIconHelper;
import com.yksj.healthtalk.filemanager.FileInfo;
import com.yksj.healthtalk.filemanager.FileSortHelper;
import com.yksj.healthtalk.filemanager.FileUtils;
import com.yksj.healthtalk.filemanager.FileViewInteractionHub;
import com.yksj.healthtalk.filemanager.IFileInteractionListener;
import com.yksj.healthtalk.filemanager.Util;
import com.yksj.healthtalk.utils.ACache;
import com.yksj.healthtalk.utils.HStringUtil;
import com.yksj.healthtalk.utils.SharePreHelper;
import com.yksj.healthtalk.utils.ThreadManager;
import com.yksj.healthtalk.utils.ToastUtil;
import com.yksj.healthtalk.utils.WeakHandler;
import com.yksj.healthtalk.views.MyPopWindow;

import org.handmark.pulltorefresh.library.PullToRefreshBase;
import org.handmark.pulltorefresh.library.PullToRefreshListView;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

/**
 * 健康讲堂首页
 */
public class ChoiceFileAty extends BaseActivity implements PullToRefreshBase.OnRefreshListener2<ListView>, AdapterView.OnItemClickListener, IFileInteractionListener {
    private WaitDialog mLoadDialog;
    private List<FileInfo> mFiles;
    private List<File> mFilesList;
    private ACache mCatch;
    private Gson mGson;
    private SharedPreferences mPreferences;
    String key = "";
    String choicFileName;
    String choicFileSize;
    String choicFilePath;
    public static final String FILENAMEC = "file_namec";
    public static final String FILESIZE = "file_size";
    public static final String FILEPATH = "file_path";
    WeakHandler mHandler = new WeakHandler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 1://
                    if (mLoadDialog != null && !mLoadDialog.isDetached()) {
                        mLoadDialog.dismissAllowingStateLoss();
                    }
                    adapter.onBoundData(mFiles, mFilesList, type);
                    break;
            }
            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choice_file_aty);
        initView();
//        initData();
        initData2();
    }

    private void initData2() {
        mFiles = new ArrayList<>();
        mFilesList = new ArrayList<>();
        mGson = new Gson();
        mCatch = ACache.get(this);
        ThreadManager.getInstance().createShortPool().execute(new Runnable() {
            @Override
            public void run() {
                judge();
                Message message = new Message();
                message.what = 1;
                mHandler.sendMessage(message);
            }
        });
    }

    String cacheKey = "";
    String numKey = "";
    String timeKey = "";

    private void judge() {
        try {
            mPreferences = getContext().getSharedPreferences(SharePreHelper.FILE_CACHE, Context.MODE_PRIVATE);
        } catch (Exception e) {
            //子线程未销毁可能时执行
        }
        boolean first = SharePreHelper.getFileCache(key);
        int num = SharePreHelper.getFileCacheNum(numKey);
        long time = SharePreHelper.getFileCacheTime(timeKey);
        long cha = System.currentTimeMillis() - time;
        //判断缓存时间是否过期

        if (!first && time != 0 & cha < 86400000) {
            for (int i = 0; i < num; i++) {
                String s = String.valueOf(i);
                String string = mCatch.getAsString(s + cacheKey);
                if (string != null) {
                    File file = mGson.fromJson(string, File.class);
                    FileInfo info = Util.GetFileInfo(file, null, false);
                    if (info != null) {
                        info.Selected = false;
                        mFiles.add(info);
                    }
                    mFilesList.add(file);
                }

            }
        } else {
            if (FileType.VIDEO.equals(type)) {
                mFiles = FileUtils.listFilesInDirWithFilters(Environment.getExternalStorageDirectory(), ".mp4");
                mFilesList = FileUtils.listFilesInDirWithFilter(Environment.getExternalStorageDirectory(), ".mp4");
            } else if (FileType.PIC.equals(type)) {
                mFiles = FileUtils.listFilesInDirWithFilters(Environment.getExternalStorageDirectory(), ".png", ".jpg");
                mFilesList = FileUtils.listFilesInDirWithFilter(Environment.getExternalStorageDirectory(), ".png", ".jpg");
            } else if (FileType.DOC.equals(type)) {
                mFiles = FileUtils.listFilesInDirWithFilters(Environment.getExternalStorageDirectory(), ".txt", ".doc", ".xls", ".pdf");
                mFilesList = FileUtils.listFilesInDirWithFilter(Environment.getExternalStorageDirectory(), ".txt", ".doc", ".xls", ".pdf");
            }
            for (int i = 0; i < mFiles.size(); i++) {
                mFiles.get(i).Selected = false;
            }
            addCatch(cacheKey, key, numKey, timeKey);
        }

        collectionList(mFiles);
    }

    private void collectionList(List<FileInfo> mList) {
//        System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
        Collections.sort(mList, new Comparator<FileInfo>() {
            /**
             *
             * @param lhs
             * @param rhs
             * @return an integer < 0 if lhs is less than rhs, 0 if they are
             *         equal, and > 0 if lhs is greater than rhs,比较数据大小时,这里比的是时间
             */
            @Override
            public int compare(FileInfo lhs, FileInfo rhs) {

                // 对日期字段进行升序，如果欲降序可采用after方法
                if (lhs.ModifiedDate < rhs.ModifiedDate) {
                    return 1;
                } else if (lhs.ModifiedDate > rhs.ModifiedDate) {
                    return -1;
                }else {
                    return 0;
                }

            }
        });
    }

    private void addCatch(String cacheKey, String key, String num, String Time) {
        ArrayList<String> strings = new ArrayList<>();
        for (int i = 0; i < mFilesList.size(); i++) {
            String s = mGson.toJson(mFilesList.get(i));
            strings.add(s);
        }
        for (int i = 0; i < strings.size(); i++) {
            String s = String.valueOf(i);
            mCatch.put(s + cacheKey, strings.get(i), ACache.TIME_DAY);
        }
        SharePreHelper.updateFileCache(key, num, Time, false, strings.size(), System.currentTimeMillis());
    }


    private PullToRefreshListView mPullRefreshListView;
    private View mEmptyView;

    private ChoiceFileAdapter adapter;

    private MyPopWindow myPopWindow;//右上角菜单


    private List<JSONObject> list = null;
    public static final String FILETYPE = "file_type";
    private String type = "";//1视频 2图片 3文件


    private void initView() {
        initializeTitle();
        titleLeftBtn.setOnClickListener(this);
        titleRightBtn2.setOnClickListener(this);
        titleRightBtn2.setText("确定");
        titleRightBtn2.setVisibility(View.VISIBLE);
        if (getIntent().hasExtra(FILETYPE)) {
            type = getIntent().getStringExtra(FILETYPE);
        }
        String title = "";
        if (FileType.VIDEO.equals(type)) {
            title = "视频";
            key = "firstVideo";
            cacheKey = "Video";
            numKey = "numVideo";
            timeKey = "VideoTime";
        } else if (FileType.PIC.equals(type)) {
            title = "图片";
            key = "firstImage";
            cacheKey = "Image";
            numKey = "numImage";
            timeKey = "ImageTime";

        } else if (FileType.DOC.equals(type)) {
            title = "文档";
            key = "firstWord";
            cacheKey = "Word";
            numKey = "numWord";
            timeKey = "WordTime";
        }

        if (SharePreHelper.getFileCache(key)) {
            mLoadDialog = WaitDialog.showLodingDialog(getSupportFragmentManager(), "初次加载,请稍后...");
        } else {
            mLoadDialog = WaitDialog.showLodingDialog(getSupportFragmentManager(), getResources());
        }

        mLoadDialog.setCancelable(false);
        titleTextV.setText(title);

        mEmptyView = findViewById(R.id.empty_view_famous1);
        mPullRefreshListView = ((PullToRefreshListView) findViewById(R.id.my_station_member__pulllist));
        ListView mListView = mPullRefreshListView.getRefreshableView();
        mPullRefreshListView.setOnRefreshListener(this);
//        if (!"30".equals(type)) {
//            mImageViewP.setVisibility(View.VISIBLE);
//            mImageViewP.setOnClickListener(this);
//        }
        adapter = new ChoiceFileAdapter(this);
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.title_back:
                onBackPressed();
                break;
            case R.id.title_right2:
                confirm(choicFilePath, choicFileName);
                break;
        }
    }

    /**
     * 选择图片
     */
    private void confirm(String path, String name) {
        if (!HStringUtil.isEmpty(path)) {
            Intent intent = new Intent();
            intent.putExtra(FILEPATH, path);
            intent.putExtra(FILETYPE, type);
            intent.putExtra(FILENAMEC, name);
            ChoiceFileAty.this.setResult(RESULT_OK, intent);
            ChoiceFileAty.this.finish();
        } else {
            ToastUtil.showShort("文件不存在");
        }

    }

    public void getPath(String path,String name) {
        choicFilePath = path;
        choicFileName = name;
    }

    @Override
    public void onPullDownToRefresh(final PullToRefreshBase<ListView> refreshView) {
        ThreadManager.getInstance().createShortPool().execute(new Runnable() {
            @Override
            public void run() {
                if (FileType.VIDEO.equals(type)) {
                    mFiles = FileUtils.listFilesInDirWithFilters(Environment.getExternalStorageDirectory(), ".mp4");
                    mFilesList = FileUtils.listFilesInDirWithFilter(Environment.getExternalStorageDirectory(), ".mp4");
                } else if (FileType.PIC.equals(type)) {
                    mFiles = FileUtils.listFilesInDirWithFilters(Environment.getExternalStorageDirectory(), ".png", ".jpg");
                    mFilesList = FileUtils.listFilesInDirWithFilter(Environment.getExternalStorageDirectory(), ".png", ".jpg");
                } else if (FileType.DOC.equals(type)) {
                    mFiles = FileUtils.listFilesInDirWithFilters(Environment.getExternalStorageDirectory(), ".txt", ".docx", ".xlsx", ".pptx");
                    mFilesList = FileUtils.listFilesInDirWithFilter(Environment.getExternalStorageDirectory(), ".txt", ".docx", ".xlsx", ".pptx");
                }
                collectionList(mFiles);
                addCatch(cacheKey, key, numKey, timeKey);
                try {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.onBoundData(mFiles, mFilesList, type);
                            refreshView.onRefreshComplete();
                            Toast.makeText(ChoiceFileAty.this, "刷新完成", Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (Exception e) {

                }
            }
        });

    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
        refreshView.onRefreshComplete();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    private void initData() {
        mFileCagetoryHelper = new FileCategoryHelper(this);
        mFileViewInteractionHub = new FileViewInteractionHub(this);
//        mFileViewInteractionHub.setMode(Mode.View);
        mFileViewInteractionHub.setRootPath("/");
        if (FileType.VIDEO.equals(type)) {
            onCategorySelected(FileCategoryHelper.FileCategory.Video);
        } else if (FileType.PIC.equals(type)) {
            onCategorySelected(FileCategory.Picture);
        } else if (FileType.DOC.equals(type)) {
            onCategorySelected(FileCategory.Doc);
        }
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
                    fileInfo.canRead = false;
                    mFileNameList.put(position, fileInfo);
                    fileList.add(fileInfo);
                }
            } while (cursor.moveToNext());
        }
//        adapter.onBoundData(fileList, type);
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
