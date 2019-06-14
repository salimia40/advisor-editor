package ir.puyaars.advisor.blog.editor.components.text

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import ir.puyaars.advisor.blog.R
import ir.puyaars.advisor.blog.editor.models.ComponentTag
import ir.puyaars.advisor.blog.editor.models.TextComponentModel
import kotlinx.android.synthetic.main.text_component_item.view.*


class TextComponentItem(context: Context, attrs: AttributeSet? = null, mode: Int = MODE_PLAIN) :
    TextCore(context, attrs, mode) {

    var indicatorText: String? = null
        private set

    override fun setMode(mode: Int) {
        when (mode) {
            MODE_PLAIN -> {
                indicatorTv.visibility = View.GONE
                inputBox.setBackgroundResource(R.drawable.text_input_bg)
            }
            MODE_UL -> {
                indicatorTv.text = UL_BULLET
                indicatorTv.visibility = View.VISIBLE
                inputBox.setBackgroundResource(R.drawable.text_input_bg)
            }
            MODE_OL -> {
                indicatorTv.visibility = View.VISIBLE
                inputBox.setBackgroundResource(R.drawable.text_input_bg)
            }
        }
        this.mode = mode
    }

    init {
        LayoutInflater.from(context).inflate(R.layout.text_component_item, this)
        this.mode = mode
    }

    //check heading
    val textHeadingStyle: Int
        get() {
            val componentTag = tag as ComponentTag
            return (componentTag.baseComponent as TextComponentModel).headingStyle
        }

    val content: String
        get() = inputBox.text.toString()


    fun setHintText(hint: String) {
        inputBox.hint = hint
    }

    override fun setText(content: String) {
        inputBox.setText(content)
    }

    override fun setIndicator(bullet: String) {
        indicatorTv.text = bullet
        this.indicatorText = bullet
    }
}

