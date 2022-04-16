package com.tool.bl53.biz.viewmodel

import android.app.Application
import android.bluetooth.BluetoothGatt
import android.util.Log
import androidx.annotation.MainThread
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.PolLiveData
import com.tool.bl53.biz.bean.CmdParams
import com.tool.bl53.biz.bean.ConnectState
import com.tool.bl53.biz.bean.ResourceState
import com.tool.bl53.utils.AppExecutors
import com.tool.bl53.utils.DataUtil
import com.tool.bl53.utils.Utils
import com.yg.ble.BleManager
import com.yg.ble.callback.BleGattCallback
import com.yg.ble.callback.BleNotifyCallback
import com.yg.ble.callback.BleWriteCallback
import com.yg.ble.data.BleDevice
import com.yg.ble.exception.BleException
import com.yg.ble.utils.HexUtil
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean


class ConnectViewModel(application: Application) : AndroidViewModel(application) {

    private var mTokenData: ByteArray? = null

    /** 防止指令重复执行 */
    private val commandExecuteState = HashMap<String, AtomicBoolean>()

    /** 指令执行的状态 */
    private val commandExecuteResults =
        HashMap<String, PolLiveData<ResourceState<Any>>>()

    /** 超时任务 */
    private val timeOutRunnable = HashMap<String, TimeOutRunnable>()
    private val connectState = PolLiveData<ConnectState<BleDevice>>()
    val connectLiveData: PolLiveData<ConnectState<BleDevice>> = connectState
    private val notifyState = PolLiveData<Boolean>()
    val notifyLiveData: PolLiveData<Boolean> = notifyState
    private var mConnectDevice: BleDevice? = null
    var mLockModel = -1

    private val bleGattCallback = object : BleGattCallback() {
        override fun onStartConnect() {
            connectState.postValue(ConnectState.connectStart())
        }

        override fun onConnectFail(bleDevice: BleDevice?, exception: BleException?) {
            connectState.postValue(ConnectState.connectFail(bleDevice))
        }

        override fun onConnectSuccess(
            bleDevice: BleDevice?,
            gatt: BluetoothGatt?,
            status: Int,
        ) {
            //  连接状态通知
            connectState.postValue(ConnectState.connectSuccess(bleDevice))

            mConnectDevice = bleDevice
            //  延迟2秒开启通知
            AppExecutors.instance.executeDelayedOnMainExecutor(notifyRunnable, 2000)
        }

        override fun onDisConnected(
            isActiveDisConnected: Boolean,
            device: BleDevice?,
            gatt: BluetoothGatt?,
            status: Int,
        ) {
            connectState.postValue(ConnectState.disConnected(device))
        }

    }

    fun connectDevice(mac: String) {
        BleManager.instance.connect(mac, bleGattCallback)
    }

    fun isConnected(): Boolean = connectState.value is ConnectState.ConnectSuccess

    private fun getToken() {
        val data = byteArrayOf(0x00, 0x00, 0x01, 0xff.toByte())
        val command = fillZero(data)
        Log.d(TAG, "getToken初始数据：========${DataUtil.byteToString(command)}")
        executeCommand(CmdParams(CmdParams.CMD_GET_TOKEN, command))
    }

    private fun syncTime() {
        val syncedTime = (System.currentTimeMillis() / 1000).toInt()
        val data = byteArrayOf(
            0x01,
            0x00,
            0x04,
            (syncedTime shr 24 and 0xFF).toByte(),
            (syncedTime shr 16 and 0xFF).toByte(),
            (syncedTime shr 8 and 0xFF).toByte(),
            (syncedTime and 0xFF).toByte()
        )

        val command = fillToken(data)
        Log.d(TAG, "syncTime初始数据：========${DataUtil.byteToString(command)}")
        executeCommand(CmdParams(CmdParams.CMD_SYNC_TIME, command))
    }

    fun getMv() {
        val data = byteArrayOf(
            0x02,
            0x00,
            0x01,
            0x01
        )

        val command = fillToken(data)
        Log.d(TAG, "getMv初始数据：========${DataUtil.byteToString(command)}")
        executeCommand(CmdParams(CmdParams.CMD_GET_MV, command))
    }

    fun openLock() {
        val head = byteArrayOf(
            0x03,
            0x00,
            0x09
        )

        val password = fillPassword(head)
        val serialNumber = fillSerialNumber(password)
        val command = fillToken(serialNumber)
        Log.d(TAG, "openLock初始数据：========${DataUtil.byteToString(command)}")
        executeCommand(CmdParams(CmdParams.CMD_OPEN_LOCK, command))
    }

    fun closeLock() {
        val head = byteArrayOf(
            0x04,
            0x00,
            0x09
        )

        val password = fillPassword(head)
        val serialNumber = fillSerialNumber(password)
        val command = fillToken(serialNumber)
        Log.d(TAG, "closeLock初始数据：========${DataUtil.byteToString(command)}")
        executeCommand(CmdParams(CmdParams.CMD_CLOSE_LOCK, command))
    }

    fun getLockModel() {
        val data = byteArrayOf(
            0x08,
            0x10,
            0x01,
            0x01
        )
        val command = fillToken(data)
        Log.d(TAG, "getLockModel初始数据：========${DataUtil.byteToString(command)}")
        executeCommand(CmdParams(CmdParams.CMD_GET_MODEL, command))
    }

    fun setLockModel(model: Int) {
        mLockModel = model
        val data = byteArrayOf(
            0x08,
            0x00,
            0x01,
            model.toByte()
        )
        val command = fillToken(data)
        Log.d(TAG, "setLockModel初始数据：========${DataUtil.byteToString(command)}")
        executeCommand(CmdParams(CmdParams.CMD_SET_MODEL, command))
    }

    private fun fillPassword(byteArray: ByteArray): ByteArray {
        val password = DataUtil.getPassword()
        val data = ByteArray(byteArray.size + password.size)
        System.arraycopy(byteArray, 0, data, 0, byteArray.size)
        System.arraycopy(password, 0, data, byteArray.size, password.size)
        return data
    }

    private fun fillSerialNumber(byteArray: ByteArray): ByteArray {
        val serialNumber = ByteArray(3)
        Random().nextBytes(serialNumber)
        val data = ByteArray(byteArray.size + serialNumber.size)
        System.arraycopy(byteArray, 0, data, 0, byteArray.size)
        System.arraycopy(serialNumber, 0, data, byteArray.size, serialNumber.size)
        return data
    }

    private fun fillToken(byteArray: ByteArray): ByteArray {
        if (mTokenData == null) return byteArrayOf()
        val data = ByteArray(16)
        System.arraycopy(byteArray, 0, data, 0, byteArray.size)
        System.arraycopy(mTokenData, 0, data, byteArray.size, mTokenData!!.size)
        return data
    }

    private fun fillZero(byteArray: ByteArray): ByteArray {
        val data = ByteArray(16)
        for (index in 0..15) {
            if (index < byteArray.size) {
                data[index] = byteArray[index]
            } else {
                data[index] = 0x00
            }
        }
        return data
    }

    @Synchronized
    private fun parseReceiverData(data: ByteArray) {
        val realData = DataUtil.decryptAes128(data) ?: return
        Log.d(TAG, "解密数据：========${DataUtil.byteToString(realData)}")
        when (Utils.byteArray2int(byteArrayOf(realData[0], realData[1]))) {
            0x0080 -> {
                mTokenData = realData.copyOfRange(3, 7)
                Log.d(TAG, "获取token成功：========${DataUtil.byteToString(mTokenData!!)}")
                val version = "V_" + realData[8].toInt() + "." +
                        realData[9].toInt() + "." + (realData[11].toInt() +
                        realData[12].toInt())
                AppExecutors.instance.getMainHandler()
                    .removeCallbacks(cmdTimeOutRunnable(CmdParams.CMD_GET_TOKEN))
                commandExecuteStateLiveData(CmdParams.CMD_GET_TOKEN).postValue(
                    ResourceState.success(version)
                )
                commandState(CmdParams.CMD_GET_TOKEN).set(false)
                syncTime()
            }
            0x0180 -> {
                AppExecutors.instance.getMainHandler()
                    .removeCallbacks(cmdTimeOutRunnable(CmdParams.CMD_SYNC_TIME))
                commandExecuteStateLiveData(CmdParams.CMD_SYNC_TIME).postValue(
                    ResourceState.success(0 == realData[3].toInt())
                )
                commandState(CmdParams.CMD_SYNC_TIME).set(false)
            }
            0x0280 -> {
                Log.d(TAG, "获取电压成功[3]：========${realData[3].toInt()}")
                Log.d(TAG, "获取电压成功[4]：========${realData[4].toInt()}")
//                val mv = "${realData[3].toInt()}.${realData[4].toInt()}v"
                val hb: Int = if (realData[3] < 0) realData[3] + 256 else realData[3].toInt()
                val lb: Int = if (realData[4] < 0) realData[4] + 256 else realData[4].toInt()
                val mv = "v" + ((hb * 256 + lb) * 1.0f / 100)
                AppExecutors.instance.getMainHandler()
                    .removeCallbacks(cmdTimeOutRunnable(CmdParams.CMD_GET_MV))
                commandExecuteStateLiveData(CmdParams.CMD_GET_MV).postValue(
                    ResourceState.success(mv)
                )
                commandState(CmdParams.CMD_GET_MV).set(false)
            }
            0x0380 -> {
                Log.d(TAG, "开锁成功：========${realData[3].toInt() == 0}")
                AppExecutors.instance.getMainHandler()
                    .removeCallbacks(cmdTimeOutRunnable(CmdParams.CMD_OPEN_LOCK))
                commandExecuteStateLiveData(CmdParams.CMD_OPEN_LOCK).postValue(
                    ResourceState.success(true)
                )
                commandState(CmdParams.CMD_OPEN_LOCK).set(false)
            }
            0x0480 -> {
                Log.d(TAG, "关锁成功：========${realData[3].toInt() == 0}")
                AppExecutors.instance.getMainHandler()
                    .removeCallbacks(cmdTimeOutRunnable(CmdParams.CMD_CLOSE_LOCK))
                commandExecuteStateLiveData(CmdParams.CMD_CLOSE_LOCK).postValue(
                    ResourceState.success(true)
                )
                commandState(CmdParams.CMD_CLOSE_LOCK).set(false)
            }
            0x0880 -> {
                Log.d(TAG, "设置工作模式成功：========${realData[3].toInt() == 0}")
                AppExecutors.instance.getMainHandler()
                    .removeCallbacks(cmdTimeOutRunnable(CmdParams.CMD_SET_MODEL))
                commandExecuteStateLiveData(CmdParams.CMD_SET_MODEL).postValue(
                    ResourceState.success(0 == realData[3].toInt())
                )
                commandState(CmdParams.CMD_SET_MODEL).set(false)
            }
            0x0890 -> {
                Log.d(TAG, "查询工作模式成功：========${realData[3].toInt()}")
                mLockModel = realData[3].toInt()
                AppExecutors.instance.getMainHandler()
                    .removeCallbacks(cmdTimeOutRunnable(CmdParams.CMD_GET_MODEL))
                commandExecuteStateLiveData(CmdParams.CMD_GET_MODEL).postValue(
                    ResourceState.success(true)
                )
                commandState(CmdParams.CMD_GET_MODEL).set(false)
            }
        }
    }

    @Synchronized
    fun commandExecuteStateLiveData(command: String): PolLiveData<ResourceState<Any>> {
        var state = commandExecuteResults[command]
        if (null == state) {
            state = PolLiveData()
            commandExecuteResults[command] = state
        }

        return state
    }

    @Synchronized
    private fun commandState(command: String): AtomicBoolean {
        var state = commandExecuteState[command]
        if (null == state) {
            state = AtomicBoolean(false)
            commandExecuteState[command] = state
        }

        return state
    }

    @MainThread
    private fun executeCommand(cmdParams: CmdParams) {
        val command = cmdParams.command
        // 指令还未执行结束，不再重复执行
        val executeState = commandState(command)
        if (!executeState.compareAndSet(false, true)) return
        val executeResult = commandExecuteStateLiveData(command)
        executeResult.postValue(ResourceState.loading())
        val buff = cmdParams.params?.let { DataUtil.encryptAes128(it) }
        buff?.let { write(it) }
        AppExecutors.instance.executeDelayedOnMainExecutor(cmdTimeOutRunnable(command), 5000)
    }

    @Synchronized
    private fun cmdTimeOutRunnable(command: String): TimeOutRunnable {
        var runnable = timeOutRunnable[command]
        if (null == runnable) {
            runnable = TimeOutRunnable(commandExecuteStateLiveData(command), commandState(command))
            timeOutRunnable[command] = runnable
        }

        return runnable
    }

    private val writeCallback = object : BleWriteCallback() {
        override fun onWriteSuccess(current: Int, total: Int, justWrite: ByteArray) {
            Log.d(TAG, "onWriteSuccess========${DataUtil.byteToString(justWrite)}")
        }

        override fun onWriteFailure(exception: BleException) {
            Log.d(TAG, "onWriteFailure========${exception?.description}")
        }

    }

    private fun write(data: ByteArray) {
        BleManager.instance
            .write(
                mConnectDevice,
                SERVICE_UUID,
                WRITE_UUID,
                data,
                split = false,
                sendNextWhenLastSuccess = false,
                intervalBetweenTwoPackage = 0L,
                callback = writeCallback
            )
    }

    private val notifyCallback = object : BleNotifyCallback() {
        override fun onNotifySuccess() {
            Log.d(TAG, "onNotifySuccess========")
            notifyState.postValue(true)
            getToken()
            AppExecutors.instance.getMainHandler().removeCallbacks(notifyRunnable)
        }

        override fun onNotifyFailure(exception: BleException?) {
            notifyState.postValue(false)
            AppExecutors.instance.executeDelayedOnMainExecutor(notifyRunnable, 2000)
            Log.d(TAG, "onNotifyFailure========${exception?.description}")
        }

        override fun onCharacteristicChanged(data: ByteArray?) {
            Log.d(TAG, "onCharacteristicChanged========${HexUtil.encodeHexStr(data!!)}")
            if (data != null)
                parseReceiverData(data)
        }

    }

    private val notifyRunnable = Runnable {
        enableNotify()
    }

    private fun enableNotify() {
        BleManager.instance.notify(mConnectDevice, SERVICE_UUID, NOTIFY_UUID, false, notifyCallback)
    }

    private val reConnectRunnable = Runnable {
        Log.d(TAG, "reConnectRunnable====")
        reConnect()

    }

    private fun reConnect() {
        BleManager.instance.disconnectAllDevice()
        BleManager.instance.connect(mConnectDevice, bleGattCallback)
    }

    class TimeOutRunnable(
        private val executeResult: PolLiveData<ResourceState<Any>>,
        private val commandState: AtomicBoolean,
    ) : Runnable {
        override fun run() {
            executeResult.postValue(ResourceState.error(null, -1, "操作超时"))
            commandState.set(false)
            AppExecutors.instance.getMainHandler().removeCallbacks(this)
        }

    }

    override fun onCleared() {
        super.onCleared()
        BleManager.instance.destroy()
    }

    companion object {
        private val TAG = ConnectViewModel::class.java.simpleName
        private const val SERVICE_UUID = "0000FEE7-0000-1000-8000-00805F9B34FB"
        private const val WRITE_UUID = "000036F5-0000-1000-8000-00805F9B34FB"
        private const val NOTIFY_UUID = "000036F6-0000-1000-8000-00805F9B34FB"
    }
}