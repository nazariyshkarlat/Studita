package com.studita.presentation.fragments.dialog_alerts

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.MediaStore.EXTRA_OUTPUT
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.os.bundleOf
import com.studita.R
import com.studita.presentation.fragments.profile.edit.CropAvatarFragment
import com.studita.presentation.fragments.base.BaseDialogFragment
import com.studita.presentation.views.CustomSnackbar
import com.studita.utils.ImageUtils.createImageFile
import com.studita.utils.ThemeUtils
import com.studita.utils.navigateTo
import kotlinx.android.synthetic.main.change_avatar_dialog_alert.*
import pub.devrel.easypermissions.EasyPermissions
import java.io.File
import java.io.IOException


class ChangeAvatarDialogAlertFragment : BaseDialogFragment(R.layout.change_avatar_dialog_alert),
    EasyPermissions.PermissionCallbacks {

    private var photoPath: String? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        photoPath = savedInstanceState?.getString("PHOTO_PATH")

        changeAvatarDialogAlertTakePhoto.setOnClickListener {
            showCameraIntent(view.context)
        }
        changeAvatarDialogAlertChooseFromGallery.setOnClickListener {
            showGalleryIntent(view.context)
        }
    }

    private fun showCameraIntent(context: Context) {
        requestPermissions(2, context) {
            Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
                try {
                    val photoFile: File? = try {
                        createImageFile(context)
                    } catch (ex: IOException) {
                        ex.printStackTrace()
                        null
                    }
                    photoFile?.also {
                        photoPath = it.absolutePath
                        val photoURI: Uri = FileProvider.getUriForFile(
                            context,
                            "${context.packageName}.fileprovider",
                            it
                        )
                        takePictureIntent.putExtra(EXTRA_OUTPUT, photoURI)
                        startActivityForResult(takePictureIntent, 0)
                    }
                }catch (e: java.lang.Exception){
                    e.printStackTrace()
                }
            }
        }
    }

    private fun showGalleryIntent(context: Context) {
        requestPermissions(3, context) {
            val pickPhoto = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(pickPhoto, 1)
        }
    }

    private fun requestPermissions(requestCode: Int, context: Context, granted: () -> Unit) {
        val galleryPermissions = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
        try {
            if (!EasyPermissions.hasPermissions(context, *galleryPermissions)) {
                EasyPermissions.requestPermissions(
                    this,
                    "Access for storage",
                    requestCode,
                    *galleryPermissions
                )
            } else {
                granted.invoke()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK) {
            var selectedImagePath: Uri? = null
            when (requestCode) {
                0 -> {
                    photoPath?.let {
                        val file = File(it)
                        selectedImagePath = Uri.fromFile(file)
                    }
                }
                1 -> {
                    if (data != null) {
                        val selectedImageUri = data.data
                        if (selectedImageUri != null)
                            selectedImagePath = selectedImageUri
                    }
                }
            }
            selectedImagePath?.let {
                dismiss()
                (activity as AppCompatActivity).navigateTo(
                    CropAvatarFragment()
                        .apply {
                            this@ChangeAvatarDialogAlertFragment.targetFragment?.let {
                                this.setTargetFragment(
                                    it,
                                    0
                                )
                            }
                            arguments = bundleOf("SELECTED_IMAGE_URI" to it)
                        }, R.id.doubleFrameLayoutFrameLayout
                )
            } ?: run {
                activity?.let {
                    CustomSnackbar(it).show(
                        it.resources.getString(R.string.select_photo_error),
                        ThemeUtils.getRedColor(it),
                        ContextCompat.getColor(it, R.color.white)
                    )
                }
            }
        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {}

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        if (requestCode == 2) {
            showCameraIntent(context!!)
        } else if (requestCode == 3) {
            showGalleryIntent(context!!)
        }
    }

    override fun shouldShowRequestPermissionRationale(permission: String): Boolean {
        return false
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState.apply {
            putString("PHOTO_PATH", photoPath)
        })
    }


}