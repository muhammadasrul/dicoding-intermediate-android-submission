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
import com.acun.storyapp.data.local.entity.StoryEntity
import com.acun.storyapp.data.local.entity.toResponse
import com.acun.storyapp.databinding.FragmentStoryViewBinding
import com.acun.storyapp.utils.AppDataStore
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
        override fun onItemClicked(item: StoryEntity, imageView: ImageView) {
            val extras = FragmentNavigatorExtras(imageView to item.photoUrl)
            findNavController().navigate(
                StoriesViewDirections.actionStoryViewToDetailStoryView(item.toResponse()),
                extras
            )
        }
    })
    private val dataStore by lazy {
        AppDataStore(requireContext())
    }

    private var token: String?  = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        lifecycleScope.launch {
            dataStore.userToken.collect { token = it }
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
//            findNavController().navigate(StoriesViewDirections.actionStoryViewToAddStoryView(null))
            findNavController().navigate(StoriesViewDirections.actionStoryViewToMapsView())
        }
    }

    private fun observeStories() {
        token?.let {
            viewModel
                .getStories(token = it)
                .observe(viewLifecycleOwner) { data ->
                    binding.recyclerViewStory.toVisible()
                    storiesAdapter.submitData(lifecycle, data)
                }
        }
    }
}