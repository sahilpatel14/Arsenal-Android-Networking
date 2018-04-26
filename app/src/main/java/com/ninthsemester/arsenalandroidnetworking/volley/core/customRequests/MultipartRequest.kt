package com.ninthsemester.arsenalandroidnetworking.volley.core.customRequests

import com.android.volley.*
import com.android.volley.toolbox.HttpHeaderParser
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.ninthsemester.arsenalandroidnetworking.models.DataPart
import org.jetbrains.anko.AnkoLogger
import org.json.JSONObject
import java.io.*
import java.nio.charset.Charset
import java.util.*

/**
 * Created by sahil-mac on 26/04/18.
 *
 * https://gist.github.com/anggadarkprince/a7c536da091f4b26bb4abf2f92926594
 */
open class MultipartRequest(
        method: Int = Request.Method.POST,
        url: String,
        private val headers: MutableMap<String, String>?,
        private val listener: Response.Listener<JSONObject>,
        errorListener: Response.ErrorListener
) : Request<JSONObject>(method, url, errorListener), AnkoLogger {


    private val twoHyphens = "--"
    private val lineEnd = "\r\n"
    private val boundary = "apiclient-" + System.currentTimeMillis()



    /**
     * returns our common header if they are set, else it calls
     * header method from parent class
     */
    override fun getHeaders(): MutableMap<String, String> = headers ?: super.getHeaders()

    override fun getBodyContentType() = "multipart/form-data;boundary=$boundary"

    override fun getBody(): ByteArray {

        val bos = ByteArrayOutputStream()
        val dos = DataOutputStream(bos)



        try {

            //  populate text payload
            if (params != null && !params.isEmpty()) {
                textParse(dos, params, paramsEncoding)
            }

            //  populate data byte payload
            if (!getByteData().isEmpty()) {
                dataParse(dos, getByteData())
            }

            // close multipart form data after text and file data
            dos.writeBytes("$twoHyphens$boundary$twoHyphens$lineEnd")
            return bos.toByteArray()
        }
        catch (e : IOException) {
            e.printStackTrace()
        }
        return ByteArray(0)
    }

    override fun parseNetworkResponse(response: NetworkResponse?): Response<JSONObject> {

        return try {
            //  Reading the response data as bytes and converting it
            //  into string.
            val json = String(
                    //  loading response data as bytes.
                    response?.data ?: ByteArray(0),
                    //  getting data about character encoding from response header.
                    Charset.forName(HttpHeaderParser.parseCharset(response?.headers)))

            Response.success(
                    JSONObject(json),
                    HttpHeaderParser.parseCacheHeaders(response))

        }
        catch (e: UnsupportedEncodingException) {

            //  In case the encoding is not supported by our system.
            Response.error<JSONObject>(ParseError(e))
        } catch (e: JsonSyntaxException) {

            //  In case the response is not present in json format
            Response.error<JSONObject>(ParseError(e))
        }
    }


    override fun deliverResponse(response: JSONObject?) {
        listener.onResponse(response)
    }


    override fun deliverError(error: VolleyError?) {
        errorListener.onErrorResponse(error)
    }


    open fun getByteData() : Map<String, DataPart> = emptyMap()

    private fun textParse(dos : DataOutputStream, params: Map<String, String>, encoding : String) =
            params.entries.forEach {
                buildTextPart(dos, it.key, it.value)
            }


    private fun dataParse(dos: DataOutputStream, params : Map<String, DataPart>) =
            params.entries.forEach {
                buildDataPart(dos, it.key, it.value)
        }


    /**
     * We are trying to create the whole multipart request on our own.
     * This step is trying to build text Part using the key:value text
     * passed in the request.
     */
    private fun buildTextPart(dos: DataOutputStream, key: String, value: String) =
            with(dos) {
                /*
                 *  --apiclient-1524735313\n\r
                 *  Content-Disposition: form-data; name="age"\n\r
                 *  \n\r
                 *  21\n\r
                 */
                writeBytes("$twoHyphens$boundary$lineEnd")
                writeBytes("Content-Disposition: form-data; name=\"$key\"$lineEnd")
                writeBytes(lineEnd)
                writeBytes("$value$lineEnd")
            }


    private fun buildDataPart(dos: DataOutputStream, inputName: String, dataFile : DataPart) =
            with(dos) {
                /*
                 *  --apiclient-1524735313\n\r
                 *  Content-Disposition: form-data; name="file"; filename="abc.png"\n\r
                 *  Content-Type: image/png\n\r
                 *  \n\r
                 */
                writeBytes("$twoHyphens$boundary$lineEnd")
                writeBytes("Content-Disposition: form-data; name=\"$inputName\"; filename=\"${dataFile.filename}\"$lineEnd")

                if (!dataFile.mimeType.trim().isEmpty()) {
                    dos.writeBytes("Content-Type: ${dataFile.mimeType}$lineEnd")
                }
                writeBytes(lineEnd)

                //  reading bytes of data representing the file
                val fis = ByteArrayInputStream(dataFile.content)
                val maxBufferSize = 1024 * 1024
                var bufferSize = Math.min(fis.available(), maxBufferSize)

                //  A temporary buffer for copying data
                val buffer = ByteArray(bufferSize)
                var bytesRead = fis.read(buffer, 0, bufferSize)

                while (bytesRead > 0) {
                    write(buffer, 0, bufferSize)
                    bufferSize = Math.min(fis.available(), maxBufferSize)
                    bytesRead = fis.read(buffer, 0, bufferSize)
                }
                writeBytes(lineEnd)
            }



}