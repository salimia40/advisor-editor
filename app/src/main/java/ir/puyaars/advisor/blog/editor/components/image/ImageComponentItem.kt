package ir.puyaars.advisor.blog.editor.components.image

import android.annotation.SuppressLint
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import ir.puyaars.advisor.blog.R
import ir.puyaars.advisor.blog.editor.models.ComponentTag
import ir.puyaars.advisor.blog.editor.models.ImageComponentModel
import ir.puyaars.advisor.blog.editor.utils.UploadResListener
import ir.puyaars.advisor.blog.editor.utils.Uploader
import kotlinx.android.synthetic.main.image_component_item.view.*

class ImageComponentItem : FrameLayout, UploadResListener {

    private var isImageUploaded: Boolean = false

    private var isImageUploading: Boolean = false

    private var downloadUrl: String? = null
    var caption: String? = null
        set(caption) {
            field = caption
            val tag = tag as ComponentTag
            (tag.baseComponent as ImageComponentModel).caption = caption.toString()
        }
    private var filePath: String? = null
    private var mContext: Context? = null
    private var imageComponentListener: ImageComponentListener? = null
    private val imageClickListener = OnClickListener {
        when {
            isImageUploaded -> uploadedState()
            isImageUploading -> uploadingState()
            else -> failedState()
        }
        hideExtraInfroWithDelay()
    }

    private val selfIndex: Int
        get() {
            val tag = tag as ComponentTag
            return tag.componentIndex!!
        }

    constructor(context: Context) : super(context) {
        init(context)
    }

    private fun init(context: Context) {
        this.mContext = context
        downloadUrl = null
        LayoutInflater.from(context).inflate(R.layout.image_component_item, this)
        attachListeners()
    }

    private fun attachListeners() {
        retryUpload.setOnClickListener { setImageInformation(filePath, false, "") }

        removeImageButton.setOnClickListener {
            if (imageComponentListener != null) {
                imageComponentListener!!.onImageRemove(selfIndex)
            }
        }

        captionEt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

            }

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                checkReturnPressToInsertNewTextComponent(charSequence)
                caption = charSequence.toString()
            }

            override fun afterTextChanged(editable: Editable) {

            }
        })
    }

    fun setImageInformation(filePath: String?,  imageUploaded: Boolean, caption: String?) {
        this.filePath = filePath
        this.caption = caption
        if (caption != null) {
            captionEt.setText(caption)
        }
        loadImage()
        if (imageUploaded) {
            onImageUploaded(filePath)
        } else {
            startImageUpload(filePath)
        }
    }

    private fun checkReturnPressToInsertNewTextComponent(charSequence: CharSequence) {
        val clen = charSequence.length
        if (clen > 0) {
            val sequenceToCheckNewLineCharacter = if (clen > 1)
                charSequence.subSequence(clen - 2, clen).toString()
            else
                charSequence.subSequence(clen - 1, clen).toString()
            val noReadableCharactersAfterCursor = sequenceToCheckNewLineCharacter.trim { it <= ' ' }.isEmpty()
            //if last characters are [AB\n<space>] or [AB\n] then we insert new TextComponentProvider
            //else if last characters are [AB\nC] ignore the insert.
            if (sequenceToCheckNewLineCharacter.contains("\n") && noReadableCharactersAfterCursor) {
                if (imageComponentListener != null) {
                    imageComponentListener!!.onExitFromCaptionAndInsertNewTextComponent(selfIndex + 1)
                }
            }
        }
    }

    private fun loadImage() {
        if (downloadUrl != null) {
            ir.puyaars.advisor.blog.editor.utils.loadImageIn(imageView, downloadUrl!!)
        }
    }

    private fun onImageUploaded(downloadUrl: String?) {
        setDownloadUrl(downloadUrl)
        isImageUploading = false
        isImageUploaded = true
        uploadedState()
        loadImage()
    }

    private fun onImageUploadFailed() {
        setDownloadUrl(null)
        isImageUploading = false
        isImageUploaded = false
        failedState()
    }

    override fun onUpSuccess(url: String) {
        onImageUploaded(url)
    }

    override fun onUpFail() {
        onImageUploadFailed()
    }

    private fun startImageUpload(filePath: String?) {
        isImageUploading = true
        uploadingState()
        Uploader.getInstance().uploadFile(filePath!!, this)
    }

    @SuppressLint("SetTextI18n")
    private fun uploadedState() {
        removeImageButton.visibility = View.VISIBLE
        retryUpload.visibility = View.GONE
        imageUploadProgressBar.visibility = View.GONE
        statusMessage.visibility = View.VISIBLE
        statusMessage.text = "\u2713 Uploaded"
        hideExtraInfroWithDelay()
        //set listener
        imageView.setOnClickListener(imageClickListener)
    }

    private fun uploadingState() {
        retryUpload.visibility = View.GONE
        statusMessage.visibility = View.VISIBLE
        statusMessage.text = context.getString(R.string.uploading)
        imageUploadProgressBar.visibility = View.VISIBLE
        removeImageButton.visibility = View.VISIBLE
        //remove listener
        imageView.setOnClickListener(null)
    }

    private fun hideExtraInfroWithDelay() {
        android.os.Handler().postDelayed({
            statusMessage.visibility = View.GONE
            removeImageButton.visibility = View.GONE
            retryUpload.visibility = View.GONE
            imageUploadProgressBar.visibility = View.GONE
        }, 2000)
    }



    private fun failedState() {
        retryUpload.visibility = View.VISIBLE
        statusMessage.visibility = View.VISIBLE
        statusMessage.text = context.getString(R.string.failedToUpload)
        imageUploadProgressBar.visibility = View.GONE
        removeImageButton.visibility = View.VISIBLE
        //remove listener
        imageView.setOnClickListener(null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context)
    }

    fun getDownloadUrl(): String? {
        return downloadUrl
    }

    private fun setDownloadUrl(downloadUrl: String?) {
        this.downloadUrl = downloadUrl
        val tag = tag as ComponentTag
        (tag.baseComponent as ImageComponentModel).url = downloadUrl
    }

    fun setFocus() {
        imageView.isEnabled = true
        captionEt.requestFocus()
    }


    fun setImageComponentListener(imageComponentListener: ImageComponentListener) {
        this.imageComponentListener = imageComponentListener
    }

    interface ImageComponentListener {
        fun onImageRemove(removeIndex: Int)

        fun onExitFromCaptionAndInsertNewTextComponent(currentIndex: Int)
    }
}
