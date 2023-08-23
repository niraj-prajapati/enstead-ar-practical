package com.nirajprajapati.enstead_ar_practtcal.helpers

import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent

class GestureHelper : SimpleOnGestureListener() {

    enum class GestureType {
        NONE, SINGLE_TAP, DOUBLE_TAP, LONG_PRESS
    }

    var gestureType = GestureType.NONE

    override fun onLongPress(e: MotionEvent) {
        super.onLongPress(e)
        gestureType = GestureType.LONG_PRESS
    }

    override fun onDoubleTap(e: MotionEvent): Boolean {
        gestureType = GestureType.DOUBLE_TAP
        return super.onDoubleTap(e)
    }

    override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
        gestureType = GestureType.SINGLE_TAP
        return super.onSingleTapConfirmed(e)
    }
}