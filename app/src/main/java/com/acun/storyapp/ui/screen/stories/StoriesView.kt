package com.acun.storyapp.ui.screen.stories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.transition.TransitionInflater
import com.acun.storyapp.R
import com.acun.storyapp.data.remote.Resource
import com.acun.storyapp.data.remote.response.StoriesResponse
import com.acun.storyapp.databinding.FragmentStoryViewBinding
import com.acun.storyapp.utils.AppDataStore
import com.acun.storyapp.utils.toGone
import com.acun.storyapp.utils.toVisible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class StoriesView : Fragment() {

    private val binding: FragmentStoryViewBinding by lazy {
        FragmentStoryViewBinding.inflate(layoutInflater)
    }

    private val viewModel: StoriesViewModel by viewModels()
    private val storiesAdapter = StoriesAdapter(object : StoriesAdapter.OnItemClickListener {
        override fun onItemClicked(item: StoriesResponse.Story, imageView: ImageView) {
            val extras = FragmentNavigatorExtras(imageView to item.photoUrl)
            findNavController().navigate(
                StoriesViewDirections.actionStoryViewToDetailStoryView(item),
                extras
            )
        }
    })
    private val dataStore by lazy {
        AppDataStore(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        lifecycleScope.launch {
            dataStore.userToken.collect {
                viewModel.getStories(token = it, page = 1, size = 100)
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        observeStories()
    }

    private fun initView() {
        binding.recyclerViewStory.run {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = storiesAdapter
        }
        binding.addStoryButton.setOnClickListener {
            findNavController().navigate(StoriesViewDirections.actionStoryViewToAddStoryView(null))
        }
    }

    private fun observeStories() {
        viewModel.storyState.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Error -> {
                    binding.errMessageTextView.text = resource.message
                    binding.errMessageContainer.toVisible()
                    binding.recyclerViewStory.toGone()
                    binding.progressBar.toGone()
                }

                is Resource.Loading -> {
                    binding.progressBar.toVisible()
                    binding.errMessageContainer.toGone()
                    binding.recyclerViewStory.toGone()
                }

                is Resource.Success -> {
                    binding.progressBar.toGone()
                    binding.errMessageContainer.toGone()
                    binding.recyclerViewStory.toVisible()
                    storiesAdapter.submitList(resource.data.orEmpty())
                }
            }
        }
    }
}