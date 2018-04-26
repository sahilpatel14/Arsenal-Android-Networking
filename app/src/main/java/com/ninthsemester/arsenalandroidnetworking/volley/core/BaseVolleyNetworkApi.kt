package com.ninthsemester.arsenalandroidnetworking.volley.core

import com.android.volley.ClientError
import com.android.volley.VolleyError
import com.android.volley.toolbox.HttpHeaderParser
import com.ninthsemester.arsenalandroidnetworking.api.ConnectionDetector
import com.ninthsemester.arsenalandroidnetworking.api.NetworkConstants
import com.ninthsemester.arsenalandroidnetworking.models.ApiError
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.error
import org.jetbrains.anko.info
import org.json.JSONException
import org.json.JSONObject
import java.net.ConnectException
import java.nio.charset.Charset

import com.android.volley.Request.Method.GET
import com.android.volley.Request.Method.POST
import com.android.volley.Request.Method.PUT
import com.android.volley.Request.Method.DELETE


/**
 * Created by sahil-mac on 26/04/18.
 */
open class BaseVolleyNetworkApi(
        private val connectionDetector: ConnectionDetector? = null) : AnkoLogger {

    protected fun handleErrorResponse(errorListener: (ApiError) -> Unit): (VolleyError) -> Unit = {


        fun toReturn() : (VolleyError) -> Unit = {
            info { "yolo" }
        }

        val isConnected = connectionDetector?.isConnectedToInternet() ?: true
        if (!isConnected) {
            ApiError(NetworkConstants.CODE_NOT_CONNECTED, "Device is not connected to internet")
                    .apply {
                        errorListener.invoke(this)
                        error { this }
                    }
        }
        else {


            /**
             * We were able to contact the server and got some response. Now
             * we will try to read error messages if any from response based on
             * different response code types.
             */
            it.networkResponse?.let {

                /*
                 * Checks if the response has error object in it. If it is present, the method
                 * converts it into ApiError object and returns. If it is not present, then the
                 * method returns a generic type of ApiError object.
                 */

                info("Our request was not processed. However, we do have some error data from server.")


                fun readErrorObjectFromResponse(data: ByteArray?): ApiError = try {

                    val errorJsonObject = JSONObject(String(data ?: ByteArray(0),
                            Charset.forName(HttpHeaderParser.parseCharset(it.headers))))

                    val errorMessage = errorJsonObject.getString(NetworkConstants.API_KEY_ERROR)
                    val errorCode = NetworkConstants.CODE_VALIDATION_ERROR

                    ApiError(errorCode, errorMessage).also { error(it) }
                } catch (e: JSONException) {
                    val message = "Failed to serialize response into valid Json"
                    ApiError(NetworkConstants.CODE_INVALID_RESPONSE_ERROR, message, e).also {
                        error(it)
                    }
                }

                /*  todo Fix this when statement
                 *  I will be adding better a error handling mechanism soon.
                 */
                when (it.statusCode) {
                    422 -> {
                        errorListener.invoke(readErrorObjectFromResponse(it.data))
                    }
                    else -> {
                        errorListener.invoke(readErrorObjectFromResponse(it.data))
                    }
                }
            }

            if (it.networkResponse == null) {


                /**
                 * At this point we can assume that server could not be contacted. This could
                 * be due to host not being available or the phone is not connected to internet.
                 * AnyHow, network Response is null. We can only return the exception type to
                 * best of our parent cause type.
                 */

                when (getCause(it)) {
                    is ConnectException -> {
                        val errorMessage = "Failed to connect with host. Make sure the server is up and running."
                        with(ApiError(NetworkConstants.CODE_CONNECTION_ERROR, errorMessage, it)) {
                            errorListener.invoke(this)
                            error(this)
                        }
                    }
                    is ClientError -> {
                        val errorMessage = "Looks like we are not connected to internet"
                        ApiError(NetworkConstants.CODE_CONNECTION_ERROR, errorMessage, it).apply {
                            errorListener.invoke(this)
                            error(this)
                        }
                    }
                    else -> {
                        ApiError(NetworkConstants.CODE_UNKNOWN_ERROR, it.localizedMessage, it).apply {
                            errorListener.invoke(this)
                            error(this)
                        }
                    }
                }

            }

        }
    }

    protected fun <T>handleResponse() : (T) -> Unit = {
        info { it.toString() }
    }

    protected fun getCause(e: Throwable): Throwable {
        var cause: Throwable?
        var result : Throwable = e

        do {
            cause = result.cause
            result = cause ?: result
        }while (cause != null && result != cause)
        return result
    }
}