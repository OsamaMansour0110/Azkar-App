package com.learining.AzkarApp.UI.BotNav.Admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.learining.AzkarApp.R
import com.learining.AzkarApp.databinding.FragmentAdminLoginBinding

class AdminLoginFragment : Fragment() {

    private var _binding: FragmentAdminLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdminLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnLogin.setOnClickListener {
            val password = binding.etPassword.text.toString()
            if (password == "123456") {
                findNavController().navigate(R.id.action_adminLoginFragment_to_adminAddFieldFragment)
            } else {
                binding.tilPassword.error = "Incorrect Password"
                Toast.makeText(requireContext(), "Access Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
