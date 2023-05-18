package com.acun.storyapp.ui.screen.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.acun.storyapp.R
import com.acun.storyapp.data.remote.Resource
import com.acun.storyapp.databinding.FragmentRegisterViewBinding
import com.acun.storyapp.ui.customview.AppEditText
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterView : Fragment() {

    private var _binding: FragmentRegisterViewBinding? = null
    private val binding get() = _binding!!

    private val viewModel: RegisterViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterViewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        observeFormValidation()
        observeRegisterResult()
    }

    private fun initView() {
        with(binding) {
            edRegisterName.run {
                setType(AppEditText.Companion.Type.USER)
                setHint(resources.getString(R.string.name))
                setListener(object : AppEditText.TextChangeListener {
                    override fun onTextChangeListener(isError: Boolean) {
                        viewModel.setIsError(isError)
                    }
                })
            }
            edRegisterEmail.run {
                setType(AppEditText.Companion.Type.EMAIL)
                setHint(resources.getString(R.string.email))
                setListener(object : AppEditText.TextChangeListener {
                    override fun onTextChangeListener(isError: Boolean) {
                        viewModel.setIsError(isError)
                    }
                })
            }
            edRegisterPassword.run {
                setType(AppEditText.Companion.Type.PASSWORD)
                setHint(resources.getString(R.string.password))
                setListener(object : AppEditText.TextChangeListener {
                    override fun onTextChangeListener(isError: Boolean) {
                        viewModel.setIsError(isError)
                    }
                })
            }

            registerButton.run {
                setText(resources.getString(R.string.register))
                setOnClickListener {
                    viewModel.register(
                        name = edRegisterName.getText(),
                        email = edRegisterEmail.getText(),
                        password = edRegisterPassword.getText()
                    )
                }
            }
        }
    }

    private fun observeFormValidation() {
        viewModel.isError.observe(viewLifecycleOwner) { isError ->
            binding.registerButton.setEnable(!isError)
        }
    }

    private fun observeRegisterResult() {
        viewModel.registerResult.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Error -> {
                    binding.registerButton.isLoading(false)
                    Toast.makeText(requireContext(), resource.message, Toast.LENGTH_SHORT).show()
                }

                is Resource.Loading -> binding.registerButton.isLoading(true)
                is Resource.Success -> {
                    binding.registerButton.isLoading(false)
                    Toast.makeText(requireContext(), resource.data?.message, Toast.LENGTH_SHORT)
                        .show()
                    findNavController().popBackStack()
                }
            }
        }
    }
}