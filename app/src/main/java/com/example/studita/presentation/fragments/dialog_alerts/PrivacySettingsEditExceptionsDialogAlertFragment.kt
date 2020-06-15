package com.example.studita.presentation.fragments.dialog_alerts

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ScrollView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import com.example.studita.R
import com.example.studita.presentation.adapter.privacy_settings_duels_exceptions.PrivacySettingsDuelsExceptionsAdapter
import com.example.studita.presentation.fragments.base.BaseDialogFragment
import com.example.studita.presentation.model.PrivacySettingsDuelsExceptionsRecyclerUiModel
import com.example.studita.presentation.model.toUiModel
import com.example.studita.presentation.view_model.PrivacySettingsDuelsExceptionsViewModel
import com.example.studita.presentation.view_model.PrivacySettingsViewModel
import com.example.studita.utils.UserUtils
import com.google.gson.Gson
import kotlinx.android.synthetic.main.privacy_duels_exceptions_dialog_alert.*
import kotlinx.android.synthetic.main.recyclerview_layout.*

class PrivacySettingsEditExceptionsDialogAlertFragment : BaseDialogFragment(R.layout.privacy_duels_exceptions_dialog_alert){

    private var privacySettingsDuelsExceptionsViewModel: PrivacySettingsDuelsExceptionsViewModel? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        privacySettingsDuelsExceptionsViewModel = ViewModelProviders.of(this).get(
            PrivacySettingsDuelsExceptionsViewModel::class.java)

        privacySettingsDuelsExceptionsViewModel?.let { viewModel->

            viewModel.privacySettingsDuelsExceptionsState.observe(
                viewLifecycleOwner,
                Observer { state ->

                    when (state) {
                        is PrivacySettingsViewModel.DuelsExceptionsResultState.FirstResults -> {

                            if (privacyDuelsExceptionsRecyclerView.adapter == null) {
                                val items =
                                    if (viewModel.recyclerItems != null) viewModel.recyclerItems!! else ArrayList(
                                        viewModel.getRecyclerItems(
                                            state.results,
                                            PrivacySettingsDuelsExceptionsRecyclerUiModel.ProgressUiModel
                                        )
                                    )
                                val adapter = PrivacySettingsDuelsExceptionsAdapter(
                                    items,
                                    viewModel
                                )
                                privacyDuelsExceptionsRecyclerView.adapter = adapter
                                viewModel.recyclerItems = adapter.items
                            } else {
                                val adapter =
                                    privacyDuelsExceptionsRecyclerView.adapter as PrivacySettingsDuelsExceptionsAdapter
                                val newData = state.results.map { it.toUiModel() }
                                adapter.items.addAll(newData)
                                adapter.items.add(PrivacySettingsDuelsExceptionsRecyclerUiModel.ProgressUiModel)
                                adapter.notifyItemRangeInserted(0, newData.size + 1)
                            }
                        }
                        is PrivacySettingsViewModel.DuelsExceptionsResultState.MoreResults -> {

                            if (privacyDuelsExceptionsRecyclerView.adapter != null) {
                                val items = state.results.map { it.toUiModel() }
                                val adapter =
                                    privacyDuelsExceptionsRecyclerView.adapter as PrivacySettingsDuelsExceptionsAdapter

                                val insertIndex = adapter.itemCount - 1
                                adapter.items.addAll(insertIndex, items)
                                adapter.notifyItemRangeInserted(
                                    insertIndex,
                                    items.size
                                )
                                if (items.size < viewModel.perPage) {
                                    adapter.notifyItemChanged(adapter.itemCount - 1, Unit)
                                }
                            } else {
                                val adapter = PrivacySettingsDuelsExceptionsAdapter(
                                    viewModel.recyclerItems!!,
                                    viewModel
                                )
                                privacyDuelsExceptionsRecyclerView.adapter = adapter
                            }
                        }
                        is PrivacySettingsViewModel.DuelsExceptionsResultState.NoMoreResultsFound -> {

                            if (privacyDuelsExceptionsRecyclerView.adapter != null) {
                                val progressIndex =
                                    viewModel.recyclerItems?.indexOfFirst { it is PrivacySettingsDuelsExceptionsRecyclerUiModel.ProgressUiModel }
                                if (progressIndex != null && progressIndex != -1) {
                                    viewModel.recyclerItems?.removeAt(progressIndex)
                                    privacyDuelsExceptionsRecyclerView.adapter?.notifyItemRemoved(
                                        progressIndex
                                    )
                                }
                            } else {
                                val adapter = PrivacySettingsDuelsExceptionsAdapter(
                                    viewModel.recyclerItems!!,
                                    viewModel
                                )
                                privacyDuelsExceptionsRecyclerView.adapter = adapter
                            }
                        }
                    }
                })

            viewModel.progressState.observe(
                viewLifecycleOwner,
                Observer { showProgress ->
                    if (showProgress) {
                        privacyDuelsExceptionsProgressBar.visibility = View.VISIBLE
                        privacyDuelsExceptionsRecyclerView.visibility = View.GONE
                    } else {
                        privacyDuelsExceptionsProgressBar.visibility = View.GONE
                        privacyDuelsExceptionsRecyclerView.visibility = View.VISIBLE
                    }
                })

            privacyDuelsExceptionsLeftButton.setOnClickListener {
                dismiss()
            }

            privacyDuelsExceptionsRightButton.setOnClickListener {
                viewModel.editDuelsExceptions()
                targetFragment?.onActivityResult(345, Activity.RESULT_OK, Intent().apply {
                    if(viewModel.editedDuelsExceptionsData.isNotEmpty())
                        putExtra("CHANGED_EXCEPTIONS", Gson().toJson(viewModel.editedDuelsExceptionsData))
                })
                dismiss()
            }

            if (savedInstanceState == null)
                viewModel.getPrivacySettingsDuelsExceptions(
                    UserUtils.getUserIDTokenData()!!,
                    false
                )
        }

        privacyDuelsExceptionsRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                checkScrollY()
            }

        })
    }

    private fun checkScrollY(){
        privacyDuelsExceptionsRecyclerView.post {
            privacyDuelsExceptionsRecyclerView.background =
                ContextCompat.getDrawable(privacyDuelsExceptionsRecyclerView.context,
                    when{
                        !privacyDuelsExceptionsRecyclerView.canScrollVertically(1) -> R.drawable.divider_top_drawable
                        !privacyDuelsExceptionsRecyclerView.canScrollVertically(-1) -> R.drawable.divider_bottom_drawable
                        else -> R.drawable.divider_top_bottom_drawable
                    })
        }
    }


}