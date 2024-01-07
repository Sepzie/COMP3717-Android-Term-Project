package com.example.comp3717_project

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.comp3717_project.database.FirebaseRepository
import com.example.comp3717_project.database.FirestoreViewModel
import com.example.comp3717_project.databinding.FragmentMainMenuBinding
import com.example.comp3717_project.game.GameActivity

/**
 * A simple [Fragment] subclass.
 */
class MainMenuFragment : Fragment() {

    private val firestoreViewModel: FirestoreViewModel by activityViewModels()
    private val repository: FirebaseRepository = FirebaseRepository()
    private var _binding: FragmentMainMenuBinding? = null
    private val binding get() = _binding!!

    private val myActivityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            val value = data?.getStringExtra("high_score")
            if (firestoreViewModel.getCurrentUser() != null){
                val user = firestoreViewModel.getCurrentUser()
                if (value != null && user?.getHighScore()!! < value.toInt()) {
                    user.let {
                        repository.updateUserHighScore(it.getName(), value.toInt())
                        user.setHighScore(value.toInt())
                    }
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainMenuBinding.inflate(inflater, container, false)

        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonMainMenuLeaderboard.setOnClickListener{
            findNavController().navigate(R.id.action_mainMenuFragment_to_leaderBoardFragment)
        }

        binding.buttonMainMenuLogOut.setOnClickListener{
            firestoreViewModel.logOut()
            findNavController().navigate(R.id.action_mainMenuFragment_to_landingFragment)
        }

        binding.buttonMainMenuPlay.setOnClickListener {
            val intent = Intent(context, GameActivity::class.java)
            intent.putExtra("high_score", firestoreViewModel.getCurrentUser()?.getHighScore())
            myActivityResultLauncher.launch(intent)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}