package com.library.base.widget

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import android.widget.Checkable
import com.blankj.utilcode.util.SizeUtils
import com.library.base.R

class SwitchButton : View, Checkable {

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        val array = context.obtainStyledAttributes(attrs, R.styleable.SwitchButton)
        isChecked = array.getBoolean(R.styleable.SwitchButton_sbChecked, false)
        mButtonColor = array.getColor(R.styleable.SwitchButton_sbThumbColor, Color.WHITE)
        val typedValue = TypedValue()
        context.theme.resolveAttribute(R.attr.colorAccent, typedValue, true)
        mCheckColor = array.getColor(R.styleable.SwitchButton_sbCheckColor, Color.LTGRAY)
        mUncheckColor = array.getColor(R.styleable.SwitchButton_sbUnCheckColor, Color.TRANSPARENT)
    }

    private val DEFAULT_WIDTH = SizeUtils.dp2px(58f)
    private val DEFAULT_HEIGHT = SizeUtils.dp2px(36f)
    private var mStrokeWidth = SizeUtils.dp2px(1f).toFloat()
    private var mStrokeColor = Color.parseColor("#ffDDDDDD")
    private var mBackgroundColor = Color.WHITE
    private var mCheckColor = 0
    private var mUncheckColor = 0
    private var mHeight = 0
    private var mWidth = 0
    private var mRadius = 0f
    private val mRect = RectF()
    private var mCenterX = 0f
    private var mCenterY = 0f
    private var mSlideMin = 0f
    private var mSlideMax = 0f
    private var mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var mButtonPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var mButtonRadius = 0f
    private var mButtonColor = 0
    private var mButtonShadowRadius = SizeUtils.dp2px(2.5f).toFloat()
    private var mButtonShadowOffset = SizeUtils.dp2px(1.5f).toFloat()
    private var mButtonShadowColor = Color.parseColor("#33000000")
    private var isChecked = false
    private var valueAnimation = ValueAnimator.ofFloat(0f, 1f)
    private var mState = ViewState()
    private var mAfterState = ViewState()
    private var mBeforeState = ViewState()
    private var mArgbEvaluator = ArgbEvaluator()
    private var mTouchTime = 0L
    private var mTouchX = 0f
    private var mSwitchType = SwitchType.NONE
    private var mSwitchChangeListener: OnSwitchChangeListener? = null
    private var mAnimatorUPdateListener = ValueAnimator.AnimatorUpdateListener { animation ->
        val value = animation.animatedValue as Float
        mState.slideX = mBeforeState.slideX + (mAfterState.slideX - mBeforeState.slideX) * value
        val fraction = (mState.slideX - mSlideMin) / (mSlideMax - mSlideMin)
        mState.checkColor = mArgbEvaluator.evaluate(fraction, mUncheckColor, mCheckColor) as Int
        mState.radius = fraction * mRadius
        postInvalidate()
    }

    init {
        // 关闭硬件加速
        setLayerType(View.LAYER_TYPE_SOFTWARE, null)
        valueAnimation.duration = 200
        valueAnimation.addUpdateListener(mAnimatorUPdateListener)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        val padding = Math.max(mButtonShadowRadius + mButtonShadowOffset, mStrokeWidth)
        mHeight = h
        mWidth = w
        mRadius = h * .5f
        mButtonRadius = mRadius - padding
        mRect.left = padding
        mRect.top = padding
        mRect.right = w.toFloat() - padding
        mRect.bottom = h.toFloat() - padding
        mCenterX = (mRect.left + mRect.right) * .5f
        mCenterY = (mRect.top + mRect.bottom) * .5f
        mSlideMin = mRect.left + mButtonRadius
        mSlideMax = mRect.right - mButtonRadius
        if (isChecked) {
            setCheckState(mState)
        } else {
            setUncheckState(mState)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)

        var widthSpec = 0
        if (widthMode == MeasureSpec.UNSPECIFIED || widthMode == MeasureSpec.AT_MOST) {
            widthSpec = MeasureSpec.makeMeasureSpec(DEFAULT_WIDTH, MeasureSpec.EXACTLY)
        }
        var heightSpace = 0
        if (heightMode == MeasureSpec.UNSPECIFIED || heightMode == MeasureSpec.AT_MOST) {
            heightSpace = MeasureSpec.makeMeasureSpec(DEFAULT_HEIGHT, MeasureSpec.AT_MOST)
        }

        super.onMeasure(widthSpec, heightSpace)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // 绘制背景
        mPaint.style = Paint.Style.FILL
        mPaint.color = mBackgroundColor
        drawRoundBg(canvas, mRect, mRadius, mPaint)
        // 绘制关闭状态边框
        mPaint.strokeWidth = mStrokeWidth
        mPaint.style = Paint.Style.STROKE
        mPaint.color = mStrokeColor
        drawRoundBg(canvas, mRect, mRadius, mPaint)
        // 绘制开启状态背景
        val radius = mState.radius * .5f
        mPaint.style = Paint.Style.STROKE
        mPaint.color = mState.checkColor
        mPaint.strokeWidth = mStrokeWidth + radius * 2
        val rect = RectF(mRect)
        rect.set(rect.left + radius, rect.top + radius, rect.right - radius, rect.bottom - radius)
        drawRoundBg(canvas, rect, mRadius, mPaint)

        // 绘制圆形按钮
        drawButton(canvas, mState.slideX, mCenterY, mButtonRadius, mButtonPaint)
    }

    private fun drawRoundBg(canvas: Canvas, rect: RectF, radius: Float, paint: Paint) {
        canvas.drawRoundRect(rect, radius, radius, paint)
    }

    private fun drawButton(canvas: Canvas, x: Float, y: Float, radius: Float, paint: Paint) {
        paint.style = Paint.Style.FILL
        paint.color = mButtonColor
        paint.setShadowLayer(mButtonShadowRadius, 0f, mButtonShadowOffset, mButtonShadowColor)
        canvas.drawCircle(x, y, radius, paint)
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 1f
        paint.color = Color.parseColor("#ffDDDDDD")
        paint.setShadowLayer(0f, 0f, 0f, 0)
        canvas.drawCircle(x, y, radius, paint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!isEnabled) return false
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                mTouchTime = System.currentTimeMillis()
                mTouchX = event.x
                return true
            }

            MotionEvent.ACTION_MOVE -> {
                mSwitchType = SwitchButton.SwitchType.DRAG
                var percent: Float
                percent = event.x / mWidth
                percent = Math.max(0f, Math.min(1f, percent))
                mState.checkColor = mArgbEvaluator.evaluate(percent, mUncheckColor, mCheckColor) as Int
                mState.radius = percent * mRadius
                mState.slideX = getSlideX(event.x)
                mTouchX = event.x
                postInvalidate()
            }

            MotionEvent.ACTION_UP -> {
                if (mSwitchType != SwitchButton.SwitchType.DRAG && System.currentTimeMillis() - mTouchTime <= 300) {
                    mSwitchType = SwitchButton.SwitchType.CLICK
                    toggle()
                } else if (mSwitchType == SwitchButton.SwitchType.DRAG) {
                    val checked = event.x / mWidth > .5f
                    setChecked(checked)
                }
                mSwitchType = SwitchType.NONE
                mTouchTime = 0
                mTouchX = 0f
            }
        }
        return super.onTouchEvent(event)
    }

    private fun getSlideX(x: Float): Float {
        var slideX = mState.slideX + (x - mTouchX)
        if (slideX > mSlideMax) {
            slideX = mSlideMax
        } else if (slideX < mSlideMin) {
            slideX = mSlideMin
        }
        return slideX
    }

    private fun setCheckState(state: ViewState) {
        state.radius = mRadius
        state.checkColor = mCheckColor
        state.slideX = mSlideMax
    }

    private fun setUncheckState(state: ViewState) {
        state.radius = 0f
        state.checkColor = mUncheckColor
        state.slideX = mSlideMin
    }

    //***********public method*************

    /**
     * 设置check颜色
     */
    fun setCheckedColor(checkedColo: Int) {
        mCheckColor = checkedColo
        postInvalidate()
    }

    /**
     * 是否选择
     */
    override fun isChecked(): Boolean {
        return isChecked
    }

    /**
     * 打开/关闭
     */
    override fun toggle() {
        setChecked(!isChecked())
    }

    /**
     * 设置是否选中
     */
    override fun setChecked(checked: Boolean) {
        if (!isEnabled) return
        post {
            if (valueAnimation.isRunning) valueAnimation.cancel()
            isChecked = checked
            mSwitchChangeListener?.onChanged(isChecked)
            mBeforeState.clone(mState)
            if (isChecked()) {
                setCheckState(mAfterState)
            } else {
                setUncheckState(mAfterState)
            }
            valueAnimation.start()
        }
    }

    /**
     * 监听选择状态
     */
    fun setOnSwitchChangeListener(listener: OnSwitchChangeListener) {
        this.mSwitchChangeListener = listener
    }

    inner class ViewState {
        var radius = 0f
        var checkColor = 0
        var slideX = 0f

        fun clone(state: ViewState) {
            radius = state.radius
            checkColor = state.checkColor
            slideX = state.slideX
        }
    }

    enum class SwitchType {
        NONE, DRAG, CLICK
    }

    interface OnSwitchChangeListener {
        fun onChanged(checked: Boolean)
    }
}