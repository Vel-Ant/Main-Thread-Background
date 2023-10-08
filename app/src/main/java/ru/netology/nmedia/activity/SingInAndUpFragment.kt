package ru.netology.nmedia.activity

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toFile
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.github.dhaval2404.imagepicker.ImagePicker
import com.github.dhaval2404.imagepicker.constant.ImageProvider
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nmedia.R
import ru.netology.nmedia.activity.NewPostFragment.Companion.textArg
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.databinding.FragmentSingInAndUpBinding
import ru.netology.nmedia.dto.MediaUpload
import ru.netology.nmedia.util.AndroidUtils
import ru.netology.nmedia.viewmodel.PostViewModel
import ru.netology.nmedia.viewmodel.SignInAndUpViewModel
import javax.inject.Inject

@AndroidEntryPoint
class SingInAndUpFragment : Fragment() {

    @Inject
    lateinit var appAuth: AppAuth
    private val viewModel: PostViewModel by activityViewModels()
    private val signInAndUpViewModel: SignInAndUpViewModel by viewModels()

    val pickAvatarLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            when (it.resultCode) {
                ImagePicker.RESULT_ERROR -> {
                    Snackbar.make(
                        requireView(),
                        ImagePicker.getError(it.data),
                        Snackbar.LENGTH_LONG
                    ).show()
                }
                Activity.RESULT_OK -> {
                    val uri = requireNotNull(it.data?.data)
                    signInAndUpViewModel.changeAvatar(uri = uri, file = uri.toFile())
                }
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentSingInAndUpBinding.inflate(
            inflater,
            container,
            false
        )

        with(binding) {

            if (arguments?.textArg == "signIn") {
                signInContainer.visibility = View.VISIBLE
                signUpContainer.visibility = View.GONE
            } else if (arguments?.textArg == "signUp") {
                signUpContainer.visibility = View.VISIBLE
                signInContainer.visibility = View.GONE
            }

            signInAndUpViewModel.state.observe(viewLifecycleOwner) { state ->
                if (state.loginError) {
                    passwordFieldAuth.error = getString(R.string.login_error)
                } else if (state.error) {
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

            signInAndUpViewModel.user.observe(viewLifecycleOwner) {
                appAuth.setAuth(it.id, it.token.toString())
                AndroidUtils.hideKeyboard(requireView())
                findNavController().navigateUp()
            }

            signInButton.setOnClickListener {
                signInAndUpViewModel.login(
                    loginFieldAuth.editText?.text.toString(),
                    passwordFieldAuth.editText?.text.toString()
                )
            }

            signUpButton.setOnClickListener {
                if (passwordFieldReg.editText?.text.toString() == confirmPasswordFieldReg.editText?.text.toString()) {
                    val avatarFile = signInAndUpViewModel.avatar.value?.file

                    if (avatarFile != null) {
                        signInAndUpViewModel.regNewUserWithPhoto(
                            login = loginFieldReg.editText?.text.toString(),
                            pass = passwordFieldReg.editText?.text.toString(),
                            name = nameFieldReg.editText?.text.toString(),
                            media = MediaUpload(avatarFile)
                        )
                    } else if (avatarFile == null) {
                        signInAndUpViewModel.regNewUser(
                            login = loginFieldReg.editText?.text.toString(),
                            pass = passwordFieldReg.editText?.text.toString(),
                            name = nameFieldReg.editText?.text.toString()
                        )
                    }
                } else {
                    confirmPasswordFieldReg.error = getString(R.string.pass_error)
                }
            }

            gallery.setOnClickListener {
                ImagePicker.with(requireActivity())
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
                    .createIntent(pickAvatarLauncher::launch)
            }

            takePhoto.setOnClickListener {
                ImagePicker.with(requireActivity())
                    .crop()
                    .compress(2048)
                    .provider(ImageProvider.CAMERA)
                    .createIntent(pickAvatarLauncher::launch)
            }

            clear.setOnClickListener {
                signInAndUpViewModel.clearAvatar()
            }

            regButton.setOnClickListener {
                passwordFieldAuth.error = null
                signUpContainer.visibility = View.VISIBLE
                signInContainer.visibility = View.GONE
            }

            alreadyRegisteredButton.setOnClickListener {
                confirmPasswordFieldReg.error = null
                signInContainer.visibility = View.VISIBLE
                signUpContainer.visibility = View.GONE
            }

            retryButton.setOnClickListener {
                if (signUpContainer.isVisible) {
                    signUpContainer.visibility = View.VISIBLE
                    signInContainer.visibility = View.GONE
                } else if (signInContainer.isVisible) {
                    signInContainer.visibility = View.VISIBLE
                    signUpContainer.visibility = View.GONE
                }
            }

            confirmPasswordFieldRegInner.setOnFocusChangeListener { _, active ->
                if (active) {
                    confirmPasswordFieldReg.error = null
                }
            }

            passwordFieldAuthInner.setOnFocusChangeListener { _, active ->
                if (active) {
                    passwordFieldAuth.error = null
                }
            }

            signInAndUpViewModel.avatar.observe(viewLifecycleOwner) {avatar ->
                if (avatar == null) {
                    return@observe
                }
                previewAvatar.setImageURI(avatar.uri)
            }

            return root
        }
    }
}