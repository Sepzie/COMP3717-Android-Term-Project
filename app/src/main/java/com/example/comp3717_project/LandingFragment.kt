package com.example.comp3717_project

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.comp3717_project.databinding.FragmentLandingBinding

/**
 * A simple [Fragment] subclass.
 */
class LandingFragment : Fragment() {

    private var _binding: FragmentLandingBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentLandingBinding.inflate(inflater, container, false)

        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonLandingLogin.setOnClickListener{
            findNavController().navigate(R.id.action_landingFragment_to_loginFragment)
        }

        binding.buttonLandingRegister.setOnClickListener{
            findNavController().navigate(R.id.action_landingFragment_to_registerFragment)
        }

        binding.buttonLandingGuest.setOnClickListener{
            findNavController().navigate(R.id.action_landingFragment_to_mainMenuFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}