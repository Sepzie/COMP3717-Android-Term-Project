package com.example.comp3717_project

//import com.example.comp3717_project.databinding.ActivityMainBinding
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.comp3717_project.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private val stopMusicBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val musicOn = intent?.getBooleanExtra("music_on", true) ?: true
            toggleMusic(musicOn)
         }
    }

    private var mediaPlayer: MediaPlayer? = null
    private lateinit var binding: ActivityMainBinding

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        val navHost = supportFragmentManager.findFragmentById(R.id.fragmentContainerView_main) as NavHostFragment
        navController = navHost.navController

        val appBarConfiguration = AppBarConfiguration(navController.graph)

        setupActionBarWithNavController(navController, appBarConfiguration)

        mediaPlayer = MediaPlayer.create(this, R.raw.background_music)
        mediaPlayer?.isLooping = true // Set looping to true
        mediaPlayer?.start()

        // Set OnCompletionListener to restart the music when it finishes playing
        mediaPlayer?.setOnCompletionListener {
            mediaPlayer?.seekTo(0) // Seek to beginning of music
            mediaPlayer?.start() // Start playing again
        }

        LocalBroadcastManager.getInstance(this).registerReceiver(
            stopMusicBroadcastReceiver, IntentFilter("com.example.stopsong.STOP_MUSIC")
        )

    }

    fun toggleMusic(musicOn: Boolean) {
        if (musicOn) {
            mediaPlayer?.start()
        } else {
            mediaPlayer?.pause()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("line", "Destroyed")
        // Release the MediaPlayer object when the activity is destroyed
        mediaPlayer?.release()
        mediaPlayer = null
    }



}