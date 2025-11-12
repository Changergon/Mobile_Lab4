package com.example.lab4

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.lab4.databinding.FragmentSignUpBinding

class SignUpFragment : Fragment() {

    private var _binding: FragmentSignUpBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentSignUpBinding.inflate(inflater, container, false)
        _binding = binding
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding?.let { binding ->
            binding.signUpButton.setOnClickListener {
                if (!validateInput()) {
                    return@setOnClickListener
                }

                val name = binding.nameEditText.text.toString().trim()
                val email = binding.emailEditText.text.toString().trim()
                val password = binding.passwordEditText.text.toString()

                val user = User(name, email, password) // Age and gender are not part of the User object, so they are omitted.

                val action = SignUpFragmentDirections.actionSignUpFragmentToSignInFragment(user)
                findNavController().navigate(action)
            }

            binding.signInText.setOnClickListener {
                // When just clicking the text, we navigate without sending a user
                val action = SignUpFragmentDirections.actionSignUpFragmentToSignInFragment(null)
                findNavController().navigate(action)
            }
        }
    }

    private fun validateInput(): Boolean {
        // Since this is a private function and is only called from onViewCreated (within a let block), 
        // it is safe to use the !! operator here. However, for consistency and absolute safety, 
        // we can also use a safe call here.
        _binding?.let { binding ->
            val name = binding.nameEditText.text.toString().trim()
            val email = binding.emailEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString()
            val ageString = binding.ageEditText.text.toString().trim()
            val selectedGenderId = binding.genderRadioGroup.checkedRadioButtonId

            if (name.isEmpty() || email.isEmpty() || password.isEmpty() || ageString.isEmpty()) {
                Toast.makeText(requireContext(), "Все поля, включая возраст, должны быть заполнены", Toast.LENGTH_SHORT).show()
                return false
            }
            if (selectedGenderId == -1) {
                Toast.makeText(requireContext(), "Пожалуйста, выберите пол", Toast.LENGTH_SHORT).show()
                return false
            }
            val age = ageString.toIntOrNull()
            if (age == null || age <= 0) {
                Toast.makeText(requireContext(), "Пожалуйста, введите корректный возраст", Toast.LENGTH_SHORT).show()
                return false
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(requireContext(), "Неверный формат электронной почты", Toast.LENGTH_SHORT).show()
                return false
            }
            if (password.length < 6) {
                Toast.makeText(requireContext(), "Пароль должен содержать не менее 6 символов", Toast.LENGTH_SHORT).show()
                return false
            }
            return true
        }
        // If _binding is null for some reason, we can't validate, so we return false.
        return false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
