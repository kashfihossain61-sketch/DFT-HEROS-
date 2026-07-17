package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.*
import com.example.ui.theme.*
import com.example.viewmodel.TournamentViewModel
import kotlinx.coroutines.delay
import java.text.NumberFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuctionDashboardScreen(
    viewModel: TournamentViewModel,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val currentUser by viewModel.currentUser.collectAsState()
    val teams by viewModel.teams.collectAsState()
    val auctionEvents by viewModel.auctionEvents.collectAsState()
    val auctionPlayers by viewModel.auctionPlayers.collectAsState()
    val auctionHistory by viewModel.auctionHistory.collectAsState()

    val isAdmin = currentUser?.role == "Admin"

    var selectedEventOverride by remember { mutableStateOf<AuctionEvent?>(null) }

    // Active Auction Event (Defaults to first active, or the absolute first event)
    val activeEvent = selectedEventOverride ?: remember(auctionEvents) {
        auctionEvents.find { it.isActive } ?: auctionEvents.firstOrNull()
    }

    var selectedTab by remember { mutableStateOf(0) } // 0: Live Arena, 1: Squads & Budgets, 2: Players Pool, 3: History Log

    // Dialog flags
    var showCreateEventDialog by remember { mutableStateOf(false) }
    var showAddPlayerDialog by remember { mutableStateOf(false) }
    var editingPlayer by remember { mutableStateOf<AuctionPlayer?>(null) }
    var showCreateTeamDialog by remember { mutableStateOf(false) }
    var editingTeam by remember { mutableStateOf<Team?>(null) }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .background(NavyDeep)
                    .statusBarsPadding()
                    .padding(horizontal = 24.dp, vertical = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        var eventDropdownExpanded by remember { mutableStateOf(false) }

                        Column(
                            modifier = Modifier.clickable { eventDropdownExpanded = true }
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = activeEvent?.name ?: "No Active Auction Event",
                                    color = Color.White,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = "Switch Event",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Text(
                                text = "Football Auction Arena",
                                color = NeonCyan,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )

                            DropdownMenu(
                                expanded = eventDropdownExpanded,
                                onDismissRequest = { eventDropdownExpanded = false },
                                modifier = Modifier.background(NavySurfaceLight)
                            ) {
                                if (auctionEvents.isEmpty()) {
                                    DropdownMenuItem(
                                        text = { Text("No events found", color = TextSecondary) },
                                        onClick = { eventDropdownExpanded = false }
                                    )
                                } else {
                                    auctionEvents.forEach { ev ->
                                        DropdownMenuItem(
                                            text = {
                                                Column {
                                                    Text(ev.name, color = Color.White, fontWeight = FontWeight.Bold)
                                                    Text("Budget: ${formatCurrency(ev.budgetPerTeam)}", color = TextSecondary, fontSize = 11.sp)
                                                }
                                            },
                                            onClick = {
                                                selectedEventOverride = ev
                                                eventDropdownExpanded = false
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Export Results Button
                        IconButton(
                            onClick = {
                                if (auctionPlayers.isEmpty()) {
                                    Toast.makeText(context, "No players to export.", Toast.LENGTH_SHORT).show()
                                } else {
                                    exportAuctionResults(context, activeEvent?.name ?: "Auction", auctionPlayers)
                                }
                            },
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(CyberBlue.copy(alpha = 0.15f))
                        ) {
                            Icon(Icons.Default.Share, contentDescription = "Export Results", tint = NeonCyan, modifier = Modifier.size(18.dp))
                        }

                        if (isAdmin) {
                            // Quick Action: Create Event & Add Players
                            IconButton(
                                onClick = { showCreateEventDialog = true },
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(NeonCyan.copy(alpha = 0.15f))
                                    .testTag("create_event_icon_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Event,
                                    contentDescription = "Create Event",
                                    tint = NeonCyan,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(4.dp))

                            Button(
                                onClick = { showAddPlayerDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = CyberBlue),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.height(36.dp).testTag("add_player_button")
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Add Player", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Custom Tab Headers
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    val tabItems = listOf("Live Arena", "Squads & Budgets", "Players Pool", "History Log")
                    tabItems.forEachIndexed { index, label ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clickable { selectedTab = index }
                                .padding(vertical = 4.dp)
                        ) {
                            Text(
                                text = label,
                                color = if (selectedTab == index) NeonCyan else TextSecondary,
                                fontSize = 14.sp,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Box(
                                modifier = Modifier
                                    .height(2.dp)
                                    .width(40.dp)
                                    .background(if (selectedTab == index) NeonCyan else Color.Transparent)
                            )
                        }
                    }
                }
            }
        },
        containerColor = NavyDeep
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (activeEvent == null) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, BorderColor, RoundedCornerShape(24.dp)),
                        colors = CardDefaults.cardColors(containerColor = NavySurface)
                    ) {
                        Column(
                            modifier = Modifier.padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Gavel,
                                contentDescription = null,
                                tint = NeonCyan,
                                modifier = Modifier.size(72.dp)
                            )
                            Text(
                                text = "No Auction Event Found",
                                color = Color.White,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = if (isAdmin) {
                                    "Set up your first football auction event to start registering players, managing team budgets, and running the live bidding arena."
                                } else {
                                    "There is no active auction event at the moment. Please check back later when an administrator starts the live auction."
                                },
                                color = TextSecondary,
                                fontSize = 14.sp,
                                textAlign = TextAlign.Center
                            )
                            if (isAdmin) {
                                Button(
                                    onClick = { showCreateEventDialog = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = CyberBlue),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.testTag("create_first_event_button")
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Create Auction Event", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            } else {
                when (selectedTab) {
                    0 -> LiveArenaTab(
                        viewModel = viewModel,
                        activeEvent = activeEvent,
                        auctionPlayers = auctionPlayers.filter { it.eventId == activeEvent.id },
                        teams = teams,
                        isAdmin = isAdmin
                    )
                    1 -> SquadsAndBudgetsTab(
                        viewModel = viewModel,
                        teams = teams,
                        auctionPlayers = auctionPlayers.filter { it.eventId == activeEvent.id },
                        isAdmin = isAdmin,
                        onEditTeam = { team ->
                            editingTeam = team
                        },
                        onCreateTeam = {
                            showCreateTeamDialog = true
                        }
                    )
                    2 -> PlayersPoolTab(
                        viewModel = viewModel,
                        auctionPlayers = auctionPlayers.filter { it.eventId == activeEvent.id },
                        isAdmin = isAdmin,
                        onEditPlayer = { player ->
                            editingPlayer = player
                        }
                    )
                    3 -> HistoryLogTab(
                        historyLogs = auctionHistory.filter { it.eventId == activeEvent.id }
                    )
                }
            }
        }
    }

    // Dialog implementations
    if (showAddPlayerDialog) {
        AddEditAuctionPlayerDialog(
            eventId = activeEvent?.id ?: "event_dft_2026",
            onDismiss = { showAddPlayerDialog = false },
            onSave = { newPlayer ->
                viewModel.addAuctionPlayer(newPlayer) {
                    Toast.makeText(context, "Player added to Auction Pool successfully!", Toast.LENGTH_SHORT).show()
                }
                showAddPlayerDialog = false
            }
        )
    }

    if (editingPlayer != null) {
        AddEditAuctionPlayerDialog(
            player = editingPlayer,
            eventId = activeEvent?.id ?: "event_dft_2026",
            onDismiss = { editingPlayer = null },
            onSave = { updatedPlayer ->
                viewModel.updateAuctionPlayer(updatedPlayer) {
                    Toast.makeText(context, "Player profile updated successfully!", Toast.LENGTH_SHORT).show()
                }
                editingPlayer = null
            }
        )
    }

    if (showCreateTeamDialog) {
        AddEditTeamDialog(
            onDismiss = { showCreateTeamDialog = false },
            onSave = { newTeam ->
                viewModel.createTeam(newTeam)
                Toast.makeText(context, "Team '${newTeam.name}' created successfully!", Toast.LENGTH_SHORT).show()
                showCreateTeamDialog = false
            }
        )
    }

    if (editingTeam != null) {
        AddEditTeamDialog(
            team = editingTeam,
            onDismiss = { editingTeam = null },
            onSave = { updatedTeam ->
                viewModel.updateTeam(updatedTeam)
                Toast.makeText(context, "Team updated successfully!", Toast.LENGTH_SHORT).show()
                editingTeam = null
            }
        )
    }

    if (showCreateEventDialog) {
        CreateAuctionEventDialog(
            onDismiss = { showCreateEventDialog = false },
            onSave = { newEvent ->
                viewModel.createAuctionEvent(newEvent) {
                    Toast.makeText(context, "Auction Event '${newEvent.name}' created!", Toast.LENGTH_SHORT).show()
                    selectedEventOverride = newEvent
                }
                showCreateEventDialog = false
            }
        )
    }
}

// ---------------- TIMER COMPONENT ----------------
@Composable
fun AuctionTimerCard(
    modifier: Modifier = Modifier
) {
    var timerSeconds by remember { mutableStateOf(60) }
    var isTimerRunning by remember { mutableStateOf(false) }

    LaunchedEffect(isTimerRunning, timerSeconds) {
        if (isTimerRunning && timerSeconds > 0) {
            delay(1000L)
            timerSeconds--
        } else if (timerSeconds == 0) {
            isTimerRunning = false
        }
    }

    Card(
        modifier = modifier
            .border(1.dp, BorderColor, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = NavySurface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("AUCTION TIMER", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            
            Text(
                text = String.format(Locale.getDefault(), "00:%02d", timerSeconds),
                color = if (timerSeconds <= 10) Color.Red else Color.White,
                fontSize = 36.sp,
                fontWeight = FontWeight.Black
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Start
                Button(
                    onClick = { isTimerRunning = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = "Start", tint = Color.White, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("START", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                // Pause
                Button(
                    onClick = { isTimerRunning = false },
                    colors = ButtonDefaults.buttonColors(containerColor = GoldTrophy),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Icon(Icons.Default.Pause, contentDescription = "Pause", tint = Color.Black, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("PAUSE", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                }

                // Reset
                Button(
                    onClick = {
                        isTimerRunning = false
                        timerSeconds = 60
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = "Reset", tint = Color.White, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("RESET", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// ---------------- TAB 1: LIVE ARENA TAB ----------------
@Composable
fun LiveArenaTab(
    viewModel: TournamentViewModel,
    activeEvent: AuctionEvent?,
    auctionPlayers: List<AuctionPlayer>,
    teams: List<Team>,
    isAdmin: Boolean
) {
    val context = LocalContext.current
    var currentIndex by remember { mutableStateOf(0) }

    val availablePlayers = remember(auctionPlayers) {
        auctionPlayers.filter { it.status == AuctionPlayerStatus.AVAILABLE }
    }

    if (availablePlayers.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(24.dp)) {
                Icon(Icons.Default.SportsFootball, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(72.dp))
                Spacer(modifier = Modifier.height(16.dp))
                Text("No Available Players Left in Pool!", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text(
                    text = "All players have been successfully Sold or marked as Unsold. View details in the Players Pool or History log tab.",
                    color = TextSecondary,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
        return
    }

    // Clamp index
    if (currentIndex >= availablePlayers.size) {
        currentIndex = availablePlayers.size - 1
    }
    if (currentIndex < 0) {
        currentIndex = 0
    }

    val currentPlayer = availablePlayers[currentIndex]

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Player Count Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "PLAYER ${currentPlayer.auctionOrder} OF ${auctionPlayers.size}",
                color = NeonCyan,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            // Pagination Controls
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IconButton(
                    onClick = { if (currentIndex > 0) currentIndex-- },
                    enabled = currentIndex > 0,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(if (currentIndex > 0) NavySurfaceLight else NavySurfaceLight.copy(alpha = 0.3f))
                ) {
                    Icon(Icons.Default.ChevronLeft, contentDescription = "Previous", tint = Color.White)
                }

                IconButton(
                    onClick = { if (currentIndex < availablePlayers.size - 1) currentIndex++ },
                    enabled = currentIndex < availablePlayers.size - 1,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(if (currentIndex < availablePlayers.size - 1) NavySurfaceLight else NavySurfaceLight.copy(alpha = 0.3f))
                ) {
                    Icon(Icons.Default.ChevronRight, contentDescription = "Next", tint = Color.White)
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Immersive Player Spotlight Card
            Card(
                modifier = Modifier
                    .weight(1.3f)
                    .border(1.dp, BorderColor, RoundedCornerShape(24.dp)),
                colors = CardDefaults.cardColors(containerColor = NavySurface)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.Black),
                        contentAlignment = Alignment.Center
                    ) {
                        if (!currentPlayer.photoUrl.isBlank()) {
                            AsyncImage(
                                model = currentPlayer.photoUrl,
                                contentDescription = currentPlayer.name,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Icon(Icons.Default.Person, contentDescription = null, tint = Color.DarkGray, modifier = Modifier.size(96.dp))
                        }

                        // Gradient overlay
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f)),
                                        startY = 200f
                                    )
                                )
                        )

                        // Position badge on top right
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(12.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(CyberBlue)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = currentPlayer.position.uppercase(),
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = currentPlayer.name,
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Detail Specs Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        SpecChip(label = "Age", value = "${currentPlayer.age} yrs", icon = Icons.Default.CalendarToday)
                        SpecChip(label = "Prev Team", value = currentPlayer.previousTeam, icon = Icons.Default.Sports)
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Divider(color = BorderColor.copy(alpha = 0.3f))
                    Spacer(modifier = Modifier.height(16.dp))

                    Text("BASE PRICE", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Text(
                        text = formatCurrency(currentPlayer.basePrice),
                        color = GoldTrophy,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }

            // Timer & Live stats Panel
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                AuctionTimerCard(modifier = Modifier.fillMaxWidth())

                // Dynamic bidding guidelines or current budget checker
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, BorderColor, RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = NavySurface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("TEAM SPENDING BUDGETS", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        teams.take(3).forEach { team ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(team.name, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                Text(formatCurrency(team.remainingBudget), color = NeonCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // Admin bidding controller
        if (isAdmin) {
            AdminBiddingControlsCard(
                currentPlayer = currentPlayer,
                teams = teams,
                onBidRecorded = { winningTeamId, bidAmount ->
                    viewModel.recordAuctionBid(currentPlayer, winningTeamId, bidAmount) { success, message ->
                        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                        if (success) {
                            // Automatically advance to next available player if successful
                            if (currentIndex < availablePlayers.size - 1) {
                                currentIndex++
                            }
                        }
                    }
                }
            )
        } else {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, BorderColor, RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = NavySurface)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Info, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Viewing as member. Only Admins can enter final bids and manage this live auction panel.",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@Composable
fun SpecChip(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(NavySurfaceLight)
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(12.dp))
            Column {
                Text(label, color = TextSecondary, fontSize = 9.sp)
                Text(value, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminBiddingControlsCard(
    currentPlayer: AuctionPlayer,
    teams: List<Team>,
    onBidRecorded: (String, Double) -> Unit
) {
    var bidAmountStr by remember { mutableStateOf(currentPlayer.basePrice.toLong().toString()) }
    var selectedTeamId by remember { mutableStateOf("") }
    var teamDropdownExpanded by remember { mutableStateOf(false) }

    val selectedTeam = remember(selectedTeamId, teams) {
        teams.find { it.id == selectedTeamId }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, GoldTrophy.copy(alpha = 0.3f), RoundedCornerShape(20.dp)),
        colors = CardDefaults.cardColors(containerColor = NavySurface)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "ADMIN LIVE BIDDING DESK",
                color = GoldTrophy,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Manually enter bid amount
                OutlinedTextField(
                    value = bidAmountStr,
                    onValueChange = { bidAmountStr = it },
                    label = { Text("Final Bid Amount ($)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = BorderColor,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier.weight(1f).testTag("bid_amount_input")
                )

                // Select winning team
                Box(modifier = Modifier.weight(1f)) {
                    OutlinedTextField(
                        value = selectedTeam?.name ?: "Select Team",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Winning Team") },
                        trailingIcon = {
                            IconButton(onClick = { teamDropdownExpanded = true }) {
                                Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = Color.White)
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonCyan,
                            unfocusedBorderColor = BorderColor,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth().clickable { teamDropdownExpanded = true }.testTag("winning_team_input")
                    )

                    DropdownMenu(
                        expanded = teamDropdownExpanded,
                        onDismissRequest = { teamDropdownExpanded = false },
                        modifier = Modifier.background(NavySurfaceLight).fillMaxWidth()
                    ) {
                        teams.forEach { team ->
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text(team.name, color = Color.White)
                                        Text("Budget left: ${formatCurrency(team.remainingBudget)}", color = TextSecondary, fontSize = 11.sp)
                                    }
                                },
                                onClick = {
                                    selectedTeamId = team.id
                                    teamDropdownExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Mark Unsold Button
                Button(
                    onClick = {
                        onBidRecorded("", 0.0)
                        bidAmountStr = currentPlayer.basePrice.toLong().toString()
                        selectedTeamId = ""
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f).height(48.dp).testTag("mark_unsold_button")
                ) {
                    Text("MARK AS UNSOLD", color = Color.White, fontWeight = FontWeight.Bold)
                }

                // Sell / Record Bid
                Button(
                    onClick = {
                        val finalBid = bidAmountStr.toDoubleOrNull()
                        if (selectedTeamId.isBlank()) {
                            onBidRecorded("", 0.0) // treat as unsold
                        } else if (finalBid == null) {
                            // invalid bid
                        } else {
                            onBidRecorded(selectedTeamId, finalBid)
                            bidAmountStr = ""
                            selectedTeamId = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GoldTrophy),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1.2f).height(48.dp).testTag("sell_player_button")
                ) {
                    Text("RECORD SOLD", color = Color.Black, fontWeight = FontWeight.Black)
                }
            }
        }
    }
}

// ---------------- TAB 2: SQUADS AND BUDGETS ----------------
@Composable
fun SquadsAndBudgetsTab(
    viewModel: TournamentViewModel,
    teams: List<Team>,
    auctionPlayers: List<AuctionPlayer>,
    isAdmin: Boolean,
    onEditTeam: (Team) -> Unit,
    onCreateTeam: () -> Unit
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        if (isAdmin) {
            Button(
                onClick = onCreateTeam,
                colors = ButtonDefaults.buttonColors(containerColor = CyberBlue),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
                    .testTag("create_team_button")
            ) {
                Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Create New Team", fontWeight = FontWeight.Bold, color = Color.White)
            }
        }

        if (teams.isEmpty()) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text("No teams registered in the system yet.", color = TextSecondary)
            }
            return
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(teams) { team ->
                val acquiredPlayers = remember(auctionPlayers, team.id) {
                    auctionPlayers.filter { it.winningTeamId == team.id && it.status == AuctionPlayerStatus.SOLD }
                }

                val spentAmount = remember(acquiredPlayers) {
                    acquiredPlayers.sumOf { it.soldPrice }
                }

                val spentPercent = if (team.budget > 0) (spentAmount / team.budget).toFloat().coerceIn(0f, 1f) else 0f

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, BorderColor, RoundedCornerShape(20.dp)),
                    colors = CardDefaults.cardColors(containerColor = NavySurface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(CircleShape)
                                        .background(CyberBlue.copy(alpha = 0.15f))
                                        .border(1.dp, NeonCyan.copy(alpha = 0.5f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (!team.logoUrl.isNullOrBlank()) {
                                        AsyncImage(
                                            model = team.logoUrl,
                                            contentDescription = "Team Logo",
                                            modifier = Modifier.fillMaxSize(),
                                            contentScale = ContentScale.Crop
                                        )
                                    } else {
                                        Icon(Icons.Default.Group, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(24.dp))
                                    }
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(team.name, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                    Text("Squad Strength: ${acquiredPlayers.size} Players", color = TextSecondary, fontSize = 11.sp)
                                }
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Column(horizontalAlignment = Alignment.End) {
                                    Text("REMAINING BUDGET", color = TextSecondary, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    Text(formatCurrency(team.remainingBudget), color = NeonCyan, fontSize = 15.sp, fontWeight = FontWeight.Black)
                                }

                                if (isAdmin) {
                                    IconButton(
                                        onClick = { onEditTeam(team) },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(Icons.Default.Edit, contentDescription = "Edit Team", tint = NeonCyan, modifier = Modifier.size(16.dp))
                                    }
                                    IconButton(
                                        onClick = {
                                            viewModel.deleteTeam(team.id)
                                            Toast.makeText(context, "Team '${team.name}' deleted.", Toast.LENGTH_SHORT).show()
                                        },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete Team", tint = Color.Red, modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Team Manager & Captain detail row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("TEAM MANAGER", color = TextSecondary, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                Text(
                                    text = if (team.managerName.isNotBlank()) team.managerName else "Not Set",
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text("TEAM CAPTAIN", color = TextSecondary, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                Text(
                                    text = if (team.captainName.isNotBlank()) team.captainName else "Not Set",
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Spending Progress Bar
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Spent: ${formatCurrency(spentAmount)}", color = TextSecondary, fontSize = 10.sp)
                                Text("Total Budget: ${formatCurrency(team.budget)}", color = TextSecondary, fontSize = 10.sp)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            LinearProgressIndicator(
                                progress = spentPercent,
                                color = if (spentPercent > 0.85f) Color.Red else CyberBlue,
                                trackColor = BorderColor,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp))
                            )
                        }

                        if (acquiredPlayers.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("ACQUIRED PLAYERS (${acquiredPlayers.size})", color = TextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                            Spacer(modifier = Modifier.height(8.dp))

                            // Horizontally scrolling list of bought players
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                acquiredPlayers.forEach { player ->
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(NavySurfaceLight)
                                            .border(1.dp, BorderColor, RoundedCornerShape(12.dp))
                                            .padding(horizontal = 10.dp, vertical = 8.dp)
                                    ) {
                                        Column {
                                            Text(player.name, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                            Text("${player.position}  •  ${formatCurrency(player.soldPrice)}", color = GoldTrophy, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
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
}// ---------------- TAB 3: PLAYERS POOL TAB ----------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayersPoolTab(
    viewModel: TournamentViewModel,
    auctionPlayers: List<AuctionPlayer>,
    isAdmin: Boolean,
    onEditPlayer: (AuctionPlayer) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf<AuctionPlayerStatus?>(null) }
    var activeSubTab by remember { mutableStateOf(0) } // 0: Live Pool, 1: Pending Registrations (Admin Only)
    var expandedPlayerId by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    val generalPlayers by viewModel.players.collectAsState()
    val auctionEvents by viewModel.auctionEvents.collectAsState()
    val activeEvent = remember(auctionEvents) {
        auctionEvents.find { it.isActive } ?: auctionEvents.firstOrNull()
    }

    val filteredList = remember(auctionPlayers, searchQuery, selectedFilter) {
        auctionPlayers.filter { player ->
            val matchesQuery = player.name.contains(searchQuery, ignoreCase = true) ||
                    player.position.contains(searchQuery, ignoreCase = true) ||
                    player.previousTeam.contains(searchQuery, ignoreCase = true)
            val matchesFilter = selectedFilter == null || player.status == selectedFilter
            matchesQuery && matchesFilter
        }
    }

    val pendingRegistrations = remember(generalPlayers, searchQuery) {
        generalPlayers.filter { player ->
            val matchesQuery = player.fullName.contains(searchQuery, ignoreCase = true) ||
                    player.position.contains(searchQuery, ignoreCase = true)
            val isPending = player.status == PlayerStatus.PENDING
            matchesQuery && isPending
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        if (isAdmin) {
            TabRow(
                selectedTabIndex = activeSubTab,
                containerColor = Color.Transparent,
                contentColor = NeonCyan,
                divider = { Spacer(modifier = Modifier.height(1.dp).background(BorderColor)) },
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Tab(
                    selected = activeSubTab == 0,
                    onClick = { activeSubTab = 0 },
                    text = { Text("Live Pool (${auctionPlayers.size})", fontWeight = FontWeight.Bold, fontSize = 13.sp) }
                )
                Tab(
                    selected = activeSubTab == 1,
                    onClick = { activeSubTab = 1 },
                    text = { Text("Pending Registrations (${pendingRegistrations.size})", fontWeight = FontWeight.Bold, fontSize = 13.sp) }
                )
            }
        } else {
            // For general users, default to Live Pool
            activeSubTab = 0
        }

        // Search text field
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text(if (activeSubTab == 0) "Search by name, position or prev team" else "Search pending by name or position") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextSecondary) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = NeonCyan,
                unfocusedBorderColor = BorderColor,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            ),
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
        )

        if (activeSubTab == 0) {
            // Filter chips row (Only relevant for Live Pool)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedFilter == null,
                    onClick = { selectedFilter = null },
                    label = { Text("All (${auctionPlayers.size})") },
                    colors = FilterChipDefaults.filterChipColors(labelColor = Color.White, selectedContainerColor = CyberBlue)
                )

                val availableCount = auctionPlayers.count { it.status == AuctionPlayerStatus.AVAILABLE }
                FilterChip(
                    selected = selectedFilter == AuctionPlayerStatus.AVAILABLE,
                    onClick = { selectedFilter = AuctionPlayerStatus.AVAILABLE },
                    label = { Text("Available ($availableCount)") },
                    colors = FilterChipDefaults.filterChipColors(labelColor = Color.White, selectedContainerColor = NeonCyan)
                )

                val soldCount = auctionPlayers.count { it.status == AuctionPlayerStatus.SOLD }
                FilterChip(
                    selected = selectedFilter == AuctionPlayerStatus.SOLD,
                    onClick = { selectedFilter = AuctionPlayerStatus.SOLD },
                    label = { Text("Sold ($soldCount)") },
                    colors = FilterChipDefaults.filterChipColors(labelColor = Color.White, selectedContainerColor = Color(0xFF4CAF50))
                )

                val unsoldCount = auctionPlayers.count { it.status == AuctionPlayerStatus.UNSOLD }
                FilterChip(
                    selected = selectedFilter == AuctionPlayerStatus.UNSOLD,
                    onClick = { selectedFilter = AuctionPlayerStatus.UNSOLD },
                    label = { Text("Unsold ($unsoldCount)") },
                    colors = FilterChipDefaults.filterChipColors(labelColor = Color.White, selectedContainerColor = Color(0xFFF44336))
                )
            }

            if (filteredList.isEmpty()) {
                Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                    Text("No players found matching current filters.", color = TextSecondary)
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(filteredList) { player ->
                        val isExpanded = expandedPlayerId == player.id
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, BorderColor, RoundedCornerShape(16.dp))
                                .clickable { expandedPlayerId = if (isExpanded) null else player.id },
                            colors = CardDefaults.cardColors(containerColor = NavySurface)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(50.dp)
                                            .clip(CircleShape)
                                            .background(CyberBlue.copy(alpha = 0.15f))
                                            .border(1.dp, NeonCyan, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (!player.photoUrl.isBlank()) {
                                            AsyncImage(
                                                model = player.photoUrl,
                                                contentDescription = player.name,
                                                modifier = Modifier.fillMaxSize(),
                                                contentScale = ContentScale.Crop
                                            )
                                        } else {
                                            Icon(Icons.Default.Person, contentDescription = null, tint = Color.White)
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(player.name, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                                        Text("${player.position} • Age ${player.age} • Prev: ${player.previousTeam}", color = TextSecondary, fontSize = 11.sp)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text("Base Price: ${formatCurrency(player.basePrice)}", color = GoldTrophy, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                            if (player.status == AuctionPlayerStatus.SOLD) {
                                                Text(" • Sold Price: ${formatCurrency(player.soldPrice)}", color = Color(0xFF4CAF50), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }

                                    // Status Column & Admin Controls
                                    Column(
                                        horizontalAlignment = Alignment.End,
                                        verticalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        val (statusText, statusColor) = when (player.status) {
                                            AuctionPlayerStatus.AVAILABLE -> "AVAILABLE" to NeonCyan
                                            AuctionPlayerStatus.SOLD -> "SOLD to ${player.winningTeamName}" to Color(0xFF4CAF50)
                                            AuctionPlayerStatus.UNSOLD -> "UNSOLD" to Color(0xFFF44336)
                                        }

                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(statusColor.copy(alpha = 0.15f))
                                                .border(1.dp, statusColor, RoundedCornerShape(6.dp))
                                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                        ) {
                                            Text(statusText, color = statusColor, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                        }

                                        if (isAdmin) {
                                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                                // Edit button
                                                IconButton(
                                                    onClick = { onEditPlayer(player) },
                                                    modifier = Modifier.size(24.dp)
                                                ) {
                                                    Icon(Icons.Default.Edit, contentDescription = "Edit Player", tint = NeonCyan, modifier = Modifier.size(14.dp))
                                                }

                                                // Reset player status to available
                                                if (player.status != AuctionPlayerStatus.AVAILABLE) {
                                                    IconButton(
                                                        onClick = {
                                                            viewModel.updateAuctionPlayer(player.copy(status = AuctionPlayerStatus.AVAILABLE, soldPrice = 0.0, winningTeamId = "", winningTeamName = ""))
                                                            Toast.makeText(context, "Player reset to Available status.", Toast.LENGTH_SHORT).show()
                                                        },
                                                        modifier = Modifier.size(24.dp)
                                                    ) {
                                                        Icon(Icons.Default.Restore, contentDescription = "Reset Player", tint = Color.LightGray, modifier = Modifier.size(14.dp))
                                                    }
                                                }

                                                // Delete player from auction pool
                                                IconButton(
                                                    onClick = {
                                                        viewModel.deleteAuctionPlayer(player.id) {
                                                            Toast.makeText(context, "Player deleted from pool.", Toast.LENGTH_SHORT).show()
                                                        }
                                                    },
                                                    modifier = Modifier.size(24.dp)
                                                ) {
                                                    Icon(Icons.Default.Delete, contentDescription = "Delete Player", tint = Color.Red, modifier = Modifier.size(14.dp))
                                                }
                                            }
                                        }
                                    }
                                }

                                // Expandable player profile detail fields
                                AnimatedVisibility(visible = isExpanded) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(top = 12.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(NavySurfaceLight)
                                            .padding(12.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Text("FULL PROFILE DETAILS", color = NeonCyan, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                            Text("Gmail Address:", color = TextSecondary, fontSize = 11.sp)
                                            Text(if (!player.gmail.isNullOrBlank()) player.gmail else "N/A", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                                        }
                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                            Text("Phone Number:", color = TextSecondary, fontSize = 11.sp)
                                            Text(if (!player.phoneNumber.isNullOrBlank()) player.phoneNumber else "N/A", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                                        }
                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                            Text("Jersey Number:", color = TextSecondary, fontSize = 11.sp)
                                            Text(if (player.jerseyNumber > 0) "#${player.jerseyNumber}" else "N/A", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                                        }
                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                            Text("Date of Birth:", color = TextSecondary, fontSize = 11.sp)
                                            Text(if (!player.dob.isNullOrBlank()) player.dob else "N/A", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                                        }
                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                            Text("Auction Order / No:", color = TextSecondary, fontSize = 11.sp)
                                            Text("${player.auctionOrder}", color = GoldTrophy, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // Pending registrations list (Admin-only sub tab)
            if (pendingRegistrations.isEmpty()) {
                Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                    Text("No pending player registrations to approve.", color = TextSecondary)
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(pendingRegistrations) { player ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, BorderColor, RoundedCornerShape(16.dp)),
                            colors = CardDefaults.cardColors(containerColor = NavySurface)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(54.dp)
                                            .clip(CircleShape)
                                            .background(CyberBlue.copy(alpha = 0.15f))
                                            .border(1.dp, NeonCyan, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (!player.profilePhotoUrl.isNullOrBlank()) {
                                            AsyncImage(
                                                model = player.profilePhotoUrl,
                                                contentDescription = player.fullName,
                                                modifier = Modifier.fillMaxSize(),
                                                contentScale = ContentScale.Crop
                                            )
                                        } else {
                                            Icon(Icons.Default.Person, contentDescription = null, tint = Color.White)
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(player.fullName, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                                        Text("Position: ${player.position}  •  Jersey: #${player.jerseyNumber}", color = TextSecondary, fontSize = 11.sp)
                                        Text("DOB: ${player.dob}", color = TextSecondary, fontSize = 11.sp)
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                // Registration Contact info block
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(NavySurfaceLight)
                                        .padding(10.dp),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text("CONTACT INFO", color = NeonCyan, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    Text("Gmail: ${player.gmail}", color = Color.White, fontSize = 11.sp)
                                    Text("Phone: ${player.phoneNumber}", color = Color.White, fontSize = 11.sp)
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Button(
                                        onClick = {
                                            // Reject/Delete Registration
                                            viewModel.deletePlayer(player.id)
                                            Toast.makeText(context, "Registration rejected/deleted.", Toast.LENGTH_SHORT).show()
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF44336)),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("Reject", color = Color.White, fontWeight = FontWeight.Bold)
                                    }

                                    Button(
                                        onClick = {
                                            // Approve Registration
                                            val updated = player.copy(status = PlayerStatus.APPROVED)
                                            viewModel.updatePlayer(updated)

                                            // Automatically add player into live Auction Pool!
                                            val eventId = activeEvent?.id ?: "event_dft_2026"
                                            val auctionPlayer = AuctionPlayer(
                                                id = "ap_" + UUID.randomUUID().toString().take(6),
                                                eventId = eventId,
                                                name = player.fullName,
                                                position = player.position,
                                                age = 23, // Default age estimation
                                                previousTeam = "Free Agent",
                                                basePrice = 100000.0,
                                                status = AuctionPlayerStatus.AVAILABLE,
                                                photoUrl = if (!player.profilePhotoUrl.isNullOrBlank()) player.profilePhotoUrl else "https://images.unsplash.com/photo-1508098682722-e99c43a406b2",
                                                auctionOrder = (auctionPlayers.maxOfOrNull { it.auctionOrder } ?: 0) + 1,
                                                gmail = player.gmail,
                                                phoneNumber = player.phoneNumber,
                                                jerseyNumber = player.jerseyNumber,
                                                dob = player.dob
                                            )
                                            viewModel.addAuctionPlayer(auctionPlayer) {
                                                Toast.makeText(context, "Registration Approved! Player '${player.fullName}' added to Auction Pool.", Toast.LENGTH_LONG).show()
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.weight(1.2f)
                                    ) {
                                        Text("Approve & Pool", color = Color.White, fontWeight = FontWeight.Bold)
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

// ---------------- TAB 4: HISTORY LOG TAB ----------------
@Composable
fun HistoryLogTab(
    historyLogs: List<AuctionHistoryLog>
) {
    if (historyLogs.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No auction events logged yet.", color = TextSecondary)
        }
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(historyLogs) { log ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, BorderColor, RoundedCornerShape(12.dp)),
                colors = CardDefaults.cardColors(containerColor = NavySurface)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val isSold = log.action == "SOLD"
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(if (isSold) Color(0xFF4CAF50).copy(alpha = 0.15f) else Color(0xFFF44336).copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isSold) Icons.Default.Check else Icons.Default.Close,
                            contentDescription = null,
                            tint = if (isSold) Color(0xFF4CAF50) else Color(0xFFF44336),
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isSold) "${log.playerName} SOLD" else "${log.playerName} UNSOLD",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (isSold) "Purchased by ${log.winningTeamName} for ${formatCurrency(log.bidAmount)}" else "No bids received. Returned to pool.",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = java.text.SimpleDateFormat("hh:mm:ss a (MM/dd)", Locale.getDefault()).format(Date(log.timestamp)),
                            color = TextSecondary,
                            fontSize = 9.sp
                        )
                    }
                }
            }
        }
    }
}

// ---------------- ADMIN DIALOG: ADD/EDIT PLAYER ----------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditAuctionPlayerDialog(
    player: AuctionPlayer? = null,
    eventId: String,
    onDismiss: () -> Unit,
    onSave: (AuctionPlayer) -> Unit
) {
    var name by remember { mutableStateOf(player?.name ?: "") }
    var position by remember { mutableStateOf(player?.position ?: "Forward") }
    var ageStr by remember { mutableStateOf(player?.age?.toString() ?: "") }
    var prevTeam by remember { mutableStateOf(player?.previousTeam ?: "") }
    var basePriceStr by remember { mutableStateOf(player?.basePrice?.toLong()?.toString() ?: "") }
    var photoUrl by remember { mutableStateOf(player?.photoUrl ?: "") }
    var auctionOrderStr by remember { mutableStateOf(player?.auctionOrder?.toString() ?: "") }

    var gmail by remember { mutableStateOf(player?.gmail ?: "") }
    var phoneNumber by remember { mutableStateOf(player?.phoneNumber ?: "") }
    var jerseyNumberStr by remember { mutableStateOf(player?.jerseyNumber?.toString() ?: "") }
    var dob by remember { mutableStateOf(player?.dob ?: "") }

    var positionDropdownExpanded by remember { mutableStateOf(false) }
    val positionsList = listOf("Forward", "Midfielder", "Defender", "Goalkeeper")

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = NavySurface,
        title = { Text(if (player == null) "Add Player to Auction Pool" else "Edit Player Profile", color = Color.White, fontWeight = FontWeight.Bold) },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 420.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Player Name") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = BorderColor,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("add_player_name")
                )

                OutlinedTextField(
                    value = gmail,
                    onValueChange = { gmail = it },
                    label = { Text("Gmail Address") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = BorderColor,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("add_player_gmail")
                )

                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = { phoneNumber = it },
                    label = { Text("Phone Number") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = BorderColor,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("add_player_phone")
                )

                // Position Dropdown
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = position,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Player Position") },
                        trailingIcon = {
                            IconButton(onClick = { positionDropdownExpanded = true }) {
                                Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = Color.White)
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonCyan,
                            unfocusedBorderColor = BorderColor,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth().clickable { positionDropdownExpanded = true }
                    )
                    DropdownMenu(
                        expanded = positionDropdownExpanded,
                        onDismissRequest = { positionDropdownExpanded = false },
                        modifier = Modifier.background(NavySurfaceLight).fillMaxWidth()
                    ) {
                        positionsList.forEach { pos ->
                            DropdownMenuItem(
                                text = { Text(pos, color = Color.White) },
                                onClick = {
                                    position = pos
                                    positionDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = jerseyNumberStr,
                    onValueChange = { jerseyNumberStr = it },
                    label = { Text("Jersey Number") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = BorderColor,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("add_player_jersey")
                )

                OutlinedTextField(
                    value = ageStr,
                    onValueChange = { ageStr = it },
                    label = { Text("Player Age") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = BorderColor,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("add_player_age")
                )

                OutlinedTextField(
                    value = dob,
                    onValueChange = { dob = it },
                    label = { Text("Date of Birth (YYYY-MM-DD)") },
                    placeholder = { Text("e.g. 2001-09-25", color = Color.Gray) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = BorderColor,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("add_player_dob")
                )

                OutlinedTextField(
                    value = prevTeam,
                    onValueChange = { prevTeam = it },
                    label = { Text("Previous Team / Club") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = BorderColor,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("add_player_prev")
                )

                OutlinedTextField(
                    value = basePriceStr,
                    onValueChange = { basePriceStr = it },
                    label = { Text("Base Price ($)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = BorderColor,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("add_player_base")
                )

                OutlinedTextField(
                    value = auctionOrderStr,
                    onValueChange = { auctionOrderStr = it },
                    label = { Text("Auction Order # (Player Number)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = BorderColor,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("add_player_order")
                )

                OutlinedTextField(
                    value = photoUrl,
                    onValueChange = { photoUrl = it },
                    label = { Text("Photo URL (Optional)") },
                    placeholder = { Text("Unsplash image link", color = Color.Gray) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = BorderColor,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("add_player_photo_url")
                )

                // Quick image suggestion presets
                Text("Tap quick photo preset:", color = TextSecondary, fontSize = 11.sp)
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val photoPresets = listOf(
                        "https://images.unsplash.com/photo-1508098682722-e99c43a406b2" to "Striker",
                        "https://images.unsplash.com/photo-1544005313-94ddf0286df2" to "Playmaker",
                        "https://images.unsplash.com/photo-1519085360753-af0119f7cbe7" to "Veteran",
                        "https://images.unsplash.com/photo-1500648767791-00dcc994a43e" to "Captain"
                    )
                    photoPresets.forEach { (url, label) ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (photoUrl == url) CyberBlue else NavySurfaceLight)
                                .border(1.dp, if (photoUrl == url) NeonCyan else BorderColor, RoundedCornerShape(8.dp))
                                .clickable { photoUrl = url }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(label, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val age = ageStr.toIntOrNull() ?: 22
                    val basePrice = basePriceStr.toDoubleOrNull() ?: 500000.0
                    val orderNum = auctionOrderStr.toIntOrNull() ?: 1
                    val jerseyNum = jerseyNumberStr.toIntOrNull() ?: 0
                    if (name.isNotBlank()) {
                        onSave(
                            (player ?: AuctionPlayer()).copy(
                                eventId = eventId,
                                name = name,
                                position = position,
                                age = age,
                                previousTeam = prevTeam,
                                basePrice = basePrice,
                                photoUrl = if (photoUrl.isBlank()) "https://images.unsplash.com/photo-1508098682722-e99c43a406b2" else photoUrl,
                                auctionOrder = orderNum,
                                gmail = gmail,
                                phoneNumber = phoneNumber,
                                jerseyNumber = jerseyNum,
                                dob = dob
                            )
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = CyberBlue),
                modifier = Modifier.testTag("submit_player_dialog")
            ) {
                Text(if (player == null) "Add Player" else "Save Changes", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
        }
    )
}

// ---------------- ADMIN DIALOG: ADD/EDIT TEAM ----------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditTeamDialog(
    team: Team? = null,
    onDismiss: () -> Unit,
    onSave: (Team) -> Unit
) {
    var name by remember { mutableStateOf(team?.name ?: "") }
    var logoUrl by remember { mutableStateOf(team?.logoUrl ?: "") }
    var budgetStr by remember { mutableStateOf(team?.budget?.toLong()?.toString() ?: "100000000") }
    var managerName by remember { mutableStateOf(team?.managerName ?: "") }
    var captainName by remember { mutableStateOf(team?.captainName ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = NavySurface,
        title = { Text(if (team == null) "Create Team" else "Edit Team", color = Color.White, fontWeight = FontWeight.Bold) },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Team Name") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = BorderColor,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("team_name_input")
                )

                OutlinedTextField(
                    value = managerName,
                    onValueChange = { managerName = it },
                    label = { Text("Team Manager") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = BorderColor,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("team_manager_input")
                )

                OutlinedTextField(
                    value = captainName,
                    onValueChange = { captainName = it },
                    label = { Text("Captain Name") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = BorderColor,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("team_captain_input")
                )

                OutlinedTextField(
                    value = budgetStr,
                    onValueChange = { budgetStr = it },
                    label = { Text("Team Budget ($)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = BorderColor,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("team_budget_input")
                )

                OutlinedTextField(
                    value = logoUrl,
                    onValueChange = { logoUrl = it },
                    label = { Text("Logo URL (Optional)") },
                    placeholder = { Text("e.g. Image URL", color = Color.Gray) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = BorderColor,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("team_logo_input")
                )

                // Suggest presets of logos
                Text("Tap quick logo preset:", color = TextSecondary, fontSize = 11.sp)
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val logoPresets = listOf(
                        "https://images.unsplash.com/photo-1508098682722-e99c43a406b2" to "Blue Shield",
                        "https://images.unsplash.com/photo-1544005313-94ddf0286df2" to "Red Crest",
                        "https://images.unsplash.com/photo-1519085360753-af0119f7cbe7" to "Gold Star"
                    )
                    logoPresets.forEach { (url, label) ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (logoUrl == url) CyberBlue else NavySurfaceLight)
                                .border(1.dp, if (logoUrl == url) NeonCyan else BorderColor, RoundedCornerShape(8.dp))
                                .clickable { logoUrl = url }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(label, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val budgetVal = budgetStr.toDoubleOrNull() ?: 100000000.0
                    if (name.isNotBlank()) {
                        val finalTeam = team?.copy(
                            name = name,
                            logoUrl = logoUrl,
                            budget = budgetVal,
                            remainingBudget = if (team == null) budgetVal else (budgetVal - (team.budget - team.remainingBudget)),
                            managerName = managerName,
                            captainName = captainName
                        ) ?: Team(
                            id = "team_" + UUID.randomUUID().toString().take(6),
                            name = name,
                            logoUrl = logoUrl,
                            budget = budgetVal,
                            remainingBudget = budgetVal,
                            managerName = managerName,
                            captainName = captainName,
                            status = TeamStatus.APPROVED
                        )
                        onSave(finalTeam)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = CyberBlue),
                modifier = Modifier.testTag("team_dialog_submit")
            ) {
                Text(if (team == null) "Create Team" else "Save Changes", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
        }
    )
}

// ---------------- ADMIN DIALOG: CREATE AUCTION EVENT ----------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateAuctionEventDialog(
    onDismiss: () -> Unit,
    onSave: (AuctionEvent) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var budgetStr by remember { mutableStateOf("100000000") } // Default 100,000,000

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = NavySurface,
        title = { Text("Create Auction Event", color = Color.White, fontWeight = FontWeight.Bold) },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Event Name") },
                    placeholder = { Text("e.g. DFT Super League 2026") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = BorderColor,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("create_event_name_input")
                )

                OutlinedTextField(
                    value = budgetStr,
                    onValueChange = { budgetStr = it },
                    label = { Text("Budget per Team ($)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = BorderColor,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("create_event_budget_input")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val budget = budgetStr.toDoubleOrNull() ?: 100000000.0
                    if (name.isNotBlank()) {
                        onSave(
                            AuctionEvent(
                                id = "event_" + UUID.randomUUID().toString().take(6),
                                name = name,
                                budgetPerTeam = budget,
                                isActive = true,
                                createdAt = System.currentTimeMillis()
                            )
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = CyberBlue),
                modifier = Modifier.testTag("submit_create_event_button")
            ) {
                Text("Create Event", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
        }
    )
}

// ---------------- UTILS & EXPORT FUNCTION ----------------
fun formatCurrency(amount: Double): String {
    val formatter = NumberFormat.getCurrencyInstance(Locale.US)
    formatter.maximumFractionDigits = 0
    return formatter.format(amount)
}

fun exportAuctionResults(context: Context, eventName: String, players: List<AuctionPlayer>) {
    try {
        val csvHeader = "Auction Order,Player Name,Position,Age,Previous Team,Base Price,Sold Price,Status,Winning Team\n"
        val csvBody = players.joinToString("\n") { p ->
            val statusStr = when (p.status) {
                AuctionPlayerStatus.AVAILABLE -> "Available"
                AuctionPlayerStatus.SOLD -> "Sold"
                AuctionPlayerStatus.UNSOLD -> "Unsold"
            }
            "${p.auctionOrder},\"${p.name}\",\"${p.position}\",${p.age},\"${p.previousTeam}\",${p.basePrice},${p.soldPrice},\"$statusStr\",\"${p.winningTeamName}\""
        }

        val shareContent = "--- FOOTBALL AUCTION RESULTS ---\n" +
                "Event: $eventName\n" +
                "Exported At: ${java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())}\n\n" +
                csvHeader + csvBody

        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_SUBJECT, "$eventName - Auction Report")
            putExtra(Intent.EXTRA_TEXT, shareContent)
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, "Share Auction Report")
        context.startActivity(shareIntent)
    } catch (e: Exception) {
        Toast.makeText(context, "Error sharing report: ${e.message}", Toast.LENGTH_SHORT).show()
    }
}
