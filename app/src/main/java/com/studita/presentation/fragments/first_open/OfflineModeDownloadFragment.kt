package com.studita.presentation.fragments.first_open

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.OneShotPreDrawListener
import androidx.core.view.updateLayoutParams
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.studita.R
import com.studita.presentation.fragments.base.BaseFragment
import com.studita.presentation.fragments.base.NavigatableFragment
import com.studita.presentation.fragments.bottom_sheets.OfflineModeDownloadErrorBottomSheet
import com.studita.presentation.view_model.OfflineModeDownloadFragmentViewModel
import com.studita.utils.ThemeUtils
import com.studita.utils.replace
import kotlinx.android.synthetic.main.offline_mode_download_layout.*

class OfflineModeDownloadFragment : BaseFragment(R.layout.offline_mode_download_layout){

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewModel = ViewModelProviders.of(this).get(OfflineModeDownloadFragmentViewModel::class.java)

        offlineModeDownloadLayoutProgressText.text = resources.getString(R.string.download_progress_template, "0", "0")

        viewModel.downloadCacheEvent.observe(viewLifecycleOwner, Observer {
            if(it){
                (activity as AppCompatActivity).replace(WelcomeFragment(), R.id.frameLayout, addToBackStack = false, startAnim = R.anim.welcome_layout_open_animation)
            }
        })

        viewModel.downloadProgressLiveData.observe(viewLifecycleOwner, Observer {
            val progress = it.first
            val totalMbSize = it.second
            val done = it.third

            formText(progress, totalMbSize)
            offlineModeDownloadLayoutProgressBar.currentProgress = progress

            offlineModeDownloadLayoutProgressBar.setProgressColor(ThemeUtils.getAccentColor(context!!))
            hideError()
        })

        viewModel.errorState.observe(viewLifecycleOwner, Observer {
            val networkError = it
            if(networkError) {
                if(childFragmentManager.findFragmentById(0) !is OfflineModeDownloadErrorBottomSheet)
                    OfflineModeDownloadErrorBottomSheet().show(childFragmentManager, null)
                hideError()
            }else{
                showError()
            }
            offlineModeDownloadLayoutProgressBar.setProgressColor(ThemeUtils.getRedColor(context!!))
        })

        (offlineModeDownloadLayoutErrorSnackbar as TextView).text = resources.getString(R.string.server_temporarily_unavailable)

        OneShotPreDrawListener.add(view){
            initErrorSnackbar()
            offlineModeDownloadLayoutCenterBlock.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                bottomMargin = offlineModeDownloadLayoutErrorSnackbar.height
            }
        }
    }

    private fun formText(progress: Float, totalMbSize: Float){
        val mbDownloaded = totalMbSize*progress

        val downloadedText = convertMbFloatToString(mbDownloaded)
        val toDownloadText = convertMbFloatToString(totalMbSize)

        offlineModeDownloadLayoutProgressText.text = resources.getString(R.string.download_progress_template, downloadedText, toDownloadText)
    }

    private fun convertMbFloatToString(float: Float) = if(float - float.toLong().toFloat() == 0F || float > 10F) float.toLong().toString() else String.format("%.1f", float)

    private fun initErrorSnackbar() {
        offlineModeDownloadLayoutBottomSection.translationY = offlineModeDownloadLayoutErrorSnackbar.height.toFloat()
    }

    private fun showError(){
        OneShotPreDrawListener.add(view!!) {
            offlineModeDownloadLayoutBottomSection.animate().translationY(0F).setInterpolator(
                FastOutSlowInInterpolator()
            ).start()
        }
    }

    private fun hideError(){
        OneShotPreDrawListener.add(view!!) {
            offlineModeDownloadLayoutBottomSection.animate()
                .translationY(offlineModeDownloadLayoutErrorSnackbar.height.toFloat())
                .setDuration(150).setInterpolator(
                    FastOutSlowInInterpolator()
                ).start()
        }
    }

}