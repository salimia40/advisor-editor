package ir.puyaars.advisor.blog.editor.components

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import ir.puyaars.advisor.blog.R


class HorizontalDividerComponent(private var context: Context) {

    val newHorizontalComponentItem: HorizontalDividerComponentItem
        get() = HorizontalDividerComponentItem(context)
}

class HorizontalDividerComponentItem : FrameLayout {
    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context)
    }

    private fun init(context: Context) {
        LayoutInflater.from(context).inflate(R.layout.horizontal_divider_item_view, this)
    }
}