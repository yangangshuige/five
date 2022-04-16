package com.tool.bl53.biz.viewmodel

import android.util.Log
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.PolLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tool.bl53.biz.bean.BL53LockInfo
import com.tool.bl53.biz.bean.BluetoothDeviceWrapper
import com.tool.bl53.biz.bean.ResourceState
import com.tool.bl53.biz.bean.SecurityInfoVO
import com.tool.bl53.network.NetworkUtils.callRequest
import com.tool.bl53.network.NetworkUtils.handlerResponse
import com.tool.bl53.network.RetrofitFactory
import com.tool.bl53.network.fold
import com.tool.bl53.utils.DataUtil
import com.tool.bl53.utils.LogUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DeviceViewModel : ViewModel() {
    private val dispatcher = Dispatchers.IO
    private val lockInfoState = PolLiveData<ResourceState<BluetoothDeviceWrapper>>()
    val lockInfoLiveData = lockInfoState

    @VisibleForTesting
    fun queryDeviceInfo(
        lockMac: String?,
        lockCode: String?,
        lockName: String?,
        queryByMac: Boolean
    ) {
        val macRequest = if (lockMac.isNullOrEmpty()) "" else lockMac.replace(":", "")
        viewModelScope.launch {
            val response = withContext(dispatcher) {
                callRequest {
                    handlerResponse(
                        if (queryByMac) {
                            RetrofitFactory.instance.service.queryDeviceInfoByMac(
                                macRequest

                            )
                        } else {
                            RetrofitFactory.instance.service.queryDeviceInfoByCode(
                                lockCode
                            )
                        }

                    )
                }
            }
            response.fold(onSuccess = { data ->
                if (data.available()) {
                    LogUtils.d("获取设备信息成功")
                    DataUtil.defaultAesKey = DataUtil.hexToBytes(data.key!!)
                    DataUtil.defaultPassword = data.passwd!!
                    lockInfoState.postValue(ResourceState.success(BluetoothDeviceWrapper().apply {
                        this.name = lockName
                        this.mac = data.mac
                    }))
                } else {
                    LogUtils.d("获取设备信息失败")
                    lockInfoState.postValue(ResourceState.error(null, -1, ""))
                }

            }, onFailure = {
                LogUtils.d("网络错误")
                lockInfoState.postValue(ResourceState.error(null, -1, ""))
            })
        }
    }

    companion object {
        private val TAG = DeviceViewModel::class.java.simpleName
    }
}