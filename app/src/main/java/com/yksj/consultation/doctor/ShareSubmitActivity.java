package com.yksj.consultation.doctor;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.library.base.base.BaseTitleActivity;
import com.library.base.dialog.SelectorDialog;
import com.library.base.utils.EventManager;
import com.library.base.utils.ResourceHelper;
import com.library.base.utils.RxChooseHelper;
import com.library.base.widget.DividerGridItemDecoration;
import com.yksj.consultation.adapter.SharePictureAdapte;
import com.yksj.consultation.adapter.SharePictureAdapte.SharePictureObserver;
import com.yksj.consultation.bean.CommentPicture;
import com.yksj.consultation.bean.ResponseBean;
import com.yksj.consultation.dialog.DialogManager;
import com.yksj.consultation.dialog.WaitDialog;
import com.yksj.consultation.event.EShareSucees;
import com.yksj.consultation.sonDoc.R;
import com.yksj.healthtalk.net.http.ApiCallbackWrapper;
import com.yksj.healthtalk.net.http.ApiService;
import com.yksj.healthtalk.utils.BitmapUtils;
import com.yksj.healthtalk.utils.ToastUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

/**
 * 发布分享
 */
public class ShareSubmitActivity extends BaseTitleActivity {
    private static final int ALBUM_RESULT = 0x001;
    private static final int CAPTURE_RESULT = 0x002;
    private static final int PICTURE_NUM = 9;

    private List<CommentPicture> mPictures = new ArrayList();//图片list类
    private List<File> mCompressPictures = new ArrayList<>();//图片压缩集合
    private EditText addreword;
    private RecyclerView mPictureContainer;
    private SharePictureAdapte mPictureAdapter;
    private WaitDialog mLoadingDialog;

    @Override
    public int createLayoutRes() {
        return R.layout.activity_share_con;
    }

    @Override
    public void initialize(Bundle bundle) {
        super.initialize(bundle);
        setTitle("分享内容");
        setRight("分享", view -> submitContent());
        initView();
    }

    private void initView() {
        addreword = findViewById(R.id.addreword);
        mPictureContainer = findViewById(R.id.picture_container);
        mPictureAdapter = new SharePictureAdapte(mPictures, PICTURE_NUM);
        mPictureContainer.setAdapter(mPictureAdapter);
        mPictureContainer.addItemDecoration(new DividerGridItemDecoration(SizeUtils.dp2px(8), true));
        mPictureAdapter.setSharePictureObserver(new SharePictureObserver() {
            @Override
            public void onChoose(View v) {
                super.onChoose(v);
                onChooseClick(v);
            }
        });
    }

    /**
     * 图片选择
     * @param v
     */
    public void onChooseClick(View v) {
        SelectorDialog.newInstance(ResourceHelper.getStringArray(R.array.choose_item))
                      .setOnItemClickListener((dialog, position) -> {
                          switch (position) {
                              case 1:
                                  RxChooseHelper.captureImage(ShareSubmitActivity.this)
                                                .subscribe(new Consumer<String>() {
                                                    @Override
                                                    public void accept(String s) throws Exception {
                                                        LogUtils.e(s);
                                                        mPictureAdapter.addData(CommentPicture.valueOf(s));
                                                    }
                                                });
                                  break;
                              case 0:
                                  RxChooseHelper.chooseImage(ShareSubmitActivity.this, mPictureAdapter.getChoosableNum())
                                                .flatMap(new Function<List<String>, ObservableSource<String>>() {
                                                    @Override
                                                    public ObservableSource<String> apply(List<String> strings) throws Exception {
                                                        return Observable.fromIterable(strings);
                                                    }
                                                })
                                                .subscribe(new Consumer<String>() {
                                                    @Override
                                                    public void accept(String s) throws Exception {
                                                        mPictureAdapter.addData(CommentPicture.valueOf(s));
                                                    }
                                                });
                                  break;
                          }
                      })
                      .show(getSupportFragmentManager());
    }

    /**
     * 开始分享
     */
    @SuppressLint("CheckResult")
    private void submitContent() {
        String content = addreword.getText().toString().trim();
        if (TextUtils.isEmpty(content)) {
            ToastUtil.showToastPanl("请输入文字");
            return;
        }

        if (mPictures.isEmpty()) {
            requestSubmit(null, content);
        } else {
            compressBitmap(mPictures)
                    .subscribe(new Consumer<List<File>>() {
                        @Override
                        public void accept(List<File> files) throws Exception {
                            mCompressPictures.addAll(files);
                            requestSubmit(files, content);
                        }
                    });
        }
    }

    /**
     * 压缩图片
     * @param pictures
     * @return
     */
    private Observable compressBitmap(List<CommentPicture> pictures) {
        return Observable.fromIterable(pictures)
                         .map(new Function<CommentPicture, File>() {
                             @Override
                             public File apply(CommentPicture picture) throws Exception {
                                 return BitmapUtils.compressBitmapByPath(picture.PICTURE_PATH, 50);
                             }
                         })
                         .filter(new Predicate<File>() {
                             @Override
                             public boolean test(File file) throws Exception {
                                 return file != null && file.exists();
                             }
                         })
                         .buffer(pictures.size())
                         .subscribeOn(Schedulers.io())
                         .observeOn(AndroidSchedulers.mainThread())
                         .doOnSubscribe(new Consumer<Disposable>() {
                             @Override
                             public void accept(Disposable disposable) throws Exception {
                                 mLoadingDialog = createWaitDialog();
                                 mLoadingDialog.show(getSupportFragmentManager());
                             }
                         })
                         .doFinally(new Action() {
                             @Override
                             public void run() throws Exception {
                                 mLoadingDialog.dismissAllowingStateLoss();
                             }
                         });
    }

    /**
     * 发布数据
     * @param pictures
     * @param content
     */
    private void requestSubmit(List<File> pictures, String content) {
        ApiService.OKHttpShareUploadServlet(content, pictures, new ApiCallbackWrapper<ResponseBean>(true) {
            @Override
            public void onResponse(ResponseBean response) {
                super.onResponse(response);
                ToastUtils.showShort(response.message);
                EventManager.post(new EShareSucees());
                if (response.isSuccess()) {
                    finish();
                }
            }
        });
    }

    private WaitDialog createWaitDialog() {
        WaitDialog waitDialog = DialogManager.getWaitDialog("图片处理中...");
        waitDialog.setBackenable(false);
        return waitDialog;
    }

    @Override
    protected void onDestroy() {
        if (mCompressPictures != null && !mCompressPictures.isEmpty()) {
            for (File file : mCompressPictures) {
                FileUtils.deleteFile(file);
            }
            mCompressPictures.clear();
            mCompressPictures = null;
        }
        super.onDestroy();
    }
}
