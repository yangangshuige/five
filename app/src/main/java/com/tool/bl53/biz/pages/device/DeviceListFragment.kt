package com.tool.bl53.biz.pages.device

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.showToast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.huawei.hms.ml.scan.HmsScan
import com.tool.bl53.R
import com.tool.bl53.biz.bean.ResourceState
import com.tool.bl53.biz.bean.ScanState
import com.tool.bl53.biz.viewmodel.DeviceViewModel
import com.tool.bl53.biz.viewmodel.ScanViewModel
import com.tool.bl53.databinding.FragmentDeviceListBinding
import com.tool.bl53.recyclerView.LineSeparatorDecoration

class DeviceListFragment : Fragment(R.layout.fragment_device_list) {
    private lateinit var scanResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var bleResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var locationResultLauncher: ActivityResultLauncher<String>
    private lateinit var viewBinding: FragmentDeviceListBinding
    private val scanViewModel: ScanViewModel by viewModels()
    private val deviceViewModel: DeviceViewModel by viewModels()
    private val mDeviceAdapter = DeviceAdapter()
    private val hasLocationPermission: Boolean
        get() {
            return ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        }
    private val lineSeparatorDecoration: LineSeparatorDecoration by lazy {
        LineSeparatorDecoration.Builder()
            .separatorOrientation(RecyclerView.HORIZONTAL)
            .separatorColor(R.color.plr_line_color)
            .separatorSizeInDp(1f)
            .build()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding = FragmentDeviceListBinding.bind(view)
        viewBinding.recyclerviewDeviceList.addItemDecoration(lineSeparatorDecoration)
        viewBinding.recyclerviewDeviceList.adapter = mDeviceAdapter
        mDeviceAdapter.onItemClick = { position ->
            scanViewModel.stopScan()
            val bleDevice = mDeviceAdapter.getItemData(position)
            deviceViewModel.queryDeviceInfo(bleDevice.mac, "", bleDevice.name, true)
        }
        viewBinding.toolbar.setOnMenuItemClickListener { item ->
            when (item?.itemId) {
                R.id.navigation_scan -> {
                    scanResultLauncher.launch(Intent(requireContext(), ScanActivity::class.java))
                }
            }
            false
        }
        viewBinding.swipeRefresh.setColorSchemeColors(
            ContextCompat.getColor(
                requireContext(),
                R.color.colorAccent
            )
        )
        viewBinding.swipeRefresh.setOnRefreshListener {
            val scanDeviceLiveData = scanViewModel.scanDeviceLiveData.value
            if (scanDeviceLiveData is ScanState.ScanFinish) {
                checkPermissions()
            } else {
                showToast("现在扫描中,请勿重复刷新")
                viewBinding.swipeRefresh.isRefreshing = false
            }
        }
        locationResultLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { permissionGranted ->
                run {
                    if (permissionGranted) {
                        checkBluetooth()
                    } else {
                        Toast.makeText(requireContext(), "请打开定位权限", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        bleResultLauncher =
            registerForActivityResult(object : ActivityResultContract<Intent, Boolean>() {
                override fun createIntent(context: Context, input: Intent): Intent {
                    return input
                }

                override fun parseResult(resultCode: Int, intent: Intent?): Boolean {
                    Log.d(TAG, "resultCode=====$resultCode" + "intent========$intent")
                    return scanViewModel.isBlueEnable()
                }

            }) { resultCallback ->
                if (resultCallback) {
                    scanViewModel.startScan()
                }
                Log.d(TAG, "ActivityResultCallback=====$resultCallback")
            }
        scanResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
                if (Activity.RESULT_OK == activityResult.resultCode) {
                    val hmsScan: HmsScan? =
                        activityResult.data?.getParcelableExtra(ScanActivity.SCAN_RESULT)
                    val code = hmsScan?.originalValue ?: ""
                    deviceViewModel.queryDeviceInfo("", code, "BL53", false)
                }
            }
        scanViewModel.registerCallBack()
        scanViewModel.scanDeviceLiveData.observeUnSticky(viewLifecycleOwner) { scanDeviceState ->
            run {
                when (scanDeviceState) {
                    is ScanState.ScanStart -> {
                        viewBinding.swipeRefresh.isRefreshing = true
                    }
                    is ScanState.Scanning -> {
                        mDeviceAdapter.addDevice(scanDeviceState.data!!)
                        mDeviceAdapter.notifyDataSetChanged()
                    }
                    is ScanState.ScanFinish -> {
                        viewBinding.swipeRefresh.isRefreshing = false
                    }
                }
            }
        }
        deviceViewModel.lockInfoLiveData.observeUnSticky(viewLifecycleOwner) { state ->
            when (state) {
                is ResourceState.Success -> {
                    findNavController().navigate(
                        R.id.deviceFragment,
                        DeviceFragment.buildArguments(state.data!!)
                    )

                }
                else -> {
                    showToast("获取设备信息失败")
                }
            }
        }
        checkPermissions()
    }

    private fun checkPermissions() {
        if (hasLocationPermission) {
            checkBluetooth()
        } else {
            locationResultLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun checkBluetooth() {
        if (scanViewModel.isBlueEnable()) {
            scanViewModel.startScan()
        } else {
            val intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            bleResultLauncher.launch(intent)
        }
    }

    companion object {
        private val TAG = DeviceListFragment::class.java.simpleName
    }
}