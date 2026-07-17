package com.example.data

import android.content.Context
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

class TournamentRepository(private val context: Context) {
    private val TAG = "TournamentRepository"

    // Firebase Auth & Firestore handles
    private var firebaseAuth: FirebaseAuth? = null
    private var firestore: FirebaseFirestore? = null
    
    // Status flag to show in UI
    private val _isFirebaseActive = MutableStateFlow(false)
    val isFirebaseActive: StateFlow<Boolean> = _isFirebaseActive.asStateFlow()

    // State flows representing the "current state"
    private val _currentUser = MutableStateFlow<AppUser?>(null)
    val currentUser: StateFlow<AppUser?> = _currentUser.asStateFlow()

    private val _tournaments = MutableStateFlow<List<Tournament>>(emptyList())
    val tournaments: StateFlow<List<Tournament>> = _tournaments.asStateFlow()

    private val _teams = MutableStateFlow<List<Team>>(emptyList())
    val teams: StateFlow<List<Team>> = _teams.asStateFlow()

    private val _groups = MutableStateFlow<List<Group>>(emptyList())
    val groups: StateFlow<List<Group>> = _groups.asStateFlow()

    private val _matches = MutableStateFlow<List<Match>>(emptyList())
    val matches: StateFlow<List<Match>> = _matches.asStateFlow()

    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _notifications = MutableStateFlow<List<Notification>>(emptyList())
    val notifications: StateFlow<List<Notification>> = _notifications.asStateFlow()

    private val _allUsers = MutableStateFlow<List<AppUser>>(emptyList())
    val allUsers: StateFlow<List<AppUser>> = _allUsers.asStateFlow()

    private val _players = MutableStateFlow<List<Player>>(emptyList())
    val players: StateFlow<List<Player>> = _players.asStateFlow()

    private val _auctionEvents = MutableStateFlow<List<AuctionEvent>>(emptyList())
    val auctionEvents: StateFlow<List<AuctionEvent>> = _auctionEvents.asStateFlow()

    private val _auctionPlayers = MutableStateFlow<List<AuctionPlayer>>(emptyList())
    val auctionPlayers: StateFlow<List<AuctionPlayer>> = _auctionPlayers.asStateFlow()

    private val _auctionHistory = MutableStateFlow<List<AuctionHistoryLog>>(emptyList())
    val auctionHistory: StateFlow<List<AuctionHistoryLog>> = _auctionHistory.asStateFlow()

    init {
        loadInitialData()
        setupFirebase()
    }

    private fun setupFirebase() {
        try {
            // Attempt to check if Firebase is configured
            val apps = FirebaseApp.getApps(context)
            if (apps.isNotEmpty() || context.resources.getIdentifier("google_app_id", "string", context.packageName) != 0) {
                // Firebase is initialized or has resources to be initialized
                if (apps.isEmpty()) {
                    FirebaseApp.initializeApp(context)
                }
                firebaseAuth = FirebaseAuth.getInstance()
                firestore = FirebaseFirestore.getInstance()
                _isFirebaseActive.value = true
                Log.d(TAG, "Firebase successfully connected. Establishing real-time listeners.")

                // Set up real-time bidirectional listeners
                listenToTournaments()
                listenToTeams()
                listenToGroups()
                listenToMatches()
                listenToChats()
                listenToNotifications()
                listenToUsers()
                listenToPlayers()
                listenToAuctionEvents()
                listenToAuctionPlayers()
                listenToAuctionHistory()
            } else {
                Log.w(TAG, "google-services.json not found. Operating in Local Demo Mode.")
                _isFirebaseActive.value = false
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize Firebase: ${e.message}. Operating in Local Demo Mode.")
            _isFirebaseActive.value = false
        }
    }

    private fun listenToTournaments() {
        firestore?.collection("tournaments")?.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.e(TAG, "Tournaments listen failed", error)
                return@addSnapshotListener
            }
            if (snapshot != null) {
                if (snapshot.isEmpty) {
                    Log.d(TAG, "Seeding tournaments to Firestore")
                    _tournaments.value.forEach { t ->
                        firestore?.collection("tournaments")?.document(t.id)?.set(t)
                    }
                } else {
                    val list = snapshot.toObjects(Tournament::class.java)
                    _tournaments.value = list
                }
            }
        }
    }

    private fun listenToTeams() {
        firestore?.collection("teams")?.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.e(TAG, "Teams listen failed", error)
                return@addSnapshotListener
            }
            if (snapshot != null) {
                if (snapshot.isEmpty) {
                    Log.d(TAG, "Seeding teams to Firestore")
                    _teams.value.forEach { team ->
                        firestore?.collection("teams")?.document(team.id)?.set(team)
                    }
                } else {
                    val list = snapshot.toObjects(Team::class.java)
                    _teams.value = list
                }
            }
        }
    }

    private fun listenToGroups() {
        firestore?.collection("groups")?.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.e(TAG, "Groups listen failed", error)
                return@addSnapshotListener
            }
            if (snapshot != null) {
                if (snapshot.isEmpty) {
                    Log.d(TAG, "Seeding groups to Firestore")
                    _groups.value.forEach { g ->
                        firestore?.collection("groups")?.document(g.id)?.set(g)
                    }
                } else {
                    val list = snapshot.toObjects(Group::class.java)
                    _groups.value = list
                }
            }
        }
    }

    private fun listenToMatches() {
        firestore?.collection("matches")?.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.e(TAG, "Matches listen failed", error)
                return@addSnapshotListener
            }
            if (snapshot != null) {
                if (snapshot.isEmpty) {
                    Log.d(TAG, "Seeding matches to Firestore")
                    _matches.value.forEach { m ->
                        firestore?.collection("matches")?.document(m.id)?.set(m)
                    }
                } else {
                    val list = snapshot.toObjects(Match::class.java)
                    _matches.value = list
                }
            }
        }
    }

    private fun listenToChats() {
        firestore?.collection("chats")?.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.e(TAG, "Chats listen failed", error)
                return@addSnapshotListener
            }
            if (snapshot != null) {
                if (snapshot.isEmpty) {
                    Log.d(TAG, "Seeding initial chats to Firestore")
                    _chatMessages.value.forEach { msg ->
                        firestore?.collection("chats")?.document(msg.id)?.set(msg)
                    }
                } else {
                    val list = snapshot.toObjects(ChatMessage::class.java)
                    _chatMessages.value = list.sortedBy { it.timestamp }
                }
            }
        }
    }

    private fun listenToNotifications() {
        firestore?.collection("notifications")?.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.e(TAG, "Notifications listen failed", error)
                return@addSnapshotListener
            }
            if (snapshot != null) {
                if (snapshot.isEmpty) {
                    Log.d(TAG, "Seeding initial notifications to Firestore")
                    _notifications.value.forEach { notif ->
                        firestore?.collection("notifications")?.document(notif.id)?.set(notif)
                    }
                } else {
                    val list = snapshot.toObjects(Notification::class.java)
                    _notifications.value = list.sortedByDescending { it.timestamp }
                }
            }
        }
    }

    private fun listenToUsers() {
        firestore?.collection("users")?.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.e(TAG, "Users listen failed", error)
                return@addSnapshotListener
            }
            if (snapshot != null) {
                if (snapshot.isEmpty) {
                    Log.d(TAG, "Seeding initial users to Firestore")
                    _allUsers.value.forEach { user ->
                        firestore?.collection("users")?.document(user.id)?.set(user)
                    }
                } else {
                    val list = snapshot.toObjects(AppUser::class.java)
                    _allUsers.value = list
                }
            }
        }
    }

    private fun listenToPlayers() {
        firestore?.collection("players")?.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.e(TAG, "Players listen failed", error)
                return@addSnapshotListener
            }
            if (snapshot != null) {
                if (snapshot.isEmpty) {
                    Log.d(TAG, "Seeding initial players to Firestore")
                    _players.value.forEach { player ->
                        firestore?.collection("players")?.document(player.id)?.set(player)
                    }
                } else {
                    val list = snapshot.toObjects(Player::class.java)
                    _players.value = list
                }
            }
        }
    }

    private fun listenToAuctionEvents() {
        firestore?.collection("auction_events")?.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.e(TAG, "AuctionEvents listen failed", error)
                return@addSnapshotListener
            }
            if (snapshot != null) {
                if (snapshot.isEmpty) {
                    Log.d(TAG, "Seeding initial auction events to Firestore")
                    _auctionEvents.value.forEach { ev ->
                        firestore?.collection("auction_events")?.document(ev.id)?.set(ev)
                    }
                } else {
                    val list = snapshot.toObjects(AuctionEvent::class.java)
                    _auctionEvents.value = list.sortedByDescending { it.createdAt }
                }
            }
        }
    }

    private fun listenToAuctionPlayers() {
        firestore?.collection("auction_players")?.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.e(TAG, "AuctionPlayers listen failed", error)
                return@addSnapshotListener
            }
            if (snapshot != null) {
                if (snapshot.isEmpty) {
                    Log.d(TAG, "Seeding initial auction players to Firestore")
                    _auctionPlayers.value.forEach { p ->
                        firestore?.collection("auction_players")?.document(p.id)?.set(p)
                    }
                } else {
                    val list = snapshot.toObjects(AuctionPlayer::class.java)
                    _auctionPlayers.value = list.sortedBy { it.auctionOrder }
                }
            }
        }
    }

    private fun listenToAuctionHistory() {
        firestore?.collection("auction_history")?.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.e(TAG, "AuctionHistory listen failed", error)
                return@addSnapshotListener
            }
            if (snapshot != null) {
                if (snapshot.isEmpty) {
                    Log.d(TAG, "Seeding initial auction history to Firestore")
                    _auctionHistory.value.forEach { log ->
                        firestore?.collection("auction_history")?.document(log.id)?.set(log)
                    }
                } else {
                    val list = snapshot.toObjects(AuctionHistoryLog::class.java)
                    _auctionHistory.value = list.sortedByDescending { it.timestamp }
                }
            }
        }
    }

    private fun loadInitialData() {
        // Pre-populate with beautiful default mock data
        val t1 = Tournament(
            id = "t_val_2026",
            name = "DFT-HEROS Premier League 2026",
            gameName = "Valorant",
            status = TournamentStatus.ONGOING,
            startDate = "July 10, 2026",
            endDate = "July 25, 2026",
            teamsCount = 8,
            maxTeams = 12,
            prizePool = "$5,000",
            description = "The flagship Valorant championship under DFT-HEROS, highlighting the finest esports talent competing for dominance."
        )
        val t2 = Tournament(
            id = "t_ml_2026",
            name = "Summer Cyber Showdown",
            gameName = "Mobile Legends",
            status = TournamentStatus.UPCOMING,
            startDate = "August 05, 2026",
            endDate = "August 15, 2026",
            teamsCount = 4,
            maxTeams = 16,
            prizePool = "$2,500",
            description = "Get ready for lightning-fast matches in Mobile Legends: Bang Bang. Form your squads and grab the crown."
        )
        val t3 = Tournament(
            id = "t_pubg_2026",
            name = "DFT-HEROS Masters Cup",
            gameName = "PUBG Mobile",
            status = TournamentStatus.COMPLETED,
            startDate = "June 01, 2026",
            endDate = "June 12, 2026",
            teamsCount = 16,
            maxTeams = 16,
            prizePool = "$10,000",
            description = "A battle royale spectacle where 16 top squads collided on Erangel. Congrats to Team Apex on winning the prize pool."
        )

        _tournaments.value = listOf(t1, t2, t3)

        // Teams
        val team1 = Team("team_elite", "Elite Gaming", "captain_alex", "Alex Carter", listOf("Alex Carter", "SovaMain", "SageMain", "JettDuelist", "OmenSmoke"), "t_val_2026", "", TeamStatus.APPROVED)
        val team2 = Team("team_knights", "DFT Knights", "captain_sarah", "Sarah Lin", listOf("Sarah Lin", "ReynaPlayer", "BreachKing", "KilljoySetup", "CypherMain"), "t_val_2026", "", TeamStatus.APPROVED)
        val team3 = Team("team_apex", "Apex Predator", "captain_jake", "Jake Vance", listOf("Jake Vance", "FadeHunt", "ViperToxic", "NeonSprint", "GekkoPet"), "t_val_2026", "", TeamStatus.APPROVED)
        val team4 = Team("team_shadow", "Shadow Syndicate", "captain_elena", "Elena Rostova", listOf("Elena Rostova", "RazeGrenade", "AstraSmoke", "DeadlockSetup", "IsoMain"), "t_val_2026", "", TeamStatus.APPROVED)
        val team5 = Team("team_phoenix", "Cyber Phoenix", "captain_tyler", "Tyler Chen", listOf("Tyler Chen", "YoruClone", "ChamberRich", "SkyeFlash", "BrimStone"), "t_val_2026", "", TeamStatus.APPROVED)
        val team6 = Team("team_warriors", "Cyber Warriors", "captain_marcus", "Marcus Aurelius", listOf("Marcus Aurelius", "HarborWater", "CloveSmoke", "VyseTrap", "PhoenixFire"), "t_val_2026", "", TeamStatus.APPROVED)

        _teams.value = listOf(team1, team2, team3, team4, team5, team6)

        // Groups
        val g1 = Group("g_alpha", "Group Alpha", "First half of the tournament", "t_val_2026", listOf("team_elite", "team_knights", "team_apex", "team_shadow"), "482910")
        val g2 = Group("g_beta", "Group Beta", "Second half of the tournament", "t_val_2026", listOf("team_phoenix", "team_warriors"), "591024")

        _groups.value = listOf(g1, g2)

        // Matches
        val m1 = Match(
            id = "match_1",
            tournamentId = "t_val_2026",
            teamAId = "team_elite",
            teamAName = "Elite Gaming",
            teamBId = "team_knights",
            teamBName = "DFT Knights",
            scoreA = 11,
            scoreB = 9,
            status = MatchStatus.LIVE,
            time = "7:00 PM (Live Now)",
            stage = "Group Stage",
            date = "July 16, 2026",
            venue = "Alpha Dome Center",
            referee = "Marshall Carter",
            lineupA = listOf("Alex Carter", "SovaMain", "SageMain", "JettDuelist", "OmenSmoke"),
            lineupB = listOf("Sarah Lin", "ReynaPlayer", "BreachKing", "KilljoySetup", "CypherMain"),
            statistics = MatchStatistics(
                killsA = 68, killsB = 62,
                assistsA = 42, assistsB = 35,
                deathsA = 62, deathsB = 68,
                objectiveA = 11, objectiveB = 9,
                ratingA = 7.1, ratingB = 6.4
            ),
            events = listOf(
                MatchEvent(minute = 2, type = MatchEventType.KILL, teamId = "team_elite", playerName = "JettDuelist", description = "First Blood! Jett eliminated Reyna with Headhunter"),
                MatchEvent(minute = 5, type = MatchEventType.OBJECTIVE, teamId = "team_elite", playerName = "Alex Carter", description = "Spike detonated at Bomb Site A"),
                MatchEvent(minute = 9, type = MatchEventType.KILL, teamId = "team_knights", playerName = "Sarah Lin", description = "Quadra Kill by Sova! DFT Knights secure round 8"),
                MatchEvent(minute = 14, type = MatchEventType.PAUSE, teamId = "", playerName = "Admin", description = "Technical Timeout called by referee Marshall Carter")
            )
        )
        val m2 = Match(
            id = "match_2",
            tournamentId = "t_val_2026",
            teamAId = "team_apex",
            teamAName = "Apex Predator",
            teamBId = "team_shadow",
            teamBName = "Shadow Syndicate",
            scoreA = 0,
            scoreB = 0,
            status = MatchStatus.UPCOMING,
            time = "9:30 PM (Tonight)",
            stage = "Group Stage",
            date = "July 16, 2026",
            venue = "Main Cyber Stadium",
            referee = "Sarah Connor",
            lineupA = listOf("Jake Vance", "FadeHunt", "ViperToxic", "NeonSprint", "GekkoPet"),
            lineupB = listOf("Elena Rostova", "RazeGrenade", "AstraSmoke", "DeadlockSetup", "IsoMain")
        )
        val m3 = Match(
            id = "match_3",
            tournamentId = "t_val_2026",
            teamAId = "team_phoenix",
            teamAName = "Cyber Phoenix",
            teamBId = "team_warriors",
            teamBName = "Cyber Warriors",
            scoreA = 13,
            scoreB = 7,
            status = MatchStatus.COMPLETED,
            time = "Yesterday",
            stage = "Group Stage",
            date = "July 15, 2026",
            venue = "Alpha Dome Center",
            referee = "Marshall Carter",
            lineupA = listOf("Tyler Chen", "YoruClone", "ChamberRich", "SkyeFlash", "BrimStone"),
            lineupB = listOf("Marcus Aurelius", "HarborWater", "CloveSmoke", "VyseTrap", "PhoenixFire"),
            manOfTheMatch = "Tyler Chen",
            statistics = MatchStatistics(
                killsA = 84, killsB = 53,
                assistsA = 51, assistsB = 30,
                deathsA = 53, deathsB = 84,
                objectiveA = 13, objectiveB = 7,
                ratingA = 8.4, ratingB = 5.2
            ),
            events = listOf(
                MatchEvent(minute = 1, type = MatchEventType.KILL, teamId = "team_phoenix", playerName = "Tyler Chen", description = "Ace! Chamber kills all 5 in the pistol round"),
                MatchEvent(minute = 10, type = MatchEventType.OBJECTIVE, teamId = "team_phoenix", playerName = "BrimStone", description = "DFT spikes successfully defused by Cyber Phoenix"),
                MatchEvent(minute = 18, type = MatchEventType.KILL, teamId = "team_warriors", playerName = "Marcus Aurelius", description = "Triple Kill with Water wall defense"),
                MatchEvent(minute = 24, type = MatchEventType.OBJECTIVE, teamId = "team_phoenix", playerName = "YoruClone", description = "Match Point secured! Cyber Phoenix win 13-7")
            )
        )

        _matches.value = listOf(m1, m2, m3)

        // Chat messages for g_alpha
        val chats = listOf(
            ChatMessage("c1", "g_alpha", "captain_alex", "Alex Carter", "Ready for the match today DFT Knights?", System.currentTimeMillis() - 3600000),
            ChatMessage("c2", "g_alpha", "captain_sarah", "Sarah Lin", "Absolutely! Sova main here, ready to reveal your locations.", System.currentTimeMillis() - 3000000),
            ChatMessage("c3", "g_alpha", "system", "System", "Welcome to the Group Alpha Chat room! Keep conversation sportsmanship-focused.", System.currentTimeMillis() - 4000000, isSystem = true),
            ChatMessage("c4", "g_alpha", "captain_alex", "Alex Carter", "GG to Cyber Phoenix in Group B for their match yesterday!", System.currentTimeMillis() - 1200000)
        )
        _chatMessages.value = chats

        // Pre-populated Users
        val u1 = AppUser("captain_alex", "Alex Carter", "alex@dft.com", "", "Captain")
        val u2 = AppUser("captain_sarah", "Sarah Lin", "sarah@dft.com", "", "Captain")
        val u3 = AppUser("captain_jake", "Jake Vance", "jake@dft.com", "", "Captain")
        val u4 = AppUser("captain_elena", "Elena Rostova", "elena@dft.com", "", "Captain")
        val u5 = AppUser("admin_user", "Admin Master", "admin@dft.com", "", "Admin")
        _allUsers.value = listOf(u1, u2, u3, u4, u5)

        // Pre-populated Notifications
        val n1 = Notification(
            id = "n_welcome",
            title = "Welcome to DFT-HEROS!",
            message = "The premium arena for managing esports brackets and tournament stats in real-time. Good luck, Captains!",
            timestamp = System.currentTimeMillis() - 86400000
        )
        _notifications.value = listOf(n1)

        // Pre-populated Players
        val p1 = Player(
            id = "player_1",
            userId = "captain_alex",
            fullName = "Alex Carter",
            gmail = "alex@dft.com",
            phoneNumber = "01712345678",
            teamName = "Elite Gaming",
            position = "Striker",
            jerseyNumber = 10,
            dob = "2000-05-12",
            profilePhotoUrl = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde",
            status = PlayerStatus.APPROVED
        )
        val p2 = Player(
            id = "player_2",
            userId = "captain_sarah",
            fullName = "Sarah Lin",
            gmail = "sarah@dft.com",
            phoneNumber = "01812345679",
            teamName = "DFT Knights",
            position = "Midfielder",
            jerseyNumber = 8,
            dob = "1999-08-22",
            profilePhotoUrl = "https://images.unsplash.com/photo-1599566150163-29194dcaad36",
            status = PlayerStatus.PENDING
        )
        val p3 = Player(
            id = "player_3",
            userId = "captain_jake",
            fullName = "Jake Vance",
            gmail = "jake@dft.com",
            phoneNumber = "01912345680",
            teamName = "Apex Predator",
            position = "Goalkeeper",
            jerseyNumber = 1,
            dob = "2001-11-05",
            profilePhotoUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d",
            status = PlayerStatus.REJECTED
        )
        _players.value = listOf(p1, p2, p3)

        // Seed initial Auction Event
        val auctionEvent = AuctionEvent(
            id = "event_dft_2026",
            name = "DFT Super League 2026 Grand Auction",
            budgetPerTeam = 100000000.0,
            isActive = true,
            createdAt = System.currentTimeMillis()
        )
        _auctionEvents.value = listOf(auctionEvent)

        // Seed initial Auction Players
        val ap1 = AuctionPlayer(
            id = "ap_1",
            eventId = "event_dft_2026",
            name = "Erling Haaland",
            position = "Striker",
            age = 25,
            previousTeam = "Borussia Dortmund",
            basePrice = 40000000.0,
            status = AuctionPlayerStatus.AVAILABLE,
            photoUrl = "https://images.unsplash.com/photo-1508098682722-e99c43a406b2",
            auctionOrder = 1
        )
        val ap2 = AuctionPlayer(
            id = "ap_2",
            eventId = "event_dft_2026",
            name = "Kylian Mbappé",
            position = "Striker",
            age = 27,
            previousTeam = "Paris Saint-Germain",
            basePrice = 50000000.0,
            status = AuctionPlayerStatus.AVAILABLE,
            photoUrl = "https://images.unsplash.com/photo-1544005313-94ddf0286df2",
            auctionOrder = 2
        )
        val ap3 = AuctionPlayer(
            id = "ap_3",
            eventId = "event_dft_2026",
            name = "Kevin De Bruyne",
            position = "Midfielder",
            age = 34,
            previousTeam = "VfL Wolfsburg",
            basePrice = 30000000.0,
            soldPrice = 45000000.0,
            winningTeamId = "team_elite",
            winningTeamName = "Elite Gaming",
            status = AuctionPlayerStatus.SOLD,
            photoUrl = "https://images.unsplash.com/photo-1519085360753-af0119f7cbe7",
            auctionOrder = 3
        )
        val ap4 = AuctionPlayer(
            id = "ap_4",
            eventId = "event_dft_2026",
            name = "Virgil van Dijk",
            position = "Defender",
            age = 34,
            previousTeam = "Southampton FC",
            basePrice = 25000000.0,
            soldPrice = 28000000.0,
            winningTeamId = "team_knights",
            winningTeamName = "DFT Knights",
            status = AuctionPlayerStatus.SOLD,
            photoUrl = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e",
            auctionOrder = 4
        )
        val ap5 = AuctionPlayer(
            id = "ap_5",
            eventId = "event_dft_2026",
            name = "Mohamed Salah",
            position = "Striker",
            age = 33,
            previousTeam = "AS Roma",
            basePrice = 35000000.0,
            status = AuctionPlayerStatus.UNSOLD,
            photoUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb",
            auctionOrder = 5
        )
        _auctionPlayers.value = listOf(ap1, ap2, ap3, ap4, ap5)

        // Seed initial logs
        val l1 = AuctionHistoryLog(
            id = "log_1",
            eventId = "event_dft_2026",
            playerId = "ap_3",
            playerName = "Kevin De Bruyne",
            action = "SOLD",
            bidAmount = 45000000.0,
            winningTeamName = "Elite Gaming",
            timestamp = System.currentTimeMillis() - 600000
        )
        val l2 = AuctionHistoryLog(
            id = "log_2",
            eventId = "event_dft_2026",
            playerId = "ap_4",
            playerName = "Virgil van Dijk",
            action = "SOLD",
            bidAmount = 28000000.0,
            winningTeamName = "DFT Knights",
            timestamp = System.currentTimeMillis() - 300000
        )
        val l3 = AuctionHistoryLog(
            id = "log_3",
            eventId = "event_dft_2026",
            playerId = "ap_5",
            playerName = "Mohamed Salah",
            action = "UNSOLD",
            bidAmount = 0.0,
            winningTeamName = "",
            timestamp = System.currentTimeMillis() - 100000
        )
        _auctionHistory.value = listOf(l1, l2, l3)

        // Update pre-populated team budgets to match purchases
        _teams.value = _teams.value.map { team ->
            when (team.id) {
                "team_elite" -> team.copy(
                    budget = 100000000.0,
                    remainingBudget = 55000000.0,
                    squad = listOf("Kevin De Bruyne")
                )
                "team_knights" -> team.copy(
                    budget = 100000000.0,
                    remainingBudget = 72000000.0,
                    squad = listOf("Virgil van Dijk")
                )
                else -> team.copy(
                    budget = 100000000.0,
                    remainingBudget = 100000000.0,
                    squad = emptyList()
                )
            }
        }
    }

    private fun registerOrUpdateUserInLocalList(user: AppUser) {
        val currentList = _allUsers.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == user.id || it.email.equals(user.email, ignoreCase = true) }
        if (index != -1) {
            currentList[index] = user
        } else {
            currentList.add(user)
        }
        _allUsers.value = currentList
    }

    // --- Authentication ---
    fun loginWithEmail(email: String, name: String, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        val determinedRole = if (email.contains("admin", ignoreCase = true)) "Admin" else "Captain"
        if (_isFirebaseActive.value) {
            // Firebase Login
            firebaseAuth?.signInWithEmailAndPassword(email, "default123456")?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val fbUser = firebaseAuth?.currentUser
                    val user = AppUser(
                        id = fbUser?.uid ?: UUID.randomUUID().toString(),
                        name = if (name.isNotEmpty()) name else (fbUser?.displayName ?: email.substringBefore("@")),
                        email = fbUser?.email ?: email,
                        role = determinedRole
                    )
                    _currentUser.value = user
                    registerOrUpdateUserInLocalList(user)
                    syncUserToFirestore(user)
                    onSuccess()
                } else {
                    // Try creating user if not exists
                    firebaseAuth?.createUserWithEmailAndPassword(email, "default123456")?.addOnCompleteListener { createTask ->
                        if (createTask.isSuccessful) {
                            val fbUser = firebaseAuth?.currentUser
                            val user = AppUser(
                                id = fbUser?.uid ?: UUID.randomUUID().toString(),
                                name = if (name.isNotEmpty()) name else email.substringBefore("@"),
                                email = fbUser?.email ?: email,
                                role = determinedRole
                            )
                            _currentUser.value = user
                            registerOrUpdateUserInLocalList(user)
                            // Sync with firestore
                            syncUserToFirestore(user)
                            onSuccess()
                        } else {
                            onFailure(createTask.exception?.message ?: "Authentication failed")
                        }
                    }
                }
            }
        } else {
            // Offline Demo Login
            val user = AppUser(
                id = "user_" + UUID.randomUUID().toString().take(6),
                name = if (name.isNotEmpty()) name else email.substringBefore("@"),
                email = email,
                role = determinedRole
            )
            _currentUser.value = user
            registerOrUpdateUserInLocalList(user)
            onSuccess()
        }
    }

    fun loginWithGoogle(idToken: String, name: String, email: String, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        val determinedRole = if (email.contains("admin", ignoreCase = true)) "Admin" else "Captain"
        if (_isFirebaseActive.value) {
            val user = AppUser(
                id = "google_" + UUID.randomUUID().toString().take(6),
                name = name,
                email = email,
                role = determinedRole
            )
            _currentUser.value = user
            registerOrUpdateUserInLocalList(user)
            syncUserToFirestore(user)
            onSuccess()
        } else {
            val user = AppUser(
                id = "google_" + UUID.randomUUID().toString().take(6),
                name = name,
                email = email,
                role = determinedRole
            )
            _currentUser.value = user
            registerOrUpdateUserInLocalList(user)
            onSuccess()
        }
    }

    fun logout() {
        if (_isFirebaseActive.value) {
            firebaseAuth?.signOut()
        }
        _currentUser.value = null
    }

    private fun syncUserToFirestore(user: AppUser) {
        if (_isFirebaseActive.value) {
            firestore?.collection("users")?.document(user.id)?.set(user)
                ?.addOnFailureListener { e -> Log.e(TAG, "Error syncing user: ${e.message}") }
        }
    }

    // --- Team Registration ---
    fun registerTeam(name: String, captainName: String, members: List<String>, tournamentId: String, onSuccess: () -> Unit) {
        val captainId = _currentUser.value?.id ?: "guest_captain"
        val newTeam = Team(
            id = "team_" + UUID.randomUUID().toString().take(6),
            name = name,
            captainId = captainId,
            captainName = captainName,
            members = members,
            tournamentId = tournamentId,
            status = TeamStatus.PENDING
        )

        // Local state update
        val updatedTeams = _teams.value.toMutableList()
        updatedTeams.add(newTeam)
        _teams.value = updatedTeams

        // Increment team count on local tournament
        val updatedTournaments = _tournaments.value.map {
            if (it.id == tournamentId) {
                it.copy(teamsCount = it.teamsCount + 1)
            } else it
        }
        _tournaments.value = updatedTournaments

        // Also add Team to Group Alpha automatically if it fits, just to show immediate interaction!
        val updatedGroups = _groups.value.map { group ->
            if (group.tournamentId == tournamentId && group.id == "g_beta") {
                group.copy(teamIds = group.teamIds + newTeam.id)
            } else group
        }
        _groups.value = updatedGroups

        // Firestore sync
        if (_isFirebaseActive.value) {
            firestore?.collection("teams")?.document(newTeam.id)?.set(newTeam)
                ?.addOnSuccessListener { onSuccess() }
                ?.addOnFailureListener { Log.e(TAG, "Error saving team to Firestore") }
        } else {
            onSuccess()
        }
    }

    // --- Group Chat ---
    fun sendChatMessage(groupId: String, messageText: String) {
        val user = _currentUser.value ?: return
        val newMessage = ChatMessage(
            id = UUID.randomUUID().toString(),
            groupId = groupId,
            senderId = user.id,
            senderName = user.name,
            message = messageText,
            timestamp = System.currentTimeMillis()
        )

        val updatedChats = _chatMessages.value.toMutableList()
        updatedChats.add(newMessage)
        _chatMessages.value = updatedChats

        if (_isFirebaseActive.value) {
            firestore?.collection("chats")?.document(newMessage.id)?.set(newMessage)
                ?.addOnFailureListener { Log.e(TAG, "Error saving chat to Firestore") }
        }
    }

    // --- Standings / Points Table Calculations ---
    fun getStandingsForTournament(tournamentId: String): List<StandingsRow> {
        // Derive dynamic standings from approved teams and played matches
        val tournamentTeams = _teams.value.filter { it.tournamentId == tournamentId }
        val tournamentMatches = _matches.value.filter { it.tournamentId == tournamentId && it.status == MatchStatus.COMPLETED }

        val standingsMap = mutableMapOf<String, StandingsRow>()

        // Initialize standings rows for all tournament teams
        tournamentTeams.forEach { team ->
            standingsMap[team.id] = StandingsRow(
                teamId = team.id,
                teamName = team.name,
                played = 0, won = 0, lost = 0, drawn = 0, points = 0, netRunRate = 0.0
            )
        }

        // Apply results from played matches
        tournamentMatches.forEach { match ->
            val rowA = standingsMap[match.teamAId]
            val rowB = standingsMap[match.teamBId]

            if (rowA != null && rowB != null) {
                val scoreA = match.scoreA
                val scoreB = match.scoreB

                val newRowA: StandingsRow
                val newRowB: StandingsRow

                if (scoreA > scoreB) {
                    newRowA = rowA.copy(played = rowA.played + 1, won = rowA.won + 1, points = rowA.points + 3, netRunRate = rowA.netRunRate + 0.5)
                    newRowB = rowB.copy(played = rowB.played + 1, lost = rowB.lost + 1, points = rowB.points + 0, netRunRate = rowB.netRunRate - 0.5)
                } else if (scoreB > scoreA) {
                    newRowA = rowA.copy(played = rowA.played + 1, lost = rowA.lost + 1, points = rowA.points + 0, netRunRate = rowA.netRunRate - 0.5)
                    newRowB = rowB.copy(played = rowB.played + 1, won = rowB.won + 1, points = rowB.points + 3, netRunRate = rowB.netRunRate + 0.5)
                } else {
                    newRowA = rowA.copy(played = rowA.played + 1, drawn = rowA.drawn + 1, points = rowA.points + 1)
                    newRowB = rowB.copy(played = rowB.played + 1, drawn = rowB.drawn + 1, points = rowB.points + 1)
                }

                standingsMap[match.teamAId] = newRowA
                standingsMap[match.teamBId] = newRowB
            }
        }

        // Return sorted list descending by points, then won, then netRunRate
        return standingsMap.values.sortedWith(
            compareByDescending<StandingsRow> { it.points }
                .thenByDescending { it.won }
                .thenByDescending { it.netRunRate }
        )
    }

    // --- Admin Operations ---

    // Tournaments
    fun createTournament(t: Tournament) {
        val updatedList = _tournaments.value.toMutableList()
        updatedList.add(t)
        _tournaments.value = updatedList

        if (_isFirebaseActive.value) {
            firestore?.collection("tournaments")?.document(t.id)?.set(t)
                ?.addOnFailureListener { Log.e(TAG, "Error saving tournament to Firestore") }
        }
    }

    fun updateTournament(t: Tournament) {
        val updatedList = _tournaments.value.map { if (it.id == t.id) t else it }
        _tournaments.value = updatedList

        if (_isFirebaseActive.value) {
            firestore?.collection("tournaments")?.document(t.id)?.set(t)
                ?.addOnFailureListener { Log.e(TAG, "Error updating tournament to Firestore") }
        }
    }

    fun deleteTournament(id: String) {
        val updatedList = _tournaments.value.filter { it.id != id }
        _tournaments.value = updatedList

        if (_isFirebaseActive.value) {
            firestore?.collection("tournaments")?.document(id)?.delete()
                ?.addOnFailureListener { Log.e(TAG, "Error deleting tournament from Firestore") }
        }
    }

    // Groups
    fun createGroup(g: Group) {
        val updatedList = _groups.value.toMutableList()
        updatedList.add(g)
        _groups.value = updatedList

        if (_isFirebaseActive.value) {
            firestore?.collection("groups")?.document(g.id)?.set(g)
                ?.addOnFailureListener { Log.e(TAG, "Error saving group to Firestore") }
        }
    }

    fun deleteGroup(id: String) {
        val updatedList = _groups.value.filter { it.id != id }
        _groups.value = updatedList

        if (_isFirebaseActive.value) {
            firestore?.collection("groups")?.document(id)?.delete()
                ?.addOnFailureListener { Log.e(TAG, "Error deleting group from Firestore") }
        }
    }

    // Teams
    fun createTeam(team: Team) {
        val updatedList = _teams.value.toMutableList()
        updatedList.add(team)
        _teams.value = updatedList

        if (_isFirebaseActive.value) {
            firestore?.collection("teams")?.document(team.id)?.set(team)
                ?.addOnFailureListener { Log.e(TAG, "Error saving team to Firestore") }
        }
    }

    fun updateTeam(team: Team) {
        val updatedList = _teams.value.map { if (it.id == team.id) team else it }
        _teams.value = updatedList

        if (_isFirebaseActive.value) {
            firestore?.collection("teams")?.document(team.id)?.set(team)
                ?.addOnFailureListener { Log.e(TAG, "Error updating team to Firestore") }
        }
    }

    fun deleteTeam(teamId: String) {
        val updatedList = _teams.value.filter { it.id != teamId }
        _teams.value = updatedList

        // Remove team from groups as well
        val updatedGroups = _groups.value.map { group ->
            if (group.teamIds.contains(teamId)) {
                val newGroup = group.copy(teamIds = group.teamIds - teamId)
                if (_isFirebaseActive.value) {
                    firestore?.collection("groups")?.document(newGroup.id)?.set(newGroup)
                }
                newGroup
            } else group
        }
        _groups.value = updatedGroups

        if (_isFirebaseActive.value) {
            firestore?.collection("teams")?.document(teamId)?.delete()
                ?.addOnFailureListener { Log.e(TAG, "Error deleting team from Firestore") }
        }
    }

    fun addTeamToGroup(groupId: String, teamId: String) {
        val updatedGroups = _groups.value.map { group ->
            if (group.id == groupId) {
                if (!group.teamIds.contains(teamId)) {
                    val newGroup = group.copy(teamIds = group.teamIds + teamId)
                    if (_isFirebaseActive.value) {
                        firestore?.collection("groups")?.document(newGroup.id)?.set(newGroup)
                    }
                    newGroup
                } else group
            } else group
        }
        _groups.value = updatedGroups
    }

    fun removeTeamFromGroup(groupId: String, teamId: String) {
        val updatedGroups = _groups.value.map { group ->
            if (group.id == groupId) {
                val newGroup = group.copy(teamIds = group.teamIds - teamId)
                if (_isFirebaseActive.value) {
                    firestore?.collection("groups")?.document(newGroup.id)?.set(newGroup)
                }
                newGroup
            } else group
        }
        _groups.value = updatedGroups
    }

    // Matches
    fun createMatch(match: Match) {
        val updatedList = _matches.value.toMutableList()
        updatedList.add(match)
        _matches.value = updatedList

        if (_isFirebaseActive.value) {
            firestore?.collection("matches")?.document(match.id)?.set(match)
                ?.addOnFailureListener { Log.e(TAG, "Error saving match to Firestore") }
        }
    }

    fun updateMatch(match: Match) {
        val updatedList = _matches.value.map { if (it.id == match.id) match else it }
        _matches.value = updatedList

        if (_isFirebaseActive.value) {
            firestore?.collection("matches")?.document(match.id)?.set(match)
                ?.addOnFailureListener { Log.e(TAG, "Error updating match to Firestore") }
        }
    }

    fun deleteMatch(matchId: String) {
        val updatedList = _matches.value.filter { it.id != matchId }
        _matches.value = updatedList

        if (_isFirebaseActive.value) {
            firestore?.collection("matches")?.document(matchId)?.delete()
                ?.addOnFailureListener { Log.e(TAG, "Error deleting match from Firestore") }
        }
    }

    // Notifications
    fun sendNotification(title: String, message: String) {
        val notif = Notification(
            id = "notif_" + UUID.randomUUID().toString().take(6),
            title = title,
            message = message,
            timestamp = System.currentTimeMillis()
        )
        val updatedList = _notifications.value.toMutableList()
        updatedList.add(0, notif) // Add to the top
        _notifications.value = updatedList

        // Post a message in system chats too to make groups see it!
        _groups.value.forEach { group ->
            val sysMessage = ChatMessage(
                id = UUID.randomUUID().toString(),
                groupId = group.id,
                senderId = "system",
                senderName = "System Broadcast",
                message = "📢 $title: $message",
                timestamp = System.currentTimeMillis(),
                isSystem = true
            )
            val updatedChats = _chatMessages.value.toMutableList()
            updatedChats.add(sysMessage)
            _chatMessages.value = updatedChats

            if (_isFirebaseActive.value) {
                firestore?.collection("chats")?.document(sysMessage.id)?.set(sysMessage)
            }
        }

        if (_isFirebaseActive.value) {
            firestore?.collection("notifications")?.document(notif.id)?.set(notif)
                ?.addOnFailureListener { Log.e(TAG, "Error saving notification to Firestore") }
        }
    }

    // Users
    fun updateUserRole(userId: String, newRole: String) {
        val updatedList = _allUsers.value.map {
            if (it.id == userId) {
                val updatedUser = it.copy(role = newRole)
                if (userId == _currentUser.value?.id) {
                    _currentUser.value = updatedUser
                }
                if (_isFirebaseActive.value) {
                    firestore?.collection("users")?.document(userId)?.set(updatedUser)
                }
                updatedUser
            } else it
        }
        _allUsers.value = updatedList
    }

    fun updateProfile(updatedUser: AppUser, onComplete: () -> Unit) {
        _currentUser.value = updatedUser
        val updatedList = _allUsers.value.map {
            if (it.id == updatedUser.id) updatedUser else it
        }
        _allUsers.value = updatedList
        
        if (_isFirebaseActive.value) {
            firestore?.collection("users")?.document(updatedUser.id)?.set(updatedUser)
                ?.addOnCompleteListener { onComplete() }
        } else {
            onComplete()
        }
    }

    // Players CRUD Operations
    fun registerPlayer(player: Player, onComplete: () -> Unit) {
        val updated = _players.value.toMutableList()
        updated.removeAll { it.userId == player.userId || it.id == player.id }
        updated.add(player)
        _players.value = updated
        
        if (_isFirebaseActive.value) {
            firestore?.collection("players")?.document(player.id)?.set(player)
                ?.addOnCompleteListener { onComplete() }
        } else {
            onComplete()
        }
    }

    fun updatePlayer(player: Player, onComplete: () -> Unit = {}) {
        val updated = _players.value.map {
            if (it.id == player.id) player else it
        }
        _players.value = updated
        
        if (_isFirebaseActive.value) {
            firestore?.collection("players")?.document(player.id)?.set(player)
                ?.addOnCompleteListener { onComplete() }
        } else {
            onComplete()
        }
    }

    fun deletePlayer(playerId: String, onComplete: () -> Unit = {}) {
        val updated = _players.value.filter { it.id != playerId }
        _players.value = updated
        
        if (_isFirebaseActive.value) {
            firestore?.collection("players")?.document(playerId)?.delete()
                ?.addOnCompleteListener { onComplete() }
        } else {
            onComplete()
        }
    }

    // --- Football Auction Operations ---
    fun createAuctionEvent(event: AuctionEvent, onComplete: () -> Unit) {
        val updated = _auctionEvents.value.toMutableList()
        updated.removeAll { it.id == event.id }
        updated.add(event)
        _auctionEvents.value = updated.sortedByDescending { it.createdAt }

        if (_isFirebaseActive.value) {
            firestore?.collection("auction_events")?.document(event.id)?.set(event)
                ?.addOnCompleteListener { onComplete() }
        } else {
            onComplete()
        }
    }

    fun addAuctionPlayer(player: AuctionPlayer, onComplete: () -> Unit) {
        val updated = _auctionPlayers.value.toMutableList()
        updated.add(player)
        _auctionPlayers.value = updated.sortedBy { it.auctionOrder }

        if (_isFirebaseActive.value) {
            firestore?.collection("auction_players")?.document(player.id)?.set(player)
                ?.addOnCompleteListener { onComplete() }
        } else {
            onComplete()
        }
    }

    fun updateAuctionPlayer(player: AuctionPlayer, onComplete: () -> Unit = {}) {
        val updated = _auctionPlayers.value.map {
            if (it.id == player.id) player else it
        }
        _auctionPlayers.value = updated.sortedBy { it.auctionOrder }

        if (_isFirebaseActive.value) {
            firestore?.collection("auction_players")?.document(player.id)?.set(player)
                ?.addOnCompleteListener { onComplete() }
        } else {
            onComplete()
        }
    }

    fun deleteAuctionPlayer(playerId: String, onComplete: () -> Unit = {}) {
        val updated = _auctionPlayers.value.filter { it.id != playerId }
        _auctionPlayers.value = updated.sortedBy { it.auctionOrder }

        if (_isFirebaseActive.value) {
            firestore?.collection("auction_players")?.document(playerId)?.delete()
                ?.addOnCompleteListener { onComplete() }
        } else {
            onComplete()
        }
    }

    fun recordAuctionBid(
        player: AuctionPlayer,
        winningTeamId: String,
        bidAmount: Double,
        onComplete: (Boolean, String) -> Unit
    ) {
        val logId = "log_" + UUID.randomUUID().toString().take(6)

        if (winningTeamId.isBlank()) {
            // Unsold player flow
            val updatedPlayer = player.copy(
                status = AuctionPlayerStatus.UNSOLD,
                soldPrice = 0.0,
                winningTeamId = "",
                winningTeamName = ""
            )

            val log = AuctionHistoryLog(
                id = logId,
                eventId = player.eventId,
                playerId = player.id,
                playerName = player.name,
                action = "UNSOLD",
                bidAmount = 0.0,
                winningTeamName = "",
                timestamp = System.currentTimeMillis()
            )

            // Update local memory
            _auctionPlayers.value = _auctionPlayers.value.map { if (it.id == player.id) updatedPlayer else it }
            _auctionHistory.value = (listOf(log) + _auctionHistory.value).sortedByDescending { it.timestamp }

            // Update Firebase
            if (_isFirebaseActive.value) {
                val batch = firestore?.batch()
                if (batch != null) {
                    val pRef = firestore!!.collection("auction_players").document(player.id)
                    val lRef = firestore!!.collection("auction_history").document(logId)
                    batch.set(pRef, updatedPlayer)
                    batch.set(lRef, log)
                    batch.commit().addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            onComplete(true, "Player marked as UNSOLD successfully.")
                        } else {
                            onComplete(false, "Failed to update Firestore: ${task.exception?.message}")
                        }
                    }
                } else {
                    onComplete(false, "Firestore batch error")
                }
            } else {
                onComplete(true, "Player marked as UNSOLD locally.")
            }
        } else {
            // Sold player flow
            val team = _teams.value.find { it.id == winningTeamId }
            if (team == null) {
                onComplete(false, "Winning team not found.")
                return
            }

            if (team.remainingBudget < bidAmount) {
                onComplete(false, "Insufficient budget. Team ${team.name} only has ${team.remainingBudget} remaining.")
                return
            }

            val updatedPlayer = player.copy(
                status = AuctionPlayerStatus.SOLD,
                soldPrice = bidAmount,
                winningTeamId = winningTeamId,
                winningTeamName = team.name
            )

            val updatedTeam = team.copy(
                remainingBudget = team.remainingBudget - bidAmount,
                squad = team.squad + player.name
            )

            val log = AuctionHistoryLog(
                id = logId,
                eventId = player.eventId,
                playerId = player.id,
                playerName = player.name,
                action = "SOLD",
                bidAmount = bidAmount,
                winningTeamName = team.name,
                timestamp = System.currentTimeMillis()
            )

            // Update local memory
            _auctionPlayers.value = _auctionPlayers.value.map { if (it.id == player.id) updatedPlayer else it }
            _teams.value = _teams.value.map { if (it.id == team.id) updatedTeam else it }
            _auctionHistory.value = (listOf(log) + _auctionHistory.value).sortedByDescending { it.timestamp }

            // Update Firebase
            if (_isFirebaseActive.value) {
                val batch = firestore?.batch()
                if (batch != null) {
                    val pRef = firestore!!.collection("auction_players").document(player.id)
                    val tRef = firestore!!.collection("teams").document(team.id)
                    val lRef = firestore!!.collection("auction_history").document(logId)
                    batch.set(pRef, updatedPlayer)
                    batch.set(tRef, updatedTeam)
                    batch.set(lRef, log)
                    batch.commit().addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            onComplete(true, "Player sold to ${team.name} successfully.")
                        } else {
                            onComplete(false, "Failed to update Firestore: ${task.exception?.message}")
                        }
                    }
                } else {
                    onComplete(false, "Firestore batch error")
                }
            } else {
                onComplete(true, "Player successfully sold locally.")
            }
        }
    }

    fun changePassword(current: String, new: String, onResult: (Boolean, String) -> Unit) {
        if (_isFirebaseActive.value) {
            val fbUser = firebaseAuth?.currentUser
            if (fbUser != null) {
                fbUser.updatePassword(new).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        onResult(true, "Password successfully updated in Firebase.")
                    } else {
                        onResult(false, task.exception?.message ?: "Failed to update password.")
                    }
                }
            } else {
                onResult(false, "No active Firebase session.")
            }
        } else {
            // Local mode, simulate successfully
            onResult(true, "Password successfully changed in local database.")
        }
    }
}
