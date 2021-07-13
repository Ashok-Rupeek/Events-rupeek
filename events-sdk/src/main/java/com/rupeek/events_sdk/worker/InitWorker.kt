package com.rupeek.events_sdk.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import com.rupeek.events_sdk.EventsRupeekApi.deviceIdentity
import com.rupeek.events_sdk.EventsRupeekApi.eventsRepository
import com.rupeek.events_sdk.EventsRupeekApi.userIdentity
import com.rupeek.events_sdk.models.Events
import com.rupeek.events_sdk.models.TempTable
import com.rupeek.events_sdk.network.helper.ResultWrapper
import com.rupeek.events_sdk.utils.API_BATCH_LIMIT
import kotlinx.coroutines.delay

class InitWorker(context: Context, workerParameters: WorkerParameters) :
    CoroutineWorker(context, workerParameters) {
    override suspend fun doWork(): Result {
        return try {
            Log.d("worker  debug:  ", "started")
            startWorkerBasedOnCondition()
        } catch (e: Exception) {
            Log.d("exception debug:  ", "${e.message}")
            Result.failure()
        }

    }

    private suspend fun startWorkerBasedOnCondition(): Result {
        return try {
            if (eventsRepository.getCountOfEventsTable() == 0) {
                Log.d("startWorkerBasedOn", "debug:  equals to 0")
                return Result.success()
            }
            if (isFetchAllowed()) {
                Log.d("startWorkerBasedOn", "debug:  greater than 20")
                val eventsList = getTopNRecordsFromEventTable()
                insertRecordIdIntoTempTable(eventsList)
                synchronizeDataset(eventsList)

            } else {
                Log.d("startWorkerBasedOn", "debug:  less than 20")
                val eventsList = getAllEvents()
                insertRecordIdIntoTempTable(eventsList)
                synchronizeDataset(eventsList)
            }
            Result.success()
        } catch (e: java.lang.Exception) {
            Result.failure()
        }

    }


    private suspend fun synchronizeDataset(eventsList: ArrayList<Events>): ListenableWorker.Result {
        eventsRepository.syncEventsData(eventsList, userIdentity, deviceIdentity).let {
            when (it) {
                is ResultWrapper.Success -> {
                    Log.d(" debug: ", "callback success")
                    deleteRecordsFromEventsTable()
                    startWorkerBasedOnCondition()
                }
                is ResultWrapper.GenericError -> {
                    Log.d("debug: error ${it.retryTime}", "${it.code} ${it.error}")
                    if (it.code == 429) {
                        delay(it.retryTime * 1000L)
                        startWorkerBasedOnCondition()
                    } else return Result.retry()
                }
                is ResultWrapper.NetworkError -> {
                    Log.d("debug: network error ", "")
                    return Result.retry()
                }
            }
            return Result.success()
        }
    }

    private suspend fun insertRecordIdIntoTempTable(eventsList: ArrayList<Events>) {
        val tempTableList: ArrayList<TempTable> = arrayListOf()
        eventsList.forEach {
            tempTableList.add(TempTable(it.id))
        }
        eventsRepository.insertAllIntoTempTable(tempTableList)
    }

    private suspend fun isFetchAllowed(): Boolean {
        val eventTableCount = eventsRepository.getCountOfEventsTable()
        return eventTableCount >= API_BATCH_LIMIT
    }

    suspend fun getTopNRecordsFromEventTable(): ArrayList<Events> {
        val eventsList: ArrayList<Events> = arrayListOf()
        eventsRepository.fetchTopNRecords().forEachIndexed { _, events ->
            eventsList.add(events)
        }
        return eventsList
    }

    suspend fun getAllEvents(): ArrayList<Events> {
        val eventsList: ArrayList<Events> = arrayListOf()
        eventsRepository.fetchAllRecordsFromEventsTable()
            .forEachIndexed { _, events ->
                eventsList.add(events)
            }
        return eventsList
    }

    suspend fun deleteRecordsFromEventsTable() {
        eventsRepository.fetchRecordsFromTempTable().forEach { id ->
            eventsRepository.deleteRecordById(id)
            eventsRepository.deleteRecordsFromTempTableById(id)
        }
    }


}
