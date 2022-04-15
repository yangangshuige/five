package com.tool.bl53.biz.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.os.Build
import android.util.Log
import com.tool.bl53.biz.bean.BluetoothDeviceWrapper
import com.tool.bl53.utils.Utils
import com.yg.ble.utils.HexUtil
import java.util.concurrent.CopyOnWriteArrayList

@SuppressLint("MissingPermission")
internal class BluetoothServiceImpl(private val context: Context) : BluetoothService {
    private val bluetoothManager: BluetoothManager
        get() {
            return context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        }
    private val bluetoothAdapter: BluetoothAdapter?
        get() {
            return bluetoothManager?.adapter
        }
    private val callBacks = CopyOnWriteArrayList<BluetoothCallback>()


    override fun registerCallBack(callBack: BluetoothCallback) {
        callBacks.add(callBack)
    }

    override fun unregisterCallBack(callBack: BluetoothCallback) {
        callBacks.remove(callBack)
    }

    private val scanCallback = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
        BluetoothAdapter.LeScanCallback { device, rssi, scanRecord ->
            val tmp: MutableMap<String, Int> = Utils.getBroadcastId(scanRecord)
            val dt = tmp["deviceType"]
            if (dt == 0x9001 || dt == 0x9002) {
                for (call in callBacks) {
                    val deviceWrapper = BluetoothDeviceWrapper().apply {
                        this.rssi = rssi
                        this.name = device.name ?: ""
                        this.mac = device.address ?: ""
                        this.id = tmp["deviceId"] ?: 0
                    }
                    call.onScanning(deviceWrapper)
                }
            }
        }
    } else {
        object : ScanCallback() {
            override fun onScanResult(callbackType: Int, result: ScanResult?) {
                val scanRecord = result?.scanRecord?.bytes ?: byteArrayOf()
                val tmp: MutableMap<String, Int> = Utils.getBroadcastId(scanRecord)
                val dt = tmp["deviceType"]
                val id = tmp["deviceId"]
                if (dt == 0x9001 || dt == 0x9002) {
                    Log.d(TAG,"scanRecord==========="+Utils.debugByteData(scanRecord))
                    Log.d(TAG,"deviceType===========$dt")
                    Log.d(TAG,"deviceId===========$id")
                    for (call in callBacks) {
                        val deviceWrapper = BluetoothDeviceWrapper().apply {
                            this.rssi = result?.rssi ?: 0
                            this.name = result?.device?.name ?: ""
                            this.mac = result?.device?.address ?: ""
                            this.id = tmp["deviceId"] ?: 0
                        }
                        call.onScanning(deviceWrapper)
                    }
                }
            }
        }
    }


    override fun startScan() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            bluetoothAdapter?.startLeScan(scanCallback as BluetoothAdapter.LeScanCallback)
        } else {
            bluetoothAdapter?.bluetoothLeScanner?.startScan(scanCallback as ScanCallback)
        }
    }

    override fun stopScan() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            bluetoothAdapter?.stopLeScan(scanCallback as BluetoothAdapter.LeScanCallback)
        } else {
            bluetoothAdapter?.bluetoothLeScanner?.stopScan(scanCallback as ScanCallback)
        }
    }

    override fun isBlueEnable(): Boolean {
        return bluetoothAdapter != null && bluetoothAdapter!!.isEnabled
    }

    companion object {
        private const val STATE_UNKNOWN = -1
        private val TAG = BluetoothService::class.java.simpleName
    }
}