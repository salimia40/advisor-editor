package ir.puyaars.advisor.blog.editor.utils

import ir.puyaars.advisor.blog.editor.Editor
import ir.puyaars.advisor.blog.editor.components.TextComponentItem.Companion.MODE_OL
import ir.puyaars.advisor.blog.editor.components.TextComponentItem.Companion.MODE_PLAIN
import ir.puyaars.advisor.blog.editor.components.TextComponentItem.Companion.MODE_UL
import ir.puyaars.advisor.blog.editor.models.DraftDataItemModel
import ir.puyaars.advisor.blog.editor.models.DraftModel
import ir.puyaars.advisor.blog.editor.models.TextComponentStyle.BLOCKQUOTE
import ir.puyaars.advisor.blog.editor.models.TextComponentStyle.H1
import ir.puyaars.advisor.blog.editor.models.TextComponentStyle.H2
import ir.puyaars.advisor.blog.editor.models.TextComponentStyle.H3
import ir.puyaars.advisor.blog.editor.models.TextComponentStyle.H4
import ir.puyaars.advisor.blog.editor.models.TextComponentStyle.H5
import ir.puyaars.advisor.blog.editor.models.TextComponentStyle.NORMAL


class RenderingUtils {
    private var mEditor: Editor? = null

    /**
     * Since childs are going to be arranged in linear fashion, child count can act as insert index.
     *
     * @return insert index.
     */
    private val insertIndex: Int
        get() = mEditor!!.childCount

    fun setEditor(mEditor: Editor) {
        this.mEditor = mEditor
    }

    fun render(contents: ArrayList<DraftDataItemModel>) {
        //visit each item type
        for (i in 0 until contents.size) {
            val item = contents[i]
            //identify item of data
            when {
                item.itemType == DraftModel.ITEM_TYPE_TEXT -> //identify mode of text item
                    when (item.mode) {
                        MODE_PLAIN ->
                            //includes NORMAL, H1-H5, Blockquote
                            renderPlainData(item)
                        MODE_OL ->
                            //renders orderedList
                            renderOrderedList(item)
                        MODE_UL ->
                            //renders unorderedList
                            renderUnOrderedList(item)
                        else ->
                            //default goes to normal text
                            renderPlainData(item)
                    }
                item.itemType == DraftModel.ITEM_TYPE_HR -> renderHR()
                item.itemType == DraftModel.ITEM_TYPE_IMAGE -> renderImage(item)
            }
        }
    }

    /**
     * Sets mode to plain and insert a a text component.
     *
     * @param item model of text data item
     */
    private fun renderPlainData(item: DraftDataItemModel) {
        mEditor!!.setCurrentInputMode(MODE_PLAIN)
        when (item.style) {
            NORMAL, H1, H2, H3, H4, H5, BLOCKQUOTE -> {
                mEditor!!.addTextComponent(insertIndex, item.content!!)
                mEditor!!.setHeading(item.style!!)
            }
            else -> {
                mEditor!!.addTextComponent(insertIndex, item.content!!)
                mEditor!!.setHeading(NORMAL)
            }
        }
    }

    /**
     * Sets mode to ordered-list and insert a a text component.
     *
     * @param item model of text data item.
     */
    private fun renderOrderedList(item: DraftDataItemModel) {
        mEditor!!.setCurrentInputMode(MODE_OL)
        mEditor!!.addTextComponent(insertIndex, item.content!!)
    }

    /**
     * Sets mode to unordered-list and insert a a text component.
     *
     * @param item model of text data item.
     */
    private fun renderUnOrderedList(item: DraftDataItemModel) {
        mEditor!!.setCurrentInputMode(MODE_UL)
        mEditor!!.addTextComponent(insertIndex, item.content!!)
    }

    /**
     * Adds Horizontal line.
     */
    private fun renderHR() {
        mEditor!!.insertHorizontalDivider(false)
    }

    /**
     * @param item model of image item.
     * Inserts image.
     * Sets caption
     */
    private fun renderImage(item: DraftDataItemModel) {
        mEditor!!.insertImage(insertIndex, item.downloadUrl!!, true, item.caption!!)
    }
}
