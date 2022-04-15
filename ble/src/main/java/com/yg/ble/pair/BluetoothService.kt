package com.yg.ble.pair

import com.yg.ble.callback.BleBondChangedCallBack
import com.yg.ble.data.BleDevice

interface BluetoothService {
    fun registerCallBack(callBack: BleBondChangedCallBack)
    fun unregisterCallBack(callBack: BleBondChangedCallBack)
    fun findBoundDevice(mac: String?): BleDevice?
    fun unBoundDevice(bleDevice: BleDevice?): Boolean
}