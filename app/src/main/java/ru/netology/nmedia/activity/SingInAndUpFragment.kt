package ru.netology.nmedia.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.R
import ru.netology.nmedia.activity.NewPostFragment.Companion.textArg
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.databinding.FragmentSingInAndUpBinding
import ru.netology.nmedia.util.AndroidUtils
import ru.netology.nmedia.viewmodel.SignInAndUpViewModel

class SingInAndUpFragment : Fragment() {

    private val signInAndUpViewModel by viewModels<SignInAndUpViewModel>()

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

        if (arguments?.textArg == "signIn") {
            binding.signInContainer.visibility = View.VISIBLE
            binding.signUpContainer.visibility = View.GONE
        } else if (arguments?.textArg == "signUp") {
            binding.signUpContainer.visibility = View.VISIBLE
            binding.signInContainer.visibility = View.GONE
        }

        with(binding) {

            signInButton.setOnClickListener {
                signInAndUpViewModel.login(
                    loginFieldAuth.editText?.text.toString(),
                    passwordFieldAuth.editText?.text.toString()
                )
            }

            signUpButton.setOnClickListener {
                if (passwordFieldReg.editText?.text.toString() == confirmPasswordFieldReg.editText?.text.toString()) {
                    signInAndUpViewModel.regNewUser(
                        loginFieldReg.editText?.text.toString(),
                        passwordFieldReg.editText?.text.toString(),
                        nameFieldReg.editText?.text.toString()
                    )
                } else {
                    confirmPasswordFieldReg.error = getString(R.string.pass_error)
                }
            }

            regButton.setOnClickListener {
                signUpContainer.visibility = View.VISIBLE
                signInContainer.visibility = View.GONE
            }

            alreadyRegisteredButton.setOnClickListener {
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
        }

        signInAndUpViewModel.state.observe(viewLifecycleOwner) { state ->
            if (state.loginError) {
                binding.passwordFieldAuth.error = getString(R.string.login_error)
            } else if (state.error) {
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

        signInAndUpViewModel.user.observe(viewLifecycleOwner) {
            AppAuth.getInstance().setAuth(it.id, it.token.toString())
            AndroidUtils.hideKeyboard(requireView())
            findNavController().navigateUp()
        }
        return binding.root
    }


}