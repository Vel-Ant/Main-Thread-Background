package ru.netology.nmedia.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.databinding.FragmentNewPostBinding
import ru.netology.nmedia.util.AndroidUtils
import ru.netology.nmedia.util.StringArg
import ru.netology.nmedia.viewmodel.PostViewModel

class NewPostFragment : Fragment() {

    companion object {
        var Bundle.textArg: String? by StringArg
    }

    private val viewModel: PostViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentNewPostBinding.inflate(
            inflater,
            container,
            false
        )

        arguments?.textArg
            ?.let(binding.edit::setText)

        viewModel.state.observe(viewLifecycleOwner) { state ->
            if (state.error) {
                binding.retryButtonNewPost.visibility = View.VISIBLE
                when (state.codeResponse) {
                    in 300..399 -> binding.error300NewPost.visibility = View.VISIBLE
                    in 400..499 -> binding.error400NewPost.visibility = View.VISIBLE
                    in 500..599 -> binding.error500NewPost.visibility = View.VISIBLE
                    else -> binding.anotherErrorNewPost.visibility = View.VISIBLE
                }
            } else {
                binding.retryButtonNewPost.visibility = View.GONE
                binding.error300NewPost.visibility = View.GONE
                binding.error400NewPost.visibility = View.GONE
                binding.error500NewPost.visibility = View.GONE
                binding.anotherErrorNewPost.visibility = View.GONE
            }
        }

        binding.retryButtonNewPost.setOnClickListener {
            viewModel.loadPosts()
            findNavController().navigateUp()
        }

        binding.ok.setOnClickListener {
            viewModel.changeContent(binding.edit.text.toString())
            viewModel.save()
            AndroidUtils.hideKeyboard(requireView())
        }
        viewModel.postCreated.observe(viewLifecycleOwner) {
            viewModel.loadPosts()
            findNavController().navigateUp()
        }
        return binding.root
    }
}