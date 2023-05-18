package com.acun.storyapp.ui.customview

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.acun.storyapp.databinding.AppButtonLayoutBinding
import com.acun.storyapp.utils.toGone
import com.acun.storyapp.utils.toVisible

class AppButton: ConstraintLayout {

    private lateinit var binding: AppButtonLayoutBinding
    private var text = ""

    constructor(context: Context) : super(context) {
        initView(context)
    }
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initView(context)
    }
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initView(context)
    }

    private fun initView(context: Context) {
        binding = AppButtonLayoutBinding.inflate(LayoutInflater.from(context), this, true)
    }

    fun setText(string: String) {
        text = string
        binding.button.text = text
    }

    fun isLoading(boolean: Boolean) {
        if (boolean) {
            binding.button.text = ""
            binding.progressBar.toVisible()
        } else {
            binding.button.text = text
            binding.progressBar.toGone()
        }
    }

    fun setEnable(isEnable: Boolean) {
        binding.button.isEnabled = isEnable
    }

    override fun setOnClickListener(l: OnClickListener?) {
        super.setOnClickListener(l)
        binding.button.setOnClickListener(l)
    }
}