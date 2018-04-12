package com.ninthsemester.arsenalandroidnetworking.volley

import android.content.Context
import android.graphics.Bitmap
import android.util.LruCache
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.ImageLoader
import com.android.volley.toolbox.Volley
import java.util.*

/**
 * Created by sahil-mac on 11/04/18.
 */
class SingletonRequestQueue private constructor(context: Context) {

    val requestQueue : RequestQueue by lazy {
        Volley.newRequestQueue(context.applicationContext)
    }

    companion object {

        /**
         * Double checked locking in action
         */
        @Volatile
        private var INSTANCE : SingletonRequestQueue? = null
        fun getInstance(context: Context) = INSTANCE ?: synchronized(this) {
            INSTANCE ?: SingletonRequestQueue(context)
        }
    }

    fun <T> addToRequestQueue(req : Request<T>) {
        requestQueue.add(req)
    }

    val imageLoader : ImageLoader by lazy {
        ImageLoader(requestQueue,
                object : ImageLoader.ImageCache {

                    private val cache = LruCache<String, Bitmap>(20)

                    override fun getBitmap(url: String?): Bitmap {
                        return cache.get(url)
                    }

                    override fun putBitmap(url: String?, bitmap: Bitmap?) {
                        cache.put(url, bitmap)
                    }
                }
                )
    }
}