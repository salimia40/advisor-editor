package ir.puyaars.advisor.blog.editor.utils

import android.view.View
import ir.puyaars.advisor.blog.editor.Editor
import ir.puyaars.advisor.blog.editor.components.ImageComponentItem
import ir.puyaars.advisor.blog.editor.components.HorizontalDividerComponentItem
import ir.puyaars.advisor.blog.editor.components.TextComponentItem
import ir.puyaars.advisor.blog.editor.components.TextComponentItem.Companion.MODE_OL
import ir.puyaars.advisor.blog.editor.components.TextComponentItem.Companion.MODE_PLAIN
import ir.puyaars.advisor.blog.editor.components.TextComponentItem.Companion.MODE_UL
import ir.puyaars.advisor.blog.editor.models.TextComponentModel
import ir.puyaars.advisor.blog.editor.models.ComponentTag
import ir.puyaars.advisor.blog.editor.models.TextComponentStyle


class MarkDownConverter {
    private val stringBuilder: StringBuilder = StringBuilder()
    private val images: MutableList<String> =  ArrayList()
    /**
     * @return flag whether views are processed or not.
     */
    var isDataProcessed: Boolean = false
        private set

    /**
     * @return markdown format of data.
     */
    val markDown: String
        get() = stringBuilder.toString()

    fun processData(mEditor: Editor): MarkDownConverter {
        val childCount = mEditor.childCount
        var view: View
        var textStyle: Int
        var componentTag: ComponentTag
        for (i in 0 until childCount) {
            view = mEditor.getChildAt(i)
            when (view) {
                is TextComponentItem -> //check mode
                    when (view.mode) {
                        MODE_PLAIN -> {
                            //check for styles {H1-H5 Blockquote Normal}
                            componentTag = view.getTag() as ComponentTag
                            textStyle = (componentTag.baseComponent as TextComponentModel).headingStyle
                            stringBuilder.append(MarkDownFormat.getTextFormat(textStyle, view.content))
                        }
                        MODE_UL -> stringBuilder.append(MarkDownFormat.getULFormat(view.content))
                        MODE_OL -> stringBuilder.append(
                            MarkDownFormat.getOLFormat(
                                view.indicatorText!!,
                                view.content
                            )
                        )
                    }
                is HorizontalDividerComponentItem -> stringBuilder.append(MarkDownFormat.lineFormat)
                is ImageComponentItem -> {
                    stringBuilder.append(MarkDownFormat.getImageFormat(view.getDownloadUrl()!!))
                    images.add(view.getDownloadUrl().toString())
                    stringBuilder.append(MarkDownFormat.getCaptionFormat(view.caption))
                }
            }
        }
        isDataProcessed = true
        return this
    }

    /**
     * @return list of inserted images.
     */
    fun getImages(): List<String> {
        return images
    }
}


object MarkDownFormat {

    val lineFormat: String
        get() = "\\n\\n---\\n\\n"

    fun getTextFormat(heading: Int, content: String): String {
        val pref: String = when (heading) {
            TextComponentStyle.H1 -> "# "
            TextComponentStyle.H2 -> "## "
            TextComponentStyle.H3 -> "### "
            TextComponentStyle.H4 -> "#### "
            TextComponentStyle.H5 -> "##### "
            TextComponentStyle.BLOCKQUOTE -> "> "
            else -> ""
        }
        return String.format("\\n%s%s\\n", pref, content)
    }

    fun getImageFormat(url: String): String {
        return String.format("\\n<center>![Image](%s)</center>", url)
    }

    fun getCaptionFormat(caption: String?): String {
        return if (caption != null) String.format("<center>%s</center>\\n\\n\\n", caption) else "\\n\\n\\n"
    }

    fun getULFormat(content: String): String {
        return String.format("  - %s\\n", content)
    }

    fun getOLFormat(indicator: String, content: String): String {
        return String.format("  %s %s\\n", indicator, content)
    }
}

