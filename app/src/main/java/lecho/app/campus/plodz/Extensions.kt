package lecho.app.campus.plodz

import android.content.Context

fun Int.pxToDp(context: Context): Int = (this / context.resources.displayMetrics.density).toInt()

fun Int.dpToPx(context: Context): Int = (this * context.resources.displayMetrics.density).toInt()
