package com.example.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.*
import com.example.ui.theme.*
import com.example.viewmodel.TournamentViewModel
import com.example.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainDashboard(
    viewModel: TournamentViewModel,
    onNavigateToTournament: (String) -> Unit,
    onNavigateToChat: (String) -> Unit,
    onNavigateToAdmin: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onLogout: () -> Unit
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val isFirebaseActive by viewModel.isFirebaseActive.collectAsState()

    var selectedTab by remember { mutableStateOf(0) }
    var selectedMatchDetail by remember { mutableStateOf<Match?>(null) }
    var isAuctionOpen by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .background(NavyDeep)
                    .statusBarsPadding()
                    .padding(horizontal = 24.dp, vertical = 16.dp)
                    .fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (isFirebaseActive) "FIREBASE SECURE" else "OFFLINE DEMO MODE",
                            color = NeonCyan,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp
                        )
                        Text(
                            text = "DFT-HEROS",
                            color = Color.White,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = (-1).sp
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Admin Quick-Link Portal Icon Button
                        if (currentUser?.role == "Admin") {
                            IconButton(
                                onClick = onNavigateToAdmin,
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(GoldTrophy.copy(alpha = 0.15f))
                                    .border(1.dp, GoldTrophy.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AdminPanelSettings,
                                    contentDescription = "Admin Portal",
                                    tint = GoldTrophy,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        // Auction Arena Button
                        IconButton(
                            onClick = { isAuctionOpen = true },
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .background(NeonCyan.copy(alpha = 0.15f))
                                .border(1.dp, NeonCyan.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                                .testTag("open_auction_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Gavel,
                                contentDescription = "Auction Arena",
                                tint = NeonCyan,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        // Settings Button
                        IconButton(
                            onClick = onNavigateToSettings,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .background(CyberBlue.copy(alpha = 0.15f))
                                .border(1.dp, CyberBlue.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Settings",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        // Profile Info
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = currentUser?.name ?: "Captain",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = currentUser?.role ?: "Captain",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }

                        // Logout button / avatar style
                        IconButton(
                            onClick = {
                                viewModel.logout()
                                onLogout()
                            },
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .background(CyberBlue.copy(alpha = 0.2f))
                                .border(1.dp, CyberBlue.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                        ) {
                            Icon(
                                imageVector = Icons.Default.ExitToApp,
                                contentDescription = "Log Out",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        },
        bottomBar = {
            NavigationBar(
                containerColor = NavySurface,
                tonalElevation = 8.dp,
                modifier = Modifier.border(1.dp, BorderColor, RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            ) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(if (selectedTab == 0) Icons.Filled.EmojiEvents else Icons.Outlined.EmojiEvents, contentDescription = "Tournaments") },
                    label = { Text("Tournaments", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = NeonCyan,
                        indicatorColor = CyberBlue,
                        unselectedIconColor = TextSecondary,
                        unselectedTextColor = TextSecondary
                    )
                )

                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(if (selectedTab == 1) Icons.Filled.CalendarMonth else Icons.Outlined.CalendarMonth, contentDescription = "Schedules") },
                    label = { Text("Schedules", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = NeonCyan,
                        indicatorColor = CyberBlue,
                        unselectedIconColor = TextSecondary,
                        unselectedTextColor = TextSecondary
                    )
                )

                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = { Icon(if (selectedTab == 2) Icons.Filled.Forum else Icons.Outlined.Forum, contentDescription = "Groups") },
                    label = { Text("Groups", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = NeonCyan,
                        indicatorColor = CyberBlue,
                        unselectedIconColor = TextSecondary,
                        unselectedTextColor = TextSecondary
                    )
                )

                NavigationBarItem(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    icon = { Icon(if (selectedTab == 3) Icons.Filled.Leaderboard else Icons.Outlined.Leaderboard, contentDescription = "Standings") },
                    label = { Text("Standings", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = NeonCyan,
                        indicatorColor = CyberBlue,
                        unselectedIconColor = TextSecondary,
                        unselectedTextColor = TextSecondary
                    )
                )

                NavigationBarItem(
                    selected = selectedTab == 4,
                    onClick = { selectedTab = 4 },
                    icon = { Icon(if (selectedTab == 4) Icons.Filled.Person else Icons.Outlined.Person, contentDescription = "Profile") },
                    label = { Text("Profile", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = NeonCyan,
                        indicatorColor = CyberBlue,
                        unselectedIconColor = TextSecondary,
                        unselectedTextColor = TextSecondary
                    )
                )
            }
        },
        containerColor = NavyDeep
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                0 -> TournamentsTab(viewModel, onNavigateToTournament)
                1 -> SchedulesTab(viewModel, onMatchClick = { selectedMatchDetail = it })
                2 -> GroupsTab(viewModel, onNavigateToChat)
                3 -> StandingsTab(viewModel)
                4 -> PlayerProfileTab(viewModel)
            }

            if (selectedMatchDetail != null) {
                MatchDetailOverlay(
                    match = selectedMatchDetail!!,
                    viewModel = viewModel,
                    onDismiss = { selectedMatchDetail = null }
                )
            }

            if (isAuctionOpen) {
                AuctionDashboardScreen(
                    viewModel = viewModel,
                    onDismiss = { isAuctionOpen = false }
                )
            }
        }
    }
}

// ======================== TABS IMPLEMENTATION ========================

@Composable
fun TournamentsTab(
    viewModel: TournamentViewModel,
    onNavigateToTournament: (String) -> Unit
) {
    val tournaments by viewModel.tournaments.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Card
        item {
            val featuredId = tournaments.firstOrNull()?.id ?: "t_val_2026"
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clickable { onNavigateToTournament(featuredId) },
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Color(0xFF1D4ED8), Color(0xFF1E3A8A))
                            )
                        )
                        .padding(20.dp)
                ) {
                    // "Live" Badge floating at top right
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "LIVE",
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }

                    // Content Left
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Global Elites Series",
                                color = Color.White,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = (-0.5).sp
                            )
                            Text(
                                text = "Prize Pool: $50,000",
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        // Bottom Row: Overlapping Avatars and Register button
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            // Avatar stack
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy((-8).dp)
                            ) {
                                repeat(3) { index ->
                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .clip(CircleShape)
                                            .background(
                                                when (index) {
                                                    0 -> Color(0xFF334155)
                                                    1 -> Color(0xFF475569)
                                                    else -> Color(0xFF1E293B)
                                                }
                                            )
                                            .border(2.dp, Color(0xFF1D4ED8), CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Shield,
                                            contentDescription = null,
                                            tint = NeonCyan,
                                            modifier = Modifier.size(12.dp)
                                        )
                                    }
                                }
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(CyberBlue)
                                        .border(2.dp, Color(0xFF1D4ED8), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "+12",
                                        color = Color.White,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            // Register Button
                            Button(
                                onClick = { onNavigateToTournament(featuredId) },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                                shape = RoundedCornerShape(12.dp),
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                                modifier = Modifier.height(36.dp)
                            ) {
                                Text(
                                    text = "Register",
                                    color = Color(0xFF1E3A8A),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        // Stats Grid (Bento Style)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Match Schedule Card
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .height(110.dp)
                        .border(1.dp, BorderColor, RoundedCornerShape(24.dp)),
                    colors = CardDefaults.cardColors(containerColor = NavySurface),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Match Schedule",
                            color = NeonCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Column {
                            Text(
                                text = "Today, 20:00",
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "A-Heros vs X-Kings",
                                color = TextSecondary,
                                fontSize = 10.sp
                            )
                        }
                    }
                }

                // Points Table Card
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .height(110.dp)
                        .border(1.dp, BorderColor, RoundedCornerShape(24.dp)),
                    colors = CardDefaults.cardColors(containerColor = NavySurface),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Points Table",
                            color = NeonCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Row(
                            verticalAlignment = Alignment.Bottom,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "#1",
                                color = Color.White,
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Black,
                                modifier = Modifier.alignByBaseline()
                            )
                            Text(
                                text = "ALPHA SQUAD",
                                color = Color(0xFF34D399),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp,
                                modifier = Modifier.alignByBaseline()
                            )
                        }
                    }
                }
            }
        }

        item {
            Text(
                text = "Active & Upcoming Tournaments",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        }

        items(tournaments) { tournament ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, BorderColor, RoundedCornerShape(14.dp))
                    .clickable { onNavigateToTournament(tournament.id) },
                colors = CardDefaults.cardColors(containerColor = NavySurface),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = tournament.gameName,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = NeonCyan
                        )
                        // Status badge
                        val badgeColor = when (tournament.status) {
                            TournamentStatus.ONGOING -> Color(0xFF4CAF50)
                            TournamentStatus.UPCOMING -> CyberBlue
                            TournamentStatus.COMPLETED -> Color.Gray
                        }
                        Box(
                            modifier = Modifier
                                .background(badgeColor.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                                .border(1.dp, badgeColor, RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = tournament.status.name,
                                color = badgeColor,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = tournament.name,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = tournament.description,
                        fontSize = 12.sp,
                        color = TextSecondary,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Divider(color = BorderColor)

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("PRIZE POOL", fontSize = 9.sp, color = TextSecondary, fontWeight = FontWeight.Bold)
                            Text(tournament.prizePool, fontSize = 14.sp, color = GoldTrophy, fontWeight = FontWeight.Bold)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("TEAMS REGISTERED", fontSize = 9.sp, color = TextSecondary, fontWeight = FontWeight.Bold)
                            Text("${tournament.teamsCount} / ${tournament.maxTeams}", fontSize = 14.sp, color = TextPrimary, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = { onNavigateToTournament(tournament.id) },
                        colors = ButtonDefaults.buttonColors(containerColor = CyberBlue),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = if (tournament.status == TournamentStatus.COMPLETED) "View Standings" else "View & Register Team",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SchedulesTab(viewModel: TournamentViewModel, onMatchClick: (Match) -> Unit) {
    val matches by viewModel.matches.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Match Schedule & Live Scores",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        }

        items(matches) { match ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onMatchClick(match) }
                    .border(
                        BorderStroke(
                            1.dp,
                            if (match.status == MatchStatus.LIVE) NeonCyan else BorderColor
                        ),
                        RoundedCornerShape(24.dp)
                    ),
                colors = CardDefaults.cardColors(containerColor = NavySurface),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (match.status == MatchStatus.LIVE) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(Color.Red)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("LIVE SCORE", color = Color.Red, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            } else {
                                Text(match.status.name, color = TextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        Text(text = match.stage, color = NeonCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Matchup Box
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Team A
                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(NavySurfaceLight),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Shield, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(28.dp))
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = match.teamAName,
                                color = TextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        // Score / VS Box
                        Column(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            if (match.status == MatchStatus.UPCOMING) {
                                Text(
                                    text = "VS",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = TextSecondary
                                )
                            } else {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = match.scoreA.toString(),
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (match.scoreA >= match.scoreB) GoldTrophy else TextSecondary
                                    )
                                    Text(
                                        text = "-",
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextSecondary
                                    )
                                    Text(
                                        text = match.scoreB.toString(),
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (match.scoreB >= match.scoreA) GoldTrophy else TextSecondary
                                    )
                                }
                            }
                        }

                        // Team B
                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(NavySurfaceLight),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Shield, contentDescription = null, tint = GoldTrophy, modifier = Modifier.size(28.dp))
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = match.teamBName,
                                color = TextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Divider(color = BorderColor)

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccessTime,
                            contentDescription = null,
                            tint = TextSecondary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = match.time,
                            color = TextSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun GroupsTab(
    viewModel: TournamentViewModel,
    onNavigateToChat: (String) -> Unit
) {
    val groups by viewModel.groups.collectAsState()
    val teams by viewModel.teams.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    var activeSubTab by remember { mutableStateOf(0) } // 0: Global, 1: Groups, 2: Teams

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "Esports Chat Lobby",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "Secure and real-time communication channels",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }
        }

        // Segmented Control Selector
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val tabs = listOf("Global", "Group Chats", "Team Chats")
                tabs.forEachIndexed { index, label ->
                    val isSelected = activeSubTab == index
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) CyberBlue else NavySurface)
                            .border(1.dp, if (isSelected) NeonCyan else BorderColor, RoundedCornerShape(12.dp))
                            .clickable { activeSubTab = index }
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) Color.White else TextSecondary,
                            maxLines = 1
                        )
                    }
                }
            }
        }

        when (activeSubTab) {
            0 -> {
                // Global Chat Description and Entry Point
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, BorderColor, RoundedCornerShape(24.dp)),
                        colors = CardDefaults.cardColors(containerColor = NavySurface),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(CircleShape)
                                    .background(CyberBlue.copy(alpha = 0.2f))
                                    .border(1.dp, NeonCyan, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Public,
                                    contentDescription = null,
                                    tint = NeonCyan,
                                    modifier = Modifier.size(32.dp)
                                )
                            }

                            Text(
                                text = "GLOBAL COMMUNITY ARENA",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White,
                                letterSpacing = 1.sp
                            )

                            Text(
                                text = "Welcome to the central communications hub! This chat line is fully real-time and open to all registered users, team members, captains, and admins. Be respectful and have fun!",
                                fontSize = 13.sp,
                                color = TextSecondary,
                                textAlign = TextAlign.Center,
                                lineHeight = 18.sp
                            )

                            Button(
                                onClick = { onNavigateToChat("global") },
                                colors = ButtonDefaults.buttonColors(containerColor = CyberBlue),
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(Icons.Default.Forum, contentDescription = null, tint = Color.White)
                                    Text("Join Global Live Chat", fontWeight = FontWeight.Bold, color = Color.White)
                                }
                            }
                        }
                    }
                }
            }
            1 -> {
                // Group Bracket Chats
                if (groups.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No bracket groups created yet.", color = TextSecondary)
                        }
                    }
                } else {
                    items(groups) { group ->
                        val isAuthorized = remember(currentUser, group, teams) {
                            val user = currentUser ?: return@remember false
                            if (user.role == "Admin") true
                            else {
                                val userTeamIds = teams.filter { it.captainId == user.id || it.members.contains(user.name) }.map { it.id }
                                group.teamIds.any { it in userTeamIds }
                            }
                        }

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, BorderColor, RoundedCornerShape(24.dp)),
                            colors = CardDefaults.cardColors(containerColor = NavySurface),
                            shape = RoundedCornerShape(24.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = group.name,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )

                                    if (isAuthorized) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                                            modifier = Modifier
                                                .background(Color(0xFF10B981).copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                                                .padding(horizontal = 8.dp, vertical = 2.dp)
                                        ) {
                                            Icon(Icons.Default.LockOpen, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(12.dp))
                                            Text("OPEN", color = Color(0xFF10B981), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                        }
                                    } else {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                                            modifier = Modifier
                                                .background(Color(0xFFEF4444).copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                                                .padding(horizontal = 8.dp, vertical = 2.dp)
                                        ) {
                                            Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFFEF4444), modifier = Modifier.size(12.dp))
                                            Text("SECURE", color = Color(0xFFEF4444), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }

                                Text(
                                    text = group.description,
                                    fontSize = 11.sp,
                                    color = TextSecondary,
                                    modifier = Modifier.padding(top = 2.dp)
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                Text(
                                    text = "Participating Teams:",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NeonCyan
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                val groupTeams = teams.filter { it.id in group.teamIds }
                                if (groupTeams.isEmpty()) {
                                    Text(
                                        text = "No teams assigned to this bracket yet.",
                                        fontSize = 12.sp,
                                        color = TextSecondary
                                    )
                                } else {
                                    groupTeams.forEach { team ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 4.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Shield,
                                                contentDescription = null,
                                                tint = NeonCyan,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = team.name,
                                                color = TextPrimary,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Medium
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "(${team.captainName})",
                                                color = TextSecondary,
                                                fontSize = 11.sp
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))
                                Divider(color = BorderColor)
                                Spacer(modifier = Modifier.height(12.dp))

                                Button(
                                    onClick = { onNavigateToChat(group.id) },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (isAuthorized) CyberBlue else NavySurfaceLight
                                    ),
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(
                                            imageVector = if (isAuthorized) Icons.Default.Forum else Icons.Default.Lock,
                                            contentDescription = null,
                                            tint = if (isAuthorized) Color.White else TextSecondary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Text(
                                            if (isAuthorized) "Open Group Chat" else "Access Secure Group Chat",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = if (isAuthorized) Color.White else TextSecondary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
            2 -> {
                // Team HQ Chats
                if (teams.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No registered teams yet.", color = TextSecondary)
                        }
                    }
                } else {
                    items(teams) { team ->
                        val isAuthorized = remember(currentUser, team) {
                            val user = currentUser ?: return@remember false
                            user.role == "Admin" || team.captainId == user.id || team.members.contains(user.name)
                        }

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, BorderColor, RoundedCornerShape(24.dp)),
                            colors = CardDefaults.cardColors(containerColor = NavySurface),
                            shape = RoundedCornerShape(24.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = team.name,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )

                                    if (isAuthorized) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                                            modifier = Modifier
                                                .background(Color(0xFF10B981).copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                                                .padding(horizontal = 8.dp, vertical = 2.dp)
                                        ) {
                                            Icon(Icons.Default.LockOpen, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(12.dp))
                                            Text("TEAM OK", color = Color(0xFF10B981), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                        }
                                    } else {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                                            modifier = Modifier
                                                .background(Color(0xFFEF4444).copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                                                .padding(horizontal = 8.dp, vertical = 2.dp)
                                        ) {
                                            Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFFEF4444), modifier = Modifier.size(12.dp))
                                            Text("ENCRYPTED", color = Color(0xFFEF4444), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("Captain: ", color = TextSecondary, fontSize = 12.sp)
                                    Text(team.captainName, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = "Roster: " + team.members.joinToString(", "),
                                    color = TextSecondary,
                                    fontSize = 11.sp,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )

                                Spacer(modifier = Modifier.height(12.dp))
                                Divider(color = BorderColor)
                                Spacer(modifier = Modifier.height(12.dp))

                                Button(
                                    onClick = { onNavigateToChat(team.id) },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (isAuthorized) CyberBlue else NavySurfaceLight
                                    ),
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(
                                            imageVector = if (isAuthorized) Icons.Default.Forum else Icons.Default.Lock,
                                            contentDescription = null,
                                            tint = if (isAuthorized) Color.White else TextSecondary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Text(
                                            if (isAuthorized) "Open Team Chat" else "Access Secure Team Chat",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = if (isAuthorized) Color.White else TextSecondary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StandingsTab(viewModel: TournamentViewModel) {
    val standings = viewModel.getStandings("t_val_2026")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Tournament Standings",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        Text(
            text = "DFT-HEROS Premier League standings",
            fontSize = 12.sp,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Standings Header
        Card(
            colors = CardDefaults.cardColors(containerColor = NavySurfaceLight),
            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "#  TEAM",
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = TextPrimary,
                    modifier = Modifier.weight(2f)
                )
                Text(
                    text = "P",
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = TextPrimary,
                    modifier = Modifier.weight(0.5f),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "W",
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = TextPrimary,
                    modifier = Modifier.weight(0.5f),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "L",
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = TextPrimary,
                    modifier = Modifier.weight(0.5f),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "PTS",
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = NeonCyan,
                    modifier = Modifier.weight(0.6f),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "NRR",
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = TextPrimary,
                    modifier = Modifier.weight(0.8f),
                    textAlign = TextAlign.End
                )
            }
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .border(1.dp, BorderColor, RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp))
                .background(NavySurface)
        ) {
            items(standings.size) { index ->
                val row = standings[index]
                val isTopRow = index == 0

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(if (index % 2 == 1) NavyDeep.copy(alpha = 0.3f) else Color.Transparent)
                        .padding(horizontal = 12.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Index + Team Name
                    Row(
                        modifier = Modifier.weight(2f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${index + 1}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = if (isTopRow) GoldTrophy else TextSecondary,
                            modifier = Modifier.width(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = if (isTopRow) GoldTrophy else CyberBlue,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = row.teamName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = TextPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    // Played
                    Text(
                        text = row.played.toString(),
                        fontSize = 13.sp,
                        color = TextPrimary,
                        modifier = Modifier.weight(0.5f),
                        textAlign = TextAlign.Center
                    )

                    // Won
                    Text(
                        text = row.won.toString(),
                        fontSize = 13.sp,
                        color = Color(0xFF4CAF50),
                        modifier = Modifier.weight(0.5f),
                        textAlign = TextAlign.Center
                    )

                    // Lost
                    Text(
                        text = row.lost.toString(),
                        fontSize = 13.sp,
                        color = Color(0xFFEF5350),
                        modifier = Modifier.weight(0.5f),
                        textAlign = TextAlign.Center
                    )

                    // Points
                    Text(
                        text = row.points.toString(),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = NeonCyan,
                        modifier = Modifier.weight(0.6f),
                        textAlign = TextAlign.Center
                    )

                    // NRR
                    val nrrSign = if (row.netRunRate > 0) "+" else ""
                    Text(
                        text = "$nrrSign${String.format("%.2f", row.netRunRate)}",
                        fontSize = 12.sp,
                        color = if (row.netRunRate >= 0) Color(0xFF4CAF50) else Color(0xFFEF5350),
                        modifier = Modifier.weight(0.8f),
                        textAlign = TextAlign.End
                    )
                }
                if (index < standings.size - 1) {
                    Divider(color = BorderColor.copy(alpha = 0.5f))
                }
            }
        }
    }
}

@Composable
fun MatchDetailOverlay(
    match: Match,
    viewModel: TournamentViewModel,
    onDismiss: () -> Unit
) {
    val allTeams by viewModel.teams.collectAsState()
    val allMatches by viewModel.matches.collectAsState()
    
    val teamA = allTeams.find { it.id == match.teamAId }
    val teamB = allTeams.find { it.id == match.teamBId }
    
    val rosterA = if (match.lineupA.isNotEmpty()) match.lineupA else (teamA?.members ?: emptyList())
    val rosterB = if (match.lineupB.isNotEmpty()) match.lineupB else (teamB?.members ?: emptyList())
    
    // Head to head calculation
    val h2hMatches = allMatches.filter {
        it.status == MatchStatus.COMPLETED &&
        ((it.teamAId == match.teamAId && it.teamBId == match.teamBId) || 
         (it.teamAId == match.teamBId && it.teamBId == match.teamAId))
    }
    
    var activeSubTab by remember { mutableStateOf(0) } // 0: Status & Timeline, 1: Stats, 2: Roster, 3: History & H2H

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyDeep.copy(alpha = 0.98f))
            .clickable(enabled = false) {} // block click throughs
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // Header: Back Button & Title
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Close", tint = Color.White)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Match Details",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.weight(1f))
                // Status Badge
                val badgeColor = when (match.status) {
                    MatchStatus.LIVE -> Color.Red
                    MatchStatus.UPCOMING -> CyberBlue
                    MatchStatus.COMPLETED -> GoldTrophy
                }
                Box(
                    modifier = Modifier
                        .background(badgeColor.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                        .border(1.dp, badgeColor.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (match.status == MatchStatus.LIVE) "LIVE NOW" else match.status.name,
                        color = badgeColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                }
            }

            // Big scoreboard card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .border(1.dp, BorderColor.copy(alpha = 0.6f), RoundedCornerShape(24.dp)),
                colors = CardDefaults.cardColors(containerColor = NavySurface),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = match.stage,
                        color = NeonCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Team A
                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(CircleShape)
                                    .background(NavySurfaceLight),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Shield, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(36.dp))
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = match.teamAName,
                                color = Color.White,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        // Score or VS
                        Column(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            if (match.status == MatchStatus.UPCOMING) {
                                Box(
                                    modifier = Modifier
                                        .background(NavySurfaceLight, RoundedCornerShape(12.dp))
                                        .padding(horizontal = 16.dp, vertical = 8.dp)
                                ) {
                                    Text(
                                        text = "VS",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = TextSecondary
                                    )
                                }
                            } else {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Text(
                                        text = match.scoreA.toString(),
                                        fontSize = 32.sp,
                                        fontWeight = FontWeight.Black,
                                        color = if (match.scoreA >= match.scoreB) GoldTrophy else TextSecondary
                                    )
                                    Text(
                                        text = "-",
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = BorderColor
                                    )
                                    Text(
                                        text = match.scoreB.toString(),
                                        fontSize = 32.sp,
                                        fontWeight = FontWeight.Black,
                                        color = if (match.scoreB >= match.scoreA) GoldTrophy else TextSecondary
                                    )
                                }
                            }
                        }

                        // Team B
                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(CircleShape)
                                    .background(NavySurfaceLight),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Shield, contentDescription = null, tint = GoldTrophy, modifier = Modifier.size(36.dp))
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = match.teamBName,
                                color = Color.White,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                    Divider(color = BorderColor.copy(alpha = 0.4f))
                    Spacer(modifier = Modifier.height(12.dp))

                    // Date & Venue Details Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Event, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(match.date, color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocationOn, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(match.venue, color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }

            // Tabs for subsections
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .background(NavySurface, RoundedCornerShape(12.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                val subTabs = listOf("Events", "Stats", "Lineup", "History")
                subTabs.forEachIndexed { index, title ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (activeSubTab == index) CyberBlue else Color.Transparent)
                            .clickable { activeSubTab = index }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = title,
                            color = if (activeSubTab == index) Color.White else TextSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Sub Tab Content Panel
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp)
            ) {
                when (activeSubTab) {
                    0 -> MatchEventsTab(match = match)
                    1 -> MatchStatsTab(match = match)
                    2 -> MatchLineupTab(rosterA = rosterA, rosterB = rosterB, teamAName = match.teamAName, teamBName = match.teamBName)
                    3 -> MatchHistoryTab(match = match, h2hMatches = h2hMatches, allMatches = allMatches)
                }
            }
        }
    }
}

@Composable
fun MatchEventsTab(match: Match) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Venue / Referee metadata card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(containerColor = NavySurface),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AccessTime, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("TIME & STATUS", fontSize = 9.sp, color = TextSecondary, fontWeight = FontWeight.Bold)
                            Text(match.time, fontSize = 13.sp, color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Person, contentDescription = null, tint = GoldTrophy, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(horizontalAlignment = Alignment.End) {
                            Text("OFFICIAL / REFEREE", fontSize = 9.sp, color = TextSecondary, fontWeight = FontWeight.Bold)
                            Text(match.referee, fontSize = 13.sp, color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                if (match.manOfTheMatch.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Divider(color = BorderColor.copy(alpha = 0.3f))
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = GoldTrophy, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "MAN OF THE MATCH: ",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldTrophy,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = match.manOfTheMatch.uppercase(),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                    }
                }
            }
        }

        Text(
            text = "Match Events Timeline",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        if (match.events.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.HourglassEmpty, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(48.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (match.status == MatchStatus.UPCOMING) "The match has not started yet." else "No events recorded for this match.",
                        color = TextSecondary,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            match.events.sortedBy { it.minute }.forEachIndexed { index, event ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    // Minute Node Column
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.width(50.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .background(CyberBlue.copy(alpha = 0.2f), CircleShape)
                                .border(1.dp, CyberBlue, CircleShape)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "${event.minute}'",
                                color = NeonCyan,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        if (index < match.events.size - 1) {
                            Box(
                                modifier = Modifier
                                    .width(2.dp)
                                    .height(40.dp)
                                    .background(BorderColor.copy(alpha = 0.5f))
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    val eventIcon = when (event.type) {
                        MatchEventType.KILL -> Icons.Default.SportsEsports
                        MatchEventType.OBJECTIVE -> Icons.Default.Flag
                        MatchEventType.CARD_YELLOW -> Icons.Default.Warning
                        MatchEventType.CARD_RED -> Icons.Default.Gavel
                        MatchEventType.PAUSE -> Icons.Default.Pause
                        MatchEventType.RESUME -> Icons.Default.PlayArrow
                        else -> Icons.Default.Info
                    }
                    val eventIconTint = when (event.type) {
                        MatchEventType.KILL -> Color.Red
                        MatchEventType.OBJECTIVE -> GoldTrophy
                        MatchEventType.CARD_YELLOW -> Color.Yellow
                        MatchEventType.CARD_RED -> Color.Red
                        MatchEventType.PAUSE -> Color.White
                        MatchEventType.RESUME -> Color.Green
                        else -> NeonCyan
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, BorderColor.copy(alpha = 0.4f), RoundedCornerShape(12.dp)),
                        colors = CardDefaults.cardColors(containerColor = NavySurface),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = eventIcon,
                                contentDescription = null,
                                tint = eventIconTint,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                if (event.playerName.isNotEmpty()) {
                                    Text(
                                        text = event.playerName,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                                Text(
                                    text = event.description,
                                    fontSize = 12.sp,
                                    color = TextSecondary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MatchStatsTab(match: Match) {
    val stats = match.statistics
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = NavySurface),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Performance Breakdown",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 4.dp)
                )

                MatchStatBar(label = "Total Kills", valueA = stats.killsA, valueB = stats.killsB, colorA = NeonCyan, colorB = GoldTrophy)
                MatchStatBar(label = "Assists", valueA = stats.assistsA, valueB = stats.assistsB, colorA = NeonCyan, colorB = GoldTrophy)
                MatchStatBar(label = "Deaths", valueA = stats.deathsA, valueB = stats.deathsB, colorA = NeonCyan, colorB = GoldTrophy)
                MatchStatBar(label = "Objectives Secured", valueA = stats.objectiveA, valueB = stats.objectiveB, colorA = NeonCyan, colorB = GoldTrophy)
                MatchStatBar(label = "Average Rating", valueA = stats.ratingA, valueB = stats.ratingB, isDouble = true, colorA = NeonCyan, colorB = GoldTrophy)
            }
        }
    }
}

@Composable
fun MatchStatBar(
    label: String,
    valueA: Number,
    valueB: Number,
    isDouble: Boolean = false,
    colorA: Color,
    colorB: Color
) {
    val valA = valueA.toDouble()
    val valB = valueB.toDouble()
    val total = valA + valB
    val percentA = if (total > 0) (valA / total).toFloat() else 0.5f

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (isDouble) String.format("%.1f", valA) else valueA.toString(),
                color = colorA,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = label,
                color = TextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = if (isDouble) String.format("%.1f", valB) else valueB.toString(),
                color = colorB,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(BorderColor.copy(alpha = 0.3f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(if (percentA > 0f) percentA else 0.001f)
                    .background(colorA)
            )
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(if ((1f - percentA) > 0f) (1f - percentA) else 0.001f)
                    .background(colorB)
            )
        }
    }
}

@Composable
fun MatchLineupTab(
    rosterA: List<String>,
    rosterB: List<String>,
    teamAName: String,
    teamBName: String
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = teamAName,
                color = NeonCyan,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            if (rosterA.isEmpty()) {
                Text("No roster registered", color = TextSecondary, fontSize = 11.sp)
            } else {
                rosterA.forEachIndexed { idx, name ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .border(1.dp, NeonCyan.copy(alpha = 0.2f), RoundedCornerShape(10.dp)),
                        colors = CardDefaults.cardColors(containerColor = NavySurface),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(NeonCyan.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(name.take(1).uppercase(), color = NeonCyan, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = name,
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = teamBName,
                color = GoldTrophy,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            if (rosterB.isEmpty()) {
                Text("No roster registered", color = TextSecondary, fontSize = 11.sp)
            } else {
                rosterB.forEachIndexed { idx, name ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .border(1.dp, GoldTrophy.copy(alpha = 0.2f), RoundedCornerShape(10.dp)),
                        colors = CardDefaults.cardColors(containerColor = NavySurface),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(GoldTrophy.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(name.take(1).uppercase(), color = GoldTrophy, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = name,
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MatchHistoryTab(match: Match, h2hMatches: List<Match>, allMatches: List<Match>) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        val winsA = h2hMatches.count { (it.teamAId == match.teamAId && it.scoreA > it.scoreB) || (it.teamBId == match.teamAId && it.scoreB > it.scoreA) }
        val winsB = h2hMatches.count { (it.teamAId == match.teamBId && it.scoreA > it.scoreB) || (it.teamBId == match.teamBId && it.scoreB > it.scoreA) }
        val draws = h2hMatches.count { it.scoreA == it.scoreB }

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = NavySurface),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Head-to-Head (H2H) Record",
                    fontSize = 12.sp,
                    color = TextSecondary,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(winsA.toString(), fontSize = 24.sp, fontWeight = FontWeight.Black, color = NeonCyan)
                        Text(match.teamAName, fontSize = 10.sp, color = TextSecondary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(draws.toString(), fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Text("Draws", fontSize = 10.sp, color = TextSecondary)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(winsB.toString(), fontSize = 24.sp, fontWeight = FontWeight.Black, color = GoldTrophy)
                        Text(match.teamBName, fontSize = 10.sp, color = TextSecondary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                }
            }
        }

        Text(
            text = "Previous H2H Meetings",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        if (h2hMatches.isEmpty()) {
            Text("No prior matches recorded in this tournament.", color = TextSecondary, fontSize = 12.sp, modifier = Modifier.padding(start = 4.dp))
        } else {
            h2hMatches.forEach { h2h ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, BorderColor.copy(alpha = 0.4f), RoundedCornerShape(12.dp)),
                    colors = CardDefaults.cardColors(containerColor = NavySurface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(h2h.stage, fontSize = 10.sp, color = NeonCyan, fontWeight = FontWeight.Bold)
                            Text(h2h.date, fontSize = 11.sp, color = TextSecondary)
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(h2h.teamAName, fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.SemiBold)
                            Text("${h2h.scoreA} - ${h2h.scoreB}", fontSize = 13.sp, color = GoldTrophy, fontWeight = FontWeight.Bold)
                            Text(h2h.teamBName, fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }

        Text(
            text = "Tournament Match History",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        
        val tournamentHistory = allMatches.filter { it.tournamentId == match.tournamentId && it.status == MatchStatus.COMPLETED && it.id != match.id }
        if (tournamentHistory.isEmpty()) {
            Text("No other completed matches in this tournament.", color = TextSecondary, fontSize = 12.sp, modifier = Modifier.padding(start = 4.dp))
        } else {
            tournamentHistory.forEach { hist ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, BorderColor.copy(alpha = 0.3f), RoundedCornerShape(12.dp)),
                    colors = CardDefaults.cardColors(containerColor = NavySurface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(hist.stage, fontSize = 10.sp, color = TextSecondary, fontWeight = FontWeight.Bold)
                            Text(hist.date, fontSize = 11.sp, color = TextSecondary)
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(hist.teamAName, fontSize = 12.sp, color = Color.White)
                            Text("${hist.scoreA} - ${hist.scoreB}", fontSize = 13.sp, color = Color.White, fontWeight = FontWeight.Bold)
                            Text(hist.teamBName, fontSize = 12.sp, color = Color.White)
                        }
                    }
                }
            }
        }
    }
}
