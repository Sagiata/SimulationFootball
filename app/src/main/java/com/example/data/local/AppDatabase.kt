package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.data.local.converter.RoomConverters
import com.example.data.local.dao.MatchHistoryDao
import com.example.data.local.dao.PlayerDao
import com.example.data.local.dao.TeamDao
import com.example.data.local.entity.MatchHistoryEntity
import com.example.data.local.entity.PlayerEntity
import com.example.data.local.entity.TeamEntity

@Database(
    entities = [
        PlayerEntity::class,
        TeamEntity::class,
        MatchHistoryEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(RoomConverters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun playerDao(): PlayerDao
    abstract fun teamDao(): TeamDao
    abstract fun matchHistoryDao(): MatchHistoryDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "tactical_fm_sim.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
