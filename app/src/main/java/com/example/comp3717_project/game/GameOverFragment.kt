package com.example.comp3717_project.game

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.comp3717_project.R

/**
 * A game over fragment.
 */
class GameOverFragment : Fragment() {
    private lateinit var textViewScore: TextView
    private lateinit var textViewHighScore: TextView

    interface OnGameOverOptionsSelectedListener {
        fun onRestart()
        fun onQuit()
    }

    private lateinit var listener: OnGameOverOptionsSelectedListener


    fun updateScores(score: Int, highScore: Int) {
        // Update your views based on the new values of param1 and param2
        textViewScore.text = getString(R.string.game_score, score)
        textViewHighScore.text = getString(R.string.game_highScore, highScore)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_game_over, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        textViewScore = view.findViewById(R.id.textView_gameOver_score)
        textViewHighScore = view.findViewById(R.id.textView_gameOver_highScore)
        val restartButton = view.findViewById<Button>(R.id.button_gameOver_restart)
        val quitButton = view.findViewById<Button>(R.id.button_gameOver_quit)


        restartButton.setOnClickListener { listener.onRestart() }
        quitButton.setOnClickListener { listener.onQuit() }

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = context as OnGameOverOptionsSelectedListener
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement OnFragmentInteractionListener")
        }
    }

}