package com.tool.bl53.utils

import android.util.Base64
import android.util.Log
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

object DataUtil {
    private val TAG = DataUtil::class.java.simpleName
    private val DEFAULT_KEY_2 = byteArrayOf(
        0xF9.toByte(),
        0xB6.toByte(),
        0xE1.toByte(),
        0x43,
        0x30,
        0x13,
        0xBD.toByte(),
        0x5F,
        0xBB.toByte(),
        0xD2.toByte(),
        0x56,
        0x3B,
        0xAA.toByte(),
        0x43,
        0xF5.toByte(),
        0x40
    )
    private val DEFAULT_KEY_1 = byteArrayOf(
        0x20,
        0x57,
        0x2F,
        0x52,
        0x36,
        0x4B,
        0x3F,
        0x47,
        0x30,
        0x50,
        0x41,
        0x58,
        0x11,
        0x63,
        0x2D,
        0x2B
    )

    fun byteToString(data: ByteArray): String {
        val ret = StringBuilder()
        for (datum in data) {
            ret.append(String.format("%02X ", datum))
        }
        return ret.toString()
    }

    fun getPassword(password: String): ByteArray {
        val data = Base64.encodeToString(
            password.toByteArray(),
            Base64.NO_WRAP
        )
        return Base64.decode(data, Base64.DEFAULT)
    }

    fun getDeviceInfoFromScanRecord(byteArray: ByteArray): List<Int> {
        val index = byteArray.indexOfFirst {
            it == 0xFF.toByte()
        }
        val list = arrayListOf<Int>()
        val id = byteArray.copyOfRange(index + 1, index + 3)
        list.add(byteArrayToInt(id))
        return list
    }

    fun byteArrayToInt(byteArray: ByteArray): Int {
        var value = 0
        for (index in byteArray.indices) {
            val shl = (byteArray.size - index-1).times(8)
            val or = byteArray[index].toInt() and (0xff)
            value = value or (or shl shl)
        }
        return value
    }

    /**
     * aes加密
     */
    fun encryptAes128(sSrc: ByteArray): ByteArray? {
        return try {
            val sks = SecretKeySpec(DEFAULT_KEY_2, "AES")
            val cipher = Cipher.getInstance("AES/ECB/NoPadding")
            cipher.init(Cipher.ENCRYPT_MODE, sks)
            cipher.doFinal(sSrc)
        } catch (ex: Exception) {
            Log.e(TAG, "======" + ex.message)
            null
        }
    }

    /**
     * aes解密
     */
    fun decryptAes128(sSrc: ByteArray): ByteArray? {
        return try {
            val sks = SecretKeySpec(DEFAULT_KEY_2, "AES")
            val cipher = Cipher.getInstance("AES/ECB/NoPadding")
            cipher.init(Cipher.DECRYPT_MODE, sks)
            cipher.doFinal(sSrc)
        } catch (ex: java.lang.Exception) {
            Log.e(TAG, "======" + ex.message)
            null
        }
    }
}