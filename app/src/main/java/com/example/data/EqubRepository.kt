package com.example.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

object EqubRepository {

    private val _userProfile = MutableStateFlow(UserProfile())
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    private val _equbs = MutableStateFlow(
        listOf(
            EqubItem(
                id = "equb-1",
                title = "Monthly Savings Equb",
                totalAmount = "2,400,000 ETB",
                monthlyContribution = "2,000 ETB",
                currentMembers = 18,
                maxMembers = 20,
                durationMonths = 12,
                nextPaymentDate = "Jun 20, 2024",
                category = "Savings",
                goalAmount = "10,000 ETB",
                progressAmount = "2,500 ETB",
                dueDate = "May 25, 2023",
                isUserJoined = true,
                userPosition = 7
            ),
            EqubItem(
                id = "equb-2",
                title = "Business Growth Equb",
                totalAmount = "600,000 ETB",
                monthlyContribution = "5,000 ETB",
                currentMembers = 10,
                maxMembers = 12,
                durationMonths = 12,
                nextPaymentDate = "June 10, 2023",
                category = "Business",
                goalAmount = "50,000 ETB",
                progressAmount = "5,000 ETB",
                dueDate = "June 10, 2023",
                isUserJoined = true,
                userPosition = 3
            ),
            EqubItem(
                id = "equb-3",
                title = "House Building Equb",
                totalAmount = "1,000,000 ETB",
                monthlyContribution = "10,000 ETB",
                currentMembers = 5,
                maxMembers = 10,
                durationMonths = 10,
                nextPaymentDate = "Jul 15, 2024",
                category = "House",
                goalAmount = "100,000 ETB",
                progressAmount = "0 ETB",
                dueDate = "Jul 15, 2024",
                isUserJoined = false
            ),
            EqubItem(
                id = "equb-4",
                title = "Automobile Equb",
                totalAmount = "1,500,000 ETB",
                monthlyContribution = "15,000 ETB",
                currentMembers = 8,
                maxMembers = 10,
                durationMonths = 10,
                nextPaymentDate = "Aug 01, 2024",
                category = "Auto",
                goalAmount = "150,000 ETB",
                progressAmount = "0 ETB",
                dueDate = "Aug 01, 2024",
                isUserJoined = false
            )
        )
    )
    val equbs: StateFlow<List<EqubItem>> = _equbs.asStateFlow()

    private val _applications = MutableStateFlow(
        listOf(
            EqubApplication(
                id = "app-1",
                equbTitle = "Monthly Savings Equb",
                amount = "20,000 ETB",
                appliedDate = "Apr 15, 2024",
                status = "Pending",
                applicantName = "Brook Melles",
                phone = "+251 911 234 567",
                reason = "To save systematically for home improvement"
            ),
            EqubApplication(
                id = "app-2",
                equbTitle = "Business Growth Equb",
                amount = "50,000 ETB",
                appliedDate = "Apr 10, 2024",
                status = "Pending",
                applicantName = "Brook Melles",
                phone = "+251 911 234 567",
                reason = "Inventory expansion for commercial retail shop"
            ),
            EqubApplication(
                id = "app-3",
                equbTitle = "House Building Equb",
                amount = "100,000 ETB",
                appliedDate = "Apr 05, 2024",
                status = "Pending",
                applicantName = "Brook Melles",
                phone = "+251 911 234 567",
                reason = "Foundation and cement purchasing"
            ),
            EqubApplication(
                id = "app-4",
                equbTitle = "Family Savings Circle",
                amount = "15,000 ETB",
                appliedDate = "Mar 01, 2024",
                status = "Approved",
                applicantName = "Brook Melles",
                phone = "+251 911 234 567",
                reason = "Family emergency safety reserve"
            )
        )
    )
    val applications: StateFlow<List<EqubApplication>> = _applications.asStateFlow()

    private val _members = MutableStateFlow(
        listOf(
            Member("m1", "Bruk A.", "50,000 ETB", "1st", "+251 911 112 233", "bruk.a@email.com", isPaid = true, initialColor = 0xFF5B34B2),
            Member("m2", "Dawit G.", "50,000 ETB", "2nd", "+251 911 223 344", "dawit.g@email.com", isPaid = true, initialColor = 0xFF3D5AFE),
            Member("m3", "Samrawit S.", "50,000 ETB", "3rd", "+251 911 334 455", "samrawit.s@email.com", isPaid = false, initialColor = 0xFFE91E63),
            Member("m4", "Yonas B.", "50,000 ETB", "4th", "+251 911 445 566", "yonas.b@email.com", isPaid = true, initialColor = 0xFF00B0FF),
            Member("m5", "Dawit A.", "50,000 ETB", "5th", "+251 911 556 677", "dawit.a@email.com", isPaid = true, initialColor = 0xFF651FFF),
            Member("m6", "Dawit G.", "50,000 ETB", "6th", "+251 911 667 788", "dawit.g2@email.com", isPaid = false, initialColor = 0xFF00E676),
            Member("m7", "Abel M. (You)", "50,000 ETB", "7th", "+251 911 234 567", "brook.melles@email.com", isPaid = true, initialColor = 0xFF4B3293),
            Member("m8", "David Bekele", "50,000 ETB", "2nd", "+251 911 234 567", "david.bekele@email.com", isPaid = true, initialColor = 0xFF7C4DFF),
            Member("m9", "Abebe Kebede", "50,000 ETB", "9th", "+251 911 889 900", "abebe.k@email.com", isPaid = false, initialColor = 0xFFFF6D00),
            Member("m10", "Sara Tesfaye", "50,000 ETB", "10th", "+251 911 990 011", "sara.t@email.com", isPaid = false, initialColor = 0xFFD500F9)
        )
    )
    val members: StateFlow<List<Member>> = _members.asStateFlow()

    private val _paymentSchedule = MutableStateFlow(
        listOf(
            PaymentRound(1, "Abebe Bikila", "10,000 ETB", "Paid"),
            PaymentRound(2, "Sara Mengistu", "10,000 ETB", "Paid"),
            PaymentRound(3, "Yonas Kebede", "10,000 ETB", "Paid"),
            PaymentRound(4, "Daniel K.", "10,000 ETB", "Pending"),
            PaymentRound(5, "Nardos T.", "10,000 ETB", "Upcoming"),
            PaymentRound(6, "Daniel K.", "10,000 ETB", "Upcoming"),
            PaymentRound(7, "Nardos T.", "10,000 ETB", "Upcoming"),
            PaymentRound(10, "Nardos T.", "10,000 ETB", "Upcoming"),
            PaymentRound(12, "Nardos T.", "10,000 ETB", "Upcoming"),
            PaymentRound(14, "Nardos T.", "10,000 ETB", "Upcoming"),
            PaymentRound(16, "Yonas K.", "10,000 ETB", "Upcoming"),
            PaymentRound(18, "Yonas Keb", "10,000 ETB", "Upcoming"),
            PaymentRound(20, "Nardos T.", "10,000 ETB", "Upcoming")
        )
    )
    val paymentSchedule: StateFlow<List<PaymentRound>> = _paymentSchedule.asStateFlow()

    private val _paymentHistory = MutableStateFlow(
        listOf(
            PaymentHistoryItem(12, "Oct 15, 2023", "1,000 ETB", "Paid"),
            PaymentHistoryItem(13, "Nov 15, 2023", "1,000 ETB", "Pending"),
            PaymentHistoryItem(14, "Dec 15, 2023", "1,000 ETB", "Pending"),
            PaymentHistoryItem(11, "Sep 15, 2023", "1,000 ETB", "Paid"),
            PaymentHistoryItem(10, "Aug 15, 2023", "1,000 ETB", "Paid"),
            PaymentHistoryItem(9, "Jul 15, 2023", "1,000 ETB", "Paid"),
            PaymentHistoryItem(8, "Jun 15, 2023", "1,000 ETB", "Paid"),
            PaymentHistoryItem(7, "May 15, 2023", "1,000 ETB", "Paid"),
            PaymentHistoryItem(6, "Dec 15, 2023", "1,000 ETB", "Paid")
        )
    )
    val paymentHistory: StateFlow<List<PaymentHistoryItem>> = _paymentHistory.asStateFlow()

    private val _payoutHistory = MutableStateFlow(
        listOf(
            PayoutHistoryItem("Equb: Monthly Savings", 4, "500 ETB", "Jun 25, 2024", false),
            PayoutHistoryItem("Equb: Monthly Savings", 3, "1,000 ETB", "May 15, 2024", false),
            PayoutHistoryItem("Equb: Family Circle", 2, "1,000 ETB", "May 15, 2024", true),
            PayoutHistoryItem("Equb: Monthly Savings", 4, "500 ETB", "May 15, 2024", false),
            PayoutHistoryItem("Equb: Family Circle", 2, "1000 ETB", "May 15, 2024", false)
        )
    )
    val payoutHistory: StateFlow<List<PayoutHistoryItem>> = _payoutHistory.asStateFlow()

    private val _transactions = MutableStateFlow(
        listOf(
            TransactionItem("t1", "May 29, 2023", "Monthly Contribution", "+5,000 ETB", isPositive = true, isSuccess = true),
            TransactionItem("t2", "May 15, 2023", "Withdrawal", "-10,000 ETB", isPositive = false, isSuccess = false),
            TransactionItem("t3", "May 15, 2023", "Withdrawal", "-10,000 ETB", isPositive = false, isSuccess = true),
            TransactionItem("t4", "May 1, 2023", "Joining Fee", "+200 ETB", isPositive = true, isSuccess = false),
            TransactionItem("t5", "May 1, 2023", "Joining Fee", "+5,000 ETB", isPositive = true, isSuccess = true),
            TransactionItem("t6", "May 1, 2023", "Withdrawal", "-10,000 ETB", isPositive = false, isSuccess = false),
            TransactionItem("t7", "Apr 29, 2023", "Monthly Contribution", "+5,000 ETB", isPositive = true, isSuccess = true),
            TransactionItem("t8", "Apr 10, 2023", "Penalty", "-150 ETB", isPositive = false, isSuccess = false),
            TransactionItem("t9", "Apr 10, 2023", "Penalty", "-150 ETB", isPositive = false, isSuccess = true)
        )
    )
    val transactions: StateFlow<List<TransactionItem>> = _transactions.asStateFlow()

    private val _messages = MutableStateFlow(
        listOf(
            ChatMessage("msg-1", "Equb Admin", "Please check the updated Equb rules.", "10:30 AM"),
            ChatMessage("msg-2", "Sarah K.", "Did you receive my payment?", "Yesterday")
        )
    )
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private val _announcements = MutableStateFlow(
        listOf(
            Announcement("a-1", "Community Update: New Features!", "Read about the latest app improvements.", "Oct 20, 2023", isSystem = true),
            Announcement("a-2", "Important: Rule Change Notification", "Review the revised contribution schedule.", "Oct 15, 2023", isSystem = true)
        )
    )
    val announcements: StateFlow<List<Announcement>> = _announcements.asStateFlow()

    // Interactive Actions
    fun submitApplication(equbTitle: String, name: String, phone: String, reason: String) {
        val newApp = EqubApplication(
            id = "app-${System.currentTimeMillis()}",
            equbTitle = equbTitle,
            amount = "20,000 ETB",
            appliedDate = "Today",
            status = "Pending",
            applicantName = name.ifBlank { "Brook Melles" },
            phone = phone.ifBlank { "+251 911 234 567" },
            reason = reason
        )
        _applications.update { listOf(newApp) + it }
    }

    fun submitPaymentProof(transactionId: String, fileName: String = "receipt.jpg") {
        val newTransaction = TransactionItem(
            id = "tx-${System.currentTimeMillis()}",
            date = "Today",
            type = "Monthly Contribution",
            amount = "+5,000 ETB",
            isPositive = true,
            isSuccess = true
        )
        _transactions.update { listOf(newTransaction) + it }
    }

    fun remindMember(memberId: String) {
        // Toggle or send reminder action
    }

    fun approveApplication(appId: String) {
        _applications.update { list ->
            list.map { if (it.id == appId) it.copy(status = "Approved") else it }
        }
    }
}
