package ru.netology.nmedia.activity

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toFile
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.github.dhaval2404.imagepicker.ImagePicker
import com.github.dhaval2404.imagepicker.constant.ImageProvider
import com.google.android.material.snackbar.Snackbar
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentNewPostBinding
import ru.netology.nmedia.di.DependencyContainer
import ru.netology.nmedia.model.PhotoModel
import ru.netology.nmedia.util.AndroidUtils
import ru.netology.nmedia.util.StringArg
import ru.netology.nmedia.viewmodel.PostViewModel
import ru.netology.nmedia.viewmodel.ViewModelFactory

class NewPostFragment : Fragment() {

    companion object {
        var Bundle.textArg: String? by StringArg
    }

    private val dependencyContainer = DependencyContainer.getInstance()
    private val viewModel: PostViewModel by activityViewModels(
        factoryProducer = {
            ViewModelFactory(dependencyContainer.repository, dependencyContainer.appAuth)
        }
    )

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

        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.new_post_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean =
                when (menuItem.itemId) {
                    R.id.save -> {
                        viewModel.changeContent(binding.edit.text.toString())
                        viewModel.save()
                        AndroidUtils.hideKeyboard(requireView())
                        findNavController().navigateUp()
                        true
                    }

                    else -> false
                }
        }, viewLifecycleOwner)

        val pickPhotoLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                when (it.resultCode) {
                    ImagePicker.RESULT_ERROR -> {
                        Snackbar.make(
                            binding.root,
                            ImagePicker.getError(it.data),
                            Snackbar.LENGTH_LONG
                        ).show()
                    }

                    Activity.RESULT_OK -> {
                        val uri = requireNotNull(it.data?.data)
                        viewModel.setPhoto(PhotoModel(uri = uri, file = uri.toFile()))
                    }
                }
            }

        with(binding) {
            arguments?.textArg
                ?.let(edit::setText)

            viewModel.state.observe(viewLifecycleOwner) { state ->
                if (state.error) {
                    retryButtonNewPost.visibility = View.VISIBLE
                    when (state.codeResponse) {
                        in 300..399 -> error300NewPost.visibility = View.VISIBLE
                        in 400..499 -> error400NewPost.visibility = View.VISIBLE
                        in 500..599 -> error500NewPost.visibility = View.VISIBLE
                        else -> anotherErrorNewPost.visibility = View.VISIBLE
                    }
                } else {
                    retryButtonNewPost.visibility = View.GONE
                    error300NewPost.visibility = View.GONE
                    error400NewPost.visibility = View.GONE
                    error500NewPost.visibility = View.GONE
                    anotherErrorNewPost.visibility = View.GONE
                }
            }

            viewModel.photo.observe(viewLifecycleOwner) { photo ->
                if (photo == null) {
                    binding.previewContainer.visibility = View.GONE
                    return@observe
                }

                previewContainer.visibility = View.VISIBLE
                preview.setImageURI(photo.uri)
            }

            viewModel.postCreated.observe(viewLifecycleOwner) {
                viewModel.loadPosts()
                findNavController().navigateUp()
            }

            retryButtonNewPost.setOnClickListener {
                AndroidUtils.hideKeyboard(requireView())
                viewModel.loadPosts()
                findNavController().navigateUp()
            }

            gallery.setOnClickListener {
                ImagePicker.with(this@NewPostFragment)
                    .crop()
                    .compress(2048)
                    .provider(ImageProvider.GALLERY)
                    .galleryOnly()
                    .galleryMimeTypes(
                        arrayOf(
                            "image/png",
                            "image/jpeg",
                        )
                    )
                    .createIntent(pickPhotoLauncher::launch)
            }

            takePhoto.setOnClickListener {
                ImagePicker.with(this@NewPostFragment)
                    .crop()
                    .compress(2048)
                    .provider(ImageProvider.CAMERA)
                    .createIntent(pickPhotoLauncher::launch)
            }

            clear.setOnClickListener {
                viewModel.clearPhoto()
            }

            return root
        }
    }
}