package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn

class TournamentViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = TournamentRepository(application)

    val isFirebaseActive: StateFlow<Boolean> = repository.isFirebaseActive
    val currentUser: StateFlow<AppUser?> = repository.currentUser
    val tournaments: StateFlow<List<Tournament>> = repository.tournaments
    val teams: StateFlow<List<Team>> = repository.teams
    val groups: StateFlow<List<Group>> = repository.groups
    val matches: StateFlow<List<Match>> = repository.matches
    val chatMessages: StateFlow<List<ChatMessage>> = repository.chatMessages
    val notifications: StateFlow<List<Notification>> = repository.notifications
    val allUsers: StateFlow<List<AppUser>> = repository.allUsers
    val players: StateFlow<List<Player>> = repository.players
    val auctionEvents: StateFlow<List<AuctionEvent>> = repository.auctionEvents
    val auctionPlayers: StateFlow<List<AuctionPlayer>> = repository.auctionPlayers
    val auctionHistory: StateFlow<List<AuctionHistoryLog>> = repository.auctionHistory

    private val _loginError = MutableStateFlow<String?>(null)
    val loginError: StateFlow<String?> = _loginError.asStateFlow()

    private val _isRegisteringTeam = MutableStateFlow(false)
    val isRegisteringTeam: StateFlow<Boolean> = _isRegisteringTeam.asStateFlow()

    fun loginWithEmail(email: String, name: String, onAuthSuccess: () -> Unit) {
        _loginError.value = null
        repository.loginWithEmail(
            email = email,
            name = name,
            onSuccess = {
                onAuthSuccess()
            },
            onFailure = { error ->
                _loginError.value = error
            }
        )
    }

    fun loginWithGoogle(email: String, name: String, onAuthSuccess: () -> Unit) {
        _loginError.value = null
        repository.loginWithGoogle(
            idToken = "mock_google_token",
            name = name,
            email = email,
            onSuccess = {
                onAuthSuccess()
            },
            onFailure = { error ->
                _loginError.value = error
            }
        )
    }

    fun logout() {
        repository.logout()
    }

    fun registerTeam(teamName: String, captainName: String, memberNames: List<String>, tournamentId: String, onComplete: () -> Unit) {
        _isRegisteringTeam.value = true
        repository.registerTeam(
            name = teamName,
            captainName = captainName,
            members = memberNames,
            tournamentId = tournamentId,
            onSuccess = {
                _isRegisteringTeam.value = false
                onComplete()
            }
        )
    }

    fun sendChatMessage(groupId: String, messageText: String) {
        if (messageText.isNotBlank()) {
            repository.sendChatMessage(groupId, messageText)
        }
    }

    fun getStandings(tournamentId: String): List<StandingsRow> {
        return repository.getStandingsForTournament(tournamentId)
    }

    // --- Admin Operations ---
    fun createTournament(t: Tournament) = repository.createTournament(t)
    fun updateTournament(t: Tournament) = repository.updateTournament(t)
    fun deleteTournament(id: String) = repository.deleteTournament(id)

    fun createGroup(g: Group) = repository.createGroup(g)
    fun deleteGroup(id: String) = repository.deleteGroup(id)

    fun createTeam(team: Team) = repository.createTeam(team)
    fun updateTeam(team: Team) = repository.updateTeam(team)
    fun deleteTeam(teamId: String) = repository.deleteTeam(teamId)
    fun addTeamToGroup(groupId: String, teamId: String) = repository.addTeamToGroup(groupId, teamId)
    fun removeTeamFromGroup(groupId: String, teamId: String) = repository.removeTeamFromGroup(groupId, teamId)

    fun createMatch(match: Match) = repository.createMatch(match)
    fun updateMatch(match: Match) = repository.updateMatch(match)
    fun deleteMatch(matchId: String) = repository.deleteMatch(matchId)

    fun sendNotification(title: String, message: String) = repository.sendNotification(title, message)

    // --- Players Operations ---
    fun registerPlayer(player: Player, onComplete: () -> Unit) = repository.registerPlayer(player, onComplete)
    fun updatePlayer(player: Player, onComplete: () -> Unit = {}) = repository.updatePlayer(player, onComplete)
    fun deletePlayer(playerId: String, onComplete: () -> Unit = {}) = repository.deletePlayer(playerId, onComplete)

    fun updateUserRole(userId: String, newRole: String) = repository.updateUserRole(userId, newRole)

    // --- Football Auction Operations ---
    fun createAuctionEvent(event: AuctionEvent, onComplete: () -> Unit) =
        repository.createAuctionEvent(event, onComplete)

    fun addAuctionPlayer(player: AuctionPlayer, onComplete: () -> Unit) =
        repository.addAuctionPlayer(player, onComplete)

    fun updateAuctionPlayer(player: AuctionPlayer, onComplete: () -> Unit = {}) =
        repository.updateAuctionPlayer(player, onComplete)

    fun deleteAuctionPlayer(playerId: String, onComplete: () -> Unit = {}) =
        repository.deleteAuctionPlayer(playerId, onComplete)

    fun recordAuctionBid(
        player: AuctionPlayer,
        winningTeamId: String,
        bidAmount: Double,
        onComplete: (Boolean, String) -> Unit
    ) = repository.recordAuctionBid(player, winningTeamId, bidAmount, onComplete)

    // --- App Settings & User preferences states ---
    private val _isDarkMode = MutableStateFlow(true)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    private val _language = MutableStateFlow("English") // "English" or "Bangla"
    val language: StateFlow<String> = _language.asStateFlow()

    private val _notificationsEnabled = MutableStateFlow(true)
    val notificationsEnabled: StateFlow<Boolean> = _notificationsEnabled.asStateFlow()

    fun setDarkMode(enabled: Boolean) {
        _isDarkMode.value = enabled
    }

    fun setLanguage(lang: String) {
        _language.value = lang
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        _notificationsEnabled.value = enabled
    }

    fun updateProfile(name: String, email: String, avatarUrl: String, onComplete: () -> Unit) {
        val user = currentUser.value ?: return
        val updatedUser = user.copy(name = name, email = email, avatarUrl = avatarUrl)
        repository.updateProfile(updatedUser) {
            onComplete()
        }
    }

    fun changePassword(current: String, new: String, onResult: (Boolean, String) -> Unit) {
        repository.changePassword(current, new, onResult)
    }
}
