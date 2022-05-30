package com.game.app.utils.recycler

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.game.app.containers.base.BaseAdapter

fun <T, B: ViewBinding> RecyclerView.setAdapter(
    context: Context?,
    adapter: BaseAdapter<T, B>?,
    type: Int = RecyclerView.HORIZONTAL,
    reverseLayout: Boolean = false,
    scrollView: Boolean = false
){
    val layoutManager: RecyclerView.LayoutManager
            = LinearLayoutManager(
        context,
        type,
        reverseLayout
    )

    this.layoutManager = layoutManager
    this.adapter = adapter

    if(scrollView){
        this.addOnScrollListener(RecyclerScrollListener(layoutManager))
    }
}