package com.tool.bl53.biz.viewmodel

import androidx.lifecycle.PolLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tool.bl53.biz.bean.BL53LockInfo
import com.tool.bl53.biz.bean.ResourceState
import com.tool.bl53.network.NetworkUtils.callRequest
import com.tool.bl53.network.NetworkUtils.handlerResponse
import com.tool.bl53.network.RetrofitFactory
import com.tool.bl53.network.fold
import com.tool.bl53.utils.LogUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DeviceViewModel : ViewModel() {
    private val dispatcher = Dispatchers.IO
    private val lockInfoState = PolLiveData<ResourceState<BL53LockInfo>>()
    val lockInfoLiveData = lockInfoState
    fun queryBL53LockInfo(lockNo: String) {
        viewModelScope.launch {
            val response = withContext(dispatcher) {
                callRequest { handlerResponse(RetrofitFactory.instance.service.getBL53LockInfo("21063567")) }
            }
            response.fold(onSuccess = {
                LogUtils.d(TAG,it.toString())
                lockInfoState.postValue(ResourceState.success(it))
            }, onFailure = {
                lockInfoState.postValue(ResourceState.error(null, -1, ""))
            })
        }
    }
    companion object{
        private val TAG =DeviceViewModel::class.java.simpleName
    }
}