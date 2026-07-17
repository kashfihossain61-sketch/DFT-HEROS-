package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.*
import com.example.ui.theme.*
import com.example.viewmodel.TournamentViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupChatScreen(
    groupId: String,
    viewModel: TournamentViewModel,
    onNavigateBack: () -> Unit
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val chatMessages by viewModel.chatMessages.collectAsState()
    val groups by viewModel.groups.collectAsState()
    val teams by viewModel.teams.collectAsState()

    val group = groups.find { it.id == groupId }
    val team = teams.find { it.id == groupId }
    val groupChats = chatMessages.filter { it.groupId == groupId }

    // Authorization logic
    val isAuthorized = remember(currentUser, groupId, group, team, teams) {
        val user = currentUser ?: return@remember false
        when {
            groupId == "global" -> true
            user.role == "Admin" -> true
            group != null -> {
                val userTeamIds = teams.filter { it.captainId == user.id || it.members.contains(user.name) }.map { it.id }
                group.teamIds.any { it in userTeamIds }
            }
            team != null -> {
                team.captainId == user.id || team.members.contains(user.name)
            }
            else -> false
        }
    }

    var demoBypass by remember { mutableStateOf(false) }
    val hasAccess = isAuthorized || demoBypass

    val titleText = remember(groupId, group, team) {
        when {
            groupId == "global" -> "Global Esports Arena"
            group != null -> group.name
            team != null -> "${team.name} [HQ]"
            else -> "Private Chat"
        }
    }

    val subtitleText = remember(groupId, group, team) {
        when {
            groupId == "global" -> "Global Community Live Chat"
            group != null -> "Private Group Bracket Chat"
            team != null -> "Private Team Headquarters"
            else -> "Esports Secure Channel"
        }
    }

    var typedMessage by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    // Scroll to the bottom whenever a new message is loaded/sent
    LaunchedEffect(groupChats.size) {
        if (groupChats.isNotEmpty()) {
            listState.animateScrollToItem(groupChats.size - 1)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(titleText, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text(subtitleText, fontSize = 11.sp, color = NeonCyan)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = NavySurface,
                    titleContentColor = TextPrimary
                )
            )
        },
        containerColor = NavyDeep,
        bottomBar = {
            if (hasAccess) {
                // Typing Bar only visible if has access
                Surface(
                    color = NavySurface,
                    modifier = Modifier
                        .border(1.dp, BorderColor, RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                        .navigationBarsPadding()
                        .imePadding()
                ) {
                    Row(
                        modifier = Modifier
                            .padding(12.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = typedMessage,
                            onValueChange = { typedMessage = it },
                            placeholder = { Text("Send message...", color = TextSecondary) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                focusedBorderColor = NeonCyan,
                                unfocusedBorderColor = BorderColor,
                                focusedContainerColor = NavyDeep,
                                unfocusedContainerColor = NavyDeep
                            ),
                            modifier = Modifier.weight(1f),
                            maxLines = 3,
                            shape = RoundedCornerShape(24.dp)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        IconButton(
                            onClick = {
                                if (typedMessage.isNotBlank()) {
                                    viewModel.sendChatMessage(groupId, typedMessage)
                                    typedMessage = ""
                                }
                            },
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(24.dp))
                                .background(CyberBlue)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Send,
                                contentDescription = "Send Message",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(NavyDeep)
        ) {
            if (!hasAccess) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxWidth().widthIn(max = 400.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Encrypted Connection",
                            tint = Color(0xFFEF4444),
                            modifier = Modifier.size(72.dp)
                        )
                        
                        Text(
                            text = "RESTRICTED CHANNEL",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        )
                        
                        Text(
                            text = when {
                                group != null -> "This terminal is private to teams assigned to ${group.name}. Only verified players of the participating teams can transmit or view messages."
                                team != null -> "This channel is encrypted and private to members of ${team.name}. Contact your Team Captain to register."
                                else -> "This secure channel requires authorization to access."
                            },
                            color = TextSecondary,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center,
                            lineHeight = 20.sp
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Button(
                            onClick = onNavigateBack,
                            colors = ButtonDefaults.buttonColors(containerColor = CyberBlue),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Return to Hub", fontWeight = FontWeight.Bold, color = Color.White)
                        }
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        Card(
                            colors = CardDefaults.cardColors(containerColor = NavySurface),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, BorderColor, RoundedCornerShape(12.dp))
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp).fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Demo Bypass Mode", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    Text("Simulate authorized participant", color = TextSecondary, fontSize = 10.sp)
                                }
                                Switch(
                                    checked = demoBypass,
                                    onCheckedChange = { demoBypass = it },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = NeonCyan,
                                        checkedTrackColor = CyberBlue
                                    )
                                )
                            }
                        }
                    }
                }
            } else if (groupChats.isEmpty()) {
                Box(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No messages in this chat room yet.", color = TextSecondary)
                }
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(groupChats) { chat ->
                        val isSelf = chat.senderId == currentUser?.id

                        if (chat.isSystem) {
                            // System Notification Message
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .background(BorderColor.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                        .padding(horizontal = 12.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = chat.message,
                                        color = NeonCyan,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        } else {
                            // Regular Chat Message
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = if (isSelf) Alignment.End else Alignment.Start
                            ) {
                                // Sender Name Header
                                if (!isSelf) {
                                    Text(
                                        text = chat.senderName,
                                        color = NeonCyan,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(start = 4.dp, bottom = 2.dp)
                                    )
                                }

                                // Message Bubble
                                Box(
                                    modifier = Modifier
                                        .clip(
                                            RoundedCornerShape(
                                                topStart = 16.dp,
                                                topEnd = 16.dp,
                                                bottomStart = if (isSelf) 16.dp else 4.dp,
                                                bottomEnd = if (isSelf) 4.dp else 16.dp
                                            )
                                        )
                                        .background(if (isSelf) ChatBubbleSelf else ChatBubbleOther)
                                        .padding(horizontal = 14.dp, vertical = 10.dp)
                                        .widthIn(max = 280.dp)
                                ) {
                                    Column {
                                        Text(
                                            text = chat.message,
                                            color = Color.White,
                                            fontSize = 14.sp
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        // Simple Timestamp
                                        val format = SimpleDateFormat("h:mm a", Locale.getDefault())
                                        Text(
                                            text = format.format(Date(chat.timestamp)),
                                            color = TextSecondary,
                                            fontSize = 9.sp,
                                            modifier = Modifier.align(Alignment.End)
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
