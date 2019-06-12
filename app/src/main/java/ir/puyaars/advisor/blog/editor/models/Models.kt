package ir.puyaars.advisor.blog.editor.models

import ir.puyaars.advisor.blog.editor.models.TextComponentStyle.NORMAL

open class BaseComponentModel(open var componentType: String? = null, open var componentIndex: Int? = null)

data class BulletComponentModel(var orderType: String? = null,var content: String? = null)

data class BulletGroupModel(
    var orderType : Int? = null,
    var startIndex : Int? = null,
    var endIndex : Int? = null
)

data class ComponentTag(var componentIndex: Int? = null, var baseComponent: BaseComponentModel? = null)

data class DraftModel(
    var draftTitle : String? = null,
    var draftId : Long? = null,
    var items : ArrayList<DraftDataItemModel>? = null
) {
    companion object {
        const val ITEM_TYPE_TEXT = 0
        const val ITEM_TYPE_IMAGE = 1
        const val ITEM_TYPE_HR = 2
    }
}

data class DraftDataItemModel(
    var itemType: Int? = null,
    var mode: Int? = null,
    var style: Int? = null,
    var content: String? = null,
    var downloadUrl: String? = null,
    var caption: String? = null
)

data class ImageComponentModel(
    var url : String? = null,
    var caption: String? = null, override var componentType: String? = null, override var componentIndex: Int? = null
) : BaseComponentModel(componentType, componentIndex)

data class TextComponentModel(
    var headingStyle: Int = NORMAL,
    override var componentIndex: Int? = null,
    override var componentType: String? = null
) : BaseComponentModel(componentType, componentIndex)

object TextComponentStyle {
    const val NORMAL = 0
    const val H1 = 1
    const val H2 = 2
    const val H3 = 3
    const val H4 = 4
    const val H5 = 5
    const val BLOCKQUOTE = 6
}
