package edu.wpi.cs.cs4518.doodledetective

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import edu.wpi.cs.cs4518.stepchart.database.PlayerData
import edu.wpi.cs.cs4518.stepchart.database.PlayerDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LeaderboardViewModel(application: Application) : AndroidViewModel(application) {
    private val playerDatabase = PlayerDatabase.getInstance(application)
    private val playerDao = playerDatabase.stepDataDao()

    private val _leaderboard = MutableLiveData<List<PlayerData>>()
    val leaderboard: LiveData<List<PlayerData>> = _leaderboard

    var lowestScore: Int = 0
    var justAdded: Boolean = false

    fun fetchLeaderboard() {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                playerDao.getLeaderboard()
            }
            _leaderboard.value = result
        }
    }

    fun insertPlayer(player: PlayerData) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                playerDao.insert(player)
                val score = playerDao.get10thHighestScore()
                if(score!=null){
                    playerDao.cleanLeaderboard(score)
                }
                fetchLeaderboard()
            }
        }
    }

    fun getLowestScore() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val score = playerDao.get10thHighestScore() ?: 0
                lowestScore = score
            }
        }
    }

}