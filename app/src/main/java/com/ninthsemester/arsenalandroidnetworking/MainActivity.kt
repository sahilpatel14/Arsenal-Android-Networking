package com.ninthsemester.arsenalandroidnetworking

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.android.volley.Response
import com.ninthsemester.arsenalandroidnetworking.volley.Samples
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info

class MainActivity : AppCompatActivity() , AnkoLogger{

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Samples.simpleRequest(this, Response.Listener {

            info { "Response is : ${it.substring(0, 500)}" }
            textView.text = it.substring(0, 500)

        } ,"act")

        Samples.makeJsonRequest(this)
        Samples.makeCustomRequest(this)
    }

    override fun onStop() {
        super.onStop()
        Samples.cancelRequest(this, "act")
    }
}
