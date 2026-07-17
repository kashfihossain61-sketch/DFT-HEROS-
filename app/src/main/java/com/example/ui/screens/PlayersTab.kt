package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.Player
import com.example.data.PlayerStatus
import com.example.ui.theme.*
import com.example.viewmodel.TournamentViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayersTab(
    viewModel: TournamentViewModel
) {
    val context = LocalContext.current
    val players by viewModel.players.collectAsState()
    
    var searchQuery by remember { mutableStateOf("") }
    var selectedStatusFilter by remember { mutableStateOf<PlayerStatus?>(null) }
    var editingPlayer by remember { mutableStateOf<Player?>(null) }

    // Filtered players based on search query and status filter
    val filteredPlayers = remember(players, searchQuery, selectedStatusFilter) {
        players.filter { player ->
            val matchesSearch = player.fullName.contains(searchQuery, ignoreCase = true) ||
                    player.teamName.contains(searchQuery, ignoreCase = true)
            val matchesStatus = selectedStatusFilter == null || player.status == selectedStatusFilter
            matchesSearch && matchesStatus
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Search & Filters Header
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Search by name or team") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextSecondary) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = NeonCyan,
                unfocusedBorderColor = BorderColor,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
                .testTag("player_admin_search")
        )

        // Status Filter Chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = selectedStatusFilter == null,
                onClick = { selectedStatusFilter = null },
                label = { Text("All (${players.size})") },
                colors = FilterChipDefaults.filterChipColors(
                    labelColor = Color.White,
                    selectedContainerColor = CyberBlue
                )
            )

            val pendingCount = players.count { it.status == PlayerStatus.PENDING }
            FilterChip(
                selected = selectedStatusFilter == PlayerStatus.PENDING,
                onClick = { selectedStatusFilter = PlayerStatus.PENDING },
                label = { Text("Pending ($pendingCount)") },
                colors = FilterChipDefaults.filterChipColors(
                    labelColor = Color.White,
                    selectedContainerColor = GoldTrophy
                )
            )

            val approvedCount = players.count { it.status == PlayerStatus.APPROVED }
            FilterChip(
                selected = selectedStatusFilter == PlayerStatus.APPROVED,
                onClick = { selectedStatusFilter = PlayerStatus.APPROVED },
                label = { Text("Approved ($approvedCount)") },
                colors = FilterChipDefaults.filterChipColors(
                    labelColor = Color.White,
                    selectedContainerColor = Color(0xFF4CAF50)
                )
            )

            val rejectedCount = players.count { it.status == PlayerStatus.REJECTED }
            FilterChip(
                selected = selectedStatusFilter == PlayerStatus.REJECTED,
                onClick = { selectedStatusFilter = PlayerStatus.REJECTED },
                label = { Text("Rejected ($rejectedCount)") },
                colors = FilterChipDefaults.filterChipColors(
                    labelColor = Color.White,
                    selectedContainerColor = Color(0xFFF44336)
                )
            )
        }

        // Players List
        if (filteredPlayers.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.PersonOff, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(64.dp))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("No players found matching filters", color = TextSecondary, fontSize = 14.sp)
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(filteredPlayers) { player ->
                    PlayerAdminCard(
                        player = player,
                        onApprove = {
                            viewModel.updatePlayer(player.copy(status = PlayerStatus.APPROVED))
                            Toast.makeText(context, "Player Approved!", Toast.LENGTH_SHORT).show()
                        },
                        onReject = {
                            viewModel.updatePlayer(player.copy(status = PlayerStatus.REJECTED))
                            Toast.makeText(context, "Player Rejected!", Toast.LENGTH_SHORT).show()
                        },
                        onEdit = { editingPlayer = player },
                        onDelete = {
                            viewModel.deletePlayer(player.id)
                            Toast.makeText(context, "Player deleted successfully.", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
        }
    }

    // Edit Player Dialog
    if (editingPlayer != null) {
        EditPlayerDialog(
            player = editingPlayer!!,
            onDismiss = { editingPlayer = null },
            onSave = { updatedPlayer ->
                viewModel.updatePlayer(updatedPlayer)
                editingPlayer = null
                Toast.makeText(context, "Player details updated.", Toast.LENGTH_SHORT).show()
            }
        )
    }
}

@Composable
fun PlayerAdminCard(
    player: Player,
    onApprove: () -> Unit,
    onReject: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, BorderColor, RoundedCornerShape(20.dp)),
        colors = CardDefaults.cardColors(containerColor = NavySurface),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Profile Photo
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(CyberBlue.copy(alpha = 0.2f))
                        .border(1.dp, NeonCyan, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    if (!player.profilePhotoUrl.isNullOrBlank()) {
                        AsyncImage(
                            model = player.profilePhotoUrl,
                            contentDescription = "Profile Photo",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(Icons.Default.Person, contentDescription = null, tint = Color.White, modifier = Modifier.size(32.dp))
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(player.fullName, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Text("${player.position}  •  Jersey #${player.jerseyNumber}", color = TextSecondary, fontSize = 12.sp)
                    Text("Team: ${player.teamName}", color = NeonCyan, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }

                // Status Indicator
                val statusColor = when (player.status) {
                    PlayerStatus.APPROVED -> Color(0xFF4CAF50)
                    PlayerStatus.REJECTED -> Color(0xFFF44336)
                    PlayerStatus.PENDING -> GoldTrophy
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(statusColor.copy(alpha = 0.15f))
                        .border(1.dp, statusColor, RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(player.status.name, color = statusColor, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Divider(color = BorderColor.copy(alpha = 0.3f))
            Spacer(modifier = Modifier.height(12.dp))

            // Sensitive Info (Gmail & Phone) - Fully Visible to Admin
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Email, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(13.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(player.gmail, color = Color.White, fontSize = 11.sp)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Phone, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(13.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(player.phoneNumber, color = Color.White, fontSize = 11.sp)
                    }
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("DoB: ${player.dob}", color = TextSecondary, fontSize = 11.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Edit
                IconButton(
                    onClick = onEdit,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(NavySurfaceLight)
                ) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit Player", tint = Color.White, modifier = Modifier.size(16.dp))
                }

                // Delete
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(NavySurfaceLight)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete Player", tint = Color.Red, modifier = Modifier.size(16.dp))
                }

                Spacer(modifier = Modifier.weight(1f))

                // Approval controls
                if (player.status != PlayerStatus.REJECTED) {
                    TextButton(
                        onClick = onReject,
                        colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFFF44336))
                    ) {
                        Text("REJECT", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                if (player.status != PlayerStatus.APPROVED) {
                    Button(
                        onClick = onApprove,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Text("APPROVE", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun EditPlayerDialog(
    player: Player,
    onDismiss: () -> Unit,
    onSave: (Player) -> Unit
) {
    var fullName by remember { mutableStateOf(player.fullName) }
    var gmail by remember { mutableStateOf(player.gmail) }
    var phoneNumber by remember { mutableStateOf(player.phoneNumber) }
    var teamName by remember { mutableStateOf(player.teamName) }
    var position by remember { mutableStateOf(player.position) }
    var jerseyNumber by remember { mutableStateOf(player.jerseyNumber.toString()) }
    var dob by remember { mutableStateOf(player.dob) }
    var profilePhotoUrl by remember { mutableStateOf(player.profilePhotoUrl) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = NavySurface,
        title = { Text("Edit Player Profile", color = Color.White, fontWeight = FontWeight.Bold) },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 420.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = { Text("Full Name") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = BorderColor,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = gmail,
                    onValueChange = { gmail = it },
                    label = { Text("Gmail Address") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = BorderColor,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = { phoneNumber = it },
                    label = { Text("Phone Number") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = BorderColor,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = teamName,
                    onValueChange = { teamName = it },
                    label = { Text("Team Name") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = BorderColor,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = position,
                    onValueChange = { position = it },
                    label = { Text("Player Position") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = BorderColor,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = jerseyNumber,
                    onValueChange = { jerseyNumber = it },
                    label = { Text("Jersey Number") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = BorderColor,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = dob,
                    onValueChange = { dob = it },
                    label = { Text("Date of Birth") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = BorderColor,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = profilePhotoUrl,
                    onValueChange = { profilePhotoUrl = it },
                    label = { Text("Profile Photo URL") },
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
                    val jersey = jerseyNumber.toIntOrNull() ?: player.jerseyNumber
                    onSave(
                        player.copy(
                            fullName = fullName,
                            gmail = gmail,
                            phoneNumber = phoneNumber,
                            teamName = teamName,
                            position = position,
                            jerseyNumber = jersey,
                            dob = dob,
                            profilePhotoUrl = profilePhotoUrl
                        )
                    )
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
