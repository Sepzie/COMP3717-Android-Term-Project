package com.example.comp3717_project.game

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.comp3717_project.MainActivity
import com.example.comp3717_project.R
import com.example.comp3717_project.game.GameView.Companion.GAME_UPDATE_RATE
import com.google.android.material.navigation.NavigationView
import com.example.comp3717_project.databinding.ActivityGameBinding


class GameActivity : AppCompatActivity(), GameOverFragment.OnGameOverOptionsSelectedListener {
    private lateinit var binding: ActivityGameBinding
    private lateinit var gameView: GameView
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var runnable: Runnable
    private lateinit var soundSwitch: SwitchCompat
    private lateinit var highScoreTextView: TextView
    var highScore: Int = 0
        set(value) {
            if (value >= field) {
                field = value
                highScoreTextView.text = getString(R.string.game_highScore, highScore)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeVariables()

        initializeGame()

        initializePauseDrawer()

    }

    private fun initializeVariables() {
        val switchMenuHeader = binding.navigationView.getHeaderView(0)
        highScoreTextView = switchMenuHeader.findViewById(R.id.textView_pauseMenuheader_highScore)
        highScore = intent.getIntExtra("high_score", 0)

        val switchMenuSoundToggleItem =
            binding.navigationView.menu.findItem(R.id.navItem_pauseMenu_toggleSound)
        soundSwitch = switchMenuSoundToggleItem.actionView!!.findViewById(R.id.switch_sound)

    }

    private fun initializeGame() {
        // Initialize the GameView and add it to the layout
        // Create AttributeSet
        gameView = GameView(this, null)
        binding.frameLayoutGame.addView(gameView, 0)
        // Set up the sound switch
        soundSwitch.setOnCheckedChangeListener { _, isChecked ->
            sendToggleMusicBroadcast(isChecked)
            gameView.toggleSound(isChecked)
        }


        // Update the game state and redraw the UI at a fixed rate
        runnable = object : Runnable {
            override fun run() {
                gameView.update()
                handler.postDelayed(this, GAME_UPDATE_RATE)
            }
        }
        handler.postDelayed(runnable, GAME_UPDATE_RATE)
    }

    private fun initializePauseDrawer() {
        // Set up the ActionBarDrawerToggle
        val toggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayoutGame,
            R.string.pause_drawer_open,
            R.string.pause_drawer_close
        )
        binding.drawerLayoutGame.addDrawerListener(toggle)
        binding.drawerLayoutGame.addDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                // Handle drawer sliding events
                if (slideOffset > 0) {
                    // Drawer is becoming visible
                    gameView.togglePause(true)
                } else {
                    // Drawer is completely closed
                    gameView.togglePause(false)
                }
            }

            // Unused methods
            override fun onDrawerOpened(drawerView: View) {}
            override fun onDrawerClosed(drawerView: View) {}
            override fun onDrawerStateChanged(newState: Int) {}
        })

        toggle.syncState()

        // Show the hamburger icon in the ActionBar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Handle the navigation item clicks
        binding.navigationView.setNavigationItemSelectedListener(NavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navItem_pauseMenu_resume -> binding.drawerLayoutGame.closeDrawer(GravityCompat.START)
                R.id.navItem_pauseMenu_toggleSound -> soundSwitch.toggle()
                R.id.navItem_pauseMenu_quit -> onQuit()
                else -> return@OnNavigationItemSelectedListener false
            }
            true
        })
    }


    fun toggleGameOverFragmentVisibility(isVisible: Boolean) {

        val gameOverFragmentContainer = binding.fragmentContainerViewGameOver

        val button = binding.buttonPause

        gameOverFragmentContainer.getFragment<GameOverFragment>().apply {
            updateScores(gameView.score, highScore)
        }

        if (isVisible) {
            gameOverFragmentContainer.visibility = View.VISIBLE
            button.visibility = View.GONE
        } else {
            gameOverFragmentContainer.visibility = View.GONE
            button.visibility = View.VISIBLE
        }
    }

    // Make sure the drawer opens and closes with the hamburger icon
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id: Int = item.itemId
        if (id == android.R.id.home) {
            if (binding.drawerLayoutGame.isDrawerOpen(GravityCompat.START)) {
                binding.drawerLayoutGame.closeDrawer(GravityCompat.START)
            } else {
                binding.drawerLayoutGame.openDrawer(GravityCompat.START)
            }
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    // Handle the back button press
    override fun onBackPressed() {
        if (binding.drawerLayoutGame.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayoutGame.closeDrawer(GravityCompat.START)
        } else {
            binding.drawerLayoutGame.openDrawer(GravityCompat.START)
        }
    }

    fun handlePause(v: View) {
        if (binding.drawerLayoutGame.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayoutGame.closeDrawer(GravityCompat.START)
        } else {
            binding.drawerLayoutGame.openDrawer(GravityCompat.START)
        }
    }

    private fun sendToggleMusicBroadcast(musicOn: Boolean) {
        val intent = Intent("com.example.stopsong.STOP_MUSIC")
        intent.putExtra("music_on", musicOn)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }

    override fun onDestroy() {
        super.onDestroy()

        // Stop the game loop when the activity is destroyed
        handler.removeCallbacks(runnable)
    }

    override fun onRestart() {
        super.onRestart()
        toggleGameOverFragmentVisibility(false)
        gameView.restartGame()
    }

    override fun onQuit() {
        // handle returning high score to main activity
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("high_score", highScore.toString())
        setResult(Activity.RESULT_OK, intent)
        finish()
    }
}