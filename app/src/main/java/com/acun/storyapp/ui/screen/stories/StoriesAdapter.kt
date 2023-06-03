package com.acun.storyapp.ui.screen.stories

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.acun.storyapp.data.local.entity.StoryEntity
import com.acun.storyapp.databinding.AppStoryCardLayoutBinding
import com.acun.storyapp.utils.setFormattedDate
import com.acun.storyapp.utils.setLocation

class StoriesAdapter(
    private val onClickListener: OnItemClickListener,
): PagingDataAdapter<StoryEntity, StoriesAdapter.StoryViewHolder>(DiffUtilCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val binding = AppStoryCardLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bind(it)
        }
    }

    inner class StoryViewHolder(private val binding: AppStoryCardLayoutBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(item: StoryEntity) {
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
        fun onItemClicked(item: StoryEntity, imageView: ImageView)
    }

    companion object {
        private val DiffUtilCallback = object : DiffUtil.ItemCallback<StoryEntity>() {
            override fun areItemsTheSame(oldItem: StoryEntity, newItem: StoryEntity): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: StoryEntity, newItem: StoryEntity): Boolean {
                return oldItem == newItem
            }
        }
    }
}