package com.example.studita.presentation.fragments

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.WindowManager
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.studita.R
import com.example.studita.domain.entity.UserDataData
import com.example.studita.presentation.draw.AvaDrawer
import com.example.studita.presentation.fragments.base.BaseFragment
import com.example.studita.presentation.fragments.base.NavigatableFragment
import com.example.studita.presentation.fragments.dialog_alerts.UnsavedChangesDialogAlertFragment
import com.example.studita.presentation.listeners.GenericTextWatcher
import com.example.studita.presentation.listeners.GenericTextWatcherImpl
import com.example.studita.presentation.listeners.setGenericTextWatcher
import com.example.studita.presentation.view_model.EditProfileViewModel
import com.example.studita.presentation.view_model.ProfileMenuFragmentViewModel
import com.example.studita.presentation.views.CustomSnackbar
import com.example.studita.utils.ThemeUtils
import com.example.studita.utils.UserUtils
import com.example.studita.utils.navigateBack
import kotlinx.android.synthetic.main.edit_profile_layout.*
import kotlinx.android.synthetic.main.edit_profile_layout.view.*
import kotlinx.android.synthetic.main.edit_profile_layout.view.editProfileLayoutAvatar
import kotlinx.android.synthetic.main.profile_menu_layout.*

class EditProfileFragment : NavigatableFragment(R.layout.edit_profile_layout), ViewTreeObserver.OnScrollChangedListener, GenericTextWatcher {

    private var editProfileViewModel: EditProfileViewModel? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        editProfileViewModel = activity?.run {
            ViewModelProviders.of(this).get(EditProfileViewModel::class.java)
        }

        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN
        )
        UserUtils.userDataLiveData.observe(activity as FragmentActivity, Observer {
            fillUserData(it)

            (view as ViewGroup).removeView(editProfileLayoutProgressBar)
            editProfileLayoutScrollView.visibility = View.VISIBLE
        })

        editProfileLayoutOpenProfileButton.pressViewImpl.pressAlpha = ThemeUtils.getPressAlpha(view.context)

        editProfileLayoutScrollView.viewTreeObserver.addOnScrollChangedListener(this)
        editProfileLayoutUserNameEditText.setGenericTextWatcher(GenericTextWatcherImpl(this, editProfileLayoutUserNameEditText))
        editProfileLayoutFullNameEditText.setGenericTextWatcher(GenericTextWatcherImpl(this, editProfileLayoutFullNameEditText))
    }

    private fun fillUserData(userDataData: UserDataData){
        editProfileLayoutUserNameEditText.setText(resources.getString(R.string.user_name_template, userDataData.userName))
        editProfileLayoutFullNameEditText.setText(userDataData.userFullName)
        fillAvatar(userDataData)
    }
    private fun fillAvatar(userDataData: UserDataData){
        if (userDataData.avatarLink == null) {
            AvaDrawer.drawAwa(editProfileLayoutAvatar, UserUtils.userData.userName!!)
        } else {
            Glide
                .with(this)
                .load(userDataData.avatarLink)
                .centerCrop()
                .apply(RequestOptions.circleCropTransform())
                .into(editProfileLayoutAvatar)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        editProfileLayoutScrollView.viewTreeObserver.removeOnScrollChangedListener(this)
    }

    override fun onScrollChanged() {
        val scrollY = editProfileLayoutScrollView.scrollY
        val showToolbar = scrollY != 0
        if(showToolbar != toolbarFragmentViewModel?.toolbarDividerState?.value)
            toolbarFragmentViewModel?.toolbarDividerState?.value = showToolbar
    }

    override fun beforeTextChanged(view: View, charSequence: CharSequence?, start: Int, count: Int, after: Int) {

    }

    override fun afterTextChanged(view: View, editable: Editable?) {}

    override fun onTextChanged(view: View, charSequence: CharSequence?, start: Int, before: Int, count: Int) {
        when (view.id) {
            R.id.editProfileLayoutUserNameEditText -> {
                if(charSequence?.length == 0) {
                    (view as EditText).setText("@")
                    view.setSelection(1)
                }else
                    editProfileViewModel?.newProfileData?.userName = charSequence?.toString()?.substring(1)?.takeIf { it.isNotEmpty() }
            }
            R.id.editProfileLayoutFullNameEditText -> editProfileViewModel?.newProfileData?.userFullName = charSequence?.toString()
        }
        showSaveChangesButton(view.context)
    }

    private fun showSaveChangesButton(context: Context){
        if(editProfileViewModel?.isProfileDataChanged() == true){
            toolbarFragmentViewModel?.showRightButtonAndSetOnClick {
                saveChangesButtonOnClick(context)
            }
        }else{
            toolbarFragmentViewModel?.hideRightButton()
        }
    }

    override fun onBackClick() {
        if (editProfileViewModel?.isProfileDataChanged() == true) {
            fragmentManager?.let {
                UnsavedChangesDialogAlertFragment().apply {
                    setTargetFragment(this@EditProfileFragment as NavigatableFragment, 0)
                }.show(it, null)
            }
        }else{
            super.onBackClick()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 228 && resultCode == RESULT_OK)
            super.onBackClick()
    }

    private fun saveChangesButtonOnClick(context: Context){
        editProfileViewModel?.saveProfileChanges()
        super.onBackClick()
        val snackbar = CustomSnackbar(context)
        snackbar.show(
                resources.getString(R.string.changes_are_saved),
                ThemeUtils.getAccentColor(snackbar.context)
        )
    }


}