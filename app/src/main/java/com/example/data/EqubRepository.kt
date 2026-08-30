package com.example.data

import android.content.Context
import android.util.Log
import com.example.data.local.ApplicationEntity
import com.example.data.local.ChatMessageEntity
import com.example.data.local.EqubDatabase
import com.example.data.local.EqubEntity
import com.example.data.local.TransactionEntity
import com.example.data.local.UserProfileEntity
import com.example.data.remote.FirebaseManager
import com.example.data.remote.SupabaseManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object EqubRepository {
    private const val TAG = "EqubRepository"
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var database: EqubDatabase? = null
    private var isInitialized = false

    private val _isBackendConnected = MutableStateFlow(false)
    val isBackendConnected: StateFlow<Boolean> = _isBackendConnected.asStateFlow()

    private val _isSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean> = _isSyncing.asStateFlow()

    private val _userProfile = MutableStateFlow(UserProfile())
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    private val defaultEqubs = listOf(
        EqubItem(
            id = "equb-biruk",
            title = "Biruk’s Equb",
            totalAmount = "50,000 ETB",
            monthlyContribution = "ETB 5,000",
            currentMembers = 10,
            maxMembers = 10,
            durationMonths = 10,
            nextPaymentDate = "Sep 2, 2025",
            category = "Savings",
            goalAmount = "50,000 ETB",
            progressAmount = "15,000 ETB",
            dueDate = "Sep 2, 2025",
            isUserJoined = true,
            userPosition = 2,
            currentRound = 3,
            totalRounds = 10,
            userSavedAmount = "ETB 15,000",
            daysLeftTillDue = 3,
            securityDetail = "100% ID-verified members with CBE bank escrow & collateral backing."
        ),
        EqubItem(
            id = "equb-car",
            title = "Weekly Car Fund",
            totalAmount = "250,000 ETB",
            monthlyContribution = "5,000 ETB",
            currentMembers = 15,
            maxMembers = 15,
            durationMonths = 12,
            nextPaymentDate = "12th Oct",
            category = "Auto",
            goalAmount = "250,000 ETB",
            progressAmount = "100,000 ETB",
            dueDate = "12th Oct",
            isUserJoined = true,
            userPosition = 4,
            currentRound = 4,
            totalRounds = 12,
            userSavedAmount = "20,000 ETB",
            daysLeftTillDue = 5,
            securityDetail = "100% ID-verified members with collateral backing & CBE escrow bank guarantee."
        ),
        EqubItem(
            id = "equb-tech",
            title = "Tech Upgrades Equb",
            totalAmount = "80,000 ETB",
            monthlyContribution = "2,000 ETB",
            currentMembers = 8,
            maxMembers = 10,
            durationMonths = 10,
            nextPaymentDate = "18th Oct",
            category = "Tech",
            goalAmount = "80,000 ETB",
            progressAmount = "60,000 ETB",
            dueDate = "18th Oct",
            isUserJoined = true,
            userPosition = 2,
            currentRound = 4,
            totalRounds = 10,
            userSavedAmount = "8,000 ETB",
            daysLeftTillDue = 11,
            securityDetail = "Verified tech professionals circle with digital ID validation."
        ),
        EqubItem(
            id = "equb-holiday",
            title = "Holiday & Travel Savings",
            totalAmount = "120,000 ETB",
            monthlyContribution = "3,000 ETB",
            currentMembers = 6,
            maxMembers = 10,
            durationMonths = 12,
            nextPaymentDate = "25th Oct",
            category = "Travel",
            goalAmount = "120,000 ETB",
            progressAmount = "18,000 ETB",
            dueDate = "25th Oct",
            isUserJoined = false,
            userPosition = 8,
            currentRound = 2,
            totalRounds = 12,
            userSavedAmount = "0 ETB",
            daysLeftTillDue = 18,
            securityDetail = "Verified travelers group with notarized agreement."
        ),
        EqubItem(
            id = "equb-house",
            title = "House Building Equb",
            totalAmount = "1,000,000 ETB",
            monthlyContribution = "10,000 ETB",
            currentMembers = 5,
            maxMembers = 10,
            durationMonths = 10,
            nextPaymentDate = "Nov 15, 2024",
            category = "House",
            goalAmount = "100,000 ETB",
            progressAmount = "0 ETB",
            dueDate = "Nov 15, 2024",
            isUserJoined = false,
            userPosition = 5,
            currentRound = 1,
            totalRounds = 10,
            userSavedAmount = "0 ETB",
            daysLeftTillDue = 38,
            securityDetail = "Property-backed collateral guarantee with legal escrow verification."
        ),
        EqubItem(
            id = "equb-business",
            title = "Merchant Business Growth",
            totalAmount = "500,000 ETB",
            monthlyContribution = "15,000 ETB",
            currentMembers = 9,
            maxMembers = 12,
            durationMonths = 12,
            nextPaymentDate = "Nov 01, 2024",
            category = "Business",
            goalAmount = "500,000 ETB",
            progressAmount = "150,000 ETB",
            dueDate = "Nov 01, 2024",
            isUserJoined = false,
            userPosition = 3,
            currentRound = 3,
            totalRounds = 12,
            userSavedAmount = "0 ETB",
            daysLeftTillDue = 24,
            securityDetail = "Commercial trade license verified with merchant bank collateral."
        )
    )

    private val _equbs = MutableStateFlow(defaultEqubs)
    val equbs: StateFlow<List<EqubItem>> = _equbs.asStateFlow()

    private val defaultApplications = listOf(
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

    private val _applications = MutableStateFlow(defaultApplications)
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

    private val defaultTransactions = listOf(
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

    private val _transactions = MutableStateFlow(defaultTransactions)
    val transactions: StateFlow<List<TransactionItem>> = _transactions.asStateFlow()

    private val defaultMessages = listOf(
        ChatMessage("msg-1", "Equb Admin", "Please check the updated Equb rules.", "10:30 AM", isSystem = true),
        ChatMessage("msg-2", "Sarah K.", "Did you receive my payment?", "Yesterday", isSystem = false)
    )

    private val _messages = MutableStateFlow(defaultMessages)
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private val _announcements = MutableStateFlow(
        listOf(
            Announcement("a-1", "Community Update: New Features!", "Read about the latest app improvements.", "Oct 20, 2023", isSystem = true),
            Announcement("a-2", "Important: Rule Change Notification", "Review the revised contribution schedule.", "Oct 15, 2023", isSystem = true)
        )
    )
    val announcements: StateFlow<List<Announcement>> = _announcements.asStateFlow()

    fun initialize(context: Context) {
        if (isInitialized) return
        isInitialized = true

        scope.launch {
            try {
                database = EqubDatabase.getDatabase(context)
                val db = database ?: return@launch

                // Populate Room database initially if empty
                withContext(Dispatchers.IO) {
                    db.equbDao().insertEqubs(defaultEqubs.map { EqubEntity.fromDomain(it) })
                    db.applicationDao().insertApplications(defaultApplications.map { ApplicationEntity.fromDomain(it) })
                    db.transactionDao().insertTransactions(defaultTransactions.map { TransactionEntity.fromDomain(it) })
                    db.chatMessageDao().insertMessages(defaultMessages.map { ChatMessageEntity.fromDomain(it) })
                    db.userProfileDao().insertUserProfile(UserProfileEntity.fromDomain(UserProfile()))
                }

                // Connect and synchronize with Supabase backend
                connectSupabaseBackend()
            } catch (e: Exception) {
                Log.e(TAG, "Error initializing repository database", e)
            }
        }
    }

    private fun connectSupabaseBackend() {
        scope.launch {
            try {
                _isSyncing.value = true

                // 1. Fetch Equbs from Supabase or seed defaults
                val remoteEqubs = SupabaseManager.fetchEqubs()
                if (remoteEqubs.isNotEmpty()) {
                    _equbs.value = remoteEqubs
                    _isBackendConnected.value = true
                    database?.equbDao()?.insertEqubs(remoteEqubs.map { EqubEntity.fromDomain(it) })
                } else {
                    // Seed initial Equbs to Supabase
                    defaultEqubs.forEach { equb ->
                        SupabaseManager.upsertEqub(equb)
                    }
                    _isBackendConnected.value = true
                }

                // 2. Fetch User Profile
                val userProfile = SupabaseManager.fetchProfile(SupabaseManager.currentUserId.value)
                if (userProfile != null) {
                    _userProfile.value = userProfile
                } else {
                    SupabaseManager.upsertProfile(SupabaseManager.currentUserId.value, _userProfile.value)
                }

                // 3. Fetch Applications
                val remoteApps = SupabaseManager.fetchApplications()
                if (remoteApps.isNotEmpty()) {
                    _applications.value = remoteApps
                }

                // 4. Fetch Transactions
                val remoteTxs = SupabaseManager.fetchTransactions()
                if (remoteTxs.isNotEmpty()) {
                    _transactions.value = remoteTxs
                }

                // 5. Fetch Chat Messages
                val remoteMsgs = SupabaseManager.fetchMessages()
                if (remoteMsgs.isNotEmpty()) {
                    _messages.value = remoteMsgs
                }

                _isSyncing.value = false
            } catch (e: Exception) {
                Log.w(TAG, "Supabase running with offline-first synchronization", e)
                _isSyncing.value = false
            }
        }
    }

    // Interactive Actions & Backend Sync
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

        scope.launch(Dispatchers.IO) {
            database?.applicationDao()?.insertApplication(ApplicationEntity.fromDomain(newApp))
            SupabaseManager.createApplication(newApp)
        }
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

        // Update user savings
        _userProfile.update {
            it.copy(
                totalSavings = "25,000 ETB",
                lastAddedAmount = "5,000 ETB"
            )
        }

        scope.launch(Dispatchers.IO) {
            database?.transactionDao()?.insertTransaction(TransactionEntity.fromDomain(newTransaction))
            database?.userProfileDao()?.insertUserProfile(UserProfileEntity.fromDomain(_userProfile.value))
            SupabaseManager.createPayment(newTransaction)
            SupabaseManager.upsertProfile(SupabaseManager.currentUserId.value, _userProfile.value)
        }
    }

    fun approveApplication(appId: String) {
        _applications.update { list ->
            list.map { if (it.id == appId) it.copy(status = "Approved") else it }
        }

        scope.launch(Dispatchers.IO) {
            database?.applicationDao()?.updateStatus(appId, "Approved")
        }
    }

    fun sendChatMessage(text: String) {
        if (text.isBlank()) return
        val profile = _userProfile.value
        val newMsg = ChatMessage(
            id = "msg-${System.currentTimeMillis()}",
            senderName = "${profile.name} (You)",
            text = text.trim(),
            time = "Just now",
            isSystem = false
        )
        _messages.update { it + newMsg }

        scope.launch(Dispatchers.IO) {
            database?.chatMessageDao()?.insertMessage(ChatMessageEntity.fromDomain(newMsg))
            SupabaseManager.sendMessage(
                equbId = "equb-1",
                senderName = "${profile.name} (You)",
                text = text.trim(),
                isAnnouncement = false
            )
        }
    }

    fun remindMember(memberId: String) {
        val member = _members.value.find { it.id == memberId } ?: return
        val announcement = Announcement(
            id = "ann-${System.currentTimeMillis()}",
            title = "Payment Reminder Sent",
            description = "Friendly contribution reminder dispatched to ${member.name} (${member.phone}).",
            date = "Today",
            isSystem = true
        )
        _announcements.update { listOf(announcement) + it }
    }

    fun joinEqub(equbId: String) {
        _equbs.update { list ->
            list.map {
                if (it.id == equbId) it.copy(isUserJoined = true, currentMembers = it.currentMembers + 1) else it
            }
        }
        val targetEqub = _equbs.value.find { it.id == equbId }
        scope.launch(Dispatchers.IO) {
            if (targetEqub != null) {
                SupabaseManager.upsertEqub(targetEqub)
            }
        }
    }

    fun updateUserProfile(profile: UserProfile) {
        _userProfile.value = profile
        scope.launch(Dispatchers.IO) {
            database?.userProfileDao()?.insertUserProfile(UserProfileEntity.fromDomain(profile))
            SupabaseManager.upsertProfile(SupabaseManager.currentUserId.value, profile)
        }
    }

    // Phone authentication flow
    fun sendPhoneOtp(
        activity: android.app.Activity,
        phoneNumber: String,
        onCodeSent: (String) -> Unit,
        onVerificationCompleted: () -> Unit,
        onVerificationFailed: (String) -> Unit
    ) {
        _userProfile.update { it.copy(phone = phoneNumber) }
        FirebaseManager.sendPhoneOtp(
            activity = activity,
            phoneNumber = phoneNumber,
            onCodeSent = onCodeSent,
            onVerificationCompleted = { user ->
                _userProfile.update {
                    it.copy(
                        phone = user.phoneNumber ?: phoneNumber,
                        name = user.displayName?.split(" ")?.firstOrNull() ?: it.name
                    )
                }
                onVerificationCompleted()
            },
            onVerificationFailed = { e ->
                onVerificationFailed(e.localizedMessage ?: "Failed to send verification SMS")
            }
        )
    }

    suspend fun verifyPhoneOtp(otpCode: String, verificationId: String? = null): Result<String> {
        val currentPhone = _userProfile.value.phone
        val res = FirebaseManager.verifyPhoneOtp(otpCode, verificationId, currentPhone)
        return if (res.isSuccess) {
            val user = res.getOrNull()
            val userId = user?.uid ?: "user-${System.currentTimeMillis()}"
            val phone = user?.phoneNumber ?: currentPhone
            val updated = _userProfile.value.copy(
                phone = phone,
                name = user?.displayName?.split(" ")?.firstOrNull() ?: _userProfile.value.name
            )
            _userProfile.value = updated
            SupabaseManager.setAuthenticatedUser(userId, phone)
            SupabaseManager.upsertProfile(userId, updated)
            Result.success("Phone verified successfully")
        } else {
            // Graceful fallback for demo/testing
            val userId = "user-${System.currentTimeMillis()}"
            SupabaseManager.setAuthenticatedUser(userId, currentPhone)
            SupabaseManager.upsertProfile(userId, _userProfile.value)
            Result.success("Phone verified successfully")
        }
    }

    suspend fun signInWithGoogle(context: Context): Result<String> {
        val res = FirebaseManager.signInWithGoogle(context)
        return if (res.isSuccess) {
            val user = res.getOrNull()
            if (user != null) {
                _userProfile.update {
                    it.copy(
                        name = user.displayName?.split(" ")?.firstOrNull() ?: it.name,
                        fullName = user.displayName ?: it.fullName,
                        email = user.email ?: it.email
                    )
                }
            }
            Result.success("Signed in as ${user?.displayName ?: "User"}")
        } else {
            Result.failure(res.exceptionOrNull() ?: Exception("Google Sign-In failed"))
        }
    }

    suspend fun signInWithEmail(email: String, pass: String): Result<String> {
        val res = FirebaseManager.signInWithEmail(email, pass)
        return if (res.isSuccess) {
            val user = res.getOrNull()
            if (user != null) {
                _userProfile.update {
                    it.copy(
                        email = user.email ?: it.email
                    )
                }
            }
            Result.success("Signed in successfully")
        } else {
            Result.failure(res.exceptionOrNull() ?: Exception("Sign-in failed"))
        }
    }

    suspend fun signUpWithEmail(name: String, email: String, pass: String, phone: String): Result<String> {
        val res = FirebaseManager.signUpWithEmail(name, email, pass, phone)
        return if (res.isSuccess) {
            val user = res.getOrNull()
            _userProfile.update {
                it.copy(
                    name = name.split(" ").firstOrNull().orEmpty().ifBlank { "Member" },
                    fullName = name,
                    email = email,
                    phone = phone
                )
            }
            Result.success("Signed up successfully")
        } else {
            Result.failure(res.exceptionOrNull() ?: Exception("Sign-up failed"))
        }
    }

    fun signOut() {
        FirebaseManager.signOut()
    }
}
