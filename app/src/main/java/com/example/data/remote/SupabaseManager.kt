package com.example.data.remote

import android.util.Log
import com.example.BuildConfig
import com.example.data.Announcement
import com.example.data.ChatMessage
import com.example.data.EqubApplication
import com.example.data.EqubItem
import com.example.data.Member
import com.example.data.PaymentRound
import com.example.data.TransactionItem
import com.example.data.UserProfile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object SupabaseManager {
    private const val TAG = "SupabaseManager"

    // Supabase project credentials (injected via BuildConfig / .env, with safe project fallbacks)
    val SUPABASE_URL: String = try {
        BuildConfig.SUPABASE_URL.ifBlank { "https://mzhhrkwnrrhclbtiszfv.supabase.co" }
    } catch (_: Exception) {
        "https://mzhhrkwnrrhclbtiszfv.supabase.co"
    }

    val SUPABASE_ANON_KEY: String = try {
        BuildConfig.SUPABASE_ANON_KEY.ifBlank { "sb_publishable_DX0A-0wK3t027pwVsAKiSg_nniWUANw" }
    } catch (_: Exception) {
        "sb_publishable_DX0A-0wK3t027pwVsAKiSg_nniWUANw"
    }

    private val JSON_MEDIA_TYPE = "application/json; charset=utf-8".toMediaType()

    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .build()

    private val _isSupabaseConnected = MutableStateFlow(false)
    val isSupabaseConnected: StateFlow<Boolean> = _isSupabaseConnected.asStateFlow()

    private val _currentUserId = MutableStateFlow("user-default")
    val currentUserId: StateFlow<String> = _currentUserId.asStateFlow()

    private val _currentUserPhone = MutableStateFlow("+251 911 234 567")
    val currentUserPhone: StateFlow<String> = _currentUserPhone.asStateFlow()

    init {
        Log.d(TAG, "Initializing Supabase with URL: $SUPABASE_URL")
    }

    fun setAuthenticatedUser(userId: String, phone: String) {
        _currentUserId.value = userId
        val formattedPhone = if (phone.startsWith("+")) phone else if (phone.startsWith("0")) "+251 ${phone.substring(1)}" else "+251 $phone"
        _currentUserPhone.value = formattedPhone
    }

    private fun buildRequest(endpoint: String, method: String = "GET", bodyJson: String? = null, preferReturn: Boolean = false): Request {
        val url = "$SUPABASE_URL/rest/v1/$endpoint"
        val builder = Request.Builder()
            .url(url)
            .addHeader("apikey", SUPABASE_ANON_KEY)
            .addHeader("Authorization", "Bearer $SUPABASE_ANON_KEY")
            .addHeader("Content-Type", "application/json")

        if (preferReturn) {
            builder.addHeader("Prefer", "return=representation")
        } else if (method == "POST") {
            builder.addHeader("Prefer", "resolution=merge-duplicates")
        }

        when (method) {
            "GET" -> builder.get()
            "POST" -> builder.post((bodyJson ?: "{}").toRequestBody(JSON_MEDIA_TYPE))
            "PATCH" -> builder.patch((bodyJson ?: "{}").toRequestBody(JSON_MEDIA_TYPE))
            "DELETE" -> builder.delete()
        }

        return builder.build()
    }

    // ==========================================
    // 1. PROFILES
    // ==========================================

    suspend fun fetchProfile(userId: String): UserProfile? = withContext(Dispatchers.IO) {
        try {
            val request = buildRequest("profiles?id=eq.$userId&select=*")
            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    _isSupabaseConnected.value = true
                    val responseBody = response.body?.string() ?: "[]"
                    val array = JSONArray(responseBody)
                    if (array.length() > 0) {
                        val obj = array.getJSONObject(0)
                        return@withContext UserProfile(
                            name = obj.optString("full_name", "Brook Melles").split(" ").firstOrNull() ?: "Brook",
                            fullName = obj.optString("full_name", "Brook Melles"),
                            phone = obj.optString("phone", "+251 911 234 567"),
                            email = obj.optString("email", "brook.melles@equb.app"),
                            totalSavings = obj.optString("total_savings", "20,000 ETB"),
                            lastAddedAmount = obj.optString("last_added_amount", "500 ETB"),
                            referralCode = obj.optString("referral_code", "EQUB2024")
                        )
                    }
                } else {
                    Log.w(TAG, "fetchProfile status: ${response.code} ${response.message}")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching profile from Supabase", e)
        }
        null
    }

    suspend fun upsertProfile(userId: String, profile: UserProfile): Boolean = withContext(Dispatchers.IO) {
        try {
            val json = JSONObject().apply {
                put("id", userId)
                put("phone", profile.phone)
                put("full_name", profile.fullName)
                put("email", profile.email)
                put("total_savings", profile.totalSavings)
                put("last_added_amount", profile.lastAddedAmount)
                put("referral_code", profile.referralCode)
            }
            val request = buildRequest("profiles", method = "POST", bodyJson = json.toString())
            client.newCall(request).execute().use { response ->
                _isSupabaseConnected.value = response.isSuccessful
                Log.d(TAG, "upsertProfile response code: ${response.code}")
                return@withContext response.isSuccessful
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error upserting profile in Supabase", e)
            false
        }
    }

    // ==========================================
    // 2. EQUBS
    // ==========================================

    suspend fun fetchEqubs(): List<EqubItem> = withContext(Dispatchers.IO) {
        try {
            val request = buildRequest("equbs?select=*&order=created_at.asc")
            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    _isSupabaseConnected.value = true
                    val responseBody = response.body?.string() ?: "[]"
                    val array = JSONArray(responseBody)
                    val list = mutableListOf<EqubItem>()
                    for (i in 0 until array.length()) {
                        val obj = array.getJSONObject(i)
                        list.add(
                            EqubItem(
                                id = obj.optString("id", "equb-$i"),
                                title = obj.optString("title", "Equb Circle"),
                                totalAmount = obj.optString("target_amount", "500,000 ETB"),
                                monthlyContribution = obj.optString("monthly_deposit", "5,000 ETB"),
                                currentMembers = obj.optInt("current_members", 10),
                                maxMembers = obj.optInt("total_members", 12),
                                durationMonths = obj.optInt("total_rounds", 12),
                                nextPaymentDate = obj.optString("next_draw_date", "June 25, 2024"),
                                category = obj.optString("category", "Savings"),
                                goalAmount = obj.optString("target_amount", "500,000 ETB"),
                                progressAmount = obj.optString("progress_amount", "5,000 ETB"),
                                dueDate = obj.optString("next_draw_date", "June 25, 2024"),
                                isUserJoined = obj.optBoolean("is_user_joined", false),
                                userPosition = obj.optInt("user_position", 3)
                            )
                        )
                    }
                    return@withContext list
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching equbs from Supabase", e)
        }
        emptyList()
    }

    suspend fun upsertEqub(equb: EqubItem): Boolean = withContext(Dispatchers.IO) {
        try {
            val json = JSONObject().apply {
                put("id", equb.id)
                put("title", equb.title)
                put("category", equb.category)
                put("target_amount", equb.totalAmount)
                put("monthly_deposit", equb.monthlyContribution)
                put("current_members", equb.currentMembers)
                put("total_members", equb.maxMembers)
                put("total_rounds", equb.durationMonths)
                put("next_draw_date", equb.nextPaymentDate)
                put("progress_amount", equb.progressAmount)
                put("is_active", true)
            }
            val request = buildRequest("equbs", method = "POST", bodyJson = json.toString())
            client.newCall(request).execute().use { response ->
                _isSupabaseConnected.value = response.isSuccessful
                return@withContext response.isSuccessful
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error upserting equb", e)
            false
        }
    }

    // ==========================================
    // 3. APPLICATIONS
    // ==========================================

    suspend fun fetchApplications(): List<EqubApplication> = withContext(Dispatchers.IO) {
        try {
            val request = buildRequest("applications?select=*&order=created_at.desc")
            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val array = JSONArray(response.body?.string() ?: "[]")
                    val list = mutableListOf<EqubApplication>()
                    for (i in 0 until array.length()) {
                        val obj = array.getJSONObject(i)
                        list.add(
                            EqubApplication(
                                id = obj.optString("id", "app-$i"),
                                equbTitle = obj.optString("equb_title", "Equb Circle"),
                                amount = obj.optString("amount", "20,000 ETB"),
                                appliedDate = obj.optString("applied_date", "Today"),
                                status = obj.optString("status", "Pending"),
                                applicantName = obj.optString("applicant_name", "Brook Melles"),
                                phone = obj.optString("phone", "+251 911 234 567"),
                                reason = obj.optString("reason", "Savings plan")
                            )
                        )
                    }
                    return@withContext list
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching applications", e)
        }
        emptyList()
    }

    suspend fun createApplication(app: EqubApplication): Boolean = withContext(Dispatchers.IO) {
        try {
            val json = JSONObject().apply {
                put("id", app.id)
                put("equb_title", app.equbTitle)
                put("amount", app.amount)
                put("applied_date", app.appliedDate)
                put("status", app.status)
                put("applicant_name", app.applicantName)
                put("phone", app.phone)
                put("reason", app.reason)
            }
            val request = buildRequest("applications", method = "POST", bodyJson = json.toString())
            client.newCall(request).execute().use { response ->
                return@withContext response.isSuccessful
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error creating application", e)
            false
        }
    }

    // ==========================================
    // 4. TRANSACTIONS & PAYMENTS
    // ==========================================

    suspend fun fetchTransactions(): List<TransactionItem> = withContext(Dispatchers.IO) {
        try {
            val request = buildRequest("payments?select=*&order=created_at.desc")
            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val array = JSONArray(response.body?.string() ?: "[]")
                    val list = mutableListOf<TransactionItem>()
                    for (i in 0 until array.length()) {
                        val obj = array.getJSONObject(i)
                        list.add(
                            TransactionItem(
                                id = obj.optString("id", "tx-$i"),
                                date = obj.optString("date", "Today"),
                                type = obj.optString("type", "Monthly Contribution"),
                                amount = obj.optString("amount", "2,000 ETB"),
                                isPositive = obj.optBoolean("is_positive", false),
                                isSuccess = obj.optBoolean("is_success", true)
                            )
                        )
                    }
                    return@withContext list
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching transactions", e)
        }
        emptyList()
    }

    suspend fun createPayment(tx: TransactionItem, equbId: String? = null): Boolean = withContext(Dispatchers.IO) {
        try {
            val json = JSONObject().apply {
                put("id", tx.id)
                if (equbId != null) put("equb_id", equbId)
                put("user_id", _currentUserId.value)
                put("type", tx.type)
                put("amount", tx.amount)
                put("date", tx.date)
                put("transaction_id", "TXN-${System.currentTimeMillis()}")
                put("payment_method", "Telebirr")
                put("is_positive", tx.isPositive)
                put("is_success", tx.isSuccess)
                put("status", if (tx.isSuccess) "Approved" else "Pending")
            }
            val request = buildRequest("payments", method = "POST", bodyJson = json.toString())
            client.newCall(request).execute().use { response ->
                return@withContext response.isSuccessful
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error recording payment", e)
            false
        }
    }

    // ==========================================
    // 5. CHAT MESSAGES
    // ==========================================

    suspend fun fetchMessages(equbId: String = "equb-1"): List<ChatMessage> = withContext(Dispatchers.IO) {
        try {
            val request = buildRequest("messages?equb_id=eq.$equbId&select=*&order=created_at.asc")
            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val array = JSONArray(response.body?.string() ?: "[]")
                    val list = mutableListOf<ChatMessage>()
                    for (i in 0 until array.length()) {
                        val obj = array.getJSONObject(i)
                        list.add(
                            ChatMessage(
                                id = obj.optString("id", "msg-$i"),
                                senderName = obj.optString("sender_name", "Member"),
                                text = obj.optString("content", ""),
                                time = obj.optString("time", "Just now"),
                                isSystem = obj.optBoolean("is_announcement", false)
                            )
                        )
                    }
                    return@withContext list
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching messages", e)
        }
        emptyList()
    }

    suspend fun sendMessage(equbId: String, senderName: String, text: String, isAnnouncement: Boolean = false): Boolean = withContext(Dispatchers.IO) {
        try {
            val json = JSONObject().apply {
                put("equb_id", equbId)
                put("sender_id", _currentUserId.value)
                put("sender_name", senderName)
                put("content", text)
                put("time", java.text.SimpleDateFormat("hh:mm a", java.util.Locale.US).format(java.util.Date()))
                put("is_announcement", isAnnouncement)
            }
            val request = buildRequest("messages", method = "POST", bodyJson = json.toString())
            client.newCall(request).execute().use { response ->
                return@withContext response.isSuccessful
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error sending message", e)
            false
        }
    }

    // ==========================================
    // 6. MEMBERS
    // ==========================================

    suspend fun fetchMembers(equbId: String = "equb-1"): List<Member> = withContext(Dispatchers.IO) {
        try {
            val request = buildRequest("equb_members?equb_id=eq.$equbId&select=*")
            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val array = JSONArray(response.body?.string() ?: "[]")
                    val list = mutableListOf<Member>()
                    for (i in 0 until array.length()) {
                        val obj = array.getJSONObject(i)
                        list.add(
                            Member(
                                id = obj.optString("id", "mem-$i"),
                                name = obj.optString("user_name", "Member"),
                                totalContributions = obj.optString("total_contributions", "6,000 ETB"),
                                rotationPosition = obj.optString("payout_month", "#${i + 1}"),
                                phone = obj.optString("phone", "+251 911 000 000"),
                                email = obj.optString("email", "member@equb.app"),
                                isPaid = obj.optBoolean("is_paid", true),
                                isPending = !obj.optBoolean("is_paid", true)
                            )
                        )
                    }
                    return@withContext list
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching members", e)
        }
        emptyList()
    }
}
