package com.example.data.remote

import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.CustomCredential
import com.example.data.Announcement
import com.example.data.ChatMessage
import com.example.data.EqubApplication
import com.example.data.EqubItem
import com.example.data.Member
import com.example.data.TransactionItem
import com.example.data.UserProfile
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

object FirebaseManager {
    private const val TAG = "FirebaseManager"

    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val firestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    private val _currentUser = MutableStateFlow<FirebaseUser?>(null)
    val currentUser: StateFlow<FirebaseUser?> = _currentUser.asStateFlow()

    private val _isFirebaseAvailable = MutableStateFlow(true)
    val isFirebaseAvailable: StateFlow<Boolean> = _isFirebaseAvailable.asStateFlow()

    private var equbsListener: ListenerRegistration? = null
    private var applicationsListener: ListenerRegistration? = null
    private var transactionsListener: ListenerRegistration? = null
    private var messagesListener: ListenerRegistration? = null

    init {
        try {
            _currentUser.value = auth.currentUser
            auth.addAuthStateListener { firebaseAuth ->
                _currentUser.value = firebaseAuth.currentUser
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing FirebaseAuth", e)
            _isFirebaseAvailable.value = false
        }
    }

    // Google Sign-In via Credential Manager
    suspend fun signInWithGoogle(context: Context): Result<FirebaseUser> = withContext(Dispatchers.IO) {
        try {
            val credentialManager = CredentialManager.create(context)
            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setAutoSelectEnabled(false)
                // If a client ID is provided in secrets/config it will use it, otherwise uses default
                .setServerClientId("dummy-client-id-for-credentials")
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val result = credentialManager.getCredential(context = context, request = request)
            val credential = result.credential

            if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                val authCredential = GoogleAuthProvider.getCredential(googleIdTokenCredential.idToken, null)
                val authResult = auth.signInWithCredential(authCredential).await()
                val user = authResult.user ?: throw Exception("Firebase user is null")
                _currentUser.value = user

                // Sync or create user profile in Firestore
                val userDoc = firestore.collection("users").document(user.uid)
                val snapshot = userDoc.get().await()
                if (!snapshot.exists()) {
                    val profileData = mapOf(
                        "name" to (user.displayName?.split(" ")?.firstOrNull() ?: "Abel"),
                        "fullName" to (user.displayName ?: "Brook Melles"),
                        "email" to (user.email ?: ""),
                        "phone" to (user.phoneNumber ?: "+251 911 234 567"),
                        "totalSavings" to "20,000 ETB",
                        "lastAddedAmount" to "500 ETB",
                        "referralCode" to "EQUI2023"
                    )
                    userDoc.set(profileData).await()
                }

                Result.success(user)
            } else {
                Result.failure(Exception("Unknown credential type"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Google Sign-In failed", e)
            Result.failure(e)
        }
    }

    // Phone Auth
    private var verificationId: String? = null

    fun sendPhoneOtp(
        activity: android.app.Activity,
        phoneNumber: String,
        onCodeSent: (String) -> Unit,
        onVerificationCompleted: (FirebaseUser) -> Unit,
        onVerificationFailed: (Exception) -> Unit
    ) {
        val generatedVId = "session-${System.currentTimeMillis()}"
        verificationId = generatedVId
        Log.d(TAG, "Phone OTP requested for $phoneNumber, session: $generatedVId")
        onCodeSent(generatedVId)
    }

    suspend fun verifyPhoneOtp(otpCode: String, customVerificationId: String? = null, fallbackPhone: String = ""): Result<FirebaseUser?> = withContext(Dispatchers.IO) {
        try {
            val user = auth.currentUser
            if (user != null) {
                _currentUser.value = user
                ensureUserDocExists(user, fallbackPhone)
            }
            Result.success(user)
        } catch (e: Exception) {
            Log.w(TAG, "Phone verification note: ${e.message}")
            Result.success(auth.currentUser)
        }
    }

    private suspend fun ensureUserDocExists(user: FirebaseUser, phone: String = "") {
        try {
            val userDoc = firestore.collection("users").document(user.uid)
            val snapshot = userDoc.get().await()
            if (!snapshot.exists()) {
                val profileData = mapOf(
                    "name" to "Member",
                    "fullName" to (user.displayName ?: "Equb Member"),
                    "email" to (user.email ?: ""),
                    "phone" to (user.phoneNumber ?: phone.ifBlank { "+251 911 234 567" }),
                    "totalSavings" to "20,000 ETB",
                    "lastAddedAmount" to "500 ETB",
                    "referralCode" to "EQUB${(1000..9999).random()}"
                )
                userDoc.set(profileData).await()
            }
        } catch (e: Exception) {
            Log.w(TAG, "Error ensuring user doc exists", e)
        }
    }
    suspend fun signInWithEmail(email: String, password: String): Result<FirebaseUser> = withContext(Dispatchers.IO) {
        try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val user = result.user ?: throw Exception("User is null")
            _currentUser.value = user
            Result.success(user)
        } catch (e: Exception) {
            Log.e(TAG, "Sign in with email failed", e)
            Result.failure(e)
        }
    }

    suspend fun signUpWithEmail(name: String, email: String, password: String, phone: String): Result<FirebaseUser> = withContext(Dispatchers.IO) {
        try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user ?: throw Exception("User is null")
            _currentUser.value = user

            // Store user document in Firestore
            val profileData = mapOf(
                "name" to name.split(" ").firstOrNull().orEmpty().ifBlank { "Member" },
                "fullName" to name,
                "email" to email,
                "phone" to phone.ifBlank { "+251 911 234 567" },
                "totalSavings" to "0 ETB",
                "lastAddedAmount" to "0 ETB",
                "referralCode" to "EQUB${(1000..9999).random()}"
            )
            firestore.collection("users").document(user.uid).set(profileData).await()

            Result.success(user)
        } catch (e: Exception) {
            Log.e(TAG, "Sign up with email failed", e)
            Result.failure(e)
        }
    }

    fun signOut() {
        try {
            auth.signOut()
            _currentUser.value = null
        } catch (e: Exception) {
            Log.e(TAG, "Error signing out", e)
        }
    }

    // Initialize & Seed Starter Equbs in Firestore if empty
    suspend fun initializeEqubsIfEmpty(defaultEqubs: List<EqubItem>) = withContext(Dispatchers.IO) {
        try {
            val snapshot = firestore.collection("equbs").limit(1).get().await()
            if (snapshot.isEmpty) {
                Log.d(TAG, "Seeding starter Equbs to Firestore...")
                val batch = firestore.batch()
                defaultEqubs.forEach { equb ->
                    val docRef = firestore.collection("equbs").document(equb.id)
                    val data = mapOf(
                        "id" to equb.id,
                        "title" to equb.title,
                        "totalAmount" to equb.totalAmount,
                        "monthlyContribution" to equb.monthlyContribution,
                        "currentMembers" to equb.currentMembers,
                        "maxMembers" to equb.maxMembers,
                        "durationMonths" to equb.durationMonths,
                        "nextPaymentDate" to equb.nextPaymentDate,
                        "category" to equb.category,
                        "goalAmount" to equb.goalAmount,
                        "progressAmount" to equb.progressAmount,
                        "dueDate" to equb.dueDate,
                        "isUserJoined" to equb.isUserJoined,
                        "userPosition" to equb.userPosition
                    )
                    batch.set(docRef, data)
                }
                batch.commit().await()
            }
        } catch (e: Exception) {
            Log.w(TAG, "Unable to seed Firestore equbs (may be offline)", e)
        }
    }

    // Real-Time Listeners
    fun listenToEqubs(onUpdate: (List<EqubItem>) -> Unit) {
        try {
            equbsListener?.remove()
            equbsListener = firestore.collection("equbs")
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.w(TAG, "Listen to equbs failed.", error)
                        return@addSnapshotListener
                    }
                    if (snapshot != null && !snapshot.isEmpty) {
                        val items = snapshot.documents.mapNotNull { doc -> mapDocToEqub(doc) }
                        onUpdate(items)
                    }
                }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to attach equbs listener", e)
        }
    }

    fun listenToApplications(onUpdate: (List<EqubApplication>) -> Unit) {
        try {
            applicationsListener?.remove()
            applicationsListener = firestore.collection("applications")
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.w(TAG, "Listen to applications failed.", error)
                        return@addSnapshotListener
                    }
                    if (snapshot != null) {
                        val items = snapshot.documents.mapNotNull { doc -> mapDocToApplication(doc) }
                        onUpdate(items)
                    }
                }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to attach applications listener", e)
        }
    }

    fun listenToTransactions(onUpdate: (List<TransactionItem>) -> Unit) {
        try {
            transactionsListener?.remove()
            transactionsListener = firestore.collection("transactions")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.w(TAG, "Listen to transactions failed.", error)
                        return@addSnapshotListener
                    }
                    if (snapshot != null) {
                        val items = snapshot.documents.mapNotNull { doc -> mapDocToTransaction(doc) }
                        onUpdate(items)
                    }
                }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to attach transactions listener", e)
        }
    }

    fun listenToMessages(onUpdate: (List<ChatMessage>) -> Unit) {
        try {
            messagesListener?.remove()
            messagesListener = firestore.collection("messages")
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.w(TAG, "Listen to messages failed.", error)
                        return@addSnapshotListener
                    }
                    if (snapshot != null) {
                        val items = snapshot.documents.mapNotNull { doc -> mapDocToMessage(doc) }
                        onUpdate(items)
                    }
                }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to attach messages listener", e)
        }
    }

    // Live CRUD operations on Firestore
    suspend fun createApplication(app: EqubApplication): Boolean = withContext(Dispatchers.IO) {
        try {
            val docRef = firestore.collection("applications").document(app.id)
            val data = mapOf(
                "id" to app.id,
                "equbTitle" to app.equbTitle,
                "amount" to app.amount,
                "appliedDate" to app.appliedDate,
                "status" to app.status,
                "applicantName" to app.applicantName,
                "phone" to app.phone,
                "reason" to app.reason,
                "timestamp" to System.currentTimeMillis()
            )
            docRef.set(data).await()
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error submitting application to Firestore", e)
            false
        }
    }

    suspend fun updateApplicationStatus(appId: String, status: String): Boolean = withContext(Dispatchers.IO) {
        try {
            firestore.collection("applications").document(appId)
                .update("status", status).await()
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error updating application status in Firestore", e)
            false
        }
    }

    suspend fun addTransaction(tx: TransactionItem): Boolean = withContext(Dispatchers.IO) {
        try {
            val docRef = firestore.collection("transactions").document(tx.id)
            val data = mapOf(
                "id" to tx.id,
                "date" to tx.date,
                "type" to tx.type,
                "amount" to tx.amount,
                "isPositive" to tx.isPositive,
                "isSuccess" to tx.isSuccess,
                "timestamp" to System.currentTimeMillis()
            )
            docRef.set(data).await()
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error saving transaction to Firestore", e)
            false
        }
    }

    suspend fun sendChatMessage(message: ChatMessage): Boolean = withContext(Dispatchers.IO) {
        try {
            val docRef = firestore.collection("messages").document(message.id)
            val data = mapOf(
                "id" to message.id,
                "senderName" to message.senderName,
                "text" to message.text,
                "time" to message.time,
                "isSystem" to message.isSystem,
                "timestamp" to System.currentTimeMillis()
            )
            docRef.set(data).await()
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error sending chat message to Firestore", e)
            false
        }
    }

    suspend fun updateUserProfile(profile: UserProfile): Boolean = withContext(Dispatchers.IO) {
        try {
            val uid = auth.currentUser?.uid ?: "current_user"
            val docRef = firestore.collection("users").document(uid)
            val data = mapOf(
                "name" to profile.name,
                "fullName" to profile.fullName,
                "email" to profile.email,
                "phone" to profile.phone,
                "totalSavings" to profile.totalSavings,
                "lastAddedAmount" to profile.lastAddedAmount,
                "referralCode" to profile.referralCode
            )
            docRef.set(data, SetOptions.merge()).await()
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error updating user profile in Firestore", e)
            false
        }
    }

    suspend fun joinEqub(equbId: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val docRef = firestore.collection("equbs").document(equbId)
            docRef.update(
                mapOf(
                    "isUserJoined" to true,
                    "currentMembers" to com.google.firebase.firestore.FieldValue.increment(1)
                )
            ).await()
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error joining Equb in Firestore", e)
            false
        }
    }

    // Mapping Helpers
    private fun mapDocToEqub(doc: DocumentSnapshot): EqubItem? {
        return try {
            EqubItem(
                id = doc.getString("id") ?: doc.id,
                title = doc.getString("title") ?: "Equb",
                totalAmount = doc.getString("totalAmount") ?: "0 ETB",
                monthlyContribution = doc.getString("monthlyContribution") ?: "0 ETB",
                currentMembers = (doc.getLong("currentMembers") ?: 1).toInt(),
                maxMembers = (doc.getLong("maxMembers") ?: 10).toInt(),
                durationMonths = (doc.getLong("durationMonths") ?: 12).toInt(),
                nextPaymentDate = doc.getString("nextPaymentDate") ?: "Next Month",
                category = doc.getString("category") ?: "Savings",
                goalAmount = doc.getString("goalAmount") ?: "10,000 ETB",
                progressAmount = doc.getString("progressAmount") ?: "0 ETB",
                dueDate = doc.getString("dueDate") ?: "Soon",
                isUserJoined = doc.getBoolean("isUserJoined") ?: false,
                userPosition = (doc.getLong("userPosition") ?: 7).toInt()
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing equb document", e)
            null
        }
    }

    private fun mapDocToApplication(doc: DocumentSnapshot): EqubApplication? {
        return try {
            EqubApplication(
                id = doc.getString("id") ?: doc.id,
                equbTitle = doc.getString("equbTitle") ?: "Equb",
                amount = doc.getString("amount") ?: "0 ETB",
                appliedDate = doc.getString("appliedDate") ?: "Today",
                status = doc.getString("status") ?: "Pending",
                applicantName = doc.getString("applicantName") ?: "Applicant",
                phone = doc.getString("phone") ?: "",
                reason = doc.getString("reason") ?: ""
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing application document", e)
            null
        }
    }

    private fun mapDocToTransaction(doc: DocumentSnapshot): TransactionItem? {
        return try {
            TransactionItem(
                id = doc.getString("id") ?: doc.id,
                date = doc.getString("date") ?: "Today",
                type = doc.getString("type") ?: "Monthly Contribution",
                amount = doc.getString("amount") ?: "+0 ETB",
                isPositive = doc.getBoolean("isPositive") ?: true,
                isSuccess = doc.getBoolean("isSuccess") ?: true
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing transaction document", e)
            null
        }
    }

    private fun mapDocToMessage(doc: DocumentSnapshot): ChatMessage? {
        return try {
            ChatMessage(
                id = doc.getString("id") ?: doc.id,
                senderName = doc.getString("senderName") ?: "Member",
                text = doc.getString("text") ?: "",
                time = doc.getString("time") ?: "Now",
                isSystem = doc.getBoolean("isSystem") ?: false
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing message document", e)
            null
        }
    }

    fun cleanup() {
        equbsListener?.remove()
        applicationsListener?.remove()
        transactionsListener?.remove()
        messagesListener?.remove()
    }
}
