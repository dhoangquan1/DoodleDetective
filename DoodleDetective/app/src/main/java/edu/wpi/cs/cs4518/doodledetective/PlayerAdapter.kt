package edu.wpi.cs.cs4518.doodledetective

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import edu.wpi.cs.cs4518.stepchart.database.PlayerData

class PlayerAdapter :
    ListAdapter<PlayerData, PlayerAdapter.ItemViewHolder>(ItemDiffCallback()) {

    class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameText: TextView = view.findViewById(R.id.player_name)
        val scoreText: TextView = view.findViewById(R.id.score)

        fun bind(player: PlayerData) {
            nameText.text = player.name
            scoreText.text = player.score.toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_player, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
// implementing the abstract class
// providing the checks for RecyclerView
class ItemDiffCallback : DiffUtil.ItemCallback<PlayerData>() {
    override fun areItemsTheSame(oldItem: PlayerData, newItem: PlayerData): Boolean {
        return oldItem.name == newItem.name
    }

    override fun areContentsTheSame(oldItem: PlayerData, newItem: PlayerData): Boolean {
        return oldItem == newItem
    }
}