package com.game.app.utils.viewpager

import android.view.View
import android.view.ViewTreeObserver
import androidx.core.view.children
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2

fun ViewPager2.wrapContentHeightWithTallestChild() {
    (children.firstOrNull() as? RecyclerView)?.layoutManager?.let { layoutManager ->
        offscreenPageLimit = layoutManager.itemCount

        viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                var tallestHeight = layoutParams.height

                for (i in 0 until layoutManager.itemCount) {
                    layoutManager.findViewByPosition(i)?.let { child ->
                        child.measure(
                            View.MeasureSpec.makeMeasureSpec(child.width, View.MeasureSpec.EXACTLY),
                            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
                        )

                        if (child.measuredHeight > tallestHeight) {
                            tallestHeight = child.measuredHeight
                        }
                    }
                }

                layoutParams = layoutParams.also { it.height = tallestHeight }

                viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })
    }
}