package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        EqubEntity::class,
        ApplicationEntity::class,
        TransactionEntity::class,
        ChatMessageEntity::class,
        UserProfileEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class EqubDatabase : RoomDatabase() {
    abstract fun equbDao(): EqubDao
    abstract fun applicationDao(): ApplicationDao
    abstract fun transactionDao(): TransactionDao
    abstract fun chatMessageDao(): ChatMessageDao
    abstract fun userProfileDao(): UserProfileDao

    companion object {
        @Volatile
        private var INSTANCE: EqubDatabase? = null

        fun getDatabase(context: Context): EqubDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    EqubDatabase::class.java,
                    "equb_database"
                )
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
