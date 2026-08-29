package com.example.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface EqubDao {
    @Query("SELECT * FROM equbs")
    fun getAllEqubs(): Flow<List<EqubEntity>>

    @Query("SELECT * FROM equbs WHERE id = :id")
    suspend fun getEqubById(id: String): EqubEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEqubs(equbs: List<EqubEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEqub(equb: EqubEntity)

    @Update
    suspend fun updateEqub(equb: EqubEntity)

    @Query("DELETE FROM equbs")
    suspend fun deleteAll()
}

@Dao
interface ApplicationDao {
    @Query("SELECT * FROM applications")
    fun getAllApplications(): Flow<List<ApplicationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertApplications(apps: List<ApplicationEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertApplication(app: ApplicationEntity)

    @Query("UPDATE applications SET status = :status WHERE id = :id")
    suspend fun updateStatus(id: String, status: String)

    @Query("DELETE FROM applications")
    suspend fun deleteAll()
}

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions ORDER BY timestamp DESC")
    fun getAllTransactions(): Flow<List<TransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransactions(transactions: List<TransactionEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(tx: TransactionEntity)

    @Query("DELETE FROM transactions")
    suspend fun deleteAll()
}

@Dao
interface ChatMessageDao {
    @Query("SELECT * FROM chat_messages ORDER BY timestamp ASC")
    fun getAllMessages(): Flow<List<ChatMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessages(messages: List<ChatMessageEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessageEntity)

    @Query("DELETE FROM chat_messages")
    suspend fun deleteAll()
}

@Dao
interface UserProfileDao {
    @Query("SELECT * FROM user_profile WHERE id = 'current_user' LIMIT 1")
    fun getUserProfile(): Flow<UserProfileEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserProfile(profile: UserProfileEntity)
}
