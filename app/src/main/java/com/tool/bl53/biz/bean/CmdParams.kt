package com.tool.bl53.biz.bean


data class CmdParams(
    val command: String,
    val params: ByteArray? = null
){
    companion object{
        const val CMD_GET_TOKEN = "获取token"
        const val CMD_SYNC_TIME = "同步时间"
        const val CMD_GET_MV = "获取电压"
        const val CMD_OPEN_LOCK = "开锁"
        const val CMD_CLOSE_LOCK = "关锁"
        const val CMD_GET_MODEL = "获取工作模式"
        const val CMD_SET_MODEL = "设置工作模式"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CmdParams

        if (command != other.command) return false
        if (params != null) {
            if (other.params == null) return false
            if (!params.contentEquals(other.params)) return false
        } else if (other.params != null) return false

        return true
    }

    override fun hashCode(): Int {
        var result = command.hashCode()
        result = 31 * result + (params?.contentHashCode() ?: 0)
        return result
    }
}