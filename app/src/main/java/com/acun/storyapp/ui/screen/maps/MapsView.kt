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
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
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
                is Resource.Error -> {
//                    binding.errMessageTextView.text = resource.message
//                    binding.errMessageContainer.toVisible()
//                    binding.recyclerViewStory.toGone()
//                    binding.progressBar.toGone()
                }

                is Resource.Loading -> {
//                    binding.progressBar.toVisible()
//                    binding.errMessageContainer.toGone()
//                    binding.recyclerViewStory.toGone()
                }

                is Resource.Success -> {
//                    binding.progressBar.toGone()
//                    binding.errMessageContainer.toGone()
//                    binding.recyclerViewStory.toVisible()
//                    storiesAdapter.submitList(resource.data.orEmpty())
                    setupMaps(resource.data.orEmpty())
                }
            }
        }
    }

    private fun setupMaps(data: List<StoriesResponse.Story>) {
        val callback = OnMapReadyCallback { googleMap ->
            data.forEach { story ->
                googleMap.addMarker(
                    MarkerOptions()
                        .position(LatLng(story.lat, story.lon))
                        .title(story.name)
                )
            }
//            googleMap.moveCamera(CameraUpdateFactory.newLatLng(LatLng(story.lat, story.lon)))
        }

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }
}