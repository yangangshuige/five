package com.tool.bl53

import android.util.Base64
import com.tool.bl53.utils.DataUtil
import com.tool.bl53.utils.Utils
import org.junit.Assert.assertEquals
import org.junit.Test
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun myTest() {
        println(priceDes(76500))
    }

    fun is16YearOld(birth: String): Boolean {
        var timestamp = SimpleDateFormat("yyyy-MM-dd").parse(birth).time
        var calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, calendar[Calendar.YEAR] - 16)
        return calendar.timeInMillis > timestamp
    }

    fun durationDes(duration: Int): String {
        if (duration == 180) return "半年"
        val year = duration / 360
        val month = (duration - year * 360) / 30
        val day = (duration - year * 360 - month * 30)
        var des = ""
        if (year > 0)
            des = "$year" + "年"
        if (month > 0)
            des = des + "$month" + "个月"
        if (day > 0)
            des = des + "$day" + "天"
        return des
    }

    fun priceDes(price: Int): String {

        return price.div(100f).toString()
    }

    @Test
    fun myTest2() {
        val length = 2
        val data = byteArrayOf(11, 12, 13, 14, 15, 16)
        val fileSize = data.size
        val writeDate = ByteArray(fileSize - length)
        System.arraycopy(data, length, writeDate, 0, data.size - length)
        for (i in writeDate.indices) {
            println("writeDate========" + writeDate[i])
        }
    }

    @Test
    fun myTest3() {
        val boolean = AtomicBoolean(true)
        boolean.compareAndSet(false, true)
        println("compareAndSet========" + boolean.compareAndSet(false, true))
        println("boolean========" + boolean.get())
    }

    @Test
    fun myTest4() {
        val password = "D56E44"
        val data = Base64.encodeToString(
            password.toByteArray(),
            Base64.NO_WRAP
        )
        val byte = Base64.decode(data, Base64.DEFAULT)
        println("data================$data")
        println("byte================${byte.size}")
    }

    @Test
    fun myTest5() {
//        00 80
//        08
//        B1 A1 92 83
//        01
//        00 21
//        01
//        00 00
//        00 00 00
        val command = byteArrayOf(
            0x00, 0x80.toByte(), 0x08, 0xB1.toByte(),
            0xA1.toByte(), 0x92.toByte(),
            0x83.toByte(), 0x01, 0x00, 0x21, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00
        )
        val a: Int = command[8].toInt()
        val b: Int = command[9].toInt()
        val d: Int = command[11].toInt()
        val e: Int = command[12].toInt()

        val c: Int = command[10].toInt()
        println("version================$a.$b$d$e")
        println("state================$c")
    }

    @Test
    fun myTest6() {
        val command: String? = null
        println("item==========${command?.isNotEmpty()}")
    }

    @Test
    fun myTest7() {
        val userName = "11090014"

        val passwordMd5: String? = calcMd5("11090014Hc")
        val s =
            "grant_type=password&username=$userName&password=$passwordMd5&scope=LockDemo&did=Xtooltech&pid=x6X18OaZZ2VLPzaM2bYj&client_id=x6X18OaZZ2VLPzaM2bYj"
        val secret = "WBgd6nQRx32dJ9p3q2Gl"
        val strSecret = "$s&secret=$secret"
        val strMd5: String? = calcMd5(strSecret)
        val strSign = "$s&sign=$strMd5"
        val finalUrl = "http://keystone.xtooltech.com/token?$strSign"
        println("finalUrl================$finalUrl")
    }

    fun calcMd5(str: String?): String? {
        var ret = ""
        if (str != null && !str.isEmpty()) {
            try {
                val md5 = MessageDigest.getInstance("MD5")
                val bytes = md5.digest(str.toByteArray())
                val result = StringBuilder()
                for (b in bytes) {
                    result.append(String.format("%02X", b))
                }
                ret = result.toString()
            } catch (e: NoSuchAlgorithmException) {
                e.printStackTrace()
            }
        }
        return ret
    }

    private fun byteArrayToInt(byteArray: ByteArray): Int {
        var value = 0
        for (index in byteArray.indices) {
            val shl = (byteArray.size - index - 1).times(8)
            val or = byteArray[index].toInt() and (0xff)
            value = value or (or shl shl)
        }
        return value
    }
}