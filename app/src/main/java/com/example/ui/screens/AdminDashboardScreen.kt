package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.*
import com.example.ui.theme.*
import com.example.viewmodel.TournamentViewModel
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    viewModel: TournamentViewModel,
    onNavigateBack: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Tournaments", "Groups", "Teams", "Matches", "Notifs", "Users", "Players")

    val tournaments by viewModel.tournaments.collectAsState()
    val teams by viewModel.teams.collectAsState()
    val groups by viewModel.groups.collectAsState()
    val matches by viewModel.matches.collectAsState()
    val notifications by viewModel.notifications.collectAsState()
    val allUsers by viewModel.allUsers.collectAsState()

    // Dialog trigger states
    var showCreateTournament by remember { mutableStateOf(false) }
    var editingTournament by remember { mutableStateOf<Tournament?>(null) }
    var showCreateGroup by remember { mutableStateOf(false) }
    var editingGroupTeams by remember { mutableStateOf<Group?>(null) }
    var showCreateMatch by remember { mutableStateOf(false) }
    var updatingMatch by remember { mutableStateOf<Match?>(null) }
    var showCreateNotification by remember { mutableStateOf(false) }

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
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        IconButton(
                            onClick = onNavigateBack,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(NavySurface)
                                .border(1.dp, BorderColor, RoundedCornerShape(12.dp))
                        ) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                        }
                        Column {
                            Text(
                                "DFT-HEROS PORTAL",
                                color = NeonCyan,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 2.sp
                            )
                            Text(
                                "Admin Control",
                                color = Color.White,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }

                    // Quick Stats Badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(GoldTrophy.copy(alpha = 0.15f))
                            .border(1.dp, GoldTrophy.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = GoldTrophy, modifier = Modifier.size(16.dp))
                            Text("SUPERUSER", color = GoldTrophy, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Custom Tab Row
                ScrollableTabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color.Transparent,
                    contentColor = NeonCyan,
                    edgePadding = 0.dp,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = NeonCyan
                        )
                    },
                    divider = {}
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = {
                                Text(
                                    text = title,
                                    fontSize = 13.sp,
                                    fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                    color = if (selectedTab == index) Color.White else TextSecondary
                                )
                            }
                        )
                    }
                }
            }
        },
        containerColor = NavyDeep
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
        ) {
            when (selectedTab) {
                0 -> TournamentsTab(
                    tournaments = tournaments,
                    onCreateClick = { showCreateTournament = true },
                    onEditClick = { editingTournament = it },
                    onDeleteClick = { viewModel.deleteTournament(it.id) }
                )
                1 -> GroupsTab(
                    groups = groups,
                    tournaments = tournaments,
                    teams = teams,
                    onCreateClick = { showCreateGroup = true },
                    onManageTeamsClick = { editingGroupTeams = it },
                    onDeleteClick = { viewModel.deleteGroup(it.id) }
                )
                2 -> TeamsTab(
                    teams = teams,
                    tournaments = tournaments,
                    onApproveClick = { team -> viewModel.updateTeam(team.copy(status = TeamStatus.APPROVED)) },
                    onRejectClick = { team -> viewModel.updateTeam(team.copy(status = TeamStatus.REJECTED)) },
                    onDeleteClick = { viewModel.deleteTeam(it.id) }
                )
                3 -> MatchesTab(
                    matches = matches,
                    tournaments = tournaments,
                    teams = teams,
                    onCreateClick = { showCreateMatch = true },
                    onUpdateClick = { updatingMatch = it },
                    onDeleteClick = { viewModel.deleteMatch(it.id) }
                )
                4 -> NotificationsTab(
                    notifications = notifications,
                    onCreateClick = { showCreateNotification = true }
                )
                5 -> UsersTab(
                    users = allUsers,
                    onRoleChange = { userId, newRole -> viewModel.updateUserRole(userId, newRole) }
                )
                6 -> PlayersTab(
                    viewModel = viewModel
                )
            }
        }
    }

    // --- DIALOGS ---

    // 1. Create Tournament Dialog
    if (showCreateTournament) {
        TournamentFormDialog(
            title = "Create Tournament",
            onDismiss = { showCreateTournament = false },
            onSave = { tournament ->
                viewModel.createTournament(tournament)
                showCreateTournament = false
            }
        )
    }

    // 2. Edit Tournament Dialog
    editingTournament?.let { tournament ->
        TournamentFormDialog(
            title = "Edit Tournament",
            initialTournament = tournament,
            onDismiss = { editingTournament = null },
            onSave = { updated ->
                viewModel.updateTournament(updated)
                editingTournament = null
            }
        )
    }

    // 3. Create Group Dialog
    if (showCreateGroup) {
        GroupFormDialog(
            tournaments = tournaments,
            onDismiss = { showCreateGroup = false },
            onSave = { group ->
                viewModel.createGroup(group)
                showCreateGroup = false
            }
        )
    }

    // 4. Manage Group Teams Dialog
    editingGroupTeams?.let { group ->
        ManageGroupTeamsDialog(
            group = group,
            teams = teams.filter { it.tournamentId == group.tournamentId },
            onDismiss = { editingGroupTeams = null },
            onAddTeam = { teamId ->
                viewModel.addTeamToGroup(group.id, teamId)
                editingGroupTeams = viewModel.groups.value.find { it.id == group.id }
            },
            onRemoveTeam = { teamId ->
                viewModel.removeTeamFromGroup(group.id, teamId)
                editingGroupTeams = viewModel.groups.value.find { it.id == group.id }
            }
        )
    }

    // 5. Create Match Dialog
    if (showCreateMatch) {
        MatchFormDialog(
            tournaments = tournaments,
            teams = teams,
            onDismiss = { showCreateMatch = false },
            onSave = { match ->
                viewModel.createMatch(match)
                showCreateMatch = false
            }
        )
    }

    // 6. Update Match Score Dialog
    updatingMatch?.let { match ->
        UpdateMatchDialog(
            match = match,
            onDismiss = { updatingMatch = null },
            onSave = { updated ->
                viewModel.updateMatch(updated)
                updatingMatch = null
            }
        )
    }

    // 7. Create Broadcast Notification Dialog
    if (showCreateNotification) {
        NotificationFormDialog(
            onDismiss = { showCreateNotification = false },
            onSave = { title, msg ->
                viewModel.sendNotification(title, msg)
                showCreateNotification = false
            }
        )
    }
}

// ================= TAB PANELS =================

@Composable
fun TournamentsTab(
    tournaments: List<Tournament>,
    onCreateClick: () -> Unit,
    onEditClick: (Tournament) -> Unit,
    onDeleteClick: (Tournament) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Registered Tournaments (${tournaments.size})", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Button(
                onClick = onCreateClick,
                colors = ButtonDefaults.buttonColors(containerColor = CyberBlue),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Create New", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        if (tournaments.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().weight(1f), contentAlignment = Alignment.Center) {
                Text("No Tournaments found.", color = TextSecondary)
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(tournaments) { tournament ->
                    Card(
                        modifier = Modifier.fillMaxWidth().border(1.dp, BorderColor, RoundedCornerShape(20.dp)),
                        colors = CardDefaults.cardColors(containerColor = NavySurface),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(tournament.name, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                    Text(tournament.gameName, color = NeonCyan, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(
                                            when (tournament.status) {
                                                TournamentStatus.UPCOMING -> Color(0xFF1E293B)
                                                TournamentStatus.ONGOING -> Color(0xFF047857)
                                                TournamentStatus.COMPLETED -> Color(0xFF374151)
                                            }
                                        )
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        tournament.status.name,
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                            Text(tournament.description, color = TextSecondary, fontSize = 12.sp, maxLines = 2)
                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Prize Pool: ${tournament.prizePool}", color = GoldTrophy, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    IconButton(
                                        onClick = { onEditClick(tournament) },
                                        modifier = Modifier.size(36.dp).background(NavySurfaceLight, RoundedCornerShape(8.dp))
                                    ) {
                                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color.White, modifier = Modifier.size(16.dp))
                                    }
                                    IconButton(
                                        onClick = { onDeleteClick(tournament) },
                                        modifier = Modifier.size(36.dp).background(Color.Red.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red, modifier = Modifier.size(16.dp))
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
fun GroupsTab(
    groups: List<Group>,
    tournaments: List<Tournament>,
    teams: List<Team>,
    onCreateClick: () -> Unit,
    onManageTeamsClick: (Group) -> Unit,
    onDeleteClick: (Group) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Tournament Groups (${groups.size})", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Button(
                onClick = onCreateClick,
                colors = ButtonDefaults.buttonColors(containerColor = CyberBlue),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Create Group", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        if (groups.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().weight(1f), contentAlignment = Alignment.Center) {
                Text("No Groups created yet.", color = TextSecondary)
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(groups) { group ->
                    val tournament = tournaments.find { it.id == group.tournamentId }
                    Card(
                        modifier = Modifier.fillMaxWidth().border(1.dp, BorderColor, RoundedCornerShape(20.dp)),
                        colors = CardDefaults.cardColors(containerColor = NavySurface),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(group.name, color = Color.White, fontSize = 17.sp, fontWeight = FontWeight.Bold)
                                    Text(tournament?.name ?: "Unknown Tournament", color = TextSecondary, fontSize = 12.sp)
                                }
                                // Group Code Badge
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(NeonCyan.copy(alpha = 0.15f))
                                        .border(1.dp, NeonCyan.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "CODE: ${group.code}",
                                        color = NeonCyan,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            if (group.description.isNotEmpty()) {
                                Text(group.description, color = TextSecondary, fontSize = 12.sp)
                                Spacer(modifier = Modifier.height(12.dp))
                            }

                            // Teams list in group
                            Text("Teams in Group (${group.teamIds.size}):", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            Spacer(modifier = Modifier.height(6.dp))
                            if (group.teamIds.isEmpty()) {
                                Text("No teams assigned yet.", color = TextSecondary, fontSize = 11.sp)
                            } else {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    group.teamIds.forEach { teamId ->
                                        val team = teams.find { it.id == teamId }
                                        team?.let {
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .background(NavySurfaceLight)
                                                    .border(1.dp, BorderColor, RoundedCornerShape(8.dp))
                                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                                            ) {
                                                Text(it.name, color = Color.White, fontSize = 11.sp)
                                            }
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Button(
                                        onClick = { onManageTeamsClick(group) },
                                        colors = ButtonDefaults.buttonColors(containerColor = NavySurfaceLight),
                                        shape = RoundedCornerShape(10.dp),
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                    ) {
                                        Icon(Icons.Default.GroupAdd, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Manage Teams", fontSize = 11.sp, color = Color.White)
                                    }
                                    IconButton(
                                        onClick = { onDeleteClick(group) },
                                        modifier = Modifier.size(36.dp).background(Color.Red.copy(alpha = 0.15f), RoundedCornerShape(10.dp))
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red, modifier = Modifier.size(16.dp))
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
fun TeamsTab(
    teams: List<Team>,
    tournaments: List<Tournament>,
    onApproveClick: (Team) -> Unit,
    onRejectClick: (Team) -> Unit,
    onDeleteClick: (Team) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            "Registered Teams (${teams.size})",
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 12.dp)
        )

        if (teams.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().weight(1f), contentAlignment = Alignment.Center) {
                Text("No Teams registered yet.", color = TextSecondary)
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(teams) { team ->
                    val tournament = tournaments.find { it.id == team.tournamentId }
                    Card(
                        modifier = Modifier.fillMaxWidth().border(1.dp, BorderColor, RoundedCornerShape(20.dp)),
                        colors = CardDefaults.cardColors(containerColor = NavySurface),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(team.name, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                    Text("Captain: ${team.captainName}", color = TextSecondary, fontSize = 12.sp)
                                    Text("Tournament: ${tournament?.name ?: "Unknown"}", color = NeonCyan, fontSize = 11.sp)
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(
                                            when (team.status) {
                                                TeamStatus.PENDING -> Color(0xFFFF9800).copy(alpha = 0.15f)
                                                TeamStatus.APPROVED -> Color(0xFF4CAF50).copy(alpha = 0.15f)
                                                TeamStatus.REJECTED -> Color(0xFFF44336).copy(alpha = 0.15f)
                                            }
                                        )
                                        .border(
                                            1.dp,
                                            when (team.status) {
                                                TeamStatus.PENDING -> Color(0xFFFF9800)
                                                TeamStatus.APPROVED -> Color(0xFF4CAF50)
                                                TeamStatus.REJECTED -> Color(0xFFF44336)
                                            }.copy(alpha = 0.4f),
                                            RoundedCornerShape(8.dp)
                                        )
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        team.status.name,
                                        color = when (team.status) {
                                            TeamStatus.PENDING -> Color(0xFFFFB74D)
                                            TeamStatus.APPROVED -> Color(0xFF81C784)
                                            TeamStatus.REJECTED -> Color(0xFFE57373)
                                        },
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            Text("Roster: ${team.members.joinToString(", ")}", color = TextSecondary, fontSize = 12.sp)

                            Spacer(modifier = Modifier.height(14.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    if (team.status == TeamStatus.PENDING) {
                                        Button(
                                            onClick = { onApproveClick(team) },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                                            shape = RoundedCornerShape(10.dp),
                                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                        ) {
                                            Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Approve", fontSize = 11.sp, color = Color.White)
                                        }
                                        Button(
                                            onClick = { onRejectClick(team) },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC62828)),
                                            shape = RoundedCornerShape(10.dp),
                                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                        ) {
                                            Icon(Icons.Default.Close, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Reject", fontSize = 11.sp, color = Color.White)
                                        }
                                    }
                                }
                                IconButton(
                                    onClick = { onDeleteClick(team) },
                                    modifier = Modifier.size(36.dp).background(Color.Red.copy(alpha = 0.15f), RoundedCornerShape(10.dp))
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red, modifier = Modifier.size(16.dp))
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
fun MatchesTab(
    matches: List<Match>,
    tournaments: List<Tournament>,
    teams: List<Team>,
    onCreateClick: () -> Unit,
    onUpdateClick: (Match) -> Unit,
    onDeleteClick: (Match) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Match Schedules (${matches.size})", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Button(
                onClick = onCreateClick,
                colors = ButtonDefaults.buttonColors(containerColor = CyberBlue),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Schedule Match", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        if (matches.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().weight(1f), contentAlignment = Alignment.Center) {
                Text("No Matches scheduled.", color = TextSecondary)
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(matches) { match ->
                    val tournament = tournaments.find { it.id == match.tournamentId }
                    Card(
                        modifier = Modifier.fillMaxWidth().border(1.dp, BorderColor, RoundedCornerShape(20.dp)),
                        colors = CardDefaults.cardColors(containerColor = NavySurface),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(tournament?.name ?: "Unknown Tournament", color = TextSecondary, fontSize = 11.sp)
                                    Text(match.stage, color = NeonCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(
                                            when (match.status) {
                                                MatchStatus.UPCOMING -> Color(0xFF1E293B)
                                                MatchStatus.LIVE -> Color(0xFF991B1B)
                                                MatchStatus.COMPLETED -> Color(0xFF047857)
                                            }
                                        )
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        match.status.name,
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Scoreboard representation
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceAround,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                                    Text(match.teamAName, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                                }
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    modifier = Modifier.padding(horizontal = 16.dp)
                                ) {
                                    Text(
                                        text = if (match.status == MatchStatus.UPCOMING) "-" else match.scoreA.toString(),
                                        color = if (match.status == MatchStatus.LIVE) NeonCyan else Color.White,
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                    Text("VS", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    Text(
                                        text = if (match.status == MatchStatus.UPCOMING) "-" else match.scoreB.toString(),
                                        color = if (match.status == MatchStatus.LIVE) NeonCyan else Color.White,
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                                    Text(match.teamBName, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Icon(Icons.Default.Schedule, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(12.dp))
                                    Text(match.time, color = TextSecondary, fontSize = 11.sp)
                                }
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Button(
                                        onClick = { onUpdateClick(match) },
                                        colors = ButtonDefaults.buttonColors(containerColor = NavySurfaceLight),
                                        shape = RoundedCornerShape(10.dp),
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                    ) {
                                        Icon(Icons.Default.Scoreboard, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Update Score", fontSize = 11.sp, color = Color.White)
                                    }
                                    IconButton(
                                        onClick = { onDeleteClick(match) },
                                        modifier = Modifier.size(36.dp).background(Color.Red.copy(alpha = 0.15f), RoundedCornerShape(10.dp))
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red, modifier = Modifier.size(16.dp))
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
fun NotificationsTab(
    notifications: List<Notification>,
    onCreateClick: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Broadcast Notifications (${notifications.size})", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Button(
                onClick = onCreateClick,
                colors = ButtonDefaults.buttonColors(containerColor = CyberBlue),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Icon(Icons.Default.Campaign, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Broadcast Notif", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        if (notifications.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().weight(1f), contentAlignment = Alignment.Center) {
                Text("No broadcasts sent.", color = TextSecondary)
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(notifications) { notif ->
                    Card(
                        modifier = Modifier.fillMaxWidth().border(1.dp, BorderColor, RoundedCornerShape(20.dp)),
                        colors = CardDefaults.cardColors(containerColor = NavySurface),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Icon(Icons.Default.Campaign, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(20.dp))
                                    Text(notif.title, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                                }
                                val formattedTime = remember(notif.timestamp) {
                                    val minutesAgo = (System.currentTimeMillis() - notif.timestamp) / 60000
                                    if (minutesAgo < 1) "Just now"
                                    else if (minutesAgo < 60) "${minutesAgo}m ago"
                                    else "${minutesAgo / 60}h ago"
                                }
                                Text(formattedTime, color = TextSecondary, fontSize = 11.sp)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(notif.message, color = TextSecondary, fontSize = 13.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun UsersTab(
    users: List<AppUser>,
    onRoleChange: (String, String) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            "Manage Platform Users (${users.size})",
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 12.dp)
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(users) { user ->
                Card(
                    modifier = Modifier.fillMaxWidth().border(1.dp, BorderColor, RoundedCornerShape(20.dp)),
                    colors = CardDefaults.cardColors(containerColor = NavySurface),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(CyberBlue.copy(alpha = 0.2f))
                                    .border(1.dp, CyberBlue, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Person, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                            }
                            Column {
                                Text(user.name, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                                Text(user.email, color = TextSecondary, fontSize = 11.sp)
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    modifier = Modifier.padding(top = 4.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(
                                                when (user.role) {
                                                    "Admin" -> GoldTrophy.copy(alpha = 0.15f)
                                                    "Captain" -> NeonCyan.copy(alpha = 0.15f)
                                                    else -> Color.White.copy(alpha = 0.1f)
                                                }
                                            )
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            user.role.uppercase(),
                                            color = when (user.role) {
                                                "Admin" -> GoldTrophy
                                                "Captain" -> NeonCyan
                                                else -> TextSecondary
                                            },
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }

                        // Role Selector Popup Trigger
                        var showRolesDropdown by remember { mutableStateOf(false) }
                        Box {
                            Button(
                                onClick = { showRolesDropdown = true },
                                colors = ButtonDefaults.buttonColors(containerColor = NavySurfaceLight),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text("Set Role", fontSize = 11.sp, color = Color.White)
                                Icon(Icons.Default.ArrowDropDown, contentDescription = null, modifier = Modifier.size(16.dp))
                            }
                            DropdownMenu(
                                expanded = showRolesDropdown,
                                onDismissRequest = { showRolesDropdown = false },
                                modifier = Modifier.background(NavySurfaceLight)
                            ) {
                                listOf("Admin", "Captain", "Member").forEach { role ->
                                    DropdownMenuItem(
                                        text = { Text(role, color = Color.White) },
                                        onClick = {
                                            onRoleChange(user.id, role)
                                            showRolesDropdown = false
                                        }
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


// ================= DIALOG FORMS =================

@Composable
fun TournamentFormDialog(
    title: String,
    initialTournament: Tournament? = null,
    onDismiss: () -> Unit,
    onSave: (Tournament) -> Unit
) {
    var name by remember { mutableStateOf(initialTournament?.name ?: "") }
    var gameName by remember { mutableStateOf(initialTournament?.gameName ?: "") }
    var status by remember { mutableStateOf(initialTournament?.status ?: TournamentStatus.UPCOMING) }
    var prizePool by remember { mutableStateOf(initialTournament?.prizePool ?: "") }
    var description by remember { mutableStateOf(initialTournament?.description ?: "") }
    var maxTeams by remember { mutableStateOf(initialTournament?.maxTeams?.toString() ?: "16") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = NavySurface,
        title = { Text(title, color = Color.White, fontWeight = FontWeight.Bold) },
        text = {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                item {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Tournament Name") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonCyan,
                            unfocusedBorderColor = BorderColor,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = gameName,
                        onValueChange = { gameName = it },
                        label = { Text("Game Name") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonCyan,
                            unfocusedBorderColor = BorderColor,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = prizePool,
                        onValueChange = { prizePool = it },
                        label = { Text("Prize Pool") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonCyan,
                            unfocusedBorderColor = BorderColor,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = maxTeams,
                        onValueChange = { maxTeams = it },
                        label = { Text("Max Teams") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonCyan,
                            unfocusedBorderColor = BorderColor,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Description") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonCyan,
                            unfocusedBorderColor = BorderColor,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    Text("Status:", color = TextSecondary, fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
                    ) {
                        TournamentStatus.values().forEach { s ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (status == s) CyberBlue else NavySurfaceLight)
                                    .border(1.dp, if (status == s) NeonCyan else Color.Transparent, RoundedCornerShape(8.dp))
                                    .clickable { status = s }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(s.name, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank() && gameName.isNotBlank()) {
                        val tournament = Tournament(
                            id = initialTournament?.id ?: "t_" + UUID.randomUUID().toString().take(6),
                            name = name,
                            gameName = gameName,
                            status = status,
                            prizePool = prizePool,
                            maxTeams = maxTeams.toIntOrNull() ?: 16,
                            description = description,
                            startDate = initialTournament?.startDate ?: "TBD",
                            endDate = initialTournament?.endDate ?: "TBD"
                        )
                        onSave(tournament)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = CyberBlue)
            ) {
                Text("Save", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
        }
    )
}

@Composable
fun GroupFormDialog(
    tournaments: List<Tournament>,
    onDismiss: () -> Unit,
    onSave: (Group) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedTournament by remember { mutableStateOf<Tournament?>(tournaments.firstOrNull()) }
    var dropdownExpanded by remember { mutableStateOf(false) }

    // 6-digit unique group code generation
    val generatedCode = remember { (100000..999999).random().toString() }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = NavySurface,
        title = { Text("Create Group", color = Color.White, fontWeight = FontWeight.Bold) },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Group Name (e.g. Group A)") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = BorderColor,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Group Description") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = BorderColor,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                // Tournament Selector
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = selectedTournament?.name ?: "Select Tournament",
                        onValueChange = {},
                        label = { Text("Assign Tournament") },
                        readOnly = true,
                        trailingIcon = { Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = Color.White) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonCyan,
                            unfocusedBorderColor = BorderColor,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth().clickable { dropdownExpanded = true }
                    )
                    DropdownMenu(
                        expanded = dropdownExpanded,
                        onDismissRequest = { dropdownExpanded = false },
                        modifier = Modifier.background(NavySurfaceLight).fillMaxWidth()
                    ) {
                        tournaments.forEach { t ->
                            DropdownMenuItem(
                                text = { Text(t.name, color = Color.White) },
                                onClick = {
                                    selectedTournament = t
                                    dropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                // Code Display Card
                Card(
                    modifier = Modifier.fillMaxWidth().border(1.dp, NeonCyan.copy(alpha = 0.3f), RoundedCornerShape(12.dp)),
                    colors = CardDefaults.cardColors(containerColor = NavySurfaceLight)
                ) {
                    Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Auto-Generated Group Code", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(generatedCode, color = NeonCyan, fontSize = 28.sp, fontWeight = FontWeight.Black, letterSpacing = 4.sp)
                        Text("Share this unique 6-digit code with squads", color = TextSecondary, fontSize = 10.sp)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank() && selectedTournament != null) {
                        val group = Group(
                            id = "g_" + UUID.randomUUID().toString().take(6),
                            name = name,
                            description = description,
                            tournamentId = selectedTournament!!.id,
                            code = generatedCode,
                            teamIds = emptyList()
                        )
                        onSave(group)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = CyberBlue)
            ) {
                Text("Create", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
        }
    )
}

@Composable
fun ManageGroupTeamsDialog(
    group: Group,
    teams: List<Team>,
    onDismiss: () -> Unit,
    onAddTeam: (String) -> Unit,
    onRemoveTeam: (String) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = NavySurface,
        title = {
            Column {
                Text("Manage Group Squads", color = Color.White, fontWeight = FontWeight.Bold)
                Text("Group: ${group.name}", color = NeonCyan, fontSize = 12.sp)
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth().heightIn(max = 400.dp)
            ) {
                Text("Current Squads in Group:", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(8.dp))

                // Assigned teams
                if (group.teamIds.isEmpty()) {
                    Text("No squads in this group yet.", color = TextSecondary, fontSize = 12.sp, modifier = Modifier.padding(bottom = 16.dp))
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.weight(1f).fillMaxWidth().padding(bottom = 16.dp)
                    ) {
                        items(group.teamIds) { teamId ->
                            val team = teams.find { it.id == teamId }
                            team?.let {
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = NavySurfaceLight),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp).fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(it.name, color = Color.White, fontSize = 13.sp)
                                        IconButton(
                                            onClick = { onRemoveTeam(it.id) },
                                            modifier = Modifier.size(24.dp)
                                        ) {
                                            Icon(Icons.Default.RemoveCircle, contentDescription = "Remove", tint = Color.Red, modifier = Modifier.size(18.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Divider(color = BorderColor, modifier = Modifier.padding(vertical = 8.dp))

                // Available teams to add
                Text("Add Squad to Group:", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(8.dp))

                val availableTeams = teams.filter { it.status == TeamStatus.APPROVED && !group.teamIds.contains(it.id) }
                if (availableTeams.isEmpty()) {
                    Text("No approved teams available to add.", color = TextSecondary, fontSize = 12.sp)
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.weight(1f).fillMaxWidth()
                    ) {
                        items(availableTeams) { team ->
                            Card(
                                colors = CardDefaults.cardColors(containerColor = NavySurfaceLight),
                                modifier = Modifier.fillMaxWidth().clickable { onAddTeam(team.id) }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp).fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(team.name, color = Color.White, fontSize = 13.sp)
                                    Icon(Icons.Default.AddCircle, contentDescription = "Add", tint = NeonCyan, modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = CyberBlue)
            ) {
                Text("Close", color = Color.White)
            }
        }
    )
}

@Composable
fun MatchFormDialog(
    tournaments: List<Tournament>,
    teams: List<Team>,
    onDismiss: () -> Unit,
    onSave: (Match) -> Unit
) {
    var selectedTournament by remember { mutableStateOf<Tournament?>(tournaments.firstOrNull()) }
    var selectedTeamA by remember { mutableStateOf<Team?>(null) }
    var selectedTeamB by remember { mutableStateOf<Team?>(null) }
    var time by remember { mutableStateOf("Today, 20:00") }
    var date by remember { mutableStateOf("July 16, 2026") }
    var venue by remember { mutableStateOf("DFT Cyber Arena") }
    var referee by remember { mutableStateOf("Marshall Carter") }
    var stage by remember { mutableStateOf("Group Stage") }

    var tDropdown by remember { mutableStateOf(false) }
    var aDropdown by remember { mutableStateOf(false) }
    var bDropdown by remember { mutableStateOf(false) }

    // Filter teams belonging to the selected tournament and approved
    val filteredTeams = remember(selectedTournament, teams) {
        teams.filter { it.tournamentId == selectedTournament?.id && it.status == TeamStatus.APPROVED }
    }

    // Auto select default teams
    LaunchedEffect(filteredTeams) {
        selectedTeamA = filteredTeams.getOrNull(0)
        selectedTeamB = filteredTeams.getOrNull(1)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = NavySurface,
        title = { Text("Schedule Match", color = Color.White, fontWeight = FontWeight.Bold) },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // Tournament Dropdown
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = selectedTournament?.name ?: "Select Tournament",
                        onValueChange = {},
                        label = { Text("Tournament") },
                        readOnly = true,
                        trailingIcon = { Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = Color.White) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonCyan,
                            unfocusedBorderColor = BorderColor,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth().clickable { tDropdown = true }
                    )
                    DropdownMenu(
                        expanded = tDropdown,
                        onDismissRequest = { tDropdown = false },
                        modifier = Modifier.background(NavySurfaceLight).fillMaxWidth()
                    ) {
                        tournaments.forEach { t ->
                            DropdownMenuItem(
                                text = { Text(t.name, color = Color.White) },
                                onClick = {
                                    selectedTournament = t
                                    tDropdown = false
                                }
                            )
                        }
                    }
                }

                // Team A Dropdown
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = selectedTeamA?.name ?: "Select Team A",
                        onValueChange = {},
                        label = { Text("Team A") },
                        readOnly = true,
                        trailingIcon = { Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = Color.White) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonCyan,
                            unfocusedBorderColor = BorderColor,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth().clickable { aDropdown = true }
                    )
                    DropdownMenu(
                        expanded = aDropdown,
                        onDismissRequest = { aDropdown = false },
                        modifier = Modifier.background(NavySurfaceLight).fillMaxWidth()
                    ) {
                        filteredTeams.forEach { team ->
                            DropdownMenuItem(
                                text = { Text(team.name, color = Color.White) },
                                onClick = {
                                    selectedTeamA = team
                                    aDropdown = false
                                }
                            )
                        }
                    }
                }

                // Team B Dropdown
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = selectedTeamB?.name ?: "Select Team B",
                        onValueChange = {},
                        label = { Text("Team B") },
                        readOnly = true,
                        trailingIcon = { Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = Color.White) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonCyan,
                            unfocusedBorderColor = BorderColor,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth().clickable { bDropdown = true }
                    )
                    DropdownMenu(
                        expanded = bDropdown,
                        onDismissRequest = { bDropdown = false },
                        modifier = Modifier.background(NavySurfaceLight).fillMaxWidth()
                    ) {
                        filteredTeams.filter { it.id != selectedTeamA?.id }.forEach { team ->
                            DropdownMenuItem(
                                text = { Text(team.name, color = Color.White) },
                                onClick = {
                                    selectedTeamB = team
                                    bDropdown = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = time,
                    onValueChange = { time = it },
                    label = { Text("Time (e.g. 20:00)") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = BorderColor,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = date,
                    onValueChange = { date = it },
                    label = { Text("Date (e.g. July 16, 2026)") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = BorderColor,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = venue,
                    onValueChange = { venue = it },
                    label = { Text("Venue") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = BorderColor,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = referee,
                    onValueChange = { referee = it },
                    label = { Text("Referee") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = BorderColor,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = stage,
                    onValueChange = { stage = it },
                    label = { Text("Stage (e.g. Group Stage, Semis, Final)") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = BorderColor,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (selectedTournament != null && selectedTeamA != null && selectedTeamB != null) {
                        val match = Match(
                            id = "match_" + UUID.randomUUID().toString().take(6),
                            tournamentId = selectedTournament!!.id,
                            teamAId = selectedTeamA!!.id,
                            teamAName = selectedTeamA!!.name,
                            teamBId = selectedTeamB!!.id,
                            teamBName = selectedTeamB!!.name,
                            scoreA = 0,
                            scoreB = 0,
                            status = MatchStatus.UPCOMING,
                            time = time,
                            stage = stage,
                            date = date,
                            venue = venue,
                            referee = referee
                        )
                        onSave(match)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = CyberBlue)
            ) {
                Text("Schedule", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
        }
    )
}

@Composable
fun UpdateMatchDialog(
    match: Match,
    onDismiss: () -> Unit,
    onSave: (Match) -> Unit
) {
    var scoreA by remember { mutableStateOf(match.scoreA.toString()) }
    var scoreB by remember { mutableStateOf(match.scoreB.toString()) }
    var status by remember { mutableStateOf(match.status) }
    
    // Additional Match Module attributes
    var venue by remember { mutableStateOf(match.venue) }
    var referee by remember { mutableStateOf(match.referee) }
    var manOfTheMatch by remember { mutableStateOf(match.manOfTheMatch) }
    
    // Stats
    var killsA by remember { mutableStateOf(match.statistics.killsA.toString()) }
    var killsB by remember { mutableStateOf(match.statistics.killsB.toString()) }
    var assistsA by remember { mutableStateOf(match.statistics.assistsA.toString()) }
    var assistsB by remember { mutableStateOf(match.statistics.assistsB.toString()) }
    var deathsA by remember { mutableStateOf(match.statistics.deathsA.toString()) }
    var deathsB by remember { mutableStateOf(match.statistics.deathsB.toString()) }
    var objectiveA by remember { mutableStateOf(match.statistics.objectiveA.toString()) }
    var objectiveB by remember { mutableStateOf(match.statistics.objectiveB.toString()) }
    var ratingA by remember { mutableStateOf(match.statistics.ratingA.toString()) }
    var ratingB by remember { mutableStateOf(match.statistics.ratingB.toString()) }
    
    // Events list
    var eventsList by remember { mutableStateOf(match.events) }
    
    // Form fields to add a new event
    var evMin by remember { mutableStateOf("") }
    var evType by remember { mutableStateOf(MatchEventType.KILL) }
    var evPlayer by remember { mutableStateOf("") }
    var evDesc by remember { mutableStateOf("") }
    var eventTypeDropdown by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = NavySurface,
        title = { Text("Update Match Stats", color = Color.White, fontWeight = FontWeight.Bold) },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 480.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Scores Section
                Text("Scores", color = NeonCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(match.teamAName, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                        OutlinedTextField(
                            value = scoreA,
                            onValueChange = { scoreA = it },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NeonCyan,
                                unfocusedBorderColor = BorderColor,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
                        )
                    }
                    Text("VS", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 12.dp).padding(top = 20.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(match.teamBName, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                        OutlinedTextField(
                            value = scoreB,
                            onValueChange = { scoreB = it },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NeonCyan,
                                unfocusedBorderColor = BorderColor,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
                        )
                    }
                }

                // Status Section
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text("Match Status:", color = TextSecondary, fontSize = 12.sp)
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth().padding(top = 6.dp)
                    ) {
                        MatchStatus.values().forEach { ms ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (status == ms) CyberBlue else NavySurfaceLight)
                                    .border(1.dp, if (status == ms) NeonCyan else Color.Transparent, RoundedCornerShape(8.dp))
                                    .clickable { status = ms }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(ms.name, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                Divider(color = BorderColor.copy(alpha = 0.3f))

                // Logistics Section
                Text("Logistics & MVP", color = NeonCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                OutlinedTextField(
                    value = venue,
                    onValueChange = { venue = it },
                    label = { Text("Venue") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = BorderColor,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = referee,
                    onValueChange = { referee = it },
                    label = { Text("Referee") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = BorderColor,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = manOfTheMatch,
                    onValueChange = { manOfTheMatch = it },
                    label = { Text("Man of the Match") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = BorderColor,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Divider(color = BorderColor.copy(alpha = 0.3f))

                // Match Statistics Section
                Text("Performance Stats", color = NeonCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                
                // Kills comparison inputs
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = killsA,
                        onValueChange = { killsA = it },
                        label = { Text("${match.teamAName} Kills") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                    )
                    OutlinedTextField(
                        value = killsB,
                        onValueChange = { killsB = it },
                        label = { Text("${match.teamBName} Kills") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                    )
                }

                // Assists comparison inputs
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = assistsA,
                        onValueChange = { assistsA = it },
                        label = { Text("${match.teamAName} Assists") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                    )
                    OutlinedTextField(
                        value = assistsB,
                        onValueChange = { assistsB = it },
                        label = { Text("${match.teamBName} Assists") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                    )
                }

                // Deaths comparison inputs
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = deathsA,
                        onValueChange = { deathsA = it },
                        label = { Text("${match.teamAName} Deaths") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                    )
                    OutlinedTextField(
                        value = deathsB,
                        onValueChange = { deathsB = it },
                        label = { Text("${match.teamBName} Deaths") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                    )
                }

                // Objectives comparison inputs
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = objectiveA,
                        onValueChange = { objectiveA = it },
                        label = { Text("${match.teamAName} Objectives") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                    )
                    OutlinedTextField(
                        value = objectiveB,
                        onValueChange = { objectiveB = it },
                        label = { Text("${match.teamBName} Objectives") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                    )
                }

                // Rating comparison inputs
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = ratingA,
                        onValueChange = { ratingA = it },
                        label = { Text("${match.teamAName} Rating") },
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                    )
                    OutlinedTextField(
                        value = ratingB,
                        onValueChange = { ratingB = it },
                        label = { Text("${match.teamBName} Rating") },
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                    )
                }

                Divider(color = BorderColor.copy(alpha = 0.3f))

                // Timeline events management
                Text("Match Timeline Events", color = NeonCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                
                eventsList.sortedBy { it.minute }.forEach { ev ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = NavySurfaceLight)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("${ev.minute}' - [${ev.type.name}] ${ev.playerName}", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Text(ev.description, color = TextSecondary, fontSize = 11.sp)
                            }
                            IconButton(
                                onClick = { eventsList = eventsList.filter { it.id != ev.id } },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = "Remove Event", tint = Color.Red, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }

                // Add new event form
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = NavySurfaceLight),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Add Timeline Event", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = evMin,
                                onValueChange = { evMin = it },
                                label = { Text("Minute") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f),
                                colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                            )
                            
                            Box(modifier = Modifier.weight(1.2f)) {
                                OutlinedTextField(
                                    value = evType.name,
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("Event Type") },
                                    trailingIcon = { Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = Color.White) },
                                    modifier = Modifier.fillMaxWidth().clickable { eventTypeDropdown = true },
                                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                                )
                                DropdownMenu(
                                    expanded = eventTypeDropdown,
                                    onDismissRequest = { eventTypeDropdown = false },
                                    modifier = Modifier.background(NavySurfaceLight)
                                ) {
                                    MatchEventType.values().forEach { type ->
                                        DropdownMenuItem(
                                            text = { Text(type.name, color = Color.White) },
                                            onClick = {
                                                evType = type
                                                eventTypeDropdown = false
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        OutlinedTextField(
                            value = evPlayer,
                            onValueChange = { evPlayer = it },
                            label = { Text("Player Name (Optional)") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                        )

                        OutlinedTextField(
                            value = evDesc,
                            onValueChange = { evDesc = it },
                            label = { Text("Description") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                        )

                        Button(
                            onClick = {
                                val min = evMin.toIntOrNull() ?: 0
                                if (evDesc.isNotEmpty()) {
                                    val newEv = MatchEvent(
                                        id = "event_" + UUID.randomUUID().toString().take(6),
                                        minute = min,
                                        type = evType,
                                        playerName = evPlayer,
                                        description = evDesc
                                    )
                                    eventsList = eventsList + newEv
                                    evMin = ""
                                    evPlayer = ""
                                    evDesc = ""
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = CyberBlue),
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Text("Add Event", fontSize = 11.sp)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val sA = scoreA.toIntOrNull() ?: 0
                    val sB = scoreB.toIntOrNull() ?: 0
                    
                    val updatedStats = MatchStatistics(
                        killsA = killsA.toIntOrNull() ?: 0,
                        killsB = killsB.toIntOrNull() ?: 0,
                        assistsA = assistsA.toIntOrNull() ?: 0,
                        assistsB = assistsB.toIntOrNull() ?: 0,
                        deathsA = deathsA.toIntOrNull() ?: 0,
                        deathsB = deathsB.toIntOrNull() ?: 0,
                        objectiveA = objectiveA.toIntOrNull() ?: 0,
                        objectiveB = objectiveB.toIntOrNull() ?: 0,
                        ratingA = ratingA.toDoubleOrNull() ?: 1.0,
                        ratingB = ratingB.toDoubleOrNull() ?: 1.0
                    )

                    val updated = match.copy(
                        scoreA = sA,
                        scoreB = sB,
                        status = status,
                        venue = venue,
                        referee = referee,
                        manOfTheMatch = manOfTheMatch,
                        statistics = updatedStats,
                        events = eventsList
                    )
                    onSave(updated)
                },
                colors = ButtonDefaults.buttonColors(containerColor = CyberBlue)
            ) {
                Text("Save", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
        }
    )
}

@Composable
fun NotificationFormDialog(
    onDismiss: () -> Unit,
    onSave: (String, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var msg by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = NavySurface,
        title = { Text("Broadcast Broadcast", color = Color.White, fontWeight = FontWeight.Bold) },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Alert Title") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = BorderColor,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = msg,
                    onValueChange = { msg = it },
                    label = { Text("Message Body") },
                    minLines = 3,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = BorderColor,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank() && msg.isNotBlank()) {
                        onSave(title, msg)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = CyberBlue)
            ) {
                Text("Broadcast", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
        }
    )
}
