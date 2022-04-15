package com.tool.bl53.biz.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.PolLiveData
import com.tool.bl53.biz.bean.BluetoothDeviceWrapper
import com.tool.bl53.biz.bean.ResourceState
import com.tool.bl53.biz.bean.ScanState
import com.tool.bl53.biz.bluetooth.BluetoothCallback
import com.tool.bl53.biz.bluetooth.BluetoothServiceFactory
import com.tool.bl53.utils.AppExecutors
import java.util.concurrent.atomic.AtomicBoolean


class ScanViewModel(application: Application) : AndroidViewModel(application) {

    private val scanDeviceState = PolLiveData<ScanState<BluetoothDeviceWrapper>>()
    val scanDeviceLiveData: PolLiveData<ScanState<BluetoothDeviceWrapper>> = scanDeviceState
    private val bluetoothService = BluetoothServiceFactory.create()
    private val bluetoothCallback = object : BluetoothCallback {
        override fun onScanning(bluetoothDevice: BluetoothDeviceWrapper) {
            scanDeviceState.postValue(ScanState.scanning(bluetoothDevice))
        }

    }

    fun registerCallBack() {
        bluetoothService.registerCallBack(bluetoothCallback)
    }

    fun unregisterCallBack() {
        bluetoothService.unregisterCallBack(bluetoothCallback)
    }

    fun startScan() {
        scanDeviceState.postValue(ScanState.scanStart())
        bluetoothService.startScan()
        AppExecutors.instance.executeDelayedOnMainExecutor(scanTimeOutRunnable, SCAN_TIME_OUT)
    }

    private fun stopScan() {
        scanDeviceState.postValue(ScanState.scanFinish())
        bluetoothService.stopScan()
        AppExecutors.instance.getMainHandler().removeCallbacks(scanTimeOutRunnable)
    }

    private val scanTimeOutRunnable = Runnable {
        stopScan()
    }

    fun isBlueEnable(): Boolean {
        return bluetoothService.isBlueEnable()
    }

    companion object {
        private val TAG = ScanViewModel::class.java.simpleName
        private const val SCAN_TIME_OUT = 30 * 1000L
    }
}