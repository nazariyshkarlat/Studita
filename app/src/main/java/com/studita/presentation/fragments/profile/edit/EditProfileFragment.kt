package com.studita.presentation.fragments.profile.edit

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.text.Editable
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.studita.R
import com.studita.domain.entity.EditProfileData
import com.studita.domain.interactor.edit_profile.EditProfileInteractor
import com.studita.presentation.draw.AvaDrawer
import com.studita.presentation.fragments.base.NavigatableFragment
import com.studita.presentation.fragments.dialog_alerts.ChangeAvatarDialogAlertFragment
import com.studita.presentation.fragments.dialog_alerts.EditProfileRemovePhotoDialogAlertFragment
import com.studita.presentation.fragments.dialog_alerts.UnsavedChangesDialogAlertFragment
import com.studita.presentation.fragments.error_fragments.InternetIsDisabledFragment
import com.studita.presentation.fragments.error_fragments.ServerProblemsFragment
import com.studita.presentation.listeners.GenericTextWatcher
import com.studita.presentation.listeners.GenericTextWatcherImpl
import com.studita.presentation.listeners.ReloadPageCallback
import com.studita.presentation.listeners.setGenericTextWatcher
import com.studita.presentation.view_model.EditProfileViewModel
import com.studita.presentation.view_model.ToolbarFragmentViewModel
import com.studita.presentation.views.CustomSnackbar
import com.studita.utils.*
import kotlinx.android.synthetic.main.edit_profile_layout.*


class EditProfileFragment : NavigatableFragment(R.layout.edit_profile_layout), GenericTextWatcher, ReloadPageCallback {

    private val editProfileViewModel: EditProfileViewModel by lazy {
        ViewModelProviders.of(this@EditProfileFragment).get(EditProfileViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        editProfileViewModel.progressState.observe(viewLifecycleOwner, Observer { inProgress ->

            if(inProgress){
                editProfileLayoutProgressBar.visibility = View.VISIBLE
                editProfileLayoutScrollView.visibility = View.GONE
            }else {
                if(editProfileViewModel.oldProfileData == null || editProfileViewModel.newProfileData == null){
                    editProfileViewModel.oldProfileData =
                        EditProfileData(UserUtils.userData.userName, UserUtils.userData.name, UserUtils.userData.bio, UserUtils.userData.avatarLink)
                    editProfileViewModel.newProfileData = editProfileViewModel.oldProfileData!!.copy()
                    fillEditTexts(editProfileViewModel.newProfileData!!)
                    editProfileLayoutSelectAvatar.requestFocus()
                    editProfileViewModel.verifyUserName()
                    this.hideKeyboard()
                }

                setTextWatchers()

                fillAvatar()
                fillCounters(editProfileViewModel.newProfileData!!)
                editProfileLayoutProgressBar.visibility = View.GONE
                editProfileLayoutScrollView.visibility = View.VISIBLE

                editProfileLayoutRemoveAvatar.setOnClickListener {
                    if (editProfileViewModel.newProfileData!!.avatarLink != null || editProfileViewModel.selectedImage != null) {
                        EditProfileRemovePhotoDialogAlertFragment().apply {
                            setTargetFragment(this@EditProfileFragment, 23)
                        }.show((activity as AppCompatActivity).supportFragmentManager, null)
                    }
                }

                if ((UserUtils.userData.avatarLink == null || editProfileViewModel.avaChanged) && editProfileViewModel.selectedImage == null)
                    editProfileLayoutRemoveAvatar.isEnabled = false
            }
        })

        editProfileViewModel.errorEvent.observe(viewLifecycleOwner, Observer { isNetworkError->
            if (isNetworkError) {
                addFragment(InternetIsDisabledFragment(), R.id.editProfileLayoutParentLayout, false)
            }else{
                addFragment(ServerProblemsFragment(), R.id.editProfileLayoutParentLayout, false)
            }
        })

        editProfileViewModel.userNameAvailableState.observe(viewLifecycleOwner, Observer {
            editProfileViewModel.checkCorrectUserName()
            when (it) {
                EditProfileViewModel.UserNameAvailable.Available -> {
                    editProfileLayoutUserNameEditText.hasError = false
                    editProfileLayoutUserNameStatusTextView.text =
                        resources.getString(R.string.edit_profile_user_name_available)
                    editProfileLayoutUserNameStatusTextView.setTextColor(
                        ThemeUtils.getGreenColor(context!!)
                    )
                }
                is EditProfileViewModel.UserNameAvailable.Unavailable  -> {
                    editProfileLayoutUserNameStatusTextView.setTextColor(
                        ThemeUtils.getRedColor(context!!)
                    )
                    editProfileLayoutUserNameEditText.hasError = true
                    editProfileLayoutUserNameStatusTextView.text =

                        when(it.errorType){
                            EditProfileViewModel.ErrorType.UNAVAILABLE -> resources.getString(R.string.edit_profile_user_name_unavailable)
                            EditProfileViewModel.ErrorType.IS_TAKEN -> resources.getString(R.string.edit_profile_user_name_is_taken)
                            EditProfileViewModel.ErrorType.CONNECTION_ERROR -> resources.getString(R.string.no_connection)
                            EditProfileViewModel.ErrorType.SERVER_ERROR -> resources.getString(R.string.server_temporarily_unavailable)
                        }

                }
            }
        })

        editProfileViewModel.saveChangesButtonVisibleState.observe(viewLifecycleOwner, Observer {
            if (it) {
                showSaveChangesButton()
                toolbarFragmentViewModel?.hideProgress()
            } else
                toolbarFragmentViewModel?.setToolbarRightButtonState(
                    ToolbarFragmentViewModel.ToolbarRightButtonState.Invisible
                )
        })

        editProfileViewModel.countersErrorState.observe(viewLifecycleOwner, Observer { showError ->
            editProfileLayoutUserNameCounter.setTextColor(
                if (showError) {
                    editProfileLayoutUserNameEditText.hasError = true
                    ThemeUtils.getRedColor(context!!)
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
                        ColorUtils.compositeColors(
                            ThemeUtils.getAccentLiteColor(snackbar.context),
                            ContextCompat.getColor(snackbar.context, R.color.white)
                        ),
                        ContextCompat.getColor(snackbar.context, R.color.black),
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

        limitEditTextsLength()

        editProfileLayoutBioEditText.setHorizontallyScrolling(false)
        editProfileLayoutBioEditText.maxLines = Integer.MAX_VALUE

        editProfileLayoutNameEditText.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus)
                (v as EditText).setSelection(v.text.length)
        }

        editProfileLayoutBioEditText.setOnFocusChangeListener { v, hasFocus ->
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

        scrollingView = editProfileLayoutScrollView

    }

    private fun fillEditTexts(editProfileData: EditProfileData) {
        editProfileLayoutUserNameEditText.setText(resources.getString(R.string.user_name_template, editProfileData.userName))
        editProfileLayoutNameEditText.setText(editProfileData.name)
        editProfileLayoutBioEditText.setText(editProfileData.bio)
    }

    private fun fillCounters(editProfileData: EditProfileData) {
        editProfileLayoutNameCounter.text = resources.getString(
            R.string.edit_text_counter_template, editProfileData.name?.length
                ?: 0, EditProfileInteractor.NAME_MAX_LENGTH
        )
        editProfileLayoutUserNameCounter.text = resources.getString(
            R.string.edit_text_counter_template, editProfileData.userName?.length
                ?: 0, EditProfileInteractor.USER_NAME_MAX_LENGTH
        )
        editProfileLayoutBioCounter.text = resources.getString(
            R.string.edit_text_counter_template, editProfileData.bio?.length
                ?: 0, EditProfileInteractor.BIO_MAX_LENGTH
        )
    }

    private fun setTextWatchers(){
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
        editProfileLayoutBioEditText.setGenericTextWatcher(
            GenericTextWatcherImpl(
                this,
                editProfileLayoutBioEditText
            )
        )
    }

    private fun limitEditTextsLength(){
        editProfileLayoutUserNameEditText.limitLength(EditProfileInteractor.USER_NAME_MAX_LENGTH+1)
        editProfileLayoutNameEditText.limitLength(EditProfileInteractor.NAME_MAX_LENGTH)
        editProfileLayoutBioEditText.limitLength(EditProfileInteractor.BIO_MAX_LENGTH)
    }

    private fun fillAvatar() {
        if ((UserUtils.userData.avatarLink == null || editProfileViewModel.avaChanged) && editProfileViewModel.selectedImage == null) {
            Glide
                .with(this)
                .clear(editProfileLayoutAvatar)
            AvaDrawer.drawAvatar(
                editProfileLayoutAvatar,
                if((editProfileViewModel.newProfileData!!.userName?.length ?: 0 > 1))
                    editProfileViewModel.newProfileData!!.userName!!
                else " ",
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
                if(charSequence.isNullOrEmpty() || charSequence.first() != '@'){
                    editProfileLayoutUserNameEditText.setText(resources.getString(R.string.user_name_template, editProfileLayoutUserNameEditText.text))
                    editProfileLayoutUserNameEditText.setSelection(editProfileLayoutUserNameEditText.text!!.length)
                    return
                }
                if (editProfileViewModel.newProfileData!!.avatarLink == null && editProfileViewModel.selectedImage == null)
                    AvaDrawer.drawAvatar(
                        editProfileLayoutAvatar,
                        if(charSequence.length > 1) charSequence.substring(1, charSequence.length) else " ",
                        PrefsUtils.getUserId()!!
                    )

                editProfileViewModel.formNewUserName(charSequence)

                verifyingUserName(view)

                editProfileViewModel.verifyUserName()
            }
            R.id.editProfileLayoutNameEditText -> {
                editProfileViewModel.formNewName(charSequence)
                editProfileViewModel.checkShowSaveButton()
            }
            R.id.editProfileLayoutBioEditText -> {
                editProfileViewModel.formNewBio(charSequence)
                editProfileViewModel.checkShowSaveButton()
            }
        }
        fillCounters(editProfileViewModel.newProfileData!!)
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
                    editProfileViewModel.newProfileData!!.avatarLink = null
                    editProfileLayoutRemoveAvatar.isEnabled = false
                }else{
                    editProfileLayoutRemoveAvatar.isEnabled = true
                }
            }
            editProfileViewModel.avaChanged = (editProfileViewModel.newProfileData!!.avatarLink != editProfileViewModel.oldProfileData!!.avatarLink) || editProfileViewModel.selectedImage != null
            editProfileViewModel.checkShowSaveButton()
            fillAvatar()
        }
    }

    override fun onPageReload() {
        editProfileViewModel.getUserData()
    }
}