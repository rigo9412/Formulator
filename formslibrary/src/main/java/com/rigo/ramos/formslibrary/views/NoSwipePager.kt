package com.rigo.ramos.formslibrary.views


import android.content.Context
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager
import android.util.AttributeSet


class NoSwipePager(context: Context, attrs: AttributeSet) : ViewPager(context, attrs) {

    var isEnable = false

    init {
        this.isEnable = true
        //this.setPageTransformer(true,FadePageTransfomer())
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return if (this.isEnable) {
            super.onTouchEvent(event)
        } else false
    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        return if (this.isEnable) {
            super.onInterceptTouchEvent(event)
        } else false
    }

    fun setPagingEnabled(enabled: Boolean) {
        this.isEnable = enabled
    }
}