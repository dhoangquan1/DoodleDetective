package edu.wpi.cs.cs4518.stepchart.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * This class initialize the database using Room
 */
@Database(entities = [PlayerData::class], version = 1, exportSchema = false)
abstract class PlayerDatabase : RoomDatabase() {
    //Return the necessary DAO to interact with the DB store on the emulator/phone
    abstract fun stepDataDao(): StepDataDao

    companion object {
        // Holds the singleton instance of the database
        // This ensure only 1 database is made and the data is persisted
        private var INSTANCE: PlayerDatabase? = null

        // If instance exist, then return instance; else, initialize a new DB
        fun getInstance(context: Context): PlayerDatabase {
            return INSTANCE ?: Room.databaseBuilder(
                context.applicationContext,
                PlayerDatabase::class.java,
                "leaderboard"
            ).build().also { INSTANCE = it }
        }
    }
}