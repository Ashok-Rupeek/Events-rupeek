package com.rupeek.events_sdk.utils

import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.rupeek.events_sdk.models.DeviceIdentity
import com.rupeek.events_sdk.models.Events
import com.rupeek.events_sdk.models.UserEvents
import com.rupeek.events_sdk.models.UserIdentity

object CommonFunctions {
    fun createPayload(
        events: ArrayList<Events>,
        userIdentity: UserIdentity,
        deviceIdentity: DeviceIdentity
    ): JsonObject {

        /***********
        //sample json for below string
        {
        "userIdentity" : {
        "Mobilnumber":"",
        "leadId":"",
        "coreId":"",
        "AppId":""
        }
        "deviceIdentity": {
        "VersionNo":"",
        "DeviceModel":"",
        "Osversion":""
        }
        "Events ":{
        "eventData":"[
        {
        Eventname:"eventname",
        Properties:{name:"name",category:"",price:"",orderid:"",total:"",discount:""},
        Category:"event_category",
        Source:"event_source",
        screenName:"screenName",
        eventType:"custom",
        timestamp:""
        },
        {
        Eventname:"Application Installed",
        eventType:"Application"
        },
        {
        Eventname:"eventname",
        Properties:{name:"name",category:"",price:"",orderid:"",total:"",discount:""},
        Category:"event_category",
        Source:"event_source",
        screenName:"screenName",
        eventType:"custom"
        },
        {
        Eventname:"Application Updated",
        eventType:"Application"
        }
        ]"
        }
        }

         ******** * */
        val stringBuilder = StringBuilder()
        //begin object
        stringBuilder.append("{")
        //userIdentity & deviceIdentity
        stringBuilder.append("\"userIdentity\" : {\"Mobilnumber\":\"${userIdentity.mobilNumber}\",\"leadId\":\"${userIdentity.leadId}\",\"coreId\":\"${userIdentity.coreId}\",\"AppId\":\"${userIdentity.appId}\" },")
        stringBuilder.append("\"deviceIdentity\" : {\"VersionNo\":\"${deviceIdentity.appVersionNo}\",\"DeviceModel\":\"${deviceIdentity.deviceModel}\",\"Osversion\":\"${deviceIdentity.osVersion}\"},")
        //event object
        stringBuilder.append("\"Event\":{")
        //begin event object array
        stringBuilder.append("\"eventData\":[")
        //iterate event list
        events.forEachIndexed { index, events ->
            if (index == 0) stringBuilder.append("{") else stringBuilder.append(",{")
            stringBuilder.append("\"Eventname\":\"${events.eventName}\",")
            events.properties?.let { stringBuilder.append("\"Properties\":$it,") }
            events.category?.let { stringBuilder.append("\"Category\":\"$it\",") }
            events.source?.let { stringBuilder.append("\"Source\":\"$it\",") }
            events.screenName?.let { stringBuilder.append("\"ScreenName\":\"$it\",") }
            events.eventType.let { stringBuilder.append("\"EventType\":\"$it\",") }
            stringBuilder.append("\"timestamp\":\"${events.timeStamp}\"")
            stringBuilder.append("}")
        }
        //end of array
        stringBuilder.append("]")
        //end of event object
        stringBuilder.append("}")
        //end object
        stringBuilder.append("}")
        val json = stringBuilder.trim().toString()
        Log.d("debug: ", "$json")
        val jsonObject = Gson().fromJson(json, JsonObject::class.java)
        return jsonObject
    }

    fun convertUserEventsIntoEvents(userEvents: UserEvents): Events {
        val objGson = Gson()
        val eventProperties = objGson.toJson(userEvents.properties)
        return Events(
            timeStamp = userEvents.timeStamp,
            eventName = userEvents.eventName,
            properties = eventProperties,
            category = userEvents.category,
            source = userEvents.source,
            screenName = userEvents.screenName,
            eventType = userEvents.eventType
        )
    }
}