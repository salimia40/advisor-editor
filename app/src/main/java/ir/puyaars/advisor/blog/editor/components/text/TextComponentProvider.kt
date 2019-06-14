package ir.puyaars.advisor.blog.editor.components.text

import android.content.Context
import android.content.res.Resources
import android.graphics.Typeface
import android.text.Editable
import android.text.TextWatcher
import android.util.TypedValue
import android.view.KeyEvent
import android.view.View
import ir.puyaars.advisor.blog.R
import ir.puyaars.advisor.blog.editor.components.text.TextCore.Companion.MODE_PLAIN
import ir.puyaars.advisor.blog.editor.models.ComponentTag
import ir.puyaars.advisor.blog.editor.models.TextComponentModel
import ir.puyaars.advisor.blog.editor.models.TextComponentStyle.BLOCKQUOTE
import ir.puyaars.advisor.blog.editor.models.TextComponentStyle.H1
import ir.puyaars.advisor.blog.editor.models.TextComponentStyle.H5
import ir.puyaars.advisor.blog.editor.models.TextComponentStyle.NORMAL
import ir.puyaars.advisor.blog.editor.utils.FontSize
import kotlinx.android.synthetic.main.text_component_item.view.*
import kotlinx.android.synthetic.main.text_view_component_item.view.*


class TextComponentProvider(
    private val mContext: Context,
    private val _textComponentCallback: TextComponentCallback? = null
) {
    private val r: Resources = mContext.resources
    private var spaceExist: Boolean = false

    /**
     * Method to create new instance according to mode provided.
     * Mode can be [PLAIN, UL, OL]
     * @param mode mode of new TextComponentProvider.
     * @return new instance of TextComponentProvider.
     */
    fun newTextComponent(mode: Int): TextComponentItem {
        val customInput = TextComponentItem(mContext, mode = mode)
        val et = customInput.inputBox
        et.setImeActionLabel("Enter", KeyEvent.KEYCODE_ENTER)
        et.setOnKeyListener(object : View.OnKeyListener {
            override fun onKey(view: View, keyCode: Int, keyEvent: KeyEvent): Boolean {
                if (keyEvent.action != KeyEvent.ACTION_DOWN)
                    return true
                if (keyCode == KeyEvent.KEYCODE_DEL) {
                    _textComponentCallback?.onRemoveTextComponent((customInput.tag as ComponentTag).componentIndex!!)
                }
                return false
            }
        })

        et.onFocusChangeListener = View.OnFocusChangeListener { _, inFocus ->
            if (inFocus) {
                _textComponentCallback?.onFocusGained(customInput)
            }
        }

        et.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

            override fun onTextChanged(charSequence: CharSequence, start: Int, before: Int, count: Int) {
                val clen = charSequence.length
                if (clen > 0) {
                    val ch = charSequence[charSequence.length - 1]
                    if (isSpaceCharacter(ch) && before < count) {
                        if (spaceExist) {
                            var newString = charSequence.toString().trim { it <= ' ' }
                            newString = String.format("%s ", newString)
                            et.setText(newString)
                            et.setSelection(newString.length)
                        }
                        spaceExist = true
                    } else {
                        spaceExist = false
                    }

                    val sequenceToCheckNewLineCharacter = if (clen > 1)
                        charSequence.subSequence(clen - 2, clen).toString()
                    else
                        charSequence.subSequence(clen - 1, clen).toString()
                    val noReadableCharactersAfterCursor = sequenceToCheckNewLineCharacter.trim { it <= ' ' }.isEmpty()
                    //if last characters are [AB\n<space>] or [AB\n] then we insert new TextComponentProvider
                    //else if last characters are [AB\nC] ignore the insert.
                    if (sequenceToCheckNewLineCharacter.contains("\n") && noReadableCharactersAfterCursor) {
                        //If last characters are like [AB\n ] then new sequence will be [AB]
                        // i.e leave 2 characters from end.
                        //else if last characters are like [AB\n] then also new sequence will be [AB]
                        //but we need to leave 1 character from end.
                        val newSequence = if (sequenceToCheckNewLineCharacter.length > 1)
                            charSequence.subSequence(0, clen - 2)
                        else
                            charSequence.subSequence(0, clen - 1)
                        et.setText(newSequence)
                        _textComponentCallback?.onInsertTextComponent((customInput.tag as ComponentTag).componentIndex!!)
                    }
                }
            }

            override fun afterTextChanged(editable: Editable) {

            }
        })

        return customInput
    }

    private fun isSpaceCharacter(ch: Char): Boolean = ch == ' '


    /**
     * updates view with latest style info.
     * @param view to be updated.
     */
    fun updateComponent(view: View) {
        val componentTag = view.tag as ComponentTag

        //get heading
        val style = (componentTag.baseComponent as TextComponentModel).headingStyle

        val styleSubject = when (view) {
            is TextComponentItem -> view.inputBox
            is TextViewComponentItem -> view.textBox
            else -> view.inputBox
        }

        //get mode
        val mode = (view as TextCore).mode

        styleSubject.textSize = FontSize.getFontSize(style).toFloat()


        when (style) {
            in H1..H5 -> {
                styleSubject.setTypeface(null, Typeface.BOLD)
                styleSubject.setBackgroundResource(R.drawable.text_input_bg)
                styleSubject.setPadding(
                    dpToPx(16), //left
                    dpToPx(8), //top
                    dpToPx(16), //right
                    dpToPx(8)//bottom
                )
                styleSubject.setLineSpacing(2f, 1.1f)
            }

            NORMAL -> {
                styleSubject.setTypeface(null, Typeface.NORMAL)
                styleSubject.setBackgroundResource(R.drawable.text_input_bg)
                if (mode == MODE_PLAIN) {
                    styleSubject.setPadding(
                        dpToPx(16), //left
                        dpToPx(4), //top
                        dpToPx(16), //right
                        dpToPx(4)//bottom
                    )
                } else {
                    styleSubject.setPadding(
                        dpToPx(4), //left
                        dpToPx(4), //top
                        dpToPx(16), //right
                        dpToPx(4)//bottom
                    )
                }
                styleSubject.setLineSpacing(2f, 1.1f)
            }

            BLOCKQUOTE -> {
                styleSubject.setTypeface(null, Typeface.ITALIC)
                styleSubject.setBackgroundResource(R.drawable.blockquote_component_bg)
                styleSubject.setPadding(
                    dpToPx(16), //
                    dpToPx(2), //top
                    dpToPx(16), //right
                    dpToPx(2)//bottom
                )
                styleSubject.setLineSpacing(2f, 1.2f)
            }
        }
    }

    fun newTextViewComponent(mode: Int): TextViewComponentItem =
        TextViewComponentItem(context = mContext, mode = mode)

    /**
     * Convert dp to px value.
     * @param dp value
     * @return pixel value of given dp.
     */
    private fun dpToPx(dp: Int): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, dp.toFloat(), r.displayMetrics).toInt()
    }

    interface TextComponentCallback {
        fun onInsertTextComponent(selfIndex: Int)

        fun onFocusGained(view: View)

        fun onRemoveTextComponent(selfIndex: Int)
    }
}