package com.tool.bl53.biz.bluetooth

import com.tool.bl53.biz.bean.BluetoothDeviceWrapper

interface BluetoothService {
    fun registerCallBack(callBack: BluetoothCallback)
    fun unregisterCallBack(callBack: BluetoothCallback)
    fun startScan()
    fun stopScan()
    fun isBlueEnable(): Boolean

}

interface BluetoothCallback {
    fun onScanning(bluetoothDevice: BluetoothDeviceWrapper)

}