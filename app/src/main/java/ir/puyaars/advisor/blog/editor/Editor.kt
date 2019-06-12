package ir.puyaars.advisor.blog.editor

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.inputmethod.InputMethodManager
import ir.puyaars.advisor.blog.editor.models.DraftModel
import ir.puyaars.advisor.blog.editor.models.ComponentTag
import ir.puyaars.advisor.blog.editor.models.ImageComponentModel
import ir.puyaars.advisor.blog.editor.models.TextComponentModel
import ir.puyaars.advisor.blog.editor.components.*
import ir.puyaars.advisor.blog.editor.components.TextComponent
import ir.puyaars.advisor.blog.editor.components.TextComponentItem.Companion.MODE_OL
import ir.puyaars.advisor.blog.editor.components.TextComponentItem.Companion.MODE_PLAIN
import ir.puyaars.advisor.blog.editor.components.TextComponentItem.Companion.MODE_UL
import ir.puyaars.advisor.blog.editor.models.TextComponentStyle.BLOCKQUOTE
import ir.puyaars.advisor.blog.editor.models.TextComponentStyle.NORMAL
import ir.puyaars.advisor.blog.editor.utils.*
import kotlinx.android.synthetic.main.text_component_item.view.*


class Editor(context: Context, attrs: AttributeSet?) : Core(context, attrs),
    TextComponent.TextComponentCallback, ImageComponentItem.ImageComponentListener {
    private var _activeView: View? = null
    private var mContext: Context? = null
    private var draftManager: DraftManager? = null
    private var __textComponent: TextComponent? = null
    private var __imageComponent: ImageComponent? = null
    private var __horizontalComponent: HorizontalDividerComponent? = null
    private var currentInputMode: Int = 0
    private var markDownConverter: MarkDownConverter? = null
    private var renderingUtils: RenderingUtils? = null
    private var editorFocusReporter: EditorFocusReporter? = null
    private var startHintText: String? = null
    private var defaultHeadingType = NORMAL
    private var isFreshEditor: Boolean = false
    private var oldDraft: DraftModel? = null

    /**
     * @return index next to focussed view.
     */
    private val nextIndex: Int
        get() {
            val (componentIndex) = _activeView!!.tag as ComponentTag
            return componentIndex!! + 1
        }

    /**
     * @return List of Draft Content.
     */
    val draft: DraftModel
        get() {
            val newDraft = draftManager!!.processDraftContent(this)
            if (oldDraft != null) {
                newDraft.draftId = oldDraft!!.draftId
            } else {
                newDraft.draftId = System.currentTimeMillis()
            }
            return newDraft
        }

    init {
        this.mContext = context
        draftManager = DraftManager()
        bulletGroupModels = ArrayList()
        markDownConverter = MarkDownConverter()
        currentInputMode = MODE_PLAIN
        __textComponent = TextComponent(context, this)
        __imageComponent = ImageComponent(context)
        __horizontalComponent = HorizontalDividerComponent(context)
    }

    /**
     * Helper method to configure editor
     *
     * @param isDraft
     * @param startHint
     * @param defaultHeadingType
     */
    fun configureEditor(
        isDraft: Boolean,
        startHint: String,
        defaultHeadingType: Int
    ) {
        this.startHintText = startHint
        this.defaultHeadingType = defaultHeadingType
        if (!isDraft) {
            startFreshEditor()
        }
    }

    /**
     * Inserts single text component
     */
    private fun startFreshEditor() {
        //starts basic editor with single text component.
        this.isFreshEditor = true
        addTextComponent(0)
        setHeading(defaultHeadingType)
    }

    /**
     * adds new TextComponent.
     *
     * @param insertIndex at which addition of new textcomponent take place.
     */
    private fun addTextComponent(insertIndex: Int) {
        val textComponentItem = __textComponent!!.newTextComponent(currentInputMode)
        //prepare tag
        val textComponentModel = TextComponentModel()
        if (insertIndex == 0) {
            if (startHintText != null && isFreshEditor) {
                textComponentItem.setHintText(startHintText!!)
            }
        }
        val componentTag = getNewComponentTag(insertIndex)
        componentTag.baseComponent = textComponentModel
        textComponentItem.tag = componentTag
        addView(textComponentItem, insertIndex)
        __textComponent!!.updateComponent(textComponentItem)
        setFocus(textComponentItem)
        reComputeTagsAfter(insertIndex)
        refreshViewOrder()
    }

    /**
     * sets heading to text component
     *
     * @param heading number to be set
     */
    fun setHeading(heading: Int) {
        currentInputMode = MODE_PLAIN
        if (_activeView is TextComponentItem) {
            (_activeView as TextComponentItem).mode = currentInputMode
            val componentTag = _activeView!!.tag as ComponentTag
            (componentTag.baseComponent as TextComponentModel).headingStyle = heading
            __textComponent!!.updateComponent(_activeView as TextComponentItem)
        }
        refreshViewOrder()
    }

    /**
     * @param view to be focused on.
     */
    private fun setFocus(view: View) {
        _activeView = view
        if (_activeView is TextComponentItem) {
            currentInputMode = (_activeView as TextComponentItem).mode
            (view as TextComponentItem).inputBox.requestFocus()
            reportStylesOfFocusedView(view)
        }
    }

    /**
     * re-compute the indexes of view after a view is inserted/deleted.
     *
     * @param startIndex index after which re-computation will be done.
     */
    private fun reComputeTagsAfter(startIndex: Int) {
        var _child: View
        for (i in startIndex until childCount) {
            _child = getChildAt(i)
            val componentTag = _child.tag as ComponentTag
            componentTag.componentIndex = i
            _child.tag = componentTag
        }
    }

    /**
     * method to send callback for focussed view back to subscriber(if any).
     *
     * @param view newly focus view.
     */
    private fun reportStylesOfFocusedView(view: TextComponentItem) {
        if (editorFocusReporter != null) {
            editorFocusReporter!!.onFocusedViewHas(view.mode, view.textHeadingStyle)
        }
    }

    fun loadDraft(draft: DraftModel) {
        oldDraft = draft
        val contents = draft.items
        if (contents != null) {
            if (contents.size > 0) {
                renderingUtils = RenderingUtils()
                renderingUtils!!.setEditor(this)
                renderingUtils!!.render(contents)
            } else {
                startFreshEditor()
            }
        } else {
            startFreshEditor()
        }
    }

    /**
     * Sets current mode for insert.
     *
     * @param currentInputMode mode of insert.
     */
    fun setCurrentInputMode(currentInputMode: Int) {
        this.currentInputMode = currentInputMode
    }

    /**
     * adds new TextComponent with pre-filled text.
     *
     * @param insertIndex at which addition of new textcomponent take place.
     */
    fun addTextComponent(insertIndex: Int, content: String) {
        val textComponentItem = __textComponent!!.newTextComponent(currentInputMode)
        //prepare tag
        val textComponentModel = TextComponentModel()
        val componentTag = getNewComponentTag(insertIndex)
        componentTag.baseComponent = textComponentModel
        textComponentItem.tag = componentTag
        textComponentItem.setText(content)
        addView(textComponentItem, insertIndex)
        __textComponent!!.updateComponent(textComponentItem)
        setFocus(textComponentItem)
        reComputeTagsAfter(insertIndex)
        refreshViewOrder()
    }

    override fun onInsertTextComponent(selfIndex: Int) {
        addTextComponent(selfIndex + 1)
    }

    override fun onFocusGained(view: View) {
        setFocus(view)
    }

    /**
     * This callback method removes view at given index.
     * It checks if there is a horizontal line just before it, it removes the line too.
     * Else it removes the current view only.
     *
     * @param selfIndex index of view to remove.
     */
    override fun onRemoveTextComponent(selfIndex: Int) {
        if (selfIndex == 0)
            return
        val viewToBeRemoved = getChildAt(selfIndex)
        val previousView = getChildAt(selfIndex - 1)
        val content = (viewToBeRemoved as TextComponentItem).inputBox.text.toString()
        if (previousView is HorizontalDividerComponentItem) {
            //remove previous view.
            removeViewAt(selfIndex - 1)
            reComputeTagsAfter(selfIndex - 1)
            //focus on latest text component
            val lastTextComponent = getLatestTextComponentIndexBefore(selfIndex - 1)
            setFocus(getChildAt(lastTextComponent))
        } else if (previousView is TextComponentItem) {
            removeViewAt(selfIndex)
            val contentLen = previousView.inputBox.text.toString().length
            previousView.inputBox.append(String.format("%s", content))
            setFocus(previousView, contentLen)
        } else if (previousView is ImageComponentItem) {
            setActiveView(previousView)
            previousView.setFocus()
        }
        reComputeTagsAfter(selfIndex)
        refreshViewOrder()
    }

    /**
     * This method searches whithin view group for a TextComponent which was
     * inserted prior to startIndex.
     *
     * @param starIndex index from which search starts.
     * @return index of LatestTextComponent before startIndex.
     */
    private fun getLatestTextComponentIndexBefore(starIndex: Int): Int {
        var view: View? = null
        for (i in starIndex downTo 0) {
            view = getChildAt(i)
            if (view is TextComponentItem)
                return i
        }
        return 0
    }

    /**
     * overloaded method for focusing view, it puts the cursor at specified position.
     *
     * @param view to be focused on.
     */
    private fun setFocus(view: View, cursorPos: Int) {
        _activeView = view
        view.requestFocus()
        if (view is TextComponentItem) {
            val mgr = mContext!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            mgr.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
            //move cursor
            view.inputBox.setSelection(cursorPos)
            reportStylesOfFocusedView(view)
        }
    }

    private fun setActiveView(view: View) {
        _activeView = view
    }

    /**
     * adds link.
     *
     * @param text link text
     * @param url  linking url.
     */
    fun addLink(text: String, url: String) {
        if (_activeView is TextComponentItem) {
            val stringBuilder = StringBuilder()
            stringBuilder
                .append(" <a href=\"")
                .append(url)
                .append("\">")
                .append(text)
                .append("</a> ")
            (_activeView as TextComponentItem).inputBox.append(stringBuilder.toString())
        }
    }

    /**
     * changes the current text into blockquote.
     */
    fun changeToBlockquote() {
        currentInputMode = MODE_PLAIN
        if (_activeView is TextComponentItem) {
            (_activeView as TextComponentItem).mode = currentInputMode
            val componentTag = _activeView!!.tag as ComponentTag
            (componentTag.baseComponent as TextComponentModel).headingStyle = BLOCKQUOTE
            __textComponent!!.updateComponent(_activeView as TextComponentItem)
        }
        refreshViewOrder()
    }

    /**
     * change the current insert mode to Ordered List Mode.
     * Increasing numbers are used to denote each item.
     */
    fun changeToOLMode() {
        currentInputMode = MODE_OL
        if (_activeView is TextComponentItem) {
            (_activeView as TextComponentItem).mode = currentInputMode
            val componentTag = _activeView!!.tag as ComponentTag
            (componentTag.baseComponent as TextComponentModel).headingStyle = NORMAL
            __textComponent!!.updateComponent(_activeView as TextComponentItem)
        }
        refreshViewOrder()
    }

    /**
     * change the current insert mode to UnOrdered List Mode.
     * Circular filled bullets are used to denote each item.
     */
    fun changeToULMode() {
        currentInputMode = MODE_UL
        if (_activeView is TextComponentItem) {
            (_activeView as TextComponentItem).mode = currentInputMode
            val componentTag = _activeView!!.getTag() as ComponentTag
            (componentTag.baseComponent as TextComponentModel).headingStyle = NORMAL
            __textComponent!!.updateComponent(_activeView as TextComponentItem)
        }
        refreshViewOrder()
    }

    /**
     * This method gets the suitable insert index using
     * `checkInvalidateAndCalculateInsertIndex()` method.
     * Prepares the ImageComponent and inserts it.
     * Since the user might need to type further, it inserts new TextComponent below
     * it.
     *
     * @param filePath uri of image to be inserted.
     */
    fun insertImage(filePath: String) {
        var insertIndex = checkInvalidateAndCalculateInsertIndex()
        val imageComponentItem = __imageComponent!!.getNewImageComponentItem(this)
        //prepare tag
        val imageComponentModel = ImageComponentModel()
        val imageComponentTag = getNewComponentTag(insertIndex)
        imageComponentTag.baseComponent = imageComponentModel
        imageComponentItem.tag = imageComponentTag
        imageComponentItem.setImageInformation(filePath,false, "")
        addView(imageComponentItem, insertIndex)
        reComputeTagsAfter(insertIndex)
        refreshViewOrder()
        //add another text component below image
        insertIndex++
        currentInputMode = MODE_PLAIN
        addTextComponent(insertIndex)
    }

    /**
     * This method checks the current active/focussed view.
     * If there is some text in it, then next insertion will take place below this
     * view.
     * Else the current focussed view will be removed and new view will inserted
     * at its position.
     *
     * @return index of next insert.
     */
    private fun checkInvalidateAndCalculateInsertIndex(): Int {
        if (_activeView == null)
            return 0
        val (componentIndex) = _activeView!!.tag as ComponentTag
        val activeIndex = componentIndex!!
        val view = getChildAt(activeIndex)
        //check for TextComponentItem
        if (view is TextComponentItem) {
            //if active text component has some texts.
            return if (view.inputBox.text.isNotEmpty()) {
                //insert below it
                activeIndex + 1
            } else {
                //remove current view
                removeViewAt(activeIndex)
                reComputeTagsAfter(activeIndex)
                refreshViewOrder()
                //insert at the current position.
                activeIndex
            }
        }
        return activeIndex + 1
    }

    /**
     * This method gets the suitable insert index using
     * `checkInvalidateAndCalculateInsertIndex()` method.
     * Prepares the ImageComponent and inserts it.
     * loads already uploaded image and sets caption
     *
     * @param filePath uri of image to be inserted.
     */
    fun insertImage(insertIndex: Int, filePath: String, uploaded: Boolean, caption: String) {
        val imageComponentItem = __imageComponent!!.getNewImageComponentItem(this)
        //prepare tag
        val imageComponentModel = ImageComponentModel()
        val imageComponentTag = getNewComponentTag(insertIndex)
        imageComponentTag.baseComponent = imageComponentModel
        imageComponentItem.tag = imageComponentTag
        imageComponentItem.setImageInformation(filePath,uploaded, caption)
        addView(imageComponentItem, insertIndex)
        reComputeTagsAfter(insertIndex)
    }

    /**
     * Inserts new horizontal ruler.
     */
    fun insertHorizontalDivider() {
        var insertIndex = nextIndex
        val horizontalDividerComponentItem = __horizontalComponent!!.newHorizontalComponentItem
        val _hrTag = getNewComponentTag(insertIndex)
        horizontalDividerComponentItem.tag = _hrTag
        addView(horizontalDividerComponentItem, insertIndex)
        reComputeTagsAfter(insertIndex)
        //add another text component below image
        insertIndex++
        currentInputMode = MODE_PLAIN
        addTextComponent(insertIndex)
        refreshViewOrder()
    }

    /**
     * Inserts new horizontal ruler.
     * Adds new text components based on passed parameter.
     */
    fun insertHorizontalDivider(insertNewTextComponentAfterThis: Boolean) {
        var insertIndex = nextIndex
        val horizontalDividerComponentItem = __horizontalComponent!!.newHorizontalComponentItem
        val _hrTag = getNewComponentTag(insertIndex)
        horizontalDividerComponentItem.tag = _hrTag
        addView(horizontalDividerComponentItem, insertIndex)
        reComputeTagsAfter(insertIndex)
        //add another text component below image
        if (insertNewTextComponentAfterThis) {
            insertIndex++
            currentInputMode = MODE_PLAIN
            addTextComponent(insertIndex)
        } else {
            setFocus(horizontalDividerComponentItem)
        }
        refreshViewOrder()
    }

    override fun onImageRemove(removeIndex: Int) {
        if (removeIndex == 0) {
            //insert 1 text component
            removeViewAt(0)
            addTextComponent(0)
        } else {
            removeViewAt(removeIndex)
        }
        reComputeTagsAfter(removeIndex)
        refreshViewOrder()
    }

    override fun onExitFromCaptionAndInsertNewTextComponent(currentIndex: Int) {
        addTextComponent(currentIndex)
    }

    /**
     * setter method to subscribe for listening to focus change.
     *
     * @param editorFocusReporter callback for editor focus.
     */
    fun setEditorFocusReporter(editorFocusReporter: EditorFocusReporter) {
        this.editorFocusReporter = editorFocusReporter
    }

    interface EditorFocusReporter {
        fun onFocusedViewHas(mode: Int, textComponentStyle: Int)
    }

}
