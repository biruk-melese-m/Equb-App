package com.example.ui.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.example.data.EqubApplication
import com.example.data.EqubItem
import com.example.data.EqubRepository
import com.example.data.Member
import com.example.ui.components.EqubBottomNavBar
import com.example.ui.components.EqubTab
import com.example.ui.screens.*

sealed class Screen {
    object Splash : Screen()
    object Onboarding : Screen()
    object Login : Screen()
    object SignUp : Screen()
    object OtpVerification : Screen()
    object SetPassword : Screen()
    object MainDashboard : Screen()
    data class EqubDetails(val equb: EqubItem) : Screen()
    data class ApplyInEqub(val equb: EqubItem) : Screen()
    object ApplicationSubmitted : Screen()
    object MyApplications : Screen()
    object ApplicationApproved : Screen()
    object EqubJoinedOverview : Screen()
    object ActiveEqub : Screen()
    object CurrentRoundStatus : Screen()
    object EqubMembersList : Screen()
    data class MemberProfileDetail(val member: Member) : Screen()
    object EqubPaymentSchedule : Screen()
    object AllTransactions : Screen()
    object MyPaymentHistory : Screen()
    object PayoutHistory : Screen()
    object SubmitPaymentProof : Screen()
    object PaymentPendingReview : Screen()
    object PaymentApprovedSuccess : Screen()
    object MessagesAnnouncements : Screen()
    object HelpSupport : Screen()
    object EqubRules : Screen()
    object AboutEqub : Screen()
    object InviteShare : Screen()
}

@Composable
fun EqubApp() {
    var screenStack by remember { mutableStateOf(listOf<Screen>(Screen.Splash)) }
    var currentTab by remember { mutableStateOf(EqubTab.HOME) }
    val snackbarHostState = remember { SnackbarHostState() }

    val currentScreen = screenStack.lastOrNull() ?: Screen.MainDashboard

    fun navigateTo(screen: Screen) {
        screenStack = screenStack + screen
    }

    fun popBack() {
        if (screenStack.size > 1) {
            screenStack = screenStack.dropLast(1)
        }
    }

    fun replaceRoot(screen: Screen) {
        screenStack = listOf(screen)
    }

    // Hardware back press handler
    BackHandler(enabled = screenStack.size > 1) {
        popBack()
    }

    val isAuthFlow = currentScreen is Screen.Splash ||
            currentScreen is Screen.Onboarding ||
            currentScreen is Screen.Login ||
            currentScreen is Screen.SignUp ||
            currentScreen is Screen.OtpVerification ||
            currentScreen is Screen.SetPassword

    val equbs by EqubRepository.equbs.collectAsState()
    val members by EqubRepository.members.collectAsState()
    val defaultEqub = equbs.first()
    val defaultMember = members.first()

    if (isAuthFlow) {
        when (currentScreen) {
            is Screen.Splash -> {
                SplashScreen(
                    onSplashFinished = { replaceRoot(Screen.Onboarding) }
                )
            }
            is Screen.Onboarding -> {
                OnboardingScreen(
                    onFinishOnboarding = { replaceRoot(Screen.Login) }
                )
            }
            is Screen.Login -> {
                LoginScreen(
                    onLoginSuccess = { replaceRoot(Screen.MainDashboard) },
                    onNavigateToSignUp = { navigateTo(Screen.SignUp) },
                    onForgotPassword = { navigateTo(Screen.OtpVerification) }
                )
            }
            is Screen.SignUp -> {
                SignUpScreen(
                    onSignUpSuccess = { navigateTo(Screen.OtpVerification) },
                    onNavigateToLogin = { popBack() },
                    onBack = { popBack() }
                )
            }
            is Screen.OtpVerification -> {
                OtpVerificationScreen(
                    onVerifySuccess = { navigateTo(Screen.SetPassword) },
                    onBack = { popBack() }
                )
            }
            is Screen.SetPassword -> {
                SetPasswordScreen(
                    onContinue = { replaceRoot(Screen.MainDashboard) },
                    onBack = { popBack() }
                )
            }
            else -> {}
        }
    } else {
        // Main App Shell with Bottom Navigation for root tabs
        val isRootTabScreen = currentScreen is Screen.MainDashboard

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            snackbarHost = { SnackbarHost(snackbarHostState) },
            bottomBar = {
                if (isRootTabScreen) {
                    EqubBottomNavBar(
                        selectedTab = currentTab,
                        onTabSelected = { tab ->
                            currentTab = tab
                        }
                    )
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(if (isRootTabScreen) innerPadding else androidx.compose.foundation.layout.PaddingValues())
            ) {
                if (isRootTabScreen) {
                    when (currentTab) {
                        EqubTab.HOME -> {
                            HomeDashboardScreen(
                                onSelectEqub = { equb -> navigateTo(Screen.EqubDetails(equb)) },
                                onNavigateToActiveEqub = { navigateTo(Screen.ActiveEqub) },
                                onNavigateToSubmitPayment = { navigateTo(Screen.SubmitPaymentProof) },
                                onNavigateToDiscover = { currentTab = EqubTab.EXPLORE },
                                onNavigateToMessages = { navigateTo(Screen.MessagesAnnouncements) }
                            )
                        }
                        EqubTab.EXPLORE -> {
                            DiscoverEqubsScreen(
                                onSelectEqub = { equb -> navigateTo(Screen.EqubDetails(equb)) }
                            )
                        }
                        EqubTab.MY_EQUBS -> {
                            ActiveEqubScreen(
                                onViewCycle = { navigateTo(Screen.CurrentRoundStatus) },
                                onViewMembers = { navigateTo(Screen.EqubMembersList) },
                                onViewSchedule = { navigateTo(Screen.EqubPaymentSchedule) }
                            )
                        }
                        EqubTab.PAYMENTS -> {
                            AllPaymentTransactionsScreen(
                                onBack = { currentTab = EqubTab.HOME }
                            )
                        }
                        EqubTab.PROFILE -> {
                            ProfileSettingsScreen(
                                onNavigateToRules = { navigateTo(Screen.EqubRules) },
                                onNavigateToAbout = { navigateTo(Screen.AboutEqub) },
                                onNavigateToHelp = { navigateTo(Screen.HelpSupport) },
                                onNavigateToInvite = { navigateTo(Screen.InviteShare) },
                                onLogout = { replaceRoot(Screen.Login) }
                            )
                        }
                    }
                } else {
                    when (currentScreen) {
                        is Screen.EqubDetails -> {
                            EqubDetailsScreen(
                                equb = currentScreen.equb,
                                onApplyNow = { navigateTo(Screen.ApplyInEqub(currentScreen.equb)) },
                                onBack = { popBack() }
                            )
                        }
                        is Screen.ApplyInEqub -> {
                            EqubApplicationFormScreen(
                                equb = currentScreen.equb,
                                onSubmit = { navigateTo(Screen.ApplicationSubmitted) },
                                onBack = { popBack() }
                            )
                        }
                        is Screen.ApplicationSubmitted -> {
                            ApplicationSubmittedScreen(
                                onViewMyApplications = { navigateTo(Screen.MyApplications) }
                            )
                        }
                        is Screen.MyApplications -> {
                            MyApplicationsScreen(
                                onSelectApplication = { app ->
                                    if (app.status == "Approved") {
                                        navigateTo(Screen.ApplicationApproved)
                                    } else {
                                        // Demo approval on click
                                        EqubRepository.approveApplication(app.id)
                                        navigateTo(Screen.ApplicationApproved)
                                    }
                                },
                                onBack = { popBack() }
                            )
                        }
                        is Screen.ApplicationApproved -> {
                            ApplicationApprovedScreen(
                                onGoToMyEqub = { navigateTo(Screen.EqubJoinedOverview) },
                                onBack = { popBack() }
                            )
                        }
                        is Screen.EqubJoinedOverview -> {
                            EqubJoinedScreen(
                                onGoToMyEqub = {
                                    currentTab = EqubTab.MY_EQUBS
                                    replaceRoot(Screen.MainDashboard)
                                },
                                onBack = { popBack() }
                            )
                        }
                        is Screen.ActiveEqub -> {
                            ActiveEqubScreen(
                                onViewCycle = { navigateTo(Screen.CurrentRoundStatus) },
                                onViewMembers = { navigateTo(Screen.EqubMembersList) },
                                onViewSchedule = { navigateTo(Screen.EqubPaymentSchedule) },
                                onBack = { popBack() }
                            )
                        }
                        is Screen.CurrentRoundStatus -> {
                            CurrentRoundStatusScreen(
                                onNavigateToMembers = { navigateTo(Screen.EqubMembersList) },
                                onNavigateToSchedule = { navigateTo(Screen.EqubPaymentSchedule) },
                                onBack = { popBack() }
                            )
                        }
                        is Screen.EqubMembersList -> {
                            EqubMembersListScreen(
                                onSelectMember = { member -> navigateTo(Screen.MemberProfileDetail(member)) },
                                onBack = { popBack() }
                            )
                        }
                        is Screen.MemberProfileDetail -> {
                            MemberProfileDetailScreen(
                                member = currentScreen.member,
                                onSendMessage = { navigateTo(Screen.MessagesAnnouncements) },
                                onBack = { popBack() }
                            )
                        }
                        is Screen.EqubPaymentSchedule -> {
                            EqubPaymentScheduleScreen(
                                onBack = { popBack() }
                            )
                        }
                        is Screen.AllTransactions -> {
                            AllPaymentTransactionsScreen(
                                onBack = { popBack() }
                            )
                        }
                        is Screen.MyPaymentHistory -> {
                            MyPaymentHistoryScreen(
                                onBack = { popBack() }
                            )
                        }
                        is Screen.PayoutHistory -> {
                            PayoutHistoryScreen(
                                onViewAll = { navigateTo(Screen.AllTransactions) },
                                onBack = { popBack() }
                            )
                        }
                        is Screen.SubmitPaymentProof -> {
                            SubmitPaymentProofScreen(
                                onSubmitSuccess = { navigateTo(Screen.PaymentPendingReview) },
                                onBack = { popBack() }
                            )
                        }
                        is Screen.PaymentPendingReview -> {
                            PaymentPendingReviewScreen(
                                onBackToDashboard = {
                                    currentTab = EqubTab.HOME
                                    replaceRoot(Screen.MainDashboard)
                                }
                            )
                        }
                        is Screen.PaymentApprovedSuccess -> {
                            PaymentApprovedSuccessScreen(
                                onViewPaymentHistory = { navigateTo(Screen.MyPaymentHistory) },
                                onBack = { popBack() }
                            )
                        }
                        is Screen.MessagesAnnouncements -> {
                            MessagesAnnouncementsScreen(
                                onBack = { popBack() }
                            )
                        }
                        is Screen.HelpSupport -> {
                            HelpSupportScreen(
                                onNavigateToRules = { navigateTo(Screen.EqubRules) },
                                onBack = { popBack() }
                            )
                        }
                        is Screen.EqubRules -> {
                            EqubRulesScreen(
                                onConfirm = { popBack() },
                                onBack = { popBack() }
                            )
                        }
                        is Screen.AboutEqub -> {
                            AboutEqubScreen(
                                onBack = { popBack() }
                            )
                        }
                        is Screen.InviteShare -> {
                            InviteShareScreen(
                                onBack = { popBack() }
                            )
                        }
                        else -> {}
                    }
                }
            }
        }
    }
}
