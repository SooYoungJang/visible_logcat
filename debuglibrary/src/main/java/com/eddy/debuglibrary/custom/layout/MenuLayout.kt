package com.eddy.debuglibrary.custom.layout

import android.content.Context
import android.content.res.Resources
import android.content.res.TypedArray
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import com.example.debuglibrary.R
import com.example.debuglibrary.databinding.MenuLayoutBinding


class MenuLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private var binding = MenuLayoutBinding.inflate(LayoutInflater.from(context), this, true)
    private val viewList: MutableList<View> = mutableListOf()

    private var isRightImageView: Boolean = false
    private var isLeftImageVIew: Boolean = false
    private var isLeftTextView: Boolean = false
    private var isRightTextView: Boolean = false
    private var isRightCheckBox: Boolean = false
    private var isLeftCheckBox: Boolean = false
    private var rightImageDrawable: Drawable? = null
    private var leftImageDrawable: Drawable? = null
    private var rightCheckBoxDrawable: Drawable? = null
    private var leftCheckBoxDrawable: Drawable? = null
    private var rightText: CharSequence = ""
    private var leftText: CharSequence = ""
    private var rightTextSize: Float = 0f
    private var leftTextSize: Float = 0f

//    private val rightTextView: TextView by lazy { TextView(context).also { it.id = View.generateViewId() }.also { viewList.add(it) } }
//    private val leftTextView: TextView by lazy { TextView(context).also { it.id = View.generateViewId() }.also { viewList.add(it) } }
//    private val rightImageView: ImageView by lazy { ImageView(context).also { it.id = View.generateViewId() }.also {  viewList.add(it) } }
//    private val leftImageView: ImageView by lazy { ImageView(context).also { it.id = View.generateViewId() }.also { viewList.add(it) } }
//    private val rightCheckBox: CheckBox by lazy { CheckBox(context).also { it.id = View.generateViewId() }.also { viewList.add(it) } }
//    private val leftCheckBox: CheckBox by lazy { CheckBox(context).also { it.id = View.generateViewId() }.also { viewList.add(it) } }

    private lateinit var rightTextView: TextView
    private lateinit var leftTextView: TextView
    private lateinit var rightImageView: ImageView
    private lateinit var leftImageView: ImageView
    private lateinit var rightCheckBox: CheckBox
    private lateinit var leftCheckBox: CheckBox

    private val attrs: TypedArray by lazy { context.obtainStyledAttributes(attrs, R.styleable.MenuLayout) }
    private val constraintSet: ConstraintSet by lazy { ConstraintSet().also { it.clone(binding.root) } }

    fun setRightImageBackgroundDrawable(value: Drawable?){ rightImageView.background = value?.mutate() }

    fun setRightImageBackgroundDrawableResource(@DrawableRes value: Int) { rightImageView.background = AppCompatResources.getDrawable(context, value)?.mutate() }

    fun setLeftImageBackgroundDrawable(value: Drawable?){ leftImageView.background = value?.mutate() }

    fun setLeftImageBackgroundDrawableResource(@DrawableRes value: Int) { leftImageView.background = AppCompatResources.getDrawable(context, value)?.mutate() }

    fun setRightCheckBoxBackgroundDrawable(value: Drawable?){ rightCheckBox.background = value?.mutate() }

    fun setRightCheckBoxBackgroundDrawableResource(@DrawableRes value: Int) { rightCheckBox.background = AppCompatResources.getDrawable(context, value)?.mutate() }

    fun setLeftCheckBoxBackgroundDrawable(value: Drawable?){ leftCheckBox.background = value?.mutate() }

    fun setLeftCheckBoxBackgroundDrawableResource(@DrawableRes value: Int) { leftCheckBox.background = AppCompatResources.getDrawable(context, value)?.mutate() }

    fun setRightText(value: CharSequence) { rightTextView.text = value }

    fun setRightTextResource(@StringRes value: Int) { rightTextView.text = context.getString(value) }

    fun setLeftText(value: CharSequence) { leftTextView.text = value }

    fun setLeftTextResource(@StringRes value: Int) { leftTextView.text = context.getString(value) }

    fun setRightTextSize(value: Float) { rightTextView.textSize = value }

    fun setLeftTextSize(value: Float) { leftTextView.textSize = value }

    init {
        setLeftTextView()
        setRightTextView()
        setRightImageView()
        setLeftImageView()
        setRightCheckBox()
        setLeftCheckBox()

        for (view in viewList) {
            Log.d("test","eddy null이 들어오냐? :::::  WifiHW $view")
            binding.root.addView(view)
        }

        setConstraintLeftTextView()
        setConstraintRightTextView()
        setConstraintRightImageView()
        setConstraintLeftImageView()
        setConstraintRightCheckBox()
        setConstraintLeftCheckBox()

        constraintSet.applyTo(binding.root)
    }

    private fun setConstraintLeftTextView() {
        constraintSet.apply {
            connect(leftTextView.id, ConstraintSet.START, R.id.root, ConstraintSet.START, 18)
            connect(leftTextView.id, ConstraintSet.TOP, R.id.root, ConstraintSet.TOP, 18)
        }
    }

    private fun setConstraintRightTextView() {
        constraintSet.apply {
            connect(rightTextView.id, ConstraintSet.END, R.id.root, ConstraintSet.END, 18)
            connect(rightTextView.id, ConstraintSet.TOP, R.id.root, ConstraintSet.TOP, 18)
        }
    }

    private fun setConstraintRightImageView() {
        if(rightTextView == null) {
            Log.d("test","eddy null이 들어오냐?   WifiHW")
            constraintSet.apply {
                connect(rightImageView.id, ConstraintSet.END, R.id.root, ConstraintSet.END, 18)
                connect(rightImageView.id, ConstraintSet.TOP, R.id.root, ConstraintSet.TOP, 18)
            }
        } else {
            constraintSet.apply {
                connect(rightImageView.id, ConstraintSet.END, rightTextView.id, ConstraintSet.START, 18)
                connect(rightImageView.id, ConstraintSet.TOP, rightTextView.id, ConstraintSet.TOP, 18)
            }
        }

    }

    private fun setConstraintLeftImageView() {
        constraintSet.apply {
            connect(leftImageView.id, ConstraintSet.END, R.id.root, ConstraintSet.END, 18)
            connect(leftImageView.id, ConstraintSet.TOP, R.id.root, ConstraintSet.TOP, 18)
        }
    }

    private fun setConstraintRightCheckBox() {
        constraintSet.apply {
            connect(rightCheckBox.id, ConstraintSet.END, R.id.root, ConstraintSet.END, 18)
            connect(rightCheckBox.id, ConstraintSet.TOP, R.id.root, ConstraintSet.TOP, 18)
        }
    }

    private fun setConstraintLeftCheckBox() {
        constraintSet.apply {
            connect(leftCheckBox.id, ConstraintSet.END, R.id.root, ConstraintSet.END, 18)
            connect(leftCheckBox.id, ConstraintSet.TOP, R.id.root, ConstraintSet.TOP, 18)
        }
    }

    private fun setLeftTextView() {
        leftTextView.text = attrs.getString(R.styleable.MenuLayout_setLeftText)
        leftTextView.textSize = attrs.getFloat(R.styleable.MenuLayout_setLeftTextSize, 0f)
        leftTextView.layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
    }

    private fun setRightTextView() {
        rightTextView.text = attrs.getString(R.styleable.MenuLayout_setRightText)
        rightTextView.textSize = attrs.getFloat(R.styleable.MenuLayout_setRightTextSize, 0f)
        rightTextView.layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
    }

    private fun setRightImageView() {
        rightImageView.setImageResource(attrs.getResourceId(R.styleable.MenuLayout_setRightImageBackgroundDrawable, 0))
        rightImageView.setImageResource(attrs.getResourceId(R.styleable.MenuLayout_setRightImageBackgroundDrawableResource, 0))
        rightImageView.layoutParams = LayoutParams(38.toDp(), 38.toDp())
    }

    private fun setLeftImageView() {
        leftImageView.setImageResource(attrs.getResourceId(R.styleable.MenuLayout_setLeftImageBackgroundDrawable, 0))
        leftImageView.setImageResource(attrs.getResourceId(R.styleable.MenuLayout_setLeftImageBackgroundDrawableResource, 0))
        leftImageView.layoutParams = LayoutParams(38.toDp(), 38.toDp())
    }

    private fun setRightCheckBox() {
        rightCheckBox.background = attrs.getDrawable(R.styleable.MenuLayout_setRightCheckBoxBackgroundDrawable)
        rightCheckBox.background = attrs.getDrawable(R.styleable.MenuLayout_setRightCheckBoxBackgroundDrawableResource)
        rightCheckBox.layoutParams = LayoutParams(38.toDp(), 38.toDp())
    }

    private fun setLeftCheckBox() {
        leftCheckBox.background = attrs.getDrawable(R.styleable.MenuLayout_setLeftCheckBoxBackgroundDrawable)
        leftCheckBox.background = attrs.getDrawable(R.styleable.MenuLayout_setLeftCheckBoxBackgroundDrawableResource)
        leftCheckBox.layoutParams = LayoutParams(38.toDp(), 38.toDp())
    }

}


private fun Int.toDp(): Int {
    return (this * Resources.getSystem().displayMetrics.density + 0.5f).toInt()
}