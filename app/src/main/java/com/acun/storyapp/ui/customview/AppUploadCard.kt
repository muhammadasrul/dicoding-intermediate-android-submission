package com.acun.storyapp.ui.customview

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import coil.load
import com.acun.storyapp.R
import com.acun.storyapp.databinding.AppUploadCardLayoutBinding
import com.acun.storyapp.utils.toGone
import com.acun.storyapp.utils.toVisible

class AppUploadCard: ConstraintLayout {

    private lateinit var binding: AppUploadCardLayoutBinding
    private var onClickListener: OnCardButtonClickListener? = null

    constructor(context: Context) : super(context) {
        initView(context, null, null)
    }
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initView(context, attrs, null)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initView(context, attrs, defStyleAttr)
    }

    private fun initView(context: Context?, attrs: AttributeSet?, defStyleAttr: Int?) {
        binding = AppUploadCardLayoutBinding.inflate(LayoutInflater.from(context), this, true)
        reset()
        with(binding) {
            cameraButton.setOnClickListener {
                onClickListener?.onCameraButtonClicked()
            }
            galleryButton.setOnClickListener {
                onClickListener?.onGalleryButtonClicked()
            }
        }
    }

    fun setListener(listener: OnCardButtonClickListener) {
        onClickListener = listener
    }

    private fun reset() {
        with(binding) {
            imageContainer.toGone()
            fileNameTextView.toGone()
            root.background = ContextCompat.getDrawable(context, R.drawable.bg_empty_upload_card)

            galleryButton.toVisible()
            cameraButton.toVisible()
            orTextView.toVisible()
        }
    }

    fun setValue(image: String, fileName: String) {
        with(binding) {
            imageView.load(image)
            fileNameTextView.text = fileName
            imageContainer.toVisible()
            fileNameTextView.toVisible()
            root.background = ContextCompat.getDrawable(context, R.drawable.bg_upload_card)

            galleryButton.toGone()
            cameraButton.toGone()
            orTextView.toGone()
        }
    }

    fun setUploadProgress(progress: Int) {
        when (progress > 100) {
            true -> binding.uploadProgress.toVisible()
            else -> binding.uploadProgress.toGone()
        }
        binding.uploadProgress.progress = progress
    }

    interface OnCardButtonClickListener {
        fun onCameraButtonClicked()
        fun onGalleryButtonClicked()
    }
}
