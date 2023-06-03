package com.acun.storyapp.ui.screen.stories.add_story

import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.net.toFile
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.acun.storyapp.R
import com.acun.storyapp.data.remote.Resource
import com.acun.storyapp.databinding.FragmentAddStoryViewBinding
import com.acun.storyapp.ui.customview.AppUploadCard
import com.acun.storyapp.utils.AppDataStore
import com.acun.storyapp.utils.getFileName
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import id.zelory.compressor.Compressor
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

@AndroidEntryPoint
class AddStoryView : Fragment(), UploadRequestBody.UploadCallback {

    private var _binding: FragmentAddStoryViewBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AddStoryViewModel by viewModels()
    private val dataStore: AppDataStore by lazy {
        AppDataStore(requireContext())
    }

    private lateinit var cameraPermissionLauncher: ActivityResultLauncher<String>
    private lateinit var locationPermissionLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var galleryResultLauncher: ActivityResultLauncher<String>

    private lateinit var fusedLocation: FusedLocationProviderClient
    private var lat: Double = 0.0
    private var long: Double = 0.0

    private var file: File? = null
    private var fileName = ""
    private var token = ""

    private val navArgs by navArgs<AddStoryViewArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddStoryViewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cameraPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                if (isGranted) findNavController().navigate(AddStoryViewDirections.actionAddStoryViewToCameraXView())
            }
        locationPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
                when {
                    permissions[android.Manifest.permission.ACCESS_COARSE_LOCATION] == true
                            && permissions[android.Manifest.permission.ACCESS_FINE_LOCATION] == true -> getCurrentLocation()

                    else -> Unit
                }
            }
        galleryResultLauncher =
            registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
                uri?.let { viewModel.setFile(it) }
            }
        fusedLocation = LocationServices.getFusedLocationProviderClient(requireContext())

        navArgs.uri?.let {
            viewModel.setFile(Uri.parse(it))
        }

        initView()
        lifecycleScope.launch {
            dataStore.userToken.collect { token = it }
        }
        viewModel.file.observe(viewLifecycleOwner) { uri ->

            if (uri.lastPathSegment?.substringAfter(".") == "jpeg") {
                uri.lastPathSegment?.let {
                    fileName = it
                }
                lifecycleScope.launch {
                    file = Compressor.compress(requireContext(), uri.toFile())
                }
            } else {
                fileName = requireActivity().contentResolver.getFileName(uri)
                val parcelFileDescriptor =
                    requireActivity().contentResolver?.openFileDescriptor(uri, "r", null)
                val inputStream = FileInputStream(parcelFileDescriptor?.fileDescriptor)

                file = File(requireActivity().cacheDir, fileName)

                val outputStream = FileOutputStream(file)
                inputStream.copyTo(outputStream)

                parcelFileDescriptor?.close()
            }

            binding.uploadCard.setValue(uri.toString(), fileName)
        }

        viewModel.uploadState.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Error -> {
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    binding.buttonAdd.isLoading(false)
                }

                is Resource.Loading -> Unit
                is Resource.Success -> findNavController().navigate(AddStoryViewDirections.actionAddStoryViewToStoryView())
            }
        }
    }

    private fun initView() {
        binding.buttonAdd.run {
            setText(getString(R.string.upload))
            setOnClickListener {
                uploadFile()
            }
        }
        binding.uploadCard.setListener(object : AppUploadCard.OnCardButtonClickListener {
            override fun onCameraButtonClicked() {
                when {
                    ContextCompat.checkSelfPermission(
                        requireContext(),
                        android.Manifest.permission.CAMERA
                    ) == PERMISSION_GRANTED -> {
                        findNavController().navigate(AddStoryViewDirections.actionAddStoryViewToCameraXView())
                    }

                    shouldShowRequestPermissionRationale(android.Manifest.permission.CAMERA) -> {
                        Snackbar.make(binding.root, getString(R.string.permission_needed), 10000).setAction(
                            "Ok"
                        ) { cameraPermissionLauncher.launch(android.Manifest.permission.CAMERA) }
                            .show()
                    }

                    else -> {
                        cameraPermissionLauncher.launch(android.Manifest.permission.CAMERA)
                    }
                }
            }

            override fun onGalleryButtonClicked() {
                galleryResultLauncher.launch("image/*")
            }
        })

        binding.locationSwitch.setOnCheckedChangeListener { compoundButton, boolean ->
            getCurrentLocation()
        }
    }

    private fun uploadFile() {
        if (file != null) {
            val textType = "text/plain".toMediaTypeOrNull()
            val desc = binding.edAddDescription.text.toString().toRequestBody(textType)
            val lat = this.lat.toString().toRequestBody(textType)
            val long = this.long.toString().toRequestBody(textType)
            val imageFile = file as File
            val requestBody = UploadRequestBody(imageFile, "image", this)
            val imageMultipart = MultipartBody.Part.createFormData("photo", fileName, requestBody)

            binding.buttonAdd.isLoading(true)
            viewModel.uploadFile(
                token = token,
                file = imageMultipart,
                description = desc,
                lat = lat,
                long = long
            )
        } else {
            Toast.makeText(
                requireContext(),
                getString(R.string.choose_a_file_first),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onProgressUpdate(percentage: Int) {
        binding.uploadCard.setUploadProgress(percentage)
        Log.d("progress", percentage.toString())
    }

    private fun getCurrentLocation() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            )
                    == PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(
                        requireContext(),
                        android.Manifest.permission.ACCESS_FINE_LOCATION
                    )
                    == PERMISSION_GRANTED -> {
                fusedLocation.lastLocation.addOnSuccessListener {
                    lat = it.latitude
                    long = it.longitude
                }
            }

            shouldShowRequestPermissionRationale(android.Manifest.permission.ACCESS_COARSE_LOCATION) -> {
                Snackbar.make(binding.root, getString(R.string.permission_needed), 10000).setAction(
                    "Ok"
                ) {
                    locationPermissionLauncher.launch(
                        arrayOf(
                            android.Manifest.permission.ACCESS_FINE_LOCATION,
                            android.Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    )
                }.show()
            }

            else -> {
                locationPermissionLauncher.launch(
                    arrayOf(
                        android.Manifest.permission.ACCESS_FINE_LOCATION,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
        }
    }
}