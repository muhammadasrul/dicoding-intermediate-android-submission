package com.acun.storyapp.ui.customview

import android.content.Context
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Patterns
import android.view.LayoutInflater
import androidx.core.content.ContextCompat
import com.acun.storyapp.R
import com.acun.storyapp.databinding.AppEditTextLayoutBinding
import com.google.android.material.textfield.TextInputLayout

class AppEditText : TextInputLayout {

    private var inputType = Type.USER
    private var isError = false
    private var listener: TextChangeListener? = null

    lateinit var binding: AppEditTextLayoutBinding

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
        binding = AppEditTextLayoutBinding.inflate(LayoutInflater.from(context), this, true)
        binding.inputText.addTextChangedListener(textWatcher)
    }

    fun setType(type: Type) {
        inputType = type

        binding.apply {
            when (type) {
                Type.EMAIL -> {
                    inputText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
                    inputTextLayout.startIconDrawable = ContextCompat.getDrawable(context, R.drawable.email)
                }

                Type.PASSWORD -> {
                    inputText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                    inputTextLayout.apply {
                        startIconDrawable = ContextCompat.getDrawable(context, R.drawable.lock)
                        endIconMode = END_ICON_PASSWORD_TOGGLE
                        endIconDrawable = ContextCompat.getDrawable(context, R.drawable.ic_password_end_toggle)
                    }
                }

                Type.USER -> {
                    inputText.inputType = InputType.TYPE_CLASS_TEXT
                    inputTextLayout.startIconDrawable = ContextCompat.getDrawable(context, R.drawable.user)
                }
            }
        }
    }

    fun setListener(listener: TextChangeListener) {
        this.listener = listener
    }

    fun setHint(hint: String) {
        binding.inputTextLayout.hint = hint
    }

    fun getText(): String = binding.inputText.text.toString()

    private val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) = Unit
        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) = Unit
        override fun afterTextChanged(text: Editable?) {
            when (inputType) {
                Type.EMAIL -> {
                    if (!Patterns.EMAIL_ADDRESS.matcher(text.toString())
                            .matches() && !text.isNullOrEmpty()
                    ) {
                        error = resources.getString(R.string.email_validation_error)
                        isError = true
                    } else {
                        isError = false
                        isErrorEnabled = isError
                    }
                }

                Type.PASSWORD -> {
                    if (text.toString().length < 8 && !text.isNullOrEmpty()) {
                        error = resources.getString(R.string.password_validation_error)
                        isError = true
                    } else {
                        isError = false
                        isErrorEnabled = isError
                    }
                }

                Type.USER -> Unit
            }

            listener?.onTextChangeListener(isError)
        }
    }

    interface TextChangeListener {
        fun onTextChangeListener(isError: Boolean)
    }

    companion object {
        enum class Type { EMAIL, PASSWORD, USER }
    }
}