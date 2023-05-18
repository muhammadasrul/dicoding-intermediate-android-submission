package com.acun.storyapp.ui.screen.stories.detail_story

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.transition.TransitionInflater
import coil.load
import com.acun.storyapp.R
import com.acun.storyapp.data.remote.response.StoriesResponse
import com.acun.storyapp.databinding.FragmentDetailViewBinding
import com.acun.storyapp.utils.setFormattedDate
import com.acun.storyapp.utils.setLocation
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailStoryView : Fragment() {

    val binding: FragmentDetailViewBinding by lazy {
        FragmentDetailViewBinding.inflate(layoutInflater)
    }

    private val navArgs by navArgs<DetailStoryViewArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = binding.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedElementEnterTransition = TransitionInflater.from(requireContext()).inflateTransition(R.transition.shared_element_transition)

        initDetail(navArgs.story)
    }

    private fun initDetail(story: StoriesResponse.Story) {
        binding.apply {
            ivDetailPhoto.apply {
                transitionName = story.photoUrl
                load(story.photoUrl)
            }
            tvDetailDate.setFormattedDate(story.createdAt)
            tvDetailName.text = story.name
            tvDetailDescription.text = story.description
            tvDetailLocation.setLocation(story.lat, story.lon)
        }
    }
}