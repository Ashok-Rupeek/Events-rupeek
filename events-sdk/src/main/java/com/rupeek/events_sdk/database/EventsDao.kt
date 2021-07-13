package com.rupeek.events_sdk.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rupeek.events_sdk.models.Events
import com.rupeek.events_sdk.models.TempTable
import com.rupeek.events_sdk.utils.API_BATCH_LIMIT
import kotlinx.coroutines.flow.Flow

@Dao
interface EventsDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(events: Events)

    @Query("SELECT * FROM Events_Table")
    suspend fun getAllEvents(): List<Events>

    @Query("SELECT COUNT(*) FROM Events_Table")
    fun observeCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM Events_Table")
    fun getCount(): Int

    @Query("SELECT * FROM Events_Table ORDER BY Events_ID LIMIT $API_BATCH_LIMIT")
    fun getTopNRecords(): List<Events>

    @Query("DELETE FROM Events_Table WHERE Events_ID IN ( SELECT Events_ID FROM Events_Table ORDER BY Events_ID LIMIT $API_BATCH_LIMIT )")
    suspend fun deleteTopTenRecords()

    @Query("DELETE FROM Events_Table WHERE Events_ID LIKE :EventsId")
    suspend fun deleteRecordsById(EventsId: Int)

    @Query("DELETE FROM Temp_Table WHERE ID LIKE :tempId")
    suspend fun deleteRecordsFromTempTableById(tempId: Int)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertToTempTable(tempTable: TempTable)

    @Query("SELECT * FROM Temp_Table")
    suspend fun getAllIds(): List<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg tempTable: TempTable)
}