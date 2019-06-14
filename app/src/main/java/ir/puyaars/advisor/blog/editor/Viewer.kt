package ir.puyaars.advisor.blog.editor

import android.content.Context
import android.util.AttributeSet
import android.view.View
import ir.puyaars.advisor.blog.editor.components.hr.HorizontalDividerComponentProvider
import ir.puyaars.advisor.blog.editor.components.image.ImageComponentProvider
import ir.puyaars.advisor.blog.editor.components.text.TextComponentProvider
import ir.puyaars.advisor.blog.editor.components.text.TextCore
import ir.puyaars.advisor.blog.editor.components.text.TextCore.Companion.MODE_PLAIN
import ir.puyaars.advisor.blog.editor.models.ComponentTag
import ir.puyaars.advisor.blog.editor.models.DraftModel
import ir.puyaars.advisor.blog.editor.models.ImageComponentModel
import ir.puyaars.advisor.blog.editor.models.TextComponentModel
import ir.puyaars.advisor.blog.editor.utils.Core
import ir.puyaars.advisor.blog.editor.utils.Renderer
import ir.puyaars.advisor.blog.editor.utils.getNewComponentTag


class Viewer(context: Context, attrs: AttributeSet?) : Core(context, attrs) {

    private val textComponentProvider: TextComponentProvider =
        TextComponentProvider(context)
    private val imageComponentProvider: ImageComponentProvider =
        ImageComponentProvider(context)
    private val horizontalComponentProvider: HorizontalDividerComponentProvider =
        HorizontalDividerComponentProvider(context)
    private val renderer = Renderer(this)
    private var activeView: View? = null


    private val nextIndex: Int
        get() {
            val (componentIndex) = activeView!!.tag as ComponentTag
            return componentIndex!! + 1
        }

    private var currentInputMode: Int = 0

    private var draft: DraftModel? = null
                set(value) {
                    field = value
                    if (value?.items != null && value.items!!.isNotEmpty()) {
                        removeAllViews()
                        renderer.render(value.items!!)
                    }
                }

    override fun setCurrentInputMode(currentInputMode: Int) {
        this.currentInputMode = currentInputMode
    }

    override fun setFocus(view: View) {
        activeView = view
    }

    override fun setHeading(heading: Int) {
        currentInputMode = MODE_PLAIN
        if(activeView is TextCore) {
            (activeView as TextCore).mode = currentInputMode

            val componentTag = activeView!!.tag as ComponentTag
            (componentTag.baseComponent as TextComponentModel).headingStyle = heading
            textComponentProvider.updateComponent(activeView as TextCore)
        }
        refreshViewOrder()
    }

    override fun addTextComponent(insertIndex: Int, content: String) {
        val textViewComponent = textComponentProvider.newTextViewComponent(this.currentInputMode)
        val textComponentModel = TextComponentModel()
        val componentTag = getNewComponentTag(insertIndex)
        componentTag.baseComponent = textComponentModel

        textViewComponent.tag = componentTag
        textViewComponent.setText(content)
        addView(textViewComponent, insertIndex)
        textComponentProvider.updateComponent(textViewComponent)
        setFocus(textViewComponent)
        reComputeTagsAfter(insertIndex)
        refreshViewOrder()
    }

    override fun insertImage(insertIndex: Int, filePath: String, uploaded: Boolean, caption: String) {
        val imageViewComponentItem = imageComponentProvider.getNewImageViewComponentItem()

        val imageComponentModel = ImageComponentModel()
        val imageComponentTag = getNewComponentTag(insertIndex)
        imageComponentTag.baseComponent = imageComponentModel

        imageViewComponentItem.tag = imageComponentTag
        imageViewComponentItem.setImageInformation(filePath, caption)
        addView(imageViewComponentItem, insertIndex)
        reComputeTagsAfter(insertIndex)
    }

    override fun insertHorizontalDivider(insertNewTextComponentAfterThis: Boolean) {
        val insertIndex = nextIndex
        val horizontalDividerComponentItem = horizontalComponentProvider.newHorizontalComponentItem
        val hrTag = getNewComponentTag(insertIndex)
        horizontalDividerComponentItem.tag = hrTag
        addView(horizontalDividerComponentItem, insertIndex)
        reComputeTagsAfter(insertIndex)

        setFocus(horizontalDividerComponentItem)

        refreshViewOrder()
    }

    private fun reComputeTagsAfter(startIndex: Int) {
        var child: View
        for (i in startIndex until childCount) {
            child = getChildAt(i)
            val componentTag = child.tag as ComponentTag
            componentTag.componentIndex = i
            child.tag = componentTag
        }
    }
}