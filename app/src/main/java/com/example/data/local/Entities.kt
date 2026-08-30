package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.Announcement
import com.example.data.ChatMessage
import com.example.data.EqubApplication
import com.example.data.EqubItem
import com.example.data.Member
import com.example.data.PaymentHistoryItem
import com.example.data.PaymentRound
import com.example.data.PayoutHistoryItem
import com.example.data.TransactionItem
import com.example.data.UserProfile

@Entity(tableName = "equbs")
data class EqubEntity(
    @PrimaryKey val id: String,
    val title: String,
    val totalAmount: String,
    val monthlyContribution: String,
    val currentMembers: Int,
    val maxMembers: Int,
    val durationMonths: Int,
    val nextPaymentDate: String,
    val category: String,
    val goalAmount: String,
    val progressAmount: String,
    val dueDate: String,
    val isUserJoined: Boolean,
    val userPosition: Int,
    val currentRound: Int = 4,
    val totalRounds: Int = 12,
    val userSavedAmount: String = "20,000 ETB",
    val daysLeftTillDue: Int = 5,
    val securityDetail: String = "100% ID-verified members with collateral backing & CBE escrow bank guarantee."
) {
    fun toDomain(): EqubItem = EqubItem(
        id = id,
        title = title,
        totalAmount = totalAmount,
        monthlyContribution = monthlyContribution,
        currentMembers = currentMembers,
        maxMembers = maxMembers,
        durationMonths = durationMonths,
        nextPaymentDate = nextPaymentDate,
        category = category,
        goalAmount = goalAmount,
        progressAmount = progressAmount,
        dueDate = dueDate,
        isUserJoined = isUserJoined,
        userPosition = userPosition,
        currentRound = currentRound,
        totalRounds = totalRounds,
        userSavedAmount = userSavedAmount,
        daysLeftTillDue = daysLeftTillDue,
        securityDetail = securityDetail
    )

    companion object {
        fun fromDomain(item: EqubItem): EqubEntity = EqubEntity(
            id = item.id,
            title = item.title,
            totalAmount = item.totalAmount,
            monthlyContribution = item.monthlyContribution,
            currentMembers = item.currentMembers,
            maxMembers = item.maxMembers,
            durationMonths = item.durationMonths,
            nextPaymentDate = item.nextPaymentDate,
            category = item.category,
            goalAmount = item.goalAmount,
            progressAmount = item.progressAmount,
            dueDate = item.dueDate,
            isUserJoined = item.isUserJoined,
            userPosition = item.userPosition,
            currentRound = item.currentRound,
            totalRounds = item.totalRounds,
            userSavedAmount = item.userSavedAmount,
            daysLeftTillDue = item.daysLeftTillDue,
            securityDetail = item.securityDetail
        )
    }
}

@Entity(tableName = "applications")
data class ApplicationEntity(
    @PrimaryKey val id: String,
    val equbTitle: String,
    val amount: String,
    val appliedDate: String,
    val status: String,
    val applicantName: String,
    val phone: String,
    val reason: String
) {
    fun toDomain(): EqubApplication = EqubApplication(
        id = id,
        equbTitle = equbTitle,
        amount = amount,
        appliedDate = appliedDate,
        status = status,
        applicantName = applicantName,
        phone = phone,
        reason = reason
    )

    companion object {
        fun fromDomain(app: EqubApplication): ApplicationEntity = ApplicationEntity(
            id = app.id,
            equbTitle = app.equbTitle,
            amount = app.amount,
            appliedDate = app.appliedDate,
            status = app.status,
            applicantName = app.applicantName,
            phone = app.phone,
            reason = app.reason
        )
    }
}

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey val id: String,
    val date: String,
    val type: String,
    val amount: String,
    val isPositive: Boolean,
    val isSuccess: Boolean,
    val timestamp: Long = System.currentTimeMillis()
) {
    fun toDomain(): TransactionItem = TransactionItem(
        id = id,
        date = date,
        type = type,
        amount = amount,
        isPositive = isPositive,
        isSuccess = isSuccess
    )

    companion object {
        fun fromDomain(tx: TransactionItem, timestamp: Long = System.currentTimeMillis()): TransactionEntity = TransactionEntity(
            id = tx.id,
            date = tx.date,
            type = tx.type,
            amount = tx.amount,
            isPositive = tx.isPositive,
            isSuccess = tx.isSuccess,
            timestamp = timestamp
        )
    }
}

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey val id: String,
    val senderName: String,
    val text: String,
    val time: String,
    val isSystem: Boolean,
    val timestamp: Long = System.currentTimeMillis()
) {
    fun toDomain(): ChatMessage = ChatMessage(
        id = id,
        senderName = senderName,
        text = text,
        time = time,
        isSystem = isSystem
    )

    companion object {
        fun fromDomain(msg: ChatMessage, timestamp: Long = System.currentTimeMillis()): ChatMessageEntity = ChatMessageEntity(
            id = msg.id,
            senderName = msg.senderName,
            text = msg.text,
            time = msg.time,
            isSystem = msg.isSystem,
            timestamp = timestamp
        )
    }
}

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val id: String = "current_user",
    val name: String,
    val fullName: String,
    val phone: String,
    val email: String,
    val totalSavings: String,
    val lastAddedAmount: String,
    val referralCode: String
) {
    fun toDomain(): UserProfile = UserProfile(
        name = name,
        fullName = fullName,
        phone = phone,
        email = email,
        totalSavings = totalSavings,
        lastAddedAmount = lastAddedAmount,
        referralCode = referralCode
    )

    companion object {
        fun fromDomain(profile: UserProfile): UserProfileEntity = UserProfileEntity(
            id = "current_user",
            name = profile.name,
            fullName = profile.fullName,
            phone = profile.phone,
            email = profile.email,
            totalSavings = profile.totalSavings,
            lastAddedAmount = profile.lastAddedAmount,
            referralCode = profile.referralCode
        )
    }
}
