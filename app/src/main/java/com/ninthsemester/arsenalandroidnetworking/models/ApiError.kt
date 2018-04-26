package com.ninthsemester.arsenalandroidnetworking.models

/**
 * Created by sahil-mac on 12/04/18.
 */
data class ApiError(
        val errorCode : Int,
        val reason : String,
        val exception : Exception ?= null)