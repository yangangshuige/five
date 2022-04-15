package com.tool.bl53.biz.bean

import android.os.Parcel
import android.os.Parcelable


class BluetoothDeviceWrapper() : Comparable<BluetoothDeviceWrapper>,Parcelable {
    var name: String? = null
    var mac: String? = null
    var rssi: Int = 0
    var id: Int = 0

    constructor(parcel: Parcel) : this() {
        name = parcel.readString()
        mac = parcel.readString()
        rssi = parcel.readInt()
        id = parcel.readInt()
    }

    override fun compareTo(other: BluetoothDeviceWrapper): Int {
        return other.rssi - this.rssi
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(mac)
        parcel.writeInt(rssi)
        parcel.writeInt(id)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<BluetoothDeviceWrapper> {
        override fun createFromParcel(parcel: Parcel): BluetoothDeviceWrapper {
            return BluetoothDeviceWrapper(parcel)
        }

        override fun newArray(size: Int): Array<BluetoothDeviceWrapper?> {
            return arrayOfNulls(size)
        }
    }

}