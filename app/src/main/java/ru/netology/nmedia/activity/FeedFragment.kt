package ru.netology.nmedia.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nmedia.R
import ru.netology.nmedia.activity.NewPostFragment.Companion.textArg
import ru.netology.nmedia.adapter.OnInteractionListener
import ru.netology.nmedia.adapter.PostsAdapter
import ru.netology.nmedia.databinding.FragmentFeedBinding
import ru.netology.nmedia.di.DependencyContainer
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.viewmodel.AuthViewModel
import ru.netology.nmedia.viewmodel.PostViewModel
import ru.netology.nmedia.viewmodel.ViewModelFactory

class FeedFragment() : Fragment() {

    private val dependencyContainer = DependencyContainer.getInstance()
    private val viewModel: PostViewModel by viewModels(
        factoryProducer = {
            ViewModelFactory(dependencyContainer.repository, dependencyContainer.appAuth)
        }
    )

    private val authViewModel: AuthViewModel by viewModels(
        factoryProducer = {
            ViewModelFactory(dependencyContainer.repository, dependencyContainer.appAuth)
        }
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentFeedBinding.inflate(
            inflater,
            container,
            false
        )

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

        with(binding) {
            list.adapter = adapter

            adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
                override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                    if (positionStart == 0) {
                        list.smoothScrollToPosition(0)
                    }
                }
            })

            viewModel.state.observe(viewLifecycleOwner) { state ->
                progress.isVisible = state.loading
                swiperefresh.isRefreshing = state.refreshing

                if (state.error) {
                    retryButton.visibility = View.VISIBLE
                    when (state.codeResponse) {
                        in 300..399 -> error300.visibility = View.VISIBLE
                        in 400..499 -> error400.visibility = View.VISIBLE
                        in 500..599 -> error500.visibility = View.VISIBLE
                        else -> anotherError.visibility = View.VISIBLE
                    }
                } else {
                    retryButton.visibility = View.GONE
                    error300.visibility = View.GONE
                    error400.visibility = View.GONE
                    error500.visibility = View.GONE
                    anotherError.visibility = View.GONE
                }
            }

            viewModel.newerCount.observe(viewLifecycleOwner) {
                if (it >= 1) {
                    newerPostsButton.visibility = View.VISIBLE
                    Log.d("FeedFragment", "newer count: $id")
                } else {
                    newerPostsButton.visibility = View.GONE
                }
            }

            viewModel.data.observe(viewLifecycleOwner) {
                emptyText.isVisible = it.empty
                adapter.submitList(it.posts)
            }

            swiperefresh.setColorSchemeResources(
                android.R.color.holo_orange_light
            )

            swiperefresh.setOnRefreshListener {
                viewModel.refresh()
            }

            newerPostsButton.setOnClickListener {
                newerPostsButton
                viewModel.loadAllNewPosts()
            }

            retryButton.setOnClickListener {
                viewModel.loadPosts()
            }

            fab.setOnClickListener {
                if (authViewModel.authenticated) {
                    findNavController().navigate(R.id.action_feedFragment_to_newPostFragment)
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Only registered users can create a post",
                        Toast.LENGTH_LONG
                    )
                        .show()
                }
            }

            return root
        }
    }
}
