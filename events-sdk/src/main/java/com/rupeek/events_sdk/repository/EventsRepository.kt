package com.rupeek.events_sdk.repository

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.rupeek.events_sdk.database.EventsDao
import com.rupeek.events_sdk.models.DeviceIdentity
import com.rupeek.events_sdk.models.Events
import com.rupeek.events_sdk.models.TempTable
import com.rupeek.events_sdk.models.UserIdentity
import com.rupeek.events_sdk.network.EventsInterface
import com.rupeek.events_sdk.network.helper.ResultWrapper
import com.rupeek.events_sdk.network.helper.safeApiCall
import com.rupeek.events_sdk.utils.CommonFunctions
import com.rupeek.events_sdk.utils.age
import com.rupeek.events_sdk.utils.employeeName
import com.rupeek.events_sdk.utils.salary
import kotlinx.coroutines.flow.Flow

class EventsRepository(
    private val eventsDao: EventsDao,
    private val eventsInterface: EventsInterface
) {
    suspend fun insertEvents(events: Events) {
        eventsDao.insert(events)
    }

    fun observeCount(): Flow<Int> {
        return eventsDao.observeCount()
    }

    suspend fun getCountOfEventsTable(): Int {
        return eventsDao.getCount()
    }

    suspend fun deleteTopTenRecords() {
        eventsDao.deleteTopTenRecords()
    }

    suspend fun deleteRecordById(id: Int) {
        eventsDao.deleteRecordsById(id)
    }

    suspend fun deleteRecordsFromTempTableById(id: Int) {
        eventsDao.deleteRecordsFromTempTableById(id)
    }

    suspend fun fetchTopNRecords(): List<Events> {
        return eventsDao.getTopNRecords()
    }

    suspend fun fetchAllRecordsFromEventsTable(): List<Events> {
        return eventsDao.getAllEvents()
    }

    suspend fun insertIntoTempTable(id: Int) {
        eventsDao.insertToTempTable(TempTable(id))
    }

    suspend fun insertAllIntoTempTable(idList: ArrayList<TempTable>) {
        eventsDao.insertAll(*idList.toTypedArray())
    }

    suspend fun fetchRecordsFromTempTable(): List<Int> {
        return eventsDao.getAllIds()
    }

    suspend fun syncEventsData(
        events: ArrayList<Events>,
        userIdentity: UserIdentity,
        deviceIdentity: DeviceIdentity
    ): ResultWrapper<Unit> {
//        CommonFunctions.createPayload(events, userIdentity, deviceIdentity)
        val json = "{ \"name\": \"${employeeName}\", \"salary\": \"${salary}\",\"age\":\"${age}\" }"
        val jsonObject = Gson().fromJson(json, JsonObject::class.java)
        return safeApiCall { eventsInterface.postEmployees(jsonObject) }
    }
}