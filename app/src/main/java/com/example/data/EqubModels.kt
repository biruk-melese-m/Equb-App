package com.example.data

data class EqubItem(
    val id: String,
    val title: String,
    val totalAmount: String,
    val monthlyContribution: String,
    val currentMembers: Int,
    val maxMembers: Int,
    val durationMonths: Int,
    val nextPaymentDate: String,
    val category: String, // "Savings", "Business", "House", "Auto", "Tech", "Travel"
    val goalAmount: String,
    val progressAmount: String,
    val dueDate: String,
    val isUserJoined: Boolean = false,
    val userPosition: Int = 4,
    val currentRound: Int = 4,
    val totalRounds: Int = 12,
    val userSavedAmount: String = "20,000 ETB",
    val daysLeftTillDue: Int = 5,
    val securityDetail: String = "100% ID-verified members with collateral backing & CBE escrow bank guarantee."
)

data class Member(
    val id: String,
    val name: String,
    val totalContributions: String,
    val rotationPosition: String,
    val phone: String,
    val email: String,
    val isPaid: Boolean,
    val isPending: Boolean = !isPaid,
    val initialColor: Long = 0xFF6B4EC4
)

data class PaymentRound(
    val roundNumber: Int,
    val recipientName: String,
    val amount: String,
    val status: String // "Paid", "Pending", "Upcoming"
)

data class PaymentHistoryItem(
    val roundNumber: Int,
    val date: String,
    val amount: String,
    val status: String // "Paid", "Pending"
)

data class PayoutHistoryItem(
    val title: String,
    val roundNumber: Int,
    val amount: String,
    val date: String,
    val isFamilyCircle: Boolean = false
)

data class TransactionItem(
    val id: String,
    val date: String,
    val type: String, // "Monthly Contribution", "Withdrawal", "Joining Fee", "Penalty"
    val amount: String,
    val isPositive: Boolean,
    val isSuccess: Boolean = true
)

data class EqubApplication(
    val id: String,
    val equbTitle: String,
    val amount: String,
    val appliedDate: String,
    val status: String, // "Pending", "Approved", "Rejected"
    val applicantName: String,
    val phone: String,
    val reason: String
)

data class ChatMessage(
    val id: String,
    val senderName: String,
    val text: String,
    val time: String,
    val isSystem: Boolean = false
)

data class Announcement(
    val id: String,
    val title: String,
    val description: String,
    val date: String,
    val category: String = "General",
    val isUrgent: Boolean = false,
    val isSystem: Boolean = true
)

data class UserProfile(
    val name: String = "Brook",
    val fullName: String = "Brook Melles",
    val phone: String = "+251 911 234 567",
    val email: String = "brook.melles@email.com",
    val totalSavings: String = "20,000 ETB",
    val lastAddedAmount: String = "500 ETB",
    val referralCode: String = "EQUB2024"
)
