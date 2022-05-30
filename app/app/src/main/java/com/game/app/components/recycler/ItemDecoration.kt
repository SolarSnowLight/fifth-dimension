package com.game.app.components.recycler

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.max

class ItemDecoration(
    private val startPadding: Int,
    private val endPadding: Int
) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect, view: View, parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val totalWidth = parent.width

        //first element
        if (parent.getChildAdapterPosition(view) == 0) {
            var firstPadding = (totalWidth - startPadding) / 2
            firstPadding = max(0, firstPadding)
            outRect[firstPadding, 0, 0] = 0
        }

        //last element
        if (parent.getChildAdapterPosition(view) == parent.adapter!!.itemCount - 1 &&
            parent.adapter!!.itemCount > 1
        ) {
            var lastPadding = (totalWidth - endPadding) / 2
            lastPadding = max(0, lastPadding)
            outRect[0, 0, lastPadding] = 0
        }
    }
}