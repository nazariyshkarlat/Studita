package com.example.studita.presentation.fragments

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import com.example.studita.utils.ImageUtils.compress
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toDrawable
import androidx.core.view.OneShotPreDrawListener
import androidx.lifecycle.ViewModelProviders
import com.example.studita.R
import com.example.studita.presentation.fragments.base.BaseFragment
import com.example.studita.presentation.fragments.base.NavigatableFragment
import com.example.studita.presentation.view_model.CropAvatarFragmentViewModel
import com.example.studita.presentation.view_model.MainActivityNavigationViewModel
import com.example.studita.utils.ImageUtils
import com.example.studita.utils.ImageUtils.rotateIfRequired
import com.example.studita.utils.ScreenUtils
import com.example.studita.utils.navigateBack
import kotlinx.android.synthetic.main.crop_avatar_layout.*
import java.io.IOException
import java.lang.Exception

class CropAvatarFragment : NavigatableFragment(R.layout.crop_avatar_layout){

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewModel = ViewModelProviders.of(this).get(CropAvatarFragmentViewModel::class.java)

        val imagePath: Uri? = arguments?.getParcelable<Uri>("SELECTED_IMAGE_URI")

        imagePath?.let {

            val bitmap = viewModel.getBitmap(it, view.context)

            if(bitmap != null) {
                cropAvatarFragmentCropView.post{
                    cropAvatarFragmentCropView.setBitmap(bitmap, 0, false, false)
                }
            }
        }

        OneShotPreDrawListener.add(view) {
            toolbarFragmentViewModel?.showRightButtonAndSetOnClick(R.drawable.ic_done_accent) {
                val resultPhoto = cropAvatarFragmentCropView.result
                super.onBackClick()
                targetFragment?.onActivityResult(345, RESULT_OK, Intent().apply {
                    putExtra("SELECTED_IMAGE", Bitmap.createScaledBitmap(resultPhoto, 128, 128, false))
                })
            }
        }
    }

}
