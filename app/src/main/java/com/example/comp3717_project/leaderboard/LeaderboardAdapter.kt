package com.example.comp3717_project.leaderboard

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.comp3717_project.database.User
import com.example.comp3717_project.R


class LeaderboardAdapter(private val mList: ArrayList<User>) :
    RecyclerView.Adapter<LeaderboardAdapter.ViewHolder>() {

    // Holds the views
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val rankTextView: TextView = itemView.findViewById(R.id.textView_userScore_rank)
        val userTextView: TextView = itemView.findViewById(R.id.textView_userScore_user)
        val scoreTextView: TextView = itemView.findViewById(R.id.textView_userScore_score)
    }

    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(parent.context).inflate(R.layout.user_score, parent, false)

        return ViewHolder(view)
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        val rank = position + 1
        holder.rankTextView.text = rank.toString()
        holder.userTextView.text = mList[position].getName()
        holder.scoreTextView.text = mList[position].getHighScore().toString()
    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return mList.size
    }

}