package com.yg.ble.pair

import android.annotation.SuppressLint
import android.app.Application
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.text.TextUtils
import android.util.Log
import com.yg.ble.callback.BleBondChangedCallBack
import com.yg.ble.data.BleDevice
import java.lang.Exception
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
    private val callBacks = CopyOnWriteArrayList<BleBondChangedCallBack>()
    private val boundStateFilter = IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED)
    private val boundStateBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val device = intent?.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
            val bondState = intent?.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, STATE_UNKNOWN)
            val preBondState =
                intent?.getIntExtra(BluetoothDevice.EXTRA_PREVIOUS_BOND_STATE, STATE_UNKNOWN)
            Log.d(TAG, "preBondState====$preBondState======bondState====$bondState")
            if (null == device || STATE_UNKNOWN == bondState || STATE_UNKNOWN == preBondState) {
                Log.d(TAG, "receiver bond state change  but query extra data fail")
                return
            }
            if (BluetoothDevice.BOND_NONE == preBondState && BluetoothDevice.BOND_BONDING == bondState) {
                for (callback in callBacks) {
                    callback.bonding()
                }
            }
            if (BluetoothDevice.BOND_BONDING == preBondState && BluetoothDevice.BOND_BONDED == bondState) {
                for (callback in callBacks) {
                    callback.bonded()
                }
            }
            if (BluetoothDevice.BOND_BONDED == preBondState && BluetoothDevice.BOND_NONE == bondState) {
                for (callback in callBacks) {
                    callback.unBond()
                }
            } else {
                for (callback in callBacks) {
                    callback.error()
                }
            }
        }

    }

    fun registerBroadCast() {
        context.registerReceiver(boundStateBroadcastReceiver, boundStateFilter)
    }

    override fun registerCallBack(callBack: BleBondChangedCallBack) {
        callBacks.add(callBack)
    }

    override fun unregisterCallBack(callBack: BleBondChangedCallBack) {
        callBacks.remove(callBack)
    }

    override fun findBoundDevice(mac: String?): BleDevice? {
        val bondedDevices = bluetoothAdapter?.bondedDevices ?: arrayListOf()
        for (device in bondedDevices) {
            if (!TextUtils.isEmpty(mac) && mac == device.address) {
                return BleDevice(device)
            }
        }
        return null
    }

    override fun unBoundDevice(bleDevice: BleDevice?): Boolean {
        val device = bleDevice?.getDevice() ?: return false
        val method = BluetoothDevice::class.java.getMethod("removeBond")
        return try {
            method.invoke(device)
            true
        } catch (ex: Exception) {
            false
        }
    }

    companion object {
        private const val STATE_UNKNOWN = -1
        private val TAG = BluetoothService::class.java.simpleName
    }
}