package edu.wpi.cs.cs4518.doodledetective

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.EditText
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import edu.wpi.cs.cs4518.doodledetective.databinding.ActivityLeaderboardBinding
import edu.wpi.cs.cs4518.stepchart.database.PlayerData
import kotlin.getValue

class LeaderboardActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLeaderboardBinding
    private lateinit var adapter: PlayerAdapter
    private val viewModel: LeaderboardViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLeaderboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = PlayerAdapter()

        binding.leaderboardRecycler.layoutManager = LinearLayoutManager(this)
        binding.leaderboardRecycler.adapter = adapter

        viewModel.getLowestScore()
        viewModel.fetchLeaderboard()

        val recentScore = intent.getIntExtra("score", 0)
        val lowestScore = viewModel.lowestScore

        if(recentScore > 0 && recentScore > lowestScore && !viewModel.justAdded ){
            showTextInputDialog { input ->
                val player = PlayerData(name = input, score = recentScore)
                viewModel.insertPlayer(player)
                viewModel.justAdded = true
            }
        }

        viewModel.leaderboard.observe(this, Observer { players ->
            adapter.submitList(players)
        })

        binding.playButton.setOnClickListener {
            navigateToPlay()
        }

    }

    fun showTextInputDialog(onConfirm: (String) -> Unit) {
        val editText = EditText(this)
        editText.hint = "Enter your name"

        editText.isSingleLine = true
        editText.maxLines = 1
        editText.ellipsize = TextUtils.TruncateAt.END

        val dialog = AlertDialog.Builder(this)
            .setTitle("New High Score!")
            .setView(editText)
            .setPositiveButton("OK") { dialog, _ ->
                val input = editText.text.toString()
                onConfirm(input)
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .create()

        dialog.show()
    }

    fun navigateToPlay(){
        val intent = Intent(this, PlayActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}