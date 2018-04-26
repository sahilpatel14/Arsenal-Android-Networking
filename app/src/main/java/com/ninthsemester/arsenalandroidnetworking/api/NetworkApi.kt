package com.ninthsemester.arsenalandroidnetworking.api

import com.google.gson.JsonObject
import com.ninthsemester.arsenalandroidnetworking.models.ApiError
import com.ninthsemester.arsenalandroidnetworking.models.DataPart
import com.ninthsemester.arsenalandroidnetworking.models.User
import org.json.JSONObject

/**
 * Created by sahil-mac on 12/04/18.
 */


/**
 * The interface will be used to make networking calls to the API.
 * This interface will be implemented by Http Clients like Volley or Retrofit
 *
 * The client, requesting this data would simply plug in the required api HttpClient
 * using dependency Injection preferably and make the calls.
 */
interface NetworkApi {

    //  Returns a particular user having the provided user id.
    fun getUser(userId : Int, responseListener : (User) -> Unit, errorListener : (ApiError)-> Unit)

    //  Returns a list of all users.
    fun getAllUsers(responseListener: (List<User>) -> Unit, errorListener: (ApiError) -> Unit)

    //  Makes a call to home page. Useful while testing the api
    fun home(responseListener: (JsonObject) -> Unit, errorListener: (ApiError) -> Unit)

    //  Registers a new user
    fun registerUser(user : User, responseListener: (JSONObject) -> Unit, errorListener: (ApiError) -> Unit)

    //  Uploads file (image) to server
    fun uploadFile(dataPart: DataPart, content_id : Int, responseListener: (JSONObject) -> Unit, errorListener: (ApiError) -> Unit)

    //  login a user
    fun login(email : String, password : String, responseListener: (JSONObject) -> Unit, errorListener: (ApiError) -> Unit)

    //  edits details of a user
    fun editUser(user: User, responseListener: (JSONObject) -> Unit, errorListener: (ApiError) -> Unit)
}