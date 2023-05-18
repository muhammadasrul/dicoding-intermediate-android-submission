package com.acun.storyapp.ui.screen.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.acun.storyapp.R
import com.acun.storyapp.data.remote.Resource
import com.acun.storyapp.databinding.FragmentLoginViewBinding
import com.acun.storyapp.ui.customview.AppEditText
import com.acun.storyapp.utils.AppDataStore
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginView : Fragment() {

    private var _binding: FragmentLoginViewBinding? = null
    private val binding get() = _binding!!

    private val viewModel: LoginViewModel by viewModels()
    private val dataStore: AppDataStore by lazy {
        AppDataStore(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginViewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loginCheck()
        initView()
        observeFormValidation()
        observeLoginResult()
    }

    private fun initView() {
        binding.edLoginEmail.run {
            setType(AppEditText.Companion.Type.EMAIL)
            setHint(resources.getString(R.string.email))
            setListener(object : AppEditText.TextChangeListener {
                override fun onTextChangeListener(isError: Boolean) {
                    viewModel.setIsError(isError)
                }
            })
        }
        binding.edLoginPassword.run {
            setType(AppEditText.Companion.Type.PASSWORD)
            setHint(resources.getString(R.string.password))
            setListener(object : AppEditText.TextChangeListener {
                override fun onTextChangeListener(isError: Boolean) {
                    viewModel.setIsError(isError)
                }
            })
        }

        binding.loginButton.run {
            setText(resources.getString(R.string.login))
            setOnClickListener {
                viewModel.login(
                    email = binding.edLoginEmail.getText(),
                    password = binding.edLoginPassword.getText()
                )
            }
        }

        binding.register.setOnClickListener {
            findNavController().navigate(LoginViewDirections.actionLoginViewToRegisterView())
        }
    }

    private fun loginCheck() {
        lifecycleScope.launch {
            dataStore.userToken.collect {
                if (it.isEmpty()) return@collect
                findNavController().navigate(LoginViewDirections.actionLoginViewToStoryView())
            }
        }
    }

    private fun observeFormValidation() {
        viewModel.isError.observe(viewLifecycleOwner) { isError ->
            binding.loginButton.setEnable(!isError)
        }
    }

    private fun observeLoginResult() {
        viewModel.loginResult.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Error -> {
                    binding.loginButton.isLoading(false)
                    Toast.makeText(requireContext(), resource.message, Toast.LENGTH_SHORT).show()
                }
                is Resource.Loading -> binding.loginButton.isLoading(true)
                is Resource.Success -> {
                    binding.loginButton.isLoading(false)
                    resource.data?.let {
                        lifecycleScope.launch {
                            dataStore.setUserToken(it.token)
                        }
                    }
                }
            }
        }
    }
}