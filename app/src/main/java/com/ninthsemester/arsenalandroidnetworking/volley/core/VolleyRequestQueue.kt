package com.ninthsemester.arsenalandroidnetworking.volley.core

import android.content.Context
import android.graphics.Bitmap
import android.util.LruCache
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.ImageLoader
import com.android.volley.toolbox.Volley

/**
 * Created by sahil-mac on 12/04/18.
 */


/**
 * Volley requires a Request Queue which appends Requests and performs
 * them one at a time. We can go with one Request Queue for the whole app.
 *
 * This is a Singleton object for the Request Queue. We can add Requests to
 * the queue, load images in a similar format, or remove requests before they
 * are processed. This might happen when user leaves the screen before the
 * request is processed.
 */
class VolleyRequestQueue private constructor(context: Context) {

    /**
     * Here we are creating a new requestQueue using the application context.
     * requestQueue is lazy initialized which means it will be initialized right
     * before it's first usage.
     */
    val requestQueue: RequestQueue by lazy {
        Volley.newRequestQueue(context.applicationContext)
    }


    /**
     * ImageLoader will be used to download images.
     */
    val imageLoader: ImageLoader by lazy {
        ImageLoader(
                requestQueue,
                object : ImageLoader.ImageCache {

                    /**
                     * We are setting up our own caching mechanism here. When
                     * we receive a bitmap, we put it into the cache. and while requesting
                     * the bitmap we first check in bitmap, if there is a match we return it.
                     *
                     */
                    private val cache = LruCache<String, Bitmap>(20)

                    override fun getBitmap(url: String?): Bitmap = cache.get(url)

                    override fun putBitmap(url: String?, bitmap: Bitmap?) {
                        cache.put(url, bitmap)
                    }
                }
        )
    }


    /**
     * Adds a Volley request to the request queue
     */
    fun <T> addToRequestQueue(request: Request<T>) {
        requestQueue.add(request)
    }

    companion object {
        @Volatile
        private var INSTANCE: VolleyRequestQueue? = null

        fun getInstance(context: Context) = INSTANCE
                ?: synchronized(this) {
            INSTANCE
                    ?: VolleyRequestQueue(context)
        }
    }
}