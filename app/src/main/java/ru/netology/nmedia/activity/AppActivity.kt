package ru.netology.nmedia.activity

import android.Manifest
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.navigation.findNavController
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.firebase.messaging.FirebaseMessaging
import ru.netology.nmedia.R
import ru.netology.nmedia.activity.NewPostFragment.Companion.textArg
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.viewmodel.AuthViewModel

class AppActivity : AppCompatActivity(R.layout.activity_app) {

    private val authViewModel by viewModels<AuthViewModel>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestNotificationsPermission()

        intent?.let {
            if (it.action != Intent.ACTION_SEND) {
                return@let
            }

            val text = it.getStringExtra(Intent.EXTRA_TEXT)
            if (text?.isNotBlank() != true) {
                return@let
            }

            intent.removeExtra(Intent.EXTRA_TEXT)
            findNavController(R.id.nav_host_fragment)
                .navigate(
                    R.id.action_feedFragment_to_newPostFragment,
                    Bundle().apply {
                        textArg = text
                    }
                )
        }

        checkGoogleApiAvailability()

        var currentMenuProvider: MenuProvider? = null

        authViewModel.data.observe(this) {
            currentMenuProvider?.let(::removeMenuProvider)

            addMenuProvider(object : MenuProvider {
                override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                    menuInflater.inflate(R.menu.menu_auth, menu)

                    menu.setGroupVisible(R.id.unauthorized, !authViewModel.authenticated)
                    menu.setGroupVisible(R.id.authorized, authViewModel.authenticated)
                }

                override fun onMenuItemSelected(menuItem: MenuItem): Boolean =
                    when (menuItem.itemId) {
                        R.id.signIn -> {
                            findNavController(R.id.nav_host_fragment)
                                .navigate(
                                    R.id.action_feedFragment_to_singInAndUpFragment,
                                    Bundle().apply { textArg = "signIn" }
                                )
                                currentMenuProvider?.let(::removeMenuProvider)
                            true
                        }

                        R.id.signUp -> {
                            findNavController(R.id.nav_host_fragment)
                                .navigate(
                                    R.id.action_feedFragment_to_singInAndUpFragment,
                                    Bundle().apply { textArg = "signUp" }
                                )
                            currentMenuProvider?.let(::removeMenuProvider)
                            true
                        }

                        R.id.logout -> {
                            createDialogLogoutButton()
                            true
                        }

                        else -> false
                    }

            }.also {
                currentMenuProvider = it
            }, this)
        }
    }

    fun createDialogLogoutButton(): Dialog {
        return this.let {
            val builder = AlertDialog.Builder(it)
            builder.setTitle("Are you sure you want to get out?")
                .setCancelable(true)
                .setPositiveButton("OK") { dialog, _ ->
                    AppAuth.getInstance().removeAuth()
                    dialog.cancel()
                    findNavController(R.id.nav_host_fragment).navigateUp()
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    dialog.cancel()
                }
            builder.show()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    private fun requestNotificationsPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            return
        }

        val permission = Manifest.permission.POST_NOTIFICATIONS

        if (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED) {
            return
        }

        requestPermissions(arrayOf(permission), 1)
    }

    private fun checkGoogleApiAvailability() {
        with(GoogleApiAvailability.getInstance()) {
            val code = isGooglePlayServicesAvailable(this@AppActivity)
            if (code == ConnectionResult.SUCCESS) {
                return@with
            }
            if (isUserResolvableError(code)) {
                getErrorDialog(this@AppActivity, code, 9000)?.show()
                return
            }
            Toast.makeText(this@AppActivity, R.string.google_play_unavailable, Toast.LENGTH_LONG)
                .show()
        }

        FirebaseMessaging.getInstance().token.addOnSuccessListener {
            println(it)
        }
    }
}