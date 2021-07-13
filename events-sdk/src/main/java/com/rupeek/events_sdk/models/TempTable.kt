package com.rupeek.events_sdk.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Temp_Table")
data class TempTable(@PrimaryKey @ColumnInfo(name = "ID") val id: Int)