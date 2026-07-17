package com.example.data

import java.util.UUID

data class AppUser(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val avatarUrl: String = "",
    val role: String = "Member" // Captain, Member, Admin
)

data class Tournament(
    val id: String = UUID.randomUUID().toString(),
    val name: String = "",
    val gameName: String = "",
    val status: TournamentStatus = TournamentStatus.UPCOMING,
    val startDate: String = "",
    val endDate: String = "",
    val teamsCount: Int = 0,
    val maxTeams: Int = 16,
    val prizePool: String = "",
    val description: String = "",
    val imageUrl: String = ""
)

enum class TournamentStatus {
    UPCOMING, ONGOING, COMPLETED
}

data class Team(
    val id: String = UUID.randomUUID().toString(),
    val name: String = "",
    val captainId: String = "",
    val captainName: String = "",
    val members: List<String> = emptyList(),
    val tournamentId: String = "",
    val logoUrl: String = "",
    val status: TeamStatus = TeamStatus.PENDING,
    val budget: Double = 100000000.0,
    val remainingBudget: Double = 100000000.0,
    val squad: List<String> = emptyList(), // list of AuctionPlayer names/IDs
    val managerName: String = ""
)

enum class TeamStatus {
    PENDING, APPROVED, REJECTED
}

data class AuctionEvent(
    val id: String = "",
    val name: String = "",
    val budgetPerTeam: Double = 100000000.0,
    val isActive: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

enum class AuctionPlayerStatus {
    AVAILABLE, SOLD, UNSOLD
}

data class AuctionPlayer(
    val id: String = "",
    val eventId: String = "",
    val name: String = "",
    val position: String = "",
    val age: Int = 22,
    val previousTeam: String = "",
    val basePrice: Double = 100000.0,
    val soldPrice: Double = 0.0,
    val winningTeamId: String = "",
    val winningTeamName: String = "",
    val status: AuctionPlayerStatus = AuctionPlayerStatus.AVAILABLE,
    val photoUrl: String = "",
    val auctionOrder: Int = 0,
    val gmail: String = "",
    val phoneNumber: String = "",
    val jerseyNumber: Int = 0,
    val dob: String = ""
)

data class AuctionHistoryLog(
    val id: String = "",
    val eventId: String = "",
    val playerId: String = "",
    val playerName: String = "",
    val action: String = "", // "SOLD", "UNSOLD"
    val bidAmount: Double = 0.0,
    val winningTeamName: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

data class Group(
    val id: String = UUID.randomUUID().toString(),
    val name: String = "", // e.g., Group A
    val description: String = "",
    val tournamentId: String = "",
    val teamIds: List<String> = emptyList(),
    val code: String = "" // Unique 6-digit group code
)

data class Notification(
    val id: String = UUID.randomUUID().toString(),
    val title: String = "",
    val message: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

data class Match(
    val id: String = UUID.randomUUID().toString(),
    val tournamentId: String = "",
    val teamAId: String = "",
    val teamAName: String = "",
    val teamBId: String = "",
    val teamBName: String = "",
    val scoreA: Int = 0,
    val scoreB: Int = 0,
    val status: MatchStatus = MatchStatus.UPCOMING,
    val time: String = "",
    val stage: String = "Group Stage", // e.g., Group Stage, Semi Final, Final
    val date: String = "2026-07-16",
    val venue: String = "DFT Cyber Arena",
    val referee: String = "Marshall Carter",
    val statistics: MatchStatistics = MatchStatistics(),
    val lineupA: List<String> = emptyList(),
    val lineupB: List<String> = emptyList(),
    val manOfTheMatch: String = "",
    val events: List<MatchEvent> = emptyList()
)

data class MatchStatistics(
    val killsA: Int = 0,
    val killsB: Int = 0,
    val assistsA: Int = 0,
    val assistsB: Int = 0,
    val deathsA: Int = 0,
    val deathsB: Int = 0,
    val objectiveA: Int = 0, // e.g. rounds, turrets, dragon kills
    val objectiveB: Int = 0,
    val ratingA: Double = 5.0, // average rating
    val ratingB: Double = 5.0
)

data class MatchEvent(
    val id: String = UUID.randomUUID().toString(),
    val minute: Int = 0,
    val type: MatchEventType = MatchEventType.KILL,
    val teamId: String = "", // empty if general system event, otherwise which team
    val playerName: String = "",
    val description: String = ""
)

enum class MatchEventType {
    KILL, OBJECTIVE, CARD_YELLOW, CARD_RED, PAUSE, RESUME, UNKNOWN
}

enum class MatchStatus {
    UPCOMING, LIVE, COMPLETED
}

data class StandingsRow(
    val teamId: String = "",
    val teamName: String = "",
    val played: Int = 0,
    val won: Int = 0,
    val lost: Int = 0,
    val drawn: Int = 0,
    val points: Int = 0,
    val netRunRate: Double = 0.0
)

data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val groupId: String = "",
    val senderId: String = "",
    val senderName: String = "",
    val message: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val isSystem: Boolean = false
)

enum class PlayerStatus {
    PENDING, APPROVED, REJECTED
}

data class Player(
    val id: String = "",
    val userId: String = "",
    val fullName: String = "",
    val gmail: String = "",
    val phoneNumber: String = "",
    val teamName: String = "",
    val position: String = "",
    val jerseyNumber: Int = 0,
    val dob: String = "",
    val profilePhotoUrl: String = "",
    val status: PlayerStatus = PlayerStatus.PENDING
)
