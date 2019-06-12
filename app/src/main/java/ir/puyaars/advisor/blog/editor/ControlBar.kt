package ir.puyaars.advisor.blog.editor

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import ir.puyaars.advisor.blog.R
import ir.puyaars.advisor.blog.editor.components.TextComponentItem.Companion.MODE_OL
import ir.puyaars.advisor.blog.editor.components.TextComponentItem.Companion.MODE_PLAIN
import ir.puyaars.advisor.blog.editor.components.TextComponentItem.Companion.MODE_UL
import ir.puyaars.advisor.blog.editor.models.TextComponentStyle.BLOCKQUOTE
import ir.puyaars.advisor.blog.editor.models.TextComponentStyle.H1
import ir.puyaars.advisor.blog.editor.models.TextComponentStyle.H2
import ir.puyaars.advisor.blog.editor.models.TextComponentStyle.H3
import ir.puyaars.advisor.blog.editor.models.TextComponentStyle.H4
import ir.puyaars.advisor.blog.editor.models.TextComponentStyle.H5
import ir.puyaars.advisor.blog.editor.models.TextComponentStyle.NORMAL
import kotlinx.android.synthetic.main.editor_control_bar.view.*


class EditorControlBar : FrameLayout, Editor.EditorFocusReporter {
    private var mContext: Context? = null
    private var mEditor: Editor? = null
    private var enabledColor: Int = 0
    private var disabledColor: Int = 0

    private var currentHeading = 1
    private var olEnabled: Boolean = false
    private var ulEnabled: Boolean = false
    private var blockquoteEnabled: Boolean = false

    private var editorControlListener: EditorControlListener? = null

    constructor(context: Context) : super(context) {
        init(context)
    }

    private fun init(context: Context) {
        this.mContext = context
        LayoutInflater.from(context).inflate(R.layout.editor_control_bar, this)

        enabledColor = Color.parseColor("#0994cf")
        disabledColor = Color.parseColor("#3e3e3e")

        normalTextBtn!!.setTextColor(enabledColor)
        headingBtn!!.setTextColor(disabledColor)
        headingNumberBtn!!.setTextColor(disabledColor)
        bulletBtn!!.setColorFilter(disabledColor)
        blockQuoteBtn!!.setColorFilter(disabledColor)
        linkBtn!!.setColorFilter(disabledColor)
        hrBtn!!.setColorFilter(disabledColor)
        imageBtn!!.setColorFilter(disabledColor)
        attachListeners()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context)
    }

    fun setEditor(editor: Editor) {
        this.mEditor = editor
        subscribeForStyles()
    }

    private fun subscribeForStyles() {
        if (mEditor != null) {
            mEditor!!.setEditorFocusReporter(this)
        }
    }

    private fun attachListeners() {
        normalTextBtn!!.setOnClickListener {
            mEditor!!.setHeading(NORMAL)
            invalidateStates(MODE_PLAIN, NORMAL)
        }

        headingBtn!!.setOnClickListener {
            if (currentHeading == MAX_HEADING) {
                currentHeading = 1
                mEditor!!.setHeading(currentHeading)
            } else {
                mEditor!!.setHeading(++currentHeading)
            }
            invalidateStates(MODE_PLAIN, currentHeading)
        }

        bulletBtn!!.setOnClickListener {
            when {
                olEnabled -> {
                    //switch to normal
                    mEditor!!.setHeading(NORMAL)
                    invalidateStates(MODE_PLAIN, NORMAL)
                    olEnabled = false
                    ulEnabled = false
                }
                ulEnabled -> {
                    // switch to ol mode
                    mEditor!!.changeToOLMode()
                    invalidateStates(MODE_OL, NORMAL)
                    olEnabled = true
                    ulEnabled = false
                }
                !olEnabled && !ulEnabled -> {
                    // switch to ul mode
                    mEditor!!.changeToULMode()
                    invalidateStates(MODE_UL, NORMAL)
                    ulEnabled = true
                    olEnabled = false
                }
            }
        }

        blockQuoteBtn!!.setOnClickListener {
            if (blockquoteEnabled) {
                //switch to normal
                mEditor!!.setHeading(NORMAL)
                invalidateStates(MODE_PLAIN, NORMAL)
            } else {
                //blockquote
                mEditor!!.changeToBlockquote()
                invalidateStates(MODE_PLAIN, BLOCKQUOTE)
            }
        }

        hrBtn!!.setOnClickListener { mEditor!!.insertHorizontalDivider() }

        linkBtn!!.setOnClickListener {
            if (editorControlListener != null) {
                editorControlListener!!.onInsertLinkClicked()
            }
        }

        imageBtn!!.setOnClickListener {
            if (editorControlListener != null) {
                editorControlListener!!.onInsertImageClicked()
            }
        }
    }

    private fun enableNormalText(enabled: Boolean) {
        if (enabled) {
            normalTextBtn!!.setTextColor(enabledColor)
        } else {
            normalTextBtn!!.setTextColor(disabledColor)
        }
    }

    private fun enableHeading(enabled: Boolean, headingNumber: Int) {
        if (enabled) {
            currentHeading = headingNumber
            headingBtn!!.setTextColor(enabledColor)
            headingNumberBtn!!.setTextColor(enabledColor)
            headingNumberBtn!!.text = headingNumber.toString()
        } else {
            currentHeading = 0
            headingBtn!!.setTextColor(disabledColor)
            headingNumberBtn!!.setTextColor(disabledColor)
            headingNumberBtn!!.text = "1"
        }
    }

    private fun enableBullet(enable: Boolean, isOrdered: Boolean) {
        if (enable) {
            if (isOrdered) {
                olEnabled = true
                ulEnabled = false
                bulletBtn!!.setImageResource(R.drawable.ol)
            } else {
                ulEnabled = true
                olEnabled = false
                bulletBtn!!.setImageResource(R.drawable.ul)
            }
            bulletBtn!!.setColorFilter(enabledColor)
        } else {
            ulEnabled = false
            olEnabled = false
            bulletBtn!!.setImageResource(R.drawable.ul)
            bulletBtn!!.setColorFilter(disabledColor)
        }
    }

    private fun enableBlockquote(enable: Boolean) {
        blockquoteEnabled = enable
        if (enable) {
            blockQuoteBtn!!.setColorFilter(enabledColor)
        } else {
            blockQuoteBtn!!.setColorFilter(disabledColor)
        }
    }

    private fun invalidateStates(mode: Int, textComponentStyle: Int) {
        when (mode) {
            MODE_OL -> {
                enableBlockquote(false)
                enableHeading(false, 1)
                enableNormalText(false)
                enableBullet(true, isOrdered = true)
            }
            MODE_UL -> {
                enableBlockquote(false)
                enableHeading(false, 1)
                enableNormalText(false)
                enableBullet(enable = true, isOrdered = false)
            }
            MODE_PLAIN -> when (textComponentStyle) {
                H1 -> {
                    enableBlockquote(false)
                    enableHeading(true, 1)
                    enableNormalText(false)
                    enableBullet(enable = false, isOrdered = false)
                }
                H2 -> {
                    enableBlockquote(false)
                    enableHeading(true, 2)
                    enableNormalText(false)
                    enableBullet(enable = false, isOrdered = false)
                }
                H3 -> {
                    enableBlockquote(false)
                    enableHeading(true, 3)
                    enableNormalText(false)
                    enableBullet(enable = false, isOrdered = false)
                }
                H4 -> {
                    enableBlockquote(false)
                    enableHeading(true, 4)
                    enableNormalText(false)
                    enableBullet(enable = false, isOrdered = false)
                }
                H5 -> {
                    enableBlockquote(false)
                    enableHeading(true, 5)
                    enableNormalText(false)
                    enableBullet(enable = false, isOrdered = false)
                }
                BLOCKQUOTE -> {
                    enableBlockquote(true)
                    enableHeading(false, 1)
                    enableNormalText(false)
                    enableBullet(enable = false, isOrdered = false)
                }
                NORMAL -> {
                    enableBlockquote(false)
                    enableHeading(false, 1)
                    enableNormalText(true)
                    enableBullet(enable = false, isOrdered = false)
                }
            }
        }
    }

    override fun onFocusedViewHas(mode: Int, textComponentStyle: Int) {
        invalidateStates(mode, textComponentStyle)
    }

    fun setEditorControlListener(editorControlListener: EditorControlListener) {
        this.editorControlListener = editorControlListener
    }

    interface EditorControlListener {
        fun onInsertImageClicked()

        fun onInsertLinkClicked()
    }

    companion object {
        const val MAX_HEADING = 5
    }
}
