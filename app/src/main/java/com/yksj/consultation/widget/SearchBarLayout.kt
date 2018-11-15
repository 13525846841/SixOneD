package com.yksj.consultation.widget

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.inputmethod.EditorInfo
import com.library.base.kt.resString
import com.yksj.consultation.sonDoc.R
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.layout_search.view.*
import java.util.concurrent.TimeUnit

/**
 *搜索条
 */
class SearchBarLayout : ConstraintLayout {
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private var onSearchChangeListener: OnSearchChangeListener? = null

    init {
        LayoutInflater.from(context).inflate(R.layout.layout_search, this)

        // 监听用户输入变化
        val textChangeSubject = PublishSubject.create<CharSequence>()
        et_search.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                textChangeSubject.onNext(s)
            }
        })
        textChangeSubject
                .debounce(600, TimeUnit.MILLISECONDS)//过滤连续输入操作
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { onSearchChangeListener?.onChanged(it.toString()) }
        // 点击搜索
        et_search.setOnEditorActionListener { v, actionId, _ ->
            var consumed = actionId == EditorInfo.IME_ACTION_SEARCH
            if (consumed) onSearchChangeListener?.onChanged(v.text.toString())
            return@setOnEditorActionListener consumed
        }
    }

    /**
     * 监听文字变化
     */
    fun setOnSearchChangeListener(listener: OnSearchChangeListener) {
        this.onSearchChangeListener = listener;
    }

    /**
     * 设置暗示文字
     */
    fun setSearchHint(hint: String) {
        et_search.hint = hint
    }

    /**
     * 设置暗示文字
     */
    fun setSearchHint(resId: Int) {
        val str = resString(resId)
        et_search.hint = str
    }

    interface OnSearchChangeListener {
        fun onChanged(str: String)
    }
}