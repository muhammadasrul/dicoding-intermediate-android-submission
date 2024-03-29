package com.acun.storyapp.ui.screen.maps

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.acun.storyapp.R
import com.acun.storyapp.data.remote.Resource
import com.acun.storyapp.data.remote.response.StoriesResponse
import com.acun.storyapp.databinding.FragmentMapsViewBinding
import com.acun.storyapp.utils.AppDataStore
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MapsView : Fragment() {

    private var _binding: FragmentMapsViewBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MapsViewModel by viewModels()
    private val dataStore by lazy {
        AppDataStore(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapsViewBinding.inflate(inflater, container, false)
        lifecycleScope.launch {
            dataStore.userToken.collect {
                viewModel.getStories(it)
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeStories()
    }

    private fun observeStories() {
        viewModel.storyState.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Success -> setupMaps(resource.data.orEmpty())
                else -> Unit
            }
        }
    }

    private fun setupMaps(data: List<StoriesResponse.Story>) {
        val callback = OnMapReadyCallback { googleMap ->
            data.forEach { story ->
                googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                        requireContext(),
                        R.raw.maps_style
                    )
                )
                googleMap.addMarker(
                    MarkerOptions()
                        .position(LatLng(story.lat, story.lon))
                        .title(story.name)
                        .snippet(story.description)
                )
            }
            googleMap.moveCamera(
                CameraUpdateFactory
                    .newLatLngZoom(LatLng(-7.593439, 110.599065), 5f)
            )
        }

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }
}