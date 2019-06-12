package ir.puyaars.advisor.blog.editor.utils

import android.content.Context
import ir.puyaars.advisor.blog.editor.components.TextComponentItem
import ir.puyaars.advisor.blog.editor.models.BulletGroupModel
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import ir.puyaars.advisor.blog.editor.components.TextComponentItem.Companion.MODE_OL


open class Core(context: Context, attrs: AttributeSet?) : LinearLayout(context, attrs) {
    var bulletGroupModels: ArrayList<BulletGroupModel> = ArrayList()

    init {
        this.orientation = VERTICAL
    }

    /**
     * Creates bullet groups and invalidate the view.
     */
    fun refreshViewOrder() {
        makeBulletGroups()
        invalidateComponentMode(bulletGroupModels)
    }


    /**
     * This method find the group of bullets.
     * There can be 2 type of group.
     * {UL,StartIndex=0,EndIndex=3}
     * and
     * {OL,StartIndex=5,EndIndex=9}
     *
     *
     * These group are useful for maintaining correct order
     * even when view are inserted and deleted in any way.
     */
    private fun makeBulletGroups() {
        bulletGroupModels.clear()
        var startIndex: Int
        var endIndex = -1
        var child: View
        val childCount = childCount
        var i = 0
        while (i < childCount) {
            child = getChildAt(i)
            //skip non-text component items
            if (child is TextComponentItem) {
                if (child.mode == MODE_OL) {
                    startIndex = i
                    //search end of this group
                    for (j in i until childCount) {
                        i = j
                        child = getChildAt(j)
                        if (child is TextComponentItem) {
                            if (child.mode == MODE_OL) {
                                endIndex = i
                            } else {
                                break
                            }
                        } else {
                            break
                        }
                    }
                    //prepare model and add
                    val groupModel = BulletGroupModel()
                    groupModel.orderType = MODE_OL
                    groupModel.startIndex = startIndex
                    groupModel.endIndex = endIndex
                    bulletGroupModels.add(groupModel)
                }
            }
            i++
        }
    }

    /**
     * Helper method to update the bullets.
     * If view are inserted/removed, bullets are reassigned to view,
     * so we need to update the view.
     *
     * @param bulletGroupModels list of groups of bullets.
     */
    private fun invalidateComponentMode(bulletGroupModels: ArrayList<BulletGroupModel>) {
        var ot: Int
        var si: Int
        var ei: Int
        var tempChild: TextComponentItem
        //loop through each group
        for (i in 0 until bulletGroupModels.size) {
            ot = bulletGroupModels[i].orderType!!
            si = bulletGroupModels[i].startIndex ?: 0
            ei = bulletGroupModels[i].endIndex ?: 0
            if (ot == MODE_OL) {
                //set ol mode
                var ci = 1
                for (j in si..ei) {
                    try {
                        tempChild = getChildAt(j) as TextComponentItem
                        tempChild.mode = MODE_OL
                        tempChild.setIndicator("$ci.")
                        ci++
                    } catch (e: Exception) {
                        Log.d("EditorCore", "pos $j")
                        e.printStackTrace()
                    }

                }
            }
        }
    }
}
