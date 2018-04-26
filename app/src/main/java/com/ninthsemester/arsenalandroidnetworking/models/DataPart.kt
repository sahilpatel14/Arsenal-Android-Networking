package com.ninthsemester.arsenalandroidnetworking.models

import java.util.*

/**
 * Created by sahil-mac on 26/04/18.
 */
data class DataPart(val filename: String,
                    val content: ByteArray,
                    val mimeType : String) {


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DataPart

        if (filename != other.filename) return false
        if (!Arrays.equals(content, other.content)) return false
        if (mimeType != other.mimeType) return false

        return true
    }

    override fun hashCode(): Int {
        var result = filename.hashCode()
        result = 31 * result + Arrays.hashCode(content)
        result = 31 * result + mimeType.hashCode()
        return result
    }


}