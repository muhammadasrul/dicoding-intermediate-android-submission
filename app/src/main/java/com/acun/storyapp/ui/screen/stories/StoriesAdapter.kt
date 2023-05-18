package com.acun.storyapp.ui.screen.stories

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.acun.storyapp.data.remote.response.StoriesResponse.Story
import com.acun.storyapp.databinding.AppStoryCardLayoutBinding
import com.acun.storyapp.utils.setFormattedDate
import com.acun.storyapp.utils.setLocation

class StoriesAdapter(
    private val onClickListener: OnItemClickListener
): ListAdapter<Story, StoriesAdapter.StoryViewHolder>(DiffUtilCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val binding = AppStoryCardLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    override fun getItemCount(): Int = currentList.size

    inner class StoryViewHolder(private val binding: AppStoryCardLayoutBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Story) {
            with(binding) {
                ivItemPhoto.apply {
                    transitionName = item.photoUrl
                    load(item.photoUrl)
                }
                tvItemName.text = item.name
                tvItemDescription.text = item.description
                tvItemLocation.setLocation(item.lat, item.lon)
                tvItemDate.setFormattedDate(item.createdAt)
                root.setOnClickListener {
                    onClickListener.onItemClicked(item, ivItemPhoto)
                }
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClicked(item: Story, imageView: ImageView)
    }

    class DiffUtilCallback: DiffUtil.ItemCallback<Story>() {
        override fun areItemsTheSame(oldItem: Story, newItem: Story): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Story, newItem: Story): Boolean {
            return oldItem == newItem
        }
    }
}