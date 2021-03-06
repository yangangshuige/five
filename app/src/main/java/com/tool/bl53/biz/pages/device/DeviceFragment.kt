package com.tool.bl53.biz.pages.device

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import androidx.fragment.app.*
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.tool.bl53.R
import com.tool.bl53.biz.bean.BluetoothDeviceWrapper
import com.tool.bl53.biz.bean.CmdParams
import com.tool.bl53.biz.bean.ConnectState
import com.tool.bl53.biz.bean.ResourceState
import com.tool.bl53.biz.viewmodel.ConnectViewModel
import com.tool.bl53.databinding.FragmentDeviceBinding
import com.tool.bl53.utils.DataUtil
import com.yg.ble.data.BleDevice

class DeviceFragment : Fragment(R.layout.fragment_device) {
    private lateinit var viewBinding: FragmentDeviceBinding
    private var bleDevice: BluetoothDeviceWrapper? = null
    private val connectViewModel: ConnectViewModel by viewModels()
    private val connectStatus = Observer<ConnectState<BleDevice>> { connectState ->
        run {
            when (connectState) {
                is ConnectState.ConnectStart -> {
                    showLoading("开始连接...")
                    viewBinding.tvStatus.text = "连接状态：开始连接..."
                }
                is ConnectState.ConnectSuccess -> {
                    hideLoading()
                    viewBinding.tvStatus.text = "连接状态：连接成功"
                }
                is ConnectState.ConnectFail -> {
                    hideLoading()
                    viewBinding.tvStatus.text = "连接状态：连接失败"
                }
                is ConnectState.DisConnected -> {
                    hideLoading()
                    viewBinding.tvStatus.text = "连接状态：连接断开"
                }
            }
        }
    }
    private val items = arrayOf("正常模式", "运输模式", "设备重启", "休眠模式", "常开模式", "常闭模式", " 测试模式")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding = FragmentDeviceBinding.bind(view)
        bleDevice = arguments?.getParcelable(KEY_DEVICE_DATA)
        if (null == bleDevice) {
            showToast("设备不存在")
            findNavController().popBackStack()
        }
        bleDevice?.mac = DataUtil.toMac(bleDevice?.mac!!)
        connectViewModel.connectDevice(bleDevice?.mac!!)
        viewBinding.tvName.text = "Name：${bleDevice?.name}"
        viewBinding.tvMac.text = "Name：${bleDevice?.mac}"
        connectViewModel.connectLiveData.observeUnSticky(viewLifecycleOwner, connectStatus)
        connectViewModel.commandExecuteStateLiveData(CmdParams.CMD_GET_TOKEN)
            .observeUnSticky(viewLifecycleOwner) { tokenState ->
                when (tokenState) {
                    is ResourceState.Loading -> {
                        showLoading("获取token...")
                        viewBinding.tvCurrent.text = "当前操作：获取token..."
                    }
                    is ResourceState.Success -> {
                        viewBinding.tvVersion.text = "固件版本：" + tokenState.data as String
                        viewBinding.tvCurrent.text = "当前操作：获取token成功"
                        hideLoading()
                    }
                    is ResourceState.Error -> {
                        showMessageFinish("获取token失败")
                        viewBinding.tvCurrent.text = "当前操作：获取token失败"
                        hideLoading()
                    }
                }
            }
        connectViewModel.commandExecuteStateLiveData(CmdParams.CMD_SYNC_TIME)
            .observeUnSticky(viewLifecycleOwner) { syncTimeState ->
                when (syncTimeState) {
                    is ResourceState.Loading -> {
                        viewBinding.tvCurrent.text = "当前操作：同步时间..."
                    }
                    is ResourceState.Success -> {
                        viewBinding.tvCurrent.text =
                            "当前操作：" + if (syncTimeState.data as Boolean) "同步时间成功" else "同步时间失败"
                    }
                    is ResourceState.Error -> {
                        viewBinding.tvCurrent.text = "当前操作：同步时间失败"
                    }
                }
            }
        connectViewModel.commandExecuteStateLiveData(CmdParams.CMD_GET_MV)
            .observeUnSticky(viewLifecycleOwner) { mvState ->
                when (mvState) {
                    is ResourceState.Loading -> {
                        viewBinding.tvCurrent.text = "当前操作：获取电压..."
                    }
                    is ResourceState.Success -> {
                        viewBinding.tvMv.text =
                            "电压：" + mvState.data as String
                        viewBinding.tvCurrent.text = "当前操作：获取电压成功"
                    }
                    is ResourceState.Error -> {
                        viewBinding.tvCurrent.text = "当前操作：获取电压失败"
                    }
                }
            }
        connectViewModel.commandExecuteStateLiveData(CmdParams.CMD_OPEN_LOCK)
            .observeUnSticky(viewLifecycleOwner) { lockState ->
                when (lockState) {
                    is ResourceState.Loading -> {
                        viewBinding.tvCurrent.text = "当前操作：开锁..."
                    }
                    is ResourceState.Success -> {
                        viewBinding.tvCurrent.text =
                            "当前操作：开锁" + if (lockState.data as Boolean) "成功" else "失败"
                    }
                    is ResourceState.Error -> {
                        viewBinding.tvCurrent.text = "当前操作：开锁失败"
                    }
                }
            }
        connectViewModel.commandExecuteStateLiveData(CmdParams.CMD_GET_MODEL)
            .observeUnSticky(viewLifecycleOwner) { lockState ->
                when (lockState) {
                    is ResourceState.Loading -> {
                        showLoading("获取工作模式...")
                        viewBinding.tvCurrent.text = "当前操作：获取工作模式..."
                    }
                    is ResourceState.Success -> {
                        hideLoading()
                        val success = lockState.data as Boolean
                        if(success){
                            viewBinding.tvModel.text = "工作模式：${items[connectViewModel.mLockModel]}"
                        }
                        viewBinding.tvCurrent.text =
                            "当前操作：获取工作模式" + if (success) "成功" else "失败"
                    }
                    is ResourceState.Error -> {
                        hideLoading()
                        viewBinding.tvCurrent.text = "当前操作：获取工作模式失败"
                    }
                }
            }
        connectViewModel.commandExecuteStateLiveData(CmdParams.CMD_SET_MODEL)
            .observeUnSticky(viewLifecycleOwner) { lockState ->
                when (lockState) {
                    is ResourceState.Loading -> {
                        showLoading("设置工作模式...")
                        viewBinding.tvCurrent.text = "当前操作：设置工作模式..."
                    }
                    is ResourceState.Success -> {
                        hideLoading()
                        val success = lockState.data as Boolean
                        if(success){
                            viewBinding.tvModel.text = "工作模式：${items[connectViewModel.mLockModel]}"
                        }
                        viewBinding.tvCurrent.text =
                            "当前操作：设置工作模式" + if (success) "成功" else "失败"
                    }
                    is ResourceState.Error -> {
                        hideLoading()
                        viewBinding.tvCurrent.text = "当前操作：设置工作模式失败"
                    }
                }
            }
        viewBinding.btnLockOpen.setOnClickListener {
            connectViewModel.openLock()
        }
        viewBinding.btnConnect.setOnClickListener {
            if (connectViewModel.isConnected()) {
                showToast("设备已连接")
            } else {
                connectViewModel.connectDevice(bleDevice?.mac!!)
            }
        }
        viewBinding.btnLockMv.setOnClickListener { connectViewModel.getMv() }

        viewBinding.btnGetModel.setOnClickListener { connectViewModel.getLockModel() }

        viewBinding.btnSetModel.setOnClickListener {

            AlertDialog.Builder(requireContext())
                .setItems(items) { dialog, which ->
                    viewBinding.tvCurrent.text = "当前操作：设置${items[which]}..."
                    connectViewModel.setLockModel(which)
                    dialog.dismiss()
                }
                .show()
        }
    }

    fun showMessageFinish(message: String) {
        AlertDialog.Builder(requireContext())
            .setMessage(message)
            .setPositiveButton(
                "好的"
            ) { dialog, i ->
                dialog.dismiss()
                findNavController().popBackStack()
            }
            .show()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    companion object {
        private const val KEY_DEVICE_DATA = "KEY_DEVICE_DATA"

        @JvmStatic
        fun buildArguments(
            bleDevice: BluetoothDeviceWrapper,
        ): Bundle {
            val bundle = Bundle()
            bundle.putParcelable(KEY_DEVICE_DATA, bleDevice)
            return bundle
        }

    }
}