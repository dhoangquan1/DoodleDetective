package edu.wpi.cs.cs4518.stepchart.database

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import java.time.LocalDate
import java.time.ZoneId

// This object helps transferring data in a more convenient way to the viewModel
// The same could be done with Pair
data class HourlySteps(
    val hour: Int,
    val totalSteps: Int
)

/**
 * DAO (Data Access Object) for fetching data from the data base and parse it to the viewModel
 */
@Dao
interface StepDataDao {

    @Insert
    suspend fun insert(playerData: PlayerData)

    @Query("""
        SELECT * 
        FROM leaderboard 
        ORDER BY score
        DESC LIMIT 10
    """)
    suspend fun getLeaderboard(): List<PlayerData>

    @Query("""
        SELECT COUNT(*)
        FROM leaderboard
    """)
    suspend fun getLeaderboardCount(): Int

    @Query("""
        SELECT score 
        FROM leaderboard 
        ORDER BY score 
        DESC LIMIT 1 
        OFFSET 9
    """)
    fun get10thHighestScore(): Int?

    @Query("""
        DELETE 
        FROM leaderboard 
        WHERE score < :threshold
    """)
    fun cleanLeaderboard(threshold: Int)

}