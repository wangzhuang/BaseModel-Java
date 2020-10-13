package com.king.basemodel.common

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.king.basemodel.BuildConfig
import com.orhanobut.logger.Logger
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat


const val REQ_CODE_UPLOAD_HEAD_IMAGE = 666


var appCtx: Context? = null


val sdf = SimpleDateFormat("yyyy-MM-dd")
val sdf1 = SimpleDateFormat("yyyy年MM月dd日")
val sdf2 = SimpleDateFormat("yyyy/MM/dd")
val sdf3 = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
val sdf4 = SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
val sdf5 = SimpleDateFormat("yyyy年MM月dd日 HH:mm")
val sdf6 = SimpleDateFormat("yyyy-MM-dd HH:mm")
//val sdf5 = SimpleDateFormat("MM月dd日")


//json start
inline fun <reified T> parseJsonToBean(jsonStr: String?): T? {
    if (jsonStr.isNullOrEmpty()) {
        return null
    }
    return Gson().fromJson(jsonStr, T::class.java)
}

inline fun <reified T> parseJsonArrayToList(jsonArray: String?): T = Gson().fromJson(jsonArray, object : TypeToken<T>() {}.type)

inline fun <reified T> parseBeanToJson(t: T): JSONObject = JSONObject(Gson().toJson(t))

inline fun <reified T> parseListToJsonArray(t: T): JSONArray = JSONArray(Gson().toJson(t, T::class.java).toString())

//json end

//log start
inline fun log(tag: String, contentStr: Any?) {
    if (!BuildConfig.DEBUG) {
        return
    }
    Logger.e("{$tag} " + contentStr.toString())
}

inline fun logJson(tag: String, contentStr: String?) {
    if (!BuildConfig.DEBUG) {
        return
    }
    Logger.e("{$tag} json:$contentStr")
}
