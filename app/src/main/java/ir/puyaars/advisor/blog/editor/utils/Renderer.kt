package ir.puyaars.advisor.blog.editor.utils

import ir.puyaars.advisor.blog.editor.components.text.TextCore
import ir.puyaars.advisor.blog.editor.models.DraftDataItemModel
import ir.puyaars.advisor.blog.editor.models.DraftModel
import ir.puyaars.advisor.blog.editor.models.TextComponentStyle

class Renderer(private val core: Core) {

    private val insertIndex: Int
        get() = core.childCount


    fun render(contents: ArrayList<DraftDataItemModel>) {
        for (i in 0 until contents.size) {
            renderItem(contents[i])
        }
    }

    private fun renderItem(item: DraftDataItemModel) {
        when (item.itemType) {
            DraftModel.ITEM_TYPE_TEXT ->
                when (item.mode) {
                    TextCore.MODE_PLAIN -> renderPlainData(item)
                    TextCore.MODE_OL -> renderOrderedList(item)
                    TextCore.MODE_UL -> renderUnOrderedList(item)
                    else -> renderPlainData(item)
                }
            DraftModel.ITEM_TYPE_HR -> renderHR()
            DraftModel.ITEM_TYPE_IMAGE -> renderImage(item)
        }
    }

    private fun renderImage(item: DraftDataItemModel) {
        core.insertImage(insertIndex, item.downloadUrl!!, true, item.caption!!)
    }

    private fun renderHR() {
        core.insertHorizontalDivider(false)
    }

    private fun renderUnOrderedList(item: DraftDataItemModel) {
        core.setCurrentInputMode(TextCore.MODE_UL)
        core.addTextComponent(insertIndex, item.content!!)
    }

    private fun renderOrderedList(item: DraftDataItemModel) {
        core.setCurrentInputMode(TextCore.MODE_OL)
        core.addTextComponent(insertIndex, item.content!!)
    }

    private fun renderPlainData(item: DraftDataItemModel) {
        core.setCurrentInputMode(TextCore.MODE_PLAIN)
        when (item.style) {
            TextComponentStyle.NORMAL, TextComponentStyle.H1, TextComponentStyle.H2, TextComponentStyle.H3, TextComponentStyle.H4, TextComponentStyle.H5, TextComponentStyle.BLOCKQUOTE -> {
                core.addTextComponent(insertIndex, item.content!!)
                core.setHeading(item.style!!)
            }
            else -> {
                core.addTextComponent(insertIndex, item.content!!)
                core.setHeading(TextComponentStyle.NORMAL)
            }
        }
    }
}
