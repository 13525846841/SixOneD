package com.yksj.consultation.news;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;

import com.yksj.consultation.adapter.KnowledgeFragmentPagerAdapter;
import com.library.base.base.BaseFragment;
import com.library.base.base.BaseTitleActivity;
import com.yksj.consultation.dialog.WaitDialog;
import com.yksj.consultation.sonDoc.R;
import com.yksj.consultation.bean.NewsClass;
import com.yksj.healthtalk.net.http.ApiCallbackWrapper;
import com.yksj.healthtalk.net.http.ApiService;
import com.yksj.healthtalk.utils.ScreenUtils;
import com.yksj.healthtalk.utils.ToastUtil;
import com.yksj.healthtalk.views.ColumnHorizontalScrollView;
import com.yksj.healthtalk.views.MyViewPager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Request;

/**
 * 信息中心
 */
public class NewsCenterActivity extends BaseTitleActivity implements View.OnClickListener {
    /**
     * 左阴影部分
     */
    public ImageView shade_left;
    /**
     * 右阴影部分
     */
    public ImageView shade_right;
    /**
     * 屏幕宽度
     */
    private int mScreenWidth = 0;
    /**
     * Item宽度
     */
    private int mItemWidth = 0;

    WaitDialog dialog;//网络加载提示
    KnowledgeFragmentPagerAdapter mAdapetr;
    List<NewsClass> knowledgelist = new ArrayList<NewsClass>();//知识库分类
    RelativeLayout rl_column;
    LinearLayout mRadioGroup_content;
    LinearLayout ll_more_columns;
    private int columnSelectIndex = 0;
    private ArrayList<BaseFragment> fragments = new ArrayList<BaseFragment>();
    private ImageView button_more_columns;
    private MyViewPager mViewPager;
    private ColumnHorizontalScrollView mColumnHorizontalScrollView;
    int savePosition = 0;
    private LayoutInflater mInflater;

    public static final String TYPE = "type";
    String type = "";

//    @Override
//    protected void onCreate(Bundle arg0) {
//        super.onCreate(arg0);
//        setContentView(R.layout.aty_knowlegebase);
//        if (!HTalkApplication.getApplication().isNetWork()) {
//            SingleBtnFragmentDialog.showDefault(getSupportFragmentManager(), "网络不可用");
//            return;
//        }
////       EventBus.getDefault().register(this);
//        initView();
//    }

    @Override
    public int createLayoutRes() {
        return R.layout.aty_knowlegebase;
    }

    @Override
    public void initialize(Bundle bundle) {
        super.initialize(bundle);
        mInflater = LayoutInflater.from(this);
        if (getIntent().hasExtra(TYPE)) type = getIntent().getStringExtra(TYPE);
        if ("News".equals(type)) {
            titleTextV.setText("新闻中心");
        } else if ("Encyclopedia".equals(type)) {
            titleTextV.setText("六一百科");
        }
        setTitle(getString(R.string.sod_news_center));
        if (getIntent().hasExtra("TITLE")) titleTextV.setText(getIntent().getStringExtra("TITLE"));
        mScreenWidth = ScreenUtils.getScreenWidth(this);
//        mItemWidth = mScreenWidth / 4;
        mColumnHorizontalScrollView = (ColumnHorizontalScrollView) findViewById(R.id.mColumnHorizontalScrollView);
        rl_column = (RelativeLayout) findViewById(R.id.rl_column);
        mRadioGroup_content = (LinearLayout) findViewById(R.id.mRadioGroup_content);
        shade_left = (ImageView) findViewById(R.id.shade_left);
        shade_right = (ImageView) findViewById(R.id.shade_right);
        ll_more_columns = (LinearLayout) findViewById(R.id.ll_more_columns);
        button_more_columns = (ImageView) findViewById(R.id.button_more_columns);
        button_more_columns.setOnClickListener(this);
        mViewPager = (MyViewPager) findViewById(R.id.mViewPager);
        requestTab();
    }

    /**
     * ViewPager切换监听方法
     */
    public ViewPager.OnPageChangeListener pageListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageSelected(int position) {
            // TODO Auto-generated method stub
            mViewPager.setCurrentItem(position);
            selectTab(position);
        }
    };

    /**
     * 获取知识库分类
     */
    public void requestTab() {
        ApiService.OKHttpNewsTab(type, new ApiCallbackWrapper<JSONObject>() {
            @Override
            public void onBefore(Request request) {
                super.onBefore(request);
                dialog = WaitDialog.showLodingDialog(getSupportFragmentManager(), getResources());
            }

            @Override
            public void onAfter() {
                super.onAfter();
                dialog.dismissAllowingStateLoss();
            }

            @Override
            public void onResponse(JSONObject response) {
                if (response.optInt("code") == 1) {
                    try {
                        knowledgelist.clear();//数据清空
                        JSONArray array = response.getJSONArray("result");
                        int count = array.length();
                        if (count > 0) {
                            for (int i = 0; i < count; i++) {
                                knowledgelist.add(NewsClass.parseFormat((JSONObject) array.get(i)));
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
//                    KBAddChildAty.setKnowledgebase(knowledgelist);
                    initTabColumn();
                    initFragment();
                    findViewById(R.id.mColumnHorizontalScrollView).setVisibility(View.VISIBLE);
                    if (knowledgelist.size() <= 1) {
                        findViewById(R.id.ll_title).setVisibility(View.GONE);
                        findViewById(R.id.line2).setVisibility(View.GONE);
                    }
                } else {
                    ToastUtil.showShort(response.optString("message"));
                }
            }
        });
    }

    /**
     * 初始化Fragment
     */
    private void initFragment() {
        fragments.clear();//清空fragment
        final int count = knowledgelist.size();

        if (count > 0) {
            for (int i = 0; i < count; i++) {
                NewsListFragment newfragment = NewsListFragment.newInstance(knowledgelist.get(i).getINFO_CLASS_ID()
                        , knowledgelist.get(i).getINFO_CLASS_NAME());
                fragments.add(newfragment);
            }
        } else if (count == 0) {
            NewsListFragment newfragment = NewsListFragment.newInstance(
                    getIntent().getStringExtra("INFO_CLASS_ID")
                    , getIntent().getStringExtra("INFO_CLASS_NAME"));
            fragments.add(newfragment);
        }
        mAdapetr = new KnowledgeFragmentPagerAdapter(getSupportFragmentManager(), fragments);
        mViewPager.setOffscreenPageLimit(0);
        mViewPager.setAdapter(mAdapetr);
        mViewPager.setOnPageChangeListener(pageListener);
    }

    /**
     * 初始化知识库栏目项
     */
    private void initTabColumn() {
        mRadioGroup_content.removeAllViews();//清空title
        int count = knowledgelist.size();
        mRadioGroup_content.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
        mRadioGroup_content.setDividerDrawable(getResources().getDrawable(R.drawable.common_line_vertical));
        mColumnHorizontalScrollView.setParam(this, mScreenWidth, mRadioGroup_content, shade_left, shade_right, ll_more_columns, rl_column);
        if (count > 0) {
            if (count <= 4) {
                mItemWidth = mScreenWidth / count;
            } else {
                mItemWidth = mScreenWidth / 4;
            }
        } else {
            return;
        }
        for (int i = 0; i < count; i++) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(mItemWidth, ViewGroup.LayoutParams.MATCH_PARENT);
//            params.leftMargin = 10;
//            params.rightMargin = 10;
//            View view = mInflater.inflate(R.layout.colunm_radio_item, null);//左侧item
            RadioButton columnTextView = (RadioButton) mInflater.inflate(R.layout.colunm_radio_item, null);//左侧item

//			View view =  View.inflate(this,R.layout.colunm_radio_item, null);

//            TextView columnTextView = new TextView(this);
//            RadioButton columnTextView = (RadioButton) view.findViewById(R.id.rb_text);
//            columnTextView.setTextAppearance(this, R.style.top_category_scroll_view_item_text);
//			localTextView.setBackground(getResources().getDrawable(R.drawable.top_category_scroll_text_view_bg));
//            columnTextView.setBackgroundResource(R.drawable.radio_buttong_bg);
//            columnTextView.setBackgroundResource(R.drawable.radio_buttong_bg);
//            columnTextView.setBackgroundResource(R.drawable.rb_top_text_bg);
//            columnTextView.setBackgroundResource(R.drawable.tab_bg);
            columnTextView.setGravity(Gravity.CENTER);
//            columnTextView.setPadding(10, 10, 10, 10);
            columnTextView.setId(i);
            columnTextView.setTextSize(16);
            columnTextView.setText(knowledgelist.get(i).getINFO_CLASS_NAME());
            columnTextView.setTextColor(getResources().getColorStateList(R.color.top_category_scroll_text_color_day));
            if (columnSelectIndex == i) {
                columnTextView.setSelected(true);
            }
            columnTextView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    for (int i = 0; i < mRadioGroup_content.getChildCount(); i++) {
                        RadioButton localView = (RadioButton) mRadioGroup_content.getChildAt(i);
                        if (localView != v)
//                            localView.setSelected(false);
                            localView.setChecked(false);
                        else {
//                            localView.setSelected(true);
                            localView.setChecked(true);
                            mViewPager.setCurrentItem(i);
                        }
                    }
                }
            });
            mRadioGroup_content.addView(columnTextView, i, params);
            if (mRadioGroup_content.getChildCount() > 0) {
                RadioButton checkView = (RadioButton) mRadioGroup_content.getChildAt(0);
                checkView.setChecked(true);
            }
            if (mRadioGroup_content.getChildCount() > 3) {
                findViewById(R.id.ll_line).setVisibility(View.GONE);
            }
//            mRadioGroup_content.addView(view, i, params);
        }
    }

    /**
     * 选择的Column里面的Tab
     */
    private void selectTab(int tab_postion) {
        columnSelectIndex = tab_postion;
        for (int i = 0; i < mRadioGroup_content.getChildCount(); i++) {
            View checkView = mRadioGroup_content.getChildAt(tab_postion);
            int k = checkView.getMeasuredWidth();
            int l = checkView.getLeft();
            int i2 = l + k / 2 - mScreenWidth / 2;
            // rg_nav_content.getParent()).smoothScrollTo(i2, 0);
            mColumnHorizontalScrollView.smoothScrollTo(i2, 0);
            // mColumnHorizontalScrollView.smoothScrollTo((position - 2) *
            // mItemWidth , 0);
        }


        for (int j = 0; j < mRadioGroup_content.getChildCount(); j++) {
            RadioButton checkView = (RadioButton) mRadioGroup_content.getChildAt(j);
            checkView.setSelected(true);
        }

        //判断是否选中
        for (int j = 0; j < mRadioGroup_content.getChildCount(); j++) {
//            RadioButton checkView = (RadioButton) mRadioGroup_content.getChildAt(j).findViewById(R.id.rb_text);
            RadioButton checkView = (RadioButton) mRadioGroup_content.getChildAt(j);
            boolean ischeck;
            if (j == tab_postion) {
                ischeck = true;
            } else {
                ischeck = false;
            }
//            checkView.setSelected(ischeck);
            checkView.setChecked(ischeck);
        }
    }


}

