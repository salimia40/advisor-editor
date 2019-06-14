package ir.puyaars.advisor.blog.editor.utils

import android.view.View
import ir.puyaars.advisor.blog.editor.Editor
import ir.puyaars.advisor.blog.editor.components.hr.HorizontalDividerComponentItem
import ir.puyaars.advisor.blog.editor.components.image.ImageComponentItem
import ir.puyaars.advisor.blog.editor.components.text.TextComponentItem
import ir.puyaars.advisor.blog.editor.components.text.TextCore.Companion.MODE_OL
import ir.puyaars.advisor.blog.editor.components.text.TextCore.Companion.MODE_PLAIN
import ir.puyaars.advisor.blog.editor.components.text.TextCore.Companion.MODE_UL
import ir.puyaars.advisor.blog.editor.models.*

class DraftManager {

    fun processDraftContent(mEditor: Editor): DraftModel {
        val drafts = ArrayList<DraftDataItemModel>()
        val childCount = mEditor.childCount
        var view: View
        var textStyle: Int
        var componentTag: ComponentTag
        for (i in 0 until childCount) {
            view = mEditor.getChildAt(i)
            when (view) {
                is TextComponentItem -> {
                    //check mode
                    when (view.mode) {
                        MODE_PLAIN -> {
                            //check for styles {H1-H5 Blockquote Normal}
                            componentTag = view.getTag() as ComponentTag
                            textStyle = (componentTag.baseComponent as TextComponentModel).headingStyle
                            drafts.add(getPlainModel(textStyle, view.content))
                        }
                        MODE_UL -> drafts.add(getUlModel(view.content))
                        MODE_OL -> drafts.add(getOlModel(view.content))
                    }
                }
                is HorizontalDividerComponentItem -> drafts.add(getHrModel())
                is ImageComponentItem -> drafts.add(
                    getImageModel(
                        view.getDownloadUrl(),
                        view.caption
                    )
                )
            }
        }
        return DraftModel(items = drafts)
    }

    private fun getHrModel(): DraftDataItemModel = DraftDataItemModel(itemType = DraftModel.ITEM_TYPE_HR)

    private fun getPlainModel(textStyle: Int, content: String): DraftDataItemModel = DraftDataItemModel(itemType = DraftModel.ITEM_TYPE_TEXT,content = content,mode = MODE_PLAIN,style = textStyle)

    private fun getUlModel(content: String): DraftDataItemModel = DraftDataItemModel(itemType = DraftModel.ITEM_TYPE_TEXT,content = content,mode = MODE_UL,style = TextComponentStyle.NORMAL)

    private fun getOlModel(content: String): DraftDataItemModel = DraftDataItemModel( itemType = DraftModel.ITEM_TYPE_TEXT, content = content, mode =  MODE_OL, style = TextComponentStyle.NORMAL)

    private fun getImageModel(downloadUrl: String?, caption: String?): DraftDataItemModel = DraftDataItemModel(itemType = DraftModel.ITEM_TYPE_IMAGE,caption = caption,downloadUrl = downloadUrl)

}
