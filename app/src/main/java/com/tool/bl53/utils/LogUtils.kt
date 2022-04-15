package com.tool.bl53.utils

import android.util.Log
import com.tool.bl53.BuildConfig


object LogUtils {

    private val allow: Boolean = BuildConfig.DEBUG

    private val TAG = LogUtils::class.java.simpleName

    fun d(msg: String?) {
        if (allow) {
            Log.d(TAG, "===========$msg")
        }
    }

    fun d(tag: String, msg: String?) {
        if (allow) {
            Log.d(tag, "===========$msg")
        }
    }

    fun w(msg: String?) {
        if (allow) {
            Log.w(TAG, "===========$msg")
        }
    }

    fun w(tag: String, msg: String?) {
        if (allow) {
            Log.w(tag, "===========$msg")
        }
    }

    fun i(msg: String?) {
        if (allow) {
            Log.i(TAG, "===========$msg")
        }
    }

    fun i(tag: String, msg: String?) {
        if (allow) {
            Log.i(tag, "===========$msg")
        }
    }

    fun e(msg: String?) {
        if (allow) {
            Log.e(TAG, "===========$msg")
        }
    }

    fun e(tag: String, msg: String?) {
        if (allow) {
            Log.e(tag, "===========$msg")
        }
    }


}