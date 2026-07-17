package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.Player
import com.example.data.PlayerStatus
import com.example.ui.theme.*
import com.example.viewmodel.TournamentViewModel
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerProfileTab(
    viewModel: TournamentViewModel
) {
    val context = LocalContext.current
    val currentUser by viewModel.currentUser.collectAsState()
    val players by viewModel.players.collectAsState()
    val teams by viewModel.teams.collectAsState()

    val myPlayer = remember(players, currentUser) {
        players.find { it.userId == currentUser?.id }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        if (myPlayer != null) {
            // Profile View
            Text(
                text = "My Player Profile",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Black,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, BorderColor, RoundedCornerShape(24.dp)),
                colors = CardDefaults.cardColors(containerColor = NavySurface),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Profile Photo
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .background(CyberBlue.copy(alpha = 0.2f))
                            .border(2.dp, NeonCyan, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        if (!myPlayer.profilePhotoUrl.isNullOrBlank()) {
                            AsyncImage(
                                model = myPlayer.profilePhotoUrl,
                                contentDescription = "Profile Photo",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(64.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = myPlayer.fullName,
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Status Badge
                    val (statusText, badgeColor, icon) = when (myPlayer.status) {
                        PlayerStatus.APPROVED -> Triple("APPROVED PLAYER", Color(0xFF4CAF50), Icons.Default.CheckCircle)
                        PlayerStatus.REJECTED -> Triple("REJECTED", Color(0xFFF44336), Icons.Default.Cancel)
                        PlayerStatus.PENDING -> Triple("PENDING APPROVAL", GoldTrophy, Icons.Default.Pending)
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(badgeColor.copy(alpha = 0.15f))
                            .border(1.dp, badgeColor, RoundedCornerShape(8.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(imageVector = icon, contentDescription = null, tint = badgeColor, modifier = Modifier.size(14.dp))
                            Text(
                                text = statusText,
                                color = badgeColor,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    Divider(color = BorderColor.copy(alpha = 0.5f))
                    Spacer(modifier = Modifier.height(24.dp))

                    // Details grid
                    ProfileDetailItem(label = "Gmail Address", value = myPlayer.gmail, icon = Icons.Default.Email)
                    ProfileDetailItem(label = "Phone Number", value = myPlayer.phoneNumber, icon = Icons.Default.Phone)
                    ProfileDetailItem(label = "Team Name", value = myPlayer.teamName, icon = Icons.Default.SportsFootball)
                    ProfileDetailItem(label = "Player Position", value = myPlayer.position, icon = Icons.Default.DirectionsRun)
                    ProfileDetailItem(label = "Jersey Number", value = "#${myPlayer.jerseyNumber}", icon = Icons.Default.Numbers)
                    ProfileDetailItem(label = "Date of Birth", value = myPlayer.dob, icon = Icons.Default.CalendarMonth)
                }
            }
        } else {
            // Registration Form
            Text(
                text = "Player Registration",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                modifier = Modifier.padding(bottom = 6.dp)
            )
            Text(
                text = "Join the tournament system by creating your player profile. Provide accurate details to be verified by tournament organizers.",
                color = TextSecondary,
                fontSize = 13.sp,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, BorderColor, RoundedCornerShape(24.dp)),
                colors = CardDefaults.cardColors(containerColor = NavySurface),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    var fullName by remember { mutableStateOf(currentUser?.name ?: "") }
                    var gmail by remember { mutableStateOf(currentUser?.email ?: "") }
                    var phoneNumber by remember { mutableStateOf("") }
                    var teamName by remember { mutableStateOf("") }
                    var position by remember { mutableStateOf("Striker") }
                    var jerseyNumber by remember { mutableStateOf("") }
                    var dob by remember { mutableStateOf("") }
                    var profilePhotoUrl by remember { mutableStateOf("") }

                    var teamDropdownExpanded by remember { mutableStateOf(false) }
                    var positionDropdownExpanded by remember { mutableStateOf(false) }

                    val positionsList = listOf("Striker", "Midfielder", "Defender", "Goalkeeper")

                    // Full Name Input
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
                        modifier = Modifier.fillMaxWidth().testTag("player_name_input")
                    )

                    // Gmail Input
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
                        modifier = Modifier.fillMaxWidth().testTag("player_gmail_input")
                    )

                    // Phone Number Input
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
                        modifier = Modifier.fillMaxWidth().testTag("player_phone_input")
                    )

                    // Team Name Selector Dropdown
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = teamName,
                            onValueChange = { teamName = it },
                            label = { Text("Team Name") },
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
                            modifier = Modifier.fillMaxWidth().testTag("player_team_input")
                        )
                        DropdownMenu(
                            expanded = teamDropdownExpanded,
                            onDismissRequest = { teamDropdownExpanded = false },
                            modifier = Modifier.background(NavySurfaceLight).fillMaxWidth()
                        ) {
                            teams.map { it.name }.distinct().forEach { name ->
                                DropdownMenuItem(
                                    text = { Text(name, color = Color.White) },
                                    onClick = {
                                        teamName = name
                                        teamDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

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
                            modifier = Modifier.fillMaxWidth().clickable { positionDropdownExpanded = true }.testTag("player_position_input")
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

                    // Jersey Number Input
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
                        modifier = Modifier.fillMaxWidth().testTag("player_jersey_input")
                    )

                    // Date of Birth Input
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
                        modifier = Modifier.fillMaxWidth().testTag("player_dob_input")
                    )

                    // Profile Photo URL
                    OutlinedTextField(
                        value = profilePhotoUrl,
                        onValueChange = { profilePhotoUrl = it },
                        label = { Text("Profile Photo URL (Optional)") },
                        placeholder = { Text("Leave blank for default avatar", color = Color.Gray) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonCyan,
                            unfocusedBorderColor = BorderColor,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth().testTag("player_photo_input")
                    )

                    // Quick suggestion avatars
                    Text(
                        text = "Or tap a quick preset avatar:",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        val avatarPresets = listOf(
                            "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde" to "Classic",
                            "https://images.unsplash.com/photo-1599566150163-29194dcaad36" to "Gamer",
                            "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d" to "Pro"
                        )
                        avatarPresets.forEach { (url, label) ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (profilePhotoUrl == url) CyberBlue else NavySurfaceLight)
                                    .border(1.dp, if (profilePhotoUrl == url) NeonCyan else BorderColor, RoundedCornerShape(12.dp))
                                    .clickable { profilePhotoUrl = url }
                                    .padding(vertical = 8.dp, horizontal = 4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(label, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Submit Button
                    Button(
                        onClick = {
                            val jersey = jerseyNumber.toIntOrNull()
                            if (fullName.isBlank() || gmail.isBlank() || phoneNumber.isBlank() || teamName.isBlank() || jersey == null || dob.isBlank()) {
                                Toast.makeText(context, "Please fill all required fields correctly.", Toast.LENGTH_SHORT).show()
                            } else {
                                val resolvedPhoto = if (profilePhotoUrl.isBlank()) {
                                    "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde"
                                } else {
                                    profilePhotoUrl
                                }
                                val newPlayer = Player(
                                    id = "player_" + UUID.randomUUID().toString().take(6),
                                    userId = currentUser?.id ?: "",
                                    fullName = fullName,
                                    gmail = gmail,
                                    phoneNumber = phoneNumber,
                                    teamName = teamName,
                                    position = position,
                                    jerseyNumber = jersey,
                                    dob = dob,
                                    profilePhotoUrl = resolvedPhoto,
                                    status = PlayerStatus.PENDING
                                )
                                viewModel.registerPlayer(newPlayer) {
                                    Toast.makeText(context, "Registration submitted for admin review!", Toast.LENGTH_LONG).show()
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CyberBlue),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("submit_registration_button"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("SUBMIT REGISTRATION", color = Color.White, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileDetailItem(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(CyberBlue.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(16.dp))
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Text(text = label, color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Medium)
            Text(text = value, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }
    }
}
