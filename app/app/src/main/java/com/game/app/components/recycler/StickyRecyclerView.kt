package com.game.app.components.recycler

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager

import androidx.recyclerview.widget.RecyclerView
import java.lang.IllegalStateException
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min


class StickyRecyclerView : RecyclerView {
    private var mScrollDirection = 0
    private var mCenterItemChangedListener: OnCenterItemChangedListener? = null

    constructor(context: Context?) : super(context!!) {}
    constructor(context: Context?, attrs: AttributeSet?) : super(
        context!!, attrs
    ) {
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(
        context!!, attrs, defStyle
    ) {
    }

    override fun onScrollStateChanged(state: Int) {
        super.onScrollStateChanged(state)
        if (state == SCROLL_STATE_IDLE) {
            if (mCenterItemChangedListener != null) {
                mCenterItemChangedListener!!.onCenterItemChanged(findCenterViewIndex())
            }
        }
    }

    override fun onScrolled(dx: Int, dy: Int) {
        super.onScrolled(dx, dy)
        scrollDirection = dx
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            val percentage = getPercentageFromCenter(child)
            val scale = 1f - 0.4f * percentage
            child.scaleX = scale
            child.scaleY = scale
        }
    }

    override fun fling(velocityX: Int, velocityY: Int): Boolean {
        smoothScrollToCenter()
        return true
    }

    private fun getPercentageFromCenter(child: View): Float {
        val centerX = (measuredWidth / 2).toFloat()
        val childCenterX = child.x + child.width / 2
        val offSet = max(centerX, childCenterX) - min(centerX, childCenterX)
        val maxOffset = measuredWidth / 2 + child.width
        return offSet / maxOffset
    }

    private fun findCenterViewIndex(): Int {
        val count = childCount
        var index = -1
        var closest = Int.MAX_VALUE
        val centerX = measuredWidth / 2
        for (i in 0 until count) {
            val child = layoutManager!!.getChildAt(i)
            val childCenterX = (child!!.x + child.width / 2).toInt()
            val distance = abs(centerX - childCenterX)
            if (distance < closest) {
                closest = distance
                index = i
            }
        }
        return if (index == -1) {
            throw IllegalStateException("Can\'t find central view.")
        } else {
            index
        }
    }

    private fun smoothScrollToCenter() {
        val linearLayoutManager = layoutManager as LinearLayoutManager?
        val lastVisibleView = linearLayoutManager!!.findLastVisibleItemPosition()
        val firstVisibleView = linearLayoutManager.findFirstVisibleItemPosition()
        val firstView = linearLayoutManager.findViewByPosition(firstVisibleView)
        val lastView = linearLayoutManager.findViewByPosition(lastVisibleView)
        val screenWidth = this.width

        //since views have variable sizes, we need to calculate side margins separately.
        val leftMargin = (screenWidth - lastView!!.width) / 2
        val rightMargin = (screenWidth - firstView!!.width) / 2 + firstView.width
        val leftEdge = lastView.left
        val rightEdge = firstView.right
        val scrollDistanceLeft = leftEdge - leftMargin
        val scrollDistanceRight = rightMargin - rightEdge
        if (mScrollDirection == SCROLL_DIRECTION_LEFT) {
            smoothScrollBy(scrollDistanceLeft, 0)
        } else if (mScrollDirection == SCROLL_DIRECTION_RIGHT) {
            smoothScrollBy(-scrollDistanceRight, 0)
        }
    }

    private var scrollDirection: Int
        get() = mScrollDirection
        private set(dx) {
            mScrollDirection = if (dx >= 0) SCROLL_DIRECTION_LEFT else SCROLL_DIRECTION_RIGHT
        }

    fun setOnCenterItemChangedListener(listener: OnCenterItemChangedListener?) {
        mCenterItemChangedListener = listener
    }

    interface OnCenterItemChangedListener {
        fun onCenterItemChanged(centerPosition: Int)
    }

    companion object {
        const val SCROLL_DIRECTION_LEFT = 0
        const val SCROLL_DIRECTION_RIGHT = 1
    }
}