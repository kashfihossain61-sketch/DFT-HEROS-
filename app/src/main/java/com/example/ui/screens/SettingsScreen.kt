package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.spring
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AppUser
import com.example.ui.theme.*
import com.example.viewmodel.TournamentViewModel
import kotlinx.coroutines.launch

// Simple Translation Utility for Settings Screen
object SettingsTrans {
    fun t(key: String, lang: String): String {
        val dict = when(lang) {
            "Bangla" -> mapOf(
                "title" to "সেটিংস",
                "edit_profile" to "প্রোফাইল সংশোধন",
                "edit_profile_desc" to "আপনার নাম, ইমেল এবং প্রোফাইল ছবি আপডেট করুন",
                "change_password" to "পাসওয়ার্ড পরিবর্তন",
                "change_password_desc" to "আপনার অ্যাকাউন্ট সুরক্ষিত রাখতে পাসওয়ার্ড পরিবর্তন করুন",
                "preferences" to "অ্যাপ্লিকেশন পছন্দসমূহ",
                "dark_light_mode" to "ডার্ক/লাইট মোড",
                "dark_light_mode_desc" to "আপনার পছন্দের চোখের স্বাচ্ছন্দ্য অনুযায়ী থিম বাছুন",
                "language" to "ভাষা (Language)",
                "language_desc" to "ইংরেজি অথবা বাংলা ভাষা নির্বাচন করুন",
                "notifications" to "বিজ্ঞপ্তি সেটিংস",
                "notifications_desc" to "খেলার আপডেট ও সিস্টেম নোটিফিকেশন চালু/বন্ধ করুন",
                "support_info" to "সহযোগিতা এবং তথ্য",
                "help_support" to "সাহায্য ও সহযোগিতা",
                "help_support_desc" to "প্রায়শই জিজ্ঞাসিত প্রশ্নাবলী এবং অভিযোগ টিকিট",
                "about_app" to "অ্যাপ্লিকেশন পরিচিতি",
                "about_app_desc" to "সংস্করণ তথ্য এবং ডেভেলপার পরিচিতি",
                "session" to "অ্যাকাউন্ট সেশন",
                "logout" to "লগ আউট",
                "logout_desc" to "আপনার অ্যাকাউন্ট থেকে নিরাপদে লগ আউট করুন",
                "save_changes" to "সংরক্ষণ করুন",
                "name" to "পূর্ণ নাম",
                "email" to "ইমেল ঠিকানা",
                "avatar_url" to "অবতার ছবির ইউআরএল",
                "current_password" to "বর্তমান পাসওয়ার্ড",
                "new_password" to "নতুন পাসওয়ার্ড",
                "confirm_password" to "নতুন পাসওয়ার্ড নিশ্চিত করুন",
                "profile_success" to "প্রোফাইল সফলভাবে আপডেট করা হয়েছে!",
                "password_error_match" to "নতুন পাসওয়ার্ড দুটি মেলেনি",
                "password_error_empty" to "সবগুলো ঘর পূরণ করা আবশ্যক",
                "password_success" to "পাসওয়ার্ড সফলভাবে পরিবর্তন করা হয়েছে!",
                "faq" to "প্রায়শই জিজ্ঞাসিত প্রশ্নাবলী (FAQ)",
                "submit_ticket" to "একটি সাপোর্ট টিকিট জমা দিন",
                "ticket_subject" to "বিষয়",
                "ticket_desc" to "আপনার সমস্যার বিস্তারিত বর্ণনা দিন",
                "ticket_success" to "সাপোর্ট টিকিট সফলভাবে জমা দেওয়া হয়েছে! আইডি: #",
                "app_version" to "অ্যাপ সংস্করণ",
                "developer_credits" to "ডেভেলপার ক্রেডিট",
                "theme_dark" to "ডার্ক মোড",
                "theme_light" to "লাইট মোড",
                "notif_on" to "নোটিফিকেশন চালু",
                "notif_off" to "নোটিফিকেশন বন্ধ",
                "confirm_logout" to "আপনি কি নিশ্চিতভাবে লগ আউট করতে চান?",
                "cancel" to "বাতিল",
                "yes" to "হ্যাঁ"
            )
            else -> mapOf(
                "title" to "Settings",
                "edit_profile" to "Edit Profile",
                "edit_profile_desc" to "Update your name, email and avatar photo",
                "change_password" to "Change Password",
                "change_password_desc" to "Secure your account with a unique password",
                "preferences" to "App Preferences",
                "dark_light_mode" to "Dark/Light Mode",
                "dark_light_mode_desc" to "Select your preferred visual atmosphere",
                "language" to "Language (ভাষা)",
                "language_desc" to "Choose between English and Bangla language",
                "notifications" to "Notifications On/Off",
                "notifications_desc" to "Manage match reminders and broadcast updates",
                "support_info" to "Support & Information",
                "help_support" to "Help & Support",
                "help_support_desc" to "Frequently asked questions and support ticket",
                "about_app" to "About App",
                "about_app_desc" to "View build version, licensing and credits",
                "session" to "Account Session",
                "logout" to "Logout",
                "logout_desc" to "Securely sign out of your account",
                "save_changes" to "Save Changes",
                "name" to "Full Name",
                "email" to "Email Address",
                "avatar_url" to "Avatar Image URL",
                "current_password" to "Current Password",
                "new_password" to "New Password",
                "confirm_password" to "Confirm New Password",
                "profile_success" to "Profile updated successfully!",
                "password_error_match" to "Passwords do not match!",
                "password_error_empty" to "All fields are required",
                "password_success" to "Password updated successfully!",
                "faq" to "Frequently Asked Questions",
                "submit_ticket" to "Submit a Support Ticket",
                "ticket_subject" to "Subject / Category",
                "ticket_desc" to "Describe your problem in detail",
                "ticket_success" to "Support ticket submitted successfully! ID: #",
                "app_version" to "App Version",
                "developer_credits" to "Developer Credits",
                "theme_dark" to "Dark Theme",
                "theme_light" to "Light Theme",
                "notif_on" to "Notifications Enabled",
                "notif_off" to "Notifications Disabled",
                "confirm_logout" to "Are you sure you want to log out?",
                "cancel" to "Cancel",
                "yes" to "Logout"
            )
        }
        return dict[key] ?: key
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: TournamentViewModel,
    onNavigateBack: () -> Unit,
    onLogout: () -> Unit
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    val language by viewModel.language.collectAsState()
    val notificationsEnabled by viewModel.notificationsEnabled.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    var showLogoutDialog by remember { mutableStateOf(false) }

    // Navigation sub-state within settings (expanding card style)
    var expandedSection by remember { mutableStateOf<String?>(null) }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = SettingsTrans.t("title", language),
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 20.sp
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("settings_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = NavySurface
                )
            )
        },
        containerColor = NavyDeep
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Profile Card
            item {
                ProfileHeaderCard(
                    user = currentUser,
                    language = language
                )
            }

            // Category 1: Account Settings
            item {
                SettingsCategoryHeader(title = if (language == "Bangla") "অ্যাকাউন্ট সেটিংস" else "Account Settings")
            }

            // Edit Profile Card
            item {
                ExpandableSettingsCard(
                    title = SettingsTrans.t("edit_profile", language),
                    description = SettingsTrans.t("edit_profile_desc", language),
                    icon = Icons.Default.Person,
                    isExpanded = expandedSection == "edit_profile",
                    onToggle = {
                        expandedSection = if (expandedSection == "edit_profile") null else "edit_profile"
                    },
                    testTag = "edit_profile_card"
                ) {
                    EditProfilePanel(
                        currentUser = currentUser,
                        language = language,
                        onSave = { name, email, avatarUrl ->
                            viewModel.updateProfile(name, email, avatarUrl) {
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar(SettingsTrans.t("profile_success", language))
                                }
                                expandedSection = null
                            }
                        }
                    )
                }
            }

            // Change Password Card
            item {
                ExpandableSettingsCard(
                    title = SettingsTrans.t("change_password", language),
                    description = SettingsTrans.t("change_password_desc", language),
                    icon = Icons.Default.Lock,
                    isExpanded = expandedSection == "change_password",
                    onToggle = {
                        expandedSection = if (expandedSection == "change_password") null else "change_password"
                    },
                    testTag = "change_password_card"
                ) {
                    ChangePasswordPanel(
                        language = language,
                        onSubmit = { currentPass, newPass, confirmPass ->
                            if (newPass != confirmPass) {
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar(SettingsTrans.t("password_error_match", language))
                                }
                            } else if (currentPass.isEmpty() || newPass.isEmpty()) {
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar(SettingsTrans.t("password_error_empty", language))
                                }
                            } else {
                                viewModel.changePassword(currentPass, newPass) { success, msg ->
                                    coroutineScope.launch {
                                        if (success) {
                                            snackbarHostState.showSnackbar(SettingsTrans.t("password_success", language))
                                            expandedSection = null
                                        } else {
                                            snackbarHostState.showSnackbar(msg)
                                        }
                                    }
                                }
                            }
                        }
                    )
                }
            }

            // Category 2: App Preferences
            item {
                SettingsCategoryHeader(title = SettingsTrans.t("preferences", language))
            }

            // Dark/Light Mode Row Selector Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(4.dp, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = NavySurface),
                    border = BorderStroke(1.dp, BorderColor)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(CyberBlue.copy(alpha = 0.1f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (isDarkMode) Icons.Default.DarkMode else Icons.Default.LightMode,
                                    contentDescription = "Theme",
                                    tint = NeonCyan
                                )
                            }
                            Column {
                                Text(
                                    text = SettingsTrans.t("dark_light_mode", language),
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary,
                                    fontSize = 15.sp
                                )
                                Text(
                                    text = SettingsTrans.t("dark_light_mode_desc", language),
                                    color = TextSecondary,
                                    fontSize = 12.sp
                                )
                            }
                        }
                        
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = if (isDarkMode) SettingsTrans.t("theme_dark", language) else SettingsTrans.t("theme_light", language),
                                color = NeonCyan,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Switch(
                                checked = isDarkMode,
                                onCheckedChange = { viewModel.setDarkMode(it) },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = CyberBlue,
                                    uncheckedThumbColor = TextSecondary,
                                    uncheckedTrackColor = NavySurfaceLight
                                ),
                                modifier = Modifier.testTag("theme_switch")
                            )
                        }
                    }
                }
            }

            // Language Selection Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(4.dp, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = NavySurface),
                    border = BorderStroke(1.dp, BorderColor)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(CyberBlue.copy(alpha = 0.1f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Language,
                                    contentDescription = "Language",
                                    tint = NeonCyan
                                )
                            }
                            Column {
                                Text(
                                    text = SettingsTrans.t("language", language),
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary,
                                    fontSize = 15.sp
                                )
                                Text(
                                    text = SettingsTrans.t("language_desc", language),
                                    color = TextSecondary,
                                    fontSize = 12.sp
                                )
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            listOf("English", "Bangla").forEach { langOption ->
                                val selected = language == langOption
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (selected) CyberBlue else NavySurfaceLight)
                                        .border(
                                            1.dp,
                                            if (selected) NeonCyan else BorderColor,
                                            RoundedCornerShape(12.dp)
                                        )
                                        .clickable { viewModel.setLanguage(langOption) }
                                        .padding(vertical = 12.dp)
                                        .testTag("lang_chip_$langOption"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = if (langOption == "Bangla") "বাংলা" else "English",
                                        fontWeight = FontWeight.Bold,
                                        color = if (selected) Color.White else TextPrimary,
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Notifications Switch Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(4.dp, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = NavySurface),
                    border = BorderStroke(1.dp, BorderColor)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(CyberBlue.copy(alpha = 0.1f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (notificationsEnabled) Icons.Default.NotificationsActive else Icons.Default.NotificationsOff,
                                    contentDescription = "Notifications",
                                    tint = NeonCyan
                                )
                            }
                            Column {
                                Text(
                                    text = SettingsTrans.t("notifications", language),
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary,
                                    fontSize = 15.sp
                                )
                                Text(
                                    text = SettingsTrans.t("notifications_desc", language),
                                    color = TextSecondary,
                                    fontSize = 12.sp
                                )
                            }
                        }
                        
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = if (notificationsEnabled) SettingsTrans.t("notif_on", language) else SettingsTrans.t("notif_off", language),
                                color = if (notificationsEnabled) NeonCyan else TextSecondary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Switch(
                                checked = notificationsEnabled,
                                onCheckedChange = { viewModel.setNotificationsEnabled(it) },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = CyberBlue,
                                    uncheckedThumbColor = TextSecondary,
                                    uncheckedTrackColor = NavySurfaceLight
                                ),
                                modifier = Modifier.testTag("notifications_switch")
                            )
                        }
                    }
                }
            }

            // Category 3: Support & Info
            item {
                SettingsCategoryHeader(title = SettingsTrans.t("support_info", language))
            }

            // Help & Support Card
            item {
                ExpandableSettingsCard(
                    title = SettingsTrans.t("help_support", language),
                    description = SettingsTrans.t("help_support_desc", language),
                    icon = Icons.Default.Info,
                    isExpanded = expandedSection == "help_support",
                    onToggle = {
                        expandedSection = if (expandedSection == "help_support") null else "help_support"
                    },
                    testTag = "help_support_card"
                ) {
                    HelpSupportPanel(
                        language = language,
                        onSubmitTicket = { subject, desc ->
                            val ticketId = (1000..9999).random()
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar(SettingsTrans.t("ticket_success", language) + ticketId)
                            }
                            expandedSection = null
                        }
                    )
                }
            }

            // About App Card
            item {
                ExpandableSettingsCard(
                    title = SettingsTrans.t("about_app", language),
                    description = SettingsTrans.t("about_app_desc", language),
                    icon = Icons.Default.SportsFootball,
                    isExpanded = expandedSection == "about_app",
                    onToggle = {
                        expandedSection = if (expandedSection == "about_app") null else "about_app"
                    },
                    testTag = "about_app_card"
                ) {
                    AboutAppPanel(language = language)
                }
            }

            // Category 4: Session Control
            item {
                SettingsCategoryHeader(title = SettingsTrans.t("session", language))
            }

            // Logout Action Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(4.dp, RoundedCornerShape(16.dp))
                        .clickable { showLogoutDialog = true },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = NavySurface),
                    border = BorderStroke(1.dp, BorderColor.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(Color.Red.copy(alpha = 0.1f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ExitToApp,
                                    contentDescription = "Logout",
                                    tint = Color.Red
                                )
                            }
                            Column {
                                Text(
                                    text = SettingsTrans.t("logout", language),
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Red,
                                    fontSize = 15.sp
                                )
                                Text(
                                    text = SettingsTrans.t("logout_desc", language),
                                    color = TextSecondary,
                                    fontSize = 12.sp
                                )
                            }
                        }
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowRight,
                            contentDescription = null,
                            tint = TextSecondary
                        )
                    }
                }
            }
        }
    }

    // Logout Confirmation Dialog
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            containerColor = NavySurface,
            title = {
                Text(
                    text = SettingsTrans.t("logout", language),
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            },
            text = {
                Text(
                    text = SettingsTrans.t("confirm_logout", language),
                    color = TextPrimary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutDialog = false
                        viewModel.logout()
                        onLogout()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                    modifier = Modifier.testTag("confirm_logout_button")
                ) {
                    Text(SettingsTrans.t("yes", language), color = Color.White)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showLogoutDialog = false }
                ) {
                    Text(SettingsTrans.t("cancel", language), color = TextSecondary)
                }
            }
        )
    }
}

// ======================== SUB-COMPONENTS ========================

@Composable
fun ProfileHeaderCard(
    user: AppUser?,
    language: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(6.dp, RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(CyberBlue, NavySurfaceLight)
                    )
                )
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Avatar Placeholder Frame with beautiful gradient border
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.1f))
                        .border(2.dp, GoldTrophy, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.SportsSoccer,
                        contentDescription = "Avatar",
                        tint = GoldTrophy,
                        modifier = Modifier.size(36.dp)
                    )
                }

                // Profile Info Text
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = user?.name ?: "DFT Captain",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                    Text(
                        text = user?.email ?: "captain@dft-heros.com",
                        fontSize = 13.sp,
                        color = TextPrimary.copy(alpha = 0.8f)
                    )
                    
                    // Role Badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(GoldTrophy.copy(alpha = 0.15f))
                            .border(1.dp, GoldTrophy.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = user?.role?.uppercase() ?: "CAPTAIN",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            color = GoldTrophy
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsCategoryHeader(title: String) {
    Text(
        text = title.uppercase(),
        fontSize = 11.sp,
        fontWeight = FontWeight.Black,
        color = NeonCyan,
        letterSpacing = 1.5.sp,
        modifier = Modifier
            .padding(horizontal = 4.dp)
            .padding(top = 8.dp)
    )
}

@Composable
fun ExpandableSettingsCard(
    title: String,
    description: String,
    icon: ImageVector,
    isExpanded: Boolean,
    onToggle: () -> Unit,
    testTag: String = "",
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(16.dp))
            .clickable { onToggle() }
            .testTag(testTag),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = NavySurface),
        border = BorderStroke(1.dp, if (isExpanded) NeonCyan else BorderColor)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(CyberBlue.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = title,
                            tint = NeonCyan
                        )
                    }
                    Column {
                        Text(
                            text = title,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            fontSize = 15.sp
                        )
                        Text(
                            text = description,
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }
                }
                Icon(
                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = TextSecondary
                )
            }

            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically(spring()) + fadeIn(),
                exit = shrinkVertically(spring()) + fadeOut()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(NavySurfaceLight.copy(alpha = 0.5f))
                        .padding(16.dp)
                ) {
                    content()
                }
            }
        }
    }
}

// ---------------- Panel 1: Edit Profile Panel ----------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfilePanel(
    currentUser: AppUser?,
    language: String,
    onSave: (String, String, String) -> Unit
) {
    var name by remember { mutableStateOf(currentUser?.name ?: "") }
    var email by remember { mutableStateOf(currentUser?.email ?: "") }
    var avatarUrl by remember { mutableStateOf(currentUser?.avatarUrl ?: "") }

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text(SettingsTrans.t("name", language)) },
            modifier = Modifier.fillMaxWidth().testTag("edit_name_input"),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = NavySurface,
                unfocusedContainerColor = NavySurface,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            ),
            singleLine = true
        )

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text(SettingsTrans.t("email", language)) },
            modifier = Modifier.fillMaxWidth().testTag("edit_email_input"),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = NavySurface,
                unfocusedContainerColor = NavySurface,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            ),
            singleLine = true
        )

        OutlinedTextField(
            value = avatarUrl,
            onValueChange = { avatarUrl = it },
            label = { Text(SettingsTrans.t("avatar_url", language)) },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = NavySurface,
                unfocusedContainerColor = NavySurface,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            ),
            singleLine = true
        )

        Button(
            onClick = { onSave(name, email, avatarUrl) },
            colors = ButtonDefaults.buttonColors(containerColor = CyberBlue),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("save_profile_button"),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.Save, contentDescription = null, tint = Color.White)
            Spacer(modifier = Modifier.width(8.dp))
            Text(SettingsTrans.t("save_changes", language), color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}

// ---------------- Panel 2: Change Password Panel ----------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangePasswordPanel(
    language: String,
    onSubmit: (String, String, String) -> Unit
) {
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var showPasswords by remember { mutableStateOf(false) }

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = currentPassword,
            onValueChange = { currentPassword = it },
            label = { Text(SettingsTrans.t("current_password", language)) },
            modifier = Modifier.fillMaxWidth().testTag("current_password_input"),
            visualTransformation = if (showPasswords) VisualTransformation.None else PasswordVisualTransformation(),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = NavySurface,
                unfocusedContainerColor = NavySurface,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            ),
            singleLine = true
        )

        OutlinedTextField(
            value = newPassword,
            onValueChange = { newPassword = it },
            label = { Text(SettingsTrans.t("new_password", language)) },
            modifier = Modifier.fillMaxWidth().testTag("new_password_input"),
            visualTransformation = if (showPasswords) VisualTransformation.None else PasswordVisualTransformation(),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = NavySurface,
                unfocusedContainerColor = NavySurface,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            ),
            singleLine = true
        )

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text(SettingsTrans.t("confirm_password", language)) },
            modifier = Modifier.fillMaxWidth().testTag("confirm_password_input"),
            visualTransformation = if (showPasswords) VisualTransformation.None else PasswordVisualTransformation(),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = NavySurface,
                unfocusedContainerColor = NavySurface,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            ),
            singleLine = true
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (language == "Bangla") "পাসওয়ার্ড দেখান" else "Show Passwords",
                color = TextSecondary,
                fontSize = 12.sp,
                modifier = Modifier.padding(end = 8.dp)
            )
            Checkbox(
                checked = showPasswords,
                onCheckedChange = { showPasswords = it },
                colors = CheckboxDefaults.colors(checkedColor = CyberBlue, uncheckedColor = TextSecondary)
            )
        }

        Button(
            onClick = { onSubmit(currentPassword, newPassword, confirmPassword) },
            colors = ButtonDefaults.buttonColors(containerColor = CyberBlue),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("submit_password_button"),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.LockOpen, contentDescription = null, tint = Color.White)
            Spacer(modifier = Modifier.width(8.dp))
            Text(SettingsTrans.t("change_password", language), color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}

// ---------------- Panel 3: Help & Support Panel ----------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpSupportPanel(
    language: String,
    onSubmitTicket: (String, String) -> Unit
) {
    var subject by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    val faqs = if (language == "Bangla") listOf(
        "কীভাবে টিম রেজিস্ট্রেশন করতে পারি?" to "টুর্নামেন্ট ডিটেইলস পেজে গিয়ে 'রজিস্টার টিম' বাটনে ক্লিক করে টিম মেম্বারদের বিবরণ দিন।",
        "কীভাবে গ্রুপ চ্যাটে বার্তা পাঠাবো?" to "গ্রুপ ট্যাবে আপনার গ্রুপ নির্বাচন করে লবিতে ঢুকে সরাসরি চ্যাট বার্তা পাঠান।",
        "কোনো সমস্যার জন্য অ্যাডমিনের সাথে কীভাবে যোগাযোগ করব?" to "এখানেই আপনার অভিযোগের টিকিট জমা দিন, আমাদের টিম অতি শীঘ্রই তা সমাধান করবে।"
    ) else listOf(
        "How can I register my team?" to "Go to the Tournament Details screen, click the 'Register Team' button, and specify your teammate details.",
        "How do I send chat messages?" to "In the Groups tab, select your group's Chat Lobby, type your message, and hit send.",
        "How do I contact an Admin?" to "You can easily submit a support ticket below, and our staff will respond as soon as possible."
    )

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        // FAQ Section
        Text(
            text = SettingsTrans.t("faq", language),
            fontWeight = FontWeight.Bold,
            color = NeonCyan,
            fontSize = 14.sp
        )

        faqs.forEach { (q, a) ->
            var qExpanded by remember { mutableStateOf(false) }
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = NavySurface),
                border = BorderStroke(1.dp, BorderColor),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { qExpanded = !qExpanded }
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "❓ $q",
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            fontSize = 13.sp,
                            modifier = Modifier.weight(1f)
                        )
                        Icon(
                            imageVector = if (qExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = null,
                            tint = TextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    if (qExpanded) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = a,
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        Divider(color = BorderColor, modifier = Modifier.padding(vertical = 4.dp))

        // Ticket Submission Section
        Text(
            text = SettingsTrans.t("submit_ticket", language),
            fontWeight = FontWeight.Bold,
            color = NeonCyan,
            fontSize = 14.sp
        )

        OutlinedTextField(
            value = subject,
            onValueChange = { subject = it },
            label = { Text(SettingsTrans.t("ticket_subject", language)) },
            modifier = Modifier.fillMaxWidth().testTag("ticket_subject_input"),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = NavySurface,
                unfocusedContainerColor = NavySurface,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            ),
            singleLine = true
        )

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text(SettingsTrans.t("ticket_desc", language)) },
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .testTag("ticket_desc_input"),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = NavySurface,
                unfocusedContainerColor = NavySurface,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            )
        )

        Button(
            onClick = {
                if (subject.isNotEmpty() && description.isNotEmpty()) {
                    onSubmitTicket(subject, description)
                    subject = ""
                    description = ""
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = CyberBlue),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("submit_ticket_button"),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.SupportAgent, contentDescription = null, tint = Color.White)
            Spacer(modifier = Modifier.width(8.dp))
            Text(SettingsTrans.t("submit_ticket", language), color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}

// ---------------- Panel 4: About App Panel ----------------
@Composable
fun AboutAppPanel(language: String) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.radialGradient(
                            colors = listOf(NeonCyan, CyberBlue)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.SportsSoccer,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(36.dp)
                )
            }
            Column {
                Text(
                    text = "DFT-HEROS",
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    fontSize = 18.sp,
                    letterSpacing = (-0.5).sp
                )
                Text(
                    text = SettingsTrans.t("app_details", language),
                    color = TextSecondary,
                    fontSize = 11.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = SettingsTrans.t("app_version", language),
                color = TextSecondary,
                fontSize = 12.sp
            )
            Text(
                text = "v1.2.0-Football-Pro",
                fontWeight = FontWeight.Bold,
                color = NeonCyan,
                fontSize = 12.sp
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = SettingsTrans.t("developer_credits", language),
                color = TextSecondary,
                fontSize = 12.sp
            )
            Text(
                text = "Google AI Studio Developer",
                fontWeight = FontWeight.Bold,
                color = NeonCyan,
                fontSize = 12.sp
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = if (language == "Bangla") {
                "DFT-Heros হলো একটি আধুনিক টুর্নামেন্ট ও টিম ম্যানেজমেন্ট সিস্টেম যা ফুটবলপ্রেমী খেলোয়াড়দের জন্য ডিজাইন করা হয়েছে। এটি রিয়েল-টাইম ফায়ারবেস ক্লাউড ডেটাবেস সিঙ্ক সাপোর্ট করে এবং নির্ভরযোগ্য খেলার স্কোর নিশ্চিত করে।"
            } else {
                "DFT-Heros is a modern tournament and squad organizer app custom-built for football athletes. Operating on bidirectional Firestore replication, it bridges real-time score analytics and automated group schedules flawlessly."
            },
            color = TextPrimary.copy(alpha = 0.9f),
            fontSize = 12.sp,
            lineHeight = 16.sp
        )
    }
}
