package com.yksj.consultation.sonDoc.dossier;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.content.DialogInterface.OnClickListener;

import com.library.base.base.BaseActivity;
import com.yksj.consultation.comm.EditFragmentDialog;
import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.sonDoc.views.widget.Tag;
import com.yksj.consultation.sonDoc.views.widget.TagListView;
import com.yksj.consultation.sonDoc.views.widget.TagView;
import com.yksj.healthtalk.net.http.ApiService;
import com.yksj.healthtalk.net.http.ObjectHttpResponseHandler;
import com.yksj.healthtalk.net.socket.SmartFoxClient;
import com.yksj.healthtalk.utils.ToastUtil;

import android.app.AlertDialog.Builder;
import android.support.v4.app.DialogFragment;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zheng on 2015/7/17.
 */
public class AtyAddKey extends BaseActivity implements TagListView.OnTagCheckedChangedListener,
        TagListView.OnTagLongClickListener ,View.OnClickListener{

    private TagListView mSystemListView;//系统
    private TagListView mCustomListView;//自定义
    private ArrayList<Tag> mSystemTags = new ArrayList<Tag>();
    private ArrayList<Tag> mSelectSystemTags,mSelectCuntomTags;
    private ArrayList<Tag> mCustomTags = new ArrayList<Tag>();
    private final String[] systemTitles = { "安全必备", "音乐", "父母学", "上班族必备","360手机卫士", "QQ"};
    private List<JSONObject> mObjList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_tag_activity);
        mSystemListView = (TagListView) findViewById(R.id.tagview);
//        mCustomListView = (TagListView) findViewById(R.id.custom_tagview);
        initView();
        initData();
    }

    private void initView() {
        initializeTitle();
        titleTextV.setText("关键词");
        titleLeftBtn.setOnClickListener(this);

    }

    private void initData() {
        initSystemGvData();
//        for (int i = 0; i < systemTitles.length; i++) {
//            Tag tag = new Tag();
//            tag.setId(i);
//            tag.setChecked(true);
//            tag.setTitle(systemTitles[i]);
//            mSystemTags.add(tag);
//        }
        /*
        SharedPreferences sharedPreferences=getSharedPreferences(SmartFoxClient.getLoginUserId()+"10086",MODE_PRIVATE);
        String cacheKey = sharedPreferences.getString("KEYWORD","");
        if(cacheKey==null||cacheKey.equals("")){}else {
            String[] cachekeys =cacheKey.split(",");
            for (int i = 0; i < cachekeys.length; i++) {
                if(cachekeys[i]==null||cachekeys[i].equals("")){
                }else {
                Tag tag = new Tag();
                tag.setId(i);
                tag.setTitle(cachekeys[i]);
                mCustomTags.add(tag);
                }
            }
        }
        mSelectCuntomTags= (ArrayList<Tag>) getIntent().getSerializableExtra("result2");
        if(mSelectSystemTags!=null){//客户自定义标签
            for(int i=0;i<mSelectCuntomTags.size();i++){
                mCustomTags.get(mSelectCuntomTags.get(i).getId()).setChecked(true);
            }
        }

        Tag tag = new Tag();
        tag.setId(-1);//当id为-1  表示点击不变色
        tag.setsID(-1);//
        tag.setTitle("+");
        mCustomTags.add(tag);

        //绑定数据  true表示响应check事件
        mCustomListView.setTags(mCustomTags,true);
        mCustomListView.setOnTagCheckedChangedListener(this);
        mCustomListView.setOnTagLongClickListener(this);
        */
    }
    @Override
    public void onTagCheckedChanged(TagView tagView, Tag tag) {
        if(tag!=null &&  -1 == tag.getId()){
            tag.setChecked(false);
            EditFragmentDialog.show(getSupportFragmentManager(),
                    "编辑标签",10, "取消", "确定", new EditFragmentDialog.OnDilaogClickListener() {
                        @Override
                        public void onDismiss(DialogFragment fragment) {

                        }

                        @Override
                        public void onClick(DialogFragment fragment, View v) {
                            EditFragmentDialog dialog= (EditFragmentDialog) fragment;
                            String editStr=dialog.getEditTextStr();
                            if(editStr!=null&&editStr.length()!=0){
                                for(Tag tag:mCustomTags){
                                    if(editStr.equals(tag.getTitle())){
                                        ToastUtil.showShort(AtyAddKey.this,"标签已存在");
                                        return;
                                    }
                                }
                                for(Tag tag:mSystemTags){
                                    if(editStr.equals(tag.getTitle())){
                                        ToastUtil.showShort(AtyAddKey.this,"标签已存在");
                                        return;
                                    }
                                }
                                SharedPreferences sharedPreferences=getSharedPreferences(SmartFoxClient.getLoginUserId()+"10086",MODE_PRIVATE);
                                String spCacheKey=sharedPreferences.getString("KEYWORD","");
                                spCacheKey=spCacheKey+editStr+",";
                                sharedPreferences.edit().putString("KEYWORD",spCacheKey).commit();
                                Tag ntag = new Tag();
                                ntag.setId(mCustomTags.size()-1);
                                ntag.setTitle(editStr);
                                dialog.setEditTextStr("");
                                mCustomTags.add(mCustomTags.size() - 1, ntag);
                                mCustomListView.setTags(mCustomTags, true);
                            }
                        }
                    });
//                AlertDialog.Builder dialog7=new AlertDialog.Builder(AtyAddKey.this);
//                dialog7.setTitle("登录");
//                LayoutInflater inflater=LayoutInflater.from(AtyAddKey.this);
//                View view2=inflater.inflate(R.layout.dialog_edit_double_btn_layout, null);
//                final EditText editTagName=(EditText) view2.findViewById(R.id.edit_dialog_edittext);
//                dialog7.setView(view2);//设置自定义布局
//                dialog7.setPositiveButton("确定", new OnClickListener() {
//
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//
//                        String editStr=editTagName.getText().toString().trim();
//                        if(editStr!=null&&editStr.length()!=0){
//                            SharedPreferences sharedPreferences=getSharedPreferences(SmartFoxClient.getLoginUserId()+"10086",MODE_PRIVATE);
//                            String spCacheKey=sharedPreferences.getString("KEYWORD","");
//                            spCacheKey=spCacheKey+editStr+",";
//                            sharedPreferences.edit().putString("KEYWORD",spCacheKey).commit();
//                            Tag ntag = new Tag();
//                            ntag.setId(mCustomTags.size()-1);
//                            ntag.setTitle(editStr);
//                            editTagName.setText("");
//                            mCustomTags.add(mCustomTags.size() - 1, ntag);
//                            mCustomListView.setTags(mCustomTags, true);
//                        }
//                    }
//                });
//                dialog7.setNegativeButton("取消", new OnClickListener() {
//
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                    }
//                });
//                dialog7.create().show();

        }
    }
    @Override
    public void onTagLongClick(TagView tagView, final Tag tag) {
        AlertDialog.Builder builder= new Builder(AtyAddKey.this);
        builder.setTitle("提示");
        builder.setMessage("您确定要删除吗?");
        builder.setPositiveButton("确定",new OnClickListener() {

            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                mCustomTags.remove(tag);
                mCustomListView.setTags(mCustomTags,true);

            }
        });
        builder.setNeutralButton("取消",new OnClickListener() {

            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                arg0.dismiss();
            }
        });
        builder.show();
    }
    //系统关键词
    private void initSystemGvData(){
        ApiService.doHttpFindMedicalKeywordTag(new ObjectHttpResponseHandler(AtyAddKey.this) {

            @Override
            public Object onParseResponse(String content) {
                try {
                    JSONObject obj = new JSONObject(content);
                    JSONArray array = obj.getJSONArray("findMedicalKeywordTag");
                    mObjList = new ArrayList<JSONObject>();
                    for (int i = 0; i < array.length(); i++) {
                        mObjList.add(array.getJSONObject(i));
                    }
                    return mObjList;
                } catch (JSONException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            public void onSuccess(Object response) {
                super.onSuccess(response);
                List<JSONObject> list=(List<JSONObject>) response;
                try {
                    for (int i = 0; i < list.size(); i++) {
                        Tag tag = new Tag();
                        tag.setId(i);
                        tag.setsID(list.get(i).optInt("TAG_ID"));
                        tag.setTitle(list.get(i).getString("TAG_NAME"));
                        mSystemTags.add(tag);
                    }
                    mSelectSystemTags= (ArrayList<Tag>) getIntent().getSerializableExtra("result1");
                    if(mSelectSystemTags!=null){//系统标签
                        for(int i=0;i<mSelectSystemTags.size();i++){
                            mSystemTags.get(mSelectSystemTags.get(i).getId()).setChecked(true);
                        }
                    }
                    mSystemListView.setTags(mSystemTags,true);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

//                mAdapter.addAll((List<JSONObject>) response);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.title_back:
                onBackPressed();
                break;
        }
    }

    @Override
    public void onBackPressed() {

        ArrayList<Tag> systemData=mSystemListView.getSelectedTags();
//        ArrayList<Tag> customerData=mCustomListView.getSelectedTags();

        Intent intent = new Intent(this,DoctorCreateCaseActivity.class);
        intent.putExtra("result1", systemData);
//        intent.putExtra("result2", customerData);
        setResult(RESULT_OK, intent);
        this.finish();
    }
}
