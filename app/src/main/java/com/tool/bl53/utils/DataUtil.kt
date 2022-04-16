package com.tool.bl53.utils

import android.util.Base64
import android.util.Log
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

object DataUtil {
    private val TAG = DataUtil::class.java.simpleName
    var defaultAesKey = byteArrayOf(
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
    var defaultPassword = "D56E44"
    fun byteToString(data: ByteArray): String {
        val ret = StringBuilder()
        for (datum in data) {
            ret.append(String.format("%02X ", datum))
        }
        return ret.toString()
    }

    fun getPassword(): ByteArray {
        val data = Base64.encodeToString(
            defaultPassword.toByteArray(),
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
            val shl = (byteArray.size - index - 1).times(8)
            val or = byteArray[index].toInt() and (0xff)
            value = value or (or shl shl)
        }
        return value
    }

    fun toMac(str: String): String {
        val sb = StringBuilder()
        for (i in 0..5) {
            sb.append(str.substring(i * 2, i * 2 + 2))
            if (i < 5) {
                sb.append(":")
            }
        }
        return sb.toString()
    }

    /**
     * 16进制字符串转换成byte数组
     */
    fun hexToBytes(hexString: String): ByteArray {
        val byteArray = hexString.toByteArray()
        val byteArraySize = byteArray.size
        val hexByteArray = ByteArray(byteArraySize / 2)
        var i = 0
        while (i < byteArraySize) {
            val strTmp = String(byteArray, i, 2)
            hexByteArray[i / 2] = strTmp.toInt(16).toByte()
            i += 2
        }
        return hexByteArray
    }

    /**
     * aes加密
     */
    fun encryptAes128(sSrc: ByteArray): ByteArray? {
        return try {
            val sks = SecretKeySpec(defaultAesKey, "AES")
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
            val sks = SecretKeySpec(defaultAesKey, "AES")
            val cipher = Cipher.getInstance("AES/ECB/NoPadding")
            cipher.init(Cipher.DECRYPT_MODE, sks)
            cipher.doFinal(sSrc)
        } catch (ex: java.lang.Exception) {
            Log.e(TAG, "======" + ex.message)
            null
        }
    }
}