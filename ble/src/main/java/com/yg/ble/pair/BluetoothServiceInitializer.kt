package com.yg.ble.pair

import android.content.Context
import androidx.startup.Initializer

class BluetoothServiceInitializer : Initializer<BluetoothService> {
    override fun create(context: Context): BluetoothService {
        return BluetoothServiceImpl(context).apply {
            registerBroadCast()
        }
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return emptyList()
    }
}