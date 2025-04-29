package com.example.parkingsystem.ui.login

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat.getSystemService
import androidx.navigation.fragment.findNavController
import com.example.parkingsystem.MainActivity
import com.example.parkingsystem.R
import com.example.parkingsystem.databinding.FragmentLoginBinding
import com.example.parkingsystem.utils.viewBinding
import com.example.parkingsystem.base.BaseFragment
import com.example.parkingsystem.utils.getSupportActionBar

class LoginFragment : BaseFragment(R.layout.fragment_login) {

    private val binding : FragmentLoginBinding by viewBinding(FragmentLoginBinding::bind)

    private lateinit var viewModel: LoginViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getSupportActionBar().hide()

        viewModel = ViewModelProvider(this)[LoginViewModel::class.java]

        viewModel.viewState.observe(viewLifecycleOwner) {
            loaderVisible(it.isLoading)

            if (it.successLogin) {
                val intent = Intent(requireContext(), MainActivity::class.java)
                startActivity(intent)
                requireNotNull(activity).finish();
            }

            if(!it.successLogin && !it.isLoading) {
                showError(it.error)
            }
        }

        with(binding) {
            registerButtonLoginPage.setOnClickListener {
                findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
            }
            loginButton.setOnClickListener {
                val email = binding.emailEditTextLogin.text.toString()
                val password = binding.passwordEditTextLogin.text.toString()
                viewModel.doLogin(email, password)
            }
        }

    }
}