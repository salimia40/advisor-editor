package ir.puyaars.advisor.blog.editor.components.image

import android.content.Context

internal class ImageComponentProvider(private val context: Context)  {


    fun getNewImageComponentItem(imageRemoveListener: ImageComponentItem.ImageComponentListener): ImageComponentItem {
        val imageComponentItem = ImageComponentItem(context)
        imageComponentItem.setImageComponentListener(imageRemoveListener)
        return imageComponentItem
    }

    fun getNewImageViewComponentItem() : ImageViewComponentItem = ImageViewComponentItem(context)
}


