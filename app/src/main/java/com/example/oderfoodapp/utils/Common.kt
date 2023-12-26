package com.example.oderfoodapp.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import com.example.oderfoodapp.model.User

object Common {
    @JvmField
    var currentUser: User? = null
    @JvmField
    var DELETE = "Delete"
    @JvmField
    var USER_KEY = "User"
    @JvmField
    var PASSWORD_KEY = "Password"
    const val INTENT_FOOD_ID = "FoodId"
    @JvmStatic
    fun isConnectedToInternet(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val info = connectivityManager.allNetworkInfo
        for (i in info.indices) {
            if (info[i].state == NetworkInfo.State.CONNECTED) return true
        }
        return false
    }
}