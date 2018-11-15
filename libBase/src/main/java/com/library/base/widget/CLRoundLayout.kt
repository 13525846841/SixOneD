package com.library.base.widget

import android.content.Context
import android.graphics.*
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet
import com.library.base.R

class CLRoundLayout : ConstraintLayout {
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        val array = context.obtainStyledAttributes(attrs, R.styleable.CLRoundLayout)
        var radiiTopLeft = array.getDimensionPixelOffset(R.styleable.CLRoundLayout_rRadii_top_left, 0).toFloat()
        var radiiTopRight = array.getDimensionPixelOffset(R.styleable.CLRoundLayout_rRadii_top_right, 0).toFloat()
        var radiiBottomLeft = array.getDimensionPixelOffset(R.styleable.CLRoundLayout_rRadii_bottom_left, 0).toFloat()
        var radiiBottomRight = array.getDimensionPixelOffset(R.styleable.CLRoundLayout_rRadii_bottom_right, 0).toFloat()
        val radiu = array.getDimensionPixelOffset(R.styleable.CLRoundLayout_rRadii, 0).toFloat()
        if (radiu != 0f) {
            radiiTopLeft = radiu
            radiiTopRight = radiiTopLeft
            radiiBottomLeft = radiiTopRight
            radiiBottomRight = radiiBottomLeft
        }
        mRadiis = floatArrayOf(radiiTopLeft, radiiTopLeft,
                radiiTopRight, radiiTopRight,
                radiiBottomLeft, radiiBottomLeft,
                radiiBottomRight, radiiBottomRight)
    }

    private val mRectF: RectF = RectF()
    private val mPath: Path = Path()
    private var mRadiis: FloatArray
    private val mPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    init {
        mPaint.style = Paint.Style.FILL
        mPaint.color = Color.WHITE
        mPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_IN)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mRectF.left = paddingLeft.toFloat()
        mRectF.top = paddingTop.toFloat()
        mRectF.right = (width - paddingRight).toFloat()
        mRectF.bottom = (height - paddingBottom).toFloat()
        mPath.reset()
        mPath.addRoundRect(mRectF, mRadiis, Path.Direction.CW)
    }

    override fun draw(canvas: Canvas) {
        canvas.save()
        canvas.clipPath(mPath)
        super.draw(canvas)
        canvas.restore()
    }

    override fun dispatchDraw(canvas: Canvas) {
        val saveLayer = canvas.saveLayer(null, null, Canvas.ALL_SAVE_FLAG)
        super.dispatchDraw(canvas)
        canvas.drawPath(mPath, mPaint)
        canvas.restoreToCount(saveLayer)
    }
}