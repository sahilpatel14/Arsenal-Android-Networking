package com.ninthsemester.arsenalandroidnetworking.models

/**
 * Created by sahil-mac on 12/04/18.
 */
data class User(val id : Int? = null,
                val name : String,
                val email : String,
                val password : String,
                val profile_pic : String?= null,
                val dob : Map<String, String>)