package com.yksj.consultation.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.blankj.utilcode.util.SizeUtils;
import com.yksj.consultation.sonDoc.R;

public class ClearEditView extends RelativeLayout implements TextWatcher {

    private Drawable mClearDrawable;
    private EditText mEditText;
    private FrameLayout mClearView;
    private Context mContext;
    private int mEditTextColor;
    private int mEditTextSize;
    private String mEditHint;
    private int mEditInputType;
    private EditTextChangeListener mEditTextChangeListener;

    public ClearEditView(Context context) {
        this(context, null);
    }

    public ClearEditView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ClearEditView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        getAttr(attrs);
        initEditView();
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                getViewTreeObserver().removeOnGlobalLayoutListener(this);
                initClearView();
            }
        });
    }

    private void getAttr(AttributeSet attrs) {
        TypedArray typedArray = mContext.obtainStyledAttributes(attrs, R.styleable.ClearEditView);
        mEditTextColor = typedArray.getColor(R.styleable.ClearEditView_ceEditTextColor, Color.parseColor("#ffffff"));
        mEditTextSize = typedArray.getDimensionPixelSize(R.styleable.ClearEditView_ceEditTextSize, 16);
        mEditHint = typedArray.getString(R.styleable.ClearEditView_ceEditHint);
        mClearDrawable = typedArray.getDrawable(R.styleable.ClearEditView_ceClearDrawable);
        mEditInputType = typedArray.getInt(R.styleable.ClearEditView_ceEditInputType, 0);
        typedArray.recycle();
    }

    /**
     * 初始化清除按钮
     */
    private void initClearView() {
        mClearView = new FrameLayout(getContext());
        mClearView.setId(R.id.clear_view);
        mClearView.setOnClickListener(v -> mEditText.setText(""));
        LayoutParams lp = new LayoutParams(getMeasuredHeight(), getMeasuredHeight());
        lp.addRule(ALIGN_PARENT_RIGHT, TRUE);
        lp.addRule(CENTER_VERTICAL, TRUE);
        mClearView.setLayoutParams(lp);
        ImageView chilView = new ImageView(getContext());
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(-2, -2);
        layoutParams.gravity = Gravity.CENTER;
        chilView.setLayoutParams(layoutParams);
        mClearDrawable = getResources().getDrawable(R.drawable.ic_edit_clear);
        chilView.setImageDrawable(mClearDrawable);
        mClearView.addView(chilView);
        addView(mClearView);
    }

    /**
     * 初始化编辑框
     */
    private void initEditView() {
        mEditText = new EditText(getContext());
        mEditText.setId(R.id.edit_view);
        mEditText.setBackground(null);
        mEditText.setMaxLines(1);
        mEditText.setTextSize(TypedValue.COMPLEX_UNIT_PX, mEditTextSize);
        mEditText.setTextColor(mEditTextColor);
        mEditText.setHint(mEditHint);
        if (mEditInputType == 2) {
            mEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        } else if (mEditInputType == 1) {
            mEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
        }
        LayoutParams lp = new LayoutParams(-1, -2);
        lp.addRule(CENTER_VERTICAL, TRUE);
        lp.addRule(LEFT_OF, R.id.clear_view);
        lp.setMargins(SizeUtils.dp2px(8), 0, 0, 0);
        mEditText.setLayoutParams(lp);
        mEditText.addTextChangedListener(this);
        addView(mEditText);
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (mEditTextChangeListener != null) {
            mEditTextChangeListener.onTextChanged(s, start, before, count);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void afterTextChanged(Editable s) {
    }

    /**
     * 获取输入的内容
     * @return
     */
    public String getEditText() {
        return mEditText.getText().toString();
    }

    /**
     * 设置输入的内容
     * @param text
     */
    public void setEditText(String text) {
        if (mEditText != null && !TextUtils.isEmpty(text)) {
            mEditText.setText(text);
        }
    }

    /**
     * 设置输入内容变化
     * @param listener
     */
    public void setOnEditTextChangeListener(EditTextChangeListener listener) {
        mEditTextChangeListener = listener;
    }

    public interface EditTextChangeListener {
        void onTextChanged(CharSequence s, int start, int before, int count);
    }
}
