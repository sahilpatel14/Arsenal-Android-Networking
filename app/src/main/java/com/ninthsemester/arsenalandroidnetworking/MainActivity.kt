package com.ninthsemester.arsenalandroidnetworking

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.ninthsemester.arsenalandroidnetworking.api.ConnectionDetector
import com.ninthsemester.arsenalandroidnetworking.volley.VolleyNetworkApi
import com.ninthsemester.arsenalandroidnetworking.volley.core.VolleyRequestQueue
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.AnkoLogger

class MainActivity : AppCompatActivity(), AnkoLogger{

    private val baseUrl by lazy { getString(R.string.base_url) }
    private val api by lazy {
        VolleyNetworkApi(
                VolleyRequestQueue.getInstance(this),
                baseUrl,
                headers = hashMapOf(Pair("token","fdsfdsfsd")),
                connectionDetector = ConnectionDetector.getInstance(this)
        )
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnRegisterUser.setOnClickListener { registerUser() }
    }


    private fun registerUser() {


        api.getAllUsers({},{})

//        val dob = HashMap<String, String>()
//
//        dob["dd"] = "14"
//        dob["mm"] = "10"
//        dob["yyyy"] = "1994"
//
//        val user = User(
//                name = "Sahil",
//                email = "sahilpatel1410@gmail.com",
//                password = "1234",
//                dob = dob
//        )
//
////        api.registerUser(user, {
////            info { it }
////        },{
////            info { it }
////        })
//
//        val stream = assets.open("android_kotlin_logos.jpg")
//        val buffer = ByteArrayOutputStream()
//
//        val bytes = stream.readBytes(16384)
//        stream.close()
//
//        val dataPart = DataPart("android_kotlin_logos.jpg", bytes, "image/jpg")
//        api.uploadFile(dataPart, 3, {}, {})
    }

}
