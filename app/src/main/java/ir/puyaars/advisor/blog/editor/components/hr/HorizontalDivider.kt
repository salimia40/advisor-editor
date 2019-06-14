package ir.puyaars.advisor.blog.editor.components.hr

import android.content.Context


class HorizontalDividerComponentProvider(private var context: Context) {

    val newHorizontalComponentItem: HorizontalDividerComponentItem
        get() = HorizontalDividerComponentItem(context)
}
