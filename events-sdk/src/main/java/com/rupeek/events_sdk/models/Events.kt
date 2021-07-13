package com.rupeek.events_sdk.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Events_Table")
data class Events(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "Events_ID") val id: Int = 0,
    @ColumnInfo(name = "TimeStamp") val timeStamp: String,
    @ColumnInfo(name = "EventName") val eventName: String,
    @ColumnInfo(name = "Properties") val properties: String?,
    @ColumnInfo(name = "Category") val category: String?,
    @ColumnInfo(name = "Source") val source: String?,
    @ColumnInfo(name = "ScreenName") val screenName: String?,
    @ColumnInfo(name = "EventType") val eventType: String,
)
