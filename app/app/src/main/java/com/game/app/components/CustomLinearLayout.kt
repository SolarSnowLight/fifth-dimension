package com.game.app.components

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout

class CustomLinearLayout: LinearLayout {
    @JvmOverloads
    constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0)
            : super(context, attrs, defStyleAttr)

}