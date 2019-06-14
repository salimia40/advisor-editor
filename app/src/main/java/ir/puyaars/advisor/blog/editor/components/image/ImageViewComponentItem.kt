package ir.puyaars.advisor.blog.editor.components.image

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import ir.puyaars.advisor.blog.R
import ir.puyaars.advisor.blog.editor.models.ComponentTag
import ir.puyaars.advisor.blog.editor.models.ImageComponentModel
import ir.puyaars.advisor.blog.editor.utils.loadImageIn
import kotlinx.android.synthetic.main.image_view_component_item.view.*


class ImageViewComponentItem(context: Context, attrs: AttributeSet? = null) : FrameLayout(context, attrs) {
    private var filePath: String? = null
    private var caption: String? = null

    init {
        LayoutInflater.from(context).inflate(R.layout.image_view_component_item, this)
    }

    fun setImageInformation(filePath: String, caption: String) {
        this.filePath = filePath
        this.caption = caption
        setDownloadUrl(filePath)
        loadImage()
    }

    private fun loadImage() {loadImageIn(imageView, filePath!!)
    }

    private fun setDownloadUrl(downloadUrl: String?) {
        val tag = tag as ComponentTag
        (tag.baseComponent as ImageComponentModel).url = downloadUrl
    }


}