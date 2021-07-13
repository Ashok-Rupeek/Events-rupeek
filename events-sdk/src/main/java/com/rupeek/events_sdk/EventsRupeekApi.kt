package com.rupeek.events_sdk

import android.app.Application
import android.util.Log
import androidx.work.*
import com.example.sync_employee.data.worker.SyncWorker
import com.rupeek.events_sdk.database.EventsDatabase
import com.rupeek.events_sdk.models.DeviceIdentity
import com.rupeek.events_sdk.models.Events
import com.rupeek.events_sdk.models.UserEvents
import com.rupeek.events_sdk.models.UserIdentity
import com.rupeek.events_sdk.network.ApiClient
import com.rupeek.events_sdk.network.EventsInterface
import com.rupeek.events_sdk.repository.EventsRepository
import com.rupeek.events_sdk.utils.CommonFunctions
import com.rupeek.events_sdk.worker.InitWorker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

object EventsRupeekApi {

    lateinit var context: Application
    lateinit var eventsDatabase: EventsDatabase
    lateinit var eventsRepository: EventsRepository
    lateinit var userIdentity: UserIdentity
    lateinit var deviceIdentity: DeviceIdentity
    private val coroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val apiService = ApiClient.getRetrofit().create(EventsInterface::class.java)
    const val SYNC_WORKER_TAG = "SYNC_WORKER_TAG"
    const val INIT_WORKER_TAG = "INIT_WORKER_TAG"

    fun init(context: Application) {
        this.context = context
        //this has to be fetched from user
        this.userIdentity = UserIdentity(appId = "101",mobilNumber = "mobileNumber",coreId = "coreId",leadId = "leadId")
        this.deviceIdentity = DeviceIdentity(deviceModel = "deviceModel",osVersion = "osVersion",appVersionNo = "appVersionNo")

        this.eventsDatabase = EventsDatabase.getInstance(EventsRupeekApi.context)
        this.eventsRepository =
            EventsRepository(eventsDatabase.eventsDao(), apiService)

        initiateWorker()
    }

    private fun initiateWorker() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresCharging(true)
            .build()

        val initWorker = OneTimeWorkRequest.Builder(InitWorker::class.java)
            .setConstraints(constraints)
            .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 15, TimeUnit.MINUTES)
            .build()

        WorkManager.getInstance(context)
            .enqueueUniqueWork(INIT_WORKER_TAG, ExistingWorkPolicy.KEEP, initWorker)

    }


    fun trackEvents(userEvents: UserEvents) {
        coroutineScope.launch {
            val events = CommonFunctions.convertUserEventsIntoEvents(userEvents)
            eventsRepository.insertEvents(events)
            val count = eventsRepository.getCountOfEventsTable()
            Log.d("count", "$count")
            if (count % 20 == 0 && count >= 20) {
                Log.d("count", "  if(count % 20 == 0 && count > 20){")
                if (!checkIsInitWorkAlreadySchduledOrRunning() && !checkIsSyncWorkAlreadySchduledOrRunning()) {
                    Log.d(" Schdule sync", "worker")
                    syncWorker()
                } else {
                    Log.d(" already Schduled", "sync worker")
                }
            } else {
                Log.d("false part", "(count % 20 == 0 && count > 20){")
            }

        }
    }


    private fun syncWorker() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresCharging(true)
            .build()

        val initWorker = OneTimeWorkRequest.Builder(SyncWorker::class.java)
            .setConstraints(constraints)
            .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 15, TimeUnit.MINUTES)
            .build()

        WorkManager.getInstance(context)
            .enqueueUniqueWork(SYNC_WORKER_TAG, ExistingWorkPolicy.KEEP, initWorker)
    }

    private suspend fun checkIsInitWorkAlreadySchduledOrRunning(): Boolean {
        Log.d("test", "checkIsWorkAlreadySchduledOrRunning")

        val workManager = WorkManager.getInstance(context)

        val workInfos = workManager.getWorkInfosForUniqueWork(INIT_WORKER_TAG).await()

        if (workInfos.size == 1) {
            val workInfo = workInfos[0]
            Log.d("debug: workInfo.state=${workInfo.state}", "id=${workInfo.id}")
            if (workInfo.state == WorkInfo.State.BLOCKED || workInfo.state == WorkInfo.State.ENQUEUED || workInfo.state == WorkInfo.State.RUNNING) {
                Log.d("isAlive", "debug: ")
                return true
            } else {
                Log.d("isDead", "debug: ")
                return false
            }
        } else {
            Log.d("notFound", "debug: ")
            return false
        }
    }

    private suspend fun checkIsSyncWorkAlreadySchduledOrRunning(): Boolean {
        Log.d("debug: test", "checkIsSyncWorkAlreadySchduledOrRunning")

        val workManager = WorkManager.getInstance(context)

        val workInfos = workManager.getWorkInfosForUniqueWork(SYNC_WORKER_TAG).await()

        if (workInfos.size == 1) {
            val workInfo = workInfos[0]
            Log.d("debug:  checkIsSyncAlready", "SchduledOrRunning")
            Log.d("debug: workInfo.state=${workInfo.state}", "id=${workInfo.id}")
            if (workInfo.state == WorkInfo.State.BLOCKED || workInfo.state == WorkInfo.State.ENQUEUED || workInfo.state == WorkInfo.State.RUNNING) {
                Log.d("isAlive", "debug: ")
                return true
            } else {
                Log.d("isDead", "debug: ")
                return false
            }
        } else {
            Log.d("notFound", "debug: ")
            return false
        }
    }

}