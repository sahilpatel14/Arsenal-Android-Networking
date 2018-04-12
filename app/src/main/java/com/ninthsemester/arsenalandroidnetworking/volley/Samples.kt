package com.ninthsemester.arsenalandroidnetworking.volley

import android.content.Context
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.error
import org.jetbrains.anko.info
import org.json.JSONObject

/**
 * Created by sahil-mac on 11/04/18.
 */
object Samples : AnkoLogger{



    fun simpleRequest(context: Context, response: Response.Listener<String> ,tag : String? = null) {

        val url = "http://www.google.com"

        val requestQueue = Volley.newRequestQueue(context.applicationContext)
        val request = StringRequest(Request.Method.GET, url, response,
                Response.ErrorListener { error { "Did not work ${it.localizedMessage}" } })
        request.tag = tag
        requestQueue.add(request)
    }

    fun cancelRequest(context: Context, tag: String) {

        val requestQueue = Volley.newRequestQueue(context)
        requestQueue.cancelAll(tag)
    }

    fun setupRequestQueue(context: Context) {

        /*
         * We need a cache and a transport network to create a request cache.
         *
         * Once the request queue is created, we need to call start() on it.
         *
         * Adding request object to requestQueue would make the call (provided start() has been called
         * on requestQueue). Once we receive a response or error, we should call stop() on request queue.
         */

        val requestCache = DiskBasedCache(context.cacheDir, 1024 * 1024/* 1 MB */)
        val network = BasicNetwork(HurlStack())

        val requestQueue = RequestQueue(requestCache, network)
        requestQueue.start()

        val url = "http://www.google.com"

        requestQueue.add(StringRequest(Request.Method.GET, url, Response.Listener<String> {

            info { it.substring(0, 500) }
            requestQueue.stop()

        }, Response.ErrorListener {
            error { it.localizedMessage }
            requestQueue.stop()
        }))

    }

    fun makeJsonRequest(context: Context) {


        val url = "https://swapi.co/api/people/1/"

        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, url, null,
                Response.Listener<JSONObject> {

                    info { it.toString() }

                },
                Response.ErrorListener {  }
        )
        SingletonRequestQueue.getInstance(context).addToRequestQueue(jsonObjectRequest)
    }

    fun makeCustomRequest(context: Context) {

        val url = "https://swapi.co/api/people/1/"

        val customObjectRequest = GsonRequest(url, User::class.java, null,
                Response.Listener {
                    info { it }
                },
                Response.ErrorListener {
                    error { it }
                }
        )

        SingletonRequestQueue.getInstance(context).addToRequestQueue(customObjectRequest)
    }

}


data class User (val name : String,
                 val height : String,
                 val films : List<String>
)