package ru.netology.nmedia.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nmedia.R
import ru.netology.nmedia.activity.NewPostFragment.Companion.textArg
import ru.netology.nmedia.adapter.OnInteractionListener
import ru.netology.nmedia.adapter.PostsAdapter
import ru.netology.nmedia.databinding.FragmentFeedBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.viewmodel.PostViewModel

class FeedFragment : Fragment() {

    private val viewModel: PostViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentFeedBinding.inflate(inflater, container, false)

        val adapter = PostsAdapter(object : OnInteractionListener {
            override fun onEdit(post: Post) {
                viewModel.edit(post)
                findNavController().navigate(
                    R.id.action_feedFragment_to_newPostFragment,
                    Bundle().apply { textArg = post.content })
            }

            override fun onLike(post: Post) {
                if (post.likedByMe) {
                    viewModel.unlikeById(post.id)
                } else {
                    viewModel.likeById(post.id)
                }
            }

            override fun onRemove(post: Post) {
                viewModel.removeById(post.id)
            }

            override fun onShare(post: Post) {
                val intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, post.content)
                    type = "text/plain"
                }

                val shareIntent =
                    Intent.createChooser(intent, getString(R.string.chooser_share_post))
                startActivity(shareIntent)
            }

            override fun onLoadPost() {
                viewModel.loadPosts()
            }

            override fun onImageView(url: String) {
                findNavController().navigate(
                    R.id.action_feedFragment_to_imageViewFragment,
                    Bundle().apply { putString("url", url) })
            }
        })
        binding.list.adapter = adapter
        viewModel.state.observe(viewLifecycleOwner) { state ->
            binding.progress.isVisible = state.loading
            binding.swiperefresh.isRefreshing = state.refreshing

            if (state.error) {
                binding.retryButton.visibility = View.VISIBLE
                when (state.codeResponse) {
                    in 300..399 -> binding.error300.visibility = View.VISIBLE
                    in 400..499 -> binding.error400.visibility = View.VISIBLE
                    in 500..599 -> binding.error500.visibility = View.VISIBLE
                    else -> binding.anotherError.visibility = View.VISIBLE
                }
            } else {
                binding.retryButton.visibility = View.GONE
                binding.error300.visibility = View.GONE
                binding.error400.visibility = View.GONE
                binding.error500.visibility = View.GONE
                binding.anotherError.visibility = View.GONE
            }
        }

        viewModel.newerCount.observe(viewLifecycleOwner) {
            if (it >= 1) {
                binding.newerPostsButton.visibility = View.VISIBLE
                Log.d("FeedFragment", "newer count: $id")
            } else {
                binding.newerPostsButton.visibility = View.GONE
            }
        }

        binding.newerPostsButton.setOnClickListener {
            binding.newerPostsButton.visibility = View.GONE
            viewModel.loadAllNewPosts()
        }

        adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                if (positionStart == 0) {
                    binding.list.smoothScrollToPosition(0)
                }
            }
        })

        viewModel.data.observe(viewLifecycleOwner) {
            binding.emptyText.isVisible = it.empty
            adapter.submitList(it.posts)
        }

        binding.retryButton.setOnClickListener {
            viewModel.loadPosts()
        }

        binding.fab.setOnClickListener {
            findNavController().navigate(R.id.action_feedFragment_to_newPostFragment)
        }

        binding.swiperefresh.setColorSchemeResources(
            android.R.color.holo_orange_light
        )

        binding.swiperefresh.setOnRefreshListener {
            viewModel.refresh()
        }

        return binding.root
    }
}
