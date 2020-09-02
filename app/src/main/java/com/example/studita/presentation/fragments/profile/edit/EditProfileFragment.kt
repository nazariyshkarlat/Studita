package com.example.studita.presentation.fragments.profile.edit

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.text.Editable
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.studita.R
import com.example.studita.domain.entity.EditProfileData
import com.example.studita.presentation.draw.AvaDrawer
import com.example.studita.presentation.fragments.base.NavigatableFragment
import com.example.studita.presentation.fragments.dialog_alerts.ChangeAvatarDialogAlertFragment
import com.example.studita.presentation.fragments.dialog_alerts.EditProfileRemovePhotoDialogAlertFragment
import com.example.studita.presentation.fragments.dialog_alerts.UnsavedChangesDialogAlertFragment
import com.example.studita.presentation.listeners.GenericTextWatcher
import com.example.studita.presentation.listeners.GenericTextWatcherImpl
import com.example.studita.presentation.listeners.setGenericTextWatcher
import com.example.studita.presentation.view_model.EditProfileViewModel
import com.example.studita.presentation.view_model.ToolbarFragmentViewModel
import com.example.studita.presentation.views.CustomSnackbar
import com.example.studita.utils.PrefsUtils
import com.example.studita.utils.ThemeUtils
import com.example.studita.utils.UserUtils
import com.example.studita.utils.limitLength
import kotlinx.android.synthetic.main.edit_profile_layout.*


class EditProfileFragment : NavigatableFragment(R.layout.edit_profile_layout), GenericTextWatcher {

    private lateinit var editProfileViewModel: EditProfileViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        editProfileViewModel =
            ViewModelProviders.of(this@EditProfileFragment).get(EditProfileViewModel::class.java)

        UserUtils.userDataLiveData.observe(viewLifecycleOwner, Observer { userData ->

            if (savedInstanceState == null) {
                editProfileViewModel.oldProfileData =
                    EditProfileData(userData.userName, userData.name, userData.avatarLink)
                editProfileViewModel.newProfileData = editProfileViewModel.oldProfileData.copy()
                fillEditTexts(editProfileViewModel.newProfileData)
            }

            fillAvatar()
            fillCounters(editProfileViewModel.newProfileData)
            editProfileViewModel.userNameAvailableState.value =
                EditProfileViewModel.UserNameAvailable.AVAILABLE
            (view as ViewGroup).removeView(editProfileLayoutProgressBar)
            editProfileLayoutScrollView.visibility = View.VISIBLE
        })

        editProfileViewModel.userNameAvailableState.observe(viewLifecycleOwner, Observer {
            editProfileViewModel.checkCorrectUserName()
            if (it == EditProfileViewModel.UserNameAvailable.AVAILABLE) {
                editProfileLayoutUserNameEditText.hasError = false
                editProfileLayoutUserNameStatusTextView.text =
                    resources.getString(R.string.edit_profile_user_name_available)
                editProfileLayoutUserNameStatusTextView.setTextColor(
                    ContextCompat.getColor(
                        view.context,
                        R.color.green
                    )
                )
            } else {
                if (it == EditProfileViewModel.UserNameAvailable.UNAVAILABLE)
                    editProfileLayoutUserNameStatusTextView.text =
                        resources.getString(R.string.edit_profile_user_name_unavailable)
                else
                    editProfileLayoutUserNameStatusTextView.text =
                        resources.getString(R.string.edit_profile_user_name_is_taken)
                editProfileLayoutUserNameStatusTextView.setTextColor(
                    ContextCompat.getColor(
                        view.context,
                        R.color.red
                    )
                )
                editProfileLayoutUserNameEditText.hasError = true
            }
        })

        editProfileViewModel.saveChangesButtonVisibleState.observe(viewLifecycleOwner, Observer {
            if (it) {
                showSaveChangesButton()
                toolbarFragmentViewModel?.hideProgress()
            } else
                toolbarFragmentViewModel?.setToolbarRightButtonState(
                    ToolbarFragmentViewModel.ToolbarRightButtonState.Disabled(
                        R.drawable.ic_done_selector
                    )
                )
        })

        editProfileViewModel.countersErrorState.observe(viewLifecycleOwner, Observer { showError ->
            editProfileLayoutUserNameCounter.setTextColor(
                if (showError) {
                    editProfileLayoutUserNameEditText.hasError = true
                    ContextCompat.getColor(view.context, R.color.red)
                } else {
                    editProfileLayoutUserNameEditText.hasError = false
                    ThemeUtils.getSecondaryColor(view.context)
                }
            )
        })

        editProfileViewModel.backClickState.observe(viewLifecycleOwner, Observer {
            if (it == EditProfileViewModel.BackClickState.SHOW_DIALOG) {
                UnsavedChangesDialogAlertFragment().apply {
                    setTargetFragment(this@EditProfileFragment as NavigatableFragment, 0)
                }.show((activity as AppCompatActivity).supportFragmentManager, null)
            } else {
                super.onBackClick()
            }
        })

        editProfileViewModel.saveProfileChangesState.observe(viewLifecycleOwner, Observer {
            when (it) {
                EditProfileViewModel.SaveProfileChangesState.FAILURE -> {
                    toolbarFragmentViewModel?.hideProgress()
                    editProfileViewModel.saveChangesButtonVisibleState.value = true
                }
                EditProfileViewModel.SaveProfileChangesState.SUCCESS -> {
                    val snackbar = CustomSnackbar(view.context)
                    snackbar.show(
                        resources.getString(R.string.changes_are_saved),
                        ThemeUtils.getAccentColor(snackbar.context),
                        delay = 500
                    )
                    super.onBackClick()
                }
                EditProfileViewModel.SaveProfileChangesState.LOADING -> {
                    toolbarFragmentViewModel?.showProgress()
                    toolbarFragmentViewModel?.setToolbarRightButtonState(ToolbarFragmentViewModel.ToolbarRightButtonState.Invisible)
                }
                else -> {
                }
            }
        })

        editProfileLayoutUserNameEditText.setGenericTextWatcher(
            GenericTextWatcherImpl(
                this,
                editProfileLayoutUserNameEditText
            )
        )
        editProfileLayoutNameEditText.setGenericTextWatcher(
            GenericTextWatcherImpl(
                this,
                editProfileLayoutNameEditText
            )
        )

        editProfileLayoutUserNameEditText.limitLength(resources.getInteger(R.integer.user_name_max_length))
        editProfileLayoutNameEditText.limitLength(resources.getInteger(R.integer.name_max_length))

        editProfileLayoutNameEditText.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus)
                (v as EditText).setSelection(v.text.length)
        }

        editProfileLayoutSelectAvatar.setOnClickListener {
            fragmentManager?.let { manager ->
                ChangeAvatarDialogAlertFragment().apply {
                    setTargetFragment(this@EditProfileFragment as NavigatableFragment, 0)
                }.show(manager, null)
            }
        }

        editProfileLayoutRemoveAvatar.setOnClickListener {
            if (editProfileViewModel.newProfileData.avatarLink != null || editProfileViewModel.selectedImage != null) {
                EditProfileRemovePhotoDialogAlertFragment().apply {
                    setTargetFragment(this@EditProfileFragment, 23)
                }.show((activity as AppCompatActivity).supportFragmentManager, null)
            }
        }

        scrollingView = editProfileLayoutScrollView

    }

    private fun fillEditTexts(editProfileData: EditProfileData) {
        editProfileLayoutUserNameEditText.setText(editProfileData.userName)
        editProfileLayoutNameEditText.setText(editProfileData.name)
    }

    private fun fillCounters(editProfileData: EditProfileData) {
        editProfileLayoutNameCounter.text = resources.getString(
            R.string.edit_text_counter_template, editProfileData.name?.length
                ?: 0, resources.getInteger(R.integer.name_max_length)
        )
        editProfileLayoutUserNameCounter.text = resources.getString(
            R.string.edit_text_counter_template, editProfileData.userName?.length
                ?: 0, resources.getInteger(R.integer.user_name_max_length)
        )
    }

    private fun fillAvatar() {
        if ((UserUtils.userData.avatarLink == null || editProfileViewModel.avaChanged) && editProfileViewModel.selectedImage == null) {
            Glide
                .with(this)
                .clear(editProfileLayoutAvatar)
            AvaDrawer.drawAvatar(
                editProfileLayoutAvatar,
                editProfileViewModel.newProfileData.userName!!,
                PrefsUtils.getUserId()!!
            )
        } else {
            Glide
                .with(this)
                .load(editProfileViewModel.selectedImage ?: UserUtils.userData.avatarLink)
                .centerCrop()
                .apply(RequestOptions.circleCropTransform())
                .into(editProfileLayoutAvatar)
        }
    }

    override fun beforeTextChanged(
        view: View,
        charSequence: CharSequence?,
        start: Int,
        count: Int,
        after: Int
    ) {
    }

    override fun afterTextChanged(view: View, editable: Editable?) {}

    override fun onTextChanged(
        view: View,
        charSequence: CharSequence?,
        start: Int,
        before: Int,
        count: Int
    ) {
        editProfileViewModel.saveChangesButtonVisibleState.value = false
        when (view.id) {
            R.id.editProfileLayoutUserNameEditText -> {
                if (charSequence?.length == 1) {
                    if (editProfileViewModel.newProfileData.avatarLink == null)
                        AvaDrawer.drawAvatar(
                            editProfileLayoutAvatar,
                            charSequence.toString(),
                            PrefsUtils.getUserId()!!
                        )
                }

                editProfileViewModel.formNewUserName(charSequence)

                verifyingUserName(view)

                editProfileViewModel.verifyUserName()
            }
            R.id.editProfileLayoutNameEditText -> {

                if ((charSequence?.length == resources.getInteger(R.integer.name_max_length)) && (charSequence.last() == ' ')) {
                    editProfileLayoutNameEditText.text?.delete(
                        charSequence.length - 1,
                        charSequence.length
                    )
                    return
                }

                editProfileViewModel.formNewName(charSequence?.trim())
                editProfileViewModel.checkShowSaveButton()
            }
        }
        fillCounters(editProfileViewModel.newProfileData)
    }

    private fun showSaveChangesButton() {
        toolbarFragmentViewModel?.setToolbarRightButtonState(
            ToolbarFragmentViewModel.ToolbarRightButtonState.IsEnabled(
                R.drawable.ic_done_selector
            ) {
                saveChangesButtonOnClick()
            })
    }

    override fun onBackClick() {
        if (UserUtils.userDataLiveData.value != null)
            editProfileViewModel.backClick()
        else
            super.onBackClick()
    }

    private fun saveChangesButtonOnClick() {
        editProfileViewModel.saveProfileChanges()
    }

    private fun verifyingUserName(view: View) {
        editProfileLayoutUserNameEditText.hasError = false
        editProfileLayoutUserNameStatusTextView.text = resources.getString(R.string.verifying)
        editProfileLayoutUserNameStatusTextView.setTextColor(ThemeUtils.getSecondaryColor(view.context))
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden)
            editProfileViewModel.checkShowSaveButton()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 228 && resultCode == Activity.RESULT_OK)
            super.onBackClick()
        else if (requestCode == 345 && resultCode == Activity.RESULT_OK) {
            if (data?.extras?.containsKey("SELECTED_IMAGE") == true) {
                editProfileViewModel.selectedImage = data.extras?.get("SELECTED_IMAGE") as Bitmap?
                if (editProfileViewModel.selectedImage == null) {
                    editProfileViewModel.newProfileData.avatarLink = null
                }
            }
            editProfileViewModel.avaChanged = true
            editProfileViewModel.checkShowSaveButton()
            fillAvatar()
        }
    }
}