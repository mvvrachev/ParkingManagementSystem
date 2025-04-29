package com.example.parkingsystem.ui.register

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.parkingsystem.MainActivity
import com.example.parkingsystem.R
import com.example.parkingsystem.base.BaseFragment
import com.example.parkingsystem.databinding.FragmentRegisterBinding
import com.example.parkingsystem.utils.getSupportActionBar
import com.example.parkingsystem.utils.viewBinding

class RegisterFragment : BaseFragment(R.layout.fragment_register) {

    private val binding : FragmentRegisterBinding by viewBinding(FragmentRegisterBinding::bind)

    private lateinit var viewModel: RegisterViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getSupportActionBar().hide()

        viewModel = ViewModelProvider(this)[RegisterViewModel::class.java]

        viewModel.viewState.observe(viewLifecycleOwner) {
            loaderVisible(it.isLoading)

            if(it.successRegister) {
               val intent = Intent(requireContext(), MainActivity::class.java)
                startActivity(intent)
                requireNotNull(activity).finish();
            }

            if(!it.successRegister && !it.isLoading) {
                showError(it.error)
            }
        }

        with(binding) {
            registerButton.setOnClickListener {
                val username = usernameEditTextRegister.text.toString()
                val email = emailEditTextRegister.text.toString()
                val carNumber = carNumber.text.toString()
                val password = passwordEditTextRegister.text.toString()
                val confirmPassword = confirmPasswordEditTextRegister.text.toString()
                viewModel.doRegister(username, email, carNumber, password, confirmPassword)
            }
        }
    }
}