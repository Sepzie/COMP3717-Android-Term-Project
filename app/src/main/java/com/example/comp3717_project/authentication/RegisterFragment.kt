package com.example.comp3717_project.authentication

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.comp3717_project.database.FirestoreViewModel
import com.example.comp3717_project.R
import com.example.comp3717_project.database.FirebaseRepository
import com.example.comp3717_project.database.User
import com.example.comp3717_project.databinding.FragmentRegisterBinding

/**
 * A simple [Fragment] subclass.
 */
class RegisterFragment : Fragment() {

    private val firestoreViewModel: FirestoreViewModel by activityViewModels()
    private val repository: FirebaseRepository = FirebaseRepository()
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)

        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val feedbackTextView = view.findViewById<TextView>(R.id.textView_register_feedback)

        binding.buttonRegisterCreateAccount.setOnClickListener {
            val name = binding.editTextRegisterUsername.text.toString()
            val password = binding.editTextRegisterPassword.text.toString()
            val confirmPassword = binding.editTextRegisterConfirmPassword.text.toString()

            if (checkInputAndDisplayError(name, R.string.all_emptyUserName, binding.textViewRegisterFeedback) ||
                checkInputAndDisplayError(password, R.string.all_emptyPassword, binding.textViewRegisterFeedback) ||
                checkInputAndDisplayError(confirmPassword, R.string.register_emptyConfirmPassword, binding.textViewRegisterFeedback) ||
                (password != confirmPassword).also { if (it) checkInputAndDisplayError("", R.string.register_feedback, binding.textViewRegisterFeedback) }) {
                return@setOnClickListener
            }

            repository.findUser(name, password) { userFound ->
                if (userFound) {
                    feedbackTextView.visibility = View.VISIBLE
                    feedbackTextView.setText(R.string.register_exists)
                    Log.d("RegisterFragment", "User already exists")
                } else {
                    feedbackTextView.visibility = View.INVISIBLE
                    Log.d("RegisterFragment", "User does not exist")
                    addUser(name, password)
                }
            }


        }

        binding.buttonRegisterBack.setOnClickListener{
            findNavController().navigate(R.id.action_registerFragment_to_landingFragment)
        }

    }

    private fun addUser(name: String, password: String) {
        repository.addUser(name, password) { userAdded ->
            if (userAdded) {
                val newUser = User(name, password)
                newUser.setHighScore(0)
                firestoreViewModel.setCurrentUser(newUser)
                findNavController().navigate(R.id.action_registerFragment_to_mainMenuFragment)
            }
            else {
                binding.textViewRegisterFeedback.visibility = View.VISIBLE
                binding.textViewRegisterFeedback.setText(R.string.register_feedback)
                Log.d("RegisterFragment", "User not added")
            }
        }
    }

    private fun checkInputAndDisplayError(input: String, errorMessage: Int, textView: TextView): Boolean {
        if (input.isBlank()) {
            binding.spaceRegisterFeedback.visibility = View.VISIBLE
            textView.visibility = View.VISIBLE
            textView.setText(errorMessage)
            return true
        }
        return false
    }
}