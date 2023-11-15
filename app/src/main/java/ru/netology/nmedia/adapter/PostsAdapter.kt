package ru.netology.nmedia.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nmedia.BuildConfig
import ru.netology.nmedia.R
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.databinding.CardAdBinding
import ru.netology.nmedia.databinding.CardPostBinding
import ru.netology.nmedia.dto.Ad
import ru.netology.nmedia.dto.DateSeparator
import ru.netology.nmedia.dto.FeedItem
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.imageview.loadImageAttachment
import ru.netology.nmedia.imageview.loadImageAvatar
import ru.netology.nmedia.viewmodel.AuthViewModel
import java.time.OffsetDateTime
import javax.inject.Inject

interface OnInteractionListener {
    fun onLike(post: Post) {}
    fun onEdit(post: Post) {}
    fun onRemove(post: Post) {}
    fun onShare(post: Post) {}
    fun onImageView(url: String) {}
}

class PostsAdapter(
    private val onInteractionListener: OnInteractionListener,
    private val appAuth: AppAuth
) : PagingDataAdapter<FeedItem, RecyclerView.ViewHolder>(PostDiffCallback()) {
    override fun getItemViewType(position: Int): Int =
        when (getItem(position)) {
            is Ad -> R.layout.card_ad
            is Post -> R.layout.card_post
            null -> error("unknown item type")
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        when (viewType) {
            R.layout.card_post -> {
                val binding =
                    CardPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                PostViewHolder(binding, onInteractionListener, appAuth)
            }

            R.layout.card_ad -> {
                val binding =
                    CardAdBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                AdViewHolder(binding)
            }

            else -> error("unknown view type: $viewType")
        }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val previosPosition = position - 1

        val previosPost = if (previosPosition < 0) {
            null
        } else {
            getItem(previosPosition) as? Post
        }

        when (val item = getItem(position)) {
            is Ad -> (holder as? AdViewHolder)?.bind(item)
            is Post -> (holder as? PostViewHolder)?.bind(previosPost, item)
            null -> error("unknown item type")
        }
    }
}

class AdViewHolder(
    private val binding: CardAdBinding,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(ad: Ad) {
        binding.image.loadImageAttachment(url = "${BuildConfig.BASE_URL}/media/${ad.image}")
    }
}

class PostViewHolder @Inject constructor(
    private val binding: CardPostBinding,
    private val onInteractionListener: OnInteractionListener,
    private val appAuth: AppAuth,
) : RecyclerView.ViewHolder(binding.root) {

    private val authViewModel: AuthViewModel = AuthViewModel(appAuth)

    fun bind(previosPost: Post?, post: Post) {
        val separator =
            if (previosPost.notToday() && post.today()) {
                DateSeparator.TODAY
            } else if (previosPost.notYesterday() && post.yesterday()) {
                DateSeparator.YESTERDAY
            } else if (previosPost.notTwoDaysAgo() && post.twoDaysAgo()) {
                DateSeparator.TWO_DAYS_AGO
            } else if (previosPost.notWeekAgo() && post.weekAgo()) {
                DateSeparator.WEEK_AGO
            } else if (previosPost.notTwoWeeksAgo() && post.twoWeeksAgo()) {
                DateSeparator.TWO_WEEKS_AGO
            } else {
                null
            }

        binding.apply {
            root.tag = separator
            author.text = post.author
            published.text = post.published.toString()
            content.text = post.content
            like.isChecked = post.likedByMe
            like.text = "${post.likes}"
            avatar.loadImageAvatar(url = "${BuildConfig.BASE_URL}/avatars/${post.authorAvatar}")

            if (post.attachment != null) {
                imageAttachment.visibility = View.VISIBLE
                imageAttachment.loadImageAttachment(url = "${BuildConfig.BASE_URL}/media/${post.attachment?.url}")

                imageAttachment.setOnClickListener {
                    post.attachment?.let {
                        onInteractionListener.onImageView(it.url)
                    }
                }

            } else {
                imageAttachment.visibility = View.GONE
            }

            menu.isVisible = post.ownedByMe

            menu.setOnClickListener {
                PopupMenu(it.context, it).apply {
                    inflate(R.menu.options_post)
                    setOnMenuItemClickListener { item ->
                        when (item.itemId) {
                            R.id.remove -> {
                                onInteractionListener.onRemove(post)
                                true
                            }

                            R.id.edit -> {
                                onInteractionListener.onEdit(post)
                                true
                            }

                            else -> false
                        }
                    }
                }.show()
            }

            like.setOnClickListener {
                if (authViewModel.authenticated) {
                    onInteractionListener.onLike(post)
                } else {
                    like.isChecked = false
                    Toast.makeText(it.context, "Only registered users can like", Toast.LENGTH_LONG)
                        .show()
                }
            }

            share.setOnClickListener {
                onInteractionListener.onShare(post)
            }
        }
    }
}

private val today = OffsetDateTime.now()
private val yesterday = today.minusDays(1)
private val twoDaysAgo = today.minusDays(2)
private val weekAgo = today.minusWeeks(1)
private val twoWeeksAgo = today.minusWeeks(2)

private fun Post.today(): Boolean =
    today.year == published.year && today.dayOfYear == published.dayOfYear

private fun Post.yesterday(): Boolean =
    yesterday.year == published.year && yesterday.dayOfYear == published.dayOfYear

private fun Post.twoDaysAgo(): Boolean =
    twoDaysAgo.year == published.year && twoDaysAgo.dayOfYear == published.dayOfYear

private fun Post.weekAgo(): Boolean =
    weekAgo.year == published.year && weekAgo.dayOfYear == published.dayOfYear

private fun Post.twoWeeksAgo(): Boolean =
    twoWeeksAgo.year == published.year && twoWeeksAgo.dayOfYear == published.dayOfYear

private fun Post?.notToday(): Boolean = this == null || !today()
private fun Post?.notYesterday(): Boolean = this == null || !yesterday()
private fun Post?.notTwoDaysAgo(): Boolean = this == null || !twoDaysAgo()
private fun Post?.notWeekAgo(): Boolean = this == null || !weekAgo()
private fun Post?.notTwoWeeksAgo(): Boolean = this == null || !twoWeeksAgo()

class PostDiffCallback : DiffUtil.ItemCallback<FeedItem>() {
    override fun areItemsTheSame(oldItem: FeedItem, newItem: FeedItem): Boolean {
        if (oldItem::class != newItem::class) {
            return false
        }
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: FeedItem, newItem: FeedItem): Boolean {
        return oldItem == newItem
    }
}
