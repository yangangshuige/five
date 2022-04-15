package com.tool.bl53.biz.pages.device

import android.bluetooth.BluetoothDevice
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tool.bl53.R
import com.tool.bl53.biz.bean.BluetoothDeviceWrapper
import com.tool.bl53.databinding.AdapterDeviceBinding
import com.tool.bl53.recyclerView.AutoInflateViewHolder
import com.tool.bl53.recyclerView.activityHost
import com.tool.bl53.utils.NavigationConfig
import java.util.concurrent.CopyOnWriteArrayList

class DeviceAdapter() :
    RecyclerView.Adapter<DeviceAdapter.DeviceViewHolder>() {
    private val deviceList = CopyOnWriteArrayList<BluetoothDeviceWrapper>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceViewHolder {
        return DeviceViewHolder(parent)
    }

    override fun onBindViewHolder(holder: DeviceViewHolder, position: Int) {
        holder.bindData(deviceList[position])
    }


    fun addDevice(bleDevice: BluetoothDeviceWrapper) {
        removeDevice(bleDevice)
        deviceList.add(bleDevice)
        deviceList.sort()
        notifyDataSetChanged()
    }

    fun removeDevice(bleDevice: BluetoothDeviceWrapper) {
        for (device in deviceList) {
            if (bleDevice.mac == device.mac) {
                deviceList.remove(device)
            }
        }
    }

    fun clear() {
        deviceList.clear()
        notifyDataSetChanged()
    }

    fun submit(devices: List<BluetoothDeviceWrapper>) {
        deviceList.clear()
        deviceList.addAll(devices)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = deviceList.size
    class DeviceViewHolder(
        parent: ViewGroup
    ) :
        AutoInflateViewHolder(parent, R.layout.adapter_device) {
        private val viewBinding = AdapterDeviceBinding.bind(itemView)
        fun bindData(bleDevice: BluetoothDeviceWrapper) {
            viewBinding.nameLabel.text = "Name:${bleDevice.name}"
            viewBinding.macLabel.text = "MAC:${bleDevice.mac}"
            viewBinding.rssiLabel.text = "RSSI:${bleDevice.rssi}"
            viewBinding.root.setOnClickListener {
                NavigationConfig.findNavController(activityHost)
                    ?.navigate(R.id.deviceFragment, DeviceFragment.buildArguments(bleDevice))
            }
        }
    }

}