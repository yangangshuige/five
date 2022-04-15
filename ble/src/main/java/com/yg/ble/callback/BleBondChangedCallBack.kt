package com.yg.ble.callback

interface BleBondChangedCallBack {
    fun bonded()
    fun bonding()
    fun unBond()
    fun error()
}