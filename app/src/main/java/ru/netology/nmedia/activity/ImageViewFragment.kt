package ru.netology.nmedia.activity

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.BuildConfig.BASE_URL
import ru.netology.nmedia.databinding.FragmentImageViewBinding
import ru.netology.nmedia.imageview.loadImageAttachment

class ImageViewFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentImageViewBinding.inflate(
            inflater,
            container,
            false
        )

        val urlAttachment = arguments?.getString("url")

        binding.apply {
            fullScreen.visibility = View.GONE
            urlAttachment?.let {
                fullScreen.loadImageAttachment("${BASE_URL}/media/${it}")
            }
            fullScreen.visibility = View.VISIBLE
        }

        binding.fullScreen.setOnClickListener {
            findNavController().navigateUp()
        }

        return binding.root
    }
}