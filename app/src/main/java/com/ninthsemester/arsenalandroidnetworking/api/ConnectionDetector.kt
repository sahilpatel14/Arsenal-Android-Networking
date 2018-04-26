package com.ninthsemester.arsenalandroidnetworking.api

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Build

/**
 * Created by sahil-mac on 26/04/18.
 */
class ConnectionDetector private constructor(context: Context){

    /**
     * if user has passed application Context, we use it to get system service,
     * else we get applicationContext from it and use that instead.
     */
    private val connectivityManager = if (context.applicationContext == null)
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    else
        context.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager


    /**
     * Checks if device is connected to internet or not.
     * Returns true if connected, false otherwise.
     */
    fun isConnectedToInternet (): Boolean =
            connectivityManager.activeNetworkInfo?.isConnectedOrConnecting ?: false


    companion object {

        @Volatile
        private var INSTANCE : ConnectionDetector? = null

        fun getInstance(context: Context) = INSTANCE ?: synchronized(this) {
            INSTANCE ?: ConnectionDetector(context)
        }
    }
}