package com.ninthsemester.arsenalandroidnetworking.volley

import com.android.volley.*
import com.android.volley.toolbox.JsonObjectRequest
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.ninthsemester.arsenalandroidnetworking.api.ConnectionDetector
import com.ninthsemester.arsenalandroidnetworking.api.NetworkApi
import com.ninthsemester.arsenalandroidnetworking.models.ApiError
import com.ninthsemester.arsenalandroidnetworking.models.DataPart
import com.ninthsemester.arsenalandroidnetworking.models.User
import com.ninthsemester.arsenalandroidnetworking.volley.core.BaseVolleyNetworkApi
import com.ninthsemester.arsenalandroidnetworking.volley.core.customRequests.GsonArrayRequest
import com.ninthsemester.arsenalandroidnetworking.volley.core.customRequests.GsonObjectRequest
import com.ninthsemester.arsenalandroidnetworking.volley.core.VolleyRequestQueue
import com.ninthsemester.arsenalandroidnetworking.volley.core.customRequests.MultipartRequest
import org.jetbrains.anko.info
import org.json.JSONObject

/**
 * Created by sahil-mac on 12/04/18.
 */
class VolleyNetworkApi(
        private val requestQueue: VolleyRequestQueue,
        private val baseUrl: String,
        private val headers: Map<String, String> = hashMapOf(),
        connectionDetector: ConnectionDetector? = null) : BaseVolleyNetworkApi(connectionDetector), NetworkApi{


    override fun registerUser(user: User, responseListener: (JSONObject) -> Unit, errorListener: (ApiError) -> Unit) {

        val url = "$baseUrl/users/new"

        val json = JSONObject(Gson().toJson(user))

        val userRequest = JsonObjectRequest(Request.Method.POST, url, json,
                Response.Listener(responseListener),
                Response.ErrorListener(handleErrorResponse(errorListener)))

        info { json }
        requestQueue.addToRequestQueue(userRequest)
    }

    override fun getUser(userId: Int, responseListener: (User) -> Unit, errorListener: (ApiError) -> Unit) {

        val url = "$baseUrl/users/$userId"

        val userRequest = GsonObjectRequest(Request.Method.GET, url, User::class.java, headers.toMutableMap(),
                Response.Listener(responseListener),
                Response.ErrorListener(handleErrorResponse(errorListener))
        )

        requestQueue.addToRequestQueue(userRequest)
    }

    override fun getAllUsers(responseListener: (List<User>) -> Unit, errorListener: (ApiError) -> Unit) {

        val url = "$baseUrl/users"

        val userArrayRequest = GsonArrayRequest(Request.Method.GET, url, headers.toMutableMap(),
                Response.Listener(responseListener),
                Response.ErrorListener(handleErrorResponse(errorListener))
        )

        requestQueue.addToRequestQueue(userArrayRequest)
    }

    override fun home(responseListener: (JsonObject) -> Unit, errorListener: (ApiError) -> Unit) {}

    override fun uploadFile(dataPart: DataPart, content_id : Int, responseListener: (JSONObject) -> Unit, errorListener: (ApiError) -> Unit) {

        val url = "$baseUrl/uploads"

        val request = object : MultipartRequest(Request.Method.PUT, url, headers.toMutableMap(),
                Response.Listener { info { it } },
                Response.ErrorListener(handleErrorResponse(errorListener))
        ) {
            override fun getParams(): MutableMap<String, String> = hashMapOf(Pair("content_id", content_id.toString()))
            override fun getByteData(): Map<String, DataPart> = hashMapOf(Pair("file", dataPart))
        }


        requestQueue.addToRequestQueue(request)
    }

    override fun login(email: String, password: String, responseListener: (JSONObject) -> Unit, errorListener: (ApiError) -> Unit) {

        val url = "$baseUrl/login"

        val json = JSONObject(Gson().toJson(hashMapOf(Pair("email", email), Pair("password", password))))

        val request = object : JsonObjectRequest(Request.Method.POST, url, json,
                Response.Listener {
                    info { it }
                },
                Response.ErrorListener (handleErrorResponse(errorListener))) {

            override fun getHeaders(): MutableMap<String, String> {
                return this@VolleyNetworkApi.headers.toMutableMap()
            }

        }
        requestQueue.addToRequestQueue(request)
    }

    override fun editUser(user: User, responseListener: (JSONObject) -> Unit, errorListener: (ApiError) -> Unit) {

    }
}