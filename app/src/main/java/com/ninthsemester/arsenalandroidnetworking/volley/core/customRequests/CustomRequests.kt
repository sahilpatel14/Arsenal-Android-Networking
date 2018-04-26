package com.ninthsemester.arsenalandroidnetworking.volley.core.customRequests

import com.android.volley.toolbox.HttpHeaderParser
import com.google.gson.*
import com.google.gson.reflect.TypeToken
import java.io.UnsupportedEncodingException
import java.nio.charset.Charset
import com.android.volley.*
import org.jetbrains.anko.AnkoLogger


/**
 * Created by sahil-mac on 12/04/18.
 */


private val gson = Gson()

/**
 * Checking if method type for HTTP method is valid.
 * it should be somewhere between [Request.Method.GET] and [Request.Method.PUT]
 */
fun isValidHttpMethodType(method: Int) = (method > -1 && method < 8)

/**
 * Creating a custom Request type for volley. This request type
 * would serialized response object into passed model type and then
 * return the model as actual response
 */
open class GsonObjectRequest<T>(
        method: Int = Request.Method.GET,
        url: String,
        private val clazz: Class<T>,
        private val headers: MutableMap<String, String>?,
        private val listener: Response.Listener<T>,
        errorListener: Response.ErrorListener
        ) : Request<T>(method, url, errorListener), AnkoLogger {

    init {
        //  If it is not a valid method type, we throw an
        //  IllegalArgumentException
        if (!isValidHttpMethodType(method)) {
            throw IllegalArgumentException("Invalid method type passed.")
        }
    }

    /**
     * returns our common header if they are set, else it calls
     * header method from parent class
     */
    override fun getHeaders(): MutableMap<String, String> = headers ?: super.getHeaders()


    /**
     * Returns response of the call from our listener.
     */
    override fun deliverResponse(response: T) = listener.onResponse(response)


    /**
     * fetches the response that is received from api call as bytes and converts it
     * into T type. All this conversion happens using Gson.
     */
    override fun parseNetworkResponse(response: NetworkResponse?): Response<T> {

        return try {

            //  Saving the response value as String. This response would
            //  most likely be of type json.
            val json = String(
                    //  Bytes are read from response. If it does not exist, we
                    //  initialize it with an empty Byte array.
                    response?.data ?: ByteArray(0),
                    //  Fetching characterSet from response header.
                    Charset.forName(HttpHeaderParser.parseCharset(response?.headers)))

            //  serializing response into model of type T and returning as success message.
            //  Also returning caching information from header.
            Response.success(gson.fromJson(json, clazz),
                    HttpHeaderParser.parseCacheHeaders(response))

        } catch (e: UnsupportedEncodingException) {

            //  In case the encoding is not supported by the system,
            //  we throw a parseException
            Response.error<T>(ParseError(e))
        } catch (e: JsonSyntaxException) {

            //  If the syntax does not conform with JSON format,
            //  we throw parseException
            Response.error<T>(ParseError(e))
        }

    }
}


/**
 * Just like JsonObjectRequest and JsonArrayRequest in volley, here we
 * define two Request types. One for Model objects and another one for
 * a list of model objects. They work fairly similarly with one major
 * difference. In here, the type of model is figured out at runtime using
 * reflection. We can not use the same methodology in [GsonObjectRequest].
 */
class GsonArrayRequest<T>(
        method: Int = Request.Method.GET,
        url: String,
        private val headers: MutableMap<String, String>?,
        private val listener: Response.Listener<List<T>>,
        errorListener: Response.ErrorListener
) : Request<List<T>>(method, url, errorListener), AnkoLogger {


    init {
        //  If it is not a valid method type, we throw an
        //  IllegalArgumentException
        if (!isValidHttpMethodType(method)) {
            throw IllegalArgumentException("Invalid method type passed.")
        }
    }

    /**
     * returns our common header if they are set, else it calls
     * header method from parent class
     */
    override fun getHeaders(): MutableMap<String, String> = headers ?: super.getHeaders()

    /**
     * Returns the final response using the listener.
     */
    override fun deliverResponse(response: List<T>?) = listener.onResponse(response)

    /**
     * fetches the response that is received from api call as bytes and converts it
     * into list of type T . All this conversion happens using Gson.
     */
    override fun parseNetworkResponse(response: NetworkResponse?): Response<List<T>> {

        return try {

            //  Reading the response data as bytes and converting it
            //  into string.
            val json = String(
                    //  loading response data as bytes.
                    response?.data ?: ByteArray(0),
                    //  getting data about character encoding from response header.
                    Charset.forName(HttpHeaderParser.parseCharset(response?.headers)))

            //  Here we are figuring out the type of models using reflection
            val listType = object : TypeToken<List<T>>() {}.type

            //  Serializing the whole list of response into list of model objects
            val outList: List<T> = gson.fromJson(json, listType)

            //  Returning the response as List of model objects.
            Response.success(outList, HttpHeaderParser.parseCacheHeaders(response))


        } catch (e: UnsupportedEncodingException) {

            //  In case the encoding is not supported by our system.
            Response.error<List<T>>(ParseError(e))
        } catch (e: JsonSyntaxException) {

            //  In case the response is not present in json format
            Response.error<List<T>>(ParseError(e))
        }

    }


}