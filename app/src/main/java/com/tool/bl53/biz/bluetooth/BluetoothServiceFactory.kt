package com.tool.bl53.biz.bluetooth

import androidx.startup.AppInitializer
import com.didi.bike.applicationholder.AppContextHolder

class BluetoothServiceFactory private constructor() {
    companion object {
        fun create(): BluetoothService {
            return AppInitializer.getInstance(AppContextHolder.applicationContext())
                .initializeComponent(BluetoothServiceInitializer::class.java)
        }
    }
}