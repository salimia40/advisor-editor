package ir.puyaars.advisor.blog.editor.components.text

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout

abstract class TextCore(context: Context, attrs: AttributeSet? = null, mode: Int = MODE_PLAIN) :
    FrameLayout(context, attrs) {

    var mode: Int? = null

    init {
        this.mode = mode
    }

    abstract fun setMode(mode: Int)
    abstract fun setIndicator(bullet: String)
    abstract fun setText(content: String)

    companion object {
        const val MODE_PLAIN = 0
        const val MODE_UL = 1
        const val MODE_OL = 2

        const val UL_BULLET = "\u2022"
    }
}