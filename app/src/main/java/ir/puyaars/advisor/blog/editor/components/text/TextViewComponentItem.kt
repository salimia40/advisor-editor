package ir.puyaars.advisor.blog.editor.components.text

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import ir.puyaars.advisor.blog.R
import kotlinx.android.synthetic.main.text_view_component_item.view.*


class TextViewComponentItem(context: Context, attrs: AttributeSet? = null, mode: Int = MODE_PLAIN) :
    TextCore(context, attrs, mode) {


    var indicatorText: String? = null
        private set

    override fun setMode(mode: Int) {
        when (mode) {
            MODE_PLAIN -> {
                indicatorTv.visibility = View.GONE
                textBox.setBackgroundResource(R.drawable.text_input_bg)
            }
            MODE_UL -> {
                indicatorTv.text = UL_BULLET
                indicatorTv.visibility = View.VISIBLE
                textBox.setBackgroundResource(R.drawable.text_input_bg)
            }
            MODE_OL -> {
                indicatorTv.visibility = View.VISIBLE
                textBox.setBackgroundResource(R.drawable.text_input_bg)
            }
        }
        this.mode = mode
    }

    init {
        LayoutInflater.from(context).inflate(R.layout.text_view_component_item, this)
    }

    override fun setIndicator(bullet: String) {
        indicatorTv.text = bullet
        this.indicatorText = bullet
    }


    override fun setText(content: String) {
        textBox.text = content
    }

}
