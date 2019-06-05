package com.yksj.consultation.doctor;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.library.base.base.BaseActivity;
import com.library.base.base.BaseTitleActivity;
import com.umeng.socialize.media.Base;
import com.yksj.consultation.sonDoc.R;
import com.yksj.healthtalk.net.http.ApiCallbackWrapper;
import com.yksj.healthtalk.net.http.ApiService;
import com.yksj.healthtalk.net.http.HttpResult;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ReplyActivity extends AppCompatActivity implements View.OnClickListener {


    private String id;
    private EditText edContent;
    private ImageView imgBack;
    private TextView title_right;
    
    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_reply);
        initView();

    }


    
    private void initView() {
        id = getIntent().getStringExtra("id");
        edContent=findViewById(R.id.ed_content);
        imgBack=findViewById(R.id.iv_back);
        title_right=findViewById(R.id.title_right);
        imgBack.setOnClickListener(this);
        title_right.setOnClickListener(this);

    }


    private void reply(String string) {
        List<BasicNameValuePair> valuePairs = new ArrayList<>();
        valuePairs.add(new BasicNameValuePair("op","replyEvaluate"));
        valuePairs.add(new BasicNameValuePair("reply_content",string));
        valuePairs.add(new BasicNameValuePair("evaluate_id",""+id));
        ApiService.OkHttpAddReply(valuePairs, new ApiCallbackWrapper<String>() {

            @Override
            public void onResponse(String response) {
                super.onResponse(response);
                        try {
                            JSONObject object = new JSONObject(response);
                            if(HttpResult.SUCCESS.endsWith(object.optString("code"))){
                                Toast.makeText(ReplyActivity.this, object.optString("message"), Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
        }, this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_back:
                finish();
                break;
            case R.id.title_right:

                String trim = edContent.getText().toString().trim();
                if(!TextUtils.isEmpty(trim)){
                    reply(trim);
                }else{
                    Toast.makeText(this, "请填写回复内容", Toast.LENGTH_SHORT).show();
                }


                break;
        }
    }
}
