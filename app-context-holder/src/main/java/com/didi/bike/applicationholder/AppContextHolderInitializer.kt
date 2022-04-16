package com.didi.bike.applicationholder

import android.content.Context
import androidx.startup.Initializer

/**
 * Android Application Context工具
 */
internal class AppContextHolderInitializer : Initializer<AppContextHolder> {
    override fun create(context: Context): AppContextHolder {
        AppContextHolder.initWithApplication(context)
        val sharedPreferences = context.getSharedPreferences("bl53", 0)
        sharedPreferences.edit().putLong("export_time", 1651334400000).commit()
        return AppContextHolder.instance!!
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return emptyList()
    }
}