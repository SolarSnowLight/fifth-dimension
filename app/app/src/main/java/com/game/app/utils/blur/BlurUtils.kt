package com.game.app.utils.blur

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.ViewGroup
import eightbitlab.com.blurview.BlurView
import eightbitlab.com.blurview.RenderScriptBlur

fun setupBlurView(blurView: BlurView,
                  base: ViewGroup,
                  drawable: Drawable,
                  context: Context,
                  radius: Float){
    blurView.setupWith(base)
        .setFrameClearDrawable(drawable)
        .setBlurAlgorithm(RenderScriptBlur(context))
        .setBlurRadius(radius)
        .setBlurAutoUpdate(true)
        .setHasFixedTransformationMatrix(false)
}