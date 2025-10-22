package com.example.lab4

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.lab4.databinding.FragmentSignInBinding

class SignInFragment : Fragment() {

    private var _binding: FragmentSignInBinding? = null
    private val binding get() = _binding!!

    private val args: SignInFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignInBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        args.user?.let {
            binding.emailEditText.setText(it.email)
            binding.passwordEditText.setText(it.password)
            Toast.makeText(requireContext(), "Привет, ${it.name}! Данные подставлены.", Toast.LENGTH_SHORT).show()
        }

        binding.signInButton.setOnClickListener {
            val email = binding.emailEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString()

            if (email.isEmpty() || password.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(requireContext(), "Пожалуйста, введите корректные данные", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val userToLogin = args.user
            if (userToLogin != null && email == userToLogin.email && password == userToLogin.password) {
                val action = SignInFragmentDirections.actionSignInFragmentToHomeFragment(userToLogin)
                findNavController().navigate(action)
            } else {
                 // If there is no registered user, or the details are incorrect, we create a new user object for the home screen.
                val action = SignInFragmentDirections.actionSignInFragmentToHomeFragment(User(email, email, password))
                findNavController().navigate(action)
            }
        }

        binding.signUpText.setOnClickListener {
            findNavController().navigate(R.id.action_signInFragment_to_signUpFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
