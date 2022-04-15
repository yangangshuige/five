package com.tool.bl53.utils

import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.io.Reader
import java.lang.reflect.Type

object GsonUtils {

    private val mGson = GsonBuilder().serializeNulls().disableHtmlEscaping().create()

    fun toJson(any: Any?): String? {
        return mGson.toJson(any)
    }

    fun toJson(src: Any?, typeOfSrc: Type): String? {
        return mGson.toJson(src, typeOfSrc)
    }

    fun <T> fromJson(json: String?, type: Class<T>): T {
        return mGson.fromJson(json, type)
    }

    fun <T> fromJson(json: String?, type: Type): T {
        return mGson.fromJson(json, type)
    }

    fun <T> fromJson(reader: Reader, type: Class<T>): T {
        return mGson.fromJson(reader, type)
    }

    fun <T> fromJson(reader: Reader, type: Type): T {
        return mGson.fromJson(reader, type)
    }


    fun getListType(type: Type): Type? {
        return TypeToken.getParameterized(MutableList::class.java, type).type
    }

    fun getSetType(type: Type): Type? {
        return TypeToken.getParameterized(MutableSet::class.java, type).type
    }

    fun getMapType(keyType: Type, valueType: Type): Type? {
        return TypeToken.getParameterized(MutableMap::class.java, keyType, valueType).type
    }

    fun getArrayType(type: Type): Type? {
        return TypeToken.getArray(type).type
    }

    fun getType(rawType: Type, vararg typeArguments: Type): Type? {
        return TypeToken.getParameterized(rawType, *typeArguments).type
    }

}