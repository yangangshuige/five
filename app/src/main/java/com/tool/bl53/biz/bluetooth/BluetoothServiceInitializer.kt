package com.tool.bl53.biz.bluetooth

import android.content.Context
import androidx.startup.Initializer

class BluetoothServiceInitializer : Initializer<BluetoothService> {
    override fun create(context: Context): BluetoothService {
        return BluetoothServiceImpl(context)
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return emptyList()
    }
}