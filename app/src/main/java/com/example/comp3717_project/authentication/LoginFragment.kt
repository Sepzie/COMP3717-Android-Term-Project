package com.example.comp3717_project.authentication

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.comp3717_project.database.FirestoreViewModel
import com.example.comp3717_project.database.INFO
import com.example.comp3717_project.database.User
import com.example.comp3717_project.R
import com.example.comp3717_project.database.FirebaseRepository
import com.example.comp3717_project.databinding.FragmentLoginBinding
import com.google.firebase.firestore.ktx.getField

/**
 * A simple [Fragment] subclass.
 */

class LoginFragment : Fragment() {

    private val firestoreViewModel: FirestoreViewModel by activityViewModels()
    private val repository: FirebaseRepository = FirebaseRepository()
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)

        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonLoginLoginButton.setOnClickListener {
            val name = binding.editTextLoginUsername.text.toString()
            val password = binding.editTextLoginPassword.text.toString()
            if (name.isBlank()) {
                setFeedbackError(getString(R.string.all_emptyUserName))
                Log.i(INFO, "Username is blank")
                return@setOnClickListener
            }
            if (password.isBlank()) {
                setFeedbackError(getString(R.string.all_emptyPassword))
                return@setOnClickListener
            }
            repository.findUser(name, password) { userExists ->
                if (userExists) {
                    repository.getUserInfo(name){
                        val highscore = it.getField<Int>("highScore")
                        val user = User(name, password)

                        if (highscore != null)
                            user.setHighScore(highscore)
                        else
                            user.setHighScore(0)

                        firestoreViewModel.setCurrentUser(user)
                        findNavController().navigate(R.id.action_loginFragment_to_mainMenuFragment)
                    }
                }
                else {
                    binding.textViewLoginFeedback.visibility = View.VISIBLE
                    binding.textViewLoginFeedback.setText(R.string.login_feedback)
                    Log.i(INFO, "User does not exist")
                }
                Log.i(INFO, "userExists = $userExists")
            }
        }

        binding.buttonLoginBack.setOnClickListener{
            findNavController().navigate(R.id.action_loginFragment_to_landingFragment)
        }

    }

    private fun setFeedbackError(message: String) {
        binding.spaceLoginFeedback.visibility = View.VISIBLE
        binding.textViewLoginFeedback.visibility = View.VISIBLE
        binding.textViewLoginFeedback.text = message
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}