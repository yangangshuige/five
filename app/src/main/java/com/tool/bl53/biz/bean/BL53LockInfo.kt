package com.tool.bl53.biz.bean

import java.io.Serializable

class BL53LockInfo : Serializable {
    private val id = 0
    private val lot_name: String = ""
    private val device_no: String = ""
    private val lockNo: String = ""
    private val lockId: String = ""
    private val bluetoothMacAdd: String = ""
    private val bluetoothVersion: String = ""
    private val bluetoothPassword: String = ""
    private val bluetoothKey: String = ""
    private val mcuVersion: String = ""
    private val mdmVersion: String = ""
    private val lockBatteryVoltage: String = ""
    private val mqttKey: String = ""
    private val iccid: String = ""
    private val imei: String = ""
    private val reg_time: Long = 0
    private val sort = 0
    private val status = 0
    override fun toString(): String {
        return "BL53LockInfo(id=$id, lot_name='$lot_name', device_no='$device_no', lockNo='$lockNo', lockId='$lockId', bluetoothMacAdd='$bluetoothMacAdd', bluetoothVersion='$bluetoothVersion', bluetoothPassword='$bluetoothPassword', bluetoothKey='$bluetoothKey', mcuVersion='$mcuVersion', mdmVersion='$mdmVersion', lockBatteryVoltage='$lockBatteryVoltage', mqttKey='$mqttKey', iccid='$iccid', imei='$imei', reg_time=$reg_time, sort=$sort, status=$status)"
    }

}