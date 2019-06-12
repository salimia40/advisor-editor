package ir.puyaars.advisor.blog.editor.utils

import android.widget.ImageView
import com.squareup.picasso.Picasso
import ir.puyaars.advisor.blog.editor.models.ComponentTag
import ir.puyaars.advisor.blog.editor.models.TextComponentStyle
import ir.puyaars.advisor.blog.editor.models.TextComponentStyle.H1
import ir.puyaars.advisor.blog.editor.models.TextComponentStyle.H2
import ir.puyaars.advisor.blog.editor.models.TextComponentStyle.H3
import ir.puyaars.advisor.blog.editor.models.TextComponentStyle.H4
import ir.puyaars.advisor.blog.editor.models.TextComponentStyle.H5


object FontSize {
    private const val H1_SIZE = 32
    private const val H2_SIZE = 28
    private const val H3_SIZE = 24
    private const val H4_SIZE = 22
    private const val H5_SIZE = 20
    private const val NORMAL = 20

    fun getFontSize(heading: Int): Int {
        when (heading) {
            H1 -> return H1_SIZE
            H2 -> return H2_SIZE
            H3 -> return H3_SIZE
            H4 -> return H4_SIZE
            H5 -> return H5_SIZE
            TextComponentStyle.NORMAL -> return NORMAL
        }
        return H5_SIZE
    }
}

fun getNewComponentTag(index: Int): ComponentTag {
    val componentTag = ComponentTag()
    componentTag.componentIndex = index
    return componentTag
}

fun loadImage(imageView: ImageView, _uri: String) {
    Picasso.get().load(_uri).into(imageView)
}