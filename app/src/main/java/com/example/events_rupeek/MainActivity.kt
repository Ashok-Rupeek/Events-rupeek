package com.example.events_rupeek

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.rupeek.events_sdk.EventsRupeekApi
import com.rupeek.events_sdk.models.UserEvents

class MainActivity : AppCompatActivity() {
    lateinit var button:Button
    val  hashMap = HashMap<String,Any>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        EventsRupeekApi.init(application)
        button = findViewById(R.id.button)
        hashMap.put("Test","Button")
        button.setOnClickListener {
            EventsRupeekApi.trackEvents(UserEvents(
                timeStamp = System.currentTimeMillis().toString(),
                eventName = "Button Click Event",
                properties = hashMap,
                eventType = "Custom"
            ))
        }
    }
}